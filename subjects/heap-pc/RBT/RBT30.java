package korat.examples.sac.autogen.rbt;

import java.util.*;
import korat.finitization.*;
import korat.finitization.impl.*;
public class RBT30 {
	public Node root;
	public int size;

	public static class Node {
		public Node left;
		public Node right;
		public Node parent;
		public int elem;
		public int color;
	}

	public boolean repOK() {
	return acyclic()&&repOkColors()&&parentOK()&&searchOk()&&root!=null&&size==10&&root.right!=null&&root.elem==3&&root.left!=null&&root.color==0&&root.parent==null&&root.right.right!=null&&root.right.elem==7&&root.right.left!=null&&root.right.color==0&&root.right.parent!=null&&root.left.right!=null&&root.left.elem==1&&root.left.left!=null&&root.left.color==0&&root.left.parent!=null&&root.right.right.right==null&&root.right.right.elem==9&&root.right.right.left!=null&&root.right.right.color==0&&root.right.right.parent!=null&&root.right.left.right!=null&&root.right.left.elem==5&&root.right.left.left!=null&&root.right.left.color==0;
	}
	public static IFinitization finRBT30(int _x_) {
		IFinitization f = FinitizationFactory.create(RBT30.class);

		IObjSet e0 = f.createObjSet(Node.class, true);
		e0.addClassDomain(f.createClassDomain(Node.class, 10));
		IIntSet e1 = f.createIntSet(0, 9);
		IIntSet e2 = f.createIntSet(10, 10);
		IIntSet e3 = f.createIntSet(0, 1);

		f.set("root", e0);
		f.set("size", e2);
		f.set("Node.left", e0);
		f.set("Node.right", e0);
		f.set("Node.parent", e0);
		f.set("Node.elem", e1);
		f.set("Node.color", e3);

		return f;
	}

	private final boolean debug(String s) {
        // System.out.println(s);
        return false;
    }
private final class Pair {
            Node e;

            int n;

            Pair(Node e, int n) {
                this.e = e;
                this.n = n;
            }
        }

        private static final class Wrapper {
            Node e;

            Wrapper(Node e) {
                this.e = e;
            }

            public boolean equals(Object obj) {
                if (!(obj instanceof Wrapper))
                    return false;
                return e == ((Wrapper) obj).e;
            }

            public int hashCode() {
                return System.identityHashCode(e);
            }
        }
	public boolean searchOk() { return isBST(root, Integer.MIN_VALUE, Integer.MAX_VALUE); }
	private boolean repOkColors() {
        // RedHasOnlyBlackChildren
        java.util.LinkedList workList = new java.util.LinkedList();
        workList.add(root);
        while (!workList.isEmpty()) {
            Node current = (Node) workList.removeFirst();
            Node cl = current.left;
            Node cr = current.right;
            if (current.color == 1) {
                if (cl != null && cl.color == 1)
                    return debug("RedHasOnlyBlackChildren1");
                if (cr != null && cr.color == 1)
                    return debug("RedHasOnlyBlackChildren2");
            }
            if (cl != null)
                workList.add(cl);
            if (cr != null)
                workList.add(cr);
        }
        // SimplePathsFromRootToNILHaveSameNumberOfBlackNodes
        int numberOfBlack = -1;
        workList = new java.util.LinkedList();
        workList.add(new Pair(root, 0));
        while (!workList.isEmpty()) {
            Pair p = (Pair) workList.removeFirst();
            Node e = p.e;
            int n = p.n;
            if (e != null && e.color == 0)
                n++;
            if (e == null) {
                if (numberOfBlack == -1)
                    numberOfBlack = n;
                else if (numberOfBlack != n)
                    return debug("SimplePathsFromRootToNILHaveSameNumberOfBlackNodes");
            } else {
                workList.add(new Pair(e.left, n));
                workList.add(new Pair(e.right, n));
            }
        }
        return true;
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
}

