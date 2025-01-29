import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.google.common.base.Strings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MethodDeclaringClass {
    public static ArrayList<String> listMethod =new ArrayList<String>();
    public static ArrayList<MethodDeclaration> methods = new ArrayList<>();
    private static List<ClassOrInterfaceDeclaration> AllClasses;
    public static SetOfClasses setOfClasses = new SetOfClasses();
    public static void listMethods(File projectDir) throws IOException {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), MethodDeclaringClass ::handle).explore(projectDir);

    }

    public ArrayList<MethodDeclaration> main(String[] args, String projectPath) throws IOException {

        File projectDir = new File(projectPath);
        AllClasses= SetOfClasses.main(args,projectPath);
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        combinedTypeSolver.add(new JavaParserTypeSolver(new File(projectDir+"/src/main/java")));

        // Configure JavaParser to use type resolution
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

        listMethods(projectDir);
        return methods;
    }

    private static void handle(int level, String path, File file) throws IOException {

        // Set up a minimal type solver that only looks at the classes used to run this sample.


        File listMethods = new File("methodCalls.txt");
        if (listMethods.createNewFile()) {
            // System.out.println("file created");
        }
        else {
            //  System.out.println("file exist");
        }

        try {

            new VoidVisitorAdapter<Object>() {


                @Override
                public void visit(MethodDeclaration n, Object arg) {
                    super.visit(n,arg);

                    methods.add(n);



                }
            }.visit(StaticJavaParser.parse(file), null);
        } catch (IOException e) {
            new RuntimeException(e);
        }
    }
}