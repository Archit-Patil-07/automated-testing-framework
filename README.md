![CI](https://github.com/Archit-Patil-07/automated-testing-framework/actions/workflows/ci.yml/badge.svg)

# Automated Testing Framework

A test automation framework built around [automationexercise.com](https://automationexercise.com), covering both its UI and public API. Built as a personal project to get hands-on with the full stack of an SDET role — not just writing test cases, but structuring a real framework, dealing with flaky tests, debugging a live API quirk, and wiring up CI so the suite runs itself on every push.

## What it actually tests

- **Login flow (UI)** — valid and invalid login attempts through a real Chrome browser, using the Page Object Model so locators live in one place instead of scattered across test files
- **Products & Brands API** — confirms the core GET endpoints return what they promise, and that hitting them with the wrong HTTP method fails the way it should (405, not a silent 200)
- **Search API** — valid search terms return results; missing parameters return a proper 400 instead of an unhandled error
- **Login verification API** — checks missing-credential handling and non-existent-user handling as two distinct failure paths, not one generic error
- **Data-driven login scenarios** — instead of hardcoding four near-identical test methods, an embedded H2 database holds the test data (missing email, missing password, both missing, invalid user), and a single `@DataProvider`-backed method runs once per row

13 test cases total, run automatically through GitHub Actions on every push.

## Stack

- **Java 17**
- **Selenium 4** — browser automation
- **TestNG** — test runner, assertions, data providers
- **RestAssured** — API testing
- **H2** — embedded database for data-driven test data (no server install needed)
- **Maven** — build and dependency management
- **GitHub Actions** — CI pipeline

## Project structure

src/test/java/
pages/:
LoginPage.java # Page Object for the login page
tests/:
LoginTest.java # UI tests: invalid + valid login
api/:
ApiTests.java # API tests: products, brands, search, login verification
utils/:
DatabaseHelper.java # Seeds + reads test data from embedded H2 via JDBC
testng.xml # Runs both suites together
.github/workflows/ci.yml # Runs mvn test on every push


## Running it locally

Before the first run:

1. Sign up for a real account on automationexercise.com — takes under a minute
2. Drop those credentials into `testValidLoginSucceeds()` in `LoginTest.java`

Then:
```bash
mvn test
```

To run just the API layer (fast, no browser needed):
```bash
mvn test -Dtest=api.ApiTests
```

## A few things worth mentioning about how this was built

**The API sends JSON with the wrong Content-Type header.** automationexercise.com's API returns valid JSON but labels it `text/html`, which makes RestAssured misread the response entirely. Fixed by explicitly registering a JSON parser for that content type in `ApiTests`'s `@BeforeClass`:
```java
RestAssured.registerParser("text/html", Parser.JSON);
```

**Headless Chrome needs more than just `--headless`.** On GitHub's runners there's no display, no sandbox permissions in the usual sense, and very little shared memory — Chrome would exit immediately with a generic "session not created" error until `--no-sandbox` and `--disable-dev-shm-usage` were added alongside `--headless=new`.

**A race condition, not a login bug.** The valid-login test failed intermittently at first — not because login was broken, but because the assertion checked for post-login page content the instant the click fired, before the redirect had actually happened. Fixed with an explicit wait polling for the expected page state instead of checking immediately.

## Possible next steps

- Extend the Page Object layer to cover product search and cart flows on the UI side
- Add Extent Reports for a nicer visual test report
- Screenshot capture on test failure via a TestNG listener
