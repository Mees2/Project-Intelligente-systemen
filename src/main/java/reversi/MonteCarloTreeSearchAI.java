package reversi;

import framework.ai.AbstractReversiAI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import framework.ai.AbstractHeuristic;


public class MonteCarloTreeSearchAI extends AbstractReversiAI {
    private int simulations = 1000;  // Hoeveel willekeurige spelletjes we simuleren per beurt (configureerbaar)
    private static final double EXPLORATION_CONSTANT = Math.sqrt(2); // C in de UCT formule: balans tussen exploitatie en exploratie
    private static final Random random = new Random(); // Voor alle willekeurige keuzes in het algoritme

    // === Heuristic: maakt de rollout slimmer ===
    // In plaats van puur random zetten te kiezen tijdens de rollout,
    // gebruikt de heuristic gewichten om BETERE zetten vaker te kiezen.
    // Bijv. hoeken krijgen gewicht 100, naast-hoek gewicht 1 → hoek wordt 100x vaker gekozen.
    // Als heuristic null is, valt MCTS terug op puur random (het oude gedrag).
    private final ReversiHeuristic heuristic;

    // === Constructor: maak een MCTS-AI MET heuristic ===
    // De ReversiHeuristic wordt meegegeven zodat de rollout slimmer speelt.
    // Voorbeeld: new MonteCarloTreeSearchAI(new ReversiHeuristic())
    public MonteCarloTreeSearchAI(ReversiHeuristic heuristic) {
        this.heuristic = heuristic;
    }

    // === Constructor zonder heuristic: puur random rollouts (oud gedrag) ===
    public MonteCarloTreeSearchAI() {
        this.heuristic = null;
    }

    // Configureerbaar: hoe diep de tree wordt geprint in de terminal
    private int maxPrintDepth = -1;

    /**
     * Stel het aantal simulaties in voor MCTS
     * @param simulations Het aantal simulaties per zet
     */
    public void setSimulations(int simulations) {
        this.simulations = simulations;
    }

    /**
     * Krijg het huidige aantal simulaties
     * @return Het aantal simulaties
     */
    public int getSimulations() {
        return simulations;
    }

    /**
     * Stel in hoe diep de tree geprint wordt in de terminal
     * @param depth Maximale diepte (0 = alleen root, -1 = alles)
     */
    public void setMaxPrintDepth(int depth) {
        this.maxPrintDepth = depth;
    }

    // === MCTSNode: één knooppunt in de zoekboom ===
    // Elke node = één specifieke bordpositie na een bepaalde zet.
    // De boom groeit per simulatie: elke simulatie voegt maximaal 1 nieuw kind toe.
    private static class MCTSNode {
        final MCTSNode parent;               // Ouder-node (null voor de root)
        final int row, col;                  // De zet die vanuit parent naar DEZE node leidde (-1,-1 voor root)
        final char playerToMove;             // Wie er in deze node aan de beurt is (niet wie de zet maakte!)
        final List<MCTSNode> children = new ArrayList<>();   // Kinderen die al aangemaakt zijn
        final List<int[]> untriedMoves;      // Geldige zetten die nog NIET als kind bestaan
        final int creationOrder;             // volgorde waarin dit kind is aangemaakt (0-based)

        int visits = 0;       // Hoe vaak deze node bezocht is (meer visits = betrouwbaarder)
        double wins = 0.0;    // Opgetelde reward vanuit perspectief van wie de zet MAAKTE (parent)

        MCTSNode(MCTSNode parent, int row, int col, char playerToMove, List<int[]> legalMoves) {
            this.parent = parent;
            this.row = row;
            this.col = col;
            this.playerToMove = playerToMove;
            this.untriedMoves = new ArrayList<>(legalMoves);
            this.creationOrder = (parent != null) ? parent.children.size() : 0;
        }

        // Root = het huidige echte bord, er is nog geen zet gedaan
        boolean isRoot() {
            return parent == null;
        }

        // Fully expanded = alle mogelijke zetten zijn al als kind-node aangemaakt
        // (maar die kinderen kunnen zelf nog onontdekte kleinkinderen hebben)
        boolean isFullyExpanded() {
            return untriedMoves.isEmpty();
        }
    }

