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
    private static boolean beurtX = true;
    private static JLabel statusLabel;
    private static JButton[] buttons = new JButton[9];
    private static boolean spelKlaar = false;

    public static void main(String[] args) {

        JFrame frame = new JFrame("Tic Tac Toe");
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
                    if (spelKlaar) return;
                    if (!button.getText().equals("")) return; // knop is al gezet

                    if (beurtX) {
                        button.setText("X");
                    } else {
                        button.setText("O");
                    }

                    if (checkWin()) {
                        statusLabel.setText("Speler " + (beurtX ? "X" : "O") + " wint!");
                        spelKlaar = true;
                        return;
                    } else if (checkDraw()) {
                        statusLabel.setText("Gelijkspel!");
                        spelKlaar = true;
                        return;
                    }

                    beurtX = !beurtX; // beurt wisselen
                    statusLabel.setText("Beurt: " + (beurtX ? "X" : "O"));
                }
            });

            panel.add(button);
        }

        frame.add(panel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private static boolean checkWin() {
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

    private static boolean checkDraw() {
        for (JButton b : buttons) {
            if (b.getText().equals("")) return false;
        }
        return true;
    }
}