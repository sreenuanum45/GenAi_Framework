package locators;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * LocatorStrategy - Encapsulates different element location strategies
 * Provides flexible and robust element identification methods
 */
public class LocatorStrategy {
    private static final Logger logger = LoggerFactory.getLogger(LocatorStrategy.class);

    private final StrategyType type;
    private final String value;
    private final String strategyName;
    private final int priority;
    private final boolean requiresVisibility;
    private final boolean isStable;

    public String getLocators() {
        return String.format("LocatorStrategy{type=%s, value='%s', priority=%d, stable=%s}",
                type, value, priority, isStable);
    }

    public String getLocator() {
        return String.format("%s: %s", type.getName(), value);
    }

    // Strategy types enum
    public enum StrategyType {
        ID("id", 1, true, true),
        NAME("name", 2, true, true),
        CLASS_NAME("className", 3, false, false),
        TAG_NAME("tagName", 4, false, false),
        LINK_TEXT("linkText", 5, true, true),
        PARTIAL_LINK_TEXT("partialLinkText", 6, true, false),
        CSS_SELECTOR("cssSelector", 7, false, false),
        XPATH("xpath", 8, false, false),
        DATA_TESTID("dataTestId", 1, true, true),
        ARIA_LABEL("ariaLabel", 2, true, true),
        PLACEHOLDER("placeholder", 3, true, false),
        ALT_TEXT("altText", 4, true, false),
        TITLE("title", 5, true, false),
        TEXT_CONTENT("textContent", 6, true, false),
        CUSTOM("custom", 9, false, false);

        private final String name;
        private final int defaultPriority;
        private final boolean isStable;
        private final boolean requiresVisibility;

        StrategyType(String name, int defaultPriority, boolean isStable, boolean requiresVisibility) {
            this.name = name;
            this.defaultPriority = defaultPriority;
            this.isStable = isStable;
            this.requiresVisibility = requiresVisibility;
        }

        public String getName() { return name; }
        public int getDefaultPriority() { return defaultPriority; }
        public boolean isStable() { return isStable; }
        public boolean requiresVisibility() { return requiresVisibility; }
    }

    // Private constructor
    private LocatorStrategy(StrategyType type, String value, String strategyName,
                            int priority, boolean requiresVisibility, boolean isStable) {
        this.type = type;
        this.value = value;
        this.strategyName = strategyName;
        this.priority = priority;
        this.requiresVisibility = requiresVisibility;
        this.isStable = isStable;
    }

    // Factory methods for creating different strategy types
    public static LocatorStrategy id(String id) {
        return new LocatorStrategy(StrategyType.ID, id, "ID: " + id,
                StrategyType.ID.getDefaultPriority(),
                StrategyType.ID.requiresVisibility(),
                StrategyType.ID.isStable());
    }

    public static LocatorStrategy name(String name) {
        return new LocatorStrategy(StrategyType.NAME, name, "NAME: " + name,
                StrategyType.NAME.getDefaultPriority(),
                StrategyType.NAME.requiresVisibility(),
                StrategyType.NAME.isStable());
    }

    public static LocatorStrategy className(String className) {
        return new LocatorStrategy(StrategyType.CLASS_NAME, className, "CLASS: " + className,
                StrategyType.CLASS_NAME.getDefaultPriority(),
                StrategyType.CLASS_NAME.requiresVisibility(),
                StrategyType.CLASS_NAME.isStable());
    }

    public static LocatorStrategy tagName(String tagName) {
        return new LocatorStrategy(StrategyType.TAG_NAME, tagName, "TAG: " + tagName,
                StrategyType.TAG_NAME.getDefaultPriority(),
                StrategyType.TAG_NAME.requiresVisibility(),
                StrategyType.TAG_NAME.isStable());
    }

    public static LocatorStrategy linkText(String linkText) {
        return new LocatorStrategy(StrategyType.LINK_TEXT, linkText, "LINK_TEXT: " + linkText,
                StrategyType.LINK_TEXT.getDefaultPriority(),
                StrategyType.LINK_TEXT.requiresVisibility(),
                StrategyType.LINK_TEXT.isStable());
    }

    public static LocatorStrategy partialLinkText(String partialLinkText) {
        return new LocatorStrategy(StrategyType.PARTIAL_LINK_TEXT, partialLinkText,
                "PARTIAL_LINK_TEXT: " + partialLinkText,
                StrategyType.PARTIAL_LINK_TEXT.getDefaultPriority(),
                StrategyType.PARTIAL_LINK_TEXT.requiresVisibility(),
                StrategyType.PARTIAL_LINK_TEXT.isStable());
    }

    public static LocatorStrategy cssSelector(String cssSelector) {
        return new LocatorStrategy(StrategyType.CSS_SELECTOR, cssSelector, "CSS: " + cssSelector,
                StrategyType.CSS_SELECTOR.getDefaultPriority(),
                StrategyType.CSS_SELECTOR.requiresVisibility(),
                StrategyType.CSS_SELECTOR.isStable());
    }

