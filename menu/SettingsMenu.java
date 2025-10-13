package menu;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.Serial;

public class SettingsMenu extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;

    private final MenuManager menuManager;
    private final LanguageManager lang = LanguageManager.getInstance();

    private JLabel titleLabel;
    private JLabel languageLabel;
    private JComboBox<String> languageComboBox;
    private final String[] languageCodes = { "nl", "en", "vn", "cn" };
    private JButton backButton;

    public SettingsMenu(MenuManager menuManager) {
        this.menuManager = menuManager;
        initializeMenu();
    }

    private void initializeMenu() {
        setTitle(lang.get("settings.title"));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(247, 247, 255));
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(247, 247, 255));

        titleLabel = new JLabel(lang.get("settings.title"), JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(5, 5, 169));

        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(5));

        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        centerPanel.setBackground(new Color(247, 247, 255));

        Color bodyTextColor = new Color(0x2B6F6E);

        languageLabel = new JLabel(lang.get("settings.language"));
        languageLabel.setForeground(bodyTextColor);
        languageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // string geeft een aantal en dit gaat in een loop om het aantal comboboxex te gaeven

        String[] languageOptions = {
            lang.get("settings.language.dutch"),
            lang.get("settings.language.english"),
            lang.get("settings.language.vietnamese"),
            lang.get("settings.language.chinese")
        };
        // combox opmaak
        languageComboBox = new JComboBox<>(languageOptions);
        languageComboBox.setMaximumSize(new Dimension(200, 30));
        languageComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        languageComboBox.setBackground(new Color(247, 247, 255));
        languageComboBox.setForeground(bodyTextColor);
        languageComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));

        String currentLang = lang.getCurrentLanguage();
        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].equals(currentLang)) {
                languageComboBox.setSelectedIndex(i);
                break;
            }
        }

        languageComboBox.addActionListener(e -> {
            int selectedIndex = languageComboBox.getSelectedIndex();
            String selectedCode = languageCodes[selectedIndex];
            if (!selectedCode.equals(lang.getCurrentLanguage())) {
                changeLanguage(selectedCode);
            }
        });

        // terug knop
        backButton = createRoundedButton(lang.get("settings.back"),
                new Color(184, 107, 214), new Color(204, 127, 234), new Color(120, 60, 150), true);
        backButton.addActionListener(e -> {
            hideMenu();
            menuManager.returnToMainMenuFromSettings();
        });
        
        // ruimte ertussen
        centerPanel.add(languageLabel);
        centerPanel.add(Box.createVerticalStrut(3));
        centerPanel.add(languageComboBox);
        centerPanel.add(Box.createVerticalStrut(125));
        centerPanel.add(backButton);

        add(centerPanel, BorderLayout.CENTER);
            
            
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });
    }
    // het veranderen van de taal
    private void changeLanguage(String languageCode) {
            
        
            lang.setLanguage(languageCode);
            String languageName = lang.getCurrentLanguageName();
        
            JOptionPane.showMessageDialog(this,
            lang.get("settings.language.changed", languageName),
            lang.get("settings.language.changed.title"),
            JOptionPane.INFORMATION_MESSAGE);

            updateLanguage();
            menuManager.updateLanguage();
    }

// opmaak die lijkt op tictactoemenu
    private JButton createRoundedButton(String text, Color baseColor, Color hoverColor, Color borderColor, boolean enabled) {
        var btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() && isEnabled() ? hoverColor : baseColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(borderColor);
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
        setTitle(lang.get("settings.title"));
        titleLabel.setText(lang.get("settings.title"));
        languageLabel.setText(lang.get("settings.language"));
        backButton.setText(lang.get("settings.back"));
    }

    public void showMenu() {
        setVisible(true);
    }

    public void hideMenu() {
        setVisible(false);
    }
}
