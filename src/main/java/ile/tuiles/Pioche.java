package ile.tuiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * <p>Classe utilitaire : on regroupe ici la « recette » des tuiles afin que le
 * plateau n'ait pas a la connaitre.</p>
 */
public final class Pioche {

    private Pioche() {
    }

    /**
     * @param aleatoire source de hasard (permet de rejouer une partie identique)
     * @return la liste melangee des 40 tuiles
     */
    public static List<Tuile> melangee(Random aleatoire) {
        List<Tuile> tuiles = new ArrayList<>(40);

      
        ajouter(tuiles, Terrain.PLAGE, Pouvoir.APPARITION_BALEINE, 3);
        ajouter(tuiles, Terrain.PLAGE, Pouvoir.APPARITION_REQUIN, 3);
        ajouter(tuiles, Terrain.PLAGE, Pouvoir.APPARITION_BARQUE, 1);
        ajouter(tuiles, Terrain.PLAGE, Pouvoir.VENT, 2);
        ajouter(tuiles, Terrain.PLAGE, Pouvoir.DAUPHIN, 3);
        ajouter(tuiles, Terrain.PLAGE, Pouvoir.APPEL_SERPENT, 1);
        ajouter(tuiles, Terrain.PLAGE, Pouvoir.APPEL_REQUIN, 1);
        ajouter(tuiles, Terrain.PLAGE, Pouvoir.APPEL_BALEINE, 1);
        ajouter(tuiles, Terrain.PLAGE, Pouvoir.PARADE_REQUIN, 1);

        ajouter(tuiles, Terrain.FORET, Pouvoir.APPARITION_BALEINE, 2);
        ajouter(tuiles, Terrain.FORET, Pouvoir.APPARITION_REQUIN, 2);
        ajouter(tuiles, Terrain.FORET, Pouvoir.APPARITION_BARQUE, 3);
        ajouter(tuiles, Terrain.FORET, Pouvoir.TOURBILLON, 2);
        ajouter(tuiles, Terrain.FORET, Pouvoir.DAUPHIN, 1);
        ajouter(tuiles, Terrain.FORET, Pouvoir.APPEL_SERPENT, 1);
        ajouter(tuiles, Terrain.FORET, Pouvoir.APPEL_REQUIN, 1);
        ajouter(tuiles, Terrain.FORET, Pouvoir.APPEL_BALEINE, 1);
        ajouter(tuiles, Terrain.FORET, Pouvoir.PARADE_REQUIN, 1);
        ajouter(tuiles, Terrain.FORET, Pouvoir.PARADE_BALEINE, 2);

        ajouter(tuiles, Terrain.MONTAGNE, Pouvoir.APPARITION_REQUIN, 1);
        ajouter(tuiles, Terrain.MONTAGNE, Pouvoir.TOURBILLON, 4);
        ajouter(tuiles, Terrain.MONTAGNE, Pouvoir.ERUPTION, 1);
        ajouter(tuiles, Terrain.MONTAGNE, Pouvoir.PARADE_REQUIN, 1);
        ajouter(tuiles, Terrain.MONTAGNE, Pouvoir.PARADE_BALEINE, 1);

        Collections.shuffle(tuiles, aleatoire);
        return tuiles;
    }

    private static void ajouter(List<Tuile> liste, Terrain terrain, Pouvoir pouvoir, int nombre) {
        for (int i = 0; i < nombre; i++) {
            liste.add(new Tuile(terrain, pouvoir));
        }
    }
}
