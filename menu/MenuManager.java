package menu;

/**
 * MenuManager beheert de navigatie tussen verschillende menu's en spellen.
 */
public final class MenuManager {
    private static final String MODE_PVP = "PVP";
    private static final String MODE_PVA = "PVA";

    private final MainMenu mainMenu;
    private final TicTacToeMenu ticTacToeMenu;
    private final SettingsMenu settingsMenu;

    /**
     * Constructor voor de MenuManager
     * Initialiseert alle menu's
     */
    public MenuManager() {
        mainMenu = new MainMenu(this);
        ticTacToeMenu = new TicTacToeMenu(this);
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
    public void startTicTacToeGame(String gameMode) {
        ticTacToeMenu.hideMenu();

        switch (gameMode) {
            case MODE_PVP -> startTicTacToePlayerVsPlayer();
            case MODE_PVA -> startTicTacToePlayerVsAI();
            default -> throw new IllegalArgumentException("Onbekende gameMode: " + gameMode);
        }
    }

    /**
     * Start TicTacToe in Speler vs Speler mode
     */
    private void startTicTacToePlayerVsPlayer() {
        var game = new TicTacToeGame(this, MODE_PVP);
        game.start();
    }

    /**
     * Start TicTacToe in Speler vs AI mode
     */
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
    }
}

//nieuwe 