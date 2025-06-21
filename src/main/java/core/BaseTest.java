package core;

import core.DriverManager;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import reporting.ExtentReportManager;

import java.lang.reflect.Method;

public class BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    @BeforeSuite
    public void suiteSetup() {
        logger.info("Test suite execution started");
        reporting.ExtentReportManager.initializeExtentReports();
    }

    @BeforeMethod
    public void setUp(Method method) {
        String browserName = com.ai.automation.core.ConfigManager.getBrowser();
        DriverManager.setDriver(browserName);
        logger.info("Test method '{}' started with browser: {}", method.getName(), browserName);
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            takeScreenshot(result.getMethod().getMethodName());
            logger.error("Test '{}' failed", result.getMethod().getMethodName());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            logger.info("Test '{}' passed", result.getMethod().getMethodName());
        }

        DriverManager.quitDriver();
    }

    @AfterSuite
    public void suiteTearDown() {
        reporting.ExtentReportManager.flushReports();
        logger.info("Test suite execution completed");
    }

    @Attachment(value = "Screenshot", type = "image/png")
    public byte[] takeScreenshot(String testName) {
        try {
            TakesScreenshot screenshot = (TakesScreenshot) DriverManager.getDriver();
            byte[] screenshotBytes = screenshot.getScreenshotAs(OutputType.BYTES);
            logger.info("Screenshot captured for test: {}", testName);
            return screenshotBytes;
        } catch (Exception e) {
            logger.error("Failed to capture screenshot", e);
            return new byte[0];
        }
    }
}