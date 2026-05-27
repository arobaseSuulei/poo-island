package ile.ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

// Fenêtre des règles détaillées

public class FenetreRegles extends JDialog {

    private static final Color FOND = Color.WHITE;

    public FenetreRegles(JDialog parent) {
        super(parent, "Règles - The Island", true);
        setSize(800, 660);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(FOND);
        setLayout(new BorderLayout(10, 10));

        JTabbedPane onglets = new JTabbedPane();
        onglets.setFont(new Font("SansSerif", Font.BOLD, 13));
        onglets.setBackground(FOND);

        onglets.addTab("Plateau", creerOnglet(TEXTE_PLATEAU));
        onglets.addTab("Explorateurs", creerOnglet(TEXTE_EXPLORATEURS));
        onglets.addTab("Barques", creerOnglet(TEXTE_BARQUES));
        onglets.addTab("Créatures", creerOnglet(TEXTE_CREATURES));
        onglets.addTab("Tuiles", creerOnglet(TEXTE_TUILES));
        onglets.addTab("Tour de jeu", creerOnglet(TEXTE_TOUR));
        onglets.addTab("Fin & score", creerOnglet(TEXTE_FIN));

        JButton retour = new JButton("Retour");
        retour.setFont(new Font("SansSerif", Font.BOLD, 16));
        retour.setBackground(new Color(190, 110, 40));
        retour.setForeground(Color.WHITE);
        retour.setFocusPainted(false);
        retour.addActionListener(e -> dispose());

        JPanel bas = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        bas.setBackground(FOND);
        bas.add(retour);

        add(onglets, BorderLayout.CENTER);
        add(bas, BorderLayout.SOUTH);
    }

    private JComponent creerOnglet(String contenu) {
        JTextArea ta = new JTextArea(contenu);
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setMargin(new Insets(15, 15, 15, 15));
        ta.setFont(new Font("SansSerif", Font.PLAIN, 14));
        ta.setBackground(FOND);
        ta.setForeground(Color.BLACK);
        ta.setCaretPosition(0);
        JScrollPane scroll = new JScrollPane(ta);
        scroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        scroll.getViewport().setBackground(FOND);
        return scroll;
    }

    private static final String TEXTE_PLATEAU =
            "LE PLATEAU\n"
            + "\n"
            + "Le plateau est une grille rectangulaire de cases hexagonales :\n"
            + "  • 40 tuiles de terrain forment l'île au centre\n"
            + "  • Plusieurs cases dorées aux quatre coins forment les plages refuges\n"
            + "    (zones sûres où débarquer vos explorateurs)\n"
            + "  • Tout le reste est de la mer (zones bleues)\n"
            + "\n"
            + "Les tuiles de terrain sont de trois types, signalées par leur lettre :\n"
            + "  • P (jaune sable) : Plage\n"
            + "  • F (vert) : Forêt\n"
            + "  • M (gris) : Montagne\n"
            + "\n"
            + "Au départ, 5 serpents de mer occupent déjà des cases de mer.\n"
            + "Le positionnement des tuiles est tiré au hasard à chaque nouvelle partie.\n";

    private static final String TEXTE_EXPLORATEURS =
            "LES EXPLORATEURS\n"
            + "\n"
            + "Chaque joueur dispose de 10 explorateurs (cercles colorés).\n"
            + "Chaque explorateur transporte un trésor d'une valeur cachée, de 1 à 6.\n"
            + "Les valeurs des dix pions de chaque joueur sont : 1, 1, 1, 2, 2, 3, 3, 4, 5, 6.\n"
            + "\n"
            + "Statuts possibles :\n"
            + "  • SUR L'ÎLE : debout sur une tuile de terrain\n"
            + "  • NAGEUR : à la nage en mer (un nageur ne se déplace que d'une case par tour)\n"
            + "  • EMBARQUÉ : à bord d'une barque\n"
            + "  • SAUVÉ : a atteint un refuge (son trésor sera comptabilisé)\n"
            + "  • PERDU : dévoré par une créature ou englouti par un tourbillon\n"
            + "\n"
            + "Règles importantes :\n"
            + "  • Un explorateur qui a quitté l'île ne peut plus y retourner.\n"
            + "  • Un nageur qui se déplace sur un requin ou un serpent de mer est\n"
            + "    immédiatement éliminé.\n"
            + "  • Un nageur peut remonter dans une barque située sur sa propre case.\n"
            + "  • Un explorateur embarqué ne peut débarquer que sur une plage refuge\n"
            + "    adjacente (sauter à l'eau volontairement est interdit).\n";

    private static final String TEXTE_BARQUES =
            "LES BARQUES\n"
            + "\n"
            + "Chaque joueur reçoit 2 barques au départ. Le reste (12 au total) est mis\n"
            + "en réserve pour les pouvoirs d'apparition de barque.\n"
            + "\n"
            + "Capacité : une barque transporte au maximum 3 explorateurs, peu importe\n"
            + "leur couleur.\n"
            + "\n"
            + "Contrôle :\n"
            + "  • Une barque vide peut être déplacée par n'importe quel joueur.\n"
            + "  • Sinon, c'est le joueur majoritaire à bord qui contrôle la barque.\n"
            + "  • En cas d'égalité au sommet, chaque joueur à égalité peut la déplacer.\n"
            + "\n"
            + "Mouvements :\n"
            + "  • Une barque se déplace d'une seule case de mer par point.\n"
            + "  • Elle ne peut pas entrer sur l'île ou un refuge.\n"
            + "  • Pour sauver des explorateurs : amenez la barque adjacente à un refuge,\n"
            + "    puis débarquez les passagers un par un.\n";

