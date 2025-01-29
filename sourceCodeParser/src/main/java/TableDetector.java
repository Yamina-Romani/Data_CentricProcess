// File: TableDetector.java

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TableDetector {

    public void main(String projectPath) throws IOException {

                // Detect table-related classes
                List<String> tableNames = detectTableClassesWithNames(projectPath);
                File file = new File("ClassTable.txt");
                FileWriter writer = new FileWriter(file,false);
                BufferedWriter bw = new BufferedWriter(writer);
                for (int i = 0; i<tableNames.size(); i++)
                {
                    bw.write(tableNames.get(i));
                    bw.write("\n");
                }
                bw.close();
                if (tableNames.isEmpty()) {
                    //System.out.println("No table-related classes detected.");
                } else {
                    //System.out.println("Detected table-related classes with table names:");
                    tableNames.forEach(System.out::println);
                }
            }


            public static List<String> detectTableClassesWithNames(String projectPath) {
                List<String> tableNames = new ArrayList<>();
                File projectDir = new File(projectPath);

                List<File> javaFiles = listJavaFiles(projectDir);

                for (File file : javaFiles) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        CompilationUnit cu = StaticJavaParser.parse(fis);

                        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                            Optional<String> tableName = extractTableNameFromAllAnnotations(cu, clazz);
                            tableName.ifPresent(name -> {
                                tableNames.add(name +" "+ clazz.getNameAsString());
                            });
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return tableNames;
            }


            private static Optional<String> extractTableNameFromAllAnnotations(CompilationUnit cu, ClassOrInterfaceDeclaration clazz) {
                NodeList<AnnotationExpr> annotations = clazz.getAnnotations();
                for ( int i = 0;i< annotations.size();i++) {

                    String annotationName = annotations.get(i).getNameAsString();
                    //System.out.println("annotationName = " + annotationName);

                    // Handle @Table annotation
                    if ("Table".equals(annotationName)) {
                        //System.out.println("annotationName = " + annotationName);
                        NormalAnnotationExpr normalAnnotation = (NormalAnnotationExpr) annotations.get(i);
                        return normalAnnotation.getPairs().stream()
                                .filter(pair -> pair.getNameAsString().equals("name"))
                                .map(pair -> pair.getValue().toString().replace("\"", ""))
                                .findFirst();
                    }
                    // Handle @Document annotation for MongoDB collections
                    if ("Document".equals(annotationName)) {
                        //System.out.println("annotationName = " + annotationName);
                        NormalAnnotationExpr normalAnnotation = (NormalAnnotationExpr) annotations.get(i);
                        return normalAnnotation.getPairs().stream()
                                .filter(pair -> pair.getNameAsString().equals("collection"))
                                .map(pair -> pair.getValue().toString().replace("\"", ""))
                                .findFirst();
                    }
                }

                    if (annotations.size()>=1 && ("Entity".equals(annotations.get(0).getNameAsString()))) {
                        return Optional.of(clazz.getNameAsString());
                    }
                    else {
                        // Check if the class implements Serializable
                        if(clazz.getImplementedTypes().stream()
                                .anyMatch(type -> type.getNameAsString().equals("Serializable")))
                        {
                            return Optional.of(clazz.getNameAsString());
                        }
                    }


                if (clazz.getNameAsString().endsWith("Table")) {
                    return Optional.of(clazz.getNameAsString().replace("Table", "").toLowerCase());
                }

                // No table name found
                return Optional.empty();
            }


            private static List<File> listJavaFiles(File dir) {
                List<File> javaFiles = new ArrayList<>();
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            javaFiles.addAll(listJavaFiles(file));
                        } else if (file.getName().endsWith(".java")) {
                            javaFiles.add(file);
                        }
                    }
                }
                return javaFiles;
            }
        }
