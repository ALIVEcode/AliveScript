package interpreteur.ast;

import java.util.Hashtable;
import java.util.List;
import java.util.function.Function;

import interpreteur.executeur.Coordonnee;
import interpreteur.tokens.Token;

/**
 * @author Mathis Laroche
 */
public abstract class Ast<T> implements Function<List<Object>, T>{
    private int importance;
    private final Hashtable<String, Ast<?>> sous_asts = new Hashtable<>();

    public Ast(){
        this.importance = -1;
    }
    
    public Ast(int importance) {
        this.importance = importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public Ast(Object[]... sous_asts) {
        this.importance = -1;
        for (Object[] sous_ast: sous_asts) {
        	assert sous_ast[0] instanceof String && sous_ast[1] instanceof Ast<?> && sous_ast.length == 2;
        	this.sous_asts.put((String) sous_ast[0], (Ast<?>) sous_ast[1]);
        }
    }
    
    public Ast(int importance, Object[]... sous_asts) {
        this.importance = importance;
        for (Object[] sous_ast: sous_asts) {
        	assert sous_ast[0] instanceof String && sous_ast[1] instanceof Ast<?> && sous_ast.length == 2;
        	this.sous_asts.put((String) sous_ast[0], (Ast<?>) sous_ast[1]);
        } 
    }
    
    public Hashtable<String, Ast<?>> getSousAst() {
    	return this.sous_asts;
    }

    public int getImportance() {
        return this.importance;
    }
    
	@Override
	public abstract T apply(List<Object> p);
}
