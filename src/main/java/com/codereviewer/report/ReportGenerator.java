package com.codereviewer.report;

import com.codereviewer.model.CodeIssue;
import com.codereviewer.model.IssueSeverity;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ReportGenerator class creates HTML reports from code analysis results.
 *
 * Responsibilities:
 * - Convert CodeIssue objects into HTML format
 * - Apply professional CSS styling
 * - Generate comprehensive statistics
 * - Write report to file
 *
 * Design Pattern: Builder Pattern + Template Method
 * - Builds HTML incrementally with clear sections
 * - CSS styling for professional appearance
 * - Customizable report sections
 *
 * Report Features:
 * - Summary statistics (total issues, by severity)
 * - Issues grouped by file and severity
 * - Code snippets showing problem areas
 * - Suggested fixes
 * - Professional styling and layout
 *
 * @author Code Review Tool Project
 * @version 1.0.0
 */
public class ReportGenerator {
    // Report metadata
    private final String reportTitle;
    private final LocalDateTime generatedTime;
    private final List<CodeIssue> issues;

    // Output configuration
    private String outputPath;

    /**
     * Constructor for ReportGenerator.
     *
     * @param reportTitle Title for the report
     * @param issues List of CodeIssue objects to report on
     */
    public ReportGenerator(String reportTitle, List<CodeIssue> issues) {
        this.reportTitle = reportTitle;
        this.issues = new ArrayList<>(issues);
        this.generatedTime = LocalDateTime.now();
        this.outputPath = "code_review_report.html";
    }

    /**
     * Set the output file path for the report.
     *
     * @param path The file path where the report will be saved
     */
    public void setOutputPath(String path) {
        this.outputPath = path;
    }

    /**
     * Generate and save the complete HTML report.
     *
     * Process:
     * 1. Build HTML structure
     * 2. Add CSS styling
     * 3. Add report content (header, summary, issues)
     * 4. Write to file
     *
     * @throws IOException If file cannot be written
     */
    public void generateReport() throws IOException {
        StringBuilder html = new StringBuilder();

        // Build the complete HTML document
        html.append(buildHtmlHeader());
        html.append(buildCssStyles());
        html.append("<body>\n");
        html.append(buildReportHeader());
        html.append(buildSummarySection());
        html.append(buildIssuesByFile());
        html.append(buildFooter());
        html.append("</body>\n");
        html.append("</html>\n");

        // Write to file
        writeReportToFile(html.toString());

        System.out.println("[ReportGenerator] Report generated successfully: " + outputPath);
    }

