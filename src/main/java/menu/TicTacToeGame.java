package menu;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.security.spec.ECField;


import tictactoe.TicTacToe;
import tictactoe.MinimaxAI;
import server.ClientTicTacToe;

public class TicTacToeGame {
    private final MenuManager menuManager;
    private final String gameMode; // "PVP" of "PVA"
    private final LanguageManager lang = LanguageManager.getInstance();
    private boolean turnX = true;
    private final String speler1;
    private final String speler2;

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

    private int extractMovePosition(String serverMsg) {
        try {
            // Find MOVE: "X" pattern
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

    /**
     * Extract player name from server message
     * Example: "SVR GAME MOVE {PLAYER: "Piet5939", MOVE: "3", DETAILS: ""}" -> returns "Piet5939"
     */
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

    /**
     * Check if the move came from us (accounting for random suffix in login)
     */
    private boolean isOurMove(String movePlayerName, String ourBaseName) {
        // Check if the move player name starts with our base name
        return movePlayerName.startsWith(ourBaseName);
    }

    /**
     * Apply opponent's move to the board
     */
    private void applyOpponentMove(int pos) {
        if (gameDone || !game.isFree(pos)) return;

        char currentPlayer = turnX ? 'X' : 'O';
        game.doMove(pos, currentPlayer);
        boardPanel.getButtons()[pos].setText(String.valueOf(currentPlayer));

        if (checkEnd(currentPlayer)) return;

        turnX = !turnX;
        updateStatusLabel();
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
        }
    }

    public void start() {
        // Check om ervoor te zorgen dat offline play mogelijk is zonder server verbinding
        if (!"SERVER".equals(gameMode)) {
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

        // Start background thread to process server responses
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
                    }
                    if (serverMsg.contains("YOURTURN")) {
                        SwingUtilities.invokeLater(() -> updateStatusLabel());
                    }
                    // Inside the server listener thread in start() method, update the MOVE handler:
                    if (serverMsg.contains("MOVE") && serverMsg.contains("PLAYER:")) {
                        String playerName = extractPlayerName(serverMsg);
                        int movePos = extractMovePosition(serverMsg);


                        // Only apply move if it's from opponent (not from us)
                        String ourName = gameMode.equals("PVP") ? speler1 :
                                (spelerRol == 'X' ? speler1 : speler2);

                        if (movePos != -1 && !isOurMove(playerName, ourName)) {
                            SwingUtilities.invokeLater(() -> applyOpponentMove(movePos));
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Connection lost: " + e.getMessage());
            }
        }, "server-listener").start();

        // Wait a moment for connection to stabilize
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        // Send login
        String playerName = gameMode.equals("PVP") ? speler1 :
                (spelerRol == 'X' ? speler1 : speler2);
        client.login(playerName);

        // Wait for login confirmation
        int attempts = 0;
        while (!loggedIn && attempts < 20) {
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            attempts++;
        }

        if (!loggedIn) {
            JOptionPane.showMessageDialog(null, "Login failed", "Error", JOptionPane.ERROR_MESSAGE);
            client.shutdown();
            menuManager.onGameFinished();
            return;
        }

        // Request match
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
        gameFrame.getContentPane().setBackground(new Color(247, 247, 255));

        // Status label
        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        statusLabel.setForeground(new Color(5, 5, 169));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        gameFrame.add(statusLabel, BorderLayout.NORTH);

        // Board panel
        boardPanel = new SquareBoardPanel();
        boardPanel.setBackground(new Color(247, 247, 255));
        gameFrame.add(boardPanel, BorderLayout.CENTER);

        // Menu button
        menuButton = createRoundedButton(lang.get("tictactoe.game.menu"),
                new Color(184, 107, 214), new Color(204, 127, 234), new Color(120, 60, 150), true);
        menuButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        menuButton.setPreferredSize(new Dimension(320, 45));
        menuButton.addActionListener(e -> returnToMenu());

        JPanel southPanel = new JPanel();
        southPanel.setBackground(new Color(247, 247, 255));
        southPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));
        southPanel.add(menuButton);
        gameFrame.add(southPanel, BorderLayout.SOUTH);

        // Window resize listener for scaling
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

    // Custom JPanel for square board and scaling
    private class SquareBoardPanel extends JPanel {
        private final JButton[] buttons = new JButton[9];

        public SquareBoardPanel() {
            setLayout(null); // We'll position buttons manually
            setBackground(new Color(247, 247, 255));
            for (int i = 0; i < 9; i++) {
                JButton btn = createRoundedButton("", Color.WHITE, new Color(230, 230, 255), new Color(120, 60, 150), true);
                btn.setFocusPainted(false);
                btn.setFont(new Font("SansSerif", Font.BOLD, 40));
                final int pos = i;
                btn.addActionListener(e -> handleButtonClick(pos));
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

    private void handleButtonClick(int pos) {
        if (gameDone || !game.isFree(pos)) return;
        if (gameMode.equals("PVA") && !isPlayersTurn()) return;

        // Send move to server
        if (client != null && client.isConnected()) {
            client.sendMove(pos);
        }

        char currentPlayer = turnX ? 'X' : 'O';
        game.doMove(pos, currentPlayer);
        boardPanel.getButtons()[pos].setText(String.valueOf(currentPlayer));

        if (checkEnd(currentPlayer)) return;

        turnX = !turnX;
        updateStatusLabel();

        if (gameMode.equals("PVA") && !isPlayersTurn() && !gameDone) {
            doAiMove();
        }
    }

    private void doAiMove() {
        SwingUtilities.invokeLater(() -> {
            try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            int move = MinimaxAI.bestMove(game, aiRol, spelerRol);
            if (move != -1) {
                game.doMove(move, aiRol);
                boardPanel.getButtons()[move].setText(String.valueOf(aiRol));
            }
            if (checkEnd(aiRol)) return;
            turnX = (spelerRol == 'X');
            updateStatusLabel();
        });
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
        } else if (gameMode.equals("PVA")){
            return lang.get("tictactoe.game.title.pva");
        }
        else if(gameMode.equals("SERVER")){
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
        }
        return "";
    }

    private void updateStatusLabel() {
        if (gameDone) return;
        char currentSymbol = turnX ? 'X' : 'O';
        String currentName = getNameBySymbol(currentSymbol);
        statusLabel.setText(lang.get("tictactoe.game.turn", currentName + " (" + currentSymbol + ")"));
    }

    private void returnToMenu() {
        int option = JOptionPane.showConfirmDialog(gameFrame, lang.get("main.exit.confirm"), lang.get("main.exit.title"), JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {

            if ("SERVER".equals(gameMode) && client != null) {
                try {
                    client.quit();
                } catch (Exception e) {
                    System.err.println("Error while quitting the game: " + e.getMessage());
                    try { client.shutdown(); } catch (Exception ignored) {}
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
}
