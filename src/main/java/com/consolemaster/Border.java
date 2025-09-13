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
    int getTopThickness();

    /**
     * Gets the thickness of the border on the bottom side.
     *
     * @return the bottom border thickness in characters
     */
    int getBottomThickness();

    /**
     * Gets the thickness of the border on the left side.
     *
     * @return the left border thickness in characters
     */
    int getLeftThickness();

    /**
     * Gets the thickness of the border on the right side.
     *
     * @return the right border thickness in characters
     */
    int getRightThickness();

    /**
     * Draws the border using the legacy Graphics context.
     *
     * @param graphics the graphics context to draw on
     * @param x the x-coordinate of the component
     * @param y the y-coordinate of the component
     * @param width the width of the component
     * @param height the height of the component
     */
    void drawBorder(Graphics graphics, int x, int y, int width, int height);

    /**
     * Draws the border using the enhanced JLine Graphics context.
     * Default implementation delegates to the legacy drawBorder method.
     *
     * @param graphics the JLine graphics context to draw on
     * @param x the x-coordinate of the component
     * @param y the y-coordinate of the component
     * @param width the width of the component
     * @param height the height of the component
     */
    default void drawBorder(JLineGraphics graphics, int x, int y, int width, int height) {
        // Default implementation: create a legacy Graphics wrapper
        char[][] charBuffer = new char[height][width];
        LegacyGraphics legacyGraphics = new LegacyGraphics(charBuffer, width, height);
        drawBorder(legacyGraphics, 0, 0, width, height);

        // Copy the result to JLine graphics
        char[][] result = legacyGraphics.toCharArray();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                graphics.drawChar(x + col, y + row, result[row][col]);
            }
        }
    }

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
