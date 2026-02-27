package com.codereviewer;

import com.codereviewer.core.CodeReviewTool;
import java.io.IOException;
import java.util.Scanner;

/**
 * Main class - Entry point for the Java Code Review Tool application.
 *
 * Responsibilities:
 * - Parse command-line arguments or accept interactive input
 * - Initialize CodeReviewTool
 * - Orchestrate the analysis workflow
 * - Handle errors gracefully
 *
 * Usage:
 * 1. Analyze single file:
 *    java Main /path/to/File.java
 *
 * 2. Analyze directory:
 *    java Main /path/to/directory
 *
 * 3. Interactive mode:
 *    java Main
 *    (prompts for input)
 *
 * Interview Note:
 * This class demonstrates:
 * - Clean separation of concerns (Main class is lightweight)
 * - Proper error handling and user feedback
 * - Support for both CLI and interactive modes
 * - Professional exception management
 *
 * @author Code Review Tool Project
 * @version 1.0.0
 */
public class Main {
    /**
     * Main method - Application entry point.
     *
     * Flow:
     * 1. Check for command-line arguments
     * 2. If arguments provided: analyze specified path
     * 3. If no arguments: enter interactive mode
     *
     * @param args Command-line arguments: [path-to-analyze]
     */
    public static void main(String[] args) {
        try {
            // Create the code review tool
            CodeReviewTool tool = new CodeReviewTool();

            if (args.length == 0) {
                // Interactive mode
                interactiveMode(tool);
            } else {
                // CLI mode
                String path = args[0];
                analyzeTarget(tool, path);
            }

        } catch (Exception e) {
            System.err.println("\n[ERROR] Fatal error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Interactive mode - Prompts user for input and analysis options.
     *
     * Features:
     * - Ask for file/directory path
     * - Ask for report title
     * - Ask for output location
     * - Analyze and generate report
     *
     * @param tool The CodeReviewTool instance
     */
    private static void interactiveMode(CodeReviewTool tool) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n========== INTERACTIVE MODE ==========");
        System.out.println("This tool analyzes Java code for quality issues.\n");

        // Get path to analyze
        System.out.print("Enter path to Java file or directory: ");
        String path = scanner.nextLine().trim();

        if (path.isEmpty()) {
            System.err.println("Error: Path cannot be empty");
            scanner.close();
            return;
        }

        // Get report title
        System.out.print("Enter report title (press Enter for default): ");
        String title = scanner.nextLine().trim();
        if (!title.isEmpty()) {
            tool.setReportTitle(title);
        }

        // Get output path
        System.out.print("Enter output file path (press Enter for 'code_review_report.html'): ");
        String outputPath = scanner.nextLine().trim();
        if (!outputPath.isEmpty()) {
            tool.setReportOutputPath(outputPath);
        }

        scanner.close();

        // Perform analysis
        System.out.println("\n========== ANALYSIS STARTED ==========\n");
        analyzeTarget(tool, path);
    }

    /**
     * Analyze a target file or directory.
     * Handles both single files and directories transparently.
     *
     * @param tool The CodeReviewTool instance
     * @param path The file or directory path
     */
    private static void analyzeTarget(CodeReviewTool tool, String path) {
        try {
            // Determine if target is file or directory
            java.nio.file.Path targetPath = java.nio.file.Paths.get(path);

            if (!java.nio.file.Files.exists(targetPath)) {
                System.err.println("Error: Path does not exist: " + path);
                return;
            }

            // Analyze
            if (java.nio.file.Files.isRegularFile(targetPath)) {
                System.out.println("[Main] Analyzing single file...");
                tool.analyzeFile(path);
            } else if (java.nio.file.Files.isDirectory(targetPath)) {
                System.out.println("[Main] Analyzing directory...");
                tool.analyzeDirectory(path);
            } else {
                System.err.println("Error: Path is neither file nor directory: " + path);
                return;
            }

            // Generate report
            System.out.println("\n[Main] Generating report...");
            tool.generateReport();

            // Print summary
            tool.printSummary();

            System.out.println("\n========== ANALYSIS COMPLETE ==========");
            System.out.println("✓ Code review finished successfully!");

        } catch (IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Display help/usage information.
     * Can be called with -h or --help argument.
     */
    private static void displayHelp() {
        System.out.println("""
                ╔════════════════════════════════════════════════════╗
                ║     Java Code Review Tool - Help & Usage           ║
                ╚════════════════════════════════════════════════════╝

                USAGE:
                ------

                1. Single File Analysis:
                   java Main path/to/MyFile.java

                2. Directory Analysis:
                   java Main path/to/directory

                3. Interactive Mode:
                   java Main
                   (You'll be prompted for input)

                4. Display Help:
                   java Main -h
                   java Main --help

                FEATURES:
                ---------
                ✓ Detects syntax errors (missing semicolons, unmatched braces)
                ✓ Checks naming conventions (camelCase, CONSTANT_CASE)
                ✓ Finds unused code (imports, variables, commented code)
                ✓ Analyzes code complexity (nesting, method length)
                ✓ Identifies best practice violations
                ✓ Generates professional HTML reports

                OUTPUT:
                -------
                - Console summary with issue statistics
                - Detailed HTML report with:
                  • Issues grouped by file and severity
                  • Code snippets showing problems
                  • Suggested fixes
                  • Professional styling and layout

                EXAMPLE:
                --------
                java Main src/main/java
                (Analyzes all .java files in src/main/java and subdirectories)
                """);
    }
}
