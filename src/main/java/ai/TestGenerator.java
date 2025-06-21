//package ai;// 5. TestGenerator.java
//
//
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.UUID;
//
//public class TestGenerator {
//    private static final Logger logger = LoggerFactory.getLogger(TestGenerator.class);
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//
//    public static void generateTestFiles(String aiResponse) {
//        try {
//            // Remove Markdown code block markers if present
//            String cleanedResponse = aiResponse.trim();
//            if (cleanedResponse.startsWith("```")) {
//                int firstNewline = cleanedResponse.indexOf('\n');
//                if (firstNewline != -1) {
//                    cleanedResponse = cleanedResponse.substring(firstNewline + 1);
//                }
//                if (cleanedResponse.endsWith("```")) {
//                    cleanedResponse = cleanedResponse.substring(0, cleanedResponse.length() - 3);
//                }
//                cleanedResponse = cleanedResponse.trim();
//            }
//
//            JsonNode jsonResponse = objectMapper.readTree(cleanedResponse);
//
//            // Generate Feature File
//            generateFeatureFile(jsonResponse.get("featureFile"));
//
//            // Generate Page Object
//            generatePageObject(jsonResponse.get("pageObject"));
//
//            // Generate Step Definition
//            generateStepDefinition(jsonResponse.get("stepDefinition"));
//
//            // Generate Runner Class
//            generateRunnerClass(jsonResponse);
//
//            // Generate TestNG Suite
//            generateTestNGSuite(jsonResponse);
//
//            logger.info("All test files generated successfully");
//
//        } catch (Exception e) {
//            logger.error("Error generating test files", e);
//            throw new RuntimeException("Failed to generate test files", e);
//        }
//    }
//
//    private static void generateFeatureFile(JsonNode featureFile) throws IOException {
//        String fileName = featureFile.get("name").asText();
//        String content = featureFile.get("content").asText();
//
//        Path featurePath = Paths.get("src/test/resources/features/" + fileName);
//        Files.createDirectories(featurePath.getParent());
//        Files.write(featurePath, content.getBytes());
//
//        logger.info("Feature file generated: {}", fileName);
//    }
//
//    private static void generatePageObject(JsonNode pageObject) throws IOException {
//        String className = pageObject.get("className").asText();
//        String content = pageObject.get("content").asText();
//
//        Path pagePath = Paths.get("src/main/java/pages/" + className + ".java");
//        Files.createDirectories(pagePath.getParent());
//        Files.write(pagePath, content.getBytes());
//
//        logger.info("Page object generated: {}", className);
//    }
//
//    private static void generateStepDefinition(JsonNode stepDefinition) throws IOException {
//        String className = stepDefinition.get("className").asText();
//        String content = stepDefinition.get("content").asText();
//
//        Path stepPath = Paths.get("src/test/java/steps/" + className + ".java");
//        Files.createDirectories(stepPath.getParent());
//        Files.write(stepPath, content.getBytes());
//
//        logger.info("Step definition generated: {}", className);
//    }
//
//    private static void generateRunnerClass(JsonNode jsonResponse) throws IOException {
//        String runnerName = "TestRunner_" + UUID.randomUUID().toString().substring(0, 8);
//        String featureName = jsonResponse.get("featureFile").get("name").asText();
//
//        String runnerContent = String.format("""
//            package runners;
//
//            import io.cucumber.junit.Cucumber;
//            import io.cucumber.junit.CucumberOptions;
//            import org.junit.runner.RunWith;
//
//            @RunWith(Cucumber.class)
//            @CucumberOptions(
//                features = "src/test/resources/features/%s",
//                glue = {"steps"},
//                plugin = {
//                    "pretty",
//                    "html:target/cucumber-reports",
//                    "json:target/cucumber-reports/Cucumber.json",
//                    "junit:target/cucumber-reports/Cucumber.xml",
//                    "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
//                    "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
//                },
//                monochrome = true
//            )
//            public class %s {
//            }
//            """, featureName, runnerName);
//
//        Path runnerPath = Paths.get("src/test/java/runners/" + runnerName + ".java");
//        Files.createDirectories(runnerPath.getParent());
//        Files.write(runnerPath, runnerContent.getBytes());
//
//        logger.info("Runner class generated: {}", runnerName);
//    }
//
//    private static void generateTestNGSuite(JsonNode jsonResponse) throws IOException {
//        String suiteName = "TestSuite_" + UUID.randomUUID().toString().substring(0, 8);
//
//        String testngContent = String.format("""
//            <?xml version="1.0" encoding="UTF-8"?>
//            <!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
//            <suite name="%s" parallel="tests" thread-count="3">
//                <listeners>
//                    <listener class-name="com.ai.automation.reporting.ExtentReportManager"/>
//                    <listener class-name="com.ai.automation.reporting.AllureReportManager"/>
//                </listeners>
//
//                <test name="AI Generated Test">
//                    <classes>
//                        <class name="runners.TestRunner"/>
//                    </classes>
//                </test>
//            </suite>
//            """, suiteName);
//
//        Path testngPath = Paths.get("testng.xml");
//        Files.write(testngPath, testngContent.getBytes());
//
//        logger.info("TestNG suite generated: testng.xml");
//    }
//}
package ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * TestGenerator class responsible for generating test automation files
 * from AI-generated JSON responses including feature files, page objects,
 * step definitions, runner classes, and TestNG suites.
 */
