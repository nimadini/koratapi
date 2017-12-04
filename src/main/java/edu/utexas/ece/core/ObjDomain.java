package edu.utexas.ece.core;

/**
 * Created by Nima Dini on 4/29/17.
 */
public class ObjDomain extends Domain {
    private Type type;
    private int numOfInstances;
    private boolean nullIncluded = true;

    public ObjDomain(Type type, int numOfInstances, boolean nullIncluded) {
        this.type = type;
        this.numOfInstances = numOfInstances;
        this.nullIncluded = nullIncluded;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ObjDomain)) {
            return false;
        }

        ObjDomain od = (ObjDomain) o;

        return this.type.equals(od.type) &&
                this.numOfInstances == od.numOfInstances &&
                this.nullIncluded == od.nullIncluded;
    }

    @Override
    public int hashCode() {
        return type.hashCode() + numOfInstances + Boolean.valueOf(nullIncluded).hashCode();
    }

    @Override
    public String toString() {
        StringBuilder objDomain = new StringBuilder();
        objDomain.append(String.format("\t\tIObjSet %s = f.createObjSet(%s.class, %s);\n",
                getJavaVarName(), type, nullIncluded));

        objDomain.append(String.format("\t\t%s.addClassDomain(f.createClassDomain(%s.class, %s));\n",
                getJavaVarName(), type, numOfInstances));

        return objDomain.toString();
    }
}
