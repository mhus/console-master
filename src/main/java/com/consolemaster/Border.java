package com.consolemaster;

/**
 * Interface for drawing borders around components.
 * Borders can define their own thickness and drawing style.
 */
public interface Border {

    /**
     * Gets the thickness of the border on the top side.
     *
     * @return the top border thickness in characters
     */
    default int getTopThickness() {
        return getBorderHeight();
    }

    /**
     * Gets the thickness of the border on the bottom side.
     *
     * @return the bottom border thickness in characters
     */
    default int getBottomThickness() {
        return getBorderHeight();
    }

    /**
     * Gets the thickness of the border on the left side.
     *
     * @return the left border thickness in characters
     */
    default int getLeftThickness() {
        return getBorderWidth();
    }

    /**
     * Gets the thickness of the border on the right side.
     *
     * @return the right border thickness in characters
     */
    default int getRightThickness() {
        return getBorderWidth();
    }

    /**
     * Gets the border width (for legacy compatibility).
     */
    int getBorderWidth();

    /**
     * Gets the border height (for legacy compatibility).
     */
    int getBorderHeight();

    /**
     * Draws the border using the Graphics context.
     *
     * @param graphics the graphics context to draw on
     * @param x the x-coordinate of the component
     * @param y the y-coordinate of the component
     * @param width the width of the component
     * @param height the height of the component
     */
    void drawBorder(Graphics graphics, int x, int y, int width, int height);

    /**
     * Gets the total horizontal space consumed by the border.
     *
     * @return left thickness + right thickness
     */
    default int getHorizontalInsets() {
        return getLeftThickness() + getRightThickness();
    }

    /**
     * Gets the total vertical space consumed by the border.
     *
     * @return top thickness + bottom thickness
     */
    default int getVerticalInsets() {
        return getTopThickness() + getBottomThickness();
    }
}
