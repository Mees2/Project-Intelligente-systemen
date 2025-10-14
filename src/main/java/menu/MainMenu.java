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
    private MainMenuPanel mainMenuPanel; // Add reference to the panel


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

        mainMenuPanel = new MainMenuPanel(menuManager);
        setContentPane(mainMenuPanel);
    }

    /**
     * Update alle UI teksten naar de huidige taal
     */
    public void updateLanguage() {
        setTitle(lang.get("main.title"));
        if (mainMenuPanel != null) {
            mainMenuPanel.updateLanguage(); // Update the panel content
        }
    }

    public void showMenu() {
        setVisible(true);
    }

    public void hideMenu() {
        setVisible(false);
    }
}
