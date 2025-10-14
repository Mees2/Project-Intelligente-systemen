package framework.bordspel;

/**
 * Representeert een positie op een spelbord
 * Kan gebruikt worden voor verschillende bordspellen (TicTacToe, Reversi, etc.)
 */
public class Positie {
    private final int index;        // Lineaire index (0-8 voor TicTacToe, 0-63 voor Reversi)
    private final int rij;          // Rij nummer
    private final int kolom;        // Kolom nummer
    
    /**
     * Constructor met lineaire index
     * @param index De positie als lineaire index
     * @param bordBreedte De breedte van het bord (3 voor TicTacToe, 8 voor Reversi)
     */
    public Positie(int index, int bordBreedte) {
        this.index = index;
        this.rij = index / bordBreedte;
        this.kolom = index % bordBreedte;
    }
    
    /**
     * Constructor met rij en kolom
     * @param rij De rij (0-gebaseerd)
     * @param kolom De kolom (0-gebaseerd)
     * @param bordBreedte De breedte van het bord
     */
    public Positie(int rij, int kolom, int bordBreedte) {
        this.rij = rij;
        this.kolom = kolom;
        this.index = rij * bordBreedte + kolom;
    }
    
    /**
     * Krijg de lineaire index van deze positie
     * @return De index
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * Krijg het rij nummer van deze positie
     * @return De rij
     */
    public int getRij() {
        return rij;
    }
    
    /**
     * Krijg het kolom nummer van deze positie
     * @return De kolom
     */
    public int getKolom() {
        return kolom;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Positie positie = (Positie) obj;
        return index == positie.index;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(index);
    }
    
    @Override
    public String toString() {
        return String.format("Positie[index=%d, rij=%d, kolom=%d]", index, rij, kolom);
    }
}
