package edu.utexas.ece.core;

/**
 * Created by Nima Dini on 4/28/17.
 */
public abstract class Field extends DotExpression {
    protected String name;

    public Field(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private Formula buildFormula(Expression expr, Op op) {
        if (expr == null) {
            return new Formula(new FieldExpression(this), op, new NullExpression());
        }

        return new Formula(new FieldExpression(this), op, expr);
    }

    private Formula buildFormula(int elem, Op op) {
        return new Formula(new FieldExpression(this), op, new IntExpression(elem));
    }

    public Formula eq(Expression expr) {
        return buildFormula(expr, Op.EQ);
    }

    public Formula neq(Expression expr) {
        return buildFormula(expr, Op.NEQ);
    }

    public Formula eq(int elem) {
        return buildFormula(elem, Op.EQ);
    }

    public Formula neq(int elem) {
        return buildFormula(elem, Op.NEQ);
    }

    public Expression dot(DotExpression rhs) {
        return new DotExpression(this, rhs);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object o);
}
