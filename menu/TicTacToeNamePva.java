package menu;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TicTacToeNamePva extends JFrame {
    private final MenuManager menuManager;
    private final LanguageManager lang = LanguageManager.getInstance();

    // Store UI components as fields
    private JLabel titleLabel;
    private JLabel speler1Label;
    private JLabel rolLabel;
    private JButton startButton;
    private JButton backButton;
    
    public TicTacToeNamePva(MenuManager menuManager) {
        this.menuManager = menuManager;
        initializeMenu();
    }

    private void initializeMenu() {
        setTitle(lang.get("tictactoe.name.title"));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(247,247,255));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel(lang.get("tictactoe.name.title"), JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(5,5,169));


        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.setBackground(new Color(247,247,255));

        add(topPanel, BorderLayout.NORTH);

        // kleur body tekst
        Color bodyTextColor = new Color(0x2B6F6E);


        speler1Label = new JLabel(lang.get("tictactoe.name.playername"));
        speler1Label.setForeground(bodyTextColor);

        JTextField textField1 = new JTextField();
        textField1.setPreferredSize(new Dimension(250, 40));
        textField1.setMaximumSize(new Dimension(500, 40));
        textField1.setAlignmentX(Component.CENTER_ALIGNMENT);
        textField1.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1, 0, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        rolLabel = new JLabel(lang.get("tictactoe.name.selectrole"));
        rolLabel.setForeground(bodyTextColor);

        JRadioButton xbutton = new JRadioButton("X");
        xbutton.setForeground(bodyTextColor);
        JRadioButton obutton = new JRadioButton("O");
        obutton.setForeground(bodyTextColor);
        
        // achtergrondkleur radio buttons
        xbutton.setBackground(new Color(247, 247, 255));
        obutton.setBackground(new Color(247, 247, 255));

        // gebruikt achtergrondkleur van radiobuttons
        xbutton.setOpaque(true);
        obutton.setOpaque(true);


        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(xbutton);
        roleGroup.add(obutton);

        buttonPanel.add(speler1Label);
        buttonPanel.add(textField1);
        buttonPanel.add(rolLabel);
        buttonPanel.add(xbutton);
        buttonPanel.add(obutton);

        startButton = createRoundedButton(lang.get("tictactoe.name.startgame"),
        new Color(184,107,214),new Color(204,127,234), new Color(120,60,150), true);
        startButton.setFont(new Font("Arial", Font.PLAIN, 14));
        startButton.addActionListener(e -> {
            String spelerNaam = textField1.getText().trim();

            if (spelerNaam.isEmpty()) {
                JOptionPane.showMessageDialog(this, lang.get("tictactoe.name.error.emptyname"), lang.get("common.error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!xbutton.isSelected() && !obutton.isSelected()) {
                JOptionPane.showMessageDialog(this, lang.get("tictactoe.name.error.emptyrole"), lang.get("common.error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            this.hideMenu();

            if (xbutton.isSelected()) {
                menuManager.startTicTacToeGame("PVA", spelerNaam, "AI");
            } else {
                menuManager.startTicTacToeGame("PVA", "AI", spelerNaam);
            }
        });

        backButton = createRoundedButton(lang.get("tictactoe.name.back"),
        new Color(184,107,214),new Color(204,127,234), new Color(120,60,150), true);
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(e -> {
            this.hideMenu();
            menuManager.closeNameSelectionPVA();
        });

        buttonPanel.add(startButton);
        buttonPanel.add(backButton);
        buttonPanel.setBackground(new Color(247,247,255));

        add(buttonPanel, BorderLayout.CENTER);

            addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });
    }
// Gekopieerd van tictactoemenu
private JButton createRoundedButton(String text, Color baseColor, Color hoverColor, Color borderColor, boolean enabled){
    var btn = new JButton(text){
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isRollover() && isEnabled() ? hoverColor : baseColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 30, 30);
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public Dimension getPreferredSize() {
            double scale = 1.0;
            if (getParent() != null) {
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window != null) {
                    scale = Math.min(window.getWidth() / 500.0, window.getHeight() / 350.0);
                    scale = Math.max(0.7, Math.min(scale, 2.0));
                }
            }
            int scaledWidth = (int)(200 * scale);
            int scaledHeight = (int)(35 * scale);
            return new Dimension(scaledWidth, scaledHeight);
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

    };

    btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
    btn.setForeground(enabled ? Color.WHITE : new Color(100,100,100));
    btn.setContentAreaFilled(false);
    btn.setOpaque(false);
    btn.setFocusPainted(false);
    btn.setBorderPainted(false);
    btn.setRolloverEnabled(true);
    btn.setEnabled(enabled);
    btn.setAlignmentX(Component.CENTER_ALIGNMENT);

    return btn;
}

private void resizeComponents() {
    double scale = Math.min(getWidth() / 500.0, getHeight() / 350.0);
    scale = Math.max(0.7, Math.min(scale, 2.0));
    resizeAllButtons(this, scale);
    revalidate();
    repaint();
}

private void resizeAllButtons(Container container, double scale) {
    for (Component comp : container.getComponents()) {
        if (comp instanceof JButton) {
            JButton btn = (JButton) comp;
            int newFontSize = (int)(12 * scale);
            btn.setFont(btn.getFont().deriveFont(Font.PLAIN, newFontSize));
            int newWidth = (int)(200 * scale);
            int newHeight = (int)(35 * scale);
            Dimension newSize = new Dimension(newWidth, newHeight);
            btn.setPreferredSize(newSize);
            btn.setMinimumSize(newSize);
            btn.setMaximumSize(newSize);
        } else if (comp instanceof JLabel) {
            JLabel label = (JLabel) comp;

            if (label == titleLabel) {
                int newTitleSize = (int)(25 * scale);
                newTitleSize = Math.max(18, Math.min(newTitleSize, 40));
                label.setFont(label.getFont().deriveFont(Font.BOLD, newTitleSize));
            } else if (label == speler1Label || label == rolLabel) {
                int newLabelSize = (int)(18 * scale);
                newLabelSize = Math.max(14, Math.min(newLabelSize, 30));
                label.setFont(label.getFont().deriveFont(Font.PLAIN, newLabelSize));
            }
        } else if (comp instanceof JTextField) {
            JTextField field = (JTextField) comp;
            int newFontSize = (int)(14 * scale);
            field.setFont(field.getFont().deriveFont(Font.PLAIN, newFontSize));
            field.setMaximumSize(new Dimension(500, (int)(40 * scale)));

        } else if (comp instanceof Container) {
            resizeAllButtons((Container) comp, scale);
        }
    }
}
    
    public void updateLanguage() {
        setTitle(lang.get("tictactoe.name.title"));
        titleLabel.setText(lang.get("tictactoe.name.title"));
        speler1Label.setText(lang.get("tictactoe.name.playername"));
        rolLabel.setText(lang.get("tictactoe.name.selectrole"));
        startButton.setText(lang.get("tictactoe.name.startgame"));
        backButton.setText(lang.get("tictactoe.name.back"));
    }

    public void showMenu() {
        setVisible(true);
    }

    public void hideMenu() {
        setVisible(false);
    }
}
