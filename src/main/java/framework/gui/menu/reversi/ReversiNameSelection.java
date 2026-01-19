package framework.gui.menu.reversi;

import framework.controllers.MenuManager;
import framework.gui.AbstractNameSelection;

import javax.swing.*;

/**
 * A dialog window for entering player names in Reversi game modes.
 * Supports PVP, AI, and TOURNAMENT modes.
 */
public class ReversiNameSelection extends AbstractNameSelection {

    public enum GameMode {
        PVP, AI, TOURNAMENT
    }

    public enum AIType {
        MCTS,
        MINIMAX
    }

    private final GameMode gameMode;
    private final AIType aiType;

    /**
     * Creates a new ReversiNameSelection dialog for PVP mode.
     */
    public ReversiNameSelection(MenuManager menuManager, GameMode gameMode) {
        super(menuManager);
        this.gameMode = gameMode;
        this.aiType = null;
        initializeMenu();
    }

    /**
     * Creates a new ReversiNameSelection dialog for AI mode.
     */
    public ReversiNameSelection(MenuManager menuManager, GameMode gameMode, AIType aiType) {
        super(menuManager);
        this.gameMode = gameMode;
        this.aiType = aiType;
        initializeMenu();
    }

    /**
     * Creates a new ReversiNameSelection dialog for TOURNAMENT mode.
     */
    public ReversiNameSelection(MenuManager menuManager, AIType aiType) {
        super(menuManager);
        this.gameMode = GameMode.TOURNAMENT;
        this.aiType = aiType;
        initializeMenu();
    }

    private void initializeMenu() {
        initializeFrame();
        createTopPanel();
        createCenterPanel();
        createPlayer1Field(getPlayer1LabelKey());

        if (gameMode == GameMode.PVP) {
            createPlayer2Field();
        }
        if (gameMode == GameMode.AI) {
            createReversiRoleSelection();
        }
        // TOURNAMENT mode doesn't need role selection - server decides
        createButtons();
        theme.addThemeChangeListener(this::updateTheme);
    }

    private String getPlayer1LabelKey() {
        return switch (gameMode) {
            case PVP -> "reversi.name.playername1";
            case AI, TOURNAMENT -> "reversi.name.playername";
        };
    }

    /**
     * Creates the role selection radio buttons for choosing Black or White.
     */
    protected void createReversiRoleSelection() {
        roleLabel = new JLabel(lang.get("reversi.name.selectrole"));
        roleLabel.setForeground(theme.getFontColor2());
        roleLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        xButton = new JRadioButton(lang.get("reversi.name.black"));
        oButton = new JRadioButton(lang.get("reversi.name.white"));

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(xButton);
        roleGroup.add(oButton);

        xButton.setForeground(theme.getFontColor2());
        oButton.setForeground(theme.getFontColor2());
        xButton.setBackground(theme.getBackgroundColor());
        oButton.setBackground(theme.getBackgroundColor());
        xButton.setOpaque(true);
        oButton.setOpaque(true);
        xButton.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        oButton.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        centerPanel.add(roleLabel);
        centerPanel.add(Box.createVerticalStrut(3));
        centerPanel.add(xButton);
        centerPanel.add(Box.createVerticalStrut(0));
        centerPanel.add(oButton);
        centerPanel.add(Box.createVerticalStrut(10));
    }

    @Override
    protected void handleStartGame() {
        String player1Name = textField1.getText().trim();

        if (player1Name.isEmpty()) {
            JOptionPane.showMessageDialog(this, lang.get("reversi.name.error.emptyname"),
                    lang.get("common.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        switch (gameMode) {
            case PVP -> {
                String player2Name = textField2.getText().trim();
                if (player2Name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, lang.get("reversi.name.error.emptyname"),
                            lang.get("common.error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                menuManager.startReversiGame("PVP", player1Name, player2Name, 'B');
            }
            case AI -> {
                if (!xButton.isSelected() && !oButton.isSelected()) {
                    JOptionPane.showMessageDialog(this, lang.get("reversi.name.error.emptyrole"),
                            lang.get("common.error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String aiName = getAIName();
                String aiModeString = aiType.name();
                char selectedColor = xButton.isSelected() ? 'B' : 'W';
                menuManager.startReversiGame(aiModeString, player1Name, aiName, selectedColor);
            }
            case TOURNAMENT -> {
                String aiName = getAIName();
                // Use TOURNAMENT_ prefix to distinguish from regular AI mode
                menuManager.startReversiGame("TOURNAMENT_" + aiType.name(), player1Name, aiName, 'B');
            }
        }
    }

    private String getAIName() {
        return switch (aiType) {
            case MCTS -> "MCTS AI";
            case MINIMAX -> "Minimax AI";
        };
    }

    private String getTitleKey() {
        if (gameMode == GameMode.PVP) {
            return "reversi.name.title";
        }
        if (gameMode == GameMode.TOURNAMENT) {
            return "reversi.name.title.tournament";
        }
        return switch (aiType) {
            case MCTS -> "reversi.name.title.mcts";
            case MINIMAX -> "reversi.name.title.minimax";
        };
    }

    @Override
    protected void handleBack() {
        menuManager.openReversiMenu();
    }

    @Override
    public void updateLanguage() {
        titleLabel.setText(lang.get(getTitleKey()));
        player1Label.setText(lang.get(getPlayer1LabelKey()));

        if (gameMode == GameMode.PVP && player2Label != null) {
            player2Label.setText(lang.get("reversi.name.playername2"));
        }
        if (gameMode == GameMode.AI && roleLabel != null) {
            roleLabel.setText(lang.get("reversi.name.selectrole"));
            if (xButton != null) xButton.setText(lang.get("reversi.name.black"));
            if (oButton != null) oButton.setText(lang.get("reversi.name.white"));
        }

        startButton.setText(lang.get("reversi.name.startgame"));
        backButton.setText(lang.get("reversi.name.back"));
    }

    @Override
    public void updateTheme() {
        setBackground(theme.getBackgroundColor());
        centerPanel.setBackground(theme.getBackgroundColor());
        topPanel.setBackground(theme.getBackgroundColor());

        if (gameMode == GameMode.AI) {
            if (xButton != null) {
                xButton.setBackground(theme.getBackgroundColor());
                xButton.setForeground(theme.getFontColor2());
            }
            if (oButton != null) {
                oButton.setBackground(theme.getBackgroundColor());
                oButton.setForeground(theme.getFontColor2());
            }
            if (roleLabel != null) {
                roleLabel.setForeground(theme.getFontColor2());
            }
        }

        startButton.putClientProperty("baseColor", theme.getMainButtonColor());
        startButton.putClientProperty("hoverColor", theme.getMainButtonColorHover());
        startButton.putClientProperty("borderColor", theme.getMainButtonColor().darker());

        backButton.putClientProperty("baseColor", theme.getMainButtonColor());
        backButton.putClientProperty("hoverColor", theme.getMainButtonColorHover());
        backButton.putClientProperty("borderColor", theme.getMainButtonColor().darker());

        titleLabel.setForeground(theme.getFontColor1());
        player1Label.setForeground(theme.getFontColor2());

        if (gameMode == GameMode.PVP && player2Label != null) {
            player2Label.setForeground(theme.getFontColor2());
        }

        textField1.setBackground(theme.getTextFieldColor());

        if (textField2 != null) {
            textField2.setBackground(theme.getTextFieldColor());
        }

        repaint();
    }
}

