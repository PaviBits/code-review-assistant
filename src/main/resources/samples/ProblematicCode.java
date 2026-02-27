/**
 * Sample Java file demonstrating various code quality issues.
 * This file is intentionally written with violations to showcase
 * what the Code Review Tool detects.
 *
 * Issues in this file:
 * 1. Missing semicolons
 * 2. Naming convention violations
 * 3. Unused imports
 * 4. Unused variables
 * 5. Commented-out code
 * 6. Deep nesting
 * 7. Magic strings and numbers
 * 8. Long methods
 * 9. System.out logging
 * 10. Empty catch blocks
 */

import java.util.*;
import java.io.*;
import java.nio.file.*; // This import might be unused

public class ProblematicCode {
    // ISSUE: Constant should be CONSTANT_CASE, not camelCase
    public static final int max_users = 100
    private static final String database_url = "jdbc:mysql://localhost:3306/mydb";

    // ISSUE: Variable name starts with uppercase (should be camelCase)
    private int UserCount = 0
    private String User_Name; // ISSUE: Snake case instead of camelCase

    /**
     * ISSUE: This method is too long and has multiple code quality problems.
     * Long methods are harder to test, understand, and maintain.
     */
    public void processUserData(String userData) {
        // ISSUE: Unused variable
        List<String> unusedList = new ArrayList<>();

        try {
            if (userData != null) {
                if (!userData.isEmpty()) {
                    if (userData.length() > 5) {
                        // ISSUE: Deep nesting (4+ levels)
                        if (userData.contains("@")) {
                            // ISSUE: Magic string - should be constant
                            System.out.println("Processing: " + userData);

                            // ISSUE: Commented-out code
                            // String processedData = userData.toLowerCase();
                            // data.save(processedData);

                            // ISSUE: Magic number - should be constant
                            int threshold = 100;
                            if (userData.length() > threshold) {
                                System.out.println("Data too long")
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // ISSUE: Empty catch block - exceptions not handled
        }
    }

    /**
     * ISSUE: Method name violation - should start with lowercase
     */
    public String GetUserName() {
        // ISSUE: System.out used instead of proper logging framework (SLF4J, Log4j)
        System.out.println("Getting user name");
        return User_Name;
    }

    /**
     * ISSUE: Another long method with complexity issues
     */
    public void ValidateAndSaveUser(String name, String email, int age) {
        // ISSUE: Complex condition with too many operators
        if ((name != null && !name.isEmpty()) &&
                (email != null && !email.isEmpty() && email.contains("@")) &&
                (age > 0 && age < 150 && age > 18)) {
            try {
                // ISSUE: Magic numbers
                if (age >= 18 && age <= 65) {
                    // ISSUE: Commented-out code
                    // database.update(name, email, age);
                    System.out.println("User saved successfully");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                // ISSUE: Returning in catch block without proper logging
                return;
            }
        }
    }

    /**
     * ISSUE: Class-level method with issues
     */
    public static void main(String[] args) {
        ProblematicCode app = new ProblematicCode();

        // ISSUE: Missing semicolon
        app.processUserData("test@example.com")

        // ISSUE: TODO comment indicating incomplete work
        // TODO: Implement proper error handling before production
        // FIXME: This method needs refactoring

        app.GetUserName();

        // ISSUE: Hardcoded string magic
        String message = "Success!";
        System.out.println(message);
    }
}
