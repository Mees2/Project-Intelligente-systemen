package tictactoe;

import framework.ai.AbstractMinimaxAI;

/**
 * MinimaxAI klasse voor TicTacToe
 * Gebruikt het framework voor herbruikbare AI functionaliteit
 * Kan ook gebruikt worden voor andere bordspellen zoals Reversi
 */
public class MinimaxAI extends AbstractMinimaxAI {

    /**
     * Bepaalt de beste zet voor de AI op basis van de huidige toestand van het spelbord
     * Gebruikt de minimax algoritme uit het framework
     * @param game Het huidige TicTacToe spel
     * @param aiPlayer Het symbool van de AI-speler ('X' of 'O')
     * @param humanPlayer Het symbool van de menselijke speler ('X' of 'O')
     * @return De index van de beste zet (0-8), of -1 als geen zet mogelijk is
     */
    public static int bestMove(TicTacToe game, char aiPlayer, char humanPlayer) {
        return AbstractMinimaxAI.bestMove(game, aiPlayer, humanPlayer);
    }
}