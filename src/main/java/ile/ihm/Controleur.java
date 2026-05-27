package ile.ihm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import ile.geometrie.Hexagone;
import ile.moteur.ArbitreDefense;
import ile.moteur.Partie;
import ile.pions.Barque;
import ile.pions.Explorateur;
import ile.pions.Joueur;
import ile.pions.Statut;
import ile.pions.monstres.Baleine;
import ile.pions.monstres.Monstre;
import ile.pions.monstres.Requin;
import ile.pions.monstres.SerpentDeMer;
import ile.plateau.Cellule;
import ile.tuiles.Moment;
import ile.tuiles.Pouvoir;
import ile.tuiles.Tuile;

/**
 * Pilote l'interaction entre la vue et la {@link Partie}.
 */
public class Controleur implements ArbitreDefense {

    private final Partie partie;
    private final FenetreJeu fenetre;

    private Explorateur explorateurSelectionne;
    private Barque barqueSelectionnee;
    private Monstre creatureSelectionnee;

    // État du cycle de sélection sur la même case
    private Hexagone derniereCelluleCliquee;
    private int indexCycle = -1;

    // État du flux d'une tuile pouvoir en cours d'utilisation
    private Tuile tuileEnCours;
    private Object cibleIntermediaire;

    public Controleur(Partie partie, FenetreJeu fenetre) {
        this.partie = partie;
        this.fenetre = fenetre;
    }

    public Partie getPartie() {
        return partie;
    }

    public Tuile getTuileEnCours() {
        return tuileEnCours;
    }

    public Hexagone getCaseSelectionnee() {
        if (cibleIntermediaire instanceof Explorateur) {
            return ((Explorateur) cibleIntermediaire).positionReelle();
        }
        if (cibleIntermediaire instanceof Barque) {
            return ((Barque) cibleIntermediaire).getPosition();
        }
        if (cibleIntermediaire instanceof Monstre) {
            return ((Monstre) cibleIntermediaire).getPosition();
        }
        if (explorateurSelectionne != null) {
            return explorateurSelectionne.positionReelle();
        }
        if (barqueSelectionnee != null) {
            return barqueSelectionnee.getPosition();
        }
        if (creatureSelectionnee != null) {
            return creatureSelectionnee.getPosition();
        }
        return null;
    }

    public Set<Hexagone> getDestinationsCourantes() {
        if (tuileEnCours != null) {
            return cellulesPourTuile();
        }
        if (explorateurSelectionne != null) {
            return partie.destinationsExplorateur(explorateurSelectionne);
        }
        if (barqueSelectionnee != null) {
            return partie.destinationsBarque(barqueSelectionnee);
        }
        if (creatureSelectionnee != null) {
            return partie.destinationsCreature(creatureSelectionnee);
        }
        return new HashSet<>();
    }

    public String libelleSelection() {
        if (tuileEnCours != null) {
            String etape = cibleIntermediaire == null ? "choisir la cible" : "choisir la destination";
            return "Tuile : " + tuileEnCours.getPouvoir().name() + " (" + etape + ")";
        }
        if (barqueSelectionnee != null) {
            int n = barqueSelectionnee.getPassagers().size();
            return "Barque (" + n + " passager" + (n > 1 ? "s)" : ")");
        }
        if (explorateurSelectionne != null) {
            return "Explorateur " + explorateurSelectionne.getCouleur();
        }
        if (creatureSelectionnee != null) {
            return creatureSelectionnee.nom();
        }
        return null;
    }

    public void cliquerSurCase(Hexagone h) {
        if (tuileEnCours != null) {
            gererClicTuile(h);
            fenetre.rafraichir();
            return;
        }
        switch (partie.getEtape()) {
            case PLACEMENT_EXPLORATEURS:
                partie.placerExplorateur(h);
                break;
            case PLACEMENT_BARQUES:
                partie.placerBarque(h);
                break;
            case DEPLACEMENT:
                gererClicDeplacement(h);
                break;
            case RETRAIT_TUILE:
                partie.retirerTuile(h);
                break;
            case DEPLACEMENT_CREATURE:
                gererClicCreature(h);
                break;
            default:
                break;
        }
        fenetre.rafraichir();
    }

    private void gererClicDeplacement(Hexagone h) {
        if (explorateurSelectionne != null
                && partie.deplacerExplorateur(explorateurSelectionne, h)) {
            reinitialiserSelection();
            return;
        }
        if (barqueSelectionnee != null
                && partie.deplacerBarque(barqueSelectionnee, h)) {
            reinitialiserSelection();
            return;
        }
        List<Object> cibles = construireSelectables(h);
        if (cibles.isEmpty()) {
            reinitialiserSelection();
            return;
        }
        int index;
        if (h.equals(derniereCelluleCliquee) && indexCycle >= 0) {
            index = (indexCycle + 1) % cibles.size();
        } else {
            index = 0;
        }
        derniereCelluleCliquee = h;
        indexCycle = index;
        Object cible = cibles.get(index);
        if (cible instanceof Barque) {
            barqueSelectionnee = (Barque) cible;
            explorateurSelectionne = null;
        } else if (cible instanceof Explorateur) {
            explorateurSelectionne = (Explorateur) cible;
            barqueSelectionnee = null;
        }
    }

