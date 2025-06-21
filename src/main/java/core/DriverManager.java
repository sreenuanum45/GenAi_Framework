package core;// 2. DriverManager.java



import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class DriverManager {
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    private static ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static void setDriver(String browserName) {
        WebDriver driver = createDriver(browserName);
        driverThreadLocal.set(driver);

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(com.ai.automation.core.ConfigManager.getImplicitWait()));
        driver.manage().deleteAllCookies();

        logger.info("Driver initialized for browser: {}", browserName);
    }

    private static WebDriver createDriver(String browserName) {
        switch (browserName.toLowerCase()) {
            case "chrome":

                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
                chromeOptions.addArguments("--disable-extensions");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                return new ChromeDriver(chromeOptions);

            case "firefox":

                return new FirefoxDriver();

            case "edge":

                return new EdgeDriver();

            default:
                throw new IllegalArgumentException("Browser not supported: " + browserName);
        }
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
            logger.info("Driver closed successfully");
        }
    }
}