package core;

import locators.SelfHealingLocator;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.Logs;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.WaitUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Abstract BasePage class providing common functionality for all page objects
 * Features include self-healing locators, comprehensive wait strategies,
 * and extensive element interaction methods
 */
public abstract class BasePage {
    protected WebDriver driver;
    protected WaitUtils waitUtils;
    protected WebDriverWait webDriverWait;
    protected Actions actions;
    protected JavascriptExecutor jsExecutor;

    private static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    private static final int DEFAULT_TIMEOUT = 10;
    private static final int POLLING_INTERVAL = 500;

    /**
     * Constructor to initialize BasePage
     * @param driver WebDriver instance
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
        this.webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
        logger.debug("BasePage initialized for: {}", this.getClass().getSimpleName());
    }

    // ==================== ELEMENT FINDING METHODS ====================

    /**
     * Find element using self-healing locator mechanism
     * @param elementName Descriptive name of the element
     * @param locators Array of locator strategies
     * @return WebElement found using self-healing mechanism
     */
    protected WebElement findElementWithHealing(String elementName, String... locators) {
        try {
            WebElement element = SelfHealingLocator.findElement(driver, elementName, locators);
            logger.debug("Successfully found element '{}' using self-healing locator", elementName);
            return element;
        } catch (Exception e) {
            logger.error("Failed to find element '{}' even with self-healing", elementName, e);
            throw new NoSuchElementException("Element '" + elementName + "' not found with any locator strategy");
        }
    }

    /**
     * Find multiple elements using self-healing locator mechanism
     * @param elementName Descriptive name of the elements
     * @param locators Array of locator strategies
     * @return List of WebElements
     */
    protected List<WebElement> findElementsWithHealing(String elementName, String... locators) {
        try {
            List<WebElement> elements = SelfHealingLocator.findElements(driver, elementName, locators);
            logger.debug("Successfully found {} elements for '{}'", elements.size(), elementName);
            return elements;
        } catch (Exception e) {
            logger.error("Failed to find elements '{}' even with self-healing", elementName, e);
            return new ArrayList<>();
        }
    }

    // ==================== CLICK OPERATIONS ====================

    /**
     * Click element with self-healing and wait
     * @param elementName Descriptive name of the element
     * @param locators Array of locator strategies
     */
    protected void clickElement(String elementName, String... locators) {
        try {
            WebElement element = findElementWithHealing(elementName, locators);
            waitUtils.waitForElementToBeClickable(element);

            // Scroll element into view before clicking
            scrollElementIntoView(element);
            element.click();

            logger.info("Successfully clicked on element: {}", elementName);
        } catch (ElementClickInterceptedException e) {
            logger.warn("Click intercepted for '{}', trying JavaScript click", elementName);
            jsClickElement(elementName, locators);
        } catch (Exception e) {
            logger.error("Failed to click element '{}'", elementName, e);
            throw new RuntimeException("Unable to click element: " + elementName, e);
        }
    }

    /**
     * Click element using JavaScript
     * @param elementName Descriptive name of the element
     * @param locators Array of locator strategies
     */
    protected void jsClickElement(String elementName, String... locators) {
        try {
            WebElement element = findElementWithHealing(elementName, locators);
            jsExecutor.executeScript("arguments[0].click();", element);
            logger.info("JavaScript clicked on element: {}", elementName);
        } catch (Exception e) {
            logger.error("Failed to JavaScript click element '{}'", elementName, e);
            throw new RuntimeException("Unable to JavaScript click element: " + elementName, e);
        }
    }

    /**
     * Double click on element
     * @param elementName Descriptive name of the element
     * @param locators Array of locator strategies
     */
    protected void doubleClickElement(String elementName, String... locators) {
        try {
            WebElement element = findElementWithHealing(elementName, locators);
            waitUtils.waitForElementToBeClickable(element);
            actions.doubleClick(element).perform();
            logger.info("Double clicked on element: {}", elementName);
        } catch (Exception e) {
            logger.error("Failed to double click element '{}'", elementName, e);
            throw new RuntimeException("Unable to double click element: " + elementName, e);
        }
    }

