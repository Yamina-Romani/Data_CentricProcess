import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;

import java.io.File;
import java.io.IOException;

public class ObtainLayer {

    public String obtainLayer(ClassOrInterfaceDeclaration c, String projectName,String path) throws IOException {
        ExistTable existTable = new ExistTable();

        String layer = null;
        NodeList<AnnotationExpr> annotation = c.getAnnotations();
        if (annotation.toString().contains("@Document") || (annotation.toString().contains(".Entity") || annotation.toString().contains("@Embeddable") || annotation.toString().contains("@Entity,") || annotation.toString().contentEquals("@Entity") || annotation.toString().contains("@Entity") || path.contains("/entities") || path.contains("/domain")  || path.contains("/domainobjects") || path.contains("/mongo") || path.contains("/model")) ) {
            layer = "Entity";

        } else {
            if (annotation.toString().contains("@Repository,") || annotation.toString().contentEquals("@Repository") || (c.getName().toString().contains("Repository")) || path.contains("/repositories") || (c.getName().toString().contains("Dao")) || (c.getName().toString().contains("DAO")) ) {
                layer = "DataAccess";
            } else {
                if (annotation.toString().contains("@Service,") || annotation.toString().contentEquals("@Service") || (c.getName().toString().contains("Service")) || path.contains("/services") ) {
                    layer = "Service";

                } else {
                    if (annotation.toString().contains("@Path") || (annotation.toString().contentEquals("@Controller")) || (annotation.toString().contains("@Controller,")) || (c.getName().toString().contains("Controller")) || path.contains("/controllers")) {
                        layer = "Controller";
                    } else {
                        if ((c.getName().toString().contains("Mapper")) || path.contains("/mapper")) {
                            layer = "Mapper";
                        } else {
                            if (existTable.exist_in_tables(c.getName().toString(), projectName)) {
                                layer = "Entity";
                            } else {
                                layer = "Other";
                            }
                        }
                    }
                }}


        }

        return layer;
    }
}