    /**
     * Finds the best move for the AI player using Monte Carlo Tree Search
     *
     * @param game     The current Reversi game
     * @param aiPlayer The AI player symbol ('B' or 'W')
     * @return An array [row, col] representing the best move, or null if no move available
     */
    // === bestMove: de hoofdmethode die de beste zet bepaalt ===
    // Wordt aangeroepen door ReversiGameController als de AI aan zet is.
    // Voert simulaties uit en geeft de beste zet terug als [rij, kolom].
    public int[] bestMove(Reversi game, char aiPlayer) {
        // Geen geldige zetten? Dan passen (null teruggeven)
        if (!game.hasValidMove(aiPlayer)) return null;

        // Maak de root-node: het huidige bord, AI is aan zet, met alle mogelijke zetten
        List<int[]> rootMoves = getValidMovesAsArrays(game, aiPlayer);
        MCTSNode root = new MCTSNode(null, -1, -1, aiPlayer, rootMoves);

        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                              MCTS SIMULATIE LOG (per simulatie)                                ║");
        System.out.printf( "║  Speler: %c | Mogelijke zetten: %-3d | Simulaties: %-6d                                       ║%n",
                aiPlayer, rootMoves.size(), simulations);
        System.out.println("╠══════╤════════════════════════════════════════════════════════════════╤══════════╤════════════════╣");
        System.out.printf( "║ %-4s │ %-62s │ %-8s │ %-14s ║%n",
                "Sim", "Pad (selectie → expansie)", "Resultaat", "Nodes");
        System.out.println("╠══════╪════════════════════════════════════════════════════════════════╪══════════╪════════════════╣");

        // Voer simulaties uit. Elke simulatie doorloopt 4 stappen:
        for (int i = 0; i < simulations; i++) {
            // Kopieer het bord zodat we het origineel niet aanpassen
            Reversi gameCopy = copyGame(game);

            // STAP 1 - Selection: loop door de bestaande boom naar een veelbelovende node
            // Onderweg worden de zetten op gameCopy toegepast zodat het bord klopt
            List<MCTSNode> selectionPath = new ArrayList<>();
            MCTSNode node = selectNodeWithState(gameCopy, root, selectionPath);

            // STAP 2 - Expansion: als de node nog onbekende zetten heeft,
            // maak 1 nieuw kind aan en pas die zet toe op gameCopy
            MCTSNode expandedNode = null;
            if (!node.untriedMoves.isEmpty()) {
                node = expand(gameCopy, node);
                expandedNode = node;
            }

            // STAP 3 - Simulation (rollout): speel het spel uit vanaf de huidige positie.
            // MET heuristic: zetten worden gewogen (slimmer, realistischer)
            // ZONDER heuristic: puur willekeurig (sneller, maar minder nauwkeurig)
            // Geeft 1.0 (win), 0.0 (verlies), of 0.5 (gelijk)
            double r = simulateRollout(gameCopy, node.playerToMove, aiPlayer, heuristic);

            // STAP 4 - Backpropagation: stuur het resultaat terug omhoog
            // door de boom (van kind naar root). Elke node update zijn visits en wins.
            backpropagate(node, r, aiPlayer);

            // Log deze simulatie
            String pathStr = buildPathString(selectionPath, expandedNode);
            String resultStr = r == 1.0 ? "WIN" : r == 0.0 ? "LOSS" : "DRAW";
            System.out.printf( "║ %-4d │ %-62s │ %-8s │ %-14d ║%n",
                    i + 1, pathStr, resultStr, getTotalNodes(root));
        }

        System.out.println("╚══════╧════════════════════════════════════════════════════════════════╧══════════╧════════════════╝");

        // Na simulaties: kies het kind met de MEESTE visits (niet hoogste winrate!)
        // Meer visits = vaker bezocht = betrouwbaarder resultaat
        MCTSNode best = null;
        int bestVisits = -1;
        for (MCTSNode child : root.children) {
            if (child.visits > bestVisits) {
                bestVisits = child.visits;
                best = child;
            }
        }

        // Print samenvatting per kind
        printSummary(root, best);

        if (best == null) return null;
        return new int[]{best.row, best.col};
    }

    // ========================== SIMULATIE LOG HELPERS ==========================

    /**
     * Bouw een pad-string op: ROOT → Kind A → Kind A1 → *Kind A11*
     */
    private String buildPathString(List<MCTSNode> selectionPath, MCTSNode expandedNode) {
        StringBuilder sb = new StringBuilder("ROOT");
        for (MCTSNode n : selectionPath) {
            sb.append("→").append(getKindLabel(n));
        }
        if (expandedNode != null) {
            sb.append("→*").append(getKindLabel(expandedNode)).append("*");
        }
        return sb.toString();
    }