    /**
     * Right click on element
     * @param elementName Descriptive name of the element
     * @param locators Array of locator strategies
     */
    protected void rightClickElement(String elementName, String... locators) {
        try {
            WebElement element = findElementWithHealing(elementName, locators);
            waitUtils.waitForElementToBeClickable(element);
            actions.contextClick(element).perform();
            logger.info("Right clicked on element: {}", elementName);
        } catch (Exception e) {
            logger.error("Failed to right click element '{}'", elementName, e);
            throw new RuntimeException("Unable to right click element: " + elementName, e);
        }
    }

    // ==================== TEXT INPUT OPERATIONS ====================

    /**
     * Enter text in element with clearing existing text
     * @param text Text to enter
     * @param elementName Descriptive name of the element
     * @param locators Array of locator strategies
     */
    protected void enterText(String text, String elementName, String... locators) {
        try {
            WebElement element = findElementWithHealing(elementName, locators);
            waitUtils.waitForElementToBeVisible(element);

            // Clear field using multiple strategies
            clearField(element);
            element.sendKeys(text);

            // Verify text was entered correctly
            String enteredText = element.getAttribute("value");
            if (!text.equals(enteredText)) {
                logger.warn("Text verification failed for '{}'. Expected: '{}', Actual: '{}'",
                        elementName, text, enteredText);
            }

            logger.info("Successfully entered text '{}' in element: {}", text, elementName);
        } catch (Exception e) {
            logger.error("Failed to enter text in element '{}'", elementName, e);
            throw new RuntimeException("Unable to enter text in element: " + elementName, e);
        }
    }

    /**
     * Append text to existing text in element
     * @param text Text to append
     * @param elementName Descriptive name of the element
     * @param locators Array of locator strategies
     */
    protected void appendText(String text, String elementName, String... locators) {
        try {
            WebElement element = findElementWithHealing(elementName, locators);
            waitUtils.waitForElementToBeVisible(element);
            element.sendKeys(text);
            logger.info("Successfully appended text '{}' to element: {}", text, elementName);
        } catch (Exception e) {
            logger.error("Failed to append text to element '{}'", elementName, e);
            throw new RuntimeException("Unable to append text to element: " + elementName, e);
        }
    }

    /**
     * Clear field using multiple strategies
     * @param element WebElement to clear
     */
    private void clearField(WebElement element) {
        try {
            element.clear();

            // Backup clearing strategy
            if (!element.getAttribute("value").isEmpty()) {
                element.sendKeys(Keys.CONTROL + "a");
                element.sendKeys(Keys.DELETE);
            }
        } catch (Exception e) {
            logger.warn("Standard clear failed, using JavaScript clear", e);
            jsExecutor.executeScript("arguments[0].value = '';", element);
        }
    }

    // ==================== TEXT RETRIEVAL METHODS ====================

    /**
     * Get text content of element
     * @param elementName Descriptive name of the element
     * @param locators Array of locator strategies
     * @return Text content of the element
     */
    protected String getElementText(String elementName, String... locators) {
        try {
            WebElement element = findElementWithHealing(elementName, locators);
            waitUtils.waitForElementToBeVisible(element);

            String text = element.getText();

            // Fallback to innerText if getText() is empty
            if (text.isEmpty()) {
                text = element.getAttribute("innerText");
            }

            // Fallback to textContent if innerText is empty
            if (text.isEmpty()) {
                text = element.getAttribute("textContent");
            }

            logger.debug("Retrieved text '{}' from element: {}", text, elementName);
            return text.trim();
        } catch (Exception e) {
            logger.error("Failed to get text from element '{}'", elementName, e);
            return "";
        }
    }

    /**
     * Get attribute value of element
     * @param attributeName Name of the attribute
     * @param elementName Descriptive name of the element
     * @param locators Array of locator strategies
     * @return Attribute value
     */
    protected String getElementAttribute(String attributeName, String elementName, String... locators) {
        try {
            WebElement element = findElementWithHealing(elementName, locators);
            waitUtils.waitForElementToBeVisible(element);
            String attributeValue = element.getAttribute(attributeName);
            logger.debug("Retrieved attribute '{}' value '{}' from element: {}",
                    attributeName, attributeValue, elementName);
            return attributeValue;
        } catch (Exception e) {
            logger.error("Failed to get attribute '{}' from element '{}'", attributeName, elementName, e);
            return "";
        }
    }

