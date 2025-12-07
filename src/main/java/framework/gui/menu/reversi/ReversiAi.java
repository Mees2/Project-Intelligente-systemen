//a simple ia for reversi that just looks at what move gets the most pieces
package framework.gui.menu.reversi;

import framework.bordspel.Positie;
import reversi.Reversi;

/**
 * A simple AI player for Reversi that uses a greedy strategy.
 * The AI selects the move that captures the most opponent pieces.
 * If no valid moves are available, the AI passes its turn.
 */
public class ReversiAi {
    private static final int BOARD_SIZE = 8;
    
    /**
     * Finds and returns the best move for the AI player.
     * The best move is determined by evaluating all valid moves
     * and selecting the one that captures the most pieces.
     *
     * @param game The current Reversi game state
     * @param player The AI player's symbol ('B' or 'W')
     * @return A Positie object representing the best move,
     *         or null if no valid moves are available
     */
    public Positie findBestMove(Reversi game, char player) {
        System.out.println("DEBUG ReversiAi: Finding best move for player " + player);
        Positie bestMove = null;
        int maxPieces = -1;

        // Evaluate all possible moves
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (game.isValidMove(row, col, player)) {
                    System.out.println("DEBUG ReversiAi: Valid move at " + row + "," + col);
                    int piecesGained = simulateMove(game, row, col, player);
                    System.out.println("DEBUG ReversiAi: Pieces gained: " + piecesGained);
                    
                    if (piecesGained > maxPieces) {
                        maxPieces = piecesGained;
                        bestMove = new Positie(row, col, BOARD_SIZE);
                    }
                }
            }
        }

        System.out.println("DEBUG ReversiAi: Best move found with " + maxPieces + " pieces: " + bestMove);
        return bestMove;
    }

    /**
     * Simulates a move and counts how many opponent pieces would be captured.
     * Creates a copy of the board state, executes the move, and calculates the difference.
     *
     * @param game The current Reversi game state
     * @param row The row of the move to simulate
     * @param col The column of the move to simulate
     * @param player The player making the move ('B' or 'W')
     * @return The number of opponent pieces that would be captured
     */
    private int simulateMove(Reversi game, int row, int col, char player) {
        // Get current piece count before move
        int countBefore = game.count(player);
        
        // Create a temporary copy of the board by creating a new game
        // and manually copying the board state
        Reversi tempGame = new Reversi();
        
        // Copy all pieces from the current board to the temporary board
        char[] sourceBord = game.getBord();  // Use the getter instead of direct access
        char[] destBord = tempGame.getBord();
        
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                char cell = game.getSymboolOp(r, c);
                if (cell != ' ') {
                    // Place piece at the correct position in the board array
                    int pos = r * BOARD_SIZE + c;
                    destBord[pos] = cell;
                }
            }
        }
        
        // Execute the move on the temporary board
        tempGame.doMove(row, col, player);
        
        // Get piece count after move and calculate difference
        int countAfter = tempGame.count(player);
        return countAfter - countBefore;
    }
}
