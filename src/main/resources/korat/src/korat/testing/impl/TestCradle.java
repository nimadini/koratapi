package korat.testing.impl;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import korat.config.ConfigManager;
import korat.finitization.IFinitization;
import korat.finitization.impl.Finitization;
import korat.finitization.impl.StateSpace;
import korat.loading.InstrumentingClassLoader;
import korat.src.korat.extension.Solution;
import korat.testing.IKoratSearchStrategy;
import korat.testing.ITester;
import korat.utils.IIntList;
import korat.utils.cv.CVFactory;
import korat.utils.cv.ICVFactory;
import korat.utils.cv.ICVFinder;

/**
 * Given the Finitization, conducts tests for all regular candidates in domain
 * state space
 * 
 * @author Sasa Misailovic <sasa.misailovic@gmail.com>
 * 
 */
public class TestCradle extends AbstractTestCaseGenerator implements ITester {

    private List<String> pathConditions = new ArrayList<String>();

    private List<HashMap<String, Integer>> cvIndexMapForFixCalls = new ArrayList<HashMap<String,Integer>>();

    private static TestCradle instance = new TestCradle();

    public static void buildInstance() {
        instance = new TestCradle();
    }

    public static TestCradle getInstance() {
        return instance;
    }

    private ICVFactory cvFactory;
    
    protected ClassLoader classLoader;
    
