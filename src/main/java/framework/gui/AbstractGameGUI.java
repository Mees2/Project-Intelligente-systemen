package framework.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import framework.boardgame.AbstractBoardGame;
import framework.boardgame.GameStatus;
import framework.controllers.LanguageManager;
import framework.controllers.MenuManager;
import framework.players.AbstractPlayer;

/**
 * Abstracte basis klasse voor een spel GUI
 * Bevat gemeenschappelijke functionaliteit voor TicTacToeGame en toekomstige Reversi GUI
 * Gebruikt de structuur uit de originele TicTacToeGame implementatie
 */
public abstract class AbstractGameGUI {
    protected final MenuManager menuManager;
    protected final LanguageManager lang = LanguageManager.getInstance();
    protected final AbstractBoardGame game;
    protected final AbstractPlayer player1;
    protected final AbstractPlayer player2;
    protected AbstractPlayer currentPlayer;

    protected JFrame gameFrame;
    protected JLabel statusLabel;
    protected JButton menuButton;
    protected boolean gameDone = false;

    /**
     * Constructor voor een spel GUI
     * @param menuManager De menu manager voor navigatie
     * @param game Het bordspel
     * @param player1 De eerste speler
     * @param player2 De tweede speler
     */
    protected AbstractGameGUI(MenuManager menuManager, AbstractBoardGame game, AbstractPlayer player1, AbstractPlayer player2) {
        this.menuManager = menuManager;
        this.game = game;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1; // Speler 1 begint altijd
    }

    /**
     * Start het spel en toont de GUI
     */
    public void start() {
        initializeGUI();
    }

    /**
     * Initialiseert de GUI componenten
     * Subklassen kunnen dit overschrijven voor spel-specifieke UI
     */
    protected void initializeGUI() {
        gameFrame = new JFrame(getGameTitle());
        gameFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        gameFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                returnToMenu();
            }
        });
        gameFrame.setSize(getFrameWidth(), getFrameHeight());
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setLayout(new BorderLayout());

        // Status label bovenaan
        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setFont(statusLabel.getFont().deriveFont(18f));
        gameFrame.add(statusLabel, BorderLayout.NORTH);

        // Spelbord in het midden
        JPanel boardPanel = createBoardPanel();
        gameFrame.add(boardPanel, BorderLayout.CENTER);

        // Menu knop onderaan
        menuButton = new JButton(lang.get("tictactoe.game.menu"));
        menuButton.addActionListener(e -> returnToMenu());
        gameFrame.add(menuButton, BorderLayout.SOUTH);

        gameFrame.setVisible(true);

        updateStatusLabel();

        // Als AI begint, doe de eerste zet
        if (currentPlayer.isAI()) {
            doAIMove();
        }
    }

    /**
     * Maak het spelbord panel
     * Subklassen moeten dit implementeren voor spel-specifieke borden
     * @return Het panel met het spelbord
     */
    protected abstract JPanel createBoardPanel();

    /**
     * Krijg de titel van het spel venster
     * @return De titel
     */
    protected abstract String getGameTitle();

    /**
     * Krijg de breedte van het game frame
     * @return De breedte in pixels
     */
    protected int getFrameWidth() {
        return 400;
    }

    /**
     * Krijg de hoogte van het game frame
     * @return De hoogte in pixels
     */
    protected int getFrameHeight() {
        return 450;
    }

    /**
     * Update het status label met de huidige speler
     */
    protected void updateStatusLabel() {
        if (gameDone) {
            return;
        }

        GameStatus status = game.getStatus();
        switch (status) {
            case IN_PROGRESS:
                if (currentPlayer.isAI()) {
                    statusLabel.setText(lang.get("tictactoe.game.turn.ai"));
                } else {
                    statusLabel.setText(lang.get("tictactoe.game.turn", currentPlayer.getName()));
                }
                break;
            case X_WINS:
                String winnerNameX = player1.getSymbol() == 'X' ? player1.getName() : player2.getName();
                statusLabel.setText(lang.get("tictactoe.game.win", winnerNameX));
                gameDone = true;
                break;
            case O_WINS:
                String winnerNameO = player1.getSymbol() == 'O' ? player1.getName() : player2.getName();
                statusLabel.setText(lang.get("tictactoe.game.win", winnerNameO));
                gameDone = true;
                break;
            case DRAW:
                statusLabel.setText(lang.get("tictactoe.game.draw"));
                gameDone = true;
                break;
            default:
                break;
        }
    }

    /**
     * Wissel van huidige speler
     */
    protected void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    /**
     * Doe een AI zet
     * Subklassen kunnen dit overschrijven voor spel-specifieke AI logica
     */
    protected abstract void doAIMove();

    /**
     * Keer terug naar het menu
     */
    protected void returnToMenu() {
        if (gameFrame != null) {
            gameFrame.dispose();
        }
        menuManager.returnToMainMenu();
    }

    /**
     * Update de GUI teksten (voor taal wijzigingen)
     */
    public void updateUITexts() {
        if (gameFrame != null) {
            gameFrame.setTitle(getGameTitle());
        }
        if (menuButton != null) {
            menuButton.setText(lang.get("tictactoe.game.menu"));
        }
        updateStatusLabel();
    }
}
