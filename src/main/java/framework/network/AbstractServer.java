package framework.network;

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
    protected int port;
    protected boolean active = false;
    protected List<ClientHandler> clients;
    
    /**
     * Constructor voor een server
     * @param port De poort waarop de server luistert
     */
    protected AbstractServer(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }
    
    /**
     * Start de server
     * @return true als server succesvol gestart is
     */
    public boolean start() {
        try {
            serverSocket = new ServerSocket(port);
            active = true;
            onStarted();
            return true;
        } catch (IOException e) {
            onStartError(e);
            return false;
        }
    }
    
    /**
     * Stop de server
     */
    public void stop() {
        active = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            // Sluit alle client verbindingen
            for (ClientHandler client : clients) {
                client.close();
            }
            clients.clear();
        } catch (IOException e) {
            // Negeer fouten bij sluiten
        }
        onStopped();
    }
    
    /**
     * Accepteer nieuwe clients
     * Deze methode blokkeert tot een client verbindt
     */
    public void acceptClients() {
        while (active) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket);
                clients.add(handler);
                onClientConnected(handler);
                new Thread(handler).start();
            } catch (IOException e) {
                if (active) { // Alleen loggen als server nog actief is
                    onAcceptError(e);
                }
            }
        }
    }
    
    /**
     * Verstuur een bericht naar alle clients
     * @param message Het bericht om te versturen
     */
    public void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }
    
    /**
     * Verwijder een client uit de lijst
     * @param handler De client handler om te verwijderen
     */
    protected void removeClient(ClientHandler handler) {
        clients.remove(handler);
        onClientDisconnected(handler);
    }
    
    /**
     * Callback wanneer server is gestart
     */
    protected abstract void onStarted();
    
    /**
     * Callback wanneer server is gestopt
     */
    protected abstract void onStopped();
    
    /**
     * Callback bij start fout
     * @param e De exceptie
     */
    protected abstract void onStartError(IOException e);
    
    /**
     * Callback bij accepteer fout
     * @param e De exceptie
     */
    protected abstract void onAcceptError(IOException e);
    
    /**
     * Callback wanneer een client verbindt
     * @param handler De client handler
     */
    protected abstract void onClientConnected(ClientHandler handler);
    
    /**
     * Callback wanneer een client verbreekt
     * @param handler De client handler
     */
    protected abstract void onClientDisconnected(ClientHandler handler);
    
    /**
     * Callback bij ontvangen bericht van een client
     * @param handler De client handler
     * @param message Het ontvangen bericht
     */
    protected abstract void onMessageReceived(ClientHandler handler, String message);
    
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
                close();
            }
        }
        
        @Override
        public void run() {
            try {
                String message;
                while ((message = input.readLine()) != null) {
                    onMessageReceived(this, message);
                }
            } catch (IOException e) {
                // Verbinding verbroken
            } finally {
                close();
                removeClient(this);
            }
        }
        
        public void sendMessage(String message) {
            if (output != null) {
                output.println(message);
            }
        }
        
        public void close() {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                // Negeer fouten bij sluiten
            }
        }
        
        public String getAddress() {
            return socket != null ? socket.getInetAddress().getHostAddress() : "unknown";
        }
    }
}
