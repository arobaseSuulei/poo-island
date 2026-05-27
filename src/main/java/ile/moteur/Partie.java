package ile.moteur;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import ile.geometrie.Hexagone;
import ile.plateau.Cellule;
import ile.plateau.Plateau;
import ile.pions.Barque;
import ile.pions.CouleurJoueur;
import ile.pions.Explorateur;
import ile.pions.Joueur;
import ile.pions.Statut;
import ile.pions.monstres.Baleine;
import ile.pions.monstres.Monstre;
import ile.pions.monstres.Requin;
import ile.pions.monstres.SerpentDeMer;
import ile.tuiles.Moment;
import ile.tuiles.Pioche;
import ile.tuiles.Pouvoir;
import ile.tuiles.Terrain;
import ile.tuiles.Tuile;

/**
 * Coeur du jeu 
 */
public class Partie {

    public static final int POINTS_PAR_TOUR = 3;
    private static final int[] TRESORS = {1, 1, 1, 2, 2, 3, 3, 4, 5, 6};
    private static final int NB_BARQUES = 12;
    private static final int NB_REQUINS = 6;
    private static final int NB_BALEINES = 5;
    private static final int BARQUES_PAR_JOUEUR = 2;

    private final Plateau plateau;
    private final List<Joueur> joueurs;
    private final Random aleatoire;
    private final De de;
    private final Journal journal = new Journal();

    private final Deque<Requin> reserveRequins = new ArrayDeque<>();
    private final Deque<Baleine> reserveBaleines = new ArrayDeque<>();
    private final Deque<Barque> reserveBarques = new ArrayDeque<>();

    private ArbitreDefense arbitre = new ArbitreDefense() {
    };

    private int indexCourant;
    private Etape etape;
    private int pointsDeplacement;
    private int barquesPlacees;
    private Class<? extends Monstre> especeLancee;

    /**
     * Crée une partie avec un hasard non reproductible.
     *
     * @param nomsJoueurs noms des joueurs (2 à 4)
     */
    public Partie(List<String> nomsJoueurs) {
        this(nomsJoueurs, new Random());
    }

