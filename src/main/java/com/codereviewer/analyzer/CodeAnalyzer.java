package com.codereviewer.analyzer;

import com.codereviewer.model.CodeIssue;
import com.codereviewer.model.IssueSeverity;
import java.util.*;
import java.util.regex.*;

/**
 * CodeAnalyzer class orchestrates the code analysis process.
 *
 * Responsibilities:
 * - Apply regex rules from RegexRuleEngine to source code
 * - Detect and catalog code issues
 * - Manage analysis state and results
 * - Provide analysis summary statistics
 *
 * Design Pattern: Facade Pattern
 * - Provides simplified interface to complex regex analysis
 * - Hides regex complexity from client code
 * - Coordinates between CodeReader and RegexRuleEngine
 *
 * Architecture:
 * - analyze(List<String> lines) : Main analysis method
 * - Individual methods for each rule type (syntax, naming, complexity, etc.)
 * - Internal helper methods for pattern matching and issue creation
 *
 * @author Code Review Tool Project
 * @version 1.0.0
 */
public class CodeAnalyzer {
    // Analysis results
    private final List<CodeIssue> issues;

    // File context
    private String currentFileName;
    private List<String> currentLines;

    // Statistics
    private int syntaxErrors;
    private int namingViolations;
    private int complexityIssues;
    private int unusedCodeIssues;
    private int bestPracticeIssues;

    /**
     * Constructor - Initialize empty issue list and zero statistics.
     */
    public CodeAnalyzer() {
        this.issues = new ArrayList<>();
        resetStatistics();
    }

    /**
     * Main analysis method - Analyze a single Java file.
     *
     * Flow:
     * 1. Set file context
     * 2. Apply all analysis rules
     * 3. Return collected issues
     *
     * @param fileName The name of the file being analyzed (for reporting)
     * @param lines The source code lines (from CodeReader)
     * @return List of CodeIssue objects found
     */
    public List<CodeIssue> analyze(String fileName, List<String> lines) {
        // Reset state for new file
        this.currentFileName = fileName;
        this.currentLines = new ArrayList<>(lines); // Make a copy to avoid modification
        List<CodeIssue> fileIssues = new ArrayList<>();

        // Run all analysis rules
        fileIssues.addAll(analyzeSyntaxErrors());
        fileIssues.addAll(analyzeNamingConventions());
        fileIssues.addAll(analyzeUnusedCode());
        fileIssues.addAll(analyzeCodeComplexity());
        fileIssues.addAll(analyzeBestPractices());

        // Add to overall results
        this.issues.addAll(fileIssues);

        return fileIssues;
    }

    /**
     * Analyze syntax errors: missing semicolons, unmatched braces, incomplete try-catch.
     *
     * @return List of syntax-related issues
     */
    private List<CodeIssue> analyzeSyntaxErrors() {
        List<CodeIssue> syntaxIssues = new ArrayList<>();

        for (int i = 0; i < currentLines.size(); i++) {
            String line = currentLines.get(i);
            int lineNumber = i + 1; // 1-based line numbering

            // Skip empty lines and comments
            if (line.trim().isEmpty() || line.trim().startsWith("//")) {
                continue;
            }

            // Rule 1: Missing Semicolon
            // Detects lines that should end with semicolon but don't
            if (isMissingSemicolon(line)) {
                CodeIssue issue = new CodeIssue(
                        currentFileName,
                        lineNumber,
                        "Syntax Error",
                        "Statement missing semicolon",
                        IssueSeverity.HIGH,
                        line.trim(),
                        "Add semicolon at the end of the statement"
                );
                syntaxIssues.add(issue);
                syntaxErrors++;
            }

            // Rule 2: Unmatched/Mismatched Braces
            // Note: This is a simplified check. A full implementation would maintain brace count.
            if (line.contains("{") && !line.trim().endsWith("}")) {
                // Check if there's an unclosed brace
                int openBraces = countOccurrences(line, "{");
                int closeBraces = countOccurrences(line, "}");
                if (openBraces > closeBraces) {
                    // This is expected for method/class definitions
                    // Only flag if it looks like a statement, not a block start
                    if (!isBlockStart(line)) {
                        CodeIssue issue = new CodeIssue(
                                currentFileName,
                                lineNumber,
                                "Syntax Error",
                                "Potentially unmatched brace",
                                IssueSeverity.MEDIUM,
                                line.trim(),
                                "Verify brace matching and block structure"
                        );
                        syntaxIssues.add(issue);
                        syntaxErrors++;
                    }
                }
            }

            // Rule 3: Empty Catch Blocks
            if (line.contains("catch") && line.trim().endsWith("{}")) {
                CodeIssue issue = new CodeIssue(
                        currentFileName,
                        lineNumber,
                        "Syntax Error",
                        "Empty catch block - exception not handled",
                        IssueSeverity.HIGH,
                        line.trim(),
                        "Add exception handling code or use try-with-resources"
                );
                syntaxIssues.add(issue);
                syntaxErrors++;
            }
        }

        return syntaxIssues;
    }

