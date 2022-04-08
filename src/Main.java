import interpreteur.executeur.Executeur;
import language.Language;

public class Main {

    static final String[] CODE = """
            afficher typeDe({})
            afficher typeDe([])
            
            afficher typeDe({1, 2, 3})
            afficher typeDe([1, 2, 3])
            
            afficher typeDe({"abs": 12})
            afficher typeDe(["abs": 12])
                        
                        
            """.split("\n");

    public static void main(String[] args) {
        Executeur executeur = new Executeur(Language.FR);
        executeur.debug = true;
        Object a;
        if (!(a = executeur.compiler(CODE, true)).equals("[]")) System.out.println(a);
        // executeur.printCompileDict();
        System.out.println(executeur.executerMain(false));
    }
}
