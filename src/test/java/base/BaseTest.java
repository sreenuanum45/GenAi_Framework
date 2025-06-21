package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Base Test Class providing common setup and teardown functionality
 */
public class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Properties properties;

    @BeforeClass
    public void setUp() throws IOException {
        loadProperties();
        initializeDriver();
        configureBrowser();
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void loadProperties() throws IOException {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream("src/test/resources/config/test.properties")) {
            properties.load(fis);
        }
    }

    private void initializeDriver() {
        String browser = properties.getProperty("browser", "chrome").toLowerCase();
        boolean headless = Boolean.parseBoolean(properties.getProperty("headless", "false"));

        switch (browser) {
            case "chrome":

                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) chromeOptions.addArguments("--headless");
                chromeOptions.addArguments("--no-sandbox", "--disable-dev-shm-usage");
                driver = new ChromeDriver(chromeOptions);
                break;
            case "firefox":

                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) firefoxOptions.addArguments("--headless");
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case "edge":

                driver = new EdgeDriver();
                break;
            default:
                throw new IllegalArgumentException("Browser not supported: " + browser);
        }
    }

    private void configureBrowser() {
        int implicitWait = Integer.parseInt(properties.getProperty("implicit.wait", "10"));
        int explicitWait = Integer.parseInt(properties.getProperty("explicit.wait", "20"));
        int pageLoadTimeout = Integer.parseInt(properties.getProperty("page.load.timeout", "30"));

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
    }

    protected String getProperty(String key) {
        return properties.getProperty(key);
    }
}
