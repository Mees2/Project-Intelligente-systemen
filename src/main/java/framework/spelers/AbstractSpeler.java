package framework.spelers;

/**
 * Abstracte basis klasse voor een speler
 * Kan zowel een menselijke speler, AI of netwerk speler zijn
 */
public abstract class AbstractSpeler {
    protected final String naam;
    protected final char symbool;
    protected final SpelerType type;
    
    /**
     * Constructor voor een speler
     * @param naam De naam van de speler
     * @param symbool Het symbool van de speler ('X' of 'O')
     * @param type Het type speler (MENS, AI, NETWERK)
     */
    protected AbstractSpeler(String naam, char symbool, SpelerType type) {
        this.naam = naam;
        this.symbool = symbool;
        this.type = type;
    }
    
    /**
     * Krijg de naam van de speler
     * @return De naam
     */
    public String getNaam() {
        return naam;
    }
    
    /**
     * Krijg het symbool van de speler
     * @return Het symbool
     */
    public char getSymbool() {
        return symbool;
    }
    
    /**
     * Krijg het type van de speler
     * @return Het type
     */
    public SpelerType getType() {
        return type;
    }
    
    /**
     * Controleert of dit een AI speler is
     * @return true als het een AI speler is
     */
    public boolean isAI() {
        return type == SpelerType.AI;
    }
    
    /**
     * Controleert of dit een menselijke speler is
     * @return true als het een menselijke speler is
     */
    public boolean isMens() {
        return type == SpelerType.MENS;
    }
    
    /**
     * Controleert of dit een netwerk speler is
     * @return true als het een netwerk speler is
     */
    public boolean isNetwerk() {
        return type == SpelerType.NETWERK;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%c) - %s", naam, symbool, type);
    }
}
