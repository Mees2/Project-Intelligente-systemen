package server;

import framework.network.AbstractClient;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * TicTacToe client that extends AbstractClient
 * Handles communication with the game server
 */
public class ClientTicTacToe extends AbstractClient {

    private Consumer<String> messageHandler;

    public ClientTicTacToe() {
        super("127.0.0.1", 7789);
    }

    public ClientTicTacToe(String host, int port) {
        super(host, port);
    }

    /**
     * Connect to the server (gebruikt door GUI)
     */
    public boolean connectToServer() {
        return connect();
    }

    /**
     * Set the message handler for received messages
     */
    public void setMessageHandler(Consumer<String> handler) {
        this.messageHandler = handler;
    }

    /**
     * Extract a field value from server message
     */
    public static String extractField(String serverMsg, String fieldName) {
        try {
            int idx = serverMsg.indexOf(fieldName + ":");
            if (idx == -1 && (idx = serverMsg.indexOf(fieldName.toUpperCase() + ":")) == -1) return "";
            String after = serverMsg.substring(idx + fieldName.length() + 1).trim();
            int start = after.indexOf('"') + 1, end = after.indexOf('"', start);
            return (start > 0 && end > start) ? after.substring(start, end) : "";
        } catch (Exception e) {
            System.err.println("Failed to parse " + fieldName + ": " + e.getMessage());
            return "";
        }
    }

    /**
     * Extract move position from server message
     */
    public static int extractMovePosition(String msg) {
        try {
            return Integer.parseInt(extractField(msg, "MOVE"));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Extract player name from server message
     */
    public static String extractPlayerName(String msg) {
        return extractField(msg, "PLAYER");
    }

    @Override
    protected void onConnected() {
        System.out.println("Successfully connected to server!");
    }

    @Override
    protected void onDisconnected() {
        System.out.println("Disconnected from server");
    }

    @Override
    protected void onConnectionError(IOException e) {
        System.err.println("Failed to connect to server: " + e.getMessage());
        System.err.println("Make sure the server is running");
    }

    @Override
    protected void onConnectionLost(IOException e) {
        System.err.println("Connection lost: " + e.getMessage());
    }

    @Override
    protected void onMessageReceived(String message) {
        String timestamp = java.time.LocalTime.now().toString();
        System.out.println("\n[" + timestamp + "] [Server MSG] " + message);

        if (messageHandler != null) {
            messageHandler.accept(message);
        }
    }

    /**
     * Deze methode logt in met de opgegeven gebruikersnaam parameter
     */
    public void login(String name) {
        System.out.println("Logging in as " + name);
        sendMessage("login " + name);
    }

    /**
     * Verstuur een zet naar de server
     */
    public void sendMove(int position) {
        if (isConnected()) {
            sendMessage("move " + position);
            System.out.println("Move sent: " + position);
        } else {
            System.err.println("Cannot send move: not connected to server");
        }
    }

    /**
     * Vraag de server om een match te starten
     */
    public void requestMatch() {
        if (isConnected()) {
            sendMessage("subscribe tic-tac-toe");
            System.out.println("Requested match for tic-tac-toe");
        }
    }

    /**
     * Geef op voor het huidige spel
     */
    public void forfeit() {
        if (isConnected()) {
            sendMessage("forfeit");
            System.out.println("Forfeited game");
        }
    }

    /**
     * Quit the game and disconnect
     */
    public void quit() {
        try {
            if (isConnected()) {
                sendMessage("quit");
                System.out.println("Sent quit to server");
            }
        } catch (Exception e) {
            System.err.println("Failed to send quit: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    /**
     * Shutdown the connection
     */
    public void shutdown() {
        disconnect();
    }
}