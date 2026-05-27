package ile.ihm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Premier ecran affiche au lancement
 * {@code Jouer} / {@code Quitter}.
 */
public class EcranAccueil extends JFrame {

    // Taille par defaut de la fenêtre. 
    private static final Dimension TAILLE = new Dimension(1000, 650);

    public EcranAccueil() {
        setTitle("The Island");
        setSize(TAILLE);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        PanneauFond fond = new PanneauFond("/images/fond-accueil", new Color(20, 60, 100));
        fond.setLayout(new GridBagLayout());

        JLabel titre = new JLabel("Bienvenue dans The ISLAND");
        titre.setFont(new Font("Serif", Font.BOLD, 48));
        titre.setForeground(Color.WHITE);

        JButton jouer = boutonPrincipal("Jouer");
        JButton quitter = boutonPrincipal("Quitter");

        jouer.addActionListener(e -> {
            dispose();
            new EcranConfiguration().setVisible(true);
        });
        quitter.addActionListener(e -> System.exit(0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(20, 20, 20, 20);

        gbc.gridy = 0;
        fond.add(titre, gbc);
        gbc.gridy = 1;
        fond.add(jouer, gbc);
        gbc.gridy = 2;
        fond.add(quitter, gbc);

        setContentPane(fond);
    }

    private JButton boutonPrincipal(String texte) {
        JButton b = new JButton(texte);
        b.setFont(new Font("SansSerif", Font.BOLD, 24));
        b.setPreferredSize(new Dimension(240, 60));
        b.setBackground(new Color(190, 110, 40));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }
}
