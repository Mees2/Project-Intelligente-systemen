package menu;

import javax.swing.*;
import java.awt.*;

/**
 * A dialog window for entering player names in Reversi Player vs Player mode.
 * This class creates and manages a form where two players can enter their names
 * before starting a Reversi game. It includes input validation and navigation
 * back to the main Reversi menu.
 */
public class ReversiNamePvp extends JFrame {
    private final MenuManager menuManager;
    private final LanguageManager lang = LanguageManager.getInstance();

    private JLabel titleLabel;
    private JLabel speler1Label;
    private JLabel speler2Label;
    private JTextField textField1;
    private JTextField textField2;
    private JButton startButton;
    private JButton backButton;

    /**
     * Creates a new ReversiNamePvp dialog.
     * 
     * @param menuManager The MenuManager instance for handling navigation between menus
     */
    public ReversiNamePvp(MenuManager menuManager) {
        this.menuManager = menuManager;
        initializeMenu();
    }

    /**
     * Initializes and configures the menu interface.
     * Creates and layouts all UI components including:
     * - Title label
     * - Name input fields for both players
     * - Start and Back buttons
     * All components are styled according to the game's visual theme.
     */
    private void initializeMenu() {
        setTitle(lang.get("reversi.name.title"));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(247, 247, 255));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(247, 247, 255));

        titleLabel = new JLabel(lang.get("reversi.name.title"), JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(5, 5, 169));

        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(5));

        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        centerPanel.setBackground(new Color(247, 247, 255));
        Color bodyTextColor = new Color(0x2B6F6E);

        speler1Label = new JLabel(lang.get("reversi.name.playername1"));
        speler1Label.setForeground(bodyTextColor);
        speler1Label.setAlignmentX(Component.CENTER_ALIGNMENT);

        textField1 = new JTextField();
        textField1.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textField1.setMaximumSize(new Dimension(500, 40));
        textField1.setAlignmentX(Component.CENTER_ALIGNMENT);

        speler2Label = new JLabel(lang.get("reversi.name.playername2"));
        speler2Label.setForeground(bodyTextColor);
        speler2Label.setAlignmentX(Component.CENTER_ALIGNMENT);

        textField2 = new JTextField();
        textField2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textField2.setMaximumSize(new Dimension(500, 40));
        textField2.setAlignmentX(Component.CENTER_ALIGNMENT);

        startButton = createRoundedButton(lang.get("reversi.name.startgame"),
                new Color(184, 107, 214), new Color(204, 127, 234), new Color(120, 60, 150), true);
        startButton.addActionListener(e -> {
            String speler1naam = textField1.getText().trim();
            String speler2naam = textField2.getText().trim();
            if (speler1naam.isEmpty() || speler2naam.isEmpty()) {
                JOptionPane.showMessageDialog(this, lang.get("reversi.name.error.emptyname"),
                        lang.get("common.error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            hideMenu();
            menuManager.startReversiGame("PVP", speler1naam, speler2naam);
        });

        backButton = createRoundedButton(lang.get("reversi.name.back"),
                new Color(184, 107, 214), new Color(204, 127, 234), new Color(120, 60, 150), true);
        backButton.addActionListener(e -> {
            hideMenu();
            menuManager.closeReversiNameSelectionPVP();
        });

        centerPanel.add(speler1Label);
        centerPanel.add(Box.createVerticalStrut(3));
        centerPanel.add(textField1);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(speler2Label);
        centerPanel.add(Box.createVerticalStrut(3));
        centerPanel.add(textField2);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(startButton);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(backButton);

        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Creates a custom styled button with rounded corners and hover effects.
     * 
     * @param text The text to display on the button
     * @param baseColor The default background color of the button
     * @param hoverColor The background color when mouse hovers over the button
     * @param borderColor The color of the button's border
     * @param enabled Whether the button should be enabled or disabled
     * @return A JButton with custom styling
     */
    private JButton createRoundedButton(String text, Color baseColor, Color hoverColor, 
            Color borderColor, boolean enabled) {
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
                int scaledWidth = (int) (200 * scale);
                int scaledHeight = (int) (35 * scale);
                return new Dimension(scaledWidth, scaledHeight);
            }
            @Override
            public Dimension getMinimumSize() { return getPreferredSize(); }
            @Override
            public Dimension getMaximumSize() { return getPreferredSize(); }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setForeground(enabled ? Color.WHITE : new Color(100, 100, 100));
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
     * This includes:
     * - Window title
     * - Header text
     * - Player name labels
     * - Button texts
     */
    public void updateLanguage() {
        setTitle(lang.get("reversi.name.title"));
        titleLabel.setText(lang.get("reversi.name.title"));
        speler1Label.setText(lang.get("reversi.name.playername1"));
        speler2Label.setText(lang.get("reversi.name.playername2"));
        startButton.setText(lang.get("reversi.name.startgame"));
        backButton.setText(lang.get("reversi.name.back"));
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
