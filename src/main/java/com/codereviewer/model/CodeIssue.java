package com.codereviewer.model;

/**
 * Data model class representing a code issue found during analysis.
 * Encapsulates all information about a specific code quality or style issue.
 *
 * This class follows the Single Responsibility Principle:
 * - Responsible ONLY for storing and providing access to issue data
 * - Used by CodeAnalyzer to report findings
 * - Used by ReportGenerator to display findings
 *
 * @author Code Review Tool Project
 * @version 1.0.0
 */
public class CodeIssue {
    // Issue metadata
    private final String fileName;      // Name of the file where issue was found
    private final int lineNumber;       // Line number where issue is located
    private final String issueType;     // Category of the issue (e.g., "Syntax Error", "Naming Convention")
    private final String description;   // Human-readable description of the issue
    private final IssueSeverity severity; // How serious this issue is

    // Detailed information
    private final String snippet;       // Code snippet showing the problematic code
    private String suggestion;          // Recommended fix or improvement

    /**
     * Constructor for CodeIssue - Basic version.
     *
     * @param fileName The name of the file containing the issue
     * @param lineNumber The line number of the issue
     * @param issueType Category of the issue
     * @param description Human-readable description
     * @param severity How serious the issue is
     * @param snippet The actual code snippet showing the issue
     */
    public CodeIssue(String fileName, int lineNumber, String issueType,
                     String description, IssueSeverity severity, String snippet) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.issueType = issueType;
        this.description = description;
        this.severity = severity;
        this.snippet = snippet;
        this.suggestion = ""; // Default: no suggestion
    }

    /**
     * Constructor for CodeIssue - Complete version with suggestion.
     *
     * @param fileName The name of the file containing the issue
     * @param lineNumber The line number of the issue
     * @param issueType Category of the issue
     * @param description Human-readable description
     * @param severity How serious the issue is
     * @param snippet The actual code snippet showing the issue
     * @param suggestion Recommended fix or improvement
     */
    public CodeIssue(String fileName, int lineNumber, String issueType,
                     String description, IssueSeverity severity, String snippet,
                     String suggestion) {
        this(fileName, lineNumber, issueType, description, severity, snippet);
        this.suggestion = suggestion;
    }

    // Getter methods - All follow encapsulation principle (private fields, public accessors)

    /**
     * Get the name of the file containing this issue.
     *
     * @return The file name (e.g., "ProductController.java")
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Get the line number where this issue is located.
     *
     * @return The 1-based line number
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Get the type/category of this issue.
     *
     * @return The issue type (e.g., "Syntax Error", "Naming Convention")
     */
    public String getIssueType() {
        return issueType;
    }

    /**
     * Get the detailed description of this issue.
     *
     * @return Human-readable description explaining what's wrong
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the severity level of this issue.
     *
     * @return The severity level (LOW, MEDIUM, or HIGH)
     */
    public IssueSeverity getSeverity() {
        return severity;
    }

    /**
     * Get the code snippet showing the problematic code.
     *
     * @return The actual code that has the issue
     */
    public String getSnippet() {
        return snippet;
    }

    /**
     * Get the suggested fix or improvement for this issue.
     *
     * @return The recommended correction or enhancement
     */
    public String getSuggestion() {
        return suggestion;
    }

    /**
     * Set the suggested fix for this issue.
     *
     * @param suggestion The recommended correction
     */
    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    /**
     * String representation of the issue - useful for debugging and console output.
     *
     * @return Formatted string showing the issue details
     */
    @Override
    public String toString() {
        return String.format(
                "[%s] %s (Line %d): %s - %s",
                severity.getLabel(),
                fileName,
                lineNumber,
                issueType,
                description
        );
    }

    /**
     * Comparison method for sorting issues by severity level (descending) and line number.
     *
     * @param other The issue to compare with
     * @return Negative if this issue is more severe, positive if less severe
     */
    public int compareByPriority(CodeIssue other) {
        // Higher severity first
        int severityComparison = other.severity.getLevel() - this.severity.getLevel();
        if (severityComparison != 0) {
            return severityComparison;
        }
        // Then by line number (ascending)
        return this.lineNumber - other.lineNumber;
    }
}
