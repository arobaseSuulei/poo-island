package ile.moteur;

import ile.pions.Joueur;
import ile.pions.monstres.Baleine;
import ile.pions.monstres.Requin;

/**
 * <p>L'interface graphique fournira une implementation interactive (boite de
 * dialogue). Les methodes possedent une implementation par defaut qui refuse la
 * defense, ce qui permet de faire fonctionner le moteur sans interface (tests).</p>
 */
public interface ArbitreDefense {

    /**
     * @param defenseur joueur menace possedant une tuile « parade requin »
     * @param requin    requin qui attaque
     * @return vrai si le joueur choisit de chasser le requin
     */
    default boolean defendreContreRequin(Joueur defenseur, Requin requin) {
        return false;
    }

    /**
     * @param defenseur joueur menace possedant une tuile « parade baleine »
     * @param baleine   baleine qui attaque
     * @return vrai si le joueur choisit de chasser la baleine
     */
    default boolean defendreContreBaleine(Joueur defenseur, Baleine baleine) {
        return false;
    }
}
