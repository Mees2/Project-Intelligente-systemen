package framework.gui.menu.reversi;

import framework.controllers.LanguageManager;
import framework.controllers.MenuManager;
import framework.controllers.ThemeManager;
import framework.gui.AbstractRoundedButton;

import javax.swing.*;
import java.awt.*;

/**
 * Configuration panel for AI vs AI game mode.
 * Allows users to configure MCTS simulations and Minimax depth.
 */
public class ReversiAIConfigPanel extends AbstractRoundedButton {
    private final MenuManager menuManager;
    private final LanguageManager lang = LanguageManager.getInstance();
    private final ThemeManager theme = ThemeManager.getInstance();

    private JLabel titleLabel;
    private JLabel mctsLabel;
    private JLabel minimaxLabel;
    private JSpinner mctsSpinner;
    private JSpinner minimaxSpinner;
    private JButton startButton;
    private JButton backButton;
    private JPanel centerPanel;

    public ReversiAIConfigPanel(MenuManager menuManager) {
        this.menuManager = menuManager;
        initializePanel();
        theme.addThemeChangeListener(this::updateTheme);
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(theme.getBackgroundColor());

        // Title
        titleLabel = new JLabel("AI vs AI Configuratie", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(theme.getFontColor1());
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Center panel with configuration options
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(theme.getBackgroundColor());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // MCTS Configuration
        JPanel mctsPanel = createConfigRow("MCTS Simulaties:", 100, 100000, 1000, 500);
        mctsLabel = (JLabel) mctsPanel.getComponent(0);
        mctsSpinner = (JSpinner) mctsPanel.getComponent(1);
        centerPanel.add(mctsPanel);
        centerPanel.add(Box.createVerticalStrut(20));

        // Minimax Configuration
        JPanel minimaxPanel = createConfigRow("Minimax Diepte:", 1, 15, 5, 1);
        minimaxLabel = (JLabel) minimaxPanel.getComponent(0);
        minimaxSpinner = (JSpinner) minimaxPanel.getComponent(1);
        centerPanel.add(minimaxPanel);
        centerPanel.add(Box.createVerticalStrut(40));

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(theme.getBackgroundColor());

        startButton = createRoundedButton("Start Game",
                theme.getButtonColor(), theme.getButtonColorHover(),
                theme.getButtonColor().darker(), true);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> startGame());
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createVerticalStrut(10));

        backButton = createRoundedButton("Terug",
                theme.getMainButtonColor(), theme.getMainButtonColorHover(),
                theme.getMainButtonColor().darker(), true);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> menuManager.openReversiMenu());
        buttonPanel.add(backButton);

        centerPanel.add(buttonPanel);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createConfigRow(String labelText, int min, int max, int defaultValue, int step) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setBackground(theme.getBackgroundColor());
        panel.setMaximumSize(new Dimension(400, 50));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setForeground(theme.getFontColor2());
        label.setPreferredSize(new Dimension(150, 30));

        SpinnerNumberModel model = new SpinnerNumberModel(defaultValue, min, max, step);
        JSpinner spinner = new JSpinner(model);
        spinner.setPreferredSize(new Dimension(100, 30));
        spinner.setFont(new Font("SansSerif", Font.PLAIN, 14));

        panel.add(label);
        panel.add(spinner);

        return panel;
    }

    private void startGame() {
        int mctsSimulations = (Integer) mctsSpinner.getValue();
        int minimaxDepth = (Integer) minimaxSpinner.getValue();

        menuManager.startReversiAIvsAI(mctsSimulations, minimaxDepth);
    }

    public void updateLanguage() {
        titleLabel.setText(lang.get("reversi.aiconfig.title"));
        mctsLabel.setText(lang.get("reversi.aiconfig.mcts"));
        minimaxLabel.setText(lang.get("reversi.aiconfig.minimax"));
        startButton.setText(lang.get("reversi.aiconfig.start"));
        backButton.setText(lang.get("reversi.aiconfig.back"));
    }

    public void updateTheme() {
        setBackground(theme.getBackgroundColor());
        centerPanel.setBackground(theme.getBackgroundColor());
        titleLabel.setForeground(theme.getFontColor1());
        mctsLabel.setForeground(theme.getFontColor2());
        minimaxLabel.setForeground(theme.getFontColor2());

        // Update spinner panels background
        for (Component comp : centerPanel.getComponents()) {
            if (comp instanceof JPanel panel) {
                panel.setBackground(theme.getBackgroundColor());
            }
        }

        startButton.putClientProperty("baseColor", theme.getButtonColor());
        startButton.putClientProperty("hoverColor", theme.getButtonColorHover());
        startButton.putClientProperty("borderColor", theme.getButtonColor().darker());

        backButton.putClientProperty("baseColor", theme.getMainButtonColor());
        backButton.putClientProperty("hoverColor", theme.getMainButtonColorHover());
        backButton.putClientProperty("borderColor", theme.getMainButtonColor().darker());

        repaint();
    }
}