    /**
     * Analyze naming convention violations.
     * Checks: camelCase for variables, CONSTANT_CASE for constants, PascalCase for classes.
     *
     * @return List of naming convention issues
     */
    private List<CodeIssue> analyzeNamingConventions() {
        List<CodeIssue> namingIssues = new ArrayList<>();
        String fullCode = String.join("\n", currentLines);

        // Rule 1: Check for non-camelCase variable names
        Pattern varPattern = Pattern.compile("(?:private|public|protected)?\\s*(?:static)?\\s*(?:final)?\\s*\\w+\\s+([A-Z_][a-zA-Z0-9_]*)\\s*[=;,\\)]");
        Matcher varMatcher = varPattern.matcher(fullCode);

        while (varMatcher.find()) {
            String varName = varMatcher.group(1);
            // Skip if it's already in correct camelCase (starts with lowercase)
            if (Character.isUpperCase(varName.charAt(0))) {
                int lineNum = getLineNumber(varMatcher.start(), fullCode);
                CodeIssue issue = new CodeIssue(
                        currentFileName,
                        lineNum,
                        "Naming Convention",
                        "Variable name '" + varName + "' should use camelCase (start with lowercase)",
                        IssueSeverity.LOW,
                        getCodeSnippet(varMatcher.start(), varMatcher.end(), fullCode),
                        "Rename to: " + Character.toLowerCase(varName.charAt(0)) + varName.substring(1)
                );
                namingIssues.add(issue);
                namingViolations++;
            }
        }

        // Rule 2: Check for non-CONSTANT_CASE constants
        Pattern constPattern = Pattern.compile("(?:public|private|protected)?\\s*static\\s+final\\s+\\w+\\s+([a-z][a-zA-Z0-9_]*)\\s*=");
        Matcher constMatcher = constPattern.matcher(fullCode);

        while (constMatcher.find()) {
            String constName = constMatcher.group(1);
            // Constants should be ALL_CAPS
            if (!constName.equals(constName.toUpperCase())) {
                int lineNum = getLineNumber(constMatcher.start(), fullCode);
                CodeIssue issue = new CodeIssue(
                        currentFileName,
                        lineNum,
                        "Naming Convention",
                        "Constant name '" + constName + "' should use CONSTANT_CASE (all uppercase)",
                        IssueSeverity.LOW,
                        getCodeSnippet(constMatcher.start(), constMatcher.end(), fullCode),
                        "Rename to: " + constName.toUpperCase()
                );
                namingIssues.add(issue);
                namingViolations++;
            }
        }

        // Rule 3: Check for class names not starting with uppercase
        Pattern classPattern = Pattern.compile("class\\s+([a-z_][a-zA-Z0-9_]*)\\s*");
        Matcher classMatcher = classPattern.matcher(fullCode);

        while (classMatcher.find()) {
            String className = classMatcher.group(1);
            int lineNum = getLineNumber(classMatcher.start(), fullCode);
            CodeIssue issue = new CodeIssue(
                    currentFileName,
                    lineNum,
                    "Naming Convention",
                    "Class name '" + className + "' should start with uppercase",
                    IssueSeverity.LOW,
                    getCodeSnippet(classMatcher.start(), classMatcher.end(), fullCode),
                    "Rename to: " + Character.toUpperCase(className.charAt(0)) + className.substring(1)
            );
            namingIssues.add(issue);
            namingViolations++;
        }

        return namingIssues;
    }

