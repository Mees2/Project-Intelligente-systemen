package framework.netwerk;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstracte basis klasse voor een netwerk server
 * Voorbereid voor toekomstige multiplayer functionaliteit
 * Kan gebruikt worden voor TicTacToe en Reversi online spel
 */
public abstract class AbstractServer {
    protected ServerSocket serverSocket;
    protected int poort;
    protected boolean actief = false;
    protected List<ClientHandler> clients;
    
    /**
     * Constructor voor een server
     * @param poort De poort waarop de server luistert
     */
    protected AbstractServer(int poort) {
        this.poort = poort;
        this.clients = new ArrayList<>();
    }
    
    /**
     * Start de server
     * @return true als server succesvol gestart is
     */
    public boolean start() {
        try {
            serverSocket = new ServerSocket(poort);
            actief = true;
            onGestart();
            return true;
        } catch (IOException e) {
            onStartFout(e);
            return false;
        }
    }
    
    /**
     * Stop de server
     */
    public void stop() {
        actief = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            // Sluit alle client verbindingen
            for (ClientHandler client : clients) {
                client.sluit();
            }
            clients.clear();
        } catch (IOException e) {
            // Negeer fouten bij sluiten
        }
        onGestopt();
    }
    
    /**
     * Accepteer nieuwe clients
     * Deze methode blokkeert tot een client verbindt
     */
    public void accepteerClients() {
        while (actief) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket);
                clients.add(handler);
                onClientVerbonden(handler);
                new Thread(handler).start();
            } catch (IOException e) {
                if (actief) { // Alleen loggen als server nog actief is
                    onAccepteerFout(e);
                }
            }
        }
    }
    
    /**
     * Verstuur een bericht naar alle clients
     * @param bericht Het bericht om te versturen
     */
    public void broadcastBericht(String bericht) {
        for (ClientHandler client : clients) {
            client.stuurBericht(bericht);
        }
    }
    
    /**
     * Verwijder een client uit de lijst
     * @param handler De client handler om te verwijderen
     */
    protected void verwijderClient(ClientHandler handler) {
        clients.remove(handler);
        onClientVerbroken(handler);
    }
    
    /**
     * Callback wanneer server is gestart
     */
    protected abstract void onGestart();
    
    /**
     * Callback wanneer server is gestopt
     */
    protected abstract void onGestopt();
    
    /**
     * Callback bij start fout
     * @param e De exceptie
     */
    protected abstract void onStartFout(IOException e);
    
    /**
     * Callback bij accepteer fout
     * @param e De exceptie
     */
    protected abstract void onAccepteerFout(IOException e);
    
    /**
     * Callback wanneer een client verbindt
     * @param handler De client handler
     */
    protected abstract void onClientVerbonden(ClientHandler handler);
    
    /**
     * Callback wanneer een client verbreekt
     * @param handler De client handler
     */
    protected abstract void onClientVerbroken(ClientHandler handler);
    
    /**
     * Callback bij ontvangen bericht van een client
     * @param handler De client handler
     * @param bericht Het ontvangen bericht
     */
    protected abstract void onBerichtOntvangen(ClientHandler handler, String bericht);
    
    /**
     * Inner class voor het afhandelen van individuele clients
     */
    protected class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader input;
        private PrintWriter output;
        
        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                sluit();
            }
        }
        
        @Override
        public void run() {
            try {
                String bericht;
                while ((bericht = input.readLine()) != null) {
                    onBerichtOntvangen(this, bericht);
                }
            } catch (IOException e) {
                // Verbinding verbroken
            } finally {
                sluit();
                verwijderClient(this);
            }
        }
        
        public void stuurBericht(String bericht) {
            if (output != null) {
                output.println(bericht);
            }
        }
        
        public void sluit() {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                // Negeer fouten bij sluiten
            }
        }
        
        public String getAdres() {
            return socket != null ? socket.getInetAddress().getHostAddress() : "onbekend";
        }
    }
}
