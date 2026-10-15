package gestionnaireBibliotheque;

public interface IListe<T> {
    public boolean ajouter(T objet);
    public T supprimer(int id);
    public T rechercher(int id);
    public boolean contient(int id);
    public int taille();
    public boolean estVide();
}
