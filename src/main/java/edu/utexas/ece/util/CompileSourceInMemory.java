package edu.utexas.ece.util;

import jdk.nashorn.internal.codegen.CompilationException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;

/**
 * Created by Nima Dini on 4/30/17.
 * Adapted from:
 *   1. http://www.java2s.com/Code/Java/JDK-6/CompilingfromMemory.htm
 *   2.  http://stackoverflow.com/questions/21544446/how-do-you-dynamically-compile-and-load-external-java-classes
 */

public class CompileSourceInMemory {
    public static void compile(String source, String className, String targetDir) throws IOException,
            ClassNotFoundException, CompilationException {

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager =
                compiler.getStandardFileManager(null, null, null);

        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        out.println(source);
        out.close();
        JavaFileObject file = new JavaSourceFromString(className, writer.toString());

        String koratJarPath = System.getProperty("user.dir") + "/target/classes/korat/dist/korat.jar";

        Iterable<String> options = Arrays.asList("-classpath", koratJarPath, "-d", targetDir);

        Iterable<? extends JavaFileObject> compilationUnits = Collections.singletonList(file);
        CompilationTask task = compiler.getTask(null, fileManager, null,
                options, null, compilationUnits);

        if (!task.call()) {
            throw new RuntimeException("Compilation failed!");
        }
    }
}

class JavaSourceFromString extends SimpleJavaFileObject {
    private final String code;

    JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension),Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}