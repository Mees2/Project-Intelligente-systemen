package framework.controllers;

import framework.gui.ApplicationFrame;
import java.util.Map;
import java.util.HashMap;
import javax.swing.JPanel;

public final class MenuManager {
    private final ApplicationFrame mainFrame;
    private final Map<String, JPanel> panels = new HashMap<>();

    // Panel name constants
    private static final String MAIN_MENU = "MAIN_MENU";
    private static final String TICTACTOE_MENU = "TICTACTOE_MENU";
    private static final String REVERSI_MENU = "REVERSI_MENU";
    private static final String SETTINGS_MENU = "SETTINGS_MENU";
    private static final String REVERSI_NAME_PVP = "REVERSI_NAME_PVP";
    private static final String REVERSI_NAME_MCTS = "REVERSI_NAME_MCTS";
    private static final String TICTACTOE_NAME_PVP = "TICTACTOE_NAME_PVP";
    private static final String TICTACTOE_NAME_PVA = "TICTACTOE_NAME_PVA";
    private static final String TICTACTOE_NAME_SERVER = "TICTACTOE_NAME_SERVER";
    private static final String TICTACTOE_NAME_TOURNAMENT = "TICTACTOE_NAME_TOURNAMENT";
    private static final String REVERSI_NAME_MONTECARLO = "REVERSI_NAME_MONTECARLO";

    public MenuManager() {
        mainFrame = new ApplicationFrame(this);
        panels.put(MAIN_MENU, new framework.gui.menu.MainMenuPanel(this));
        panels.put(TICTACTOE_MENU, new framework.gui.menu.GameMenu(this, framework.gui.menu.GameMenu.GameType.TICTACTOE));
        panels.put(REVERSI_MENU, new framework.gui.menu.GameMenu(this, framework.gui.menu.GameMenu.GameType.REVERSI));
        panels.put(SETTINGS_MENU, new SettingsMenu(this));
        panels.forEach(mainFrame::addPanel);
    }

    public void startApplication() {
        mainFrame.setVisible(true);
        mainFrame.showPanel(MAIN_MENU);
    }

    public void openReversiMenu() {
        mainFrame.showPanel(REVERSI_MENU);
    }

    public void openReversiNamePvp() {
        if (!panels.containsKey(REVERSI_NAME_PVP)) {
            framework.gui.menu.reversi.ReversiNameSelection namePanel =
                new framework.gui.menu.reversi.ReversiNameSelection(this, 
                    framework.gui.menu.reversi.ReversiNameSelection.GameMode.PVP);
            panels.put(REVERSI_NAME_PVP, namePanel);
            mainFrame.addPanel(REVERSI_NAME_PVP, namePanel);
        }
        mainFrame.showPanel(REVERSI_NAME_PVP);
    }

    public void openReversiNameMCTS() {
        if (!panels.containsKey(REVERSI_NAME_MCTS)) {
            framework.gui.menu.reversi.ReversiNameSelection namePanel =
                new framework.gui.menu.reversi.ReversiNameSelection(this,
                    framework.gui.menu.reversi.ReversiNameSelection.GameMode.AI,
                    framework.gui.menu.reversi.ReversiNameSelection.AIType.MCTS);
            panels.put(REVERSI_NAME_MCTS, namePanel);
            mainFrame.addPanel(REVERSI_NAME_MCTS, namePanel);
        }
        mainFrame.showPanel(REVERSI_NAME_MCTS);
    }

    public void openReversiNameMINIMAX() {
        final String key = "REVERSI_NAME_MINIMAX";
        if (!panels.containsKey(key)) {
            framework.gui.menu.reversi.ReversiNameSelection namePanel =
                new framework.gui.menu.reversi.ReversiNameSelection(this,
                    framework.gui.menu.reversi.ReversiNameSelection.GameMode.AI,
                    framework.gui.menu.reversi.ReversiNameSelection.AIType.MINIMAX);
            panels.put(key, namePanel);
            mainFrame.addPanel(key, namePanel);
        }
        mainFrame.showPanel(key);
    }

    public void openReversiNameServer() {
        final String key = "REVERSI_NAME_SERVER";
        if (!panels.containsKey(key)) {
            framework.gui.menu.reversi.ReversiNameSelection namePanel =
                new framework.gui.menu.reversi.ReversiNameSelection(this,
                    framework.gui.menu.reversi.ReversiNameSelection.GameMode.SERVER);
            panels.put(key, namePanel);
            mainFrame.addPanel(key, namePanel);
        }
        mainFrame.showPanel(key);
    }

    public void openReversiNameTournament() {
        final String key = "REVERSI_NAME_TOURNAMENT";
        if (!panels.containsKey(key)) {
            framework.gui.menu.reversi.ReversiNameSelection namePanel =
                new framework.gui.menu.reversi.ReversiNameSelection(this,
                    framework.gui.menu.reversi.ReversiNameSelection.GameMode.TOURNAMENT);
            panels.put(key, namePanel);
            mainFrame.addPanel(key, namePanel);
        }
        mainFrame.showPanel(key);
    }

