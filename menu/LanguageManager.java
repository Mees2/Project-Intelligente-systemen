package menu;

import java.util.HashMap;
import java.util.Map;

/**
 * LanguageManager beheert alle vertalingen voor de applicatie.
 * Ondersteunt Nederlands en Engels.
 */
public final class LanguageManager {
    private static LanguageManager instance;
    private String currentLanguage = "nl"; // Default: Nederlands
    
    // Map voor alle vertalingen: key -> (language -> translation)
    private final Map<String, Map<String, String>> translations = new HashMap<>();
    
    private LanguageManager() {
        initializeTranslations();
    }
    
    /**
     * Singleton pattern - krijg de enige instance van LanguageManager
     */
    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }
    
    /**
     * Initialiseer alle vertalingen
     */
    private void initializeTranslations() {
        // Hoofdmenu vertalingen
        addTranslation("main.title", "Spelcollectie - Hoofdmenu", "Game Collection - Main Menu");
        addTranslation("main.welcome", "Welkom bij de Spelcollectie", "Welcome to the Game Collection");
        addTranslation("main.tictactoe", "TicTacToe", "TicTacToe");
        addTranslation("main.reversi.soon", "Reversi (Binnenkort beschikbaar)", "Reversi (Coming Soon)");
        addTranslation("main.settings", "Instellingen", "Settings");
        addTranslation("main.exit", "Afsluiten", "Exit");
        addTranslation("main.exit.confirm", "Weet je zeker dat je het programma wilt afsluiten?", 
                      "Are you sure you want to exit the program?");
        addTranslation("main.exit.title", "Bevestig afsluiten", "Confirm Exit");
        
        // Instellingen menu vertalingen
        addTranslation("settings.title", "Instellingen", "Settings");
        addTranslation("settings.language", "Taal:", "Language:");
        addTranslation("settings.language.dutch", "Nederlands", "Dutch");
        addTranslation("settings.language.english", "English", "English");
        addTranslation("settings.language.changed", "Taal is gewijzigd naar {0}", 
                      "Language changed to {0}");
        addTranslation("settings.language.changed.title", "Taal gewijzigd", "Language Changed");
        addTranslation("settings.back", "Terug naar hoofdmenu", "Back to Main Menu");
        
        // TicTacToe menu vertalingen
        addTranslation("tictactoe.menu.title", "TicTacToe - Kies Spelmode", "TicTacToe - Choose Game Mode");
        addTranslation("tictactoe.menu.header", "Kies je spelmode", "Choose your game mode");
        addTranslation("tictactoe.menu.pvp", "Speler vs Speler", "Player vs Player");
        addTranslation("tictactoe.menu.pvp.desc", "Speel tegen een vriend", "Play against a friend");
        addTranslation("tictactoe.menu.pva", "Speler vs AI", "Player vs AI");
        addTranslation("tictactoe.menu.pva.desc", "Speel tegen de computer", "Play against the computer");
        addTranslation("tictactoe.menu.server.soon", "Server (Binnenkort beschikbaar)", "Server (Coming Soon)");
        addTranslation("tictactoe.menu.back", "Terug", "Back");
        
        // TicTacToe spel vertalingen
        addTranslation("tictactoe.game.title.pvp", "TicTacToe - Speler vs Speler", "TicTacToe - Player vs Player");
        addTranslation("tictactoe.game.title.pva", "TicTacToe - Speler vs AI", "TicTacToe - Player vs AI");
        addTranslation("tictactoe.game.turn", "Beurt van speler: {0}", "Player {0}'s turn");
        addTranslation("tictactoe.game.win", "Speler {0} wint!", "Player {0} wins!");
        addTranslation("tictactoe.game.draw", "Gelijkspel!", "It's a draw!");
        addTranslation("tictactoe.game.newgame", "Nieuw Spel", "New Game");
        addTranslation("tictactoe.game.menu", "Terug naar Menu", "Back to Menu");
        addTranslation("tictactoe.game.reset", "Reset", "Reset");
        addTranslation("tictactoe.game.player", "Speler", "Player");
        addTranslation("tictactoe.game.ai", "AI", "AI");
        
        // Algemene vertalingen
        addTranslation("common.yes", "Ja", "Yes");
        addTranslation("common.no", "Nee", "No");
        addTranslation("common.ok", "OK", "OK");
        addTranslation("common.cancel", "Annuleren", "Cancel");
    }
    
    /**
     * Voeg een vertaling toe voor Nederlands en Engels
     */
    private void addTranslation(String key, String dutch, String english) {
        Map<String, String> languageMap = new HashMap<>();
        languageMap.put("nl", dutch);
        languageMap.put("en", english);
        translations.put(key, languageMap);
    }
    
    /**
     * Krijg een vertaalde tekst voor de huidige taal
     * @param key De sleutel van de vertaling
     * @return De vertaalde tekst
     */
    public String get(String key) {
        Map<String, String> languageMap = translations.get(key);
        if (languageMap == null) {
            return key; // Return key if translation not found
        }
        String translation = languageMap.get(currentLanguage);
        return translation != null ? translation : key;
    }
    
    /**
     * Krijg een vertaalde tekst met placeholders vervangen
     * @param key De sleutel van de vertaling
     * @param args De argumenten om de placeholders {0}, {1}, etc. te vervangen
     * @return De vertaalde tekst met vervangen placeholders
     */
    public String get(String key, String... args) {
        String translation = get(key);
        for (int i = 0; i < args.length; i++) {
            translation = translation.replace("{" + i + "}", args[i]);
        }
        return translation;
    }
    
    /**
     * Stel de huidige taal in
     * @param languageCode De taalcode: "nl" voor Nederlands, "en" voor Engels
     */
    public void setLanguage(String languageCode) {
        if (languageCode.equals("nl") || languageCode.equals("en")) {
            this.currentLanguage = languageCode;
        }
    }
    
    /**
     * Krijg de huidige taalcode
     * @return De taalcode: "nl" of "en"
     */
    public String getCurrentLanguage() {
        return currentLanguage;
    }
    
    /**
     * Krijg de volledige naam van de huidige taal
     * @return De naam van de taal
     */
    public String getCurrentLanguageName() {
        switch (currentLanguage) {
            case "nl": return "Nederlands";
            case "en": return "English";
            default: return "Nederlands";
        }
    }
}
