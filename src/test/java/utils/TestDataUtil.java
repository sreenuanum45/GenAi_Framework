package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for managing test data
 */
public class TestDataUtil {
    private static final Logger logger = LoggerFactory.getLogger(TestDataUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, String> getTestData(String fileName) {
        Map<String, String> testData = new HashMap<>();

        try {
            File file = new File("src/test/resources/testdata/" + fileName);
            JsonNode jsonNode = objectMapper.readTree(file);

            jsonNode.fields().forEachRemaining(entry -> {
                testData.put(entry.getKey(), entry.getValue().asText());
            });

        } catch (IOException e) {
            logger.error("Failed to load test data from {}", fileName, e);
        }

        return testData;
    }
}
