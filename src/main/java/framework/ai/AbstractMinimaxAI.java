package framework.ai;

import framework.boardgame.AbstractBoardGame;

/**
 * Abstracte basis klasse voor Minimax AI
 * Bevat het minimax algoritme dat werkt voor TicTacToe, Reversi en andere bordspellen
 * Gebruikt de logica uit de originele MinimaxAI implementatie
 */
public abstract class AbstractMinimaxAI {
    
    /**
     * Vindt de beste zet voor de AI speler
     * Gebruikt de originele logica uit TicTacToe.MinimaxAI.bestMove()
     * @param game Het bordspel
     * @param aiPlayer Het symbool van de AI speler
     * @param opponentPlayer Het symbool van de tegenstander
     * @return De positie van de beste zet, of -1 als geen zet mogelijk is
     */
    public static int bestMove(AbstractBoardGame game, char aiPlayer, char opponentPlayer) {
        int bestScore = Integer.MIN_VALUE; // Start zo laag mogelijk om straks te verbeteren
        int move = -1; // Houdt de index van de beste zet bij
        
        // Doorloop alle mogelijke zetten
        for (int i = 0; i < game.getBoardSize(); i++) {
            if (game.isFree(i)) { // Alleen lege vakjes zijn geldig
                game.doMove(i, aiPlayer); // Simuleer zet voor de AI
                int score = minimax(game, 0, false, aiPlayer, opponentPlayer); // Laat minimax bepalen hoe goed deze zet is
                game.undoMove(i); // Zet weer terugdraaien
                
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
     * @param game Het bordspel
     * @param depth De diepte van de recursie (voor score berekening)
     * @param isMax true als de AI aan de beurt is (maximizer), false als de tegenstander aan de beurt is (minimizer)
     * @param aiPlayer Het symbool van de AI speler
     * @param opponentPlayer Het symbool van de tegenstander
     * @return De score van de huidige positie
     */
    protected static int minimax(AbstractBoardGame game, int depth, boolean isMax, char aiPlayer, char opponentPlayer) {
        // Basiscases: kijk of iemand gewonnen heeft of dat het gelijkspel is
        if (game.isWin(aiPlayer)) {
            return 10 - depth;   // AI wint → hoge score, snellere winst is beter
        }
        if (game.isWin(opponentPlayer)) {
            return depth - 10; // Tegenstander wint → lage score
        }
        if (game.isDraw()) {
            return 0; // Geen zetten meer → gelijkspel
        }
        
        int best;
        if (isMax) {
            // Als de AI aan de beurt is (maximizer)
            best = Integer.MIN_VALUE;
            for (int i = 0; i < game.getBoardSize(); i++) {
                if (game.isFree(i)) {
                    game.doMove(i, aiPlayer); // Simuleer AI-zet
                    best = Math.max(best, minimax(game, depth + 1, false, aiPlayer, opponentPlayer)); // Kijk naar beste antwoord van de tegenstander
                    game.undoMove(i); // Zet ongedaan maken
                }
            }
        } else {
            // Als de tegenstander aan de beurt is (minimizer)
            best = Integer.MAX_VALUE;
            for (int i = 0; i < game.getBoardSize(); i++) {
                if (game.isFree(i)) {
                    game.doMove(i, opponentPlayer); // Simuleer zet van de tegenstander
                    best = Math.min(best, minimax(game, depth + 1, true, aiPlayer, opponentPlayer)); // Kijk naar slechtste scenario voor AI
                    game.undoMove(i); // Zet ongedaan maken
                }
            }
        }
        return best; // Geeft de hoogst/laagst gevonden score terug
    }
}
