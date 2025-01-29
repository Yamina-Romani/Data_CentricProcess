import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ClassMethods {
    public void SaveMethods(String ClassName, List<MethodDeclaration> methods ) throws IOException {
        File listMethod = new File("methods.txt");


        FileWriter writer = new FileWriter(listMethod, true);
        BufferedWriter bw = new BufferedWriter(writer);
        if(!ClassName.contentEquals("Criteria") && !ClassName.contentEquals("Criterion") && !ClassName.contentEquals("GeneratedCriteria")) {
            bw.write(ClassName + " : ");
            if (!methods.isEmpty()) {
                int i = 0;
                while (i < methods.size()) {
                    bw.write(methods.get(i).getName().toString() + " ");
                    i++;
                }
            }
            bw.write("\n");
            bw.close();
        }
    }
}
