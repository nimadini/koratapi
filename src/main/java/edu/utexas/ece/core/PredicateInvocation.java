package edu.utexas.ece.core;

/**
 * Created by Nima Dini on 5/2/17.
 */
public class PredicateInvocation extends Formula {
    public PredicateInvocation(Expression expr) {
        this.lhs = expr;
        this.op = null;
        this.rhs = null;
    }

    public Op op() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return lhs.toString();
    }
}
