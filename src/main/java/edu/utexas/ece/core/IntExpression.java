package edu.utexas.ece.core;

/**
 * Created by Nima Dini on 5/1/17.
 */
public class IntExpression extends Expression {
    private int elem;

    public IntExpression(int elem) {
        this.elem = elem;
    }

    @Override
    public String toString() {
        return String.valueOf(elem);
    }
}
