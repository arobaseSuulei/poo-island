package ile.tuiles;


public class Tuile {

    private final Terrain terrain;
    private final Pouvoir pouvoir;
    private boolean revelee;

    /**
     * Cree une tuile face cachee.
     *
     * @param terrain nature visible de la tuile
     * @param pouvoir pouvoir dissimule sous la tuile
     */
    public Tuile(Terrain terrain, Pouvoir pouvoir) {
        this.terrain = terrain;
        this.pouvoir = pouvoir;
        this.revelee = false;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public Pouvoir getPouvoir() {
        return pouvoir;
    }

    /** @return vrai si le pouvoir de la tuile a deja ete devoile. */
    public boolean estRevelee() {
        return revelee;
    }

    public void reveler() {
        revelee = true;
    }

    @Override
    public String toString() {
        return terrain + " [" + pouvoir + "]";
    }
}
