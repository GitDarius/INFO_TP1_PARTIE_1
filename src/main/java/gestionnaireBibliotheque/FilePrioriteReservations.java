package gestionnaireBibliotheque;

import java.util.PriorityQueue;

public class FilePrioriteReservations {
    //ATTRIBUTS D'INSTANCE
    private PriorityQueue<Reservation> filePriorite;

    //CONSTRUCTEURS
    public FilePrioriteReservations() {
        filePriorite = new PriorityQueue<Reservation>();
    }

    //MÉTHODES PUBLIQUES
    /** Ajoute une réservation dans la file de priorité
     * @return Retourne true si l'ajout a été effectué correctement
     */
    public boolean ajouter(Reservation reservation) {
        return filePriorite.offer(reservation);
    }

    /** Retire la réservation la plus prioritaire
     * @return Retourne la réservation retirée ou null si la liste est vide
     */
    public Reservation retirer() {
        return filePriorite.poll();
    }

    /** Retourne la réservation la plus prioritaire sans la retirer
     * @return La réservation la plus prioritaire ou null si la liste est vide.
     */
    public Reservation consulter() {
        return filePriorite.peek();
    }

    public boolean estVide() {
        return filePriorite.isEmpty();
    }

    public int taille() {
        return filePriorite.size();
    }

    /** Cherche une réservation pour un livre donné
     * @return La réservation pour le livre donné ou null si rien trouvé
     */
    public Reservation chercherPourLivre(int idLivre) {
        Reservation resPrioritaire = null;
        for (Reservation r : filePriorite) {
            if(r.getLivre().getId() == idLivre && (resPrioritaire == null || r.compareTo(resPrioritaire) < 0)) {
                resPrioritaire = r;
            }
        }
        return resPrioritaire;
    }

    /** Retire et retourne la réservation prioritaire associée à un livre donné
     * @return La réservation retirée ou null si aucune réservation n'existe pour ce livre
     */
    public Reservation retirerPourLivre(int idLivre) {
        Reservation resPrioritaire = chercherPourLivre(idLivre);
        if(resPrioritaire != null) {
            filePriorite.remove(resPrioritaire);
        }
        return resPrioritaire;
    }

    @Override
    public String toString() {
        return filePriorite.toString();
    }
}
