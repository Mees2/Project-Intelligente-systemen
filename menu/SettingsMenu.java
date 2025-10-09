package menu;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

//Het instellingen-menu van de spelcollectie.

public final class SettingsMenu extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;

    private final MenuManager menuManager;
    private final LanguageManager lang = LanguageManager.getInstance();
    
    private JLabel titleLabel;
    private JLabel languageLabel;
    private JRadioButton dutchRadio;
    private JRadioButton englishRadio;
    private JRadioButton vietnameseRadio;
    private JRadioButton chineseRadio;
    private JButton backButton;

    public SettingsMenu(MenuManager menuManager) {
        this.menuManager = menuManager;
        initializeMenu();
    }

    private void initializeMenu() {
        setTitle(lang.get("settings.title"));
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(247,247,255));

        add(createTitleLabel(), BorderLayout.NORTH);
        add(createSettingsPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JLabel createTitleLabel() {
        titleLabel = new JLabel(lang.get("settings.title"), JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 25));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        titleLabel.setForeground(new Color(5,5,169));
        return titleLabel;
    }

    private JPanel createSettingsPanel() {
        var settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        settingsPanel.setBackground(new Color(247, 247, 255));

        // kleur body tekst
        Color bodyTextColor = new Color(0x2B6F6E);

        // Taal sectie
        languageLabel = new JLabel(lang.get("settings.language"));
        languageLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        languageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        var languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        languagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        languagePanel.setBackground(new Color(247, 247, 255));
        languageLabel.setForeground(bodyTextColor);

        
        // Radio buttons voor taal selectie met achtergrondopmaak
        String currentLang = lang.getCurrentLanguage();
        dutchRadio = new JRadioButton(lang.get("settings.language.dutch"), currentLang.equals("nl"));
        dutchRadio.setBackground(new Color(247, 247, 255));
        dutchRadio.setOpaque(true);
        dutchRadio.setForeground(bodyTextColor);

        englishRadio = new JRadioButton(lang.get("settings.language.english"), currentLang.equals("en"));
        englishRadio.setBackground(new Color(247, 247,255));
        englishRadio.setOpaque(true);
        englishRadio.setForeground(bodyTextColor);

        vietnameseRadio = new JRadioButton(lang.get("settings.language.vietnamese"), currentLang.equals("vn"));
        vietnameseRadio.setBackground(new Color(247, 247, 255));
        vietnameseRadio.setOpaque(true);
        vietnameseRadio.setForeground(bodyTextColor);

        chineseRadio = new JRadioButton(lang.get("settings.language.chinese"), currentLang.equals("cn"));
        chineseRadio.setBackground(new Color(247, 247, 255));
        chineseRadio.setOpaque(true);
        chineseRadio.setForeground(bodyTextColor);

        // Button group om slechts één selectie toe te staan
        var languageGroup = new ButtonGroup();
        languageGroup.add(dutchRadio);
        languageGroup.add(englishRadio);
        languageGroup.add(vietnameseRadio);
        languageGroup.add(chineseRadio);
        
        // Action listeners voor taalwijziging
        dutchRadio.addActionListener(e -> changeLanguage("nl"));
        englishRadio.addActionListener(e -> changeLanguage("en"));
        vietnameseRadio.addActionListener(e -> changeLanguage("vn"));
        chineseRadio.addActionListener(e -> changeLanguage("cn"));
        
        languagePanel.add(dutchRadio);
        languagePanel.add(englishRadio);
        languagePanel.add(vietnameseRadio);
        languagePanel.add(chineseRadio);

        
        settingsPanel.add(languageLabel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        settingsPanel.add(languagePanel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        return settingsPanel;
    }

    private JPanel createButtonPanel() {
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(247, 247, 255));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        backButton = createRoundedButton(lang.get("settings.back"),
        new Color(184, 107, 214),
        new Color(204, 127, 234),
        new Color(120, 60, 150),
        true
    );
    
    backButton.addActionListener(e -> menuManager.returnToMainMenuFromSettings());
    
    buttonPanel.add(backButton);
    return buttonPanel;
}

    /**
     * Wijzig de taal van de applicatie
     */
    private void changeLanguage(String languageCode) {
        lang.setLanguage(languageCode);
        String languageName = lang.getCurrentLanguageName();
        
        JOptionPane.showMessageDialog(this,
            lang.get("settings.language.changed", languageName),
            lang.get("settings.language.changed.title"),
            JOptionPane.INFORMATION_MESSAGE);
        
        // Update UI-elementen
        updateUITexts();
        
        // Update het hoofdmenu
        menuManager.updateLanguage();
    }
    
    private JButton createRoundedButton(String text, Color baseColor, Color hoverColor, Color borderColor, boolean enabled){
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
     * Update alle UI-teksten naar de huidige taal
     */
    private void updateUITexts() {
        setTitle(lang.get("settings.title"));
        titleLabel.setText(lang.get("settings.title"));
        languageLabel.setText(lang.get("settings.language"));
        dutchRadio.setText(lang.get("settings.language.dutch"));
        englishRadio.setText(lang.get("settings.language.english"));
        vietnameseRadio.setText(lang.get("settings.language.vietnamese"));
        chineseRadio.setText(lang.get("settings.language.chinese"));
        backButton.setText(lang.get("settings.back"));
    }
    

    public void updateLanguage() {
        updateUITexts();
    }

    public void showMenu() {
        updateUITexts(); // Update teksten wanneer menu wordt getoond
        setVisible(true);
    }

    public void hideMenu() {
        setVisible(false);
    }
}
