package com.consolemaster;

import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;

/**
 * Base class for all drawable elements in the console framework.
 * A Canvas represents a rectangular area that can contain text and graphics
 * and can be rendered to the console.
 */
@Getter
@Setter
public abstract class Canvas {

    protected static final Comparator<? super Canvas> Z_ORDER_COMPARATOR = Comparator.comparingInt(Canvas::getZ);

    private String name;
    private int x;
    private int y;
    private int z;
    private int width;
    private int height;
    private boolean visible = true;

    // Focus management
    private boolean canFocus = false;
    private boolean hasFocus = false;

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
     * @param width  the width of the canvas
     * @param height the height of the canvas
     */
    public Canvas(String name, int width, int height) {
        this.name = name;
        this.x = 0;
        this.y = 0;
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
    public Canvas(String name, int x, int y, int width, int height, int minWidth, int minHeight, int maxWidth, int maxHeight) {
        this(name, width, height);
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;

        // Ensure current size respects constraints
        enforceConstraints();
    }

    public void setRange(int minWidth, int minHeight, int maxWidth, int maxHeight) {
        this.minWidth = Math.max(0, minWidth);
        this.minHeight = Math.max(0, minHeight);
        this.maxWidth = Math.max(1, Math.max(this.minWidth, maxWidth));
        this.maxHeight = Math.max(1, Math.max(this.minHeight, maxHeight));
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
        this.maxWidth = Math.max(1, Math.max(minWidth, maxWidth));
        enforceConstraints();
    }

    /**
     * Sets the maximum height constraint.
     *
     * @param maxHeight the maximum height
     */
    public void setMaxHeight(int maxHeight) {
        this.maxHeight = Math.max(1, Math.max(minHeight, maxHeight));
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

    // Focus management methods

    /**
     * Sets whether this canvas can receive focus.
     *
     * @param canFocus true if this canvas can receive focus
     */
    public void setCanFocus(boolean canFocus) {
        this.canFocus = canFocus;
        // If canvas can no longer receive focus, remove current focus
        if (!canFocus && hasFocus) {
            setHasFocus(false);
        }
    }

    /**
     * Sets the focus state of this canvas.
     * Note: This method should typically only be called by the FocusManager.
     *
     * @param hasFocus true if this canvas should have focus
     */
    public void setHasFocus(boolean hasFocus) {
        if (this.hasFocus != hasFocus) {
            this.hasFocus = hasFocus;
            onFocusChanged(hasFocus);
        }
    }

    /**
     * Requests focus for this canvas.
     * The actual focus granting depends on the FocusManager.
     *
     * @return true if focus was successfully requested, false otherwise
     */
    public boolean requestFocus() {
        if (!canFocus || !isVisible()) {
            return false;
        }
        // Find the ScreenCanvas and request focus through its FocusManager
        Canvas parent = findScreenCanvas();
        if (parent instanceof ScreenCanvas screenCanvas) {
            return screenCanvas.requestFocus(this);
        }
        return false;
    }

    /**
     * Finds the root ScreenCanvas for this canvas.
     *
     * @return the ScreenCanvas if found, null otherwise
     */
    private Canvas findScreenCanvas() {
        // For now, this is a simplified approach
        // In a complete implementation, we would traverse up the parent hierarchy
        return null;
    }

    /**
     * Called when the focus state of this canvas changes.
     * Subclasses can override this method to respond to focus changes.
     *
     * @param focused true if the canvas gained focus, false if it lost focus
     */
    protected void onFocusChanged(boolean focused) {
        // Default implementation does nothing
        // Subclasses can override to respond to focus changes
    }

    /**
     * Recalculates the minimum size requirements of this canvas.
     * Base implementation does nothing - subclasses should override this method
     * to implement specific packing behavior.
     */
    public void pack() {
        // Base implementation - no operation
        // Subclasses like CompositeCanvas should override this
    }

    /**
     * Abstract method for painting the canvas content.
     *
     * @param graphics the graphics context to paint on
     */
    public abstract void paint(Graphics graphics);

    /**
     * Legacy paint method for backward compatibility.
     * Creates a LegacyGraphics wrapper from char buffer.
     *
     * @param charBuffer the character buffer to draw on
     */
    public void paintLegacy(char[][] charBuffer) {
        LegacyGraphics legacyGraphics = new LegacyGraphics(charBuffer, getWidth(), getHeight());
        paint(legacyGraphics);
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

    /**
     * Alias for getCanFocus().
     */
    public boolean isCanFocus() {
        return canFocus;
    }

    /**
     * Alias for getHasFocus().
     */
    public boolean isHasFocus() {
        return hasFocus;
    }
}
