package framework.ai;

import framework.boardgame.Position;
import reversi.Reversi;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for Reversi AI implementations.
 * Contains shared utility methods and constants used by different AI algorithms.
 */
public abstract class AbstractReversiAI {

    protected static final int BOARD_SIZE = 8;

    // Positional weight matrix for board evaluation
    protected static final int[][] POSITION_WEIGHTS = {
        {100, -20,  10,   5,   5,  10, -20, 100},
        {-20, -50,  -2,  -2,  -2,  -2, -50, -20},
        { 10,  -2,   5,   1,   1,   5,  -2,  10},
        {  5,  -2,   1,   0,   0,   1,  -2,   5},
        {  5,  -2,   1,   0,   0,   1,  -2,   5},
        { 10,  -2,   5,   1,   1,   5,  -2,  10},
        {-20, -50,  -2,  -2,  -2,  -2, -50, -20},
        {100, -20,  10,   5,   5,  10, -20, 100}
    };

    // Corner positions
    protected static final boolean[][] CORNERS = {
        {true, false, false, false, false, false, false, true},
        {false, false, false, false, false, false, false, false},
        {false, false, false, false, false, false, false, false},
        {false, false, false, false, false, false, false, false},
        {false, false, false, false, false, false, false, false},
        {false, false, false, false, false, false, false, false},
        {false, false, false, false, false, false, false, false},
        {true, false, false, false, false, false, false, true}
    };

    // Adjacent to corners positions
    protected static final boolean[][] ADJACENT_TO_CORNERS = {
        {false, true, false, false, false, false, true, false},
        {true, true, false, false, false, false, true, true},
        {false, false, false, false, false, false, false, false},
        {false, false, false, false, false, false, false, false},
        {false, false, false, false, false, false, false, false},
        {false, false, false, false, false, false, false, false},
        {true, true, false, false, false, false, true, true},
        {false, true, false, false, false, false, true, false}
    };

    /**
     * Gets the opponent character for a given player.
     *
     * @param player The current player ('B' or 'W')
     * @return The opponent character
     */
    protected static char getOpponent(char player) {
        return (player == 'B') ? 'W' : 'B';
    }

    /**
     * Creates a deep copy of the Reversi game state.
     *
     * @param game The game to copy
     * @return A new Reversi instance with the same board state
     */
    protected static Reversi copyGame(Reversi game) {
        Reversi copy = new Reversi();
        char[] sourceBoard = game.getBord();
        char[] destBoard = copy.getBord();

        for (int i = 0; i < sourceBoard.length; i++) {
            destBoard[i] = sourceBoard[i];
        }

        return copy;
    }

