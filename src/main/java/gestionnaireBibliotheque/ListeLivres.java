package gestionnaireBibliotheque;

public class ListeLivres extends ListeGenerique<Livre> {
    //MÉTHODES PRIVÉES
    @Override
    protected int getId(Livre livre) {
        return livre.getId();
    }
}
