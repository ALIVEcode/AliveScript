package interpreteur.tokens;


import interpreteur.generateurs.lexer.Regle;

/**
 * Les explications vont être rajouté quand j'aurai la motivation de les écrire XD
 * @author Mathis Laroche
 *
 *
 */

public class Token {

    private final String nom, valeur, categorie;
    private final int debut;
    private final Regle regleParent;
    
    public Token(String nom, String valeur, String categorie, int debut, Regle regleParent){
        this.nom = nom; 
        this.valeur = valeur;
        this.categorie = categorie;
        this.debut = debut;
        this.regleParent = regleParent;
    }

    public Token(String nom, String valeur, String categorie, int debut){
        this.nom = nom;
        this.valeur = valeur;
        this.categorie = categorie;
        this.debut = debut;
        this.regleParent = null;
    }
    
    public String obtenirCategorie(){
        return this.categorie;
    }

    public String obtenirNom(){
        return this.nom;
    }

    public String obtenirValeur(){
        return this.valeur;
    }
    
    public int obtenirDebut() {
    	return this.debut;
    }

    public Regle getRegleParent() {
        return regleParent;
    }

    @Override
	public String toString() {
		return "('" + this.obtenirNom() + "' '" + this.obtenirValeur() + "')";
	}
}
















