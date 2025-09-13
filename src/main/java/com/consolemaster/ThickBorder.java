package com.consolemaster;

import org.jline.utils.AttributedStyle;

/**
 * A thick double-line border for emphasis.
 */
public class ThickBorder implements Border {

    private final AttributedStyle style;

    /**
     * Creates a thick border with no styling.
     */
    public ThickBorder() {
        this(null);
    }

    /**
     * Creates a thick border with specified styling.
     *
     * @param style JLine AttributedStyle for the border
     */
    public ThickBorder(AttributedStyle style) {
        this.style = style;
    }

    @Override
    public int getTopThickness() { return 1; }

    @Override
    public int getBottomThickness() { return 1; }

    @Override
    public int getLeftThickness() { return 1; }

    @Override
    public int getRightThickness() { return 1; }

    @Override
    public void drawBorder(Graphics graphics, int x, int y, int width, int height) {
        // Draw border using thick characters
        char borderChar = '#';

        // Draw corners and edges
        for (int i = 0; i < width; i++) {
            graphics.drawChar(x + i, y, borderChar);
            graphics.drawChar(x + i, y + height - 1, borderChar);
        }

        for (int i = 0; i < height; i++) {
            graphics.drawChar(x, y + i, borderChar);
            graphics.drawChar(x + width - 1, y + i, borderChar);
        }
    }

    @Override
    public void drawBorder(JLineGraphics graphics, int x, int y, int width, int height) {
        if (style != null) {
            graphics.setStyle(style);
        }

        char borderChar = '#';

        // Draw border using thick characters
        for (int i = 0; i < width; i++) {
            graphics.drawChar(x + i, y, borderChar);
            graphics.drawChar(x + i, y + height - 1, borderChar);
        }

        for (int i = 0; i < height; i++) {
            graphics.drawChar(x, y + i, borderChar);
            graphics.drawChar(x + width - 1, y + i, borderChar);
        }
    }
}
