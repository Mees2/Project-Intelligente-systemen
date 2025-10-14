package framework.spelers;

/**
 * Representeert een menselijke speler
 * Gebruikt in TicTacToe en Reversi voor player vs player en player vs AI modes
 */
public class MenselijkeSpeler extends AbstractSpeler {
    
    /**
     * Constructor voor een menselijke speler
     * @param naam De naam van de speler
     * @param symbool Het symbool van de speler ('X' of 'O')
     */
    public MenselijkeSpeler(String naam, char symbool) {
        super(naam, symbool, SpelerType.MENS);
    }
    
    /**
     * Standaard constructor met alleen symbool
     * Gebruikt een standaard naam gebaseerd op het symbool
     * @param symbool Het symbool van de speler
     */
    public MenselijkeSpeler(char symbool) {
        super("Speler " + symbool, symbool, SpelerType.MENS);
    }
}