public class TestGenerator {
    private static final Logger logger = LoggerFactory.getLogger(TestGenerator.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String PAGE_OBJECTS_DIR = "src/main/java/pages/";
    private static final String FILE_SUFFIX = ".java";
    // Configuration constants
    private static final String FEATURES_PATH = "src/test/resources/features/";
    private static final String PAGES_PATH = "src/main/java/pages/";
    private static final String STEPS_PATH = "src/test/java/steps/";
    private static final String RUNNERS_PATH = "src/test/java/runners/";
    private static final String TESTNG_FILE = "testng.xml";
    private static final String REPORTS_PATH = "target/cucumber-reports";

    /**
     * Main method to generate all test files from AI response
     *
     * @param aiResponse JSON response from AI containing test file specifications
     */
    public static void generateTestFiles(String aiResponse) {
        try {
            logger.info("Starting test file generation process...");

            // Clean and parse AI response
            String cleanedResponse = cleanAIResponse(aiResponse);
            JsonNode jsonResponse = objectMapper.readTree(cleanedResponse);

            // Validate JSON structure
            validateJsonStructure(jsonResponse);

            // Create necessary directories
            createDirectories();

            // Generate all test files
            generateFeatureFile(jsonResponse.get("featureFile"));
            generatePageObject(jsonResponse.get("pageObject"));
            generateStepDefinition(jsonResponse.get("stepDefinition"));
            generateRunnerClass(jsonResponse);
            generateTestNGSuite(jsonResponse);

            // Generate additional supporting files
            generatePropertiesFile();
            generateBaseTestClass();
            generateUtilityClasses();

            logger.info("All test files generated successfully");

        } catch (Exception e) {
            logger.error("Error generating test files: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate test files", e);
        }
    }

    /**
     * Clean AI response by removing markdown code blocks
     */
    private static String cleanAIResponse(String aiResponse) {
        String cleanedResponse = aiResponse.trim();

        if (cleanedResponse.startsWith("```")) {
            int firstNewline = cleanedResponse.indexOf('\n');
            if (firstNewline != -1) {
                cleanedResponse = cleanedResponse.substring(firstNewline + 1);
            }
            if (cleanedResponse.endsWith("```")) {
                cleanedResponse = cleanedResponse.substring(0, cleanedResponse.length() - 3);
            }
            cleanedResponse = cleanedResponse.trim();
        }

        return cleanedResponse;
    }

    /**
     * Validate JSON structure contains required fields
     */
    private static void validateJsonStructure(JsonNode jsonResponse) {
        List<String> missingFields = new ArrayList<>();

        if (!jsonResponse.has("featureFile")) missingFields.add("featureFile");
        if (!jsonResponse.has("pageObject")) missingFields.add("pageObject");
        if (!jsonResponse.has("stepDefinition")) missingFields.add("stepDefinition");

        if (!missingFields.isEmpty()) {
            throw new IllegalArgumentException("Missing required fields in JSON: " + String.join(", ", missingFields));
        }
    }

    /**
     * Create necessary directory structure
     */
    private static void createDirectories() throws IOException {
        String[] directories = {
                FEATURES_PATH, PAGES_PATH, STEPS_PATH, RUNNERS_PATH,
                "src/test/java/utils", "src/test/java/base",
                "src/test/resources/config", REPORTS_PATH
        };

        for (String dir : directories) {
            Files.createDirectories(Paths.get(dir));
        }

        logger.info("Directory structure created successfully");
    }

    /**
     * Generate Cucumber feature file
     */
    private static void generateFeatureFile(JsonNode featureFile) throws IOException {
        String fileName = featureFile.get("name").asText();
        String content = featureFile.get("content").asText();

        // Ensure .feature extension
        if (!fileName.endsWith(".feature")) {
            fileName += ".feature";
        }

        // Add @smoke tag to all scenarios if not present
        StringBuilder taggedContent = new StringBuilder();
        String[] lines = content.split("\r?\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.trim().startsWith("Scenario")) {
                // Check if previous line is a tag
                if (i == 0 || !lines[i-1].trim().startsWith("@")) {
                    taggedContent.append("  @smoke\n");
                }
            }
            taggedContent.append(line).append("\n");
        }

        Path featurePath = Paths.get(FEATURES_PATH + fileName);
        Files.write(featurePath, taggedContent.toString().getBytes());

        logger.info("Feature file generated: {}", fileName);
    }

