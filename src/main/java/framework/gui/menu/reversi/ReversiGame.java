package framework.gui.menu.reversi;

import framework.boardgame.Position;
import framework.boardgame.AbstractBoardGame;
import framework.controllers.LanguageManager;
import framework.controllers.MenuManager;
import reversi.Reversi;
import reversi.ReversiMinimax;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Handles the graphical user interface and game logic for a Reversi game.
 */
public class ReversiGame {
    private final MenuManager menuManager;
    private final String gameMode;
    private final LanguageManager lang = LanguageManager.getInstance();
    private final String player1;
    private final String player2;
    
    private JFrame gameFrame;
    private JLabel statusLabel;
    private JLabel scoreLabel;
    private BoardPanel boardPanel;
    private Reversi game;
    private boolean gameDone = false;
    private boolean turnBlack = true;
    
    private ReversiMinimax ai;
    private volatile boolean aiThinking = false;
    private char humanRole = 'B'; // Human is always black
    private char aiRole = 'W';    // AI is always white

    /**
     * Creates a new Reversi game instance.
     */
    public ReversiGame(MenuManager menuManager, String gameMode, String player1, String player2) {
        this.menuManager = menuManager;
        this.gameMode = gameMode;
        this.player1 = player1;
        this.player2 = player2;
    }

    /**
     * Initializes and starts a new game.
     */
    public void start() {
        game = new Reversi();
        ai = new ReversiMinimax();
        System.out.println("DEBUG: Game started");
        initializeGame();
    }

