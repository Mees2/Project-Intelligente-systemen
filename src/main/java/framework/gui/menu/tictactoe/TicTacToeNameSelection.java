package framework.gui.menu.tictactoe;

import framework.controllers.MenuManager;
import framework.gui.AbstractNameSelection;

import javax.swing.*;

public class TicTacToeNameSelection extends AbstractNameSelection {

    public enum GameMode {
        PVP, PVA, SERVER, TOURNAMENT
    }

    private final GameMode gameMode;

    public TicTacToeNameSelection(MenuManager menuManager, GameMode gameMode) {
        super(menuManager);
        this.gameMode = gameMode;
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
        if (gameMode == GameMode.PVA) {
            createRoleSelection();
        }
        createButtons();
        theme.addThemeChangeListener(this::updateTheme);
    }

    private String getPlayer1LabelKey() {
        return switch (gameMode) {
            case PVP -> "tictactoe.name.playername1";
            case PVA, SERVER, TOURNAMENT -> "tictactoe.name.playername";
        };
    }
    public void dispose() {
    }

    @Override
    protected void handleStartGame() {
        String player1Name = textField1.getText().trim();

        if (player1Name.isEmpty()) {
            JOptionPane.showMessageDialog(frame, lang.get("tictactoe.name.error.emptyname"),
                    lang.get("common.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        switch (gameMode) {
            case PVP -> {
                String player2Name = textField2.getText().trim();
                if (player2Name.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, lang.get("tictactoe.name.error.emptyname"),
                            lang.get("common.error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                hideMenu();
                menuManager.startTicTacToeGame("PVP", player1Name, player2Name);
            }
            case PVA -> {
                if (!xButton.isSelected() && !oButton.isSelected()) {
                    JOptionPane.showMessageDialog(frame, lang.get("tictactoe.name.error.emptyrole"),
                            lang.get("common.error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                hideMenu();
                if (xButton.isSelected()) {
                    menuManager.startTicTacToeGame("PVA", player1Name, "AI");
                } else {
                    menuManager.startTicTacToeGame("PVA", "AI", player1Name);
                }
            }
            case SERVER -> {
                hideMenu();
                menuManager.startTicTacToeGame("SERVER", player1Name, "");
            }
            case TOURNAMENT -> {
                hideMenu();
                menuManager.startTicTacToeGame("TOURNAMENT", player1Name, "");
            }
        }
    }

    @Override
    protected void handleBack() {
        hideMenu();
        switch (gameMode) {
            case PVP -> menuManager.closeNameSelection();
            case PVA -> menuManager.closeNameSelection();
            case SERVER -> menuManager.closeNameSelection();
            case TOURNAMENT -> menuManager.closeNameSelection();
        }
    }

    @Override
    public void updateLanguage() {
        frame.setTitle(lang.get("tictactoe.name.title"));
        titleLabel.setText(lang.get("tictactoe.name.title"));
        player1Label.setText(lang.get(getPlayer1LabelKey()));

        if (gameMode == GameMode.PVP && player2Label != null) {
            player2Label.setText(lang.get("tictactoe.name.player2name"));
        }
        if (gameMode == GameMode.PVA && roleLabel != null) {
            roleLabel.setText(lang.get("tictactoe.name.selectrole"));
        }
        startButton.setText(lang.get("tictactoe.name.startgame"));
        backButton.setText(lang.get("tictactoe.name.back"));
    }

    @Override
    public void updateTheme() {
        frame.getContentPane().setBackground(theme.getBackgroundColor());
        centerPanel.setBackground(theme.getBackgroundColor());
        topPanel.setBackground(theme.getBackgroundColor());

        if (gameMode == GameMode.PVA) {
            oButton.setBackground(theme.getBackgroundColor());
            xButton.setBackground(theme.getBackgroundColor());
            oButton.setForeground(theme.getFontColor2());
            xButton.setForeground(theme.getFontColor2());
            roleLabel.setForeground(theme.getFontColor2());
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
        frame.repaint();
    }
}