    /**
     * Get CSS property value of element
     * @param propertyName Name of the CSS property
     * @param elementName Descriptive name of the element
     * @param locators Array of locator strategies
     * @return CSS property value
     */
    protected String getCssValue(String propertyName, String elementName, String... locators) {
        try {
            WebElement element = findElementWithHealing(elementName, locators);
            waitUtils.waitForElementToBeVisible(element);
            String cssValue = element.getCssValue(propertyName);
            logger.debug("Retrieved CSS property '{}' value '{}' from element: {}",
                    propertyName, cssValue, elementName);
            return cssValue;
        } catch (Exception e) {
            logger.error("Failed to get CSS property '{}' from element '{}'", propertyName, elementName, e);
            return "";
        }
    }

    // ==================== ELEMENT STATE VERIFICATION ====================

    /**
     * Check if element is displayed
     * @param elementName Descriptive name of the element
     * @param locators Array of locator strategies
     * @return true if element is displayed, false otherwise
     */
    protected boolean isElementDisplayed(String elementName, String... locators) {
        try {
            WebElement element = findElementWithHealing(elementName, locators);
            boolean displayed = element.isDisplayed();
            logger.debug("Element '{}' display status: {}", elementName, displayed);
            return displayed;
        } catch (Exception e) {
            logger.debug("Element '{}' not found or not displayed", elementName);
            return false;
        }
    }

    /**
     * Check if element is enabled
     * @param elementName Descriptive name of the element
     * @param locators Array of locator strategies
     * @return true if element is enabled, false otherwise
     */
    protected boolean isElementEnabled(String elementName, String... locators) {
        try {
            WebElement element = findElementWithHealing(elementName, locators);
            boolean enabled = element.isEnabled();
            logger.debug("Element '{}' enabled status: {}", elementName, enabled);
            return enabled;
        } catch (Exception e) {
            logger.debug("Element '{}' not found or not enabled", elementName);
            return false;
        }
    }

    /**
     * Check if element is selected (for checkboxes, radio buttons)
     * @param elementName Descriptive name of the element
     * @param locators Array of locator strategies
     * @return true if element is selected, false otherwise
     */
    protected boolean isElementSelected(String elementName, String... locators) {
        try {
            WebElement element = findElementWithHealing(elementName, locators);
            boolean selected = element.isSelected();
            logger.debug("Element '{}' selected status: {}", elementName, selected);
            return selected;
        } catch (Exception e) {
            logger.debug("Element '{}' not found or not selectable", elementName);
            return false;
        }
    }

    // ==================== DROPDOWN OPERATIONS ====================

    /**
     * Select dropdown option by visible text
     * @param optionText Visible text of the option
     * @param elementName Descriptive name of the dropdown
     * @param locators Array of locator strategies
     */
    protected void selectDropdownByText(String optionText, String elementName, String... locators) {
        try {
            WebElement dropdown = findElementWithHealing(elementName, locators);
            waitUtils.waitForElementToBeVisible(dropdown);
            Select select = new Select(dropdown);
            select.selectByVisibleText(optionText);
            logger.info("Selected option '{}' from dropdown: {}", optionText, elementName);
        } catch (Exception e) {
            logger.error("Failed to select option '{}' from dropdown '{}'", optionText, elementName, e);
            throw new RuntimeException("Unable to select dropdown option: " + optionText, e);
        }
    }

    /**
     * Select dropdown option by value
     * @param value Value attribute of the option
     * @param elementName Descriptive name of the dropdown
     * @param locators Array of locator strategies
     */
    protected void selectDropdownByValue(String value, String elementName, String... locators) {
        try {
            WebElement dropdown = findElementWithHealing(elementName, locators);
            waitUtils.waitForElementToBeVisible(dropdown);
            Select select = new Select(dropdown);
            select.selectByValue(value);
            logger.info("Selected option with value '{}' from dropdown: {}", value, elementName);
        } catch (Exception e) {
            logger.error("Failed to select option with value '{}' from dropdown '{}'", value, elementName, e);
            throw new RuntimeException("Unable to select dropdown option by value: " + value, e);
        }
    }