    /**
     * Truncate een string tot maxLen, met "..." als het te lang is
     */
    private String truncate(String str, int maxLen) {
        if (str.length() <= maxLen) return str;
        return str.substring(0, maxLen - 3) + "...";
    }

    /**
     * Print een samenvatting van alle kinderen van root + de gekozen zet
     */
    private void printSummary(MCTSNode root, MCTSNode chosenNode) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    SAMENVATTING                              ║");
        System.out.printf( "║  Totaal nodes: %-6d | Diepte: %-4d                        ║%n",
                getTotalNodes(root), getTreeDepth(root));
        System.out.println("╠══════════════════════════════════════════════════════════════╣");

        // Toon alle kinderen van root op creationOrder
        for (int i = 0; i < root.children.size(); i++) {
            MCTSNode child = root.children.get(i);
            double wr = child.visits > 0 ? (child.wins / child.visits) * 100 : 0;
            String bar = generateWinBar(wr);
            String label = getKindLabel(child);
            String marker = (child == chosenNode) ? " ◄◄ GEKOZEN" : "";
            System.out.printf("║  [%s] (%d,%d) vis=%-5d wr=%.1f%% %s%s%n",
                    label, child.row, child.col, child.visits, wr, bar, marker);
        }

        if (chosenNode != null) {
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            double chosenWr = chosenNode.visits > 0 ? (chosenNode.wins / chosenNode.visits) * 100 : 0;
            System.out.printf( "║  ► GEKOZEN ZET: [%s] → positie (%d,%d)                    %n",
                    getKindLabel(chosenNode), chosenNode.row, chosenNode.col);
            System.out.printf( "║    visits=%d  winrate=%.1f%%  van %d kinderen              %n",
                    chosenNode.visits, chosenWr, root.children.size());
        }
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    // ========================== TREE VISUALISATIE ==========================

    /**
     * Print de volledige MCTS-boom naar de terminal
     */
    private void printTree(MCTSNode root, MCTSNode chosenNode) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                   MCTS SIMULATIE TREE                       ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf( "║  Totaal nodes: %-6d | Diepte: %-4d | Simulaties: %-6d  ║%n",
                getTotalNodes(root), getTreeDepth(root), simulations);
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        // Print root node
        System.out.printf("ROOT [%c aan zet] visits=%d  wins=%.1f  winrate=%.1f%%%n",
                root.playerToMove, root.visits, root.wins,
                root.visits > 0 ? (root.wins / root.visits) * 100 : 0);

        // Toon children op volgorde van aanmaak (creationOrder) i.p.v. gesorteerd op visits
        List<MCTSNode> orderedChildren = new ArrayList<>(root.children);
        // children staan al op creationOrder omdat ze in die volgorde zijn toegevoegd

        for (int i = 0; i < orderedChildren.size(); i++) {
            boolean isLast = (i == orderedChildren.size() - 1);
            MCTSNode child = orderedChildren.get(i);
            String kindLabel = getKindLabel(child);
            printNode(child, root, "", isLast, 1, kindLabel, chosenNode);
        }

