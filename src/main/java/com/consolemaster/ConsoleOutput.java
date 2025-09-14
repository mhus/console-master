package com.consolemaster;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * A Canvas component that displays captured console output from stdout and stderr.
 * This component can display the output in different modes and with different styling.
 */
@Getter
@Setter
public class ConsoleOutput extends Canvas {

    private final OutputCapture outputCapture;

    // Display settings
    private DisplayMode displayMode = DisplayMode.BOTH;
    private int maxDisplayLines = 100;
    private boolean showLineNumbers = false;
    private boolean autoScroll = true;
    private int scrollOffset = 0;

    // Styling
    private AnsiColor stdoutColor = AnsiColor.WHITE;
    private AnsiColor stderrColor = AnsiColor.BRIGHT_RED;
    private AnsiColor lineNumberColor = AnsiColor.BRIGHT_BLACK;
    private String stdoutPrefix = "[OUT] ";
    private String stderrPrefix = "[ERR] ";

    /**
     * Display modes for console output.
     */
    public enum DisplayMode {
        STDOUT_ONLY,    // Show only stdout
        STDERR_ONLY,    // Show only stderr
        BOTH,           // Show both stdout and stderr (interleaved by time)
        SEPARATED       // Show stdout and stderr in separate sections
    }

    /**
     * Creates a new ConsoleOutput canvas.
     *
     * @param name the name of the canvas
     * @param x the x position
     * @param y the y position
     * @param width the width
     * @param height the height
     * @param outputCapture the output capture instance to display
     */
    public ConsoleOutput(String name, int x, int y, int width, int height, OutputCapture outputCapture) {
        super(name, x, y, width, height);
        this.outputCapture = outputCapture;
        setCanFocus(true);
    }

    @Override
    public void paint(Graphics graphics) {
        graphics.clear();

        if (outputCapture == null) {
            graphics.drawStyledString(0, 0, "No output capture available", AnsiColor.BRIGHT_BLACK, null);
            return;
        }

        switch (displayMode) {
            case STDOUT_ONLY -> paintSingleStream(graphics, outputCapture.getStdoutLines(), stdoutColor, stdoutPrefix);
            case STDERR_ONLY -> paintSingleStream(graphics, outputCapture.getStderrLines(), stderrColor, stderrPrefix);
            case BOTH -> paintInterleavedStreams(graphics);
            case SEPARATED -> paintSeparatedStreams(graphics);
        }
    }

    /**
     * Paints a single stream using Graphics (works with both Legacy and JLine implementations).
     */
    private void paintSingleStream(Graphics graphics, List<String> lines, AnsiColor color, String prefix) {
        int startLine = calculateStartLine(lines.size());
        int endLine = Math.min(startLine + getHeight(), lines.size());

        for (int i = startLine; i < endLine; i++) {
            int displayLine = i - startLine;
            String line = lines.get(i);

            if (showLineNumbers) {
                String lineNumber = String.format("%4d: ", i + 1);
                graphics.drawStyledString(0, displayLine, lineNumber, lineNumberColor, null);
                graphics.drawStyledString(6, displayLine, prefix + line, color, null);
            } else {
                graphics.drawStyledString(0, displayLine, prefix + line, color, null);
            }
        }

        // Show scroll indicator if needed
        if (lines.size() > getHeight()) {
            String scrollInfo = String.format("(%d/%d)", endLine, lines.size());
            graphics.drawStyledString(getWidth() - scrollInfo.length(), getHeight() - 1, scrollInfo, AnsiColor.BRIGHT_BLACK, null);
        }
    }