    /**
     * Build HTML document header with metadata.
     *
     * @return HTML header string
     */
    private String buildHtmlHeader() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>" + escapeHtml(reportTitle) + "</title>\n" +
                "</head>\n";
    }

    /**
     * Build comprehensive CSS styling for the report.
     *
     * Features:
     * - Professional color scheme
     * - Responsive layout
     * - Severity-based color coding
     * - Print-friendly styles
     *
     * @return CSS style section
     */
    private String buildCssStyles() {
        return "<style>\n" +
                "    * {\n" +
                "        margin: 0;\n" +
                "        padding: 0;\n" +
                "        box-sizing: border-box;\n" +
                "    }\n" +
                "\n" +
                "    body {\n" +
                "        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
                "        background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);\n" +
                "        color: #333;\n" +
                "        line-height: 1.6;\n" +
                "        padding: 20px;\n" +
                "    }\n" +
                "\n" +
                "    .container {\n" +
                "        max-width: 1200px;\n" +
                "        margin: 0 auto;\n" +
                "        background: white;\n" +
                "        border-radius: 8px;\n" +
                "        box-shadow: 0 4px 6px rgba(0,0,0,0.1);\n" +
                "        overflow: hidden;\n" +
                "    }\n" +
                "\n" +
                "    .header {\n" +
                "        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
                "        color: white;\n" +
                "        padding: 40px;\n" +
                "        text-align: center;\n" +
                "    }\n" +
                "\n" +
                "    .header h1 {\n" +
                "        font-size: 2.5em;\n" +
                "        margin-bottom: 10px;\n" +
                "        font-weight: 600;\n" +
                "    }\n" +
                "\n" +
                "    .header p {\n" +
                "        font-size: 1.1em;\n" +
                "        opacity: 0.9;\n" +
                "    }\n" +
                "\n" +
                "    .metadata {\n" +
                "        background: #f8f9fa;\n" +
                "        padding: 20px 40px;\n" +
                "        border-bottom: 2px solid #e9ecef;\n" +
                "        display: flex;\n" +
                "        justify-content: space-between;\n" +
                "        flex-wrap: wrap;\n" +
                "    }\n" +
                "\n" +
                "    .metadata-item {\n" +
                "        margin: 5px 20px 5px 0;\n" +
                "    }\n" +
                "\n" +
                "    .metadata-label {\n" +
                "        font-weight: 600;\n" +
                "        color: #667eea;\n" +
                "    }\n" +
                "\n" +
                "    .content {\n" +
                "        padding: 40px;\n" +
                "    }\n" +
                "\n" +
                "    .section {\n" +
                "        margin-bottom: 40px;\n" +
                "    }\n" +
                "\n" +
                "    .section h2 {\n" +
                "        color: #667eea;\n" +
                "        margin-bottom: 20px;\n" +
                "        padding-bottom: 10px;\n" +
                "        border-bottom: 3px solid #667eea;\n" +
                "        font-size: 1.8em;\n" +
                "    }\n" +
                "\n" +
                "    .section h3 {\n" +
                "        color: #555;\n" +
                "        margin-top: 25px;\n" +
                "        margin-bottom: 15px;\n" +
                "        font-size: 1.3em;\n" +
                "    }\n" +
                "\n" +
                "    .summary-grid {\n" +
                "        display: grid;\n" +
                "        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));\n" +
                "        gap: 20px;\n" +
                "        margin-bottom: 30px;\n" +
                "    }\n" +
                "\n" +
                "    .summary-card {\n" +
                "        padding: 20px;\n" +
                "        border-radius: 8px;\n" +
                "        text-align: center;\n" +
                "        box-shadow: 0 2px 4px rgba(0,0,0,0.1);\n" +
                "        transition: transform 0.3s ease, box-shadow 0.3s ease;\n" +
                "    }\n" +
                "\n" +
                "    .summary-card:hover {\n" +
                "        transform: translateY(-5px);\n" +
                "        box-shadow: 0 4px 8px rgba(0,0,0,0.2);\n" +
                "    }\n" +
                "\n" +
                "    .summary-card h4 {\n" +
                "        font-size: 0.9em;\n" +
                "        font-weight: 600;\n" +
                "        margin-bottom: 10px;\n" +
                "        opacity: 0.8;\n" +
                "    }\n" +
                "\n" +
                "    .summary-card .count {\n" +
                "        font-size: 2.5em;\n" +
                "        font-weight: bold;\n" +
                "    }\n" +
                "\n" +
                "    .card-total {\n" +
                "        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
                "        color: white;\n" +
                "    }\n" +
                "\n" +
                "    .card-high {\n" +
                "        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);\n" +
                "        color: white;\n" +
                "    }\n" +
                "\n" +
                "    .card-medium {\n" +
                "        background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);\n" +
                "        color: white;\n" +
                "    }\n" +
                "\n" +
                "    .card-low {\n" +
                "        background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);\n" +
                "        color: white;\n" +
                "    }\n" +
                "\n" +
                "    .issues-table {\n" +
                "        width: 100%;\n" +
                "        border-collapse: collapse;\n" +
                "        margin-top: 20px;\n" +
                "        box-shadow: 0 2px 4px rgba(0,0,0,0.1);\n" +
                "    }\n" +
                "\n" +
                "    .issues-table thead {\n" +
                "        background: #f8f9fa;\n" +
                "    }\n" +
                "\n" +
                "    .issues-table th {\n" +
                "        padding: 15px;\n" +
                "        text-align: left;\n" +
                "        font-weight: 600;\n" +
                "        color: #333;\n" +
                "        border-bottom: 2px solid #dee2e6;\n" +
                "    }\n" +
                "\n" +
                "    .issues-table td {\n" +
                "        padding: 12px 15px;\n" +
                "        border-bottom: 1px solid #dee2e6;\n" +
                "    }\n" +
                "\n" +
                "    .issues-table tbody tr:hover {\n" +
                "        background: #f8f9fa;\n" +
                "        transition: background 0.2s ease;\n" +
                "    }\n" +
                "\n" +
                "    .issue-row {\n" +
                "        display: table-row;\n" +
                "    }\n" +
                "\n" +
                "    .severity-badge {\n" +
                "        display: inline-block;\n" +
                "        padding: 5px 12px;\n" +
                "        border-radius: 20px;\n" +
                "        font-weight: 600;\n" +
                "        font-size: 0.85em;\n" +
                "    }\n" +
                "\n" +
                "    .severity-high {\n" +
                "        background-color: #f5576c;\n" +
                "        color: white;\n" +
                "    }\n" +
                "\n" +
                "    .severity-medium {\n" +
                "        background-color: #fee140;\n" +
                "        color: #333;\n" +
                "    }\n" +
                "\n" +
                "    .severity-low {\n" +
                "        background-color: #30cfd0;\n" +
                "        color: white;\n" +
                "    }\n" +
                "\n" +
                "    .issue-description {\n" +
                "        color: #555;\n" +
                "        margin: 5px 0;\n" +
                "    }\n" +
                "\n" +
                "    .code-snippet {\n" +
                "        background: #f8f9fa;\n" +
                "        border-left: 4px solid #667eea;\n" +
                "        padding: 10px;\n" +
                "        margin: 10px 0;\n" +
                "        font-family: 'Courier New', monospace;\n" +
                "        font-size: 0.9em;\n" +
                "        overflow-x: auto;\n" +
                "        border-radius: 4px;\n" +
                "    }\n" +
                "\n" +
                "    .suggestion {\n" +
                "        background: #e8f5e9;\n" +
                "        border-left: 4px solid #4caf50;\n" +
                "        padding: 10px;\n" +
                "        margin: 10px 0;\n" +
                "        font-family: 'Courier New', monospace;\n" +
                "        font-size: 0.9em;\n" +
                "        border-radius: 4px;\n" +
                "    }\n" +
                "\n" +
                "    .footer {\n" +
                "        background: #f8f9fa;\n" +
                "        padding: 20px 40px;\n" +
                "        text-align: center;\n" +
                "        color: #666;\n" +
                "        font-size: 0.9em;\n" +
                "        border-top: 1px solid #dee2e6;\n" +
                "    }\n" +
                "\n" +
                "    .no-issues {\n" +
                "        background: #d4edda;\n" +
                "        border: 1px solid #c3e6cb;\n" +
                "        color: #155724;\n" +
                "        padding: 20px;\n" +
                "        border-radius: 4px;\n" +
                "        text-align: center;\n" +
                "        font-weight: 500;\n" +
                "    }\n" +
                "\n" +
                "    /* Print Styles */\n" +
                "    @media print {\n" +
                "        body {\n" +
                "            background: white;\n" +
                "        }\n" +
                "        .container {\n" +
                "            box-shadow: none;\n" +
                "            max-width: 100%;\n" +
                "        }\n" +
                "        .issues-table tbody tr {\n" +
                "            page-break-inside: avoid;\n" +
                "        }\n" +
                "    }\n" +
                "</style>\n";
    }

    /**
     * Build the report header section with title and metadata.
     *
     * @return HTML for header section
     */
    private String buildReportHeader() {
        String dateTime = generatedTime.format(
                DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss")
        );

        return "<div class=\"container\">\n" +
                "    <div class=\"header\">\n" +
                "        <h1>🔍 Code Review Report</h1>\n" +
                "        <p>" + escapeHtml(reportTitle) + "</p>\n" +
                "    </div>\n" +
                "    <div class=\"metadata\">\n" +
                "        <div class=\"metadata-item\">\n" +
                "            <span class=\"metadata-label\">Report Title:</span> " + escapeHtml(reportTitle) + "\n" +
                "        </div>\n" +
                "        <div class=\"metadata-item\">\n" +
                "            <span class=\"metadata-label\">Generated:</span> " + dateTime + "\n" +
                "        </div>\n" +
                "        <div class=\"metadata-item\">\n" +
                "            <span class=\"metadata-label\">Tool:</span> Java Code Review Tool v1.0.0\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <div class=\"content\">\n";
    }

    /**
     * Build the summary statistics section.
     *
     * Shows:
     * - Total issues found
     * - Breakdown by severity level
     * - Statistics by issue type
     *
     * @return HTML for summary section
     */
    private String buildSummarySection() {
        long highCount = issues.stream()
                .filter(i -> i.getSeverity() == IssueSeverity.HIGH).count();
        long mediumCount = issues.stream()
                .filter(i -> i.getSeverity() == IssueSeverity.MEDIUM).count();
        long lowCount = issues.stream()
                .filter(i -> i.getSeverity() == IssueSeverity.LOW).count();

        StringBuilder html = new StringBuilder();
        html.append("        <div class=\"section\">\n");
        html.append("            <h2>📊 Summary Statistics</h2>\n");
        html.append("            <div class=\"summary-grid\">\n");
        html.append(buildSummaryCard("Total Issues", issues.size(), "card-total"));
        html.append(buildSummaryCard("High Severity", (int) highCount, "card-high"));
        html.append(buildSummaryCard("Medium Severity", (int) mediumCount, "card-medium"));
        html.append(buildSummaryCard("Low Severity", (int) lowCount, "card-low"));
        html.append("            </div>\n");

        if (issues.isEmpty()) {
            html.append("            <div class=\"no-issues\">\n");
            html.append("                ✓ No issues found! Your code is excellent.\n");
            html.append("            </div>\n");
        }

        html.append("        </div>\n");
        return html.toString();
    }

    /**
     * Build a summary statistic card.
     *
     * @param label The card label
     * @param count The numeric value to display
     * @param cssClass CSS class for styling
     * @return HTML for one card
     */
    private String buildSummaryCard(String label, int count, String cssClass) {
        return "                <div class=\"summary-card " + cssClass + "\">\n" +
                "                    <h4>" + label + "</h4>\n" +
                "                    <div class=\"count\">" + count + "</div>\n" +
                "                </div>\n";
    }

    /**
     * Build the issues section grouped by file.
     *
     * Organizes issues:
     * 1. Grouped by file name
     * 2. Within each file, sorted by severity then line number
     * 3. Each issue shows: type, line number, description, code snippet, suggestion
     *
     * @return HTML for issues section
     */
    private String buildIssuesByFile() {
        if (issues.isEmpty()) {
            return "";
        }

        // Group issues by file
        Map<String, List<CodeIssue>> issuesByFile = new LinkedHashMap<>();
        for (CodeIssue issue : issues) {
            issuesByFile.computeIfAbsent(issue.getFileName(), k -> new ArrayList<>())
                    .add(issue);
        }

        StringBuilder html = new StringBuilder();
        html.append("        <div class=\"section\">\n");
        html.append("            <h2>📋 Issues by File</h2>\n");

        for (Map.Entry<String, List<CodeIssue>> fileEntry : issuesByFile.entrySet()) {
            String fileName = fileEntry.getKey();
            List<CodeIssue> fileIssues = fileEntry.getValue();

            // Sort issues within file by severity and line number
            fileIssues.sort((a, b) -> a.compareByPriority(b));

            html.append("            <h3>📄 ").append(escapeHtml(fileName)).append("</h3>\n");
            html.append("            <table class=\"issues-table\">\n");
            html.append("                <thead>\n");
            html.append("                    <tr>\n");
            html.append("                        <th>Line</th>\n");
            html.append("                        <th>Severity</th>\n");
            html.append("                        <th>Type</th>\n");
            html.append("                        <th>Description</th>\n");
            html.append("                        <th>Details</th>\n");
            html.append("                    </tr>\n");
            html.append("                </thead>\n");
            html.append("                <tbody>\n");

            for (CodeIssue issue : fileIssues) {
                html.append("                    <tr>\n");
                html.append("                        <td>").append(issue.getLineNumber()).append("</td>\n");
                html.append("                        <td><span class=\"severity-badge ").append(issue.getSeverity().getCssClass()).append("\">")
                        .append(issue.getSeverity().getLabel()).append("</span></td>\n");
                html.append("                        <td>").append(escapeHtml(issue.getIssueType())).append("</td>\n");
                html.append("                        <td>\n");
                html.append("                            <div class=\"issue-description\">").append(escapeHtml(issue.getDescription())).append("</div>\n");

                if (!issue.getSnippet().isEmpty()) {
                    html.append("                            <div class=\"code-snippet\">").append(escapeHtml(issue.getSnippet())).append("</div>\n");
                }

                if (!issue.getSuggestion().isEmpty()) {
                    html.append("                            <div class=\"suggestion\"><strong>✓ Suggestion:</strong> ").append(escapeHtml(issue.getSuggestion())).append("</div>\n");
                }

                html.append("                        </td>\n");
                html.append("                        <td style=\"text-align: center; color: #999;\">").append(issue.getIssueType().substring(0, 1)).append("</td>\n");
                html.append("                    </tr>\n");
            }

            html.append("                </tbody>\n");
            html.append("            </table>\n");
        }

        html.append("        </div>\n");
        return html.toString();
    }

    /**
     * Build the report footer.
     *
     * @return HTML for footer section
     */
    private String buildFooter() {
        return "    </div>\n" +
                "</div>\n" +
                "<div class=\"footer\">\n" +
                "    <p>Generated by Java Code Review Tool | " +
                "    <a href=\"https://github.com/\" style=\"color: #667eea; text-decoration: none;\">GitHub Repository</a></p>\n" +
                "</div>\n";
    }

    /**
     * Write HTML content to file.
     *
     * @param htmlContent The complete HTML document
     * @throws IOException If file cannot be written
     */
    private void writeReportToFile(String htmlContent) throws IOException {
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(htmlContent);
        }
    }

    /**
     * Escape HTML special characters to prevent injection issues.
     * Converts: < > & " '
     *
     * @param text The text to escape
     * @return Escaped HTML-safe text
     */
    private String escapeHtml(String text) {
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * Get the path where the report was saved.
     *
     * @return Output file path
     */
    public String getOutputPath() {
        return outputPath;
    }

    /**
     * Get count of issues by type.
     *
     * @return Map of issue type to count
     */
    public Map<String, Integer> getIssuesByType() {
        Map<String, Integer> typeCount = new HashMap<>();
        for (CodeIssue issue : issues) {
            typeCount.put(issue.getIssueType(),
                    typeCount.getOrDefault(issue.getIssueType(), 0) + 1);
        }
        return typeCount;
    }
}
