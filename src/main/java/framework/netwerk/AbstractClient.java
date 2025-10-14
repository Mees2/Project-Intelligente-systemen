package framework.netwerk;

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
    protected String serverAdres;
    protected int serverPoort;
    protected boolean verbonden = false;
    
    /**
     * Constructor voor een client
     * @param serverAdres Het IP adres van de server
     * @param serverPoort De poort van de server
     */
    protected AbstractClient(String serverAdres, int serverPoort) {
        this.serverAdres = serverAdres;
        this.serverPoort = serverPoort;
    }
    
    /**
     * Verbind met de server
     * @return true als verbinding succesvol is
     */
    public boolean verbind() {
        try {
            socket = new Socket(serverAdres, serverPoort);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            verbonden = true;
            onVerbonden();
            return true;
        } catch (IOException e) {
            onVerbindingFout(e);
            return false;
        }
    }
    
    /**
     * Verbreek de verbinding met de server
     */
    public void verbreek() {
        verbonden = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            // Negeer fouten bij sluiten
        }
        onVerbroken();
    }
    
    /**
     * Verstuur een bericht naar de server
     * @param bericht Het bericht om te versturen
     */
    public void stuurBericht(String bericht) {
        if (verbonden && output != null) {
            output.println(bericht);
        }
    }
    
    /**
     * Controleert of de client verbonden is
     * @return true als verbonden
     */
    public boolean isVerbonden() {
        return verbonden && socket != null && !socket.isClosed();
    }
    
    /**
     * Callback wanneer verbinding is gemaakt
     */
    protected abstract void onVerbonden();
    
    /**
     * Callback wanneer verbinding is verbroken
     */
    protected abstract void onVerbroken();
    
    /**
     * Callback bij verbindingsfout
     * @param e De exceptie
     */
    protected abstract void onVerbindingFout(IOException e);
    
    /**
     * Callback bij ontvangen bericht
     * @param bericht Het ontvangen bericht
     */
    protected abstract void onBerichtOntvangen(String bericht);
}
