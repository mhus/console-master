package com.consolemaster;

import lombok.Getter;

/**
 * Abstract base class for graphics contexts providing common drawing operations.
 * Provides a unified interface for drawing operations on the console.
 */
@Getter
public abstract class Graphics {

    protected final int width;
    protected final int height;

    /**
     * Creates a Graphics context with specified dimensions.
     */
    public Graphics(int width, int height) {
        this.width = width;
        this.height = height;
    }

    // Abstract methods that must be implemented by subclasses

    /**
     * Clears the entire graphics buffer.
     */
    public abstract void clear();

    /**
     * Draws a character at the specified position.
     */
    public abstract void drawChar(int x, int y, char c);

    /**
     * Draws a string starting at the specified position.
     */
    public abstract void drawString(int x, int y, String text);

    /**
     * Draws a styled string with explicit color and format parameters.
     */
    public abstract void drawStyledString(int x, int y, String text, AnsiColor foregroundColor, AnsiColor backgroundColor, AnsiFormat... formats);

    /**
     * Draws a styled string with explicit color and format parameters.
     */
    public abstract void drawStyledChar(int x, int y, char text, AnsiColor foregroundColor, AnsiColor backgroundColor, AnsiFormat... formats);

    /**
     * Sets the foreground color for subsequent drawing operations.
     */
    public abstract void setForegroundColor(AnsiColor color);

    /**
     * Sets the background color for subsequent drawing operations.
     */
    public abstract void setBackgroundColor(AnsiColor color);

    /**
     * Sets the text formatting for subsequent drawing operations.
     */
    public abstract void setFormats(AnsiFormat... formats);

    /**
     * Resets all styling to default.
     */
    public abstract void resetStyle();

    // Common utility methods that can be shared

    /**
     * Checks if the coordinates are within the graphics bounds.
     */
    protected boolean isValid(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Draws a horizontal line with the specified character.
     */
    public void drawHorizontalLine(int x1, int x2, int y, char c) {
        if (y < 0 || y >= height) {
            return;
        }

        int startX = Math.max(0, Math.min(x1, x2));
        int endX = Math.min(width - 1, Math.max(x1, x2));

        for (int x = startX; x <= endX; x++) {
            drawChar(x, y, c);
        }
    }

    /**
     * Draws a vertical line with the specified character.
     */
    public void drawVerticalLine(int x, int y1, int y2, char c) {
        if (x < 0 || x >= width) {
            return;
        }

        int startY = Math.max(0, Math.min(y1, y2));
        int endY = Math.min(height - 1, Math.max(y1, y2));

        for (int y = startY; y <= endY; y++) {
            drawChar(x, y, c);
        }
    }

    /**
     * Draws a rectangle outline with the specified character.
     */
    public void drawRectangle(int x, int y, int width, int height, char c) {
        // Top and bottom lines
        drawHorizontalLine(x, x + width - 1, y, c);
        drawHorizontalLine(x, x + width - 1, y + height - 1, c);

        // Left and right lines
        drawVerticalLine(x, y, y + height - 1, c);
        drawVerticalLine(x + width - 1, y, y + height - 1, c);
    }

    /**
     * Draws a rectangle outline with the specified character (alternative method name).
     */
    public void drawRect(int x, int y, int width, int height, char c) {
        drawRectangle(x, y, width, height, c);
    }

    /**
     * Fills a rectangle with the specified character.
     */
    public void fillRectangle(int x, int y, int width, int height, char c) {
        for (int dy = 0; dy < height; dy++) {
            drawHorizontalLine(x, x + width - 1, y + dy, c);
        }
    }

    /**
     * Fills a rectangle with the specified character (alternative method name).
     */
    public void fillRect(int x, int y, int width, int height, char c) {
        fillRectangle(x, y, width, height, c);
    }

    public abstract StyledChar getStyledChar(int x, int y);
}
