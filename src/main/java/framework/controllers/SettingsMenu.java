package framework.controllers;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.Serial;

/** Het TicTacToeSettings submenu waar gebruikers de taal kunnen veranderen,
 *  kunnen kiezen tussen Dark en lightmode en terug kunnen gaan naar het hoofdmenu */

public class SettingsMenu extends JPanel{
    @Serial
    private static final long serialVersionUID = 1L;

    private final MenuManager menuManager;
    private final LanguageManager lang = LanguageManager.getInstance();

    // Store UI components as fields
    private JLabel titleLabel;
    private JLabel languageLabel;
    private JComboBox<String> languageComboBox;
    private final String[] languageCodes = { "nl", "en", "vn", "cn" };
    private JButton backButton;
    private JLabel darkmodeLabel;
    private JButton darkModeButton;
    private JPanel centerPanel;
    private JPanel topPanel;

    /** Constructor voor het TicTacToeSettings menu
     *  @param menuManager De menumanager die de navigatie beheert */
    public SettingsMenu(MenuManager menuManager) {
        this.menuManager = menuManager;
        // geeft de interface
        initializeMenu();
    }
    /** Initialiseert de tictactoenamepvp interface test*** */
    private void initializeMenu() {
        //setTitle(lang.get("settings.title"));
        //setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //setSize(500, 350);
        //setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setBackground(new Color(247, 247, 255));

        // titelgedeelte
        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(247, 247, 255));

        titleLabel = new JLabel(lang.get("settings.title"), JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(5, 5, 169));

