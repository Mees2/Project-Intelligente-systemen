package framework.gui;

import framework.controllers.LanguageManager;
import framework.controllers.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Abstract base class for game UI components
 * Provides common functionality for TicTacToe and Reversi game UIs
 */
public abstract class AbstractGameUI extends AbstractRoundedButton {
    protected final LanguageManager lang = LanguageManager.getInstance();
    protected final ThemeManager theme = ThemeManager.getInstance();

    protected JLabel statusLabel;
    protected JButton menuButton;
    protected JPanel boardPanel;
    protected Runnable menuButtonListener;

    public AbstractGameUI() {
        theme.addThemeChangeListener(this::updateTheme);
    }

    /**
     * Initialize common UI components (status label, menu button, layout)
     */
    protected void initializeCommonUI(String menuLabelKey, Dimension menuButtonSize) {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getInstance().getBackgroundColor());

        // Status label
        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        statusLabel.setForeground(ThemeManager.getInstance().getFontColor1());
        statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(statusLabel, BorderLayout.NORTH);

        // Menu button
        menuButton = createRoundedButton(lang.get(menuLabelKey),
                new Color(184, 107, 214), new Color(204, 127, 234),
                new Color(120, 60, 150), true);
        menuButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        menuButton.setPreferredSize(menuButtonSize);
        menuButton.addActionListener(e -> {
            if (menuButtonListener != null) menuButtonListener.run();
        });

        JPanel southPanel = createSouthPanel();
        southPanel.add(menuButton);
        add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * Create the south panel that holds the menu button
     * Can be overridden for custom layout
     */
    protected JPanel createSouthPanel() {
        JPanel southPanel = new JPanel();
        southPanel.setBackground(ThemeManager.getInstance().getBackgroundColor());
        return southPanel;
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
     * Set menu button listener
     */
    public void setMenuButtonListener(Runnable listener) {
        this.menuButtonListener = listener;
    }

    /**
     * Update common theme elements
     */
    protected void updateCommonTheme() {
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
    }

    /**
     * Setup responsive font sizing
     * @param statusFontRatio The ratio for status label font (height / ratio)
     */
    protected void setupResponsiveFontSizing(int statusFontRatio) {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int height = getHeight();
                statusLabel.setFont(statusLabel.getFont().deriveFont(
                    (float) Math.max(18, height / statusFontRatio)));
                onComponentResized(height);
            }
        });
    }

    /**
     * Called when component is resized - override for additional resize handling
     */
    protected void onComponentResized(int height) {
        // Override in subclasses if needed
    }

    /**
     * Update theme colors - must be implemented by subclasses
     */
    public abstract void updateTheme();
}

