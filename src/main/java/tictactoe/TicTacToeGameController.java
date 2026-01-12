package tictactoe;

import framework.boardgame.Move;
import framework.boardgame.Position;
import framework.boardgame.GameResult;
import framework.players.AbstractPlayer;

/**
 * TicTacToe Game Controller - Backend game logic only
 * Handelt alle spelregels en zetten af, volledig losgekoppeld van UI
 */
public class TicTacToeGameController {
    private final TicTacToe game;
    private final AbstractPlayer player1;
    private final AbstractPlayer player2;
    private AbstractPlayer currentPlayer;
    private boolean gameDone = false;
    private boolean isPlayer1Turn = true;
    private GameListener gameListener;
    private volatile boolean aiThinking = false;

    /**
     * Listener interface voor UI updates
     */
    public interface GameListener {
        void onMoveExecuted(Move move);
        void onGameEnded(GameResult result);
        void onStatusChanged(String status);
        void onAIThinking(boolean thinking);
    }

    public TicTacToeGameController(TicTacToe game, AbstractPlayer player1, AbstractPlayer player2) {
        this.game = game;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
    }

    public void setGameListener(GameListener listener) {
        this.gameListener = listener;
    }

    /**
     * Voer een zet uit op basis van index (0-8)
     */
    public boolean makeMove(int index) {
        if (gameDone || aiThinking || !game.isFree(index)) {
            return false;
        }

        Move move = new Move(index, currentPlayer.getSymbol(), game.getBoardWidth());
        game.doMove(move);

        if (gameListener != null) {
            gameListener.onMoveExecuted(move);
        }

        if (checkGameEnd()) {
            return true;
        }

        switchPlayer();
        notifyStatusChanged();

        if (currentPlayer.isAI()) {
            makeAIMove();
        }

        return true;
    }

    /**
     * Voer een zet uit op basis van rij en kolom
     */
    public boolean makeMove(int row, int col) {
        Position pos = new Position(row, col, game.getBoardWidth());
        return makeMove(pos.getIndex());
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

        char opponent = (currentPlayer.getSymbol() == 'X') ? 'O' : 'X';
        int bestMoveIndex = MinimaxAI.bestMove(game, currentPlayer.getSymbol(), opponent);

        aiThinking = false;
        if (gameListener != null) {
            gameListener.onAIThinking(false);
        }

        if (bestMoveIndex != -1) {
            makeMove(bestMoveIndex);
        }
    }

    /**
     * Controleer of het spel voorbij is
     */
    private boolean checkGameEnd() {
        char lastPlayerSymbol = currentPlayer.getSymbol();

        if (game.isWin(lastPlayerSymbol)) {
            gameDone = true;
            if (gameListener != null) {
                gameListener.onGameEnded(GameResult.createWin(currentPlayer));
            }
            return true;
        }

        if (game.isDraw()) {
            gameDone = true;
            if (gameListener != null) {
                gameListener.onGameEnded(GameResult.createDraw());
            }
            return true;
        }

        return false;
    }

    /**
     * Wissel van speler
     */
    private void switchPlayer() {
        currentPlayer = isPlayer1Turn ? player2 : player1;
        isPlayer1Turn = !isPlayer1Turn;
    }

    private void notifyStatusChanged() {
        if (gameListener != null) {
            gameListener.onStatusChanged(currentPlayer.getName() + "'s turn");
        }
    }

    // Getters
    public TicTacToe getGame() { return game; }
    public AbstractPlayer getCurrentPlayer() { return currentPlayer; }
    public AbstractPlayer getPlayer1() { return player1; }
    public AbstractPlayer getPlayer2() { return player2; }
    public boolean isGameDone() { return gameDone; }
    public char[] getBoardState() { return game.getBord(); }
    public boolean isAIThinking() { return aiThinking; }


}
