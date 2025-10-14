package framework.gui;

import javax.swing.JButton;

/**
 * Aangepaste JButton voor spelbord vakjes
 * Houdt de positie (index) bij voor gebruik in TicTacToe en Reversi
 */
public class SpelKnop extends JButton {
    private final int positie;
    
    /**
     * Constructor voor een spel knop
     * @param positie De positie (index) van deze knop op het bord
     */
    public SpelKnop(int positie) {
        super("");
        this.positie = positie;
        setFont(getFont().deriveFont(40f));
    }
    
    /**
     * Constructor met tekst
     * @param tekst De initiële tekst
     * @param positie De positie (index) van deze knop op het bord
     */
    public SpelKnop(String tekst, int positie) {
        super(tekst);
        this.positie = positie;
        setFont(getFont().deriveFont(40f));
    }
    
    /**
     * Krijg de positie van deze knop
     * @return De positie (index)
     */
    public int getPositie() {
        return positie;
    }
}
