package gestionnaireBibliotheque;

import java.util.ArrayList;

public abstract class Utilisateur {
    //CONSTANTES
    public static final int MAX_RESERVATIONS = 10;

    //ATTRIBUTS D'INSTANCE
    private int id;
    private String nom;
    private ListeEmprunts empruntsEnCours;
    private ListeEmprunts empruntsTermines;
    private FilePrioriteReservations reservations;

    //CONSTRUCTEURS
    public Utilisateur(int id, String nom) {
        this.id = id;
        this.nom = nom;
        empruntsEnCours = new ListeEmprunts();
        empruntsTermines = new ListeEmprunts();
        reservations = new FilePrioriteReservations();
    }

    //MÉTHODES ABSTRAITES
    public abstract int nombreMaxEmprunts();
    public abstract double calculerPenalite(int joursRetard);
    public abstract TypeUtilisateur getTypeUtilisateur();

    //MÉTHODES PUBLIQUES
    public void ajouterEmpruntEnCours(Emprunt emprunt) {
        empruntsEnCours.ajouter(emprunt);
    }

    public void ajouterEmpruntTermine(Emprunt emprunt) {
        empruntsTermines.ajouter(emprunt);
    }

    public Emprunt supprimerEmpruntEnCours(int idEmprunt) {
        return empruntsEnCours.supprimer(idEmprunt);
    }

    public Emprunt rechercherEmpruntEnCours(int idEmprunt) {
        return empruntsEnCours.rechercher(idEmprunt);
    }

    public Emprunt rechercherEmpruntTermine(int idEmprunt) {
        return empruntsTermines.rechercher(idEmprunt);
    }

    public boolean aDesEmpruntsEnCours() {
        return empruntsEnCours.taille() > 0;
    }

    public boolean peutEmprunter() {
        return empruntsEnCours.taille() < nombreMaxEmprunts();
    }
    public boolean peutReserver() { return  reservations.taille() < MAX_RESERVATIONS; }

    public boolean possedeReservationEnAttentePourLivre(int idLivre) {
        return reservations.chercherPourLivre(idLivre) != null;
    }

    public boolean ajouterReservation(Reservation reservation) {
        if(!peutReserver()) return false;
        return reservations.ajouter(reservation);
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "type d'utilisateur=" + getTypeUtilisateur() +
                ", nom='" + nom + '\'' +
                ", id=" + id +
                ", nb emprunts en cours=" + empruntsEnCours.taille() +
                ", nb emprunts en terminés=" + empruntsTermines.taille() +
                '}';
    }

    //GETTER ET SETTER
    public int getId() { return id; }
    public String getNom() { return nom; }
    public ListeEmprunts getEmpruntsEnCours() { return empruntsEnCours; }
}
