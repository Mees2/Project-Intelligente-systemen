package framework.gui.menu.tictactoe;

import framework.controllers.LanguageManager;
import framework.controllers.MenuManager;
import framework.controllers.ThemeManager;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TicTacToeNamePvp extends JFrame {
    private final MenuManager menuManager;
    private final LanguageManager lang = LanguageManager.getInstance();
    private final ThemeManager theme = ThemeManager.getInstance();

    // Store UI components as fields
    private JLabel titleLabel;
    private JPanel topPanel;
    private JPanel centerPanel;
    private JLabel speler1Label;
    private JLabel speler2Label;
    private JButton startButton;
    private JButton backButton;
    private JTextField textField1;
    private JTextField textField2;

    public TicTacToeNamePvp(MenuManager menuManager) {
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
     * Initialiseert de tictactoenamepvp interface test***
     */
    private void initializeMenu() {
        setTitle(lang.get("tictactoe.name.title"));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(247,247,255));
        
        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        // achtergrond
        topPanel.setBackground(new Color(247,247,255));

        // titel label
        titleLabel = new JLabel(lang.get("tictactoe.name.title"), JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(5,5,169));

        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(10));

        add(topPanel, BorderLayout.NORTH);

        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        centerPanel.setBackground(new Color(247,247,255));

        // kleur body tekst
        Color bodyTextColor = new Color(0x2B6F6E);
        // Speler 1 tekst
        speler1Label = new JLabel(lang.get("tictactoe.name.playername1"));
        speler1Label.setForeground(bodyTextColor);
        speler1Label.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Speler 1 inputveld
        textField1 = new JTextField();
        textField1.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textField1.setMaximumSize(new Dimension(500, 40));
        textField1.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Speler 2 tekst
        speler2Label = new JLabel(lang.get("tictactoe.name.playername2"));
        speler2Label.setForeground(bodyTextColor);
        speler2Label.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Speler 2 inputveld
        textField2 = new JTextField();
        textField2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textField2.setMaximumSize(new Dimension(500, 40));
        textField2.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Startbutton met check
        startButton = createRoundedButton(lang.get("tictactoe.name.startgame"),
        new Color(184,107,214),new Color(204,127,234), new Color(120,60,150), true);
        startButton.addActionListener(e -> {
            String speler1naam = textField1.getText().trim();
            String speler2naam = textField2.getText().trim();

            if (speler1naam.isEmpty() || speler2naam.isEmpty()) {
                JOptionPane.showMessageDialog(this, lang.get("tictactoe.name.error.emptyname"), lang.get("common.error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            this.hideMenu();
            menuManager.startTicTacToeGame("PVP", speler1naam, speler2naam);
        });
        // backbutton
        backButton = createRoundedButton(lang.get("tictactoe.name.back"),
        new Color(184,107,214),new Color(204,127,234), new Color(120,60,150), true);
        backButton.addActionListener(e -> menuManager.closeNameSelectionPVP());

        // Add met ruimte ertussen
        centerPanel.add(speler1Label);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(textField1);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(speler2Label);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(textField2);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(startButton);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(backButton);

        add(centerPanel, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });

    }

// Gekopieerd van tictactoemenu
private JButton createRoundedButton(String text, Color baseColor, Color hoverColor, Color borderColor, boolean enabled){
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

private void resizeComponents() {
    double scale = Math.min(getWidth() / 500.0, getHeight() / 350.0);
    scale = Math.max(0.7, Math.min(scale, 2.0));
    resizeAllButtons(this, scale);
    revalidate();
    repaint();
}

private void resizeAllButtons(Container container, double scale) {
    for (Component comp : container.getComponents()) {
        if (comp instanceof JButton) {
            JButton btn = (JButton) comp;
            int newFontSize = (int)(12 * scale);
            btn.setFont(btn.getFont().deriveFont(Font.PLAIN, newFontSize));
            Dimension newSize = new Dimension((int)(200 * scale), (int)(35 * scale));
            btn.setPreferredSize(newSize);
            btn.setMinimumSize(newSize);
            btn.setMaximumSize(newSize);
        } else if (comp instanceof JLabel) {
            JLabel label = (JLabel) comp;
                if (label == titleLabel) {
                    int newTitleSize = (int)(25 * scale);
                    newTitleSize = Math.max(18, Math.min(newTitleSize, 40));
                    label.setFont(label.getFont().deriveFont(Font.BOLD, newTitleSize));
                } else if (label == speler1Label || label == speler2Label) {
                    int newLabelSize = (int)(18 * scale);
                    newLabelSize = Math.max(14, Math.min(newLabelSize, 30));
                    label.setFont(label.getFont().deriveFont(Font.PLAIN, newLabelSize));
                }

        } else if(comp instanceof JTextField) {
            JTextField field = (JTextField) comp;
            int newFontSize = (int)(14 * scale);
            field.setFont(field.getFont().deriveFont(Font.PLAIN, newFontSize));
            field.setMaximumSize(new Dimension(500, (int)(40 * scale)));
        } else if(comp instanceof Container) {
            resizeAllButtons((Container) comp, scale);
        }
    }
}
   

    public void updateLanguage() {
        setTitle(lang.get("tictactoe.name.title"));
        titleLabel.setText(lang.get("tictactoe.name.title"));
        speler1Label.setText(lang.get("tictactoe.name.playername1"));
        speler2Label.setText(lang.get("tictactoe.name.playername2"));
        startButton.setText(lang.get("tictactoe.name.startgame"));
        backButton.setText(lang.get("tictactoe.name.back"));
    }

    public void updateTheme() {
        ThemeManager theme = ThemeManager.getInstance();
        getContentPane().setBackground(theme.getBackgroundColor());
        centerPanel.setBackground(theme.getBackgroundColor());
        topPanel.setBackground(theme.getBackgroundColor());

        startButton.putClientProperty("baseColor", theme.getMainButtonColor());
        startButton.putClientProperty("hoverColor", theme.getMainButtonColorHover());
        startButton.putClientProperty("borderColor", theme.getMainButtonColor().darker());

        backButton.putClientProperty("baseColor", theme.getMainButtonColor());
        backButton.putClientProperty("hoverColor", theme.getMainButtonColorHover());
        startButton.putClientProperty("borderColor", theme.getMainButtonColor().darker());

        titleLabel.setForeground(theme.getFontColor1());
        speler1Label.setForeground(theme.getFontColor2());
        speler2Label.setForeground(theme.getFontColor2());


        textField1.setBackground(theme.getTextFieldColor());
        textField2.setBackground(theme.getTextFieldColor());


        repaint();
    }

    public void showMenu() {
        setVisible(true);
    }

    public void hideMenu() {
        setVisible(false);
    }
}

