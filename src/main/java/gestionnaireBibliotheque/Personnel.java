package gestionnaireBibliotheque;

public class Personnel extends Utilisateur{
    //CONSTRUCTEURS
    public Personnel(int id, String nom) {
        super(id, nom);
    }

    //MÉTHODES PUBLIQUES
    @Override
    public int nombreMaxEmprunts() { return 7; }
    @Override
    public double calculerPenalite(int joursRetard) { return joursRetard * 0.5; }
    @Override
    public TypeUtilisateur getTypeUtilisateur() { return TypeUtilisateur.PERSONNEL; }
}
