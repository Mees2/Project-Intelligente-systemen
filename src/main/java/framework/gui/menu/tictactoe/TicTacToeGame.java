package framework.gui.menu.tictactoe;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

import framework.controllers.GameMode;
import framework.controllers.LanguageManager;
import framework.controllers.MenuManager;
import framework.controllers.ThemeManager;
import framework.gui.AbstractRoundedButton;
import tictactoe.TicTacToe;
import tictactoe.MinimaxAI;
import server.ClientTicTacToe;

public class TicTacToeGame extends AbstractRoundedButton {
    private final MenuManager menuManager;
    private final GameMode gameMode;
    private final LanguageManager lang = LanguageManager.getInstance();
    private final ThemeManager theme = ThemeManager.getInstance();
    private boolean turnX = true;
    private final String player1;
    private final String player2;
    private volatile boolean aiTurnPending = false;
    private volatile boolean aiBusy = false;

    private JLabel statusLabel;
    private JButton menuButton;
    private SquareBoardPanel boardPanel;
    private final TicTacToe game = new TicTacToe();
    private boolean gameDone = false;

    private char playerRole;
    private char aiRole;
    private ClientTicTacToe client;
    private volatile boolean loggedIn = false;
    private volatile boolean inGame = false;
    private String localPlayerName = "";
    private String opponentName = "";

    private String extractField(String serverMsg, String fieldName) {
        try {
            int idx = serverMsg.indexOf(fieldName + ":");
            if (idx == -1 && (idx = serverMsg.indexOf(fieldName.toUpperCase() + ":")) == -1) return "";
            String after = serverMsg.substring(idx + fieldName.length() + 1).trim();
            int start = after.indexOf('"') + 1, end = after.indexOf('"', start);
            return (start > 0 && end > start) ? after.substring(start, end) : "";
        } catch (Exception e) {
            System.err.println("Failed to parse " + fieldName + ": " + e.getMessage());
            return "";
        }
    }

    private int extractMovePosition(String msg) {
        try { return Integer.parseInt(extractField(msg, "MOVE")); }
        catch (NumberFormatException e) { return -1; }
    }

    private String extractPlayerName(String msg) { return extractField(msg, "PLAYER"); }

    private boolean isOurMove(String movePlayerName, String ourBaseName) {
        return movePlayerName.startsWith(ourBaseName);
    }

    private void updateButtonStates(boolean enable) {
        if (boardPanel == null || boardPanel.getButtons() == null) {
            System.err.println("[DEBUG] updateButtonStates called but boardPanel is null!");
            return;
        }

        System.out.println("[DEBUG] updateButtonStates called with enable=" + enable);
        for (JButton btn : boardPanel.getButtons()) {
            int index = java.util.Arrays.asList(boardPanel.getButtons()).indexOf(btn);
            boolean shouldEnable = enable && game.isFree(index) && !game.isGameOver();
            btn.setEnabled(shouldEnable);
            System.out.println("[DEBUG] Button " + index + " set to: " + shouldEnable + " (free=" + game.isFree(index) + ", gameOver=" + game.isGameOver() + ")");
        }
    }

    private int countMoves() {
        int count = 0;
        for (int i = 0; i < game.getBoardSize(); i++) if (!game.isFree(i)) count++;
        return count;
    }

    // Consolidated message handler
    private void handleServerMessage(String msg) {
        String timestamp = java.time.LocalTime.now().toString();
        System.out.println("\n[" + timestamp + "] [Server MSG] " + msg);

        if (msg.contains("OK")) {
            loggedIn = true;
            System.out.println("[" + timestamp + "] [LOGIN] Successfully logged in");
        }
        else if (msg.contains("MATCH")) handleMatchMessage(msg);
        else if (msg.contains("YOURTURN")) handleYourTurnMessage();
        else if (msg.contains("MOVE") && msg.contains("PLAYER:")) handleMoveMessage(msg);
    }

