import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class AbstractClient {
    protected Socket socket;
    protected BufferedReader input;
    protected PrintWriter output;
    protected String serverAddress;
    protected int serverPort;
    protected boolean connected = false;
    private Thread listenerThread;

    protected AbstractClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public boolean connect() {
        try {
            socket = new Socket(serverAddress, serverPort);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            connected = true;
            onConnected();
            startMessageListener();
            return true;
        } catch (IOException e) {
            onConnectionError(e);
            return false;
        }
    }

    protected void startMessageListener() {
        listenerThread = new Thread(() -> {
            try {
                String message;
                while (connected && (message = input.readLine()) != null) {
                    onMessageReceived(message);
                }
            } catch (IOException e) {
                if (connected) {
                    onConnectionLost(e);
                }
            }
        }, "server-listener");
        listenerThread.start();
    }

    public void disconnect() {
        connected = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (listenerThread != null) {
                listenerThread.interrupt();
            }
        } catch (IOException e) {
            // Ignore
        }
        onDisconnected();
    }

    public void sendMessage(String message) {
        if (connected && output != null) {
            output.println(message);
        }
    }

    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }

    public BufferedReader getReader() {
        return input;
    }

    protected abstract void onConnected();
    protected abstract void onDisconnected();
    protected abstract void onConnectionError(IOException e);
    protected abstract void onConnectionLost(IOException e);
    protected abstract void onMessageReceived(String message);
}
