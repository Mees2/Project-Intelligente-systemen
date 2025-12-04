package framework.boardgame;

/**
 * Enum voor de status van een spel
 * Gebruikt om de huidige staat van het spel bij te houden
 */
public enum GameStatus {
    /**
     * Het spel is aan de gang
     */
    IN_PROGRESS,
    
    /**
     * Speler X heeft gewonnen
     */
    X_WINS,
    
    /**
     * Speler O heeft gewonnen
     */
    O_WINS,
    
    /**
     * Het spel is gelijkspel
     */
    DRAW,
    
    /**
     * Het spel is nog niet gestart
     */
    NOT_STARTED
}
