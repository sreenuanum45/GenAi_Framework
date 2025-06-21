package pages;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class OrangeHRMLoginPage {
  private WebDriver driver;
  private By usernameField = By.xpath("//input[@name='username']");
  private By passwordField = By.xpath("//input[@name='password']");
  private By loginButton = By.xpath("//button[@type='submit']");
  private By userMenu = By.xpath("//span[@class='oxd-userdropdown-name']");
  private By logoutButton = By.xpath("//a[@href='/web/index.php/auth/logout']");

  public OrangeHRMLoginPage(WebDriver driver) {
    this.driver = driver;
  }

  public void enterUsername(String username) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.elementToBeClickable(usernameField)).sendKeys(username);
  }

  public void enterPassword(String password) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.elementToBeClickable(passwordField)).sendKeys(password);
  }

  public void clickLoginButton() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
  }

  public void clickUserMenu() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.elementToBeClickable(userMenu)).click();
  }

  public void clickLogoutButton() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.elementToBeClickable(logoutButton)).click();
  }
}