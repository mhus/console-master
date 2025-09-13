package com.consolemaster;

import lombok.Getter;

/**
 * Graphics context for drawing operations on the console.
 * Provides methods to draw text and simple graphics elements with ANSI styling support.
 */
@Getter
public class Graphics {

    private final StyledChar[][] buffer;
    private final int width;
    private final int height;

    // Current drawing style
    private AnsiColor currentForegroundColor;
    private AnsiColor currentBackgroundColor;
    private AnsiFormat[] currentFormats = new AnsiFormat[0];

    /**
     * Creates a Graphics context with a plain character buffer.
     */
    public Graphics(char[][] charBuffer, int width, int height) {
        this.width = width;
        this.height = height;
        this.buffer = new StyledChar[height][width];

        // Convert char buffer to StyledChar buffer
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.buffer[y][x] = new StyledChar(charBuffer[y][x]);
            }
        }
    }

    /**
     * Creates a Graphics context with a StyledChar buffer.
     */
    public Graphics(StyledChar[][] buffer, int width, int height) {
        this.buffer = buffer;
        this.width = width;
        this.height = height;
    }

    /**
     * Sets the foreground color for subsequent drawing operations.
     */
    public void setForegroundColor(AnsiColor color) {
        this.currentForegroundColor = color;
    }

    /**
     * Sets the background color for subsequent drawing operations.
     */
    public void setBackgroundColor(AnsiColor color) {
        this.currentBackgroundColor = color;
    }

    /**
     * Sets the text formatting for subsequent drawing operations.
     */
    public void setFormats(AnsiFormat... formats) {
        this.currentFormats = formats != null ? formats : new AnsiFormat[0];
    }

    /**
     * Resets all styling to default.
     */
    public void resetStyle() {
        this.currentForegroundColor = null;
        this.currentBackgroundColor = null;
        this.currentFormats = new AnsiFormat[0];
    }

    /**
     * Draws a styled character at the specified position.
     */
    public void drawStyledChar(int x, int y, StyledChar styledChar) {
        if (isValid(x, y)) {
            buffer[y][x] = styledChar;
        }
    }

    /**
     * Draws a character at the specified position with current styling.
     */
    public void drawChar(int x, int y, char c) {
        if (isValid(x, y)) {
            buffer[y][x] = new StyledChar(c, currentForegroundColor, currentBackgroundColor, currentFormats);
        }
    }

    /**
     * Draws a string starting at the specified position with current styling.
     */
    public void drawString(int x, int y, String text) {
        if (text == null || y < 0 || y >= height) {
            return;
        }

        for (int i = 0; i < text.length() && x + i < width; i++) {
            if (x + i >= 0) {
                buffer[y][x + i] = new StyledChar(text.charAt(i), currentForegroundColor, currentBackgroundColor, currentFormats);
            }
        }
    }

    /**
     * Draws a string with explicit styling.
     */
    public void drawStyledString(int x, int y, String text, AnsiColor foregroundColor, AnsiColor backgroundColor, AnsiFormat... formats) {
        if (text == null || y < 0 || y >= height) {
            return;
        }

        for (int i = 0; i < text.length() && x + i < width; i++) {
            if (x + i >= 0) {
                buffer[y][x + i] = new StyledChar(text.charAt(i), foregroundColor, backgroundColor, formats);
            }
        }
    }

    /**
     * Draws a horizontal line with current styling.
     */
    public void drawHorizontalLine(int x1, int x2, int y, char c) {
        if (y < 0 || y >= height) {
            return;
        }

        int startX = Math.max(0, Math.min(x1, x2));
        int endX = Math.min(width - 1, Math.max(x1, x2));

        for (int x = startX; x <= endX; x++) {
            buffer[y][x] = new StyledChar(c, currentForegroundColor, currentBackgroundColor, currentFormats);
        }
    }

    /**
     * Draws a vertical line with current styling.
     */
    public void drawVerticalLine(int x, int y1, int y2, char c) {
        if (x < 0 || x >= width) {
            return;
        }

        int startY = Math.max(0, Math.min(y1, y2));
        int endY = Math.min(height - 1, Math.max(y1, y2));

        for (int y = startY; y <= endY; y++) {
            buffer[y][x] = new StyledChar(c, currentForegroundColor, currentBackgroundColor, currentFormats);
        }
    }

    /**
     * Draws a rectangle outline with current styling.
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
     * Fills a rectangular area with the specified character and current styling.
     */
    public void fillRect(int x, int y, int width, int height, char c) {
        for (int row = y; row < y + height && row < this.height; row++) {
            if (row >= 0) {
                for (int col = x; col < x + width && col < this.width; col++) {
                    if (col >= 0) {
                        buffer[row][col] = new StyledChar(c, currentForegroundColor, currentBackgroundColor, currentFormats);
                    }
                }
            }
        }
    }

    /**
     * Clears the entire graphics buffer with spaces.
     */
    public void clear() {
        fillRect(0, 0, width, height, ' ');
    }

    /**
     * Converts the buffer to a plain character array (without styling).
     */
    public char[][] toCharArray() {
        char[][] result = new char[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result[y][x] = buffer[y][x].getCharacter();
            }
        }
        return result;
    }

    /**
     * Converts the buffer to an ANSI-styled string representation.
     */
    public String toAnsiString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sb.append(buffer[y][x].toAnsiString());
            }
            if (y < height - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Checks if the given coordinates are valid within the buffer.
     */
    private boolean isValid(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
