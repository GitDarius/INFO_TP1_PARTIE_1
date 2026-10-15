package gestionnaireBibliotheque;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GestionnaireBibliotheque {
    //ATTRIBUTS D'INSTANCE
    private Map<Integer, Utilisateur> utilisateurs;
    private Map<Integer, Utilisateur> utilisateursAuthentifies;
    private ListeLivres livres;
    private FilePrioriteReservations reservationsEnAttente;

    public GestionnaireBibliotheque() {
        utilisateurs = new HashMap<>();
        utilisateursAuthentifies = new HashMap<>();
        livres = new ListeLivres();
        reservationsEnAttente = new FilePrioriteReservations();
    }

    //MÉTHODES PUBLIQUES
    public String traiterCommande(int idConnexion, TypeCommande typeCommande, String argument) {
        return switch (typeCommande) {
            case ID -> traiterID(idConnexion, argument);
            case BOOKS -> traiterBOOKS();
            case BORROW -> traiterBORROW(idConnexion, argument);
            case RETURN -> traiterRETURN(idConnexion, argument);
            case RESERVE -> traiterRESERVE(idConnexion, argument);
            case INFO -> argument == null ? traiterINFO(idConnexion) : traiterINFO(idConnexion, argument);
            case PENALTY -> traiterPENALTY(idConnexion, argument);
            case REGISTER -> traiterREGISTER(argument);
            case EXIT -> traiterEXIT(idConnexion);
        };
    }

    public String traiterID(int idConnexion, String argument) {
        /* ---------- VÉRIFICATIONS ---------- */
        //Variables
        int idUtilisateur = 0;
        Utilisateur utilisateur;

        //Argument invalide
        try{
            idUtilisateur = Integer.parseInt(argument);
        }catch(NumberFormatException e) {
            return String.valueOf(Erreur.BAD_ARGUMENT_ERROR);
        }
        //Utilisateur n'existe pas
        utilisateur = utilisateurs.get(idUtilisateur);
        if(utilisateur == null){
            return String.valueOf(Erreur.BAD_ARGUMENT_ERROR);
        }
        //Id de connexion existe
        if(utilisateursAuthentifies.containsKey(idConnexion)) {
            return String.valueOf(Erreur.ALREADY_AUTHENTIFIED_ERROR);
        }
        //Utilisateur déjà connecté
        for (Utilisateur u : utilisateursAuthentifies.values()) {
            if(u.getId() == idUtilisateur) {
                return String.valueOf(Erreur.TOO_MANY_CONNECTIONS_ERROR);
            }
        }
        /* ---------- TRAITER REQUÊTE ---------- */
        //Ajouter la connexion dans la liste de connexions
        utilisateursAuthentifies.put(idConnexion, utilisateur);
        return String.format("%s %d %s", Reponse.AUTHORIZED, idUtilisateur, utilisateur.getNom());
    }

    public String traiterBOOKS() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s %d", Reponse.BOOKS, livres.taille()));
        for(Livre l : livres) {
            sb.append(" ").append(l);
        }
        return sb.toString();
    }

    public String traiterBORROW(int idConnexion, String argument) {
        /* ---------- VÉRIFICATIONS ---------- */
        //Variables
        int idLivre;
        int jourEmprunt;
        Livre livre;
        Utilisateur utilisateur;
        //Utilisateur n'est pas connecté
        utilisateur = utilisateursAuthentifies.get(idConnexion);
        if (utilisateur == null) {
            return String.valueOf(Erreur.AUTHENTICATION_ERROR);
        }
        //Argument invalide
        try{
            String[] argumentsSplit = argument.split(" ");
            idLivre = Integer.parseInt(argumentsSplit[0]);
            jourEmprunt = Integer.parseInt(argumentsSplit[1]);
        }catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return String.valueOf(Erreur.BAD_ARGUMENT_ERROR);
        }
        //Le livre n'exista pas
        if(!livres.contient(idLivre)) {
            return String.valueOf(Erreur.BOOK_NOT_FOUND_ERROR);
        }
        //Le livre n'est pas disponible
        livre = livres.rechercher(idLivre);
        if(livre.getStatut() != StatutLivre.DISPONIBLE) {
            return String.valueOf(Erreur.BOOK_NOT_AVAILABLE_ERROR);
        }
        //Utilisateur ne peut plus emprunter
        if(!utilisateur.peutEmprunter()) {
            return String.valueOf(Erreur.BORROW_LIMIT_ERROR);
        }
        /* ---------- TRAITER REQUÊTE ---------- */
        Emprunt nouveauEmprunt = new Emprunt(livre, utilisateur.getId(), jourEmprunt);
        utilisateur.ajouterEmpruntEnCours(nouveauEmprunt);
        livre.setStatut(StatutLivre.EMPRUNTE);
        return String.format(
                "%s %d %d %d",
                Reponse.BORROW_OK,
                nouveauEmprunt.getId(),
                idLivre,
                nouveauEmprunt.getJourRetourPrevu()
        );
    }

    public String traiterRETURN(int idConnexion, String argument) {
        /* ---------- VÉRIFICATIONS ---------- */
        //Variables
        int idEmprunt;
        int jourRetour;
        Utilisateur utilisateur;
        //Utilisateur n'est pas connecté
        utilisateur = utilisateursAuthentifies.get(idConnexion);
        if (utilisateur == null) {
            return String.valueOf(Erreur.AUTHENTICATION_ERROR);
        }
        //Arguments invalides
        try{
            String[] argumentsSplit = argument.split(" ");
            idEmprunt = Integer.parseInt(argumentsSplit[0]);
            jourRetour = Integer.parseInt(argumentsSplit[1]);
        }catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return String.valueOf(Erreur.BAD_ARGUMENT_ERROR);
        }
        //L'utilisateur n'a pas de tel emprunt.
        if(utilisateur.rechercherEmpruntEnCours(idEmprunt) == null) {
            return String.valueOf(Erreur.BAD_BORROW_ERROR);
        }
        /* ---------- TRAITER REQUÊTE ---------- */
        //Supprimer emprunt de l'utilisateur, corriger le statut et mettre dans sa liste d'emprunts terminés
        Emprunt empruntSupprime = utilisateur.supprimerEmpruntEnCours(idEmprunt);
        empruntSupprime.retourner(jourRetour);
        utilisateur.ajouterEmpruntTermine(empruntSupprime);
        //Gérer reservations
        Livre livreEmprunte = empruntSupprime.getLivre();
        int idLivre = livreEmprunte.getId();
        Reservation reservationExistante = reservationsEnAttente.retirerPourLivre(idLivre);
        if(reservationExistante == null) {
            livreEmprunte.setStatut(StatutLivre.DISPONIBLE);
            return String.format("%s %d %d", Reponse.RETURN_OK, idEmprunt, idLivre);
        }
        reservationExistante.setStatut(StatutReservation.ATTRIBUEE);
        int idNouvelEmprunteur = reservationExistante.getIdUtilisateur();
        // QUESTION : Devrais-je utiliser la méthode traiterBORROW ?
        // Ce code ne met pas le statut du livre a emprunté
        // Et que se passe-t-il si l'utilisateur à une réservation, mais ne peut plus emprunter.
        Emprunt nouvelEmprunt = new Emprunt(livreEmprunte, idNouvelEmprunteur, jourRetour);
        utilisateurs.get(idNouvelEmprunteur).ajouterEmpruntEnCours(nouvelEmprunt);
        return String.format("%s %d %d", Reponse.RESERVATION_ASSIGNED, nouvelEmprunt.getId(), idLivre);
    }

    public String traiterRESERVE(int idConnexion, String argument) {
        /* ---------- VÉRIFICATIONS ---------- */
        //Variables
        int idLivre;
        Utilisateur utilisateur;
        Livre livreDemande;
        //Utilisateur n'est pas connecté
        utilisateur = utilisateursAuthentifies.get(idConnexion);
        if (utilisateur == null) {
            return String.valueOf(Erreur.AUTHENTICATION_ERROR);
        }
        //Argument invalide
        try{
            idLivre = Integer.parseInt(argument);
        }catch(NumberFormatException e) {
            return String.valueOf(Erreur.BAD_ARGUMENT_ERROR);
        }
        //Le livre demandé n'existe pas
        livreDemande = livres.rechercher(idLivre);
        if(livreDemande == null) {
            return String.valueOf(Erreur.BOOK_NOT_FOUND_ERROR);
        }
        //Le livre demandé est déjà disponible
        if(livreDemande.getStatut() == StatutLivre.DISPONIBLE) {
            return String.valueOf(Erreur.BOOK_ALREADY_AVAILABLE_ERROR);
        }
        //Si l'utilisateur n'a plus le droit de reserver
        if(!utilisateur.peutReserver()) {
            return String.valueOf(Erreur.RESERVATION_LIMIT_ERROR);
        }
        //Si l'utilisateur à déjà une réservation pour ce livre
        if(utilisateur.possedeReservationEnAttentePourLivre(idLivre)) {
            return String.valueOf(Erreur.BAD_ARGUMENT_ERROR);
        }
        /* ---------- TRAITER REQUÊTE ---------- */
        int idUtilisateur = utilisateur.getId();
        TypeUtilisateur typeUtilisateur = utilisateur.getTypeUtilisateur();
        Reservation nouvelleReservation = new Reservation(livreDemande, idUtilisateur, typeUtilisateur);
        reservationsEnAttente.ajouter(nouvelleReservation);
        utilisateur.ajouterReservation(nouvelleReservation);
        livreDemande.setStatut(StatutLivre.RESERVE);
        return String.format("%s %d %d", Reponse.RESERVATION_OK, nouvelleReservation.getId(), idLivre);
    }

    public String traiterINFO(int idConnexion){
        /* ---------- VÉRIFICATIONS ---------- */
        //Variables
        Utilisateur utilisateur;
        //Utilisateur n'est pas connecté
        utilisateur = utilisateursAuthentifies.get(idConnexion);
        if (utilisateur == null) {
            return String.valueOf(Erreur.AUTHENTICATION_ERROR);
        }
        /* ---------- TRAITER REQUÊTE ---------- */
        StringBuilder sb = new StringBuilder();
        ListeEmprunts empruntsEnCours = utilisateur.getEmpruntsEnCours();
        sb.append(String.format("%s %d", Reponse.INFO, empruntsEnCours.taille()));
        for(Emprunt e : empruntsEnCours) {
            sb.append(" ").append(e);
        }
        return sb.toString();
    }

    public String traiterINFO(int idConnexion, String argument) {
        /* ---------- VÉRIFICATIONS ---------- */
        //Variables
        Utilisateur utilisateur;
        Emprunt emprunt;
        int idEmprunt;
        //Utilisateur n'est pas connecté
        utilisateur = utilisateursAuthentifies.get(idConnexion);
        if (utilisateur == null) {
            return String.valueOf(Erreur.AUTHENTICATION_ERROR);
        }
        //Argument invalide
        try{
            idEmprunt = Integer.parseInt(argument);
        }catch(NumberFormatException e) {
            return String.valueOf(Erreur.BAD_ARGUMENT_ERROR);
        }
        //L'utilisateur n'a pas d'emprunt avec cet id
        emprunt = utilisateur.rechercherEmpruntEnCours(idEmprunt);
        if(emprunt == null) {
            return String.valueOf(Erreur.BAD_BORROW_ERROR);
        }
        /* ---------- TRAITER REQUÊTE ---------- */
        return String.format("%s %d %s", Reponse.INFO, 1, emprunt);
    }

    public String traiterPENALTY(int idConnexion, String argument) {
        /* ---------- VÉRIFICATIONS ---------- */
        //Variables
        int idEmprunt;
        int jourActuel;
        Utilisateur utilisateur;
        Emprunt emprunt;
        //Utilisateur n'est pas connecté
        utilisateur = utilisateursAuthentifies.get(idConnexion);
        if (utilisateur == null) {
            return String.valueOf(Erreur.AUTHENTICATION_ERROR);
        }
        //Mauvais argument
        try{
            String[] argumentsSplit = argument.split(" ");
            idEmprunt = Integer.parseInt(argumentsSplit[0]);
            jourActuel = Integer.parseInt(argumentsSplit[1]);
        }catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return String.valueOf(Erreur.BAD_ARGUMENT_ERROR);
        }
        //L'utilisateur n'a pas d'emprunt avec cet id
        Emprunt empruntEnCours = utilisateur.rechercherEmpruntEnCours(idEmprunt);
        emprunt = empruntEnCours == null ? utilisateur.rechercherEmpruntTermine(idEmprunt) : empruntEnCours;
        if(emprunt == null) {
            return String.valueOf(Erreur.BAD_BORROW_ERROR);
        }
        /* ---------- TRAITER REQUÊTE ---------- */
        int joursRetard = emprunt.calculerJoursRetard(jourActuel);
        double penalite = utilisateur.calculerPenalite(joursRetard);
        return String.format(Locale.US,"%s %.2f %d", Reponse.PENALTY, penalite, joursRetard);
    }

    public String traiterREGISTER(String argument) {
        /* ---------- VÉRIFICATIONS ---------- */
        //Variables
        int idUtilisateur;
        TypeUtilisateur typeUtilisateur;
        String nomUtilisateur;
        //Mauvais argument
        try{
            String[] argumentsSplit = argument.split(" ");
            idUtilisateur = Integer.parseInt(argumentsSplit[0]);
            typeUtilisateur = TypeUtilisateur.valueOf(argumentsSplit[1]);
            nomUtilisateur = argumentsSplit[2];
        }catch(ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            return String.valueOf(Erreur.BAD_ARGUMENT_ERROR);
        }
        //L'identifiant est déjà utilisé
        if(utilisateurs.containsKey(idUtilisateur)){
            return String.valueOf(Erreur.BAD_ARGUMENT_ERROR);
        }
        /* ---------- TRAITER REQUÊTE ---------- */
        Utilisateur nouvelUtilisateur = switch (typeUtilisateur){
            case ETUDIANT -> new Etudiant(idUtilisateur, nomUtilisateur);
            case PROFESSEUR -> new Professeur(idUtilisateur, nomUtilisateur);
            case PERSONNEL -> new Personnel(idUtilisateur, nomUtilisateur);
        };
        utilisateurs.put(idUtilisateur, nouvelUtilisateur);
        return String.format("%s %d %s", Reponse.REGISTERED, idUtilisateur, nomUtilisateur);
    }

    public String traiterEXIT(int idConnexion) {
        /* ---------- VÉRIFICATIONS ---------- */
        if (!utilisateursAuthentifies.containsKey(idConnexion)) {
            return String.valueOf(Erreur.AUTHENTICATION_ERROR);
        }
        /* ---------- TRAITER REQUÊTE ---------- */
        utilisateursAuthentifies.remove(idConnexion);
        return String.format("%s", Reponse.END);
    }
}
