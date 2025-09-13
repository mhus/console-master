package com.consolemaster;

/**
 * Legacy Graphics implementation using StyledChar buffer.
 * Provides backward compatibility for existing components.
 */
public class LegacyGraphics extends Graphics {

    private final StyledChar[][] buffer;

    // Current drawing style
    private AnsiColor currentForegroundColor;
    private AnsiColor currentBackgroundColor;
    private AnsiFormat[] currentFormats = new AnsiFormat[0];

    /**
     * Creates a Graphics context with a plain character buffer.
     */
    public LegacyGraphics(char[][] charBuffer, int width, int height) {
        super(width, height);
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
    public LegacyGraphics(StyledChar[][] buffer, int width, int height) {
        super(width, height);
        this.buffer = buffer;
    }

    @Override
    public void setForegroundColor(AnsiColor color) {
        this.currentForegroundColor = color;
    }

    @Override
    public void setBackgroundColor(AnsiColor color) {
        this.currentBackgroundColor = color;
    }

    @Override
    public void setFormats(AnsiFormat... formats) {
        this.currentFormats = formats != null ? formats : new AnsiFormat[0];
    }

    @Override
    public void resetStyle() {
        this.currentForegroundColor = null;
        this.currentBackgroundColor = null;
        this.currentFormats = new AnsiFormat[0];
    }

    @Override
    public void clear() {
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                buffer[y][x] = new StyledChar(' ');
            }
        }
    }

    @Override
    public void drawChar(int x, int y, char c) {
        if (isValid(x, y)) {
            buffer[y][x] = new StyledChar(c, currentForegroundColor, currentBackgroundColor, currentFormats);
        }
    }

    @Override
    public void drawString(int x, int y, String text) {
        if (text == null || y < 0 || y >= getHeight()) {
            return;
        }

        for (int i = 0; i < text.length() && x + i < getWidth(); i++) {
            if (x + i >= 0) {
                buffer[y][x + i] = new StyledChar(text.charAt(i), currentForegroundColor, currentBackgroundColor, currentFormats);
            }
        }
    }

    @Override
    public void drawStyledString(int x, int y, String text, AnsiColor foregroundColor, AnsiColor backgroundColor, AnsiFormat... formats) {
        if (text == null || y < 0 || y >= getHeight()) {
            return;
        }

        for (int i = 0; i < text.length() && x + i < getWidth(); i++) {
            if (x + i >= 0) {
                buffer[y][x + i] = new StyledChar(text.charAt(i), foregroundColor, backgroundColor, formats);
            }
        }
    }

    /**
     * Gets the internal buffer.
     */
    public StyledChar[][] getBuffer() {
        return buffer;
    }

    /**
     * Converts the buffer to a plain character array.
     */
    public char[][] toCharArray() {
        char[][] result = new char[getHeight()][getWidth()];
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                result[y][x] = buffer[y][x] != null ? buffer[y][x].getCharacter() : ' ';
            }
        }
        return result;
    }

    /**
     * Gets a styled character at the specified position.
     */
    public StyledChar getStyledChar(int x, int y) {
        if (isValid(x, y)) {
            return buffer[y][x];
        }
        return null;
    }

    /**
     * Sets a styled character at the specified position.
     */
    public void setStyledChar(int x, int y, StyledChar styledChar) {
        if (isValid(x, y)) {
            buffer[y][x] = styledChar;
        }
    }
}
