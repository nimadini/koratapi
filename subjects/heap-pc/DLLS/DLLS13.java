package korat.examples.sac.autogen.dlls;

import java.util.*;
import korat.finitization.*;
import korat.finitization.impl.*;
public class DLLS13 {
	public Entry header;
	public int size;

	public static class Entry {
		public Entry next;
		public Entry previous;
		public int element;
	}

	public boolean repOK() {
	return circular()&&size()&&link()&&sorted() &&size==11&&header!=null&&header.next!=null&&header.previous!=null&&header.element==0&&header.next.next!=null&&header.next.previous!=null&&header.next.element==4&&header.previous.next!=null&&header.previous.previous!=null&&header.previous.element==8&&header.next.next.next!=null&&header.next.next.previous!=null&&header.next.next.element==6&&header.previous.previous.next!=null&&header.previous.previous.previous!=null&&header.previous.previous.element==7&&header.next.next.next.next!=null;
	}
	public static IFinitization finDLLS13(int _x_) {
		IFinitization f = FinitizationFactory.create(DLLS13.class);

		IObjSet e0 = f.createObjSet(Entry.class, true);
		e0.addClassDomain(f.createClassDomain(Entry.class, 11));
		IIntSet e1 = f.createIntSet(0, 11);
		IIntSet e2 = f.createIntSet(11, 11);

		f.set("header", e0);
		f.set("size", e2);
		f.set("Entry.next", e0);
		f.set("Entry.previous", e0);
		f.set("Entry.element", e1);

		return f;
	}

	public boolean sorted() {
        Entry current = header;
        int lastElem = current.element;

        current = current.next;

        while (current != header) {
            // if list with non unique elements.
            if (current.element < lastElem) {
                return false;
            }
            
            lastElem = current.element;
            current = current.next;
        }
        
        return true;
    }
	public boolean circular() {
        if (header == null)
            return false;
        if (header.element != 0) {
            return false;
        }

        Set visited = new java.util.HashSet();
        visited.add(header);
        Entry current = header;

        while (true) {
            Entry next = current.next;
            if (next == null)
                return false;

            current = next;

            if (!visited.add(next))
                break;
        }

        if (current != header)
            return false;

        return true;
    }
	public boolean size() {
        Set visited = new java.util.HashSet();
        visited.add(header);
        Entry current = header.next;

        while (true) {
            if (visited.contains(current)) {
                break;
            }
            visited.add(current);
            current = current.next;
        }

        return visited.size() == size;
    }
	public boolean link() {
        Entry current = header;

        while (true) {
            Entry next = current.next;

            if (next.previous != current) {
                return false;
            }

            current = next;

            if (current == header) {
                break;
            }
        }

        return true;
    }
}

