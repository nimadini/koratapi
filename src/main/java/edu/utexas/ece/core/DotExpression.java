package edu.utexas.ece.core;

/**
 * Created by Nima Dini on 4/28/17.
 */
public class DotExpression extends Expression {
    private DotExpression lhs;
    private DotExpression rhs;

    protected DotExpression() {}

    public DotExpression(DotExpression lhs, DotExpression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public String toString() {
        return lhs.toString() + "." + rhs.toString();
    }
}
