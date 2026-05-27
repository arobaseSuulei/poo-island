package ile.moteur;

/**
 * <p>Les deux premieres concernent la mise en place ; les cinq suivantes se
 * succedent a chaque tour, dans l'ordre impose par les regles.</p>
 */
public enum Etape {

    PLACEMENT_EXPLORATEURS,
    PLACEMENT_BARQUES,
    DEBUT_TOUR,
    DEPLACEMENT,
    RETRAIT_TUILE,
    LANCER_DE,
    DEPLACEMENT_CREATURE,
    TERMINEE;
}
