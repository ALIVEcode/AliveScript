import interpreteur.executeur.Executeur;
import language.Language;

public class Main {

    static final String[] CODE = """
            
            function allo(x, y, z)
                afficher "Salut! " + (x + y + z)
            end function
            
            allo(1, 2, 3)
            """.split("\n");


    public static void main(String[] args) {
        Executeur executeur = new Executeur(Language.EN);
        executeur.debug = true;
        Object a;
        if (!(a = executeur.compiler(CODE, true)).equals("[]")) System.out.println(a);
            executeur.printCompileDict();
        System.out.println(executeur.executerMain(false));
    }
}
