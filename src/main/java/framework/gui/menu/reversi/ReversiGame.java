package framework.gui.menu.reversi;

import framework.bordspel.Positie;  
import framework.controllers.LanguageManager;
import framework.controllers.MenuManager;
import reversi.Reversi;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Handles the graphical user interface and game logic for a Reversi game.
 * Supports both Player vs Player (PVP) and Player vs AI (PVA) modes.
 * Features include board display, move validation, score tracking, and turn management.
 */
public class ReversiGame {
    private final MenuManager menuManager;
    private final String gameMode; // "PVP" or "PVA"
    private final LanguageManager lang = LanguageManager.getInstance();
    private final String speler1; // Human player (always black)
    private final String speler2; // Opponent (AI or human player)
    
    private JFrame gameFrame;
    private JLabel statusLabel;
    private JLabel scoreLabel;
    private BoardPanel boardPanel;
    private Reversi game;
    private boolean gameDone = false;
    private boolean turnBlack = true; // Black always starts
    
    private ReversiAi ai; // AI player (only used in PVA mode)
    private boolean consecutivePass = false; // Track if last turn was a pass

    /**
     * Creates a new Reversi game instance.
     *
     * @param menuManager The menu manager for handling navigation
     * @param gameMode The game mode ("PVP" or "PVA")
     * @param speler1 Name of the human player (black pieces)
     * @param speler2 Name of the opponent (AI or human player)
     */
    public ReversiGame(MenuManager menuManager, String gameMode, String speler1, String speler2) {
        this.menuManager = menuManager;
        this.gameMode = gameMode;
        this.speler1 = speler1;
        this.speler2 = speler2;
        if ("PVA".equals(gameMode)) {
            this.ai = new ReversiAi();
        }
    }

    /**
     * Initializes and starts a new game.
     * Creates the game logic instance and sets up the UI.
     */
    public void start() {
        game = new Reversi();
        initializeGame();
    }

    /**
     * Initializes the game window and all UI components.
     * Sets up the board, status displays, score tracking, and control buttons.
     */
    private void initializeGame() {
        gameDone = false;
        turnBlack = true;
        consecutivePass = false;

        String title = "PVA".equals(gameMode) ? 
            lang.get("reversi.game.title.pva") : lang.get("reversi.game.title.pvp");
        
        gameFrame = new JFrame(title);
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

        // Add score panel
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

        JButton menuButton = new JButton(lang.get("tictactoe.game.menu"));
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
        
        // If playing against AI and it's AI's turn (which shouldn't happen as black starts),
        // make the AI move
        if ("PVA".equals(gameMode) && !turnBlack) {
            makeAIMove();
        }
    }

