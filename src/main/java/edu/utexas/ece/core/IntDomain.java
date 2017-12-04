package edu.utexas.ece.core;

/**
 * Created by Nima Dini on 4/29/17.
 */
public class IntDomain extends Domain {
    private int min;
    private int max;

    public IntDomain(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IntDomain)) {
            return false;
        }

        IntDomain id = (IntDomain) o;

        return this.min == id.min && this.max == id.max;
    }

    @Override
    public int hashCode() {
        return min + max;
    }

    @Override
    public String toString() {
        return String.format("\t\tIIntSet %s = f.createIntSet(%s, %s);\n", getJavaVarName(), min, max);
    }
}
