package com.consolemaster;

import lombok.Getter;
import lombok.Setter;

/**
 * A Canvas implementation for drawing horizontal or vertical ruler lines.
 * The ruler always has a width or height of 1 depending on its orientation.
 *
 * For horizontal rulers: height is always 1, width can vary
 * For vertical rulers: width is always 1, height can vary
 */
@Getter
@Setter
public class Ruler extends Canvas {

    /**
     * Orientation for the Ruler.
     */
    public enum Orientation {
        HORIZONTAL,  // Horizontal line (height = 1)
        VERTICAL     // Vertical line (width = 1)
    }

    private final Orientation orientation;
    private char lineChar;
    private AnsiColor lineColor = AnsiColor.WHITE;
    private AnsiFormat[] lineFormats = new AnsiFormat[0];

    /**
     * Creates a horizontal Ruler with specified length.
     *
     * @param name   the name of the canvas
     */
    public Ruler(String name) {
        this(name, Orientation.HORIZONTAL);
    }

    /**
     * Creates a Ruler with specified orientation and length.
     *
     * @param name        the name of the canvas
     * @param orientation the orientation of the ruler
     */
    public Ruler(String name, Orientation orientation) {
        super(name, 1, 1);

        this.orientation = orientation;

        // Set appropriate default character based on orientation
        this.lineChar = (orientation == Orientation.HORIZONTAL) ? '-' : '|';

        // Set size constraints to enforce ruler behavior
        if (orientation == Orientation.HORIZONTAL) {
            setRange(1, 1, Integer.MAX_VALUE, 1);  // min/max height = 1
        } else {
            setRange(1, 1, 1, Integer.MAX_VALUE);  // min/max width = 1
        }
    }

    /**
     * Sets the length of the ruler.
     * For horizontal rulers, this sets the width.
     * For vertical rulers, this sets the height.
     *
     * @param length the new length
     */
    public void setLength(int length) {
        if (orientation == Orientation.HORIZONTAL) {
            setWidth(Math.max(1, length));
        } else {
            setHeight(Math.max(1, length));
        }
    }

    /**
     * Gets the length of the ruler.
     * For horizontal rulers, this returns the width.
     * For vertical rulers, this returns the height.
     *
     * @return the length of the ruler
     */
    public int getLength() {
        return orientation == Orientation.HORIZONTAL ? getWidth() : getHeight();
    }

    /**
     * Overrides setWidth to enforce ruler constraints.
     * For vertical rulers, width is always 1.
     */
    @Override
    public void setWidth(int width) {
        if (orientation == Orientation.VERTICAL) {
            super.setWidth(1);  // Force width to 1 for vertical rulers
        } else {
            super.setWidth(Math.max(1, width));  // Allow width changes for horizontal rulers
        }
    }

    /**
     * Overrides setHeight to enforce ruler constraints.
     * For horizontal rulers, height is always 1.
     */
    @Override
    public void setHeight(int height) {
        if (orientation == Orientation.HORIZONTAL) {
            super.setHeight(1);  // Force height to 1 for horizontal rulers
        } else {
            super.setHeight(Math.max(1, height));  // Allow height changes for vertical rulers
        }
    }

    @Override
    public void paint(Graphics graphics) {
        graphics.clear();

        if (!isVisible()) {
            return;
        }

        // Apply color and formatting
        if (lineColor != null) {
            graphics.setForegroundColor(lineColor);
        }

        if (lineFormats.length > 0) {
            graphics.setFormats(lineFormats);
        }

        if (orientation == Orientation.HORIZONTAL) {
            // Draw horizontal line using utility method
            graphics.drawHorizontalLine(0, getWidth() - 1, 0, lineChar);
        } else {
            // Draw vertical line using utility method
            graphics.drawVerticalLine(0, 0, getHeight() - 1, lineChar);
        }

        // Reset styling
        graphics.resetStyle();
    }

    /**
     * Creates a horizontal ruler with default settings.
     *
     * @param name   the name of the ruler
     * @return a new horizontal Ruler
     */
    public static Ruler horizontal(String name) {
        return new Ruler(name, Orientation.HORIZONTAL);
    }

    /**
     * Creates a vertical ruler with default settings.
     *
     * @param name   the name of the ruler
     * @return a new vertical Ruler
     */
    public static Ruler vertical(String name) {
        return new Ruler(name, Orientation.VERTICAL);
    }

    @Override
    public String toString() {
        return String.format("Ruler[name=%s, orientation=%s, length=%d, char='%c']",
                           getName(), orientation, getLength(), lineChar);
    }
}