    /**
     * Crée une partie en fournissant la source de hasard 
     * @param nomsJoueurs noms des joueurs (2 à 4)
     * @param aleatoire   génerateur de nombres aleatoires
     */
    public Partie(List<String> nomsJoueurs, Random aleatoire) {
        this.aleatoire = aleatoire;
        this.de = new De(aleatoire);
        this.joueurs = new ArrayList<>();

        CouleurJoueur[] couleurs = CouleurJoueur.values();
        for (int i = 0; i < nomsJoueurs.size(); i++) {
            Joueur joueur = new Joueur(nomsJoueurs.get(i), couleurs[i]);
            for (int tresor : TRESORS) {
                joueur.getExplorateurs().add(new Explorateur(couleurs[i], tresor));
            }
            joueurs.add(joueur);
        }

        for (int i = 0; i < NB_REQUINS; i++) {
            reserveRequins.add(new Requin());
        }
        for (int i = 0; i < NB_BALEINES; i++) {
            reserveBaleines.add(new Baleine());
        }
        int barquesEnReserve = NB_BARQUES - BARQUES_PAR_JOUEUR * joueurs.size();
        for (int i = 0; i < barquesEnReserve; i++) {
            reserveBarques.add(new Barque());
        }

        this.plateau = new Plateau();
        this.plateau.poserTuiles(Pioche.melangee(aleatoire));
        this.plateau.placerSerpentsInitiaux();

        this.indexCourant = 0;
        this.etape = Etape.PLACEMENT_EXPLORATEURS;
        journal.ajouter("Preparation : " + joueurCourant().getNom() + " place un explorateur.");
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public List<Joueur> getJoueurs() {
        return joueurs;
    }

    public Joueur joueurCourant() {
        return joueurs.get(indexCourant);
    }

    public Etape getEtape() {
        return etape;
    }

    public int getPointsDeplacement() {
        return pointsDeplacement;
    }

    public Journal getJournal() {
        return journal;
    }

    public int getReserveRequins() {
        return reserveRequins.size();
    }

    public int getReserveBaleines() {
        return reserveBaleines.size();
    }

    public int getReserveBarques() {
        return reserveBarques.size();
    }

    /** @return le nom de l'espece tiree au dernier lancer, ou {@code null}. */
    public String especeLanceeNom() {
        return especeLancee == null ? null : nomEspece(especeLancee);
    }

    /** Installe l'arbitre charge des demandes de defense (interface graphique). */
    public void setArbitre(ArbitreDefense arbitre) {
        this.arbitre = arbitre;
    }

    /**
     * Pose un explorateur du joueur courant sur une tuile déserte de l'ile.
     *
     * @param h case visee
     * @return vrai si le placement est valide
     */
    public boolean placerExplorateur(Hexagone h) {
        if (etape != Etape.PLACEMENT_EXPLORATEURS) {
            return false;
        }
        Cellule c = plateau.getCellule(h);
        if (c == null || !c.aTuile() || !c.getExplorateurs().isEmpty()) {
            return false;
        }
        Explorateur e = prochainExplorateurAPoser(joueurCourant());
        if (e == null) {
            return false;
        }
        e.setPosition(h);
        c.getExplorateurs().add(e);
        journal.ajouter(joueurCourant().getNom() + " place un explorateur en " + h + ".");

        indexCourant = (indexCourant + 1) % joueurs.size();
        if (tousExplorateursPoses()) {
            etape = Etape.PLACEMENT_BARQUES;
            indexCourant = 0;
            journal.ajouter("Explorateurs places. " + joueurCourant().getNom() + " place une barque.");
        }
        return true;
    }

    /**
     * Pose une barque du joueur courant sur une case de mer inoccupée voisine
     * d'une tuile.
     *
     * @param h case visée
     * @return vrai si le placement est valide
     */
    public boolean placerBarque(Hexagone h) {
        if (etape != Etape.PLACEMENT_BARQUES) {
            return false;
        }
        Cellule c = plateau.getCellule(h);
        if (c == null || !c.estMer() || !c.getBarques().isEmpty() || !voisineDuneTuile(c)) {
            return false;
        }
        Barque barque = new Barque();
        barque.setPosition(h);
        c.getBarques().add(barque);
        barquesPlacees++;
        journal.ajouter(joueurCourant().getNom() + " place une barque en " + h + ".");

        indexCourant = (indexCourant + 1) % joueurs.size();
        if (barquesPlacees == BARQUES_PAR_JOUEUR * joueurs.size()) {
            etape = Etape.DEBUT_TOUR;
            indexCourant = 0;
            pointsDeplacement = POINTS_PAR_TOUR;
            journal.ajouter("La partie commence ! Tour de " + joueurCourant().getNom() + ".");
        }
        return true;
    }

    private Explorateur prochainExplorateurAPoser(Joueur joueur) {
        for (Explorateur e : joueur.getExplorateurs()) {
            if (e.getPosition() == null && e.getStatut() == Statut.SUR_TERRE) {
                return e;
            }
        }
        return null;
    }

    private boolean tousExplorateursPoses() {
        for (Joueur joueur : joueurs) {
            for (Explorateur e : joueur.getExplorateurs()) {
                if (e.getPosition() == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean voisineDuneTuile(Cellule c) {
        for (Cellule voisine : plateau.voisines(c.getPosition())) {
            if (voisine.aTuile()) {
                return true;
            }
        }
        return false;
    }

    public void passerDebutTour() {
        if (etape == Etape.DEBUT_TOUR) {
            etape = Etape.DEPLACEMENT;
            pointsDeplacement = POINTS_PAR_TOUR;
            journal.ajouter(joueurCourant().getNom() + " deplace ses pions (" + pointsDeplacement + " points).");
        }
    }

    public void terminerDeplacement() {
        if (etape == Etape.DEPLACEMENT) {
            etape = Etape.RETRAIT_TUILE;
            journal.ajouter(joueurCourant().getNom() + " doit retirer une tuile.");
        }
    }

    public void passerCreature() {
        if (etape == Etape.DEPLACEMENT_CREATURE) {
            passerAuJoueurSuivant();
        }
    }

    private void passerAuJoueurSuivant() {
        especeLancee = null;
        for (Joueur joueur : joueurs) {
            for (Explorateur e : joueur.getExplorateurs()) {
                e.setDeplaceCeTour(false);
            }
        }
        indexCourant = (indexCourant + 1) % joueurs.size();
        etape = Etape.DEBUT_TOUR;
        journal.ajouter("--- Tour de " + joueurCourant().getNom() + " ---");

        if (terrainMinimalRetirable() == null) {
            journal.ajouter("Plus aucune tuile a retirer.");
            terminer();
        }
    }

    /**
     * Jouer une tuile « dauphin » pour deplacer un de ses nageurs de 1 à 3 cases.
     *
     * @param tuile  tuile dauphin de la main
     * @param nageur nageur a deplacer
     * @param dest   case de mer d'arrivee
     * @return vrai si l'action est valide
     */
    public boolean jouerDauphin(Tuile tuile, Explorateur nageur, Hexagone dest) {
        Joueur j = joueurCourant();
        if (etape != Etape.DEBUT_TOUR || !j.getMain().contains(tuile) || tuile.getPouvoir() != Pouvoir.DAUPHIN) {
            return false;
        }
        if (nageur.getStatut() != Statut.NAGEUR || nageur.getCouleur() != j.getCouleur()) {
            return false;
        }
        Cellule arrivee = plateau.getCellule(dest);
        if (arrivee == null || !arrivee.estMer()
                || !plateau.casesAccessibles(nageur.getPosition(), 3, Cellule::estMer).contains(dest)) {
            return false;
        }
        plateau.getCellule(nageur.getPosition()).getExplorateurs().remove(nageur);
        nageur.setPosition(dest);
        arrivee.getExplorateurs().add(nageur);
        j.getMain().remove(tuile);
        journal.ajouter(j.getNom() + " utilise un dauphin.");
        confronter(arrivee);
        return true;
    }

    /**
     * Joue une tuile « vent » pour deplacer une de ses barques de 1 à 3 cases.
     *
     * @param tuile  tuile vent de la main
     * @param barque barque a deplacer
     * @param dest   case de mer d'arrivee
     * @return vrai si l'action est valide
     */
    public boolean jouerVent(Tuile tuile, Barque barque, Hexagone dest) {
        Joueur j = joueurCourant();
        if (etape != Etape.DEBUT_TOUR || !j.getMain().contains(tuile) || tuile.getPouvoir() != Pouvoir.VENT) {
            return false;
        }
        if (!barque.estManoeuvrablePar(j.getCouleur())) {
            return false;
        }
        Cellule arrivee = plateau.getCellule(dest);
        if (arrivee == null || !arrivee.estMer()
                || !plateau.casesAccessibles(barque.getPosition(), 3, Cellule::estMer).contains(dest)) {
            return false;
        }
        plateau.getCellule(barque.getPosition()).getBarques().remove(barque);
        barque.setPosition(dest);
        arrivee.getBarques().add(barque);
        j.getMain().remove(tuile);
        journal.ajouter(j.getNom() + " profite du vent.");
        confronter(arrivee);
        return true;
    }

    /**
     * Jouer une tuile « appel » pour deplacer une creature déjà presente vers une
     * case de mer libre.
     *
     * @param tuile    tuile appel de la main
     * @param creature creature a deplacer
     * @param dest     case de mer libre d'arrivee
     * @return vrai si l'action est valide
     */
    public boolean jouerAppel(Tuile tuile, Monstre creature, Hexagone dest) {
        Joueur j = joueurCourant();
        if (etape != Etape.DEBUT_TOUR || !j.getMain().contains(tuile)) {
            return false;
        }
        Class<? extends Monstre> attendu = especePourAppel(tuile.getPouvoir());
        if (attendu == null || !attendu.isInstance(creature)) {
            return false;
        }
        Cellule arrivee = plateau.getCellule(dest);
        if (arrivee == null || !arrivee.estMer() || !caseLibrePourCreature(arrivee)) {
            return false;
        }
        plateau.getCellule(creature.getPosition()).getMonstres().remove(creature);
        creature.setPosition(dest);
        arrivee.getMonstres().add(creature);
        j.getMain().remove(tuile);
        journal.ajouter(j.getNom() + " deplace " + creature.nom().toLowerCase() + ".");
        return true;
    }

    /**
     * Calcule les cases ou l'explorateur donne peut se rendre ce tour.
     *
     * @param e explorateur concerne
     * @return l'ensemble des destinations valides
     */
    public Set<Hexagone> destinationsExplorateur(Explorateur e) {
        Set<Hexagone> destinations = new HashSet<>();
        if (etape != Etape.DEPLACEMENT || pointsDeplacement <= 0
                || e.getCouleur() != joueurCourant().getCouleur() || !e.estEnJeu()) {
            return destinations;
        }
        Hexagone ici = e.positionReelle();
        switch (e.getStatut()) {
            case SUR_TERRE:
                for (Cellule voisine : plateau.voisines(ici)) {
                    destinations.add(voisine.getPosition()); // tuile, mer ou refuge
                }
                break;
            case NAGEUR:
                Cellule celluleNageur = plateau.getCellule(ici);
                if (barqueAccueillante(celluleNageur) != null) {
                    destinations.add(ici);
                }
                for (Cellule voisine : plateau.voisines(ici)) {
                    if (voisine.estRefuge()) {
                        destinations.add(voisine.getPosition());
                    }
                }
                if (!e.aDeplaceCeTour()) {
                    for (Cellule voisine : plateau.voisines(ici)) {
                        if (voisine.estMer()) {
                            destinations.add(voisine.getPosition());
                        }
                    }
                }
                break;
            case EMBARQUE:

                for (Cellule voisine : plateau.voisines(ici)) {
                    if (voisine.estRefuge() || barqueAccueillante(voisine) != null) {
                        destinations.add(voisine.getPosition());
                    }
                }
                break;
            default:
                break;
        }
        return destinations;
    }

    /**
     * Déplace un explorateur vers la case indiquee.
     *
     * @param e    explorateur a deplacer
     * @param dest case d'arrivee
     * @return vrai si le deplacement a eu lieu
     */
    public boolean deplacerExplorateur(Explorateur e, Hexagone dest) {
        if (!destinationsExplorateur(e).contains(dest)) {
            return false;
        }
        Cellule depart = plateau.getCellule(e.positionReelle());
        Cellule arrivee = plateau.getCellule(dest);
        switch (e.getStatut()) {
            case SUR_TERRE:
                deplacerDepuisTerre(e, depart, arrivee);
                break;
            case NAGEUR:
                deplacerDepuisNage(e, depart, arrivee);
                break;
            case EMBARQUE:
                deplacerDepuisBarque(e, arrivee);
                break;
            default:
                return false;
        }
        pointsDeplacement--;
        return true;
    }

    private void deplacerDepuisTerre(Explorateur e, Cellule depart, Cellule arrivee) {
        depart.getExplorateurs().remove(e);
        if (arrivee.aTuile()) {
            e.setPosition(arrivee.getPosition());
            arrivee.getExplorateurs().add(e);
        } else if (arrivee.estRefuge()) {
            mettreEnSecurite(e, arrivee);
        } else {
            Barque barque = barqueAccueillante(arrivee);
            if (barque != null) {
                barque.embarquer(e);
                journal.ajouter(e.getCouleur() + " embarque en " + arrivee.getPosition() + ".");
            } else {
                tomberALeau(e, arrivee);
            }
        }
    }

    private void deplacerDepuisNage(Explorateur e, Cellule depart, Cellule arrivee) {
        if (arrivee.estRefuge()) {
            depart.getExplorateurs().remove(e);
            mettreEnSecurite(e, arrivee);
            return;
        }

        Barque barque = barqueAccueillante(arrivee);
        if (barque != null) {
            depart.getExplorateurs().remove(e);
            barque.embarquer(e);
            journal.ajouter(e.getCouleur() + " grimpe dans une barque.");
            return;
        }

        depart.getExplorateurs().remove(e);
        e.setPosition(arrivee.getPosition());
        arrivee.getExplorateurs().add(e);
        e.setDeplaceCeTour(true);
        confronter(arrivee);
    }

    private void deplacerDepuisBarque(Explorateur e, Cellule arrivee) {
        Barque barqueActuelle = e.getBarque();
        if (arrivee.estRefuge()) {
            barqueActuelle.debarquer(e);
            mettreEnSecurite(e, arrivee);
            return;
        }

        Barque cible = barqueAccueillante(arrivee);
        barqueActuelle.debarquer(e);
        cible.embarquer(e);
        journal.ajouter(e.getCouleur() + " change de barque.");
    }

    private void mettreEnSecurite(Explorateur e, Cellule refuge) {
        e.setStatut(Statut.SAUVE);
        e.setPosition(refuge.getPosition());
        refuge.getExplorateurs().add(e);
        journal.ajouter(e.getCouleur() + " atteint un refuge et est sauve !");
    }

    private void tomberALeau(Explorateur e, Cellule mer) {
        e.setStatut(Statut.NAGEUR);
        e.setPosition(mer.getPosition());
        e.setDeplaceCeTour(true);
        mer.getExplorateurs().add(e);
        journal.ajouter(e.getCouleur() + " se retrouve a la nage en " + mer.getPosition() + ".");
        confronter(mer);
    }

    /** Première barque de la case pouvant accueillir un passager, ou {@code null}. */
    private Barque barqueAccueillante(Cellule c) {
        for (Barque b : c.getBarques()) {
            if (!b.estPleine()) {
                return b;
            }
        }
        return null;
    }

    /**
     * Cases de mer voisines ou la barque peut aller.
     *
     * @param b barque concernee
     * @return l'ensemble des destinations valides
     */
    public Set<Hexagone> destinationsBarque(Barque b) {
        Set<Hexagone> destinations = new HashSet<>();
        if (etape != Etape.DEPLACEMENT || pointsDeplacement <= 0
                || !b.estManoeuvrablePar(joueurCourant().getCouleur())) {
            return destinations;
        }
        for (Cellule voisine : plateau.voisines(b.getPosition())) {
            if (voisine.estMer()) {
                destinations.add(voisine.getPosition());
            }
        }
        return destinations;
    }

    /**
     * Deplace une barque vers une case de mer voisine.
     *
     * @param b    barque a deplacer
     * @param dest case d'arrivee
     * @return vrai si le deplacement a eu lieu
     */
    public boolean deplacerBarque(Barque b, Hexagone dest) {
        if (!destinationsBarque(b).contains(dest)) {
            return false;
        }
        Cellule arrivee = plateau.getCellule(dest);
        plateau.getCellule(b.getPosition()).getBarques().remove(b);
        b.setPosition(dest);
        arrivee.getBarques().add(b);
        pointsDeplacement--;
        journal.ajouter(joueurCourant().getNom() + " deplace une barque vers " + dest + ".");
        confronter(arrivee);
        return true;
    }

    /** Applique l'effet des creatures presentes sur une case (sans defense possible). */
    private void confronter(Cellule c) {
        for (Monstre m : new ArrayList<>(c.getMonstres())) {
            m.attaquer(c);
        }
    }

    /**
     * @return les cases dont la tuile peut etre retiree ce tour
     */
    public Set<Hexagone> tuilesRetirables() {
        Set<Hexagone> retirables = new HashSet<>();
        Terrain minimum = terrainMinimalRetirable();
        if (minimum == null) {
            return retirables;
        }
        for (Hexagone h : plateau.getEmplacementsIle()) {
            Cellule c = plateau.getCellule(h);
            if (c.aTuile() && c.getTuile().getTerrain() == minimum && voisineDeLaMer(c)) {
                retirables.add(h);
            }
        }
        return retirables;
    }

    /**
     * Retire la tuile de la case indiquee, devoile son pouvoir et l'applique.
     *
     * @param h case dont on retire la tuile
     * @return la tuile retiree, ou {@code null} si l'action est invalide
     */
    public Tuile retirerTuile(Hexagone h) {
        if (etape != Etape.RETRAIT_TUILE || !tuilesRetirables().contains(h)) {
            return null;
        }
        Cellule c = plateau.getCellule(h);
        Tuile tuile = c.getTuile();
        tuile.reveler();
        c.setTuile(null);
        journal.ajouter(joueurCourant().getNom() + " retire une tuile " + tuile.getTerrain() + ".");

        for (Explorateur e : new ArrayList<>(c.getExplorateurs())) {
            if (e.getStatut() == Statut.SUR_TERRE) {
                e.setStatut(Statut.NAGEUR);
                e.setDeplaceCeTour(true);
                journal.ajouter(e.getCouleur() + " tombe a l'eau.");
            }
        }

        if (tuile.getPouvoir().getMoment() == Moment.IMMEDIAT) {
            journal.ajouter("Pouvoir revele : " + tuile.getPouvoir().getLibelle() + ".");
            appliquerPouvoirImmediat(tuile.getPouvoir(), h);
        } else {
            joueurCourant().getMain().add(tuile);
            journal.ajouter(joueurCourant().getNom() + " garde une tuile en main.");
        }

        if (etape != Etape.TERMINEE) {
            etape = Etape.LANCER_DE;
        }
        return tuile;
    }

    private Terrain terrainMinimalRetirable() {
        Terrain minimum = null;
        for (Hexagone h : plateau.getEmplacementsIle()) {
            Cellule c = plateau.getCellule(h);
            if (c.aTuile() && voisineDeLaMer(c)) {
                Terrain t = c.getTuile().getTerrain();
                if (minimum == null || t.ordinal() < minimum.ordinal()) {
                    minimum = t;
                }
            }
        }
        return minimum;
    }

    private boolean voisineDeLaMer(Cellule c) {
        for (Cellule voisine : plateau.voisines(c.getPosition())) {
            if (voisine.estMer()) {
                return true;
            }
        }
        return false;
    }

    private void appliquerPouvoirImmediat(Pouvoir pouvoir, Hexagone h) {
        Cellule c = plateau.getCellule(h);
        switch (pouvoir) {
            case APPARITION_REQUIN:
                Requin requin = reserveRequins.poll();
                if (requin != null) {
                    plateau.ajouterMonstre(requin, h);
                    requin.attaquer(c);
                }
                break;
            case APPARITION_BALEINE:
                Baleine baleine = reserveBaleines.poll();
                if (baleine != null) {
                    plateau.ajouterMonstre(baleine, h);
                }
                break;
            case APPARITION_BARQUE:
                faireApparaitreBarque(c);
                break;
            case TOURBILLON:
                declencherTourbillon(h);
                break;
            case ERUPTION:
                journal.ajouter("ERUPTION VOLCANIQUE ! La partie s'acheve.");
                terminer();
                break;
            default:
                break;
        }
    }

    private void faireApparaitreBarque(Cellule c) {
        Barque barque = reserveBarques.poll();
        if (barque == null) {
            return;
        }
        barque.setPosition(c.getPosition());
        c.getBarques().add(barque);
        for (Explorateur nageur : c.explorateursParStatut(Statut.NAGEUR)) {
            if (!barque.estPleine()) {
                c.getExplorateurs().remove(nageur);
                barque.embarquer(nageur);
            }
        }
    }

    private void declencherTourbillon(Hexagone centre) {
        List<Cellule> zone = new ArrayList<>();
        zone.add(plateau.getCellule(centre));
        zone.addAll(plateau.voisines(centre));
        for (Cellule c : zone) {
            if (!c.estMer()) {
                continue;
            }
            for (Explorateur nageur : c.explorateursParStatut(Statut.NAGEUR)) {
                c.getExplorateurs().remove(nageur);
                nageur.setStatut(Statut.PERDU);
                nageur.setPosition(null);
            }
            for (Monstre m : new ArrayList<>(c.getMonstres())) {
                c.getMonstres().remove(m);
                renvoyerEnReserve(m);
            }
            for (Barque b : new ArrayList<>(c.getBarques())) {
                for (Explorateur passager : new ArrayList<>(b.getPassagers())) {
                    b.debarquer(passager);
                    passager.setStatut(Statut.PERDU);
                    passager.setPosition(null);
                }
                c.getBarques().remove(b);
                reserveBarques.add(b);
            }
        }
        journal.ajouter("Un tourbillon engloutit la zone autour de " + centre + ".");
    }


    /**
     * Lance le de des creatures. Si aucune creature de l'espece tiree n'est
     * presente, le tour passe immediatement.
     *
     * @return le nom de l'espece tiree, ou {@code null} si l'action est invalide
     */
    public String lancerDe() {
        if (etape != Etape.LANCER_DE) {
            return null;
        }
        especeLancee = de.lancer();
        String nom = nomEspece(especeLancee);
        journal.ajouter(joueurCourant().getNom() + " lance le de : " + nom + ".");
        boolean presente = plateau.tousLesMonstres().stream().anyMatch(especeLancee::isInstance);
        if (!presente) {
            journal.ajouter("Aucune creature de ce type. Le tour passe.");
            passerAuJoueurSuivant();
        } else {
            etape = Etape.DEPLACEMENT_CREATURE;
        }
        return nom;
    }

    /**
     * @return les creatures de l'espece tiree pouvant etre deplacees
     */
    public List<Monstre> creaturesDeplacables() {
        List<Monstre> liste = new ArrayList<>();
        if (etape == Etape.DEPLACEMENT_CREATURE && especeLancee != null) {
            for (Monstre m : plateau.tousLesMonstres()) {
                if (especeLancee.isInstance(m)) {
                    liste.add(m);
                }
            }
        }
        return liste;
    }

    /**
     * @param creature creature consideree
     * @return les cases ou cette creature peut se rendre selon sa portee
     */
    public Set<Hexagone> destinationsCreature(Monstre creature) {
        if (etape != Etape.DEPLACEMENT_CREATURE || especeLancee == null || !especeLancee.isInstance(creature)) {
            return new HashSet<>();
        }
        return plateau.casesAccessibles(creature.getPosition(), creature.portee(), Cellule::estMer);
    }

    /**
     * Deplace la creature designee par le de, puis resout son attaque (la
     * defense est proposee aux joueurs menaces).
     *
     * @param creature creature a deplacer
     * @param dest     case de mer d'arrivee
     * @return vrai si le deplacement a eu lieu
     */
    public boolean deplacerCreature(Monstre creature, Hexagone dest) {
        if (!destinationsCreature(creature).contains(dest)) {
            return false;
        }
        Cellule arrivee = plateau.getCellule(dest);
        plateau.getCellule(creature.getPosition()).getMonstres().remove(creature);
        creature.setPosition(dest);
        arrivee.getMonstres().add(creature);
        journal.ajouter(creature.nom() + " se deplace vers " + dest + ".");

        resoudreAttaque(creature, arrivee);
        if (etape != Etape.TERMINEE) {
            passerAuJoueurSuivant();
        }
        return true;
    }

    private void resoudreAttaque(Monstre creature, Cellule cible) {
        Joueur agresseur = joueurCourant();
        if (creature instanceof Requin) {
            for (Explorateur nageur : cible.explorateursParStatut(Statut.NAGEUR)) {
                Joueur proprietaire = joueurDeCouleur(nageur.getCouleur());
                if (proprietaire != agresseur && possede(proprietaire, Pouvoir.PARADE_REQUIN)
                        && arbitre.defendreContreRequin(proprietaire, (Requin) creature)) {
                    jouerParade(proprietaire, Pouvoir.PARADE_REQUIN, creature);
                    return;
                }
            }
        } else if (creature instanceof Baleine) {
            for (Barque b : cible.getBarques()) {
                if (b.estVide()) {
                    continue;
                }
                CouleurJoueur controle = b.controleur();
                Joueur proprietaire = controle == null ? null : joueurDeCouleur(controle);
                if (proprietaire != null && proprietaire != agresseur
                        && possede(proprietaire, Pouvoir.PARADE_BALEINE)
                        && arbitre.defendreContreBaleine(proprietaire, (Baleine) creature)) {
                    jouerParade(proprietaire, Pouvoir.PARADE_BALEINE, creature);
                    return;
                }
            }
        }
        creature.attaquer(cible);
    }

    private void jouerParade(Joueur defenseur, Pouvoir parade, Monstre creature) {
        for (Tuile t : new ArrayList<>(defenseur.getMain())) {
            if (t.getPouvoir() == parade) {
                defenseur.getMain().remove(t);
                break;
            }
        }
        plateau.getCellule(creature.getPosition()).getMonstres().remove(creature);
        renvoyerEnReserve(creature);
        journal.ajouter(defenseur.getNom() + " repousse " + creature.nom().toLowerCase() + " !");
    }

    private void renvoyerEnReserve(Monstre creature) {
        creature.setPosition(null);
        if (creature instanceof Requin) {
            reserveRequins.add((Requin) creature);
        } else if (creature instanceof Baleine) {
            reserveBaleines.add((Baleine) creature);
        }
    }

    private void terminer() {
        etape = Etape.TERMINEE;
        for (Joueur joueur : joueurs) {
            for (Explorateur e : joueur.getExplorateurs()) {
                if (e.estEnJeu()) {
                    e.setStatut(Statut.PERDU);
                }
            }
        }
        journal.ajouter(" Fin de la partie");
        for (Joueur joueur : classement()) {
            journal.ajouter(joueur.getNom() + " : " + joueur.score()
                    + " points (" + joueur.nombreSauves() + " sauves).");
        }
        Joueur vainqueur = getVainqueur();
        if (vainqueur != null) {
            journal.ajouter("Vainqueur : " + vainqueur.getNom() + " !");
        }
    }

    /**
     * @return le classement des joueurs, du meilleur au moins bon
     */
    public List<Joueur> classement() {
        List<Joueur> tries = new ArrayList<>(joueurs);
        tries.sort(Comparator
                .comparingInt(Joueur::score)
                .thenComparingInt(Joueur::nombreSauves)
                .reversed());
        return tries;
    }

    /**
     * @return le vainqueur si la partie est terminee, sinon {@code null}
     */
    public Joueur getVainqueur() {
        if (etape != Etape.TERMINEE) {
            return null;
        }
        return classement().get(0);
    }

 
    private boolean caseLibrePourCreature(Cellule c) {
        return c.getMonstres().isEmpty() && c.getBarques().isEmpty() && c.getExplorateurs().isEmpty();
    }

    private Joueur joueurDeCouleur(CouleurJoueur couleur) {
        for (Joueur joueur : joueurs) {
            if (joueur.getCouleur() == couleur) {
                return joueur;
            }
        }
        return null;
    }

    private boolean possede(Joueur joueur, Pouvoir pouvoir) {
        if (joueur == null) {
            return false;
        }
        for (Tuile t : joueur.getMain()) {
            if (t.getPouvoir() == pouvoir) {
                return true;
            }
        }
        return false;
    }

    private Class<? extends Monstre> especePourAppel(Pouvoir pouvoir) {
        switch (pouvoir) {
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

    private String nomEspece(Class<? extends Monstre> espece) {
        if (espece == SerpentDeMer.class) {
            return "Serpent de mer";
        }
        if (espece == Requin.class) {
            return "Requin";
        }
        if (espece == Baleine.class) {
            return "Baleine";
        }
        return "?";
    }
}
