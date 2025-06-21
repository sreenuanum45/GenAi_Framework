package locators;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.WaitUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ElementLocator - Advanced element location with self-healing capabilities
 * Provides intelligent element discovery and fallback mechanisms
 */
public class ElementLocator {
    private static final Logger logger = LoggerFactory.getLogger(ElementLocator.class);

    private  WebDriver driver;
    private final WebDriverWait wait;
    private final SelfHealingLocator selfHealingLocator;
    private final Map<String, By> locatorCache;
    private final Duration defaultTimeout;

    public ElementLocator(WebDriver driver) {
        this.driver = driver;
        this.defaultTimeout = Duration.ofSeconds(10);
        this.wait = new WebDriverWait(driver, defaultTimeout);
        this.selfHealingLocator = new SelfHealingLocator();
        this.locatorCache = new HashMap<>();
    }

    public ElementLocator(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.defaultTimeout = timeout;
        this.wait = new WebDriverWait(driver, timeout);
        this.selfHealingLocator = new SelfHealingLocator();
        this.locatorCache = new HashMap<>();
    }

    /**
     * Find element using multiple locator strategies with self-healing
     */
    public WebElement findElement(String elementName, LocatorStrategy... strategies) {
        logger.debug("Attempting to find element: {}", elementName);

        // Check cache first
        if (locatorCache.containsKey(elementName)) {
            try {
                WebElement element = driver.findElement(locatorCache.get(elementName));
                if (element.isDisplayed()) {
                    logger.debug("Element found using cached locator: {}", elementName);
                    return element;
                }
            } catch (Exception e) {
                logger.warn("Cached locator failed for element: {}. Removing from cache.", elementName);
                locatorCache.remove(elementName);
            }
        }

        // Try each strategy
        for (LocatorStrategy strategy : strategies) {
            try {
                WebElement element = findElementByStrategy(strategy);
                if (element != null && element.isDisplayed()) {
                    // Cache successful locator
                    locatorCache.put(elementName, strategy.getBy());
                    logger.debug("Element found using strategy: {} for element: {}",
                            strategy.getStrategyName(), elementName);
                    return element;
                }
            } catch (Exception e) {
                logger.debug("Strategy {} failed for element {}: {}",
                        strategy.getStrategyName(), elementName, e.getMessage());
            }
        }

        // If all strategies fail, try self-healing
        logger.warn("All primary strategies failed for element: {}. Attempting self-healing...", elementName);
        WebElement healedElement = selfHealingLocator.findElementWithHealing(elementName, strategies);

        if (healedElement != null) {
            logger.info("Element found using self-healing mechanism: {}", elementName);
            return healedElement;
        }

        throw new RuntimeException("Unable to locate element: " + elementName +
                " using any of the provided strategies or self-healing");
    }

    /**
     * Find multiple elements using locator strategies
     */
    public List<WebElement> findElements(String elementName, LocatorStrategy strategy) {
        logger.debug("Attempting to find elements: {}", elementName);

        try {
            List<WebElement> elements = driver.findElements(strategy.getBy());
            if (!elements.isEmpty()) {
                logger.debug("Found {} elements for: {}", elements.size(), elementName);
                return elements;
            }
        } catch (Exception e) {
            logger.debug("Strategy {} failed for elements {}: {}",
                    strategy.getStrategyName(), elementName, e.getMessage());
        }

        // Try self-healing for multiple elements
        List<WebElement> healedElements = selfHealingLocator.findElementsWithHealing(elementName, strategy);
        if (!healedElements.isEmpty()) {
            logger.info("Elements found using self-healing mechanism: {}", elementName);
            return healedElements;
        }

        return new ArrayList<>();
    }

    /**
     * Wait for element to be present and visible
     */
    public WebElement waitForElement(String elementName, LocatorStrategy strategy) {
        return waitForElement(elementName, strategy, defaultTimeout);
    }

    /**
     * Wait for element with custom timeout
     */
    public WebElement waitForElement(String elementName, LocatorStrategy strategy, Duration timeout) {
        logger.debug("Waiting for element: {} with timeout: {} seconds", elementName, timeout.getSeconds());

        WebDriverWait customWait = new WebDriverWait(driver, timeout);

        try {
            WebElement element = customWait.until(ExpectedConditions.presenceOfElementLocated(strategy.getBy()));
            customWait.until(ExpectedConditions.visibilityOf(element));

            // Cache successful locator
            locatorCache.put(elementName, strategy.getBy());
            logger.debug("Element found and visible: {}", elementName);
            return element;

        } catch (Exception e) {
            logger.warn("Wait failed for element: {}. Attempting self-healing...", elementName);

            // Try self-healing with wait
            WebElement healedElement = selfHealingLocator.waitForElementWithHealing(
                    elementName, strategy, timeout);

            if (healedElement != null) {
                logger.info("Element found using self-healing with wait: {}", elementName);
                return healedElement;
            }

            throw new RuntimeException("Element not found within timeout: " + elementName +
                    " (" + timeout.getSeconds() + " seconds)", e);
        }
    }

