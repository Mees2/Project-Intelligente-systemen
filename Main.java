import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    // true = beurt X, false = beurt O
    private static boolean beurtX = true;

    public static void main(String[] args) {

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new GridLayout(3, 3, 10, 10));

        // 9 knoppen maken in een lus
        for (int i = 1; i <= 9; i++) {
            JButton button = new JButton(String.valueOf(i));

            // ActionListener toevoegen
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (button.getText().equals("X") || button.getText().equals("O")) {
                        return; // knop is al gezet, niks doen
                    }
                    if (beurtX) {
                        button.setText("X");
                    } else {
                        button.setText("O");
                    }
                    beurtX = !beurtX; // beurt wisselen
                }
            });

            frame.add(button);
        }

        frame.setVisible(true);
    }
}
