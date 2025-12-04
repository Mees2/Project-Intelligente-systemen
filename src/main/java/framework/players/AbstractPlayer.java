package framework.players;

/**
 * Abstracte basis klasse voor een speler
 * Kan zowel een menselijke speler, AI of netwerk speler zijn
 */
public abstract class AbstractPlayer {
    protected final String name;
    protected final char symbol;
    protected final PlayerType type;
    
    /**
     * Constructor voor een speler
     * @param name De naam van de speler
     * @param symbol Het symbool van de speler ('X' of 'O')
     * @param type Het type speler (HUMAN, AI, NETWORK)
     */
    protected AbstractPlayer(String name, char symbol, PlayerType type) {
        this.name = name;
        this.symbol = symbol;
        this.type = type;
    }
    
    /**
     * Krijg de naam van de speler
     * @return De naam
     */
    public String getName() {
        return name;
    }
    
    /**
     * Krijg het symbool van de speler
     * @return Het symbool
     */
    public char getSymbol() {
        return symbol;
    }
    
    /**
     * Krijg het type van de speler
     * @return Het type
     */
    public PlayerType getType() {
        return type;
    }
    
    /**
     * Controleert of dit een AI speler is
     * @return true als het een AI speler is
     */
    public boolean isAI() {
        return type == PlayerType.AI;
    }
    
    /**
     * Controleert of dit een menselijke speler is
     * @return true als het een menselijke speler is
     */
    public boolean isHuman() {
        return type == PlayerType.HUMAN;
    }
    
    /**
     * Controleert of dit een netwerk speler is
     * @return true als het een netwerk speler is
     */
    public boolean isNetwork() {
        return type == PlayerType.NETWORK;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%c) - %s", name, symbol, type);
    }
}
