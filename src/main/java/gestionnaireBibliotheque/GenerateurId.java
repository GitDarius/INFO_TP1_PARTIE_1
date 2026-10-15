package gestionnaireBibliotheque;

public class GenerateurId {
    private int prochainId = 1;
    public int prochainID() {
        return prochainId++;
    }
}
