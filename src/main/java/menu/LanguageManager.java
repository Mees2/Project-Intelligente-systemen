package menu;

import java.util.HashMap;
import java.util.Map;

/**
 * LanguageManager beheert alle vertalingen voor de applicatie.
 * Ondersteunt Nederlands en Engels.
 */
public final class LanguageManager {
    private static LanguageManager instance;
    private String currentLanguage = "en"; // Default: engels
    
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
        addTranslation("main.title", "Spelcollectie - Hoofdmenu", "Game Collection - Main Menu","Bộ sưu tập trò Game - Menu chính","游戏合集 - 主菜单");
        addTranslation("main.welcome", "<html>Welkom<br>bij de<br>Spelcollectie</html>", "<html>Welcome<br>to the<br>Game Collection","<html>Chào mừng<br>đến với bộ sưu<br>tập trò Game</html>","<html>欢迎<br>来到<br>游戏合集</html>");
        addTranslation("main.tictactoe", "TicTacToe", "TicTacToe","Cờ ca-rô","井字棋");
        addTranslation("main.reversi.soon", "Reversi (Binnenkort beschikbaar)", "Reversi (Coming Soon)","Cờ Lật(Sắp ra)","黑白棋（即将推出）");
        addTranslation("main.reversi", "Reversi", "Reversi", "Reversi", "黑白棋");
        addTranslation("main.settings", "Instellingen", "Settings","Cai đặt","设置");
        addTranslation("main.exit", "Afsluiten", "Exit", "Ra","推出");
        addTranslation("main.exit.confirm", "Weet je zeker dat je het programma wilt afsluiten?", 
                      "Are you sure you want to exit the program?",
                            "Có chắc chắn muốn thoát khỏi chương trình không?",
                                "是否确定退出程序?");
        addTranslation("main.exit.title", "Bevestig afsluiten", "Confirm Exit",
                "xác nhận thoát","退出程序");
        
        // Instellingen menu vertalingen
        addTranslation("settings.title", "Instellingen", "Settings","Cai đặt","设置");
        addTranslation("settings.language", "Taal:", "Language:", "Ngôn ngữ","语d言");
        addTranslation("settings.language.dutch", "Nederlands", "Nederlands", "Nederlands","Nederlands");
        addTranslation("settings.language.english", "Engels", "English","Tiếng Anh","英文");
        addTranslation("settings.language.vietnamese", "Tiếng Việt", "Tiếng Việt","Tiếng Việt","Tiếng Việt");
        addTranslation("settings.language.chinese", "中文", "中文","中文","中文");
        addTranslation("settings.language.changed", "Taal is gewijzigd naar Nederlands",
                      "Language changed to English",
                            "Ngôn ngữ đã thay đổi thành Tiếng Việt" ,"语言已更改为中文");
        addTranslation("settings.language.changed.title", "Taal gewijzigd", "Language Changed","ngôn ngữ đã thay đổi ","语言已更改");
        addTranslation("settings.back", "Terug naar hoofdmenu", "Back to Main Menu","quay lại menu chính","返回主菜单");
        
        // TicTacToe menu vertalingen
        addTranslation("tictactoe.menu.title", "TicTacToe - Kies Spelmode", "TicTacToe - Choose Game Mode","Cờ ca-rô chọn Game","");
        addTranslation("tictactoe.menu.header", "Kies je spelmode", "Choose your game mode","Chọn chế độ game","选择模式");
        addTranslation("tictactoe.menu.pvp", "Speler vs Speler", "Player vs Player","Người chơi đấu với người chơi","玩家对战");
        addTranslation("tictactoe.menu.pvp.desc", "Speel tegen een vriend", "Play against a friend","Chơi với bạn"," 好友对战");
        addTranslation("tictactoe.menu.pva", "Speler vs AI", "Player vs AI","Chơi với AI"," 玩家对战 AI");
        addTranslation("tictactoe.menu.pva.desc", "Speel tegen de computer", "Play against the computer","Chơi với máy tính","电脑对战");
        addTranslation("tictactoe.menu.server", "Server", "Server","Máy chủ","服务器");
        addTranslation("tictactoe.menu.back", "Terug", "Back","Quay lại","返回");
        
        // TicTacToe spel vertalingen
        addTranslation("tictactoe.game.title.pvp", "TicTacToe - Speler vs Speler", "TicTacToe - Player vs Player","Cờ ca-rô -Người chơi đấu với người chơi","井字棋 - 玩家对战");
        addTranslation("tictactoe.game.title.pva", "TicTacToe - Speler vs AI", "TicTacToe - Player vs AI","Cờ ca-rô - Chơi với AI","玩家对战 AI");
        addTranslation("tictactoe.game.title.server", "TicTacToe - Speler vs Speler Online", "TicTacToe - Player vs Player Online", "temp", "temp2");
        addTranslation("tictactoe.game.turn", "Beurt van speler: {0}", "Player {0}'s turn","Lượt của người chơi {0}"," 玩家 {0} 的回合");
        addTranslation("tictactoe.game.win", "Speler {0} wint!", "Player {0} wins!","Người chơi {0} thắng"," 玩家 {0} 获胜");
        addTranslation("tictactoe.game.draw", "Gelijkspel!", "It's a draw!","Vẽ tranh!","平局!");
        addTranslation("tictactoe.game.newgame", "Nieuw Spel", "New Game","Game mới","新游戏d");
        addTranslation("tictactoe.game.menu", "Terug naar Menu", "Back to Menu","Quay lại menu","返回菜单");
        addTranslation("tictactoe.game.reset", "Reset", "Reset","Cài lại","重置");
        addTranslation("tictactoe.game.player", "Speler", "Player","Người chơi","玩家");
        addTranslation("tictactoe.game.ai", "AI", "AI","Trí tuệ nhân tạo"
                ,"人工智能");

