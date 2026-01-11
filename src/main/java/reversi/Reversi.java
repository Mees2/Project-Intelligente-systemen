package reversi;

import framework.boardgame.AbstractBoardGame;
import framework.boardgame.Move;
import framework.boardgame.Position;

/**
 * Implements the game logic for Reversi (also known as Othello).
 * Uses Move and Position classes from the framework.
 */
public class Reversi extends AbstractBoardGame {
    
    /**
     * Creates a new Reversi game with an 8x8 board.
     * Initializes the board with the standard starting position:
     * two black and two white pieces in the center, diagonally arranged.
     */
    public Reversi() {
        super(8, 8, ' ');
        // Standard starting position
        board[27] = 'W'; // (3,3)
        board[28] = 'B'; // (3,4)
        board[35] = 'B'; // (4,3)
        board[36] = 'W'; // (4,4)
    }

    /**
     * Determines if the specified player has won the game.
     * A player wins if they have more pieces than their opponent when no more moves are possible.
     *
     * @param player The player to check for winning ('B' or 'W')
     * @return true if the specified player has won, false otherwise
     */
    @Override
    public boolean isWin(char player) {
        if (!hasValidMove('B') && !hasValidMove('W')) {
            int b = count('B'), w = count('W');
            return (player == 'B' && b > w) || (player == 'W' && w > b);
        }
        return false;
    }

    /**
     * Determines if the game has ended in a draw.
     * A draw occurs when no more moves are possible and both players have the same number of pieces.
     *
     * @return true if the game is a draw, false otherwise
     */
    @Override
    public boolean isDraw() {
        if (!hasValidMove('B') && !hasValidMove('W')) {
            int b = count('B'), w = count('W');
            return b == w;
        }
        return false;
    }

    /**
     * Checks if a move is valid using Position object
     */
    public boolean isValidMove(Position pos, char player) {
        return isValidMove(pos.getRow(), pos.getColumn(), player);
    }

    /**
     * Checks if a move is valid for the specified player.
     * A move is valid if:
     * - The target cell is empty
     * - The move would flip at least one opponent's piece
     *
     * @param row The row where the piece would be placed (0-7)
     * @param col The column where the piece would be placed (0-7)
     * @param player The player making the move ('B' or 'W')
     * @return true if the move is valid, false otherwise
     */
    public boolean isValidMove(int row, int col, char player) {
        if (getSymbolAt(row, col) != emptySymbol) return false;
        char opponent = (player == 'B') ? 'W' : 'B';
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int r = row + dr, c = col + dc, count = 0;
                while (r >= 0 && r < 8 && c >= 0 && c < 8 && getSymbolAt(r, c) == opponent) {
                    r += dr; c += dc; count++;
                }
                if (count > 0 && r >= 0 && r < 8 && c >= 0 && c < 8 && getSymbolAt(r, c) == player)
                    return true;
            }
        }
        return false;
    }

    /**
     * Executes a move using Move object
     */
    @Override
    public void doMove(Move move) {
        Position pos = move.getPosition();
        doMove(pos.getRow(), pos.getColumn(), move.getPlayer());
    }

    /**
     * Executes a move for the specified player.
     * Places a piece at the specified position and flips all captured opponent pieces.
     * Assumes the move has already been validated.
     *
     * @param row The row where the piece is placed (0-7)
     * @param col The column where the piece is placed (0-7)
     * @param player The player making the move ('B' or 'W')
     */
    public void doMove(int row, int col, char player) {
        int pos = row * 8 + col;
        board[pos] = player;
        char opponent = (player == 'B') ? 'W' : 'B';
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int r = row + dr, c = col + dc, count = 0;
                while (r >= 0 && r < 8 && c >= 0 && c < 8 && getSymbolAt(r, c) == opponent) {
                    r += dr; c += dc; count++;
                }
                if (count > 0 && r >= 0 && r < 8 && c >= 0 && c < 8 && getSymbolAt(r, c) == player) {
                    int rr = row + dr, cc = col + dc;
                    while (rr != r || cc != c) {
                        board[rr * 8 + cc] = player;
                        rr += dr; cc += dc;
                    }
                }
            }
        }
        updateStatus();
    }

    /**
     * Checks if the specified player has any valid moves available.
     *
     * @param player The player to check for valid moves ('B' or 'W')
     * @return true if the player has at least one valid move, false otherwise
     */
    public boolean hasValidMove(char player) {
        for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++)
                if (isValidMove(row, col, player)) return true;
        return false;
    }

    /**
     * Counts the number of pieces belonging to the specified player.
     *
     * @param player The player whose pieces to count ('B' or 'W')
     * @return The number of pieces the player has on the board
     */
    public int count(char player) {
        int cnt = 0;
        for (char c : board)
            if (c == player) cnt++;
        return cnt;
    }
}