    public static LocatorStrategy xpath(String xpath) {
        return new LocatorStrategy(StrategyType.XPATH, xpath, "XPATH: " + xpath,
                StrategyType.XPATH.getDefaultPriority(),
                StrategyType.XPATH.requiresVisibility(),
                StrategyType.XPATH.isStable());
    }

    public static LocatorStrategy dataTestId(String testId) {
        return new LocatorStrategy(StrategyType.DATA_TESTID, testId, "DATA_TESTID: " + testId,
                StrategyType.DATA_TESTID.getDefaultPriority(),
                StrategyType.DATA_TESTID.requiresVisibility(),
                StrategyType.DATA_TESTID.isStable());
    }

    public static LocatorStrategy ariaLabel(String ariaLabel) {
        return new LocatorStrategy(StrategyType.ARIA_LABEL, ariaLabel, "ARIA_LABEL: " + ariaLabel,
                StrategyType.ARIA_LABEL.getDefaultPriority(),
                StrategyType.ARIA_LABEL.requiresVisibility(),
                StrategyType.ARIA_LABEL.isStable());
    }

    public static LocatorStrategy placeholder(String placeholder) {
        return new LocatorStrategy(StrategyType.PLACEHOLDER, placeholder, "PLACEHOLDER: " + placeholder,
                StrategyType.PLACEHOLDER.getDefaultPriority(),
                StrategyType.PLACEHOLDER.requiresVisibility(),
                StrategyType.PLACEHOLDER.isStable());
    }

    public static LocatorStrategy altText(String altText) {
        return new LocatorStrategy(StrategyType.ALT_TEXT, altText, "ALT_TEXT: " + altText,
                StrategyType.ALT_TEXT.getDefaultPriority(),
                StrategyType.ALT_TEXT.requiresVisibility(),
                StrategyType.ALT_TEXT.isStable());
    }

    public static LocatorStrategy title(String title) {
        return new LocatorStrategy(StrategyType.TITLE, title, "TITLE: " + title,
                StrategyType.TITLE.getDefaultPriority(),
                StrategyType.TITLE.requiresVisibility(),
                StrategyType.TITLE.isStable());
    }

    public static LocatorStrategy textContent(String text) {
        return new LocatorStrategy(StrategyType.TEXT_CONTENT, text, "TEXT_CONTENT: " + text,
                StrategyType.TEXT_CONTENT.getDefaultPriority(),
                StrategyType.TEXT_CONTENT.requiresVisibility(),
                StrategyType.TEXT_CONTENT.isStable());
    }

    public static LocatorStrategy custom(String customLocator, String description) {
        return new LocatorStrategy(StrategyType.CUSTOM, customLocator, "CUSTOM: " + description,
                StrategyType.CUSTOM.getDefaultPriority(),
                StrategyType.CUSTOM.requiresVisibility(),
                StrategyType.CUSTOM.isStable());
    }

    // Create strategy with custom priority
    public static LocatorStrategy withPriority(LocatorStrategy strategy, int priority) {
        return new LocatorStrategy(strategy.type, strategy.value, strategy.strategyName,
                priority, strategy.requiresVisibility, strategy.isStable);
    }

    // Create strategy with custom visibility requirement
    public static LocatorStrategy withVisibility(LocatorStrategy strategy, boolean requiresVisibility) {
        return new LocatorStrategy(strategy.type, strategy.value, strategy.strategyName,
                strategy.priority, requiresVisibility, strategy.isStable);
    }

    /**
     * Convert strategy to Selenium By object
     */
    public By getBy() {
        switch (type) {
            case ID:
                return By.id(value);
            case NAME:
                return By.name(value);
            case CLASS_NAME:
                return By.className(value);
            case TAG_NAME:
                return By.tagName(value);
            case LINK_TEXT:
                return By.linkText(value);
            case PARTIAL_LINK_TEXT:
                return By.partialLinkText(value);
            case CSS_SELECTOR:
                return By.cssSelector(value);
            case XPATH:
                return By.xpath(value);
            case DATA_TESTID:
                return By.cssSelector("[data-testid='" + value + "']");
            case ARIA_LABEL:
                return By.cssSelector("[aria-label='" + value + "']");
            case PLACEHOLDER:
                return By.cssSelector("[placeholder='" + value + "']");
            case ALT_TEXT:
                return By.cssSelector("[alt='" + value + "']");
            case TITLE:
                return By.cssSelector("[title='" + value + "']");
            case TEXT_CONTENT:
                return By.xpath("//*[contains(text(), '" + value + "')]");
            case CUSTOM:
                // Try to parse custom locator
                return parseCustomLocator(value);
            default:
                throw new IllegalArgumentException("Unsupported strategy type: " + type);
        }
    }

