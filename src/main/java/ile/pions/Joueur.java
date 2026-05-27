package ile.pions;

import java.util.ArrayList;
import java.util.List;

import ile.tuiles.Tuile;

public class Joueur {

    private final String nom;
    private final CouleurJoueur couleur;
    private final List<Explorateur> explorateurs = new ArrayList<>();
    private final List<Tuile> main = new ArrayList<>();

    /**
     * Cree un joueur sans explorateur 
     * @param nom     nom affiche du joueur
     * @param couleur couleur de ses pions
     */
    public Joueur(String nom, CouleurJoueur couleur) {
        this.nom = nom;
        this.couleur = couleur;
    }

    public String getNom() {
        return nom;
    }

    public CouleurJoueur getCouleur() {
        return couleur;
    }

    public List<Explorateur> getExplorateurs() {
        return explorateurs;
    }

    public List<Tuile> getMain() {
        return main;
    }

    /**
     * @return la somme des tresors des explorateurs sauves
     */
    public int score() {
        int total = 0;
        for (Explorateur e : explorateurs) {
            if (e.getStatut() == Statut.SAUVE) {
                total += e.getTresor();
            }
        }
        return total;
    }

    /**
     * @return le nombre d'explorateurs mis en securite
     */
    public int nombreSauves() {
        int n = 0;
        for (Explorateur e : explorateurs) {
            if (e.getStatut() == Statut.SAUVE) {
                n++;
            }
        }
        return n;
    }

    /**
     * @return le nombre d'explorateurs encore en jeu
     */
    public int nombreEnJeu() {
        int n = 0;
        for (Explorateur e : explorateurs) {
            if (e.estEnJeu()) {
                n++;
            }
        }
        return n;
    }

    @Override
    public String toString() {
        return nom + " (" + couleur + ")";
    }
}
