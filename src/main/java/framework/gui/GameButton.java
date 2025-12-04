package framework.gui;

import javax.swing.JButton;

/**
 * Aangepaste JButton voor spelbord vakjes
 * Houdt de positie (index) bij voor gebruik in TicTacToe en Reversi
 */
public class GameButton extends JButton {
    private final int position;
    
    /**
     * Constructor voor een spel knop
     * @param position De positie (index) van deze knop op het bord
     */
    public GameButton(int position) {
        super("");
        this.position = position;
        setFont(getFont().deriveFont(40f));
    }
    
    /**
     * Constructor met tekst
     * @param text De initiële tekst
     * @param position De positie (index) van deze knop op het bord
     */
    public GameButton(String text, int position) {
        super(text);
        this.position = position;
        setFont(getFont().deriveFont(40f));
    }
    
    /**
     * Krijg de positie van deze knop
     * @return De positie (index)
     */
    public int getPosition() {
        return position;
    }
}
