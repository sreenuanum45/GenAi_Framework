package utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElementUtils {
    private static final Logger logger = LoggerFactory.getLogger(ElementUtils.class);

    public static void scrollToElement(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
        logger.info("Scrolled to element");
    }

    public static void highlightElement(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.border='3px solid red'", element);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        js.executeScript("arguments[0].style.border=''", element);
        logger.info("Element highlighted");
    }

    public static void clickByJavaScript(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", element);
        logger.info("Clicked element using JavaScript");
    }

    public static void enterTextByJavaScript(WebDriver driver, WebElement element, String text) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value=arguments[1];", element, text);
        logger.info("Text entered using JavaScript: {}", text);
    }

    public static String getElementAttribute(WebElement element, String attribute) {
        String value = element.getAttribute(attribute);
        logger.info("Retrieved attribute '{}': {}", attribute, value);
        return value;
    }

    public static boolean isElementEnabled(WebElement element) {
        boolean enabled = element.isEnabled();
        logger.info("Element enabled status: {}", enabled);
        return enabled;
    }

    public static boolean isElementSelected(WebElement element) {
        boolean selected = element.isSelected();
        logger.info("Element selected status: {}", selected);
        return selected;
    }
}
