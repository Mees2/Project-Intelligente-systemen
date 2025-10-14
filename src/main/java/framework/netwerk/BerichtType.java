package framework.netwerk;

/**
 * Enum voor verschillende bericht types in netwerk communicatie
 * Voorbereid voor toekomstige multiplayer functionaliteit
 */
public enum BerichtType {
    /**
     * Verbinding aanvraag van een client
     */
    VERBINDEN,
    
    /**
     * Verbinding geaccepteerd door server
     */
    VERBINDING_OK,
    
    /**
     * Verbinding geweigerd door server
     */
    VERBINDING_GEWEIGERD,
    
    /**
     * Een zet is gedaan door een speler
     */
    ZET,
    
    /**
     * Het spel is afgelopen
     */
    SPEL_AFGELOPEN,
    
    /**
     * Chat bericht tussen spelers
     */
    CHAT,
    
    /**
     * Speler wil het spel verlaten
     */
    VERLATEN,
    
    /**
     * Keepalive bericht om verbinding actief te houden
     */
    PING,
    
    /**
     * Antwoord op keepalive
     */
    PONG
}
