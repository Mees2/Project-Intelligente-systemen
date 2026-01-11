package framework.boardgame;
import framework.players.AbstractPlayer;

public class GameResult {
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