    /**
     * Gets all valid moves for a player as Position objects.
     *
     * @param game The current game state
     * @param player The player to get moves for
     * @return List of valid Position objects
     */
    protected static List<Position> getValidMovesAsPositions(Reversi game, char player) {
        List<Position> moves = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (game.isValidMove(row, col, player)) {
                    moves.add(new Position(row, col, BOARD_SIZE));
                }
            }
        }
        return moves;
    }

    /**
     * Gets all valid moves for a player as int arrays [row, col].
     *
     * @param game The current game state
     * @param player The player to get moves for
     * @return List of valid moves as int arrays
     */
    protected static List<int[]> getValidMovesAsArrays(Reversi game, char player) {
        List<int[]> moves = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (game.isValidMove(row, col, player)) {
                    moves.add(new int[]{row, col});
                }
            }
        }
        return moves;
    }

    /**
     * Checks if a cell has an empty neighbor.
     *
     * @param game The current game state
     * @param row The row to check
     * @param col The column to check
     * @return true if the cell has at least one empty neighbor
     */
    protected static boolean hasEmptyNeighbor(Reversi game, int row, int col) {
        for (int r = Math.max(0, row - 1); r <= Math.min(BOARD_SIZE - 1, row + 1); r++) {
            for (int c = Math.max(0, col - 1); c <= Math.min(BOARD_SIZE - 1, col + 1); c++) {
                if (game.getSymbolAt(r, c) == ' ') return true;
            }
        }
        return false;
    }

    /**
     * Checks if a position is a corner.
     *
     * @param row The row to check
     * @param col The column to check
     * @return true if the position is a corner
     */
    protected static boolean isCorner(int row, int col) {
        return CORNERS[row][col];
    }

    /**
     * Checks if a position is adjacent to a corner.
     *
     * @param row The row to check
     * @param col The column to check
     * @return true if the position is adjacent to a corner
     */
    protected static boolean isAdjacentToCorner(int row, int col) {
        return ADJACENT_TO_CORNERS[row][col];
    }

    /**
     * Checks if a position is on an edge of the board.
     *
     * @param row The row to check
     * @param col The column to check
     * @return true if the position is on an edge
     */
    protected static boolean isEdge(int row, int col) {
        return row == 0 || row == BOARD_SIZE - 1 || col == 0 || col == BOARD_SIZE - 1;
    }

    /**
     * Gets the positional weight for a specific board position.
     *
     * @param row The row
     * @param col The column
     * @return The positional weight value
     */
    protected static int getPositionWeight(int row, int col) {
        return POSITION_WEIGHTS[row][col];
    }

    /**
     * Evaluates the final game state (game over).
     *
     * @param game The final game state
     * @param aiPlayer The AI player symbol
     * @return The final evaluation score
     */
    protected static int evaluateGameEnd(Reversi game, char aiPlayer) {
        char opponent = getOpponent(aiPlayer);
        int aiCount = game.count(aiPlayer);
        int opponentCount = game.count(opponent);

        if (aiCount > opponentCount) {
            return 10000 + (aiCount - opponentCount); // AI wins
        } else if (aiCount < opponentCount) {
            return -10000 - (opponentCount - aiCount); // AI loses
        } else {
            return 0; // Draw
        }
    }

    /**
     * Calculates corner control score.
     *
     * @param game The current game state
     * @param aiPlayer The AI player symbol
     * @param opponent The opponent symbol
     * @return The corner control score
     */
    protected static int getCornerScore(Reversi game, char aiPlayer, char opponent) {
        int score = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (CORNERS[row][col]) {
                    char cell = game.getSymbolAt(row, col);
                    if (cell == aiPlayer) score++;
                    else if (cell == opponent) score--;
                }
            }
        }
        return score;
    }

    /**
     * Calculates positional score based on board position weights.
     *
     * @param game The current game state
     * @param aiPlayer The AI player symbol
     * @param opponent The opponent symbol
     * @return The positional score
     */
    protected static int getPositionalScore(Reversi game, char aiPlayer, char opponent) {
        int score = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                char cell = game.getSymbolAt(row, col);
                if (cell == aiPlayer) {
                    score += POSITION_WEIGHTS[row][col];
                } else if (cell == opponent) {
                    score -= POSITION_WEIGHTS[row][col];
                }
            }
        }
        return score;
    }

    /**
     * Calculates frontier disc score (discs adjacent to empty spaces).
     * Fewer frontier discs is better.
     *
     * @param game The current game state
     * @param aiPlayer The AI player symbol
     * @param opponent The opponent symbol
     * @return The frontier score (positive means AI has more frontier discs)
     */
    protected static int getFrontierScore(Reversi game, char aiPlayer, char opponent) {
        int aiF = 0;
        int opponentF = 0;

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                char cell = game.getSymbolAt(row, col);
                if (cell != ' ' && hasEmptyNeighbor(game, row, col)) {
                    if (cell == aiPlayer) aiF++;
                    else opponentF++;
                }
            }
        }

        return aiF - opponentF;
    }

    /**
     * Checks if a disc is stable (can't be flipped).
     * A disc is stable if all directions lead to an edge of the same color.
     *
     * @param game The current game state
     * @param row The row to check
     * @param col The column to check
     * @return true if the disc is stable
     */
    protected static boolean isStable(Reversi game, int row, int col) {
        char piece = game.getSymbolAt(row, col);
        if (piece == ' ') return false;

        // A piece is stable if all directions lead to an edge of same color
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}, {-1,-1}, {-1,1}, {1,-1}, {1,1}};

        for (int[] dir : directions) {
            int r = row + dir[0];
            int c = col + dir[1];

            while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE) {
                char cell = game.getSymbolAt(r, c);
                if (cell != piece) return false;
                r += dir[0];
                c += dir[1];
            }
        }
        return true;
    }

    /**
     * Calculates stability score (discs that can't be flipped).
     *
     * @param game The current game state
     * @param aiPlayer The AI player symbol
     * @param opponent The opponent symbol
     * @return The stability score (positive means AI has more stable discs)
     */
    protected static int getStabilityScore(Reversi game, char aiPlayer, char opponent) {
        int aiStable = 0;
        int opponentStable = 0;

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                char cell = game.getSymbolAt(row, col);
                if (cell != ' ' && isStable(game, row, col)) {
                    if (cell == aiPlayer) aiStable++;
                    else opponentStable++;
                }
            }
        }

        return aiStable - opponentStable;
    }
}
