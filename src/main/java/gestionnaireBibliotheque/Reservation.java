package gestionnaireBibliotheque;

import com.sun.tools.jconsole.JConsoleContext;

public class Reservation implements Comparable<Reservation>{
    //ATTRIBUTS STATIQUES
    private static final GenerateurId generateurId = new GenerateurId();
    private static final GenerateurId generateurOrdreReservation = new GenerateurId();

    //ATTRIBUTS D'INSTANCE
    private int id;
    private Livre livre;
    private int idUtilisateur;
    private TypeUtilisateur typeUtilisateur;
    private int ordreReservation;
    private StatutReservation statut;

    //CONSTRUCTEURS
    public Reservation(Livre livre, int idUtilisateur, TypeUtilisateur typeUtilisateur) {
        this.livre = livre;
        this.idUtilisateur = idUtilisateur;
        this.typeUtilisateur = typeUtilisateur;
        id = generateurId.prochainID();
        ordreReservation = generateurOrdreReservation.prochainID();
        statut = StatutReservation.EN_ATTENTE;
    }

    //MÉTHODES PUBLIQUES
    /** @return -1 si this est prioritaire, 1 si this est moins prioritaire. 0 si même priorité */
    @Override
    public int compareTo(Reservation r) {
        boolean memesTypes = typeUtilisateur == r.typeUtilisateur;
        int valeurThis = memesTypes ? ordreReservation : getValeurOrdre(this);
        int valeurAutre = memesTypes ? r.ordreReservation : getValeurOrdre(r);
        return Integer.compare(valeurThis, valeurAutre);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", livre=" + livre +
                ", idUtilisateur=" + idUtilisateur +
                ", typeUtilisateur=" + typeUtilisateur +
                ", ordreReservation=" + ordreReservation +
                ", statut=" + statut +
                '}';
    }

    //MÉTHODES PRIVÉES

    /** @return L'ordre de priorité de la réservation, selon le type de l'utilisateur. 0 étant le plus important*/
    private int getValeurOrdre(Reservation reservation) {
        return switch (reservation.typeUtilisateur) {
            case PROFESSEUR -> 0;
            case PERSONNEL -> 1;
            case ETUDIANT -> 2;
        };
    }

    //GETTER AND SETTER
    public int getId() { return id; }
    public Livre getLivre() {
        return livre;
    }
    public int getIdUtilisateur() { return idUtilisateur; }
    public void setStatut(StatutReservation statut) { this.statut = statut; }
}