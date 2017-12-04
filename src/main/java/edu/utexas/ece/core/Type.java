package edu.utexas.ece.core;

/**
 * Created by Nima Dini on 5/1/17.
 */
public class Type {
    private String type;

    public Type(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Type)) {
            return false;
        }

        Type t = (Type) o;

        return this.type.equals(t.type);
    }

    @Override
    public String toString() {
        return type;
    }
}
