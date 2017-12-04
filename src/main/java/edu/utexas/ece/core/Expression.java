package edu.utexas.ece.core;

/**
 * Created by Nima Dini on 4/28/17.
 */
public abstract class Expression {
    public Formula neq(Expression e) {
        return new Formula(this, Op.NEQ, e);
    }

    public Formula eq(Expression e) {
        return new Formula(this, Op.EQ, e);
    }

    public Formula eq(int val) {
        return new Formula(this, Op.EQ, new IntExpression(val));
    }

    public Formula dot(Expression e) {
        return new Formula(this, Op.DOT, e);
    }

    public Formula lt(Expression e) {
        return new Formula(this, Op.LT, e);
    }

    public Formula lte(Expression e) {
        return new Formula(this, Op.LTE, e);
    }

    public Formula gt(Expression e) {
        return new Formula(this, Op.GT, e);
    }

    public Formula gte(Expression e) {
        return new Formula(this, Op.GTE, e);
    }

    public Formula dot(Field field) {
        return new Formula(this, Op.DOT, field);
    }

    public Formula lt(Field field) {
        return new Formula(this, Op.LT, field);
    }

    public Formula lte(Field field) {
        return new Formula(this, Op.LTE, field);
    }

    public Formula gt(Field field) {
        return new Formula(this, Op.GT, field);
    }

    public Formula gte(Field field) {
        return new Formula(this, Op.GTE, field);
    }

    @Override
    public abstract String toString();
}
