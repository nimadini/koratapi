package korat.testing.impl;

import java.util.IdentityHashMap;
import java.util.Map;

import korat.finitization.IFinitization;
import korat.finitization.IIntSet;
import korat.finitization.impl.*;
import korat.instrumentation.IKoratArray;
import korat.testing.IKoratSearchStrategy;
import korat.utils.IIntList;
import korat.utils.IntListAI;

/**
 * StateSpaceExplorer implements Korat search strategy
 * 
 * @author Sasa Misailovic <sasa.misailovic@gmail.com>
 * 
 */
public class StateSpaceExplorer implements IKoratSearchStrategy {

    protected StateSpace stateSpace;

    protected CandidateBuilder candidateBuilder;

    protected int[] candidateVector;
    
    protected int[] startCV;
    
    protected int[] endCV;

    protected IIntList accessedFields;

    protected IIntList changedFields;

    public StateSpaceExplorer(IFinitization ifin) {
        Finitization fin = (Finitization)ifin; 
        stateSpace = fin.getStateSpace();

        int totalNumberOfFields = stateSpace.getTotalNumberOfFields();
        accessedFields = new IntListAI(totalNumberOfFields);

        changedFields = new IntListAI(totalNumberOfFields);
        for (int i = 0; i < totalNumberOfFields; i++)
            changedFields.add(i);

        candidateBuilder = new CandidateBuilder(stateSpace, changedFields);
        candidateVector = new int[totalNumberOfFields];
        
        startCV = fin.getInitialCandidateVector();
        
        endCV = null;
    }

    /*
     * -------------------------------------------------------------------------
     * Implementation of IKoratSearchStrategy interface.
     * -------------------------------------------------------------------------
     */
    public IIntList getAccessedFields() {
        return accessedFields;
    }

    public int[] getCandidateVector() {
        return candidateVector;
    }

    public void setEndCandidateVector(int[] endCV) {
        if (endCV.length != candidateVector.length)
            throw new RuntimeException("Invalid length of end candidate vector");
        this.endCV = endCV;
    }

    public void setStartCandidateVector(int[] startCV) {
        if (startCV.length != candidateVector.length)
            throw new RuntimeException("Invalid length of start candidate vector");
        this.startCV = startCV;
    }
    
    protected boolean firstTestCase = true;

    public Object nextTestCase(String[] heapPCs) {
        if (firstTestCase) {
            firstTestCase = false;
            // candidate vector to start vector
            candidateVector = startCV;
        } else {
            // find next candidate vector
            boolean hasNext = getNextCandidate(heapPCs);
            if (!hasNext) {
                // if vector is invalid, return null
                return null;
            }
        }
        return candidateBuilder.buildCandidate(candidateVector);
    }
    
    /*
     * -------------------------------------------------------------------------
     * Internal stuff.
     * -------------------------------------------------------------------------
     */

    private void resetCVElem(CVElem lastAccessedField, int lastAccessedFieldIndex) {
        if (lastAccessedField.isNullExcluded()) {
            candidateVector[lastAccessedFieldIndex] = 1;
        }

        else {
            candidateVector[lastAccessedFieldIndex] = 0;
        }
    }

    public int dotExprToCVIndex(String expr, StateSpace space) throws IllegalAccessException, NoSuchFieldException {
        String[] derefs = expr.split(".");

        Object obj = space.getRootObject();

        if (derefs.length == 1) {
            return space.getIndexInCandidateVector(obj, derefs[0]);
        }

        for (int i = 1; i < derefs.length - 1; i += 1) {
            obj.getClass().getField(derefs[i]).setAccessible(true);
            obj = obj.getClass().getField(derefs[i]).get(obj);
        }

        return space.getIndexInCandidateVector(obj, derefs[derefs.length-1]);
    }

    public int indexInFieldDomainForGivenValue(CVElem lhsCVElem, String rhs) {
        if (lhsCVElem.getFieldDomain() instanceof IntSet) {
            IntSet intSet = (IntSet) lhsCVElem.getFieldDomain();
            if (Integer.parseInt(rhs) > intSet.getMin()) {
                throw new RuntimeException("phrase index out of range");
            }
            return Integer.parseInt(rhs) - intSet.getMin();
        }

        throw new RuntimeException("Unsupported rhs");
    }

    public void pruneOnFirstAccess(String[] heapPCs, StateSpace stateSpace) {
        for (String heapPC : heapPCs) {
            String[] assignment = heapPC.split("==");

            if (assignment.length != 2) {
                throw new RuntimeException("Invalid heapPC phrase: " + heapPC);
            }

            try {
                int lhsCVIdx = dotExprToCVIndex(assignment[0], stateSpace);
                int fdIdx = indexInFieldDomainForGivenValue(stateSpace.getCVElem(lhsCVIdx), assignment[1]);

                // TODO(nd): need to check if it's the first access.
                // Setting candidate vector to desired value.
                candidateVector[lhsCVIdx] = fdIdx;

            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException("Invalid heapPC phrase: " + heapPC);
            }
        }
    }
    
