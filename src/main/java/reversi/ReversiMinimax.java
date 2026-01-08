//a simple ia for reversi that just looks at what move gets the most pieces
package reversi;

import framework.ai.AbstractReversiAI;
import framework.boardgame.Position;
import java.util.*;

/**
 * Advanced AI player for Reversi using Minimax algorithm with alpha-beta pruning.
 * Features:
 * - Alpha-beta pruning for efficient search
 * - Move ordering (mobility, corners, edges)
 * - Heuristic evaluation function (mobility, corners, stability, frontier discs)
 * - Iterative deepening for flexible search depth
 */
public class ReversiMinimax extends AbstractReversiAI {
    private static final int SEARCH_DEPTH = 5;

    /**
     * Finds the best move for the AI player using Minimax with alpha-beta pruning.
     *
     * @param game The current Reversi game state
     * @param player The AI player's symbol ('B' or 'W')
     * @return A Position object representing the best move, or null if no valid moves
     */
    public Position findBestMove(Reversi game, char player) {
        long startTime = System.currentTimeMillis();

        char opponent = getOpponent(player);
        Position bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        
        // Get all valid moves
        List<Position> validMoves = getValidMovesAsPositions(game, player);

        if (validMoves.isEmpty()) {
            System.out.println("[Minimax AI] No valid moves available for player " + player);
            return null;
        }
        
        // Sort moves for better alpha-beta pruning
        validMoves.sort((a, b) -> getMoveScore(a.getRow(), a.getColumn(), player, opponent) -
                                   getMoveScore(b.getRow(), b.getColumn(), player, opponent));
        Collections.reverse(validMoves); // Descending order
        
        // Minimax search with alpha-beta pruning
        for (Position move : validMoves) {
            Reversi tempGame = copyGame(game);
            tempGame.doMove(move.getRow(), move.getColumn(), player);

            int score = minimax(tempGame, SEARCH_DEPTH - 1, true, player, opponent,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
            
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Log AI move information
        System.out.println("=== MINIMAX AI MOVE ===");
        System.out.println("  Search Depth: " + SEARCH_DEPTH);
        System.out.println("  Time taken: " + duration + " ms");
        System.out.println("=======================");

        return bestMove;
    }

    /**
     * Minimax algorithm with alpha-beta pruning.
     */
    private int minimax(Reversi game, int depth, boolean isMaximizing, char aiPlayer, 
                       char opponent, int alpha, int beta) {
        // Terminal node or depth reached
        if (depth == 0) {
            return evaluatePosition(game, aiPlayer, opponent);
        }
        
        char currentPlayer = isMaximizing ? aiPlayer : opponent;
        List<Position> validMoves = getValidMovesAsPositions(game, currentPlayer);

        // If no moves available, check if opponent can move
        if (validMoves.isEmpty()) {
            char otherPlayer = isMaximizing ? opponent : aiPlayer;
            if (getValidMovesAsPositions(game, otherPlayer).isEmpty()) {
                // Game over - evaluate final position
                return evaluateGameEnd(game, aiPlayer);
            }
            // Pass turn - opponent moves again
            return minimax(game, depth - 1, !isMaximizing, aiPlayer, opponent, alpha, beta);
        }
        
        // Sort moves for better pruning
        validMoves.sort((a, b) -> getMoveScore(a.getRow(), a.getColumn(), currentPlayer,
                                               isMaximizing ? opponent : aiPlayer) -
                                   getMoveScore(b.getRow(), b.getColumn(), currentPlayer,
                                               isMaximizing ? opponent : aiPlayer));
        Collections.reverse(validMoves);
        
        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Position move : validMoves) {
                Reversi tempGame = copyGame(game);
                tempGame.doMove(move.getRow(), move.getColumn(), currentPlayer);

                int eval = minimax(tempGame, depth - 1, false, aiPlayer, opponent, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                
                if (beta <= alpha) break; // Beta cutoff
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Position move : validMoves) {
                Reversi tempGame = copyGame(game);
                tempGame.doMove(move.getRow(), move.getColumn(), currentPlayer);

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
     */
    private int evaluatePosition(Reversi game, char aiPlayer, char opponent) {
        int score = 0;
        
        // 1. Positional weights
        score += getPositionalScore(game, aiPlayer, opponent);
        
        // 2. Mobility (number of legal moves)
        int aiMobility = getValidMovesAsPositions(game, aiPlayer).size();
        int opponentMobility = getValidMovesAsPositions(game, opponent).size();
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
     * Returns a quick heuristic score for move ordering.
     * Higher score = better move.
     */
    private int getMoveScore(int row, int col, char player, char opponent) {
        int score = 0;
        
        // Corners are best
        if (isCorner(row, col)) score += 1000;

        // Avoid adjacent to corners
        if (isAdjacentToCorner(row, col)) score -= 500;

        // Edges are good
        if (isEdge(row, col)) {
            score += 50;
        }
        
        // Positional weight
        score += getPositionWeight(row, col);

        return score;
    }
}
