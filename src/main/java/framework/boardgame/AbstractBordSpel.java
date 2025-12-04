package framework.boardgame;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstracte basis klasse voor bordspellen
 * Bevat gemeenschappelijke functionaliteit voor TicTacToe, Reversi en andere bordspellen
 * Gebruikt de bestaande logica uit TicTacToe als basis
 */
public abstract class AbstractBoardGame {
    protected char[] board;
    protected final int boardSize;        // Totaal aantal vakjes (9 voor TicTacToe, 64 voor Reversi)
    protected final int boardWidth;        // Breedte van het bord (3 voor TicTacToe, 8 voor Reversi)
    protected final int boardHeight;         // Hoogte van het bord (3 voor TicTacToe, 8 voor Reversi)
    protected final char emptySymbol;       // Symbool voor leeg vakje (' ' voor TicTacToe)
    protected GameStatus status;
    
    /**
     * Constructor - initialiseert een nieuw spelbord
     * @param boardWidth De breedte van het bord (aantal kolommen)
     * @param boardHeight De hoogte van het bord (aantal rijen)
     * @param emptySymbol Het symbool voor een leeg vakje
     */
    protected AbstractBoardGame(int boardWidth, int boardHeight, char emptySymbol) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.boardSize = boardWidth * boardHeight;
        this.emptySymbol = emptySymbol;
        this.board = new char[boardSize];
        this.status = GameStatus.NOT_STARTED;
        initializeBoard();
    }
    
    /**
     * Initialiseert het bord met lege vakjes
     * Gebruikt de logica uit de originele TicTacToe constructor
     */
    protected void initializeBoard() {
        for (int i = 0; i < boardSize; i++) {
            board[i] = emptySymbol;
        }
        status = GameStatus.IN_PROGRESS;
    }
    
    /**
     * Controleert of een speler heeft gewonnen
     * @param player De speler om te controleren
     * @return true als de speler heeft gewonnen
     */
    public abstract boolean isWin(char player);
    
    /**
     * Controleert of het spel gelijkspel is
     * @return true als het bord vol is zonder winnaar
     */
    public abstract boolean isDraw();
    
    /**
     * Voert een zet uit op het bord
     * Gebruikt de logica uit de originele TicTacToe.doMove()
     * @param pos De positie (index)
     * @param player De speler
     */
    public void doMove(int pos, char player) {
        if (isValidPosition(pos) && board[pos] == emptySymbol) {
            board[pos] = player;
            updateStatus();
        }
    }

    /**
     * Voert een zet uit met een Zet object
     * @param move De zet om uit te voeren
     */
    public void doMove(Move move) {
        doMove(move.getIndex(), move.getPlayer());
    }

    /**
     * Maakt een zet ongedaan (gebruikt door AI algoritmes)
     * Gebruikt de logica uit de originele TicTacToe.undoMove()
     * @param pos De positie om leeg te maken
     */
    public void undoMove(int pos) {
        if (isValidPosition(pos)) {
            board[pos] = emptySymbol;
            status = GameStatus.IN_PROGRESS; // Reset status bij undo
        }
    }

    /**
     * Controleert of een positie vrij is
     * Gebruikt de logica uit de originele TicTacToe.isFree()
     * @param pos De positie om te controleren
     * @return true als de positie vrij is
     */
    public boolean isFree(int pos) {
        return isValidPosition(pos) && board[pos] == emptySymbol;
    }

    /**
     * Controleert of een positie geldig is (binnen de grenzen)
     * @param pos De positie om te controleren
     * @return true als de positie binnen de grenzen valt
     */
    protected boolean isValidPosition(int pos) {
        return pos >= 0 && pos < boardSize;
    }

    /**
     * Reset het bord naar de beginstaat
     */
    public void reset() {
        initializeBoard();
    }

    /**
     * Krijg het symbool op een bepaalde positie
     * @param pos De positie (index)
     * @return Het symbool op die positie
     */
    public char getSymbolAt(int pos) {
        if (isValidPosition(pos)) {
            return board[pos];
        }
        return emptySymbol;
    }

    /**
     * Krijg het symbool op een bepaalde rij en kolom
     * @param row De rij
     * @param column De kolom
     * @return Het symbool op die positie
     */
    public char getSymbolAt(int row, int column) {
        int index = row * boardWidth + column;
        return getSymbolAt(index);
    }

    /**
     * Krijg alle beschikbare zetten (lege posities)
     * @return Lijst van indices waar nog gezet kan worden
     */
    public List<Integer> getAvailableMoves() {
        List<Integer> moves = new ArrayList<>();
        for (int i = 0; i < boardSize; i++) {
            if (isFree(i)) {
                moves.add(i);
            }
        }
        return moves;
    }

    /**
     * Update de spel status (win/draw/bezig)
     */
    protected void updateStatus() {
        if (isWin('X')) {
            status = GameStatus.X_WINS;
        } else if (isWin('O')) {
            status = GameStatus.O_WINS;
        } else if (isDraw()) {
            status = GameStatus.DRAW;
        } else {
            status = GameStatus.IN_PROGRESS;
        }
    }

    /**
     * Krijg de huidige spel status
     * @return De status
     */
    public GameStatus getStatus() {
        return status;
    }

    /**
     * Krijg de grootte van het bord (totaal aantal vakjes)
     * @return Het aantal vakjes op het bord
     */
    public int getBoardSize() {
        return boardSize;
    }

    /**
     * Krijg de breedte van het bord
     * @return De breedte (aantal kolommen)
     */
    public int getBoardWidth() {
        return boardWidth;
    }

    /**
     * Krijg de hoogte van het bord
     * @return De hoogte (aantal rijen)
     */
    public int getBoardHeight() {
        return boardHeight;
    }
    
    /**
     * Krijg het leeg symbool
     * @return Het symbool voor lege vakjes
     */
    public char getEmptySymbol() {
        return emptySymbol;
    }

    /**
     * Controleert of het spel afgelopen is
     * @return true als het spel afgelopen is
     */
    public boolean isGameOver() {
        return status != GameStatus.IN_PROGRESS && status != GameStatus.NOT_STARTED;
    }
}