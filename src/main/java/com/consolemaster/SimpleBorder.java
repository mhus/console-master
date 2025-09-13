package com.consolemaster;

import lombok.Getter;
import lombok.Setter;

/**
 * A simple border implementation that can be drawn around a canvas.
 * Now uses native TextStyle instead of JLine's AttributedStyle.
 */
@Getter
@Setter
public class SimpleBorder implements Border {

    private char topChar = '-';
    private char bottomChar = '-';
    private char leftChar = '|';
    private char rightChar = '|';
    private char topLeftChar = '+';
    private char topRightChar = '+';
    private char bottomLeftChar = '+';
    private char bottomRightChar = '+';

    // Native styling
    private TextStyle borderStyle = TextStyle.DEFAULT;
    private AnsiColor borderColor;
    private AnsiFormat[] borderFormats;

    public SimpleBorder() {
        // Default border with no special styling
    }

    public SimpleBorder(char borderChar) {
        this.topChar = borderChar;
        this.bottomChar = borderChar;
        this.leftChar = borderChar;
        this.rightChar = borderChar;
        this.topLeftChar = borderChar;
        this.topRightChar = borderChar;
        this.bottomLeftChar = borderChar;
        this.bottomRightChar = borderChar;
    }

    public SimpleBorder(char horizontal, char vertical, char corner) {
        this.topChar = horizontal;
        this.bottomChar = horizontal;
        this.leftChar = vertical;
        this.rightChar = vertical;
        this.topLeftChar = corner;
        this.topRightChar = corner;
        this.bottomLeftChar = corner;
        this.bottomRightChar = corner;
    }

    public SimpleBorder(AnsiColor color) {
        this.borderColor = color;
        updateBorderStyle();
    }

    public SimpleBorder(AnsiColor color, AnsiFormat... formats) {
        this.borderColor = color;
        this.borderFormats = formats;
        updateBorderStyle();
    }

    /**
     * Sets the border color.
     */
    public void setBorderColor(AnsiColor color) {
        this.borderColor = color;
        updateBorderStyle();
    }

    /**
     * Sets the border formats.
     */
    public void setBorderFormats(AnsiFormat... formats) {
        this.borderFormats = formats;
        updateBorderStyle();
    }

    /**
     * Updates the border style based on current color and format settings.
     */
    private void updateBorderStyle() {
        this.borderStyle = new TextStyle(borderColor, null, borderFormats);
    }

    /**
     * Sets the border style directly.
     */
    public void setBorderStyle(TextStyle style) {
        this.borderStyle = style != null ? style : TextStyle.DEFAULT;
    }

    @Override
    public void drawBorder(Graphics graphics, int x, int y, int width, int height) {
        if (width < 2 || height < 2) {
            return; // Too small to draw a border
        }

        // Draw corners
        drawStyledChar(graphics, x, y, topLeftChar);
        drawStyledChar(graphics, x + width - 1, y, topRightChar);
        drawStyledChar(graphics, x, y + height - 1, bottomLeftChar);
        drawStyledChar(graphics, x + width - 1, y + height - 1, bottomRightChar);

        // Draw horizontal borders
        for (int i = x + 1; i < x + width - 1; i++) {
            drawStyledChar(graphics, i, y, topChar);
            drawStyledChar(graphics, i, y + height - 1, bottomChar);
        }

        // Draw vertical borders
        for (int i = y + 1; i < y + height - 1; i++) {
            drawStyledChar(graphics, x, i, leftChar);
            drawStyledChar(graphics, x + width - 1, i, rightChar);
        }
    }

    /**
     * Draws a single character with the border style.
     */
    private void drawStyledChar(Graphics graphics, int x, int y, char c) {
        if (borderStyle.hasFormatting()) {
            graphics.drawStyledString(x, y, String.valueOf(c), borderColor, null, borderFormats);
        } else {
            graphics.drawChar(x, y, c);
        }
    }

    @Override
    public int getBorderWidth() {
        return 1; // Simple border is always 1 character wide
    }

    @Override
    public int getBorderHeight() {
        return 1; // Simple border is always 1 character high
    }
}
