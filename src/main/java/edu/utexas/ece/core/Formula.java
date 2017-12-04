package edu.utexas.ece.core;

/**
 * Created by Nima Dini on 4/28/17.
 */
public class Formula extends Expression {
    protected Expression lhs;
    protected Op op;
    protected Expression rhs;

    private Expression promoteExprIfNull(Expression expr) {
        if (expr == null) {
            return new NullExpression();
        }

        return expr;
    }

    protected  Formula() {}

    public Formula(Expression lhs, Op op, Expression rhs) {
        this.lhs = promoteExprIfNull(lhs);
        this.op = op;
        this.rhs = promoteExprIfNull(rhs);
    }

    public Expression getLHS() {
        return lhs;
    }

    public Expression getRHS() {
        return rhs;
    }

    public void setLHS(Expression lhs) {
        this.lhs = lhs;
    }

    public void setRHS(Expression rhs) {
        this.rhs = rhs;
    }

    public Op op() {
        return this.op;
    }

    public Formula and(Formula rhs) {
        return new Formula(this, Op.AND, rhs);
    }

    @Override
    public String toString() {
        return lhs.toString() + op.get() + rhs.toString();
    }
}
