package ile.geometrie;

/**
 * Les six directions menant d'une case vers une case voisine, exprimees en
 * coordonnees axiales {@code (dq, dr)}.
 */
public enum Orientation {

    NORD(0, -1),
    SUD(0, 1),
    NORD_EST(1, -1),
    SUD_EST(1, 0),
    NORD_OUEST(-1, 0),
    SUD_OUEST(-1, 1);

    private final int dq;
    private final int dr;

    Orientation(int dq, int dr) {
        this.dq = dq;
        this.dr = dr;
    }

    /** @return le décalage en colonne axiale. */
    public int getDq() {
        return dq;
    }

    /** @return le décalage en ligne axiale. */
    public int getDr() {
        return dr;
    }
}
