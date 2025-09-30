import menu.MenuManager;
import javax.swing.UIManager;

/**
 * Hoofdklasse voor de Spelcollectie applicatie
 * Start het menu systeem waarmee gebruikers verschillende spellen kunnen selecteren
 */
public class Main {

    /**
     * Hoofdmethode - het startpunt van de applicatie
     * Initialiseert en start het menu systeem
     * @param args Command line argumenten (niet gebruikt)
     */
    public static void main(String[] args) {
        // Gebruik system look & feel voor betere integratie
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Als Windows look & feel niet beschikbaar is, gebruik default
            System.out.println("Kon Windows look & feel niet instellen, gebruik default.");
        }

        // Start de applicatie via het menu systeem
        MenuManager menuManager = new MenuManager();
        menuManager.startApplication();
    }
}