package interpreteur.as.lang.datatype;

public interface ASHasAttr {

    ASObjet<?> getAttr(String attrName);
    void setAttr(String attrName, ASObjet<?> newValue);

}
