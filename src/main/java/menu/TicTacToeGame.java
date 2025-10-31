package menu;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;

import tictactoe.TicTacToe;
import tictactoe.MinimaxAI;
import server.ClientTicTacToe;

public class TicTacToeGame {
    private final MenuManager menuManager;
    private final String gameMode; // "PVP" or "PVA" or "SERVER" or "TOURNAMENT"
    private final LanguageManager lang = LanguageManager.getInstance();
    private final ThemeManager theme = ThemeManager.getInstance();
    private boolean turnX = true;
    private final String speler1;
    private final String speler2;
    private volatile boolean aiTurnPending = false;
    private volatile boolean aiBusy = false;

    private JLabel statusLabel;
    private JButton menuButton;
    private SquareBoardPanel boardPanel;
    private final TicTacToe game = new TicTacToe();
    private boolean gameDone = false;
    private JFrame gameFrame;
    private char spelerRol;
    private char aiRol;
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

    /**
     * Apply opponent's move and update turn tracking consistently.
     * After opponent plays a symbol, toggle turn based on that symbol.
     */
    private void applyOpponentMove(int pos, char symbol) {
        if (gameDone || !game.isFree(pos)) return;

        game.doMove(pos, symbol);
        boardPanel.getButtons()[pos].setText(String.valueOf(symbol));

        if (checkEnd(symbol)) return;

        // After opponent plays, toggle turn: if they played X, next is O; if they played O, next is X
        turnX = (symbol == 'O');
        updateStatusLabel();

        // Trigger AI move if it's now the AI's turn in TOURNAMENT mode
        if ("TOURNAMENT".equals(gameMode) && isPlayersTurn() && !aiBusy) {
            aiTurnPending = true;
            doAiMoveServer();
        }
    }

    public TicTacToeGame(MenuManager menuManager, String gameMode, String speler1, String speler2) {
        this.menuManager = menuManager;
        this.gameMode = gameMode;
        this.speler1 = speler1;
        this.speler2 = speler2;

        if (gameMode.equals("PVA")) {
            if (speler1.equals("AI")) {
                aiRol = 'X';
                spelerRol = 'O';
            } else {
                aiRol = 'O';
                spelerRol = 'X';
            }
        } else if (gameMode.equals("SERVER") || gameMode.equals("TOURNAMENT")) {
            aiRol = 'O'; // placeholder, will be set on MATCH
        }

        ThemeManager.getInstance().addThemeChangeListener(this::updateTheme);

    }

