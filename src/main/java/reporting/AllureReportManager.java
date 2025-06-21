package reporting;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

public class AllureReportManager {
    private static final Logger logger = LoggerFactory.getLogger(AllureReportManager.class);

    public static void addStep(String stepName, Status status, String description) {
        Allure.step(stepName, () -> {
            if (description != null && !description.isEmpty()) {
                Allure.addAttachment("Step Details", description);
            }
        });
        logger.info("Allure step added: {} - {}", stepName, status);
    }

    public static void addAttachment(String name, String content) {
        Allure.addAttachment(name, content);
        logger.info("Allure attachment added: {}", name);
    }

    public static void addScreenshot(byte[] screenshot) {
        Allure.addAttachment("Screenshot", "image/png",
                new ByteArrayInputStream(screenshot), ".png");
        logger.info("Screenshot attached to Allure report");
    }

    public static void addEnvironmentInfo(String key, String value) {
        System.setProperty("allure.results.directory", "target/allure-results");
        logger.info("Environment info added: {} = {}", key, value);
    }
}