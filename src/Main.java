import interpreteur.executeur.Executeur;
import language.Language;

public class Main {

    static final String[] CODE = """
            "experimental"
                        
            structure Personne
                var nom: texte
                var prenom: texte
                var age: entier
                var argent: decimal
            fin structure

            var personne = Personne { nom: "Dupont", prenom: "Jean", age: 25, argent: 100.0 }
            
            afficher personne

            """.split("\n");

    public static void main(String[] args) {
        Executeur executeur = new Executeur(Language.FR);
        executeur.debug = true;
        Object a;
        if (!(a = executeur.compiler(CODE, true)).equals("[]")) System.out.println(a);
        //executeur.printCompileDict();
        System.out.println(executeur.executerMain(false));
    }
}
