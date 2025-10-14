package framework.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import framework.bordspel.AbstractBordSpel;
import framework.bordspel.SpelStatus;
import framework.spelers.AbstractSpeler;
import menu.LanguageManager;
import menu.MenuManager;

/**
 * Abstracte basis klasse voor een spel GUI
 * Bevat gemeenschappelijke functionaliteit voor TicTacToeGame en toekomstige Reversi GUI
 * Gebruikt de structuur uit de originele TicTacToeGame implementatie
 */
public abstract class AbstractSpelGUI {
    protected final MenuManager menuManager;
    protected final LanguageManager lang = LanguageManager.getInstance();
    protected final AbstractBordSpel spel;
    protected final AbstractSpeler speler1;
    protected final AbstractSpeler speler2;
    protected AbstractSpeler huidigeSpeler;
    
    protected JFrame gameFrame;
    protected JLabel statusLabel;
    protected JButton menuButton;
    protected boolean gameDone = false;
    
    /**
     * Constructor voor een spel GUI
     * @param menuManager De menu manager voor navigatie
     * @param spel Het bordspel
     * @param speler1 De eerste speler
     * @param speler2 De tweede speler
     */
    protected AbstractSpelGUI(MenuManager menuManager, AbstractBordSpel spel, AbstractSpeler speler1, AbstractSpeler speler2) {
        this.menuManager = menuManager;
        this.spel = spel;
        this.speler1 = speler1;
        this.speler2 = speler2;
        this.huidigeSpeler = speler1; // Speler 1 begint altijd
    }
    
    /**
     * Start het spel en toont de GUI
     */
    public void start() {
        initialiseerGUI();
    }
    
    /**
     * Initialiseert de GUI componenten
     * Subklassen kunnen dit overschrijven voor spel-specifieke UI
     */
    protected void initialiseerGUI() {
        gameFrame = new JFrame(getSpelTitel());
        gameFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        gameFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                returnToMenu();
            }
        });
        gameFrame.setSize(getFrameBreedte(), getFrameHoogte());
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setLayout(new BorderLayout());
        
        // Status label bovenaan
        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setFont(statusLabel.getFont().deriveFont(18f));
        gameFrame.add(statusLabel, BorderLayout.NORTH);
        
        // Spelbord in het midden
        JPanel bordPanel = maakBordPanel();
        gameFrame.add(bordPanel, BorderLayout.CENTER);
        
        // Menu knop onderaan
        menuButton = new JButton(lang.get("tictactoe.game.menu"));
        menuButton.addActionListener(e -> returnToMenu());
        gameFrame.add(menuButton, BorderLayout.SOUTH);
        
        gameFrame.setVisible(true);
        
        updateStatusLabel();
        
        // Als AI begint, doe de eerste zet
        if (huidigeSpeler.isAI()) {
            doeAIZet();
        }
    }
    
    /**
     * Maak het spelbord panel
     * Subklassen moeten dit implementeren voor spel-specifieke borden
     * @return Het panel met het spelbord
     */
    protected abstract JPanel maakBordPanel();
    
    /**
     * Krijg de titel van het spel venster
     * @return De titel
     */
    protected abstract String getSpelTitel();
    
    /**
     * Krijg de breedte van het game frame
     * @return De breedte in pixels
     */
    protected int getFrameBreedte() {
        return 400;
    }
    
    /**
     * Krijg de hoogte van het game frame
     * @return De hoogte in pixels
     */
    protected int getFrameHoogte() {
        return 450;
    }
    
    /**
     * Update het status label met de huidige speler
     */
    protected void updateStatusLabel() {
        if (gameDone) {
            return;
        }
        
        SpelStatus status = spel.getStatus();
        switch (status) {
            case BEZIG:
                if (huidigeSpeler.isAI()) {
                    statusLabel.setText(lang.get("tictactoe.game.turn.ai"));
                } else {
                    statusLabel.setText(lang.get("tictactoe.game.turn", huidigeSpeler.getNaam()));
                }
                break;
            case X_WINT:
                String winnaarnaamX = speler1.getSymbool() == 'X' ? speler1.getNaam() : speler2.getNaam();
                statusLabel.setText(lang.get("tictactoe.game.win", winnaarnaamX));
                gameDone = true;
                break;
            case O_WINT:
                String winnaarnaamO = speler1.getSymbool() == 'O' ? speler1.getNaam() : speler2.getNaam();
                statusLabel.setText(lang.get("tictactoe.game.win", winnaarnaamO));
                gameDone = true;
                break;
            case GELIJKSPEL:
                statusLabel.setText(lang.get("tictactoe.game.draw"));
                gameDone = true;
                break;
            default:
                break;
        }
    }
    
    /**
     * Wissel van huidige speler
     */
    protected void wisselSpeler() {
        huidigeSpeler = (huidigeSpeler == speler1) ? speler2 : speler1;
    }
    
    /**
     * Doe een AI zet
     * Subklassen kunnen dit overschrijven voor spel-specifieke AI logica
     */
    protected abstract void doeAIZet();
    
    /**
     * Keer terug naar het menu
     */
    protected void returnToMenu() {
        if (gameFrame != null) {
            gameFrame.dispose();
        }
        menuManager.returnToMainMenu();
    }
    
    /**
     * Update de GUI teksten (voor taal wijzigingen)
     */
    public void updateUITexts() {
        if (gameFrame != null) {
            gameFrame.setTitle(getSpelTitel());
        }
        if (menuButton != null) {
            menuButton.setText(lang.get("tictactoe.game.menu"));
        }
        updateStatusLabel();
    }
}
