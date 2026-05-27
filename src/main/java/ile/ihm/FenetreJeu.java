package ile.ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;

import ile.moteur.Etape;
import ile.moteur.Partie;

/**
 * Fenêtre principale de la partie.
 */
public class FenetreJeu extends JFrame {

    private static final Dimension TAILLE = new Dimension(1280, 800);
    private static final Color FOND = new Color(15, 40, 70);

    private final Partie partie;
    private final Controleur controleur;
    private final PanneauPlateau panneauPlateau;
    private final PanneauActions panneauActions;
    private final PanneauJournal panneauJournal;
    private final PanneauJoueurs panneauJoueurs;

    private boolean finAffichee;

    /**
     * Crée la fenetre et demarre une nouvelle partie
     *
     * @param nomsJoueurs noms des joueurs (2 à 4)
     */
    public FenetreJeu(List<String> nomsJoueurs) {
        this.partie = new Partie(nomsJoueurs);
        this.controleur = new Controleur(partie, this);
        this.partie.setArbitre(controleur);

        this.panneauPlateau = new PanneauPlateau(partie);
        this.panneauPlateau.setControleur(controleur);
        this.panneauActions = new PanneauActions(partie, controleur);
        this.panneauJournal = new PanneauJournal(partie);
        this.panneauJoueurs = new PanneauJoueurs(partie);

        setTitle("The Island - Partie en cours");
        setSize(TAILLE);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Container racine = getContentPane();
        racine.setBackground(FOND);
        racine.setLayout(new BorderLayout(8, 8));
        if (racine instanceof JComponent) {
            ((JComponent) racine).setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        }

        panneauJournal.setPreferredSize(new Dimension(230, 0));
        panneauJoueurs.setPreferredSize(new Dimension(310, 0));

        racine.add(panneauJournal, BorderLayout.WEST);
        racine.add(panneauPlateau, BorderLayout.CENTER);
        racine.add(panneauJoueurs, BorderLayout.EAST);
        racine.add(panneauActions, BorderLayout.SOUTH);
    }

    public Partie getPartie() {
        return partie;
    }

    public void rafraichir() {
        panneauPlateau.repaint();
        panneauActions.rafraichir();
        panneauJournal.rafraichir();
        panneauJoueurs.rafraichir();
        if (partie.getEtape() == Etape.TERMINEE && !finAffichee) {
            finAffichee = true;
            afficherResultats();
        }
    }

    private void afficherResultats() {
        new FenetreFinPartie(this, partie).setVisible(true);
    }
}
