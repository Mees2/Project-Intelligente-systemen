package framework.gui.menu.tictactoe;

import tictactoe.*;
import framework.controllers.MenuManager;
import framework.boardgame.Move;

import javax.swing.*;
import java.awt.*;
import framework.players.*;

/**
 * TicTacToe Game UI - Frontend only
 * Delegate alle game logic naar TicTacToeGameController
 * ~180 lijnen
 */
public class TicTacToeGame extends JPanel implements TicTacToeGameController.GameListener {
    private final MenuManager menuManager;
    private final String gameMode;
    private final String player1Name;
    private final String player2Name;

    private JFrame gameFrame;
    private JLabel statusLabel;
    private BoardPanel boardPanel;
    private TicTacToeGameController gameController;

    public TicTacToeGame(MenuManager menuManager, String gameMode, String player1, String player2) {
        this.menuManager = menuManager;
        this.gameMode = gameMode;
        this.player1Name = player1;
        this.player2Name = player2;
    }

    public void start() {
        TicTacToe game = new TicTacToe();
        AbstractPlayer p1, p2;

        if ("PVA".equals(gameMode)) {
            p1 = new HumanPlayer(player1Name, 'X');
            p2 = new AIPlayer(player2Name, 'O');
        } else {
            p1 = new HumanPlayer(player1Name, 'X');
            p2 = new HumanPlayer(player2Name, 'O');
        }

        gameController = new TicTacToeGameController(game, p1, p2);
        gameController.setGameListener(this);

        initializeUI();
        gameFrame.setVisible(true);
    }

    private void initializeUI() {
        gameFrame = new JFrame("TicTacToe");
        gameFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        gameFrame.setSize(500, 550);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setLayout(new BorderLayout());
        gameFrame.getContentPane().setBackground(new Color(240, 240, 240));

        // Status Label
        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        gameFrame.add(statusLabel, BorderLayout.NORTH);

        // Game Board
        boardPanel = new BoardPanel();
        gameFrame.add(boardPanel, BorderLayout.CENTER);

        // Menu Button
        JButton menuButton = new JButton("Menu");
        menuButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        menuButton.addActionListener(e -> returnToMenu());
        JPanel southPanel = new JPanel();
        southPanel.add(menuButton);
        gameFrame.add(southPanel, BorderLayout.SOUTH);

        updateStatusLabel();
    }

    @Override
    public void onMoveExecuted(Move move) {
        boardPanel.updateBoard();
    }

    @Override
    public void onGameEnded(TicTacToeGameController.GameResult result) {
        statusLabel.setText(result.getDescription());
        boardPanel.updateBoard();
    }

    @Override
    public void onStatusChanged(String status) {
        statusLabel.setText(status);
    }

    @Override
    public void onAIThinking(boolean thinking) {
        if (thinking) {
            statusLabel.setText("AI thinking...");
        } else {
            updateStatusLabel();
        }
    }

    private void updateStatusLabel() {
        statusLabel.setText(gameController.getCurrentPlayer().getName() + "'s turn");
    }

    private void returnToMenu() {
        int option = JOptionPane.showConfirmDialog(gameFrame, "Return to menu?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            gameFrame.dispose();
            menuManager.onTicTacToeGameFinished();
        }
    }

    /**
     * Inner class: Game Board UI
     */
    private class BoardPanel extends JPanel {
        private final JButton[] buttons = new JButton[9];

        public BoardPanel() {
            setLayout(new GridLayout(3, 3, 5, 5));
            setBackground(new Color(200, 200, 200));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            for (int i = 0; i < 9; i++) {
                JButton btn = new JButton();
                btn.setFont(new Font("SansSerif", Font.BOLD, 32));
                btn.setFocusPainted(false);
                btn.setBackground(Color.WHITE);
                final int index = i;
                btn.addActionListener(e -> {
                    if (!gameController.isGameDone() && !gameController.isAIThinking()) {
                        gameController.makeMove(index);
                    }
                });
                buttons[i] = btn;
                add(btn);
            }

            updateBoard();
        }

        public void updateBoard() {
            char[] board = gameController.getBoardState();
            for (int i = 0; i < 9; i++) {
                buttons[i].setText(board[i] == ' ' ? "" : String.valueOf(board[i]));
                buttons[i].setEnabled(!gameController.isGameDone() && board[i] == ' ');
            }
        }
    }
}
