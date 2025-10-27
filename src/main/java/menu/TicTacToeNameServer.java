package menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TicTacToeNameServer extends JFrame{

        private final MenuManager menuManager;
        private final LanguageManager lang = LanguageManager.getInstance();
        private final ThemeManager theme = ThemeManager.getInstance();

        private JLabel titleLabel;
        private JLabel speler1Label;
        private JLabel rolLabel;
        private JPanel topPanel;
        private JPanel centerPanel;
        private JTextField textField1;
        private JButton startButton;
        private JButton backButton;

        public TicTacToeNameServer(MenuManager menuManager) {
            this.menuManager = menuManager;
            initializeMenu();
            theme.addThemeChangeListener(this::updateTheme);

            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    resizeComponents();
                }
            });

        }
        /**-
         * Initialiseert de tictactoenamepva interface test***
         */
        private void initializeMenu() {
            setTitle("tictactoe.name.title");
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            setSize(500, 350);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());
            getContentPane().setBackground(new Color(247, 247, 255));

            topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
            topPanel.setBackground(new Color(247, 247, 255));
            // Titel
            titleLabel = new JLabel(lang.get("tictactoe.name.title"), JLabel.CENTER);
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setForeground(new Color(5, 5, 169));

            topPanel.add(Box.createVerticalStrut(10));
            topPanel.add(titleLabel);
            topPanel.add(Box.createVerticalStrut(5));

            add(topPanel, BorderLayout.NORTH);

            centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
            centerPanel.setBackground(new Color(247, 247, 255));
            // Achtergrondkleur
            Color bodyTextColor = new Color(0x2B6F6E);
            // Speler 1 tekst
            speler1Label = new JLabel(lang.get("tictactoe.name.playername"));
            speler1Label.setForeground(bodyTextColor);
            speler1Label.setAlignmentX(Component.CENTER_ALIGNMENT);
            // Speler 1 inputveld
            textField1 = new JTextField();
            textField1.setFont(new Font("SansSerif", Font.PLAIN, 14));
            textField1.setMaximumSize(new Dimension(500, 40));
            textField1.setAlignmentX(Component.CENTER_ALIGNMENT);
            // start tictactoe SERVER
            startButton = createRoundedButton(lang.get("tictactoe.name.startgame"),
                    new Color(184, 107, 214), new Color(204, 127, 234), new Color(120, 60, 150), true);
            startButton.addActionListener(e -> {
                String spelerNaam = textField1.getText().trim();
                if (spelerNaam.isEmpty()) {
                    JOptionPane.showMessageDialog(this, lang.get("tictactoe.name.error.emptyname"),
                            lang.get("common.error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                //menuManager.startTicTacToeGame("SERVER", spelerNaam, "AI"); });

                hideMenu();
                menuManager.startTicTacToeGame("SERVER", "AI", spelerNaam); });


            // gaat terug
            backButton = createRoundedButton(lang.get("tictactoe.name.back"),
                    new Color(184, 107, 214), new Color(204, 127, 234), new Color(120, 60, 150), true);
            backButton.addActionListener(e -> {
                hideMenu();
                menuManager.closeNameSelectionPVA();

            });
            // add en ruimte ertussen
            centerPanel.add(speler1Label);
            centerPanel.add(Box.createVerticalStrut(3));
            centerPanel.add(textField1);
            centerPanel.add(Box.createVerticalStrut(40));
            centerPanel.add(startButton);
            centerPanel.add(Box.createVerticalStrut(10));
            centerPanel.add(backButton);

            add(centerPanel, BorderLayout.CENTER);

            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    resizeComponents();
                }
            });
        }
        // lijkt op tictactoemenu
        private JButton createRoundedButton(String text, Color baseColor, Color hoverColor, Color borderColor, boolean enabled) {
            var btn = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color base = (Color) getClientProperty("baseColor");
                    Color hover = (Color) getClientProperty("hoverColor");
                    Color border = (Color) getClientProperty("borderColor");
                    if (base == null) base = baseColor;
                    if (hover == null) hover = hoverColor;
                    if (border == null) border = borderColor;

                    g2.setColor(getModel().isRollover() && isEnabled() ? hover : base);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                    g2.setColor(border);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 30, 30);
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
                    int scaledWidth = (int) (200 * scale);
                    int scaledHeight = (int) (35 * scale);
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
            btn.setForeground(enabled ? Color.WHITE : new Color(100, 100, 100));
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
            resizeAllComponents(this, scale);
            revalidate();
            repaint();
        }

        private void resizeAllComponents(Container container, double scale) {
            for (Component comp : container.getComponents()) {
                if (comp instanceof JButton) {
                    JButton btn = (JButton) comp;
                    int newFontSize = (int)(12 * scale);
                    btn.setFont(btn.getFont().deriveFont(Font.PLAIN, newFontSize));
                    Dimension newSize = new Dimension((int)(200 * scale), (int)(35 * scale));
                    btn.setPreferredSize(newSize);
                    btn.setMinimumSize(newSize);
                    btn.setMaximumSize(newSize);
                } else if (comp instanceof JLabel label) {
                    if (label == titleLabel) {
                        int newTitleSize = (int)(25 * scale);
                        label.setFont(label.getFont().deriveFont(Font.BOLD, Math.max(18, Math.min(newTitleSize, 40))));
                    } else {
                        int newLabelSize = (int)(18 * scale);
                        label.setFont(label.getFont().deriveFont(Font.PLAIN, Math.max(14, Math.min(newLabelSize, 30))));
                    }
                } else if (comp instanceof JTextField field) {
                    int newFontSize = (int)(14 * scale);
                    field.setFont(field.getFont().deriveFont(Font.PLAIN, newFontSize));
                    field.setMaximumSize(new Dimension(500, (int)(40 * scale)));
                } else if (comp instanceof JRadioButton radio) {
                    int newFontSize = (int)(14 * scale);
                    radio.setFont(radio.getFont().deriveFont(Font.PLAIN, newFontSize));
                } else if (comp instanceof Container child) {
                    resizeAllComponents(child, scale);
                }
            }
        }

        public void updateLanguage() {
            setTitle(lang.get("tictactoe.name.title"));
            titleLabel.setText(lang.get("tictactoe.name.title"));
            speler1Label.setText(lang.get("tictactoe.name.playername"));
            startButton.setText(lang.get("tictactoe.name.startgame"));
            backButton.setText(lang.get("tictactoe.name.back"));
        }

        public void updateTheme() {
            ThemeManager theme = ThemeManager.getInstance();
            getContentPane().setBackground(theme.getBackgroundColor());
            centerPanel.setBackground(theme.getBackgroundColor());
            topPanel.setBackground(theme.getBackgroundColor());

            startButton.putClientProperty("baseColor", theme.getMainButtonColor());
            startButton.putClientProperty("hoverColor", theme.getMainButtonColor().brighter());
            startButton.putClientProperty("borderColor", theme.getMainButtonColor().darker());

            backButton.putClientProperty("baseColor", theme.getMainButtonColor());
            backButton.putClientProperty("hoverColor", theme.getMainButtonColor().brighter());
            backButton.putClientProperty("borderColor", theme.getMainButtonColor().darker());

            titleLabel.setForeground(theme.getFontColor1());
            speler1Label.setForeground(theme.getFontColor2());

            textField1.setBackground(theme.getTextFieldColor());
            repaint();
        }


    public void showMenu() {
            setVisible(true);
        }

        public void hideMenu() {
            setVisible(false);
        }



}
