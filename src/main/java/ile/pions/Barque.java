package ile.pions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ile.geometrie.Hexagone;


public class Barque {

    public static final int CAPACITE = 3;

    private Hexagone position;
    private final List<Explorateur> passagers = new ArrayList<>();

    public Hexagone getPosition() {
        return position;
    }

    public void setPosition(Hexagone position) {
        this.position = position;
    }

    public List<Explorateur> getPassagers() {
        return passagers;
    }

    public boolean estPleine() {
        return passagers.size() >= CAPACITE;
    }

    public boolean estVide() {
        return passagers.isEmpty();
    }

    /**
     * @param e explorateur a embarquer
     */
    public void embarquer(Explorateur e) {
        passagers.add(e);
        e.setStatut(Statut.EMBARQUE);
        e.setBarque(this);
        e.setPosition(null);
    }

    /**
     * @param e explorateur a debarquer
     */
    public void debarquer(Explorateur e) {
        passagers.remove(e);
        e.setBarque(null);
    }

    /**
     * @return la couleur majoritaire, ou {@code null} si la barque est vide ou
     *         si plusieurs couleurs sont a egalite au sommet
     */
    public CouleurJoueur controleur() {
        Map<CouleurJoueur, Integer> comptes = comptesParCouleur();
        if (comptes.isEmpty()) {
            return null;
        }
        int max = 0;
        for (int n : comptes.values()) {
            max = Math.max(max, n);
        }
        CouleurJoueur gagnante = null;
        int nbAuSommet = 0;
        for (Map.Entry<CouleurJoueur, Integer> entree : comptes.entrySet()) {
            if (entree.getValue() == max) {
                gagnante = entree.getKey();
                nbAuSommet++;
            }
        }
        return nbAuSommet == 1 ? gagnante : null;
    }

    /**
     * Indique si la couleur donnee peut manoeuvrer la barque 
     * @param couleur couleur testee
     * @return vrai si cette couleur peut deplacer la barque
     */
    public boolean estManoeuvrablePar(CouleurJoueur couleur) {
        Map<CouleurJoueur, Integer> comptes = comptesParCouleur();
        if (comptes.isEmpty()) {
            return true;
        }
        int max = 0;
        for (int n : comptes.values()) {
            max = Math.max(max, n);
        }
        int aLui = comptes.getOrDefault(couleur, 0);
        return aLui == max && aLui > 0;
    }

    private Map<CouleurJoueur, Integer> comptesParCouleur() {
        Map<CouleurJoueur, Integer> comptes = new HashMap<>();
        for (Explorateur e : passagers) {
            comptes.merge(e.getCouleur(), 1, Integer::sum);
        }
        return comptes;
    }
}
