import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    private static boolean beurtX = true; // speler X (menselijk figuur persoon) begint
    private static JLabel statusLabel;
    private static JButton[] buttons = new JButton[9];
    private static TicTacToe game = new TicTacToe();
    private static boolean spelKlaar = false;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tic Tac Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 450);
        frame.setLayout(new BorderLayout());

        statusLabel = new JLabel("Beurt: X (jij)", JLabel.CENTER);
        frame.add(statusLabel, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3, 10, 10));

        for (int i = 0; i < 9; i++) {
            JButton button = new JButton("");
            buttons[i] = button;
            button.setFont(button.getFont().deriveFont(40f));

            final int pos = i;
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (spelKlaar || !beurtX) return;
                    if (!game.isFree(pos)) return;

                    game.doMove(pos, 'X');
                    button.setText("X");

                    if (checkEinde('X')) return;

                    beurtX = false;
                    statusLabel.setText("Beurt: O (AI)");
                    doeAiZet();
                }
            });

            panel.add(button);
        }

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static void doeAiZet() {
        int move = MinimaxAI.bestMove(game, 'O', 'X');
        if (move != -1) {
            game.doMove(move, 'O');
            buttons[move].setText("O");
        }

        if (checkEinde('O')) return;

        beurtX = true;
        statusLabel.setText("Beurt: X (jij)");
    }

    private static boolean checkEinde(char speler) {
        if (game.isWin(speler)) {
            statusLabel.setText("Speler " + speler + " wint!");
            spelKlaar = true;
            return true;
        } else if (game.isDraw()) {
            statusLabel.setText("Gelijkspel!");
            spelKlaar = true;
            return true;
        }
        return false;
    }
}