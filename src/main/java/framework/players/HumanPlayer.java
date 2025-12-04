package framework.players;

/**
 * Representeert een menselijke speler
 * Gebruikt in TicTacToe en Reversi voor player vs player en player vs AI modes
 */
public class HumanPlayer extends AbstractPlayer {
    
    /**
     * Constructor voor een menselijke speler
     * @param name De naam van de speler
     * @param symbol Het symbool van de speler ('X' of 'O')
     */
    public HumanPlayer(String name, char symbol) {
        super(name, symbol, PlayerType.HUMAN);
    }
    
    /**
     * Standaard constructor met alleen symbool
     * Gebruikt een standaard naam gebaseerd op het symbool
     * @param symbol Het symbool van de speler
     */
    public HumanPlayer(char symbol) {
        super("Player " + symbol, symbol, PlayerType.HUMAN);
    }
}