    /**
     * Select dropdown option by index
     * @param index Index of the option (0-based)
     * @param elementName Descriptive name of the dropdown
     * @param locators Array of locator strategies
     */
    protected void selectDropdownByIndex(int index, String elementName, String... locators) {
        try {
            WebElement dropdown = findElementWithHealing(elementName, locators);
            waitUtils.waitForElementToBeVisible(dropdown);
            Select select = new Select(dropdown);
            select.selectByIndex(index);
            logger.info("Selected option at index '{}' from dropdown: {}", index, elementName);
        } catch (Exception e) {
            logger.error("Failed to select option at index '{}' from dropdown '{}'", index, elementName, e);
            throw new RuntimeException("Unable to select dropdown option by index: " + index, e);
        }
    }

    /**
     * Get all dropdown options
     * @param elementName Descriptive name of the dropdown
     * @param locators Array of locator strategies
     * @return List of option texts
     */
    protected List<String> getDropdownOptions(String elementName, String... locators) {
        try {
            WebElement dropdown = findElementWithHealing(elementName, locators);
            waitUtils.waitForElementToBeVisible(dropdown);
            Select select = new Select(dropdown);
            List<WebElement> options = select.getOptions();

            List<String> optionTexts = new ArrayList<>();
            for (WebElement option : options) {
                optionTexts.add(option.getText());
            }

            logger.debug("Retrieved {} options from dropdown: {}", optionTexts.size(), elementName);
            return optionTexts;
        } catch (Exception e) {
            logger.error("Failed to get options from dropdown '{}'", elementName, e);
            return new ArrayList<>();
        }
    }

    // ==================== NAVIGATION METHODS ====================

    /**
     * Navigate to specified URL
     * @param url URL to navigate to
     */
    protected void navigateToUrl(String url) {
        try {
            driver.get(url);
            waitForPageLoad();
            logger.info("Successfully navigated to URL: {}", url);
        } catch (Exception e) {
            logger.error("Failed to navigate to URL: {}", url, e);
            throw new RuntimeException("Unable to navigate to URL: " + url, e);
        }
    }

    /**
     * Get current URL
     * @return Current URL
     */
    protected String getCurrentUrl() {
        try {
            String currentUrl = driver.getCurrentUrl();
            logger.debug("Current URL: {}", currentUrl);
            return currentUrl;
        } catch (Exception e) {
            logger.error("Failed to get current URL", e);
            return "";
        }
    }

    /**
     * Get page title
     * @return Page title
     */
    protected String getPageTitle() {
        try {
            String title = driver.getTitle();
            logger.debug("Page title: {}", title);
            return title;
        } catch (Exception e) {
            logger.error("Failed to get page title", e);
            return "";
        }
    }

    /**
     * Refresh current page
     */
    protected void refreshPage() {
        try {
            driver.navigate().refresh();
            waitForPageLoad();
            logger.info("Page refreshed successfully");
        } catch (Exception e) {
            logger.error("Failed to refresh page", e);
            throw new RuntimeException("Unable to refresh page", e);
        }
    }

    /**
     * Navigate back in browser history
     */
    protected void navigateBack() {
        try {
            driver.navigate().back();
            waitForPageLoad();
            logger.info("Navigated back successfully");
        } catch (Exception e) {
            logger.error("Failed to navigate back", e);
            throw new RuntimeException("Unable to navigate back", e);
        }
    }

    /**
     * Navigate forward in browser history
     */
    protected void navigateForward() {
        try {
            driver.navigate().forward();
            waitForPageLoad();
            logger.info("Navigated forward successfully");
        } catch (Exception e) {
            logger.error("Failed to navigate forward", e);
            throw new RuntimeException("Unable to navigate forward", e);
        }
    }

    // ==================== PAGE VERIFICATION METHODS ====================

    /**
     * Verify page contains expected text
     * @param expectedText Text to search for
     * @return true if page contains the text, false otherwise
     */
    protected boolean verifyPageContains(String expectedText) {
        try {
            String pageSource = driver.getPageSource();
            boolean contains = pageSource.contains(expectedText);
            logger.info("Page contains '{}': {}", expectedText, contains);
            return contains;
        } catch (Exception e) {
            logger.error("Failed to verify page contains text '{}'", expectedText, e);
            return false;
        }
    }

