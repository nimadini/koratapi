package edu.utexas.ece.core;

import edu.utexas.ece.exception.VarDomainAlreadyDefinedException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nima Dini on 4/28/17.
 */
public class Bounds {
    private Map<Field, Domain> domainForVar = new HashMap<Field, Domain>();

    public void boundVariable(Field field, Domain domain) throws VarDomainAlreadyDefinedException {
        if (domainForVar.containsKey(field)) {
            throw new VarDomainAlreadyDefinedException();
        }

        this.domainForVar.put(field, domain);
    }

    private int numOfFields() {
        return domainForVar.keySet().size();
    }

    public boolean isEmpty() {
        return numOfFields() == 0;
    }

    public Domain getDomainForVar(Field field) {
        return domainForVar.get(field);
    }
}
