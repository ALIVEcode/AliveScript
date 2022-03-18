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

    public Utiliser(Var module, Var[] sous_modules, Executeur executeurInstance) {
        super(executeurInstance);
        this.module = module;
        this.sous_modules = Arrays.asList(sous_modules);
        this.loadModule();
    }

    public Utiliser(Var module, Executeur executeurInstance) {
        super(executeurInstance);
        this.module = module;
        this.sous_modules = new ArrayList<>();
        this.loadModule();
    }

    private void loadModule() {
        if (sous_modules.isEmpty()) {
            executeurInstance.getAsModuleManager().utiliserModule(module.getNom());
        } else {
            executeurInstance.getAsModuleManager().utiliserModule(module.getNom(), sous_modules.stream().map(Var::getNom).toArray(String[]::new));
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
