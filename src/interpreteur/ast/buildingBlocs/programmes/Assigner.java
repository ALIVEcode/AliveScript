package interpreteur.ast.buildingBlocs.programmes;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.ASScope;
import interpreteur.as.lang.ASVariable;
import interpreteur.as.lang.datatype.ASHasAttr;
import interpreteur.as.lang.datatype.ASIterable;
import interpreteur.as.lang.datatype.ASListe;
import interpreteur.as.lang.datatype.ASObjet;
import interpreteur.ast.buildingBlocs.Expression;
import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.ast.buildingBlocs.expressions.*;

public class Assigner extends Programme {
    //private static final HashSet<CreerSetter> waitingSetters = new HashSet<>();
    //private static final HashSet<CreerGetter> waitingGetters = new HashSet<>();

    private final Expression<?> expr;
    private final Expression<?> valeur;
    private final BinOp.Operation op;
    private final Var var;

    public Assigner(Expression<?> expr, Expression<?> valeur, BinOp.Operation op) {
        // get la variable
        if (expr instanceof Var var) this.var = var;
        else if (expr instanceof GetAttr getAttr && getAttr.obj() instanceof Var var) this.var = var;
        else if (expr instanceof CreerListe.SousSection sousSection && sousSection.getExpr() instanceof Var var) {
            this.var = var;
        } else if (expr instanceof CreerListe.SousSection sousSection && sousSection.getExpr() instanceof GetAttr getAttr && getAttr.obj() instanceof Var var) {
            this.var = var;
        } else {
            throw new ASErreur.ErreurSyntaxe("Il est impossible d'assigner \u00E0 autre chose qu'une variable");
        }
        this.expr = expr;
        this.valeur = valeur;
        this.op = op;
        //addVariable();
    }

    @Override
    public Object execute() {
        //ASObjet.Variable variable = ASObjet.VariableManager.obtenirVariable(var.getNom());
        ASVariable variable = ASScope.getCurrentScopeInstance().getVariable(var.getNom());


        ASObjet<?> valeur = this.valeur.eval();
        if (variable == null) {
            throw new ASErreur.ErreurVariableInconnue("La variable " + var.getNom() + " n'a pas \u00E9t\u00E9 initialis\u00E9e." +
                    "\nAvez-vous oubli\u00E9 de mettre 'var' devant la d\u00E9claration de la variable?");
        }

        if (expr instanceof CreerListe.SousSection) {
            ASObjet<?> valeurVariable = getValeur(variable);
            if (!(valeurVariable instanceof ASListe listeInitial)) {
                throw new ASErreur.ErreurType("L'assignement d'index ou de sous-section n'est pas d\u00E9finie pour " +
                        "un \u00E9l\u00E9ment de type '" + valeurVariable.getNomType() + "'.");
            }

            // si l'assignement est de forme
            // var[debut:fin] = valeur
            if (expr instanceof CreerListe.SousSection.CreerSousSection sousSection) {
                if (!(valeur instanceof ASIterable<?>)) {
                    // TODO ERREUR peut pas assigner une sous liste à autre chose qu'à une liste
                    throw new ASErreur.ErreurAssignement("un interval de valeur doit \u00EAtre assign\u00E9 \u00E0 une liste");
                }
                int fin = sousSection.getFin();
                int debut = sousSection.getDebut();
                valeur = listeInitial.remplacerRange(debut, fin, (ASIterable<?>) valeur);
            }
            // si l'assignement est de forme
            // var[idxOrKey] = valeur
            else if (expr instanceof CreerListe.SousSection.IndexSection indexSection) {
                int idx = indexSection.getIdx();
                if (op != null) {
                    valeur = op.apply(expr, new ValeurConstante(valeur));
                }
                valeur = listeInitial.remplacer(idx, valeur);
                changerValeur(variable, valeur);
                return null;
            }
        }

        if (op != null && variable.pasInitialisee()) {
            throw new ASErreur.ErreurAssignement("La variable '" + var.getNom() + "' est utilis\u00E9e avant d'\u00EAtre d\u00E9clar\u00E9e");
        }

        if (op != null) {
            valeur = op.apply(new ValeurConstante(getValeur(variable)), new ValeurConstante(valeur));
        }

        changerValeur(variable, valeur);

        return null;
    }