    private List<Object> construireSelectables(Hexagone h) {
        Cellule c = partie.getPlateau().getCellule(h);
        if (c == null) {
            return Collections.emptyList();
        }
        Joueur joueur = partie.joueurCourant();
        List<Object> liste = new ArrayList<>();
        for (Explorateur e : c.getExplorateurs()) {
            if (e.getCouleur() == joueur.getCouleur() && e.estEnJeu()) {
                liste.add(e);
            }
        }
        for (Barque b : c.getBarques()) {
            if (b.estManoeuvrablePar(joueur.getCouleur())) {
                liste.add(b);
            }
        }
        for (Barque b : c.getBarques()) {
            for (Explorateur e : b.getPassagers()) {
                if (e.getCouleur() == joueur.getCouleur()) {
                    liste.add(e);
                }
            }
        }
        return liste;
    }

    private void gererClicCreature(Hexagone h) {
        if (creatureSelectionnee != null
                && partie.deplacerCreature(creatureSelectionnee, h)) {
            reinitialiserSelection();
            return;
        }
        List<Monstre> creaturesIci = creaturesEligiblesIci(h);
        if (creaturesIci.isEmpty()) {
            reinitialiserSelection();
            return;
        }
        int index;
        if (h.equals(derniereCelluleCliquee) && indexCycle >= 0) {
            index = (indexCycle + 1) % creaturesIci.size();
        } else {
            index = 0;
        }
        derniereCelluleCliquee = h;
        indexCycle = index;
        creatureSelectionnee = creaturesIci.get(index);
    }

    private List<Monstre> creaturesEligiblesIci(Hexagone h) {
        Cellule c = partie.getPlateau().getCellule(h);
        if (c == null) {
            return Collections.emptyList();
        }
        List<Monstre> eligibles = partie.creaturesDeplacables();
        List<Monstre> resultat = new ArrayList<>();
        for (Monstre m : c.getMonstres()) {
            if (eligibles.contains(m)) {
                resultat.add(m);
            }
        }
        return resultat;
    }

