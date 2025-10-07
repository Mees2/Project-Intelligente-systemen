package menu;

import java.awt.*;
import javax.swing.*;

public class TicTacToeNamePva extends JFrame {
    private final MenuManager menuManager;

    public TicTacToeNamePva(MenuManager menuManager) {
        this.menuManager = menuManager;
        initializeMenu();
    }

    private void initializeMenu() {
        setTitle("TikTakToe - Kies Naam Speler");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("TicTacToe - Kies Naam Speler", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(10));

        add(topPanel, BorderLayout.NORTH);

        JLabel speler1Label = new JLabel("Naam Speler:");
        JTextField textField1 = new JTextField();
        textField1.setPreferredSize(new Dimension(250, 40));
        textField1.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1, 0, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel rolLabel = new JLabel("Selecteer Rol:");
        JRadioButton xbutton = new JRadioButton("X");
        JRadioButton obutton = new JRadioButton("O");

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(xbutton);
        roleGroup.add(obutton);

        buttonPanel.add(speler1Label);
        buttonPanel.add(textField1);
        buttonPanel.add(rolLabel);
        buttonPanel.add(xbutton);
        buttonPanel.add(obutton);

        JButton startButton = new JButton("Start het spel");
        startButton.setFont(new Font("Arial", Font.PLAIN, 14));
        startButton.addActionListener(e -> {
            String spelerNaam = textField1.getText().trim();

            if (spelerNaam.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vul een naam in.", "Fout", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!xbutton.isSelected() && !obutton.isSelected()) {
                JOptionPane.showMessageDialog(this, "Selecteer een rol.", "Fout", JOptionPane.ERROR_MESSAGE);
                return;
            }

            this.hideMenu();

            if (xbutton.isSelected()) {
                String spelerMetSymbol = spelerNaam + " (X)";
                String aiMetSymbol = "AI (O)";
                menuManager.startTicTacToeGame("PVA", spelerMetSymbol, aiMetSymbol);
            } else { 
                String aiMetSymbol = "AI (X)";
                String spelerMetSymbol = spelerNaam + " (O)";
                menuManager.startTicTacToeGame("PVA", aiMetSymbol, spelerMetSymbol);
            }
        });

        JButton backButton = new JButton("Terug naar Spelmethode");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(e -> menuManager.closeNameSelectionPVA());

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
