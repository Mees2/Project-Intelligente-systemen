import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    // true = beurt X, false = beurt O
    private static boolean turnX = true;
    private static JLabel statusLabel; // hier staat de info over wiens beurt het is of wie gewonnen heeft.
    private static JButton[] buttons = new JButton[9];
    private static boolean gameDone = false; //wanneer true eindigt het spel

    public static void main(String[] args) {

        JFrame frame = new JFrame("Tic Tac Toe"); //hier wordt de frame/window gemaakt waarin wordt gespeeld
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 450);
        frame.setLayout(new BorderLayout());

        // statuslabel
        statusLabel = new JLabel("Beurt: X", JLabel.CENTER);
        frame.add(statusLabel, BorderLayout.NORTH);

        // speelveld
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3, 10, 10));

        // 9 knoppen maken in een lus
        for (int i = 0; i < 9; i++) {
            JButton button = new JButton("");
            buttons[i] = button;

            button.setFont(button.getFont().deriveFont(40f)); // grote letters
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (gameDone) return;
                    if (!button.getText().equals("")) return; // knop is al gezet

                    if (turnX) {
                        button.setText("X");
                    } else {
                        button.setText("O");
                    }

                    if (checkWin()) { //checkWin staat op line 74 in Main.java het checkt of een speler een winnende positie heeft.
                        statusLabel.setText("Speler " + (turnX ? "X" : "O") + " wint!");
                        gameDone = true;
                        return;
                    } else if (checkDraw()) { //checkDraw staat op line 92 in Main.java het checkt of er gelijkspel is
                        statusLabel.setText("Gelijkspel!");
                        gameDone = true;
                        return;
                    }

                    turnX = !turnX; // beurt wisselen: true wordt false, false wordt true
                    statusLabel.setText("Beurt: " + (turnX ? "X" : "O"));
                }
            });

            panel.add(button);
        }

        frame.add(panel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private static boolean checkWin() { // er worden winposities geinitialiceerd en dan wordt er gecheckt of er een geldig is en zo ja voor wie
        int[][] winPosities = {
            {0,1,2}, {3,4,5}, {6,7,8}, // rijen
            {0,3,6}, {1,4,7}, {2,5,8}, // kolommen
            {0,4,8}, {2,4,6}           // diagonalen
        };

        for (int[] w : winPosities) {
            String a = buttons[w[0]].getText();
            String b = buttons[w[1]].getText();
            String c = buttons[w[2]].getText();
            if (!a.equals("") && a.equals(b) && b.equals(c)) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkDraw() { //door te kijken of er lege vakjes op het bord zijn, 
        for (JButton b : buttons) { // checkt deze functie of er gelijkspel is.
            if (b.getText().equals("")) return false;
        }
        return true;
    }
}