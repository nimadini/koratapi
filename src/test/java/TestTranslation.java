import java.util.List;

import static junit.framework.TestCase.assertEquals;
import org.junit.Test;
import korat.src.korat.extension.Solution;

import edu.utexas.ece.*;
import edu.utexas.ece.core.*;

/**
 * Created by Nima Dini on 5/1/17.
 */
public class TestTranslation {

    @Test
    public void testBinarySearchTree() throws Exception {
        /* binary search tree with parent pointers */

        KoratAPI ka = new KoratAPI();

        // Declare types.
        Type bt = ka.type("BST");
        Type node = ka.type("Node");

        // Declare fields.
        Field root = ka.field("root", bt, node);
        Field size = ka.field("size", bt, ka.INT);
        Field left = ka.field("left", node, node);
        Field right = ka.field("right", node, node);
        Field parent = ka.field("parent", node, node);
        Field elem = ka.field("elem", node, ka.INT);

        // Define helper predicates.
        Predicate acyclic = ka.predicate("acyclic()",
                bt, TestResources.treeAcyclic);
        Predicate parentOk = ka.predicate("parentOK()",
                bt, TestResources.treeParentOK);
        ka.predicate("isBST(Node root, int min, int max) ",
                bt, TestResources.treeBST);
        Predicate searchOk = ka.predicate("searchOk()",
                bt, TestResources.searchOK);

        // Define repOK.
        Formula f1 = ka.predicateInvocation(acyclic);
        Formula f2 = ka.predicateInvocation(parentOk);
        Formula f3 = ka.predicateInvocation(searchOk);
        Formula f4 = f2.and(f3);
        Formula f5 = f1.and(f4);

        Predicate repOK = ka.predicate("repOK()", bt, f5);

        // Define domains of values.
        Domain nodes = ka.objDomain(node, 3, true);
        Domain ints = ka.intDomain(1, 3);
        Domain sizes = ka.intDomain(3, 3);

        // Set field value domains.
        ka.setDomain(root, nodes);
        ka.setDomain(size, sizes);
        ka.setDomain(left, nodes);
        ka.setDomain(right, nodes);
        ka.setDomain(parent, nodes);
        ka.setDomain(elem, ints);

        ka.repOK(bt);

        List<Solution> solutions = ka.solve(repOK);

        for (Solution sol : solutions) {
            System.out.println(sol);
        }

        assertEquals(5, solutions.size());
    }

    @Test
    public void SLLExperiment() throws Exception {
        /* singly linked list with unique elements */

        KoratAPI ka = new KoratAPI();

        // Declare types.
        Type sll = ka.type("SLL");
        Type entry = ka.type("Entry");

        // Declare fields.
        Field header = ka.field("header", sll, entry);
        Field size = ka.field("size", sll, ka.INT);
        Field elem = ka.field("element", entry, ka.INT);
        Field next = ka.field("next", entry, entry);

        // Define helper predicates.
        Predicate sllStructure = ka.predicate("structureOK()",
                sll, TestResources.sllStructureOK);
        Predicate sllSorted = ka.predicate("sorted() ",
                sll, TestResources.sllSorted);
        Predicate sllSize = ka.predicate("size() ",
                sll, TestResources.sllSize);

        // Define repOK.
        Formula f1 = ka.predicateInvocation(sllStructure);
        Formula f2 = ka.predicateInvocation(sllSize);
        Formula f3 = ka.predicateInvocation(sllSorted);
        Formula f4 = f2.and(f3);
        Formula f5 = f1.and(f4);

        Predicate repOK = ka.predicate("repOK()", sll, f5);

        // Define domains of values.
        Domain entries = ka.objDomain(entry, 3, true);
        Domain elements = ka.intDomain(0, 3);
        Domain sizes = ka.intDomain(3, 3);

        // Set field value domains.
        ka.setDomain(header, entries);
        ka.setDomain(size, sizes);
        ka.setDomain(elem, elements);
        ka.setDomain(next, entries);

        ka.repOK(sll);

        List<Solution> solutions = ka.solve(repOK);

        for (Solution sol : solutions) {
            System.out.println(sol);
        }

        assertEquals(10, solutions.size());
    }
}
