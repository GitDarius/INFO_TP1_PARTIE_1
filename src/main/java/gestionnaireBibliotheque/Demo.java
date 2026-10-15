package gestionnaireBibliotheque;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Demo {
    public static void main(String[] args) {
        Livre livre = new Livre("Java", "Deitel", "Informatique");
        Livre livre1 = new Livre("Java", "Deitel", "Informatique");
        Livre livre2 = new Livre("Java", "Deitel", "Informatique");
        Livre livre3 = new Livre("Java", "Deitel", "Informatique");
        Emprunt e = new Emprunt(livre, 1, 10);
        Emprunt e1 = new Emprunt(livre1, 2, 10);
        Emprunt e2 = new Emprunt(livre2, 3, 10);
        Emprunt e3 = new Emprunt(livre3, 4, 10);

        GestionnaireBibliotheque gb = new GestionnaireBibliotheque();
        ArrayList<Livre> listeLivres = new ArrayList<>();
        listeLivres.add(livre);
        listeLivres.add(livre1);
        listeLivres.add(livre2);
        listeLivres.add(livre3);
        System.out.println(Arrays.toString("".split(" ")));
    }
}
