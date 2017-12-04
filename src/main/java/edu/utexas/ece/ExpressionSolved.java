package edu.utexas.ece;

import edu.utexas.ece.core.Expression;
import edu.utexas.ece.core.Formula;
import edu.utexas.ece.core.PredicateInvocation;

/**
 * Created by Nima Dini on 5/8/17.
 */
public class ExpressionSolved implements Comparable<ExpressionSolved> {
    private Expression expr;
    private long totalExplored;

    ExpressionSolved(Expression expr, long totalExplored) {
        this.expr = expr;
        this.totalExplored = totalExplored;
    }

    public Formula getFormula() {
        return new PredicateInvocation(expr);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ExpressionSolved)) {
            return false;
        }

        ExpressionSolved es = (ExpressionSolved) o;

        return expr.equals(es.expr) && totalExplored == es.totalExplored;
    }

    @Override
    public int hashCode() {
        return expr.hashCode() + new Long(totalExplored).hashCode();
    }

    @Override
    public int compareTo(ExpressionSolved o) {
        if (this.totalExplored < o.totalExplored) {
            return -1;
        }

        if (this.totalExplored > o.totalExplored) {
            return 1;
        }

        return 0;
    }
}
