package menu;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import TicTacToe.TicTacToe;
import TicTacToe.MinimaxAI;

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
        } else {
            return lang.get("tictactoe.game.title.pva");
        }
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
        int option = JOptionPane.showConfirmDialog(
                gameFrame,
                lang.get("main.exit.confirm"),
                lang.get("main.exit.title"),
                JOptionPane.YES_NO_OPTION
        );
        if (option == JOptionPane.YES_OPTION) {
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

