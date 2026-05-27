package ile.ihm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Fenêtre principale d'aide proposant trois choix : afficher l'aide textuelle,
 * consulter les règles détaillées, ou revenir au jeu.
 */
public class FenetreAide extends JDialog {

    private static final Color FOND = Color.WHITE;
    private static final Color TEXTE_TITRE = new Color(20, 60, 100);

    public FenetreAide(JFrame parent) {
        super(parent, "Aide - The Island", true);
        setSize(440, 360);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(FOND);
        setLayout(new GridBagLayout());

        JLabel titre = new JLabel("Centre d'aide");
        titre.setForeground(TEXTE_TITRE);
        titre.setFont(new Font("Serif", Font.BOLD, 28));

        JButton btnAide = bouton("Aide");
        JButton btnRegle = bouton("Règle");
        JButton btnRetour = bouton("Retour");

        btnAide.addActionListener(e -> new FenetreAideTexte(this).setVisible(true));
        btnRegle.addActionListener(e -> new FenetreRegles(this).setVisible(true));
        btnRetour.addActionListener(e -> dispose());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 20, 10, 20);

        gbc.gridy = 0;
        add(titre, gbc);
        gbc.gridy = 1;
        add(btnAide, gbc);
        gbc.gridy = 2;
        add(btnRegle, gbc);
        gbc.gridy = 3;
        add(btnRetour, gbc);
    }

    private JButton bouton(String texte) {
        JButton b = new JButton(texte);
        b.setFont(new Font("SansSerif", Font.BOLD, 18));
        b.setPreferredSize(new Dimension(220, 50));
        b.setBackground(new Color(190, 110, 40));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }
}
