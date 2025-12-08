package framework.gui;

import framework.controllers.LanguageManager;
import framework.controllers.MenuManager;
import framework.controllers.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public abstract class AbstractNameSelection extends AbstractRoundedButton {
    protected final MenuManager menuManager;
    protected final LanguageManager lang = LanguageManager.getInstance();
    protected final ThemeManager theme = ThemeManager.getInstance();

    protected JLabel titleLabel;
    protected JLabel player1Label;
    protected JLabel player2Label;
    protected JLabel roleLabel;
    protected JTextField textField1;
    protected JTextField textField2;
    protected JPanel topPanel;
    protected JPanel centerPanel;
    protected JRadioButton xButton;
    protected JRadioButton oButton;
    protected JButton startButton;
    protected JButton backButton;

    protected AbstractNameSelection(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    protected void initializeFrame() {
        setLayout(new BorderLayout());
        setBackground(new Color(247, 247, 255));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });
    }

    protected void createTopPanel() {
        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(theme.getBackgroundColor());

        titleLabel = new JLabel(lang.get("tictactoe.name.title"), JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(theme.getFontColor1());

        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(5));

        add(topPanel, BorderLayout.NORTH);
    }

    protected void createCenterPanel() {
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        centerPanel.setBackground(theme.getBackgroundColor());
    }

    protected void createPlayer1Field(String labelKey) {
        player1Label = new JLabel(lang.get(labelKey));
        player1Label.setForeground(theme.getFontColor2());
        player1Label.setAlignmentX(Component.CENTER_ALIGNMENT);

        textField1 = new JTextField();
        textField1.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textField1.setMaximumSize(new Dimension(500, 40));
        textField1.setAlignmentX(Component.CENTER_ALIGNMENT);
        textField1.setBackground(theme.getTextFieldColor());

        centerPanel.add(player1Label);
        centerPanel.add(Box.createVerticalStrut(3));
        centerPanel.add(textField1);
        centerPanel.add(Box.createVerticalStrut(10));
    }

    protected void createPlayer2Field() {
        player2Label = new JLabel(lang.get("tictactoe.name.playername2"));
        player2Label.setForeground(theme.getFontColor2());
        player2Label.setAlignmentX(Component.CENTER_ALIGNMENT);

        textField2 = new JTextField();
        textField2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textField2.setMaximumSize(new Dimension(500, 40));
        textField2.setAlignmentX(Component.CENTER_ALIGNMENT);
        textField2.setBackground(theme.getTextFieldColor());

        centerPanel.add(player2Label);
        centerPanel.add(Box.createVerticalStrut(3));
        centerPanel.add(textField2);
        centerPanel.add(Box.createVerticalStrut(10));
    }

    protected void createRoleSelection() {
        Color bodyTextColor = new Color(0x2B6F6E);

        roleLabel = new JLabel(lang.get("tictactoe.name.selectrole"));
        roleLabel.setForeground(bodyTextColor);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        xButton = new JRadioButton("X");
        oButton = new JRadioButton("O");

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(xButton);
        roleGroup.add(oButton);

        xButton.setForeground(bodyTextColor);
        oButton.setForeground(bodyTextColor);
        xButton.setBackground(new Color(247, 247, 255));
        oButton.setBackground(new Color(247, 247, 255));
        xButton.setOpaque(true);
        oButton.setOpaque(true);
        xButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        oButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        xButton.setBackground(theme.getBackgroundColor());
        oButton.setBackground(theme.getBackgroundColor());

        centerPanel.add(roleLabel);
        centerPanel.add(Box.createVerticalStrut(3));
        centerPanel.add(xButton);
        centerPanel.add(Box.createVerticalStrut(0));
        centerPanel.add(oButton);
        centerPanel.add(Box.createVerticalStrut(10));
    }

    protected void createButtons() {
        startButton = createRoundedButton(lang.get("tictactoe.name.startgame"),
                new Color(184, 107, 214), new Color(204, 127, 234), new Color(120, 60, 150), true);
        startButton.addActionListener(e -> handleStartGame());

        backButton = createRoundedButton(lang.get("tictactoe.name.back"),
                new Color(184, 107, 214), new Color(204, 127, 234), new Color(120, 60, 150), true);
        backButton.addActionListener(e -> handleBack());

        centerPanel.add(startButton);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(backButton);

        add(centerPanel, BorderLayout.CENTER);
    }

    protected void resizeComponents() {
        double scale = Math.min(getWidth() / 500.0, getHeight() / 350.0);
        scale = Math.max(0.7, Math.min(scale, 2.0));
        //resizeAllComponents(this, scale);
        revalidate();
        repaint();
    }

    // ...existing code...

    public abstract void updateLanguage();
    public abstract void updateTheme();
    protected abstract void handleStartGame();
    protected abstract void handleBack();
}