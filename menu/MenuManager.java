package menu;

/**
 * MenuManager beheert de navigatie tussen verschillende menu's en spellen.
 */
public final class MenuManager {
    private static final String MODE_PVP = "PVP";
    private static final String MODE_PVA = "PVA";

    private final MainMenu mainMenu;
    private final TicTacToeMenu ticTacToeMenu;
    private final TicTacToeNamePvp ticTacToeNamePvp;
    private final TicTacToeNamePva ticTacToeNamePva;

    /**
     * Constructor voor de MenuManager
     * Initialiseert alle menu's
     */
    public MenuManager() {
        mainMenu = new MainMenu(this);
        ticTacToeMenu = new TicTacToeMenu(this);
        // sven
        ticTacToeNamePvp = new TicTacToeNamePvp(this);
        ticTacToeNamePva = new TicTacToeNamePva(this);
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
     * Start een TicTacToe spel met de opgegeven mode
     * @param gameMode De spelmode: "PVP" voor Player vs Player, "PVA" voor Player vs AI
     */

    // Was startTicTacToe, misschien wijzigen als reversi wordt geinplementeerd?
    public void openNameSelection(String gameMode) {
        ticTacToeMenu.hideMenu();

        switch (gameMode) {
            case MODE_PVP -> ticTacToeNamePvp.showMenu();
            case MODE_PVA -> ticTacToeNamePva.showMenu();
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
}

//nieuwe 