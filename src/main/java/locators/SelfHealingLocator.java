package locators;// 4. SelfHealingLocator.java


import core.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.WaitUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class SelfHealingLocator {
    private static final Logger logger = LoggerFactory.getLogger(SelfHealingLocator.class);

    public static WebElement findElement(WebDriver driver, String elementName, String... locatorStrategies) {
        for (String strategy : locatorStrategies) {
            try {
                By locator = parseLocator(strategy);
                WebElement element = driver.findElement(locator);
                if (element.isDisplayed()) {
                    logger.info("Element '{}' found using strategy: {}", elementName, strategy);
                    return element;
                }
            } catch (Exception e) {
                logger.debug("Strategy '{}' failed for element '{}'", strategy, elementName);
            }
        }

        // Fallback to intelligent locator generation
        return findElementByIntelligentSearch(driver, elementName);
    }

    private static By parseLocator(String strategy) {
        if (strategy.startsWith("id=")) {
            return By.id(strategy.substring(3));
        } else if (strategy.startsWith("name=")) {
            return By.name(strategy.substring(5));
        } else if (strategy.startsWith("class=")) {
            return By.className(strategy.substring(6));
        } else if (strategy.startsWith("xpath=")) {
            return By.xpath(strategy.substring(6));
        } else if (strategy.startsWith("css=")) {
            return By.cssSelector(strategy.substring(4));
        } else if (strategy.startsWith("linkText=")) {
            return By.linkText(strategy.substring(9));
        } else if (strategy.startsWith("partialLinkText=")) {
            return By.partialLinkText(strategy.substring(16));
        } else if (strategy.startsWith("tagName=")) {
            return By.tagName(strategy.substring(8));
        }

        // Default to xpath
        return By.xpath(strategy);
    }

    private static WebElement findElementByIntelligentSearch(WebDriver driver, String elementName) {
        List<String> commonStrategies = Arrays.asList(
                "//button[contains(text(),'" + elementName + "')]",
                "//input[@placeholder='" + elementName + "']",
                "//label[contains(text(),'" + elementName + "')]/following-sibling::input",
                "//*[@title='" + elementName + "']",
                "//*[@alt='" + elementName + "']",
                "//*[contains(@class,'" + elementName.toLowerCase() + "')]",
                "//*[@data-testid='" + elementName.toLowerCase() + "']"
        );

        for (String strategy : commonStrategies) {
            try {
                WebElement element = driver.findElement(By.xpath(strategy));
                if (element.isDisplayed()) {
                    logger.info("Element '{}' found using intelligent search: {}", elementName, strategy);
                    return element;
                }
            } catch (Exception e) {
                // Continue to next strategy
            }
        }

        throw new RuntimeException("Unable to locate element: " + elementName);
    }

    public static List<WebElement> findElements(WebDriver driver, String elementName, String[] locators) {
        for (String locator : locators) {
            try {
                By by = parseLocator(locator);
                List<WebElement> elements = driver.findElements(by);
                if (!elements.isEmpty()) {
                    logger.info("Elements for '{}' found using locator: {}", elementName, locator);
                    return elements;
                }
            } catch (Exception e) {
                logger.debug("Locator '{}' failed for element '{}'", locator, elementName);
            }
        }

        // Fallback to intelligent locator generation
        return findElementsByIntelligentSearch(driver, elementName);
    }

    private static List<WebElement> findElementsByIntelligentSearch(WebDriver driver, String elementName) {
        List<String> commonStrategies = Arrays.asList(
                "//button[contains(text(),'" + elementName + "')]",
                "//input[@placeholder='" + elementName + "']",
                "//label[contains(text(),'" + elementName + "')]/following-sibling::input",
                "//*[@title='" + elementName + "']",
                "//*[@alt='" + elementName + "']",
                "//*[contains(@class,'" + elementName.toLowerCase() + "')]",
                "//*[@data-testid='" + elementName.toLowerCase() + "']"
        );

        for (String strategy : commonStrategies) {
            try {
                List<WebElement> elements = driver.findElements(By.xpath(strategy));
                if (!elements.isEmpty()) {
                    logger.info("Elements for '{}' found using intelligent search: {}", elementName, strategy);
                    return elements;
                }
            } catch (Exception e) {
                // Continue to next strategy
            }
        }

        throw new RuntimeException("Unable to locate elements: " + elementName);
    }

    public WebElement findElementWithHealing(String elementName, LocatorStrategy[] strategies) {
        WebDriver driver = DriverManager.getDriver();
        for (LocatorStrategy strategy : strategies) {
            try {
                WebElement element = findElement(driver, elementName, strategy.getLocators());
                if (element != null) {
                    return element;
                }
            } catch (Exception e) {
                logger.warn("Failed to find element '{}' using strategy '{}'", elementName, strategy);
            }
        }

        throw new RuntimeException("Unable to locate element: " + elementName);
    }

    public List<WebElement> findElementsWithHealing(String elementName, LocatorStrategy strategy) {
        WebDriver driver = DriverManager.getDriver();
        try {
            List<WebElement> elements = driver.findElements(By.xpath(strategy.getLocator()));
            if (elements.isEmpty()) {
                throw new RuntimeException("No elements found for: " + elementName);
            }
            return elements;
        } catch (Exception e) {
            logger.error("Failed to find elements for '{}': {}", elementName, e.getMessage());
            throw new RuntimeException("Unable to locate elements: " + elementName, e);
        }
    }

    public WebElement waitForClickableElementWithHealing(String elementName, LocatorStrategy strategy, Duration timeout) {
        WebDriver driver = DriverManager.getDriver();
        try {
            WebElement element = findElementWithHealing(elementName, new LocatorStrategy[]{strategy});
            WaitUtils.waitForElementToBeClickable(element);
            return element;
        } catch (Exception e) {
            logger.error("Failed to wait for clickable element '{}': {}", elementName, e.getMessage());
            throw new RuntimeException("Unable to wait for clickable element: " + elementName, e);
        }
    }

    public WebElement waitForElementWithHealing(String elementName, LocatorStrategy strategy, Duration timeout) {
        WebDriver driver = DriverManager.getDriver();
        try {
            WebElement element = findElementWithHealing(elementName, new LocatorStrategy[]{strategy});
            WaitUtils.waitForElementToBeVisible(element);
            return element;
        } catch (Exception e) {
            logger.error("Failed to wait for element '{}': {}", elementName, e.getMessage());
            throw new RuntimeException("Unable to wait for element: " + elementName, e);
        }
    }
}