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
