package menu;

import java.awt.*;
import javax.swing.*;

public class TicTacToeNamePva extends JFrame {
    private final MenuManager menuManager;
    private final LanguageManager lang = LanguageManager.getInstance();

    // Store UI components as fields
    private JLabel titleLabel;
    private JLabel speler1Label;
    private JLabel rolLabel;
    private JButton startButton;
    private JButton backButton;
    

    public TicTacToeNamePva(MenuManager menuManager) {
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
        topPanel.setBackground(new Color(247,247,255));

        add(topPanel, BorderLayout.NORTH);

        speler1Label = new JLabel(lang.get("tictactoe.name.playername"));
        JTextField textField1 = new JTextField();
        textField1.setPreferredSize(new Dimension(250, 40));
        textField1.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1, 0, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        rolLabel = new JLabel(lang.get("tictactoe.name.selectrole"));
        JRadioButton xbutton = new JRadioButton("X");
        JRadioButton obutton = new JRadioButton("O");
        
        // achtergrondkleur radio buttons
        xbutton.setBackground(new Color(247, 247, 255));
        obutton.setBackground(new Color(247, 247, 255));

        // gebruikt achtergrondkleur van radiobuttons
        xbutton.setOpaque(true);
        obutton.setOpaque(true);


        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(xbutton);
        roleGroup.add(obutton);

        buttonPanel.add(speler1Label);
        buttonPanel.add(textField1);
        buttonPanel.add(rolLabel);
        buttonPanel.add(xbutton);
        buttonPanel.add(obutton);

        startButton = new JButton(lang.get("tictactoe.name.startgame"));
        startButton.setFont(new Font("Arial", Font.PLAIN, 14));
        startButton.addActionListener(e -> {
            String spelerNaam = textField1.getText().trim();

            if (spelerNaam.isEmpty()) {
                JOptionPane.showMessageDialog(this, lang.get("tictactoe.name.error.emptyname"), lang.get("common.error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!xbutton.isSelected() && !obutton.isSelected()) {
                JOptionPane.showMessageDialog(this, lang.get("tictactoe.name.error.emptyrole"), lang.get("common.error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            this.hideMenu();

            if (xbutton.isSelected()) {
                menuManager.startTicTacToeGame("PVA", spelerNaam, "AI");
            } else {
                menuManager.startTicTacToeGame("PVA", "AI", spelerNaam);
            }
        });

        backButton = new JButton(lang.get("tictactoe.name.back"));
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(e -> menuManager.closeNameSelectionPVA());

        buttonPanel.add(startButton);
        buttonPanel.add(backButton);
        buttonPanel.setBackground(new Color(247,247,255));

        add(buttonPanel, BorderLayout.CENTER);
    }
    
    public void updateLanguage() {
        setTitle(lang.get("tictactoe.name.title"));
        titleLabel.setText(lang.get("tictactoe.name.title"));
        speler1Label.setText(lang.get("tictactoe.name.playername"));
        rolLabel.setText(lang.get("tictactoe.name.selectrole"));
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
