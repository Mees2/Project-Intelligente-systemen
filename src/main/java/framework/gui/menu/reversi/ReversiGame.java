package framework.gui.menu.reversi;

import reversi.*;
import framework.controllers.MenuManager;
import framework.controllers.LanguageManager;
import framework.boardgame.Move;
import framework.players.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Reversi Game UI - Frontend only
 * Delegate alle game logic naar ReversiGameController
 */
public class ReversiGame extends JPanel implements ReversiGameController.GameListener {
    private final MenuManager menuManager;
    private final LanguageManager lang = LanguageManager.getInstance();
    private final String player1Name;
    private final String player2Name;
    private final String gameMode;

    private JFrame gameFrame;
    private JLabel statusLabel;
    private JLabel scoreLabel;
    private BoardPanel boardPanel;
    private ReversiGameController gameController;

    public ReversiGame(MenuManager menuManager, String gameMode, String player1, String player2) {
        this.menuManager = menuManager;
        this.player1Name = player1;
        this.player2Name = player2;
        this.gameMode = gameMode;
    }

    public void start() {
        Reversi game = new Reversi();
        ReversiMinimax minimaxAI = new ReversiMinimax();
        MonteCarloTreeSearchAI mctsAI = new MonteCarloTreeSearchAI();

        AbstractPlayer p1 = new HumanPlayer(player1Name, 'B');
        AbstractPlayer p2;

        // CHECK GAME MODE CORRECTLY
        if ("PVA".equalsIgnoreCase(gameMode) || "MCTS".equalsIgnoreCase(gameMode)) {
            p2 = new AIPlayer(player2Name, 'W');
        } else {
            // Player vs Player
            p2 = new HumanPlayer(player2Name, 'W');
        }

        gameController = new ReversiGameController(game, p1, p2, minimaxAI, mctsAI);
        gameController.setUseMCTS("MCTS".equalsIgnoreCase(gameMode));
        gameController.setGameListener(this);

        initializeUI();
        gameFrame.setVisible(true);

        // REFRESH BOARD DISPLAY
        if (boardPanel != null) {
            boardPanel.updateBoard();
        }
        updateScoreLabel();

        // AI goes first if needed
        if (p2.isAI()) {
            gameController.makeAIMove();
        }
    }

    private void initializeUI() {
        gameFrame = new JFrame("Reversi - " + gameMode);
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
        scoreLabel = new JLabel();
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        scorePanel.add(scoreLabel);
        gameFrame.add(scorePanel, BorderLayout.EAST);

        boardPanel = new BoardPanel();
        boardPanel.setBackground(new Color(247, 247, 255));
        gameFrame.add(boardPanel, BorderLayout.CENTER);

        JButton menuButton = new JButton("Menu");
        menuButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        menuButton.addActionListener(e -> returnToMenu());
        JPanel southPanel = new JPanel();
        southPanel.setBackground(new Color(247, 247, 255));
        southPanel.add(menuButton);
        gameFrame.add(southPanel, BorderLayout.SOUTH);

        updateScoreLabel();
        statusLabel.setText(gameController.getCurrentPlayer().getName() + "'s turn");
    }

    @Override
    public void onMoveExecuted(Move move) {
        boardPanel.updateBoard();
        updateScoreLabel();
    }

    @Override
    public void onGameEnded(ReversiGameController.GameResult result) {
        statusLabel.setText(result.getDescription());
        boardPanel.updateBoard();
    }

    @Override
    public void onStatusChanged(String status) {
        statusLabel.setText(status);
        boardPanel.updateBoard();
    }

    @Override
    public void onAIThinking(boolean thinking) {
        if (thinking) {
            statusLabel.setText("AI thinking...");
        } else {
            statusLabel.setText(gameController.getCurrentPlayer().getName() + "'s turn");
        }
        boardPanel.updateBoard();
    }

    public void updateScoreLabel() {
        if (gameController != null) {
            int p1Score = gameController.getScore(gameController.getPlayer1().getSymbol());
            int p2Score = gameController.getScore(gameController.getPlayer2().getSymbol());
            scoreLabel.setText(String.format("<html>%s: %d<br>%s: %d</html>",
                player1Name, p1Score, player2Name, p2Score));
        }
    }

    private void returnToMenu() {
        int option = JOptionPane.showConfirmDialog(gameFrame, "Return to menu?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            gameFrame.dispose();
            menuManager.onReversiGameFinished();
        }
    }

    /**
     * Inner class: Game Board UI
     */
    private class BoardPanel extends JPanel {
        private final int SIZE = 8;
        private final JButton[][] buttons = new JButton[SIZE][SIZE];

        public BoardPanel() {
            setLayout(null);
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    JButton btn = new JButton();
                    btn.setFocusPainted(false);
                    btn.setBackground(new Color(61, 169, 166));
                    final int r = row, c = col;
                    btn.addActionListener(e -> {
                        if (!gameController.isGameDone() && !gameController.isAIThinking()) {
                            gameController.makeMove(r, c);
                        }
                    });
                    buttons[row][col] = btn;
                    add(btn);
                }
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
            int cell = size / SIZE;
            int gap = Math.max(4, cell / 20);

            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    buttons[row][col].setBounds(
                        marginX + col * cell + gap / 2,
                        marginY + row * cell + gap / 2,
                        cell - gap, cell - gap
                    );
                }
            }
        }

        public void updateBoard() {
            Reversi game = gameController.getGame();
            char currentPlayerSymbol = gameController.getCurrentPlayer().getSymbol();
            boolean isCurrentPlayerHuman = !gameController.getCurrentPlayer().isAI();
            boolean isAIThinking = gameController.isAIThinking();

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
                    } else if (!gameController.isGameDone() && !isAIThinking 
                            && isCurrentPlayerHuman
                            && game.isValidMove(row, col, currentPlayerSymbol)) {
                        // HIGHLIGHT LEGAL MOVES
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

