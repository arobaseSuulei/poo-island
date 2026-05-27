package ile;

import javax.swing.SwingUtilities;

import ile.ihm.EcranAccueil;

/**
 * @author Groupe2
 */
public final class Lanceur {

    private Lanceur() {
    }

    /**
     * Demarre l'application : ouvre l'ecran d'accueil.
     *
     * @param args arguments de la ligne de commande 
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EcranAccueil().setVisible(true));
    }
}
