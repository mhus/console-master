package com.consolemaster;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * A specialized CompositeCanvas that draws a border and contains exactly one child.
 * The child is automatically positioned within the border's inner area.
 */
@Getter
@Setter
public class Box extends Canvas implements Composable {

    private final Border border;
    private Canvas child;

    /**
     * Creates a Box with a simple border.
     *
     * @param name the name of the box
     * @param x the x-coordinate of the box
     * @param y the y-coordinate of the box
     * @param width the width of the box
     * @param height the height of the box
     */
    public Box(String name, int x, int y, int width, int height) {
        this(name, width, height, new DefaultBorder());
    }

    /**
     * Creates a Box with a specified border.
     *
     * @param name   the name of the box
     * @param width  the width of the box
     * @param height the height of the box
     * @param border the border to draw around the content
     */
    public Box(String name, int width, int height, Border border) {
        super(name, width, height);
        this.border = border != null ? border : new DefaultBorder();
    }

    /**
     * Sets the child content of this box. Only one child is allowed.
     * The child will be automatically positioned within the border's inner area.
     *
     * @param child the child canvas to display inside the box
     */
    public void setChild(Canvas child) {
        this.child = child;
        if (child != null) {
            updateChildBounds();
        }
    }

    /**
     * Gets the child content of this box.
     *
     * @return the child canvas or null if no child is set
     */
    public Canvas getChild() {
        return child;
    }

    /**
     * Gets the inner width available for the child (excluding border).
     *
     * @return the width available for content
     */
    public int getInnerWidth() {
        return Math.max(0, getWidth() - border.getHorizontalInsets());
    }

    /**
     * Gets the inner height available for the child (excluding border).
     *
     * @return the height available for content
     */
    public int getInnerHeight() {
        return Math.max(0, getHeight() - border.getVerticalInsets());
    }

    /**
     * Gets the x-coordinate of the inner content area.
     *
     * @return the inner x-coordinate
     */
    public int getInnerX() {
        return getX() + border.getLeftThickness();
    }

    /**
     * Gets the y-coordinate of the inner content area.
     *
     * @return the inner y-coordinate
     */
    public int getInnerY() {
        return getY() + border.getTopThickness();
    }

    /**
     * Updates the bounds of the child to fit within the inner area.
     * With SubGraphics, the child should be positioned relative to the inner area.
     */
    private void updateChildBounds() {
        if (child != null) {
            // Position child relative to the inner area (starting at border thickness)
            child.setX(border.getLeftThickness());
            child.setY(border.getTopThickness());
            child.setWidth(getInnerWidth());
            child.setHeight(getInnerHeight());
        }
    }

    /**
     * Overridden to update child bounds when box position changes.
     */
    @Override
    public void setX(int x) {
        super.setX(x);
        updateChildBounds();
    }

    /**
     * Overridden to update child bounds when box position changes.
     */
    @Override
    public void setY(int y) {
        super.setY(y);
        updateChildBounds();
    }

    /**
     * Overridden to update child bounds when box size changes.
     */
    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        updateChildBounds();
    }

    /**
     * Overridden to update child bounds when box size changes.
     */
    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        updateChildBounds();
    }

    /**
     * Gets the border of this box.
     *
     * @return the border used by this box
     */
    public Border getBorder() {
        return border;
    }

    /**
     * Packs the box to fit its child content.
     * Calculates the minimum size needed based on the child's requirements plus border space.
     */
    @Override
    public void pack() {
        if (child != null) {
            // Pack the child first
            child.pack();

            // Calculate required size including border
            int requiredWidth = child.getMinWidth() + border.getHorizontalInsets();
            int requiredHeight = child.getMinHeight() + border.getVerticalInsets();

            // Update our minimum size
            setMinWidth(requiredWidth);
            setMinHeight(requiredHeight);

            // If current size is smaller than required, resize
            if (getWidth() < requiredWidth) {
                setWidth(requiredWidth);
            }
            if (getHeight() < requiredHeight) {
                setHeight(requiredHeight);
            }

            // Update child bounds after potential size change
            updateChildBounds();
        }
    }

    @Override
    public void paint(Graphics graphics) {
        // Draw the border using local coordinates (0,0 to width,height)
        border.drawBorder(graphics, 0, 0, getWidth(), getHeight(), child != null && child.isCanFocus() && child.isHasFocus());

        // Draw the child if it exists and is visible
        if (child != null && child.isVisible()) {
            // Create a ClippingGraphics for the child that represents the inner area
            // (excluding the border space)
            ClippingGraphics childGraphics = new ClippingGraphics(
                graphics,
                child.getX(),
                child.getY(),
                child.getWidth(),
                child.getHeight()
            );

            // The child can now draw starting at (0,0) in its own coordinate system
            child.paint(childGraphics);
        }
    }

    @Override
    public List<Canvas> getChildren() {
        return child != null ? List.of(child) : List.of();
    }

    @Override
    public int getChildCount() {
        return child != null ? 1 : 0;
    }
}
