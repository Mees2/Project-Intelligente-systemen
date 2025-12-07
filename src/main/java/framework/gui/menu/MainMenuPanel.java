package framework.gui.menu;

import framework.controllers.LanguageManager;
import framework.controllers.MenuManager;
import framework.controllers.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Frontend UI panel voor het hoofdmenu
 */
public final class MainMenuPanel extends JPanel {

    // Classes declaration for instance variables and referencing
    private final MenuManager menuManager;
    private final LanguageManager lang = LanguageManager.getInstance();
    private final ThemeManager theme = ThemeManager.getInstance();

    // Base dimensions and font sizes for scaling
    private static final int BASE_WIDTH = 700;
    private static final int BASE_HEIGHT = 450;
    private static final int BASE_TITLE_FONT = 50;
    private static final int BASE_BUTTON_FONT = 14;
    private static final int BASE_BUTTON_W_LARGE = 250;
    private static final int BASE_BUTTON_W_SMALL = 150;
    private static final int BASE_BUTTON_H = 35;

    //Components used in the panel
    private JLabel titleLabel;
    private JButton tttButton;
    private JButton reversiButton;
    private JButton exitButton;
    private JButton settingsButton;

    //Constructor for MainMenuPanel
    public MainMenuPanel(MenuManager menuManager) {
        this.menuManager = menuManager; // Assigns parameter
        initializeUI(); // Calls method to setp UI

        theme.addThemeChangeListener(this::updateTheme); // registers for theme notifs

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        }); //adds listener for window resizing
    }

    // Sets up the UI layout and components
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(247, 247, 255));

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createButtonPanel(), BorderLayout.EAST);
        add(createLeftPanel(), BorderLayout.WEST);
    }

    // Creates title panel and north border layout in window
    private JPanel createTitlePanel() {
        var titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false); // Make panel transparent

        titleLabel = createTitleLabel();
        titlePanel.add(titleLabel, BorderLayout.WEST);

        return titlePanel;
    }

    // Creates button panel and east border layout in window
    private JPanel createButtonPanel() {
        var buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);

        // Container for buttons on the right side
        var buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.setOpaque(false);
        buttonContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 50));

        // TicTacToe button
        tttButton = createRoundedButton(lang.get("main.tictactoe"), menuManager::openTicTacToeMenu,
                new Color(61, 169, 166), new Color(81, 189, 186), new Color(40, 120, 120), true, BASE_BUTTON_W_LARGE);
        buttonContainer.add(tttButton); // Add TicTacToe button
        buttonContainer.add(Box.createVerticalStrut(10)); // Spacing between buttons

        // Reversi button
        reversiButton = createRoundedButton(lang.get("main.reversi"), menuManager::openReversiMenu,
                new Color(61, 169, 166), new Color(81, 189, 186), new Color(40, 120, 120), true, BASE_BUTTON_W_LARGE);
        buttonContainer.add(reversiButton);
        buttonContainer.add(Box.createVerticalGlue()); // Push buttons to the top
        buttonPanel.add(buttonContainer, BorderLayout.EAST);
        return buttonPanel;
    }

    // Creates left panel and west border layout in window
    private JPanel createLeftPanel() {
        var leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 50, 0));

        // Create a container for both buttons
        var buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.setOpaque(false);

        // Settings button
        settingsButton = createRoundedButton(lang.get("main.settings"), menuManager::openSettingsMenu,
                new Color(184, 107, 214), new Color(204, 127, 234), new Color(120, 60, 150), true, BASE_BUTTON_W_SMALL);
        buttonContainer.add(settingsButton);
        buttonContainer.add(Box.createVerticalStrut(10));

        // Exit button
        exitButton = createRoundedButton(lang.get("main.exit"), menuManager::confirmExit,
                new Color(184, 107, 214), new Color(204, 127, 234), new Color(120, 60, 150), true, BASE_BUTTON_W_SMALL);
        buttonContainer.add(exitButton);

        leftPanel.add(buttonContainer, BorderLayout.SOUTH);
        return leftPanel;
    }

    // Creates title label with styling
    private JLabel createTitleLabel() {
        var titleLabel = new JLabel(lang.get("main.welcome"), JLabel.LEFT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, BASE_TITLE_FONT));
        titleLabel.setForeground(new Color(5, 5, 169));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 50, 25, 10));
        return titleLabel;
    }

    /**
     * Creates rounded buttons with custom colors and actions
     */
    private JButton createRoundedButton(String text, Runnable action,
                                        Color baseColor, Color hoverColor, Color borderColor,
                                        boolean enabled, int width) {
        var btn = new JButton(text) {
            @Override
            //Custom painting for rounded button appearance
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // For smooth edges


                Color base = (Color) getClientProperty("baseColor"); //Stores base color from theme
                Color hover = (Color) getClientProperty("hoverColor");
                Color border = (Color) getClientProperty("borderColor");
                if (base == null) base = baseColor;
                if (hover == null) hover = hoverColor;
                if (border == null) border = borderColor;

                g2.setColor(getModel().isRollover() && isEnabled() ? hover : base); //sets hover effect
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Draws rounded rectangle
                g2.setColor(border);
                g2.setStroke(new BasicStroke(2)); //sets border thinkness
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("SansSerif", Font.PLAIN, BASE_BUTTON_FONT)); //Sets button font
        btn.setForeground(enabled ? Color.WHITE : new Color(100, 100, 100)); // Sets font color
        btn.setContentAreaFilled(false); // Makes button background transparent
        btn.setOpaque(false); // Ensures transparency
        btn.setFocusPainted(false); // Removes focus border
        btn.setBorderPainted(false); // Removes default border
        btn.setRolloverEnabled(true); // Enables hover effect
        btn.setEnabled(enabled); // Sets button enabled state

        btn.putClientProperty("baseWidth", width); // Store base width for resizing

        var size = new Dimension(width, BASE_BUTTON_H); // Sets button size
        btn.setPreferredSize(size);

        if (enabled) btn.addActionListener(e -> action.run()); // Adds action listener if enabled
        return btn;
    }

