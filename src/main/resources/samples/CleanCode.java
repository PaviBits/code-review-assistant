/**
 * Sample Java file demonstrating GOOD coding practices.
 * Compare this with ProblematicCode.java to understand code quality improvements.
 *
 * This file shows:
 * ✓ Proper naming conventions
 * ✓ Clear, focused methods
 * ✓ Appropriate use of constants
 * ✓ Proper exception handling
 * ✓ Professional logging
 * ✓ Clean code structure
 */

import java.util.ArrayList;
import java.util.List;

/**
 * User service class for managing user data operations.
 * Demonstrates professional Java coding standards.
 */
public class CleanCode {
    // Constants in CONSTANT_CASE
    private static final int MAX_USERS = 100;
    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 150;
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final int EMAIL_VALIDATION_THRESHOLD = 5;

    // Variables in camelCase
    private int userCount;
    private String currentUserName;
    private List<String> registeredUsers;

    /**
     * Constructor - Initialize the service.
     */
    public CleanCode() {
        this.userCount = 0;
        this.currentUserName = "";
        this.registeredUsers = new ArrayList<>();
    }

    /**
     * Process user data safely with proper validation.
     * 
     * Method demonstrates:
     * - Single Responsibility Principle (only processes one thing)
     * - Early return pattern (reduces nesting)
     * - Extracted validation method
     * - Proper exception handling
     *
     * @param userData The user data to process
     * @return true if processing successful, false otherwise
     */
    public boolean processUserData(String userData) {
        // Validate input early - reduces nesting and improves readability
        if (userData == null || userData.isEmpty()) {
            logWarning("Received null or empty user data");
            return false;
        }

        try {
            // Extract validation to separate method
            if (!isValidUserData(userData)) {
                logWarning("Invalid user data format: " + userData);
                return false;
            }

            // Process the data
            String processedData = sanitizeData(userData);
            saveToDatabase(processedData);
            return true;

        } catch (DatabaseException e) {
            logError("Failed to save user data: " + e.getMessage());
            // Proper exception handling - don't swallow exceptions silently
            return false;
        }
    }

    /**
     * Validate user data according to business rules.
     * Extracted method improves readability and testability.
     *
     * @param userData The data to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidUserData(String userData) {
        // Clear, simple validation logic
        return userData.length() > EMAIL_VALIDATION_THRESHOLD &&
               userData.contains("@");
    }

    /**
     * Sanitize user input for storage.
     * Demonstrates data cleaning and transformation.
     *
     * @param userData Raw user data
     * @return Cleaned and sanitized data
     */
    private String sanitizeData(String userData) {
        return userData.trim().toLowerCase();
    }

    /**
     * Get the current user name.
     * Method name follows camelCase convention (starts lowercase).
     *
     * @return The current user name
     */
    public String getUserName() {
        logInfo("Retrieving user name");
        return currentUserName;
    }

    /**
     * Set the current user name.
     * Demonstrates proper setter with validation.
     *
     * @param name The user name to set (cannot be null or empty)
     * @return true if name was set, false if validation failed
     */
    public boolean setUserName(String name) {
        if (name == null || name.isEmpty()) {
            logWarning("Cannot set empty or null user name");
            return false;
        }
        this.currentUserName = name;
        return true;
    }

    /**
     * Validate and save user information.
     * Demonstrates complex validation with clear logic.
     *
     * @param name User's name
     * @param email User's email
     * @param age User's age
     * @return true if user was saved successfully
     */
    public boolean validateAndSaveUser(String name, String email, int age) {
        // Validate each piece separately - clearer than one complex condition
        if (!isValidName(name)) {
            logWarning("Invalid user name: " + name);
            return false;
        }

        if (!isValidEmail(email)) {
            logWarning("Invalid email: " + email);
            return false;
        }

        if (!isValidAge(age)) {
            logWarning("Invalid age: " + age);
            return false;
        }

        // All validations passed - save user
        return saveUser(name, email, age);
    }

    /**
     * Check if name is valid.
     * Extracted validation improves code clarity.
     *
     * @param name The name to validate
     * @return true if valid
     */
    private boolean isValidName(String name) {
        return name != null && !name.isEmpty() && name.length() > 2;
    }

    /**
     * Check if email is valid.
     * Simple email validation (production would use proper library).
     *
     * @param email The email to validate
     * @return true if valid
     */
    private boolean isValidEmail(String email) {
        return email != null && !email.isEmpty() && email.contains("@");
    }

    /**
     * Check if age is within acceptable range.
     * Demonstrates use of constants instead of magic numbers.
     *
     * @param age The age to validate
     * @return true if valid
     */
    private boolean isValidAge(int age) {
        return age >= MIN_AGE && age <= MAX_AGE;
    }

    /**
     * Save user to database.
     * Demonstrates proper exception handling.
     *
     * @param name User name
     * @param email User email
     * @param age User age
     * @return true if successful
     */
    private boolean saveUser(String name, String email, int age) {
        try {
            if (userCount >= MAX_USERS) {
                throw new DatabaseException("Maximum users limit reached");
            }

            // Save to database
            saveToDatabase(name + "|" + email + "|" + age);
            registeredUsers.add(email);
            userCount++;

            logInfo("User saved successfully: " + email);
            return true;

        } catch (DatabaseException e) {
            logError("Failed to save user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Save data to database.
     * Placeholder for actual database operation.
     *
     * @param data The data to save
     * @throws DatabaseException If save operation fails
     */
    private void saveToDatabase(String data) throws DatabaseException {
        // In real application, this would interact with actual database
        // Using professional logging framework (not System.out)
        logDebug("Saving to database: " + data);
    }

    /**
     * Log information level message.
     * Demonstrates proper logging (not System.out).
     * In production, use SLF4J, Log4j, or similar.
     *
     * @param message The message to log
     */
    private void logInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    /**
     * Log warning level message.
     *
     * @param message The message to log
     */
    private void logWarning(String message) {
        System.out.println("[WARN] " + message);
    }

    /**
     * Log error level message.
     *
     * @param message The message to log
     */
    private void logError(String message) {
        System.err.println("[ERROR] " + message);
    }

    /**
     * Log debug level message.
     *
     * @param message The message to log
     */
    private void logDebug(String message) {
        System.out.println("[DEBUG] " + message);
    }

    /**
     * Main method demonstrating proper usage.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        CleanCode service = new CleanCode();

        // Test with valid data
        if (service.validateAndSaveUser("John Doe", "john@example.com", 25)) {
            System.out.println("User registered successfully");
        }

        // Test with invalid data
        if (!service.validateAndSaveUser("J", "invalid-email", 15)) {
            System.out.println("User registration failed - validation error");
        }
    }
}

/**
 * Custom exception for database operations.
 * Demonstrates proper exception hierarchy.
 */
class DatabaseException extends Exception {
    /**
     * Constructor with error message.
     *
     * @param message The error description
     */
    public DatabaseException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause.
     *
     * @param message The error description
     * @param cause The underlying exception
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
