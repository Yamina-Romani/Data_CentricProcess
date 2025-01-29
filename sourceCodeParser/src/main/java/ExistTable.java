import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

public class ExistTable {
    public boolean exist_in_tables(String className,  String projectName) throws IOException {
        File file_ = new File(projectName);
        String parentPath = file_.getParent();
        File dir  = new File(parentPath);


         File[] liste = dir.listFiles();

      if (liste != null) {
        String line;
        int i ;
        ArrayList<String> result;
        ArrayList<String> pair;
        for(File item : liste) {
            if (item.isFile()) {
                if (item.getName().toString().contains("Service")) {
                    File file = new File(parentPath + "/" + item.getName().toString());
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);
                    while ((line = br.readLine()) != null) {
                        line = line.replace("[", "");
                        line = line.replace("]", "");
                        line = line.replace("'", "");
                        line = line.replace(",", "");
                        line = line.replace(":", "");

                        StringTokenizer st = new StringTokenizer(line, " ");
                        result = new ArrayList<>();
                        while (st.hasMoreTokens()) {
                            result.add(st.nextToken());
                            i = 0;

                            while (i < result.size()) {
                                if (result.get(i).contentEquals(className.toLowerCase(Locale.ROOT).replace(" ", "")) || result.get(i).contentEquals(className.replace(" ", ""))) {
                                    return Boolean.TRUE;

                                }
                                i++;
                            }
                        }
                    }
                }
            }
        }}

        return Boolean.FALSE;
    }
}
