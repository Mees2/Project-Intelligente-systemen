package framework.bordspel;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstracte basis klasse voor bordspellen
 * Bevat gemeenschappelijke functionaliteit voor TicTacToe, Reversi en andere bordspellen
 * Gebruikt de bestaande logica uit TicTacToe als basis
 */
public abstract class AbstractBordSpel {
    protected char[] bord;
    protected final int bordGrootte;        // Totaal aantal vakjes (9 voor TicTacToe, 64 voor Reversi)
    protected final int bordBreedte;        // Breedte van het bord (3 voor TicTacToe, 8 voor Reversi)
    protected final int bordHoogte;         // Hoogte van het bord (3 voor TicTacToe, 8 voor Reversi)
    protected final char leegSymbool;       // Symbool voor leeg vakje (' ' voor TicTacToe)
    protected SpelStatus status;
    
    /**
     * Constructor - initialiseert een nieuw spelbord
     * @param bordBreedte De breedte van het bord (aantal kolommen)
     * @param bordHoogte De hoogte van het bord (aantal rijen)
     * @param leegSymbool Het symbool voor een leeg vakje
     */
    protected AbstractBordSpel(int bordBreedte, int bordHoogte, char leegSymbool) {
        this.bordBreedte = bordBreedte;
        this.bordHoogte = bordHoogte;
        this.bordGrootte = bordBreedte * bordHoogte;
        this.leegSymbool = leegSymbool;
        this.bord = new char[bordGrootte];
        this.status = SpelStatus.NIET_GESTART;
        initialiseerBord();
    }
    
    /**
     * Initialiseert het bord met lege vakjes
     * Gebruikt de logica uit de originele TicTacToe constructor
     */
    protected void initialiseerBord() {
        for (int i = 0; i < bordGrootte; i++) {
            bord[i] = leegSymbool;
        }
        status = SpelStatus.BEZIG;
    }
    
    /**
     * Controleert of een speler heeft gewonnen
     * @param speler De speler om te controleren
     * @return true als de speler heeft gewonnen
     */
    public abstract boolean isWin(char speler);
    
    /**
     * Controleert of het spel gelijkspel is
     * @return true als het bord vol is zonder winnaar
     */
    public abstract boolean isDraw();
    
    /**
     * Voert een zet uit op het bord
     * Gebruikt de logica uit de originele TicTacToe.doMove()
     * @param pos De positie (index)
     * @param speler De speler
     */
    public void doMove(int pos, char speler) {
        if (isGeldigePositie(pos) && bord[pos] == leegSymbool) {
            bord[pos] = speler;
            updateStatus();
        }
    }
    
    /**
     * Voert een zet uit met een Zet object
     * @param zet De zet om uit te voeren
     */
    public void doMove(Zet zet) {
        doMove(zet.getIndex(), zet.getSpeler());
    }
    
    /**
     * Maakt een zet ongedaan (gebruikt door AI algoritmes)
     * Gebruikt de logica uit de originele TicTacToe.undoMove()
     * @param pos De positie om leeg te maken
     */
    public void undoMove(int pos) {
        if (isGeldigePositie(pos)) {
            bord[pos] = leegSymbool;
            status = SpelStatus.BEZIG; // Reset status bij undo
        }
    }
    
    /**
     * Controleert of een positie vrij is
     * Gebruikt de logica uit de originele TicTacToe.isFree()
     * @param pos De positie om te controleren
     * @return true als de positie vrij is
     */
    public boolean isFree(int pos) {
        return isGeldigePositie(pos) && bord[pos] == leegSymbool;
    }
    
    /**
     * Controleert of een positie geldig is (binnen de grenzen)
     * @param pos De positie om te controleren
     * @return true als de positie binnen de grenzen valt
     */
    protected boolean isGeldigePositie(int pos) {
        return pos >= 0 && pos < bordGrootte;
    }
    
    /**
     * Reset het bord naar de beginstaat
     */
    public void reset() {
        initialiseerBord();
    }
    
    /**
     * Krijg het symbool op een bepaalde positie
     * @param pos De positie (index)
     * @return Het symbool op die positie
     */
    public char getSymboolOp(int pos) {
        if (isGeldigePositie(pos)) {
            return bord[pos];
        }
        return leegSymbool;
    }
    
    /**
     * Krijg het symbool op een bepaalde rij en kolom
     * @param rij De rij
     * @param kolom De kolom
     * @return Het symbool op die positie
     */
    public char getSymboolOp(int rij, int kolom) {
        int index = rij * bordBreedte + kolom;
        return getSymboolOp(index);
    }
    
    /**
     * Krijg alle beschikbare zetten (lege posities)
     * @return Lijst van indices waar nog gezet kan worden
     */
    public List<Integer> getBeschikbareZetten() {
        List<Integer> zetten = new ArrayList<>();
        for (int i = 0; i < bordGrootte; i++) {
            if (isFree(i)) {
                zetten.add(i);
            }
        }
        return zetten;
    }
    
    /**
     * Update de spel status (win/draw/bezig)
     */
    protected void updateStatus() {
        if (isWin('X')) {
            status = SpelStatus.X_WINT;
        } else if (isWin('O')) {
            status = SpelStatus.O_WINT;
        } else if (isDraw()) {
            status = SpelStatus.GELIJKSPEL;
        } else {
            status = SpelStatus.BEZIG;
        }
    }
    
    /**
     * Krijg de huidige spel status
     * @return De status
     */
    public SpelStatus getStatus() {
        return status;
    }
    
    /**
     * Krijg de grootte van het bord (totaal aantal vakjes)
     * @return Het aantal vakjes op het bord
     */
    public int getBordGrootte() {
        return bordGrootte;
    }
    
    /**
     * Krijg de breedte van het bord
     * @return De breedte (aantal kolommen)
     */
    public int getBordBreedte() {
        return bordBreedte;
    }
    
    /**
     * Krijg de hoogte van het bord
     * @return De hoogte (aantal rijen)
     */
    public int getBordHoogte() {
        return bordHoogte;
    }
    
    /**
     * Krijg het leeg symbool
     * @return Het symbool voor lege vakjes
     */
    public char getLeegSymbool() {
        return leegSymbool;
    }
    
    /**
     * Controleert of het spel afgelopen is
     * @return true als het spel afgelopen is
     */
    public boolean isGameOver() {
        return status != SpelStatus.BEZIG && status != SpelStatus.NIET_GESTART;
    }
}
