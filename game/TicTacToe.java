package game;

/**
 * TicTacToe spellogica klasse
 * Beheert het spelbord en de spelregels voor TicTacToe
 */
public class TicTacToe {
    private char[] board; // 'X', 'O', of ' '

    /**
     * Constructor - initialiseert een nieuw leeg spelbord
     */
    public TicTacToe() {
        board = new char[9];
        for (int i = 0; i < 9; i++) board[i] = ' '; // Hier wordt een virtueel bord gemaakt voor de spellogica
    }

    /**
     * Geeft het huidige spelbord terug
     * @return Het spelbord als char array
     */
    public char[] getBoard() {
        return board;
    }

    /**
     * Controleert of een speler heeft gewonnen
     * @param speler De speler om te controleren ('X' of 'O')
     * @return true als de speler heeft gewonnen
     */
    public boolean isWin(char speler) {
        // Winnende posities: rijen, kolommen en diagonalen
        int[][] winPosities = {
            {0,1,2}, {3,4,5}, {6,7,8}, // rijen
            {0,3,6}, {1,4,7}, {2,5,8}, // kolommen
            {0,4,8}, {2,4,6}           // diagonalen
        };
        for (int[] w : winPosities) {
            if (board[w[0]] == speler && board[w[1]] == speler && board[w[2]] == speler) {
                return true;
            }
        }
        return false;
    }

    /**
     * Controleert of het spel gelijkspel is
     * @return true als het bord vol is zonder winnaar
     */
    public boolean isDraw() {
        // Controleer of er nog lege vakken zijn
        for (char c : board) {
            if (c == ' ') return false;
        }
        return !isWin('X') && !isWin('O');
    }

    /**
     * Voert een zet uit op het bord
     * @param pos De positie (0-8)
     * @param speler De speler ('X' of 'O')
     * @return true als de zet succesvol was
     */
    public boolean doMove(int pos, char speler) {
        if (board[pos] == ' ') {
            board[pos] = speler;
            return true;
        }
        return false;
    }

    /**
     * Maakt een zet ongedaan (gebruikt door Minimax AI)
     * @param pos De positie om leeg te maken
     */
    public void undoMove(int pos) {
        board[pos] = ' ';
    }

    /**
     * Controleert of een positie vrij is
     * @param pos De positie om te controleren (0-8)
     * @return true als de positie vrij is
     */
    public boolean isFree(int pos) {
        return board[pos] == ' ';
    }
}