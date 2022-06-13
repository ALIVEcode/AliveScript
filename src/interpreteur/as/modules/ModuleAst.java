package interpreteur.as.modules;

import interpreteur.as.lang.datatype.fonction.ASFonctionModule;
import interpreteur.as.lang.datatype.ASObjet;
import interpreteur.as.lang.datatype.fonction.ASParametre;
import interpreteur.as.modules.core.ASModule;
import interpreteur.as.lang.ASTypeExpr;
import interpreteur.executeur.Executeur;

public class ModuleAst {
    static ASModule charger(Executeur executeurInstance) {
        return new ASModule(new ASFonctionModule[]{
            new ASFonctionModule("genererArbre", new ASParametre[] {
                    new ASParametre("param", new ASTypeExpr("texte"), null)
            }, new ASTypeExpr("texte")) {
                @Override
                public ASObjet<?> executer() {
                    //List<Token> a = Executeur.getLexer().lex((String) this.getValeurParam("param").getValue());
                    //return new Texte(Executeur.getAst().parse(a).toString());
                    return null;
                }
            }
        });
    }
}
