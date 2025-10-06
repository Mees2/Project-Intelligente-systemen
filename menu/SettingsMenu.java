package menu;

import javax.swing.*;
import java.awt.*;

/**
 * Het instellingen menu van de spelcollectie.
 * Hier kunnen gebruikers instellingen aanpassen zoals de taal.
 */
public final class SettingsMenu extends JFrame {
    private static final long serialVersionUID = 1L;

    private final MenuManager menuManager;
    private final LanguageManager lang = LanguageManager.getInstance();
    
    private JLabel titleLabel;
    private JLabel languageLabel;
    private JRadioButton dutchRadio;
    private JRadioButton englishRadio;
    private JRadioButton vietnameseRadio;
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

        add(createTitleLabel(), BorderLayout.NORTH);
        add(createSettingsPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JLabel createTitleLabel() {
        titleLabel = new JLabel(lang.get("settings.title"), JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        return titleLabel;
    }

    private JPanel createSettingsPanel() {
        var settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        // Taal sectie
        languageLabel = new JLabel(lang.get("settings.language"));
        languageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        languageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        var languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        languagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Radio buttons voor taal selectie
        String currentLang = lang.getCurrentLanguage();
        dutchRadio = new JRadioButton(lang.get("settings.language.dutch"), currentLang.equals("nl"));
        englishRadio = new JRadioButton(lang.get("settings.language.english"), currentLang.equals("en"));
        vietnameseRadio = new JRadioButton(lang.get("settings.language.vietnamese"), currentLang.equals("vi"));
        
        // Button group om slechts één selectie toe te staan
        var languageGroup = new ButtonGroup();
        languageGroup.add(dutchRadio);
        languageGroup.add(englishRadio);
        languageGroup.add(vietnameseRadio);
        
        // Action listeners voor taalwijziging
        dutchRadio.addActionListener(e -> changeLanguage("nl"));
        englishRadio.addActionListener(e -> changeLanguage("en"));
        vietnameseRadio.addActionListener(e -> changeLanguage("vi"));
        
        languagePanel.add(dutchRadio);
        languagePanel.add(englishRadio);
        languagePanel.add(vietnameseRadio);
        
        settingsPanel.add(languageLabel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        settingsPanel.add(languagePanel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        return settingsPanel;
    }

    private JPanel createButtonPanel() {
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        backButton = new JButton(lang.get("settings.back"));
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
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
        
        // Update UI elementen
        updateUITexts();
        
        // Update het hoofdmenu
        menuManager.updateLanguage();
    }
    
    /**
     * Update alle UI teksten naar de huidige taal
     */
    private void updateUITexts() {
        setTitle(lang.get("settings.title"));
        titleLabel.setText(lang.get("settings.title"));
        languageLabel.setText(lang.get("settings.language"));
        dutchRadio.setText(lang.get("settings.language.dutch"));
        englishRadio.setText(lang.get("settings.language.english"));
        vietnameseRadio.setText(lang.get("settings.language.vietnamese"));
        backButton.setText(lang.get("settings.back"));
    }
    
    public void showMenu() {
        updateUITexts(); // Update teksten wanneer menu wordt getoond
        setVisible(true);
    }

    public void hideMenu() {
        setVisible(false);
    }
}
