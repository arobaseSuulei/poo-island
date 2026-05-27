package ile.ihm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import ile.geometrie.Hexagone;
import ile.moteur.Etape;
import ile.moteur.Partie;
import ile.pions.Barque;
import ile.pions.CouleurJoueur;
import ile.pions.Explorateur;
import ile.pions.Statut;
import ile.pions.monstres.Baleine;
import ile.pions.monstres.Monstre;
import ile.pions.monstres.Requin;
import ile.pions.monstres.SerpentDeMer;
import ile.plateau.Cellule;
import ile.plateau.Plateau;
import ile.tuiles.Terrain;

/**
 * Panneau qui dessine le plateau et gere les clics.
 *
 * Hexagonese et  conversion axiale en pixel :
 *   x = sqrt(3) * size * (q + r/2)
 *   y = 1.5 * size * r
 */
public class PanneauPlateau extends JPanel {

    private static final Color FOND = new Color(15, 40, 70);
    private static final Color C_MER = new Color(50, 105, 165);
    private static final Color C_MER_PROFONDE = new Color(35, 75, 130);

    // Tuiles de terrain 
    private static final Color C_PLAGE = new Color(244, 215, 130);
    private static final Color C_FORET = new Color(40, 120, 55);
    private static final Color C_MONTAGNE = new Color(115, 115, 135);
    private static final Color C_REFUGE = new Color(232, 200, 110);

    // Bordures et surbrillances
    private static final Color C_BORDURE = new Color(20, 30, 40);
    private static final Color C_BORDURE_TUILE = new Color(60, 35, 20);
    private static final Color C_SELECTION = new Color(255, 240, 80, 170);
    private static final Color C_DESTINATION = new Color(120, 240, 120, 150);
    private static final Color C_RETIRABLE = new Color(240, 80, 80, 150);
    private static final Color C_PLACEMENT = new Color(120, 240, 180, 130);
    private static final Color C_CREATURE_ELIGIBLE = new Color(255, 150, 30, 160);

    private final Partie partie;
    private Controleur controleur;

    private double taille;
    private double centreX;
    private double centreY;

