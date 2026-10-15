package gestionnaireBibliotheque;

public class Livre {
    //CONSTANTES
    private final int DUREE_MAX_EMPRUNT = 40;

    //ATTRIBUTS STATIQUES
    private static final GenerateurId generateurId = new GenerateurId();

    //ATTRIBUTS D'INSTANCE
    private int id;
    private String titre;
    private String auteur;
    private String categorie;
    // QUESTION : Ce statut est vraiment utile ? Un livre peut à la fois être emprunté et réservé
    // Et Perdu est utile ? S'il est perdu, on devrait l'enlever de la liste des livres
    // Si on garde le statut, il faudrait le prendre en considération lors des opérations.
    private StatutLivre statut;

    //CONSTRUCTEURS
    public Livre(String titre, String auteur, String categorie) {
        this.titre = titre;
        this.auteur = auteur;
        this.categorie = categorie;
        id = generateurId.prochainID();
        statut = StatutLivre.DISPONIBLE;
    }

    //MÉTHODES PUBLIQUES
    @Override
    public String toString() {
        return String.format(
                "{%d %s %s %s %s}",
                id,
                titre.replace(" ", ""),
                auteur.replace(" ", ""),
                categorie.replace(" ", ""),
                statut
        );
    }

    @Override
    public boolean equals(Object obj) {
        // S'ils ont la même réference
        if (this == obj) {
            return true;
        }
        // Si ce n'est pas un livre, retourner directement false
        if (!(obj instanceof Livre)) {
            return false;
        }

        // On transforme obj en Livre et on compare les "id"s
        Livre autre = (Livre) obj;
        return id == autre.id;
    }

    //GETTER AND SETTER
    public int getDUREE_MAX_EMPRUNT() {
        return DUREE_MAX_EMPRUNT;
    }
    public int getId() {
        return id;
    }
    public StatutLivre getStatut() { return statut; }
    public void setStatut(StatutLivre nouveauStatut) {statut = nouveauStatut;}
}
