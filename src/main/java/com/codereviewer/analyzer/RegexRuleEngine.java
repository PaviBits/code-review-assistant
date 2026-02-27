package com.codereviewer.analyzer;

import java.util.*;
import java.util.regex.*;

/**
 * RegexRuleEngine class encapsulates all regex patterns used for code analysis.
 *
 * Design Pattern: Strategy Pattern + Encapsulation
 * - Contains all regex patterns in one place (Single Responsibility)
 * - Easy to maintain and extend with new rules
 * - Separates regex complexity from analysis logic
 *
 * Key Features:
 * - Syntax validation (missing semicolons, unmatched braces)
 * - Naming convention checks (camelCase, CONSTANT_CASE)
 * - Code structure analysis (long methods, deep nesting)
 * - Unused code detection (imports, variables)
 * - Commented code detection
 *
 * Interview Tip: Explain how each regex pattern works:
 * - Lookahead/Lookbehind assertions for context-aware matching
 * - Capturing groups for extracting information
 * - Flags for case-insensitive and multiline matching
 *
 * @author Code Review Tool Project
 * @version 1.0.0
 */
public class RegexRuleEngine {

    // ============= SYNTAX ERROR PATTERNS =============

    /**
     * Regex Pattern: Missing Semicolon
     * Detects Java statements that don't end with a semicolon.
     *
     * Pattern Explanation:
     * - (?<![\{\}]) : Negative lookbehind - NOT preceded by { or }
     * - [a-zA-Z0-9\)\]\"\']+$ : Line ends with identifier, closing paren/bracket, or quote
     * - Excludes class/interface definitions and block statements
     *
     * Examples that will match:
     * - int x = 5
     * - System.out.println("hello")
     * - return value
     *
     * Examples that won't match (correct):
     * - int x = 5;
     * - } // closing brace
     */
    public static final String MISSING_SEMICOLON =
            "^\\s*[a-zA-Z_].*[a-zA-Z0-9\\)\\]\\\"\\']\\s*$(?<!.*\\{)(?<!.*\\()";

    /**
     * Regex Pattern: Unmatched Braces
     * Detects potentially unmatched opening braces, closing braces without matching opening.
     *
     * This is a simplified check - a full implementation would maintain brace count.
     */
    public static final String UNMATCHED_BRACES = "(?<!\\\\)\\{(?!.*\\})";

    /**
     * Regex Pattern: Incomplete Try-Catch
     * Detects try blocks without corresponding catch or finally.
     *
     * Pattern: try with opening brace but no catch/finally visible
     */
    public static final String INCOMPLETE_TRY_CATCH =
            "try\\s*\\{[^}]*\\}(?!\\s*(catch|finally))";

    /**
     * Regex Pattern: Possible Null Pointer Access
     * Detects variable access without null checks.
     *
     * Matches patterns like: object.method() or array[i]
     * when used without prior null validation.
     */
    public static final String NULL_POINTER_RISK =
            "(?<!if\\s*\\(.*!=\\s*null)[a-zA-Z_][a-zA-Z0-9_]*\\.(\\w+)\\(";

    // ============= NAMING CONVENTION PATTERNS =============

    /**
     * Regex Pattern: Non-camelCase Variable Names
     * Detects variable/method declarations that don't follow camelCase convention.
     *
     * Correct camelCase: myVariable, firstName, getUserData
     * Incorrect: my_variable, MyVariable, my123Variable, _privateVar
     *
     * Pattern Explanation:
     * - (?:private|public|protected)? : Optional access modifier
     * - (?:static)? : Optional static keyword
     * - \\w+ : Type (e.g., int, String, List)
     * - [A-Z] : STARTS WITH UPPERCASE (violation!)
     * - Or contains underscore (violation!)
     *
     * Used for: Variables, method parameters
     */
    public static final String NON_CAMEL_CASE_VARIABLE =
            "(?:private|public|protected)?\\s*(?:static)?\\s*(?:final)?\\s*\\w+\\s+([A-Z_][a-zA-Z0-9_]*)\\s*[=;,\\)]";

    /**
     * Regex Pattern: Non-CONSTANT_CASE Constants
     * Detects constant declarations not following CONSTANT_CASE convention.
     *
     * Correct: public static final int MAX_SIZE = 100;
     * Incorrect: public static final int max_size = 100;
     *            public static final int MaxSize = 100;
     *
     * Pattern: static final without all caps
     */
    public static final String NON_CONSTANT_CASE =
            "(?:public|private|protected)?\\s*static\\s+final\\s+\\w+\\s+([a-z][a-zA-Z0-9]*)\\s*=";

