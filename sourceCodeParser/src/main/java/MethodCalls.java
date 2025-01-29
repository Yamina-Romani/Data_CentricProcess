import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedTypeParameterDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.Pair;
import com.google.common.base.Strings;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MethodCalls {
    public static ArrayList<ArrayList<String>> listMethodCall =new ArrayList<ArrayList<String>>();
    private static List<ClassOrInterfaceDeclaration> AllClasses;
    public static ArrayList<String> listMethod =new ArrayList<String>();

    public static MethodDeclaringClass methodDeclaringClass = new MethodDeclaringClass();
    public static SetOfClasses setOfClasses = new SetOfClasses();
    public static ArrayList<MethodDeclaration> Methods ;

    public static void listMethodCalls(File projectDir) throws IOException {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), MethodCalls::handle).explore(projectDir);

    }

    public ArrayList<ArrayList<String>> main(String[] args,String projectPath) throws IOException {

        File projectDir = new File(projectPath);
        AllClasses= SetOfClasses.main(args,projectPath);
        Methods = methodDeclaringClass.main(args,projectPath);
        listMethod = getMethods(Methods);
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        combinedTypeSolver.add(new JavaParserTypeSolver(new File(projectDir+"/src/main/java")));

        // Configure JavaParser to use type resolution
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

        listMethodCalls(projectDir);
        return listMethodCall;
    }


    private static void handle(int level, String path, File file) throws IOException {

        // Set up a minimal type solver that only looks at the classes used to run this sample.


        File listMethodCalls = new File("methodCalls.txt");
        if (listMethodCalls.createNewFile()) {
            // System.out.println("file created");
        }
        else {
            //  System.out.println("file exist");
        }
        File file_ = new File("methodCalls.txt");

        FileWriter writer = new FileWriter(file_, true);
        BufferedWriter bw = new BufferedWriter(writer);
        String c = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));

        System.out.println("\n"+path.substring(path.lastIndexOf('/')+1,path.lastIndexOf('.')));
        System.out.println(Strings.repeat("=", path.length()));
        File f1 = new File("vide");

        FileReader reader = null;
        boolean trouve=false;

            reader = new FileReader(f1);
            BufferedReader br = new BufferedReader(reader);
            String line;
            while ((line=br.readLine())!=null && !trouve)
            {
                if(line.contentEquals(c))
                {
                    trouve=true;
                }
            }
            if(!trouve) {
                if(!c.contains("maven") && !c.contains("Maven")){

                        bw.write(c+'\n');

                    }}


        try {

            boolean finalTrouve = trouve;
            new VoidVisitorAdapter<Object>() {

                @Override
                public void visit(MethodCallExpr n, Object arg) {
                    super.visit(n,arg);
                    String message = " ";
                    if(!finalTrouve) {
                    if(!c.contains("maven") && !c.contains("Maven")) {

                        ResolvedType returnedType = null;
                        String type = "", Solvedtype = null;
                        Boolean trouv = Boolean.FALSE;
                        ArrayList<String> set = new ArrayList<>();
                        set.add(c);
                       // System.out.println(" method calll = " + n);
                        if (n.getScope().isPresent()) {
                            if (listMethod.contains(n.getName().toString())) {
                                //System.out.println(" method " + n.getName().toString());
                                if (n.getScope().get().toString().contains("(") && listMethod.contains(n.getScope().get().toString().substring(0, n.getScope().get().toString().indexOf('(')))) {


                                    returnedType = getReturns(Methods, n.getScope().get().toString().substring(0, n.getScope().get().toString().indexOf('(')));
                                    //System.out.println("returnedType : " + returnedType);
                                } else {
                                    if (n.getScope().get().toString().contains("(") && !n.getScope().get().toString().contains("new StringBuffer")) {

                                        ArrayList<String> parts = new ArrayList<>();
                                        int j = 0;
                                        int indice = 0;
                                        for (int i = 0; i < n.getScope().get().toString().length(); i++) {

                                            char[] st = n.getScope().get().toString().toCharArray();
                                            if (st[i] == ')' && i + 1 < n.getScope().get().toString().length() && st[i + 1] == '.') {
                                                parts.add(n.getScope().get().toString().substring(indice, i + 1));
                                                indice = i + 2;
                                                j++;
                                            }
                                        }
                                        if (parts.size() > 1) {
                                            returnedType = getReturns(Methods, parts.get(parts.size() - 1).substring(0, parts.get(parts.size() - 1).indexOf('(')));

                                        }
                                    }
                                }
                                ResolvedType resolvedType = null;
                                Expression rs;
                                if (returnedType != null) {
                                    resolvedType = returnedType;
                                } else {

                                    rs = n.getScope().get();

                                    try {
                                        if (!rs.calculateResolvedType().isNull()) {
                                            resolvedType = rs.calculateResolvedType();
                                        }
                                    } catch (Exception e) {
                                        message = e.getMessage();

                                    }
                                    if (message.contentEquals(" ")) {
                                        resolvedType = rs.calculateResolvedType();

                                    }
                                }


                                if (resolvedType != null) {
                                    if (resolvedType.isReferenceType()) {
                                        List<Pair<ResolvedTypeParameterDeclaration, ResolvedType>> val = resolvedType.asReferenceType().getTypeParametersMap();
                                        if (val.size() > 0) {
                                            if (val.get(0).b.isReferenceType()) {
                                                Solvedtype = val.get(0).b.asReferenceType().getTypeDeclaration().getName().toString();
                                            }
                                        } else {

                                            Solvedtype = resolvedType.asReferenceType().getTypeDeclaration().getName().toString();

                                        }

                                    } else {
                                        if (resolvedType.isConstraint()) {
                                            if (resolvedType.asConstraintType().getBound().isTypeVariable()) {
                                            } else {
                                                Solvedtype = resolvedType.asConstraintType().getBound().asReferenceType().getTypeDeclaration().getName().toString();
                                            }
                                        } else {
                                            Solvedtype = resolvedType.toString();
                                        }

                                    }
                                    if (Solvedtype != null) {
                                        for (int i = 0; i < AllClasses.size(); i++) {
                                            if (AllClasses.get(i).getName().toString().contentEquals(Solvedtype)) {
                                                type = Solvedtype;

                                                set.add(n.getScope().toString());
                                                set.add(type);
                                            }

                                        }
                                    }
                                }
                            }
                        }

                        try {

                            if (type != "") {
                                bw.write(n.toString() + "\n");
                                bw.write("source class: " + c + "  ");
                                bw.write("target class: " + type + "\n");
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }else {
                        System.out.println(" "+c);
                    }
                    }}
            }.visit(StaticJavaParser.parse(file), null);

            bw.close();
        } catch (IOException e) {
            new RuntimeException(e);
        }
    }

    private static ResolvedType getReturns(ArrayList<MethodDeclaration> methods, String substring) {
        int i = 0;
        ResolvedType type = null;
        while (i<methods.size()) {

            if (methods.get(i).getName().toString().contentEquals(substring)) {
                try {


                    type = methods.get(i).resolve().getReturnType();
                } catch (Exception e){
            }
                if (type!=null)
                {return type;}
            }
           i=i+1;

        }
        return null;
    }

    private ArrayList<String> getMethods(ArrayList<MethodDeclaration> methods) {
        ArrayList<String> ListMethod = new ArrayList<>();
        int i = 0;
        while (i<methods.size())
        {
            ListMethod.add(methods.get(i).getName().toString());
            i=i+1;
        }
        return ListMethod;
    }

}