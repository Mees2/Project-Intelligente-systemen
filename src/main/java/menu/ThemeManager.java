package menu;
    import java.awt.Color;

    public class ThemeManager {
        private static ThemeManager instance;

        private Color backgroundColor;
        private Color mainButtonColor;
        private Color buttonColor;
        private Color fontColor1;
        private Color fontColor2;
        private boolean darkMode = false;

        private ThemeManager() {
            //backgroundColor = new Color(0, 0, 84);
            //mainButtonColor = new Color(129, 45, 163);
            //buttonColor = new Color(44, 124, 122);
        }

        public static ThemeManager getInstance() {
            if (instance == null) instance = new ThemeManager();
            return instance;
        }

        public Color getBackgroundColor() { return backgroundColor; }
        public Color getMainButtonColor() { return mainButtonColor; }
        public Color getButtonColor() { return buttonColor; }
        public Color getFontColor1() { return fontColor1; }
        public Color getFontColor2() { return fontColor2; }

        public void setBackgroundColor(Color color) { backgroundColor = color; }
        public void setMainButtonColor(Color color) { mainButtonColor = color; }
        public void setButtonColor(Color color) { buttonColor = color; }
        public void setFontColor1(Color color) { fontColor1 = color; }
        public void setFontColor2(Color color) { fontColor2 = color; }

        public boolean isDarkMode() {
            return darkMode;
        }

        public void setDarkMode(boolean dark) {
            darkMode = dark;
            if (dark) {
                // Dark mode colors
                backgroundColor = new Color(0, 0, 84);
                mainButtonColor = new Color(129, 45, 163);
                buttonColor = new Color(44, 124, 122);
                fontColor1 = new Color(247, 247, 255);
                fontColor2 = new Color(61,169,166);

            } else {
                // Light mode colors (original)
                backgroundColor = new Color(247, 247, 255);
                mainButtonColor = new Color(204, 127, 234);
                buttonColor = new Color(61,169,166);
                fontColor1 = new Color(5, 5, 169);
                fontColor2 = new Color(0x2B6F6E);
            }
        }
}
