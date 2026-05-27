package ile.ihm;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * <p>Si l'image fournie est introuvable, le panneau se contente de remplir son
 * espace avec une couleur de repli.</p>
 */
public class PanneauFond extends JPanel {

    private final BufferedImage image;
    private final Color couleurDeRepli;

    /**
     * @param cheminRessource chemin sans extension de l'image dans les ressources
     * @param couleurDeRepli  couleur a utiliser si l'image est absente
     */
    public PanneauFond(String cheminRessource, Color couleurDeRepli) {
        this.image = Images.charger(cheminRessource);
        this.couleurDeRepli = couleurDeRepli;
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(couleurDeRepli);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
