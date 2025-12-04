package framework.network;

/**
 * Enum voor verschillende bericht types in netwerk communicatie
 * Voorbereid voor toekomstige multiplayer functionaliteit
 */
public enum MessageType {
    /**
     * Verbinding aanvraag van een client
     */
    CONNECT,
    
    /**
     * Verbinding geaccepteerd door server
     */
    CONNECTION_OK,
    
    /**
     * Verbinding geweigerd door server
     */
    CONNECTION_REFUSED,
    
    /**
     * Een zet is gedaan door een speler
     */
    MOVE,
    
    /**
     * Het spel is afgelopen
     */
    GAME_OVER,
    
    /**
     * Chat bericht tussen spelers
     */
    CHAT,
    
    /**
     * Speler wil het spel verlaten
     */
    LEAVE,
    
    /**
     * Keepalive bericht om verbinding actief te houden
     */
    PING,
    
    /**
     * Antwoord op keepalive
     */
    PONG
}
