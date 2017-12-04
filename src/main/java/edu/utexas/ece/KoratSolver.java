package edu.utexas.ece;

import edu.utexas.ece.core.*;
import edu.utexas.ece.core.Properties;
import edu.utexas.ece.exception.DuplicateDeclarationException;
import edu.utexas.ece.exception.NoInputBoundsSpecifiedException;
import edu.utexas.ece.exception.UnspecifiedMainClassException;
import edu.utexas.ece.exception.VarDomainAlreadyDefinedException;
import edu.utexas.ece.util.CompileSourceInMemory;
import edu.utexas.ece.util.KoratMiddleware;
import korat.src.korat.extension.Solution;
import korat.testing.impl.KoratTestException;
import korat.testing.impl.TestCradle;

import java.io.IOException;
import java.util.*;

/**
 * Created by Nima Dini on 4/29/17.
 */
public class KoratSolver implements ISolver, Iterable<Solution> {
    private Type containerType = null;
    private Bounds bounds = null;
    private JavaClass main = null;

    private Set<Field> fields = new LinkedHashSet<Field>();
    private List<Domain> domains = new ArrayList<Domain>();

    private Set<Predicate> predicates = new HashSet<Predicate>();

    private Set<String> fixedCandidateElems = new HashSet<String>();

    public JavaClass getClass(List<JavaClass> classes, String name) {
        JavaClass cls = new JavaClass(name);
        if (classes.contains(cls)) {
            return classes.get(classes.indexOf(cls));
        }

        classes.add(cls);
        return cls;
    }

    protected void addField(Field field) throws DuplicateDeclarationException {
        if (fields.contains(field)) {
            throw new DuplicateDeclarationException();
        }

        fields.add(field);
    }

    public Set<Field> getFields() {
        return fields;
    }

    protected void addDomain(Domain domain) {
        if (!domains.contains(domain)) {
            domains.add(domain);
        }
    }

    protected void addPredicate(Predicate predicate) {
        if (!predicates.contains(predicate)) {
            predicates.add(predicate);
        }
    }

    public List<Domain> getDomains() {
        return domains;
    }

    public Domain getDomain(Domain domain) {
        return domains.get(domains.indexOf(domain));
    }

    public KoratSolver() {
        bounds = new Bounds();
    }

    public String getContainerTypeName() {
        return containerType.toString();
    }

    public void setContainer(Type cType) {
        this.containerType = cType;
    }

    public void boundField(Field field, Domain domain) throws VarDomainAlreadyDefinedException {
        bounds.boundVariable(field, getDomain(domain));
    }

    private String getFinitization(JavaClass main) {
        StringBuilder fin = new StringBuilder();
        fin.append(String.format("public static IFinitization fin%s(int _x_) {\n", main.getName()));
        fin.append(String.format("\t\tIFinitization f = FinitizationFactory.create(%s.class);\n\n", main.getName()));

        for (Domain domain : this.getDomains()) {
            fin.append(domain.toString());
        }

        fin.append("\n");

        for (Field field : fields) {
            if (!(field instanceof BinaryField)) {
                continue;
            }

            BinaryField binVar = (BinaryField) field;
            fin.append(binVar.finAPICall(main.getName().equals(binVar.getFrom().toString()),
                    bounds.getDomainForVar(binVar).getJavaVarName()));
        }

        for (String fix : fixedCandidateElems) {
            fin.append(fix);
            fin.append("\n");
        }

        fin.append("\n\t\treturn f;\n\t}\n");

        return fin.toString();
    }

    private static String targetDir = System.getProperty("user.dir") + "/target/classes";

