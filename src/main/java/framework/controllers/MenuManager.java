package framework.controllers;

import javax.swing.JOptionPane;

import framework.gui.menu.*;
import framework.gui.menu.reversi.ReversiGame;
import framework.gui.menu.reversi.ReversiMenu;
import framework.gui.menu.reversi.ReversiNamePvp;
import framework.gui.menu.tictactoe.*;
import server.ClientTicTacToe;


/**
 * MenuManager beheert de navigatie tussen verschillende menu's en spellen.
 */
public final class MenuManager {
    private static final String MODE_PVP = "PVP";
    private static final String MODE_PVA = "PVA";
    private static final String MODE_SERVER = "SERVER";
    private static final String MODE_TOURNAMENT = "TOURNAMENT";

    private final MainMenu mainMenu;
    private final TicTacToeMenu ticTacToeMenu;
    private final SettingsMenu settingsMenu;
    private TicTacToeNameSelection ticTacToeNameSelection;
    private ReversiMenu reversiMenu;
    private ReversiNamePvp reversiNamePvp;
    private ReversiGame reversiGame;
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
        //ticTacToeNameSelection = new TicTacToeNameSelection(this)
        // reversi
        reversiMenu = new ReversiMenu(this);           
        reversiNamePvp = new ReversiNamePvp(this);     
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
    public void closeNameSelection() {
        ticTacToeNameSelection.hideMenu();
        ticTacToeMenu.showMenu();
    }
    // van naam PVA terug naar TTT menu
    /*public void closeNameSelectionPVA() {
        ticTacToeNameSelection.hideMenu();
        ticTacToeMenu.showMenu();
    }*/
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

        TicTacToeNameSelection.GameMode mode = switch (gameMode) {
            case MODE_PVP -> TicTacToeNameSelection.GameMode.PVP;
            case MODE_PVA -> TicTacToeNameSelection.GameMode.PVA;
            case MODE_SERVER -> TicTacToeNameSelection.GameMode.SERVER;
            case MODE_TOURNAMENT -> TicTacToeNameSelection.GameMode.TOURNAMENT;
            default -> throw new IllegalArgumentException("Unknown gameMode: " + gameMode);
        };

        // Dispose old instance if it exists
        if (ticTacToeNameSelection != null) {
            ticTacToeNameSelection.dispose();
        }

        ticTacToeNameSelection = new TicTacToeNameSelection(this, mode);
        ticTacToeNameSelection.showMenu();

/*        switch (gameMode) {
            case MODE_PVP -> ticTacToeNameSelection.showMenu();
            case MODE_PVA -> ticTacToeNamePva.showMenu();
            //case MODE_SERVER -> ticTacToeNameServer.showMenu();
            case MODE_TOURNAMENT -> ticTacToeNameTournament.showMenu();
            default -> throw new IllegalArgumentException("Onbekende gameMode: " + gameMode);
        }*/
    }

    public void startTicTacToeGame(String gameMode, String player1Name, String player2Name) {
        TicTacToeGame game = new TicTacToeGame(this, gameMode, player1Name, player2Name);
        game.start();
    }

    private void hideAllMenus() {
        mainMenu.hideMenu();
        ticTacToeMenu.hideMenu();
        settingsMenu.hideMenu();
        ticTacToeNameSelection.hideMenu();
        reversiMenu.hideMenu();
        reversiNamePvp.hideMenu();
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

    public void openReversiMenu() {
        if (reversiMenu == null) reversiMenu = new ReversiMenu(this);
        hideAllMenus();
        reversiMenu.showMenu();
    }

    public void openReversiNamePvp() {
        if (reversiNamePvp == null) reversiNamePvp = new ReversiNamePvp(this);
        hideAllMenus();
        reversiNamePvp.showMenu();
    }

    public void startReversiGame(String mode, String player1, String player2) {
        reversiGame = new ReversiGame(this, mode, player1, player2);
        hideAllMenus();
        reversiGame.start();
    }

    public void closeReversiNameSelectionPVP() {
        if (reversiNamePvp != null) reversiNamePvp.hideMenu();
        openReversiMenu();
    }

    public void onReversiGameFinished() {
        if (reversiGame != null) reversiGame.close();
        openReversiMenu();
    }

    /**
     * Update de taal in alle menu's
     */
    public void updateLanguage() {
        mainMenu.updateLanguage();
        ticTacToeMenu.updateLanguage();
        settingsMenu.updateLanguage();
        ticTacToeNameSelection.updateLanguage();
        ticTacToeNameSelection.updateLanguage();
        //ticTacToeNameServer.updateLanguage();
        reversiMenu.updateLanguage();
        reversiNamePvp.updateLanguage();

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


    public Object getTicTacToeMenu() {
        return ticTacToeMenu;
    }
}
