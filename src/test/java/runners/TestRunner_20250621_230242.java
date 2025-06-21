    package runners;

    import io.cucumber.testng.AbstractTestNGCucumberTests;
    import io.cucumber.testng.CucumberOptions;
    import org.testng.annotations.DataProvider;
    import java.util.Date;
    import java.text.SimpleDateFormat;

    /**
     * Cucumber TestNG Runner
     * Generated on: 20250621_230242
     * Feature: OrangeHRMLoginTest
     */
    @CucumberOptions(
        features = "src/test/resources/features",
        glue = {"steps", "hooks"},
        plugin = {
            "pretty",
            "html:target/cucumber-reports/html-report.html",
            "json:target/cucumber-reports/cucumber.json",
            "junit:target/cucumber-reports/cucumber.xml",
            "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
            "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true,
        publish = true,
        tags = "@smoke or @regression"
    )
    public class TestRunner_20250621_230242 extends AbstractTestNGCucumberTests {

        @Override
        @DataProvider(parallel = true)
        public Object[][] scenarios() {
            return super.scenarios();
        }

        /**
         * Get current timestamp for logging/reporting purposes
         * @return formatted timestamp string
         */
        private static String getCurrentTimestamp() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }

        /**
         * Get the feature name this runner was generated for
         * @return feature name
         */
        public static String getFeatureName() {
            return "OrangeHRMLoginTest";
        }

    }
