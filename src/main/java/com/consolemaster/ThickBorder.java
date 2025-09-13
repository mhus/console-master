package com.consolemaster;

import lombok.Getter;
import lombok.Setter;

/**
 * A thick border implementation using '#' characters.
 * Now uses native TextStyle instead of JLine's AttributedStyle.
 */
@Getter
@Setter
public class ThickBorder implements Border {

    private char borderChar = '#';

    // Native styling
    private TextStyle borderStyle = TextStyle.DEFAULT;
    private AnsiColor borderColor;
    private AnsiFormat[] borderFormats;

    public ThickBorder() {
        // Default thick border
    }

    public ThickBorder(char borderChar) {
        this.borderChar = borderChar;
    }

    public ThickBorder(AnsiColor color) {
        this.borderColor = color;
        updateBorderStyle();
    }

    public ThickBorder(AnsiColor color, AnsiFormat... formats) {
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
        if (width < 1 || height < 1) {
            return;
        }

        // Draw top and bottom borders
        for (int i = x; i < x + width; i++) {
            drawStyledChar(graphics, i, y, borderChar);
            if (height > 1) {
                drawStyledChar(graphics, i, y + height - 1, borderChar);
            }
        }

        // Draw left and right borders
        for (int i = y; i < y + height; i++) {
            drawStyledChar(graphics, x, i, borderChar);
            if (width > 1) {
                drawStyledChar(graphics, x + width - 1, i, borderChar);
            }
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
        return 1;
    }

    @Override
    public int getBorderHeight() {
        return 1;
    }
}
