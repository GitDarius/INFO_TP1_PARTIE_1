package gestionnaireBibliotheque;

public class  Emprunt {
    //ATTRIBUTS STATIQUES
    private static final GenerateurId generateurId = new GenerateurId();

    //ATTRIBUTS D'INSTANCE
    private int id;
    private Livre livre;
    private int idUtilisateur;
    private int jourEmprunt;
    private int jourRetourPrevu;
    private int jourRetour;
    private StatutEmprunt statut = StatutEmprunt.EN_COURS;

    //CONSTRUCTEURS
    public Emprunt(Livre livreEmprunte, int idUtilisateur, int jourEmprunt) {
        this.idUtilisateur = idUtilisateur;
        this.jourEmprunt = jourEmprunt;
        livre = livreEmprunte;
        jourRetourPrevu = livreEmprunte.getDUREE_MAX_EMPRUNT();
        id = generateurId.prochainID();
    }

    //MÉTHODES PUBLIQUES

    /** @return True si le jour actuel dépasse le jour prévu du retour. False sinon */
    public boolean estEnRetard(int jourActuel) {
        return jourActuel > jourRetourPrevu;
    }

    /** @return Le nombre de jours de retard de l'emprunt. Si pas en retard, retourne zéro */
    public int calculerJoursRetard(int jourActuel) {
        return Math.max(0, jourActuel - jourRetourPrevu);
    }

    /** Met à jour le jour du retour et le statut (en_retard / retourné) */
    public void retourner(int jourRetour) {
        this.jourRetour = jourRetour;
        statut = calculerJoursRetard(jourRetour) > 0 ? StatutEmprunt.EN_RETARD : StatutEmprunt.RETOURNE;
    }

    @Override
    public String toString() {
        return String.format(
                "{%d %d %d %d %s}",
                id,
                livre.getId(),
                jourEmprunt,
                jourRetourPrevu,
                statut
        );
    }

    //GETTER AND SETTER
    public int getId() {
        return id;
    }
    public int getJourRetourPrevu() { return jourRetourPrevu; }
    public Livre getLivre() { return livre; }
}