    /**
     * Initializes the game window and all UI components.
     */
    private void initializeGame() {
        gameDone = false;
        turnBlack = true;

        gameFrame = new JFrame("Reversi vs Minimax AI");
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

        JPanel scorePanel = new JPanel();
        scorePanel.setBackground(new Color(247, 247, 255));
        scorePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        scoreLabel = new JLabel();
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        scoreLabel.setForeground(new Color(5, 5, 169));
        scorePanel.add(scoreLabel);
        gameFrame.add(scorePanel, BorderLayout.EAST);

        boardPanel = new BoardPanel();
        boardPanel.setBackground(new Color(247, 247, 255));
        gameFrame.add(boardPanel, BorderLayout.CENTER);

        JButton menuButton = new JButton("Menu");
        menuButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        menuButton.setPreferredSize(new Dimension(220, 45));
        menuButton.addActionListener(e -> returnToMenu());

        JPanel southPanel = new JPanel();
        southPanel.setBackground(new Color(247, 247, 255));
        southPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));
        southPanel.add(menuButton);
        gameFrame.add(southPanel, BorderLayout.SOUTH);

        gameFrame.setVisible(true);
        updateStatusLabel();
    }

    /**
     * Handles a player's move when they click a cell on the board.
     */
    private void handleButtonClick(int row, int col) {
        if (gameDone || aiThinking) return;
        
        // Only allow human player to move on their turn
        if (!turnBlack) return; // Not human's turn
        
        if (!game.isValidMove(row, col, humanRole)) return;
        
        System.out.println("DEBUG: Human move at " + row + "," + col);
        game.doMove(row, col, humanRole);
        boardPanel.updateBoard();

        if (checkGameEnd()) return;

        // Check if AI has valid moves
        if (!game.hasValidMove(aiRole)) {
            System.out.println("DEBUG: AI has no valid moves, passing...");
            // Check if human can move again
            if (!game.hasValidMove(humanRole)) {
                System.out.println("DEBUG: No one can move - game over");
                gameDone = true;
                updateStatusLabel();
                boardPanel.updateBoard();
                return;
            }
            // Human gets another turn
            updateStatusLabel();
            boardPanel.updateBoard();
            return;
        }

        // AI's turn
        turnBlack = false;
        updateStatusLabel();
        boardPanel.updateBoard();
        
        makeAIMove();
    }

    /**
     * Makes the AI's move.
     */
    private void makeAIMove() {
        if (gameDone || aiThinking) return;
        
        aiThinking = true;
        updateStatusLabel();

        SwingWorker<Position, Void> worker = new SwingWorker<Position, Void>() {
            @Override
            protected Position doInBackground() {
                return ai.findBestMove(game, aiRole);
            }

            @Override
            protected void done() {
                try {
                    Position move = get();
                    aiThinking = false;
                    
                    if (move == null) {
                        System.out.println("DEBUG: AI has no valid moves, passing...");
                        // Check if human can move
                        if (!game.hasValidMove(humanRole)) {
                            System.out.println("DEBUG: No one can move - game over");
                            gameDone = true;
                            updateStatusLabel();
                            boardPanel.updateBoard();
                            return;
                        }
                        // Human gets another turn
                        turnBlack = true;
                        updateStatusLabel();
                        boardPanel.updateBoard();
                        return;
                    }
                    
                    // Make the move
                    System.out.println("DEBUG: AI move at " + move.getRow() + "," + move.getColumn());
                    game.doMove(move.getRow(), move.getColumn(), aiRole);
                    boardPanel.updateBoard();

                    if (checkGameEnd()) return;

                    // Check if human has valid moves
                    if (!game.hasValidMove(humanRole)) {
                        System.out.println("DEBUG: Human has no valid moves, passing...");
                        // Check if AI can move again
                        if (!game.hasValidMove(aiRole)) {
                            System.out.println("DEBUG: No one can move - game over");
                            gameDone = true;
                            updateStatusLabel();
                            boardPanel.updateBoard();
                            return;
                        }
                        // AI gets another turn
                        updateStatusLabel();
                        boardPanel.updateBoard();
                        makeAIMove();
                        return;
                    }

                    // Human's turn
                    turnBlack = true;
                    updateStatusLabel();
                    boardPanel.updateBoard();
                } catch (Exception e) {
                    aiThinking = false;
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    /**
     * Checks if game has ended.
     */
    private boolean checkGameEnd() {
        if (!game.hasValidMove('B') && !game.hasValidMove('W')) {
            gameDone = true;
            updateStatusLabel();
            boardPanel.updateBoard();
            return true;
        }
        return false;
    }

    /**
     * Updates status and score labels.
     */
    private void updateStatusLabel() {
        int black = game.count('B');
        int white = game.count('W');
        scoreLabel.setText(String.format("<html>%s (●): %d<br>%s (○): %d</html>", 
            player1, black, player2, white));

        if (gameDone) {
            if (black > white) {
                statusLabel.setText(player1 + " wins!");
            } else if (white > black) {
                statusLabel.setText(player2 + " wins!");
            } else {
                statusLabel.setText("Draw!");
            }
        } else if (aiThinking) {
            statusLabel.setText("AI thinking...");
        } else {
            statusLabel.setText(turnBlack ? (player1 + "'s turn") : (player2 + "'s turn"));
        }
    }

    /**
     * Handles the return to menu action.
     */
    private void returnToMenu() {
        int option = JOptionPane.showConfirmDialog(gameFrame, "Return to menu?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            gameFrame.dispose();
            menuManager.onReversiGameFinished();
        }
    }

    /**
     * Closes the game window.
     */
    public void close() {
        if (gameFrame != null) gameFrame.dispose();
    }

    /**
     * Inner class representing the game board.
     */
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
                    btn.setBackground(new Color(61, 169, 166));
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
                }
            }
        }

        public void updateBoard() {
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    JButton btn = buttons[row][col];
                    char val = game.getSymbolAt(row, col);
                    btn.setIcon(null);
                    btn.setBackground(new Color(61, 169, 166));
                    btn.setBorder(BorderFactory.createLineBorder(new Color(40, 120, 120), 2));

                    if (val == 'B') {
                        btn.setIcon(createDiscIcon(Color.BLACK));
                    } else if (val == 'W') {
                        btn.setIcon(createDiscIcon(Color.WHITE));
                    } else if (!gameDone && !aiThinking && turnBlack && game.isValidMove(row, col, humanRole)) {
                        btn.setBackground(new Color(184, 107, 214, 180));
                        btn.setBorder(BorderFactory.createLineBorder(new Color(120, 60, 150), 3));
                    }
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
            g2.setColor(color.equals(Color.WHITE) ? Color.BLACK : Color.WHITE);
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(2, 2, size - 4, size - 4);
            g2.dispose();
            return new ImageIcon(image);
        }
    }
}

