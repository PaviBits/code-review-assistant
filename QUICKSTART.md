# Quick Start Guide - Java Code Review Tool

## ⚡ 5-Minute Setup

### Prerequisites
- Java 11 or higher (Java 17+ recommended)
- Any IDE (IntelliJ IDEA, Eclipse, or VS Code)
- Terminal/Command Prompt

### Step 1: Project Structure
The project is organized as follows:
```
CodeReviewTool/
├── src/main/java/
│   ├── com/codereviewer/Main.java
│   ├── com/codereviewer/core/
│   ├── com/codereviewer/analyzer/
│   ├── com/codereviewer/model/
│   └── com/codereviewer/report/
├── src/main/resources/samples/
│   ├── ProblematicCode.java (with violations)
│   └── CleanCode.java (best practices)
├── output/
│   └── code_review_report.html (generated)
└── README.md
```

### Step 2: Compile the Project

#### Option A: Using Compiler Directly
```bash
cd CodeReviewTool
javac -d target/classes -sourcepath src/main/java src/main/java/com/codereviewer/**/*.java
```

#### Option B: Using IDE
1. Open `CodeReviewTool` folder in your IDE
2. Mark `src/main/java` as "Sources Root"
3. Build project (Ctrl+B in IntelliJ, Ctrl+Shift+B in VS Code)

#### Option C: One-Command Compilation
```bash
javac -d bin -encoding UTF-8 src/main/java/com/codereviewer/*.java \
  src/main/java/com/codereviewer/core/*.java \
  src/main/java/com/codereviewer/analyzer/*.java \
  src/main/java/com/codereviewer/model/*.java \
  src/main/java/com/codereviewer/report/*.java
```

### Step 3: Run the Application

#### Test with Sample Files
```bash
cd CodeReviewTool
java -cp target/classes com.codereviewer.Main src/main/resources/samples
```

#### Interactive Mode
```bash
java -cp target/classes com.codereviewer.Main
```
Then follow the prompts to:
1. Enter file/directory path
2. Set report title
3. Choose output location

#### Analyze Your Own Code
```bash
java -cp target/classes com.codereviewer.Main /path/to/your/java/files
```

### Step 4: View Report
The generated HTML report will be created as `code_review_report.html`. 
Open it in any web browser to see:
- Summary statistics
- Issues grouped by file
- Color-coded severity levels
- Code snippets and suggestions

---

## 📋 Expected Output

### Sample Run
```
==================================================
Java Code Review Tool v1.0.0
==================================================

[Tool] Analyzing directory: src/main/resources/samples

[CodeReader] Found 2 Java files in: src/main/resources/samples
[CodeReader] Read 145 lines from: ...ProblematicCode.java
[CodeReader] Read 321 lines from: ...CleanCode.java

[Tool] Analyzing file: ...ProblematicCode.java
[Tool] Found 27 issues
  ✓ ProblematicCode.java : 27 issues

[Tool] Analyzing file: ...CleanCode.java
[Tool] Found 0 issues
  ✓ CleanCode.java : 0 issues

[Tool] Total issues found: 27
[Tool] Generating HTML report...
[ReportGenerator] Report generated successfully: code_review_report.html

==================================================
Analysis Summary:
  Total Issues: 27
  Syntax Errors: 4
  Naming Violations: 8
  Unused Code Issues: 7
  Complexity Issues: 5
  Best Practice Issues: 3

Breakdown by Severity:
  HIGH:   4 issues
  MEDIUM: 15 issues
  LOW:    8 issues

Top 5 Critical Issues:
  1. [ProblematicCode.java:17] Missing semicolon
  2. [ProblematicCode.java:18] Constant not in CONSTANT_CASE
  3. [ProblematicCode.java:22] Variable name starts with uppercase
  4. [ProblematicCode.java:42] Empty catch block
  5. [ProblematicCode.java:51] Method name should start with lowercase

==================================================

========== ANALYSIS COMPLETE ==========
✓ Code review finished successfully!
```

---

## 🔧 Troubleshooting

