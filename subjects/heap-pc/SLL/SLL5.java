package korat.examples.sac.autogen.sll;

import java.util.*;
import korat.finitization.*;
import korat.finitization.impl.*;
public class SLL5 {
	public Entry header;
	public int size;

	public static class Entry {
		public int element;
		public Entry next;
	}

	public boolean repOK() {
	return structureOK()&&size() &&sorted() &&unique() &&header!=null&&size==10&&header.next!=null&&header.element==0&&header.next.next!=null&&header.next.element==0&&header.next.next.next!=null&&header.next.next.element==2&&header.next.next.next.next!=null&&header.next.next.next.element==5&&header.next.next.next.next.next!=null&&header.next.next.next.next.element==8;
	}
	public static IFinitization finSLL5(int _x_) {
		IFinitization f = FinitizationFactory.create(SLL5.class);

		IObjSet e0 = f.createObjSet(Entry.class, true);
		e0.addClassDomain(f.createClassDomain(Entry.class, 11));
		IIntSet e1 = f.createIntSet(0, 14);
		IIntSet e2 = f.createIntSet(10, 10);

		f.set("header", e0);
		f.set("size", e2);
		f.set("Entry.element", e1);
		f.set("Entry.next", e0);

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
}