    /**
     * Wait for element to be clickable
     */
    public WebElement waitForClickableElement(String elementName, LocatorStrategy strategy) {
        return waitForClickableElement(elementName, strategy, defaultTimeout);
    }

    /**
     * Wait for element to be clickable with custom timeout
     */
    public WebElement waitForClickableElement(String elementName, LocatorStrategy strategy, Duration timeout) {
        logger.debug("Waiting for clickable element: {} with timeout: {} seconds",
                elementName, timeout.getSeconds());

        WebDriverWait customWait = new WebDriverWait(driver, timeout);

        try {
            WebElement element = customWait.until(ExpectedConditions.elementToBeClickable(strategy.getBy()));

            // Cache successful locator
            locatorCache.put(elementName, strategy.getBy());
            logger.debug("Clickable element found: {}", elementName);
            return element;

        } catch (Exception e) {
            logger.warn("Wait for clickable failed for element: {}. Attempting self-healing...", elementName);

            WebElement healedElement = selfHealingLocator.waitForClickableElementWithHealing(
                    elementName, strategy, timeout);

            if (healedElement != null) {
                logger.info("Clickable element found using self-healing: {}", elementName);
                return healedElement;
            }

            throw new RuntimeException("Clickable element not found within timeout: " + elementName +
                    " (" + timeout.getSeconds() + " seconds)", e);
        }
    }

    /**
     * Check if element exists without throwing exception
     */
    public boolean isElementPresent(String elementName, LocatorStrategy strategy) {
        try {
            driver.findElement(strategy.getBy());
            return true;
        } catch (Exception e) {
            logger.debug("Element not present: {}", elementName);
            return false;
        }
    }

    /**
     * Check if element is visible
     */
    public boolean isElementVisible(String elementName, LocatorStrategy strategy) {
        try {
            WebElement element = driver.findElement(strategy.getBy());
            return element.isDisplayed();
        } catch (Exception e) {
            logger.debug("Element not visible: {}", elementName);
            return false;
        }
    }

    /**
     * Get element text with fallback strategies
     */
    public String getElementText(String elementName, LocatorStrategy... strategies) {
        WebElement element = findElement(elementName, strategies);

        // Try different text extraction methods
        String text = element.getText();
        if (text == null || text.trim().isEmpty()) {
            text = element.getAttribute("textContent");
        }
        if (text == null || text.trim().isEmpty()) {
            text = element.getAttribute("innerText");
        }
        if (text == null || text.trim().isEmpty()) {
            text = element.getAttribute("value");
        }

        logger.debug("Retrieved text for element {}: {}", elementName, text);
        return text != null ? text.trim() : "";
    }

    /**
     * Get element attribute with error handling
     */
    public String getElementAttribute(String elementName, String attributeName, LocatorStrategy... strategies) {
        WebElement element = findElement(elementName, strategies);
        String attributeValue = element.getAttribute(attributeName);

        logger.debug("Retrieved attribute {} for element {}: {}", attributeName, elementName, attributeValue);
        return attributeValue;
    }

    /**
     * Clear locator cache
     */
    public void clearCache() {
        locatorCache.clear();
        logger.debug("Locator cache cleared");
    }

    /**
     * Get cache statistics
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("cacheSize", locatorCache.size());
        stats.put("cachedElements", new ArrayList<>(locatorCache.keySet()));
        return stats;
    }

    /**
     * Find element by specific strategy
     */
    private WebElement findElementByStrategy(LocatorStrategy strategy) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(strategy.getBy()));

        // Additional validation based on strategy requirements
        if (strategy.requiresVisibility() && !element.isDisplayed()) {
            wait.until(ExpectedConditions.visibilityOf(element));
        }

        return element;
    }

    /**
     * Enhanced element interaction with retry mechanism
     */
    public void performActionWithRetry(String elementName, Runnable action, LocatorStrategy... strategies) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                WebElement element = findElement(elementName, strategies);
                action.run();
                logger.debug("Action performed successfully on element: {} (attempt {})", elementName, attempt + 1);
                return;
            } catch (Exception e) {
                attempt++;
                logger.warn("Action failed on element: {} (attempt {}): {}", elementName, attempt, e.getMessage());

                if (attempt < maxRetries) {
                    WaitUtils.waitForSeconds(1); // Brief pause before retry
                } else {
                    throw new RuntimeException("Action failed after " + maxRetries + " attempts on element: " + elementName, e);
                }
            }
        }
    }
}