    public PanneauPlateau(Partie partie) {
        this.partie = partie;
        setBackground(FOND);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (controleur == null) {
                    return;
                }
                Hexagone h = caseContenant(e.getX(), e.getY());
                if (h != null) {
                    controleur.cliquerSurCase(h);
                }
            }
        });
    }

    public void setControleur(Controleur controleur) {
        this.controleur = controleur;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        ajusterTaille();

        g2.setColor(C_MER_PROFONDE);
        g2.fillRect(0, 0, getWidth(), getHeight());

        Set<Hexagone> surbrillances = calculerSurbrillances();
        Color couleurSurbrillance = couleurSurbrillance();
        Hexagone selection = controleur == null ? null : controleur.getCaseSelectionnee();

        for (Cellule c : partie.getPlateau().toutesLesCellules()) {
            dessinerCellule(g2, c, surbrillances, couleurSurbrillance, selection);
        }
    }

    private void ajusterTaille() {
        // Calcule la boîte englobante du plateau 
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (Cellule c : partie.getPlateau().toutesLesCellules()) {
            Hexagone h = c.getPosition();
            double x = Math.sqrt(3.0) * (h.getQ() + h.getR() / 2.0);
            double y = 1.5 * h.getR();
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }
        // Ajoute la demi-largeur ou demi-hauteur d'un hexagone autour.
        double largeurTotale = (maxX - minX) + Math.sqrt(3.0);
        double hauteurTotale = (maxY - minY) + 2.0;

        double maxParLargeur = getWidth() / largeurTotale;
        double maxParHauteur = getHeight() / hauteurTotale;
        this.taille = Math.max(14.0, Math.min(maxParLargeur, maxParHauteur));
        double centroidX = (minX + maxX) / 2.0;
        double centroidY = (minY + maxY) / 2.0;
        this.centreX = getWidth() / 2.0 - taille * centroidX;
        this.centreY = getHeight() / 2.0 - taille * centroidY;
    }

    private int[] centreEnPixel(Hexagone h) {
        // Conversion axial à pixel pour des hexagones 
        //   x = sqrt(3) * size * (q + r/2)
        //   y = 1.5 * size * r
        double x = centreX + taille * Math.sqrt(3.0) * (h.getQ() + h.getR() / 2.0);
        double y = centreY + taille * 1.5 * h.getR();
        return new int[]{(int) Math.round(x), (int) Math.round(y)};
    }

    private Polygon polygoneHexagone(int cx, int cy) {
        Polygon p = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60.0 * i + 30.0);
            int x = (int) Math.round(cx + taille * Math.cos(angle));
            int y = (int) Math.round(cy + taille * Math.sin(angle));
            p.addPoint(x, y);
        }
        return p;
    }

    private Hexagone caseContenant(int px, int py) {
        for (Cellule c : partie.getPlateau().toutesLesCellules()) {
            int[] xy = centreEnPixel(c.getPosition());
            if (polygoneHexagone(xy[0], xy[1]).contains(px, py)) {
                return c.getPosition();
            }
        }
        return null;
    }

    private void dessinerCellule(Graphics2D g2, Cellule c, Set<Hexagone> surbrillances,
                                 Color couleurSurbrillance, Hexagone selection) {
        int[] xy = centreEnPixel(c.getPosition());
        Polygon hex = polygoneHexagone(xy[0], xy[1]);

        // Fond coloré
        g2.setColor(couleurDeBase(c));
        g2.fillPolygon(hex);

        // Lettre du terrain (P, F, M)
        if (c.aTuile()) {
            dessinerLettreTerrain(g2, c.getTuile().getTerrain(), xy[0], xy[1]);
        }

        // Surbrillances
        if (surbrillances.contains(c.getPosition())) {
            g2.setColor(couleurSurbrillance);
            g2.fillPolygon(hex);
        }
        if (c.getPosition().equals(selection)) {
            g2.setColor(C_SELECTION);
            g2.fillPolygon(hex);
        }

        // Bordures
        if (c.aTuile() || c.estRefuge()) {
            g2.setColor(C_BORDURE_TUILE);
            g2.setStroke(new BasicStroke(2.0f));
        } else {
            g2.setColor(C_BORDURE);
            g2.setStroke(new BasicStroke(1.0f));
        }
        g2.drawPolygon(hex);
        dessinerContenu(g2, c, xy[0], xy[1]);
    }

    private Color couleurDeBase(Cellule c) {
        if (c.estRefuge()) {
            return C_REFUGE;
        }
        if (c.aTuile()) {
            switch (c.getTuile().getTerrain()) {
                case PLAGE:
                    return C_PLAGE;
                case FORET:
                    return C_FORET;
                case MONTAGNE:
                    return C_MONTAGNE;
                default:
                    return C_MER;
            }
        }
        return C_MER;
    }

    private void dessinerLettreTerrain(Graphics2D g2, Terrain t, int cx, int cy) {
        String lettre;
        switch (t) {
            case PLAGE:
                lettre = "P";
                break;
            case FORET:
                lettre = "F";
                break;
            case MONTAGNE:
                lettre = "M";
                break;
            default:
                return;
        }
        Font police = new Font("SansSerif", Font.BOLD, Math.max(18, (int) (taille * 0.75)));
        g2.setFont(police);
        FontMetrics fm = g2.getFontMetrics();
        int x = cx - fm.stringWidth(lettre) / 2;
        int y = cy + fm.getAscent() / 2 - 3;
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(lettre, x + 2, y + 2);
        g2.setColor(Color.WHITE);
        g2.drawString(lettre, x, y);
    }

    private Color couleurSurbrillance() {
        Etape e = partie.getEtape();
        if (e == Etape.RETRAIT_TUILE) {
            return C_RETIRABLE;
        }
        if (e == Etape.PLACEMENT_EXPLORATEURS || e == Etape.PLACEMENT_BARQUES) {
            return C_PLACEMENT;
        }
        if (e == Etape.DEPLACEMENT_CREATURE
                && controleur != null
                && controleur.getCaseSelectionnee() == null) {
            return C_CREATURE_ELIGIBLE;
        }
        return C_DESTINATION;
    }

    private Set<Hexagone> calculerSurbrillances() {
        Set<Hexagone> resultat = new HashSet<>();
        Etape e = partie.getEtape();
        Plateau p = partie.getPlateau();
        switch (e) {
            case PLACEMENT_EXPLORATEURS:
                for (Hexagone h : p.getEmplacementsIle()) {
                    Cellule c = p.getCellule(h);
                    if (c.aTuile() && c.getExplorateurs().isEmpty()) {
                        resultat.add(h);
                    }
                }
                break;
            case PLACEMENT_BARQUES:
                for (Cellule c : p.toutesLesCellules()) {
                    if (c.estMer() && c.getBarques().isEmpty() && voisineDuneTuile(c)) {
                        resultat.add(c.getPosition());
                    }
                }
                break;
            case RETRAIT_TUILE:
                resultat.addAll(partie.tuilesRetirables());
                break;
            case DEPLACEMENT:
                if (controleur != null) {
                    resultat.addAll(controleur.getDestinationsCourantes());
                }
                break;
            case DEPLACEMENT_CREATURE:
                if (controleur != null && controleur.getCaseSelectionnee() == null) {
                    // Aucune creature selectionnee : on met en valeur les creatures
                    // de l'espece tiree pour que le joueur voie ou cliquer.
                    for (Monstre m : partie.creaturesDeplacables()) {
                        resultat.add(m.getPosition());
                    }
                } else if (controleur != null) {
                    resultat.addAll(controleur.getDestinationsCourantes());
                }
                break;
            default:
                break;
        }
        return resultat;
    }

    private boolean voisineDuneTuile(Cellule c) {
        for (Cellule v : partie.getPlateau().voisines(c.getPosition())) {
            if (v.aTuile()) {
                return true;
            }
        }
        return false;
    }

    private void dessinerContenu(Graphics2D g2, Cellule c, int cx, int cy) {
        int idxM = 0;
        for (Monstre m : c.getMonstres()) {
            int dx = (idxM % 3 - 1) * (int) (taille * 0.5);
            int dy = -(int) (taille * 0.55) + (idxM / 3) * 4;
            dessinerMonstre(g2, m, cx + dx, cy + dy);
            idxM++;
        }

        int idxB = 0;
        for (Barque b : c.getBarques()) {
            int dy = (int) (taille * 0.45) + idxB * 8;
            dessinerBarque(g2, b, cx, cy + dy);
            idxB++;
        }

        List<Explorateur> visibles = new ArrayList<>();
        for (Explorateur e : c.getExplorateurs()) {
            if (e.getStatut() != Statut.EMBARQUE) {
                visibles.add(e);
            }
        }
        if (!visibles.isEmpty()) {
            dessinerExplorateurs(g2, visibles, cx, cy);
        }
    }

    private void dessinerExplorateurs(Graphics2D g2, List<Explorateur> liste, int cx, int cy) {
        int rayon = Math.max(6, (int) (taille * 0.22));
        int espacement = Math.max(10, (int) (taille * 0.5));
        int n = liste.size();
        int startX = cx - (n - 1) * espacement / 2;
        boolean afficherValeur = partie.getEtape() == Etape.PLACEMENT_EXPLORATEURS;

        Font police = new Font("SansSerif", Font.BOLD, Math.max(10, (int) (rayon * 1.4)));

        for (int i = 0; i < n; i++) {
            Explorateur e = liste.get(i);
            int x = startX + i * espacement;

            g2.setColor(couleurDuJoueur(e.getCouleur()));
            g2.fillOval(x - rayon, cy - rayon, 2 * rayon, 2 * rayon);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawOval(x - rayon, cy - rayon, 2 * rayon, 2 * rayon);

            if (afficherValeur) {
                g2.setColor(Color.WHITE);
                g2.setFont(police);
                FontMetrics fm = g2.getFontMetrics();
                String texte = String.valueOf(e.getTresor());
                int tx = x - fm.stringWidth(texte) / 2;
                int ty = cy + fm.getAscent() / 2 - 2;
                g2.drawString(texte, tx, ty);
            }
        }
    }

    private void dessinerBarque(Graphics2D g2, Barque b, int cx, int cy) {
        int largeur = Math.max(20, (int) (taille * 1.0));
        int hauteur = Math.max(10, (int) (taille * 0.4));
        g2.setColor(new Color(110, 70, 30));
        g2.fillRoundRect(cx - largeur / 2, cy - hauteur / 2, largeur, hauteur, hauteur, hauteur);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(cx - largeur / 2, cy - hauteur / 2, largeur, hauteur, hauteur, hauteur);

        int n = b.getPassagers().size();
        if (n == 0) {
            return;
        }
        int rayon = Math.max(3, (int) (taille * 0.12));
        int espacement = Math.min(largeur / 4, 2 * rayon + 2);
        int startX = cx - (n - 1) * espacement / 2;
        for (int i = 0; i < n; i++) {
            int x = startX + i * espacement;
            g2.setColor(couleurDuJoueur(b.getPassagers().get(i).getCouleur()));
            g2.fillOval(x - rayon, cy - rayon, 2 * rayon, 2 * rayon);
        }
    }

    private void dessinerMonstre(Graphics2D g2, Monstre m, int cx, int cy) {
        Color couleur;
        String symbole;
        if (m instanceof SerpentDeMer) {
            couleur = new Color(0, 140, 0);
            symbole = "S";
        } else if (m instanceof Requin) {
            couleur = new Color(200, 30, 30);
            symbole = "R";
        } else if (m instanceof Baleine) {
            couleur = new Color(30, 50, 170);
            symbole = "B";
        } else {
            return;
        }
        int rayon = Math.max(7, (int) (taille * 0.32));
        g2.setColor(couleur);
        g2.fillOval(cx - rayon, cy - rayon, 2 * rayon, 2 * rayon);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawOval(cx - rayon, cy - rayon, 2 * rayon, 2 * rayon);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(11, (int) (taille * 0.5))));
        FontMetrics fm = g2.getFontMetrics();
        int sx = cx - fm.stringWidth(symbole) / 2;
        int sy = cy + fm.getAscent() / 2 - 2;
        g2.drawString(symbole, sx, sy);
    }

    private Color couleurDuJoueur(CouleurJoueur c) {
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
}
