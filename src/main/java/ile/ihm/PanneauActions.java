package ile.ihm;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import ile.moteur.Etape;
import ile.moteur.Partie;
import ile.tuiles.Moment;
import ile.tuiles.Tuile;

/**
 * Panneau d'actions en bas de la fenêtre.
 */
public class PanneauActions extends JPanel {

    private static final Color FOND = new Color(30, 70, 110);
    private static final Color BORDURE = new Color(200, 200, 200);
    private static final Color BOUTON = new Color(190, 110, 40);
    private static final Color BOUTON_TUILE = new Color(140, 60, 160);
    private static final Color BOUTON_ANNULER = new Color(160, 60, 60);
    private static final Color BOUTON_AIDE = new Color(70, 130, 180);

    private final Partie partie;
    private final Controleur controleur;

    private final JLabel infos = new JLabel();
    private final JButton boutonDebutTour = new JButton("Passer début de tour");
    private final JButton boutonJouerTuile = new JButton("Jouer une tuile");
    private final JButton boutonAnnulerTuile = new JButton("Annuler tuile");
    private final JButton boutonTerminerDep = new JButton("Terminer déplacement");
    private final JButton boutonLancerDe = new JButton("Lancer le dé");
    private final JButton boutonPasserCreature = new JButton("Passer créature");
    private final JButton boutonAide = new JButton("Aide");

    public PanneauActions(Partie partie, Controleur controleur) {
        this.partie = partie;
        this.controleur = controleur;
        setBackground(FOND);

        TitledBorder titre = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDURE, 1), "Actions");
        titre.setTitleColor(Color.WHITE);
        titre.setTitleFont(new Font("SansSerif", Font.BOLD, 14));
        setBorder(titre);
        setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));

        infos.setForeground(Color.WHITE);
        infos.setFont(new Font("SansSerif", Font.BOLD, 13));

        styliser(boutonDebutTour, BOUTON);
        styliser(boutonJouerTuile, BOUTON_TUILE);
        styliser(boutonAnnulerTuile, BOUTON_ANNULER);
        styliser(boutonTerminerDep, BOUTON);
        styliser(boutonLancerDe, BOUTON);
        styliser(boutonPasserCreature, BOUTON);
        styliser(boutonAide, BOUTON_AIDE);

        boutonDebutTour.addActionListener(e -> controleur.actionPasserDebutTour());
        boutonJouerTuile.addActionListener(e -> controleur.actionJouerTuile());
        boutonAnnulerTuile.addActionListener(e -> controleur.actionAnnulerTuile());
        boutonTerminerDep.addActionListener(e -> controleur.actionTerminerDeplacement());
        boutonLancerDe.addActionListener(e -> controleur.actionLancerDe());
        boutonPasserCreature.addActionListener(e -> controleur.actionPasserCreature());
        boutonAide.addActionListener(e -> ouvrirAide());

        add(infos);
        add(boutonDebutTour);
        add(boutonJouerTuile);
        add(boutonAnnulerTuile);
        add(boutonTerminerDep);
        add(boutonLancerDe);
        add(boutonPasserCreature);
        add(boutonAide);

        rafraichir();
    }

    private void styliser(JButton b, Color fond) {
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBackground(fond);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
    }

    private void ouvrirAide() {
        Window proprietaire = SwingUtilities.getWindowAncestor(this);
        JFrame parent = (proprietaire instanceof JFrame) ? (JFrame) proprietaire : null;
        new FenetreAide(parent).setVisible(true);
    }

    // Met à jour le texte et l'activation des boutons 
    public void rafraichir() {
        Etape e = partie.getEtape();
        boolean tuileEnCours = controleur.getTuileEnCours() != null;

        String tour = partie.joueurCourant().getNom() + " (" + partie.joueurCourant().getCouleur() + ")";
        StringBuilder texte = new StringBuilder();
        texte.append("Tour : ").append(tour);
        texte.append("   |   Phase : ").append(libellePhase(e));
        if (e == Etape.DEPLACEMENT) {
            texte.append("   |   Points : ").append(partie.getPointsDeplacement());
        }
        if (e == Etape.PLACEMENT_EXPLORATEURS) {
            int valeur = prochaineValeurAPoser();
            if (valeur > 0) {
                texte.append("   |   Prochain trésor : ").append(valeur);
            }
        }
        if (e == Etape.DEPLACEMENT_CREATURE && partie.especeLanceeNom() != null) {
            texte.append("   |   Dé : ").append(partie.especeLanceeNom());
        }
        if (tuileEnCours) {
            texte.append("   |   ").append(controleur.libelleSelection());
        }
        infos.setText(texte.toString());

        boutonDebutTour.setEnabled(!tuileEnCours && e == Etape.DEBUT_TOUR);
        boutonJouerTuile.setEnabled(!tuileEnCours
                && e == Etape.DEBUT_TOUR
                && aTuilesJouables());
        boutonAnnulerTuile.setEnabled(tuileEnCours);
        boutonTerminerDep.setEnabled(!tuileEnCours && e == Etape.DEPLACEMENT);
        boutonLancerDe.setEnabled(!tuileEnCours && e == Etape.LANCER_DE);
        boutonPasserCreature.setEnabled(!tuileEnCours && e == Etape.DEPLACEMENT_CREATURE);
    }

    private boolean aTuilesJouables() {
        for (Tuile t : partie.joueurCourant().getMain()) {
            if (t.getPouvoir().getMoment() == Moment.DEBUT_TOUR) {
                return true;
            }
        }
        return false;
    }

    private int prochaineValeurAPoser() {
        for (var explo : partie.joueurCourant().getExplorateurs()) {
            if (explo.getPosition() == null) {
                return explo.getTresor();
            }
        }
        return 0;
    }

    private String libellePhase(Etape e) {
        switch (e) {
            case PLACEMENT_EXPLORATEURS:
                return "Placement des explorateurs";
            case PLACEMENT_BARQUES:
                return "Placement des barques";
            case DEBUT_TOUR:
                return "Début de tour";
            case DEPLACEMENT:
                return "Déplacement";
            case RETRAIT_TUILE:
                return "Retrait d'une tuile";
            case LANCER_DE:
                return "Lancer du dé";
            case DEPLACEMENT_CREATURE:
                return "Déplacement de créature";
            case TERMINEE:
                return "Partie terminée";
            default:
                return e.toString();
        }
    }
}
