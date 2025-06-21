package utils;

import com.ai.automation.core.ConfigManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class WaitUtils {
    private static WebDriverWait wait = null;
    private static final Logger logger = LoggerFactory.getLogger(WaitUtils.class);

    public WaitUtils(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getExplicitWait()));
    }

    public static void waitForSeconds(int i) {
        try {
            Thread.sleep(i * 1000);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted during wait", e);
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
    }

    public static void waitForElementToBeVisible(WebElement element) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            logger.warn("Element not visible within timeout");
            throw e;
        }
    }

    public static void waitForElementToBeClickable(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            logger.warn("Element not clickable within timeout");
            throw e;
        }
    }

    public void waitForPageTitle(String title) {
        try {
            wait.until(ExpectedConditions.titleContains(title));
        } catch (Exception e) {
            logger.warn("Page title '{}' not found within timeout", title);
            throw e;
        }
    }

    public void waitForUrlContains(String urlFragment) {
        try {
            wait.until(ExpectedConditions.urlContains(urlFragment));
        } catch (Exception e) {
            logger.warn("URL fragment '{}' not found within timeout", urlFragment);
            throw e;
        }
    }
}