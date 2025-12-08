package framework.gui;

import framework.controllers.MenuManager;
import javax.swing.*;
import java.awt.*;

public class ApplicationFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final MenuManager menuManager;

    public ApplicationFrame(MenuManager menuManager) {
        this.menuManager = menuManager;
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);

        setTitle("Game Collection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null);

        setContentPane(contentPanel);
    }

    public void addPanel(String name, JPanel panel) {
        contentPanel.add(panel, name);
    }

    public void showPanel(String name) {
        cardLayout.show(contentPanel, name);
    }
}