    /**
     * Regex Pattern: Class Name Violation
     * Detects class declarations not starting with uppercase letter.
     *
     * Correct: class MyClass, interface IRepository
     * Incorrect: class myClass, class my_class
     */
    public static final String CLASS_NAME_VIOLATION =
            "class\\s+([a-z_][a-zA-Z0-9_]*)\\s*";

    /**
     * Regex Pattern: Method Name Violation
     * Detects method declarations not following camelCase (starting lowercase).
     *
     * Correct: public void getValue(), private int calculateTotal()
     * Incorrect: public void GetValue(), public void get_value()
     */
    public static final String METHOD_NAME_VIOLATION =
            "(?:public|private|protected)?\\s*(?:static)?\\s*(?:synchronized)?\\s*\\w+\\s+([A-Z_][a-zA-Z0-9_]*)\\s*\\(";

    // ============= UNUSED CODE PATTERNS =============

    /**
     * Regex Pattern: Unused Imports
     * Detects import statements that might not be used.
     *
     * Note: Full detection requires tracking actual usage throughout the file.
     * This pattern identifies imports that are less likely to be used:
     * - Imports of entire packages (e.g., import java.util.*;)
     * - Duplicate imports
     *
     * Pattern: import statement
     */
    public static final String UNUSED_IMPORT =
            "^import\\s+[a-zA-Z0-9\\.]+\\.\\*;$";

    /**
     * Regex Pattern: Unused Variable Declaration
     * Detects variables that are declared but never referenced.
     *
     * Simplified version - looks for declarations followed by immediate } or code block end.
     * Full implementation would need AST analysis or more sophisticated parsing.
     *
     * Example: int unused = 5; // no further reference to 'unused'
     */
    public static final String UNUSED_VARIABLE =
            "(?:private|public|protected)?\\s*(?:static)?\\s*(?:final)?\\s*\\w+\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*[^;]*;\\s*(?=\\}|$)";

    // ============= CODE COMPLEXITY PATTERNS =============

    /**
     * Regex Pattern: Long Method (>20 lines)
     * Detects method declarations that might be too long.
     *
     * This is a simplified check - full detection requires counting lines inside method.
     * Pattern detects method opening but needs line counting in analyzer.
     */
    public static final String LONG_METHOD_START =
            "(?:public|private|protected)?\\s*(?:static)?\\s*(?:synchronized)?\\s*\\w+\\s+\\w+\\s*\\([^)]*\\)\\s*\\{";

    /**
     * Regex Pattern: Deeply Nested Blocks
     * Detects excessive nesting levels (indentation depth).
     *
     * This pattern looks for deeply indented code (more than 4 levels).
     * Each level is typically 4 spaces or 1 tab.
     *
     * Example:
     * if (...) {           // Level 1
     *     if (...) {       // Level 2
     *         if (...) {   // Level 3
     *             if (...) { // Level 4 - TOO DEEP!
     */
    public static final String DEEPLY_NESTED =
            "^\\s{16,}[\\w\\{]"; // 16+ spaces = 4+ levels of indentation

    /**
     * Regex Pattern: Complex Conditional
     * Detects overly complex if conditions (too many && and || operators).
     *
     * Matches if statements with 3 or more logical operators.
     */
    public static final String COMPLEX_CONDITION =
            "if\\s*\\([^)]*&&[^)]*&&[^)]*[\\||&&]";

    // ============= COMMENTED CODE PATTERNS =============

    /**
     * Regex Pattern: Commented-out Code
     * Detects lines that look like actual code but are commented out.
     *
     * Patterns:
     * - // variable_name = value
     * - // methodName()
     * - // return something
     *
     * Excludes documentation comments (javadoc style and ////)
     * Excludes comment-only lines (no actual code)
     */
    public static final String COMMENTED_OUT_CODE =
            "//\\s*(?!\\/)(?!\\s*)([a-zA-Z_][a-zA-Z0-9_]*\\s*[=\\(\\.]|return|for|while|if|switch)";

    /**
     * Regex Pattern: TODO/FIXME Comments
     * Detects incomplete work marked in comments.
     *
     * Useful for identifying areas that need attention.
     * Example: // TODO: refactor this method
     */
    public static final String TODO_FIXME =
            "//\\s*(TODO|FIXME|HACK|XXX|BUG)\\s*:?\\s*(.+)";

