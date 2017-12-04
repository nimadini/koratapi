package edu.utexas.ece.core;

/**
 * Created by Nima Dini on 5/2/17.
 */
public class Predicate {
    private String invocation;
    private Type type;
    private String body;

    public String getInvocation() {
        return invocation;
    }

    public Predicate(String invocation, Type type, String body) {
        this.invocation = invocation;
        this.type = type;
        this.body = body;
    }

    @Override
    public String toString() {
        return body;
    }
}
