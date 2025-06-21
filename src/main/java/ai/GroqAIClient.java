package ai;

import com.ai.automation.core.ConfigManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GroqAIClient {
    private static final Logger logger = LoggerFactory.getLogger(GroqAIClient.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String processPrompt(String userPrompt) {
        try {
            String systemPrompt = """
                You are an expert test automation engineer. Convert the given English prompt into structured JSON format containing:
                1. Feature file content in Gherkin format
                2. Page object model class with locators
                3. Step definition class
                4. Test data

                Analyze the prompt and identify:
                - Actions (click, type, verify, navigate)
                - Elements (buttons, fields, links)
                - Test data (URLs, text inputs, expected results)
                - Page objects needed

                Return only valid JSON with this structure:
                {
                  "featureFile": {
                    "name": "LoginTest.feature",
                    "content": "Feature: Login functionality\\n  Scenario: Successful login\\n    Given..."
                  },
                  "pageObject": {
                    "className": "LoginPage",
                    "content": "package pages;\\n\\nimport..."
                  },
                  "stepDefinition": {
                    "className": "LoginSteps",
                    "content": "package steps;\\n\\nimport..."
                  },
                  "testData": {
                    "url": "https://example.com",
                    "email": "user@example.com",
                    "password": "mypassword"
                  }
                }

                Use advanced locator strategies with self-healing capabilities.
                """;

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama-3.3-70b-versatile");
            requestBody.put("messages", new Object[]{
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userPrompt)
            });
            requestBody.put("temperature", 0.1);
            requestBody.put("max_tokens", 4000);

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(ConfigManager.getGroqApiUrl());
                post.setHeader("Authorization", "Bearer " + ConfigManager.getGroqApiKey());
                post.setHeader("Content-Type", "application/json");

                String jsonString = objectMapper.writeValueAsString(requestBody);
                post.setEntity(new StringEntity(jsonString));

                try (CloseableHttpResponse response = client.execute(post)) {
                    HttpEntity entity = response.getEntity();
                    String responseString = EntityUtils.toString(entity);

                    JsonNode jsonResponse = objectMapper.readTree(responseString);
                    String content = jsonResponse.get("choices").get(0).get("message").get("content").asText();

                    logger.info("AI Response received successfully");
                    return content;
                }
            }
        } catch (Exception e) {
            logger.error("Error processing prompt with Groq AI", e);
            throw new RuntimeException("Failed to process prompt", e);
        }
    }
}