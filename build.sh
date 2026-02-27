#!/bin/bash
# Build Script for Java Code Review Tool
# Compiles all Java source files and creates executable JAR

# Color codes for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Java Code Review Tool - Build Script${NC}"
echo -e "${BLUE}========================================${NC}"

# Check if Java is installed
if ! command -v javac &> /dev/null; then
    echo -e "${RED}Error: Java compiler (javac) not found${NC}"
    echo "Please install JDK 11 or higher"
    exit 1
fi

echo -e "${GREEN}✓ Java compiler found${NC}"
java -version

# Create build directory
echo -e "\n${YELLOW}Creating build directory...${NC}"
mkdir -p target/classes
mkdir -p output

echo -e "${GREEN}✓ Build directories created${NC}"

# Compile all Java files
echo -e "\n${YELLOW}Compiling Java source files...${NC}"

javac -d target/classes \
      -encoding UTF-8 \
      -source 11 \
      -target 11 \
      src/main/java/com/codereviewer/model/*.java \
      src/main/java/com/codereviewer/core/*.java \
      src/main/java/com/codereviewer/analyzer/*.java \
      src/main/java/com/codereviewer/report/*.java \
      src/main/java/com/codereviewer/*.java

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Compilation successful${NC}"
else
    echo -e "${RED}✗ Compilation failed${NC}"
    exit 1
fi

# Verify all classes were compiled
echo -e "\n${YELLOW}Verifying compiled classes...${NC}"
CLASS_COUNT=$(find target/classes -name "*.class" | wc -l)
echo -e "${GREEN}✓ Found $CLASS_COUNT compiled classes${NC}"

# Optional: Create JAR file
echo -e "\n${YELLOW}Creating JAR file...${NC}"

# Create manifest file
cat > target/MANIFEST.MF << EOF
Manifest-Version: 1.0
Main-Class: com.codereviewer.Main
Implementation-Title: Java Code Review Tool
Implementation-Version: 1.0.0
Implementation-Vendor: Code Review Tool Project
Created-By: Java Build Script
EOF

# Create JAR
jar cfm target/code-review-tool.jar target/MANIFEST.MF -C target/classes .

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ JAR created: target/code-review-tool.jar${NC}"
else
    echo -e "${RED}✗ JAR creation failed${NC}"
fi

# Build complete message
echo -e "\n${BLUE}========================================${NC}"
echo -e "${GREEN}Build Successful!${NC}"
echo -e "${BLUE}========================================${NC}"

echo -e "\nUsage:"
echo -e "  ${YELLOW}Command Line:${NC}"
echo -e "    java -cp target/classes com.codereviewer.Main [path]"
echo -e "\n  ${YELLOW}JAR File:${NC}"
echo -e "    java -jar target/code-review-tool.jar [path]"
echo -e "\n  ${YELLOW}Test with Samples:${NC}"
echo -e "    java -cp target/classes com.codereviewer.Main src/main/resources/samples"
echo -e "\nReport will be generated as: code_review_report.html"
echo -e "${BLUE}========================================${NC}"
