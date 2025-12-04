package framework.gui.menu.reversi;

import framework.controllers.LanguageManager;
import framework.controllers.MenuManager;
import reversi.Reversi;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Handles the graphical user interface and game logic for a Reversi game.
 * This class manages the game window, board display, player turns, and score tracking.
 * It provides a visual interface for playing Reversi with features like:
 * - An 8x8 game board with clickable cells
 * - Visual indicators for valid moves
 * - Score tracking for both players
 * - Turn indicators
 * - Game state management (win/draw conditions)
 */
public class ReversiGame {
    /** MenuManager instance for handling navigation between different menus */
    private final MenuManager menuManager;
    
    /** Current game mode (e.g., "PVP") */
    private final String gameMode;
    
    /** Language manager for handling multilingual support */
    private final LanguageManager lang = LanguageManager.getInstance();
    
    /** Name of the first player (black pieces) */
    private final String player1;
    
    /** Name of the second player (white pieces) */
    private final String player2;
    
    /** Main game window */
    private JFrame gameFrame;
    
    /** Label showing current game status (turn, win, draw) */
    private JLabel statusLabel;
    
    /** Panel containing the game board */
    private BoardPanel boardPanel;
    
    /** The Reversi game logic instance */
    private Reversi game;
    
    /** Indicates if the game has ended */
    private boolean gameDone = false;
    
    /** Indicates whose turn it is (true for black, false for white) */
    private boolean turnBlack = true;
    
    /** Label showing the current score */
    private JLabel scoreLabel;

    /**
     * Creates a new Reversi game instance.
     *
     * @param menuManager The menu manager for handling navigation
     * @param gameMode The game mode (e.g., "PVP")
     * @param player1 Name of the first player (black)
     * @param player2 Name of the second player (white)
     */
    public ReversiGame(MenuManager menuManager, String gameMode, String player1, String player2) {
        this.menuManager = menuManager;
        this.gameMode = gameMode;
        this.player1 = player1;
        this.player2 = player2;
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
     * Sets up the board, status displays, and control buttons.
     */
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

    /**
     * Handles a player's move when they click a cell on the board.
     * Validates the move, updates the game state, and checks for win/draw conditions.
     *
     * @param row The row of the clicked cell
     * @param col The column of the clicked cell
     */
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

    /**
     * Updates the status and score labels based on current game state.
     * Shows current player's turn or game result, and updates the score display.
     */
    private void updateStatusLabel() {
    // Update score first
    int black = game.count('B');
    int white = game.count('W');
    scoreLabel.setText(String.format("<html>%s (●): %d<br>%s (○): %d</html>", 
        player1, black, player2, white));

    // Existing status updates
    if (gameDone) {
        if (black > white) {
            statusLabel.setText(lang.get("reversi.game.win", player1));
        } else if (white > black) {
            statusLabel.setText(lang.get("reversi.game.win", player2));
        } else {
            statusLabel.setText(lang.get("reversi.game.draw"));
        }
    } else {
        String name = turnBlack ? player1 : player2;
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
        /** Size of the board (8x8) */
        private final int SIZE = 8;
        
        /** 2D array of buttons representing board cells */
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

        /**
         * Custom painting for the board panel.
         * Ensures proper layout of all board cells.
         *
         * @param g The Graphics context to paint with
         */
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
                    char val = game.getSymbolAt(row, col);

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
            g2.setColor(color.equals(Color.BLACK) ? Color.WHITE : Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(2, 2, size - 4, size - 4);
            g2.dispose();
            return new ImageIcon(image);
        }
    }
}