    /**
     * Regex Pattern: Dead Code Comments
     * Detects multi-line commented blocks (often indicate removed code).
     */
    public static final String DEAD_CODE_BLOCK =
            "/\\*\\s*[a-zA-Z0-9_].*\\*/";

    // ============= BEST PRACTICE PATTERNS =============

    /**
     * Regex Pattern: Hardcoded Strings (Magic Strings)
     * Detects hardcoded string values that should be constants.
     *
     * Example: System.out.println("Error: Invalid input")
     * Should be: System.out.println(ERROR_MESSAGE)
     */
    public static final String MAGIC_STRING =
            "[^\\w]\"[a-zA-Z][a-zA-Z0-9\\s,\\.!?:;-]{5,}\"";

    /**
     * Regex Pattern: Hardcoded Numbers (Magic Numbers)
     * Detects hardcoded numeric values that should be named constants.
     *
     * Example: if (age > 18)
     * Should be: if (age > LEGAL_AGE)
     */
    public static final String MAGIC_NUMBER =
            "(?<!\\.)\\b([0-9]{2,}|0x[0-9a-fA-F]+)\\b(?![a-zA-Z0-9_])";

    /**
     * Regex Pattern: Using System.out for Logging
     * Detects direct System.out.println usage (should use proper logging framework).
     *
     * Professional apps use SLF4J, Log4j, or similar.
     */
    public static final String IMPROPER_LOGGING =
            "System\\.(out|err)\\.(println|print|printf)";

    /**
     * Regex Pattern: Empty Catch Blocks
     * Detects catch blocks that don't handle exceptions.
     *
     * This is a code smell - exceptions should always be handled somehow.
     */
    public static final String EMPTY_CATCH =
            "catch\\s*\\([^)]+\\)\\s*\\{\\s*\\}";

    /**
     * Regex Pattern: Multiple Return Statements
     * Detects methods with more than one return point.
     *
     * While not always wrong, multiple returns make code harder to follow.
     * Better approach: single exit point at the end.
     */
    public static final String MULTIPLE_RETURNS =
            "return\\s+[^;]*;.*return\\s+[^;]*;";

    // ============= HELPER METHOD: COMPILE PATTERN =============

    /**
     * Compile a regex pattern string into a Pattern object.
     * Useful for pattern caching and error handling.
     *
     * @param patternString The regex pattern as a string
     * @param flags Optional Pattern flags (e.g., Pattern.CASE_INSENSITIVE)
     * @return A compiled Pattern object ready for matching
     * @throws PatternSyntaxException If the regex syntax is invalid
     */
    public static Pattern compilePattern(String patternString, int flags) {
        try {
            return Pattern.compile(patternString, flags);
        } catch (PatternSyntaxException e) {
            System.err.println("Invalid regex pattern: " + patternString);
            throw e;
        }
    }

    /**
     * Compile a regex pattern string with MULTILINE and DOTALL flags.
     * These flags are useful for matching across multiple lines.
     *
     * @param patternString The regex pattern as a string
     * @return A compiled Pattern object with MULTILINE and DOTALL flags
     */
    public static Pattern compileMultilinePattern(String patternString) {
        return Pattern.compile(patternString, Pattern.MULTILINE | Pattern.DOTALL);
    }

    /**
     * Test if a string matches a regex pattern.
     * Convenience method for quick pattern matching without compilation.
     *
     * @param text The text to test
     * @param pattern The regex pattern
     * @return true if the pattern matches the text
     */
    public static boolean matches(String text, String pattern) {
        try {
            return Pattern.matches(pattern, text);
        } catch (PatternSyntaxException e) {
            System.err.println("Regex pattern error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Find all matches of a pattern in text.
     *
     * @param text The text to search
     * @param pattern The compiled Pattern
     * @return A list of all match results
     */
    public static List<MatchResult> findAllMatches(String text, Pattern pattern) {
        List<MatchResult> results = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            results.add(matcher.toMatchResult());
        }
        return results;
    }

    /**
     * Extract captured groups from a pattern match.
     *
     * Useful for extracting variable names, method names, etc. from code.
     *
     * @param text The text to search
     * @param pattern The compiled Pattern with capturing groups
     * @param groupIndex Which group to extract (0 = full match, 1+ = captured groups)
     * @return A list of matched group strings
     */
    public static List<String> extractGroups(String text, Pattern pattern, int groupIndex) {
        List<String> groups = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            if (groupIndex <= matcher.groupCount()) {
                String group = matcher.group(groupIndex);
                if (group != null) {
                    groups.add(group);
                }
            }
        }
        return groups;
    }
}
