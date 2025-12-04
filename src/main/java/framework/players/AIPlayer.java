package framework.players;

/**
 * Representeert een AI speler
 * Gebruikt in TicTacToe en Reversi voor player vs AI mode
 */
public class AIPlayer extends AbstractPlayer {
    
    /**
     * Constructor voor een AI speler
     * @param name De naam van de AI
     * @param symbol Het symbool van de AI ('X' of 'O')
     */
    public AIPlayer(String name, char symbol) {
        super(name, symbol, PlayerType.AI);
    }
    
    /**
     * Standaard constructor met alleen symbool
     * Gebruikt "AI" als standaard naam
     * @param symbol Het symbool van de AI
     */
    public AIPlayer(char symbol) {
        super("AI", symbol, PlayerType.AI);
    }
}
