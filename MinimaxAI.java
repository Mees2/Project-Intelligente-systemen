public class MinimaxAI {

    // Bepaalt de beste zet voor de AI op basis van de huidige toestand van het spelbord
    public static int bestMove(TicTacToe game, char aiPlayer, char humanPlayer) {
        int bestScore = Integer.MIN_VALUE; // Start zo laag mogelijk om straks te verbeteren
        int move = -1; // Houdt de index van de beste zet bij

        // Doorloop alle mogelijke zetten (0 t/m 8)
        for (int i = 0; i < 9; i++) {
            if (game.isFree(i)) { // Alleen lege vakjes zijn geldig
                game.doMove(i, aiPlayer); // Simuleer zet voor de AI
                int score = minimax(game, false, aiPlayer, humanPlayer); // Laat minimax bepalen hoe goed deze zet is
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

    // Het recursieve minimax-algoritme dat de score van een bordpositie berekent
    private static int minimax(TicTacToe game, boolean isMax, char aiPlayer, char humanPlayer) {
        // Basiscases: kijk of iemand gewonnen heeft of dat het gelijkspel is
        if (game.isWin(aiPlayer)) return 10;   // AI wint → hoge score
        if (game.isWin(humanPlayer)) return -10; // Mens wint → lage score
        if (game.isDraw()) return 0; // Geen zetten meer → gelijkspel

        // Als de AI aan de beurt is (maximizer)
        if (isMax) {
            int best = Integer.MIN_VALUE; // Start laag om straks maximum te nemen
            for (int i = 0; i < 9; i++) {
                if (game.isFree(i)) {
                    game.doMove(i, aiPlayer); // Simuleer AI-zet
                    best = Math.max(best, minimax(game, false, aiPlayer, humanPlayer)); // Kijk naar beste antwoord van de tegenstander
                    game.undoMove(i); // Zet ongedaan maken
                }
            }
            return best; // Geeft de hoogst gevonden score terug
        } 
        // Als de mens aan de beurt is (minimizer)
        else {
            int best = Integer.MAX_VALUE; // Start hoog om straks minimum te nemen
            for (int i = 0; i < 9; i++) {
                if (game.isFree(i)) {
                    game.doMove(i, humanPlayer); // Simuleer zet van de mens
                    best = Math.min(best, minimax(game, true, aiPlayer, humanPlayer)); // Kijk naar slechtste scenario voor AI
                    game.undoMove(i); // Zet ongedaan maken
                }
            }
            return best; // Geeft de laagst gevonden score terug
        }
    }
}