package ile.ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Fenêtre d'aide textuelle
 */
public class FenetreAideTexte extends JDialog {

    private static final Color FOND = Color.WHITE;

    private static final String TEXTE =
            "COMMENT JOUER À THE ISLAND ?\n"
            + "\n"
            + "1. MISE EN PLACE\n"
            + "   • Chacun votre tour, cliquez sur une tuile verte pour y déposer un explorateur.\n"
            + "     Vous voyez la valeur du trésor que vous placez (1 à 6).\n"
            + "   • Une fois les 10 explorateurs de chacun déposés, placez deux barques chacun.\n"
            + "     Les cases en vert sont les emplacements valides (mer adjacente à l'île).\n"
            + "\n"
            + "2. À VOTRE TOUR\n"
            + "   • Cliquez « Passer début de tour » pour commencer.\n"
            + "   • Vous avez 3 points de déplacement.\n"
            + "   • Cliquez sur un de vos pions : la case s'illumine en jaune et les\n"
            + "     destinations possibles s'affichent en vert.\n"
            + "   • Cliquez une case verte pour vous y déplacer (1 point par déplacement).\n"
            + "   • Cliquez « Terminer déplacement » quand vous avez fini.\n"
            + "\n"
            + "3. NAGEUR → BARQUE\n"
            + "   • Un nageur peut remonter dans une barque de la même case : cliquez-le,\n"
            + "     puis cliquez à nouveau sur sa case (où se trouve la barque).\n"
            + "   • Un explorateur embarqué ne peut débarquer que sur une plage refuge\n"
            + "     adjacente, ou changer pour une barque adjacente.\n"
            + "\n"
            + "4. RETIRER UNE TUILE\n"
            + "   • Cliquez sur une tuile en rouge pour la retirer.\n"
            + "   • L'ordre est imposé : plages d'abord, forêts ensuite, montagnes enfin.\n"
            + "   • Son pouvoir caché est dévoilé et appliqué (ou gardé en main).\n"
            + "\n"
            + "5. LANCER LE DÉ\n"
            + "   • Cliquez « Lancer le dé ».\n"
            + "   • Les créatures de l'espèce tirée s'illuminent en orange : cliquez-en une\n"
            + "     puis cliquez sa destination (les cases accessibles s'illuminent en vert).\n"
            + "   • Sinon, cliquez « Passer créature » : le tour passe.\n"
            + "\n"
            + "OBJECTIF\n"
            + "   • Amenez vos explorateurs sur les plages refuges aux quatre coins du plateau.\n"
            + "   • Chaque explorateur sauvé rapporte la valeur de son trésor.\n"
            + "   • Le joueur avec le plus de points à la fin gagne.\n"
            + "\n"
            + "ATTENTION\n"
            + "   • L'éruption volcanique cachée sous une tuile montagne met fin à la partie\n"
            + "     immédiatement : tous les explorateurs non sauvés sont perdus.\n"
            + "   • Mémorisez bien les valeurs de vos pions au départ : elles sont cachées\n"
            + "     dès qu'ils sont posés sur le plateau.\n";

    public FenetreAideTexte(JDialog parent) {
        super(parent, "Aide - Comment jouer", true);
        setSize(700, 640);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(FOND);
        setLayout(new BorderLayout(10, 10));

        JTextArea texte = new JTextArea(TEXTE);
        texte.setEditable(false);
        texte.setLineWrap(true);
        texte.setWrapStyleWord(true);
        texte.setMargin(new Insets(15, 15, 15, 15));
        texte.setFont(new Font("SansSerif", Font.PLAIN, 14));
        texte.setBackground(FOND);
        texte.setForeground(Color.BLACK);
        texte.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(texte);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        scroll.getViewport().setBackground(FOND);

        JButton retour = new JButton("Retour");
        retour.setFont(new Font("SansSerif", Font.BOLD, 16));
        retour.setBackground(new Color(190, 110, 40));
        retour.setForeground(Color.WHITE);
        retour.setFocusPainted(false);
        retour.addActionListener(e -> dispose());

        JPanel bas = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        bas.setBackground(FOND);
        bas.add(retour);

        add(scroll, BorderLayout.CENTER);
        add(bas, BorderLayout.SOUTH);
    }
}
