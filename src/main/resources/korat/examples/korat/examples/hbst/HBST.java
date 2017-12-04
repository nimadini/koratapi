package korat.examples.hbst;

import java.util.*;
import korat.finitization.*;
import korat.finitization.impl.*;

public class HBST {
    public Node root;
    public int size;

    public static class Node {
        public Node right;
        public int elem;
        public Node left;
        public Node parent;
    }

    public boolean repOK() {
        return acyclic() && parentOK() && isBalanced() && searchOk();
    }

    public boolean isBalanced() {
        return isBalanced(root);
    }

    public int height(Node node) {
        if (node == null)
            return 0;

        return 1 + Math.max(height(node.left), height(node.right));
    }

    public boolean isBalanced(Node node) {
        if (node == null)
            return true;

        int lh = height(node.left);
        int rh = height(node.right);

        if (Math.abs(lh - rh) <= 1 && isBalanced(node.left) && isBalanced(node.right)) {
            return true;
        }

        return false;
    }

    public static IFinitization finHBST(int num) {
        IFinitization f = FinitizationFactory.create(HBST.class);

        IObjSet e5 = f.createObjSet(Node.class, true);
        e5.addClassDomain(f.createClassDomain(Node.class, num));
        IIntSet e6 = f.createIntSet(0, num);
        IIntSet e7 = f.createIntSet(num, num);

        f.set("root", e5);
        f.set("size", e7);
        f.set("Node.left", e5);
        f.set("Node.right", e5);
        f.set("Node.parent", e5);
        f.set("Node.elem", e6);

        return f;
    }

    public boolean searchOk() { return isBST(root, Integer.MIN_VALUE, Integer.MAX_VALUE); }

    public boolean parentOK() {
        if (root == null)
            return true;

        if (root.parent != null) {
            return false;
        }

        List<Node> workList = new ArrayList<Node>();
        workList.add(root);

        while (!workList.isEmpty()) {
            Node current = workList.remove(0);

            if (current.left != null) {
                if (current.left.parent != current) {
                    return false;
                }

                workList.add(current.left);
            }

            if (current.right != null) {
                if (current.right.parent != current) {
                    return false;
                }

                workList.add(current.right);
            }
        }

        return true;
    }

    public boolean isBST(Node root, int min, int max) {
        if (root == null) {
            return true;
        }

        return root.elem > min && root.elem < max &&
                isBST(root.left, min, root.elem) &&
                isBST(root.right, root.elem, max);
    }
    private final boolean debug(String s) {
        // System.out.println(s);
        return false;
    }

    public boolean acyclic() {
        if (root == null)
            return size == 0;
        // checks that tree has no cycle
        Set visited = new HashSet();
        visited.add(root);
        LinkedList workList = new LinkedList();
        workList.add(root);
        while (!workList.isEmpty()) {
            Node current = (Node) workList.removeFirst();
            if (current.left != null) {
                if (!visited.add(current.left))
                    return false;
                workList.add(current.left);
            }
            if (current.right != null) {
                if (!visited.add(current.right))
                    return false;
                workList.add(current.right);
            }
        }
        return visited.size() == size;
    }
}