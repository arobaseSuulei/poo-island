package ile.tuiles;


public enum Pouvoir {

    APPARITION_REQUIN(Moment.IMMEDIAT, "Un requin surgit sur la case"),
    APPARITION_BALEINE(Moment.IMMEDIAT, "Une baleine surgit sur la case"),
    APPARITION_BARQUE(Moment.IMMEDIAT, "Une barque apparait sur la case"),
    TOURBILLON(Moment.IMMEDIAT, "Tourbillon : la case et ses voisines sont englouties"),
    ERUPTION(Moment.IMMEDIAT, "Eruption volcanique : fin immediate de la partie"),

    DAUPHIN(Moment.DEBUT_TOUR, "Un dauphin deplace un de vos nageurs de 1 a 3 cases"),
    VENT(Moment.DEBUT_TOUR, "Le vent deplace une de vos barques de 1 a 3 cases"),
    APPEL_SERPENT(Moment.DEBUT_TOUR, "Deplace un serpent vers une case de mer libre"),
    APPEL_REQUIN(Moment.DEBUT_TOUR, "Deplace un requin vers une case de mer libre"),
    APPEL_BALEINE(Moment.DEBUT_TOUR, "Deplace une baleine vers une case de mer libre"),

    PARADE_REQUIN(Moment.DEFENSE, "Chasse un requin qui attaque l'un de vos nageurs"),
    PARADE_BALEINE(Moment.DEFENSE, "Chasse une baleine qui menace l'une de vos barques");

    private final Moment moment;
    private final String libelle;

    Pouvoir(Moment moment, String libelle) {
        this.moment = moment;
        this.libelle = libelle;
    }

    /** @return le moment ou ce pouvoir peut etre joue. */
    public Moment getMoment() {
        return moment;
    }

    /** @return une description courte destinee a l'interface. */
    public String getLibelle() {
        return libelle;
    }
}
