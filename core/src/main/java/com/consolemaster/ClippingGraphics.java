package com.consolemaster;

import lombok.Getter;

/**
 * A ClippingGraphics provides a restricted view of a parent Graphics context.
 * It allows a Canvas to draw within its own coordinate system starting at (0,0)
 * while the actual drawing is translated to the correct position in the parent graphics.
 *
 * All drawing operations are automatically clipped to the sub-region bounds.
 */
@Getter
public class ClippingGraphics extends Graphics {

    private final Graphics parentGraphics;
    private final int offsetX;
    private final int offsetY;

    /**
     * Creates a ClippingGraphics that represents a rectangular region of the parent graphics.
     *
     * @param parentGraphics the parent graphics context
     * @param offsetX        the x-offset in the parent graphics where this sub-region starts
     * @param offsetY        the y-offset in the parent graphics where this sub-region starts
     * @param width          the width of this sub-region
     * @param height         the height of this sub-region
     */
    public ClippingGraphics(Graphics parentGraphics, int offsetX, int offsetY, int width, int height) {
        super(width, height);
        this.parentGraphics = parentGraphics instanceof ClippingGraphics ?
                ((ClippingGraphics) parentGraphics).getParentGraphics() : parentGraphics;
        this.offsetX = parentGraphics instanceof ClippingGraphics ? ((ClippingGraphics) parentGraphics).getOffsetX() + offsetX : offsetX;
        this.offsetY = parentGraphics instanceof ClippingGraphics ? ((ClippingGraphics) parentGraphics).getOffsetY() + offsetY : offsetY;
    }

    /**
     * Translates local coordinates to parent coordinates and checks bounds.
     *
     * @param x local x coordinate
     * @param y local y coordinate
     * @return true if the translated coordinates are valid in both local and parent context
     */
    private boolean isValidAndInBounds(int x, int y) {
        // Check local bounds
        if (!isValid(x, y)) {
            return false;
        }

        // Check parent bounds
        int parentX = offsetX + x;
        int parentY = offsetY + y;
        return parentX >= 0 && parentX < parentGraphics.getWidth() &&
               parentY >= 0 && parentY < parentGraphics.getHeight();
    }

    @Override
    public void clear() {
        // Clear only this sub-region
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isValidAndInBounds(x, y)) {
                    parentGraphics.drawChar(offsetX + x, offsetY + y, ' ');
                }
            }
        }
    }

    @Override
    public void drawChar(int x, int y, char c) {
        if (isValidAndInBounds(x, y)) {
            parentGraphics.drawChar(offsetX + x, offsetY + y, c);
        }
    }

    @Override
    public void drawString(int x, int y, String text) {
        if (text == null || text.isEmpty()) {
            return;
        }

        for (int i = 0; i < text.length(); i++) {
            int charX = x + i;
            if (charX >= width) {
                break; // Stop when we reach the right boundary
            }
            if (isValidAndInBounds(charX, y)) {
                parentGraphics.drawChar(offsetX + charX, offsetY + y, text.charAt(i));
            }
        }
    }

    @Override
    public void drawStyledString(int x, int y, String text, AnsiColor foregroundColor, AnsiColor backgroundColor, AnsiFormat... formats) {
        if (text == null || text.isEmpty()) {
            return;
        }

        // Check if the starting position is within bounds
        if (!isValid(x, y)) {
            return;
        }

        // Calculate how much of the string we can actually draw
        int maxLength = Math.min(text.length(), width - x);
        if (maxLength <= 0) {
            return;
        }

        // Extract the drawable portion of the string
        String drawableText = text.substring(0, maxLength);

        if (isValidAndInBounds(x, y)) {
            parentGraphics.drawStyledString(offsetX + x, offsetY + y, drawableText, foregroundColor, backgroundColor, formats);
        }
    }

    @Override
    public void drawStyledChar(int x, int y, char text, AnsiColor foregroundColor, AnsiColor backgroundColor, AnsiFormat... formats) {
        if (isValidAndInBounds(x, y)) {
            parentGraphics.drawStyledChar(offsetX + x, offsetY + y, text, foregroundColor, backgroundColor, formats);
        }
    }

    @Override
    public void setForegroundColor(AnsiColor color) {
        parentGraphics.setForegroundColor(color);
    }

    @Override
    public void setBackgroundColor(AnsiColor color) {
        parentGraphics.setBackgroundColor(color);
    }

    @Override
    public void setFormats(AnsiFormat... formats) {
        parentGraphics.setFormats(formats);
    }

    @Override
    public void resetStyle() {
        parentGraphics.resetStyle();
    }

    // Override utility methods to respect clipping bounds

    @Override
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

    @Override
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

    @Override
    public void fillRectangle(int x, int y, int width, int height, char c) {
        for (int dy = 0; dy < height; dy++) {
            int currentY = y + dy;
            if (currentY >= this.height) {
                break;
            }
            drawHorizontalLine(x, x + width - 1, currentY, c);
        }
    }

    @Override
    public StyledChar getStyledChar(int x, int y) {
        return isValidAndInBounds(x, y) ? parentGraphics.getStyledChar(offsetX + x, offsetY + y) : null;
    }

    /**
     * Creates a sub-region of this ClippingGraphics.
     * This allows for nested clipping regions.
     *
     * @param x      the x-offset within this clipping graphics
     * @param y      the y-offset within this clipping graphics
     * @param width  the width of the new sub-region
     * @param height the height of the new sub-region
     * @return a new ClippingGraphics representing the nested region
     */
    public ClippingGraphics createClippingGraphics(int x, int y, int width, int height) {
        // Clip the requested region to our bounds
        int clippedX = Math.max(0, x);
        int clippedY = Math.max(0, y);
        int clippedWidth = Math.min(width, this.width - clippedX);
        int clippedHeight = Math.min(height, this.height - clippedY);

        // Ensure dimensions are non-negative
        clippedWidth = Math.max(0, clippedWidth);
        clippedHeight = Math.max(0, clippedHeight);

        return new ClippingGraphics(parentGraphics, offsetX + clippedX, offsetY + clippedY, clippedWidth, clippedHeight);
    }
}
