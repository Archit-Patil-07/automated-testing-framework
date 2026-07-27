package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Uses an embedded H2 database as a source of test data for data-driven tests,
 * instead of hardcoding values inside test methods. This is the same JDBC
 * pattern used to connect to any real database (MySQL, PostgreSQL, etc.) —
 * H2 is used here specifically because it needs no separate server install,
 * which makes it ideal for a self-contained framework like this one.
 */
public class DatabaseHelper {

    // DB_CLOSE_DELAY=-1 keeps this in-memory database alive for the whole JVM
    // session (i.e., the whole test run), instead of disappearing the instant
    // one connection closes.
    private static final String DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";

    /**
     * Creates the table (if it doesn't already exist) and seeds it with
     * login test scenarios. Called once before the data-driven test runs.
     */
    public static void setupLoginTestData() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS login_test_data (" +
                    "id INT PRIMARY KEY, " +
                    "email VARCHAR(100), " +
                    "password VARCHAR(100), " +
                    "expected_message VARCHAR(200))");

            stmt.execute("DELETE FROM login_test_data"); // clean slate on every run

            stmt.execute("INSERT INTO login_test_data VALUES " +
                    "(1, '', '', 'Bad request, email or password parameter is missing in POST request.'), " +
                    "(2, 'someone@fake.com', '', 'Bad request, email or password parameter is missing in POST request.'), " +
                    "(3, '', 'somepassword123', 'Bad request, email or password parameter is missing in POST request.'), " +
                    "(4, 'definitely_not_a_real_user_999@fake.com', 'wrongpass', 'User not found!')");
        }
    }

    /**
     * Reads every row back out as an Object[][] — the exact shape TestNG's
     * @DataProvider expects, so each row becomes one separate test run.
     */
    public static Object[][] getLoginTestData() throws SQLException {
        List<Object[]> rows = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT email, password, expected_message FROM login_test_data ORDER BY id")) {

            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("expected_message")
                });
            }
        }
        return rows.toArray(new Object[0][]);
    }
}
