import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    private static boolean turnX = true; // layer X (menselijk figuur persoon) begint
    private static JLabel statusLabel;
    private static JButton[] buttons = new JButton[9];
    private static TicTacToe game = new TicTacToe();
    private static boolean gameDone = false;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tic Tac Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 450);
        frame.setLayout(new BorderLayout()); //hier wordt de window gemaakt

        statusLabel = new JLabel("Beurt: X (jij)", JLabel.CENTER); //dit laat zien wie aan de beurt is en wie wint!
        frame.add(statusLabel, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3, 10, 10));

        for (int i = 0; i < 9; i++) { //buttons worden gemaakt
            JButton button = new JButton("");
            buttons[i] = button;
            button.setFont(button.getFont().deriveFont(40f)); //groot font

            final int pos = i;
            button.addActionListener(new ActionListener() { //hiermee word ervoor gezorgt dat we iets met de knoppen kunnen
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (gameDone || !turnX) return;// zolang deze condities correct zijn runt de code hieronder niet
                    if (!game.isFree(pos)) return; //je klikt op een knop en als het mag

                    game.doMove(pos, 'X'); //verantdert de knop naar x in logica
                    button.setText("X"); //en op het bord

                    if (checkEnd('X')) return; //als het de laatste move is doet hij niet de code eronder

                    turnX = false;
                    statusLabel.setText("Beurt: O (AI)"); //namelijk de beurt van de AI
                    doAiMove(); 
                }
            });

            panel.add(button);
        }

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static void doAiMove() {
        int move = MinimaxAI.bestMove(game, 'O', 'X'); //check Minimax.java als je wil weten wat er gebeurt
        if (move != -1) {
            game.doMove(move, 'O'); //logica
            buttons[move].setText("O"); //bord
        }

        if (checkEnd('O')) return;

        turnX = true;
        statusLabel.setText("Beurt: X (jij)");
    }

    private static boolean checkEnd(char layer) { //checkt op win en draw. check TicTacToe.java line 13 en 27 voor meer info
        if (game.isWin(layer)) {
            statusLabel.setText("speler " + layer + " wint!");
            gameDone = true;
            return true;
        } else if (game.isDraw()) {
            statusLabel.setText("Gelijkspel!");
            gameDone = true;
            return true;
        }
        return false;
    }
}