    /**
     * Generate Page Object Model class
     */
    private static void generatePageObject(JsonNode pageObject) throws IOException {
        // Validate input JSON structure
        if (!pageObject.has("className") || !pageObject.has("content")) {
            throw new IllegalArgumentException("Invalid JSON structure: Missing 'className' or 'content'");
        }

        String className = pageObject.get("className").asText();
        String content = pageObject.get("content").asText();

        // Validate required fields
        if (className.isBlank()) {
            throw new IllegalArgumentException("Class name cannot be blank");
        }
        if (content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be blank");
        }

        // Create directory if missing
        Path outputDir = Paths.get(PAGE_OBJECTS_DIR);
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        // Fix WebDriverWait constructor for Selenium 4+
        content = content.replaceAll(
            "new\\s+WebDriverWait\\s*\\(\\s*driver\\s*,\\s*(\\d+)\\s*\\)",
            "new WebDriverWait(driver, Duration.ofSeconds($1))"
        );
        // Ensure import for java.time.Duration exists
        if (!content.contains("import java.time.Duration;")) {
            int pkgIdx = content.indexOf("package ");
            int semiIdx = content.indexOf(';', pkgIdx);
            if (pkgIdx != -1 && semiIdx != -1) {
                content = content.substring(0, semiIdx + 1) +
                    "\nimport java.time.Duration;" +
                    content.substring(semiIdx + 1);
            } else {
                content = "import java.time.Duration;\n" + content;
            }
        }

        // Write file with explicit charset and atomic replacement
        Path outputPath = outputDir.resolve(className + FILE_SUFFIX);
        Path tempPath = outputDir.resolve(className + ".tmp");

        Files.writeString(tempPath, content, StandardCharsets.UTF_8);
        Files.move(tempPath, outputPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

        logger.info("Generated page object: {}", outputPath);
    }

    /**
     * Generate Step Definition class
     */
    private static void generateStepDefinition(JsonNode stepDefinition) throws IOException {
        String className = stepDefinition.get("className").asText();
        String content = stepDefinition.get("content").asText();

        Path stepPath = Paths.get(STEPS_PATH + className + ".java");
        Files.write(stepPath, content.getBytes());

        logger.info("Step definition generated: {}", className);
    }


    //    private static void generateRunnerClass(JsonNode jsonResponse) throws IOException {
//        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
//        String runnerName = "TestRunner_" + timestamp;
//        String featureName = jsonResponse.get("featureFile").get("name").asText();
//
//        // Remove .feature extension if present for runner
//        if (featureName.endsWith(".feature")) {
//            featureName = featureName.substring(0, featureName.length() - 8);
//        }
//
//        String runnerContent = String.format("""
//                package runners;
//                import io.cucumber.testng.AbstractTestNGCucumberTests;
//                import io.cucumber.testng.CucumberOptions;
//                import org.testng.annotations.DataProvider;
//
//                import java.util.Date;
//                import java.text.SimpleDateFormat;
//
//                /**
//                 * Cucumber TestNG Runner
//                 * Generated on: %s
//                 */
//                @CucumberOptions(
//                    features = "src/test/resources/features",
//                    glue = {"steps", "hooks"},
//                    plugin = {
//                        "pretty",
//                        "html:target/cucumber-reports/html-report.html",
//                        "json:target/cucumber-reports/cucumber.json",
//                        "junit:target/cucumber-reports/cucumber.xml",
//                        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
//                        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
//                    },
//                    monochrome = true,
//                    publish = true,
//                    tags = "@smoke or @regression"
//                )
//                public class %s extends AbstractTestNGCucumberTests {
//
//                    @Override
//                    @DataProvider(parallel = true)
//                    public Object[][] scenarios() {
//                        return super.scenarios();
//                    }
//
//                    // Get current timestamp for the generated comment
//                    private static String getCurrentTimestamp() {
//                        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//                    }
//
//                    // Entry point for direct execution (optional)
//                    public static void main(String[] args) {
//                        // This space can be used for custom TestNG runner initialization
//                        System.out.printf("Cucumber TestNG Runner generated at: %s%n", getCurrentTimestamp());
//                    }
//                }
//            """, timestamp, runnerName);
//
//        Path runnerPath = Paths.get(RUNNERS_PATH + runnerName + ".java");
//        Files.write(runnerPath, runnerContent.getBytes());
//
//        logger.info("Runner class generated: {}", runnerName);
//    }
    private static void generateRunnerClass(JsonNode jsonResponse) throws IOException {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String runnerName = "TestRunner_" + timestamp;
        String featureName = jsonResponse.get("featureFile").get("name").asText();

        // Remove .feature extension if present for runner
        if (featureName.endsWith(".feature")) {
            featureName = featureName.substring(0, featureName.length() - 8);
        }

        // Template with proper formatting placeholders
        String runnerTemplate = """
                package runners;
           
                import io.cucumber.testng.AbstractTestNGCucumberTests;
                import io.cucumber.testng.CucumberOptions;
                import org.testng.annotations.DataProvider;
                import java.util.Date;
                import java.text.SimpleDateFormat;
            
                /**
                 * Cucumber TestNG Runner
                 * Generated on: %s
                 * Feature: %s
                 */
                @CucumberOptions(
                    features = "src/test/resources/features",
                    glue = {"steps", "hooks"},
                    plugin = {
                        "pretty",
                        "html:target/cucumber-reports/html-report.html",
                        "json:target/cucumber-reports/cucumber.json",
                        "junit:target/cucumber-reports/cucumber.xml",
                        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
                    },
                    monochrome = true,
                    publish = true,
                    tags = "@smoke or @regression"
                )
                public class %s extends AbstractTestNGCucumberTests {
            
                    @Override
                    @DataProvider(parallel = true)
                    public Object[][] scenarios() {
                        return super.scenarios();
                    }
            
                    /**
                     * Get current timestamp for logging/reporting purposes
                     * @return formatted timestamp string
                     */
                    private static String getCurrentTimestamp() {
                        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    }
            
                    /**
                     * Get the feature name this runner was generated for
                     * @return feature name
                     */
                    public static String getFeatureName() {
                        return "%s";
                    }
            
                }
            """;

        // Format with three arguments: timestamp, feature name, runner class name, feature name again
        String runnerContent = String.format(runnerTemplate, timestamp, featureName, runnerName, featureName);

        // Ensure the runners directory exists
        Path runnersDir = Paths.get(RUNNERS_PATH);
        if (!Files.exists(runnersDir)) {
            Files.createDirectories(runnersDir);
        }

        // Write the runner file
        Path runnerPath = Paths.get(RUNNERS_PATH + runnerName + ".java");
        Files.writeString(runnerPath, runnerContent);

        logger.info("Runner class generated: {} for feature: {}", runnerName, featureName);
    }
    /**
     * Generate TestNG Suite configuration
     */
    private static void generateTestNGSuite(JsonNode jsonResponse) throws IOException {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String suiteName = "AI_TestSuite_" + timestamp;

        String testngContent = String.format("""
                <?xml version=1.0 encoding=UTF-8?>
                <!DOCTYPE suite SYSTEM http://testng.org/testng-1.0.dtd\">
                <suite name=\"%s\" parallel=\"tests\" thread-count=\"3\" verbose=\"2\">
                    <parameter name=\"browser\" value=\"chrome\"/>
                    <parameter name=\"environment\" value=\"qa\"/>
                    <parameter name=\"headless\" value=\"false\"/>
                    <listeners>
                        <listener class-name="reporting.ExtentReportManager"/>
                        <listener class-name=\"reporting.AllureReportManager\"/>
                    </listeners>
                    <test name=\"Smoke Tests\" parallel=\"methods\" thread-count=\"2\">
                        <parameter name=\"testType\" value=\"smoke\"/>
                        <groups>
                            <run>
                                <include name=\"smoke\"/>
                            </run>
                        </groups>
                        <classes>
                            <class name=\"runners.TestRunner_%s\"/>
                        </classes>
                    </test>
                    <test name=\"Regression Tests\" parallel=\"methods\" thread-count=\"3\">
                        <parameter name=\"testType\" value=\"regression\"/>
                        <groups>
                            <run>
                                <include name=\"regression\"/>
                            </run>
                        </groups>
                        <classes>
                            <class name=\"runners.TestRunner_%s\"/>
                        </classes>
                    </test>
                </suite>
                """, suiteName, timestamp, timestamp);

        Path testngPath = Paths.get(TESTNG_FILE);
        Files.write(testngPath, testngContent.getBytes());

        logger.info("TestNG suite generated: {}", TESTNG_FILE);
    }

    /**
     * Generate configuration properties file
     */
    private static void generatePropertiesFile() throws IOException {
        String propertiesContent = """
                # Test Configuration Properties
                # Browser Configuration
                browser=chrome
                headless=false
                implicit.wait=10
                explicit.wait=20
                page.load.timeout=30
                
                # Environment Configuration
                base.url=https://example.com
                api.base.url=https://api.example.com
                environment=qa
                
                # Database Configuration
                db.host=localhost
                db.port=5432
                db.name=testdb
                db.username=testuser
                db.password=testpass
                
                # Reporting Configuration
                extent.report.path=target/extent-reports/
                allure.results.directory=target/allure-results/
                screenshot.on.failure=true
                
                # Retry Configuration
                retry.count=2
                retry.enabled=true
                """;

        Path propertiesPath = Paths.get("src/test/resources/config/test.properties");
        Files.write(propertiesPath, propertiesContent.getBytes());

        logger.info("Properties file generated: test.properties");
    }

    /**
     * Generate base test class for common functionality
     */
    private static void generateBaseTestClass() throws IOException {
        String baseTestContent = """
                package base;
                
                import org.openqa.selenium.WebDriver;
                import org.openqa.selenium.chrome.ChromeDriver;
                import org.openqa.selenium.firefox.FirefoxDriver;
                import org.openqa.selenium.edge.EdgeDriver;
                import org.openqa.selenium.chrome.ChromeOptions;
                import org.openqa.selenium.firefox.FirefoxOptions;
                import org.openqa.selenium.support.ui.WebDriverWait;
                import org.testng.annotations.*;
                import io.github.bonigarcia.wdm.WebDriverManager;
                
                import java.time.Duration;
                import java.util.Properties;
                import java.io.FileInputStream;
                import java.io.IOException;
                
                /**
                 * Base Test Class providing common setup and teardown functionality
                 */
                public class BaseTest {
                    protected WebDriver driver;
                    protected WebDriverWait wait;
                    protected Properties properties;
                
                    @BeforeClass
                    public void setUp() throws IOException {
                        loadProperties();
                        initializeDriver();
                        configureBrowser();
                    }
                
                    @AfterClass
                    public void tearDown() {
                        if (driver != null) {
                            driver.quit();
                        }
                    }
                
                    private void loadProperties() throws IOException {
                        properties = new Properties();
                        try (FileInputStream fis = new FileInputStream("src/test/resources/config/test.properties")) {
                            properties.load(fis);
                        }
                    }
                
                    private void initializeDriver() {
                        String browser = properties.getProperty("browser", "chrome").toLowerCase();
                        boolean headless = Boolean.parseBoolean(properties.getProperty("headless", "false"));
                
                        switch (browser) {
                            case "chrome":
                
                                ChromeOptions chromeOptions = new ChromeOptions();
                                if (headless) chromeOptions.addArguments("--headless");
                                chromeOptions.addArguments("--no-sandbox", "--disable-dev-shm-usage");
                                driver = new ChromeDriver(chromeOptions);
                                break;
                            case "firefox":
                
                                FirefoxOptions firefoxOptions = new FirefoxOptions();
                                if (headless) firefoxOptions.addArguments("--headless");
                                driver = new FirefoxDriver(firefoxOptions);
                                break;
                            case "edge":
                
                                driver = new EdgeDriver();
                                break;
                            default:
                                throw new IllegalArgumentException("Browser not supported: " + browser);
                        }
                    }
                
                    private void configureBrowser() {
                        int implicitWait = Integer.parseInt(properties.getProperty("implicit.wait", "10"));
                        int explicitWait = Integer.parseInt(properties.getProperty("explicit.wait", "20"));
                        int pageLoadTimeout = Integer.parseInt(properties.getProperty("page.load.timeout", "30"));
                
                        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
                        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
                        driver.manage().window().maximize();
                
                        wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
                    }
                
                    protected String getProperty(String key) {
                        return properties.getProperty(key);
                    }
                }
                """;

        Path baseTestPath = Paths.get("src/test/java/base/BaseTest.java");
        Files.write(baseTestPath, baseTestContent.getBytes());

        logger.info("Base test class generated: BaseTest.java");
    }

    /**
     * Generate utility classes for common functions
     */
    private static void generateUtilityClasses() throws IOException {
        // Generate Screenshot Utility
        String screenshotUtilContent = """
                package utils;
                
                import org.apache.commons.io.FileUtils;
                import org.openqa.selenium.OutputType;
                import org.openqa.selenium.TakesScreenshot;
                import org.openqa.selenium.WebDriver;
                import org.slf4j.Logger;
                import org.slf4j.LoggerFactory;
                
                import java.io.File;
                import java.io.IOException;
                import java.time.LocalDateTime;
                import java.time.format.DateTimeFormatter;
                
                /**
                 * Utility class for capturing screenshots
                 */
                public class ScreenshotUtil {
                    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtil.class);
                
                    public static String captureScreenshot(WebDriver driver, String testName) {
                        try {
                            TakesScreenshot screenshot = (TakesScreenshot) driver;
                            File sourceFile = screenshot.getScreenshotAs(OutputType.FILE);
                
                            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                            String fileName = testName + "_" + timestamp + ".png";
                            String filePath = "target/screenshots/" + fileName;
                
                            File destFile = new File(filePath);
                            destFile.getParentFile().mkdirs();
                
                            FileUtils.copyFile(sourceFile, destFile);
                            logger.info("Screenshot captured: {}", filePath);
                
                            return filePath;
                        } catch (IOException e) {
                            logger.error("Failed to capture screenshot", e);
                            return null;
                        }
                    }
                }
                """;

        Path screenshotPath = Paths.get("src/test/java/utils/ScreenshotUtil.java");
        Files.write(screenshotPath, screenshotUtilContent.getBytes());

        // Generate Test Data Utility
        String testDataUtilContent = """
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
                """;

        Path testDataPath = Paths.get("src/test/java/utils/TestDataUtil.java");
        Files.write(testDataPath, testDataUtilContent.getBytes());

        logger.info("Utility classes generated successfully");
    }

    /**
     * Generate test files with custom configuration
     */
    public static void generateTestFiles(String aiResponse, String outputPath, boolean includeReports) {
        // Implementation for custom path and optional reports
        generateTestFiles(aiResponse);

        if (includeReports) {
            generateReportingConfiguration();
        }
    }

    /**
     * Generate reporting configuration files
     */
    private static void generateReportingConfiguration() {
        try {
            // Generate Extent Reports configuration
            String extentConfig = """
                    extent.reporter.spark.start=true
                    extent.reporter.spark.out=target/extent-reports/ExtentSpark.html
                    extent.reporter.spark.config=src/test/resources/config/extent-config.xml
                    
                    extent.reporter.json.start=true
                    extent.reporter.json.out=target/extent-reports/ExtentJson.json
                    
                    extent.reporter.pdf.start=true
                    extent.reporter.pdf.out=target/extent-reports/ExtentPdf.pdf
                    """;

            Path extentPath = Paths.get("src/test/resources/config/extent.properties");
            Files.write(extentPath, extentConfig.getBytes());

            logger.info("Reporting configuration generated successfully");

        } catch (IOException e) {
            logger.error("Failed to generate reporting configuration", e);
        }
    }
}

