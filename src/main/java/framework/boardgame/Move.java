package framework.boardgame;

/**
 * Representeert een zet in een bordspel
 * Bevat informatie over de positie en de speler die de zet doet
 */
public class Move {
    private final Position position;
    private final char player;
    
    /**
     * Constructor voor een zet
     * @param position De positie waar de zet wordt gedaan
     * @param player Het symbool van de speler ('X', 'O', etc.)
     */
    public Move(Position position, char player) {
        this.position = position;
        this.player = player;
    }
    
    /**
     * Constructor met index
     * @param index De lineaire index van de positie
     * @param player Het symbool van de speler
     * @param boardWidth De breedte van het bord
     */
    public Move(int index, char player, int boardWidth) {
        this.position = new Position(index, boardWidth);
        this.player = player;
    }
    
    /**
     * Krijg de positie van deze zet
     * @return De positie
     */
    public Position getPosition() {
        return position;
    }
    
    /**
     * Krijg het symbool van de speler die deze zet doet
     * @return Het speler symbool
     */
    public char getPlayer() {
        return player;
    }
    
    /**
     * Krijg de index van de positie
     * @return De index
     */
    public int getIndex() {
        return position.getIndex();
    }
    
    @Override
    public String toString() {
        return String.format("Move[player=%c, position=%s]", player, position);
    }
}
