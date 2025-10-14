package framework.spelers;

/**
 * Representeert een AI speler
 * Gebruikt in TicTacToe en Reversi voor player vs AI mode
 */
public class AISpeler extends AbstractSpeler {
    
    /**
     * Constructor voor een AI speler
     * @param naam De naam van de AI
     * @param symbool Het symbool van de AI ('X' of 'O')
     */
    public AISpeler(String naam, char symbool) {
        super(naam, symbool, SpelerType.AI);
    }
    
    /**
     * Standaard constructor met alleen symbool
     * Gebruikt "AI" als standaard naam
     * @param symbool Het symbool van de AI
     */
    public AISpeler(char symbool) {
        super("AI", symbool, SpelerType.AI);
    }
}
