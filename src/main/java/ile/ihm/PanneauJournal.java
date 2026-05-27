package ile.ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import ile.moteur.Partie;

/**
 * Journal des évènements de la partie.
 */
public class PanneauJournal extends JPanel {

    private static final Color FOND = Color.WHITE;
    private static final Color TITRE = new Color(20, 60, 100);
    private static final Color BORDURE = new Color(100, 100, 100);

    private final Partie partie;
    private final JTextArea zone;

    public PanneauJournal(Partie partie) {
        this.partie = partie;
        setBackground(FOND);
        setLayout(new BorderLayout(0, 0));

        TitledBorder titre = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDURE, 1), "Journal");
        titre.setTitleColor(TITRE);
        titre.setTitleFont(new Font("SansSerif", Font.BOLD, 14));
        setBorder(titre);

        zone = new JTextArea();
        zone.setEditable(false);
        zone.setLineWrap(true);
        zone.setWrapStyleWord(true);
        zone.setFont(new Font("SansSerif", Font.PLAIN, 11));
        zone.setBackground(FOND);
        zone.setForeground(Color.BLACK);
        zone.setMargin(new Insets(6, 6, 6, 6));

        JScrollPane scroll = new JScrollPane(zone);
        scroll.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        scroll.getViewport().setBackground(FOND);
        add(scroll, BorderLayout.CENTER);

        rafraichir();
    }

    // Recopie les lignes du journal et fait défiler vers la dernière. 
    public void rafraichir() {
        StringBuilder sb = new StringBuilder();
        for (String ligne : partie.getJournal().getLignes()) {
            sb.append(ligne).append('\n');
        }
        zone.setText(sb.toString());
        zone.setCaretPosition(zone.getDocument().getLength());
    }
}
