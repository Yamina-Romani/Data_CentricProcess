import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SetOfClasses {

    public static List<ClassOrInterfaceDeclaration> AllClasses = new ArrayList<>();

    public  List<ClassOrInterfaceDeclaration> getAllClasses() {
        return AllClasses;
    }


    public static List<ClassOrInterfaceDeclaration> listClasses(File projectDir) throws IOException {

        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            if(path.contains("/src/main/java/") || (path.contains("/src/net/jforum") && !(path.contains("/www/website/src/net/jforum")))){
            try {
                new VoidVisitorAdapter<Object>() {

                    @Override
                    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                        super.visit(n, arg);
                       if(!n.getName().toString().contentEquals("Criteria") && !n.getName().toString().contentEquals("GeneratedCriteria") &&
                               !n.getName().toString().contentEquals("Criterion")) {

                           AllClasses.add(n);
                       }
                    }
                }.visit(StaticJavaParser.parse(file), null);


            } catch (IOException e) {
                new RuntimeException(e);
            }}
        }).explore(projectDir);
return AllClasses;
    }


    public static List<ClassOrInterfaceDeclaration> main(String[] args,String projectPath) throws IOException {
        Scanner sc = new Scanner(System.in);

        File projectDir = new File(projectPath);
        AllClasses=listClasses(projectDir);
        return AllClasses;
    }
}