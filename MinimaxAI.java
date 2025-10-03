public class MinimaxAI {

    public static int bestMove(TicTacToe game, char aiPlayer, char humanPlayer) {
        int bestScore = Integer.MIN_VALUE;
        int move = -1;

        for (int i = 0; i < 9; i++) {
            if (game.isFree(i)) {
                game.doMove(i, aiPlayer);
                int score = minimax(game, false, aiPlayer, humanPlayer);
                game.undoMove(i);

                if (score > bestScore) {
                    bestScore = score;
                    move = i;
                }
            }
        }
        return move;
    }

    private static int minimax(TicTacToe game, boolean isMax, char aiPlayer, char humanPlayer) {
        if (game.isWin(aiPlayer)) return 10;
        if (game.isWin(humanPlayer)) return -10;
        if (game.isDraw()) return 0;

        if (isMax) {
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (game.isFree(i)) {
                    game.doMove(i, aiPlayer);
                    best = Math.max(best, minimax(game, false, aiPlayer, humanPlayer));
                    game.undoMove(i);
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (game.isFree(i)) {
                    game.doMove(i, humanPlayer);
                    best = Math.min(best, minimax(game, true, aiPlayer, humanPlayer));
                    game.undoMove(i);
                }
            }
            return best;
        }
    }
}