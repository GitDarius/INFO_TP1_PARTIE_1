package gestionnaireBibliotheque;

public class ListeEmprunts extends ListeGenerique<Emprunt> {
    //MÉTHODES PRIVÉES
    @Override
    protected int getId(Emprunt emprunt) {
        return emprunt.getId();
    }
}
