package framework.controllers;

import javax.swing.JOptionPane;

import framework.gui.menu.*;
import framework.gui.menu.reversi.ReversiGame;
import framework.gui.menu.reversi.ReversiNameSelection;
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
    private final SettingsMenu settingsMenu;
    private final GameMenu ticTacToeMenu;
    private final GameMenu reversiMenu;
    private TicTacToeNameSelection ticTacToeNameSelection;
    private ReversiNameSelection reversiNamePvp;
    private ReversiGame reversiGame;
    private final LanguageManager lang = LanguageManager.getInstance();

    private ClientTicTacToe serverClient;

    /**
     * Constructor voor de MenuManager
     * Initialiseert alle menu's
     */
    public MenuManager() {
        mainMenu = new MainMenu(this);
        ticTacToeMenu = new GameMenu(this, GameMenu.GameType.TICTACTOE);
        reversiMenu = new GameMenu(this, GameMenu.GameType.REVERSI);
        settingsMenu = new SettingsMenu(this);
    }

    /**
     * Start de applicatie door het hoofdmenu te tonen
     */
    public void startApplication() {
        mainMenu.showMenu();
    }

    /**
     * Opent het TicTacToe submenu
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
        reversiMenu.hideMenu();
        mainMenu.showMenu();
    }

    /**
     * Keert terug van naam selectie naar TTT menu
     */
    public void closeNameSelection() {
        if (ticTacToeNameSelection != null) {
            ticTacToeNameSelection.hideMenu();
        }
        ticTacToeMenu.showMenu();
    }

    /**
     * Opent het instellingen menu
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
     * Opent naam selectie voor TicTacToe
     */
    public void openNameSelection(String gameMode) {
        ticTacToeMenu.hideMenu();

        TicTacToeNameSelection.GameMode mode = switch (gameMode) {
            case MODE_PVP -> TicTacToeNameSelection.GameMode.PVP;
            case MODE_PVA -> TicTacToeNameSelection.GameMode.PVA;
            case MODE_SERVER -> TicTacToeNameSelection.GameMode.SERVER;
            case MODE_TOURNAMENT -> TicTacToeNameSelection.GameMode.TOURNAMENT;
            default -> throw new IllegalArgumentException("Unknown game mode: " + gameMode);
        };

        if (ticTacToeNameSelection != null) {
            ticTacToeNameSelection.dispose();
        }
        ticTacToeNameSelection = new TicTacToeNameSelection(this, mode);
        ticTacToeNameSelection.showMenu();
    }

    /**
     * Start een TicTacToe spel
     */
    public void startTicTacToeGame(String gameMode, String player1Name, String player2Name) {
        TicTacToeGame game = new TicTacToeGame(this, gameMode, player1Name, player2Name);
        game.start();
    }

    /**
     * Verbergt alle menu's
     */
    private void hideAllMenus() {
        mainMenu.hideMenu();
        ticTacToeMenu.hideMenu();
        reversiMenu.hideMenu();
        settingsMenu.hideMenu();

        if (ticTacToeNameSelection != null) {
            ticTacToeNameSelection.hideMenu();
        }
        if (reversiNamePvp != null) {
            reversiNamePvp.hideMenu();
        }
    }

    /**
     * Wordt aangeroepen wanneer een spel is afgelopen
     */
    public void onGameFinished() {
        ticTacToeMenu.showMenu();
    }

    /**
     * Opent het Reversi menu
     */
    public void openReversiMenu() {
        hideAllMenus();
        reversiMenu.showMenu();
    }

    /**
     * Opent naam selectie voor Reversi PvP
     */
    public void openReversiNamePvp() {
        reversiMenu.hideMenu();
        if (reversiNamePvp != null) {
            reversiNamePvp.dispose();
        }
        reversiNamePvp = new ReversiNameSelection(this);
        reversiNamePvp.showMenu();
    }

    /**
     * Sluit Reversi naam selectie
     */
    public void closeReversiNameSelectionPVP() {
        if (reversiNamePvp != null) {
            reversiNamePvp.hideMenu();
        }
        reversiMenu.showMenu();
    }

    /**
     * Start een Reversi spel
     */
    public void startReversiGame(String mode, String player1, String player2) {
        reversiGame = new ReversiGame(this, mode, player1, player2);
        hideAllMenus();
        reversiGame.start();
    }

    /**
     * Update de taal van alle menu's
     */
    public void updateLanguage() {
        mainMenu.updateLanguage();
        ticTacToeMenu.updateLanguage();
        reversiMenu.updateLanguage();
        settingsMenu.updateLanguage();

        if (ticTacToeNameSelection != null) {
            ticTacToeNameSelection.updateLanguage();
        }
        if (reversiNamePvp != null) {
            reversiNamePvp.updateLanguage();
        }
    }
    /**
     * Wordt aangeroepen wanneer een Reversi spel is afgelopen
     */
    public void onReversiGameFinished() {
        reversiMenu.showMenu();
    }
    /**
     * Toont een bevestigingsdialoog en sluit de applicatie af bij bevestiging
     */
    public void confirmExit() {
        int result = JOptionPane.showConfirmDialog(
                null,
                lang.get("main.exit.confirm"),
                lang.get("main.exit.title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }


}
