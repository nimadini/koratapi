package korat.testing.impl;

import korat.finitization.impl.CVElem;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.*;


/**
 * Created by cagdasyelen on 5/10/17.
 */
public class PathConditionGenerator {


    public int percentage;

    private Map<String, Object> pathMap = new LinkedHashMap<String, Object>();

    private HashMap<String, Integer> cvIndexMap = new HashMap<>();
    private CVElem [] structureList = null;

    private ArrayList<String> pathConditions = new ArrayList<String>();
    private String finalPathCondition = "";

    public PathConditionGenerator(int percentage){

        this.percentage = percentage;
    }

    public PathConditionGenerator(int percentage, CVElem [] structureList){
        this.percentage = percentage;
        this.structureList = structureList;
    }

    public HashMap<String, Integer> getCvIndexMap(){
        return this.cvIndexMap;
    }

    public void generatePathConditions(Object testCase) {
        Field[] fields = testCase.getClass().getDeclaredFields();



        for (int i = 0; i < fields.length; i++) {
            try {
                if (!(Modifier.isTransient(fields[i].getModifiers()) || Modifier.isStatic(fields[i].getModifiers()))) {
                    fields[i].setAccessible(true);

                    String currentPath = "";

                    for (String key : this.pathMap.keySet()) {
                        if (testCase.equals(this.pathMap.get(key)))
                            currentPath = key + "." + fields[i].getName();
                    }
                    if (currentPath.equals(""))
                        currentPath = fields[i].getName();


                    if (!this.pathMap.containsKey(currentPath)) {

                        this.pathMap.put(currentPath, fields[i].get(testCase));
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void generatePathConditions2(Object testCase) {
        Field[] fields = testCase.getClass().getDeclaredFields();



        for (int i = 0; i < fields.length; i++) {
            try {
                if (!(Modifier.isTransient(fields[i].getModifiers()) || Modifier.isStatic(fields[i].getModifiers()))) {
                    fields[i].setAccessible(true);

                    String currentPath = "";

                    for (String key : this.pathMap.keySet()) {
                        if (testCase.equals(this.pathMap.get(key)))
                            currentPath = key + "." + fields[i].getName();
                    }
                    if (currentPath.equals(""))
                        currentPath = fields[i].getName();


                    if (!this.pathMap.containsKey(currentPath)) {

                        this.pathMap.put(currentPath, fields[i].get(testCase));


                        for(int j = 0 ; j < this.structureList.length ; j++){

                            String fieldName = this.structureList[j].getFieldName();
                            Object fieldParent = this.structureList[j].getObj();
                            int index = this.structureList[j].indexInStateSpace;

                            String tempKey = "";
                            for(String key : this.pathMap.keySet()){
                                if(testCase.equals(this.pathMap.get(key)))
                                    tempKey = key + "." + fieldName;
                            }


                            if(currentPath.equals(fieldName) && !this.cvIndexMap.containsKey(fieldName)){
                                this.cvIndexMap.put(fieldName, index);

                                //System.out.println("Key : " + fieldName);
                                //System.out.println("Index : " + index);
                            }

                            else if(testCase.equals(fieldParent) && !this.cvIndexMap.containsKey(tempKey) && !tempKey.equals("") ){
                                this.cvIndexMap.put(tempKey , index);

                                ///System.out.println("Key : " + tempKey);
                                //System.out.println("Index : " + index);
                            }



                        }
                    }



                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }






    }


    public void traverse(Object testCase) {
        ObjectGraph.visitor(new ObjectGraph.Visitor() {
            @Override
            public boolean visit(Object object, Class clazz) {

                //System.out.println("\nVISITED  "  + object.toString());
                generatePathConditions(object);

                return false;
            }
        }).excludeStatic()
                .excludeTransient()
                .traverse(testCase);
    }

    public void traverse2(Object testCase) {
        ObjectGraph.visitor(new ObjectGraph.Visitor() {
            @Override
            public boolean visit(Object object, Class clazz) {

                //System.out.println("\nVISITED  "  + object.toString());
                generatePathConditions2(object);

                return false;
            }
        }).excludeStatic()
                .excludeTransient()
                .traverse(testCase);
    }


    public void parsePathConditions() {

        for (String key : this.pathMap.keySet()) {

            Object obj = this.pathMap.get(key);
            StringBuilder sb = new StringBuilder();
            sb.append(key);

            try {
                Integer i = (Integer) obj;

                String s = i.toString();

                sb.append(" == ");
                sb.append(s);
            } catch (ClassCastException cce) {
                sb.append(" != null");
            } catch (NullPointerException npe) {
                sb.append(" == null");
            }

            this.pathConditions.add(sb.toString());


        }


        StringBuilder sb = new StringBuilder(this.pathConditions.get(0));

        for (int i = 1; i < (float) this.pathConditions.size()*percentage/100.0; i++) {
            sb.append(" && ");
            sb.append(this.pathConditions.get(i));
        }

        this.finalPathCondition = sb.toString();


    }

    public void printPathConditions() {


        System.out.println("\n---------- PATH CONDITIONS ------------\n");

        for (String str : this.pathConditions) {
            System.out.println(str);
        }

        System.out.println("\n---------------------------------------\n");
    }

    public String getFinalPathCondition() {

        return this.finalPathCondition;

    }


}