package framework.gui.menu;

import framework.controllers.LanguageManager;
import framework.controllers.MenuManager;
import framework.controllers.ThemeManager;
import framework.gui.AbstractRoundedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * A unified game menu that handles both TicTacToe and Reversi game options.
 * Uses a game type parameter to determine which buttons and actions to display.
 */
public class GameMenu extends AbstractRoundedButton {

    public enum GameType {
        TICTACTOE, REVERSI
    }

    private final JFrame frame;
    private final MenuManager menuManager;
    private final LanguageManager lang = LanguageManager.getInstance();
    private final ThemeManager theme = ThemeManager.getInstance();
    private final GameType gameType;

    private JLabel titleLabel;
    private JButton pvpButton;
    private JButton pvaButton;
    private JButton serverButton;
    private JButton tournamentButton;
    private JButton backButton;
    private JPanel buttonPanel;

    /**
     * Constructor for the GameMenu
     * @param menuManager The menu manager that controls navigation
     * @param gameType The type of game (TICTACTOE or REVERSI)
     */
    public GameMenu(MenuManager menuManager, GameType gameType) {
        this.menuManager = menuManager;
        this.gameType = gameType;
        this.frame = new JFrame();
        initializeMenu();

        theme.addThemeChangeListener(this::updateTheme);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });
    }

    /**
     * Initializes the game menu interface
     */
    private void initializeMenu() {
        frame.setTitle(lang.get(getTitleKey()));
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(500, 350);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(theme.getBackgroundColor());

        // Title label
        titleLabel = new JLabel(lang.get(getHeaderKey()), JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 25));
        titleLabel.setForeground(theme.getFontColor1());
        frame.add(titleLabel, BorderLayout.NORTH);

        // Button panel
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        buttonPanel.setOpaque(false);

        createGameButtons();

        frame.add(buttonPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the appropriate buttons based on game type
     */
    private void createGameButtons() {
        switch (gameType) {
            case TICTACTOE -> createTicTacToeButtons();
            case REVERSI -> createReversiButtons();
        }
    }

    /**
     * Creates buttons specific to TicTacToe menu
     */
    private void createTicTacToeButtons() {
        // Player vs Player button
        pvpButton = createRoundedButton(lang.get("tictactoe.menu.pvp"),
                theme.getButtonColor(), theme.getButtonColorHover(),
                theme.getButtonColor().darker(), true);
        pvpButton.addActionListener(e -> menuManager.openNameSelection("PVP"));
        buttonPanel.add(pvpButton);
        buttonPanel.add(Box.createVerticalStrut(10));

        // Player vs AI button
        pvaButton = createRoundedButton(lang.get("tictactoe.menu.pva"),
                theme.getButtonColor(), theme.getButtonColorHover(),
                theme.getButtonColor().darker(), true);
        pvaButton.addActionListener(e -> menuManager.openNameSelection("PVA"));
        buttonPanel.add(pvaButton);
        buttonPanel.add(Box.createVerticalStrut(10));

        // Server button
        serverButton = createRoundedButton(lang.get("tictactoe.menu.server"),
                theme.getButtonColor(), theme.getButtonColorHover(),
                theme.getButtonColor().darker(), true);
        serverButton.addActionListener(e -> menuManager.openNameSelection("SERVER"));
        buttonPanel.add(serverButton);
        buttonPanel.add(Box.createVerticalStrut(10));

        // Tournament button
        tournamentButton = createRoundedButton(lang.get("tictactoe.menu.tournament"),
                theme.getButtonColor(), theme.getButtonColorHover(),
                theme.getButtonColor().darker(), true);
        tournamentButton.addActionListener(e -> menuManager.openNameSelection("TOURNAMENT"));
        buttonPanel.add(tournamentButton);
        buttonPanel.add(Box.createVerticalStrut(30));

        // Back button
        backButton = createRoundedButton(lang.get("tictactoe.menu.back"),
                theme.getMainButtonColor(), theme.getMainButtonColorHover(),
                theme.getMainButtonColor().darker(), true);
        backButton.addActionListener(e -> menuManager.returnToMainMenu());
        buttonPanel.add(backButton);
    }

    private void createReversiButtons() {
        // Player vs Player button
        pvpButton = createRoundedButton(lang.get("reversi.menu.pvp"),
                theme.getButtonColor(), theme.getButtonColorHover(),
                theme.getButtonColor().darker(), true);
        pvpButton.addActionListener(e -> menuManager.openReversiNamePvp());
        buttonPanel.add(pvpButton);
        buttonPanel.add(Box.createVerticalStrut(20));

        // Player vs AI button
        pvaButton = createRoundedButton(lang.get("reversi.menu.pva.soon"),
                new Color(200, 200, 200), new Color(200, 200, 200),
                new Color(150, 150, 150), false);
        buttonPanel.add(pvaButton);
        buttonPanel.add(Box.createVerticalStrut(40));

        // Back button
        backButton = createRoundedButton(lang.get("reversi.menu.back"),
                theme.getMainButtonColor(), theme.getMainButtonColorHover(),
                theme.getMainButtonColor().darker(), true);
        backButton.addActionListener(e -> {
            hideMenu();
            menuManager.returnToMainMenu();
        });
        buttonPanel.add(backButton);
    }

    private String getTitleKey() {
        return switch (gameType) {
            case TICTACTOE -> "tictactoe.menu.title";
            case REVERSI -> "reversi.menu.title";
        };
    }

    private String getHeaderKey() {
        return switch (gameType) {
            case TICTACTOE -> "tictactoe.menu.header";
            case REVERSI -> "reversi.menu.header";
        };
    }

    private void resizeComponents() {
        double scale = Math.min(frame.getWidth() / 500.0, frame.getHeight() / 350.0);
        scale = Math.max(0.7, Math.min(scale, 2.0));
        resizeAllComponents(frame, scale);
        frame.revalidate();
        frame.repaint();
    }
    private void resizeAllComponents(Container container, double scale) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton btn) {
                int newFontSize = (int)(12 * scale);
                btn.setFont(btn.getFont().deriveFont(Font.PLAIN, newFontSize));

                int newWidth = (int)(200 * scale);
                int newHeight = (int)(35 * scale);
                Dimension newSize = new Dimension(newWidth, newHeight);
                btn.setPreferredSize(newSize);
                btn.setMinimumSize(newSize);
                btn.setMaximumSize(newSize);
            } else if (comp instanceof JLabel label) {
                int newTitleSize = (int)(25 * scale);
                newTitleSize = Math.max(18, Math.min(newTitleSize, 40));
                label.setFont(label.getFont().deriveFont(Font.BOLD, newTitleSize));
            } else if (comp instanceof Container child) {
                resizeAllComponents(child, scale);
            }
        }
    }
    public void updateLanguage() {
        frame.setTitle(lang.get(getTitleKey()));
        titleLabel.setText(lang.get(getHeaderKey()));

        switch (gameType) {
            case TICTACTOE -> {
                pvpButton.setText(lang.get("tictactoe.menu.pvp"));
                pvaButton.setText(lang.get("tictactoe.menu.pva"));
                serverButton.setText(lang.get("tictactoe.menu.server"));
                tournamentButton.setText(lang.get("tictactoe.menu.tournament"));
                backButton.setText(lang.get("tictactoe.menu.back"));
            }
            case REVERSI -> {
                pvpButton.setText(lang.get("reversi.menu.pvp"));
                pvaButton.setText(lang.get("reversi.menu.pva.soon"));
                backButton.setText(lang.get("reversi.menu.back"));
            }
        }
    }

    public void updateTheme() {
        frame.getContentPane().setBackground(theme.getBackgroundColor());
        titleLabel.setForeground(theme.getFontColor1());

        // Update button colors
        pvpButton.putClientProperty("baseColor", theme.getButtonColor());
        pvpButton.putClientProperty("hoverColor", theme.getButtonColorHover());
        pvpButton.putClientProperty("borderColor", theme.getButtonColor().darker());

        if (gameType == GameType.TICTACTOE) {
            pvaButton.putClientProperty("baseColor", theme.getButtonColor());
            pvaButton.putClientProperty("hoverColor", theme.getButtonColorHover());
            pvaButton.putClientProperty("borderColor", theme.getButtonColor().darker());

            serverButton.putClientProperty("baseColor", theme.getButtonColor());
            serverButton.putClientProperty("hoverColor", theme.getButtonColorHover());
            serverButton.putClientProperty("borderColor", theme.getButtonColor().darker());

            tournamentButton.putClientProperty("baseColor", theme.getButtonColor());
            tournamentButton.putClientProperty("hoverColor", theme.getButtonColorHover());
            tournamentButton.putClientProperty("borderColor", theme.getButtonColor().darker());
        }

        backButton.putClientProperty("baseColor", theme.getMainButtonColor());
        backButton.putClientProperty("hoverColor", theme.getMainButtonColorHover());
        backButton.putClientProperty("borderColor", theme.getMainButtonColor().darker());

        frame.repaint();
    }
    public void showMenu() {
        frame.setVisible(true);
    }
    public void hideMenu() {
        frame.setVisible(false);
    }
}
