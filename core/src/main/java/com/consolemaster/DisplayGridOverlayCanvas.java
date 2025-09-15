package com.consolemaster;

/**
 * A Canvas implementation that displays a grid overlay with crosses every 10 characters
 * in both horizontal and vertical directions. This is useful for debugging and layout
 * visualization purposes.
 */
public class DisplayGridOverlayCanvas extends Canvas {

    private static final int GRID_SPACING = 10;
    private static final char CROSS_CHAR = '+';

    /**
     * Creates a new DisplayGridOverlayCanvas with the specified position and dimensions.
     *
     * @param name   the name of the canvas
     * @param x      the x-coordinate of the canvas
     * @param y      the y-coordinate of the canvas
     * @param width  the width of the canvas
     * @param height the height of the canvas
     */
    public DisplayGridOverlayCanvas(String name) {
        super(name, Integer.MAX_VALUE, Integer.MAX_VALUE);
        setZ(Integer.MAX_VALUE); // Ensure it is on top
    }

    /**
     * Creates a new DisplayGridOverlayCanvas with default name and specified position and dimensions.
     *
     * @param x      the x-coordinate of the canvas
     * @param y      the y-coordinate of the canvas
     * @param width  the width of the canvas
     * @param height the height of the canvas
     */
    public DisplayGridOverlayCanvas() {
        this("GridOverlay");
    }

    @Override
    public void paint(Graphics graphics) {

        // Draw crosses at grid intersections (including edges)
        for (int x = 0; x < graphics.getWidth(); x += GRID_SPACING) {
            for (int y = 0; y < graphics.getHeight(); y += GRID_SPACING) {
                graphics.drawStyledChar(x, y, CROSS_CHAR, AnsiColor.BRIGHT_WHITE, AnsiColor.BRIGHT_RED);
            }
        }
    }
}
