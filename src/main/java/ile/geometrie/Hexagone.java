package ile.geometrie;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Coordonnee d'une case du plateau, en repere axial (q, r).
 */
public final class Hexagone {

    private final int q;
    private final int r;

    /**
     * Crée une coordonnee axiale.
     * @param q colonne axiale
     * @param r ligne axiale
     */
    public Hexagone(int q, int r) {
        this.q = q;
        this.r = r;
    }

    public int getQ() {
        return q;
    }

    public int getR() {
        return r;
    }

    /**
     * Renvoie la case voisine dans la direction indiquée
     * @param direction direction visee
     * @return l'hexagone voisin
     */
    public Hexagone voisin(Orientation direction) {
        return new Hexagone(q + direction.getDq(), r + direction.getDr());
    }

    /**
     * Renvoie les six voisins de cet hexagone.
     * @return la liste des six hexagones adjacents
     */
    public List<Hexagone> voisins() {
        List<Hexagone> liste = new ArrayList<>(6);
        for (Orientation direction : Orientation.values()) {
            liste.add(voisin(direction));
        }
        return liste;
    }

    /**
     * Distance hexagonale entre deux cases.
     *
     * @param autre autre hexagone
     * @return la distance hexagonale
     */
    public int distance(Hexagone autre) {
        // Formule classique en coordonnees cube
        int dq = Math.abs(q - autre.q);
        int dr = Math.abs(r - autre.r);
        int ds = Math.abs((q + r) - (autre.q + autre.r));
        return (dq + dr + ds) / 2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Hexagone)) {
            return false;
        }
        Hexagone autre = (Hexagone) o;
        return q == autre.q && r == autre.r;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r);
    }

    @Override
    public String toString() {
        return "(" + q + ", " + r + ")";
    }
}
