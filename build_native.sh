#!/bin/bash

# Build script for native compilation of Console Master Demo with GraalVM
# This script compiles the demo application to a native executable using GraalVM

set -e  # Exit on any error

cd "$(dirname "$0")"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEMO_DIR="${PROJECT_DIR}/demo"
TARGET_DIR="${DEMO_DIR}/target"
EXECUTABLE_NAME="console-master-demo"

echo -e "${BLUE}=== Console Master Demo - Native Build Script ===${NC}"
echo ""

# Check if GraalVM is installed and native-image is available
echo -e "${YELLOW}Checking GraalVM installation...${NC}"
if ! command -v native-image &> /dev/null; then
    echo -e "${RED}Error: native-image command not found!${NC}"
    echo "Please ensure GraalVM is installed and native-image is available in PATH."
    echo ""
    echo "Installation instructions:"
    echo "1. Install GraalVM (Java 21 or higher)"
    echo "2. Set JAVA_HOME to GraalVM installation"
    echo "3. Install native-image: gu install native-image"
    echo ""
    exit 1
fi

# Display GraalVM information
echo -e "${GREEN}GraalVM found:${NC}"
java -version
echo ""

# Check if we're in the correct directory
if [ ! -f "${PROJECT_DIR}/pom.xml" ]; then
    echo -e "${RED}Error: Not in the correct project directory!${NC}"
    echo "Please run this script from the console-master root directory."
    exit 1
fi

# Clean previous builds
echo -e "${YELLOW}Cleaning previous builds...${NC}"
mvn clean -q

# Build all modules first
echo -e "${YELLOW}Building all project modules...${NC}"
mvn install -DskipTests -q
if [ $? -ne 0 ]; then
    echo -e "${RED}Error: Failed to build project modules!${NC}"
    exit 1
fi

# Build native executable with the native profile
echo -e "${YELLOW}Building native executable...${NC}"
echo "This may take several minutes..."
cd "${DEMO_DIR}"

mvn package -Pnative -DskipTests
if [ $? -ne 0 ]; then
    echo -e "${RED}Error: Native compilation failed!${NC}"
    exit 1
fi

# Check if the executable was created
EXECUTABLE_PATH="${TARGET_DIR}/${EXECUTABLE_NAME}"
if [ ! -f "${EXECUTABLE_PATH}" ]; then
    echo -e "${RED}Error: Native executable not found at ${EXECUTABLE_PATH}${NC}"
    exit 1
fi

# Make executable if needed
chmod +x "${EXECUTABLE_PATH}"

# Display results
echo ""
echo -e "${GREEN}=== Build Successful! ===${NC}"
echo -e "${GREEN}Native executable created:${NC} ${EXECUTABLE_PATH}"

# Get file size
if command -v du &> /dev/null; then
    SIZE=$(du -h "${EXECUTABLE_PATH}" | cut -f1)
    echo -e "${GREEN}Executable size:${NC} ${SIZE}"
fi

# Display usage instructions
echo ""
echo -e "${BLUE}Usage:${NC}"
echo "  ${EXECUTABLE_PATH}"
echo ""
echo -e "${BLUE}Or copy to a location in your PATH:${NC}"
echo "  cp ${EXECUTABLE_PATH} /usr/local/bin/"
echo ""

# Test the executable
echo -e "${YELLOW}Testing the native executable...${NC}"
if "${EXECUTABLE_PATH}" --help 2>/dev/null || "${EXECUTABLE_PATH}" 0 2>/dev/null; then
    echo -e "${GREEN}Native executable test: PASSED${NC}"
else
    echo -e "${YELLOW}Note: Could not test executable automatically${NC}"
    echo "Please test manually: ${EXECUTABLE_PATH}"
fi

echo ""
echo -e "${GREEN}Native build completed successfully!${NC}"
