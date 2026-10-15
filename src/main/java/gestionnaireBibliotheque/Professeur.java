package gestionnaireBibliotheque;

public class Professeur extends Utilisateur {
    //CONSTRUCTEURS
    public Professeur(int id, String nom) {
        super(id, nom);
    }

    //MÉTHODES PUBLIQUES
    @Override
    public int nombreMaxEmprunts() { return 10; }
    @Override
    public double calculerPenalite(int joursRetard) { return joursRetard * 0.75; }
    @Override
    public TypeUtilisateur getTypeUtilisateur() { return TypeUtilisateur.PROFESSEUR; }
}