    public void openReversiNamePva() {
        if (!panels.containsKey(REVERSI_NAME_MONTECARLO)) {
            framework.gui.menu.reversi.ReversiNameSelection namePanel =
                    new framework.gui.menu.reversi.ReversiNameSelection(this,
                        framework.gui.menu.reversi.ReversiNameSelection.GameMode.AI,
                        framework.gui.menu.reversi.ReversiNameSelection.AIType.MCTS);
            panels.put(REVERSI_NAME_MONTECARLO, namePanel);
            mainFrame.addPanel(REVERSI_NAME_MONTECARLO, namePanel);
        }
        mainFrame.showPanel(REVERSI_NAME_MONTECARLO);
    }

    public void returnToMainMenu() {
        mainFrame.showPanel(MAIN_MENU);
    }

    public void openTicTacToeMenu() {
        mainFrame.showPanel(TICTACTOE_MENU);
    }

    public void openSettingsMenu() {
        mainFrame.showPanel(SETTINGS_MENU);
    }

    public void confirmExit() {
        int option = javax.swing.JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to exit?",
                "Confirm Exit",
                javax.swing.JOptionPane.YES_NO_OPTION
        );
        if (option == javax.swing.JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public void openNameSelection(String mode) {
        framework.gui.menu.tictactoe.TicTacToeNameSelection.GameMode gameMode;
        String panelKey;

        switch (mode) {
            case "PVP" -> {
                gameMode = framework.gui.menu.tictactoe.TicTacToeNameSelection.GameMode.PVP;
                panelKey = TICTACTOE_NAME_PVP;
            }
            case "PVA" -> {
                gameMode = framework.gui.menu.tictactoe.TicTacToeNameSelection.GameMode.PVA;
                panelKey = TICTACTOE_NAME_PVA;
            }
            case "SERVER" -> {
                gameMode = framework.gui.menu.tictactoe.TicTacToeNameSelection.GameMode.SERVER;
                panelKey = TICTACTOE_NAME_SERVER;
            }
            case "TOURNAMENT" -> {
                gameMode = framework.gui.menu.tictactoe.TicTacToeNameSelection.GameMode.TOURNAMENT;
                panelKey = TICTACTOE_NAME_TOURNAMENT;
            }
            default -> {
                System.err.println("Unknown game mode: " + mode);
                return;
            }
        }

        // Create panel if it doesn't exist
        if (!panels.containsKey(panelKey)) {
            framework.gui.menu.tictactoe.TicTacToeNameSelection namePanel =
                new framework.gui.menu.tictactoe.TicTacToeNameSelection(this, gameMode);
            panels.put(panelKey, namePanel);
            mainFrame.addPanel(panelKey, namePanel);
        }
        mainFrame.showPanel(panelKey);
    }

    public void startTicTacToeGame(String mode, String player1, String player2) {
        String panelKey = "TICTACTOE_GAME_" + mode;

        framework.gui.menu.tictactoe.TicTacToeGame gamePanel =
            new framework.gui.menu.tictactoe.TicTacToeGame(this, mode, player1, player2);

        gamePanel.start();

        panels.put(panelKey, gamePanel);
        mainFrame.addPanel(panelKey, gamePanel);
        mainFrame.showPanel(panelKey);

        gamePanel.revalidate();
        gamePanel.repaint();
        mainFrame.revalidate();
        mainFrame.repaint();
    }


    public void returnToTicTacToeMenu() {
        mainFrame.showPanel(TICTACTOE_MENU);
    }

    public void onReversiGameFinished() {
        mainFrame.showPanel(REVERSI_MENU);
    }

    public void onTicTacToeGameFinished() {
        mainFrame.showPanel(TICTACTOE_MENU);
    }

    public void startReversiGame(String mode, String player1, String player2, char playerColor) {
        String panelKey = "REVERSI_GAME_" + mode;

        framework.gui.menu.reversi.ReversiGame gamePanel =
            new framework.gui.menu.reversi.ReversiGame(this, mode, player1, player2, playerColor);

        gamePanel.start();

        panels.put(panelKey, gamePanel);
        mainFrame.addPanel(panelKey, gamePanel);
        mainFrame.showPanel(panelKey);

        gamePanel.revalidate();
        gamePanel.repaint();
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public void returnToMainMenuFromSettings() {
        mainFrame.showPanel(MAIN_MENU);
    }

    public void updateLanguage() {
        for (JPanel panel : panels.values()) {
            try {
                java.lang.reflect.Method method = panel.getClass().getMethod("updateLanguage");
                method.invoke(panel);
            } catch (Exception e) {
            }
        }
        mainFrame.setTitle(LanguageManager.getInstance().get("main.title"));
    }

    public void closeNameSelection() {
        mainFrame.showPanel(TICTACTOE_MENU);
    }

}