    /**
     * Analyze unused code: unused imports, unused variables, commented-out code.
     *
     * @return List of unused code issues
     */
    private List<CodeIssue> analyzeUnusedCode() {
        List<CodeIssue> unusedIssues = new ArrayList<>();
        String fullCode = String.join("\n", currentLines);

        // Rule 1: Wildcard imports (often indicate unused imports)
        Pattern wildcardImport = Pattern.compile("^import\\s+[a-zA-Z0-9\\.]+\\.\\*;$", Pattern.MULTILINE);
        Matcher importMatcher = wildcardImport.matcher(fullCode);

        while (importMatcher.find()) {
            int lineNum = getLineNumber(importMatcher.start(), fullCode);
            CodeIssue issue = new CodeIssue(
                    currentFileName,
                    lineNum,
                    "Unused Code",
                    "Wildcard import - may import unused classes",
                    IssueSeverity.LOW,
                    getCodeSnippet(importMatcher.start(), importMatcher.end(), fullCode),
                    "Replace with explicit imports: import java.util.List; import java.util.Map;"
            );
            unusedIssues.add(issue);
            unusedCodeIssues++;
        }

        // Rule 2: Commented-out code
        Pattern commentedCode = Pattern.compile("//\\s*(?!\\/)(?!\\s*)([a-zA-Z_][a-zA-Z0-9_]*\\s*[=\\(\\.]|return|for|while|if|switch)");
        Matcher codeMatcher = commentedCode.matcher(fullCode);

        while (codeMatcher.find()) {
            int lineNum = getLineNumber(codeMatcher.start(), fullCode);
            CodeIssue issue = new CodeIssue(
                    currentFileName,
                    lineNum,
                    "Unused Code",
                    "Commented-out code detected - remove or uncomment",
                    IssueSeverity.MEDIUM,
                    getCodeSnippet(codeMatcher.start(), codeMatcher.end(), fullCode),
                    "Remove dead code from version control - use Git history if needed"
            );
            unusedIssues.add(issue);
            unusedCodeIssues++;
        }

        // Rule 3: TODO/FIXME comments
        Pattern todoPattern = Pattern.compile("//\\s*(TODO|FIXME|HACK|XXX)\\s*:?\\s*(.+)");
        Matcher todoMatcher = todoPattern.matcher(fullCode);

        while (todoMatcher.find()) {
            int lineNum = getLineNumber(todoMatcher.start(), fullCode);
            CodeIssue issue = new CodeIssue(
                    currentFileName,
                    lineNum,
                    "Incomplete Work",
                    "Found " + todoMatcher.group(1) + " comment: " + todoMatcher.group(2),
                    IssueSeverity.MEDIUM,
                    getCodeSnippet(todoMatcher.start(), todoMatcher.end(), fullCode),
                    "Address this TODO before committing to production"
            );
            unusedIssues.add(issue);
            unusedCodeIssues++;
        }

        return unusedIssues;
    }

    /**
     * Analyze code complexity: long methods, deep nesting, complex conditions.
     *
     * @return List of complexity-related issues
     */
    private List<CodeIssue> analyzeCodeComplexity() {
        List<CodeIssue> complexityIssues = new ArrayList<>();

        for (int i = 0; i < currentLines.size(); i++) {
            String line = currentLines.get(i);
            int lineNumber = i + 1;

            // Rule 1: Deeply nested code (more than 4 levels)
            // Count leading whitespace to determine indentation level
            int indentation = line.length() - line.stripLeading().length();
            int nestLevel = indentation / 4; // Assuming 4-space indentation

            if (nestLevel > 4 && !line.trim().isEmpty()) {
                CodeIssue issue = new CodeIssue(
                        currentFileName,
                        lineNumber,
                        "Code Complexity",
                        "Code nesting is too deep (" + nestLevel + " levels) - reduces readability",
                        IssueSeverity.MEDIUM,
                        line.trim(),
                        "Refactor: Extract method, use early return, or simplify conditions"
                );
                complexityIssues.add(issue);
                this.complexityIssues++;
            }

            // Rule 2: Complex conditions (multiple && and ||)
            if (line.contains("if") || line.contains("while")) {
                int logicalOperators = countOccurrences(line, "&&") + countOccurrences(line, "||");
                if (logicalOperators > 3) {
                    CodeIssue issue = new CodeIssue(
                            currentFileName,
                            lineNumber,
                            "Code Complexity",
                            "Condition has too many logical operators (" + logicalOperators + ") - hard to understand",
                            IssueSeverity.MEDIUM,
                            line.trim(),
                            "Extract condition to a named boolean method or use if-else chains"
                    );
                    complexityIssues.add(issue);
                    this.complexityIssues++;
                }
            }

            // Rule 3: Long method detection (simplified)
            // Method with more than 30 lines is often too long
            if (line.contains("(") && line.contains(")") && line.contains("{")) {
                // This is a method start, count until matching close brace
                int openBraces = countOccurrences(line, "{");
                int closeBraces = countOccurrences(line, "}");

                // If opening brace is on same line, method body starts here
                if (openBraces > closeBraces && i + 30 < currentLines.size()) {
                    // Simple heuristic: if there are 30+ lines of content after method start
                    CodeIssue issue = new CodeIssue(
                            currentFileName,
                            lineNumber,
                            "Code Complexity",
                            "Method may be too long (potential long method code smell)",
                            IssueSeverity.MEDIUM,
                            line.trim(),
                            "Consider breaking method into smaller, focused methods (Single Responsibility)"
                    );
                    // Note: This is heuristic; actual line count would need better parsing
                    complexityIssues.add(issue);
                    this.complexityIssues++;
                }
            }
        }

        return complexityIssues;
    }

