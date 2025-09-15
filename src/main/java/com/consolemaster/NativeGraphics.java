package com.consolemaster;

import lombok.Getter;

/**
 * Native Graphics implementation that replaces JLineGraphics.
 * Uses StyledChar instead of JLine's AttributedString for ANSI support.
 */
@Getter
public class NativeGraphics extends Graphics {

    private final StyledChar[][] buffer;
    private TextStyle currentStyle = TextStyle.DEFAULT;

    /**
     * Creates a native Graphics context.
     */
    public NativeGraphics(int width, int height) {
        super(width, height);
        this.buffer = new StyledChar[height][width];
        clear();
    }

    /**
     * Sets the current style for subsequent drawing operations.
     */
    public void setStyle(TextStyle style) {
        this.currentStyle = style != null ? style : TextStyle.DEFAULT;
    }

    @Override
    public void setForegroundColor(AnsiColor color) {
        if (color != null) {
            this.currentStyle = currentStyle.withForeground(color);
        }
    }

    @Override
    public void setBackgroundColor(AnsiColor color) {
        if (color != null) {
            this.currentStyle = currentStyle.withBackground(color);
        }
    }

    @Override
    public void setFormats(AnsiFormat... formats) {
        if (formats != null) {
            this.currentStyle = TextStyle.DEFAULT.withFormats(formats);
        } else {
            this.currentStyle = TextStyle.DEFAULT;
        }
    }

    /**
     * Resets the current style to default.
     */
    @Override
    public void resetStyle() {
        this.currentStyle = TextStyle.DEFAULT;
    }

    @Override
    public void clear() {
        StyledChar emptyCell = new StyledChar(' ', TextStyle.DEFAULT);
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                buffer[y][x] = emptyCell;
            }
        }
    }

    @Override
    public void drawChar(int x, int y, char c) {
        if (isValidPosition(x, y)) {
            buffer[y][x] = new StyledChar(c, currentStyle);
        }
    }

    @Override
    public void drawString(int x, int y, String text) {
        if (text == null || text.isEmpty()) return;

        for (int i = 0; i < text.length() && x + i < getWidth(); i++) {
            if (isValidPosition(x + i, y)) {
                buffer[y][x + i] = new StyledChar(text.charAt(i), currentStyle);
            }
        }
    }

    @Override
    public void drawStyledString(int x, int y, String text, AnsiColor foreground, AnsiColor background, AnsiFormat... formats) {
        if (text == null || text.isEmpty()) return;

        TextStyle style = new TextStyle(foreground, background, formats);
        for (int i = 0; i < text.length() && x + i < getWidth(); i++) {
            if (isValidPosition(x + i, y)) {
                buffer[y][x + i] = new StyledChar(text.charAt(i), style);
            }
        }
    }

    /**
     * Draws a styled string using the provided TextStyle.
     */
    public void drawStyledString(int x, int y, String text, TextStyle style) {
        if (text == null || text.isEmpty()) return;

        for (int i = 0; i < text.length() && x + i < getWidth(); i++) {
            if (isValidPosition(x + i, y)) {
                buffer[y][x + i] = new StyledChar(text.charAt(i), style);
            }
        }
    }

    /**
     * Gets the styled string at the specified position.
     */
    public StyledString getStyledString(int x, int y) {
        if (isValidPosition(x, y)) {
            StyledChar styledChar = buffer[y][x];
            return new StyledString(String.valueOf(styledChar.getCharacter()), styledChar.getStyle());
        }
        return new StyledString(" ");
    }

    /**
     * Sets a styled string at the specified position.
     */
    public void setStyledString(int x, int y, StyledString styledString) {
        if (isValidPosition(x, y) && styledString != null && !styledString.isEmpty()) {
            char c = styledString.charAt(0);
            buffer[y][x] = new StyledChar(c, styledString.getStyle());
        } else if (isValidPosition(x, y)) {
            buffer[y][x] = new StyledChar(' ', TextStyle.DEFAULT);
        }
    }

    /**
     * Converts the graphics buffer to a 2D array of StyledString for rendering.
     */
    public StyledString[][] toStyledStringArray() {
        StyledString[][] result = new StyledString[getHeight()][getWidth()];
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                StyledChar styledChar = buffer[y][x];
                result[y][x] = new StyledString(String.valueOf(styledChar.getCharacter()), styledChar.getStyle());
            }
        }
        return result;
    }

    /**
     * Renders the graphics buffer to the terminal using ANSI escape sequences.
     */
    public void toAnsiString(Terminal terminal) {
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                StyledChar styledChar = buffer[y][x];
                StyledString styledString = new StyledString(String.valueOf(styledChar.getCharacter()), styledChar.getStyle());
                styledString.toAnsiString(terminal);
            }
            if (y < getHeight() - 1) {
                terminal.write("\n");
            }
        }
    }

    /**
     * Legacy compatibility method - converts to char array.
     */
    public char[][] toCharArray() {
        char[][] result = new char[getHeight()][getWidth()];
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                result[y][x] = buffer[y][x].getCharacter();
            }
        }
        return result;
    }

    /**
     * Gets a styled character at the specified position for legacy compatibility.
     */
    public StyledChar getStyledChar(int x, int y) {
        if (isValidPosition(x, y)) {
            return buffer[y][x];
        }
        return new StyledChar(' ', TextStyle.DEFAULT);
    }

    /**
     * Sets a styled character at the specified position for legacy compatibility.
     */
    public void setStyledChar(int x, int y, StyledChar styledChar) {
        if (isValidPosition(x, y) && styledChar != null) {
            buffer[y][x] = styledChar;
        }
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }
}
