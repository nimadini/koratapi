package edu.utexas.ece.core;

/**
 * Created by Nima Dini on 4/29/17.
 */
public class Properties {
    public final static String heading =
            "import java.util.*;\n" +
            "import korat.finitization.*;\n" +
            "import korat.finitization.impl.*;";

    public static String getRepOKBody(boolean guard) {
        if (guard) {
            return  "public boolean repOK() {\n" +
                    "\t\tboolean result = false;\n" +
                    "\t\ttry {\n" +
                    "\t\t\tresult = %s;\n" +
                    "\t\t} catch (Exception e) {}\n" +
                    "\n\t" +
                    "\treturn result;\n" +
                    "\t}";
        } else {
            return  "public boolean repOK() {\n" +
                    "\treturn %s;\n" +
                    "\t}";
        }
    }

    public static boolean isPrimaryType(String type) {
        return type.equals("int") || type.equals("Integer");
    }
}
