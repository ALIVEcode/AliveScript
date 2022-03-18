package interpreteur.executeur;


import java.util.Objects;

public class Coordonnee {
    private String coord;
    private int numLigne;

    public Coordonnee(String coord) {
        this.coord = coord;
        this.numLigne = -1;
    }

    Coordonnee(String coord, int ligne) {
        this.coord = coord;
        this.numLigne = ligne;
    }

    public void setCoord(String coord) {
        this.coord = coord;
    }

    public void setNumLigne(int numLigne) {
        this.numLigne = numLigne;
    }

    /**
     * @return le scope dans lequel se situe la coordonnee
     */
    public String getScope() {
        return coord.substring(coord.lastIndexOf(">") + 1);
    }

    /**
     * @return le bloc le plus recent dans lequel se situe la coordonne
     */
    public String getBlocActuel() {
        if (coord.indexOf("<", 1) == -1) return getScope();
        else return coord.substring(coord.indexOf(">") + 1, coord.indexOf("<", 1));
    }

    /**
     * @return la boucle la plus recente dans lequel se situe la coordonne
     */
    public String getBoucleActuelle() {
        int lastIdx = coord.length();
        String boucleActuelle = null;
        for (Boucle boucle: Boucle.values()) {
            int idx = coord.indexOf(boucle.getNom());
            if (idx != -1 && idx < lastIdx) {
                lastIdx = idx;
                boucleActuelle = boucle.getNom();
            }
        }
        return boucleActuelle;
    }

    /**
     * @param nomNouveauBloc <li>nom du bloc qui va remplacer le bloc actuel</li>
     * @return la nouvelle coordonnee avec le bloc remplacer
     */
    public Coordonnee remplacerBlocActuel(String nomNouveauBloc) {
        finBloc();
        nouveauBloc(nomNouveauBloc);
        return this;
    }

    /**
     * recommence le bloc actuel
     *
     * @return la coordonnee avec le bloc recommencer
     */
    public Coordonnee recommencerLeBlocActuel() {
        finBloc();
        moinsUn();
        return this;
    }

    /**
     * recommence la boucle actuelle
     */
    public void recommencerBoucleActuelle() {
        while (!getBlocActuel().equals(getBoucleActuelle()) && getBoucleActuelle() != null) {
            finBloc();
        }
        recommencerLeBlocActuel();
    }

    /**
     * ajoute un bloc a la coordonnee
     *
     * @param nom <li>nom du nouveau bloc</li>
     * @return la coordonne avec le nouveau bloc
     */
    public Coordonnee nouveauBloc(String nom) {
        coord = "<0>" + nom + coord;
        return this;
    }

    /**
     * retire le bloc actuel de la coordonnee
     *
     * @return la coordonnee avec le bloc actuel en moins
     */
    public Coordonnee finBloc() {
        coord = coord.replaceFirst("<\\d+>\\w+", "");
        return this;
    }

    /**
     * ajoute un a la coordonnee (dans le bloc actuel)
     *
     * @return la nouvelle coordonnee
     */
    public Coordonnee plusUn() {
        String premierNum = coord.substring(coord.indexOf("<") + 1, coord.indexOf(">"));
        int nextNum = Integer.parseInt(premierNum) + 1;
        coord = "<" + nextNum + coord.substring(coord.indexOf(">"));
        return this;
    }

    /**
     * retire un a la coordonnee (dans le bloc actuel)
     *
     * @return la nouvelle coordonnee
     */
    public Coordonnee moinsUn() {
        String premierNum = coord.substring(coord.indexOf("<") + 1, coord.indexOf(">"));
        int nextNum = Integer.parseInt(premierNum) - 1;
        coord = "<" + nextNum + coord.substring(coord.indexOf(">"));
        return this;
    }

    @Override
    public String toString() {
        return coord;
    }

    public int getLigne() {
        return numLigne;
    }

    public Coordonnee copy() {
        return new Coordonnee(this.coord, this.numLigne);
    }

    public Coordonnee copy(int numLigne) {
        return new Coordonnee(this.coord, numLigne);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordonnee that)) return false;
        return numLigne == that.numLigne &&
                Objects.equals(coord, that.coord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coord, numLigne);
    }

    enum Boucle {
        POUR("pour"),
        TANT_QUE("tant_que"),
        FAIRE("faire"),
        REPETER("repeter");

        private final String nom;

        Boucle(String nom) {
            this.nom = nom;
        }

        public String getNom() {
            return nom;
        }
    }
}


























