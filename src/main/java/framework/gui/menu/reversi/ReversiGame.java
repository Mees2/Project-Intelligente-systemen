package framework.gui.menu.reversi;

import reversi.*;
import framework.controllers.MenuManager;
import framework.controllers.LanguageManager;
import framework.controllers.GameMode;
import framework.boardgame.Move;
import framework.boardgame.Position;
import framework.boardgame.GameResult;
import framework.players.*;
import server.ClientTicTacToe;

import javax.swing.*;
import java.awt.*;

/**
 * Reversi Game Controller - Manages game logic and UI coordination
 * Supports PVP, PVA, MCTS, MINIMAX, SERVER, and TOURNAMENT modes
 */
public class ReversiGame extends JPanel implements ReversiGameController.GameListener {
    private final MenuManager menuManager;
    private final LanguageManager lang = LanguageManager.getInstance();
    private final String player1Name;
    private final String player2Name;
    private final GameMode gameMode;
    private final char playerColor;
    private ReversiUI ui;
    private ReversiGameController gameController;
    private Reversi game;

    // Server mode fields
    private ClientTicTacToe client;
    private volatile boolean loggedIn = false;
    private String localPlayerName = "";
    private String opponentName = "";
    private char playerRole;
    private char aiRole;
    private volatile boolean aiTurnPending = false;
    private volatile boolean aiBusy = false;
    private boolean currentTurnIsBlack = true;
    private ReversiMinimax minimaxAI;
    private MonteCarloTreeSearchAI mctsAI;

    public ReversiGame(MenuManager menuManager, String gameModeString, String player1, String player2, char selectedColor) {
        this.menuManager = menuManager;
        this.player1Name = player1;
        this.player2Name = player2;
        this.gameMode = GameMode.fromCode(gameModeString.toUpperCase());
        this.playerColor = selectedColor;
    }

    public void start() {
        game = new Reversi();
        minimaxAI = new ReversiMinimax();
        mctsAI = new MonteCarloTreeSearchAI();

        if (!gameMode.isServerMode()) {
            // Local game modes: PVP, PVA, MCTS, MINIMAX
            startLocalGame();
        } else {
            // Server modes: SERVER, TOURNAMENT
            startServerGame();
        }
    }

