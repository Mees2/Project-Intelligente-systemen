package menu;

import java.util.HashMap;
import java.util.Map;

/**
 * LanguageManager beheert alle vertalingen voor de applicatie.
 * Ondersteunt Nederlands, Engels en Vietnamees.
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
        addTranslation("main.title", "Spelcollectie - Hoofdmenu", "Game Collection - Main Menu", "Bộ sưu tập trò chơi - Menu chính");
        addTranslation("main.welcome", "Welkom bij de Spelcollectie", "Welcome to the Game Collection", "Chào mừng đến với Bộ sưu tập trò chơi");
        addTranslation("main.tictactoe", "TicTacToe", "TicTacToe", "TicTacToe");
        addTranslation("main.reversi.soon", "Reversi (Binnenkort beschikbaar)", "Reversi (Coming Soon)", "Reversi (Sắp ra mắt)");
        addTranslation("main.settings", "Instellingen", "Settings", "Cài đặt");
        addTranslation("main.exit", "Afsluiten", "Exit", "Thoát");
        addTranslation("main.exit.confirm", "Weet je zeker dat je het programma wilt afsluiten?", 
                      "Are you sure you want to exit the program?", 
                      "Bạn có chắc chắn muốn thoát chương trình?");
        addTranslation("main.exit.title", "Bevestig afsluiten", "Confirm Exit", "Xác nhận thoát");
        
        // Instellingen menu vertalingen
        addTranslation("settings.title", "Instellingen", "Settings", "Cài đặt");
        addTranslation("settings.language", "Taal / Language / Ngôn ngữ:", "Language:", "Ngôn ngữ:");
        addTranslation("settings.language.dutch", "Nederlands", "Dutch", "Tiếng Hà Lan");
        addTranslation("settings.language.english", "English", "English", "Tiếng Anh");
        addTranslation("settings.language.vietnamese", "Tiếng Việt", "Vietnamese", "Tiếng Việt");
        addTranslation("settings.language.changed", "Taal is gewijzigd naar {0}", 
                      "Language changed to {0}", 
                      "Ngôn ngữ đã được thay đổi thành {0}");
        addTranslation("settings.language.changed.title", "Taal gewijzigd", "Language Changed", "Đã thay đổi ngôn ngữ");
        addTranslation("settings.back", "Terug naar hoofdmenu", "Back to Main Menu", "Quay lại Menu chính");
        
        // TicTacToe menu vertalingen
        addTranslation("tictactoe.menu.title", "TicTacToe - Kies Spelmode", "TicTacToe - Choose Game Mode", "TicTacToe - Chọn chế độ chơi");
        addTranslation("tictactoe.menu.header", "Kies je spelmode", "Choose your game mode", "Chọn chế độ chơi của bạn");
        addTranslation("tictactoe.menu.pvp", "Speler vs Speler", "Player vs Player", "Người chơi vs Người chơi");
        addTranslation("tictactoe.menu.pvp.desc", "Speel tegen een vriend", "Play against a friend", "Chơi với bạn bè");
        addTranslation("tictactoe.menu.pva", "Speler vs AI", "Player vs AI", "Người chơi vs AI");
        addTranslation("tictactoe.menu.pva.desc", "Speel tegen de computer", "Play against the computer", "Chơi với máy tính");
        addTranslation("tictactoe.menu.back", "Terug", "Back", "Quay lại");
        
        // TicTacToe spel vertalingen
        addTranslation("tictactoe.game.title.pvp", "TicTacToe - Speler vs Speler", "TicTacToe - Player vs Player", "TicTacToe - Người chơi vs Người chơi");
        addTranslation("tictactoe.game.title.pva", "TicTacToe - Speler vs AI", "TicTacToe - Player vs AI", "TicTacToe - Người chơi vs AI");
        addTranslation("tictactoe.game.turn", "Beurt van speler: {0}", "Player {0}'s turn", "Lượt của người chơi: {0}");
        addTranslation("tictactoe.game.win", "Speler {0} wint!", "Player {0} wins!", "Người chơi {0} thắng!");
        addTranslation("tictactoe.game.draw", "Gelijkspel!", "It's a draw!", "Hòa!");
        addTranslation("tictactoe.game.newgame", "Nieuw Spel", "New Game", "Trò chơi mới");
        addTranslation("tictactoe.game.menu", "Terug naar Menu", "Back to Menu", "Quay lại Menu");
        addTranslation("tictactoe.game.reset", "Reset", "Reset", "Đặt lại");
        addTranslation("tictactoe.game.player", "Speler", "Player", "Người chơi");
        addTranslation("tictactoe.game.ai", "AI", "AI", "AI");
        
        // Algemene vertalingen
        addTranslation("common.yes", "Ja", "Yes", "Có");
        addTranslation("common.no", "Nee", "No", "Không");
        addTranslation("common.ok", "OK", "OK", "OK");
        addTranslation("common.cancel", "Annuleren", "Cancel", "Hủy");
    }
    
    /**
     * Voeg een vertaling toe voor Nederlands, Engels en Vietnamees
     */
    private void addTranslation(String key, String dutch, String english, String vietnamese) {
        Map<String, String> languageMap = new HashMap<>();
        languageMap.put("nl", dutch);
        languageMap.put("en", english);
        languageMap.put("vi", vietnamese);
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
     * @param languageCode De taalcode: "nl" voor Nederlands, "en" voor Engels, "vi" voor Vietnamees
     */
    public void setLanguage(String languageCode) {
        if (languageCode.equals("nl") || languageCode.equals("en") || languageCode.equals("vi")) {
            this.currentLanguage = languageCode;
        }
    }
    
    /**
     * Krijg de huidige taalcode
     * @return De taalcode: "nl", "en", of "vi"
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
            case "vi": return "Tiếng Việt";
            default: return "Nederlands";
        }
    }
}
