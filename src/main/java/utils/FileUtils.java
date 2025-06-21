package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static void writeToFile(String content, String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            Files.write(path, content.getBytes());
            logger.info("File written successfully: {}", filePath);
        } catch (IOException e) {
            logger.error("Error writing to file: {}", filePath, e);
            throw new RuntimeException("Failed to write file", e);
        }
    }

    public static String readFromFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            logger.error("Error reading from file: {}", filePath, e);
            throw new RuntimeException("Failed to read file", e);
        }
    }

    public static void createDirectory(String directoryPath) {
        try {
            Files.createDirectories(Paths.get(directoryPath));
            logger.info("Directory created: {}", directoryPath);
        } catch (IOException e) {
            logger.error("Error creating directory: {}", directoryPath, e);
        }
    }

    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    public static Properties loadProperties(String propertiesFile) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(propertiesFile)) {
            properties.load(input);
            logger.info("Properties loaded from: {}", propertiesFile);
        } catch (IOException e) {
            logger.error("Error loading properties from: {}", propertiesFile, e);
        }
        return properties;
    }
}