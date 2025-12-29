package framework.gui.menu.tictactoe;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
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

    private int extractMovePosition(String serverMsg) {
        try {
            int moveIndex = serverMsg.indexOf("MOVE:");
            if (moveIndex == -1) return -1;

            String afterMove = serverMsg.substring(moveIndex + 5).trim();
            int start = afterMove.indexOf('"') + 1;
            int end = afterMove.indexOf('"', start);

            if (start > 0 && end > start) {
                String posStr = afterMove.substring(start, end);
                return Integer.parseInt(posStr);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse move position: " + e.getMessage());
        }
        return -1;
    }

    private String extractPlayerName(String serverMsg) {
        try {
            int playerIndex = serverMsg.indexOf("PLAYER:");
            if (playerIndex == -1) return "";

            String afterPlayer = serverMsg.substring(playerIndex + 7).trim();
            int start = afterPlayer.indexOf('"') + 1;
            int end = afterPlayer.indexOf('"', start);

            if (start > 0 && end > start) {
                return afterPlayer.substring(start, end);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse player name: " + e.getMessage());
        }
        return "";
    }

    private String extractFieldValue(String serverMsg, String fieldName) {
        try {
            int idx = serverMsg.indexOf(fieldName + ":");
            if (idx == -1) {
                idx = serverMsg.indexOf(fieldName.toUpperCase() + ":");
                if (idx == -1) return "";
            }
            String after = serverMsg.substring(idx + fieldName.length() + 1).trim();
            int start = after.indexOf('"') + 1;
            int end = after.indexOf('"', start);
            if (start > 0 && end > start) {
                return after.substring(start, end);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse field " + fieldName + ": " + e.getMessage());
        }
        return "";
    }

    private boolean isOurMove(String movePlayerName, String ourBaseName) {
        return movePlayerName.startsWith(ourBaseName);
    }

    private void applyOpponentMove(int pos, char symbol) {
        if (game.isGameOver() || !game.isFree(pos)) return;

        game.doMove(pos, symbol);
        boardPanel.getButtons()[pos].setText(String.valueOf(game.getSymbolAt(pos)));

        if (game.isGameOver()) {
            updateGameEndStatus();
            return;
        }

        turnX = (symbol == 'O');
        updateStatusLabel();

        if (gameMode == GameMode.TOURNAMENT && isPlayersTurn() && !aiBusy) {
            aiTurnPending = true;
            doAiMoveServer();
        }
    }

    public TicTacToeGame(MenuManager menuManager, String gameModeString, String player1, String player2) {
        this.menuManager = menuManager;
        this.gameMode = GameMode.fromCode(gameModeString);
        this.player1 = player1;
        this.player2 = player2;

        if (gameMode == GameMode.PVA) {
            if (player1.equals("AI")) {
                aiRole = 'X';
                playerRole = 'O';
            } else {
                aiRole = 'O';
                playerRole = 'X';
            }
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

        // Wait for connection to stabilize
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }

        // Determine player name and login
        String playerName = determinePlayerName();
        localPlayerName = playerName;
        client.login(playerName);

        // Wait for login confirmation
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
        if (gameMode.isServerMode()) {
            return player1.equals("AI") ? player2 : player1;
        } else if (gameMode == GameMode.PVP) {
            return player1;
        } else {
            return (playerRole == 'X' ? player1 : player2);
        }
    }

    private boolean waitForLogin() {
        int attempts = 0;
        while (!loggedIn && attempts < 20) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            attempts++;
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

        if (gameMode == GameMode.TOURNAMENT) {
            for (JButton b : boardPanel.getButtons()) {
                b.setEnabled(false);
            }
        }

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

        if (gameMode == GameMode.PVA && !isPlayersTurn()) {
            doAiMove();
        }
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

        if (client != null && client.isConnected()) {
            client.sendMove(pos);
        }

        char currentPlayer = turnX ? 'X' : 'O';
        game.doMove(pos, currentPlayer);
        boardPanel.getButtons()[pos].setText(String.valueOf(game.getSymbolAt(pos)));

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
        if (gameMode == GameMode.PVP) {
            return (symbol == 'X') ? player1 : player2;
        } else if (gameMode == GameMode.PVA) {
            if (symbol == playerRole) return (playerRole == 'X') ? player1 : player2;
            else return "AI";
        } else if (gameMode.isServerMode()) {
            if (symbol == playerRole) return localPlayerName != null ? localPlayerName : "";
            else return opponentName != null ? opponentName : "";
        }
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
                    System.err.println("Error while quitting the game: " + e.getMessage());
                    try {
                        client.shutdown();
                    } catch (Exception ignored) {
                    }
                }
            }
            menuManager.onTicTacToeGameFinished();
        }
    }
}
