package interpreteur.as;


import interpreteur.generateurs.lexer.LexerGenerator;
import interpreteur.generateurs.lexer.LexerLoader;

/**
 * TODO Les explications.
 * Les explications vont être rajouté quand j'aurai la motivation de les écrire XD
 *
 * @author Mathis Laroche
 */
public class ASLexer extends LexerGenerator {
    public ASLexer(String filePath) {
        super();
        LexerLoader loader = new LexerLoader(filePath);
        loader.load();
        sortRegle();
    }

    public ASLexer() {
        this(null);
    }
}
