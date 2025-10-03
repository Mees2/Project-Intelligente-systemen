package menu;

import java.awt.*;
import javax.swing.*;

public class TicTacToeNamePvp extends JFrame {
    private final MenuManager menuManager;

    public TicTacToeNamePvp(MenuManager menuManager) {
        this.menuManager = menuManager;
        initializeMenu();
    }

    private void initializeMenu() {
        setTitle("TikTakToe - Kies Naam Spelers");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("TicTacToe - Kies Naam Spelers", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(10));

        add(topPanel, BorderLayout.NORTH);

        JLabel speler1Label = new JLabel("Naam Speler 1 (X):");
        JTextField textField1 = new JTextField();
        textField1.setPreferredSize(new Dimension(250,40));
        textField1.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel speler2Label = new JLabel("Naam Speler 2 (O):");
        JTextField textField2 = new JTextField();
        textField2.setPreferredSize(new Dimension(250,40));
        textField2.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        buttonPanel.add(speler1Label);
        buttonPanel.add(textField1);
        buttonPanel.add(speler2Label);
        buttonPanel.add(textField2);

        JButton startButton = new JButton("Start het spel");
        startButton.setFont(new Font("Arial", Font.PLAIN, 14));
        startButton.addActionListener(e -> {
            String speler1naam = textField1.getText().trim();
            String speler2naam = textField2.getText().trim();

            if (speler1naam.isEmpty() || speler2naam.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vul alstublieft beide namen in.", "Fout", JOptionPane.ERROR_MESSAGE);
                return;
            }

            this.hideMenu();
            menuManager.startTicTacToeGame("PVP", speler1naam, speler2naam);
        });

        JButton backButton = new JButton("Terug naar Spelmethode");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(e -> menuManager.closeNameSelectionPVP());

        buttonPanel.add(startButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    public void showMenu() {
        setVisible(true);
    }

    public void hideMenu() {
        setVisible(false);
    }
}


