package framework.gui.menu.reversi;

import reversi.*;
import framework.controllers.MenuManager;
import framework.controllers.LanguageManager;
import framework.boardgame.Move;
import framework.boardgame.GameResult;
import framework.players.*;

import javax.swing.*;
import java.awt.*;

/**
 * Reversi Game Controller - Manages game logic and UI coordination
 */
public class ReversiGame extends JPanel implements ReversiGameController.GameListener {
    private final MenuManager menuManager;
    private final LanguageManager lang = LanguageManager.getInstance();
    private final String player1Name;
    private final String player2Name;
    private final String gameMode;
    private final char playerColor;
    private ReversiUI ui;
    private ReversiGameController gameController;

    // AI configuratie parameters
    private int mctsSimulations = 1000;
    private int minimaxDepth = 5;

    public ReversiGame(MenuManager menuManager, String gameMode, String player1, String player2, char selectedColor) {
        this.menuManager = menuManager;
        this.player1Name = player1;
        this.player2Name = player2;
        this.gameMode = gameMode;
        this.playerColor = selectedColor;
    }

    /**
     * Constructor voor AI vs AI met configureerbare parameters
     */
    public ReversiGame(MenuManager menuManager, String gameMode, String player1, String player2, char selectedColor,
                       int mctsSimulations, int minimaxDepth) {
        this.menuManager = menuManager;
        this.player1Name = player1;
        this.player2Name = player2;
        this.gameMode = gameMode;
        this.playerColor = selectedColor;
        this.mctsSimulations = mctsSimulations;
        this.minimaxDepth = minimaxDepth;
    }

    public void start() {
        Reversi game = new Reversi();
        ReversiMinimax minimaxAI = new ReversiMinimax();
        MonteCarloTreeSearchAI mctsAI = new MonteCarloTreeSearchAI();

        // Configureer de AI parameters dynamisch
        mctsAI.setSimulations(mctsSimulations);
        minimaxAI.setSearchDepth(minimaxDepth);

        AbstractPlayer p1, p2;

        if ("AI_VS_AI".equalsIgnoreCase(gameMode)) {
            // Monte Carlo (Black) vs Minimax (White)
            p1 = new AIPlayer("MCTS", 'B');
            p2 = new AIPlayer("Minimax", 'W');
        } else if ("PVA".equalsIgnoreCase(gameMode) || "MCTS".equalsIgnoreCase(gameMode) || "MINIMAX".equalsIgnoreCase(gameMode)) {
            if (playerColor == 'B') {
                p1 = new HumanPlayer(player1Name, 'B');
                p2 = new AIPlayer(player2Name, 'W');
            } else {
                p1 = new AIPlayer(player2Name, 'B');
                p2 = new HumanPlayer(player1Name, 'W');
            }
        } else {
            if (playerColor == 'B') {
                p1 = new HumanPlayer(player1Name, 'B');
                p2 = new HumanPlayer(player2Name, 'W');
            } else {
                p1 = new HumanPlayer(player2Name, 'B');
                p2 = new HumanPlayer(player1Name, 'W');
            }
        }

        gameController = new ReversiGameController(game, p1, p2, minimaxAI, mctsAI);

        // Configureer welke AI voor welke speler
        if ("AI_VS_AI".equalsIgnoreCase(gameMode)) {
            // Player 1 (Black) = MCTS, Player 2 (White) = Minimax
            gameController.setPlayer1AI("MCTS", mctsSimulations);
            gameController.setPlayer2AI("MINIMAX", minimaxDepth);
        } else if ("MCTS".equalsIgnoreCase(gameMode)) {
            // Speler vs MCTS AI: de AI-speler moet MCTS gebruiken
            if (playerColor == 'B') {
                // Mens = player1 (B), AI = player2 (W) → player2 = MCTS
                gameController.setPlayer1AI("MCTS", mctsSimulations); // ongebruikt, mens is p1
                gameController.setPlayer2AI("MCTS", mctsSimulations);
            } else {
                // AI = player1 (B), Mens = player2 (W) → player1 = MCTS
                gameController.setPlayer1AI("MCTS", mctsSimulations);
                gameController.setPlayer2AI("MCTS", mctsSimulations); // ongebruikt, mens is p2
            }
        } else if ("MINIMAX".equalsIgnoreCase(gameMode)) {
            // Speler vs Minimax AI: de AI-speler moet MINIMAX gebruiken
            if (playerColor == 'B') {
                gameController.setPlayer1AI("MINIMAX", minimaxDepth);
                gameController.setPlayer2AI("MINIMAX", minimaxDepth);
            } else {
                gameController.setPlayer1AI("MINIMAX", minimaxDepth);
                gameController.setPlayer2AI("MINIMAX", minimaxDepth);
            }
        }

        gameController.setGameListener(this);

        ui = new ReversiUI(game);
        setLayout(new BorderLayout());
        add(ui, BorderLayout.CENTER);
        setPreferredSize(new Dimension(700, 800));

        initializeGame();
    }

    private void initializeGame() {
        ui.initializeUI();
        ui.setButtonClickListener(this::handleButtonClick);
        ui.setMenuButtonListener(this::returnToMenu);
        updateBoard();
        updateScoreLabel();
        updateStatusLabel();

        if (gameController.getPlayer1().isAI()) {
            gameController.makeAIMove();
        }
    }

    private void handleButtonClick(int row, int col) {
        if (!gameController.isGameDone() && !gameController.isAIThinking()) {
            gameController.makeMove(row, col);
        }
    }

    @Override
    public void onMoveExecuted(Move move) {
        updateBoard();
        updateScoreLabel();
    }

    @Override
    public void onGameEnded(GameResult result) {
        ui.updateStatusLabel(result.getDescription());
        updateBoard();
    }

    @Override
    public void onStatusChanged(String status) {
        ui.updateStatusLabel(status);
        updateBoard();
    }

    @Override
    public void onAIThinking(boolean thinking) {
        if (thinking) {
            ui.updateStatusLabel("AI thinking...");
        } else {
            updateStatusLabel();
        }
        updateBoard();
    }

    private void updateStatusLabel() {
        if (gameController != null) {
            String status = gameController.getCurrentPlayer().getName() + "'s turn";
            ui.updateStatusLabel(status);
        }
    }

    private void updateScoreLabel() {
        if (gameController != null) {
            String blackPlayerName = gameController.getPlayer1().getSymbol() == 'B'
                    ? gameController.getPlayer1().getName()
                    : gameController.getPlayer2().getName();
            String whitePlayerName = gameController.getPlayer1().getSymbol() == 'W'
                    ? gameController.getPlayer1().getName()
                    : gameController.getPlayer2().getName();

            int blackScore = gameController.getScore('B');
            int whiteScore = gameController.getScore('W');

            ui.updateScoreLabel(blackPlayerName, blackScore, whitePlayerName, whiteScore);
        }
    }

    private void updateBoard() {
        if (gameController != null && ui != null) {
            char currentPlayerSymbol = gameController.getCurrentPlayer().getSymbol();
            boolean isCurrentPlayerHuman = !gameController.getCurrentPlayer().isAI();
            boolean isAIThinking = gameController.isAIThinking();
            ui.updateBoard(currentPlayerSymbol, isCurrentPlayerHuman, isAIThinking);
        }
    }

    private void returnToMenu() {
        int option = JOptionPane.showConfirmDialog(this,
                "Return to menu?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            menuManager.onReversiGameFinished();
        }
    }
}
