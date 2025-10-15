package reversi;

import framework.bordspel.AbstractBordSpel;

public class Reversi extends AbstractBordSpel {
    public Reversi() {
        super(8, 8, ' ');
        // Startpositie
        bord[27] = 'W'; // (3,3)
        bord[28] = 'B'; // (3,4)
        bord[35] = 'B'; // (4,3)
        bord[36] = 'W'; // (4,4)
    }

    @Override
    public boolean isWin(char speler) {
        if (!hasValidMove('B') && !hasValidMove('W')) {
            int b = count('B'), w = count('W');
            return (speler == 'B' && b > w) || (speler == 'W' && w > b);
        }
        return false;
    }

    @Override
    public boolean isDraw() {
        if (!hasValidMove('B') && !hasValidMove('W')) {
            int b = count('B'), w = count('W');
            return b == w;
        }
        return false;
    }

    public boolean isValidMove(int row, int col, char player) {
        if (getSymboolOp(row, col) != leegSymbool) return false;
        char opponent = (player == 'B') ? 'W' : 'B';
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int r = row + dr, c = col + dc, count = 0;
                while (r >= 0 && r < 8 && c >= 0 && c < 8 && getSymboolOp(r, c) == opponent) {
                    r += dr; c += dc; count++;
                }
                if (count > 0 && r >= 0 && r < 8 && c >= 0 && c < 8 && getSymboolOp(r, c) == player)
                    return true;
            }
        }
        return false;
    }

    public void doMove(int row, int col, char player) {
        int pos = row * 8 + col;
        bord[pos] = player;
        char opponent = (player == 'B') ? 'W' : 'B';
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int r = row + dr, c = col + dc, count = 0;
                while (r >= 0 && r < 8 && c >= 0 && c < 8 && getSymboolOp(r, c) == opponent) {
                    r += dr; c += dc; count++;
                }
                if (count > 0 && r >= 0 && r < 8 && c >= 0 && c < 8 && getSymboolOp(r, c) == player) {
                    int rr = row + dr, cc = col + dc;
                    while (rr != r || cc != c) {
                        bord[rr * 8 + cc] = player;
                        rr += dr; cc += dc;
                    }
                }
            }
        }
        updateStatus();
    }

    public boolean hasValidMove(char player) {
        for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++)
                if (isValidMove(row, col, player)) return true;
        return false;
    }

    public int count(char player) {
        int cnt = 0;
        for (char c : bord)
            if (c == player) cnt++;
        return cnt;
    }
}
