package framework.gui.menu.tictactoe;

import java.awt.*;
import javax.swing.*;
import java.util.List;

import framework.controllers.GameMode;
import framework.controllers.LanguageManager;
import framework.controllers.MenuManager;
import tictactoe.TicTacToe;
import tictactoe.MinimaxAI;
import server.ClientTicTacToe;

public class TicTacToeGame extends JPanel {
    private final MenuManager menuManager;
    private final GameMode gameMode;
    private final LanguageManager lang = LanguageManager.getInstance();
    private boolean turnX = true;
    private final String player1;
    private final String player2;
    private volatile boolean aiTurnPending = false;
    private volatile boolean aiBusy = false;

    // UI Component
    private TicTacToeUI gameUI;

    private final TicTacToe game = new TicTacToe();
    private boolean gameDone = false;

    private char playerRole;
    private char aiRole;
    private ClientTicTacToe client;
    private volatile boolean loggedIn = false;
    private String localPlayerName = "";
    private String opponentName = "";

    private boolean isOurMove(String movePlayerName, String ourBaseName) {
        return movePlayerName.startsWith(ourBaseName);
    }

    private void updateButtonStates(boolean enable) {
        if (gameUI != null) gameUI.updateButtonStates(enable);
    }

    private int countMoves() {
        int count = 0;
        for (int i = 0; i < game.getBoardSize(); i++) if (!game.isFree(i)) count++;
        return count;
    }

    private void handleServerMessage(String msg) {
        if (msg.contains("OK")) loggedIn = true;
        else if (msg.contains("MATCH")) handleMatchMessage(msg);
        else if (msg.contains("YOURTURN")) handleYourTurnMessage();
        else if (msg.contains("MOVE") && msg.contains("PLAYER:")) handleMoveMessage(msg);
    }

    private void handleMatchMessage(String msg) {
        String playerToMove = ClientTicTacToe.extractField(msg, "PLAYERTOMOVE");
        String opponent = ClientTicTacToe.extractField(msg, "OPPONENT");
        boolean starterIsLocal = !playerToMove.isEmpty() && !localPlayerName.isEmpty() &&
                                 (playerToMove.startsWith(localPlayerName) || localPlayerName.startsWith(playerToMove));

        SwingUtilities.invokeLater(() -> {
            opponentName = opponent;
            playerRole = starterIsLocal ? 'X' : 'O';
            aiRole = playerRole;
            turnX = true;
            updateStatusLabel();
            updateButtonStates(starterIsLocal);

            if (gameMode == GameMode.TOURNAMENT && playerRole == 'X' && !aiBusy) {
                aiTurnPending = true;
                doAiMoveServer();
            }
        });
    }

    private void handleYourTurnMessage() {
        SwingUtilities.invokeLater(() -> {
            turnX = (countMoves() % 2 == 0);
            updateStatusLabel();
            updateButtonStates(true);
            if (gameMode == GameMode.TOURNAMENT && !aiBusy) {
                aiTurnPending = true;
                doAiMoveServer();
            }
        });
    }

    private void handleMoveMessage(String msg) {
        String playerName = ClientTicTacToe.extractPlayerName(msg);
        int movePos = ClientTicTacToe.extractMovePosition(msg);
        String ourName = gameMode.isServerMode() ? localPlayerName : (playerRole == 'X' ? player1 : player2);

        if (movePos == -1) return;

        if (isOurMove(playerName, ourName)) {
            aiBusy = false;
            aiTurnPending = false;
        } else {
            char opponentSymbol = (playerRole == 'X') ? 'O' : 'X';
            SwingUtilities.invokeLater(() -> {
                if (!game.isFree(movePos)) return;
                game.doMove(movePos, opponentSymbol);
                gameUI.updateButtonText(movePos, opponentSymbol);
                turnX = (countMoves() % 2 == 0);
                updateStatusLabel();
                if (game.isGameOver()) updateGameEndStatus();
                else updateButtonStates(true);
            });
        }
    }

    public TicTacToeGame(MenuManager menuManager, String gameModeString, String player1, String player2) {
        this.menuManager = menuManager;
        this.gameMode = GameMode.fromCode(gameModeString);
        this.player1 = player1;
        this.player2 = player2;

        if (gameMode == GameMode.PVA) {
            boolean aiIsX = player1.equals("AI");
            aiRole = aiIsX ? 'X' : 'O';
            playerRole = aiIsX ? 'O' : 'X';
        }

        gameUI = new TicTacToeUI(game);
        setLayout(new BorderLayout());
        add(gameUI, BorderLayout.CENTER);
        setPreferredSize(new Dimension(500, 600));
    }

    public void start() {
        if (!gameMode.isServerMode()) {
            initializeGame();
            return;
        }

        initializeGame();
        client = new ClientTicTacToe();
        client.setMessageHandler(this::handleServerMessage);

        if (!client.connectToServer()) {
            JOptionPane.showMessageDialog(null, "Failed to connect to server", "Connection Error", JOptionPane.ERROR_MESSAGE);
            menuManager.onTicTacToeGameFinished();
            return;
        }

        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        localPlayerName = determinePlayerName();
        client.login(localPlayerName);

        if (!waitForLogin()) {
            JOptionPane.showMessageDialog(null, "Login failed", "Error", JOptionPane.ERROR_MESSAGE);
            client.shutdown();
            menuManager.onTicTacToeGameFinished();
            return;
        }

        client.requestMatch();
    }