    public void actionJouerTuile() {
        if (partie.getEtape() != ile.moteur.Etape.DEBUT_TOUR) {
            return;
        }
        Joueur j = partie.joueurCourant();
        List<Tuile> jouables = new ArrayList<>();
        for (Tuile t : j.getMain()) {
            if (t.getPouvoir().getMoment() == Moment.DEBUT_TOUR) {
                jouables.add(t);
            }
        }
        if (jouables.isEmpty()) {
            JOptionPane.showMessageDialog(fenetre,
                    "Vous n'avez aucune tuile pouvoir jouable en début de tour.",
                    "Aucune tuile jouable", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] options = new String[jouables.size()];
        for (int i = 0; i < jouables.size(); i++) {
            options[i] = jouables.get(i).getPouvoir().getLibelle();
        }
        int choix = JOptionPane.showOptionDialog(fenetre,
                "Choisissez une tuile à jouer :", "Tuile pouvoir",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        if (choix < 0) {
            return;
        }
        tuileEnCours = jouables.get(choix);
        cibleIntermediaire = null;
        fenetre.rafraichir();
    }

    public void actionAnnulerTuile() {
        tuileEnCours = null;
        cibleIntermediaire = null;
        fenetre.rafraichir();
    }

    private void gererClicTuile(Hexagone h) {
        Pouvoir p = tuileEnCours.getPouvoir();
        Cellule c = partie.getPlateau().getCellule(h);
        if (c == null) {
            return;
        }
        switch (p) {
            case DAUPHIN:
                gererClicDauphin(h, c);
                break;
            case VENT:
                gererClicVent(h, c);
                break;
            case APPEL_SERPENT:
            case APPEL_REQUIN:
            case APPEL_BALEINE:
                gererClicAppel(h, c, p);
                break;
            default:
                actionAnnulerTuile();
                break;
        }
    }

    private void gererClicDauphin(Hexagone h, Cellule c) {
        Joueur j = partie.joueurCourant();
        if (cibleIntermediaire == null) {
            for (Explorateur e : c.getExplorateurs()) {
                if (e.getStatut() == Statut.NAGEUR && e.getCouleur() == j.getCouleur()) {
                    cibleIntermediaire = e;
                    return;
                }
            }
        } else if (cibleIntermediaire instanceof Explorateur) {
            Explorateur nageur = (Explorateur) cibleIntermediaire;
            if (partie.jouerDauphin(tuileEnCours, nageur, h)) {
                actionAnnulerTuile();
            }
        }
    }

    private void gererClicVent(Hexagone h, Cellule c) {
        Joueur j = partie.joueurCourant();
        if (cibleIntermediaire == null) {
            for (Barque b : c.getBarques()) {
                if (b.estManoeuvrablePar(j.getCouleur())) {
                    cibleIntermediaire = b;
                    return;
                }
            }
        } else if (cibleIntermediaire instanceof Barque) {
            Barque barque = (Barque) cibleIntermediaire;
            if (partie.jouerVent(tuileEnCours, barque, h)) {
                actionAnnulerTuile();
            }
        }
    }

    private void gererClicAppel(Hexagone h, Cellule c, Pouvoir p) {
        Class<? extends Monstre> attendu = especePourAppel(p);
        if (cibleIntermediaire == null) {
            if (attendu == null) {
                return;
            }
            for (Monstre m : c.getMonstres()) {
                if (attendu.isInstance(m)) {
                    cibleIntermediaire = m;
                    return;
                }
            }
        } else if (cibleIntermediaire instanceof Monstre) {
            Monstre m = (Monstre) cibleIntermediaire;
            if (partie.jouerAppel(tuileEnCours, m, h)) {
                actionAnnulerTuile();
            }
        }
    }

    private Class<? extends Monstre> especePourAppel(Pouvoir p) {
        switch (p) {
            case APPEL_SERPENT:
                return SerpentDeMer.class;
            case APPEL_REQUIN:
                return Requin.class;
            case APPEL_BALEINE:
                return Baleine.class;
            default:
                return null;
        }
    }

    private Set<Hexagone> cellulesPourTuile() {
        Set<Hexagone> resultat = new HashSet<>();
        Pouvoir p = tuileEnCours.getPouvoir();
        Joueur j = partie.joueurCourant();

        if (cibleIntermediaire == null) {
            switch (p) {
                case DAUPHIN:
                    for (Cellule c : partie.getPlateau().toutesLesCellules()) {
                        for (Explorateur e : c.getExplorateurs()) {
                            if (e.getStatut() == Statut.NAGEUR && e.getCouleur() == j.getCouleur()) {
                                resultat.add(c.getPosition());
                            }
                        }
                    }
                    break;
                case VENT:
                    for (Cellule c : partie.getPlateau().toutesLesCellules()) {
                        for (Barque b : c.getBarques()) {
                            if (b.estManoeuvrablePar(j.getCouleur())) {
                                resultat.add(c.getPosition());
                            }
                        }
                    }
                    break;
                case APPEL_SERPENT:
                case APPEL_REQUIN:
                case APPEL_BALEINE:
                    Class<? extends Monstre> attendu = especePourAppel(p);
                    if (attendu != null) {
                        for (Monstre m : partie.getPlateau().tousLesMonstres()) {
                            if (attendu.isInstance(m)) {
                                resultat.add(m.getPosition());
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        } else {
            switch (p) {
                case DAUPHIN:
                    Explorateur nageur = (Explorateur) cibleIntermediaire;
                    resultat.addAll(partie.getPlateau().casesAccessibles(
                            nageur.getPosition(), 3, Cellule::estMer));
                    break;
                case VENT:
                    Barque barque = (Barque) cibleIntermediaire;
                    resultat.addAll(partie.getPlateau().casesAccessibles(
                            barque.getPosition(), 3, Cellule::estMer));
                    break;
                case APPEL_SERPENT:
                case APPEL_REQUIN:
                case APPEL_BALEINE:
                    for (Cellule c : partie.getPlateau().toutesLesCellules()) {
                        if (c.estMer() && c.getMonstres().isEmpty()
                                && c.getBarques().isEmpty()
                                && c.getExplorateurs().isEmpty()) {
                            resultat.add(c.getPosition());
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return resultat;
    }


    public void actionPasserDebutTour() {
        partie.passerDebutTour();
        reinitialiserSelection();
        fenetre.rafraichir();
    }

    public void actionTerminerDeplacement() {
        partie.terminerDeplacement();
        reinitialiserSelection();
        fenetre.rafraichir();
    }

    public void actionLancerDe() {
        partie.lancerDe();
        reinitialiserSelection();
        fenetre.rafraichir();
    }

    public void actionPasserCreature() {
        partie.passerCreature();
        reinitialiserSelection();
        fenetre.rafraichir();
    }

    private void reinitialiserSelection() {
        explorateurSelectionne = null;
        barqueSelectionnee = null;
        creatureSelectionnee = null;
        tuileEnCours = null;
        cibleIntermediaire = null;
        derniereCelluleCliquee = null;
        indexCycle = -1;
    }


    @Override
    public boolean defendreContreRequin(Joueur defenseur, Requin requin) {
        int rep = JOptionPane.showConfirmDialog(fenetre,
                defenseur.getNom() + " : un requin va attaquer l'un de vos nageurs !\n\n"
                        + "Souhaitez-vous jouer votre tuile « parade requin » pour le chasser ?",
                "Défense possible — " + defenseur.getNom(),
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return rep == JOptionPane.YES_OPTION;
    }

    @Override
    public boolean defendreContreBaleine(Joueur defenseur, Baleine baleine) {
        int rep = JOptionPane.showConfirmDialog(fenetre,
                defenseur.getNom() + " : une baleine va faire chavirer votre barque !\n\n"
                        + "Souhaitez-vous jouer votre tuile « parade baleine » pour la chasser ?",
                "Défense possible — " + defenseur.getNom(),
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return rep == JOptionPane.YES_OPTION;
    }
}