    /**
     * Verify current URL contains expected text
     * @param expectedUrlPart URL part to verify
     * @return true if URL contains the expected part, false otherwise
     */
    protected boolean verifyUrlContains(String expectedUrlPart) {
        try {
            String currentUrl = getCurrentUrl();
            boolean contains = currentUrl.contains(expectedUrlPart);
            logger.info("URL contains '{}': {}", expectedUrlPart, contains);
            return contains;
        } catch (Exception e) {
            logger.error("Failed to verify URL contains '{}'", expectedUrlPart, e);
            return false;
        }
    }

    /**
     * Verify page title contains expected text
     * @param expectedTitlePart Title part to verify
     * @return true if title contains the expected part, false otherwise
     */
    protected boolean verifyTitleContains(String expectedTitlePart) {
        try {
            String title = getPageTitle();
            boolean contains = title.contains(expectedTitlePart);
            logger.info("Title contains '{}': {}", expectedTitlePart, contains);
            return contains;
        } catch (Exception e) {
            logger.error("Failed to verify title contains '{}'", expectedTitlePart, e);
            return false;
        }
    }

    // ==================== SCROLLING METHODS ====================

    /**
     * Scroll element into view
     * @param element WebElement to scroll into view
     */
    protected void scrollElementIntoView(WebElement element) {
        try {
            jsExecutor.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            Thread.sleep(500); // Allow smooth scrolling to complete
            logger.debug("Scrolled element into view");
        } catch (Exception e) {
            logger.warn("Failed to scroll element into view", e);
        }
    }

    /**
     * Scroll to top of page
     */
    protected void scrollToTop() {
        try {
            jsExecutor.executeScript("window.scrollTo({top: 0, behavior: 'smooth'});");
            logger.debug("Scrolled to top of page");
        } catch (Exception e) {
            logger.warn("Failed to scroll to top", e);
        }
    }

    /**
     * Scroll to bottom of page
     */
    protected void scrollToBottom() {
        try {
            jsExecutor.executeScript("window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'});");
            logger.debug("Scrolled to bottom of page");
        } catch (Exception e) {
            logger.warn("Failed to scroll to bottom", e);
        }
    }

    /**
     * Scroll by specific pixels
     * @param xPixels Horizontal pixels to scroll
     * @param yPixels Vertical pixels to scroll
     */
    protected void scrollByPixels(int xPixels, int yPixels) {
        try {
            jsExecutor.executeScript(String.format("window.scrollBy(%d, %d);", xPixels, yPixels));
            logger.debug("Scrolled by {} x {} pixels", xPixels, yPixels);
        } catch (Exception e) {
            logger.warn("Failed to scroll by pixels", e);
        }
    }

    // ==================== WINDOW HANDLING METHODS ====================

    /**
     * Switch to window by title
     * @param windowTitle Title of the window to switch to
     * @return true if switch was successful, false otherwise
     */
    protected boolean switchToWindowByTitle(String windowTitle) {
        try {
            Set<String> windowHandles = driver.getWindowHandles();
            String originalWindow = driver.getWindowHandle();

            for (String windowHandle : windowHandles) {
                driver.switchTo().window(windowHandle);
                if (driver.getTitle().contains(windowTitle)) {
                    logger.info("Switched to window with title: {}", windowTitle);
                    return true;
                }
            }

            // Switch back to original window if target not found
            driver.switchTo().window(originalWindow);
            logger.warn("Window with title '{}' not found", windowTitle);
            return false;
        } catch (Exception e) {
            logger.error("Failed to switch to window with title '{}'", windowTitle, e);
            return false;
        }
    }

    /**
     * Close current window and switch to main window
     */
    protected void closeCurrentWindowAndSwitchToMain() {
        try {
            Set<String> windowHandles = driver.getWindowHandles();
            String currentWindow = driver.getWindowHandle();

            driver.close();

            for (String windowHandle : windowHandles) {
                if (!windowHandle.equals(currentWindow)) {
                    driver.switchTo().window(windowHandle);
                    break;
                }
            }

            logger.info("Closed current window and switched to main window");
        } catch (Exception e) {
            logger.error("Failed to close window and switch to main", e);
        }
    }

    // ==================== ALERT HANDLING METHODS ====================

