package edu.utexas.ece.core;

/**
 * Created by Nima Dini on 5/9/17.
 */
public class ArrayDomain extends Domain {
    private Type type;
    private int size;
    private int count;

    public ArrayDomain(Type type, int size, int count) {
        this.type = type;
        this.size = size;
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ArrayDomain)) {
            return false;
        }

        ArrayDomain ad = (ArrayDomain) o;

        return this.type.equals(ad.type) && this.size == ad.size && this.count == ad.count;
    }

    @Override
    public int hashCode() {
        return type.hashCode() + new Integer(size).hashCode() + new Integer(count).hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // TODO: Refactor isPrimaryType to take Type instead of String.
        if (Properties.isPrimaryType(type.toString())) {
            String lens = getUniqueName();
            String fdForEachArrayElem = getUniqueName();
            String elems = getJavaVarName();

            sb.append(String.format("\t\tIIntSet %s = f.createIntSet(%s, %s);\n", lens, size, size));
            sb.append(String.format("\t\tIIntSet %s = f.createIntSet(0, %s);\n", fdForEachArrayElem, size-1));
            sb.append(String.format("\t\tIArraySet %s = f.createArraySet(%s[].class, %s, %s, %s);\n", elems, type.toString(), lens, fdForEachArrayElem, count));
        }

        else {
            String bindingsCD = getUniqueName();
            String bindings = getUniqueName();
            String lens = getUniqueName();
            String elems = getJavaVarName();

            sb.append(String.format("\t\tIClassDomain %s = f.createClassDomain(%s.class, %s);\n", bindingsCD, type.toString(), size));
            sb.append(String.format("\t\tIObjSet %s = f.createObjSet(%s.class);\n", bindings, type.toString()));
            sb.append(String.format("\t\t%s.addClassDomain(%s);\n", bindings, bindingsCD));
            sb.append(String.format("\t\tIIntSet %s = f.createIntSet(0, %s);\n", lens, size));
            sb.append(String.format("\t\tIArraySet %s = f.createArraySet(%s[].class, %s, %s, %s);\n", elems, type.toString(), lens, bindings, count));
        }

        return sb.toString();
    }
}
