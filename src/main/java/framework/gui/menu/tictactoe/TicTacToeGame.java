package framework.gui.menu.tictactoe;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

import framework.controllers.GameMode;
import framework.controllers.LanguageManager;
import framework.controllers.MenuManager;
import framework.controllers.ThemeManager;
import framework.gui.AbstractRoundedButton;
import tictactoe.TicTacToe;
import tictactoe.MinimaxAI;
import server.ClientTicTacToe;

public class TicTacToeGame extends AbstractRoundedButton {
    private final MenuManager menuManager;
    private final GameMode gameMode;
    private final LanguageManager lang = LanguageManager.getInstance();
    private final ThemeManager theme = ThemeManager.getInstance();
    private boolean turnX = true;
    private final String player1;
    private final String player2;
    private volatile boolean aiTurnPending = false;
    private volatile boolean aiBusy = false;

    private JLabel statusLabel;
    private JButton menuButton;
    private SquareBoardPanel boardPanel;
    private final TicTacToe game = new TicTacToe();
    private boolean gameDone = false;

    private char playerRole;
    private char aiRole;
    private ClientTicTacToe client;
    private volatile boolean loggedIn = false;
    private volatile boolean inGame = false;
    private String localPlayerName = "";
    private String opponentName = "";

    private int extractMovePosition(String serverMsg) {
        try {
            int moveIndex = serverMsg.indexOf("MOVE:");
            if (moveIndex == -1) return -1;

            String afterMove = serverMsg.substring(moveIndex + 5).trim();
            int start = afterMove.indexOf('"') + 1;
            int end = afterMove.indexOf('"', start);

            if (start > 0 && end > start) {
                String posStr = afterMove.substring(start, end);
                return Integer.parseInt(posStr);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse move position: " + e.getMessage());
        }
        return -1;
    }

    private String extractPlayerName(String serverMsg) {
        try {
            int playerIndex = serverMsg.indexOf("PLAYER:");
            if (playerIndex == -1) return "";

            String afterPlayer = serverMsg.substring(playerIndex + 7).trim();
            int start = afterPlayer.indexOf('"') + 1;
            int end = afterPlayer.indexOf('"', start);

            if (start > 0 && end > start) {
                return afterPlayer.substring(start, end);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse player name: " + e.getMessage());
        }
        return "";
    }

    private String extractFieldValue(String serverMsg, String fieldName) {
        try {
            int idx = serverMsg.indexOf(fieldName + ":");
            if (idx == -1) {
                idx = serverMsg.indexOf(fieldName.toUpperCase() + ":");
                if (idx == -1) return "";
            }
            String after = serverMsg.substring(idx + fieldName.length() + 1).trim();
            int start = after.indexOf('"') + 1;
            int end = after.indexOf('"', start);
            if (start > 0 && end > start) {
                return after.substring(start, end);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse field " + fieldName + ": " + e.getMessage());
        }
        return "";
    }

    private boolean isOurMove(String movePlayerName, String ourBaseName) {
        return movePlayerName.startsWith(ourBaseName);
    }

    private void applyOpponentMove(int pos, char symbol) {
        if (game.isGameOver() || !game.isFree(pos)) return;

        game.doMove(pos, symbol);
        boardPanel.getButtons()[pos].setText(String.valueOf(game.getSymbolAt(pos)));

        if (game.isGameOver()) {
            updateGameEndStatus();
            return;
        }

        turnX = (symbol == 'O');
        updateStatusLabel();

        if (gameMode == GameMode.TOURNAMENT && isPlayersTurn() && !aiBusy) {
            aiTurnPending = true;
            doAiMoveServer();
        }
    }

    public TicTacToeGame(MenuManager menuManager, String gameModeString, String player1, String player2) {
        this.menuManager = menuManager;
        this.gameMode = GameMode.fromCode(gameModeString);
        this.player1 = player1;
        this.player2 = player2;

        if (gameMode == GameMode.PVA) {
            if (player1.equals("AI")) {
                aiRole = 'X';
                playerRole = 'O';
            } else {
                aiRole = 'O';
                playerRole = 'X';
            }
        }

        theme.addThemeChangeListener(this::updateTheme);

        setPreferredSize(new Dimension(500, 600));

    }

    public void start() {
        // Checked of we in gamemode SERVER of TOURNAMENT zitten, zo ja, maak verbinding met de server en wacht op een tegenstander.
        // Zo niet? start het spel lokaal en skip alles server gerelateerd.
        if (!gameMode.isServerMode()) {
            initializeGame();
            return;

        }

        client = new ClientTicTacToe();
        if (!client.connectToServer()) {
            JOptionPane.showMessageDialog(null,
                    "Failed to connect to server",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            menuManager.onTicTacToeGameFinished();
            return;
        }
        // Luistert naar berichten van de server in een aparte thread en checked de inhoud, zodat wij accuraat kunnen blijven reageren op de UI.
        new Thread(() -> {
            try {
                String serverMsg;
                while (client.isConnected() && (serverMsg = client.getReader().readLine()) != null) {
                    System.out.println("[Server] " + serverMsg);

                    if (serverMsg.contains("OK")) {
                        loggedIn = true;
                    }

                    if (serverMsg.contains("MATCH")) {
                        inGame = true;
                        String playerToMove = extractFieldValue(serverMsg, "PLAYERTOMOVE");
                        String opponent = extractFieldValue(serverMsg, "OPPONENT");

                        // bepaald of de speler die aan de beurt is onze lokale speler is.
                        boolean playerToMoveIsLocal = false;
                        if (!playerToMove.isEmpty() && !localPlayerName.isEmpty()) {
                            if (playerToMove.startsWith(localPlayerName) || localPlayerName.startsWith(playerToMove))
                                playerToMoveIsLocal = true;
                        }

                        final boolean starterIsLocal = playerToMoveIsLocal;
                        final String opponentFinal = opponent;

                        SwingUtilities.invokeLater(() -> {
                            opponentName = opponentFinal != null ? opponentFinal : "";

                            // Server bepaald wie welke rol krijgt op basis van wie er start.
                            playerRole = starterIsLocal ? 'X' : 'O';
                            aiRole = playerRole; // In TOURNAMENT gamemode, AI speelt met onze rol.

                            // Zet de juiste beurt op basis van wie er start. (X gaat altijd eerst)
                            turnX = true;
                            updateStatusLabel();

                            // geplande AI zet als wij de starter zijn (onze symbool is X)
                            if (gameMode == GameMode.TOURNAMENT && playerRole == 'X') {
                                aiTurnPending = true;
                                if (!aiBusy) doAiMoveServer();
                            }
                        });
                    }

                    if (serverMsg.contains("YOURTURN")) {
                        SwingUtilities.invokeLater(() -> {
                            // zorgt ervoor dat de beurt status overeenkomt met ons symbool
                            turnX = (playerRole == 'X');
                            updateStatusLabel();

                            if (gameMode == GameMode.TOURNAMENT && !aiBusy) {
                                aiTurnPending = true;
                                doAiMoveServer();
                            }
                        });
                    }
                    // Verwerkt zet (MOVE) berichten van de server
                    if (serverMsg.contains("MOVE") && serverMsg.contains("PLAYER:")) {
                        String playerName = extractPlayerName(serverMsg);
                        int movePos = extractMovePosition(serverMsg);

                        // Bepaalt onze basisnaam voor vergelijking
                        String ourName = gameMode == GameMode.PVP ? player1 :
                                gameMode.isServerMode() ? localPlayerName :
                                        (playerRole == 'X' ? player1 : player2);

                        System.out.println("[DEBUG] Move from player: '" + playerName + "', our name: '" + ourName + "', our role: " + playerRole);

                        if (movePos != -1) {
                            // checkt of de zet van onszelf is of van de tegenstander. is de zet van ons? negeer de zet anders pas de zet toe.
                            if (isOurMove(playerName, ourName)) {
                                System.out.println("[DEBUG] Recognized as OUR move - ignoring");
                                aiBusy = false;
                                aiTurnPending = false;
                            } else {
                                System.out.println("[DEBUG] Recognized as OPPONENT move - applying");
                                char opponentSymbol = (playerRole == 'X') ? 'O' : 'X';
                                System.out.println("[DEBUG] Applying opponent symbol: " + opponentSymbol + " at position: " + movePos);
                                SwingUtilities.invokeLater(() -> applyOpponentMove(movePos, opponentSymbol));
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Connection lost: " + e.getMessage());
            }
        }, "server-listener").start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }

        // Bepaalt de spelersnaam voor login op basis van gamemode.
        String playerName;
        if (gameMode.isServerMode()) {
            playerName = player1.equals("AI") ? player2 : player1;
        } else if (gameMode == GameMode.PVP) {
            playerName = player1;
        } else {
            playerName = (playerRole == 'X' ? player1 : player2);
        }

        localPlayerName = playerName;
        client.login(playerName);

        // Wacht tot we ingelogd zijn of timeout na 2 seconden
        int attempts = 0;
        while (!loggedIn && attempts < 20) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            attempts++;
        }
        // Als inloggen mislukt, toon foutmelding en keer terug naar menu
        if (!loggedIn) {
            JOptionPane.showMessageDialog(null, "Login failed", "Error", JOptionPane.ERROR_MESSAGE);
            client.shutdown();
            menuManager.onTicTacToeGameFinished();
            return;
        }

        client.requestMatch();
        initializeGame();
    }

    private void initializeGame() {
        turnX = true;
        gameDone = false;
        game.reset();

        setLayout(new BorderLayout());
        setBackground(ThemeManager.getInstance().getBackgroundColor());

        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        statusLabel.setForeground(ThemeManager.getInstance().getFontColor1());
        statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(statusLabel, BorderLayout.NORTH);

        boardPanel = new SquareBoardPanel();
        boardPanel.setBackground(ThemeManager.getInstance().getBackgroundColor());
        add(boardPanel, BorderLayout.CENTER);

        if (gameMode == GameMode.TOURNAMENT) {
            for (JButton b : boardPanel.getButtons()) {
                b.setEnabled(false);
            }
        }

        menuButton = createRoundedButton(lang.get("tictactoe.game.menu"),
                new Color(184, 107, 214), new Color(204, 127, 234), new Color(120, 60, 150), true);
        menuButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        menuButton.setPreferredSize(new Dimension(320, 45));
        menuButton.addActionListener(e -> returnToMenu());

        JPanel southPanel = new JPanel();
        southPanel.setBackground(ThemeManager.getInstance().getBackgroundColor());
        southPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));
        southPanel.add(menuButton);
        add(southPanel, BorderLayout.SOUTH);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                boardPanel.resizeFont();
                statusLabel.setFont(statusLabel.getFont().deriveFont((float) Math.max(18, getHeight() / 25)));
            }
        });
        updateStatusLabel();

        if (gameMode == GameMode.PVA && !isPlayersTurn()) {
            doAiMove();
        }
    }

    private class SquareBoardPanel extends JPanel {
        private final JButton[] buttons;

        public SquareBoardPanel() {
            buttons = new JButton[game.getBoardSize()];
            setLayout(null);
            setBackground(ThemeManager.getInstance().getBackgroundColor());
            for (int i = 0; i < game.getBoardSize(); i++) {
                JButton btn = createTicTacToeButton("", ThemeManager.getInstance().getTitleColor(), ThemeManager.getInstance().getFontColor1(), new Color(120, 60, 150), true);
                btn.setFocusPainted(false);
                btn.setFont(new Font("SansSerif", Font.BOLD, 40));
                final int pos = i;
                if (gameMode != GameMode.TOURNAMENT) {
                    btn.addActionListener(e -> handleButtonClick(pos));
                }
                buttons[i] = btn;
                add(btn);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            layoutButtons();
        }

        private void layoutButtons() {
            int size = Math.min(getWidth(), getHeight());
            int marginX = (getWidth() - size) / 2;
            int marginY = (getHeight() - size) / 2;
            int cell = size / game.getBoardWidth();
            int gap = Math.max(6, cell / 15);

            for (int row = 0; row < game.getBoardHeight(); row++) {
                for (int col = 0; col < game.getBoardWidth(); col++) {
                    int idx = row * game.getBoardWidth() + col;
                    JButton btn = buttons[idx];
                    int x = marginX + col * cell + gap / 2;
                    int y = marginY + row * cell + gap / 2;
                    int w = cell - gap;
                    int h = cell - gap;
                    btn.setBounds(x, y, w, h);
                }
            }
        }

        public void resizeFont() {
            int size = Math.min(getWidth(), getHeight());
            int fontSize = Math.max(24, size / 6);
            for (JButton btn : buttons) {
                btn.setFont(new Font("SansSerif", Font.BOLD, fontSize));
            }
        }

        public JButton[] getButtons() {
            return buttons;
        }
    }

    private void handleButtonClick(int pos) {
        if (game.isGameOver() || !game.isFree(pos)) return;
        if (gameMode == GameMode.PVA && !isPlayersTurn()) return;
        if (gameMode == GameMode.TOURNAMENT) return;

        if (client != null && client.isConnected()) {
            client.sendMove(pos);
        }

        char currentPlayer = turnX ? 'X' : 'O';
        game.doMove(pos, currentPlayer);
        boardPanel.getButtons()[pos].setText(String.valueOf(game.getSymbolAt(pos)));

        if (game.isGameOver()) {
            updateGameEndStatus();
            return;
        }

        turnX = (currentPlayer == 'O');
        updateStatusLabel();

        if (gameMode == GameMode.PVA && !isPlayersTurn() && !game.isGameOver()) {
            doAiMove();
        }
    }


    private void doAiMove() {
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            List<Integer> availableMoves = game.getAvailableMoves();
            if (availableMoves.isEmpty()) return;

            char opponentSymbol = (aiRole == 'X') ? 'O' : 'X';
            int move = MinimaxAI.bestMove(game, aiRole, opponentSymbol);

            if (move != -1 && game.isFree(move)) {
                game.doMove(move, aiRole);
                boardPanel.getButtons()[move].setText(String.valueOf(game.getSymbolAt(move)));
            }

            if (game.isGameOver()) {
                updateGameEndStatus();
                return;
            }

            turnX = (aiRole == 'O');
            updateStatusLabel();
        });
    }

