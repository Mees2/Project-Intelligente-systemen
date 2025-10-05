package menu;

import javax.swing.*;
import java.awt.*;

/**
 * Het hoofdmenu van de spelcollectie.
 */
public final class MainMenu extends JFrame {
    private static final long serialVersionUID = 1L;

    private final MenuManager menuManager;

    public MainMenu(MenuManager menuManager) {
        this.menuManager = menuManager;
        initializeMenu();
    }

    private void initializeMenu() {
        setTitle("Spelcollectie - Hoofdmenu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 450); 
        setResizable(true);
        setLocationRelativeTo(null);

        setContentPane(new MainMenuPanel(menuManager));
    }

    public void showMenu() {
        setVisible(true);
    }

    public void hideMenu() {
        setVisible(false);
    }
}

