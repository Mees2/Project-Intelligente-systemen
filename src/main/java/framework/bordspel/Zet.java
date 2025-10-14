package framework.bordspel;

/**
 * Representeert een zet in een bordspel
 * Bevat informatie over de positie en de speler die de zet doet
 */
public class Zet {
    private final Positie positie;
    private final char speler;
    
    /**
     * Constructor voor een zet
     * @param positie De positie waar de zet wordt gedaan
     * @param speler Het symbool van de speler ('X', 'O', etc.)
     */
    public Zet(Positie positie, char speler) {
        this.positie = positie;
        this.speler = speler;
    }
    
    /**
     * Constructor met index
     * @param index De lineaire index van de positie
     * @param speler Het symbool van de speler
     * @param bordBreedte De breedte van het bord
     */
    public Zet(int index, char speler, int bordBreedte) {
        this.positie = new Positie(index, bordBreedte);
        this.speler = speler;
    }
    
    /**
     * Krijg de positie van deze zet
     * @return De positie
     */
    public Positie getPositie() {
        return positie;
    }
    
    /**
     * Krijg het symbool van de speler die deze zet doet
     * @return Het speler symbool
     */
    public char getSpeler() {
        return speler;
    }
    
    /**
     * Krijg de index van de positie
     * @return De index
     */
    public int getIndex() {
        return positie.getIndex();
    }
    
    @Override
    public String toString() {
        return String.format("Zet[speler=%c, positie=%s]", speler, positie);
    }
}
