package ile.pions.monstres;

import ile.pions.Explorateur;
import ile.pions.Statut;
import ile.plateau.Cellule;

/***
 * <p>Il devore les nageurs de la case ou il s'arrete, mais n'a aucun effet sur
 * les barques.</p>
 */
public class Requin extends Monstre {

    @Override
    public int portee() {
        return 2;
    }

    @Override
    public String nom() {
        return "Requin";
    }

    @Override
    public void attaquer(Cellule cellule) {
        for (Explorateur nageur : cellule.explorateursParStatut(Statut.NAGEUR)) {
            cellule.getExplorateurs().remove(nageur);
            nageur.setStatut(Statut.PERDU);
            nageur.setPosition(null);
        }
    }
}