    /**
     * Paints interleaved stdout and stderr streams using Graphics.
     */
    private void paintInterleavedStreams(Graphics graphics) {
        // For simplicity, just show stdout and stderr in sequence
        // In a real implementation, you might want to timestamp the outputs for proper interleaving
        List<String> stdoutLines = outputCapture.getStdoutLines();
        List<String> stderrLines = outputCapture.getStderrLines();

        int totalLines = stdoutLines.size() + stderrLines.size();
        int startLine = calculateStartLine(totalLines);
        int currentY = 0;
        int linesDisplayed = 0;

        // Display stdout lines first
        for (int i = Math.max(0, startLine - stderrLines.size()); i < stdoutLines.size() && currentY < getHeight(); i++) {
            if (linesDisplayed >= startLine) {
                String line = stdoutLines.get(i);
                if (showLineNumbers) {
                    String lineNumber = String.format("%4d: ", linesDisplayed + 1);
                    graphics.drawStyledString(0, currentY, lineNumber, lineNumberColor, null);
                    graphics.drawStyledString(6, currentY, stdoutPrefix + line, stdoutColor, null);
                } else {
                    graphics.drawStyledString(0, currentY, stdoutPrefix + line, stdoutColor, null);
                }
                currentY++;
            }
            linesDisplayed++;
        }

        // Display stderr lines
        for (int i = Math.max(0, startLine - stdoutLines.size()); i < stderrLines.size() && currentY < getHeight(); i++) {
            if (linesDisplayed >= startLine) {
                String line = stderrLines.get(i);
                if (showLineNumbers) {
                    String lineNumber = String.format("%4d: ", linesDisplayed + 1);
                    graphics.drawStyledString(0, currentY, lineNumber, lineNumberColor, null);
                    graphics.drawStyledString(6, currentY, stderrPrefix + line, stderrColor, null);
                } else {
                    graphics.drawStyledString(0, currentY, stderrPrefix + line, stderrColor, null);
                }
                currentY++;
            }
            linesDisplayed++;
        }
    }

    /**
     * Paints separated stdout and stderr streams using Graphics.
     */
    private void paintSeparatedStreams(Graphics graphics) {
        int halfHeight = getHeight() / 2;

        // Draw stdout in top half
        graphics.drawStyledString(0, 0, "=== STDOUT ===", AnsiColor.BRIGHT_CYAN, null);
        List<String> stdoutLines = outputCapture.getLastStdoutLines(halfHeight - 2);
        for (int i = 0; i < stdoutLines.size() && i < halfHeight - 2; i++) {
            graphics.drawStyledString(0, i + 1, stdoutLines.get(i), stdoutColor, null);
        }

        // Draw stderr in bottom half
        int stderrStart = halfHeight;
        graphics.drawStyledString(0, stderrStart, "=== STDERR ===", AnsiColor.BRIGHT_CYAN, null);
        List<String> stderrLines = outputCapture.getLastStderrLines(halfHeight - 1);
        for (int i = 0; i < stderrLines.size() && i < halfHeight - 1; i++) {
            graphics.drawStyledString(0, stderrStart + i + 1, stderrLines.get(i), stderrColor, null);
        }
    }

    /**
     * Calculates the starting line for display based on scroll settings.
     */
    private int calculateStartLine(int totalLines) {
        if (autoScroll) {
            return Math.max(0, totalLines - getHeight());
        } else {
            return Math.max(0, Math.min(scrollOffset, totalLines - getHeight()));
        }
    }

    /**
     * Scrolls up by the specified number of lines.
     *
     * @param lines number of lines to scroll up
     */
    public void scrollUp(int lines) {
        if (!autoScroll) {
            scrollOffset = Math.max(0, scrollOffset - lines);
        }
    }

    /**
     * Scrolls down by the specified number of lines.
     *
     * @param lines number of lines to scroll down
     */
    public void scrollDown(int lines) {
        if (!autoScroll) {
            int totalLines = getTotalDisplayLines();
            scrollOffset = Math.min(Math.max(0, totalLines - getHeight()), scrollOffset + lines);
        }
    }

    /**
     * Scrolls to the top of the output.
     */
    public void scrollToTop() {
        autoScroll = false;
        scrollOffset = 0;
    }

    /**
     * Scrolls to the bottom of the output and enables auto-scroll.
     */
    public void scrollToBottom() {
        autoScroll = true;
        scrollOffset = 0;
    }

    /**
     * Gets the total number of lines that would be displayed.
     */
    private int getTotalDisplayLines() {
        switch (displayMode) {
            case STDOUT_ONLY -> {
                return outputCapture.getStdoutLines().size();
            }
            case STDERR_ONLY -> {
                return outputCapture.getStderrLines().size();
            }
            case BOTH -> {
                return outputCapture.getStdoutLines().size() + outputCapture.getStderrLines().size();
            }
            case SEPARATED -> {
                return Math.max(outputCapture.getStdoutLines().size(), outputCapture.getStderrLines().size());
            }
        }
        return 0;
    }

    /**
     * Clears all captured output.
     */
    public void clearOutput() {
        if (outputCapture != null) {
            outputCapture.clear();
        }
    }
}
