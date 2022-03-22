import interpreteur.executeur.Executeur;

public class Main {

    static final String[] CODE = """
            fonction a()
                afficher "ça fonctionne a"
            fin fonction
            
            fonction b(p1) -> entier
                afficher "ça fonctionne b"
                afficher p1 si p1 < -8 sinon 44
            fin fonction
            
            a()
            b(23)
            a()
            
            afficher {1 + 1...10}
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
