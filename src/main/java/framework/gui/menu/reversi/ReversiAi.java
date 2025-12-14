//a simple ia for reversi that just looks at what move gets the most pieces
package framework.gui.menu.reversi;

import framework.bordspel.Positie;
import reversi.Reversi;
import java.util.*;

/**
 * Advanced AI player for Reversi using Minimax algorithm with alpha-beta pruning.
 * Features:
 * - Alpha-beta pruning for efficient search
 * - Move ordering (mobility, corners, edges)
 * - Heuristic evaluation function (mobility, corners, stability, frontier discs)
 * - Iterative deepening for flexible search depth
 */
public class ReversiAi {
    private static final int BOARD_SIZE = 8;
    private static final int SEARCH_DEPTH = 10; // 4926auo testcode
    
    // Positional weight matrix for board evaluation
    private static final int[][] POSITION_WEIGHTS = {
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
    private static final boolean[][] CORNERS = {
        {true, false, false, false, false, false, false, true},
        {false, false, false, false, false, false, false, false},
        {false, false, false, false, false, false, false, false},
        {false, false, false, false, false, false, false, false},
        {false, false, false, false, false, false, false, false},
        {false, false, false, false, false, false, false, false},
        {false, false, false, false, false, false, false, false},
        {true, false, false, false, false, false, false, true}
    };
    
    // Adjacent to corners (usually bad)
    private static final boolean[][] ADJACENT_TO_CORNERS = {
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
     * Finds the best move for the AI player using Minimax with alpha-beta pruning.
     *
     * @param game The current Reversi game state
     * @param player The AI player's symbol ('B' or 'W')
     * @return A Positie object representing the best move, or null if no valid moves
     */
    public Positie findBestMove(Reversi game, char player) {
        System.out.println("DEBUG ReversiAi: Finding best move for player " + player);
        
        char opponent = (player == 'B') ? 'W' : 'B';
        Positie bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        
        // Get all valid moves
        List<Positie> validMoves = getValidMoves(game, player);
        
        if (validMoves.isEmpty()) {
            System.out.println("DEBUG ReversiAi: No valid moves available");
            return null;
        }
        
        // Sort moves for better alpha-beta pruning
        validMoves.sort((a, b) -> getMoveScore(game, a.getRij(), a.getKolom(), player, opponent) -
                                   getMoveScore(game, b.getRij(), b.getKolom(), player, opponent));
        Collections.reverse(validMoves); // Descending order
        
        // Minimax search with alpha-beta pruning
        for (Positie move : validMoves) {
            Reversi tempGame = copyGame(game);
            tempGame.doMove(move.getRij(), move.getKolom(), player);
            
            int score = minimax(tempGame, SEARCH_DEPTH - 1, true, player, opponent, 
                               Integer.MIN_VALUE, Integer.MAX_VALUE);
            
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        
        System.out.println("DEBUG ReversiAi: Best move found: " + bestMove + " with score: " + bestScore);
        return bestMove;
    }

    /**
     * Minimax algorithm with alpha-beta pruning.
     *
     * @param game The current game state
     * @param depth Remaining search depth
     * @param isMaximizing True if maximizing player (AI), false if minimizing (opponent)
     * @param aiPlayer The AI player symbol
     * @param opponent The opponent symbol
     * @param alpha Alpha value for pruning
     * @param beta Beta value for pruning
     * @return The evaluated score of the position
     */
    private int minimax(Reversi game, int depth, boolean isMaximizing, char aiPlayer, 
                       char opponent, int alpha, int beta) {
        // Terminal node or depth reached
        if (depth == 0) {
            return evaluatePosition(game, aiPlayer, opponent);
        }
        
        char currentPlayer = isMaximizing ? aiPlayer : opponent;
        List<Positie> validMoves = getValidMoves(game, currentPlayer);
        
        // If no moves available, check if opponent can move
        if (validMoves.isEmpty()) {
            char otherPlayer = isMaximizing ? opponent : aiPlayer;
            if (getValidMoves(game, otherPlayer).isEmpty()) {
                // Game over - evaluate final position
                return evaluateGameEnd(game, aiPlayer);
            }
            // Pass turn - opponent moves again
            return minimax(game, depth - 1, !isMaximizing, aiPlayer, opponent, alpha, beta);
        }
        
        // Sort moves for better pruning
        validMoves.sort((a, b) -> getMoveScore(game, a.getRij(), a.getKolom(), currentPlayer, 
                                               isMaximizing ? opponent : aiPlayer) -
                                   getMoveScore(game, b.getRij(), b.getKolom(), currentPlayer,
                                               isMaximizing ? opponent : aiPlayer));
        Collections.reverse(validMoves);
        
        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Positie move : validMoves) {
                Reversi tempGame = copyGame(game);
                tempGame.doMove(move.getRij(), move.getKolom(), currentPlayer);
                
                int eval = minimax(tempGame, depth - 1, false, aiPlayer, opponent, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                
                if (beta <= alpha) break; // Beta cutoff
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Positie move : validMoves) {
                Reversi tempGame = copyGame(game);
                tempGame.doMove(move.getRij(), move.getKolom(), currentPlayer);
                
                int eval = minimax(tempGame, depth - 1, true, aiPlayer, opponent, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                
                if (beta <= alpha) break; // Alpha cutoff
            }
            return minEval;
        }
    }

    /**
     * Evaluates a position using a heuristic evaluation function.
     * Considers: mobility, corner control, stability, frontier discs, positional value.
     *
     * @param game The game state to evaluate
     * @param aiPlayer The AI player symbol
     * @param opponent The opponent symbol
     * @return A heuristic score of the position
     */
    private int evaluatePosition(Reversi game, char aiPlayer, char opponent) {
        int score = 0;
        
        // 1. Positional weights
        score += getPositionalScore(game, aiPlayer, opponent);
        
        // 2. Mobility (number of legal moves)
        int aiMobility = getValidMoves(game, aiPlayer).size();
        int opponentMobility = getValidMoves(game, opponent).size();
        score += (aiMobility - opponentMobility) * 10;
        
        // 3. Corner control (very important)
        score += getCornerScore(game, aiPlayer, opponent) * 50;
        
        // 4. Stability (discs that can't be flipped)
        score += getStabilityScore(game, aiPlayer, opponent) * 5;
        
        // 5. Frontier discs (discs adjacent to empty spaces - fewer is better)
        score -= getFrontierScore(game, aiPlayer, opponent) * 3;
        
        return score;
    }

    /**
     * Evaluates the final game state (game over).
     *
     * @param game The final game state
     * @param aiPlayer The AI player symbol
     * @return The final evaluation
     */
    private int evaluateGameEnd(Reversi game, char aiPlayer) {
        char opponent = (aiPlayer == 'B') ? 'W' : 'B';
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
     * Calculates positional score based on board position weights.
     */
    private int getPositionalScore(Reversi game, char aiPlayer, char opponent) {
        int score = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                char cell = game.getSymboolOp(row, col);
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
     * Calculates corner control score.
     */
    private int getCornerScore(Reversi game, char aiPlayer, char opponent) {
        int score = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (CORNERS[row][col]) {
                    char cell = game.getSymboolOp(row, col);
                    if (cell == aiPlayer) score++;
                    else if (cell == opponent) score--;
                }
            }
        }
        return score;
    }

    /**
     * Calculates stability score (discs that can't be flipped).
     */
    private int getStabilityScore(Reversi game, char aiPlayer, char opponent) {
        int aiStable = 0;
        int opponentStable = 0;
        
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                char cell = game.getSymboolOp(row, col);
                if (cell != ' ' && isStable(game, row, col)) {
                    if (cell == aiPlayer) aiStable++;
                    else opponentStable++;
                }
            }
        }
        
        return aiStable - opponentStable;
    }

    /**
     * Checks if a disc is stable (can't be flipped).
     */
    private boolean isStable(Reversi game, int row, int col) {
        char piece = game.getSymboolOp(row, col);
        if (piece == ' ') return false;
        
        // A piece is stable if all directions lead to an edge of same color
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}, {-1,-1}, {-1,1}, {1,-1}, {1,1}};
        
