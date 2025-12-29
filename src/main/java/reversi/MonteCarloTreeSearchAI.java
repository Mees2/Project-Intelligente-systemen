package reversi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Monte Carlo Tree Search AI implementation for Reversi.
 * Uses MCTS algorithm to find the best move by simulating random games.
 */
public class MonteCarloTreeSearchAI {
    private static final int SIMULATIONS = 1000; // Number of simulations per move
    private static final double EXPLORATION_CONSTANT = Math.sqrt(2);
    private static final Random random = new Random();

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
     * Finds the best move for the AI player using Monte Carlo Tree Search
     * @param game The current Reversi game
     * @param aiPlayer The AI player symbol ('B' or 'W')
     * @return An array [row, col] representing the best move, or null if no move available
     */
    public static int[] bestMove(Reversi game, char aiPlayer) {
        if (!game.hasValidMove(aiPlayer)) {
            return null;
        }

        MCTSNode root = new MCTSNode(-1, -1, aiPlayer, null);

        // Expand root with all valid moves
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (game.isValidMove(row, col, aiPlayer)) {
                    root.children.add(new MCTSNode(row, col, aiPlayer, root));
                }
            }
        }

        // Run simulations
        for (int i = 0; i < SIMULATIONS; i++) {
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

        char nextPlayer = (node.player == 'B') ? 'W' : 'B';

        // Add all valid moves as children
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (game.isValidMove(row, col, nextPlayer)) {
                    node.children.add(new MCTSNode(row, col, nextPlayer, node));
                }
            }
        }

        // If no valid moves for next player, check if current player can move
        if (node.children.isEmpty() && game.hasValidMove(node.player)) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (game.isValidMove(row, col, node.player)) {
                        node.children.add(new MCTSNode(row, col, node.player, node));
                    }
                }
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

        char currentPlayer = (node.player == 'B') ? 'W' : 'B';
        int passCount = 0;

        // Play random moves until game ends
        while (!game.isWin('B') && !game.isWin('W') && !game.isDraw() && passCount < 2) {
            List<int[]> validMoves = new ArrayList<>();

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (game.isValidMove(row, col, currentPlayer)) {
                        validMoves.add(new int[]{row, col});
                    }
                }
            }

            if (validMoves.isEmpty()) {
                passCount++;
                currentPlayer = (currentPlayer == 'B') ? 'W' : 'B';
                continue;
            }

            passCount = 0;
            int[] move = validMoves.get(random.nextInt(validMoves.size()));
            game.doMove(move[0], move[1], currentPlayer);
            currentPlayer = (currentPlayer == 'B') ? 'W' : 'B';
        }

        // Determine result
        if (game.isWin(aiPlayer)) {
            return 1;
        } else if (game.isDraw()) {
            return 0;
        } else {
            return -1;
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

    /**
     * Creates a deep copy of the game state
     */
    private static Reversi copyGame(Reversi original) {
        Reversi copy = new Reversi();
        for (int i = 0; i < 64; i++) {
            int row = i / 8;
            int col = i % 8;
            copy.board[i] = original.getSymbolAt(row, col);
        }
        return copy;
    }
}
