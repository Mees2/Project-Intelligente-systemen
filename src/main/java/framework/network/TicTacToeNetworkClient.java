package framework.network;

import server.ClientTicTacToe;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Lightweight network wrapper around server.ClientTicTacToe.
 * Parses server messages and forwards events to a listener so the UI
 * doesn't need to read/parse raw server lines.
 */
public class TicTacToeNetworkClient {
    public interface Listener {
        void onLogin();
        void onMatch(String playerToMove, String opponent);
        void onYourTurn();
        void onMove(String playerName, int position);
        void onConnectionLost(Exception e);
    }

    private final ClientTicTacToe client;
    private final Listener listener;
    private Thread readerThread;
    private volatile boolean running = false;

    public TicTacToeNetworkClient(Listener listener) {
        this.client = new ClientTicTacToe();
        this.listener = listener;
    }

    public boolean connect() {
        boolean ok = client.connectToServer();
        if (!ok) return false;
        startReader();
        return true;
    }

    private void startReader() {
        running = true;
        readerThread = new Thread(() -> {
            try {
                BufferedReader in = client.getReader();
                String line;
                while (running && in != null && (line = in.readLine()) != null) {
                    handleLine(line);
                }
            } catch (IOException e) {
                if (listener != null) listener.onConnectionLost(e);
            } catch (Exception e) {
                if (listener != null) listener.onConnectionLost(e);
            }
        }, "ticnet-reader");
        readerThread.start();
    }

    private void handleLine(String line) {
        if (line == null) return;
        String upper = line.toUpperCase();
        if (upper.contains("OK")) {
            if (listener != null) listener.onLogin();
            return;
        }
        if (upper.contains("MATCH")) {
            String playerToMove = extractField(line, "PLAYERTOMOVE");
            String opponent = extractField(line, "OPPONENT");
            if (listener != null) listener.onMatch(playerToMove, opponent);
            return;
        }
        if (upper.contains("YOURTURN")) {
            if (listener != null) listener.onYourTurn();
            return;
        }
        if (upper.contains("MOVE") && upper.contains("PLAYER:")) {
            String player = extractField(line, "PLAYER");
            int pos = extractIntField(line, "MOVE");
            if (listener != null) listener.onMove(player, pos);
        }
    }

    private String extractField(String serverMsg, String fieldName) {
        try {
            int idx = serverMsg.indexOf(fieldName + ":");
            if (idx == -1) idx = serverMsg.indexOf(fieldName.toUpperCase() + ":");
            if (idx == -1) return "";
            String after = serverMsg.substring(idx + fieldName.length() + 1).trim();
            int start = after.indexOf('"') + 1;
            int end = after.indexOf('"', start);
            return (start > 0 && end > start) ? after.substring(start, end) : "";
        } catch (Exception e) {
            return "";
        }
    }

    private int extractIntField(String msg, String fieldName) {
        try {
            String s = extractField(msg, fieldName);
            if (s == null || s.isEmpty()) {
                int idx = msg.indexOf(fieldName + ":");
                if (idx == -1) idx = msg.indexOf(fieldName.toUpperCase() + ":");
                if (idx == -1) return -1;
                String after = msg.substring(idx + fieldName.length() + 1).trim();
                StringBuilder num = new StringBuilder();
                for (int i = 0; i < after.length(); i++) {
                    char c = after.charAt(i);
                    if (Character.isDigit(c)) num.append(c);
                    else if (num.length() > 0) break;
                }
                return num.length() > 0 ? Integer.parseInt(num.toString()) : -1;
            }
            return Integer.parseInt(s);
        } catch (Exception e) {
            return -1;
        }
    }

    public void login(String name) {
        client.login(name);
    }

    public void requestMatch() {
        client.requestMatch();
    }

    public void sendMove(int pos) {
        client.sendMove(pos);
    }

    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    public void quit() {
        try { client.quit(); } catch (Exception ignored) {}
    }

    public void shutdown() {
        running = false;
        try {
            if (readerThread != null) readerThread.interrupt();
            client.shutdown();
        } catch (Exception ignored) {}
    }
}

