package korat.examples.sac.autogen.hbst;

import java.util.*;
import korat.finitization.*;
import korat.finitization.impl.*;
public class HBST67 {
	public Node root;
	public int size;

	public static class Node {
		public Node left;
		public Node right;
		public Node parent;
		public int elem;
	}

	public boolean repOK() {
	return acyclic()&&parentOK()&&isBalanced()&&searchOk()&&root!=null&&size==10&&root.right!=null&&root.elem==4&&root.left!=null&&root.parent==null&&root.right.right!=null&&root.right.elem==7&&root.right.left!=null&&root.right.parent!=null&&root.left.right!=null&&root.left.elem==1&&root.left.left!=null&&root.left.parent!=null&&root.right.right.right!=null&&root.right.right.elem==8&&root.right.right.left==null&&root.right.right.parent!=null&&root.right.left.right==null&&root.right.left.elem==6&&root.right.left.left!=null;
	}
	public static IFinitization finHBST67(int _x_) {
		IFinitization f = FinitizationFactory.create(HBST67.class);

		IObjSet e0 = f.createObjSet(Node.class, true);
		e0.addClassDomain(f.createClassDomain(Node.class, 10));
		IIntSet e1 = f.createIntSet(0, 10);
		IIntSet e2 = f.createIntSet(10, 10);

		f.set("root", e0);
		f.set("size", e2);
		f.set("Node.left", e0);
		f.set("Node.right", e0);
		f.set("Node.parent", e0);
		f.set("Node.elem", e1);

		return f;
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
	public boolean searchOk() { return isBST(root, Integer.MIN_VALUE, Integer.MAX_VALUE); }
}

