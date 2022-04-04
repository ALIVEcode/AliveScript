import interpreteur.executeur.Executeur;
import language.Language;

public class Main {

    static final String[] CODE = """
            fonction o()
                afficher "o"
            fin fonction
            
            fonction abc()
                fonction o()
                    afficher "b"
                fin fonction
                o()
            fin fonction
            
            var _ao = 0
            var ao
            get ao
                retourner _ao
            fin get
            
            
            set ao(a)
                afficher a
                _ao = a
                retourner _ao
            fin set
            
            afficher ao
            ao += 1
            afficher ao
            abc()
            o()
            
            
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
