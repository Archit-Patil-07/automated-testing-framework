package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

/**
 * Page Object for automationexercise.com's Login page.
 */
public class LoginPage {

    private WebDriver driver;

    private By signupOrLoginLink = By.linkText("Signup / Login");
    private By loginEmailField = By.cssSelector("input[data-qa='login-email']");
    private By loginPasswordField = By.cssSelector("input[data-qa='login-password']");
    private By loginButton = By.cssSelector("button[data-qa='login-button']");

    // Added a flexible locator to find the "Logged in as" text anywhere in the DOM
    private By loggedInTextLocator = By.xpath("//*[contains(text(), 'Logged in as')]");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void goToLoginPage() {
        driver.findElement(signupOrLoginLink).click();
    }

    public void login(String email, String password) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginEmailField)).sendKeys(email);
        driver.findElement(loginPasswordField).sendKeys(password);
        driver.findElement(loginButton).click();
    }

    public boolean isLoginSuccessful() {
        try {
            // Explicitly wait for the browser to load the next page and render the text
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.visibilityOfElementLocated(loggedInTextLocator)).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            // If the element doesn't appear after 10 seconds, the login failed
            return false;
        }
    }

    public boolean isLoginFailed() {
        // After a failed login attempt you're kept on the /login page
        return driver.getCurrentUrl().contains("/login");
    }
}