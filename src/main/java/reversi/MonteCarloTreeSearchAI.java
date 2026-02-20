package reversi;

import framework.ai.AbstractReversiAI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MonteCarloTreeSearchAI extends AbstractReversiAI {
    private static final int SIMULATIONS = 5000; // Number of simulations per move
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

    //bestMove is de enige public methode, deze wordt aangeroepen door de GameController--
    public int[] bestMove(Reversi game, char aiPlayer) {
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

            // Log elke 100 simulaties
            if ((i + 1) % 100 == 0) {
                System.out.printf("Sim %d: depth=%d, nodes=%d%n",
                        i + 1, getTreeDepth(root), getTotalNodes(root));
            }

            // 4) Backprop
            backpropagate(node, r);
        }

        // Eindresultaat loggen
        System.out.printf("Final: depth=%d, nodes=%d%n", getTreeDepth(root), getTotalNodes(root));


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
    private MCTSNode selectNodeWithState(Reversi copyGame, MCTSNode root) {
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

    private MCTSNode bestUctChild(MCTSNode node) {
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

    private double uctValue(MCTSNode parent, MCTSNode child) {
        if (child.visits == 0) return Double.POSITIVE_INFINITY;

        // wins is vanuit rootPlayer perspectief
        double winRate = child.wins / child.visits;

        double lnParent = Math.log(Math.max(1, parent.visits));
        double explore = EXPLORATION_CONSTANT * Math.sqrt(lnParent / child.visits);

        return winRate + explore;
    }


    // expansion
    private MCTSNode expand(Reversi gameCopy, MCTSNode node) {
        if (node.untriedMoves.isEmpty()) return node;

        int idx = random.nextInt(node.untriedMoves.size());
        int[] move = node.untriedMoves.remove(idx);

        // move wordt gespeeld door node.playerToMove
        gameCopy.doMove(move[0], move[1], node.playerToMove);

        char nextPlayer = getOpponent(node.playerToMove);
        List<int[]> nextMoves = getValidMovesAsArrays(gameCopy, nextPlayer);

        // PASS handling: als next player geen moves heeft maar current wel, dan blijft playerToMove hetzelfde
        if (nextMoves.isEmpty() && gameCopy.hasValidMove(node.playerToMove)) {
            nextPlayer = node.playerToMove;
            nextMoves = getValidMovesAsArrays(gameCopy, nextPlayer);
        }

        MCTSNode child = new MCTSNode(node, move[0], move[1], nextPlayer, nextMoves);
        node.children.add(child);
        return child;
    }


    // Simulation
    private double simulateRollout(Reversi gameCopy, char playerToMove, char rootPlayer) {
        char current = playerToMove;
        int passCount = 0;

        while (!isTerminal(gameCopy) && passCount < 2) {
            List<int[]> moves = getValidMovesAsArrays(gameCopy, current);

            if (moves.isEmpty()) {
                passCount++;
                current = getOpponent(current);
                continue;
            }

            passCount = 0;
            int[] move = moves.get(random.nextInt(moves.size()));
            gameCopy.doMove(move[0], move[1], current);
            current = getOpponent(current);
        }

        // reward vanuit rootPlayer perspectief
        return reward(gameCopy, rootPlayer);
    }

    private boolean isTerminal(Reversi gameCopy) {
        // gebruik alleen reads
        if (gameCopy.isWin('B') || gameCopy.isWin('W') || gameCopy.isDraw()) return true;
        return !gameCopy.hasValidMove('B') && !gameCopy.hasValidMove('W');
    }

    private double reward(Reversi gameCopy, char rootPlayer) {
        char opp = getOpponent(rootPlayer);

        if (gameCopy.isWin(rootPlayer)) return 1.0;
        if (gameCopy.isWin(opp)) return 0.0;
        if (gameCopy.isDraw()) return 0.5;

        // fallback (als jullie win/draw niet altijd terminal afvangen)
        int diff = gameCopy.count(rootPlayer) - gameCopy.count(opp);
        if (diff > 0) return 1.0;
        if (diff < 0) return 0.0;
        return 0.5;
    }

    //backpropagation
    private void backpropagate(MCTSNode node, double aiReward) {
        while (node != null) {
            node.visits++;
            // We tellen altijd gwn de reward voor de AI op, ongeacht wie er aan de beurt is.
            // Dit lossen we vervolgens wiskundig op in de uctValue functie.
            node.wins += aiReward;
            node = node.parent;
        }
    }

    // Voeg toe aan de klasse
    private int getTreeDepth(MCTSNode root) {
        if (root.children.isEmpty()) return 0;
        int maxDepth = 0;
        for (MCTSNode child : root.children) {
            maxDepth = Math.max(maxDepth, getTreeDepth(child));
        }
        return 1 + maxDepth;
    }

    private int getTotalNodes(MCTSNode root) {
        int count = 1;
        for (MCTSNode child : root.children) {
            count += getTotalNodes(child);
        }
        return count;
    }
}
