package ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Main entry point for processing AI prompts and generating automated tests
 * This class orchestrates the entire workflow from prompt processing to test execution
 */
public class PromptProcessor {
    private static final Logger logger = LoggerFactory.getLogger(PromptProcessor.class);

    // Configuration constants
    private static final String CONFIG_FILE = "application.properties";
    private static final String DEFAULT_REPORTS_DIR = "target/cucumber-reports";
    private static final String DEFAULT_MAVEN_CMD = "mvn";
    private static final int DEFAULT_TIMEOUT_MINUTES = 10;

    private static Properties config;

    static {
        loadConfiguration();
    }

    /**
     * Load configuration from properties file
     */
    private static void loadConfiguration() {
        config = new Properties();
        try (InputStream input = PromptProcessor.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                config.load(input);
                logger.info("Configuration loaded from {}", CONFIG_FILE);
            } else {
                logger.warn("Configuration file {} not found, using defaults", CONFIG_FILE);
            }
        } catch (IOException e) {
            logger.warn("Error loading configuration file: {}", e.getMessage());
        }
    }

    /**
     * Get configuration value with default fallback
     */
    private static String getConfigValue(String key, String defaultValue) {
        return config.getProperty(key, defaultValue);
    }

    /**
     * Main method to process AI prompts and generate tests
     * @param prompt The test scenario prompt to process
     */
    public static void processPrompt(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            logger.error("Prompt cannot be null or empty");
            throw new IllegalArgumentException("Prompt is required");
        }

        logger.info("Starting prompt processing workflow");
        logger.debug("Processing prompt: {}", prompt);

        try {
            // Step 1: Validate prerequisites
            validatePrerequisites();

            // Step 2: Process prompt with AI
            logger.info("Step 1: Processing prompt with AI client");
            String aiResponse = GroqAIClient.processPrompt(prompt);

            if (aiResponse == null || aiResponse.trim().isEmpty()) {
                throw new RuntimeException("AI client returned empty response");
            }

            logger.info("AI response received successfully (length: {})", aiResponse.length());

            // Step 3: Generate test files
            logger.info("Step 2: Generating test files from AI response");
            TestGenerator.generateTestFiles(aiResponse);

            // Step 4: Execute tests
            logger.info("Step 3: Executing generated tests");
            executeTests();

            logger.info("Prompt processing workflow completed successfully");

        } catch (Exception e) {
            logger.error("Error in prompt processing workflow: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process prompt: " + e.getMessage(), e);
        }
    }

    /**
     * Validate system prerequisites before processing
     */
    private static void validatePrerequisites() {
        logger.debug("Validating system prerequisites");

        // Check if Maven is available
        String mavenCmd = getMavenCommand();
        try {
            ProcessBuilder pb = new ProcessBuilder(mavenCmd, "--version");
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("Maven is not properly configured or accessible");
            }
        } catch (Exception e) {
            logger.warn("Could not verify Maven installation: {}", e.getMessage());
        }

        // Ensure project structure exists
        createDirectoryStructure();
    }

    /**
     * Create necessary directory structure
     */
    private static void createDirectoryStructure() {
        String[] directories = {
                "src/test/java",
                "src/test/resources/features",
                "target",
                getReportsDirectory()
        };

        for (String dir : directories) {
            Path dirPath = Paths.get(dir);
            try {
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                    logger.debug("Created directory: {}", dir);
                }
            } catch (IOException e) {
                logger.warn("Could not create directory {}: {}", dir, e.getMessage());
            }
        }
    }

    /**
     * Execute the generated tests using Maven
     */