    private void doAiMoveServer() {
        if (gameMode != GameMode.TOURNAMENT) return;
        if (game.isGameOver() || client == null || !client.isConnected()) return;

        if (!aiTurnPending || aiBusy) return;
        aiBusy = true;
        aiTurnPending = false;

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                aiBusy = false;
                return;
            }

            List<Integer> availableMoves = game.getAvailableMoves();
            if (availableMoves.isEmpty()) {
                aiBusy = false;
                return;
            }

            char opponentSymbol = (aiRole == 'X') ? 'O' : 'X';
            int move = MinimaxAI.bestMove(game, aiRole, opponentSymbol);

            if (move == -1) {
                aiBusy = false;
                return;
            }

            client.sendMove(move);

            SwingUtilities.invokeLater(() -> {
                if (game.isGameOver() || !game.isFree(move)) {
                    aiBusy = false;
                    return;
                }

                game.doMove(move, aiRole);
                boardPanel.getButtons()[move].setText(String.valueOf(game.getSymbolAt(move)));

                if (game.isGameOver()) {
                    updateGameEndStatus();
                    aiBusy = false;
                    return;
                }

                turnX = (aiRole == 'O');
                updateStatusLabel();
                aiBusy = false;
            });
        }, "tournament-ai").start();
    }

    private void updateGameEndStatus() {
        gameDone = true;
        switch (game.getStatus()) {
            case X_WINS:
                String xWinner = getNameBySymbol('X');
                statusLabel.setText(lang.get("tictactoe.game.win", xWinner + " (X)"));
                break;
            case O_WINS:
                String oWinner = getNameBySymbol('O');
                statusLabel.setText(lang.get("tictactoe.game.win", oWinner + " (O)"));
                break;
            case DRAW:
                statusLabel.setText(lang.get("tictactoe.game.draw"));
                break;
            default:
                break;
        }
    }

    private boolean isPlayersTurn() {
        return (turnX ? 'X' : 'O') == playerRole;
    }


    private String getNameBySymbol(char symbol) {
        if (gameMode == GameMode.PVP) {
            return (symbol == 'X') ? player1 : player2;
        } else if (gameMode == GameMode.PVA) {
            if (symbol == playerRole) return (playerRole == 'X') ? player1 : player2;
            else return "AI";
        } else if (gameMode.isServerMode()) {
            if (symbol == playerRole) return localPlayerName != null ? localPlayerName : "";
            else return opponentName != null ? opponentName : "";
        }
        return "";
    }

    private void updateStatusLabel() {
        if (game.isGameOver()) return;
        char currentSymbol = turnX ? 'X' : 'O';
        String currentName = getNameBySymbol(currentSymbol);
        // in gamemode Server of Tournament, laat een tijdelijk wacht bericht zien totdat we de naam van de tegenstander hebben ontvangen van de gameserver.
        if (gameMode.isServerMode() && (opponentName == null || opponentName.isEmpty())) {
            statusLabel.setText(lang.get("tictactoe.game.turn", lang.get("tictactoe.game.waitingfordetails")));
            return;
        }
        statusLabel.setText(lang.get("tictactoe.game.turn", currentName + " (" + currentSymbol + ")"));
    }

    private void updateTheme() {
        setBackground(theme.getBackgroundColor());
        statusLabel.setForeground(theme.getFontColor1());
        boardPanel.setBackground(theme.getBackgroundColor());

        menuButton.putClientProperty("baseColor", theme.getMainButtonColor());
        menuButton.putClientProperty("hoverColor", theme.getMainButtonColorHover());
        menuButton.putClientProperty("borderColor", theme.getMainButtonColor().darker());
        menuButton.repaint();

        for (JButton btn : boardPanel.getButtons()) {
            btn.putClientProperty("baseColor", theme.getTitleColor());
            btn.putClientProperty("hoverColor", theme.getTitleColor());
            btn.putClientProperty("borderColor", new Color(120, 60, 150));
            btn.repaint();
        }

        repaint();
    }
    private void returnToMenu() {
        int option = JOptionPane.showConfirmDialog(this,
                lang.get("main.exit.confirm"),
                lang.get("main.exit.title"),
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            if (gameMode.isServerMode() && client != null) {
                try {
                    client.quit();
                } catch (Exception e) {
                    System.err.println("Error while quitting the game: " + e.getMessage());
                    try {
                        client.shutdown();
                    } catch (Exception ignored) {
                    }
                }
            }
            menuManager.onTicTacToeGameFinished();
        }
    }
}
