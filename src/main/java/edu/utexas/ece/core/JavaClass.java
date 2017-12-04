package edu.utexas.ece.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nima Dini on 4/29/17.
 */
public class JavaClass {
    private boolean _static = true;
    private String name;
    private List<String> fields = new ArrayList<String>();
    private List<JavaClass> innerClasses = new ArrayList<JavaClass>();
    private List<String> methods = new ArrayList<String>();

    public JavaClass(String name) {
        this.name = name;
    }

    public JavaClass(boolean _static, String name) {
        this(name);
        this._static = _static;
    }

    public void addField(String field) {
        this.fields.add(field);
    }

    public void addInnerClass(JavaClass cls) {
        this.innerClasses.add(cls);
    }

    public void addMethod(String method) {
        this.methods.add(method);
    }

    public boolean containsClass(JavaClass cls) {
        return this.innerClasses.contains(cls);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JavaClass)) {
            return false;
        }

        JavaClass cls = (JavaClass) o;

        // TODO(nd): Assumes 'name' uniquely identifies a JavaClass.
        return this.name.equals(cls.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    private String getTabs(int tabs) {
        StringBuilder indent = new StringBuilder();

        for (int i = 0; i < tabs; i++) {
            indent.append("\t");
        }

        return indent.toString();
    }

    public String stringify(int tabs) {
        StringBuilder source = new StringBuilder();

        Object[] declParams = this._static ? new String[] { " static ", this.name } : new String[] { " ", this.name };

        source.append(getTabs(tabs));
        source.append(String.format("public%sclass %s {\n", declParams));

        String indent = getTabs(tabs+1);

        for (String field : this.fields) {
            source.append(indent);
            source.append(field);
            source.append("\n");
        }

        for (JavaClass cls : this.innerClasses) {
            source.append("\n");
            source.append(cls.stringify(tabs+1));
            source.append("\n");
        }

        for (String method : this.methods) {
            source.append(indent);
            source.append(method);
            source.append("\n");
        }

        source.append(getTabs(tabs));
        source.append("}\n");

        return source.toString();
    }

    @Override
    public String toString() {
        return stringify(0);
    }
}
