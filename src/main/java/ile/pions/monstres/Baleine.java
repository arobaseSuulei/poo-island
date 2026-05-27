package ile.pions.monstres;

import java.util.ArrayList;

import ile.pions.Barque;
import ile.pions.Explorateur;
import ile.pions.Statut;
import ile.plateau.Cellule;

/**
 * <p>Elle fait chavirer toute barque chargee : les passagers tombent a l'eau et
 * deviennent nageurs. Si un requin occupe la meme case, ces malheureux sont
 * aussitot devores. La baleine epargne les nageurs deja presents et les barques
 * vides.</p>
 */
public class Baleine extends Monstre {

    @Override
    public int portee() {
        return 3;
    }

    @Override
    public String nom() {
        return "Baleine";
    }

    @Override
    public void attaquer(Cellule cellule) {
        boolean requinPresent = !cellule.monstresDeType(Requin.class).isEmpty();
        for (Barque barque : new ArrayList<>(cellule.getBarques())) {
            if (barque.estVide()) {
                continue;
            }
            for (Explorateur passager : new ArrayList<>(barque.getPassagers())) {
                barque.debarquer(passager);
                if (requinPresent) {
                    passager.setStatut(Statut.PERDU);
                    passager.setPosition(null);
                } else {
                    passager.setStatut(Statut.NAGEUR);
                    passager.setPosition(cellule.getPosition());
                    cellule.getExplorateurs().add(passager);
                }
            }
            cellule.getBarques().remove(barque);
        }
    }
}
