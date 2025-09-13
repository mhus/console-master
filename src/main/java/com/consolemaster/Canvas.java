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

    // Size constraints
    private int minWidth = 0;
    private int minHeight = 0;
    private int maxWidth = Integer.MAX_VALUE;
    private int maxHeight = Integer.MAX_VALUE;

    // Layout constraint for positioning hints
    private LayoutConstraint layoutConstraint;

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
     * Creates a new Canvas with position, dimensions and size constraints.
     *
     * @param x         the x-coordinate of the canvas
     * @param y         the y-coordinate of the canvas
     * @param width     the width of the canvas
     * @param height    the height of the canvas
     * @param minWidth  the minimum width constraint
     * @param minHeight the minimum height constraint
     * @param maxWidth  the maximum width constraint
     * @param maxHeight the maximum height constraint
     */
    public Canvas(int x, int y, int width, int height, int minWidth, int minHeight, int maxWidth, int maxHeight) {
        this(x, y, width, height);
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;

        // Ensure current size respects constraints
        enforceConstraints();
    }

    /**
     * Sets the width and enforces size constraints.
     *
     * @param width the new width
     */
    public void setWidth(int width) {
        this.width = Math.max(minWidth, Math.min(maxWidth, width));
    }

    /**
     * Sets the height and enforces size constraints.
     *
     * @param height the new height
     */
    public void setHeight(int height) {
        this.height = Math.max(minHeight, Math.min(maxHeight, height));
    }

    /**
     * Sets the minimum width constraint.
     *
     * @param minWidth the minimum width
     */
    public void setMinWidth(int minWidth) {
        this.minWidth = Math.max(0, minWidth);
        enforceConstraints();
    }

    /**
     * Sets the minimum height constraint.
     *
     * @param minHeight the minimum height
     */
    public void setMinHeight(int minHeight) {
        this.minHeight = Math.max(0, minHeight);
        enforceConstraints();
    }

    /**
     * Sets the maximum width constraint.
     *
     * @param maxWidth the maximum width
     */
    public void setMaxWidth(int maxWidth) {
        this.maxWidth = Math.max(1, maxWidth);
        enforceConstraints();
    }

    /**
     * Sets the maximum height constraint.
     *
     * @param maxHeight the maximum height
     */
    public void setMaxHeight(int maxHeight) {
        this.maxHeight = Math.max(1, maxHeight);
        enforceConstraints();
    }

    /**
     * Sets both minimum width and height constraints.
     *
     * @param minWidth  the minimum width
     * @param minHeight the minimum height
     */
    public void setMinSize(int minWidth, int minHeight) {
        this.minWidth = Math.max(0, minWidth);
        this.minHeight = Math.max(0, minHeight);
        enforceConstraints();
    }

    /**
     * Sets both maximum width and height constraints.
     *
     * @param maxWidth  the maximum width
     * @param maxHeight the maximum height
     */
    public void setMaxSize(int maxWidth, int maxHeight) {
        this.maxWidth = Math.max(1, maxWidth);
        this.maxHeight = Math.max(1, maxHeight);
        enforceConstraints();
    }

    /**
     * Checks if the canvas meets its minimum size requirements.
     *
     * @return true if width >= minWidth and height >= minHeight
     */
    public boolean meetsMinimumSize() {
        return width >= minWidth && height >= minHeight;
    }

    /**
     * Checks if the canvas exceeds its maximum size constraints.
     *
     * @return true if width <= maxWidth and height <= maxHeight
     */
    public boolean withinMaximumSize() {
        return width <= maxWidth && height <= maxHeight;
    }

    /**
     * Checks if the canvas size is within all constraints.
     *
     * @return true if size meets minimum and maximum constraints
     */
    public boolean isValidSize() {
        return meetsMinimumSize() && withinMaximumSize();
    }

    /**
     * Enforces size constraints on the current width and height.
     */
    private void enforceConstraints() {
        this.width = Math.max(minWidth, Math.min(maxWidth, this.width));
        this.height = Math.max(minHeight, Math.min(maxHeight, this.height));
    }

    /**
     * Gets the layout constraint for this canvas.
     *
     * @return the layout constraint or null if not set
     */
    public LayoutConstraint getLayoutConstraint() {
        return layoutConstraint;
    }

    /**
     * Sets the layout constraint for this canvas.
     *
     * @param layoutConstraint the layout constraint for positioning hints
     */
    public void setLayoutConstraint(LayoutConstraint layoutConstraint) {
        this.layoutConstraint = layoutConstraint;
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
