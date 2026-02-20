package reversi;

import framework.ai.AbstractReversiAI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MonteCarloTreeSearchAI extends AbstractReversiAI {
    private static final int SIMULATIONS = 1000; // Number of simulations per move
    private static final double EXPLORATION_CONSTANT = Math.sqrt(2);
    private static final Random random = new Random();


    private static class MCTSNode {
        final MCTSNode parent;
        final int row, col;          // move from parent -> this node
        final char playerToMove;     // player to move at this node
        final List<MCTSNode> children = new ArrayList<>();
        final List<int[]> untriedMoves;  // legal moves for playerToMove in this position

        int visits = 0;
        double wins = 0.0;           // reward from ROOT player's perspective

        MCTSNode(MCTSNode parent, int row, int col, char playerToMove, List<int[]> legalMoves) {
            this.parent = parent;
            this.row = row;
            this.col = col;
            this.playerToMove = playerToMove;
            this.untriedMoves = new ArrayList<>(legalMoves);
        }

        boolean isRoot() {
            return parent == null;
        }

        boolean isFullyExpanded() {
            return untriedMoves.isEmpty();
        }
    }

    /**
     * Finds the best move for the AI player using Monte Carlo Tree Search
     *
     * @param game     The current Reversi game
     * @param aiPlayer The AI player symbol ('B' or 'W')
     * @return An array [row, col] representing the best move, or null if no move available
     */
    public static int[] bestMove(Reversi game, char aiPlayer) {
        if (!game.hasValidMove(aiPlayer)) return null;

        // root: playerToMove = aiPlayer, legal moves van aiPlayer
        List<int[]> rootMoves = getValidMovesAsArrays(game, aiPlayer);
        MCTSNode root = new MCTSNode(null, -1, -1, aiPlayer, rootMoves);

        for (int i = 0; i < SIMULATIONS; i++) {
            Reversi gameCopy = copyGame(game);

            // 1) Selection (en moves toepassen op game)
            MCTSNode node = selectNodeWithState(gameCopy, root);

            // 2) Expansion (past ook 1 move toe op game)
            if (!node.untriedMoves.isEmpty()) {
                node = expand(gameCopy, node);
            }

            // 3) Simulation (rollout vanaf huidige game)
            double r = simulateRollout(gameCopy, node.playerToMove, aiPlayer);

            // 4) Backprop
            backpropagate(node, r, aiPlayer);
        }

        // kies child met meeste visits
        MCTSNode best = null;
        int bestVisits = -1;
        for (MCTSNode child : root.children) {
            if (child.visits > bestVisits) {
                bestVisits = child.visits;
                best = child;
            }
        }
        if (best == null) return null;
        return new int[]{best.row, best.col};
    }


    // Selection
    private static MCTSNode selectNodeWithState(Reversi copyGame, MCTSNode root) {
        MCTSNode node = root;

        // zolang node fully expanded is en children heeft: kies best UCT-child
        while (node.isFullyExpanded() && !node.children.isEmpty()) {
            MCTSNode parent = node;
            node = bestUctChild(node);

            // De move (node.row, node.col) werd gespeeld door parent.playerToMove
            copyGame.doMove(node.row, node.col, parent.playerToMove);
        }
        return node;
    }

    private static MCTSNode bestUctChild(MCTSNode node) {
        MCTSNode best = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (MCTSNode child : node.children) {
            double uct = uctValue(node, child);
            if (uct > bestValue) {
                bestValue = uct;
                best = child;
            }
        }
        return best;
    }

    private static double uctValue(MCTSNode parent, MCTSNode child) {
        if (child.visits == 0) return Double.POSITIVE_INFINITY;

        // wins is vanuit rootPlayer perspectief
        double winRate = child.wins / child.visits;

        double lnParent = Math.log(Math.max(1, parent.visits));
        double explore = EXPLORATION_CONSTANT * Math.sqrt(lnParent / child.visits);

        return winRate + explore;
    }


    // expansion
    private static MCTSNode expand(Reversi game, MCTSNode node) {
        if (node.untriedMoves.isEmpty()) return node;

        int idx = random.nextInt(node.untriedMoves.size());
        int[] move = node.untriedMoves.remove(idx);

        // move wordt gespeeld door node.playerToMove
        game.doMove(move[0], move[1], node.playerToMove); // Klopt dit?

        char nextPlayer = getOpponent(node.playerToMove);
        List<int[]> nextMoves = getValidMovesAsArrays(game, nextPlayer);

        // PASS handling: als next player geen moves heeft maar current wel, dan blijft playerToMove hetzelfde
        if (nextMoves.isEmpty() && game.hasValidMove(node.playerToMove)) {
            nextPlayer = node.playerToMove;
            nextMoves = getValidMovesAsArrays(game, nextPlayer);
        }

        MCTSNode child = new MCTSNode(node, move[0], move[1], nextPlayer, nextMoves);
        node.children.add(child);
        return child;
    }


    // Simulation
    private static double simulateRollout(Reversi game, char playerToMove, char rootPlayer) {
        char current = playerToMove;
        int passCount = 0;

        while (!isTerminal(game) && passCount < 2) {
            List<int[]> moves = getValidMovesAsArrays(game, current);

            if (moves.isEmpty()) {
                passCount++;
                current = getOpponent(current);
                continue;
            }

            passCount = 0;
            int[] move = moves.get(random.nextInt(moves.size()));
            game.doMove(move[0], move[1], current);
            current = getOpponent(current);
        }

        // reward vanuit rootPlayer perspectief
        return reward(game, rootPlayer);
    }

    private static boolean isTerminal(Reversi game) {
        // gebruik alleen reads
        if (game.isWin('B') || game.isWin('W') || game.isDraw()) return true;
        return !game.hasValidMove('B') && !game.hasValidMove('W');
    }

    private static double reward(Reversi game, char rootPlayer) {
        char opp = getOpponent(rootPlayer);

        if (game.isWin(rootPlayer)) return 1.0;
        if (game.isWin(opp)) return 0.0;
        if (game.isDraw()) return 0.5;

        // fallback (als jullie win/draw niet altijd terminal afvangen)
        int diff = game.count(rootPlayer) - game.count(opp);
        if (diff > 0) return 1.0;
        if (diff < 0) return 0.0;
        return 0.5;
    }

    //backpropagation
    private static void backpropagate(MCTSNode node, double reward, char rootPlayer) {
        while (node != null) {
            node.visits++;
            // Sla wins op vanuit het perspectief van node.playerToMove
            if (node.playerToMove == rootPlayer) {
                node.wins += reward;        // rootPlayer wil hoge reward
            } else {
                node.wins += (1.0 - reward); // tegenstander wil lage reward voor root
            }
            node = node.parent;
        }
    }
}
