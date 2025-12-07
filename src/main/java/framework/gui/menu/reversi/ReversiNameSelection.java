package framework.gui.menu.reversi;

import framework.controllers.MenuManager;
import framework.gui.AbstractNameSelection;

import javax.swing.*;

/**
 * A dialog window for entering player names in Reversi Player vs Player mode.
 * This class creates and manages a form where two players can enter their names
 * before starting a Reversi game. It includes input validation and navigation
 * back to the main Reversi menu.
 */
public class ReversiNameSelection extends AbstractNameSelection {

    /**
     * Creates a new ReversiNamePvp dialog.
     *
     * @param menuManager The MenuManager instance for handling navigation between menus
     */
    public ReversiNameSelection(MenuManager menuManager) {
        super(menuManager);
        initializeMenu();
    }

    /**
     * Initializes and configures the menu interface.
     * Creates and layouts all UI components including:
     * - Title label
     * - Name input fields for both players
     * - Start and Back buttons
     * All components are styled according to the game's visual theme.
     */
    private void initializeMenu() {
        initializeFrame();
        createTopPanel();
        createCenterPanel();
        createPlayer1Field("reversi.name.playername1");
        createPlayer2Field();
        createButtons();
    }

    @Override
    protected void handleStartGame() {
        String player1Name = textField1.getText().trim();
        String player2Name = textField2.getText().trim();

        if (player1Name.isEmpty() || player2Name.isEmpty()) {
            JOptionPane.showMessageDialog(frame, lang.get("reversi.name.error.emptyname"),
                    lang.get("common.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        hideMenu();
        menuManager.startReversiGame("PVP", player1Name, player2Name);
    }

    @Override
    protected void handleBack() {
        hideMenu();
        menuManager.closeReversiNameSelectionPVP();
    }

    @Override
    public void updateLanguage() {
        frame.setTitle(lang.get("reversi.name.title"));
        titleLabel.setText(lang.get("reversi.name.title"));
        player1Label.setText(lang.get("reversi.name.playername1"));

        if (player2Label != null) {
            player2Label.setText(lang.get("reversi.name.playername2"));
        }

        startButton.setText(lang.get("reversi.name.startgame"));
        backButton.setText(lang.get("reversi.name.back"));
    }

    @Override
    public void updateTheme() {
        frame.getContentPane().setBackground(theme.getBackgroundColor());
        centerPanel.setBackground(theme.getBackgroundColor());
        topPanel.setBackground(theme.getBackgroundColor());

        startButton.putClientProperty("baseColor", theme.getMainButtonColor());
        startButton.putClientProperty("hoverColor", theme.getMainButtonColorHover());
        startButton.putClientProperty("borderColor", theme.getMainButtonColor().darker());

        backButton.putClientProperty("baseColor", theme.getMainButtonColor());
        backButton.putClientProperty("hoverColor", theme.getMainButtonColorHover());
        backButton.putClientProperty("borderColor", theme.getMainButtonColor().darker());

        titleLabel.setForeground(theme.getFontColor1());
        player1Label.setForeground(theme.getFontColor2());

        if (player2Label != null) {
            player2Label.setForeground(theme.getFontColor2());
        }

        textField1.setBackground(theme.getTextFieldColor());

        if (textField2 != null) {
            textField2.setBackground(theme.getTextFieldColor());
        }

        frame.repaint();
    }

    public void dispose() {
        if (frame != null) {
            frame.dispose();
        }
    }

}
