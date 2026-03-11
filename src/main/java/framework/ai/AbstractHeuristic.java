package framework.ai;

import java.util.List;

/**
 * Abstracte basisklasse voor heuristieken die gebruikt worden in MCTS rollouts.
 *
 * === Wat doet een heuristic? ===
 * Normaal kiest MCTS tijdens de rollout (stap 3) een VOLLEDIG WILLEKEURIGE zet.
 * Dat werkt, maar is niet slim: soms kiest de AI een rampzalige zet puur door toeval.
 *
 * Een heuristic maakt de rollout SLIMMER door zetten te scoren.
 * In plaats van blindelings random te kiezen, geeft de heuristic elke zet een gewicht:
 *   - Goede zetten (bijv. een hoek in Reversi) krijgen een HOOG gewicht → worden vaker gekozen
 *   - Slechte zetten (bijv. naast een hoek) krijgen een LAAG gewicht → worden minder vaak gekozen
 *
 * === Hoe werkt gewogen selectie? ===
 * Stel er zijn 3 zetten met gewichten:
 *   Zet A: gewicht 10 (hoek → heel goed)
 *   Zet B: gewicht 1  (naast hoek → slecht)
 *   Zet C: gewicht 3  (normaal veld)
 *
 * Totaal = 14. De kans op elke zet:
 *   A: 10/14 = 71%   ← wordt vaak gekozen (goed!)
 *   B:  1/14 =  7%   ← wordt zelden gekozen (terecht)
 *   C:  3/14 = 21%   ← soms gekozen
 *
 * Dit is GEEN harde keuze (altijd de beste pakken) — er zit nog steeds randomness in.
 * Dat is belangrijk voor MCTS: je wilt variatie in de rollouts, maar wel SLIMME variatie.
 *
 * === Hoe gebruik je deze klasse? ===
 * 1. Maak een subklasse (bijv. ReversiHeuristic) die evaluateMove() implementeert
 * 2. Geef die heuristic mee aan de MCTS via de constructor
 * 3. MCTS gebruikt selectWeightedMove() in de rollout in plaats van pure random
 */
public abstract class AbstractHeuristic {

    /**
     * Geeft een gewicht (score) aan een specifieke zet op het bord.
     * Hoe hoger het gewicht, hoe vaker deze zet gekozen wordt in de rollout.
     *
     * Subklassen implementeren hier hun domeinkennis, bijv:
     *   - Reversi: hoeken = hoog gewicht, naast hoeken = laag gewicht
     *   - Schaken: zetten die het centrum controleren = hoger gewicht
     *
     * @param row    De rij van de zet (0-indexed)
     * @param col    De kolom van de zet (0-indexed)
     * @param player De speler die de zet doet ('B' of 'W')
     * @return Een positief gewicht (>= 1). Hoger = betere zet.
     */
    public abstract double evaluateMove(int row, int col, char player);

    /**
     * Kiest een zet uit de lijst op basis van gewogen kansen.
     *
     * Werking stap voor stap:
     * 1. Bereken het gewicht van elke zet via evaluateMove()
     * 2. Tel alle gewichten op (= totaalGewicht)
     * 3. Genereer een random getal tussen 0 en totaalGewicht
     * 4. Loop door de zetten en trek steeds het gewicht af van het random getal
     * 5. Zodra het getal <= 0 wordt, kies die zet
     *
     * Voorbeeld met gewichten [10, 1, 3] en random = 8.5:
     *   - Zet A (10): 8.5 - 10 = -1.5 → negatief → kies Zet A!
     *
     * Voorbeeld met random = 12.0:
     *   - Zet A (10): 12.0 - 10 = 2.0 → nog positief, ga door
     *   - Zet B (1):  2.0 - 1  = 1.0 → nog positief, ga door
     *   - Zet C (3):  1.0 - 3  = -2.0 → negatief → kies Zet C!
     *
     * @param moves  Lijst van geldige zetten als [rij, kolom]
     * @param player De speler die aan zet is
     * @param random De Random-instantie voor reproduceerbare willekeur
     * @return De gekozen zet als [rij, kolom]
     */
    public int[] selectWeightedMove(List<int[]> moves, char player, java.util.Random random) {
        // Stap 1 + 2: bereken gewichten en tel op
        double totalWeight = 0.0;
        double[] weights = new double[moves.size()];

        for (int i = 0; i < moves.size(); i++) {
            int[] move = moves.get(i);
            // Vraag aan de subklasse hoe goed deze zet is
            weights[i] = evaluateMove(move[0], move[1], player);
            totalWeight += weights[i];
        }

        // Stap 3: genereer een random punt op de "gewichtslijn"
        double randomPoint = random.nextDouble() * totalWeight;

        // Stap 4 + 5: loop door de zetten, trek gewichten af, kies bij <= 0
        for (int i = 0; i < moves.size(); i++) {
            randomPoint -= weights[i];
            if (randomPoint <= 0) {
                return moves.get(i);  // Deze zet is gekozen!
            }
        }

        // Vangnet: door floating-point afrondingen kan randomPoint net > 0 blijven
        // In dat geval: kies de laatste zet
        return moves.get(moves.size() - 1);
    }
}