    private void changerValeur(ASVariable variable, ASObjet<?> nouvelleValeur) {
        var expr = this.expr instanceof CreerListe.SousSection sousSection ? sousSection.getExpr() : this.expr;
        if (expr instanceof GetAttr getAttr && variable.getValeurApresGetter() instanceof ASHasAttr hasAttr) {
            hasAttr.setAttr(getAttr.attr().getNom(), nouvelleValeur);
        } else if (expr instanceof Var) {
            variable.changerValeur(nouvelleValeur);
        } else {
            throw new ASErreur.ErreurSyntaxe("Il est impossible d'assigner \u00E0 autre chose qu'une variable");
        }
    }

    private ASObjet<?> getValeur(ASVariable variable) {
        var expr = this.expr instanceof CreerListe.SousSection sousSection ? sousSection.getExpr() : this.expr;
        if (expr instanceof GetAttr getAttr && variable.getValeurApresGetter() instanceof ASHasAttr hasAttr) {
            return hasAttr.getAttr(getAttr.attr().getNom());
        } else if (expr instanceof Var) {
            return variable.getValeurApresGetter();
        } else {
            throw new ASErreur.ErreurSyntaxe("Il est impossible d'assigner \u00E0 autre chose qu'une variable");
        }
    }

    @Override
    public String toString() {
        return "Assigner{" +
                "expr=" + expr +
                ", valeur=" + valeur +
                '}';
    }

    /*
    public static void addWaitingGetter(CreerGetter getter) {
        waitingGetters.add(getter);
    }
    */

    /*
    public static void addWaitingSetter(CreerSetter setter) {
        waitingSetters.add(setter);
    }
    */

    /*
    private void addVariable() {

        // get l'objet variable s'il existe
        ASObjet.Variable varObj = ASObjet.VariableManager.obtenirVariable(var.getNom(), var.getScope());

        // si la variable existe déjà et que c'est une constante, lance une erreur, car on ne peut pas modifier une constante
        if (varObj instanceof ASObjet.Constante)
            throw new ASErreur.ErreurAssignement("Il est impossible de changer la valeur d'une constante");

        // si le mot "const" est présent dans l'assignement de la variable
        if (constante) {
            // si la variable existait déjà
            if (varObj != null) {
                throw new ASErreur.ErreurAssignement("Impossible de cr\u00E9er la constante, car la variable '" + var.getNom() + "' a d\u00E9j\u00E0 \u00E9t\u00E9 d\u00E9clar\u00E9e");
            }

            // sinon on crée la constante
            else {
                ASObjet.VariableManager.ajouterConstante(new ASObjet.Constante(var.getNom(), null), var.getScope());
            }

        } else {
            // si la variable n'existe pas et qu'elle n'est pas déclarée avec "const", on crée la variable
            if (varObj == null) {
                if (op != null) {
                    throw new ASErreur.ErreurAssignement("La variable '" + var.getNom() + "' n'a pas \u00E9t\u00E9 d\u00E9clar\u00E9e");
                }
                ASObjet.VariableManager.ajouterVariable(new ASObjet.Variable(var.getNom(), null, type == null ? new Type("tout") : type), var.getScope());
            } else if (type != null) {
                throw new ASErreur.ErreurAssignement("La variable " + var.getNom() + " a d\u00E9j\u00E0 \u00E9t\u00E9 d\u00E9clar\u00E9e avec un type");
            }
        }

        // si des setters et des getters attendaient la déclaration de la variable pour pouvoir être attachée à celle-ci, on les attache
        CreerGetter getter = waitingGetters.stream().filter(waitingGetter -> waitingGetter.getVar().equals(var)).findFirst().orElse(null);
        CreerSetter setter = waitingSetters.stream().filter(waitingSetter -> waitingSetter.getVar().equals(var)).findFirst().orElse(null);
        if (getter != null) {
            getter.addGetter();
            waitingGetters.remove(getter);
        }
        if (setter != null) {
            setter.addSetter();
            waitingSetters.remove(setter);
        }
    }
    */


}



















