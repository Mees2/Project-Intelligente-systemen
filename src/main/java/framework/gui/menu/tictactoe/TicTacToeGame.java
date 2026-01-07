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
    private TicTacToeUI ui;

    // Game Logic
    private final TicTacToe game = new TicTacToe();
    private boolean gameDone = false;

    // Network & Roles
    private char playerRole;
    private char aiRole;
    private ClientTicTacToe client;
    private volatile boolean loggedIn = false;
    private volatile boolean inGame = false;
    private String localPlayerName = "";
    private String opponentName = "";

    private boolean isOurMove(String movePlayerName, String ourBaseName) {
        return movePlayerName.startsWith(ourBaseName);
    }

    private void updateButtonStates(boolean enable) {
        if (ui != null) {
            ui.updateButtonStates(enable);
        }
    }

    private int countMoves() {
        int count = 0;
        for (int i = 0; i < game.getBoardSize(); i++) if (!game.isFree(i)) count++;
        return count;
    }

    // Consolidated message handler
    private void handleServerMessage(String msg) {
        if (msg.contains("OK")) {
            loggedIn = true;
            System.out.println("[DEBUG LOGIN] ✅ Received OK - Login successful! loggedIn = " + loggedIn);
        }
        else if (msg.contains("MATCH")) {
            System.out.println("[DEBUG] Received MATCH message");
            handleMatchMessage(msg);
        }
        else if (msg.contains("YOURTURN")) {
            System.out.println("[DEBUG] Received YOURTURN message");
            handleYourTurnMessage();
        }
        else if (msg.contains("MOVE") && msg.contains("PLAYER:")) {
            System.out.println("[DEBUG] Received MOVE message");
            handleMoveMessage(msg);
        }
    }

    private void handleMatchMessage(String msg) {
        inGame = true;
        String playerToMove = ClientTicTacToe.extractField(msg, "PLAYERTOMOVE");
        String opponent = ClientTicTacToe.extractField(msg, "OPPONENT");
        boolean starterIsLocal = !playerToMove.isEmpty() && !localPlayerName.isEmpty() &&
                                 (playerToMove.startsWith(localPlayerName) || localPlayerName.startsWith(playerToMove));

        SwingUtilities.invokeLater(() -> {
            opponentName = opponent;
            playerRole = starterIsLocal ? 'X' : 'O';
            aiRole = playerRole;
            turnX = true;

            System.out.println("[DEBUG MATCH] Match started! Role: " + playerRole + ", Start: " + starterIsLocal + ", Opponent: " + opponentName);
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
            int moveCount = countMoves();
            turnX = (moveCount % 2 == 0);
            System.out.println("[DEBUG YOURTURN] Received. Moves: " + moveCount + ", turnX: " + turnX + ", role: " + playerRole);
            updateStatusLabel();
            updateButtonStates(true);

            if (gameMode == GameMode.TOURNAMENT && !aiBusy) {
                aiTurnPending = true;
                doAiMoveServer();
            }
        });
    }

    private void handleMoveMessage(String msg) {
        System.out.println("[DEBUG MOVE] ========== PROCESSING MOVE MESSAGE ==========");
        String playerName = ClientTicTacToe.extractPlayerName(msg);
        int movePos = ClientTicTacToe.extractMovePosition(msg);
        String ourName = gameMode.isServerMode() ? localPlayerName : (playerRole == 'X' ? player1 : player2);

        System.out.println("[DEBUG MOVE] Position: " + movePos);
        System.out.println("[DEBUG MOVE] Player: '" + playerName + "'");
        System.out.println("[DEBUG MOVE] Our name: '" + ourName + "'");
        System.out.println("[DEBUG MOVE] Our role: " + playerRole);
        System.out.println("[DEBUG MOVE] UI null? " + (ui == null));
        System.out.println("[DEBUG MOVE] Game null? " + (game == null));

        if (movePos == -1) {
            System.err.println("[DEBUG MOVE] Invalid position!");
            return;
        }

        if (isOurMove(playerName, ourName)) {
            System.out.println("[DEBUG MOVE] >>> Our move - ignoring (already applied locally)");
            aiBusy = false;
            aiTurnPending = false;
        } else {
            System.out.println("[DEBUG MOVE] >>> Opponent move - WILL APPLY TO BOARD");
            char opponentSymbol = (playerRole == 'X') ? 'O' : 'X';
            System.out.println("[DEBUG MOVE] Opponent symbol: " + opponentSymbol);

            SwingUtilities.invokeLater(() -> {
                System.out.println("[DEBUG MOVE] Inside SwingUtilities.invokeLater");
                System.out.println("[DEBUG MOVE] Position " + movePos + " is free: " + game.isFree(movePos));

                if (!game.isFree(movePos)) {
                    System.err.println("[DEBUG MOVE] ERROR: Position not free!");
                    System.err.println("[DEBUG MOVE] Current symbol at position: " + game.getSymbolAt(movePos));
                    return;
                }

                System.out.println("[DEBUG MOVE] Applying move to game state...");
                game.doMove(movePos, opponentSymbol);

                System.out.println("[DEBUG MOVE] Updating UI button text...");
                if (ui != null) {
                    ui.updateButtonText(movePos, opponentSymbol);
                    System.out.println("[DEBUG MOVE] UI updated successfully");
                } else {
                    System.err.println("[DEBUG MOVE] ERROR: UI is null!");
                }

                turnX = (countMoves() % 2 == 0);
                System.out.println("[DEBUG MOVE] Move count: " + countMoves() + ", turnX: " + turnX);
                updateStatusLabel();

                if (game.isGameOver()) {
                    System.out.println("[DEBUG MOVE] Game is over!");
                    updateGameEndStatus();
                } else {
                    System.out.println("[DEBUG MOVE] Enabling buttons for our turn...");
                    updateButtonStates(true);
                }

                System.out.println("[DEBUG MOVE] ========== MOVE PROCESSING COMPLETE ==========");
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

        // Create UI component
        ui = new TicTacToeUI(game);
        setLayout(new BorderLayout());
        add(ui, BorderLayout.CENTER);
        setPreferredSize(new Dimension(500, 600));

    }

    public void start() {
        if (!gameMode.isServerMode()) {
            initializeGame();
            return;
        }

        System.out.println("[DEBUG START] ========== STARTING TOURNAMENT MODE ==========");

        // CRITICAL: Initialize UI FIRST so it's ready when server messages arrive
        initializeGame();
        System.out.println("[DEBUG START] UI initialized");

        client = new ClientTicTacToe();

        // CRITICAL: Set message handler BEFORE connecting so AbstractClient's listener can use it
        client.setMessageHandler(this::handleServerMessage);
        System.out.println("[DEBUG START] Message handler set");

        if (!client.connectToServer()) {
            JOptionPane.showMessageDialog(null,
                    "Failed to connect to server",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            menuManager.onTicTacToeGameFinished();
            return;
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }

        String playerName = determinePlayerName();
        localPlayerName = playerName;
        System.out.println("[DEBUG START] Logging in as: " + playerName);
        client.login(playerName);

        if (!waitForLogin()) {
            JOptionPane.showMessageDialog(null, "Login failed", "Error", JOptionPane.ERROR_MESSAGE);
            client.shutdown();
            menuManager.onTicTacToeGameFinished();
            return;
        }

        System.out.println("[DEBUG START] Login successful!");
        System.out.println("[DEBUG START] Requesting match...");
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

        // Initialize UI with button listeners
        boolean enableButtons = gameMode != GameMode.TOURNAMENT;
        ui.initializeUI(enableButtons);

        // Set up button click listener
        ui.setButtonClickListener(this::handleButtonClick);

        // Set up menu button listener
        ui.setMenuButtonListener(this::returnToMenu);

        updateStatusLabel();

        if (gameMode == GameMode.PVA && !isPlayersTurn()) doAiMove();
    }


    private void handleButtonClick(int pos) {
        if (game.isGameOver() || !game.isFree(pos)) return;
        if (gameMode == GameMode.PVA && !isPlayersTurn()) return;
        if (gameMode == GameMode.TOURNAMENT) return;

        // Validate turn for server modes
        if (gameMode.isServerMode()) {
            if (!ui.isButtonEnabled(pos)) {
                System.out.println("[DEBUG CLICK] Button not enabled!");
                return;
            }
            int moveCount = countMoves();
            char expectedTurn = (moveCount % 2 == 0) ? 'X' : 'O';
            if (expectedTurn != playerRole) {
                System.out.println("[DEBUG CLICK] Not our turn! Expected: " + expectedTurn + ", Role: " + playerRole);
                return;
            }
        }

        char currentPlayer = turnX ? 'X' : 'O';

        if (client != null && client.isConnected()) {
            System.out.println("[DEBUG CLICK] Sending move " + pos + " (" + currentPlayer + ")");
            client.sendMove(pos);
        }

        // Apply move locally
        game.doMove(pos, currentPlayer);
        ui.updateButtonText(pos, currentPlayer);

        if (gameMode.isServerMode()) updateButtonStates(false);

        if (game.isGameOver()) {
            updateGameEndStatus();
            return;
        }

        turnX = (currentPlayer == 'O');
        updateStatusLabel();

        if (gameMode == GameMode.PVA && !isPlayersTurn() && !game.isGameOver()) {
            doAiMove();
        }
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
                ui.updateButtonText(move, aiRole);
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
                ui.updateButtonText(move, aiRole);

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
        String message = "";
        switch (game.getStatus()) {
            case X_WINS:
                String xWinner = getNameBySymbol('X');
                message = lang.get("tictactoe.game.win", xWinner + " (X)");
                break;
            case O_WINS:
                String oWinner = getNameBySymbol('O');
                message = lang.get("tictactoe.game.win", oWinner + " (O)");
                break;
            case DRAW:
                message = lang.get("tictactoe.game.draw");
                break;
            default:
                break;
        }
        ui.updateGameEndStatus(message);
    }

    private boolean isPlayersTurn() {
        return (turnX ? 'X' : 'O') == playerRole;
    }


    private String getNameBySymbol(char symbol) {
        if (gameMode == GameMode.PVP) return (symbol == 'X') ? player1 : player2;
        if (gameMode == GameMode.PVA) return (symbol == playerRole) ? ((playerRole == 'X') ? player1 : player2) : "AI";
        if (gameMode.isServerMode()) return (symbol == playerRole) ? (localPlayerName != null ? localPlayerName : "") : (opponentName != null ? opponentName : "");
        return "";
    }

    private void updateStatusLabel() {
        if (game.isGameOver()) return;
        char currentSymbol = turnX ? 'X' : 'O';
        String currentName = getNameBySymbol(currentSymbol);
        String message;

        // in gamemode Server of Tournament, laat een tijdelijk wacht bericht zien totdat we de naam van de tegenstander hebben ontvangen van de gameserver.
        if (gameMode.isServerMode() && (opponentName == null || opponentName.isEmpty())) {
            message = lang.get("tictactoe.game.turn", lang.get("tictactoe.game.waitingfordetails"));
        } else {
            message = lang.get("tictactoe.game.turn", currentName + " (" + currentSymbol + ")");
        }

        ui.updateStatusLabel(message);
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
