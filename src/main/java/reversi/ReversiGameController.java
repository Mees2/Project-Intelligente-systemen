package reversi;

import framework.boardgame.Move;
import framework.boardgame.Position;
import framework.players.AbstractPlayer;

/**
 * Reversi Game Controller - Backend game logic only
 * Handelt alle Reversi-specifieke spelregels af (passes, flips, etc)
 * Volledig losgekoppeld van UI
 */
public class ReversiGameController {
    private final Reversi game;
    private final AbstractPlayer player1;
    private final AbstractPlayer player2;
    private AbstractPlayer currentPlayer;
    private boolean gameDone = false;
    private boolean lastMoveWasPass = false;
    private GameListener gameListener;
    private volatile boolean aiThinking = false;
    private final ReversiMinimax minimaxAI;
    private final MonteCarloTreeSearchAI mctsAI;
    private boolean useMCTS = false;

    /**
     * Listener interface voor UI updates
     */
    public interface GameListener {
        void onMoveExecuted(Move move);
        void onGameEnded(GameResult result);
        void onStatusChanged(String status);
        void onAIThinking(boolean thinking);
    }

    public ReversiGameController(Reversi game, AbstractPlayer player1, AbstractPlayer player2,
                                ReversiMinimax minimaxAI, MonteCarloTreeSearchAI mctsAI) {
        this.game = game;
        this.player1 = player1;
        this.player2 = player2;
        this.minimaxAI = minimaxAI;
        this.mctsAI = mctsAI;
        this.currentPlayer = player1;
    }

    public void setGameListener(GameListener listener) {
        this.gameListener = listener;
    }

    public void setUseMCTS(boolean use) {
        this.useMCTS = use;
    }

    /**
     * Voer een zet uit met rij/kolom coördinaten
     */
    public boolean makeMove(int row, int col) {
        if (gameDone || aiThinking) return false;

        if (!game.isValidMove(row, col, currentPlayer.getSymbol())) {
            return false;
        }

        Move move = new Move(row * 8 + col, currentPlayer.getSymbol(), 8);
        game.doMove(move);

        if (gameListener != null) {
            gameListener.onMoveExecuted(move);
        }

        lastMoveWasPass = false;

        if (checkGameEnd()) {
            return true;
        }

        // SWITCH PLAYER FIRST
        switchPlayer();

        // CHECK IF CURRENT PLAYER HAS LEGAL MOVES
        if (!hasLegalMoves(currentPlayer.getSymbol())) {
            // CURRENT PLAYER HAS NO LEGAL MOVES - PASS TO OPPONENT
            lastMoveWasPass = true;
            notifyStatusChanged("Pass! " + currentPlayer.getName() + " has no legal moves. " 
                              + getOpponentPlayer().getName() + "'s turn");

            // TRIGGER UI UPDATE
            if (gameListener != null) {
                gameListener.onMoveExecuted(null);
            }

            switchPlayer();

            // CHECK IF OPPONENT ALSO HAS NO LEGAL MOVES - GAME OVER
            if (!hasLegalMoves(currentPlayer.getSymbol())) {
                gameDone = true;
                if (gameListener != null) {
                    gameListener.onGameEnded(determineWinner());
                }
                return true;
            }

            notifyStatusChanged(currentPlayer.getName() + "'s turn");

            if (gameListener != null) {
                gameListener.onMoveExecuted(null);
            }

            if (currentPlayer.isAI()) {
                makeAIMove();
            }

            return true;
        }

        // CURRENT PLAYER HAS LEGAL MOVES - CONTINUE NORMALLY
        notifyStatusChanged();

        if (currentPlayer.isAI()) {
            makeAIMove();
        }

        return true;
    }

    /**
     * Check if a player has any legal moves available
     */
    private boolean hasLegalMoves(char player) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (game.isValidMove(row, col, player)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get the opponent player
     */
    private AbstractPlayer getOpponentPlayer() {
        return (currentPlayer == player1) ? player2 : player1;
    }

    /**
     * Maak een AI zet
     */
    public void makeAIMove() {
        if (aiThinking || !currentPlayer.isAI() || gameDone) {
            return;
        }

        aiThinking = true;
        if (gameListener != null) {
            gameListener.onAIThinking(true);
        }

        Position bestMove;
        if (useMCTS) {
            int[] moveArray = MonteCarloTreeSearchAI.bestMove(game, currentPlayer.getSymbol());
            bestMove = (moveArray == null) ? null : new Position(moveArray[0], moveArray[1], 8);
        } else {
            bestMove = minimaxAI.findBestMove(game, currentPlayer.getSymbol());
        }

        aiThinking = false;
        if (gameListener != null) {
            gameListener.onAIThinking(false);
        }

        if (bestMove != null) {
            makeMove(bestMove.getRow(), bestMove.getColumn());
        }
    }

    /**
     * Wissel van speler
     */
    private void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    /**
     * Bepaal de winnaar
     * De speler met de meeste schijven wint
     */
    private GameResult determineWinner() {
        int p1Count = game.count(player1.getSymbol());
        int p2Count = game.count(player2.getSymbol());

        if (p1Count > p2Count) {
            return GameResult.createWin(player1);
        } else if (p2Count > p1Count) {
            return GameResult.createWin(player2);
        } else {
            return GameResult.createDraw();
        }
    }

    /**
     * Controleer of het spel voorbij is
     * Spel eindigt als het bord vol is
     */
    private boolean checkGameEnd() {
        // Controleer of het bord vol is
        for (char c : game.getBord()) {
            if (c == ' ') {
                return false; // Nog lege vakjes
            }
        }
        
        // Bord vol - spel voorbij
        gameDone = true;
        if (gameListener != null) {
            gameListener.onGameEnded(determineWinner());
        }
        return true;
    }

    private void notifyStatusChanged() {
        if (gameListener != null) {
            gameListener.onStatusChanged(currentPlayer.getName() + "'s turn");
        }
    }

    private void notifyStatusChanged(String status) {
        if (gameListener != null) {
            gameListener.onStatusChanged(status);
        }
    }

    // Getters
    public Reversi getGame() { return game; }
    public AbstractPlayer getCurrentPlayer() { return currentPlayer; }
    public AbstractPlayer getPlayer1() { return player1; }
    public AbstractPlayer getPlayer2() { return player2; }
    public boolean isGameDone() { return gameDone; }
    public char[] getBoardState() { return game.getBord(); }
    public boolean isAIThinking() { return aiThinking; }
    public int getScore(char player) { return game.count(player); }
    public boolean wasLastMovePass() { return lastMoveWasPass; }

    /**
     * GameResult inner class
     */
    public static class GameResult {
        public enum ResultType { WIN, DRAW }
        private final ResultType type;
        private final AbstractPlayer winner;

        private GameResult(ResultType type, AbstractPlayer winner) {
            this.type = type;
            this.winner = winner;
        }

        public static GameResult createWin(AbstractPlayer winner) {
            return new GameResult(ResultType.WIN, winner);
        }

        public static GameResult createDraw() {
            return new GameResult(ResultType.DRAW, null);
        }

        public String getDescription() {
            return type == ResultType.WIN ? winner.getName() + " wins!" : "Draw!";
        }

        public ResultType getType() { return type; }
        public AbstractPlayer getWinner() { return winner; }
    }
}
