package menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Frontend UI panel voor het hoofdmenu
 */
public final class MainMenuPanel extends JPanel {

    private final MenuManager menuManager;
    private final LanguageManager lang = LanguageManager.getInstance();
    private final ThemeManager theme = ThemeManager.getInstance();

    private static final int BASE_WIDTH = 700;
    private static final int BASE_HEIGHT = 450;
    private static final int BASE_TITLE_FONT = 50;
    private static final int BASE_BUTTON_FONT = 14;
    private static final int BASE_BUTTON_W_LARGE = 250;
    private static final int BASE_BUTTON_W_SMALL = 150;
    private static final int BASE_BUTTON_H = 35;

    private JLabel titleLabel;
    private JButton tttButton;
    private JButton reversiButton;
    private JButton exitButton;
    private JButton settingsButton;

    public MainMenuPanel(MenuManager menuManager) {
        this.menuManager = menuManager;
        initializeUI();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(247, 247, 255));

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createButtonPanel(), BorderLayout.EAST);
        add(createLeftPanel(), BorderLayout.WEST);
    }

    private JPanel createTitlePanel() {
        var titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        titleLabel = createTitleLabel();
        titlePanel.add(titleLabel, BorderLayout.WEST);

        return titlePanel;
    }

    private JPanel createButtonPanel() {
        var buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);

        var buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.setOpaque(false);
        buttonContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 50));


        tttButton = createRoundedButton(lang.get("main.tictactoe"), menuManager::openTicTacToeMenu,
                new Color(61, 169, 166), new Color(81, 189, 186), new Color(40, 120, 120), true, BASE_BUTTON_W_LARGE);
        buttonContainer.add(tttButton);
        buttonContainer.add(Box.createVerticalStrut(10));

        reversiButton = createRoundedButton(lang.get("main.reversi"), menuManager::openReversiMenu,
                new Color(61, 169, 166), new Color(81, 189, 186), new Color(40, 120, 120), true, BASE_BUTTON_W_LARGE);
        buttonContainer.add(reversiButton);
        buttonContainer.add(Box.createVerticalGlue());
        buttonPanel.add(buttonContainer, BorderLayout.EAST);
        return buttonPanel;
    }

    private JPanel createLeftPanel() {
        var leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 50, 0));

        // Create a container for both buttons
        var buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.setOpaque(false);

        settingsButton = createRoundedButton(lang.get("main.settings"), menuManager::openSettingsMenu,
                new Color(184, 107, 214), new Color(204, 127, 234), new Color(120, 60, 150), true, BASE_BUTTON_W_SMALL);
        buttonContainer.add(settingsButton);
        buttonContainer.add(Box.createVerticalStrut(10));

        exitButton = createRoundedButton(lang.get("main.exit"), menuManager::confirmExit,
                new Color(184, 107, 214), new Color(204, 127, 234), new Color(120, 60, 150), true, BASE_BUTTON_W_SMALL);
        buttonContainer.add(exitButton);

        leftPanel.add(buttonContainer, BorderLayout.SOUTH);
        return leftPanel;
    }

    private JLabel createTitleLabel() {
        var titleLabel = new JLabel(lang.get("main.welcome"), JLabel.LEFT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, BASE_TITLE_FONT));
        titleLabel.setForeground(new Color(5, 5, 169));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 50, 25, 10));
        return titleLabel;
    }

    /**
     * Ronde knop
     */
    private JButton createRoundedButton(String text, Runnable action,
                                        Color baseColor, Color hoverColor, Color borderColor,
                                        boolean enabled, int width) {
        var btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() && isEnabled() ? hoverColor : baseColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("SansSerif", Font.PLAIN, BASE_BUTTON_FONT));
        btn.setForeground(enabled ? Color.WHITE : new Color(100, 100, 100));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setRolloverEnabled(true);
        btn.setEnabled(enabled);

        btn.putClientProperty("baseWidth", width);

        var size = new Dimension(width, BASE_BUTTON_H);
        btn.setPreferredSize(size);

        if (enabled) btn.addActionListener(e -> action.run());
        return btn;
    }

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
     * Automatische resizing
     */
    private void resizeComponents() {
        int w = getWidth();
        int h = getHeight();

        if (w == 0 || h == 0) return;

        double scale = Math.min(w / (double) BASE_WIDTH, h / (double) BASE_HEIGHT);
        scale = Math.max(0.7, Math.min(scale, 2.0)); // Increased max scale from 1.5 to 2.0 for fullscreen

        if (titleLabel != null) {
            int newTitleSize = (int) Math.round(BASE_TITLE_FONT * scale);
            newTitleSize = Math.max(30, Math.min(newTitleSize, 100)); // Add min/max limits for title
            titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, newTitleSize));
        }

        resizeButton(tttButton, scale);
        resizeButton(reversiButton, scale);
        resizeButton(settingsButton, scale);
        resizeButton(exitButton, scale);

        revalidate();
        repaint();
    }

    private void resizeButton(JButton btn, double scale) {
        if (btn == null) return;

        Integer baseWidth = (Integer) btn.getClientProperty("baseWidth");
        if (baseWidth == null) baseWidth = BASE_BUTTON_W_LARGE;

        int newWidth = (int) Math.round(baseWidth * scale);
        int newHeight = (int) Math.round(BASE_BUTTON_H * scale);
        int newFontSize = (int) Math.round(BASE_BUTTON_FONT * scale);


        newWidth = Math.max(120, Math.min(newWidth, baseWidth + 100));
        newHeight = Math.max(28, Math.min(newHeight, 60));
        newFontSize = Math.max(12, Math.min(newFontSize, 22));

        btn.setFont(btn.getFont().deriveFont(Font.PLAIN, newFontSize));
        Dimension newSize = new Dimension(newWidth, newHeight);
        btn.setPreferredSize(newSize);
        btn.setMinimumSize(newSize);
        btn.setMaximumSize(newSize);
    }

    public void updateLanguage() {
        if (titleLabel == null) return;
        titleLabel.setText(lang.get("main.welcome"));
        tttButton.setText(lang.get("main.tictactoe"));
        reversiButton.setText(lang.get("main.reversi.soon"));
        settingsButton.setText(lang.get("main.settings"));
        exitButton.setText(lang.get("main.exit"));
    }

    public void updateColors() {
        ThemeManager theme = ThemeManager.getInstance();
        setBackground(theme.getBackgroundColor());


        tttButton.setForeground(theme.getButtonColor());
        reversiButton.setForeground(theme.getButtonColor());
        settingsButton.setForeground(theme.getMainButtonColor());
        exitButton.setForeground(theme.getMainButtonColor());
    }
}
