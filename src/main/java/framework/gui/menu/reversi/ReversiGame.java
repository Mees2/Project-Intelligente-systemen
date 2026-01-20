package framework.gui.menu.reversi;

import reversi.*;
import framework.controllers.MenuManager;
import framework.controllers.LanguageManager;
import framework.boardgame.Move;
import framework.boardgame.Position;
import framework.boardgame.GameResult;
import framework.players.*;
import server.ClientReversi;

import javax.swing.*;
import java.awt.*;

/**
 * Reversi Game Controller - Manages game logic and UI coordination
 * Supports PVP, AI, and TOURNAMENT modes
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

    // Tournament mode fields
    private ClientReversi client;
    private volatile boolean loggedIn = false;
    private String localPlayerName = "";
    private String opponentName = "";
    private char aiRole = 'B';
    private volatile boolean aiTurnPending = false;
    private volatile boolean aiBusy = false;
    private Reversi game;
    private ReversiMinimax minimaxAI;
    private MonteCarloTreeSearchAI mctsAI;
    private boolean useMCTS = false;

    public ReversiGame(MenuManager menuManager, String gameMode, String player1, String player2, char selectedColor) {
        this.menuManager = menuManager;
        this.player1Name = player1;
        this.player2Name = player2;
        this.gameMode = gameMode;
        this.playerColor = selectedColor;
    }

    private boolean isTournamentMode() {
        return gameMode != null && gameMode.startsWith("TOURNAMENT_");
    }

    public void start() {
        game = new Reversi();
        minimaxAI = new ReversiMinimax();
        mctsAI = new MonteCarloTreeSearchAI();

        // Determine if using MCTS based on game mode
        useMCTS = gameMode.contains("MCTS");

        if (isTournamentMode()) {
            startTournamentMode();
        } else {
            startRegularMode();
        }
    }

    private void startRegularMode() {
        AbstractPlayer p1, p2;

        if ("PVA".equalsIgnoreCase(gameMode) || "MCTS".equalsIgnoreCase(gameMode) || "MINIMAX".equalsIgnoreCase(gameMode)) {
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
        gameController.setUseMCTS("MCTS".equalsIgnoreCase(gameMode));
        gameController.setGameListener(this);

        ui = new ReversiUI(game);
        setLayout(new BorderLayout());
        add(ui, BorderLayout.CENTER);
        setPreferredSize(new Dimension(700, 800));

        initializeGame();
    }

    private void startTournamentMode() {
        ui = new ReversiUI(game);
        setLayout(new BorderLayout());
        add(ui, BorderLayout.CENTER);
        setPreferredSize(new Dimension(700, 800));

        ui.initializeUI();
        ui.setButtonClickListener((row, col) -> {}); // Disable manual clicks in tournament
        ui.setMenuButtonListener(this::returnToMenu);
        updateBoardTournament();
        ui.updateStatusLabel("Connecting to server...");

        // Connect to server
        client = new ClientReversi();
        client.setMessageHandler(this::handleServerMessage);

        if (!client.connectToServer()) {
            JOptionPane.showMessageDialog(null, "Failed to connect to server", "Connection Error", JOptionPane.ERROR_MESSAGE);
            menuManager.onReversiGameFinished();
            return;
        }

        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        localPlayerName = player1Name;
        client.login(localPlayerName);

        if (!waitForLogin()) {
            JOptionPane.showMessageDialog(null, "Login failed", "Error", JOptionPane.ERROR_MESSAGE);
            client.shutdown();
            menuManager.onReversiGameFinished();
            return;
        }

        ui.updateStatusLabel("Waiting for opponent...");
        client.requestMatch();
    }

    private boolean waitForLogin() {
        for (int attempts = 0; !loggedIn && attempts < 20; attempts++) {
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }
        return loggedIn;
    }

    private void handleServerMessage(String msg) {
        if (msg.contains("OK")) {
            loggedIn = true;
        } else if (msg.contains("MATCH")) {
            handleMatchMessage(msg);
        } else if (msg.contains("YOURTURN")) {
            handleYourTurnMessage();
        } else if (msg.contains("MOVE") && msg.contains("PLAYER:")) {
            handleMoveMessage(msg);
        } else if (msg.contains("WIN") || msg.contains("LOSS") || msg.contains("DRAW")) {
            handleGameEndMessage(msg);
        }
    }

    private void handleMatchMessage(String msg) {
        String playerToMove = ClientReversi.extractField(msg, "PLAYERTOMOVE");
        String opponent = ClientReversi.extractField(msg, "OPPONENT");
        boolean starterIsLocal = !playerToMove.isEmpty() && !localPlayerName.isEmpty() &&
                                 (playerToMove.startsWith(localPlayerName) || localPlayerName.startsWith(playerToMove));

        SwingUtilities.invokeLater(() -> {
            opponentName = opponent;
            // In Reversi, Black always goes first
            aiRole = starterIsLocal ? 'B' : 'W';

            ui.updateScoreLabel(starterIsLocal ? localPlayerName : opponentName, 2,
                               starterIsLocal ? opponentName : localPlayerName, 2);
            ui.updateStatusLabel("Game started! " + (starterIsLocal ? "Your turn" : opponentName + "'s turn"));
            updateBoardTournament();

            if (starterIsLocal && !aiBusy) {
                aiTurnPending = true;
                doAiMoveTournament();
            }
        });
    }

    private void handleYourTurnMessage() {
        System.out.println("[TIMING] YOURTURN received at: " + System.currentTimeMillis());

        // Start AI calculation IMMEDIATELY on a new thread - don't wait for EDT
        if (!aiBusy) {
            aiTurnPending = true;
            doAiMoveTournament();
        }

        // Update UI separately (non-blocking)
        SwingUtilities.invokeLater(() -> {
            ui.updateStatusLabel("Your turn");
            updateBoardTournament();
        });
    }

    private void handleMoveMessage(String msg) {
        String playerName = ClientReversi.extractPlayerName(msg);
        int movePos = ClientReversi.extractMovePosition(msg);

        if (movePos == -1) return;

        boolean isOurMove = playerName.startsWith(localPlayerName) || localPlayerName.startsWith(playerName);

        if (isOurMove) {
            aiBusy = false;
            aiTurnPending = false;
        } else {
            // Opponent's move - update the board
            char opponentSymbol = (aiRole == 'B') ? 'W' : 'B';
            int row = movePos / 8;
            int col = movePos % 8;

            SwingUtilities.invokeLater(() -> {
                if (game.isValidMove(row, col, opponentSymbol)) {
                    game.doMove(row, col, opponentSymbol);
                    updateBoardTournament();
                    updateScoreLabelTournament();
                }
            });
        }
    }

    private void handleGameEndMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            aiBusy = false;
            aiTurnPending = false;

            String status;
            if (msg.contains("WIN")) {
                status = "You won!";
            } else if (msg.contains("LOSS")) {
                status = "You lost!";
            } else {
                status = "Draw!";
            }
            ui.updateStatusLabel(status);
            updateBoardTournament();

            // Reset for next game after a short delay
            new Thread(() -> {
                try {
                    Thread.sleep(2000); // Show result for 2 seconds
                } catch (InterruptedException ignored) {}
                SwingUtilities.invokeLater(this::resetForNextGame);
            }, "reversi-game-reset").start();
        });
    }

    private void resetForNextGame() {
        game = new Reversi();
        ui.setGame(game);
        aiBusy = false;
        aiTurnPending = false;
        opponentName = "";
        aiRole = 'B';
        updateBoardTournament();
        ui.updateScoreLabel("Black", 2, "White", 2);
        ui.updateStatusLabel("Waiting for next match...");
    }

    private void doAiMoveTournament() {
        if (!isTournamentMode()) return;
        if (game.isWin('B') || game.isWin('W') || game.isDraw()) return;
        if (client == null || !client.isConnected()) return;
        if (!aiTurnPending || aiBusy) return;

        aiBusy = true;
        aiTurnPending = false;

        new Thread(() -> {
            // Check if AI has valid moves
            if (!game.hasValidMove(aiRole)) {
                aiBusy = false;
                return;
            }

            // Get best move from AI
            int[] bestMove;
            if (useMCTS) {
                bestMove = MonteCarloTreeSearchAI.bestMove(game, aiRole);
            } else {
                Position pos = minimaxAI.findBestMove(game, aiRole);
                bestMove = (pos != null) ? new int[]{pos.getRow(), pos.getColumn()} : null;
            }

            if (bestMove == null) {
                aiBusy = false;
                return;
            }

            int row = bestMove[0];
            int col = bestMove[1];
            int position = row * 8 + col;

            // Send move to server
            client.sendMove(position);

            SwingUtilities.invokeLater(() -> {
                if (game.isValidMove(row, col, aiRole)) {
                    game.doMove(row, col, aiRole);
                    updateBoardTournament();
                    updateScoreLabelTournament();
                }
                aiBusy = false;
            });
        }, "reversi-tournament-ai").start();
    }

    private void updateBoardTournament() {
        if (ui != null && game != null) {
            ui.updateBoard(aiRole, false, aiBusy);
        }
    }

    private void updateScoreLabelTournament() {
        if (ui != null && game != null) {
            int blackScore = game.count('B');
            int whiteScore = game.count('W');
            String blackName = (aiRole == 'B') ? localPlayerName : opponentName;
            String whiteName = (aiRole == 'W') ? localPlayerName : opponentName;
            ui.updateScoreLabel(blackName, blackScore, whiteName, whiteScore);
        }
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
            if (isTournamentMode() && client != null) {
                try {
                    client.quit();
                } catch (Exception e) {
                    System.err.println("Error while quitting: " + e.getMessage());
                    try { client.shutdown(); } catch (Exception ignored) {}
                }
            }
            menuManager.onReversiGameFinished();
        }
    }
}
