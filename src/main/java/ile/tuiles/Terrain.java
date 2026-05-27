package ile.tuiles;

/**
 * <p>L'ordre de declaration reflete l'ordre de retrait impose par les regles :
 * on retire d'abord les plages, puis les forets, et enfin les montagnes. On
 * peut donc comparer deux terrains par leur {@link #ordinal()}.</p>
 */
public enum Terrain {
    PLAGE,
    FORET,
    MONTAGNE;
}
