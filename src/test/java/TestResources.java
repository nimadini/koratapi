/**
 * Created by Nima Dini on 5/8/17.
 */

public class TestResources {
    public static final String treeAcyclic = "public boolean acyclic() {\n" +
            "        if (root == null)\n" +
            "            return size == 0;\n" +
            "        // checks that tree has no cycle\n" +
            "        Set visited = new HashSet();\n" +
            "        visited.add(root);\n" +
            "        LinkedList workList = new LinkedList();\n" +
            "        workList.add(root);\n" +
            "        while (!workList.isEmpty()) {\n" +
            "            Node current = (Node) workList.removeFirst();\n" +
            "            if (current.left != null) {\n" +
            "                if (!visited.add(current.left))\n" +
            "                    return false;\n" +
            "                workList.add(current.left);\n" +
            "            }\n" +
            "            if (current.right != null) {\n" +
            "                if (!visited.add(current.right))\n" +
            "                    return false;\n" +
            "                workList.add(current.right);\n" +
            "            }\n" +
            "        }\n" +
            "        return visited.size() == size;\n" +
            "    }";

    public static final String treeParentOK = "public boolean parentOK() {\n" +
            "        if (root == null)\n" +
            "            return true;\n" +
            "\n" +
            "        if (root.parent != null) {\n" +
            "            return false;\n" +
            "        }\n" +
            "\n" +
            "        List<Node> workList = new ArrayList<Node>();\n" +
            "        workList.add(root);\n" +
            "\n" +
            "        while (!workList.isEmpty()) {\n" +
            "            Node current = workList.remove(0);\n" +
            "\n" +
            "            if (current.left != null) {\n" +
            "                if (current.left.parent != current) {\n" +
            "                    return false;\n" +
            "                }\n" +
            "\n" +
            "                workList.add(current.left);\n" +
            "            }\n" +
            "\n" +
            "            if (current.right != null) {\n" +
            "                if (current.right.parent != current) {\n" +
            "                    return false;\n" +
            "                }\n" +
            "\n" +
            "                workList.add(current.right);\n" +
            "            }\n" +
            "        }\n" +
            "\n" +
            "        return true;\n" +
            "    }";


    public static final String treeBST =
            "public boolean isBST(Node root, int min, int max) {\n" +
                    "        if (root == null) {\n" +
                    "            return true;\n" +
                    "        }\n" +
                    "\n" +
                    "        return root.elem > min && root.elem < max &&\n" +
                    "            isBST(root.left, min, root.elem) && \n" +
                    "            isBST(root.right, root.elem, max);\n" +
                    "    }";

    public static final String searchOK =
            "public boolean searchOk() { " +
                    "return isBST(root, Integer.MIN_VALUE, Integer.MAX_VALUE); " +
                    "}";

    public static final String sllStructureOK = "public boolean structureOK() {\n" +
            "        if (header == null)\n" +
            "            return false;\n" +
            "\n" +
            "        if (header.element != 0)\n" +
            "            return false;\n" +
            "\n" +
            "        Set<Entry> visited = new java.util.HashSet<Entry>();\n" +
            "        visited.add(header);\n" +
            "        Entry current = header;\n" +
            "\n" +
            "        while (true) {\n" +
            "            Entry next = current.next;\n" +
            "            if (next == null)\n" +
            "                break;\n" +
            "\n" +
            "            if (!visited.add(next))\n" +
            "                return false;\n" +
            "\n" +
            "            current = next;\n" +
            "        }\n" +
            "\n" +
            "        return true;\n" +
            "    }";

    public static final String sllSorted = "public boolean sorted() {\n" +
            "        int prev = header.element;\n" +
            "\n" +
            "        Entry current = header.next;\n" +
            "\n" +
            "        while (current != null) {\n" +
            "            if (current.element < prev) {\n" +
            "                return false;\n" +
            "            }\n" +
            "\n" +
            "            prev = current.element;\n" +
            "            current = current.next;\n" +
            "        }\n" +
            "\n" +
            "        return true;\n" +
            "    }";

    public static final String sllSize = "public boolean size() {\n" +
            "        int elems = 0;\n" +
            "\n" +
            "        Entry current = header;\n" +
            "\n" +
            "        while (current != null) {\n" +
            "            current = current.next;\n" +
            "            elems++;\n" +
            "        }\n" +
            "\n" +
            "        return elems == size;\n" +
            "    }";
}
