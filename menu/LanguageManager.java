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
        addTranslation("main.title", "Spelcollectie - Hoofdmenu", "Game Collection - Main Menu"," ","游戏合集 - 主菜单");
        addTranslation("main.welcome", "Welkom bij de Spelcollectie", "Welcome to the Game Collection"," ","欢迎来到游戏合集");
        addTranslation("main.tictactoe", "TicTacToe", "TicTacToe"," ","井字棋");
        addTranslation("main.reversi.soon", "Reversi (Binnenkort beschikbaar)", "Reversi (Coming Soon)","","黑白棋（即将推出）");
        addTranslation("main.settings", "Instellingen", "Settings","Cai đặt","设置");
        addTranslation("main.exit", "Afsluiten", "Exit", "","推出");
        addTranslation("main.exit.confirm", "Weet je zeker dat je het programma wilt afsluiten?", 
                      "Are you sure you want to exit the program?",
                            " ",
                                "是否确定退出程序?");
        addTranslation("main.exit.title", "Bevestig afsluiten", "Confirm Exit"," ","退出程序");
        
        // Instellingen menu vertalingen
        addTranslation("settings.title", "Instellingen", "Settings","Cai đặt","设置");
        addTranslation("settings.language", "Taal:", "Language:", "","语d言");
        addTranslation("settings.language.dutch", "Nederlands", "Dutch", "Tiếng Hà Lan","荷兰语");
        addTranslation("settings.language.english", "English", "English","Tiếng Anh","英文");
        addTranslation("settings.language.vietnamese", "Vietnamees", "Vietnamese","Tiếng Việt","越南语");
        addTranslation("settings.language.chinese", "Chinees", "Chinese","Tiếng Trung Quốc","中文");
        addTranslation("settings.language.changed", "Taal is gewijzigd naar {0}", 
                      "Language changed to {0}",
                            " " ,"语言已更改为{0}");
        addTranslation("settings.language.changed.title", "Taal gewijzigd", "Language Changed"," ","语言已更改");
        addTranslation("settings.back", "Terug naar hoofdmenu", "Back to Main Menu","","返回主菜单");
        
        // TicTacToe menu vertalingen
        addTranslation("tictactoe.menu.title", "TicTacToe - Kies Spelmode", "TicTacToe - Choose Game Mode","","");
        addTranslation("tictactoe.menu.header", "Kies je spelmode", "Choose your game mode","","选择模式");
        addTranslation("tictactoe.menu.pvp", "Speler vs Speler", "Player vs Player","","玩家对战");
        addTranslation("tictactoe.menu.pvp.desc", "Speel tegen een vriend", "Play against a friend",""," 好友对战");
        addTranslation("tictactoe.menu.pva", "Speler vs AI", "Player vs AI",""," 玩家对战 AI");
        addTranslation("tictactoe.menu.pva.desc", "Speel tegen de computer", "Play against the computer","","电脑对战");
        addTranslation("tictactoe.menu.server.soon", "Server (Binnenkort beschikbaar)", "Server (Coming Soon)","","服务器（即将推出）");
        addTranslation("tictactoe.menu.back", "Terug", "Back","","返回");
        
        // TicTacToe spel vertalingen
        addTranslation("tictactoe.game.title.pvp", "TicTacToe - Speler vs Speler", "TicTacToe - Player vs Player","","井字棋 - 玩家对战");
        addTranslation("tictactoe.game.title.pva", "TicTacToe - Speler vs AI", "TicTacToe - Player vs AI","","玩家对战 AI");
        addTranslation("tictactoe.game.turn", "Beurt van speler: {0}", "Player {0}'s turn",""," 玩家 {0} 的回合");
        addTranslation("tictactoe.game.win", "Speler {0} wint!", "Player {0} wins!",""," 玩家 {0} 获胜");
        addTranslation("tictactoe.game.draw", "Gelijkspel!", "It's a draw!","","平局!");
        addTranslation("tictactoe.game.newgame", "Nieuw Spel", "New Game","","新游戏d");
        addTranslation("tictactoe.game.menu", "Terug naar Menu", "Back to Menu","","返回菜单");
        addTranslation("tictactoe.game.reset", "Reset", "Reset","","重置");
        addTranslation("tictactoe.game.player", "Speler", "Player","","玩家");
        addTranslation("tictactoe.game.ai", "AI", "AI","","人工智能");
        
        // Algemene vertalingen
        addTranslation("common.yes", "Ja", "Yes","","是");
        addTranslation("common.no", "Nee", "No","","否");
        addTranslation("common.ok", "OK", "OK","","确定");
        addTranslation("common.cancel", "Annuleren", "Cancel","","取消");
    }
    
    /**
     * Voeg een vertaling toe voor Nederlands en Engels
     */
    private void addTranslation(String key, String dutch, String english, String vietnamese, String chinese) {
        Map<String, String> languageMap = new HashMap<>();
        languageMap.put("nl", dutch);
        languageMap.put("en", english);
        languageMap.put("vn", vietnamese);
        languageMap.put("cn", chinese);
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
        if (languageCode.equals("nl") || languageCode.equals("en")||languageCode.equals("vn")||languageCode.equals("cn")) {
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
            case "en": return "English";
            case "vn": return "Tiếng Việt";
            case "cn": return "中文";
            default: return "Nederlands";
        }
    }
}