    private static final String TEXTE_CREATURES =
            "LES CRÉATURES MARINES\n"
            + "\n"
            + "Trois espèces sont en jeu, identifiées par leur lettre :\n"
            + "\n"
            + "S — SERPENT DE MER (vert) — portée 1\n"
            + "  Engloutit toute barque chargée et ses passagers.\n"
            + "  Détruit tous les nageurs sur sa case d'arrivée.\n"
            + "  Épargne les barques vides.\n"
            + "\n"
            + "R — REQUIN (rouge) — portée 1 à 2\n"
            + "  Dévore tous les nageurs sur sa case d'arrivée.\n"
            + "  N'a aucun effet sur les barques.\n"
            + "\n"
            + "B — BALEINE (bleu) — portée 1 à 3\n"
            + "  Fait chavirer toute barque chargée : les passagers deviennent nageurs.\n"
            + "  Si un requin est sur la même case, les passagers tombés sont aussitôt\n"
            + "  dévorés.\n"
            + "  Épargne les nageurs déjà présents et les barques vides.\n"
            + "\n"
            + "Au début de la partie, 5 serpents de mer sont déjà sur le plateau.\n"
            + "6 requins et 5 baleines attendent en réserve.\n";

    private static final String TEXTE_TUILES =
            "LES POUVOIRS DES TUILES\n"
            + "\n"
            + "Quand une tuile est retirée, son pouvoir caché est dévoilé.\n"
            + "\n"
            + "POUVOIRS IMMÉDIATS (résolus aussitôt la tuile retournée) :\n"
            + "  • Apparition d'un requin (la créature attaque immédiatement les\n"
            + "    nageurs présents sur la case).\n"
            + "  • Apparition d'une baleine.\n"
            + "  • Apparition d'une barque (les nageurs présents y montent, max 3).\n"
            + "  • Tourbillon : engloutit la case et toutes les cases de mer voisines\n"
            + "    (nageurs perdus, barques + passagers perdus, créatures retirées).\n"
            + "  • Éruption volcanique : la partie s'achève immédiatement.\n"
            + "\n"
            + "POUVOIRS DÉBUT DE TOUR (gardés en main) :\n"
            + "  • Dauphin : déplace un de vos nageurs de 1 à 3 cases de mer.\n"
            + "  • Vent : déplace une de vos barques de 1 à 3 cases de mer.\n"
            + "  • Appel serpent / requin / baleine : déplace une créature présente\n"
            + "    sur le plateau vers une case de mer libre.\n"
            + "\n"
            + "POUVOIRS DE DÉFENSE (joués en réaction à une attaque adverse) :\n"
            + "  • Parade requin : chasse un requin qui menace l'un de vos nageurs.\n"
            + "  • Parade baleine : chasse une baleine qui menace une de vos barques.\n";

    private static final String TEXTE_TOUR =
            "LE DÉROULEMENT D'UN TOUR\n"
            + "\n"
            + "Les joueurs jouent l'un après l'autre. À chaque tour, dans l'ordre :\n"
            + "\n"
            + "1. DÉBUT DE TOUR\n"
            + "   Vous pouvez jouer une tuile pouvoir de votre main (dauphin, vent,\n"
            + "   appel de créature). Sinon, cliquez « Passer début de tour ».\n"
            + "\n"
            + "2. DÉPLACEMENT (3 points)\n"
            + "   Vous répartissez vos 3 points comme vous voulez :\n"
            + "     • Déplacer un explorateur d'une case = 1 point\n"
            + "     • Déplacer une barque d'une case = 1 point\n"
            + "     • Faire embarquer / débarquer un explorateur = 1 point\n"
            + "   Un même explorateur peut être déplacé plusieurs fois (sauf un nageur,\n"
            + "   limité à 1 case par tour).\n"
            + "\n"
            + "3. RETRAIT D'UNE TUILE\n"
            + "   Vous devez retirer une tuile en surbrillance rouge.\n"
            + "   L'ordre est imposé : toutes les plages d'abord, puis toutes les forêts,\n"
            + "   et enfin les montagnes.\n"
            + "   Si un explorateur était sur la tuile, il tombe à l'eau (devient nageur).\n"
            + "\n"
            + "4. LANCER DU DÉ\n"
            + "   Le dé des créatures est lancé. Si une créature de l'espèce tirée est\n"
            + "   sur le plateau, vous pouvez la déplacer selon sa portée.\n"
            + "   Si la créature arrive sur une cible, elle attaque (le joueur menacé\n"
            + "   peut éventuellement utiliser une tuile de défense).\n";

    private static final String TEXTE_FIN =
            "FIN DE PARTIE ET SCORE\n"
            + "\n"
            + "La partie s'arrête immédiatement quand la tuile « éruption volcanique »\n"
            + "est révélée : c'est l'unique tuile à effet « fin du jeu ».\n"
            + "\n"
            + "Conséquences :\n"
            + "  • Tous les explorateurs encore sur l'île, à la nage ou embarqués sont\n"
            + "    éliminés (l'île s'enfonce sous les flots).\n"
            + "  • Seuls les explorateurs ayant atteint un refuge sont sauvés.\n"
            + "\n"
            + "Calcul du score :\n"
            + "  • Chaque explorateur sauvé rapporte la valeur de son trésor (1 à 6).\n"
            + "  • Le joueur ayant le plus grand total de points gagne.\n"
            + "  • En cas d'égalité, c'est le joueur ayant sauvé le plus d'explorateurs\n"
            + "    qui est désigné vainqueur.\n"
            + "\n"
            + "Conseil stratégique :\n"
            + "  • Mémorisez bien la position de vos pions de haute valeur (5 et 6).\n"
            + "  • Sauvez-les en priorité : un seul pion de valeur 6 vaut six pions de\n"
            + "    valeur 1 !\n";
}
