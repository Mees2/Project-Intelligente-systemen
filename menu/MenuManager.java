package menu;

/**
 * MenuManager beheert de navigatie tussen verschillende menu's en spellen
 * Deze klasse coordineert het openen en sluiten van menu's en het starten van spellen
 */
public class MenuManager {
    private MainMenu mainMenu;
    private TicTacToeMenu ticTacToeMenu;

    /**
     * Constructor voor de MenuManager
     * Initialiseert alle menu's
     */
    public MenuManager() {
        initializeMenus();
    }

    /**
     * Initialiseert alle menu objecten
     */
    private void initializeMenus() {
        mainMenu = new MainMenu(this);
        ticTacToeMenu = new TicTacToeMenu(this);
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
     * Start een TicTacToe spel met de opgegeven mode
     * @param gameMode De spelmode: "PVP" voor Player vs Player, "PVA" voor Player vs AI
     */
    public void startTicTacToeGame(String gameMode) {
        ticTacToeMenu.hideMenu();
        
        // Start het TicTacToe spel in de juiste mode
        if (gameMode.equals("PVP")) {
            startTicTacToePlayerVsPlayer();
        } else if (gameMode.equals("PVA")) {
            startTicTacToePlayerVsAI();
        }
    }

    /**
     * Start TicTacToe in Speler vs Speler mode
     */
    private void startTicTacToePlayerVsPlayer() {
        TicTacToeGame game = new TicTacToeGame(this, "PVP");
        game.start();
    }

    /**
     * Start TicTacToe in Speler vs AI mode
     */
    private void startTicTacToePlayerVsAI() {
        TicTacToeGame game = new TicTacToeGame(this, "PVA");
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