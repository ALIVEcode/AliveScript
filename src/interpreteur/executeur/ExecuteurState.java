package interpreteur.executeur;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.ASScope;
import interpreteur.as.lang.managers.ASFonctionManager;
import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.ast.buildingBlocs.programmes.Declarer;
import interpreteur.data_manager.Data;
import interpreteur.data_manager.DataVoiture;
import utils.Pair;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Optional;
import java.util.Stack;

public class ExecuteurState {
    final private static Coordonnee debutCoord = new Coordonnee("<0>main");

    // data explaining the actions to do to the com.server
    private final ArrayList<Data> datas = new ArrayList<>();
    // data stack used when the program asks the site for information
    private final Stack<Object> dataResponse = new Stack<>();
    private final Hashtable<String, Hashtable<String, Programme>> coordCompileDict = new Hashtable<>();
    private final Coordonnee coordRunTime = new Coordonnee(debutCoord.toString());
    private Pair<Stack<ASScope>, Stack<ASScope.ScopeInstance>> scope = null;

    public ExecuteurState(Executeur executeur) {

    }

    //----------------- Saving -----------------//
    private void saveScope() {
        var newStack = new Stack<ASScope>();
        newStack.addAll(ASScope.getScopeStack());
        var newStackInstance = new Stack<ASScope.ScopeInstance>();
        newStackInstance.addAll(ASScope.getScopeInstanceStack());
        this.scope = new Pair<>(newStack, newStackInstance);
    }

    public void save() {
        saveScope();
    }

    public void load() {
        Optional.ofNullable(this.scope).ifPresentOrElse(ASScope::loadFromPair, () -> {
            throw new ASErreur.ErreurAliveScript("ErreurCompilation", "Le code n'est pas compil\u00E9");
        });

    }

    public void reset() {
        ASScope.resetAllScope();
        // créer le scope global
        ASScope.makeNewCurrentScope();

        // supprime les variables, fonctions et iterateurs de la memoire
        datas.clear();

        ASFonctionManager.reset();

        Declarer.reset();
        DataVoiture.reset();

        // remet la coordonnee d'execution au debut du programme
        coordRunTime.setCoord(debutCoord.toString());
    }
}
