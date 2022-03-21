import interpreteur.executeur.Executeur;

public class Main {

    static final String[] CODE = """
            var a = 12
            afficher "hello world!"
            afficher a
            a = "salut"
            afficher a
            """.split("\n");


    public static void main(String[] args) {
        Executeur executeur = new Executeur();
        executeur.debug = true;
        Object a;
        if (!(a = executeur.compiler(CODE, true)).equals("[]")) System.out.println(a);
        // executeur.printCompileDict();
        System.out.println(executeur.executerMain(false));
    }
}
