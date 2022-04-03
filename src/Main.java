import interpreteur.executeur.Executeur;
import language.Language;

public class Main {

    static final String[] CODE = """
            var a = 12
            afficher a
            afficher a
            fonction abc()
                afficher "hey!"
            fin fonction
            abc()
            
            const def = fonction(msg="abc")
            
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
