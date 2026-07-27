# Automation Exercise Test Framework

An end-to-end test automation framework covering both the UI and public API of
[automationexercise.com](https://automationexercise.com), built with Java, Selenium,
TestNG, and RestAssured, using the Page Object Model and a GitHub Actions CI pipeline.

## What's inside
- `src/test/java/pages/LoginPage.java` — Page Object for the login page
- `src/test/java/tests/LoginTest.java` — UI tests: invalid login, valid login
- `src/test/java/api/ApiTests.java` — 7 API tests against documented, real endpoints
  (products list, brands list, search product, verify login — including negative/
  method-validation cases, not just happy paths)
- `testng.xml` — runs both suites together
- `.github/workflows/ci.yml` — runs `mvn test` automatically on every push

## Before your first run (10 minutes, do this first)
1. **Create a real test account**: go to automationexercise.com → "Signup / Login" →
   "New User Signup!" → fill the form. Takes under a minute.
2. Open `LoginTest.java` and replace the placeholder email/password in
   `testValidLoginSucceeds()` with your real account's credentials.
3. **Verify the locators**: open the login page, right-click the email field →
   Inspect, and confirm it matches `input[data-qa='login-email']` in
   `LoginPage.java`. If the site has changed, update the locator — this check is a
   normal first step, not a sign something's wrong with the framework.

## How to run
```bash
mvn test
```
This runs all 7 API tests and both UI tests, using `testng.xml`.

To run only the API tests (fast, no browser needed):
```bash
mvn test -Dtest=api.ApiTests
```

## Pushing to GitHub (for the CI pipeline to actually run)
```bash
git init
git add .
git commit -m "Initial automation framework: UI + API tests, POM, CI"
git branch -M main
git remote add origin <your-empty-github-repo-url>
git push -u origin main
```
Once pushed, check the "Actions" tab on your GitHub repo — you'll see the pipeline
run automatically and report pass/fail. That green checkmark is worth screenshotting
for your resume/portfolio.

## If the UI test fails in GitHub Actions (headless issue)
GitHub's runners have no visible display, so Chrome needs headless mode there. In
`LoginTest.java`'s `setUp()`, replace:
```java
driver = new ChromeDriver();
```
with:
```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--headless=new");
driver = new ChromeDriver(options);
```
The API tests need no such change — this is itself a good interview talking point on
*why* API tests are more stable in CI than UI tests.

## Resume line
> Built an end-to-end test automation framework (Java, Selenium, TestNG, RestAssured)
> covering the UI and public API of an e-commerce site, using Page Object Model for
> maintainability and GitHub Actions for CI. Automated 9 test cases spanning login
> flows and product/search API endpoints, including negative and method-validation
> scenarios, with results reported via an automated CI pipeline on every commit.

## Talking points for the interview
- **Why POM**: locators live in one place (`LoginPage.java`); if the site changes,
  you fix it once, not in every test.
- **Why API + UI together**: API tests are faster and more stable, so backend
  logic gets validated there; Selenium is reserved for what genuinely needs a
  browser (rendering, JS behavior, real click-flows).
- **Why negative tests matter**: `testPostToProductsListIsRejected` and the
  missing-parameter tests prove the API fails *correctly*, not just that it
  succeeds when everything is right — this is often the difference testers who
  only check happy paths miss.
- **Why CI**: every push automatically re-runs the whole suite — this is
  regression testing (Part 3 of your prep guide) turned into practice, not just
  theory.
