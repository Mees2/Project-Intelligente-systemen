package framework.network;

import java.io.*;
import java.net.Socket;

/**
 * Abstracte basis klasse voor een netwerk client
 * Voorbereid voor toekomstige multiplayer functionaliteit
 * Kan gebruikt worden voor TicTacToe en Reversi online spel
 */
public abstract class AbstractClient {
    protected Socket socket;
    protected BufferedReader input;
    protected PrintWriter output;
    protected String serverAddress;
    protected int serverPort;
    protected boolean connected = false;
    
    /**
     * Constructor voor een client
     * @param serverAddress Het IP adres van de server
     * @param serverPort De poort van de server
     */
    protected AbstractClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }
    
    /**
     * Verbind met de server
     * @return true als verbinding succesvol is
     */
    public boolean connect() {
        try {
            socket = new Socket(serverAddress, serverPort);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            connected = true;
            onConnected();
            return true;
        } catch (IOException e) {
            onConnectionError(e);
            return false;
        }
    }
    
    /**
     * Verbreek de verbinding met de server
     */
    public void disconnect() {
        connected = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            // Negeer fouten bij sluiten
        }
        onDisconnected();
    }
    
    /**
     * Verstuur een bericht naar de server
     * @param message Het bericht om te versturen
     */
    public void sendMessage(String message) {
        if (connected && output != null) {
            output.println(message);
        }
    }
    
    /**
     * Controleert of de client verbonden is
     * @return true als verbonden
     */
    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }
    
    /**
     * Callback wanneer verbinding is gemaakt
     */
    protected abstract void onConnected();
    
    /**
     * Callback wanneer verbinding is verbroken
     */
    protected abstract void onDisconnected();
    
    /**
     * Callback bij verbindingsfout
     * @param e De exceptie
     */
    protected abstract void onConnectionError(IOException e);
    
    /**
     * Callback bij ontvangen bericht
     * @param message Het ontvangen bericht
     */
    protected abstract void onMessageReceived(String message);
}
