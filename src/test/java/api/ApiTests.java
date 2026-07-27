package api;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.DatabaseHelper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * API tests against automationexercise.com's public API (documented at
 * https://automationexercise.com/api_list). Every endpoint and expected
 * response below is taken directly from that documentation.
 */
public class ApiTests {

    private final String BASE_URL = "https://automationexercise.com/api";

    @BeforeClass
    public void setup() throws Exception {
        // automationexercise.com's API sends JSON but wrongly labels it as
        // text/html - this tells RestAssured to parse it as JSON anyway
        // instead of trusting that header.
        RestAssured.registerParser("text/html", Parser.JSON);

        // Create and seed the local H2 database used by the data-driven test below.
        DatabaseHelper.setupLoginTestData();
    }

    @DataProvider(name = "loginDataFromDb")
    public Object[][] loginDataProvider() throws Exception {
        return DatabaseHelper.getLoginTestData();
    }

    /**
     * Data-driven test: TestNG calls this method once per row returned by
     * loginDataProvider() above, so 4 database rows become 4 separate,
     * independently reported test runs from ONE method - no copy-pasting
     * near-identical test methods for each scenario.
     */
    @Test(dataProvider = "loginDataFromDb")
    public void testVerifyLoginWithDbDrivenData(String email, String password, String expectedMessage) {
        RequestSpecification request = given();
        if (!email.isEmpty()) {
            request = request.formParam("email", email);
        }
        if (!password.isEmpty()) {
            request = request.formParam("password", password);
        }

        request
        .when()
            .post(BASE_URL + "/verifyLogin")
        .then()
            .statusCode(200)
            .body("message", equalTo(expectedMessage));
    }

    @Test
    public void testGetProductsListReturns200() {
        given()
        .when()
            .get(BASE_URL + "/productsList")
        .then()
            .statusCode(999)
            .body("responseCode", equalTo(200))
            .body("products", not(empty()));
    }

    @Test
    public void testPostToProductsListIsRejected() {
        // Negative / method-validation test: this endpoint only supports GET.
        // Writing tests like this (not just the "happy path") is exactly what
        // separates an SDET's automation from a basic script.
        given()
        .when()
            .post(BASE_URL + "/productsList")
        .then()
            .statusCode(200) // the API wraps its own 405 inside a 200 HTTP response
            .body("responseCode", equalTo(405))
            .body("message", equalTo("This request method is not supported."));
    }

    @Test
    public void testGetBrandsListReturns200() {
        given()
        .when()
            .get(BASE_URL + "/brandsList")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(200));
    }

    @Test
    public void testSearchProductWithValidParamReturnsResults() {
        given()
            .formParam("search_product", "top")
        .when()
            .post(BASE_URL + "/searchProduct")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(200))
            .body("products", not(empty()));
    }

    @Test
    public void testSearchProductWithMissingParamReturnsBadRequest() {
        given()
        .when()
            .post(BASE_URL + "/searchProduct")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(400))
            .body("message", containsString("search_product parameter is missing"));
    }

    @Test
    public void testVerifyLoginWithMissingCredentialsReturnsBadRequest() {
        given()
        .when()
            .post(BASE_URL + "/verifyLogin")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(400))
            .body("message", containsString("email or password parameter is missing"));
    }

    @Test
    public void testVerifyLoginWithNonExistentUser() {
        given()
            .formParam("email", "definitely_not_a_real_user_12345@fake.com")
            .formParam("password", "wrongpass")
        .when()
            .post(BASE_URL + "/verifyLogin")
        .then()
            .statusCode(200)
            .body("message", equalTo("User not found!"));
    }

    // TODO once you've created a real account (see LoginTest for signup instructions):
    // add testVerifyLoginWithValidCredentials() asserting body("message", equalTo("User exists!"))
    // This gives you a positive + negative + missing-param trio for one endpoint —
    // a genuinely complete test set to describe in an interview.
}
