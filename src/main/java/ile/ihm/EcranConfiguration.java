package ile.ihm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Écran de configuration de la partie.
 */
public class EcranConfiguration extends JFrame {

    private static final Dimension TAILLE = new Dimension(1000, 650);

    private final JTextField champNombre = new JTextField(3);
    private final JPanel zonePseudos = new JPanel();
    private final List<JTextField> champsPseudos = new ArrayList<>();
    private int nombreJoueurs;

    public EcranConfiguration() {
        setTitle("The Island - Configuration");
        setSize(TAILLE);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        PanneauFond fond = new PanneauFond("/images/fond-configuration", new Color(20, 60, 100));
        fond.setLayout(new GridBagLayout());

        // Bouton retour en haut a gauche
        JButton retour = new JButton("Retour");
        retour.setFont(new Font("SansSerif", Font.BOLD, 16));
        retour.setBackground(new Color(140, 70, 30));
        retour.setForeground(Color.WHITE);
        retour.setFocusPainted(false);
        retour.addActionListener(e -> {
            dispose();
            new EcranAccueil().setVisible(true);
        });

        // Saisie du nombre de joueurs
        JLabel question = new JLabel("Combien de joueurs pour cette aventure ?");
        question.setFont(new Font("Serif", Font.BOLD, 28));
        question.setForeground(Color.WHITE);

        champNombre.setFont(new Font("SansSerif", Font.BOLD, 24));
        champNombre.setHorizontalAlignment(JTextField.CENTER);

        JButton ok = new JButton("OK");
        ok.setFont(new Font("SansSerif", Font.BOLD, 20));
        ok.setBackground(new Color(190, 110, 40));
        ok.setForeground(Color.WHITE);
        ok.setFocusPainted(false);
        ok.addActionListener(e -> traiterNombre());

        JPanel rangeeNombre = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        rangeeNombre.setOpaque(false);
        rangeeNombre.add(champNombre);
        rangeeNombre.add(ok);

        // Zone destinée à accueillir les champs de pseudos après validation
        zonePseudos.setOpaque(false);
        zonePseudos.setLayout(new GridLayout(0, 1, 8, 8));

        // Mise en place
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 20, 10, 20);

        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        fond.add(retour, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 1;
        fond.add(question, gbc);

        gbc.gridy = 2;
        fond.add(rangeeNombre, gbc);

        gbc.gridy = 3;
        fond.add(zonePseudos, gbc);

        setContentPane(fond);
    }

    private void traiterNombre() {
        int n;
        try {
            n = Integer.parseInt(champNombre.getText().trim());
        } catch (NumberFormatException ex) {
            n = -1;
        }
        if (n < 2 || n > 4) {
            JOptionPane.showMessageDialog(this,
                    "Saisissez un nombre entre 2 et 4.",
                    "Nombre invalide", JOptionPane.ERROR_MESSAGE);
            return;
        }
        nombreJoueurs = n;
        afficherZonePseudos();
    }

    private void afficherZonePseudos() {
        zonePseudos.removeAll();
        champsPseudos.clear();

        JLabel titre = new JLabel("Entrez vos pseudos");
        titre.setFont(new Font("Serif", Font.BOLD, 24));
        titre.setForeground(Color.WHITE);
        zonePseudos.add(titre);

        for (int i = 1; i <= nombreJoueurs; i++) {
            JPanel ligne = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            ligne.setOpaque(false);

            JLabel label = new JLabel("Joueur " + i + " :");
            label.setFont(new Font("SansSerif", Font.BOLD, 18));
            label.setForeground(Color.WHITE);

            JTextField champ = new JTextField(15);
            champ.setFont(new Font("SansSerif", Font.PLAIN, 18));

            champsPseudos.add(champ);
            ligne.add(label);
            ligne.add(champ);
            zonePseudos.add(ligne);
        }

        JButton demarrer = new JButton("Démarrer la partie");
        demarrer.setFont(new Font("SansSerif", Font.BOLD, 20));
        demarrer.setBackground(new Color(190, 110, 40));
        demarrer.setForeground(Color.WHITE);
        demarrer.setFocusPainted(false);
        demarrer.addActionListener(e -> demarrerPartie());
        zonePseudos.add(demarrer);

        zonePseudos.revalidate();
        zonePseudos.repaint();
    }

    private void demarrerPartie() {
        List<String> noms = new ArrayList<>();
        for (int i = 0; i < champsPseudos.size(); i++) {
            String pseudo = champsPseudos.get(i).getText().trim();
            if (pseudo.isEmpty()) {
                pseudo = "Joueur " + (i + 1);
            }
            noms.add(pseudo);
        }
        dispose();
        new FenetreJeu(noms).setVisible(true);
    }
}
