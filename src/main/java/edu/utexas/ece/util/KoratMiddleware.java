package edu.utexas.ece.util;

import java.util.List;
import edu.utexas.ece.core.JavaClass;
import korat.src.korat.extension.Solution;
import korat.testing.impl.KoratTestException;
import korat.testing.impl.TestCradle;

/**
 * Created by Nima Dini on 5/6/17.
 */
public class KoratMiddleware {

    private static void init(String className, String finName, boolean printCandVects, boolean quiet) {
        TestCradle.buildInstance();
        TestCradle.getInstance().config.className = className;
        TestCradle.getInstance().config.finitization = finName;
        TestCradle.getInstance().config.printCandVects = printCandVects;
        TestCradle.getInstance().config.quiet = quiet;
    }

    public static long count(String className, String finName, String[] heapPC) throws KoratTestException {
        init(className, finName, false, true);

        return TestCradle.getInstance().count(className, new String[]{ "5" }, heapPC);
    }

    public static long explored(String className, String finName)
            throws KoratTestException, ClassNotFoundException {

        TestCradle.time = System.currentTimeMillis();

        init(className, finName, true, true);

        long exp = TestCradle.getInstance().explored(className, new String[]{ "5" });

        TestCradle.time = System.currentTimeMillis() - TestCradle.time;

        return exp;
    }

    public static List<Solution> korat(String className, String finName, boolean printCV)
            throws KoratTestException, ClassNotFoundException {

        init(className, finName, false, true);

        return TestCradle.getInstance().start(className, new String[]{ "5" }, null, null, null);
    }

    public static List<String> getCVElemNames(String className, String finName)
            throws KoratTestException, ClassNotFoundException {

        init(className, finName, false, true);

        return TestCradle.getInstance().getCVElemNames(className, new String[]{ "5" });
    }

    static String koratNext(String className, String finName, int[] startCV)
            throws KoratTestException, ClassNotFoundException {

        init(className, finName, false, true);
        return TestCradle.getInstance().startNextValidCandidate(className, new String[]{ "5" }, startCV, true);
    }

    public static Solution koratNextValidCandidate(JavaClass main, Solution sol) throws ClassNotFoundException,
            KoratTestException {

        int[] cv = null;

        if (sol != null) {
            cv = sol.getCV();
        }

        String result = koratNext(main.getName(), String.format("fin%s", main.getName()), cv);

        if (result != null) {
            return new Solution(result);
        }

        return null;
    }
}
