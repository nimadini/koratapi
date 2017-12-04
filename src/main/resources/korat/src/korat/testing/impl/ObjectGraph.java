package korat.testing.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;


public class ObjectGraph {

    final Map<Object, Class> visited = new IdentityHashMap<Object, Class>();
    final Queue<Object> toVisit = new ArrayDeque<Object>();

    final Visitor visitor;

    boolean excludeTransient = true;
    boolean excludeStatic = true;

    private final Set<Class> excludedClasses = new HashSet<Class>();

    public interface Visitor {

        boolean visit(Object object, Class clazz);
    }

    ObjectGraph(Visitor visitor) {
        this.visitor = visitor;
    }

    static public ObjectGraph visitor(Visitor visitor) {
        return new ObjectGraph(visitor);
    }


    public ObjectGraph includeTransient() {
        excludeTransient = false;
        return this;
    }


    public ObjectGraph excludeTransient() {
        excludeTransient = true;
        return this;
    }


    public ObjectGraph includeStatic() {
        excludeStatic = false;
        return this;
    }


    public ObjectGraph excludeStatic() {
        excludeStatic = true;
        return this;
    }


    public ObjectGraph excludeClasses(Class... classes) {
        for (Class c : classes) {
            if (c == null) {
                throw new NullPointerException("Null class not allowed");
            }
            excludedClasses.add(c);
        }
        return this;
    }


    public void traverse(Object root) {
        // Reset the state
        visited.clear();
        toVisit.clear();

        if (root == null)
            return;

        addIfNotVisited(root, root.getClass());
        start();
    }


    private boolean canDescend(Class clazz) {
        // We can't descend into Primitives (they are not objects)
        return !clazz.isPrimitive();
    }


    private boolean isExcludedClass(Class clazz) {
        for (Class c : excludedClasses) {
            if (c.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }


    private void addIfNotVisited(Object object, Class clazz) {
        if (object != null && !visited.containsKey(object)) {
            toVisit.add(object);
            visited.put(object, clazz);
        }
    }


    private List<Field> getAllFields(List<Field> fields, Class clazz) {
        // TODO consider caching the results of this.

        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        if (clazz.getSuperclass() != null) {
            getAllFields(fields, clazz.getSuperclass());
        }

        return Collections.unmodifiableList(fields);
    }

    private void start() {

        while (!toVisit.isEmpty()) {

            Object obj = toVisit.remove();
            Class clazz = visited.get(obj);

            boolean terminate = visitor.visit(obj, clazz);
            if (terminate)
                return;

            if (!canDescend(clazz))
                continue;

            if (clazz.isArray()) {
                // If an Array, add each element to follow up
                Class arrayType = clazz.getComponentType();

                final int len = Array.getLength(obj);

                for (int i = 0; i < len; i++) {
                    addIfNotVisited(Array.get(obj, i), arrayType);
                }

            } else {
                // If a normal class, add each field
                List<Field> fields = getAllFields(new ArrayList<Field>(), obj.getClass());
                for (Field field : fields) {
                    int modifiers = field.getModifiers();

                    if (excludeStatic && (modifiers & Modifier.STATIC) == Modifier.STATIC)
                        continue;

                    if (excludeTransient && (modifiers & Modifier.TRANSIENT) == Modifier.TRANSIENT)
                        continue;

                    Class fieldType = field.getType();

                    // If the field type is directly on the exclude list, then skip.
                    // Strictly this isn't needed as isExcludedClass is called later, but this is cheap
                    // and avoids getting the object, which could be expensive (think hibernate).
                    if (excludedClasses.contains(fieldType))
                        continue;

                    try {
                        field.setAccessible(true);
                        Object value = field.get(obj);

                        // If the object's type, or parent of the object's type is on the exclude list, then
                        // skip.
                        if (value != null && isExcludedClass(value.getClass()))
                            continue;

                        if(!fieldType.isPrimitive())
                            addIfNotVisited(value, fieldType);

                    } catch (IllegalAccessException e) {
                        // Ignore the exception
                    }
                }
            }
        }
    }
}