    public void buildClass(String repOK, String name, String overrideName) throws UnspecifiedMainClassException,
            NoInputBoundsSpecifiedException, IOException, ClassNotFoundException {

        List<JavaClass> classes = new ArrayList<JavaClass>();
        Set<JavaClass> declared = new HashSet<JavaClass>();

        if (containerType == null) {
            throw new UnspecifiedMainClassException();
        }

        if (bounds.isEmpty()) {
            throw new NoInputBoundsSpecifiedException();
        }

        this.main = new JavaClass(false, containerType.toString());
        classes.add(main);

        declared.add(main);
        main.addMethod(repOK);
        main.addMethod(getFinitization(main));

        for (Predicate p : predicates) {
            main.addMethod(p.toString());
        }

        for (Field field : fields) {
            if (!(field instanceof BinaryField)) {
                continue;
            }

            BinaryField binVar = (BinaryField) field;

            JavaClass from = this.getClass(classes, binVar.getFrom().toString());
            JavaClass to  = this.getClass(classes, binVar.getTo().toString());

            if (!from.equals(to) &&
                    !declared.contains(to) && !Properties.isPrimaryType(binVar.getTo().toString())) {
                from.addInnerClass(to);
                declared.add(to);
            }

            String toTypeName = binVar.getTo().toString();

            if (binVar.isArray()) {
                toTypeName += "[]";
            }

            from.addField(String.format("public %s %s;", toTypeName, binVar.getName()));
        }

        String source = edu.utexas.ece.core.Properties.heading + "\n" + main;

        if (name == null) {
            name = main.getName();
        }

        else {
            source = source.replaceAll(main.getName(), name);
        }

        if (overrideName != null) {
            source = source.replaceAll(name, overrideName);
            name = overrideName;
        }

        CompileSourceInMemory.compile(source, name, targetDir);
    }

    @Override
    public List<Solution> solve(String repOK, String name) throws IOException, ClassNotFoundException,
            UnspecifiedMainClassException, NoInputBoundsSpecifiedException, KoratTestException {

        if (name == null) {
            name = containerType.toString();
        }

        buildClass(repOK, name, null);

        return KoratMiddleware.korat(name, String.format("fin%s", name), true);
    }

    public List<String> getFieldNames(String repOK, String name) throws IOException, ClassNotFoundException,
            UnspecifiedMainClassException, NoInputBoundsSpecifiedException, KoratTestException {

        if (name == null) {
            name = containerType.toString();
        }

        buildClass(repOK, name, null);

        return KoratMiddleware.getCVElemNames(name, String.format("fin%s", name));
    }

    @Override
    public long explored(String repOK, String name) throws IOException, ClassNotFoundException,
            UnspecifiedMainClassException, NoInputBoundsSpecifiedException, KoratTestException {

        if (name == null) {
            name = containerType.toString();
        }

        buildClass(repOK, name, null);

        return KoratMiddleware.explored(name, String.format("fin%s", name));
    }

    @Override
    public long count(String repOK, String[] heapPC) throws IOException, ClassNotFoundException,
            UnspecifiedMainClassException, NoInputBoundsSpecifiedException, KoratTestException {

        String name = containerType.toString();

        buildClass(repOK, name, null);

        long cnt = KoratMiddleware.count(main.getName(), String.format("fin%s", main.getName()), heapPC);

        if (KoratAPI.PRINT) {
            System.out.println("time:" + TestCradle.time);
            System.out.println("explored:" + TestCradle.explored);
        }

        return cnt;
    }

    @Override
    public Iterator<Solution> iterator() {
        return new Iterator<Solution>() {
            private Solution current = null;
            private Solution next = null;

            @Override
            public boolean hasNext() {
                if (next != null) {
                    return true;
                }

                try {
                    this.next = KoratMiddleware.koratNextValidCandidate(main, current);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return next != null;
            }

            @Override
            public Solution next() {
                Solution sol = this.next;

                if (next == null) {
                    try {
                        sol = KoratMiddleware.koratNextValidCandidate(main, current);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (sol == null) {
                    throw new NoSuchElementException();
                }

                this.current = sol;
                this.next = null;

                return sol;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
