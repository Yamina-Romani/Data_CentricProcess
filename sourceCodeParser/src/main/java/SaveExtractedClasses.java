import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SaveExtractedClasses {

    public void write() {
        List<String> sourceFiles = List.of(
                "Entity.txt",
                "DataAccess.txt",
                "Mapper.txt",
                "Service.txt",
                "Controller.txt",
                "Other.txt"
        );
        String destinationFile = "AllClasses.txt"; // Destination file path

        StringBuilder mergedContent = new StringBuilder();

        for (String filePath : sourceFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    mergedContent.append(line).append(System.lineSeparator());
                }
            } catch (IOException e) {
                System.err.println("Error reading from file " + filePath + ": " + e.getMessage());
            }
        }

        try (FileWriter writer = new FileWriter(destinationFile, false)) { // false to overwrite
            writer.write(mergedContent.toString());
            System.out.println("Files have been merged into " + destinationFile);
        } catch (IOException e) {
            System.err.println("Error writing to the destination file: " + e.getMessage());
        }
    }


}
