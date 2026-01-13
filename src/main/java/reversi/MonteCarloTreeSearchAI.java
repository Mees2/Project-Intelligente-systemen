package reversi;

import framework.ai.AbstractReversiAI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Monte Carlo Tree Search AI implementation for Reversi.
 * Uses MCTS algorithm to find the best move by simulating random games.
 */
public class MonteCarloTreeSearchAI extends AbstractReversiAI {
    private static final int DEFAULT_SIMULATIONS = 1000;
    private int simulations = DEFAULT_SIMULATIONS; // Number of simulations per move
    private static final double EXPLORATION_CONSTANT = Math.sqrt(2);
    private static final Random random = new Random();

    /**
     * Set the number of simulations for MCTS
     * @param sims Number of simulations (recommended: 100-100000)
     */
    public void setSimulations(int sims) {
        this.simulations = Math.max(100, Math.min(sims, 100000)); // Clamp between 100-100000
    }

    /**
     * Get current number of simulations
     */
    public int getSimulations() {
        return simulations;
    }

    /**
     * Represents a node in the Monte Carlo search tree
     */
    private static class MCTSNode {
        int row;
        int col;
        char player;
        int wins = 0;
        int visits = 0;
        MCTSNode parent;
        List<MCTSNode> children = new ArrayList<>();

        MCTSNode(int row, int col, char player, MCTSNode parent) {
            this.row = row;
            this.col = col;
            this.player = player;
            this.parent = parent;
        }

        /**
         * Calculates the UCB1 (Upper Confidence Bound) value for this node
         */
        double getUCB1() {
            if (visits == 0) return Double.MAX_VALUE;
            return (double) wins / visits +
                   EXPLORATION_CONSTANT * Math.sqrt(Math.log(parent.visits) / visits);
        }
    }

    /**
     * Static wrapper for backwards compatibility - uses default simulations
     */
    public static int[] bestMove(Reversi game, char aiPlayer) {
        MonteCarloTreeSearchAI ai = new MonteCarloTreeSearchAI();
        return ai.findBestMove(game, aiPlayer);
    }

