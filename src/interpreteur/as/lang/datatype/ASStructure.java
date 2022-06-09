package interpreteur.as.lang.datatype;


import interpreteur.as.experimental.annotations.Experimental;
import interpreteur.as.experimental.annotations.ExperimentalStage;

@Experimental(stage = ExperimentalStage.PROTOTYPE)
public class ASStructure implements ASObjet<Object> {

    public ASStructure() {
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public boolean boolValue() {
        return false;
    }

    @Override
    public String obtenirNomType() {
        return null;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