        // voegt de titellabel en ruimte toe boven en onder de titel
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(5));

        // plaatst het topPanel bovenaan het venster
        add(topPanel, BorderLayout.NORTH);

        // gedeelte voor het invoeren van de namen
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        centerPanel.setBackground(new Color(247, 247, 255));
        // kleur van dit gedeelte, toppanel en centerpanel moeten beide een achtergrond kleur ingesteld hebben
        Color bodyTextColor = new Color(0x2B6F6E);
        // Language kopje
        languageLabel = new JLabel(lang.get("settings.language"));
        languageLabel.setForeground(bodyTextColor);
        languageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // de opties van de dropdown menu
        String[] languageOptions = {
            lang.get("settings.language.dutch"),
            lang.get("settings.language.english"),
            lang.get("settings.language.vietnamese"),
            lang.get("settings.language.chinese")
        };

        // combox (dropdown menu) opmaak
        languageComboBox = new JComboBox<>(languageOptions);
        languageComboBox.setMaximumSize(new Dimension(200, 30));
        languageComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        languageComboBox.setBackground(new Color(247, 247, 255));
        languageComboBox.setForeground(bodyTextColor);
        languageComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Kijkt naar de huidige taal en geselecteerd deze taal als default in de dropdown menu
        String currentLang = lang.getCurrentLanguage();
        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].equals(currentLang)) {
                languageComboBox.setSelectedIndex(i);
                break;
            }
        }
        // bij keuze dropdown menu wordt er vergeleken met de huidige taal en als dit anders is wijzigt hij naar
        // de taal van de keuze in de dropdown menu
        languageComboBox.addActionListener(e -> {
            int selectedIndex = languageComboBox.getSelectedIndex();
            String selectedCode = languageCodes[selectedIndex];
            if (!selectedCode.equals(lang.getCurrentLanguage())) {
                changeLanguage(selectedCode);
            }
        });

        // Darkmode Button
        darkmodeLabel = new JLabel(lang.get("settings.changemode"));
        darkmodeLabel.setForeground(bodyTextColor);
        darkmodeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        darkModeButton = createRoundedButton(lang.get("settings.darkmode"),
                new Color(61, 169, 166),
                new Color(81, 189, 186),
                new Color(40, 120, 120), true);

        // Updates theme according to what mode is selected
        darkModeButton.addActionListener(e -> {
            ThemeManager theme = ThemeManager.getInstance();
            theme.setDarkMode(!theme.isDarkMode());
            updateTheme();
            darkModeButton.setText(theme.isDarkMode() ?(lang.get("settings.lightmode")) : (lang.get("settings.darkmode")));

        });


        // terug knop
        backButton = createRoundedButton(lang.get("settings.back"),
                new Color(184, 107, 214), new Color(204, 127, 234), new Color(120, 60, 150), true);
        backButton.addActionListener(e -> {
            hideMenu();
            menuManager.returnToMainMenuFromSettings();
        });
        
        // ruimte tussen elementen en voegt de elementen zelf ook toe
        centerPanel.add(languageLabel);
        centerPanel.add(Box.createVerticalStrut(3));
        centerPanel.add(languageComboBox);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(darkmodeLabel);
        centerPanel.add(Box.createVerticalStrut(3));
        centerPanel.add(darkModeButton);
        centerPanel.add(Box.createVerticalStrut(60));
        centerPanel.add(backButton);

        // plaatst centerpanel in het midden
        add(centerPanel, BorderLayout.CENTER);

        // listeneer voor het automatisch herschalen bij verstergrootteveranderingen
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });
    }
    // het veranderen van de taal bij keuze dropdown menu
    private void changeLanguage(String languageCode) {
            
        
            lang.setLanguage(languageCode);
            String languageName = lang.getCurrentLanguageName();
        
            JOptionPane.showMessageDialog(this,
            lang.get("settings.language.changed", languageName),
            lang.get("settings.language.changed.title"),
            JOptionPane.INFORMATION_MESSAGE);

            updateLanguage();
            menuManager.updateLanguage();
    }

    /** Methodes om de buttons mee te creeren */
    private JButton createRoundedButton(String text, Color baseColor, Color hoverColor, Color borderColor, boolean enabled) {
        var btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color base = (Color) getClientProperty("baseColor"); // stores the previous state and updates accordingly
                Color hover = (Color) getClientProperty("hoverColor");
                Color border = (Color) getClientProperty("borderColor");
                if (base == null) base = baseColor;
                if (hover == null) hover = hoverColor;
                if (border == null) border = borderColor;


                g2.setColor(getModel().isRollover() && isEnabled() ? hover : base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(border);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            // het dynamisch schalen van de buttons
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
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        // de style van de knoppen
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
    /** past de groote van de componenten aan afhankelijk van de grote van het venster */
    private void resizeComponents() {
        double scale = Math.min(getWidth() / 500.0, getHeight() / 350.0);
        scale = Math.max(0.7, Math.min(scale, 2.0));
        resizeAllComponents(this, scale);
        revalidate();
        repaint();
    }
    /** schaalt de knoppen */
    private void resizeAllComponents(Container container, double scale) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                int newFontSize = (int)(12 * scale);
                btn.setFont(btn.getFont().deriveFont(Font.PLAIN, newFontSize));
                Dimension newSize = new Dimension((int)(200 * scale), (int)(35 * scale));
                btn.setPreferredSize(newSize);
                btn.setMinimumSize(newSize);
                btn.setMaximumSize(newSize);
            } else if (comp instanceof JLabel label) {
                if (label == titleLabel) {
                    int newTitleSize = (int)(25 * scale);
                    label.setFont(label.getFont().deriveFont(Font.BOLD, Math.max(18, Math.min(newTitleSize, 40))));
                } else {
                    int newLabelSize = (int)(18 * scale);
                    label.setFont(label.getFont().deriveFont(Font.PLAIN, Math.max(14, Math.min(newLabelSize, 30))));
                }
            } else if (comp instanceof JTextField field) {
                int newFontSize = (int)(14 * scale);
                field.setFont(field.getFont().deriveFont(Font.PLAIN, newFontSize));
                field.setMaximumSize(new Dimension(500, (int)(40 * scale)));
            } else if (comp instanceof JRadioButton radio) {
                int newFontSize = (int)(14 * scale);
                radio.setFont(radio.getFont().deriveFont(Font.PLAIN, newFontSize));
            } else if (comp instanceof Container child) {
                resizeAllComponents(child, scale);
            }
        }
    }

    /** Update alle UI teksten naar de huidige taal */
    public void updateLanguage() {
        //setTitle(lang.get("settings.title"));
        titleLabel.setText(lang.get("settings.title"));
        languageLabel.setText(lang.get("settings.language"));
        backButton.setText(lang.get("settings.back"));
        darkModeButton.setText(lang.get("settings.darkmode"));
        darkmodeLabel.setText(lang.get("settings.changemode"));

        String[] languageOptions = {
                lang.get("settings.language.dutch"),
                lang.get("settings.language.english"),
                lang.get("settings.language.vietnamese"),
                lang.get("settings.language.chinese")
        };

        // Store current selection
        int currentIndex = languageComboBox.getSelectedIndex();

        // Remove listener temporarily to avoid triggering language change
        java.awt.event.ActionListener[] listeners = languageComboBox.getActionListeners();
        for (java.awt.event.ActionListener listener : listeners) {
            languageComboBox.removeActionListener(listener);
        }

        // Update items
        languageComboBox.removeAllItems();
        for (String option : languageOptions) {
            languageComboBox.addItem(option);
        }
        languageComboBox.setSelectedIndex(currentIndex);

        // Re-add listeners
        for (java.awt.event.ActionListener listener : listeners) {
            languageComboBox.addActionListener(listener);
        }
    }

// Updates themes similarily to UpdateLang...
    private void updateTheme() {
        ThemeManager theme = ThemeManager.getInstance();
        setBackground(theme.getBackgroundColor());
        centerPanel.setBackground(theme.getBackgroundColor());
        topPanel.setBackground(theme.getBackgroundColor());

        backButton.putClientProperty("baseColor", theme.getMainButtonColor());
        backButton.putClientProperty("hoverColor", theme.getMainButtonColorHover());
        backButton.putClientProperty("borderColor", theme.getMainButtonColor().darker());

        darkModeButton.putClientProperty("baseColor", theme.getButtonColor());
        darkModeButton.putClientProperty("hoverColor", theme.getButtonColorHover());
        darkModeButton.putClientProperty("borderColor", theme.getButtonColor().darker());

        titleLabel.setForeground(theme.getFontColor1());
        languageLabel.setForeground(theme.getFontColor2());
        languageComboBox.setBackground(theme.getTextFieldColor());
        darkmodeLabel.setForeground(theme.getFontColor2());

        repaint();
    }

    /** maakt het menu zichtbaar */
    public void showMenu() {
        setVisible(true);
    }
    /** verbergt het menu */
    public void hideMenu() {
        setVisible(false);
    }
}