    /**
     * Accept alert dialog
     * @return Alert text before accepting
     */
    protected String acceptAlert() {
        try {
            Alert alert = webDriverWait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            alert.accept();
            logger.info("Accepted alert with text: {}", alertText);
            return alertText;
        } catch (Exception e) {
            logger.error("Failed to accept alert", e);
            return "";
        }
    }

    /**
     * Dismiss alert dialog
     * @return Alert text before dismissing
     */
    protected String dismissAlert() {
        try {
            Alert alert = webDriverWait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            alert.dismiss();
            logger.info("Dismissed alert with text: {}", alertText);
            return alertText;
        } catch (Exception e) {
            logger.error("Failed to dismiss alert", e);
            return "";
        }
    }

    /**
     * Enter text in alert prompt and accept
     * @param text Text to enter in prompt
     * @return Alert text before entering text
     */
    protected String enterTextInAlertAndAccept(String text) {
        try {
            Alert alert = webDriverWait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            alert.sendKeys(text);
            alert.accept();
            logger.info("Entered text '{}' in alert and accepted", text);
            return alertText;
        } catch (Exception e) {
            logger.error("Failed to enter text in alert", e);
            return "";
        }
    }

    // ==================== WAIT METHODS ====================

    /**
     * Wait for page to load completely
     */
    protected void waitForPageLoad() {
        try {
            webDriverWait.until(webDriver ->
                    jsExecutor.executeScript("return document.readyState").equals("complete"));
            logger.debug("Page loaded completely");
        } catch (Exception e) {
            logger.warn("Page load wait timed out", e);
        }
    }

    /**
     * Wait for element to be present
     * @param elementName Descriptive name of the element
     * @param locators Array of locator strategies
     * @return true if element becomes present, false otherwise
     */
    protected boolean waitForElementPresent(String elementName, String... locators) {
        try {
            findElementWithHealing(elementName, locators);
            logger.debug("Element '{}' is present", elementName);
            return true;
        } catch (Exception e) {
            logger.debug("Element '{}' not present within timeout", elementName);
            return false;
        }
    }

