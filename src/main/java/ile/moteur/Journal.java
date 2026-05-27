package ile.moteur;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Journal {

    private static final int MAX_LIGNES = 200;

    private final List<String> lignes = new ArrayList<>();

    /**
     * Ajoute une ligne a l'historique.
     *
     * @param message texte a journaliser
     */
    public void ajouter(String message) {
        lignes.add(message);
        if (lignes.size() > MAX_LIGNES) {
            lignes.remove(0);
        }
    }

    /** @return la liste (non modifiable) des lignes du journal. */
    public List<String> getLignes() {
        return Collections.unmodifiableList(lignes);
    }

    /** @return la derniere ligne, ou une chaine vide si le journal est vide. */
    public String derniere() {
        return lignes.isEmpty() ? "" : lignes.get(lignes.size() - 1);
    }
}
