package ile.pions.monstres;

import ile.geometrie.Hexagone;
import ile.plateau.Cellule;

public abstract class Monstre {

    private Hexagone position;

    public Hexagone getPosition() {
        return position;
    }

    public void setPosition(Hexagone position) {
        this.position = position;
    }

    /**
     * @return le nombre maximal de cases de mer franchissables en un deplacement
     */
    public abstract int portee();

    /**
     * @return le nom lisible de la creature
     */
    public abstract String nom();

    /**
     * Applique l'effet propre a la creature sur la case ou elle arrive.
     *
     * @param cellule case de mer atteinte par la creature
     */
    public abstract void attaquer(Cellule cellule);
}
