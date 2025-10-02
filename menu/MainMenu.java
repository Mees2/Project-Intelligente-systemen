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
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createTitleLabel(), BorderLayout.NORTH);
        add(createButtonPanel(), BorderLayout.CENTER);
    }

    private JLabel createTitleLabel() {
        var titleLabel = new JLabel("Welkom bij de Spelcollectie", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        return titleLabel;
    }

    private JPanel createButtonPanel() {
        var buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        buttonPanel.add(createNavButton("TicTacToe", menuManager::openTicTacToeMenu));
        buttonPanel.add(createDisabledButton("Reversi (Binnenkort beschikbaar)"));
        buttonPanel.add(new JLabel());
        buttonPanel.add(createExitButton());

        return buttonPanel;
    }

    private JButton createNavButton(String text, Runnable action) {
        var btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.addActionListener(_ -> action.run());
        return btn;
    }

    private JButton createDisabledButton(String text) {
        var btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setEnabled(false);
        return btn;
    }

    private JButton createExitButton() {
        var exitButton = new JButton("Afsluiten");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exitButton.addActionListener(_ -> {
            var option = JOptionPane.showConfirmDialog(
                this,
                "Weet je zeker dat je het programma wilt afsluiten?",
                "Bevestig afsluiten",
                JOptionPane.YES_NO_OPTION
            );
            if (option == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        return exitButton;
    }

    public void showMenu() {
        setVisible(true);
    }

    public void hideMenu() {
        setVisible(false);
    }
}

//nieuwe 