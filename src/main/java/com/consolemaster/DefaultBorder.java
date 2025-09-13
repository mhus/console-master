package com.consolemaster;

import lombok.Getter;
import lombok.Setter;

/**
 * A simple border implementation that can be drawn around a canvas.
 * Now uses native TextStyle instead of JLine's AttributedStyle.
 */
@Getter
@Setter
public class DefaultBorder implements Border {

    private final BorderStyle borderStyle;

    // Native styling
    private TextStyle textStyle = TextStyle.DEFAULT;
    private AnsiColor borderColor;
    private AnsiFormat[] borderFormats;

    /**
     * Creates a SimpleBorder with default single-line style.
     */
    public DefaultBorder() {
        this.borderStyle = BorderStyle.SINGLE;
    }

    /**
     * Creates a SimpleBorder with the specified border style.
     *
     * @param borderStyle the style to use for drawing the border
     */
    public DefaultBorder(BorderStyle borderStyle) {
        this.borderStyle = borderStyle != null ? borderStyle : BorderStyle.SINGLE;
    }

    /**
     * Creates a SimpleBorder with color styling.
     *
     * @param color the border color
     */
    public DefaultBorder(AnsiColor color) {
        this.borderStyle = BorderStyle.SINGLE;
        this.borderColor = color;
        updateTextStyle();
    }

    /**
     * Creates a SimpleBorder with border style and color.
     *
     * @param borderStyle the style to use for drawing the border
     * @param color the border color
     */
    public DefaultBorder(BorderStyle borderStyle, AnsiColor color) {
        this.borderStyle = borderStyle != null ? borderStyle : BorderStyle.SINGLE;
        this.borderColor = color;
        updateTextStyle();
    }

    /**
     * Creates a SimpleBorder with color and formatting.
     *
     * @param color the border color
     * @param formats the text formatting options
     */
    public DefaultBorder(AnsiColor color, AnsiFormat... formats) {
        this.borderStyle = BorderStyle.SINGLE;
        this.borderColor = color;
        this.borderFormats = formats;
        updateTextStyle();
    }

    /**
     * Creates a SimpleBorder with border style, color and formatting.
     *
     * @param borderStyle the style to use for drawing the border
     * @param color the border color
     * @param formats the text formatting options
     */
    public DefaultBorder(BorderStyle borderStyle, AnsiColor color, AnsiFormat... formats) {
        this.borderStyle = borderStyle != null ? borderStyle : BorderStyle.SINGLE;
        this.borderColor = color;
        this.borderFormats = formats;
        updateTextStyle();
    }

    // Legacy constructors for backward compatibility
    public DefaultBorder(char borderChar) {
        this.borderStyle = BorderStyle.SINGLE; // Use single style, char will be ignored
    }

    public DefaultBorder(char horizontal, char vertical, char corner) {
        this.borderStyle = BorderStyle.SINGLE; // Use single style, chars will be ignored
    }

    /**
     * Sets the border color.
     */
    public void setBorderColor(AnsiColor color) {
        this.borderColor = color;
        updateTextStyle();
    }

    /**
     * Sets the border formats.
     */
    public void setBorderFormats(AnsiFormat... formats) {
        this.borderFormats = formats;
        updateTextStyle();
    }

    /**
     * Updates the border style based on current color and format settings.
     */
    private void updateTextStyle() {
        this.textStyle = new TextStyle(borderColor, null, borderFormats);
    }

    /**
     * Sets the border style directly.
     */
    public void setBorderStyle(TextStyle style) {
        this.textStyle = style != null ? style : TextStyle.DEFAULT;
    }

    @Override
    public void drawBorder(Graphics graphics, int x, int y, int width, int height) {
        if (width < 2 || height < 2) {
            return; // Too small to draw a border
        }

        // Draw corners using BorderStyle characters
        drawStyledChar(graphics, x, y, borderStyle.getTopLeft());
        drawStyledChar(graphics, x + width - 1, y, borderStyle.getTopRight());
        drawStyledChar(graphics, x, y + height - 1, borderStyle.getBottomLeft());
        drawStyledChar(graphics, x + width - 1, y + height - 1, borderStyle.getBottomRight());

        // Draw horizontal borders (top and bottom)
        for (int i = x + 1; i < x + width - 1; i++) {
            drawStyledChar(graphics, i, y, borderStyle.getHorizontal());
            drawStyledChar(graphics, i, y + height - 1, borderStyle.getHorizontal());
        }

        // Draw vertical borders (left and right)
        for (int i = y + 1; i < y + height - 1; i++) {
            drawStyledChar(graphics, x, i, borderStyle.getVertical());
            drawStyledChar(graphics, x + width - 1, i, borderStyle.getVertical());
        }
    }

    /**
     * Draws a single character with the border style.
     */
    private void drawStyledChar(Graphics graphics, int x, int y, char c) {
        if (textStyle.hasFormatting()) {
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
