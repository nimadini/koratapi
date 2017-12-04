package edu.utexas.ece.core;

import edu.utexas.ece.exception.DuplicateDeclarationException;

/**
 * Created by Nima Dini on 4/28/17.
 */
public class UnaryField extends Field {
    private Type type;

    public UnaryField(String name, Type type) throws DuplicateDeclarationException {
        super(name);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() +
                this.type.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof UnaryField)) {
            return false;
        }

        UnaryField rhs = (UnaryField) o;

        return this.name.equals(rhs.name) &&
                this.type.equals(rhs.type);
    }
}