    /**
     * Finds the best move for the AI player using Monte Carlo Tree Search
     * @param game The current Reversi game
     * @param aiPlayer The AI player symbol ('B' or 'W')
     * @return An array [row, col] representing the best move, or null if no move available
     */
    public int[] findBestMove(Reversi game, char aiPlayer) {
        long startTime = System.currentTimeMillis();

        if (!game.hasValidMove(aiPlayer)) {
            System.out.println("[MCTS AI] No valid moves available for player " + aiPlayer);
            return null;
        }

        MCTSNode root = new MCTSNode(-1, -1, aiPlayer, null);

        // Expand root with all valid moves
        List<int[]> validMoves = getValidMovesAsArrays(game, aiPlayer);
        for (int[] move : validMoves) {
            root.children.add(new MCTSNode(move[0], move[1], aiPlayer, root));
        }

        // Run simulations
        for (int i = 0; i < simulations; i++) {
            Reversi gameCopy = copyGame(game);
            MCTSNode node = selectNode(root);

            if (node.visits > 0 && !isTerminal(gameCopy, node)) {
                node = expandNode(gameCopy, node);
            }

            int result = simulate(gameCopy, node, aiPlayer);
            backpropagate(node, result);
        }

        // Select the move with the highest visit count
        MCTSNode bestNode = null;
        int maxVisits = -1;
        for (MCTSNode child : root.children) {
            if (child.visits > maxVisits) {
                maxVisits = child.visits;
                bestNode = child;
            }
        }

        if (bestNode == null) {
            return null;
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Log AI move information
        System.out.println("=== MCTS AI MOVE ===");
        System.out.println("  Simulations: " + simulations);
        System.out.println("  Time taken: " + duration + " ms");
        System.out.println("====================");

        return new int[]{bestNode.row, bestNode.col};
    }

    /**
     * Selects the most promising node to explore using UCB1
     */
    private static MCTSNode selectNode(MCTSNode node) {
        while (!node.children.isEmpty()) {
            MCTSNode best = null;
            double bestValue = -1;

            for (MCTSNode child : node.children) {
                double value = child.getUCB1();
                if (value > bestValue) {
                    bestValue = value;
                    best = child;
                }
            }
            node = best;
        }
        return node;
    }

    /**
     * Expands a node by adding its children (valid moves)
     */
    private static MCTSNode expandNode(Reversi game, MCTSNode node) {
        // Apply the move for this node
        if (node.row != -1 && node.col != -1) {
            game.doMove(node.row, node.col, node.player);
        }

        char nextPlayer = getOpponent(node.player);

        // Add all valid moves as children
        List<int[]> validMoves = getValidMovesAsArrays(game, nextPlayer);
        for (int[] move : validMoves) {
            node.children.add(new MCTSNode(move[0], move[1], nextPlayer, node));
        }

        // If no valid moves for next player, check if current player can move
        if (node.children.isEmpty() && game.hasValidMove(node.player)) {
            List<int[]> currentPlayerMoves = getValidMovesAsArrays(game, node.player);
            for (int[] move : currentPlayerMoves) {
                node.children.add(new MCTSNode(move[0], move[1], node.player, node));
            }
        }

        if (!node.children.isEmpty()) {
            return node.children.get(random.nextInt(node.children.size()));
        }
        return node;
    }

    /**
     * Simulates a random game from the current position
     * @return 1 if AI wins, 0 if draw, -1 if AI loses
     */
    private static int simulate(Reversi game, MCTSNode node, char aiPlayer) {
        // Apply the node's move
        if (node.row != -1 && node.col != -1) {
            game.doMove(node.row, node.col, node.player);
        }

        char currentPlayer = getOpponent(node.player);
        int passCount = 0;

        // Play random moves until game ends
        while (!game.isWin('B') && !game.isWin('W') && !game.isDraw() && passCount < 2) {
            List<int[]> validMoves = getValidMovesAsArrays(game, currentPlayer);

            if (validMoves.isEmpty()) {
                passCount++;
                currentPlayer = getOpponent(currentPlayer);
                continue;
            }

            passCount = 0;
            int[] move = validMoves.get(random.nextInt(validMoves.size()));
            game.doMove(move[0], move[1], currentPlayer);
            currentPlayer = getOpponent(currentPlayer);
        }

        // Determine result using stability-aware evaluation
        return evaluateSimulationResult(game, aiPlayer);
    }

    /**
     * Evaluates the simulation result considering stability and piece count.
     * Uses getStabilityScore from AbstractReversiAI for a more nuanced evaluation.
     *
     * @param game The final game state
     * @param aiPlayer The AI player symbol
     * @return 1 if AI wins, 0 if draw, -1 if AI loses
     */
    private static int evaluateSimulationResult(Reversi game, char aiPlayer) {
        char opponent = getOpponent(aiPlayer);

        // First check for clear win/loss/draw
        if (game.isWin(aiPlayer)) {
            return 1;
        } else if (game.isWin(opponent)) {
            return -1;
        } else if (game.isDraw()) {
            return 0;
        }

        // If game ended due to no valid moves, use piece count and stability
        int aiCount = game.count(aiPlayer);
        int opponentCount = game.count(opponent);
        int stabilityScore = getStabilityScore(game, aiPlayer, opponent);

        // Combine piece count advantage with stability advantage
        int totalAdvantage = (aiCount - opponentCount) + (stabilityScore / 2);

        if (totalAdvantage > 0) {
            return 1;
        } else if (totalAdvantage < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * Backpropagates the simulation result up the tree
     */
    private static void backpropagate(MCTSNode node, int result) {
        while (node != null) {
            node.visits++;
            if (result == 1) {
                node.wins++;
            } else if (result == 0) {
                node.wins += 0.5; // Half point for draw
            }
            node = node.parent;
        }
    }

    /**
     * Checks if a game state is terminal
     */
    private static boolean isTerminal(Reversi game, MCTSNode node) {
        if (node.row != -1 && node.col != -1) {
            game.doMove(node.row, node.col, node.player);
        }
        return game.isWin('B') || game.isWin('W') || game.isDraw() ||
               (!game.hasValidMove('B') && !game.hasValidMove('W'));
    }
}
