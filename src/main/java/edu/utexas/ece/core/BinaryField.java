package edu.utexas.ece.core;

import edu.utexas.ece.exception.DuplicateDeclarationException;

/**
 * Created by Nima Dini on 4/28/17.
 */
public class BinaryField extends Field {
    private Type from;
    private Type to;
    private boolean isArray = false;

    public boolean isArray() {
        return isArray;
    }

    public BinaryField(String name, Type from, Type to) throws DuplicateDeclarationException {
        super(name);
        this.from = from;
        this.to = to;
    }

    public BinaryField(String name, Type from, Type to, boolean isArray) throws DuplicateDeclarationException {
        this(name, from, to);
        this.isArray = isArray;
    }

    public Type getFrom() {
        return from;
    }

    public Type getTo() {
        return to;
    }

    public String finAPICall(boolean isMain, String varName) {
        String fieldName = isMain ? name : from + "." + name;

        return String.format("\t\tf.set(\"%s\", %s);\n", fieldName, varName);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() +
                this.from.hashCode() +
                this.to.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof BinaryField)) {
            return false;
        }

        BinaryField rhs = (BinaryField) o;

        return this.name.equals(rhs.name) &&
                this.from.equals(rhs.from) &&
                this.to.equals(rhs.to);
    }
}
