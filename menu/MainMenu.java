package menu;

import javax.swing.*;
import java.awt.*;

/**
 * Het hoofdmenu van de spelcollectie.
 */
public final class MainMenu extends JFrame {
    private static final long serialVersionUID = 1L;

    private final MenuManager menuManager;
    private final LanguageManager lang = LanguageManager.getInstance();
    
    private JLabel titleLabel;
    private JButton ticTacToeButton;
    private JButton reversiButton;
    private JButton settingsButton;
    private JButton exitButton;

    public MainMenu(MenuManager menuManager) {
        this.menuManager = menuManager;
        initializeMenu();
    }

    private void initializeMenu() {
        setTitle(lang.get("main.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createTitleLabel(), BorderLayout.NORTH);
        add(createButtonPanel(), BorderLayout.CENTER);
    }

    private JLabel createTitleLabel() {
        titleLabel = new JLabel(lang.get("main.welcome"), JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        return titleLabel;
    }

    private JPanel createButtonPanel() {
        var buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        ticTacToeButton = createNavButton(lang.get("main.tictactoe"), menuManager::openTicTacToeMenu);
        reversiButton = createDisabledButton(lang.get("main.reversi.soon"));
        settingsButton = createNavButton(lang.get("main.settings"), menuManager::openSettingsMenu);
        
        buttonPanel.add(ticTacToeButton);
        buttonPanel.add(reversiButton);
        buttonPanel.add(settingsButton);
        buttonPanel.add(new JLabel());
        buttonPanel.add(createExitButton());

        return buttonPanel;
    }

    private JButton createNavButton(String text, Runnable action) {
        var btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private JButton createDisabledButton(String text) {
        var btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setEnabled(false);
        return btn;
    }

    private JButton createExitButton() {
        exitButton = new JButton(lang.get("main.exit"));
        exitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exitButton.addActionListener(e -> {
            var option = JOptionPane.showConfirmDialog(
                this,
                lang.get("main.exit.confirm"),
                lang.get("main.exit.title"),
                JOptionPane.YES_NO_OPTION
            );
            if (option == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        return exitButton;
    }
    
    /**
     * Update alle UI teksten naar de huidige taal
     */
    public void updateLanguage() {
        setTitle(lang.get("main.title"));
        titleLabel.setText(lang.get("main.welcome"));
        ticTacToeButton.setText(lang.get("main.tictactoe"));
        reversiButton.setText(lang.get("main.reversi.soon"));
        settingsButton.setText(lang.get("main.settings"));
        exitButton.setText(lang.get("main.exit"));
    }

    public void showMenu() {
        setVisible(true);
    }

    public void hideMenu() {
        setVisible(false);
    }
}
