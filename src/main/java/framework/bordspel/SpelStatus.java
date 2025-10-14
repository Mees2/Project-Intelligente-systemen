package framework.bordspel;

/**
 * Enum voor de status van een spel
 * Gebruikt om de huidige staat van het spel bij te houden
 */
public enum SpelStatus {
    /**
     * Het spel is aan de gang
     */
    BEZIG,
    
    /**
     * Speler X heeft gewonnen
     */
    X_WINT,
    
    /**
     * Speler O heeft gewonnen
     */
    O_WINT,
    
    /**
     * Het spel is gelijkspel
     */
    GELIJKSPEL,
    
    /**
     * Het spel is nog niet gestart
     */
    NIET_GESTART
}
