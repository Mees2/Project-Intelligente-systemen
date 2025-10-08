package menu;

import java.awt.*;
import javax.swing.*;

public class TicTacToeNamePvp extends JFrame {
    private final MenuManager menuManager;
    private final LanguageManager lang = LanguageManager.getInstance();

    // Store UI components as fields
    private JLabel titleLabel;
    private JLabel speler1Label;
    private JLabel speler2Label;
    private JButton startButton;
    private JButton backButton;

    public TicTacToeNamePvp(MenuManager menuManager) {
        this.menuManager = menuManager;
        initializeMenu();
    }

    private void initializeMenu() {
        setTitle(lang.get("tictactoe.name.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel(lang.get("tictactoe.name.title"), JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(10));

        add(topPanel, BorderLayout.NORTH);

        speler1Label = new JLabel(lang.get("tictactoe.name.playername1"));
        JTextField textField1 = new JTextField();
        textField1.setPreferredSize(new Dimension(250,40));
        textField1.setFont(new Font("Arial", Font.PLAIN, 14));
        
        speler2Label = new JLabel(lang.get("tictactoe.name.playername2"));
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

        startButton = new JButton(lang.get("tictactoe.name.startgame"));
        startButton.setFont(new Font("Arial", Font.PLAIN, 14));
        startButton.addActionListener(e -> {
            String speler1naam = textField1.getText().trim();
            String speler2naam = textField2.getText().trim();

            if (speler1naam.isEmpty() || speler2naam.isEmpty()) {
                JOptionPane.showMessageDialog(this, lang.get("tictactoe.name.error.emptyname"), lang.get("common.error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            this.hideMenu();
            menuManager.startTicTacToeGame("PVP", speler1naam, speler2naam);
        });

        backButton = new JButton(lang.get("tictactoe.name.back"));
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(e -> menuManager.closeNameSelectionPVP());

        buttonPanel.add(startButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    public void updateLanguage() {
        setTitle(lang.get("tictactoe.name.title"));
        titleLabel.setText(lang.get("tictactoe.name.title"));
        speler1Label.setText(lang.get("tictactoe.name.playername1"));
        speler2Label.setText(lang.get("tictactoe.name.playername2"));
        startButton.setText(lang.get("tictactoe.name.startgame"));
        backButton.setText(lang.get("tictactoe.name.back"));
    }

    public void showMenu() {
        setVisible(true);
    }

    public void hideMenu() {
        setVisible(false);
    }
}


