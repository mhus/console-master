package com.consolemaster;

import lombok.Getter;
import lombok.Setter;

/**
 * Base class for all drawable elements in the console framework.
 * A Canvas represents a rectangular area that can contain text and graphics
 * and can be rendered to the console.
 */
@Getter
@Setter
public abstract class Canvas {

    private int x;
    private int y;
    private int width;
    private int height;
    private boolean visible = true;

    /**
     * Creates a new Canvas with the specified position and dimensions.
     *
     * @param x      the x-coordinate of the canvas
     * @param y      the y-coordinate of the canvas
     * @param width  the width of the canvas
     * @param height the height of the canvas
     */
    public Canvas(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Paint method to draw the content of this canvas.
     * This method should be implemented by subclasses to define
     * what content should be rendered.
     *
     * @param graphics the graphics context to draw on
     */
    public abstract void paint(Graphics graphics);

    /**
     * Paint method using JLine's enhanced graphics context.
     * Default implementation delegates to the standard paint method.
     * Override this for better JLine integration.
     *
     * @param graphics the JLine graphics context to draw on
     */
    public void paint(JLineGraphics graphics) {
        // Default implementation: create a legacy Graphics wrapper
        char[][] charBuffer = new char[getHeight()][getWidth()];
        Graphics legacyGraphics = new Graphics(charBuffer, getWidth(), getHeight());
        paint(legacyGraphics);

        // Copy the result to JLine graphics
        char[][] result = legacyGraphics.toCharArray();
        for (int y = 0; y < getHeight() && y < graphics.getHeight(); y++) {
            for (int x = 0; x < getWidth() && x < graphics.getWidth(); x++) {
                graphics.drawChar(getX() + x, getY() + y, result[y][x]);
            }
        }
    }

    /**
     * Checks if the given coordinates are within the bounds of this canvas.
     *
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return true if the coordinates are within bounds, false otherwise
     */
    public boolean contains(int x, int y) {
        return x >= this.x && x < this.x + this.width &&
               y >= this.y && y < this.y + this.height;
    }

    /**
     * Gets the right boundary of this canvas.
     *
     * @return the x-coordinate of the right edge
     */
    public int getRight() {
        return x + width;
    }

    /**
     * Gets the bottom boundary of this canvas.
     *
     * @return the y-coordinate of the bottom edge
     */
    public int getBottom() {
        return y + height;
    }
}