    protected TestCradle() {
        classLoader = new InstrumentingClassLoader();
        Finitization.setClassLoader(classLoader);
        cvFactory = CVFactory.getCVFactory();
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /*
     * -------------------------------------------------------------------------
     * Implementation of ITester interface.
     * -------------------------------------------------------------------------
     */

    private boolean traceStarted;

    public void startFieldTrace() {
        traceStarted = true;
        accessedFields.clear();
    }

    public void continueFieldTrace() {
        traceStarted = true;
    }

    public void stopFieldTrace() {
        traceStarted = false;
    }

    public void notifyFieldAccess(Object obj, String field) {
        if (!traceStarted)
            return;

        int accessedFieldIndex = stateSpace.getIndexInCandidateVector(obj,
                field);

        if (accessedFieldIndex != -1)
            accessedFields.add(accessedFieldIndex);
    }

    public void notifyFieldAccess(int accessedFieldIndex) {
        if (!traceStarted)
            return;

        if (accessedFieldIndex != -1)
            accessedFields.add(accessedFieldIndex);
    }

    /*
     * -------------------------------------------------------------------------
     * Info about korat execution that can be obtained after calling start
     * method.
     * -------------------------------------------------------------------------
     */

    protected long validCasesGenerated;

    protected long totalExplored;

    public long getValidCasesGenerated() {
        return validCasesGenerated;
    }

    public long getTotalExplored() {
        return totalExplored;
    }
    
    /*
     * -------------------------------------------------------------------------
     * Info that listeners may query in the middle of the search process.
     * -------------------------------------------------------------------------
     */
    
    public int[] getCandidateVector() {
        return stateSpaceExplorer.getCandidateVector().clone();
    }
    
    public boolean isPredicateOK() {
        return predicateOK;
    }
    
    public IIntList getAccessedFields() {
        return stateSpaceExplorer.getAccessedFields();
    }
    
    public StateSpace getStateSpace() {
        return stateSpace;
    }
    
    /*
     * -------------------------------------------------------------------------
     * Start method.
     * -------------------------------------------------------------------------
     */

    private boolean executeFixCalls = false;

    public List<String> getPathConditions(String className, String[] finArgs, int percentage) throws KoratTestException{
        executeFixCalls = false;
        startForPathConditions(className, finArgs, null, false, percentage);
        return pathConditions;
    }

    public List<HashMap<String, Integer>> getCvIndexMapForFixCalls(String className, String[] finArgs, int percentage) throws KoratTestException{
        executeFixCalls = true;
        startForPathConditions(className, finArgs, null, false, percentage);
        return cvIndexMapForFixCalls;
    }

    public long count(String className, String[] finArgs, String[] heapPC) throws KoratTestException {
        start(className, finArgs, null, null, heapPC);
        return validCasesGenerated;
    }

    public long explored(String className, String[] finArgs) throws KoratTestException {
        start(className, finArgs, null, null, null);
        return totalExplored;
    }

    public List<Solution> start(String className, String[] finArgs, int[] startCV, int[] endCV, String[] heapPC)
            throws KoratTestException {
        try {
        
            Class clazz = classLoader.loadClass(className);
            return start(clazz, finArgs, startCV, endCV, heapPC);
        
        } catch (ClassNotFoundException e) {
            throw new CannotFindClassUnderTest(className, e.getMessage(), e);
        }
    }

    public List<Solution> lone(String className, String[] finArgs,
               List<String> baseFieldNames, List<Solution> baseStarts, List<Solution> baseEnds)
            throws KoratTestException {

        try {

            Class clazz = classLoader.loadClass(className);
            String finName = config.finitization;
            Method finitize = getFinMethod(clazz, finName, finArgs);
            IFinitization fin = invokeFinMethod(clazz, finitize, finArgs);
            List<String> evolvedFieldNames = getCVElemNames(className, finArgs);

            return lone(fin, baseFieldNames, baseStarts, baseEnds, evolvedFieldNames);

        } catch (ClassNotFoundException e) {
            throw new CannotFindClassUnderTest(className, e.getMessage(), e);
        }
    }

    public String startNextValidCandidate(String className, String[] finArgs, int[] startCV, boolean nextValid)
            throws KoratTestException {

        try {

            Class clazz = classLoader.loadClass(className);
            return startNextValidCandidate(clazz, finArgs, startCV, nextValid);

        } catch (ClassNotFoundException e) {
            throw new CannotFindClassUnderTest(className, e.getMessage(), e);
        }
    }

    public String startForPathConditions(String className, String[] finArgs, int[] startCV, boolean nextValid, int percentage) throws KoratTestException {
        try {

            Class clazz = classLoader.loadClass(className);
            return startPathConditionSearch(clazz, finArgs, startCV, nextValid, percentage);

        } catch (ClassNotFoundException e) {
            throw new CannotFindClassUnderTest(className, e.getMessage(), e);
        }

    }

    /*
     * -------------------------------------------------------------------------
     * Internal stuff.
     * -------------------------------------------------------------------------
     */

    public ConfigManager config = ConfigManager.getInstance();
    
    private String finName = null;

    protected StateSpace stateSpace;

    protected IIntList accessedFields;

    protected IKoratSearchStrategy stateSpaceExplorer;

    protected boolean predicateOK;

    private List<Solution> start(Class clazz, String[] finArgs, int[] startCV, int[] endCV, String[] heapPC)
            throws KoratTestException {

        finName = config.finitization;
        Method finitize = getFinMethod(clazz, finName, finArgs);
        IFinitization fin = invokeFinMethod(clazz, finitize, finArgs);
        return startTestGeneration(fin, startCV, endCV, heapPC);
    }

    public List<Solution> findEnds(String className, String[] finArgs, int[] startCV) throws KoratTestException {
        try {

            Class clazz = classLoader.loadClass(className);
            return findEnds(clazz, finArgs, startCV);

        } catch (ClassNotFoundException e) {
            throw new CannotFindClassUnderTest(className, e.getMessage(), e);
        }
    }

    private List<Solution> findEnds(Class clazz, String[] finArgs, int[] startCV)
            throws KoratTestException {

        finName = config.finitization;
        Method finitize = getFinMethod(clazz, finName, finArgs);
        IFinitization fin = invokeFinMethod(clazz, finitize, finArgs);
        return startTestGenerationEnd(fin, startCV);
    }

    private String startNextValidCandidate(Class clazz, String[] finArgs, int[] startCV, boolean nextValid)
            throws KoratTestException {

        finName = config.finitization;
        Method finitize = getFinMethod(clazz, finName, finArgs);
        IFinitization fin = invokeFinMethod(clazz, finitize, finArgs);
        return nextValidCandidate(fin, startCV);
    }

    private String startPathConditionSearch(Class clazz, String[] finArgs, int[] startCV, boolean nextValid, int percentage)
            throws KoratTestException {

        //COMPAT1.4
        //finName = getFinName(clazz.getSimpleName());
        finName = config.finitization;
        Method finitize = getFinMethod(clazz, finName, finArgs);
        IFinitization fin = invokeFinMethod(clazz, finitize, finArgs);
        return startTestGenerationForPathConditions(fin, startCV, nextValid, percentage);
    }

    private Method getFinMethod(Class cls, String finName, String[] finArgs)
            throws CannotFindFinitizationException {

        Method finitize = null;
        for (Method m : cls.getDeclaredMethods()) {
            if (finName.equals(m.getName())
                    && m.getParameterTypes().length == finArgs.length) {
                finitize = m;
                break;
            }
        }
        if (finitize == null) {
            throw new CannotFindFinitizationException(cls, finName);
        }
        return finitize;
        
    }

    private IFinitization invokeFinMethod(Class cls, Method finitize,
            String[] finArgs) throws CannotInvokeFinitizationException {

        int paramNumber = finArgs.length;
        Class[] finArgTypes = finitize.getParameterTypes();
        Object[] finArgValues = new Object[paramNumber];
        
        for (int i = 0; i < paramNumber; i++) {
            Class clazz = finArgTypes[i];
            String arg = finArgs[i].trim();
            Object val;

            if (clazz == boolean.class || clazz == Boolean.class) {
                val = Boolean.parseBoolean(arg);
            } else if (clazz == byte.class || clazz == Byte.class) {
                val = Byte.parseByte(arg);
            } else if (clazz == double.class || clazz == Double.class) {
                val = Double.parseDouble(arg);
            } else if (clazz == float.class || clazz == Float.class) {
                val = Float.parseFloat(arg);
            } else if (clazz == int.class || clazz == Integer.class) {
                val = Integer.parseInt(arg);
            } else if (clazz == long.class || clazz == Long.class) {
                val = Long.parseLong(arg);
            } else if (clazz == short.class || clazz == Short.class) {
                val = Short.parseShort(arg);
            } else if (clazz == String.class) {
                val = arg;
            } else
                throw new CannotInvokeFinitizationException(cls, finitize.getName(),
                        "Only parameters of primitive classes are allowed");

            finArgValues[i] = val;
        }

        try {
            return (IFinitization) finitize.invoke(null, (Object[]) finArgValues);
        } catch (Exception e) {
            throw new CannotInvokeFinitizationException(cls, finitize.getName(), e);
        }
    }

    public static long time = 0;
    public static long validFound = -1;
    public static long explored = -1;

    public List<String> getCVElemNames(String className, String[] finArgs) throws KoratTestException {
        try {
            List<String> fieldNames = new ArrayList<String>();

            Class clazz = classLoader.loadClass(className);
            String finName = config.finitization;
            Method finitize = getFinMethod(clazz, finName, finArgs);
            IFinitization fin = invokeFinMethod(clazz, finitize, finArgs);

            stateSpaceExplorer = new StateSpaceExplorer(fin);
            stateSpace = ((Finitization)fin).getStateSpace();

            for (int idx = 0; idx < stateSpace.getTotalNumberOfFields(); idx += 1) {
                fieldNames.add(stateSpace.getCVElem(idx).getFieldName());
            }

            return fieldNames;

        } catch (ClassNotFoundException e) {
            throw new CannotFindClassUnderTest(className, e.getMessage(), e);
        }
    }

    protected String startTestGenerationForPathConditions(IFinitization fin, int[] startCV, boolean nextValid, int percentage)
            throws CannotInvokePredicateException, CannotFindPredicateException {

        time = System.currentTimeMillis();

        stateSpaceExplorer = new StateSpaceExplorer(fin);
        stateSpace = ((Finitization)fin).getStateSpace();
        // initStartAndEndCVs(stateSpaceExplorer);

        if (startCV != null) {
            stateSpaceExplorer.setStartCandidateVector(startCV);
        }

        totalExplored = 0;
        validCasesGenerated = 0;

        Object testCase = null;
        Class testCaseClass = fin.getFinClass();

        Method predicate = getPredicateMethod(testCaseClass, config.predicate);

        /* ---- Search Loop ---- */

        accessedFields = stateSpaceExplorer.getAccessedFields();

        if (config.printCandVects)
            System.out.println(stateSpace);

        if (nextValid && startCV != null) {
            testCase = stateSpaceExplorer.nextTestCase(null);
            if (testCase == null)
                return null;

            predicateOK = checkPredicate(testCase, predicate);
            printStatus(stateSpaceExplorer, predicateOK);
            notifyClients(testCase);
            stateSpaceExplorer.reportCurrentAsValid();
        }

        while (!interrupted) {

            testCase = stateSpaceExplorer.nextTestCase(null);
            if (testCase == null)
                break;

            totalExplored++;

            predicateOK = checkPredicate(testCase, predicate);

            if (predicateOK) {
                validCasesGenerated++;
                if(this.executeFixCalls){
                    PathConditionGenerator gn = new PathConditionGenerator(percentage, stateSpace.getStructureList());
                    gn.traverse2(testCase);
                    this.cvIndexMapForFixCalls.add(gn.getCvIndexMap());
                }
                PathConditionGenerator generator = new PathConditionGenerator(percentage);
                generator.traverse(testCase);
                generator.parsePathConditions();
                this.pathConditions.add(generator.getFinalPathCondition());
            }
            printStatus(stateSpaceExplorer, predicateOK);

            notifyClients(testCase);

            // invokes method, checks for correctness and notifies listeners
            if (predicateOK) {
                if (nextValid) {
                    String result = "";

                    for (int i : stateSpaceExplorer.getCandidateVector()) {
                        result = result + i + " ";
                    }

                    return result;
                }

                if (validCasesGenerated == config.maxStructs) {
                    interrupt();
                } else {
                    stateSpaceExplorer.reportCurrentAsValid();
                }
            }

        }

        if (dos != null) {
            try {
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        notifyTestFinished(totalExplored, validCasesGenerated);

        if (!config.quiet) {
            System.out.println("Total explored:" + totalExplored);
            System.out.println("New found:" + validCasesGenerated);
        }

        time = System.currentTimeMillis() - time;

        validFound = validCasesGenerated;
        return null;
    }

    protected Method initTestGeneration(IFinitization fin, int[] startCV, int[] endCV)
            throws CannotFindPredicateException {

        stateSpaceExplorer = new StateSpaceExplorer(fin);
        stateSpace = ((Finitization)fin).getStateSpace();

        if (startCV != null) {
            stateSpaceExplorer.setStartCandidateVector(startCV);
        }

        if (endCV != null) {
            stateSpaceExplorer.setEndCandidateVector(endCV);
        }

        totalExplored = 0;
        validCasesGenerated = 0;

        accessedFields = stateSpaceExplorer.getAccessedFields();

        Class testCaseClass = fin.getFinClass();

        if (config.printCandVects)
            System.out.println(stateSpace);

        return getPredicateMethod(testCaseClass, config.predicate);
    }

    protected void concludeTestGeneration() {
        if (dos != null) {
            try {
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        notifyTestFinished(totalExplored, validCasesGenerated);

        if (!config.quiet) {
            System.out.println("Total explored:" + totalExplored);
            System.out.println("New found:" + validCasesGenerated);
        }
    }

    protected String nextValidCandidate(IFinitization fin, int[] prevValidCV)
            throws CannotInvokePredicateException, CannotFindPredicateException {

        Method predicate = initTestGeneration(fin, prevValidCV, null);
        Object testCase = stateSpaceExplorer.nextTestCase(null);

//        if (testCase == null)
//            return null;

        predicateOK = checkPredicate(testCase, predicate);
        printStatus(stateSpaceExplorer, predicateOK);
        notifyClients(testCase);
        stateSpaceExplorer.reportCurrentAsValid();

        while (!interrupted) {
            testCase = stateSpaceExplorer.nextTestCase(null);
            if (testCase == null)
                break;

            predicateOK = checkPredicate(testCase, predicate);

            if (predicateOK) {
                validCasesGenerated++;
            }
            printStatus(stateSpaceExplorer, predicateOK);

            notifyClients(testCase);

            // invokes method, checks for correctness and notifies listeners
            if (predicateOK) {
                String result = "";

                for (int i : stateSpaceExplorer.getCandidateVector()) {
                    result = result + i + " ";
                }

                return result;
            }
        }

        concludeTestGeneration();

        return null;
    }

    private int[][] evolvedCVs(List<Solution> base,
                                    List<String> baseFieldNames, List<String> evolvedFieldNames) {

        int[][] evolvedCVs = new int[base.size()][];

        // For each base solution.
        for (int solIdx = 0; solIdx < base.size(); solIdx += 1) {
            evolvedCVs[solIdx] = new int[evolvedFieldNames.size()];

            int baseIdx = 0;
            int evolvedIdx = 0;

            while (evolvedIdx != evolvedFieldNames.size()) {
                // If no more base elements or current CVElem (field) value from evolved is not found in base.
                if (baseIdx == baseFieldNames.size() ||
                        !baseFieldNames.get(baseIdx).equals(evolvedFieldNames.get(evolvedIdx))) {

                    evolvedCVs[solIdx][evolvedIdx] = 0;
                    evolvedIdx += 1;

                } else {
                    evolvedCVs[solIdx][evolvedIdx] = base.get(solIdx).getCV()[baseIdx];

                    baseIdx += 1;
                    evolvedIdx += 1;
                }
            }
        }

        return evolvedCVs;
    }

    protected List<Solution> lone(IFinitization fin, List<String> baseFieldNames,
                                  List<Solution> baseStarts, List<Solution> baseEnds, List<String> evolvedFieldNames)
            throws CannotInvokePredicateException, CannotFindPredicateException {

            int[][] evolvedStartCVs = evolvedCVs(baseStarts, baseFieldNames, evolvedFieldNames);
            int[][] evolvedEndCVs = evolvedCVs(baseEnds, baseFieldNames, evolvedFieldNames);

            List<Solution> solutions = new ArrayList<Solution>();

            for (int i = 0; i < evolvedStartCVs.length; i += 1) {
                int[] startCV = evolvedStartCVs[i];
                // If the last explored candidate in a base subject is valid, there is no endCV for that mixed range.
                int[] endCV = i < evolvedEndCVs.length ? evolvedEndCVs[i] : null;

                solutions.addAll(startTestGeneration(fin, startCV, endCV, null));
            }

//            for (int[] newSol : evolvedStartCVs) {
//                for (int elem : newSol) {
//                    System.out.print(elem + " ");
//                }
//
//                System.out.println();
//            }

            return solutions;
    }

    // For evolved generation, we need the valid+1 as the endpoint while mapping the mixed ranges.
    protected List<Solution> startTestGenerationEnd(IFinitization fin, int[] startCV)
            throws CannotInvokePredicateException, CannotFindPredicateException {

        List<Solution> solutions = new ArrayList<Solution>();
        Method predicate = initTestGeneration(fin, startCV, null);

        boolean prevValid = false;

        Object testCase = null;

        while (!interrupted) {

            testCase = stateSpaceExplorer.nextTestCase(null);
            if (testCase == null)
                break;

            if (prevValid) {
                solutions.add(new Solution(getCandidateVector()));
                prevValid = false;
            }

            totalExplored++;

            predicateOK = checkPredicate(testCase, predicate);

            if (predicateOK) {
                prevValid = true;
                validCasesGenerated++;
            }
            printStatus(stateSpaceExplorer, predicateOK);

            notifyClients(testCase);

            if (predicateOK) {
                if (validCasesGenerated == config.maxStructs) {
                    interrupt();
                } else {
                    stateSpaceExplorer.reportCurrentAsValid();
                }
            }
        }

        concludeTestGeneration();

        explored = totalExplored;

        return solutions;
    }

    protected List<Solution> startTestGeneration(IFinitization fin, int[] startCV, int[] endCV, String[] heapPCs)
            throws CannotInvokePredicateException, CannotFindPredicateException {

        List<Solution> solutions = new ArrayList<Solution>();
        Method predicate = initTestGeneration(fin, startCV, endCV);

        Object testCase = null;

        while (!interrupted) {

            testCase = stateSpaceExplorer.nextTestCase(heapPCs);
            if (testCase == null)
                break;

            totalExplored++;

            predicateOK = checkPredicate(testCase, predicate);

            if (predicateOK) {
                solutions.add(new Solution(getCandidateVector()));
                validCasesGenerated++;
            }
            printStatus(stateSpaceExplorer, predicateOK);

            notifyClients(testCase);
            
            if (predicateOK) {
                if (validCasesGenerated == config.maxStructs) {
                    interrupt();
                } else {
                    stateSpaceExplorer.reportCurrentAsValid();
                }
            }
        }

        concludeTestGeneration();

        validFound = validCasesGenerated;
        explored = totalExplored;

        return solutions;
    }

    protected Method getPredicateMethod(Class<?> testClass, String predicateName)
            throws CannotFindPredicateException {
        try {
            return testClass.getMethod(predicateName, (Class[]) null);
        } catch (Exception e) {
            throw new CannotFindPredicateException(testClass, predicateName, e);
        }
    }

    protected boolean checkPredicate(Object testCase, Method predicate)
            throws CannotInvokePredicateException {
        startFieldTrace();
        try {
            return (Boolean) predicate.invoke(testCase, (Object[]) null);
        } catch (Exception e) {
            throw new CannotInvokePredicateException(testCase.getClass(),
                    predicate.getName(), e.getMessage(), e);
        } finally {
            stopFieldTrace();
        }
    }

    protected void initStartAndEndCVs(IKoratSearchStrategy ssExplorer) {
        long endCVNo = config.cvEnd;
        long startCVNo = config.cvStart;
        if (endCVNo == -1 && startCVNo == -1)
            return;
        
        ICVFinder cvFile = null;        
        try {
            
            cvFile = cvFactory.createCVFinder(config.cvFile);
            if (startCVNo != -1) {
                ssExplorer.setStartCandidateVector(cvFile.readCV(startCVNo));
            }
            if (endCVNo != -1) {
                ssExplorer.setEndCandidateVector(cvFile.readCV(endCVNo));
            }
                        
        } catch (Exception e) {
            throw new RuntimeException("Exception during accessing file with candidate vectors", e);
        } finally {
            try {
                if (cvFile != null)
                    cvFile.close();
            } catch (IOException e) {
            }
        }
    }
   
    private DataOutputStream dos = null;
    
    protected void printStatus(IKoratSearchStrategy sse, boolean predicateOK) {

        long progressThreshold = config.progress;
        if (progressThreshold > 0 && totalExplored % progressThreshold == 0) {
            
            ///////////////////////////////////////////////////
            if (dos == null) {
                try {
                    dos = new DataOutputStream(
                            new BufferedOutputStream(new FileOutputStream("acclist.dat")));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            
            IIntList accList = getAccessedFields();
            try {
                dos.writeLong(totalExplored - 1);
                int[] cv = sse.getCandidateVector();
                dos.writeInt(cv.length);
                for (int i = 0; i < cv.length; i++)
                    dos.writeInt(cv[i]);
                
                dos.writeInt(accList.numberOfElements());
                for (int i = 0; i < accList.numberOfElements(); i++) {
                    dos.writeInt(accList.get(i));
                }
                dos.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            /////////////////////////////////////////////////////
            
            System.out.println("Total explored:" + totalExplored);
            System.out.println("New found:" + validCasesGenerated);
            
            int[] cv = sse.getCandidateVector();
            for (int i = 0; i < cv.length; i++)
                System.out.print(cv[i] + " ");
            
            System.out.print(" :: ");
            for (int i = 0; i < accList.numberOfElements(); i++) {
                System.out.print(accList.get(i) + " ");
            }
            System.out.println("\n");
            
        } else if (config.printCandVects) {
            printCV(sse, predicateOK);
        }
    }

    
    private void printCV(IKoratSearchStrategy sse, boolean predicateOK) {
        int[] cv = sse.getCandidateVector();
        for (int i = 0; i < cv.length; i++)
            System.out.print(cv[i] + " ");
        System.out.print(" :: ");

        IIntList fieldAccesses = sse.getAccessedFields();
        int[] acA = fieldAccesses.toArray();
        for (int i = 0; i < acA.length; i++)
            System.out.print(acA[i] + " ");

        if (predicateOK) {
            System.out.println("***");
        } else {
            System.out.println();
        }
    }

}