    private String determinePlayerName() {
        if (gameMode.isServerMode()) return player1.equals("AI") ? player2 : player1;
        if (gameMode == GameMode.PVP) return player1;
        return playerRole == 'X' ? player1 : player2;
    }

    private boolean waitForLogin() {
        for (int attempts = 0; !loggedIn && attempts < 20; attempts++) {
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }
        return loggedIn;
    }

    private void initializeGame() {
        turnX = true;
        gameDone = false;
        game.reset();
        gameUI.initializeUI(gameMode != GameMode.TOURNAMENT);
        gameUI.setButtonClickListener(this::handleButtonClick);
        gameUI.setMenuButtonListener(this::returnToMenu);
        updateStatusLabel();
        if (gameMode == GameMode.PVA && !isPlayersTurn()) doAiMove();
    }

    private void handleButtonClick(int pos) {
        if (game.isGameOver() || !game.isFree(pos)) return;
        if (gameMode == GameMode.PVA && !isPlayersTurn()) return;
        if (gameMode == GameMode.TOURNAMENT) return;

        if (gameMode.isServerMode()) {
            if (!gameUI.isButtonEnabled(pos)) return;
            if ((countMoves() % 2 == 0 ? 'X' : 'O') != playerRole) return;
        }

        char currentPlayer = turnX ? 'X' : 'O';
        if (client != null && client.isConnected()) client.sendMove(pos);

        game.doMove(pos, currentPlayer);
        gameUI.updateButtonText(pos, currentPlayer);
        if (gameMode.isServerMode()) updateButtonStates(false);

        if (game.isGameOver()) {
            updateGameEndStatus();
            return;
        }

        turnX = (currentPlayer == 'O');
        updateStatusLabel();
        if (gameMode == GameMode.PVA && !isPlayersTurn() && !game.isGameOver()) doAiMove();
    }


    private void doAiMove() {
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            List<Integer> availableMoves = game.getAvailableMoves();
            if (availableMoves.isEmpty()) return;

            char opponentSymbol = (aiRole == 'X') ? 'O' : 'X';
            int move = MinimaxAI.bestMove(game, aiRole, opponentSymbol);

            if (move != -1 && game.isFree(move)) {
                game.doMove(move, aiRole);
                gameUI.updateButtonText(move, aiRole);
            }

            if (game.isGameOver()) {
                updateGameEndStatus();
                return;
            }

            turnX = (aiRole == 'O');
            updateStatusLabel();
        });
    }

    private void doAiMoveServer() {
        if (gameMode != GameMode.TOURNAMENT) return;
        if (game.isGameOver() || client == null || !client.isConnected()) return;

        if (!aiTurnPending || aiBusy) return;
        aiBusy = true;
        aiTurnPending = false;

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                aiBusy = false;
                return;
            }

            List<Integer> availableMoves = game.getAvailableMoves();
            if (availableMoves.isEmpty()) {
                aiBusy = false;
                return;
            }

            char opponentSymbol = (aiRole == 'X') ? 'O' : 'X';
            int move = MinimaxAI.bestMove(game, aiRole, opponentSymbol);

            if (move == -1) {
                aiBusy = false;
                return;
            }

            client.sendMove(move);

            SwingUtilities.invokeLater(() -> {
                if (game.isGameOver() || !game.isFree(move)) {
                    aiBusy = false;
                    return;
                }

                game.doMove(move, aiRole);
                gameUI.updateButtonText(move, aiRole);

                if (game.isGameOver()) {
                    updateGameEndStatus();
                    aiBusy = false;
                    return;
                }

                turnX = (aiRole == 'O');
                updateStatusLabel();
                aiBusy = false;
            });
        }, "tournament-ai").start();
    }

    private void updateGameEndStatus() {
        gameDone = true;
        switch (game.getStatus()) {
            case X_WINS:
                gameUI.updateGameEndStatus(lang.get("tictactoe.game.win", getNameBySymbol('X') + " (X)"));
                break;
            case O_WINS:
                gameUI.updateGameEndStatus(lang.get("tictactoe.game.win", getNameBySymbol('O') + " (O)"));
                break;
            case DRAW:
                gameUI.updateGameEndStatus(lang.get("tictactoe.game.draw"));
                break;
        }
    }

    private boolean isPlayersTurn() {
        return (turnX ? 'X' : 'O') == playerRole;
    }

    private String getNameBySymbol(char symbol) {
        if (gameMode == GameMode.PVP) return symbol == 'X' ? player1 : player2;
        if (gameMode == GameMode.PVA) return symbol == playerRole ? (playerRole == 'X' ? player1 : player2) : "AI";
        if (gameMode.isServerMode()) return symbol == playerRole ? localPlayerName : opponentName;
        return "";
    }

    private void updateStatusLabel() {
        if (game.isGameOver()) return;
        char currentSymbol = turnX ? 'X' : 'O';
        String currentName = getNameBySymbol(currentSymbol);
        String message = gameMode.isServerMode() && opponentName.isEmpty()
            ? lang.get("tictactoe.game.turn", lang.get("tictactoe.game.waitingfordetails"))
            : lang.get("tictactoe.game.turn", currentName + " (" + currentSymbol + ")");
        gameUI.updateStatusLabel(message);
    }

    private void returnToMenu() {
        int option = JOptionPane.showConfirmDialog(this,
                lang.get("main.exit.confirm"),
                lang.get("main.exit.title"),
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
            menuManager.onTicTacToeGameFinished();
        }
    }
}
