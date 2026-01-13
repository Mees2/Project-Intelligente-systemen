package framework.gui.menu.reversi;

import framework.controllers.LanguageManager;
import framework.controllers.MenuManager;
import framework.controllers.ThemeManager;
import framework.gui.AbstractRoundedButton;

import javax.swing.*;
import java.awt.*;

/**
 * Selection panel for AI vs AI game mode in Reversi.
 * Allows users to select which AI type plays as Black and which plays as White.
 */
public class ReversiAIvsAISelection extends AbstractRoundedButton {
    private final MenuManager menuManager;
    private final LanguageManager lang = LanguageManager.getInstance();
    private final ThemeManager theme = ThemeManager.getInstance();

    private JLabel titleLabel;
    private JLabel blackAILabel;
    private JLabel whiteAILabel;
    private JLabel blackParamLabel;
    private JLabel whiteParamLabel;
    private JComboBox<String> blackAICombo;
    private JComboBox<String> whiteAICombo;
    private JSpinner blackParamSpinner;
    private JSpinner whiteParamSpinner;
    private JButton startButton;
    private JButton backButton;
    private JPanel centerPanel;

    public ReversiAIvsAISelection(MenuManager menuManager) {
        this.menuManager = menuManager;
        initializeMenu();
        theme.addThemeChangeListener(this::updateTheme);
    }

    private void initializeMenu() {
        setLayout(new BorderLayout());
        setBackground(theme.getBackgroundColor());

        // Title
        titleLabel = new JLabel("AI vs AI", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(theme.getFontColor1());
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Center panel
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(theme.getBackgroundColor());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Black AI selection
        blackAILabel = new JLabel("Black AI:");
        blackAILabel.setForeground(theme.getFontColor2());
        blackAILabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(blackAILabel);
        centerPanel.add(Box.createVerticalStrut(10));

        String[] aiTypes = {"Minimax", "MCTS"};
        blackAICombo = new JComboBox<>(aiTypes);
        blackAICombo.setMaximumSize(new Dimension(300, 35));
        blackAICombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        blackAICombo.addActionListener(e -> updateBlackParamLabel());
        centerPanel.add(blackAICombo);
        centerPanel.add(Box.createVerticalStrut(15));

        // Black AI parameter (depth or simulations)
        blackParamLabel = new JLabel("Search Depth:");
        blackParamLabel.setForeground(theme.getFontColor2());
        blackParamLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(blackParamLabel);
        centerPanel.add(Box.createVerticalStrut(10));

        blackParamSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        blackParamSpinner.setMaximumSize(new Dimension(100, 30));
        blackParamSpinner.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(blackParamSpinner);
        centerPanel.add(Box.createVerticalStrut(30));

        // White AI selection
        whiteAILabel = new JLabel("White AI:");
        whiteAILabel.setForeground(theme.getFontColor2());
        whiteAILabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(whiteAILabel);
        centerPanel.add(Box.createVerticalStrut(10));

        whiteAICombo = new JComboBox<>(aiTypes);
        whiteAICombo.setMaximumSize(new Dimension(300, 35));
        whiteAICombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        whiteAICombo.addActionListener(e -> updateWhiteParamLabel());
        centerPanel.add(whiteAICombo);
        centerPanel.add(Box.createVerticalStrut(15));

        // White AI parameter (depth or simulations)
        whiteParamLabel = new JLabel("Search Depth:");
        whiteParamLabel.setForeground(theme.getFontColor2());
        whiteParamLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(whiteParamLabel);
        centerPanel.add(Box.createVerticalStrut(10));

        whiteParamSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        whiteParamSpinner.setMaximumSize(new Dimension(100, 30));
        whiteParamSpinner.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(whiteParamSpinner);
        centerPanel.add(Box.createVerticalStrut(40));

        add(centerPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(theme.getBackgroundColor());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 30, 50));

        startButton = createRoundedButton("Start Game",
                theme.getButtonColor(), theme.getButtonColorHover(),
                theme.getButtonColor().darker(), true);
        startButton.addActionListener(e -> handleStartGame());
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createVerticalStrut(10));

        backButton = createRoundedButton("Back",
                theme.getMainButtonColor(), theme.getMainButtonColorHover(),
                theme.getMainButtonColor().darker(), true);
        backButton.addActionListener(e -> menuManager.openReversiMenu());
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateBlackParamLabel() {
        String selected = (String) blackAICombo.getSelectedItem();
        if ("Minimax".equals(selected)) {
            blackParamLabel.setText("Search Depth:");
            blackParamSpinner.setModel(new SpinnerNumberModel(3, 1, 10, 1));
        } else {
            blackParamLabel.setText("Simulations:");
            blackParamSpinner.setModel(new SpinnerNumberModel(1000, 100, 100000, 100));
        }
    }

    private void updateWhiteParamLabel() {
        String selected = (String) whiteAICombo.getSelectedItem();
        if ("Minimax".equals(selected)) {
            whiteParamLabel.setText("Search Depth:");
            whiteParamSpinner.setModel(new SpinnerNumberModel(3, 1, 10, 1));
        } else {
            whiteParamLabel.setText("Simulations:");
            whiteParamSpinner.setModel(new SpinnerNumberModel(1000, 100, 100000, 100));
        }
    }

    private void handleStartGame() {
        String blackAI = (String) blackAICombo.getSelectedItem();
        String whiteAI = (String) whiteAICombo.getSelectedItem();
        int blackParam = (Integer) blackParamSpinner.getValue();
        int whiteParam = (Integer) whiteParamSpinner.getValue();

        // Create AI names with their types
        String player1Name = "Black " + blackAI;
        String player2Name = "White " + whiteAI;

        // Pass AI types and parameters as game mode string
        String gameMode = "AI_VS_AI:" + blackAI + ":" + whiteAI + ":" + blackParam + ":" + whiteParam;

        menuManager.startReversiGame(gameMode, player1Name, player2Name, 'B');
    }

    public void updateTheme() {
        setBackground(theme.getBackgroundColor());
        centerPanel.setBackground(theme.getBackgroundColor());
        titleLabel.setForeground(theme.getFontColor1());
        blackAILabel.setForeground(theme.getFontColor2());
        whiteAILabel.setForeground(theme.getFontColor2());
        blackParamLabel.setForeground(theme.getFontColor2());
        whiteParamLabel.setForeground(theme.getFontColor2());

        startButton.putClientProperty("baseColor", theme.getButtonColor());
        startButton.putClientProperty("hoverColor", theme.getButtonColorHover());
        startButton.putClientProperty("borderColor", theme.getButtonColor().darker());

        backButton.putClientProperty("baseColor", theme.getMainButtonColor());
        backButton.putClientProperty("hoverColor", theme.getMainButtonColorHover());
        backButton.putClientProperty("borderColor", theme.getMainButtonColor().darker());

        repaint();
    }
}

