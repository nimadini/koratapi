package edu.utexas.ece.core;

/**
 * Created by Nima Dini on 4/28/17.
 */
public enum Op {
    AND("&&"), LAND("&&"), SAND("&&&"), LT("<"), LTE("<="), GT(">"), GTE(">="), EQ("=="), NEQ("!="), DOT(".");

    private String value;

    private Op(String value) {
        this.value = value;
    }

    public String get() {
        return value;
    }
}