package tictactoe;

import framework.boardgame.AbstractBoardGame;

/**
 * TicTacToe spellogica klasse
 * Beheert het spelbord en de spelregels voor TicTacToe
 * Gebruikt het framework voor herbruikbare bordspel functionaliteit
 */
public class TicTacToe extends AbstractBoardGame {

    /**
     * Constructor - initialiseert een nieuw leeg 3x3 spelbord
     * Gebruikt de framework basis klasse voor gemeenschappelijke functionaliteit
     */
    public TicTacToe() {
        super(3, 3, ' '); // 3x3 bord, spatie voor leeg vakje
    }

    /**
     * Controleert of een speler heeft gewonnen
     * Implementeert de TicTacToe specifieke win-condities
     * @param player De speler om te controleren ('X' of 'O')
     * @return true als de speler heeft gewonnen
     */
    @Override
    public boolean isWin(char player) {
        // Winnende posities: rijen, kolommen en diagonalen
        int[][] winPositions = {
            {0,1,2}, {3,4,5}, {6,7,8}, // rijen
            {0,3,6}, {1,4,7}, {2,5,8}, // kolommen
            {0,4,8}, {2,4,6}           // diagonalen
        };
        for (int[] w : winPositions) {
            if (board[w[0]] == player && board[w[1]] == player && board[w[2]] == player) {
                return true;
            }
        }
        return false;
    }

    /**
     * Controleert of het spel gelijkspel is
     * Een gelijkspel treedt op wanneer het bord vol is en niemand gewonnen heeft
     * @return true als het bord vol is zonder winnaar
     */
    @Override
    public boolean isDraw() {
        // Controleer of er nog lege vakken zijn
        for (char c : board) {
            if (c == ' ') return false;
        }
        return !isWin('X') && !isWin('O');
    }
}