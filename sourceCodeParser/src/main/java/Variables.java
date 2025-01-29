import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.*;
import java.util.ArrayList;

public class Variables {

    public static   ArrayList<ArrayList<String>> listObject = new ArrayList<ArrayList<String>>();
    public static void listVariables(File projectDir) throws IOException {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), Variables::handle).explore(projectDir);

    }

    public  ArrayList<ArrayList<String>> main(String[] args,String projectPath) throws IOException {


        File projectDir = new File(projectPath);
        listVariables(projectDir);
        return listObject;

    }

    private static void handle(int level, String path, File file) throws IOException {


        ArrayList<String> ClassObject = new ArrayList<>();

        String clasName;
        clasName= path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));

        try {

            new VoidVisitorAdapter<Object>() {

                @Override
                public void visit(VariableDeclarationExpr n, Object arg) {
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
                    if(!ClassObject.contains(n.getVariables().get(0).getName())) {
                         ClassObject.add(n.getVariables().get(0).getName().toString());
                         ClassObject.add(n.getVariables().get(0).getType().toString());
                     }
                }

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