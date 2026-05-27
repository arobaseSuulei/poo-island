package ile.moteur;

import java.util.List;
import java.util.Random;

import ile.pions.monstres.Baleine;
import ile.pions.monstres.Monstre;
import ile.pions.monstres.Requin;
import ile.pions.monstres.SerpentDeMer;

/**
 * <p>Le de renvoie la <em>classe</em> de l'espece tiree, ce qui permet ensuite
 * de retrouver, par polymorphisme, les creatures correspondantes sur le
 * plateau.</p>
 */
public class De {

    private static final List<Class<? extends Monstre>> FACES = List.of(
            SerpentDeMer.class, SerpentDeMer.class,
            Requin.class, Requin.class,
            Baleine.class, Baleine.class);

    private final Random aleatoire;

    /**
     * @param aleatoire source de hasard partagee avec la partie
     */
    public De(Random aleatoire) {
        this.aleatoire = aleatoire;
    }

    /**
     * Lance le de.
     *
     * @return la classe de l'espece tiree
     */
    public Class<? extends Monstre> lancer() {
        return FACES.get(aleatoire.nextInt(FACES.size()));
    }
}
