package edu.utexas.ece;

import edu.utexas.ece.core.*;
import edu.utexas.ece.exception.DuplicateDeclarationException;
import edu.utexas.ece.exception.NoInputBoundsSpecifiedException;
import edu.utexas.ece.exception.UnspecifiedMainClassException;
import edu.utexas.ece.exception.VarDomainAlreadyDefinedException;
import korat.src.korat.extension.Solution;
import korat.testing.impl.KoratTestException;

import java.io.IOException;
import java.util.*;

import static edu.utexas.ece.core.Properties.getRepOKBody;

/**
 * Created by Nima Dini on 5/1/17.
 */
public class KoratAPI {
    private KoratSolver solver;

    public final Type INT = type("int");

    public static final boolean PRINT = true;

    private boolean guard = false;

    public KoratAPI() {
        solver = new KoratSolver();
    }

    public void repOK(Type type) {
        this.solver.setContainer(type);
    }

    public Type type(String type) {
        return new Type(type);
    }

    /* Surround repOK logic in a try-catch block */
    public void setGuard(boolean guard) {
        this.guard = guard;
    }

    public Field field(String name, Type type) throws DuplicateDeclarationException {
        Field field = new UnaryField(name, type);
        solver.addField(field);
        return field;
    }

    public Field field(String name, Type from, Type to) throws DuplicateDeclarationException {
        Field field = new BinaryField(name, from, to);
        solver.addField(field);
        return field;
    }

    public Field field(String name, Type from, Type to, boolean isArray) throws DuplicateDeclarationException {
        Field field = new BinaryField(name, from, to, true);
        solver.addField(field);
        return field;
    }

    public Domain objDomain(Type type, int numOfInstances, boolean nullIncluded) {
        Domain domain = new ObjDomain(type, numOfInstances, nullIncluded);
        solver.addDomain(domain);
        return domain;
    }

    public Domain arrayDomain(Type type, int size, int count) {
        Domain domain = new ArrayDomain(type, size, count);
        solver.addDomain(domain);
        return domain;
    }

    public Domain intDomain(int min, int max) {
        Domain domain = new IntDomain(min, max);
        solver.addDomain(domain);
        return domain;
    }

    public Predicate predicate(String invocation, Type type, String body) {
        Predicate predicate = new Predicate(invocation, type, body);
        solver.addPredicate(predicate);
        return predicate;
    }

    public Predicate predicate(String invocation, Type type, Formula formula) {
        return new Predicate(invocation, type, String.format("public boolean %s {return %s;}",
                invocation, formula.toString()));
    }

    public Formula predicateInvocation(Predicate predicate) {
        return new PredicateInvocation(new InvocationExpression(predicate.getInvocation()));
    }

    public void setDomain(Field field, Domain domain) throws VarDomainAlreadyDefinedException {
        solver.boundField(field, domain);
    }

    static int i = 0;

    private String getRepOK(Expression expr) {
        return String.format(getRepOKBody(guard), expr.toString());
    }

    public List<Solution> solve(Expression expr, String name) throws ClassNotFoundException, UnspecifiedMainClassException,
            NoInputBoundsSpecifiedException, IOException, KoratTestException {

        return solver.solve(String.format(getRepOKBody(guard), expr.toString()), name);
    }

    public List<Solution> solve(Expression expr) throws ClassNotFoundException, UnspecifiedMainClassException,
            NoInputBoundsSpecifiedException, IOException, KoratTestException {

        return solver.solve(String.format(getRepOKBody(guard), expr.toString()), null);
    }

    private List<Solution> intersection(List<Solution> globalSols, List<Solution> subSols) {
        if (globalSols == null) {
            return subSols;
        }

        List<Solution> intersection = new ArrayList<Solution>();

        for (Solution sol : globalSols) {
            if (subSols.contains(sol)) {
                intersection.add(sol);
            }
        }

        return intersection;
    }

    private List<String> getRepOKs(Expression expr) {
        return Arrays.asList(expr.toString().split(Op.SAND.get()));
    }

    public List<Solution> solveNew(Expression expr) throws ClassNotFoundException, UnspecifiedMainClassException,
            NoInputBoundsSpecifiedException, IOException, KoratTestException {

        List<String> repOKs = getRepOKs(expr);

        List<Solution> globalSols = null;

        for (String repOK : repOKs) {
            List<Solution> subSol = solver.solve(String.format(getRepOKBody(guard), repOK), solver.getContainerTypeName()+i);
            i += 1;
            globalSols = intersection(globalSols, subSol);
        }

        return globalSols;
    }

    public List<String> getFieldNames(Expression expr) throws ClassNotFoundException, UnspecifiedMainClassException,
            NoInputBoundsSpecifiedException, IOException, KoratTestException {

        return solver.getFieldNames(String.format(getRepOKBody(guard), expr.toString()), null);
    }

    public long explored(Expression expr, String name) throws ClassNotFoundException, UnspecifiedMainClassException,
            NoInputBoundsSpecifiedException, IOException, KoratTestException {

        return solver.explored(String.format(getRepOKBody(guard), expr.toString()), name);
    }

    public long count(Expression expr) throws ClassNotFoundException, UnspecifiedMainClassException,
            NoInputBoundsSpecifiedException, IOException, KoratTestException {

        return solver.count(String.format(getRepOKBody(guard), expr.toString()), null);
    }

    public long count(Expression expr, Formula enforce) throws ClassNotFoundException, UnspecifiedMainClassException,
            NoInputBoundsSpecifiedException, IOException, KoratTestException {

        String[] heapPC = enforce.toString().split(Op.AND.get());
        return solver.count(String.format(getRepOKBody(guard), expr.toString()), heapPC);
    }

    public List<Solution> solve(Predicate predicate) throws ClassNotFoundException, UnspecifiedMainClassException,
            NoInputBoundsSpecifiedException, IOException, KoratTestException {

        return solver.solve(predicate.toString(), null);
    }

    public long count(Predicate predicate) throws ClassNotFoundException, UnspecifiedMainClassException,
            NoInputBoundsSpecifiedException, IOException, KoratTestException {

        return solver.count(predicate.toString(), null);
    }

    public Iterator<Solution> iterator(Expression expr) throws ClassNotFoundException,
            UnspecifiedMainClassException, NoInputBoundsSpecifiedException, IOException {

        solver.buildClass(String.format(getRepOKBody(guard), expr.toString()), null, null);
        return solver.iterator();
    }

    public Iterator<Solution> iterator(Predicate predicate) throws ClassNotFoundException,
            UnspecifiedMainClassException, NoInputBoundsSpecifiedException, IOException {

        solver.buildClass(predicate.toString(), null, null);
        return solver.iterator();
    }
}
