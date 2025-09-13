package com.consolemaster;

import lombok.Getter;
import lombok.Setter;

/**
 * A simple canvas implementation that displays text content.
 * Useful for displaying static text or simple text-based UI elements.
 */
@Getter
@Setter
public class TextCanvas extends Canvas {

    private String text;
    private char borderChar = '*';
    private boolean showBorder = false;

    /**
     * Creates a new TextCanvas with the specified position, dimensions and text.
     *
     * @param x      the x-coordinate of the canvas
     * @param y      the y-coordinate of the canvas
     * @param width  the width of the canvas
     * @param height the height of the canvas
     * @param text   the text to display
     */
    public TextCanvas(int x, int y, int width, int height, String text) {
        super(x, y, width, height);
        this.text = text != null ? text : "";
    }

    /**
     * Creates a new TextCanvas with border enabled.
     *
     * @param x          the x-coordinate of the canvas
     * @param y          the y-coordinate of the canvas
     * @param width      the width of the canvas
     * @param height     the height of the canvas
     * @param text       the text to display
     * @param showBorder whether to show a border around the text
     */
    public TextCanvas(int x, int y, int width, int height, String text, boolean showBorder) {
        this(x, y, width, height, text);
        this.showBorder = showBorder;
    }

    @Override
    public void paint(Graphics graphics) {
        // Draw border if enabled
        if (showBorder) {
            graphics.drawRect(getX(), getY(), getWidth(), getHeight(), borderChar);
        }

        // Calculate text area
        int textX = getX() + (showBorder ? 1 : 0);
        int textY = getY() + (showBorder ? 1 : 0);
        int textWidth = getWidth() - (showBorder ? 2 : 0);
        int textHeight = getHeight() - (showBorder ? 2 : 0);

        // Split text into lines and draw
        if (text != null && !text.isEmpty()) {
            String[] lines = text.split("\n");
            for (int i = 0; i < lines.length && i < textHeight; i++) {
                String line = lines[i];
                if (line.length() > textWidth) {
                    line = line.substring(0, textWidth);
                }
                graphics.drawString(textX, textY + i, line);
            }
        }
    }
}