        // TicTacToe naam invoer vertalingen
        addTranslation("tictactoe.name.title", "TicTacToe - Kies Naam en Rol", "TicTacToe - Choose Name and Role", "Cờ ca-rô - Chọn tên và vai trò","选择名称和角色");
        addTranslation("tictactoe.name.playername", "Naam Speler:", "Player Name:", "Tên người chơi:","玩家名称:");
        addTranslation("tictactoe.name.playername1", "Naam Speler 1 (X):", "Player Name 1 (X):", "Tên người chơi 1 (X):","玩家名称 1 (X):");
        addTranslation("tictactoe.name.playername2", "Naam Speler 2 (O):", "Player Name 2 (O):",  "Tên người chơi 2 (O):","玩家名称 2 (O):");
        addTranslation("tictactoe.name.selectrole", "Selecteer Rol:", "Select Role:", "Chọn vai trò:","选择角色:");
        addTranslation("tictactoe.name.startgame", "Start Spel", "Start Game", "Bắt đầu chơi Game","开始游戏"); 
        addTranslation("tictactoe.name.back", "Terug", "Back", "Quay lại","返回");
        addTranslation("tictactoe.name.error.emptyname", "Naam mag niet leeg zijn.", "Name cannot be empty.", "Tên không được để trống","名称不能为空");
        addTranslation("tictactoe.name.error.emptyrole", "Rol mag niet leeg zijn.", "Role cannot be empty.", "Vai trò không được để trống","角色不能为空");

        // Reversi menu translations
        addTranslation("reversi.menu.title", "Reversi - Kies Spelmode", "Reversi - Choose Game Mode", "Reversi - Chọn chế độ", "黑白棋 - 选择模式");
        addTranslation("reversi.menu.header", "Kies je spelmode", "Choose your game mode", "Chọn chế độ game", "选择模式");
        addTranslation("reversi.menu.pvp", "Speler vs Speler", "Player vs Player", "Người chơi đấu với người chơi", "玩家对战");
        addTranslation("reversi.menu.pva.soon", "Speler vs AI (binnenkort)", "Player vs AI (coming soon)", "Người chơi vs AI (sắp có)", "玩家对战AI（即将推出）");
        addTranslation("reversi.menu.back", "Terug", "Back", "Quay lại", "返回");

        // Reversi name input
        addTranslation("reversi.name.title", "Reversi - Kies Namen", "Reversi - Choose Names", "Reversi - Chọn tên", "黑白棋 - 选择名称");
        addTranslation("reversi.name.playername1", "Naam Speler 1 (Zwart):", "Player Name 1 (Black):", "Tên người chơi 1 (Đen):", "玩家1名称（黑）:");
        addTranslation("reversi.name.playername2", "Naam Speler 2 (Wit):", "Player Name 2 (White):", "Tên người chơi 2 (Trắng):", "玩家2名称（白）:");
        addTranslation("reversi.name.startgame", "Start Spel", "Start Game", "Bắt đầu chơi", "开始游戏");
        addTranslation("reversi.name.back", "Terug", "Back", "Quay lại", "返回");
        addTranslation("reversi.name.error.emptyname", "Naam mag niet leeg zijn.", "Name cannot be empty.", "Tên không được để trống", "名称不能为空");

        // Reversi game
        addTranslation("reversi.game.title.pvp", "Reversi - Speler vs Speler", "Reversi - Player vs Player", "Reversi - Người chơi đấu với người chơi", "黑白棋 - 玩家对战");
        addTranslation("reversi.game.turn", "Beurt van speler: {0}", "Player {0}'s turn", "Lượt của người chơi {0}", "玩家 {0} 的回合");
        addTranslation("reversi.game.win", "Speler {0} wint!", "Player {0} wins!", "Người chơi {0} thắng", "玩家 {0} 获胜");
        addTranslation("reversi.game.draw", "Gelijkspel!", "It's a draw!", "Vẽ tranh!", "平局!");

        // Algemene vertalingen
        addTranslation("common.yes", "Ja", "Yes","Đúng","是");
        addTranslation("common.no", "Nee", "No","Không","否");
        addTranslation("common.ok", "OK", "OK","Đồng ý","确定");
        addTranslation("common.cancel", "Annuleren", "Cancel","Xoá bỏ","取消");
        addTranslation("common.error", "Fout", "Error","Lỗi","错误");
    }
    
    /**
     * Voeg een vertaling toe
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
            case "nl": return "Nederlands";
            case "vn": return "Tiếng Việt";
            case "cn": return "中文";
            default: return "English"; // Default naar Engels
        }
    }
}
