package edu.utexas.ece.core;

/**
 * Created by Nima Dini on 5/2/17.
 */
public class InvocationExpression extends Expression {
    private String invokExpr;

    public InvocationExpression(String inv) {
        this.invokExpr = inv;
    }

    @Override
    public String toString() {
        return invokExpr;
    }
}
