package gestionnaireBibliotheque;

public class Etudiant extends Utilisateur {
    //CONSTRUCTEURS
    public Etudiant(int id, String nom) {
        super(id, nom);
    }

    //MÉTHODES PUBLIQUES
    @Override
    public int nombreMaxEmprunts() { return 5; }
    @Override
    public double calculerPenalite(int joursRetard) { return joursRetard * 0.25; }
    @Override
    public TypeUtilisateur getTypeUtilisateur() { return TypeUtilisateur.ETUDIANT; }
}
