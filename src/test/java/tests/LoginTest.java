package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.LoginPage;
import org.openqa.selenium.chrome.ChromeOptions;

public class LoginTest {

    private WebDriver driver;
    private LoginPage loginPage;

    @BeforeClass
    public void setUp() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");           // no visible browser window - required on CI servers
        options.addArguments("--no-sandbox");               // CI runners run as a restricted user; this permission is needed to launch Chrome at all
        options.addArguments("--disable-dev-shm-usage");    // CI runners often have very little /dev/shm (shared memory); this tells Chrome not to rely on it
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.get("https://automationexercise.com");
        loginPage = new LoginPage(driver);
    }

    @Test(priority = 1)
    public void testInvalidLoginShowsError() {
        loginPage.goToLoginPage();
        loginPage.login("nonexistent_user_12345@fake.com", "wrongPassword123");
        Assert.assertTrue(loginPage.isLoginFailed(),
                "Expected to remain on the login page after invalid credentials");
    }

    @Test(priority = 2)
    public void testValidLoginSucceeds() {
        // BEFORE RUNNING THIS TEST: create a real account manually on
        // automationexercise.com (Signup / Login -> New User Signup!) and replace
        // the credentials below with that account. Takes under a minute, and gives
        // you a real account to demo in the interview too.
        driver.navigate().to("https://automationexercise.com/login");
        loginPage.login("architpatil150@gmail.com", "dhoop123");
        Assert.assertTrue(loginPage.isLoginSuccessful(),
                "Expected successful login to show 'Logged in as' in the nav");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
