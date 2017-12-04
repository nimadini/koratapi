package korat.examples.queens;

import java.util.*;

import korat.finitization.IArraySet;
import korat.finitization.IClassDomain;
import korat.finitization.IFinitization;
import korat.finitization.IIntSet;
import korat.finitization.impl.FinitizationFactory;

public class Queens {
    int position[];

    boolean sameRowCheck() {
        boolean[] rows = new boolean[position.length];

        for (int k = 0; k < position.length; k += 1) {
            if (rows[position[k]] == true) {
                return false;
            }

            rows[position[k]] = true;
        }

        return true;
    }

    /*** Optimal implementation:
        public boolean diagonalCheck() {
            for (int k = 1; k < position.length; k += 1) {
                for (int i = 0; i < k; i += 1) {
                    if (position[i] == position[k] - (k - i) ||
                        position[i] == position[k] + (k - i)) {

                        return false;
                    }
                }
            }

            return true;
        }
    ***/

    public boolean diagonalCheck() {
        for (int k = 0; k < position.length; k += 1) {
            for (int i = 0; i < position.length; i += 1) {
                if (i == k) {
                    continue;
                }

                if (position[i] == position[k] - (k - i) ||
                        position[i] == position[k] + (k - i)) {

                    return false;
                }
            }
        }

        return true;
    }

    public boolean repOK() {
        return sameRowCheck() && diagonalCheck();
    }

    public static IFinitization finQueens(int n) {
        IFinitization f = FinitizationFactory.create(Queens.class);

        IIntSet arrayLength = f.createIntSet(n, n);
        IIntSet positions = f.createIntSet(0, n-1);
        int numOfArrays = 1;

        IArraySet elems = f.createArraySet(int[].class, arrayLength, positions, numOfArrays);

        f.set("position", elems);

        return f;
    }
}