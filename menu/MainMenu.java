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
        setSize(700, 450);
        setResizable(true);
        setLocationRelativeTo(null);

        setContentPane(new MainMenuPanel(menuManager));
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

