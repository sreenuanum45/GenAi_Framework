package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import pages.OrangeHRMLoginPage;

public class OrangeHRMLoginSteps {
  private WebDriver driver;
  private OrangeHRMLoginPage loginPage;

  @Given("I am on the OrangeHRM login page")
  public void i_am_on_the_OrangeHRM_login_page() {
    System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
    driver = new ChromeDriver();
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
    loginPage = new OrangeHRMLoginPage(driver);
  }

  @When("I enter username {string}")
  public void i_enter_username(String username) {
    loginPage.enterUsername(username);
  }

  @When("I enter password {string}")
  public void i_enter_password(String password) {
    loginPage.enterPassword(password);
  }

  @When("I click the login button")
  public void i_click_the_login_button() {
    loginPage.clickLoginButton();
  }

  @When("I click on the user menu")
  public void i_click_on_the_user_menu() {
    loginPage.clickUserMenu();
  }

  @When("I click on the logout button")
  public void i_click_on_the_logout_button() {
    loginPage.clickLogoutButton();
  }

  @Then("I should be logged out")
  public void i_should_be_logged_out() {
    driver.quit();
  }
}