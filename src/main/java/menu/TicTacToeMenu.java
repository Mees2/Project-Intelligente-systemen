package menu;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;


/**
 * Het TicTacToe submenu waar de gebruiker de spelmode kan kiezen
 * Opties: Speler vs Speler, Speler vs AI, Server, Terug naar hoofdmenu
 */
public class TicTacToeMenu extends JFrame {
    private final MenuManager menuManager;
    private final LanguageManager lang = LanguageManager.getInstance();
    private final ThemeManager theme = ThemeManager.getInstance();

    private JLabel titleLabel;
    private JButton pvpButton;
    private JButton pvaButton;
    private JButton serverButton;
    private JButton tournamentButton;
    private JButton backButton;

    /**
     * Constructor voor het TicTacToe menu
     * @param menuManager De menumanager die de navigatie beheert
     */
    public TicTacToeMenu(MenuManager menuManager) {
        this.menuManager = menuManager;
        initializeMenu();

        theme.addThemeChangeListener(this::updateTheme);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });
    }

    /**
     * Initialiseert de TicTacToe menu interface test***
     */
    private void initializeMenu() {
        setTitle(lang.get("tictactoe.menu.title"));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(247,247,255));

        // Titel label
        titleLabel = new JLabel(lang.get("tictactoe.menu.header"), JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 25));
        titleLabel.setForeground(new Color(5,5,169));
        add(titleLabel, BorderLayout.NORTH);

        // Menu knoppen panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        buttonPanel.setOpaque(false);

        // Speler vs Speler knop
        pvpButton = createRoundedButton(lang.get("tictactoe.menu.pvp"),
        new Color(61,169,166), new Color(81,189,186), new Color(40,120,120), true);
        pvpButton.addActionListener(e -> menuManager.openNameSelection("PVP"));
        buttonPanel.add(pvpButton);
        buttonPanel.add(Box.createVerticalStrut(10));

        // Speler vs AI knop
        pvaButton = createRoundedButton(lang.get("tictactoe.menu.pva"),
        new Color(61,169,166), new Color(81,189,186), new Color(40,120,120), true);
        pvaButton.addActionListener(e -> menuManager.openNameSelection("PVA"));
        buttonPanel.add(pvaButton);
        buttonPanel.add(Box.createVerticalStrut(10));

        // Server knop
        serverButton = createRoundedButton(lang.get("tictactoe.menu.server"),
        new Color(61,169,166), new Color(81,189,186), new Color(40,120,120), true);
        serverButton.addActionListener(e -> menuManager.openNameSelection("SERVER"));
        buttonPanel.add(serverButton);
        buttonPanel.add(Box.createVerticalStrut(10));

        // Toernooi knop
        tournamentButton = createRoundedButton(lang.get("tictactoe.menu.tournament"),
        new Color(61,169,166), new Color(81,189,186), new Color(40,120,120), true);
        tournamentButton.addActionListener(e -> menuManager.openNameSelection("TOURNAMENT"));
        buttonPanel.add(tournamentButton);
        buttonPanel.add(Box.createVerticalStrut(30));

        // Terug naar hoofdmenu knop
        backButton = createRoundedButton(lang.get("tictactoe.menu.back"),
        new Color(184,107,214),new Color(204,127,234), new Color(120,60,150), true);
        backButton.addActionListener(e -> menuManager.returnToMainMenu());
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });
    }

    /**
     * Creates a custom button with rounded corners and hover effects.
     * The button's appearance is customized with:
     * - Rounded corners
     * - Custom colors for normal state, hover state, and border
     * - Scaling based on window size
     * 
     * @param text The text to display on the button
     * @param baseColor The default background color
     * @param hoverColor The background color when mouse hovers over
     * @param borderColor The color of the button's border
     * @param enabled Whether the button should be enabled
     * @return A styled JButton with the specified properties
     */
    private JButton createRoundedButton(String text, Color baseColor, Color hoverColor, Color borderColor, boolean enabled) {
        var btn = new JButton(text){
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color base = (Color) getClientProperty("baseColor");
                Color hover = (Color) getClientProperty("hoverColor");
                Color border = (Color) getClientProperty("borderColor");
                if (base == null) base = baseColor;
                if (hover == null) hover = hoverColor;
                if (border == null) border = borderColor;

                g2.setColor(getModel().isRollover() && isEnabled() ? hover : base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(border);
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
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }

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
     * Resizes all components when the window is resized.
     * Calculates the appropriate scale based on window dimensions and
     * applies it to all buttons and labels.
     */
    private void resizeComponents() {
        double scale = Math.min(getWidth() / 500.0, getHeight() / 350.0);
        scale = Math.max(0.7, Math.min(scale, 2.0));
        resizeAllButtons(this, scale);
        revalidate();
        repaint();
    }

    /**
     * Recursively resizes all buttons and labels in a container.
     * Adjusts font sizes and dimensions based on the provided scale.
     * 
     * @param container The container whose components need to be resized
     * @param scale The scaling factor to apply (between 0.7 and 2.0)
     */
    private void resizeAllButtons(Container container, double scale) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                int newFontSize = (int)(12 * scale);
                btn.setFont(btn.getFont().deriveFont(Font.PLAIN, newFontSize));

                int newWidth = (int)(200 * scale);
                int newHeight = (int)(35 * scale);
                Dimension newSize = new Dimension(newWidth, newHeight);
                btn.setPreferredSize(newSize);
                btn.setMinimumSize(newSize);
                btn.setMaximumSize(newSize);
            } else if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                int newTitleSize = (int)(25 * scale);
                newTitleSize = Math.max(18, Math.min(newTitleSize, 40));
                label.setFont(label.getFont().deriveFont(Font.BOLD, newTitleSize));
            } else if (comp instanceof Container) {
                resizeAllButtons((Container) comp, scale);
            }
        }
    }
    /**
     * Update alle UI teksten naar de huidige taal
     */
    public void updateLanguage() {
        setTitle(lang.get("tictactoe.menu.title"));
        titleLabel.setText(lang.get("tictactoe.menu.header"));
        pvpButton.setText(lang.get("tictactoe.menu.pvp"));
        pvaButton.setText(lang.get("tictactoe.menu.pva"));
        serverButton.setText(lang.get("tictactoe.menu.server"));
        tournamentButton.setText(lang.get("tictactoe.menu.tournament"));
        backButton.setText(lang.get("tictactoe.menu.back"));
    }

    /**
     * Update het thema van het menu
     */
    public void updateTheme() {
        ThemeManager theme = ThemeManager.getInstance();
        getContentPane().setBackground(theme.getBackgroundColor());

        pvpButton.putClientProperty("baseColor", theme.getButtonColor());
        pvpButton.putClientProperty("hoverColor", theme.getButtonColorHover());
        pvpButton.putClientProperty("borderColor", theme.getButtonColor().darker());

        pvaButton.putClientProperty("baseColor", theme.getButtonColor());
        pvaButton.putClientProperty("hoverColor", theme.getButtonColorHover());
        pvaButton.putClientProperty("borderColor", theme.getButtonColor().darker());

        serverButton.putClientProperty("baseColor", theme.getButtonColor());
        serverButton.putClientProperty("hoverColor", theme.getButtonColorHover());
        serverButton.putClientProperty("borderColor", theme.getButtonColor().darker());

        tournamentButton.putClientProperty("baseColor", theme.getButtonColor());
        tournamentButton.putClientProperty("hoverColor", theme.getButtonColorHover());
        tournamentButton.putClientProperty("borderColor", theme.getButtonColor().darker());

        backButton.putClientProperty("baseColor", theme.getMainButtonColor());
        backButton.putClientProperty("hoverColor", theme.getMainButtonColorHover());
        backButton.putClientProperty("borderColor", theme.getMainButtonColor().darker());




        titleLabel.setForeground(theme.getFontColor1());

        repaint();
    }


    /**
     * Toont het TicTacToe menu
     */
    public void showMenu() {
        setVisible(true);
    }

    /**
     * Verbergt het TicTacToe menu
     */
    public void hideMenu() {
        setVisible(false);
    }
}


