package gestionnaireBibliotheque;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class ListeGenerique<T> implements IListe<T>, Iterable<T> {
    //ATTRIBUTS D'INSTANCE
    private ArrayList<T> liste;

    //CONSTRUCTEURS
    public ListeGenerique() {
        liste = new ArrayList<>();
    }

    //MÉTHODES ABSTRAITES
    protected abstract int getId(T obj);

    //MÉTHODES PUBLIQUES
    @Override
    public boolean ajouter(T obj) {
        return liste.add(obj);
    }

    @Override
    public T rechercher(int id) {
        for (T obj : liste) { if (getId(obj) == id) return obj; }
        return null;
    }

    @Override
    public T supprimer(int id) {
        T objIdentifie = rechercher(id);
        if (objIdentifie != null) liste.remove(objIdentifie);
        return objIdentifie;
    }

    @Override
    public boolean contient(int id) {
        return rechercher(id) != null;
    }

    @Override
    public int taille() {
        return liste.size();
    }

    @Override
    public boolean estVide() {
        return liste.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return liste.iterator();
    }

    @Override
    public String toString() {
        return liste.toString();
    }
}