    /**
     * Analyze best practices: magic strings/numbers, improper logging, empty catch blocks.
     *
     * @return List of best practice issues
     */
    private List<CodeIssue> analyzeBestPractices() {
        List<CodeIssue> practiceIssues = new ArrayList<>();
        String fullCode = String.join("\n", currentLines);

        // Rule 1: System.out/err usage (should use logging framework)
        Pattern loggingPattern = Pattern.compile("System\\.(out|err)\\.(println|print|printf)");
        Matcher loggingMatcher = loggingPattern.matcher(fullCode);

        while (loggingMatcher.find()) {
            int lineNum = getLineNumber(loggingMatcher.start(), fullCode);
            CodeIssue issue = new CodeIssue(
                    currentFileName,
                    lineNum,
                    "Best Practice",
                    "Using System.out/err for logging - use SLF4J or Log4j instead",
                    IssueSeverity.MEDIUM,
                    getCodeSnippet(loggingMatcher.start(), loggingMatcher.end(), fullCode),
                    "Use: private static final Logger logger = LoggerFactory.getLogger(MyClass.class);\nlogger.info(\"message\");"
            );
            practiceIssues.add(issue);
            bestPracticeIssues++;
        }

        // Rule 2: Magic strings/numbers
        Pattern magicStringPattern = Pattern.compile("[^\\w]\"[a-zA-Z][a-zA-Z0-9\\s,\\.!?:;-]{5,}\"");
        Matcher stringMatcher = magicStringPattern.matcher(fullCode);
        int magicStringCount = 0;

        while (stringMatcher.find() && magicStringCount < 3) { // Limit to 3 issues per file
            int lineNum = getLineNumber(stringMatcher.start(), fullCode);
            String match = getCodeSnippet(stringMatcher.start(), stringMatcher.end(), fullCode);
            CodeIssue issue = new CodeIssue(
                    currentFileName,
                    lineNum,
                    "Best Practice",
                    "Magic string detected: " + match + " - should be named constant",
                    IssueSeverity.LOW,
                    match,
                    "Define as: private static final String MESSAGE = " + match + ";"
            );
            practiceIssues.add(issue);
            bestPracticeIssues++;
            magicStringCount++;
        }

        // Rule 3: Magic numbers
        Pattern magicNumberPattern = Pattern.compile("(?<!\\.)\\b(\\d{2,}|0x[0-9a-fA-F]+)\\b(?![a-zA-Z0-9_])");
        Matcher numberMatcher = magicNumberPattern.matcher(fullCode);
        int magicNumberCount = 0;

        while (numberMatcher.find() && magicNumberCount < 3) { // Limit to 3 issues per file
            String number = numberMatcher.group(1);
            // Skip common harmless numbers
            if (number.equals("0") || number.equals("1") || number.equals("2")) {
                continue;
            }
            int lineNum = getLineNumber(numberMatcher.start(), fullCode);
            CodeIssue issue = new CodeIssue(
                    currentFileName,
                    lineNum,
                    "Best Practice",
                    "Magic number '" + number + "' should be named constant",
                    IssueSeverity.LOW,
                    number,
                    "Define as: private static final int THRESHOLD = " + number + ";"
            );
            practiceIssues.add(issue);
            bestPracticeIssues++;
            magicNumberCount++;
        }

        return practiceIssues;
    }

