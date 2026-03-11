package reversi;

import framework.ai.AbstractHeuristic;

/**
 * Reversi-specifieke heuristic voor MCTS rollouts.
 *
 * === Waarom deze heuristic? ===
 * In Reversi zijn niet alle velden gelijkwaardig:
 *   - HOEKEN (0,0), (0,7), (7,0), (7,7) zijn extreem waardevol
 *     → eenmaal bezet kunnen ze NOOIT meer omgedraaid worden
 *   - Velden NAAST hoeken zijn gevaarlijk
 *     → ze geven de tegenstander vaak toegang tot de hoek
 *   - Randen zijn redelijk goed → moeilijker om te draaien
 *   - Centervelden zijn neutraal
 *
 * Deze kennis wordt vertaald naar gewichten:
 *   Hoog gewicht = zet wordt vaker gekozen in de rollout
 *   Laag gewicht = zet wordt zelden gekozen (maar nog steeds mogelijk!)
 *
 * === Effect op MCTS ===
 * Zonder heuristic: rollout kiest random → veel "domme" potjes → minder betrouwbare statistieken
 * Met heuristic: rollout kiest SLIMMER → realistischere potjes → betere beslissingen
 *
 * De boom hoeft niet te veranderen — alleen de kwaliteit van de rollouts verbetert.
 */
public class ReversiHeuristic extends AbstractHeuristic {

    // Gewichtenmatrix: hoe waardevol is elk veld op het 8x8 bord?
    // Gebaseerd op bekende Reversi-strategie:
    //
    //   100 = hoek          → beste positie, kan niet omgedraaid worden
    //    50 = rand           → redelijk veilig, moeilijk te veroveren
    //    10 = normaal veld   → standaard waarde
    //     1 = naast hoek     → gevaarlijk! geeft tegenstander hoektoegang
    //
    // Voorbeeld: als de rollout kan kiezen tussen (0,0) en (0,1):
    //   (0,0) gewicht 100, (0,1) gewicht 1 → kans op (0,0) = 100/101 ≈ 99%
    //   → bijna altijd de hoek, wat strategisch correct is
    private static final double[][] WEIGHT_MAP = {
        {100,  1, 50, 10, 10, 50,  1, 100},  // Rij 0: hoeken links/rechts, naast-hoek gevaarlijk
        {  1,  1, 10, 10, 10, 10,  1,   1},  // Rij 1: naast-hoek rij, de (1,1) is berucht slecht
        { 50, 10, 10, 10, 10, 10, 10,  50},  // Rij 2: rand links/rechts
        { 10, 10, 10, 10, 10, 10, 10,  10},  // Rij 3: centrum
        { 10, 10, 10, 10, 10, 10, 10,  10},  // Rij 4: centrum
        { 50, 10, 10, 10, 10, 10, 10,  50},  // Rij 5: rand links/rechts
        {  1,  1, 10, 10, 10, 10,  1,   1},  // Rij 6: naast-hoek rij
        {100,  1, 50, 10, 10, 50,  1, 100},  // Rij 7: hoeken links/rechts
    };

    /**
     * Geeft het gewicht van een zet op basis van de positie op het bord.
     *
     * De speler-parameter wordt hier niet gebruikt omdat de positiewaarde
     * in Reversi niet afhangt van wie er speelt — een hoek is voor iedereen goed.
     * (In een geavanceerdere versie zou je ook de bord-toestand kunnen meewegen.)
     *
     * @param row    De rij (0-7)
     * @param col    De kolom (0-7)
     * @param player De speler ('B' of 'W') — niet gebruikt in deze implementatie
     * @return Het gewicht van deze positie (1-100)
     */
    @Override
    public double evaluateMove(int row, int col, char player) {
        return WEIGHT_MAP[row][col];
    }
}
