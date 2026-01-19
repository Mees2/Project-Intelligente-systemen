package framework.gui.menu.reversi;

import reversi.*;
import framework.controllers.ThemeManager;
import framework.gui.AbstractGameUI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * UI component for Reversi game - handles all visual elements and rendering
 */
public class ReversiUI extends AbstractGameUI {
    private JLabel scoreLabel;
    private ReversiBoardPanel reversiBoardPanel;
    private Reversi game;  // Remove final to allow updating
    private ButtonClickListener buttonClickListener;

    public interface ButtonClickListener {
        void onButtonClick(int row, int col);
    }

    public ReversiUI(Reversi game) {
        this.game = game;
        setPreferredSize(new Dimension(700, 800));
    }

    /**
     * Initialize all UI components
     */
    public void initializeUI() {
        initializeCommonUI("reversi.name.back", new Dimension(200, 40));
        JPanel scorePanel = new JPanel();
        scorePanel.setBackground(ThemeManager.getInstance().getBackgroundColor());
        scoreLabel = new JLabel();
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        scoreLabel.setForeground(ThemeManager.getInstance().getFontColor1());
        scorePanel.add(scoreLabel);
        add(scorePanel, BorderLayout.EAST);
        reversiBoardPanel = new ReversiBoardPanel();
        reversiBoardPanel.setBackground(ThemeManager.getInstance().getBackgroundColor());
        boardPanel = reversiBoardPanel;
        add(reversiBoardPanel, BorderLayout.CENTER);


        setupResponsiveFontSizing(35);
    }

    @Override
    protected void onComponentResized(int height) {
        if (scoreLabel != null) {
            scoreLabel.setFont(scoreLabel.getFont().deriveFont((float) Math.max(14, height / 45)));
        }
    }

    /**
     * Update score label
     */
    public void updateScoreLabel(String player1Name, int player1Score, String player2Name, int player2Score) {
        if (scoreLabel != null) {
            scoreLabel.setText(String.format("<html>%s: %d<br>%s: %d</html>",
                    player1Name, player1Score, player2Name, player2Score));
        }
    }

    /**
     * Update the board display
     */
    public void updateBoard(char currentPlayerSymbol, boolean isCurrentPlayerHuman, boolean isAIThinking) {
        if (reversiBoardPanel != null) {
            reversiBoardPanel.updateBoard(currentPlayerSymbol, isCurrentPlayerHuman, isAIThinking);
        }
    }

    /**
     * Update theme colors
     */
    @Override
    public void updateTheme() {
        updateCommonTheme();

        if (scoreLabel != null) {
            scoreLabel.setForeground(theme.getFontColor1());
        }

        repaint();
    }

    /**
     * Set button click listener
     */
    public void setButtonClickListener(ButtonClickListener listener) {
        this.buttonClickListener = listener;
    }

    /**
     * Set a new game instance (used for tournament mode reset)
     */
    public void setGame(Reversi newGame) {
        this.game = newGame;
        if (reversiBoardPanel != null) {
            reversiBoardPanel.repaint();
        }
    }

    /**
     * Inner class for the game board panel
     */
    private class ReversiBoardPanel extends JPanel {
        private final int SIZE = 8;
        private final JButton[][] buttons = new JButton[SIZE][SIZE];

        public ReversiBoardPanel() {
            setLayout(null);
            setBackground(ThemeManager.getInstance().getBackgroundColor());

            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    JButton btn = new JButton();
                    btn.setFocusPainted(false);
                    btn.setBackground(new Color(61, 169, 166));
                    final int r = row, c = col;
                    btn.addActionListener(e -> {
                        if (buttonClickListener != null) {
                            buttonClickListener.onButtonClick(r, c);
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

        public void updateBoard(char currentPlayerSymbol, boolean isCurrentPlayerHuman, boolean isAIThinking) {
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
                    } else if (!isAIThinking && isCurrentPlayerHuman
                            && game.isValidMove(row, col, currentPlayerSymbol)) {
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