### Issue: "Command not found: javac"
**Solution**: Java is not in PATH. Install JDK from oracle.com/java/

### Issue: "Could not find or load main class"
**Solution**: 
1. Verify all files are compiled: `ls target/classes/com/codereviewer/`
2. Check classpath is correct: `-cp target/classes`

### Issue: "No files found in directory"
**Solution**: Directory doesn't contain .java files or path is incorrect. Verify:
```bash
ls -R path/to/check | grep .java
```

### Issue: HTML report looks unstyled
**Solution**: CSS is embedded in HTML. Open with modern browser (Chrome, Firefox, Edge). Some older browsers don't support gradients.

---

## 💡 Testing the Tool

### Test 1: Sample Code with Issues
```bash
java -cp target/classes com.codereviewer.Main src/main/resources/samples/ProblematicCode.java
```
Should find ~27 issues across multiple categories.

### Test 2: Clean Code
```bash
java -cp target/classes com.codereviewer.Main src/main/resources/samples/CleanCode.java
```
Should find 0 issues (demonstrates professional coding standards).

### Test 3: Multiple Files
```bash
java -cp target/classes com.codereviewer.Main src/main/resources/samples/
```
Should analyze both files and aggregate results.

### Test 4: Custom Code
```bash
java -cp target/classes com.codereviewer.Main /path/to/your/code
```
Analyze your own Java project.

---

## 🎯 Key Analysis Features

The tool detects:

### Syntax Errors (HIGH severity)
- ✓ Missing semicolons
- ✓ Unmatched braces
- ✓ Empty catch blocks
- ✓ Incomplete try-catch

### Naming Violations (LOW severity)
- ✓ Variables not in camelCase
- ✓ Constants not in CONSTANT_CASE
- ✓ Classes not in PascalCase
- ✓ Methods not in camelCase

### Code Complexity (MEDIUM severity)
- ✓ Deep nesting (4+ levels)
- ✓ Complex conditions (3+ operators)
- ✓ Long methods
- ✓ Multiple return points

### Unused Code (MEDIUM severity)
- ✓ Wildcard imports
- ✓ Unused variables
- ✓ Commented-out code
- ✓ TODO/FIXME markers

### Best Practices (LOW-MEDIUM severity)
- ✓ Magic strings/numbers
- ✓ System.out/err logging (should use SLF4J)
- ✓ Potential null pointer access

---

## 📊 Report Features

The generated HTML report includes:

1. **Header Section**
   - Report title
   - Generation timestamp
   - Tool version

2. **Summary Statistics**
   - Total issues
   - Breakdown by severity
   - Color-coded cards

3. **Detailed Issues**
   - Grouped by file
   - Sorted by severity
   - Line numbers
   - Code snippets
   - Suggested fixes

4. **Professional Styling**
   - Modern gradient design
   - Responsive layout
   - Print-friendly
   - Mobile-compatible

---

## 🚀 Next Steps

### Extend the Tool
1. Add custom rule configuration file
2. Support more programming languages
3. Integrate with CI/CD pipelines
4. Create IDE plugins
5. Add performance metrics

### Improve Analysis
1. Implement AST-based analysis
2. Add machine learning for bug detection
3. Support for more complex patterns
4. Cross-file analysis

### Enhance Reports
1. Historical trend tracking
2. Comparison between runs
3. Custom templates
4. Export to multiple formats

---

## 📞 Support

For issues or questions:
1. Check the main README.md for detailed documentation
2. Review sample files (ProblematicCode.java vs CleanCode.java)
3. Check console output for specific error messages
4. Verify Java version: `java -version`

---

## ✨ Tips for Interview Use

1. **Run a live demo** on sample code during interview
2. **Explain the architecture** - discuss design patterns
3. **Show the HTML report** - demonstrates full-stack capability
4. **Walk through regex patterns** - shows technical depth
5. **Discuss improvements** - shows forward thinking
6. **Highlight code quality** - well-commented, professional

---

**Happy Code Reviewing! 🎉**
