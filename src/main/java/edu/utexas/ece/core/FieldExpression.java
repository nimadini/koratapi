package edu.utexas.ece.core;

/**
 * Created by Nima Dini on 5/1/17.
 */
public class FieldExpression extends Expression {
    private Field field;

    FieldExpression(Field field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return field.toString();
    }
}