//    private static void executeTests() {
//        try {
//            // Prepare reports directory
//            prepareReportsDirectory();
//
//            // Build Maven command
//            String mavenCmd = getMavenCommand();
//            String[] command = buildMavenCommand(mavenCmd);
//
//            logger.info("Executing command: {}", String.join(" ", command));
//
//            // Execute Maven command
//            ProcessBuilder pb = new ProcessBuilder(command);
//            pb.directory(new File("."));
//            pb.inheritIO();
//
//            // Set environment variables if needed
//            pb.environment().put("MAVEN_OPTS", "-Xmx1024m");
//
//            Process process = pb.start();
//
//            // Wait for completion with timeout
//            int timeoutMinutes = Integer.parseInt(getConfigValue("test.timeout.minutes", String.valueOf(DEFAULT_TIMEOUT_MINUTES)));
//            boolean completed = process.waitFor(timeoutMinutes, TimeUnit.MINUTES);
//
//            if (!completed) {
//                logger.error("Test execution timed out after {} minutes", timeoutMinutes);
//                process.destroyForcibly();
//                throw new RuntimeException("Test execution timed out");
//            }
//
//            int exitCode = process.exitValue();
//
//            if (exitCode == 0) {
//                logger.info("Tests executed successfully");
//                logTestResults();
//            } else {
//                logger.error("Test execution failed with exit code: {}", exitCode);
//                throw new RuntimeException("Test execution failed with exit code: " + exitCode);
//            }
//
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            logger.error("Test execution was interrupted", e);
//            throw new RuntimeException("Test execution was interrupted", e);
//        } catch (Exception e) {
//            logger.error("Error executing tests: {}", e.getMessage(), e);
//            throw new RuntimeException("Failed to execute tests", e);
//        }
//    }


    private static void executeTests() {
        Process process = null;
        try {
            // 1. Setup reporting directory
            prepareReportsDirectory();

            // 2. Build Maven command
            String mavenCmd = getMavenCommand();
            String[] command = buildMavenCommand(mavenCmd);
            logger.info("Executing command: {}", String.join(" ", command));

            // 3. Configure process builder
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(".").getAbsoluteFile());  // Use absolute path
            pb.redirectErrorStream(true);  // Merge stdout/stderr

            // 4. Set environment variables
            Map<String, String> env = pb.environment();
            env.put("MAVEN_OPTS", "-Xmx1024m -XX:+UseG1GC");

            // 5. Start process
            process = pb.start();

            // 6. Handle process output with threading
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info("[MAVEN] {}", line);
                }
            }

            // 7. Configure timeout
            int timeoutMinutes = getConfiguredTimeout();
            boolean completed = process.waitFor(timeoutMinutes, TimeUnit.MINUTES);

            // 8. Handle timeout
            if (!completed) {
                logger.error("Test execution timed out after {} minutes", timeoutMinutes);
                terminateProcess(process);
                throw new TestExecutionException("Test execution timed out");
            }

            // 9. Check exit status
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                logger.error("Tests failed with exit code: {}", exitCode);
                throw new TestExecutionException("Test execution failed with code: " + exitCode);
            }

            logger.info("Tests executed successfully");
            logTestResults();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Test execution interrupted", e);
            terminateProcess(process);
            throw new TestExecutionException("Execution interrupted", e);
        } catch (IOException e) {
            logger.error("I/O error during test execution", e);
            terminateProcess(process);
            throw new TestExecutionException("I/O failure", e);
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            terminateProcess(process);
            throw new TestExecutionException("Unexpected failure", e);
        }
    }

    // Helper methods
    private static int getConfiguredTimeout() {
        try {
            return Integer.parseInt(getConfigValue("test.timeout.minutes",
                    String.valueOf(DEFAULT_TIMEOUT_MINUTES)));
        } catch (NumberFormatException e) {
            logger.warn("Invalid timeout configuration. Using default: {} minutes",
                    DEFAULT_TIMEOUT_MINUTES);
            return DEFAULT_TIMEOUT_MINUTES;
        }
    }

    private static void terminateProcess(Process process) {
        if (process == null) return;

        try {
            if (process.isAlive()) {
                process.destroyForcibly().waitFor(5, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            logger.warn("Error terminating process: {}", e.getMessage());
        }
    }

    // Custom exception
    private static class TestExecutionException extends RuntimeException {
        public TestExecutionException(String message) { super(message); }
        public TestExecutionException(String message, Throwable cause) { super(message, cause); }
    }

    /**
     * Build Maven command based on configuration
     */
    private static String[] buildMavenCommand(String mavenCmd) {
        String testProfile = getConfigValue("test.profile", "");
        String additionalArgs = getConfigValue("maven.additional.args", "");

        if (!testProfile.isEmpty()) {
            return new String[]{mavenCmd, "clean", "test", "-P" + testProfile};
        } else if (!additionalArgs.isEmpty()) {
            String[] baseCmd = {mavenCmd, "clean", "test"};
            String[] additionalArgsArray = additionalArgs.split("\\s+");
            String[] fullCmd = new String[baseCmd.length + additionalArgsArray.length];
            System.arraycopy(baseCmd, 0, fullCmd, 0, baseCmd.length);
            System.arraycopy(additionalArgsArray, 0, fullCmd, baseCmd.length, additionalArgsArray.length);
            return fullCmd;
        } else {
            return new String[]{mavenCmd, "clean", "test"};
        }
    }

    /**
     * Get the appropriate Maven command based on OS
     */
    private static String getMavenCommand() {
        String configuredMaven = getConfigValue("maven.command", "");
        if (!configuredMaven.isEmpty()) {
            return configuredMaven;
        }

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            // Try different common Windows Maven locations
            String[] windowsPaths = {
                    "C:\\Program Files\\Apache\\maven\\bin\\mvn.cmd",
                    "C:\\Program Files\\Maven\\bin\\mvn.cmd",
                    "mvn.cmd",
                    "mvn"
            };

            for (String path : windowsPaths) {
                if (isCommandAvailable(path)) {
                    return path;
                }
            }
        }

        return DEFAULT_MAVEN_CMD;
    }

    /**
     * Check if a command is available in the system
     */
    private static boolean isCommandAvailable(String command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command, "--version");
            Process process = pb.start();
            return process.waitFor(5, TimeUnit.SECONDS) && process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Prepare the reports directory
     */
    private static void prepareReportsDirectory() throws IOException {
        String reportsDir = getReportsDirectory();
        File cucumberReports = new File(reportsDir);

        // If it exists as a file, delete it
        if (cucumberReports.exists() && cucumberReports.isFile()) {
            boolean deleted = cucumberReports.delete();
            if (!deleted) {
                logger.warn("Could not delete existing reports file: {}", reportsDir);
            }
        }

        // Create directory if it doesn't exist
        if (!cucumberReports.exists()) {
            boolean created = cucumberReports.mkdirs();
            if (created) {
                logger.debug("Created reports directory: {}", reportsDir);
            } else {
                logger.warn("Could not create reports directory: {}", reportsDir);
            }
        }
    }

    /**
     * Get the reports directory from configuration
     */
    private static String getReportsDirectory() {
        return getConfigValue("reports.directory", DEFAULT_REPORTS_DIR);
    }

    /**
     * Log test results summary
     */
    private static void logTestResults() {
        String reportsDir = getReportsDirectory();
        File reportsDirectory = new File(reportsDir);

        if (reportsDirectory.exists() && reportsDirectory.isDirectory()) {
            File[] reportFiles = reportsDirectory.listFiles();
            if (reportFiles != null && reportFiles.length > 0) {
                logger.info("Test reports generated in: {}", reportsDir);
                logger.info("Number of report files: {}", reportFiles.length);
            } else {
                logger.warn("No report files found in: {}", reportsDir);
            }
        } else {
            logger.warn("Reports directory not found: {}", reportsDir);
        }
    }

    /**
     * Main entry point for the application
     */
    public static void main(String[] args) {
        logger.info("Starting PromptProcessor application");

        try {
            String prompt;

            // Use command line argument if provided
            if (args.length > 0) {
                prompt = String.join(" ", args);
                logger.info("Using prompt from command line arguments");
            } else {
                // Default test scenarios
                prompt = getDefaultPrompt();
                logger.info("Using default test scenarios");
            }

            // Process the prompt
            processPrompt(prompt);

            logger.info("Application completed successfully");
            System.exit(0);

        } catch (Exception e) {
            logger.error("Application failed: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    /**
     * Get default test prompt scenarios
     */
    private static String getDefaultPrompt() {
        return """
            Scenario 1: OrangeHRM Login Test
            Go to https://opensource-demo.orangehrmlive.com/web/index.php/auth/login
            Enter Username 'Admin'
            Enter Password 'admin123'
            Click the login button
            Click on the user menu
            Click on the logout
            Close the browser
           """;
    }

    /**
     * Process prompt with additional validation and error handling
     * This method can be used for testing or when more control is needed
     */
    public static ProcessResult processPromptWithResult(String prompt) {
        long startTime = System.currentTimeMillis();
        ProcessResult result = new ProcessResult();

        try {
            processPrompt(prompt);
            result.setSuccess(true);
            result.setMessage("Prompt processed successfully");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            result.setException(e);
        } finally {
            result.setExecutionTime(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    /**
     * Result class for tracking processing outcomes
     */
    public static class ProcessResult {
        private boolean success;
        private String message;
        private Exception exception;
        private long executionTime;

        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public Exception getException() { return exception; }
        public void setException(Exception exception) { this.exception = exception; }

        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }

        @Override
        public String toString() {
            return String.format("ProcessResult{success=%s, message='%s', executionTime=%dms}",
                    success, message, executionTime);
        }
    }
}