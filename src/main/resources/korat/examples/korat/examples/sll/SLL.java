package korat.examples.sll;

import java.util.*;
import korat.finitization.*;
import korat.finitization.impl.*;

public class SLL {
    public Entry header;
    public int size;

    public static class Entry {
        public Entry next;
        public int element;
    }

    public boolean repOK() {
        return structureOK() && size() && sorted() && unique();
    }

    public static IFinitization finSLL(int num) {
        IFinitization f = FinitizationFactory.create(SLL.class);

        IObjSet e5 = f.createObjSet(Entry.class, true);
        e5.addClassDomain(f.createClassDomain(Entry.class, num+1));
        IIntSet e6 = f.createIntSet(0, num+4);
        IIntSet e7 = f.createIntSet(num, num);

        f.set("header", e5);
        f.set("size", e7);
        f.set("Entry.element", e6);
        f.set("Entry.next", e5);

        return f;
    }

    public boolean size() {
        int elems = 0;

        Entry current = header;

        while (current != null) {
            current = current.next;
            elems++;
        }

        return elems == size + 1;
    }

    public boolean structureOK() {
        if (header == null)
            return false;

        if (header.element != 0)
            return false;

        Set<Entry> visited = new java.util.HashSet<Entry>();
        visited.add(header);
        Entry current = header;

        while (true) {
            Entry next = current.next;
            if (next == null)
                break;

            if (!visited.add(next))
                return false;

            current = next;
        }

        return true;
    }

    public boolean unique() {
        Entry current = header.next;

        Set<Integer> elems = new HashSet<Integer>();

        while (current != null) {
            if (elems.contains(current.element)) {
                return false;
            }

            elems.add(current.element);

            current = current.next;
        }

        return true;
    }

    public boolean sorted() {
        int prev = header.element;

        Entry current = header.next;

        while (current != null) {
            if (current.element < prev) {
                return false;
            }

            prev = current.element;
            current = current.next;
        }

        return true;
    }
}