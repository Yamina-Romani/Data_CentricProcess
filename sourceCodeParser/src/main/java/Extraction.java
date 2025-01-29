import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Extraction {
    public static List<ClassOrInterfaceDeclaration> AllClasses = new ArrayList<>();

    public void extraction(ClassOrInterfaceDeclaration Class, String layer, String project, ArrayList<ArrayList<String>> listCalls, ArrayList<ArrayList<String>> listObject, ArrayList<ArrayList<String>> listVariables) throws IOException {
        File extract = new File(layer+".txt");
        File extract2 = new File("AllClasses.txt");
        String arg[] = new String[0];
        AllClasses=SetOfClasses.main(arg,project);
        int i, j,k;
        String var = null, p;

        FileWriter writer = new FileWriter(extract,true);
        BufferedWriter bw = new BufferedWriter(writer);
        FileWriter writer2 = new FileWriter(extract2, true);
        BufferedWriter bw2 = new BufferedWriter(writer2);
        Node parentNode = Class.getParentNode().orElse(null);
        if (parentNode instanceof ClassOrInterfaceDeclaration && Class.getMethods().size()==0) {
            // It's a nested class
            ClassOrInterfaceDeclaration outerClass = (ClassOrInterfaceDeclaration) parentNode;
            //System.out.println("Nested Class: " + outerClass.getNameAsString() + "." + Class.getNameAsString());
        } else {
            bw.write(Class.getName().toString() + ": ");
            bw2.write(Class.getName().toString() + ": ");


            NodeList<AnnotationExpr> annot = Class.getAnnotations();
            List<FieldDeclaration> fields = Class.getFields();
            List<MethodDeclaration> methods = Class.getMethods();
            NodeList<TypeParameter> parameters = Class.getTypeParameters();
            NodeList<ClassOrInterfaceType> implemented = Class.getImplementedTypes();
            NodeList<ClassOrInterfaceType> extended = Class.getExtendedTypes();
            i = 0;
            while (i < 8) {
                if (i == 0) {
                    j = 0;
                    while (!fields.isEmpty() && j < fields.size()) {
                        String f = fields.get(j).getVariables().get(0).getName().toString();
                        // System.out.println("f ="+fields.get(j)+" type = "+fields.get(j).getElementType());


                        if (fields.get(j).getElementType().getChildNodes().size() > 1) {

                            var = fields.get(j).getElementType().getChildNodes().get(1).toString();
                        } else {
                            if (fields.get(j).getElementType().getChildNodes().size() > 2) {
                                var = fields.get(j).getElementType().getChildNodes().get(2).toString();
                            } else {
                                var = fields.get(j).getElementType().toString();

                            }
                        }
                        if (var != null && exist(var, AllClasses)) {
                            if (f.length() > 3) {
                                bw.write(f + ", ");
                                bw2.write(f + ", ");

                            }
                            bw.write(var + ", ");
                            bw2.write(var + ", ");
                        }
                        j++;
                    }
                }
                if (i == 1) {
                    j = 0;
                    String methodName;
                    while (!methods.isEmpty() && j < methods.size()) {
                        methodName = Class.getName().toString() + " " + methods.get(j).getName();
                        if (!methodName.contentEquals("get") && !methodName.contentEquals("set") && !methodName.contentEquals("put") && !methodName.contentEquals("edit") && !methodName.contentEquals("save") && !methodName.contentEquals("delete") && !methodName.contentEquals("update")) {
                            bw.write(methodName + ", ");
                            bw2.write(methodName + ", ");
                        }
                        if (methods.get(j).getType().getChildNodes().size() > 2) {
                            var = methods.get(j).getType().getChildNodes().get(2).toString();
                            if (exist(var, AllClasses))
                                bw.write(var + ", ");
                                bw2.write(var + ", ");
                        } else {
                            if (methods.get(j).getType().getChildNodes().size() > 1) {
                                var = methods.get(j).getType().getChildNodes().get(1).toString();
                                if (exist(var, AllClasses)) {
                                    bw.write(var + ", ");
                                    bw2.write(var + ", ");
                                }
                                NodeList<Parameter> param = methods.get(j).getParameters();
                                k = 0;
                                while (k < param.size()) {
                                    String pName = param.get(k).getName().toString();
                                    if (pName.length() > 3) {
                                        bw.write(pName + ", ");
                                        bw2.write(pName + ", ");
                                    }
                                    if (param.get(k).getType().getChildNodes().size() > 2) {
                                        p = param.get(k).getType().getChildNodes().get(2).toString();

                                    } else {
                                        if (param.get(k).getType().getChildNodes().size() > 1) {
                                            p = param.get(k).getType().getChildNodes().get(1).toString();

                                        } else {
                                            p = param.get(k).getType().getElementType().toString();

                                        }
                                    }
                                    if (exist(p, AllClasses)) {
                                        bw.write(p + ", ");
                                        bw2.write(p + ", ");
                                    }
                                    k++;
                                }

                            } else {
                                var = methods.get(j).getType().toString();
                                if (exist(var, AllClasses)) {
                                    bw.write(var + ", ");
                                    bw2.write(var + ", ");
                                }
                                NodeList<Parameter> param = methods.get(j).getParameters();
                                k = 0;
                                while (k < param.size()) {
                                    String pName = param.get(k).getName().toString();
                                    if (pName.length() > 3) {
                                        bw.write(pName + ", ");
                                        bw2.write(pName + ", ");
                                    }
                                    if (param.get(k).getType().getChildNodes().size() > 2) {
                                        p = param.get(k).getType().getChildNodes().get(2).toString();

                                    } else {
                                        if (param.get(k).getType().getChildNodes().size() > 1) {
                                            p = param.get(k).getType().getChildNodes().get(1).toString();

                                        } else {
                                            p = param.get(k).getType().getElementType().toString();

                                        }

                                    }
                                    if (exist(p, AllClasses)) {
                                        bw.write(p + ", ");
                                        bw2.write(p + ", ");
                                    }
                                    k++;
                                }

                            }
                        }
                        j++;

                    }

                }
                if (i == 2) {
                    k = 0;
                    boolean trouv = Boolean.FALSE;
                    int l;
                    ArrayList<String> lis = new ArrayList<>();
                    // System.out.println(Class.getName()+ " objects \n "+ listObject);
                    while (k < listObject.size() && !trouv) {
                        if (listObject.get(k).size() > 0 && listObject.get(k).get(0).contentEquals(Class.getName().toString())) {
                            lis = listObject.get(k);
                            l = 1;
                            while (l < lis.size()) {
                                if (exist(lis.get(l), AllClasses)) {
                                    bw.write(lis.get(l) + ", ");
                                    bw2.write(lis.get(l) + ", ");
                                }
                                l++;
                            }
                            trouv = Boolean.TRUE;
                        }
                        k++;
                    }
                }
                if (i == 3) {
                    if (implemented.isNonEmpty()) {
                        k = 0;
                        while (k < implemented.size()) {
                            if (implemented.get(k).getChildNodes().size() > 1) {
                                p = implemented.get(k).getChildNodes().get(1).toString();
                            } else {
                                p = implemented.get(k).getElementType().toString();

                            }
                            if (exist(p, AllClasses)) {
                                bw.write(p + ", ");
                                bw2.write(p + ", ");
                            }
                            k++;
                        }
                    }
                }
                if (i == 4) {
                    if (extended.isNonEmpty()) {
                        k = 0;
                        while (k < extended.size()) {
                            if (extended.get(k).getChildNodes().size() > 1) {
                                p = extended.get(k).getChildNodes().get(1).toString();

                            } else {
                                p = extended.get(k).getElementType().toString();

                            }
                            if (exist(p, AllClasses)) {
                                bw.write(p + ", ");
                                bw2.write(p + ", ");
                            }
                            k++;
                        }
                    }
                }
                if (i == 5) {
                    int m = 0;
                    while (m < listCalls.size()) {

                        if (listCalls.get(m).size() > 2 && listCalls.get(m).get(0).contentEquals(Class.getName().toString())) {

                            var = listCalls.get(m).get(2);
                            if (exist(var, AllClasses)) {

                                bw.write(listCalls.get(m).get(1) + " " + var + ", ");
                                bw2.write(listCalls.get(m).get(1) + " " + var + ", ");
                            }
                        }

                        m++;
                    }
                }
                if (i == 6) {
                    if (annot.toString().contains("@IdClass")) {
                        int l = 0;

                        while (l < annot.size()) {
                            if (annot.get(l).toString().contains("@IdClass")) {
                                int s = 0;
                                while (s < annot.get(l).getChildNodes().size()) {
                                    if (annot.get(l).getChildNodes().get(s).toString().contains(".class")) {
                                        String c = annot.get(l).getChildNodes().get(s).toString().replace(".class", "");
                                        bw.write(c + " " + c + ", ");
                                        bw2.write(c + " " + c + ", ");
                                    }
                                    s++;
                                }


                            }
                            l++;
                        }
                    }
                }
                if (i == 7) {
                    j = 0;
                    while (j < listVariables.size()) {
                        if (listVariables.get(j).get(0).contentEquals(Class.getName().toString())) {
                            int s = 1;
                            while (s < listVariables.get(j).size()) {

                                if (s + 1 < listVariables.get(j).size() && listVariables.get(j).get(s + 1) != null && exist(listVariables.get(j).get(s + 1), AllClasses)) {
                                    if (listVariables.get(j).get(s).length() > 3) {
                                        if (exist(listVariables.get(j).get(s), AllClasses)) {
                                            bw.write(listVariables.get(j).get(s) + ", ");
                                            bw2.write(listVariables.get(j).get(s) + ", ");
                                        }
                                    }
                                    if (exist(listVariables.get(j).get(s + 1), AllClasses)) {
                                        bw.write(listVariables.get(j).get(s + 1) + ", ");
                                        bw2.write(listVariables.get(j).get(s + 1) + ", ");
                                    }
                                }
                                s = s + 2;
                            }

                        }
                        j++;
                    }
                }
                i++;

            }
            bw.write("\n");
            bw.close();
            bw2.write("\n");
            bw2.close();
        }
AllClasses.clear();
    }
/********************************      exist     ***************************************/
    private boolean exist(String var, List<ClassOrInterfaceDeclaration> allClasses) {
        int i;
        i=0;
        while (i<allClasses.size())
        {
            if(var.contentEquals(allClasses.get(i).getName().toString()))
            {
                return Boolean.TRUE;
            }
            i++;
        }
        return Boolean.FALSE;
    }
}
