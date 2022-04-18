package interpreteur.ast.buildingBlocs.programmes;

import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.ast.buildingBlocs.expressions.Var;
import interpreteur.executeur.Executeur;

import javax.lang.model.type.NullType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Utiliser extends Programme {
    private final Var module;
    private final List<Var> sous_modules;
    private final String prefix;


    public Utiliser(Var module, Var[] sous_modules, Executeur executeurInstance, String prefix) {
        super(executeurInstance);
        this.module = module;
        this.sous_modules = sous_modules == null ? null : Arrays.asList(sous_modules);
        this.prefix = prefix;
        this.loadModule();
    }

    public Utiliser(Var module, Executeur executeurInstance, String prefix) {
        this(module, new Var[]{}, executeurInstance, prefix);
    }

    public Utiliser(Var module, Var[] sous_modules, Executeur executeurInstance) {
        this(module, sous_modules, executeurInstance, "");
    }

    public Utiliser(Var module, Executeur executeurInstance) {
        this(module, executeurInstance, module.getNom());
    }

    private void loadModule() {
        if (sous_modules.isEmpty()) {
            executeurInstance.getAsModuleManager().utiliserModuleAvecPrefix(module.getNom(), prefix);
        } else {
            executeurInstance.getAsModuleManager().utiliserModuleAvecPrefix(module.getNom(), sous_modules.stream().map(Var::getNom).toArray(String[]::new), prefix);
        }
    }

    @Override
    public NullType execute() {
        return null;
    }

    @Override
    public String toString() {
        return "Utiliser{" +
               "module=" + module +
               ", sous_modules?=" + sous_modules +
               '}';
    }
}
