import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.*;
import java.util.ArrayList;

public class ObjectCreation {

    public static   ArrayList<ArrayList<String>> listObject = new ArrayList<ArrayList<String>>();
    public static void listObjectCreation(File projectDir) throws IOException {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), ObjectCreation::handle).explore(projectDir);

    }

    public  ArrayList<ArrayList<String>> main(String[] args,String projectPath) throws IOException {


        File projectDir = new File(projectPath);
        listObjectCreation(projectDir);

        return listObject;

    }

    private static void handle(int level, String path, File file) throws IOException {


        ArrayList<String> ClassObject = new ArrayList<>();

        String clasName;
        clasName= path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));

        try {

            new VoidVisitorAdapter<Object>() {

                @Override
                public void visit(ObjectCreationExpr n, Object arg) {
                    super.visit(n,arg);

                    File f1 = new File("vide");
                    FileReader reader = null;
                    boolean trouve=false;
                    try {
                        reader = new FileReader(f1);
                        BufferedReader br = new BufferedReader(reader);
                        String line;
                        while ((line=br.readLine())!=null && !trouve)
                        {
                            if(line.contentEquals(clasName.toString()))
                            {
                                trouve=true;
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                if(!trouve){
                    ClassObject.add(clasName);
                    if(n.getType().getChildNodes().size()>1)
                    {
                        ClassObject.add(n.getType().getChildNodes().get(1).toString());
                    }
                    else
                    {
                        ClassObject.add(n.getType().getElementType().toString());

                    }}

                }
            }.visit(StaticJavaParser.parse(file), null);
            if(!(listObject.contains(ClassObject)) && !(ClassObject.isEmpty())) {
                listObject.add(ClassObject);
            }
        } catch (IOException e) {
            new RuntimeException(e);
        }
    }

}