    private void handleMatchMessage(String msg) {
        inGame = true;
        String playerToMove = extractField(msg, "PLAYERTOMOVE");
        String opponent = extractField(msg, "OPPONENT");
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
        String playerName = extractPlayerName(msg);
        int movePos = extractMovePosition(msg);
        String ourName = gameMode.isServerMode() ? localPlayerName : (playerRole == 'X' ? player1 : player2);

        System.out.println("[DEBUG MOVE] Position: " + movePos + ", Player: '" + playerName + "', Our: '" + ourName + "'");

        if (movePos == -1) {
            System.err.println("[DEBUG MOVE] Invalid position!");
            return;
        }

        if (isOurMove(playerName, ourName)) {
            System.out.println("[DEBUG MOVE] Our move - ignoring");
            aiBusy = false;
            aiTurnPending = false;
        } else {
            System.out.println("[DEBUG MOVE] Opponent move - applying");
            char opponentSymbol = (playerRole == 'X') ? 'O' : 'X';

            SwingUtilities.invokeLater(() -> {
                if (!game.isFree(movePos)) {
                    System.err.println("[DEBUG MOVE] Position not free!");
                    return;
                }

                game.doMove(movePos, opponentSymbol);
                JButton button = boardPanel.getButtons()[movePos];
                button.setText(String.valueOf(opponentSymbol));
                button.repaint();

                turnX = (countMoves() % 2 == 0);
                System.out.println("[DEBUG MOVE] Applied. Moves: " + countMoves() + ", turnX: " + turnX);
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

        theme.addThemeChangeListener(this::updateTheme);
        setPreferredSize(new Dimension(500, 600));

    }

    public void start() {
        if (!gameMode.isServerMode()) {
            initializeGame();
            return;
        }

        client = new ClientTicTacToe();
        if (!client.connectToServer()) {
            JOptionPane.showMessageDialog(null,
                    "Failed to connect to server",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            menuManager.onTicTacToeGameFinished();
            return;
        }

        // Start server listener thread
        new Thread(() -> {
            try {
                String serverMsg;
                while (client.isConnected() && (serverMsg = client.getReader().readLine()) != null) {
                    handleServerMessage(serverMsg);
                }
            } catch (IOException e) {
                System.err.println("Connection lost: " + e.getMessage());
            }
        }, "server-listener").start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }

        String playerName = determinePlayerName();
        localPlayerName = playerName;
        client.login(playerName);

        if (!waitForLogin()) {
            JOptionPane.showMessageDialog(null, "Login failed", "Error", JOptionPane.ERROR_MESSAGE);
            client.shutdown();
            menuManager.onTicTacToeGameFinished();
            return;
        }

        client.requestMatch();
        initializeGame();
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

        setLayout(new BorderLayout());
        setBackground(ThemeManager.getInstance().getBackgroundColor());

        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        statusLabel.setForeground(ThemeManager.getInstance().getFontColor1());
        statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(statusLabel, BorderLayout.NORTH);

        boardPanel = new SquareBoardPanel();
        boardPanel.setBackground(ThemeManager.getInstance().getBackgroundColor());
        add(boardPanel, BorderLayout.CENTER);

        if (gameMode == GameMode.TOURNAMENT) updateButtonStates(false);

        menuButton = createRoundedButton(lang.get("tictactoe.game.menu"),
                new Color(184, 107, 214), new Color(204, 127, 234), new Color(120, 60, 150), true);
        menuButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        menuButton.setPreferredSize(new Dimension(320, 45));
        menuButton.addActionListener(e -> returnToMenu());

        JPanel southPanel = new JPanel();
        southPanel.setBackground(ThemeManager.getInstance().getBackgroundColor());
        southPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));
        southPanel.add(menuButton);
        add(southPanel, BorderLayout.SOUTH);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                statusLabel.setFont(statusLabel.getFont().deriveFont((float) Math.max(18, getHeight() / 25)));
            }
        });
        updateStatusLabel();

        if (gameMode == GameMode.PVA && !isPlayersTurn()) doAiMove();
    }

    private class SquareBoardPanel extends JPanel {
        private final JButton[] buttons;

        public SquareBoardPanel() {
            buttons = new JButton[game.getBoardSize()];
            setLayout(null);
            setBackground(ThemeManager.getInstance().getBackgroundColor());
            for (int i = 0; i < game.getBoardSize(); i++) {
                JButton btn = createTicTacToeButton("", ThemeManager.getInstance().getTitleColor(), ThemeManager.getInstance().getFontColor1(), new Color(120, 60, 150), true);
                btn.setFocusPainted(false);
                btn.setFont(new Font("SansSerif", Font.BOLD, 40));
                final int pos = i;
                if (gameMode != GameMode.TOURNAMENT) {
                    btn.addActionListener(e -> handleButtonClick(pos));
                }
                buttons[i] = btn;
                add(btn);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            layoutButtons();
        }

        private void layoutButtons() {
            int size = Math.min(getWidth(), getHeight());
            int marginX = (getWidth() - size) / 2;
            int marginY = (getHeight() - size) / 2;
            int cell = size / game.getBoardWidth();
            int gap = Math.max(6, cell / 15);

            for (int row = 0; row < game.getBoardHeight(); row++) {
                for (int col = 0; col < game.getBoardWidth(); col++) {
                    int idx = row * game.getBoardWidth() + col;
                    JButton btn = buttons[idx];
                    int x = marginX + col * cell + gap / 2;
                    int y = marginY + row * cell + gap / 2;
                    int w = cell - gap;
                    int h = cell - gap;
                    btn.setBounds(x, y, w, h);
                }
            }
        }
        public JButton[] getButtons() {
            return buttons;
        }
    }

    private void handleButtonClick(int pos) {
        if (game.isGameOver() || !game.isFree(pos)) return;
        if (gameMode == GameMode.PVA && !isPlayersTurn()) return;
        if (gameMode == GameMode.TOURNAMENT) return;

        // Validate turn for server modes
        if (gameMode.isServerMode()) {
            if (!boardPanel.getButtons()[pos].isEnabled()) {
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
        boardPanel.getButtons()[pos].setText(String.valueOf(game.getSymbolAt(pos)));
        boardPanel.getButtons()[pos].repaint();


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
                boardPanel.getButtons()[move].setText(String.valueOf(game.getSymbolAt(move)));
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
                boardPanel.getButtons()[move].setText(String.valueOf(game.getSymbolAt(move)));

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
                String xWinner = getNameBySymbol('X');
                statusLabel.setText(lang.get("tictactoe.game.win", xWinner + " (X)"));
                break;
            case O_WINS:
                String oWinner = getNameBySymbol('O');
                statusLabel.setText(lang.get("tictactoe.game.win", oWinner + " (O)"));
                break;
            case DRAW:
                statusLabel.setText(lang.get("tictactoe.game.draw"));
                break;
            default:
                break;
        }
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
        // in gamemode Server of Tournament, laat een tijdelijk wacht bericht zien totdat we de naam van de tegenstander hebben ontvangen van de gameserver.
        if (gameMode.isServerMode() && (opponentName == null || opponentName.isEmpty())) {
            statusLabel.setText(lang.get("tictactoe.game.turn", lang.get("tictactoe.game.waitingfordetails")));
            return;
        }
        statusLabel.setText(lang.get("tictactoe.game.turn", currentName + " (" + currentSymbol + ")"));
    }

    private void updateTheme() {
        setBackground(theme.getBackgroundColor());
        statusLabel.setForeground(theme.getFontColor1());
        boardPanel.setBackground(theme.getBackgroundColor());

        menuButton.putClientProperty("baseColor", theme.getMainButtonColor());
        menuButton.putClientProperty("hoverColor", theme.getMainButtonColorHover());
        menuButton.putClientProperty("borderColor", theme.getMainButtonColor().darker());
        menuButton.repaint();

        for (JButton btn : boardPanel.getButtons()) {
            btn.putClientProperty("baseColor", theme.getTitleColor());
            btn.putClientProperty("hoverColor", theme.getTitleColor());
            btn.putClientProperty("borderColor", new Color(120, 60, 150));
            btn.repaint();
        }

        repaint();
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
