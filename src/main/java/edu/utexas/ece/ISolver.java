package edu.utexas.ece;

import korat.src.korat.extension.Solution;

import java.util.List;

/**
 * Created by Nima Dini on 4/28/17.
 */
public interface ISolver {
    List<Solution> solve(String repOK, String name) throws Exception;

    long explored(String repOK, String name) throws Exception;

    long count(String repOK, String[] heapPC) throws Exception;
}
