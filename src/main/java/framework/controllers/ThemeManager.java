package framework.controllers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    private static ThemeManager instance;
    private List<ThemeChangeListener> listeners = new ArrayList<>();

    private Color backgroundColor;
    private Color mainButtonColor;
    private Color mainButtonColorHover;
    private Color buttonColor;
    private Color buttonColorHover;
    private Color titleColor;
    private Color smallfontColor;
    private Color fontColor1;
    private Color fontColor2;
    private Color textfieldColor;
    private boolean darkMode = false;

    private ThemeManager() {
        setDarkMode(false); // Initialize with light mode
    }

    public static ThemeManager getInstance() {
        if (instance == null) instance = new ThemeManager();
        return instance;
    }

    public void addThemeChangeListener(ThemeChangeListener listener) {
        listeners.add(listener);
    }

    public void removeThemeChangeListener(ThemeChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyThemeChanged() {
        for (ThemeChangeListener listener : listeners) {
            listener.onThemeChanged();
        }
    }

    public Color getBackgroundColor() { return backgroundColor; }
    public Color getMainButtonColor() { return mainButtonColor; }
    public Color getMainButtonColorHover() { return mainButtonColorHover; }
    public Color getButtonColor() { return buttonColor; }
    public Color getButtonColorHover() { return buttonColorHover; }
    public Color getTitleColor() { return titleColor; }
    public Color getSmallfontColor() { return smallfontColor; }
    public Color getFontColor1() { return fontColor1; }
    public Color getFontColor2() { return fontColor2; }
    public Color getTextFieldColor(){return textfieldColor;}

    public void setBackgroundColor(Color color) { backgroundColor = color; }
    public void setMainButtonColor(Color color) { mainButtonColor = color; }
    public void setMainButtonColorHover(Color color) { mainButtonColorHover = color; }
    public void setButtonColor(Color color) { buttonColor = color; }
    public void setButtonColorHover(Color color) { buttonColorHover = color; }
    public void setTitleColor(Color color) { titleColor = color; }
    public void setSmallfontColor(Color color) { smallfontColor = color; }
    public void setFontColor1(Color color) { fontColor1 = color; }
    public void setFontColor2(Color color) { fontColor2 = color; }
    public void setTextFieldColor(Color color) {textfieldColor = color;}

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean dark) {
        darkMode = dark;
        if (dark) {
            backgroundColor = new Color(0, 0, 66);
            mainButtonColor = new Color(129, 45, 163);
            mainButtonColorHover = new Color(143, 50, 180);
            buttonColor = new Color(44, 124, 122);
            buttonColorHover = new Color(53, 147, 145);
            titleColor = new Color(205, 205, 255);
            smallfontColor = new Color(205, 205, 255);
            fontColor1 = new Color(205, 205, 255);
            fontColor2 = new Color(61, 169, 166);
            textfieldColor = new Color(226,226,255);
        } else {
            backgroundColor = new Color(247, 247, 255);
            mainButtonColor = new Color(184, 107, 214);
            mainButtonColorHover = new Color(204, 127, 234);
            buttonColor = new Color(61, 169, 166);
            buttonColorHover = new Color(81, 189, 186);
            titleColor = new Color(255, 255, 255);
            smallfontColor = new Color(255, 255, 255);
            fontColor1 = new Color(5, 5, 169);
            fontColor2 = new Color(0x2B6F6E);
            textfieldColor = new Color(255,255,255);
        }

        // Notify listeners after theme change
        notifyThemeChanged();
    }

    public interface ThemeChangeListener {
        void onThemeChanged();
    }
}
