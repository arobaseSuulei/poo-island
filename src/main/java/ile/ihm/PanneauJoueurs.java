package ile.ihm;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import ile.moteur.Partie;
import ile.pions.CouleurJoueur;
import ile.pions.Explorateur;
import ile.pions.Joueur;
import ile.pions.Statut;

/**
 * Panneau de droite : une carte par joueur.
 on les affiche dans les cercles tant
 * que le pion n'a pas été posé sur le plateau
 */
public class PanneauJoueurs extends JPanel {

    private static final Color FOND = new Color(30, 70, 110);
    private static final Color FOND_JOUEUR = new Color(40, 80, 120);
    private static final Color BORDURE = new Color(200, 200, 200);
    private static final Color SURBRILLANCE_COURANT = new Color(255, 240, 80);

    private final Partie partie;
    private final List<VueJoueur> vues = new ArrayList<>();

    public PanneauJoueurs(Partie partie) {
        this.partie = partie;
        setBackground(FOND);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        TitledBorder titre = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDURE, 1), "Joueurs");
        titre.setTitleColor(Color.WHITE);
        titre.setTitleFont(new Font("SansSerif", Font.BOLD, 14));
        setBorder(titre);

        add(Box.createRigidArea(new Dimension(0, 6)));
        for (Joueur j : partie.getJoueurs()) {
            VueJoueur vj = new VueJoueur(j);
            vues.add(vj);
            add(vj);
            add(Box.createRigidArea(new Dimension(0, 8)));
        }
        add(Box.createVerticalGlue());

        rafraichir();
    }

    // Met a jour chaque vue joueur. 
    public void rafraichir() {
        Joueur courant = partie.joueurCourant();
        for (VueJoueur vj : vues) {
            vj.rafraichir(vj.joueur == courant);
        }
    }

    private static Color couleurDuJoueur(CouleurJoueur c) {
        switch (c) {
            case ROUGE:
                return new Color(220, 50, 50);
            case BLEU:
                return new Color(40, 90, 220);
            case VERT:
                return new Color(40, 170, 40);
            case JAUNE:
                return new Color(240, 210, 50);
            default:
                return Color.WHITE;
        }
    }


    private static class VueJoueur extends JPanel {

        private final Joueur joueur;
        private final JLabel nom = new JLabel();
        private final JLabel statut = new JLabel();
        private final GrillePions grille;

        VueJoueur(Joueur joueur) {
            this.joueur = joueur;
            setLayout(new BorderLayout(0, 4));
            setBackground(FOND_JOUEUR);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

            Color couleur = couleurDuJoueur(joueur.getCouleur());

            nom.setForeground(couleur);
            nom.setFont(new Font("SansSerif", Font.BOLD, 13));

            grille = new GrillePions(joueur);

            statut.setForeground(Color.WHITE);
            statut.setFont(new Font("SansSerif", Font.PLAIN, 11));

            add(nom, BorderLayout.NORTH);
            add(grille, BorderLayout.CENTER);
            add(statut, BorderLayout.SOUTH);
        }

        void rafraichir(boolean courant) {
            Color couleur = couleurDuJoueur(joueur.getCouleur());
            int epaisseur = courant ? 3 : 2;
            Color bord = courant ? SURBRILLANCE_COURANT : couleur;
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(bord, epaisseur),
                    BorderFactory.createEmptyBorder(6 - epaisseur, 8 - epaisseur, 6 - epaisseur, 8 - epaisseur)));

            String etiquette = joueur.getNom() + "  (" + joueur.getCouleur() + ")";
            if (courant) {
                etiquette = "> " + etiquette;
            }
            nom.setText(etiquette);

            int total = joueur.getExplorateurs().size();
            int sauves = joueur.nombreSauves();
            int enJeu = joueur.nombreEnJeu();
            int perdus = total - sauves - enJeu;
            int main = joueur.getMain().size();
            statut.setText("Sauvés : " + sauves + "   Perdus : " + perdus
                    + "   En jeu : " + enJeu + "   Main : " + main);

            grille.repaint();
        }
    }


    private static class GrillePions extends JPanel {

        private final Joueur joueur;

        GrillePions(Joueur joueur) {
            this.joueur = joueur;
            setBackground(FOND_JOUEUR);
            setPreferredSize(new Dimension(0, 42));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            List<Explorateur> liste = joueur.getExplorateurs();
            int n = liste.size();
            int largeurDispo = getWidth() - 8;
            int espacement = Math.min(28, Math.max(18, largeurDispo / n));
            int rayon = Math.max(7, Math.min(13, espacement / 2 - 2));
            int totalLargeur = n * espacement;
            int startX = (getWidth() - totalLargeur) / 2 + espacement / 2;
            int y = getHeight() / 2;

            Color couleurPion = couleurDuJoueur(joueur.getCouleur());
            Font police = new Font("SansSerif", Font.BOLD, rayon + 2);
            g2.setFont(police);
            FontMetrics fm = g2.getFontMetrics();

            for (int i = 0; i < n; i++) {
                Explorateur e = liste.get(i);
                int x = startX + i * espacement;

                boolean nonPose = (e.getPosition() == null && e.getStatut() == Statut.SUR_TERRE);
                boolean perdu = e.getStatut() == Statut.PERDU;
                boolean sauve = e.getStatut() == Statut.SAUVE;

                // Disque
                Color fond = perdu ? new Color(90, 90, 90) : couleurPion;
                g2.setColor(fond);
                g2.fillOval(x - rayon, y - rayon, 2 * rayon, 2 * rayon);

                // Bordure speciale pour sauves et perdus
                if (sauve) {
                    g2.setColor(new Color(50, 220, 50));
                    g2.setStroke(new BasicStroke(2.5f));
                } else if (perdu) {
                    g2.setColor(new Color(200, 30, 30));
                    g2.setStroke(new BasicStroke(2.0f));
                    // petite croix pour bien marquer perdu
                    int d = rayon - 2;
                    g2.drawLine(x - d, y - d, x + d, y + d);
                    g2.drawLine(x - d, y + d, x + d, y - d);
                } else {
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(1.0f));
                }
                g2.drawOval(x - rayon, y - rayon, 2 * rayon, 2 * rayon);

                // Valeur visible UNIQUEMENT tant que le pion n'a pas été pose
                if (nonPose) {
                    g2.setColor(Color.WHITE);
                    String v = String.valueOf(e.getTresor());
                    g2.drawString(v, x - fm.stringWidth(v) / 2, y + fm.getAscent() / 2 - 1);
                }
            }
        }
    }
}
