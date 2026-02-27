package com.codereviewer.core;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CodeReader class handles all file I/O operations.
 *
 * Responsibilities:
 * - Reading Java source files (.java)
 * - Listing all Java files in a directory (recursive)
 * - Providing file content with line numbers
 * - Error handling for file operations
 *
 * Design Pattern: Single Responsibility Principle
 * - This class is ONLY responsible for reading/accessing files
 * - Analysis logic is delegated to CodeAnalyzer
 * - This ensures clean separation of concerns
 *
 * @author Code Review Tool Project
 * @version 1.0.0
 */
public class CodeReader {
    // Class-level logger for debugging purposes
    private static final String LOG_PREFIX = "[CodeReader]";

    /**
     * Read a single Java file and return its content as a list of lines.
     * Each line is trimmed of leading/trailing whitespace but preserves internal spacing.
     *
     * Performance Note: Uses Files.readAllLines() for simplicity. For very large files
     * (>10MB), consider using BufferedReader with streaming approach.
     *
     * @param filePath The absolute or relative path to the Java file
     * @return A List of strings, where each string is one line of code
     * @throws IOException If file cannot be read or doesn't exist
     */
    public List<String> readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        // Validate file exists and is readable
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }
        if (!Files.isReadable(path)) {
            throw new IOException("File is not readable: " + filePath);
        }

        try {
            // Read all lines from the file
            List<String> lines = Files.readAllLines(path);
            System.out.println(LOG_PREFIX + " Read " + lines.size() + " lines from: " + filePath);
            return lines;
        } catch (IOException e) {
            System.err.println(LOG_PREFIX + " Error reading file: " + filePath);
            throw e;
        }
    }

    /**
     * Read a Java file and return content as a single string.
     * Useful for regex pattern matching across the entire file.
     *
     * @param filePath The path to the Java file
     * @return Complete file content as a single string with newlines preserved
     * @throws IOException If file cannot be read
     */
    public String readFileAsString(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    /**
     * Get a specific line from a file.
     * Useful for displaying the exact code that has an issue.
     *
     * @param filePath The path to the Java file
     * @param lineNumber The line number (1-based indexing)
     * @return The content of that line, or empty string if line doesn't exist
     * @throws IOException If file cannot be read
     */
    public String getLine(String filePath, int lineNumber) throws IOException {
        List<String> lines = readFile(filePath);
        if (lineNumber > 0 && lineNumber <= lines.size()) {
            return lines.get(lineNumber - 1); // Convert 1-based to 0-based indexing
        }
        return "";
    }

    /**
     * Get lines in a range from a file (useful for showing context around an issue).
     *
     * @param filePath The path to the Java file
     * @param startLine Start line number (1-based)
     * @param endLine End line number (1-based, inclusive)
     * @return List of lines in the specified range
     * @throws IOException If file cannot be read
     */
    public List<String> getLineRange(String filePath, int startLine, int endLine) throws IOException {
        List<String> lines = readFile(filePath);
        int start = Math.max(0, startLine - 1); // Convert to 0-based
        int end = Math.min(lines.size(), endLine);
        return lines.subList(start, end);
    }

    /**
     * Find all Java files in a directory (recursively).
     *
     * Uses Java NIO's DirectoryStream API for efficient file traversal.
     * Recursively searches subdirectories.
     *
     * @param directoryPath The root directory to search
     * @return A list of absolute paths to all .java files found
     * @throws IOException If directory doesn't exist or cannot be accessed
     */
    public List<String> findAllJavaFiles(String directoryPath) throws IOException {
        Path dirPath = Paths.get(directoryPath);

        // Validate directory exists
        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            throw new IOException("Directory not found or is not a directory: " + directoryPath);
        }

        // Use Files.walk() to recursively find all .java files
        try (var stream = Files.walk(dirPath)) {
            List<String> javaFiles = stream
                    .filter(Files::isRegularFile)                          // Only files, not directories
                    .filter(path -> path.toString().endsWith(".java"))    // Only .java files
                    .map(Path::toString)                                   // Convert Path to String
                    .collect(Collectors.toList());

            System.out.println(LOG_PREFIX + " Found " + javaFiles.size() + " Java files in: " + directoryPath);
            return javaFiles;
        }
    }

    /**
     * Read all Java files in a directory and return them as a map.
     * The map keys are file paths, and values are the file contents (as List<String>).
     *
     * This is useful for batch processing multiple files.
     *
     * @param directoryPath The root directory to search
     * @return A Map<FilePath, FileContent> of all Java files found
     * @throws IOException If directory cannot be accessed
     */
    public Map<String, List<String>> readAllJavaFilesInDirectory(String directoryPath) throws IOException {
        List<String> javaFiles = findAllJavaFiles(directoryPath);
        Map<String, List<String>> fileContents = new HashMap<>();

        for (String filePath : javaFiles) {
            try {
                List<String> content = readFile(filePath);
                fileContents.put(filePath, content);
            } catch (IOException e) {
                System.err.println(LOG_PREFIX + " Skipping file due to read error: " + filePath);
                // Continue processing other files even if one fails
            }
        }

        return fileContents;
    }

    /**
     * Get file name from a full path.
     * Utility method to extract just the file name (e.g., "ProductController.java" from full path).
     *
     * @param filePath The full file path
     * @return Just the file name
     */
    public static String getFileName(String filePath) {
        return Paths.get(filePath).getFileName().toString();
    }

    /**
     * Get the directory containing a file.
     *
     * @param filePath The full file path
     * @return The directory path
     */
    public static String getDirectory(String filePath) {
        return Paths.get(filePath).getParent().toString();
    }

    /**
     * Check if a file is a valid Java source file.
     *
     * @param filePath The path to check
     * @return true if file exists, is readable, and ends with .java
     */
    public static boolean isValidJavaFile(String filePath) {
        Path path = Paths.get(filePath);
        return Files.exists(path) && Files.isReadable(path) && filePath.endsWith(".java");
    }
}