    private void startLocalGame() {
        AbstractPlayer p1, p2;
        String modeStr = gameMode.getCode();

        if ("PVA".equalsIgnoreCase(modeStr) || "MCTS".equalsIgnoreCase(modeStr) || "MINIMAX".equalsIgnoreCase(modeStr)) {
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
        gameController.setUseMCTS("MCTS".equalsIgnoreCase(modeStr));
        gameController.setGameListener(this);

        ui = new ReversiUI(game);
        setLayout(new BorderLayout());
        add(ui, BorderLayout.CENTER);
        setPreferredSize(new Dimension(700, 800));

        initializeLocalGame();
    }

    private void initializeLocalGame() {
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

    private void startServerGame() {
        ui = new ReversiUI(game);
        setLayout(new BorderLayout());
        add(ui, BorderLayout.CENTER);
        setPreferredSize(new Dimension(700, 800));

        ui.initializeUI();
        ui.setButtonClickListener(this::handleServerButtonClick);
        ui.setMenuButtonListener(this::returnToMenu);
        updateServerBoard(false);
        updateServerScoreLabel();

        client = new ClientTicTacToe();
        client.setMessageHandler(this::handleServerMessage);

        if (!client.connectToServer()) {
            JOptionPane.showMessageDialog(null, "Failed to connect to server", "Connection Error", JOptionPane.ERROR_MESSAGE);
            menuManager.onReversiGameFinished();
            return;
        }

        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        localPlayerName = determinePlayerName();
        client.login(localPlayerName);

        if (!waitForLogin()) {
            JOptionPane.showMessageDialog(null, "Login failed", "Error", JOptionPane.ERROR_MESSAGE);
            client.shutdown();
            menuManager.onReversiGameFinished();
            return;
        }

        client.requestMatch("reversi");
        ui.updateStatusLabel("Waiting for opponent...");
    }

    private String determinePlayerName() {
        if (gameMode.isServerMode()) return player1Name.equals("AI") ? player2Name : player1Name;
        return playerRole == 'B' ? player1Name : player2Name;
    }

    private boolean waitForLogin() {
        for (int attempts = 0; !loggedIn && attempts < 20; attempts++) {
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }
        return loggedIn;
    }

    // ===== SERVER MESSAGE HANDLING =====

    private void handleServerMessage(String msg) {
        if (msg.contains("OK")) loggedIn = true;
        else if (msg.contains("MATCH")) handleMatchMessage(msg);
        else if (msg.contains("YOURTURN")) handleYourTurnMessage();
        else if (msg.contains("MOVE") && msg.contains("PLAYER:")) handleMoveMessage(msg);
        else if (msg.contains("WIN") || msg.contains("LOSS") || msg.contains("DRAW")) handleGameEndMessage(msg);
    }

    private void handleMatchMessage(String msg) {
        String playerToMove = ClientTicTacToe.extractField(msg, "PLAYERTOMOVE");
        String opponent = ClientTicTacToe.extractField(msg, "OPPONENT");
        boolean starterIsLocal = !playerToMove.isEmpty() && !localPlayerName.isEmpty() &&
                                 (playerToMove.startsWith(localPlayerName) || localPlayerName.startsWith(playerToMove));

        SwingUtilities.invokeLater(() -> {
            opponentName = opponent;
            playerRole = starterIsLocal ? 'B' : 'W';
            aiRole = playerRole;
            currentTurnIsBlack = true;
            updateServerStatusLabel();
            updateServerBoard(starterIsLocal);

            if (gameMode == GameMode.TOURNAMENT && playerRole == 'B' && !aiBusy) {
                aiTurnPending = true;
                doAiMoveServer();
            }
        });
    }

    private void handleYourTurnMessage() {
        SwingUtilities.invokeLater(() -> {
            currentTurnIsBlack = (countMoves() % 2 == 0);
            updateServerStatusLabel();
            updateServerBoard(true);
            if (gameMode == GameMode.TOURNAMENT && !aiBusy) {
                aiTurnPending = true;
                doAiMoveServer();
            }
        });
    }

    private void handleMoveMessage(String msg) {
        String playerName = ClientTicTacToe.extractPlayerName(msg);
        int movePos = ClientTicTacToe.extractMovePosition(msg);
        String ourName = gameMode.isServerMode() ? localPlayerName : (playerRole == 'B' ? player1Name : player2Name);

        if (movePos == -1) return;

        if (isOurMove(playerName, ourName)) {
            aiBusy = false;
            aiTurnPending = false;
        } else {
            char opponentSymbol = (playerRole == 'B') ? 'W' : 'B';
            SwingUtilities.invokeLater(() -> {
                int row = movePos / 8;
                int col = movePos % 8;
                if (!game.isValidMove(row, col, opponentSymbol)) return;
                game.doMove(row, col, opponentSymbol);
                currentTurnIsBlack = !currentTurnIsBlack;
                updateServerStatusLabel();
                updateServerScoreLabel();
                updateServerBoard(true);
            });
        }
    }

    private void handleGameEndMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            String result;
            if (msg.contains("WIN")) {
                result = localPlayerName + " wins!";
            } else if (msg.contains("LOSS")) {
                result = opponentName + " wins!";
            } else {
                result = "Draw!";
            }
            ui.updateStatusLabel(result);
            updateServerBoard(false);
        });
    }

    private boolean isOurMove(String movePlayerName, String ourBaseName) {
        return movePlayerName.startsWith(ourBaseName);
    }

    private int countMoves() {
        int count = 0;
        for (int i = 0; i < 64; i++) {
            if (game.getSymbolAt(i / 8, i % 8) != ' ') count++;
        }
        return count - 4; // Subtract initial 4 pieces
    }

    // ===== SERVER BUTTON CLICK =====

    private void handleServerButtonClick(int row, int col) {
        if (game.isWin('B') || game.isWin('W') || game.isDraw()) return;
        if (gameMode == GameMode.TOURNAMENT) return;

        char currentSymbol = currentTurnIsBlack ? 'B' : 'W';
        if (currentSymbol != playerRole) return;
        if (!game.isValidMove(row, col, currentSymbol)) return;

        int pos = row * 8 + col;
        if (client != null && client.isConnected()) client.sendMove(pos);

        game.doMove(row, col, currentSymbol);
        currentTurnIsBlack = !currentTurnIsBlack;
        updateServerStatusLabel();
        updateServerScoreLabel();
        updateServerBoard(false);
    }

    // ===== AI MOVE FOR TOURNAMENT MODE =====

    private void doAiMoveServer() {
        if (gameMode != GameMode.TOURNAMENT) return;
        if (game.isWin('B') || game.isWin('W') || game.isDraw()) return;
        if (client == null || !client.isConnected()) return;

        if (!aiTurnPending || aiBusy) return;
        aiBusy = true;
        aiTurnPending = false;

        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                aiBusy = false;
                return;
            }

            Position bestMove = minimaxAI.findBestMove(game, aiRole);

            if (bestMove == null) {
                aiBusy = false;
                return;
            }

            int pos = bestMove.getRow() * 8 + bestMove.getColumn();
            client.sendMove(pos);

            SwingUtilities.invokeLater(() -> {
                if (!game.isValidMove(bestMove.getRow(), bestMove.getColumn(), aiRole)) {
                    aiBusy = false;
                    return;
                }

                game.doMove(bestMove.getRow(), bestMove.getColumn(), aiRole);
                currentTurnIsBlack = !currentTurnIsBlack;
                updateServerStatusLabel();
                updateServerScoreLabel();
                updateServerBoard(false);
                aiBusy = false;
            });
        }, "reversi-tournament-ai").start();
    }

    // ===== SERVER UI UPDATES =====

    private void updateServerStatusLabel() {
        if (game.isWin('B') || game.isWin('W') || game.isDraw()) return;
        char currentSymbol = currentTurnIsBlack ? 'B' : 'W';
        String currentName = getNameBySymbol(currentSymbol);
        String message = opponentName.isEmpty()
            ? "Waiting for match details..."
            : currentName + "'s turn (" + (currentSymbol == 'B' ? "Black" : "White") + ")";
        ui.updateStatusLabel(message);
    }

    private void updateServerScoreLabel() {
        String blackName = getNameBySymbol('B');
        String whiteName = getNameBySymbol('W');
        int blackScore = game.count('B');
        int whiteScore = game.count('W');
        ui.updateScoreLabel(blackName, blackScore, whiteName, whiteScore);
    }

    private void updateServerBoard(boolean enableButtons) {
        char currentSymbol = currentTurnIsBlack ? 'B' : 'W';
        boolean isPlayerTurn = (currentSymbol == playerRole) && enableButtons;
        ui.updateBoard(currentSymbol, isPlayerTurn && gameMode != GameMode.TOURNAMENT, aiBusy);
    }

    private String getNameBySymbol(char symbol) {
        if (gameMode == GameMode.PVP) return symbol == 'B' ? player1Name : player2Name;
        if (gameMode.isServerMode()) return symbol == playerRole ? localPlayerName : opponentName;
        return symbol == playerColor ? player1Name : player2Name;
    }

    // ===== LOCAL GAME HANDLERS (existing code) =====

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
            if (gameMode.isServerMode() && client != null) {
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