    /**
     * Parse custom locator string
     */
    private By parseCustomLocator(String customLocator) {
        if (customLocator.startsWith("//") || customLocator.startsWith("(//")) {
            return By.xpath(customLocator);
        } else if (customLocator.startsWith("#") || customLocator.startsWith(".") ||
                customLocator.contains("[") || customLocator.contains(">")) {
            return By.cssSelector(customLocator);
        } else {
            // Default to CSS selector
            logger.warn("Custom locator format unclear, defaulting to CSS selector: {}", customLocator);
            return By.cssSelector(customLocator);
        }
    }

    /**
     * Validate strategy configuration
     */
    public boolean isValid() {
        if (value == null || value.trim().isEmpty()) {
            logger.error("Strategy value is null or empty for type: {}", type);
            return false;
        }

        // Additional validation based on strategy type
        switch (type) {
            case XPATH:
                return isValidXPath(value);
            case CSS_SELECTOR:
                return isValidCssSelector(value);
            case ID:
                return isValidId(value);
            default:
                return true;
        }
    }

    /**
     * Basic XPath validation
     */
    private boolean isValidXPath(String xpath) {
        try {
            // Basic validation - check for common XPath patterns
            return xpath.contains("/") || xpath.contains("@") || xpath.contains("[");
        } catch (Exception e) {
            logger.error("Invalid XPath: {}", xpath, e);
            return false;
        }
    }

    /**
     * Basic CSS selector validation
     */
    private boolean isValidCssSelector(String cssSelector) {
        try {
            // Basic validation - check for CSS selector patterns
            return cssSelector.matches(".*[#.\\[\\]>+~:].*") ||
                    cssSelector.matches("^[a-zA-Z][a-zA-Z0-9-_]*$");
        } catch (Exception e) {
            logger.error("Invalid CSS selector: {}", cssSelector, e);
            return false;
        }
    }

    /**
     * Basic ID validation
     */
    private boolean isValidId(String id) {
        // HTML5 allows any characters except spaces in IDs
        return !id.contains(" ");
    }

    /**
     * Get strategy stability score (higher = more stable)
     */
    public int getStabilityScore() {
        int score = isStable ? 50 : 0;

        // Add points based on strategy type stability
        switch (type) {
            case ID:
            case DATA_TESTID:
                score += 30;
                break;
            case NAME:
            case ARIA_LABEL:
                score += 25;
                break;
            case LINK_TEXT:
            case PLACEHOLDER:
                score += 20;
                break;
            case CSS_SELECTOR:
                score += 15;
                break;
            case XPATH:
                score += 10;
                break;
            case CLASS_NAME:
            case TAG_NAME:
                score += 5;
                break;
            default:
                score += 0;
        }

        return score;
    }

    /**
     * Create a fallback strategy for this strategy
     */
    public LocatorStrategy createFallback() {
        switch (type) {
            case ID:
                return cssSelector("#" + value);
            case CLASS_NAME:
                return cssSelector("." + value);
            case TAG_NAME:
                return cssSelector(value);
            case NAME:
                return cssSelector("[name='" + value + "']");
            case LINK_TEXT:
                return xpath("//a[text()='" + value + "']");
            case PARTIAL_LINK_TEXT:
                return xpath("//a[contains(text(), '" + value + "')]");
            default:
                return null;
        }
    }

    // Getters
    public StrategyType getType() { return type; }
    public String getValue() { return value; }
    public String getStrategyName() { return strategyName; }
    public int getPriority() { return priority; }
    public boolean requiresVisibility() { return requiresVisibility; }
    public boolean isStable() { return isStable; }

    // Utility methods for strategy comparison and sorting
    public static int compareByPriority(LocatorStrategy s1, LocatorStrategy s2) {
        return Integer.compare(s1.priority, s2.priority);
    }

    public static int compareByStability(LocatorStrategy s1, LocatorStrategy s2) {
        return Integer.compare(s2.getStabilityScore(), s1.getStabilityScore());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LocatorStrategy that = (LocatorStrategy) obj;
        return type == that.type && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public String toString() {
        return String.format("LocatorStrategy{type=%s, value='%s', priority=%d, stable=%s}",
                type, value, priority, isStable);
    }

    /**
     * Create a builder for complex strategy configuration
     */
    public static class Builder {
        private StrategyType type;
        private String value;
        private String name;
        private int priority = -1;
        private Boolean requiresVisibility;
        private Boolean isStable;

        public Builder(StrategyType type, String value) {
            this.type = type;
            this.value = value;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder requiresVisibility(boolean requiresVisibility) {
            this.requiresVisibility = requiresVisibility;
            return this;
        }

        public Builder isStable(boolean isStable) {
            this.isStable = isStable;
            return this;
        }

        public LocatorStrategy build() {
            String strategyName = name != null ? name : type.getName() + ": " + value;
            int finalPriority = priority != -1 ? priority : type.getDefaultPriority();
            boolean finalRequiresVisibility = requiresVisibility != null ? requiresVisibility : type.requiresVisibility();
            boolean finalIsStable = isStable != null ? isStable : type.isStable();

            return new LocatorStrategy(type, value, strategyName, finalPriority, finalRequiresVisibility, finalIsStable);
        }
    }
}