package ile.pions.monstres;

import java.util.ArrayList;

import ile.pions.Barque;
import ile.pions.Explorateur;
import ile.pions.Statut;
import ile.plateau.Cellule;

/**
 * <p>Il engloutit toute barque chargee et ses passagers, ainsi que tous les
 * nageurs presents sur la case. Une barque vide est epargnee.</p>
 */
public class SerpentDeMer extends Monstre {

    @Override
    public int portee() {
        return 1;
    }

    @Override
    public String nom() {
        return "Serpent de mer";
    }

    @Override
    public void attaquer(Cellule cellule) {
        for (Barque barque : new ArrayList<>(cellule.getBarques())) {
            if (!barque.estVide()) {
                for (Explorateur passager : new ArrayList<>(barque.getPassagers())) {
                    barque.debarquer(passager);
                    passager.setStatut(Statut.PERDU);
                    passager.setPosition(null);
                }
                cellule.getBarques().remove(barque);
            }
        }
        for (Explorateur nageur : cellule.explorateursParStatut(Statut.NAGEUR)) {
            cellule.getExplorateurs().remove(nageur);
            nageur.setStatut(Statut.PERDU);
            nageur.setPosition(null);
        }
    }
}
