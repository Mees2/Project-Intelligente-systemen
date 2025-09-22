public class TicTacToe {
    private char[] board; // 'X', 'O', of ' '

    public TicTacToe() {
        board = new char[9];
        for (int i = 0; i < 9; i++) board[i] = ' ';
    }

    public char[] getBoard() {
        return board;
    }

    public boolean isWin(char speler) {
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

    public boolean isDraw() {
        for (char c : board) {
            if (c == ' ') return false;
        }
        return !isWin('X') && !isWin('O');
    }

    public boolean doMove(int pos, char speler) {
        if (board[pos] == ' ') {
            board[pos] = speler;
            return true;
        }
        return false;
    }

    public void undoMove(int pos) {
        board[pos] = ' ';
    }

    public boolean isFree(int pos) {
        return board[pos] == ' ';
    }
}
