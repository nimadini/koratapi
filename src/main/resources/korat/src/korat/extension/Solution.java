package korat.src.korat.extension;

/**
 * Created by Nima Dini on 4/28/17.
 */
public class Solution {
    private String candidateVector;

    public Solution(String cv) {
        this.candidateVector = cv;
    }

    public Solution(int[] cv) {
        String candidate = "";

        for (int cvElem : cv) {
            candidate += (cvElem + " ");
        }

        this.candidateVector = candidate;
    }

    public int[] getCV() {
        if (candidateVector == null) {
            return null;
        }

        String[] elems = candidateVector.split(" ");

        int[] cv = new int[elems.length];

        for (int i = 0; i < cv.length; i++) {
            cv[i] = Integer.parseInt(elems[i]);
        }

        return cv;
    }

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof Solution)) {
            return false;
        }

        Solution sol = (Solution) o;

        return sol.candidateVector.equals(this.candidateVector);
    }

    @Override
    public String toString() {
        return candidateVector;
    }
}
