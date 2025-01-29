import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.checkerframework.checker.units.qual.C;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Javamain {
    private static String projectPath, layer;

    public static Variables variables=new Variables();
    public static MethodCalls methodCall2 = new MethodCalls();
    public static ClassMethods classMethods = new ClassMethods();
    public static ArrayList<ArrayList<String>> listObject= new ArrayList<ArrayList<String>> ();
    public static ArrayList<ArrayList<String>> listCalledMethods=new   ArrayList<ArrayList<String>> ();
    public static ArrayList<ArrayList<String>> variable=new ArrayList<>();

    public static void javamain(File projectDir) throws IOException {


        Extraction extraction = new Extraction();
        ObtainLayer obtainLayer = new ObtainLayer();
        String arg[] = new String[0];

        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
           // System.out.println(path);
            //System.out.println(Strings.repeat("=", path.length()));C:\Users\hp\Downloads\TeamMatch_ServerSideSoftware_v2\Library_TeamMatch
            if((path.toString().contains("/src/main/java/" ) && !(path.toString().contains("/Library_TeamMatch/src/main"))) || (path.contains("/src/net/jforum") && !(path.contains("/www/website/src/net/jforum"))) && !(path.contains("/test/java") )&& !(path.contains("desktop") )) {


                try {
                    new VoidVisitorAdapter<Object>() {


                        @Override
                        public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                            super.visit(n, arg);
                            File f1 = new File("vide");
                            FileReader reader = null;
                            boolean trouve=false;
                            try {
                                reader = new FileReader(f1);
                                BufferedReader br = new BufferedReader(reader);
                                String line;
                                while ((line=br.readLine())!=null && !trouve)
                                {
                                 if(line.contentEquals(n.getName().toString()))
                                 {
                                     trouve=true;
                                 }
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if(!trouve) {
                                try {
                                    layer = obtainLayer.obtainLayer(n, projectPath, path);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    extraction.extraction(n, layer, projectPath, listCalledMethods, listObject, variable);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    classMethods.SaveMethods(n.getName().toString(), n.getMethods());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }


                        }
                    }.

                            visit(StaticJavaParser.parse(file), null);


                } catch (IOException e) {
                    new RuntimeException(e);
                }
             }
    }
             ).explore(projectDir);
        }



        public static void copyFiles() {
            String projectDir = System.getProperty("user.dir");
            File file = new File(projectDir);
            String parentPath = file.getParent();
            File file_ = new File(projectPath);
            String DestPath = file_.getParent();
            //System.out.println("parentPath "+DestPath);


            Path sourcePath = Paths.get(parentPath, "sourceCodeParser", "AllClasses.txt");
            Path sourcePath1 = Paths.get(parentPath, "sourceCodeParser", "methodCalls.txt");
            Path sourcePath2 = Paths.get(parentPath, "sourceCodeParser", "ClassTable.txt");


            copyFile(sourcePath, Paths.get(DestPath, sourcePath.getFileName().toString()));
            copyFile(sourcePath1, Paths.get(DestPath, sourcePath1.getFileName().toString()));
            copyFile(sourcePath2, Paths.get(DestPath, sourcePath2.getFileName().toString()));
        }

        private static void copyFile(Path sourcePath, Path destinationPath) {
            try {
                if (Files.exists(sourcePath)) {
                    Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    System.err.println("Source file does not exist: " + sourcePath);
                }
            } catch (IOException e) {
                System.err.println("Error while copying file: " + e.getMessage());
            }
        }




    public static void deleteFiles() throws IOException {

        String projectDir = System.getProperty("user.dir");

        File folder = new File(projectDir);
        if (!folder.exists()) {
            throw new IOException("The specified folder does not exist: " + folder.getAbsolutePath());
        }
        if (!folder.isDirectory()) {
            throw new IOException("The specified path is not a folder: " + folder.getAbsolutePath());
        }
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith("txt") && (file.getName().contains("AllClasses") || file.getName().contains("ClassT") ||
                        file.getName().contains("Controller") || file.getName().contains("Access") || file.getName().contains("Entity") || file.getName().contains("Mapper") || file.getName().contains("Service") || file.getName().contains("method") || file.getName().contains("Other"))) {
                    // Delete the file if it matches the criteria
                    if (!file.delete()) {
                        throw new IOException("Failed to delete file: " + file.getAbsolutePath());
                    }
                }
            }
        }
    }
    public static void main(String[] args) throws IOException {
        File projectDir ;
        String  ProjectName ;
        if (args.length == 0) {
            System.out.println("No arguments provided!");
           // System.exit(1);
            Scanner sc = new Scanner(System.in);
            //System.out.println("Veuillez saisir le nom de projet :");
            projectPath = sc.nextLine();
            projectDir = new File(projectPath);

        }
        else {
            // Print received arguments
            //System.out.println("Received arguments: " + args[0].toString());
            projectPath=args[0].toString();
            projectDir = new File(projectPath);
        }
        deleteFiles();
        String [] arg = new String[0];
        ObjectCreation objectCreation = new ObjectCreation();

        variable=variables.main(arg,projectPath);
        listCalledMethods = methodCall2.main(arg, projectPath);
        listObject  = objectCreation.main(arg, projectPath);


        javamain(projectDir);
        SaveExtractedClasses Save = new SaveExtractedClasses();
        Save.write();
        TableDetector tableDetector = new TableDetector();
        tableDetector.main(projectPath);
        copyFiles();
        deleteFiles();
        System.exit(0);



    }
    }