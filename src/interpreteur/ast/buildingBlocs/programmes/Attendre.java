package interpreteur.ast.buildingBlocs.programmes;

import interpreteur.ast.buildingBlocs.Expression;
import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.data_manager.Data;

@Deprecated
public class Attendre extends Programme {
    private final Expression<?> delais;
    public Attendre(Expression<?> delais) {
        this.delais = delais;
    }

    @Override
    public Data execute() {
        return new Data(Data.Id.ATTENDRE).addParam(((Number) delais.eval().getValue()).doubleValue());
    }

    @Override
    public String toString() {
        return "Attendre{" +
                "delais=" + delais +
                '}';
    }
}
