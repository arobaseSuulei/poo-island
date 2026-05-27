package ile.plateau;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

import ile.geometrie.Hexagone;
import ile.pions.monstres.Monstre;
import ile.pions.monstres.SerpentDeMer;
import ile.tuiles.Tuile;

public class Plateau {


    public static final int TUILES_ILE = 40;
    public static final int LARGEUR = 12;
    public static final int HAUTEUR = 13;
    private static final int SERPENTS_INITIAUX = 5;
    private static final int[] CASES_PAR_LIGNE = {7, 10, 11, 10, 11, 12, 11, 12, 11, 10, 11, 10, 7};
    private static final int[] DEBUT_PAR_LIGNE = {3, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 3};
    private static final int[][] REFUGES_OFFSET = {
            {1, 1}, {10, 1},
            {1, 2}, {11, 2},
            {1, 10}, {11, 10},
            {1, 11}, {10, 11}
    };

    private static final int[][] ILE_OFFSET = {

            {4, 3}, {5, 3}, {6, 3}, {7, 3},
          
            {4, 4}, {5, 4}, {6, 4}, {7, 4}, {8, 4},
           
            {2, 5}, {3, 5}, {4, 5}, {5, 5}, {6, 5}, {7, 5}, {8, 5}, {9, 5},
           
            {3, 6}, {4, 6}, {5, 6}, {7, 6}, {8, 6}, {9, 6},
           
            {2, 7}, {3, 7}, {4, 7}, {5, 7}, {6, 7}, {7, 7}, {8, 7}, {9, 7},
            
            {4, 8}, {5, 8}, {6, 8}, {7, 8}, {8, 8},
           
            {4, 9}, {5, 9}, {6, 9}, {7, 9}
    };

   
    private static final int[][] SERPENTS_OFFSET = {
            {6, 6},  
            {2, 1},  
            {9, 1}, 
            {2, 11}, 
            {9, 11}  
    };

    private final Map<Hexagone, Cellule> cellules = new HashMap<>();
    private final List<Hexagone> emplacementsIle = new ArrayList<>();
    private final List<Hexagone> refuges = new ArrayList<>();

    public Plateau() {
        construireGeometrie();
    }

    /**
     * @param col colonne dans la grille offset
     * @param row rangée dans la grille offset
     * @return l'hexagone axial correspondant
     */
    public static Hexagone depuisOffset(int col, int row) {
        int q = col - (row - (row & 1)) / 2;
        return new Hexagone(q, row);
    }

    private void construireGeometrie() {

        Set<Hexagone> refugesPrevus = new HashSet<>();
        for (int[] coord : REFUGES_OFFSET) {
            refugesPrevus.add(depuisOffset(coord[0], coord[1]));
        }
        refuges.addAll(refugesPrevus);

        for (int[] coord : ILE_OFFSET) {
            emplacementsIle.add(depuisOffset(coord[0], coord[1]));
        }

        for (int row = 0; row < CASES_PAR_LIGNE.length; row++) {
            int count = CASES_PAR_LIGNE[row];
            int start = DEBUT_PAR_LIGNE[row];
            for (int dc = 0; dc < count; dc++) {
                Hexagone h = depuisOffset(start + dc, row);
                boolean refuge = refugesPrevus.contains(h);
                cellules.put(h, new Cellule(h, refuge, null));
            }
        }
    }

    public void poserTuiles(List<Tuile> tuiles) {
        for (int i = 0; i < emplacementsIle.size(); i++) {
            cellules.get(emplacementsIle.get(i)).setTuile(tuiles.get(i));
        }
    }

    public void placerSerpentsInitiaux() {
        for (int[] coord : SERPENTS_OFFSET) {
            Hexagone h = depuisOffset(coord[0], coord[1]);
            ajouterMonstre(new SerpentDeMer(), h);
        }
    }

    public Cellule getCellule(Hexagone h) {
        return cellules.get(h);
    }

    public boolean contient(Hexagone h) {
        return cellules.containsKey(h);
    }

    public Collection<Cellule> toutesLesCellules() {
        return cellules.values();
    }

    public List<Hexagone> getEmplacementsIle() {
        return emplacementsIle;
    }

    public List<Hexagone> getRefuges() {
        return refuges;
    }

    public List<Cellule> voisines(Hexagone h) {
        List<Cellule> liste = new ArrayList<>();
        for (Hexagone v : h.voisins()) {
            Cellule c = cellules.get(v);
            if (c != null) {
                liste.add(c);
            }
        }
        return liste;
    }

    public void ajouterMonstre(Monstre monstre, Hexagone h) {
        monstre.setPosition(h);
        cellules.get(h).getMonstres().add(monstre);
    }

    public List<Monstre> tousLesMonstres() {
        List<Monstre> liste = new ArrayList<>();
        for (Cellule c : cellules.values()) {
            liste.addAll(c.getMonstres());
        }
        return liste;
    }

   
    public Set<Hexagone> casesAccessibles(Hexagone depart, int distanceMax,
                                          Predicate<Cellule> traversable) {
        Set<Hexagone> atteintes = new HashSet<>();
        Map<Hexagone, Integer> distance = new HashMap<>();
        Queue<Hexagone> file = new ArrayDeque<>();
        distance.put(depart, 0);
        file.add(depart);

        while (!file.isEmpty()) {
            Hexagone courant = file.poll();
            int d = distance.get(courant);
            if (d >= distanceMax) {
                continue;
            }
            for (Hexagone voisin : courant.voisins()) {
                Cellule cellule = cellules.get(voisin);
                if (cellule == null || distance.containsKey(voisin)) {
                    continue;
                }
                if (!traversable.test(cellule)) {
                    continue;
                }
                distance.put(voisin, d + 1);
                atteintes.add(voisin);
                file.add(voisin);
            }
        }
        return atteintes;
    }
}
