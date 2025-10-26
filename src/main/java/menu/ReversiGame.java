package menu;

import reversi.Reversi;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ReversiGame {
    private final MenuManager menuManager;
    private final String gameMode;
    private final LanguageManager lang = LanguageManager.getInstance();
    private final String speler1;
    private final String speler2;
    private JFrame gameFrame;
    private JLabel statusLabel;
    private BoardPanel boardPanel;
    private Reversi game;
    private boolean gameDone = false;
    private boolean turnBlack = true; // Black always starts
    private JLabel scoreLabel;

    public ReversiGame(MenuManager menuManager, String gameMode, String speler1, String speler2) {
        this.menuManager = menuManager;
        this.gameMode = gameMode;
        this.speler1 = speler1;
        this.speler2 = speler2;
    }

    public void start() {
        game = new Reversi();
        initializeGame();
    }

    private void initializeGame() {
        gameDone = false;
        turnBlack = true;

        gameFrame = new JFrame(lang.get("reversi.game.title.pvp"));
        gameFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        gameFrame.setSize(700, 800);
        gameFrame.setMinimumSize(new Dimension(500, 600));
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setLayout(new BorderLayout());
        gameFrame.getContentPane().setBackground(new Color(247, 247, 255));

        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        statusLabel.setForeground(new Color(5, 5, 169));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        gameFrame.add(statusLabel, BorderLayout.NORTH);

        boardPanel = new BoardPanel();
        boardPanel.setBackground(new Color(247, 247, 255));
        gameFrame.add(boardPanel, BorderLayout.CENTER);

        JButton menuButton = new JButton(lang.get("tictactoe.game.menu"));
        menuButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        menuButton.setPreferredSize(new Dimension(220, 45));
        menuButton.addActionListener(e -> returnToMenu());

        JPanel southPanel = new JPanel();
        southPanel.setBackground(new Color(247, 247, 255));
        southPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));
        southPanel.add(menuButton);
        gameFrame.add(southPanel, BorderLayout.SOUTH);

        JPanel scorePanel = new JPanel();
        scorePanel.setBackground(new Color(247, 247, 255));
        scorePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        scoreLabel = new JLabel();
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        scoreLabel.setForeground(new Color(5, 5, 169));
        scorePanel.add(scoreLabel);
        gameFrame.add(scorePanel, BorderLayout.EAST);

        gameFrame.setVisible(true);

        updateStatusLabel();
    }

    private void handleButtonClick(int row, int col) {
        if (gameDone) return;
        char current = turnBlack ? 'B' : 'W';
        if (!game.isValidMove(row, col, current)) return;
        game.doMove(row, col, current);
        boardPanel.updateBoard();

        if (game.isWin('B') || game.isWin('W') || game.isDraw()) {
            gameDone = true;
            updateStatusLabel();
            boardPanel.updateBoard();
            return;
        }

        turnBlack = !turnBlack;
        updateStatusLabel();
        boardPanel.updateBoard();
    }

    

    private void updateStatusLabel() {
    // Update score first
    int black = game.count('B');
    int white = game.count('W');
    scoreLabel.setText(String.format("<html>%s (●): %d<br>%s (○): %d</html>", 
        speler1, black, speler2, white));

    // Existing status updates
    if (gameDone) {
        if (black > white) {
            statusLabel.setText(lang.get("reversi.game.win", speler1));
        } else if (white > black) {
            statusLabel.setText(lang.get("reversi.game.win", speler2));
        } else {
            statusLabel.setText(lang.get("reversi.game.draw"));
        }
    } else {
        String name = turnBlack ? speler1 : speler2;
        statusLabel.setText(lang.get("reversi.game.turn", name));
        }
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
            menuManager.onReversiGameFinished();
        }
    }

    public void close() {
        if (gameFrame != null) gameFrame.dispose();
    }

    // --- BoardPanel inner class ---
    private class BoardPanel extends JPanel {
        private final int SIZE = 8;
        private final JButton[][] buttons = new JButton[SIZE][SIZE];

        public BoardPanel() {
            setLayout(null);
            setBackground(new Color(247, 247, 255));
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    JButton btn = new JButton();
                    btn.setFocusPainted(false);
                    btn.setFont(new Font("SansSerif", Font.BOLD, 24));
                    btn.setBackground(new Color(61, 169, 166)); // Board color
                    btn.setForeground(Color.BLACK);
                    final int r = row, c = col;
                    btn.addActionListener(e -> handleButtonClick(r, c));
                    buttons[row][col] = btn;
                    add(btn);
                }
            }
            updateBoard();
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
            int cell = size / SIZE;
            int gap = Math.max(4, cell / 20);
            

            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    int x = marginX + col * cell + gap / 2;
                    int y = marginY + row * cell + gap / 2;
                    int w = cell - gap;
                    int h = cell - gap;
                    buttons[row][col].setBounds(x, y, w, h);
                    buttons[row][col].setFont(new Font("SansSerif", Font.BOLD, Math.max(16, cell / 2)));
                }
            }
        }

        public void updateBoard() {
            char current = turnBlack ? 'B' : 'W';
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    JButton btn = buttons[row][col];
                    char val = game.getSymboolOp(row, col);

                    // Only set icon for black or white discs
                    if (val == 'B') {
                        btn.setIcon(createDiscIcon(Color.BLACK));
                    } else if (val == 'W') {
                        btn.setIcon(createDiscIcon(Color.WHITE));
                    } else {
                        btn.setIcon(null); // No disc for empty cell!
                    }

                    btn.setBackground(new Color(61, 169, 166)); // Default board color
                    btn.setBorder(BorderFactory.createLineBorder(new Color(40, 120, 120), 2));
                    // Highlight legal moves
                    if (!gameDone && game.isValidMove(row, col, current)) {
                        btn.setBackground(new Color(184, 107, 214, 180)); // Highlight color
                        btn.setBorder(BorderFactory.createLineBorder(new Color(120, 60, 150), 3));
                    }
                    btn.setEnabled(true); 
                }
            }
        }

        private Icon createDiscIcon(Color color) {
            int size = 32;
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillOval(2, 2, size - 4, size - 4);
            g2.setColor(color.equals(Color.BLACK) ? Color.WHITE : Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(2, 2, size - 4, size - 4);
            g2.dispose();
            return new ImageIcon(image);
        }
    }
}