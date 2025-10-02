package menu;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Import de bestaande game classes
import TicTacToe.TicTacToe;
import TicTacToe.MinimaxAI;

/**
 * TicTacToeGame klasse die het TicTacToe spel beheert
 * Ondersteunt zowel Speler vs Speler als Speler vs AI-modes
 */
public class TicTacToeGame {
    private final MenuManager menuManager;
    private final String gameMode; // "PVP" of "PVA"
    private boolean turnX = true; // Speler X (menselijke speler) begint altijd
    private JLabel statusLabel;
    private final JButton[] buttons = new JButton[9];
    private final TicTacToe game = new TicTacToe();
    private boolean gameDone = false;
    private JFrame gameFrame;

    /**
     * Constructor voor TicTacToeGame
     * @param menuManager De menumanager voor navigatie
     * @param gameMode De spelmode: "PVP" voor Player vs Player, "PVA" voor Player vs AI
     */
    public TicTacToeGame(MenuManager menuManager, String gameMode) {
        this.menuManager = menuManager;
        this.gameMode = gameMode;
    }

    /**
     * Start het TicTacToe spel
     */
    public void start() {
        initializeGame();
    }

    /**
     * Initialiseert de spelinterface
     */
    private void initializeGame() {
        gameFrame = new JFrame(getTitleForMode());
        gameFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        gameFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                returnToMenu();
            }
        });
        gameFrame.setSize(400, 450);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setLayout(new BorderLayout());

        // Status label met informatie over huidige beurt
        statusLabel = new JLabel(getInitialStatusText(), JLabel.CENTER);
        gameFrame.add(statusLabel, BorderLayout.NORTH);

        // Spelbord panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3, 10, 10));

        // Creëer de 9 spelknoppen
        for (int i = 0; i < 9; i++) {
            JButton button = new JButton("");
            buttons[i] = button;
            button.setFont(button.getFont().deriveFont(40f));

            final int pos = i;
            button.addActionListener(_ -> handleButtonClick(pos));

            panel.add(button);
        }

        gameFrame.add(panel, BorderLayout.CENTER);

        // Menu knop toevoegen
        JButton menuButton = new JButton("Terug naar Menu");
        menuButton.addActionListener(_ -> returnToMenu());
        gameFrame.add(menuButton, BorderLayout.SOUTH);

        gameFrame.setVisible(true);
    }

    /**
     * Behandelt een klik op een speelknop
     * @param pos De positie van de geklikte knop (0-8)
     */
    private void handleButtonClick(int pos) {
        if (gameDone || !game.isFree(pos)) return;

        // Zet van de huidige speler
        char currentPlayer = turnX ? 'X' : 'O';
        game.doMove(pos, currentPlayer);
        buttons[pos].setText(String.valueOf(currentPlayer));

        if (checkEnd(currentPlayer)) return;

        // Wissel van beurt
        turnX = !turnX;

        if (gameMode.equals("PVP")) {
            // Player vs Player: beide spelers zijn menselijk
            statusLabel.setText(turnX ? "Beurt: X" : "Beurt: O");
        } else if (gameMode.equals("PVA")) {
            // Player vs AI: alleen als het nu de AI's beurt is
            if (!turnX) { // O is de AI
                statusLabel.setText("Beurt: O (AI)");
                doAiMove();
            } else {
                statusLabel.setText("Beurt: X (jij)");
            }
        }
    }

    /**
     * Laat de AI een zet doen (alleen in PVA-mode)
     */
    private void doAiMove() {
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(500); // Korte vertraging voor betere gebruikerservaring
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            int move = MinimaxAI.bestMove(game, 'O', 'X');
            if (move != -1) {
                game.doMove(move, 'O');
                buttons[move].setText("O");
            }

            if (checkEnd('O')) return;

            turnX = true;
            statusLabel.setText("Beurt: X (jij)");
        });
    }

    /**
     * Controleert of het spel beëindigd is (win of gelijkspel)
     * @param player De speler die zojuist een zet heeft gedaan
     * @return true als het spel beëindigd is
     */
    private boolean checkEnd(char player) {
        if (game.isWin(player)) {
            if (gameMode.equals("PVA")) {
                if (player == 'X') {
                    statusLabel.setText("Gefeliciteerd! Jij wint!");
                } else {
                    statusLabel.setText("AI wint! Probeer het opnieuw.");
                }
            } else {
                statusLabel.setText("Speler " + player + " wint!");
            }
            gameDone = true;
            return true;
        } else if (game.isDraw()) {
            statusLabel.setText("Gelijkspel!");
            gameDone = true;
            return true;
        }
        return false;
    }

    /**
     * Krijgt de titel voor het spelvenster gebaseerd op de mode
     * @return De titel string
     */
    private String getTitleForMode() {
        if (gameMode.equals("PVP")) {
            return "TicTacToe - Speler vs Speler";
        } else {
            return "TicTacToe - Speler vs AI";
        }
    }

    /**
     * Krijgt de initiële status tekst gebaseerd op de mode
     * @return De status tekst
     */
    private String getInitialStatusText() {
        if (gameMode.equals("PVP")) {
            return "Beurt: X";
        } else {
            return "Beurt: X (jij)";
        }
    }

    /**
     * Keert terug naar het menu
     */
    private void returnToMenu() {
        int option = JOptionPane.showConfirmDialog(
            gameFrame,
            "Weet je zeker dat je terug wilt naar het menu?",
            "Bevestig",
            JOptionPane.YES_NO_OPTION
        );
        if (option == JOptionPane.YES_OPTION) {
            gameFrame.dispose();
            menuManager.onGameFinished();
        }
    }
}

//nieuwe 