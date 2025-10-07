package menu;

import javax.swing.*;
import java.awt.*;

/**
 * Het TicTacToe submenu waar de gebruiker de spelmode kan kiezen
 * Opties: Speler vs Speler, Speler vs AI, Server, Terug naar hoofdmenu
 */
public class TicTacToeMenu extends JFrame {
    private final MenuManager menuManager;
    private final LanguageManager lang = LanguageManager.getInstance();
    
    private JLabel titleLabel;
    private JButton pvpButton;
    private JButton pvaButton;
    private JButton serverButton;
    private JButton backButton;

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
        setTitle(lang.get("tictactoe.menu.title"));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Titel label
        titleLabel = new JLabel(lang.get("tictactoe.menu.header"), JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // Menu knoppen panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Speler vs Speler knop
        pvpButton = new JButton(lang.get("tictactoe.menu.pvp"));
        pvpButton.setFont(new Font("Arial", Font.PLAIN, 14));
        pvpButton.addActionListener(e -> menuManager.openNameSelection("PVP"));
        buttonPanel.add(pvpButton);

        // Speler vs AI knop
        pvaButton = new JButton(lang.get("tictactoe.menu.pva"));
        pvaButton.setFont(new Font("Arial", Font.PLAIN, 14));
        pvaButton.addActionListener(e -> menuManager.openNameSelection("PVA"));
        buttonPanel.add(pvaButton);

        // Server knop (voor toekomstige implementatie)
        serverButton = new JButton(lang.get("tictactoe.menu.server.soon"));
        serverButton.setFont(new Font("Arial", Font.PLAIN, 14));
        serverButton.setEnabled(false); // Uitgeschakeld tot implementatie
        buttonPanel.add(serverButton);

        // Lege ruimte
        buttonPanel.add(new JLabel(""));

        // Terug naar hoofdmenu knop
        backButton = new JButton(lang.get("tictactoe.menu.back"));
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(e -> menuManager.returnToMainMenu());
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.CENTER);
    }
    
    /**
     * Update alle UI teksten naar de huidige taal
     */
    public void updateLanguage() {
        setTitle(lang.get("tictactoe.menu.title"));
        titleLabel.setText(lang.get("tictactoe.menu.header"));
        pvpButton.setText(lang.get("tictactoe.menu.pvp"));
        pvaButton.setText(lang.get("tictactoe.menu.pva"));
        serverButton.setText(lang.get("tictactoe.menu.server.soon"));
        backButton.setText(lang.get("tictactoe.menu.back"));
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

//nieuwe 