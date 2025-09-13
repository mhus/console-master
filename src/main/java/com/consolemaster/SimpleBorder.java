package com.consolemaster;

import org.jline.utils.AttributedStyle;

/**
 * A simple single-line border using ASCII characters.
 */
public class SimpleBorder implements Border {

    private final char topBottomChar;
    private final char leftRightChar;
    private final char cornerChar;
    private final AttributedStyle style;

    /**
     * Creates a simple border with default characters and no styling.
     */
    public SimpleBorder() {
        this('-', '|', '+', null);
    }

    /**
     * Creates a simple border with specified characters.
     *
     * @param topBottomChar character for top and bottom borders
     * @param leftRightChar character for left and right borders
     * @param cornerChar character for corners
     */
    public SimpleBorder(char topBottomChar, char leftRightChar, char cornerChar) {
        this(topBottomChar, leftRightChar, cornerChar, null);
    }

    /**
     * Creates a simple border with specified characters and styling.
     *
     * @param topBottomChar character for top and bottom borders
     * @param leftRightChar character for left and right borders
     * @param cornerChar character for corners
     * @param style JLine AttributedStyle for the border
     */
    public SimpleBorder(char topBottomChar, char leftRightChar, char cornerChar, AttributedStyle style) {
        this.topBottomChar = topBottomChar;
        this.leftRightChar = leftRightChar;
        this.cornerChar = cornerChar;
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
        // Draw corners
        graphics.drawChar(x, y, cornerChar);
        graphics.drawChar(x + width - 1, y, cornerChar);
        graphics.drawChar(x, y + height - 1, cornerChar);
        graphics.drawChar(x + width - 1, y + height - 1, cornerChar);

        // Draw top and bottom borders
        for (int i = 1; i < width - 1; i++) {
            graphics.drawChar(x + i, y, topBottomChar);
            graphics.drawChar(x + i, y + height - 1, topBottomChar);
        }

        // Draw left and right borders
        for (int i = 1; i < height - 1; i++) {
            graphics.drawChar(x, y + i, leftRightChar);
            graphics.drawChar(x + width - 1, y + i, leftRightChar);
        }
    }

    @Override
    public void drawBorder(JLineGraphics graphics, int x, int y, int width, int height) {
        if (style != null) {
            graphics.setStyle(style);
        }

        // Draw corners
        graphics.drawChar(x, y, cornerChar);
        graphics.drawChar(x + width - 1, y, cornerChar);
        graphics.drawChar(x, y + height - 1, cornerChar);
        graphics.drawChar(x + width - 1, y + height - 1, cornerChar);

        // Draw top and bottom borders
        for (int i = 1; i < width - 1; i++) {
            graphics.drawChar(x + i, y, topBottomChar);
            graphics.drawChar(x + i, y + height - 1, topBottomChar);
        }

        // Draw left and right borders
        for (int i = 1; i < height - 1; i++) {
            graphics.drawChar(x, y + i, leftRightChar);
            graphics.drawChar(x + width - 1, y + i, leftRightChar);
        }
    }
}
