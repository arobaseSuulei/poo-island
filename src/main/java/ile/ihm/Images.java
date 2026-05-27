package ile.ihm;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * Chargeur d'images depuis le dossier {@code resources/}.
 */
public final class Images {

    private static final String[] EXTENSIONS = {".jpg", ".png", ".jpeg"};
    private static final Map<String, BufferedImage> CACHE = new HashMap<>();

    private Images() {
    }

    /**
     * @param nomSansExtension chemin sans extension, p. ex. {@code "/images/fond-accueil"}
     * @return l'image chargee, ou {@code null} si aucun fichier correspondant
     */
    public static BufferedImage charger(String nomSansExtension) {
        if (CACHE.containsKey(nomSansExtension)) {
            return CACHE.get(nomSansExtension);
        }
        for (String ext : EXTENSIONS) {
            URL url = Images.class.getResource(nomSansExtension + ext);
            if (url != null) {
                try {
                    BufferedImage img = ImageIO.read(url);
                    CACHE.put(nomSansExtension, img);
                    return img;
                } catch (IOException e) {
                    // on continue avec les autres extensions
                }
            }
        }
        CACHE.put(nomSansExtension, null);
        return null;
    }
}