    public void start() {
        if (!"SERVER".equals(gameMode) && !"TOURNAMENT".equals(gameMode)) {
            initializeGame();
            return;
        }

        client = new ClientTicTacToe();
        if (!client.connectToServer()) {
            JOptionPane.showMessageDialog(null,
                    "Failed to connect to server",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            menuManager.onGameFinished();
            return;
        }

        new Thread(() -> {
            try {
                String serverMsg;
                while (client.isConnected() && (serverMsg = client.getReader().readLine()) != null) {
                    System.out.println("[Server] " + serverMsg);

                    if (serverMsg.contains("OK")) {
                        loggedIn = true;
                    }

                    if (serverMsg.contains("MATCH")) {
                        inGame = true;
                        String playerToMove = extractFieldValue(serverMsg, "PLAYERTOMOVE");
                        String opponent = extractFieldValue(serverMsg, "OPPONENT");

                        boolean playerToMoveIsLocal = false;
                        if (!playerToMove.isEmpty() && !localPlayerName.isEmpty()) {
                            if (playerToMove.startsWith(localPlayerName) || localPlayerName.startsWith(playerToMove))
                                playerToMoveIsLocal = true;
                        }

                        final boolean starterIsLocal = playerToMoveIsLocal;
                        final String opponentFinal = opponent;

                        SwingUtilities.invokeLater(() -> {
                            opponentName = opponentFinal != null ? opponentFinal : "";

                            // Server tells who starts; convention: starter -> X
                            spelerRol = starterIsLocal ? 'X' : 'O';
                            aiRol = spelerRol; // In TOURNAMENT mode, AI plays with our assigned symbol

                            // Set initial turn: X always goes first
                            turnX = true;
                            updateStatusLabel();

                            // Schedule AI move if we start (our symbol is X)
                            if ("TOURNAMENT".equals(gameMode) && spelerRol == 'X') {
                                aiTurnPending = true;
                                if (!aiBusy) doAiMoveServer();
                            }
                        });
                    }

                    if (serverMsg.contains("YOURTURN")) {
                        SwingUtilities.invokeLater(() -> {
                            // Ensure turn state matches our symbol
                            turnX = (spelerRol == 'X');
                            updateStatusLabel();

                            if ("TOURNAMENT".equals(gameMode) && !aiBusy) {
                                aiTurnPending = true;
                                doAiMoveServer();
                            }
                        });
                    }

                    if (serverMsg.contains("MOVE") && serverMsg.contains("PLAYER:")) {
                        String playerName = extractPlayerName(serverMsg);
                        int movePos = extractMovePosition(serverMsg);

                        String ourName = gameMode.equals("PVP") ? speler1 :
                                ("SERVER".equals(gameMode) || "TOURNAMENT".equals(gameMode)) ? localPlayerName :
                                        (spelerRol == 'X' ? speler1 : speler2);

                        System.out.println("[DEBUG] Move from player: '" + playerName + "', our name: '" + ourName + "', our role: " + spelerRol);

                        if (movePos != -1) {
                            if (isOurMove(playerName, ourName)) {
                                System.out.println("[DEBUG] Recognized as OUR move - ignoring");
                                aiBusy = false;
                                aiTurnPending = false;
                            } else {
                                System.out.println("[DEBUG] Recognized as OPPONENT move - applying");
                                char opponentSymbol = (spelerRol == 'X') ? 'O' : 'X';
                                System.out.println("[DEBUG] Applying opponent symbol: " + opponentSymbol + " at position: " + movePos);
                                SwingUtilities.invokeLater(() -> applyOpponentMove(movePos, opponentSymbol));
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Connection lost: " + e.getMessage());
            }
        }, "server-listener").start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }

        String playerName;
        if ("SERVER".equals(gameMode) || "TOURNAMENT".equals(gameMode)) {
            playerName = speler1.equals("AI") ? speler2 : speler1;
        } else if ("PVP".equals(gameMode)) {
            playerName = speler1;
        } else {
            playerName = (spelerRol == 'X' ? speler1 : speler2);
        }

        localPlayerName = playerName;
        client.login(playerName);

        int attempts = 0;
        while (!loggedIn && attempts < 20) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            attempts++;
        }

        if (!loggedIn) {
            JOptionPane.showMessageDialog(null, "Login failed", "Error", JOptionPane.ERROR_MESSAGE);
            client.shutdown();
            menuManager.onGameFinished();
            return;
        }

        client.requestMatch();
        initializeGame();
    }

    private void initializeGame() {
        turnX = true;
        gameDone = false;

        gameFrame = new JFrame(getTitleForMode());
        gameFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        gameFrame.setSize(500, 600);
        gameFrame.setMinimumSize(new Dimension(400, 500));
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setLayout(new BorderLayout());
        gameFrame.getContentPane().setBackground(ThemeManager.getInstance().getBackgroundColor());


        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        statusLabel.setForeground(ThemeManager.getInstance().getFontColor1());
        statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        gameFrame.add(statusLabel, BorderLayout.NORTH);

        boardPanel = new SquareBoardPanel();
        boardPanel.setBackground(ThemeManager.getInstance().getBackgroundColor());
        gameFrame.add(boardPanel, BorderLayout.CENTER);

        if ("TOURNAMENT".equals(gameMode)) {
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
        gameFrame.add(southPanel, BorderLayout.SOUTH);

        gameFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                boardPanel.resizeFont();
                statusLabel.setFont(statusLabel.getFont().deriveFont((float) Math.max(18, gameFrame.getHeight() / 25)));
            }
        });

        gameFrame.setVisible(true);
        updateStatusLabel();

        if (gameMode.equals("PVA") && !isPlayersTurn()) {
            doAiMove();
        }
    }

    private class SquareBoardPanel extends JPanel {
        private final JButton[] buttons = new JButton[9];

        public SquareBoardPanel() {
            setLayout(null);
            setBackground(ThemeManager.getInstance().getBackgroundColor());
            for (int i = 0; i < 9; i++) {
                JButton btn = createRoundedButton("", ThemeManager.getInstance().getTitleColor(), ThemeManager.getInstance().getFontColor1(), new Color(120, 60, 150), true);
                btn.setFocusPainted(false);
                btn.setFont(new Font("SansSerif", Font.BOLD, 40));
                final int pos = i;
                if (!"TOURNAMENT".equals(gameMode)) {
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
            int cell = size / 3;
            int gap = Math.max(6, cell / 15);

            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    int idx = row * 3 + col;
                    JButton btn = buttons[idx];
                    int x = marginX + col * cell + gap / 2;
                    int y = marginY + row * cell + gap / 2;
                    int w = cell - gap;
                    int h = cell - gap;
                    btn.setBounds(x, y, w, h);
                }
            }
        }

        public void resizeFont() {
            int size = Math.min(getWidth(), getHeight());
            int fontSize = Math.max(24, size / 6);
            for (JButton btn : buttons) {
                btn.setFont(new Font("SansSerif", Font.BOLD, fontSize));
            }
        }

        public JButton[] getButtons() {
            return buttons;
        }
    }

    private JButton createRoundedButton(String text, Color baseColor, Color hoverColor, Color borderColor, boolean enabled) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color base = (Color) getClientProperty("baseColor");
                Color hover = (Color) getClientProperty("hoverColor");
                Color border = (Color) getClientProperty("borderColor");
                if (base == null) base = baseColor;
                if (hover == null) hover = hoverColor;
                if (border == null) border = borderColor;

                int arc = 20;
                g2.setColor(isEnabled() ? baseColor : Color.LIGHT_GRAY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setForeground(new Color(5, 5, 169));
        button.setEnabled(enabled);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) button.setBackground(baseColor);
            }
        });
        return button;
    }

    /**
     * Handle button click in PVP or PVA modes.
     * After a move, toggle turn based on the symbol that just played.
     */
    private void handleButtonClick(int pos) {
        if (gameDone || !game.isFree(pos)) return;
        if (gameMode.equals("PVA") && !isPlayersTurn()) return;
        if ("TOURNAMENT".equals(gameMode)) return;

        if (client != null && client.isConnected()) {
            client.sendMove(pos);
        }

        char currentPlayer = turnX ? 'X' : 'O';
        game.doMove(pos, currentPlayer);
        boardPanel.getButtons()[pos].setText(String.valueOf(currentPlayer));

        if (checkEnd(currentPlayer)) return;

        // Toggle turn: if X just played, next is O; if O just played, next is X
        turnX = (currentPlayer == 'O');
        updateStatusLabel();

        if (gameMode.equals("PVA") && !isPlayersTurn() && !gameDone) {
            doAiMove();
        }
    }

    /**
     * AI move in PVA mode.
     * After AI plays, toggle turn based on the AI's symbol.
     */
    private void doAiMove() {
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            char opponentSymbol = (aiRol == 'X') ? 'O' : 'X';
            int move = MinimaxAI.bestMove(game, aiRol, opponentSymbol);

            if (move != -1) {
                game.doMove(move, aiRol);
                boardPanel.getButtons()[move].setText(String.valueOf(aiRol));
            }

            if (checkEnd(aiRol)) return;

            // After AI plays, toggle turn: if AI played X, next is O; if AI played O, next is X
            turnX = (aiRol == 'O');
            updateStatusLabel();
        });
    }

    /**
     * AI move in TOURNAMENT mode.
     * After AI plays, toggle turn based on the AI's symbol.
     */
    private void doAiMoveServer() {
        if (!"TOURNAMENT".equals(gameMode)) return;
        if (gameDone || client == null || !client.isConnected()) return;

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

            char opponentSymbol = (aiRol == 'X') ? 'O' : 'X';
            int move = MinimaxAI.bestMove(game, aiRol, opponentSymbol);

            if (move == -1) {
                aiBusy = false;
                return;
            }

            client.sendMove(move);

            SwingUtilities.invokeLater(() -> {
                if (gameDone || !game.isFree(move)) {
                    aiBusy = false;
                    return;
                }

                game.doMove(move, aiRol);
                boardPanel.getButtons()[move].setText(String.valueOf(aiRol));

                if (checkEnd(aiRol)) {
                    aiBusy = false;
                    return;
                }

                // After AI plays, toggle turn: if AI played X, next is O; if AI played O, next is X
                turnX = (aiRol == 'O');
                updateStatusLabel();
                aiBusy = false;
            });
        }, "tournament-ai").start();
    }

    private boolean checkEnd(char player) {
        if (game.isWin(player)) {
            String winnaar = getNameBySymbol(player);
            statusLabel.setText(lang.get("tictactoe.game.win", winnaar + " (" + player + ")"));
            gameDone = true;
            return true;
        } else if (game.isDraw()) {
            statusLabel.setText(lang.get("tictactoe.game.draw"));
            gameDone = true;
            return true;
        } else if (!isWinPossible()) {
            statusLabel.setText(lang.get("tictactoe.game.draw"));
            gameDone = true;
            return true;
        }
        return false;
    }

    private String getTitleForMode() {
        if (gameMode.equals("PVP")) {
            return lang.get("tictactoe.game.title.pvp");
        } else if (gameMode.equals("PVA")) {
            return lang.get("tictactoe.game.title.pva");
        } else if (gameMode.equals("SERVER") || gameMode.equals("TOURNAMENT")) {
            return lang.get("tictactoe.game.title.server");
        }
        return lang.get("tictactoe.game.title");
    }

    private boolean isPlayersTurn() {
        return (turnX ? 'X' : 'O') == spelerRol;
    }

    private String getNameBySymbol(char symbol) {
        if (gameMode.equals("PVP")) {
            return (symbol == 'X') ? speler1 : speler2;
        } else if (gameMode.equals("PVA")) {
            if (symbol == spelerRol) return (spelerRol == 'X') ? speler1 : speler2;
            else return "AI";
        } else if ("SERVER".equals(gameMode) || "TOURNAMENT".equals(gameMode)) {
            if (symbol == spelerRol) return localPlayerName != null ? localPlayerName : "";
            else return opponentName != null ? opponentName : "";
        }
        return "";
    }

    private void updateStatusLabel() {
        if (gameDone) return;
        char currentSymbol = turnX ? 'X' : 'O';
        String currentName = getNameBySymbol(currentSymbol);
        if (("SERVER".equals(gameMode) || "TOURNAMENT".equals(gameMode)) && (opponentName == null || opponentName.isEmpty())) {
            statusLabel.setText(lang.get("tictactoe.game.turn", lang.get("tictactoe.game.waitingfordetails")));
            return;
        }
        statusLabel.setText(lang.get("tictactoe.game.turn", currentName + " (" + currentSymbol + ")"));
    }

    private void returnToMenu() {
        int option = JOptionPane.showConfirmDialog(gameFrame, lang.get("main.exit.confirm"), lang.get("main.exit.title"), JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {

            if (("SERVER".equals(gameMode) || "TOURNAMENT".equals(gameMode)) && client != null) {
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
            gameFrame.dispose();
            menuManager.onGameFinished();
        }
    }

    private boolean isWinPossible() {
        return isWinPossibleRecursive(!turnX);
    }

    private boolean isWinPossibleRecursive(boolean xTurn) {
        if (game.isWin('X') || game.isWin('O')) return true;
        if (game.isDraw()) return false;
        char speler = xTurn ? 'X' : 'O';
        for (int i = 0; i < 9; i++) {
            if (game.isFree(i)) {
                game.doMove(i, speler);
                boolean possible = isWinPossibleRecursive(!xTurn);
                game.undoMove(i);
                if (possible) return true;
            }
        }
        return false;
    }

    private void updateTheme() {
        if (gameFrame != null) {
            gameFrame.getContentPane().setBackground(theme.getBackgroundColor());
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

            gameFrame.repaint();
        }
    }
}