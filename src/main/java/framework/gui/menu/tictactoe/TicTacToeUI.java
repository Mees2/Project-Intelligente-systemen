package framework.gui.menu.tictactoe;

import java.awt.*;
import javax.swing.*;
import framework.controllers.LanguageManager;
import framework.controllers.ThemeManager;
import framework.gui.AbstractGameUI;
import tictactoe.TicTacToe;

/**
 * UI component for TicTacToe game - handles all visual elements and rendering
 */
public class TicTacToeUI extends AbstractGameUI {
    private SquareBoardPanel squareBoardPanel;
    private final TicTacToe game;

    // Callback for button clicks
    private ButtonClickListener buttonClickListener;

    public interface ButtonClickListener {
        void onButtonClick(int position);
    }

    public TicTacToeUI(TicTacToe game) {
        this.game = game;
        setPreferredSize(new Dimension(500, 600));
    }

    /**
     * Initialize all UI components
     */
    public void initializeUI(boolean enableButtons) {
        // Initialize common components
        initializeCommonUI("tictactoe.game.menu", new Dimension(320, 45));

        // Board panel
        squareBoardPanel = new SquareBoardPanel(enableButtons);
        squareBoardPanel.setBackground(ThemeManager.getInstance().getBackgroundColor());
        boardPanel = squareBoardPanel;
        add(squareBoardPanel, BorderLayout.CENTER);

        // Setup responsive font sizing (ratio: 25)
        setupResponsiveFontSizing(25);
    }

    @Override
    protected JPanel createSouthPanel() {
        JPanel southPanel = new JPanel();
        southPanel.setBackground(ThemeManager.getInstance().getBackgroundColor());
        southPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));
        return southPanel;
    }

    /**
     * Update game end status display
     */
    public void updateGameEndStatus(String message) {
        updateStatusLabel(message);
    }

    /**
     * Enable or disable board buttons
     */
    public void updateButtonStates(boolean enable) {
        if (squareBoardPanel == null || squareBoardPanel.getButtons() == null) {
            System.err.println("[DEBUG] updateButtonStates called but boardPanel is null!");
            return;
        }

        System.out.println("[DEBUG] updateButtonStates called with enable=" + enable);
        for (JButton btn : squareBoardPanel.getButtons()) {
            int index = java.util.Arrays.asList(squareBoardPanel.getButtons()).indexOf(btn);
            boolean shouldEnable = enable && game.isFree(index) && !game.isGameOver();
            btn.setEnabled(shouldEnable);
            System.out.println("[DEBUG] Button " + index + " set to: " + shouldEnable + " (free=" + game.isFree(index) + ", gameOver=" + game.isGameOver() + ")");
        }
    }

    /**
     * Update button text at position
     */
    public void updateButtonText(int position, char symbol) {
        if (squareBoardPanel != null && squareBoardPanel.getButtons() != null) {
            JButton button = squareBoardPanel.getButtons()[position];
            button.setText(String.valueOf(symbol));
            button.repaint();
        }
    }

    /**
     * Update theme colors
     */
    @Override
    public void updateTheme() {
        updateCommonTheme();

        if (squareBoardPanel != null && squareBoardPanel.getButtons() != null) {
            for (JButton btn : squareBoardPanel.getButtons()) {
                btn.putClientProperty("baseColor", theme.getTitleColor());
                btn.putClientProperty("hoverColor", theme.getTitleColor());
                btn.putClientProperty("borderColor", new Color(120, 60, 150));
                btn.repaint();
            }
        }

        repaint();
    }

    /**
     * Check if a button is enabled
     */
    public boolean isButtonEnabled(int position) {
        if (squareBoardPanel != null && squareBoardPanel.getButtons() != null) {
            return squareBoardPanel.getButtons()[position].isEnabled();
        }
        return false;
    }

    /**
     * Set button click listener
     */
    public void setButtonClickListener(ButtonClickListener listener) {
        this.buttonClickListener = listener;
    }

    /**
     * Get board buttons array
     */
    public JButton[] getButtons() {
        return squareBoardPanel != null ? squareBoardPanel.getButtons() : null;
    }

    /**
     * Inner class for the game board panel
     */
    private class SquareBoardPanel extends JPanel {
        private final JButton[] buttons;

        public SquareBoardPanel(boolean enableButtons) {
            buttons = new JButton[game.getBoardSize()];
            setLayout(null);
            setBackground(ThemeManager.getInstance().getBackgroundColor());

            for (int i = 0; i < game.getBoardSize(); i++) {
                JButton btn = createTicTacToeButton("", ThemeManager.getInstance().getTitleColor(),
                        ThemeManager.getInstance().getFontColor1(), new Color(120, 60, 150), true);
                btn.setFocusPainted(false);
                btn.setFont(new Font("SansSerif", Font.BOLD, 40));
                final int pos = i;

                if (enableButtons) {
                    btn.addActionListener(e -> {
                        if (buttonClickListener != null) {
                            buttonClickListener.onButtonClick(pos);
                        }
                    });
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
}