    protected boolean getNextCandidate(String[] heapPCs) {
        boolean nextCandidateFound = false;

        changedFields.clear();

        if (heapPCs != null) {
            pruneOnFirstAccess(heapPCs, stateSpace);
        }

        while (!nextCandidateFound) {

            if (accessedFields.isEmpty())
                break; // candidate not found - search is completed

            int lastAccessedFieldIndex = accessedFields.removeLast();
            CVElem lastAccessedField = stateSpace.getCVElem(lastAccessedFieldIndex);
            FieldDomain fDomain = stateSpace.getFieldDomain(lastAccessedFieldIndex);
            int maxInstanceIndexForFieldDomain = fDomain.getNumberOfElements() - 1;
            int currentInstanceIndex = candidateVector[lastAccessedFieldIndex];

            if (lastAccessedField.isExcludedFromSearch()){ //array fields are exempt from search
                nextCandidateFound = false;
            
            } else if (currentInstanceIndex >= maxInstanceIndexForFieldDomain) {

                resetCVElem (lastAccessedField, lastAccessedFieldIndex);
//                candidateVector[lastAccessedFieldIndex] = 0;
                changedFields.add(lastAccessedFieldIndex);
                nextCandidateFound = false;

            } else {
                /*
                 * if we wanted just to exercise pruning, without
                 * non-isomorphism checks the following lines would suffice:
                 * candidateVector[lastAccessedField]++; 
                 * nextCandidateFound = true;
                 */

                int numberOfAccessedFields = accessedFields.numberOfElements();
                int maxInstanceIndexInClassDomain = -1;

                ClassDomain cDomain = fDomain.getClassDomainFor(currentInstanceIndex);

                if (fDomain.isPrimitiveType()
                        || !cDomain.isIncludedInIsomorphismChecking()) {

                    candidateVector[lastAccessedFieldIndex]++;
                    changedFields.add(lastAccessedFieldIndex);
                    nextCandidateFound = true;

                } else {

                    for (int i = 0; i < numberOfAccessedFields; i++) {
                        int accessedFieldIndex = accessedFields.get(i);
                        int activeInstanceIndex = candidateVector[accessedFieldIndex];

                        FieldDomain fd = stateSpace.getFieldDomain(accessedFieldIndex);
                        ClassDomain cd = fd.getClassDomainFor(activeInstanceIndex);

                        if (cd != null && cd == (cDomain)) {
                            int instanceIndex = fd.getClassDomainIndexFor(activeInstanceIndex);
                            if (maxInstanceIndexInClassDomain < instanceIndex)
                                maxInstanceIndexInClassDomain = instanceIndex;
                        }
                    }

                    int currentInstanceIndexInClassDomain = fDomain.getClassDomainIndexFor(currentInstanceIndex);

                    if (currentInstanceIndexInClassDomain <= maxInstanceIndexInClassDomain) {
                        candidateVector[lastAccessedFieldIndex]++;
                        changedFields.add(lastAccessedFieldIndex);
                        nextCandidateFound = true;

                    } else {

                        int nextInstanceIndex = fDomain.getIndexOfFirstObjectInNextClassDomain(currentInstanceIndex);
                        if (nextInstanceIndex == -1) {
//                            candidateVector[lastAccessedFieldIndex] = 0;
                            resetCVElem (lastAccessedField, lastAccessedFieldIndex);
                            changedFields.add(lastAccessedFieldIndex);
                            nextCandidateFound = false;
                        } else {
                            candidateVector[lastAccessedFieldIndex] = nextInstanceIndex;
                            changedFields.add(lastAccessedFieldIndex);
                            nextCandidateFound = true;
                        }

                    }
                }
            }

        }// end while

        if (nextCandidateFound) {
            nextCandidateFound = !reachedEndCV();
        }

        return nextCandidateFound;
    }

    private boolean reachedEndCV() {
        
        if (endCV == null)
            return false;
        for (int i = 0; i < candidateVector.length; i++) {
            if (candidateVector[i] != endCV[i])
                return false;
        }
        return true;
        
    }
    
    Map<Object, Object> visited = new IdentityHashMap<Object, Object>();

    public void reportCurrentAsValid() {
        visited.clear();
        Object root = stateSpace.getRootObject();
        touch(root);       
    }
       
    protected void touch(Object obj) {
    
        visited.put(obj, null);
        int[] objFlds = stateSpace.getFieldIndicesFor(obj);
        for (int fldIndex : objFlds) 
            touchField(fldIndex);

    }

    private void touchField(int fldIndex) {
    
        accessedFields.add(fldIndex);

        FieldDomain fd = stateSpace.getFieldDomain(fldIndex);
        if (fd.isPrimitiveType())
            return;

        int fldValueIndex = candidateVector[fldIndex];

        Object value = null;
        if (fd.isArrayType()) {
        
            value = ((ArraySet) fd).getArray(fldValueIndex);
            if (!visited.containsKey(value))
                touchArray(value);
            
        } else {
        
            value = ((ObjSet) fd).getObject(fldValueIndex);
            if (value!=null && !visited.containsKey(value))
                touch(value);
            
        }   
             
    }

    private void touchArray(Object obj) {
        visited.put(obj, null);
        
        int[] objFlds = stateSpace.getFieldIndicesFor(obj);
        IKoratArray arr = (IKoratArray) obj;
        
        int length = arr.getLength();
        for (int i = 0; i < length + 1; i++) {
            touchField(objFlds[i]);
        }
       
    }

}
