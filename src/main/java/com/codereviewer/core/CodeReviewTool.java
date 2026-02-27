package com.codereviewer.core;

import com.codereviewer.analyzer.CodeAnalyzer;
import com.codereviewer.model.CodeIssue;
import com.codereviewer.model.IssueSeverity;
import com.codereviewer.report.ReportGenerator;

import java.io.IOException;
import java.util.*;

/**
 * CodeReviewTool class - Main orchestrator for the code review process.
 *
 * Responsibilities:
 * - Coordinate between CodeReader, CodeAnalyzer, and ReportGenerator
 * - Manage the overall workflow
 * - Provide convenient API for users
 *
 * Design Pattern: Facade Pattern
 * - Simplifies the complex code review process
 * - Hides implementation details from client code
 * - Provides a single entry point for all operations
 *
 * Workflow:
 * 1. Initialize tool with configuration
 * 2. Read Java source files
 * 3. Analyze code using CodeAnalyzer
 * 4. Generate HTML report
 * 5. Print summary to console
 *
 * Usage Example:
 * CodeReviewTool tool = new CodeReviewTool();
 * tool.analyzeFile("path/to/file.java");
 * tool.generateReport("Code Review");
 *
 * @author Code Review Tool Project
 * @version 1.0.0
 */
public class CodeReviewTool {
    // Core components (all set in constructor)
    private final CodeReader codeReader;
    private final CodeAnalyzer codeAnalyzer;

    // Configuration
    private String reportTitle;
    private String reportOutputPath;

    /**
     * Constructor - Initialize the code review tool.
     */
    public CodeReviewTool() {
        this.codeReader = new CodeReader();
        this.codeAnalyzer = new CodeAnalyzer();
        this.reportTitle = "Code Review Report";
        this.reportOutputPath = "code_review_report.html";

        System.out.println("=".repeat(60));
        System.out.println("Java Code Review Tool v1.0.0");
        System.out.println("=".repeat(60));
    }

    /**
     * Set the report title (displayed in HTML report).
     *
     * @param title The title for the report
     */
    public void setReportTitle(String title) {
        this.reportTitle = title;
    }

    /**
     * Set the output path for the HTML report.
     *
     * @param path The file path where report will be saved
     */
    public void setReportOutputPath(String path) {
        this.reportOutputPath = path;
    }

    /**
     * Analyze a single Java file.
     *
     * Process:
     * 1. Read the file
     * 2. Pass to analyzer
     * 3. Collect and store issues
     *
     * @param filePath Path to the Java file to analyze
     * @throws IOException If file cannot be read
     */
    public void analyzeFile(String filePath) throws IOException {
        System.out.println("\n[Tool] Analyzing file: " + filePath);

        // Validate file
        if (!CodeReader.isValidJavaFile(filePath)) {
            throw new IOException("Invalid Java file: " + filePath);
        }

        // Read file
        List<String> lines = codeReader.readFile(filePath);

        // Analyze
        String fileName = CodeReader.getFileName(filePath);
        List<CodeIssue> issues = codeAnalyzer.analyze(fileName, lines);

        // Print results for this file
        System.out.println("[Tool] Found " + issues.size() + " issues");
    }

    /**
     * Analyze all Java files in a directory (recursively).
     *
     * Process:
     * 1. Find all .java files in directory
     * 2. Analyze each file
     * 3. Aggregate results
     *
     * @param directoryPath Path to directory containing Java files
     * @throws IOException If directory cannot be accessed
     */
    public void analyzeDirectory(String directoryPath) throws IOException {
        System.out.println("\n[Tool] Analyzing directory: " + directoryPath);

        // Find all Java files
        List<String> javaFiles = codeReader.findAllJavaFiles(directoryPath);

        if (javaFiles.isEmpty()) {
            System.out.println("[Tool] No Java files found in directory");
            return;
        }

        // Analyze each file
        int totalIssues = 0;
        for (String filePath : javaFiles) {
            try {
                List<String> lines = codeReader.readFile(filePath);
                String fileName = CodeReader.getFileName(filePath);
                List<CodeIssue> issues = codeAnalyzer.analyze(fileName, lines);
                totalIssues += issues.size();
                System.out.println("  ✓ " + fileName + " : " + issues.size() + " issues");
            } catch (IOException e) {
                System.err.println("  ✗ Error analyzing " + filePath + ": " + e.getMessage());
            }
        }

        System.out.println("[Tool] Total issues found: " + totalIssues);
    }

    /**
     * Generate an HTML report with all analysis results.
     *
     * Process:
     * 1. Create ReportGenerator with collected issues
     * 2. Configure report output
     * 3. Generate HTML file
     *
     * @throws IOException If report cannot be written
     */
    public void generateReport() throws IOException {
        System.out.println("\n[Tool] Generating HTML report...");

        List<CodeIssue> allIssues = codeAnalyzer.getAllIssues();

        ReportGenerator generator = new ReportGenerator(reportTitle, allIssues);
        generator.setOutputPath(reportOutputPath);
        generator.generateReport();

        System.out.println("[Tool] Report saved to: " + reportOutputPath);
    }

    /**
     * Print analysis summary to console.
     *
     * Displays:
     * - Total issues
     * - Breakdown by severity
     * - Stats by issue type
     */
    public void printSummary() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(codeAnalyzer.getSummary());
        System.out.println("=".repeat(60));

        // Print issues by severity
        System.out.println("\nBreakdown by Severity:");
        System.out.println("  HIGH:   " + codeAnalyzer.getIssueCountBySeverity(IssueSeverity.HIGH) + " issues");
        System.out.println("  MEDIUM: " + codeAnalyzer.getIssueCountBySeverity(IssueSeverity.MEDIUM) + " issues");
        System.out.println("  LOW:    " + codeAnalyzer.getIssueCountBySeverity(IssueSeverity.LOW) + " issues");

        // Print top issues
        List<CodeIssue> allIssues = codeAnalyzer.getAllIssues();
        if (!allIssues.isEmpty()) {
            System.out.println("\nTop 5 Critical Issues:");
            List<CodeIssue> highSeverity = allIssues.stream()
                    .filter(i -> i.getSeverity() == IssueSeverity.HIGH)
                    .limit(5)
                    .toList();

            if (highSeverity.isEmpty()) {
                System.out.println("  No high-severity issues found!");
            } else {
                for (int i = 0; i < highSeverity.size(); i++) {
                    CodeIssue issue = highSeverity.get(i);
                    System.out.println("  " + (i + 1) + ". [" + issue.getFileName() + ":" +
                            issue.getLineNumber() + "] " + issue.getDescription());
                }
            }
        }
    }

    /**
     * Get all analyzed issues.
     *
     * @return List of CodeIssue objects
     */
    public List<CodeIssue> getIssues() {
        return codeAnalyzer.getAllIssues();
    }

    /**
     * Get analyzer for direct access.
     * Useful for advanced usage or custom analysis.
     *
     * @return The CodeAnalyzer instance
     */
    public CodeAnalyzer getAnalyzer() {
        return codeAnalyzer;
    }

    /**
     * Get code reader for direct file access.
     * Useful for custom file handling.
     *
     * @return The CodeReader instance
     */
    public CodeReader getCodeReader() {
        return codeReader;
    }
}
