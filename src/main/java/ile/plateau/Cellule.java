package ile.plateau;

import java.util.ArrayList;
import java.util.List;

import ile.geometrie.Hexagone;
import ile.pions.Barque;
import ile.pions.Explorateur;
import ile.pions.Statut;
import ile.pions.monstres.Monstre;
import ile.tuiles.Tuile;

/**
 * Case du plateau situee a une coordonnee {@link Hexagone}.
 *
 * <p>Une cellule est soit de la terre,soit de la mer. Elle memorise ce qu'elle contient : les explorateurs poses
 * dessus, les barques et les creatures.</p>
 */
public class Cellule {

    private final Hexagone position;
    private final boolean refuge;
    private Tuile tuile;

    private final List<Explorateur> explorateurs = new ArrayList<>();
    private final List<Barque> barques = new ArrayList<>();
    private final List<Monstre> monstres = new ArrayList<>();

    /**
     * Cree une cellule.
     *
     * @param position coordonnee de la case
     * @param refuge   vrai s'il s'agit d'une plage refuge (zone sure permanente)
     * @param tuile    tuile de terrain posee, ou {@code null} pour de la mer
     */
    public Cellule(Hexagone position, boolean refuge, Tuile tuile) {
        this.position = position;
        this.refuge = refuge;
        this.tuile = tuile;
    }

    public Hexagone getPosition() {
        return position;
    }

    public boolean estRefuge() {
        return refuge;
    }

    public Tuile getTuile() {
        return tuile;
    }

    public void setTuile(Tuile tuile) {
        this.tuile = tuile;
    }

    public boolean aTuile() {
        return tuile != null;
    }

    public List<Explorateur> getExplorateurs() {
        return explorateurs;
    }

    public List<Barque> getBarques() {
        return barques;
    }

    public List<Monstre> getMonstres() {
        return monstres;
    }

    /**
     * @return vrai si la cellule est de la terre ferme (tuile de l'ile ou refuge)
     */
    public boolean estTerre() {
        return refuge || tuile != null;
    }

    /**
     * @return vrai si la cellule est de la mer (navigable par barques et creatures)
     */
    public boolean estMer() {
        return !estTerre();
    }

    /**
     * Renvoie une copie de la liste des explorateurs presents ayant le statut
     * demande 
     * @param statut statut recherche
     * @return la liste (modifiable, independante) des explorateurs concernes
     */
    public List<Explorateur> explorateursParStatut(Statut statut) {
        List<Explorateur> resultat = new ArrayList<>();
        for (Explorateur e : explorateurs) {
            if (e.getStatut() == statut) {
                resultat.add(e);
            }
        }
        return resultat;
    }

    /**
     * Renvoie les creatures presentes appartenant au type demande.
     * @param <T>  type de creature
     * @param type classe de creature recherchee (p. ex. {@code Requin.class})
     * @return la liste des creatures de ce type
     */
    public <T extends Monstre> List<T> monstresDeType(Class<T> type) {
        List<T> resultat = new ArrayList<>();
        for (Monstre m : monstres) {
            if (type.isInstance(m)) {
                resultat.add(type.cast(m));
            }
        }
        return resultat;
    }

    @Override
    public String toString() {
        return "Cellule " + position + (estTerre() ? " [terre]" : " [mer]");
    }
}
