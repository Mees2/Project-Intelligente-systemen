package menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Het hoofdmenu van de spelcollectie
 * Hier kan de gebruiker kiezen welk spel te spelen of het programma afsluiten
 */
public class MainMenu extends JFrame {
    private MenuManager menuManager;

    /**
     * Constructor voor het hoofdmenu
     * @param menuManager De menu manager die de navigatie beheert
     */
    public MainMenu(MenuManager menuManager) {
        this.menuManager = menuManager;
        initializeMenu();
    }

    /**
     * Initialiseert het hoofdmenu interface
     */
    private void initializeMenu() {
        setTitle("Spelcollectie - Hoofdmenu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Titel label
        JLabel titleLabel = new JLabel("Welkom bij de Spelcollectie", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Menu knoppen panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // TicTacToe knop
        JButton ticTacToeButton = new JButton("TicTacToe");
        ticTacToeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        ticTacToeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuManager.openTicTacToeMenu();
            }
        });
        buttonPanel.add(ticTacToeButton);

        // Reversi knop (voor toekomstige implementatie)
        JButton reversiButton = new JButton("Reversi (Binnenkort beschikbaar)");
        reversiButton.setFont(new Font("Arial", Font.PLAIN, 14));
        reversiButton.setEnabled(false); // Uitgeschakeld tot implementatie
        buttonPanel.add(reversiButton);

        // Lege ruimte
        buttonPanel.add(new JLabel(""));

        // Afsluiten knop
        JButton exitButton = new JButton("Afsluiten");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(
                    MainMenu.this,
                    "Weet je zeker dat je het programma wilt afsluiten?",
                    "Bevestig afsluiten",
                    JOptionPane.YES_NO_OPTION
                );
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    /**
     * Toont het hoofdmenu
     */
    public void showMenu() {
        setVisible(true);
    }

    /**
     * Verbergt het hoofdmenu
     */
    public void hideMenu() {
        setVisible(false);
    }
}