package hooks;


import core.BaseTest;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reporting.AllureReportManager;
import reporting.ExtentReportManager;


public class TestHooks extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(TestHooks.class);

    @Before
    public void beforeScenario(Scenario scenario) {
        logger.info("Starting scenario: {}", scenario.getName());
        ExtentReportManager.createTest(scenario.getName(), "AI Generated Test Scenario");
        AllureReportManager.addStep("Scenario Started",
                io.qameta.allure.model.Status.PASSED,
                "Scenario: " + scenario.getName());
    }

    @After
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            byte[] screenshot = takeScreenshot(scenario.getName());
            ExtentReportManager.attachScreenshot("data:image/png;base64," +
                    java.util.Base64.getEncoder().encodeToString(screenshot));
            AllureReportManager.addScreenshot(screenshot);
            ExtentReportManager.logFail("Scenario failed: " + scenario.getName());
        } else {
            ExtentReportManager.logPass("Scenario passed: " + scenario.getName());
        }

        logger.info("Completed scenario: {}", scenario.getName());
    }

    public byte[] takeScreenshot(String scenarioName) {
        // TODO: Implement screenshot logic or import from utility
        return new byte[0];
    }
}
