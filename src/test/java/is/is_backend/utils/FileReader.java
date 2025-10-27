package is.is_backend.utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReader {

    public static String readJsonFile(String filePath) {
        try {
            var resource = FileReader.class.getClassLoader().getResource(filePath);
            if (resource == null) {
                throw new RuntimeException("File not found: " + filePath);
            }
            byte[] bytes = Files.readAllBytes(Paths.get(resource.toURI()));
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON file: " + filePath, e);
        }
    }
}
