package com.consolemaster;

import lombok.Getter;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

/**
 * Enhanced Graphics context using JLine's AttributedString for better ANSI support.
 * Provides methods to draw text and simple graphics elements with JLine styling.
 */
@Getter
public class JLineGraphics {

    private final AttributedString[][] buffer;
    private final int width;
    private final int height;

    // Current drawing style using JLine's AttributedStyle
    private AttributedStyle currentStyle = AttributedStyle.DEFAULT;

    /**
     * Creates a JLine Graphics context.
     */
    public JLineGraphics(int width, int height) {
        this.width = width;
        this.height = height;
        this.buffer = new AttributedString[height][width];
        clear();
    }

    /**
     * Sets the current style for subsequent drawing operations.
     */
    public void setStyle(AttributedStyle style) {
        this.currentStyle = style != null ? style : AttributedStyle.DEFAULT;
    }

    /**
     * Sets foreground color using JLine's color constants.
     */
    public void setForegroundColor(int color) {
        this.currentStyle = currentStyle.foreground(color);
    }

    /**
     * Sets background color using JLine's color constants.
     */
    public void setBackgroundColor(int color) {
        this.currentStyle = currentStyle.background(color);
    }

    /**
     * Sets text to bold.
     */
    public void setBold(boolean bold) {
        this.currentStyle = bold ? currentStyle.bold() : currentStyle.boldOff();
    }

    /**
     * Sets text to italic.
     */
    public void setItalic(boolean italic) {
        this.currentStyle = italic ? currentStyle.italic() : currentStyle.italicOff();
    }

    /**
     * Sets text to underlined.
     */
    public void setUnderline(boolean underline) {
        this.currentStyle = underline ? currentStyle.underline() : currentStyle.underlineOff();
    }

    /**
     * Resets style to default.
     */
    public void resetStyle() {
        this.currentStyle = AttributedStyle.DEFAULT;
    }

    /**
     * Draws a character at the specified position with current style.
     */
    public void drawChar(int x, int y, char c) {
        if (isValid(x, y)) {
            buffer[y][x] = new AttributedString(String.valueOf(c), currentStyle);
        }
    }

    /**
     * Draws a string starting at the specified position with current style.
     */
    public void drawString(int x, int y, String text) {
        if (text == null || y < 0 || y >= height) {
            return;
        }

        for (int i = 0; i < text.length() && x + i < width; i++) {
            if (x + i >= 0) {
                buffer[y][x + i] = new AttributedString(String.valueOf(text.charAt(i)), currentStyle);
            }
        }
    }

    /**
     * Draws a string with explicit style.
     */
    public void drawStyledString(int x, int y, String text, AttributedStyle style) {
        if (text == null || y < 0 || y >= height) {
            return;
        }

        for (int i = 0; i < text.length() && x + i < width; i++) {
            if (x + i >= 0) {
                buffer[y][x + i] = new AttributedString(String.valueOf(text.charAt(i)), style);
            }
        }
    }

    /**
     * Draws a horizontal line with current style.
     */
    public void drawHorizontalLine(int x1, int x2, int y, char c) {
        if (y < 0 || y >= height) {
            return;
        }

        int startX = Math.max(0, Math.min(x1, x2));
        int endX = Math.min(width - 1, Math.max(x1, x2));

        for (int x = startX; x <= endX; x++) {
            buffer[y][x] = new AttributedString(String.valueOf(c), currentStyle);
        }
    }

    /**
     * Draws a vertical line with current style.
     */
    public void drawVerticalLine(int x, int y1, int y2, char c) {
        if (x < 0 || x >= width) {
            return;
        }

        int startY = Math.max(0, Math.min(y1, y2));
        int endY = Math.min(height - 1, Math.max(y1, y2));

        for (int y = startY; y <= endY; y++) {
            buffer[y][x] = new AttributedString(String.valueOf(c), currentStyle);
        }
    }

    /**
     * Draws a rectangle outline with current style.
     */
    public void drawRect(int x, int y, int width, int height, char c) {
        // Top and bottom borders
        drawHorizontalLine(x, x + width - 1, y, c);
        drawHorizontalLine(x, x + width - 1, y + height - 1, c);

        // Left and right borders
        drawVerticalLine(x, y, y + height - 1, c);
        drawVerticalLine(x + width - 1, y, y + height - 1, c);
    }

    /**
     * Fills a rectangular area with the specified character and current style.
     */
    public void fillRect(int x, int y, int width, int height, char c) {
        for (int row = y; row < y + height && row < this.height; row++) {
            if (row >= 0) {
                for (int col = x; col < x + width && col < this.width; col++) {
                    if (col >= 0) {
                        buffer[row][col] = new AttributedString(String.valueOf(c), currentStyle);
                    }
                }
            }
        }
    }

    /**
     * Clears the entire graphics buffer with spaces.
     */
    public void clear() {
        AttributedString space = new AttributedString(" ", AttributedStyle.DEFAULT);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                buffer[y][x] = space;
            }
        }
    }

    /**
     * Converts the buffer to JLine's AttributedString for efficient terminal output.
     */
    public AttributedString toAttributedString() {
        AttributedStringBuilder builder = new AttributedStringBuilder();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                builder.append(buffer[y][x]);
            }
            if (y < height - 1) {
                builder.append('\n');
            }
        }

        return builder.toAttributedString();
    }

    /**
     * Converts the buffer to a plain string (for compatibility).
     */
    @Override
    public String toString() {
        return toAttributedString().toString();
    }

    /**
     * Checks if the given coordinates are valid within the buffer.
     */
    private boolean isValid(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
