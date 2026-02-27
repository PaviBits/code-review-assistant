package com.codereviewer.model;

/**
 * Enumeration for issue severity levels.
 * Provides a standardized way to categorize code review findings.
 *
 * @author Code Review Tool Project
 * @version 1.0.0
 */
public enum IssueSeverity {
    /**
     * Low severity: Code works but has style/convention issues.
     * Example: Variable naming not following camelCase convention.
     */
    LOW("LOW", 1),

    /**
     * Medium severity: Code has potential issues affecting maintainability.
     * Example: Unused imports, unused variables, or overly complex methods.
     */
    MEDIUM("MEDIUM", 2),

    /**
     * High severity: Critical issues that may cause errors or bugs.
     * Example: Missing semicolons, unmatched braces, syntax errors.
     */
    HIGH("HIGH", 3);

    // Severity label (for display purposes)
    private final String label;

    // Severity level (for sorting, where 3 = highest priority)
    private final int level;

    /**
     * Constructor for IssueSeverity enum.
     *
     * @param label The display label for this severity level
     * @param level The numeric level (higher = more severe)
     */
    IssueSeverity(String label, int level) {
        this.label = label;
        this.level = level;
    }

    /**
     * Get the display label for this severity level.
     *
     * @return The string label (e.g., "LOW", "MEDIUM", "HIGH")
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get the numeric severity level.
     *
     * @return The numeric level (1 = lowest, 3 = highest)
     */
    public int getLevel() {
        return level;
    }

    /**
     * Get the CSS color class name for HTML report styling.
     * Used to color-code issues in the generated HTML report.
     *
     * @return CSS class name (e.g., "severity-low", "severity-high")
     */
    public String getCssClass() {
        return "severity-" + label.toLowerCase();
    }
}
