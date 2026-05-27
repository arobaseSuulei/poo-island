package ile.pions;

import ile.geometrie.Hexagone;

/**
 * Pion explorateur appartenant a un joueur.
 *
 * <p>Chaque explorateur transporte un tresor d'une valeur cachee de 1 a 6 ;
 * cette valeur n'est comptabilisee que si l'explorateur atteint un refuge.</p>
 */
public class Explorateur {

    private final CouleurJoueur couleur;
    private final int tresor;

    private Statut statut;
    private Hexagone position;     
    private Barque barque;        
    private boolean deplaceCeTour; 

    /**
     * Cree un explorateur
     * @param couleur couleur du joueur proprietaire
     * @param tresor  valeur du tresor transporte (1 a 6)
     */
    public Explorateur(CouleurJoueur couleur, int tresor) {
        this.couleur = couleur;
        this.tresor = tresor;
        this.statut = Statut.SUR_TERRE;
    }

    public CouleurJoueur getCouleur() {
        return couleur;
    }

    public int getTresor() {
        return tresor;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public Hexagone getPosition() {
        return position;
    }

    public void setPosition(Hexagone position) {
        this.position = position;
    }

    public Barque getBarque() {
        return barque;
    }

    public void setBarque(Barque barque) {
        this.barque = barque;
    }

    public boolean aDeplaceCeTour() {
        return deplaceCeTour;
    }

    public void setDeplaceCeTour(boolean valeur) {
        this.deplaceCeTour = valeur;
    }

    /**    
     * @return l'hexagone occupe, ou {@code null} si l'explorateur n'est pas pose
     */
    public Hexagone positionReelle() {
        if (statut == Statut.EMBARQUE && barque != null) {
            return barque.getPosition();
        }
        return position;
    }

    /**
     * @return vrai si l'explorateur peut encore agir (ni sauve, ni perdu)
     */
    public boolean estEnJeu() {
        return statut != Statut.SAUVE && statut != Statut.PERDU;
    }
}