        // Print samenvatting: welk kind is gekozen
        System.out.println();
        if (chosenNode != null) {
            String chosenLabel = getKindLabel(chosenNode);
            double chosenWr = chosenNode.visits > 0 ? (chosenNode.wins / chosenNode.visits) * 100 : 0;
            System.out.println("╔══════════════════════════════════════════════════════════════╗");
            System.out.printf( "║  ► GEKOZEN ZET: [%s] → positie (%d,%d)                    %n",
                    chosenLabel, chosenNode.row, chosenNode.col);
            System.out.printf( "║    visits=%d  winrate=%.1f%%  van %d kinderen              %n",
                    chosenNode.visits, chosenWr, orderedChildren.size());
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
        }
        System.out.println();
        System.out.println("─────────────────── EINDE TREE ───────────────────");
        System.out.println();
    }

    /**
     * Recursief een node en al zijn children printen met tree-structuur
     */
    private void printNode(MCTSNode node, MCTSNode parent, String prefix, boolean isLast, int depth, String kindLabel, MCTSNode chosenNode) {
        // Stop als we de maximale printdiepte bereikt hebben (tenzij -1 = onbeperkt)
        if (maxPrintDepth >= 0 && depth > maxPrintDepth) return;

        String connector = isLast ? "└── " : "├── ";
        String childPrefix = isLast ? "    " : "│   ";

        double winRate = node.visits > 0 ? (node.wins / node.visits) * 100 : 0;
        double uct = parent != null ? uctValue(parent, node) : 0;

        // Markeer het gekozen kind met een pijl, en de best bezochte met een ster
        String marker = "";
        if (node == chosenNode) {
            marker = " ◄◄ GEKOZEN ZET";
        } else if (parent != null && !parent.children.isEmpty()) {
            MCTSNode bestChild = parent.children.stream()
                    .max(Comparator.comparingInt(n -> n.visits))
                    .orElse(null);
            if (bestChild == node) marker = " ★";
        }

        // Balk indicator gebaseerd op winrate
        String bar = generateWinBar(winRate);

        System.out.printf("%s%s[%s] (%d,%d) %c | vis=%d  wr=%.1f%% %s  uct=%.3f%s%n",
                prefix, connector,
                kindLabel,
                node.row, node.col,
                node.playerToMove,
                node.visits,
                winRate,
                bar,
                uct,
                marker);

        // Recursief children printen op volgorde van aanmaak (creationOrder)
        // children staan al op creationOrder omdat ze in die volgorde zijn toegevoegd
        for (int i = 0; i < node.children.size(); i++) {
            boolean childIsLast = (i == node.children.size() - 1);
            MCTSNode child = node.children.get(i);
            String childKindLabel = getKindLabel(child);
            printNode(child, node, prefix + childPrefix, childIsLast, depth + 1, childKindLabel, chosenNode);
        }
    }

    /**
     * Geeft het Kind-label op basis van creationOrder (A, B, C, ..., Z, AA, AB, ...)
     */
    private String getKindLabel(MCTSNode node) {
        if (node.parent == null) return "ROOT";
        // Bouw het label recursief op: parent-label + eigen letter
        String parentLabel = getKindLabel(node.parent);
        char letter = (char) ('A' + node.creationOrder);
        if (parentLabel.equals("ROOT")) {
            return "Kind " + letter;
        }
        return parentLabel + (node.creationOrder + 1);
    }

    /**
     * Genereer een visuele balk op basis van winrate (0-100%)
     */
    private String generateWinBar(double winRate) {
        int barLength = 10;
        int filled = (int) Math.round(winRate / 100.0 * barLength);
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            bar.append(i < filled ? "█" : "░");
        }
        bar.append("]");
        return bar.toString();
    }

    // === Selection: loop door de boom naar een veelbelovend knooppunt ===
    // Begin bij de root en kies steeds het kind met de hoogste UCT-waarde
    // totdat we een node vinden die nog niet fully expanded is.
    // Onderweg passen we de zetten toe op gameCopy zodat het bord klopt.
    //
    //   Pad als Root fully expanded is en UCT kiest A → A is fully expanded, UCT kiest A1 → A1 niet fully expanded → STOP
    private MCTSNode selectNodeWithState(Reversi copyGame, MCTSNode root, List<MCTSNode> path) {
        MCTSNode node = root;

        // Ga dieper zolang ALLE zetten al als kind bestaan EN er kinderen zijn
        while (node.isFullyExpanded() && !node.children.isEmpty()) {
            MCTSNode parent = node;        // Onthoud de ouder
            node = bestUctChild(node);      // Kies het kind met de hoogste UCT-score
            path.add(node);

            // Pas de zet toe op het gekopieerde bord.
            // parent.playerToMove deed de zet, node.row/col is de positie
            copyGame.doMove(node.row, node.col, parent.playerToMove);
        }
        // Geef de gevonden node terug. Deze heeft nog untried moves (of is een blad)
        return node;
    }

    // === bestUctChild: kies het kind met de hoogste UCT-waarde ===
    // Loop door alle kinderen, bereken UCT per kind, geef de beste terug.
    private static MCTSNode bestUctChild(MCTSNode node) {
        MCTSNode best = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (MCTSNode child : node.children) {
            double uct = uctValue(node, child);
            if (uct > bestValue) {
                bestValue = uct;
                best = child;
            }
        }
        return best;
    }

    // === UCT-formule: Upper Confidence Bound for Trees ===
    // UCT = winRate + C * sqrt(ln(parentVisits) / childVisits)
    //
    // - winRate (exploitatie): hoe goed scoort dit kind gemiddeld?
    //   Hoog = deze zet wint vaak → kies hem vaker
    //
    // - explore (exploratie): hoe weinig is dit kind bezocht t.o.v. de ouder?
    //   Hoog = weinig bezocht → probeer hem ook eens
    //
    // De balans zorgt ervoor dat we niet alleen de "beste" zet kiezen,
    // maar ook onbekende zetten verkennen die misschien nog beter zijn.
    private static double uctValue(MCTSNode parent, MCTSNode child) {
        // Nooit bezocht? Geef oneindige score → wordt altijd eerst gekozen
        if (child.visits == 0) return Double.POSITIVE_INFINITY;

        // Exploitatie: gemiddelde score van dit kind (0.0 tot 1.0)
        double winRate = child.wins / child.visits;

        // Exploratie: wordt groter als ouder vaak bezocht is maar dit kind weinig
        double lnParent = Math.log(Math.max(1, parent.visits));
        double explore = EXPLORATION_CONSTANT * Math.sqrt(lnParent / child.visits);

        return winRate + explore;
    }


    // === Expansion: voeg één nieuw kind toe aan de boom ===
    // Kies een willekeurige onbekende zet, maak er een kind-node van,
    // en pas de zet toe op het bord.
    //
    // Voorbeeld: node heeft untriedMoves = [(2,3), (4,5), (3,2)]
    //   → kies random (4,5) → maak Kind(4,5) → verwijder uit untriedMoves
    private static MCTSNode expand(Reversi game, MCTSNode node) {
        // Veiligheidscheck: niets te expanderen? Geef node zelf terug
        if (node.untriedMoves.isEmpty()) return node;

        // Kies een willekeurige zet uit de onbekende zetten
        int idx = random.nextInt(node.untriedMoves.size());
        // Verwijder hem uit de lijst (zodat hij niet opnieuw gekozen wordt)
        int[] move = node.untriedMoves.remove(idx);

        // Pas de zet toe op het bord. node.playerToMove is degene die aan zet is in deze node
        game.doMove(move[0], move[1], node.playerToMove);

        // Normaal is de tegenstander nu aan de beurt
        char nextPlayer = getOpponent(node.playerToMove);
        List<int[]> nextMoves = getValidMovesAsArrays(game, nextPlayer);

        // PASS handling: in Reversi kan een speler soms niet zetten.
        // Als de tegenstander geen zetten heeft maar de huidige speler WEL,
        // dan "past" de tegenstander en blijft dezelfde speler aan zet.
        if (nextMoves.isEmpty() && game.hasValidMove(node.playerToMove)) {
            nextPlayer = node.playerToMove;
            nextMoves = getValidMovesAsArrays(game, nextPlayer);
        }

        // Maak het nieuwe kind aan en voeg toe aan de boom
        MCTSNode child = new MCTSNode(node, move[0], move[1], nextPlayer, nextMoves);
        node.children.add(child);
        return child;
    }


    // === Simulation (Rollout): speel het spel willekeurig uit ===
    // Vanaf de huidige bordpositie doen beide spelers om de beurt
    // een WILLEKEURIGE geldige zet totdat het spel voorbij is.
    // De zetten worden NIET in de boom opgeslagen — alleen het eindresultaat telt.
    //
    // Parameters:
    //   game         = het gekopieerde bord (al bijgewerkt door selection + expansion)
    //   playerToMove = wie er nu aan de beurt is
    //   rootPlayer   = de AI ('B' of 'W'), nodig om de reward te berekenen
    private static double simulateRollout(Reversi game, char playerToMove, char rootPlayer, AbstractHeuristic heuristic) {
        char current = playerToMove;  // Wie er nu aan de beurt is (wisselt elke beurt)
        int passCount = 0;            // Telt hoeveel keer ACHTER ELKAAR een speler moest passen

        // Ga door zolang het spel niet afgelopen is EN niet 2x achter elkaar gepast
        // passCount >= 2 = beide spelers konden niet zetten = spel is voorbij
        while (!isTerminal(game) && passCount < 2) {
            // Bereken geldige zetten voor de huidige speler
            List<int[]> moves = getValidMovesAsArrays(game, current);

            if (moves.isEmpty()) {
                // Geen zetten mogelijk → deze speler PAST
                passCount++;                       // Tel de pass (B past = 1, W past ook = 2 → stop)
                current = getOpponent(current);     // Wissel naar de andere speler
                continue;                           // Probeer of die wél kan zetten
            }

            // Er is wél een geldige zet → reset passCount
            // (passCount telt alleen OPEENVOLGENDE passes)
            passCount = 0;

            // Kies een zet:
            // MET heuristic → gewogen keuze (goede zetten worden vaker gekozen)
            // ZONDER heuristic → puur random (elke zet even waarschijnlijk)
            int[] move;
            if (heuristic != null) {
                // Gebruik de heuristic: selectWeightedMove() kiest op basis van gewichten
                // Bijv. hoek (gewicht 100) wordt veel vaker gekozen dan naast-hoek (gewicht 1)
                move = heuristic.selectWeightedMove(moves, current, random);
            } else {
                // Geen heuristic meegegeven → oud gedrag: puur random
                move = moves.get(random.nextInt(moves.size()));
            }

            game.doMove(move[0], move[1], current);
            current = getOpponent(current);  // Wissel van speler
        }

        // Spel is voorbij → bereken de score vanuit het perspectief van de AI
        return reward(game, rootPlayer);
    }

    // === isTerminal: is het spel afgelopen? ===
    // True als: iemand heeft gewonnen, het is gelijk, of GEEN van beide spelers kan nog zetten
    private static boolean isTerminal(Reversi game) {
        if (game.isWin('B') || game.isWin('W') || game.isDraw()) return true;
        return !game.hasValidMove('B') && !game.hasValidMove('W');
    }

    // === reward: bereken de score van het eindresultaat ===
    // Altijd vanuit perspectief van rootPlayer (de AI):
    //   1.0 = AI wint
    //   0.0 = AI verliest
    //   0.5 = gelijkspel
    private static double reward(Reversi game, char rootPlayer) {
        char opp = getOpponent(rootPlayer);

        if (game.isWin(rootPlayer)) return 1.0;  // AI wint → maximale beloning
        if (game.isWin(opp)) return 0.0;          // Tegenstander wint → geen beloning
        if (game.isDraw()) return 0.5;             // Gelijkspel → halve beloning

        // Fallback: als isWin/isDraw het niet detecteert, tel de stenen
        int diff = game.count(rootPlayer) - game.count(opp);
        if (diff > 0) return 1.0;  // Meer stenen = win
        if (diff < 0) return 0.0;  // Minder stenen = verlies
        return 0.5;                // Evenveel stenen = gelijk
    }

    // === Backpropagation: stuur het resultaat terug omhoog door de boom ===
    // Voorbeeld: rootPlayer = 'B', reward = 1.0 (B won)
    //
    //   Root (B aan zet)           → isRoot         → wins += 1.0   (AI perspectief)
    //     └── Kind A (W aan zet)   → parent is B    → wins += 1.0   (B deed de zet, B won = goed)
    //           └── Kind B (B aan zet) → parent is W → wins += 0.0  (W deed de zet, B won = slecht voor W)
    //                 └── Kind C (W aan zet) → parent is B → wins += 1.0 (B deed de zet, B won = goed)
    //
    // Elke node slaat op hoe goed de zet was voor de speler die hem deed.
    // Zo kan bestUctChild altijd "kies de hoogste winrate" doen,
    // en elke speler kiest automatisch wat goed is voor zichzelf.
    private static void backpropagate(MCTSNode node, double reward, char rootPlayer) {
        while (node != null) {
            node.visits++;

            if (node.isRoot()) {
                // Root is altijd de AI → tel reward direct op
                node.wins += reward;
            } else if (node.parent.playerToMove == rootPlayer) {
                // De AI (rootPlayer) maakte de zet naar dit kind
                // AI won? reward = 1.0 → deze zet was goed → wins += 1.0
                // AI verloor? reward = 0.0 → deze zet was slecht → wins += 0.0
                node.wins += reward;
            } else {
                // De tegenstander maakte de zet naar dit kind
                // AI won? reward = 1.0 → voor tegenstander slecht → wins += 0.0 (= 1.0 - 1.0)
                // AI verloor? reward = 0.0 → voor tegenstander goed → wins += 1.0 (= 1.0 - 0.0)
                node.wins += (1.0 - reward);
            }

            node = node.parent;
        }
    }

    // Voeg toe aan de klasse
    private int getTreeDepth(MCTSNode root) {
        if (root.children.isEmpty()) return 0;
        int maxDepth = 0;
        for (MCTSNode child : root.children) {
            maxDepth = Math.max(maxDepth, getTreeDepth(child));
        }
        return 1 + maxDepth;
    }

    private int getTotalNodes(MCTSNode root) {
        int count = 1;
        for (MCTSNode child : root.children) {
            count += getTotalNodes(child);
        }
        return count;
    }
}
