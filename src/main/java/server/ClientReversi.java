package server;

import framework.network.AbstractClient;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Reversi client that extends AbstractClient
 * Handles communication with the game server for Reversi tournament mode
 */
public class ClientReversi extends AbstractClient {

    private Consumer<String> messageHandler;

    public ClientReversi() {
        super("127.0.0.1", 7789);
    }

    public ClientReversi(String host, int port) {
        super(host, port);
    }

    /**
     * Connect to the server
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
        System.out.println("Successfully connected to Reversi server!");
    }

    @Override
    protected void onDisconnected() {
        System.out.println("Disconnected from Reversi server");
    }

    @Override
    protected void onConnectionError(IOException e) {
        System.err.println("Failed to connect to Reversi server: " + e.getMessage());
        System.err.println("Make sure the server is running");
    }

    @Override
    protected void onConnectionLost(IOException e) {
        System.err.println("Connection lost: " + e.getMessage());
    }

    @Override
    protected void onMessageReceived(String message) {
        String timestamp = java.time.LocalTime.now().toString();
        System.out.println("\n[" + timestamp + "] [Reversi Server MSG] " + message);

        if (messageHandler != null) {
            messageHandler.accept(message);
        }
    }

    /**
     * Login with the specified username
     */
    public void login(String name) {
        System.out.println("Logging in as " + name);
        sendMessage("login " + name);
    }

    /**
     * Send a move to the server (position is 0-63 for 8x8 board)
     */
    public void sendMove(int position) {
        if (isConnected()) {
            sendMessage("move " + position);
            System.out.println("Reversi move sent: " + position);
        } else {
            System.err.println("Cannot send move: not connected to server");
        }
    }

    /**
     * Request a match for Reversi
     */
    public void requestMatch() {
        if (isConnected()) {
            //sendMessage("subscribe reversi");
            System.out.println("Requested match for reversi");
        }
    }

    /**
     * Forfeit the current game
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
            System.err.println("Error sending quit: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    /**
     * Shutdown the client connection
     */
    public void shutdown() {
        disconnect();
    }
}