    /**
     * Handles a player's move when they click a cell on the board.
     * Validates the move, updates the game state, and triggers AI move if applicable.
     *
     * @param row The row of the clicked cell
     * @param col The column of the clicked cell
     */
    private void handleButtonClick(int row, int col) {
        if (gameDone || !turnBlack) return; // Only allow moves during player's turn
        
        char current = 'B'; // Player is always black
        
        if (!game.isValidMove(row, col, current)) return;
        
        game.doMove(row, col, current);
        boardPanel.updateBoard();
        consecutivePass = false;

        if (checkGameEnd()) return;

        turnBlack = false;
        updateStatusLabel();

        // If PVA mode, make AI move after a short delay
        if ("PVA".equals(gameMode)) {
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(500); // Delay for better UX
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                makeAIMove();
            });
        }
    }

    /**
     * Makes the AI's move in PVA mode.
     * If the AI has valid moves, it chooses the best one.
     * If no valid moves, it passes and the turn goes back to the player.
     */
    private void makeAIMove() {
        if (gameDone) return;

        char aiPlayer = 'W'; // AI is always white

        // Check if AI has valid moves
        if (!game.hasValidMove(aiPlayer)) {
            // AI must pass
            consecutivePass = true;
            
            // Check if player can move
            if (!game.hasValidMove('B')) {
                // Both players can't move - game ends
                gameDone = true;
                updateStatusLabel();
                boardPanel.updateBoard();
                return;
            }
            
            // Pass to player
            turnBlack = true;
            updateStatusLabel();
            return;
        }

        // AI makes its best move
        Positie bestMove = ai.findBestMove(game, aiPlayer);
        if (bestMove != null) {
            game.doMove(bestMove.getRij(), bestMove.getKolom(), aiPlayer);
            boardPanel.updateBoard();
            consecutivePass = false;

            if (checkGameEnd()) return;

            turnBlack = true;
            updateStatusLabel();
        }
    }

    /**
     * Checks if the game has ended due to both players being unable to move.
     *
     * @return true if the game has ended, false otherwise
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
     * Updates the status and score labels based on current game state.
     * Shows current player's turn or game result, and updates the score display.
     */
    private void updateStatusLabel() {
        // Update score
        int black = game.count('B');
        int white = game.count('W');
        scoreLabel.setText(String.format("<html>%s (●): %d<br>%s (●): %d</html>", 
            speler1, black, speler2, white));

        // Update status
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

    /**
     * Handles the return to menu action.
     * Shows a confirmation dialog before closing the game window.
     */
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

    /**
     * Closes the game window and cleans up resources.
     */
    public void close() {
        if (gameFrame != null) gameFrame.dispose();
    }

    /**
     * Inner class representing the game board.
     * Manages the visual representation of the Reversi board and handles cell interactions.
     */
    private class BoardPanel extends JPanel {
        private final int SIZE = 8;
        private final JButton[][] buttons = new JButton[SIZE][SIZE];

        /**
         * Creates a new board panel with an 8x8 grid of buttons.
         * Initializes all cells with appropriate styling and click handlers.
         */
        public BoardPanel() {
            setLayout(null);
            setBackground(new Color(247, 247, 255));
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    JButton btn = new JButton();
                    btn.setFocusPainted(false);
                    btn.setFont(new Font("SansSerif", Font.BOLD, 24));
                    btn.setBackground(new Color(61, 169, 166));
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

        /**
         * Calculates and sets the position and size of all board cells.
         * Ensures the board remains square and properly scaled.
         */
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

        /**
         * Updates the visual state of all board cells.
         * Shows current pieces, highlights valid moves, and updates cell styling.
         */
        public void updateBoard() {
            char current = turnBlack ? 'B' : 'W';
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    JButton btn = buttons[row][col];
                    char val = game.getSymboolOp(row, col);
                    btn.setText("");
                    btn.setBackground(new Color(61, 169, 166));
                    btn.setBorder(BorderFactory.createLineBorder(new Color(40, 120, 120), 2));
                    btn.setIcon(null);

                    if (val == 'B') {
                        btn.setIcon(createDiscIcon(Color.BLACK));
                    } else if (val == 'W') {
                        btn.setIcon(createDiscIcon(Color.WHITE));
                    } else {
                        // Highlight legal moves (only for player in PVA mode)
                        if (!gameDone && turnBlack && game.isValidMove(row, col, current)) {
                            btn.setBackground(new Color(184, 107, 214, 180));
                            btn.setBorder(BorderFactory.createLineBorder(new Color(120, 60, 150), 3));
                        }
                    }
                    btn.setEnabled(!gameDone && turnBlack && game.isValidMove(row, col, current));
                }
            }
        }

        /**
         * Creates an icon representing a game piece (disc).
         * 
         * @param color The color of the disc (black or white)
         * @return An Icon object representing the game piece
         */
        private Icon createDiscIcon(Color color) {
            int size = 32;
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(color);
            g2.fillOval(2, 2, size - 4, size - 4);

            if (color.equals(Color.WHITE)) {
                g2.setColor(Color.BLACK);
            } else {
                g2.setColor(Color.WHITE);
            }
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(2, 2, size - 4, size - 4);

            g2.dispose();
            return new ImageIcon(image);
        }
    }
}

