package framework.gui.menu.reversi;

import framework.controllers.LanguageManager;
import framework.controllers.MenuManager;

import javax.swing.*;
import java.awt.*;

/**
 * The menu interface for the Reversi game.
 * Provides options to start different game modes and navigate back to the main menu.
 * Currently supports Player vs Player mode, with Player vs AI mode planned for future implementation.
 */
public class ReversiMenu extends JFrame {
    /** The menu manager for handling navigation between different menus */
    private final MenuManager menuManager;
    
    /** Language manager for handling multilingual support */
    private final LanguageManager lang = LanguageManager.getInstance();

    /** UI components for the menu */
    private JLabel titleLabel;
    private JButton pvpButton;
    private JButton pvaButton;
    private JButton backButton;

    /**
     * Creates a new Reversi menu with the specified menu manager.
     * Initializes and displays the menu interface.
     *
     * @param menuManager The menu manager to handle navigation
     */
    public ReversiMenu(MenuManager menuManager) {
        this.menuManager = menuManager;
        initializeMenu();
    }

    /**
     * Initializes the menu interface with all its components.
     * Sets up the layout, creates buttons, and configures the window properties.
     * The menu includes:
     * - A title header
     * - Player vs Player button (enabled)
     * - Player vs AI button (disabled, coming soon)
     * - Back button to return to main menu
     */
    private void initializeMenu() {
        setTitle(lang.get("reversi.menu.title"));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(247,247,255));

        titleLabel = new JLabel(lang.get("reversi.menu.header"), JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 25));
        titleLabel.setForeground(new Color(5,5,169));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        buttonPanel.setOpaque(false);

        pvpButton = createRoundedButton(lang.get("reversi.menu.pvp"),
            new Color(61,169,166), new Color(81,189,186), new Color(40,120,120), true);
        pvpButton.addActionListener(e -> menuManager.openReversiNamePvp());
        buttonPanel.add(pvpButton);
        buttonPanel.add(Box.createVerticalStrut(20));

        pvaButton = createRoundedButton(lang.get("reversi.menu.pva.soon"),
            new Color(200,200,200), new Color(200,200,200), new Color(150,150,150), false);
        buttonPanel.add(pvaButton);
        buttonPanel.add(Box.createVerticalStrut(40));

        backButton = createRoundedButton(lang.get("reversi.menu.back"),
            new Color(184,107,214), new Color(204,127,234), new Color(120,60,150), true);
        backButton.addActionListener(e -> {
            hideMenu();
            menuManager.returnToMainMenu();
        });
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    /**
     * Creates a custom button with rounded corners and hover effects.
     * The button's appearance changes based on its enabled state and mouse hover.
     *
     * @param text The text to display on the button
     * @param baseColor The default background color
     * @param hoverColor The background color when mouse hovers over
     * @param borderColor The color of the button's border
     * @param enabled Whether the button should be enabled
     * @return A styled JButton with the specified properties
     */
    private JButton createRoundedButton(String text, Color baseColor, Color hoverColor, 
            Color borderColor, boolean enabled){
        var btn = new JButton(text){
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() && isEnabled() ? hoverColor : baseColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override
            public Dimension getPreferredSize() {
                double scale = 1.0;
                if (getParent() != null) {
                    Window window = SwingUtilities.getWindowAncestor(this);
                    if (window != null) {
                        scale = Math.min(window.getWidth() / 500.0, window.getHeight() / 350.0);
                        scale = Math.max(0.7, Math.min(scale, 2.0));
                    }
                }
                int scaledWidth = (int)(200 * scale);
                int scaledHeight = (int)(35 * scale);
                return new Dimension(scaledWidth, scaledHeight);
            }
            @Override
            public Dimension getMinimumSize() { return getPreferredSize(); }
            @Override
            public Dimension getMaximumSize() { return getPreferredSize(); }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setForeground(enabled ? Color.WHITE : new Color(100,100,100));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setRolloverEnabled(true);
        btn.setEnabled(enabled);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    /**
     * Updates all text elements in the menu to the current language setting.
     * This includes the window title, menu header, and all button texts.
     * Called when the application language is changed.
     */
    public void updateLanguage() {
        setTitle(lang.get("reversi.menu.title"));
        titleLabel.setText(lang.get("reversi.menu.header"));
        pvpButton.setText(lang.get("reversi.menu.pvp"));
        pvaButton.setText(lang.get("reversi.menu.pva.soon"));
        backButton.setText(lang.get("reversi.menu.back"));
    }

    /**
     * Makes the menu visible to the user.
     */
    public void showMenu() { setVisible(true); }

    /**
     * Hides the menu from the user.
     */
    public void hideMenu() { setVisible(false); }
}