    /**
     * Get all issues found during analysis.
     *
     * @return List of all CodeIssue objects
     */
    public List<CodeIssue> getAllIssues() {
        // Sort by severity (highest first) then by file name and line number
        issues.sort((a, b) -> {
            int severityComp = b.getSeverity().getLevel() - a.getSeverity().getLevel();
            if (severityComp != 0) return severityComp;

            int fileComp = a.getFileName().compareTo(b.getFileName());
            if (fileComp != 0) return fileComp;

            return a.getLineNumber() - b.getLineNumber();
        });

        return issues;
    }

    /**
     * Get summary of analysis results.
     *
     * @return Summary string with counts of each issue type
     */
    public String getSummary() {
        return String.format(
                "Analysis Summary:\n" +
                        "  Total Issues: %d\n" +
                        "  Syntax Errors: %d\n" +
                        "  Naming Violations: %d\n" +
                        "  Unused Code Issues: %d\n" +
                        "  Complexity Issues: %d\n" +
                        "  Best Practice Issues: %d",
                issues.size(),
                syntaxErrors,
                namingViolations,
                unusedCodeIssues,
                complexityIssues,
                bestPracticeIssues
        );
    }

    /**
     * Get issue count by severity.
     *
     * @param severity The severity level to count
     * @return Number of issues with that severity
     */
    public int getIssueCountBySeverity(IssueSeverity severity) {
        return (int) issues.stream()
                .filter(issue -> issue.getSeverity() == severity)
                .count();
    }

    // ============= HELPER METHODS =============

    /**
     * Check if a line is missing a semicolon at the end.
     * Implements logic for detecting various statement types that should end with semicolon.
     *
     * @param line The line to check
     * @return true if line appears to be missing semicolon
     */
    private boolean isMissingSemicolon(String line) {
        String trimmed = line.trim();

        // Skip lines that should NOT end with semicolon
        if (trimmed.isEmpty() ||
                trimmed.endsWith("{") ||
                trimmed.endsWith("}") ||
                trimmed.endsWith(",") ||
                trimmed.startsWith("//") ||
                trimmed.startsWith("*") ||
                trimmed.startsWith("/*") ||
                trimmed.endsWith(":") ||
                trimmed.contains("class ") ||
                trimmed.contains("interface ") ||
                trimmed.contains("enum ") ||
                trimmed.startsWith("public ") && trimmed.contains("(") ||
                trimmed.startsWith("private ") && trimmed.contains("(") ||
                trimmed.startsWith("protected ") && trimmed.contains("(")) {
            return false;
        }

        // Check if line looks like a statement but doesn't end with semicolon
        return (trimmed.endsWith(")") ||
                trimmed.endsWith("\"") ||
                trimmed.endsWith("'") ||
                trimmed.endsWith("]")) &&
                !trimmed.endsWith(");") &&
                !trimmed.endsWith("\");") &&
                !trimmed.endsWith("');");
    }

    /**
     * Check if a line is the start of a block (class, method, if, etc.).
     *
     * @param line The line to check
     * @return true if line starts a new block
     */
    private boolean isBlockStart(String line) {
        String trimmed = line.trim();
        return trimmed.matches("^(class|interface|enum|if|else|while|for|switch|try|catch|finally).*\\{$");
    }

    /**
     * Count occurrences of a substring in a string.
     * Utility method for counting braces, operators, etc.
     *
     * @param text The text to search
     * @param substring The substring to count
     * @return Number of occurrences
     */
    private int countOccurrences(String text, String substring) {
        return text.split(Pattern.quote(substring), -1).length - 1;
    }

    /**
     * Get the line number for a position in full code string.
     * Counts newlines before the position.
     *
     * @param position Position in the code string
     * @param fullCode The complete code as string
     * @return 1-based line number
     */
    private int getLineNumber(int position, String fullCode) {
        return fullCode.substring(0, Math.min(position, fullCode.length()))
                .split("\n", -1).length;
    }

    /**
     * Get a code snippet around a position.
     * Used for displaying context in issue reports.
     *
     * @param start Start position
     * @param end End position
     * @param fullCode The complete code
     * @return Snippet of code
     */
    private String getCodeSnippet(int start, int end, String fullCode) {
        int snippetStart = Math.max(0, start - 20);
        int snippetEnd = Math.min(fullCode.length(), end + 20);
        return fullCode.substring(snippetStart, snippetEnd).replaceAll("\n", " ");
    }

    /**
     * Reset statistics for new analysis.
     */
    private void resetStatistics() {
        syntaxErrors = 0;
        namingViolations = 0;
        complexityIssues = 0;
        unusedCodeIssues = 0;
        bestPracticeIssues = 0;
    }
}
