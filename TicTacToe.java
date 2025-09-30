public class TicTacToe {
    private char[] board; // 'X', 'O', of ' '

    public TicTacToe() {
        board = new char[9];
        for (int i = 0; i < 9; i++) board[i] = ' '; //hier wordt eig een soort virtueel bord gemaakt. hier wordt de logica mee gedaan. de knoppen zijn er eig alleen voor de display
    }

    public char[] getBoard() { //moet ik dit documenteren? het returned het board.
        return board;
    }

    public boolean isWin(char speler) { // hieronder bepaald hij winnende posities en in de for loop checkt hij het board erop
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

    public boolean isDraw() { //checkt of het gelijkspel is door in het board opzoek te gaan naar strings met spatie
        for (char c : board) {
            if (c == ' ') return false;
        }
        return !isWin('X') && !isWin('O');
    }

    public boolean doMove(int pos, char speler) { //verandert een positie van een string met spatie naar een met een symbool ,
                                                //afhankelijk van wie om dat moment de speler is 
        if (board[pos] == ' ') {                //wordt gebruikt in de minimax functie
            board[pos] = speler;
            return true;
        }
        return false;
    }

    public void undoMove(int pos) { //verandert de waarde van een move naar een string met een spatie, wordt gebruikt in de minimax functie
        board[pos] = ' ';
    }

    public boolean isFree(int pos) { //checkt of een positie een string met een spatie is wordt gebruikt in de minimax functie
        return board[pos] == ' ';
    }
}
