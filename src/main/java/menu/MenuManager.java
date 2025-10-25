package menu;

import javax.swing.JOptionPane;
import server.ClientTicTacToe;


/**
 * MenuManager beheert de navigatie tussen verschillende menu's en spellen.
 */
public final class MenuManager {
    private static final String MODE_PVP = "PVP";
    private static final String MODE_PVA = "PVA";
    private static final String MODE_SERVER = "SERVER";

    private final MainMenu mainMenu;
    private final TicTacToeMenu ticTacToeMenu;
    private final SettingsMenu settingsMenu;
    private final TicTacToeNamePvp ticTacToeNamePvp;
    private final TicTacToeNamePva ticTacToeNamePva;
    private final TicTacToeNameServer ticTacToeNameServer;
    private final LanguageManager lang = LanguageManager.getInstance();
    
    private ClientTicTacToe serverClient;

    /**
     * Constructor voor de MenuManager
     * Initialiseert alle menu's
     */
    public MenuManager() {
        mainMenu = new MainMenu(this);
        ticTacToeMenu = new TicTacToeMenu(this);
        settingsMenu = new SettingsMenu(this);
        ticTacToeNamePvp = new TicTacToeNamePvp(this);
        ticTacToeNamePva = new TicTacToeNamePva(this);
        ticTacToeNameServer = new TicTacToeNameServer(this);
    }

    /**
     * Start de applicatie door het hoofdmenu te tonen
     */
    public void startApplication() {
        mainMenu.showMenu();
    }

    /**
     * Opent het TicTacToe submenu
     * Verbergt het hoofdmenu en toont het TicTacToe menu
     */
    public void openTicTacToeMenu() {
        mainMenu.hideMenu();
        ticTacToeMenu.showMenu();
    }

    /**
     * Keert terug naar het hoofdmenu vanaf een submenu
     */
    public void returnToMainMenu() {
        ticTacToeMenu.hideMenu();
        mainMenu.showMenu();
    }
    // van naam PVP terug naar TTT menu
    public void closeNameSelectionPVP() {
        ticTacToeNamePvp.hideMenu();
        ticTacToeMenu.showMenu();
    }
    // van naam PVA terug naar TTT menu
    public void closeNameSelectionPVA() {
        ticTacToeNamePvp.hideMenu();
        ticTacToeMenu.showMenu();
    }
    /**
     * Opent het instellingen menu
     * Verbergt het hoofdmenu en toont het instellingen-menu
     */
    public void openSettingsMenu() {
        mainMenu.hideMenu();
        settingsMenu.showMenu();
    }

    /**
     * Keert terug naar het hoofdmenu vanaf het instellingen-menu
     */
    public void returnToMainMenuFromSettings() {
        settingsMenu.hideMenu();
        mainMenu.showMenu();
    }

    /**
     * Start een TicTacToe spel met de opgegeven mode
     * @param gameMode De spelmode: "PVP" voor Player vs Player, "PVA" voor Player vs AI
     */

    // Was startTicTacToe, misschien wijzigen als reversi wordt geinplementeerd?
    public void openNameSelection(String gameMode) {
        ticTacToeMenu.hideMenu();

        switch (gameMode) {
            case MODE_PVP -> ticTacToeNamePvp.showMenu();
            case MODE_PVA -> ticTacToeNamePva.showMenu();
            case MODE_SERVER -> ticTacToeNameServer.showMenu();
            default -> throw new IllegalArgumentException("Onbekende gameMode: " + gameMode);
        }
    }

    public void startTicTacToeGame(String gameMode, String speler1Naam, String speler2Naam) {
        TicTacToeGame game = new TicTacToeGame(this, gameMode, speler1Naam, speler2Naam);
        game.start();
    }
    /**
     * Start TicTacToe in Speler vs Speler mode

    private void startTicTacToePlayerVsPlayer() {
        var game = new TicTacToeGame(this, MODE_PVP);
        game.start();
    }

    /**
     * Start TicTacToe in Speler vs AI mode

    private void startTicTacToePlayerVsAI() {
        var game = new TicTacToeGame(this, MODE_PVA);
        game.start();
    }

    /**
     * Wordt aangeroepen wanneer een spel beëindigd wordt
     * Keert terug naar het TicTacToe menu
     */
    public void onGameFinished() {
        ticTacToeMenu.showMenu();
    }

    /**
     * Update de taal in alle menu's
     */
    public void updateLanguage() {
        mainMenu.updateLanguage();
        ticTacToeMenu.updateLanguage();
        settingsMenu.updateLanguage();
        ticTacToeNamePvp.updateLanguage();
        ticTacToeNamePva.updateLanguage();

    }


    public void confirmExit() {
        LanguageManager lang = LanguageManager.getInstance();
        var option = JOptionPane.showConfirmDialog(
                null,
                lang.get("main.exit.confirm"),
                lang.get("main.exit.title"),
                JOptionPane.YES_NO_OPTION
        );
        if (option == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    /**
     * Verbind met de TicTacToe server
     */
    public void connectToServer() {
        // Maak een nieuwe client aan als deze nog niet bestaat
        if (serverClient == null) {
            serverClient = new ClientTicTacToe();
        }
        
        // Probeer te verbinden
        boolean connected = serverClient.connectToServer();
        
        if (connected) {
            JOptionPane.showMessageDialog(
                null,
                "Succesvol verbonden met de server!\nServer: 127.0.0.1:7789",
                "Server Verbinding",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            // Start de client thread voor het ontvangen van berichten
            Thread clientThread = new Thread(serverClient);
            clientThread.start();
            
        } else {
            JOptionPane.showMessageDialog(
                null,
                "Kon niet verbinden met de server.\nZorg ervoor dat de server draait op 127.0.0.1:7789",
                "Verbindingsfout",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
