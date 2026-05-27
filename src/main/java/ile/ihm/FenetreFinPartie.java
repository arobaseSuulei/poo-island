package ile.ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import ile.moteur.Partie;
import ile.pions.CouleurJoueur;
import ile.pions.Joueur;

/**
 * Fenêtre affichée à la fin de la partie 
 */
public class FenetreFinPartie extends JDialog {

    private static final Color FOND = Color.WHITE;
    private static final Color TITRE = new Color(20, 60, 100);

    private final JFrame parent;

    /**
     * Crée et affiche la fenêtre de fin.
     * @param parent la fenêtre de jeu qui invoque la fin
     * @param partie partie terminée dont on récupère le classement
     */
    public FenetreFinPartie(JFrame parent, Partie partie) {
        super(parent, "Fin de la partie", true);
        this.parent = parent;

        setSize(560, 540);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getContentPane().setBackground(FOND);
        setLayout(new BorderLayout(10, 10));

        add(creerEnTete(partie), BorderLayout.NORTH);
        add(creerClassement(partie), BorderLayout.CENTER);
        add(creerBoutons(), BorderLayout.SOUTH);
    }

    private JPanel creerEnTete(Partie partie) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(FOND);
        p.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JLabel titre = new JLabel("Fin de la partie !", SwingConstants.CENTER);
        titre.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        titre.setFont(new Font("Serif", Font.BOLD, 32));
        titre.setForeground(TITRE);

        Joueur vainqueur = partie.getVainqueur();
        JLabel sousTitre = new JLabel(
                vainqueur != null
                        ? "Vainqueur : " + vainqueur.getNom()
                        : "Personne ne remporte la partie",
                SwingConstants.CENTER);
        sousTitre.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        sousTitre.setFont(new Font("SansSerif", Font.BOLD, 22));
        sousTitre.setForeground(vainqueur != null
                ? couleurDuJoueur(vainqueur.getCouleur())
                : Color.DARK_GRAY);

        p.add(titre);
        p.add(Box.createRigidArea(new Dimension(0, 8)));
        p.add(sousTitre);
        return p;
    }

    private JPanel creerClassement(Partie partie) {
        JPanel zone = new JPanel(new BorderLayout());
        zone.setBackground(FOND);
        zone.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JLabel titre = new JLabel("Classement");
        titre.setFont(new Font("Serif", Font.BOLD, 20));
        titre.setForeground(TITRE);
        titre.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        zone.add(titre, BorderLayout.NORTH);

        JPanel grille = new JPanel(new GridLayout(0, 4, 12, 6));
        grille.setBackground(FOND);

        grille.add(entete("Rang"));
        grille.add(entete("Joueur"));
        grille.add(entete("Score"));
        grille.add(entete("Sauvés"));

        int rang = 1;
        for (Joueur joueur : partie.classement()) {
            grille.add(cellule(String.valueOf(rang)));

            JLabel nom = new JLabel(joueur.getNom());
            nom.setFont(new Font("SansSerif", Font.BOLD, 14));
            nom.setForeground(couleurDuJoueur(joueur.getCouleur()));
            grille.add(nom);

            grille.add(cellule(joueur.score() + " pts"));
            grille.add(cellule(String.valueOf(joueur.nombreSauves())));
            rang++;
        }

        zone.add(grille, BorderLayout.CENTER);
        return zone;
    }

    private JPanel creerBoutons() {
        JPanel bas = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        bas.setBackground(FOND);

        JButton nouvelle = bouton("Nouvelle partie", new Color(60, 130, 60));
        nouvelle.addActionListener(e -> nouvellePartie());

        JButton quitter = bouton("Quitter", new Color(160, 60, 60));
        quitter.addActionListener(e -> System.exit(0));

        bas.add(nouvelle);
        bas.add(quitter);
        return bas;
    }

    private void nouvellePartie() {
        dispose();
        if (parent != null) {
            parent.dispose();
        }
        SwingUtilities.invokeLater(() -> new EcranAccueil().setVisible(true));
    }

    private JLabel entete(String texte) {
        JLabel l = new JLabel(texte);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setForeground(TITRE);
        return l;
    }

    private JLabel cellule(String texte) {
        JLabel l = new JLabel(texte);
        l.setFont(new Font("SansSerif", Font.PLAIN, 14));
        l.setForeground(Color.BLACK);
        return l;
    }

    private JButton bouton(String texte, Color fond) {
        JButton b = new JButton(texte);
        b.setFont(new Font("SansSerif", Font.BOLD, 16));
        b.setPreferredSize(new Dimension(190, 50));
        b.setBackground(fond);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }

    private static Color couleurDuJoueur(CouleurJoueur c) {
        switch (c) {
            case ROUGE:
                return new Color(220, 50, 50);
            case BLEU:
                return new Color(40, 90, 220);
            case VERT:
                return new Color(40, 150, 40);
            case JAUNE:
                return new Color(210, 170, 30);
            default:
                return Color.BLACK;
        }
    }
}