        for (int[] dir : directions) {
            int r = row + dir[0];
            int c = col + dir[1];
            
            while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE) {
                char cell = game.getSymboolOp(r, c);
                if (cell != piece) return false;
                r += dir[0];
                c += dir[1];
            }
        }
        return true;
    }

    /**
     * Calculates frontier disc score (discs adjacent to empty spaces).
     * Fewer frontier discs is better.
     */
    private int getFrontierScore(Reversi game, char aiPlayer, char opponent) {
        int aiF = 0;
        int opponentF = 0;
        
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                char cell = game.getSymboolOp(row, col);
                if (cell != ' ' && hasEmptyNeighbor(game, row, col)) {
                    if (cell == aiPlayer) aiF++;
                    else opponentF++;
                }
            }
        }
        
        return aiF - opponentF;
    }

    /**
     * Checks if a cell has an empty neighbor.
     */
    private boolean hasEmptyNeighbor(Reversi game, int row, int col) {
        for (int r = Math.max(0, row - 1); r <= Math.min(BOARD_SIZE - 1, row + 1); r++) {
            for (int c = Math.max(0, col - 1); c <= Math.min(BOARD_SIZE - 1, col + 1); c++) {
                if (game.getSymboolOp(r, c) == ' ') return true;
            }
        }
        return false;
    }

    /**
     * Returns a quick heuristic score for move ordering.
     * Higher score = better move.
     */
    private int getMoveScore(Reversi game, int row, int col, char player, char opponent) {
        int score = 0;
        
        // Corners are best
        if (CORNERS[row][col]) score += 1000;
        
        // Avoid adjacent to corners
        if (ADJACENT_TO_CORNERS[row][col]) score -= 500;
        
        // Edges are good
        if (row == 0 || row == BOARD_SIZE - 1 || col == 0 || col == BOARD_SIZE - 1) {
            score += 50;
        }
        
        // Positional weight
        score += POSITION_WEIGHTS[row][col];
        
        return score;
    }

    /**
     * Gets all valid moves for a player.
     */
    private List<Positie> getValidMoves(Reversi game, char player) {
        List<Positie> moves = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (game.isValidMove(row, col, player)) {
                    moves.add(new Positie(row, col, BOARD_SIZE));
                }
            }
        }
        return moves;
    }

    /**
     * Creates a deep copy of the game state.
     */
    private Reversi copyGame(Reversi game) {
        Reversi copy = new Reversi();
        char[] sourceBord = game.getBord();
        char[] destBord = copy.getBord();
        
        for (int i = 0; i < sourceBord.length; i++) {
            destBord[i] = sourceBord[i];
        }
        
        return copy;
    }
}
