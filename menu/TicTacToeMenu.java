package menu;

import javax.swing.*;
import java.awt.*;

/**
 * Het TicTacToe submenu waar de gebruiker de spelmode kan kiezen
 * Opties: Speler vs Speler, Speler vs AI, Server, Terug naar hoofdmenu
 */
public class TicTacToeMenu extends JFrame {
    private final MenuManager menuManager;

    /**
     * Constructor voor het TicTacToe menu
     * @param menuManager De menumanager die de navigatie beheert
     */
    public TicTacToeMenu(MenuManager menuManager) {
        this.menuManager = menuManager;
        initializeMenu();
    }

    /**
     * Initialiseert de TicTacToe menu interface test***
     */
    private void initializeMenu() {
        setTitle("TicTacToe - Spelmode Selectie");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Titel label
        JLabel titleLabel = new JLabel("TicTacToe - Kies je spelmode", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // Menu knoppen panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Speler vs Speler knop
        JButton pvpButton = new JButton("Speler vs Speler");
        pvpButton.setFont(new Font("Arial", Font.PLAIN, 14));
        pvpButton.addActionListener(_ -> menuManager.startTicTacToeGame("PVP"));
        buttonPanel.add(pvpButton);

        // Speler vs AI knop
        JButton pvaButton = new JButton("Speler vs AI");
        pvaButton.setFont(new Font("Arial", Font.PLAIN, 14));
        pvaButton.addActionListener(_ -> menuManager.startTicTacToeGame("PVA"));
        buttonPanel.add(pvaButton);

        // Server knop (voor toekomstige implementatie)
        JButton serverButton = new JButton("Server (Binnenkort beschikbaar)");
        serverButton.setFont(new Font("Arial", Font.PLAIN, 14));
        serverButton.setEnabled(false); // Uitgeschakeld tot implementatie
        buttonPanel.add(serverButton);

        // Lege ruimte
        buttonPanel.add(new JLabel(""));

        // Terug naar hoofdmenu knop
        JButton backButton = new JButton("Terug naar Hoofdmenu");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(_ -> menuManager.returnToMainMenu());
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    /**
     * Toont het TicTacToe menu
     */
    public void showMenu() {
        setVisible(true);
    }

    /**
     * Verbergt het TicTacToe menu
     */
    public void hideMenu() {
        setVisible(false);
    }
}