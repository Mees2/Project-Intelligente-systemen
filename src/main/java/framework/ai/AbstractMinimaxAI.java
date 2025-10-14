package framework.ai;

import framework.bordspel.AbstractBordSpel;

/**
 * Abstracte basis klasse voor Minimax AI
 * Bevat het minimax algoritme dat werkt voor TicTacToe, Reversi en andere bordspellen
 * Gebruikt de logica uit de originele MinimaxAI implementatie
 */
public abstract class AbstractMinimaxAI {
    
    /**
     * Vindt de beste zet voor de AI speler
     * Gebruikt de originele logica uit TicTacToe.MinimaxAI.bestMove()
     * @param spel Het bordspel
     * @param aiSpeler Het symbool van de AI speler
     * @param tegenstanderSpeler Het symbool van de tegenstander
     * @return De positie van de beste zet, of -1 als geen zet mogelijk is
     */
    public static int bestMove(AbstractBordSpel spel, char aiSpeler, char tegenstanderSpeler) {
        int bestScore = Integer.MIN_VALUE; // Start zo laag mogelijk om straks te verbeteren
        int move = -1; // Houdt de index van de beste zet bij
        
        // Doorloop alle mogelijke zetten
        for (int i = 0; i < spel.getBordGrootte(); i++) {
            if (spel.isFree(i)) { // Alleen lege vakjes zijn geldig
                spel.doMove(i, aiSpeler); // Simuleer zet voor de AI
                int score = minimax(spel, 0, false, aiSpeler, tegenstanderSpeler); // Laat minimax bepalen hoe goed deze zet is
                spel.undoMove(i); // Zet weer terugdraaien
                
                // Als deze zet beter is dan vorige, onthoud hem
                if (score > bestScore) {
                    bestScore = score;
                    move = i;
                }
            }
        }
        return move; // Geef het beste gevonden vak terug
    }
    
    /**
     * Het recursieve minimax-algoritme dat de score van een bordpositie berekent
     * Gebruikt de originele logica uit TicTacToe.MinimaxAI.minimax()
     * @param spel Het bordspel
     * @param depth De diepte van de recursie (voor score berekening)
     * @param isMax true als de AI aan de beurt is (maximizer), false als de tegenstander aan de beurt is (minimizer)
     * @param aiSpeler Het symbool van de AI speler
     * @param tegenstanderSpeler Het symbool van de tegenstander
     * @return De score van de huidige positie
     */
    protected static int minimax(AbstractBordSpel spel, int depth, boolean isMax, char aiSpeler, char tegenstanderSpeler) {
        // Basiscases: kijk of iemand gewonnen heeft of dat het gelijkspel is
        if (spel.isWin(aiSpeler)) {
            return 10 - depth;   // AI wint → hoge score, snellere winst is beter
        }
        if (spel.isWin(tegenstanderSpeler)) {
            return depth - 10; // Tegenstander wint → lage score
        }
        if (spel.isDraw()) {
            return 0; // Geen zetten meer → gelijkspel
        }
        
        int best;
        if (isMax) {
            // Als de AI aan de beurt is (maximizer)
            best = Integer.MIN_VALUE;
            for (int i = 0; i < spel.getBordGrootte(); i++) {
                if (spel.isFree(i)) {
                    spel.doMove(i, aiSpeler); // Simuleer AI-zet
                    best = Math.max(best, minimax(spel, depth + 1, false, aiSpeler, tegenstanderSpeler)); // Kijk naar beste antwoord van de tegenstander
                    spel.undoMove(i); // Zet ongedaan maken
                }
            }
        } else {
            // Als de tegenstander aan de beurt is (minimizer)
            best = Integer.MAX_VALUE;
            for (int i = 0; i < spel.getBordGrootte(); i++) {
                if (spel.isFree(i)) {
                    spel.doMove(i, tegenstanderSpeler); // Simuleer zet van de tegenstander
                    best = Math.min(best, minimax(spel, depth + 1, true, aiSpeler, tegenstanderSpeler)); // Kijk naar slechtste scenario voor AI
                    spel.undoMove(i); // Zet ongedaan maken
                }
            }
        }
        return best; // Geeft de hoogst/laagst gevonden score terug
    }
}
