package framework.gui.menu.tictactoe;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import framework.controllers.LanguageManager;
import framework.controllers.ThemeManager;
import framework.gui.AbstractRoundedButton;
import tictactoe.TicTacToe;

/**
 * UI component for TicTacToe game - handles all visual elements and rendering
 */
public class TicTacToeUI extends AbstractRoundedButton {
    private final LanguageManager lang = LanguageManager.getInstance();
    private final ThemeManager theme = ThemeManager.getInstance();

    private JLabel statusLabel;
    private JButton menuButton;
    private SquareBoardPanel boardPanel;
    private final TicTacToe game;

    // Callback for button clicks
    private ButtonClickListener buttonClickListener;
    private Runnable menuButtonListener;

    public interface ButtonClickListener {
        void onButtonClick(int position);
    }

    public TicTacToeUI(TicTacToe game) {
        this.game = game;
        theme.addThemeChangeListener(this::updateTheme);
        setPreferredSize(new Dimension(500, 600));
    }

    /**
     * Initialize all UI components
     */
    public void initializeUI(boolean enableButtons) {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getInstance().getBackgroundColor());

        // Status label
        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        statusLabel.setForeground(ThemeManager.getInstance().getFontColor1());
        statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(statusLabel, BorderLayout.NORTH);

        // Board panel
        boardPanel = new SquareBoardPanel(enableButtons);
        boardPanel.setBackground(ThemeManager.getInstance().getBackgroundColor());
        add(boardPanel, BorderLayout.CENTER);

        // Menu button
        menuButton = createRoundedButton(lang.get("tictactoe.game.menu"),
                new Color(184, 107, 214), new Color(204, 127, 234), new Color(120, 60, 150), true);
        menuButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        menuButton.setPreferredSize(new Dimension(320, 45));
        menuButton.addActionListener(e -> {
            if (menuButtonListener != null) menuButtonListener.run();
        });

        JPanel southPanel = new JPanel();
        southPanel.setBackground(ThemeManager.getInstance().getBackgroundColor());
        southPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));
        southPanel.add(menuButton);
        add(southPanel, BorderLayout.SOUTH);

        // Responsive font sizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                statusLabel.setFont(statusLabel.getFont().deriveFont((float) Math.max(18, getHeight() / 25)));
            }
        });
    }

    /**
     * Update status label text
     */
    public void updateStatusLabel(String text) {
        if (statusLabel != null) {
            statusLabel.setText(text);
        }
    }

    /**
     * Update game end status display
     */
    public void updateGameEndStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    /**
     * Enable or disable board buttons
     */
    public void updateButtonStates(boolean enable) {
        if (boardPanel == null || boardPanel.getButtons() == null) {
            System.err.println("[DEBUG] updateButtonStates called but boardPanel is null!");
            return;
        }

        System.out.println("[DEBUG] updateButtonStates called with enable=" + enable);
        for (JButton btn : boardPanel.getButtons()) {
            int index = java.util.Arrays.asList(boardPanel.getButtons()).indexOf(btn);
            boolean shouldEnable = enable && game.isFree(index) && !game.isGameOver();
            btn.setEnabled(shouldEnable);
            System.out.println("[DEBUG] Button " + index + " set to: " + shouldEnable + " (free=" + game.isFree(index) + ", gameOver=" + game.isGameOver() + ")");
        }
    }

    /**
     * Update button text at position
     */
    public void updateButtonText(int position, char symbol) {
        if (boardPanel != null && boardPanel.getButtons() != null) {
            JButton button = boardPanel.getButtons()[position];
            button.setText(String.valueOf(symbol));
            button.repaint();
        }
    }

    /**
     * Update theme colors
     */
    public void updateTheme() {
        setBackground(theme.getBackgroundColor());
        if (statusLabel != null) {
            statusLabel.setForeground(theme.getFontColor1());
        }
        if (boardPanel != null) {
            boardPanel.setBackground(theme.getBackgroundColor());
        }

        if (menuButton != null) {
            menuButton.putClientProperty("baseColor", theme.getMainButtonColor());
            menuButton.putClientProperty("hoverColor", theme.getMainButtonColorHover());
            menuButton.putClientProperty("borderColor", theme.getMainButtonColor().darker());
            menuButton.repaint();
        }

        if (boardPanel != null && boardPanel.getButtons() != null) {
            for (JButton btn : boardPanel.getButtons()) {
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
        if (boardPanel != null && boardPanel.getButtons() != null) {
            return boardPanel.getButtons()[position].isEnabled();
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
     * Set menu button listener
     */
    public void setMenuButtonListener(Runnable listener) {
        this.menuButtonListener = listener;
    }

    /**
     * Get board buttons array
     */
    public JButton[] getButtons() {
        return boardPanel != null ? boardPanel.getButtons() : null;
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