    /**
     * Wait for element to disappear
     * @param elementName Descriptive name of the element
     * @param locators Array of locator strategies
     * @param timeoutSeconds Timeout in seconds
     * @return true if element disappears, false otherwise
     */
    protected boolean waitForElementToDisappear(String elementName, int timeoutSeconds, String... locators) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            customWait.until(driver -> {
                try {
                    WebElement element = findElementWithHealing(elementName, locators);
                    return !element.isDisplayed();
                } catch (Exception e) {
                    return true; // Element not found means it's gone
                }
            });
            logger.debug("Element '{}' disappeared", elementName);
            return true;
        } catch (Exception e) {
            logger.debug("Element '{}' did not disappear within {} seconds", elementName, timeoutSeconds);
            return false;
        }
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Take screenshot of current page
     * @return Screenshot as byte array
     */
    protected byte[] takeScreenshot() {
        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            byte[] screenshotBytes = screenshot.getScreenshotAs(OutputType.BYTES);
            logger.debug("Screenshot taken successfully");
            return screenshotBytes;
        } catch (Exception e) {
            logger.error("Failed to take screenshot", e);
            return new byte[0];
        }
    }

    /**
     * Execute custom JavaScript
     * @param script JavaScript code to execute
     * @param args Arguments to pass to the script
     * @return Result of script execution
     */
    protected Object executeJavaScript(String script, Object... args) {
        try {
            Object result = jsExecutor.executeScript(script, args);
            logger.debug("JavaScript executed successfully");
            return result;
        } catch (Exception e) {
            logger.error("Failed to execute JavaScript: {}", script, e);
            return null;
        }
    }

    /**
     * Highlight element for debugging purposes
     * @param element WebElement to highlight
     */
    protected void highlightElement(WebElement element) {
        try {
            String originalStyle = element.getAttribute("style");
            jsExecutor.executeScript(
                    "arguments[0].setAttribute('style', 'border: 3px solid red; background-color: yellow;');",
                    element);

            Thread.sleep(1000); // Show highlight for 1 second

            jsExecutor.executeScript(
                    "arguments[0].setAttribute('style', arguments[1]);",
                    element, originalStyle);

            logger.debug("Element highlighted successfully");
        } catch (Exception e) {
            logger.warn("Failed to highlight element", e);
        }
    }

    /**
     * Get page load time
     * @return Page load time in milliseconds
     */
    /**
     * Get page load time
     * @return Page load time in milliseconds
     */
    protected long getPageLoadTime() {
        try {
            Object loadTime = jsExecutor.executeScript(
                    "return window.performance.timing.loadEventEnd - window.performance.timing.navigationStart;");
            long loadTimeMs = Long.parseLong(loadTime.toString());
            logger.debug("Page load time: {} ms", loadTimeMs);
            return loadTimeMs;
        } catch (Exception e) {
            logger.error("Failed to get page load time", e);
            return -1;  // Return -1 to indicate failure
        }
    }

    /**
     * Get browser console logs (requires browser logging preferences configured)
     * @return List of console log entries
     */
    protected List<LogEntry> getBrowserConsoleLogs() {
        try {
            Logs logs = driver.manage().logs();
            return logs.get(LogType.BROWSER).getAll();
        } catch (Exception e) {
            logger.error("Failed to get browser console logs", e);
            return new ArrayList<>();
        }
    }

    /**
     * Drag and drop element to target location
     * @param sourceElementName Descriptive name of source element
     * @param targetElementName Descriptive name of target element
     * @param sourceLocators Source element locators
     * @param targetLocators Target element locators
     */
    protected void dragAndDrop(String sourceElementName, String targetElementName,
                               String[] sourceLocators, String[] targetLocators) {
        try {
            WebElement source = findElementWithHealing(sourceElementName, sourceLocators);
            WebElement target = findElementWithHealing(targetElementName, targetLocators);
            waitUtils.waitForElementToBeVisible(source);
            waitUtils.waitForElementToBeVisible(target);

            actions.dragAndDrop(source, target).build().perform();
            logger.info("Dragged '{}' to '{}'", sourceElementName, targetElementName);
        } catch (Exception e) {
            logger.error("Drag and drop failed from '{}' to '{}'",
                    sourceElementName, targetElementName, e);
            throw new RuntimeException("Drag and drop operation failed", e);
        }
    }

    /**
     * Hover over element
     * @param elementName Descriptive name of element
     * @param locators Element locators
     */
    protected void hoverOverElement(String elementName, String... locators) {
        try {
            WebElement element = findElementWithHealing(elementName, locators);
            waitUtils.waitForElementToBeVisible(element);
            actions.moveToElement(element).perform();
            logger.info("Hovered over element: {}", elementName);
        } catch (Exception e) {
            logger.error("Failed to hover over element '{}'", elementName, e);
            throw new RuntimeException("Hover operation failed", e);
        }
    }

    /**
     * Switch to frame by element
     * @param elementName Descriptive name of frame element
     * @param locators Frame locators
     */
    protected void switchToFrame(String elementName, String... locators) {
        try {
            WebElement frame = findElementWithHealing(elementName, locators);
            driver.switchTo().frame(frame);
            logger.info("Switched to frame: {}", elementName);
        } catch (Exception e) {
            logger.error("Failed to switch to frame '{}'", elementName, e);
            throw new RuntimeException("Frame switch failed", e);
        }
    }

    /**
     * Switch to default content (main document)
     */
    protected void switchToDefaultContent() {
        try {
            driver.switchTo().defaultContent();
            logger.info("Switched to default content");
        } catch (Exception e) {
            logger.error("Failed to switch to default content", e);
            throw new RuntimeException("Default content switch failed", e);
        }
    }

    /**
     * Press keyboard key
     * @param key Key to press (from Keys class)
     */
    protected void pressKey(Keys key) {
        try {
            actions.sendKeys(key).perform();
            logger.info("Pressed keyboard key: {}", key.name());
        } catch (Exception e) {
            logger.error("Failed to press key: {}", key.name(), e);
        }
    }

    /**
     * Clear browser cookies
     */
    protected void clearCookies() {
        try {
            driver.manage().deleteAllCookies();
            logger.info("Cleared all browser cookies");
        } catch (Exception e) {
            logger.error("Failed to clear cookies", e);
        }
    }
}  // End of BasePage class