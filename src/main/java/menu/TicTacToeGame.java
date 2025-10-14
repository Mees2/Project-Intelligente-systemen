package menu;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import tictactoe.TicTacToe;
import tictactoe.MinimaxAI;
import framework.spelers.AbstractSpeler;
import framework.spelers.MenselijkeSpeler;
import framework.spelers.AISpeler;

public class TicTacToeGame {
    private final MenuManager menuManager;
    private final String gameMode;
    private final LanguageManager lang = LanguageManager.getInstance();
    private final AbstractSpeler speler1;
    private final AbstractSpeler speler2;
    private boolean turnX = true;
    private JLabel statusLabel;
    private final JButton[] buttons = new JButton[9];
    private final TicTacToe game = new TicTacToe();
    private boolean gameDone = false;
    private JFrame gameFrame;
    private JButton menuButton;

    public TicTacToeGame(MenuManager menuManager, String gameMode, String speler1Naam, String speler2Naam) {
        this.menuManager = menuManager;
        this.gameMode = gameMode;
        this.speler1 = new MenselijkeSpeler(speler1Naam, 'X');
        if (gameMode.equals("PVA") && speler2Naam.equals("AI")) {
            this.speler2 = new AISpeler("AI", 'O');
        } else {
            this.speler2 = new MenselijkeSpeler(speler2Naam, 'O');
        }
    }

    public void start() {
        initializeGame();
    }

    private void initializeGame() {
        turnX = true;
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
        menuButton = new JButton(lang.get("tictactoe.game.menu"));
        menuButton.addActionListener(e -> returnToMenu());
        gameFrame.add(menuButton, BorderLayout.SOUTH);
        gameFrame.setVisible(true);
        updateStatusLabel();
        if (speler1.isAI()) {
            doAiMove();
        }
    }

    private void handleButtonClick(int pos) {
        if (gameDone || !game.isFree(pos)) return;
        AbstractSpeler huidigeSpeler = turnX ? speler1 : speler2;
        if (huidigeSpeler.isAI()) return;
        char currentPlayer = huidigeSpeler.getSymbool();
        game.doMove(pos, currentPlayer);
        buttons[pos].setText(String.valueOf(currentPlayer));
        if (checkEnd(currentPlayer)) return;
        turnX = !turnX;
        updateStatusLabel();
        AbstractSpeler volgendeSpeler = turnX ? speler1 : speler2;
        if (volgendeSpeler.isAI() && !gameDone) {
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
            AbstractSpeler aiSpeler = turnX ? speler1 : speler2;
            AbstractSpeler tegenstanderSpeler = turnX ? speler2 : speler1;
            int move = MinimaxAI.bestMove(game, aiSpeler.getSymbool(), tegenstanderSpeler.getSymbool());
            if (move != -1) {
                game.doMove(move, aiSpeler.getSymbool());
                buttons[move].setText(String.valueOf(aiSpeler.getSymbool()));
            }
            if (checkEnd(aiSpeler.getSymbool())) return;
            turnX = !turnX;
            updateStatusLabel();
        });
    }

    private boolean checkEnd(char player) {
        if (game.isWin(player)) {
            AbstractSpeler winnaar = (speler1.getSymbool() == player) ? speler1 : speler2;
            statusLabel.setText(lang.get("tictactoe.game.win", winnaar.getNaam() + " (" + player + ")"));
            gameDone = true;
            return true;
        } else if (game.isDraw() || !isWinPossible()) {
            statusLabel.setText(lang.get("tictactoe.game.draw"));
            gameDone = true;
            return true;
        }
        return false;
    }

    private String getTitleForMode() {
        if (gameMode.equals("PVP")) {
            return lang.get("tictactoe.game.title.pvp");
        }
        return lang.get("tictactoe.game.title.pva");
    }

    private void updateStatusLabel() {
        if (gameDone) return;
        AbstractSpeler huidigeSpeler = turnX ? speler1 : speler2;
        statusLabel.setText(lang.get("tictactoe.game.turn", huidigeSpeler.getNaam() + " (" + huidigeSpeler.getSymbool() + ")"));
    }

    private void returnToMenu() {
        int option = JOptionPane.showConfirmDialog(gameFrame, lang.get("main.exit.confirm"), lang.get("main.exit.title"), JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            gameFrame.dispose();
            menuManager.onGameFinished();
        }
    }

    private boolean isWinPossible() {
        return isWinPossibleRecursive(!turnX);
    }

    private boolean isWinPossibleRecursive(boolean xTurn) {
        if (game.isWin('X') || game.isWin('O')) return true;
        if (game.isDraw()) return false;
        char speler = xTurn ? 'X' : 'O';
        for (int i = 0; i < 9; i++) {
            if (game.isFree(i)) {
                game.doMove(i, speler);
                boolean possible = isWinPossibleRecursive(!xTurn);
                game.undoMove(i);
                if (possible) return true;
            }
        }
        return false;
    }
}