// exit confirmation dialog
    private void confirmExit() {
        var option = JOptionPane.showConfirmDialog(
                this,
                "Weet je zeker dat je het programma wilt afsluiten?",
                "Bevestig afsluiten",
                JOptionPane.YES_NO_OPTION
        );
        if (option == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * Automatische resizing window components bij venster grootte wijziging
     */
    private void resizeComponents() {
        int w = getWidth(); //gets current width
        int h = getHeight(); //gets current height

        if (w == 0 || h == 0) return; // Avoid division by zero

        double scale = Math.min(w / (double) BASE_WIDTH, h / (double) BASE_HEIGHT); // Calculate scale factor
        scale = Math.max(0.7, Math.min(scale, 2.0)); // Increased max scale from 1.5 to 2.0 for fullscreen

        if (titleLabel != null) {
            int newTitleSize = (int) Math.round(BASE_TITLE_FONT * scale); // Scale title font size
            newTitleSize = Math.max(30, Math.min(newTitleSize, 100)); // Add min/max limits for title
            titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, newTitleSize)); // Apply new font size
        }

        //Resize buttons
        resizeButton(tttButton, scale);
        resizeButton(reversiButton, scale);
        resizeButton(settingsButton, scale);
        resizeButton(exitButton, scale);

        // Revalidate and repaint to apply changes
        revalidate();
        repaint();
    }

    /**
     * Automatische resizing van knoppen bij venster grootte wijziging
     */
    private void resizeButton(JButton btn, double scale) {
        if (btn == null) return;

        Integer baseWidth = (Integer) btn.getClientProperty("baseWidth"); // Retrieve stored base width
        if (baseWidth == null) baseWidth = BASE_BUTTON_W_LARGE; // Default if not set

        int newWidth = (int) Math.round(baseWidth * scale);
        int newHeight = (int) Math.round(BASE_BUTTON_H * scale);
        int newFontSize = (int) Math.round(BASE_BUTTON_FONT * scale);

        // Apply min/max limits to button dimensions and font size
        newWidth = Math.max(120, Math.min(newWidth, baseWidth + 100));
        newHeight = Math.max(28, Math.min(newHeight, 60));
        newFontSize = Math.max(12, Math.min(newFontSize, 22));

        // Update button font and size
        btn.setFont(btn.getFont().deriveFont(Font.PLAIN, newFontSize));
        Dimension newSize = new Dimension(newWidth, newHeight);
        btn.setPreferredSize(newSize);
        btn.setMinimumSize(newSize);
        btn.setMaximumSize(newSize);
    }
 // Update alle UI teksten naar de huidige taal
    public void updateLanguage() {
        if (titleLabel == null) return;
        titleLabel.setText(lang.get("main.welcome"));
        tttButton.setText(lang.get("main.tictactoe"));
        reversiButton.setText(lang.get("main.reversi"));
        settingsButton.setText(lang.get("main.settings"));
        exitButton.setText(lang.get("main.exit"));
    }
//Updates the theme colors for all components
    public void updateTheme() {
        ThemeManager theme = ThemeManager.getInstance(); // Get current theme instance
        setBackground(theme.getBackgroundColor()); // Update panel background color

        //Gets new theme button colors and stores them in client properties
        tttButton.putClientProperty("baseColor", theme.getButtonColor());
        tttButton.putClientProperty("hoverColor", theme.getButtonColorHover());
        tttButton.putClientProperty("borderColor", theme.getButtonColor().darker());

        reversiButton.putClientProperty("baseColor", theme.getButtonColor());
        reversiButton.putClientProperty("hoverColor", theme.getButtonColorHover());
        reversiButton.putClientProperty("borderColor", theme.getButtonColor().darker());

        settingsButton.putClientProperty("baseColor", theme.getMainButtonColor());
        settingsButton.putClientProperty("hoverColor", theme.getMainButtonColorHover());
        settingsButton.putClientProperty("borderColor", theme.getMainButtonColor().darker());

        exitButton.putClientProperty("baseColor", theme.getMainButtonColor());
        exitButton.putClientProperty("hoverColor", theme.getMainButtonColorHover());
        exitButton.putClientProperty("borderColor", theme.getMainButtonColor().darker());

        // Update title font colors
        titleLabel.setForeground(theme.getFontColor1());

        repaint();
    }


}
