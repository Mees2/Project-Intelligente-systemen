package framework.boardgame;

/**
 * Representeert een positie op een spelbord
 * Kan gebruikt worden voor verschillende bordspellen (TicTacToe, Reversi, etc.)
 */
public class Position {
    private final int index;        // Lineaire index (0-8 voor TicTacToe, 0-63 voor Reversi)
    private final int row;          // Rij nummer
    private final int column;        // Kolom nummer
    
    /**
     * Constructor met lineaire index
     * @param index De positie als lineaire index
     * @param boardWidth De breedte van het bord (3 voor TicTacToe, 8 voor Reversi)
     */
    public Position(int index, int boardWidth) {
        this.index = index;
        this.row = index / boardWidth;
        this.column = index % boardWidth;
    }
    
    /**
     * Constructor met rij en kolom
     * @param row De rij (0-gebaseerd)
     * @param column De kolom (0-gebaseerd)
     * @param boardWidth De breedte van het bord
     */
    public Position(int row, int column, int boardWidth) {
        this.row = row;
        this.column = column;
        this.index = row * boardWidth + column;
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
    public int getRow() {
        return row;
    }
    
    /**
     * Krijg het kolom nummer van deze positie
     * @return De kolom
     */
    public int getColumn() {
        return column;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return index == position.index;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(index);
    }
    
    @Override
    public String toString() {
        return String.format("Position[index=%d, row=%d, column=%d]", index, row, column);
    }
}
