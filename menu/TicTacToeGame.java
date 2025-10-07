package menu;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import TicTacToe.TicTacToe;
import TicTacToe.MinimaxAI;

public class TicTacToeGame {
    private final MenuManager menuManager;
    private final String gameMode; // "PVP" of "PVA"
    private final LanguageManager lang = LanguageManager.getInstance();
    private boolean turnX = true; // Speler X (menselijke speler) begint altijd
    private final String speler1; // naam speler1 (X)
    private final String speler2; // naam speler2 (O)

    private JLabel statusLabel;
    private final JButton[] buttons = new JButton[9];
    private final TicTacToe game = new TicTacToe();
    private boolean gameDone = false;
    private JFrame gameFrame;
    private JButton menuButton;
    private char spelerRol; // Rol menselijke speler (X of O)
    private char aiRol; // Rol AI (X of O)

    public TicTacToeGame(MenuManager menuManager, String gameMode, String speler1, String speler2) {
        this.menuManager = menuManager;
        this.gameMode = gameMode;
        this.speler1 = speler1;
        this.speler2 = speler2;

        if (gameMode.equals("PVA")) {
            // Rollen toewijzen op basis van wie "AI" is in speler1/speler2
            if (speler1.equals("AI")) {
                aiRol = 'X';
                spelerRol = 'O';
            } else {
                aiRol = 'O';
                spelerRol = 'X';
            }
        }
    }

    public void start() {
        initializeGame();
    }

    private void initializeGame() {
        turnX = true; // X begint altijd

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

        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setFont(statusLabel.getFont().deriveFont(18f));
        gameFrame.add(statusLabel, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3, 10, 10));

        for (int i = 0; i < 9; i++) {
            JButton button = new JButton("");
            buttons[i] = button;
            button.setFont(button.getFont().deriveFont(40f));
            final int pos = i;
            button.addActionListener(e -> handleButtonClick(pos));
            panel.add(button);
        }
        gameFrame.add(panel, BorderLayout.CENTER);

        // Menu knop toevoegen
        menuButton = new JButton(lang.get("tictactoe.game.menu"));
        menuButton.addActionListener(e -> returnToMenu());
        gameFrame.add(menuButton, BorderLayout.SOUTH);

        gameFrame.setVisible(true);

        updateStatusLabel();

        if (gameMode.equals("PVA") && !isPlayersTurn()) {
            doAiMove();
        }
    }

    private void handleButtonClick(int pos) {
        if (gameDone || !game.isFree(pos)) return;

        // Alleen reageren als het de speler zijn beurt is
        if (gameMode.equals("PVA") && !isPlayersTurn()) return;

        char currentPlayer = turnX ? 'X' : 'O';
        game.doMove(pos, currentPlayer);
        buttons[pos].setText(String.valueOf(currentPlayer));

        if (checkEnd(currentPlayer)) return;

        turnX = !turnX;
        updateStatusLabel();

        if (gameMode.equals("PVA") && !isPlayersTurn() && !gameDone) {
            doAiMove();
        }
    }

    private void doAiMove() {
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            int move = MinimaxAI.bestMove(game, aiRol, spelerRol);
            if (move != -1) {
                game.doMove(move, aiRol);
                buttons[move].setText(String.valueOf(aiRol));
            }

            if (checkEnd(aiRol)) return;

            // Na AI zet is weer speler aan de beurt
            turnX = (spelerRol == 'X');

            updateStatusLabel();
        });
    }

    private boolean checkEnd(char player) {
        if (game.isWin(player)) {
            if (gameMode.equals("PVA")) {
                if (player == 'X') {
                    statusLabel.setText(lang.get("tictactoe.game.win", lang.get("tictactoe.game.player")));
                } else {
                    statusLabel.setText(lang.get("tictactoe.game.win", lang.get("tictactoe.game.ai")));
                }
            } else {
                statusLabel.setText(lang.get("tictactoe.game.win", String.valueOf(player)));
            }
            String winnaar = getNameBySymbol(player);
            statusLabel.setText("Gefeliciteerd! " + winnaar + " (" + player + ") wint!");
            gameDone = true;
            return true;
        } else if (game.isDraw()) {
            statusLabel.setText(lang.get("tictactoe.game.draw"));
            gameDone = true;
            return true;
        } else if (!isWinPossible()) {
        statusLabel.setText("Gelijkspel!");
        gameDone = true;
        return true;
        }
        
        return false;
    }


    private String getTitleForMode() {
        if (gameMode.equals("PVP")) {
            return lang.get("tictactoe.game.title.pvp");
        } else {
            return lang.get("tictactoe.game.title.pva");
        }
    }

    /**
     * Krijgt de initiële status tekst gebaseerd op de mode
     * @return De status tekst
     */
    private String getInitialStatusText() {
        if (gameMode.equals("PVP")) {
            return lang.get("tictactoe.game.turn", "X");
        } else {
            return lang.get("tictactoe.game.turn", "X");
        }
    }
    private boolean isPlayersTurn() {
        return (turnX ? 'X' : 'O') == spelerRol;
    }

    private String getNameBySymbol(char symbol) {
        if (gameMode.equals("PVP")) {
            return (symbol == 'X') ? speler1 : speler2;
        } else if (gameMode.equals("PVA")) {
            // AI is letterlijk "AI", speler naam komt uit input
            if (symbol == spelerRol) return (spelerRol == 'X') ? speler1 : speler2;
            else return "AI";
        }
        return "";
    }

    private void updateStatusLabel() {
        if (gameDone) return;

        char currentSymbol = turnX ? 'X' : 'O';
        String currentName = getNameBySymbol(currentSymbol);
        statusLabel.setText("Beurt: " + currentName + " (" + currentSymbol + ")");
    }

    private void returnToMenu() {
        int option = JOptionPane.showConfirmDialog(
            gameFrame,
            lang.get("main.exit.confirm"),
            lang.get("main.exit.title"),
            JOptionPane.YES_NO_OPTION
        );
        if (option == JOptionPane.YES_OPTION) {
            gameFrame.dispose();
            menuManager.onGameFinished();
        }
    }
    /**
     * Controleert vanaf de huidige spelstaat (na de laatst gespeelde zet)
     * of er nog een pad bestaat dat eindigt in winst voor iemand.
     * Let op: checkEnd wordt aangeroepen direct na een zet, dus de volgende
     * speler is !turnX — we moeten de zoekboom vanaf die speler beginnen.
     */
    private boolean isWinPossible() {
        // start vanaf de speler die nu aan de beurt is (na de laatst uitgevoerde zet)
        return isWinPossibleRecursive(!turnX);
    }

    private boolean isWinPossibleRecursive(boolean xTurn) {
        // Als er al een winnaar is -> er was dus een pad naar winst
        if (game.isWin('X') || game.isWin('O')) return true;

        // Als bord vol en geen winnaar -> dit pad leidt tot remise
        if (game.isDraw()) return false;

        char speler = xTurn ? 'X' : 'O';

        // Probeer alle vrije posities
        for (int i = 0; i < 9; i++) {
            if (game.isFree(i)) {
                game.doMove(i, speler);
                boolean possible = isWinPossibleRecursive(!xTurn);
                game.undoMove(i);

                if (possible) return true; // vond een pad naar winst
            }
        }

        return false; // geen pad naar winst gevonden in deze tak
    }
}

