package edu.utexas.ece.core;

/**
 * Created by Nima Dini on 4/29/17.
 */
public class Domain {
    private static int uniqueIndex = -1;

    protected String javaVarName = null;

    protected static int genUniqueIndex() {
        uniqueIndex++;

        return uniqueIndex;
    }

    protected String getUniqueName() {
        return "e" + genUniqueIndex();
    }

    public String getJavaVarName() {
        if (javaVarName == null) {
            javaVarName = "e" + genUniqueIndex();
        }

        return javaVarName;
    }
}
