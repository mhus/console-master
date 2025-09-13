package com.consolemaster;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * A composite canvas that can contain multiple child canvases.
 * Child canvases are rendered in the order they were added.
 * Uses a layout manager to arrange child components.
 */
@Getter
public class CompositeCanvas extends Canvas {

    private final List<Canvas> children = new ArrayList<>();
    private Layout layout = NoLayout.INSTANCE; // Default layout

    /**
     * Creates a new CompositeCanvas with the specified position and dimensions.
     *
     * @param x      the x-coordinate of the canvas
     * @param y      the y-coordinate of the canvas
     * @param width  the width of the canvas
     * @param height the height of the canvas
     */
    public CompositeCanvas(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    /**
     * Creates a new CompositeCanvas with a specific layout.
     *
     * @param x      the x-coordinate of the canvas
     * @param y      the y-coordinate of the canvas
     * @param width  the width of the canvas
     * @param height the height of the canvas
     * @param layout the layout manager to use
     */
    public CompositeCanvas(int x, int y, int width, int height, Layout layout) {
        super(x, y, width, height);
        this.layout = layout != null ? layout : NoLayout.INSTANCE;
    }

    /**
     * Sets the layout manager for this composite canvas.
     *
     * @param layout the new layout manager (null defaults to NoLayout)
     */
    public void setLayout(Layout layout) {
        this.layout = layout != null ? layout : NoLayout.INSTANCE;
        doLayout(); // Apply new layout immediately
    }

    /**
     * Adds a child canvas to this composite.
     *
     * @param child the canvas to add
     */
    public void addChild(Canvas child) {
        if (child != null) {
            children.add(child);
            layout.childAdded(this, child);
            doLayout(); // Re-layout after adding child
        }
    }

    /**
     * Removes a child canvas from this composite.
     *
     * @param child the canvas to remove
     * @return true if the child was removed, false otherwise
     */
    public boolean removeChild(Canvas child) {
        boolean removed = children.remove(child);
        if (removed) {
            layout.childRemoved(this, child);
            doLayout(); // Re-layout after removing child
        }
        return removed;
    }

    /**
     * Removes all child canvases from this composite.
     */
    public void removeAllChildren() {
        List<Canvas> oldChildren = new ArrayList<>(children);
        children.clear();

        // Notify layout of all removed children
        for (Canvas child : oldChildren) {
            layout.childRemoved(this, child);
        }
        doLayout(); // Re-layout after clearing all children
    }

    /**
     * Triggers a layout recalculation.
     * Call this method when the container size changes or manual re-layout is needed.
     */
    public void doLayout() {
        layout.layoutChildren(this);
    }

    /**
     * Gets the preferred size for this container based on its layout and children.
     *
     * @return the preferred size as calculated by the layout manager
     */
    public Layout.Dimension getPreferredSize() {
        return layout.getPreferredSize(this);
    }

    /**
     * Gets the number of child canvases.
     *
     * @return the number of children
     */
    public int getChildCount() {
        return children.size();
    }

    /**
     * Overridden to trigger layout when size changes.
     */
    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        doLayout();
    }

    /**
     * Overridden to trigger layout when size changes.
     */
    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        doLayout();
    }

    /**
     * Paints this composite canvas by rendering all visible child canvases
     * in the order they were added.
     *
     * @param graphics the graphics context to draw on
     */
    @Override
    public void paint(Graphics graphics) {
        for (Canvas child : children) {
            if (child.isVisible()) {
                child.paint(graphics);
            }
        }
    }

    /**
     * Adds a child canvas to this composite with a layout constraint.
     *
     * @param child the canvas to add
     * @param constraint the layout constraint for positioning hints
     */
    public void addChild(Canvas child, LayoutConstraint constraint) {
        if (child != null) {
            child.setLayoutConstraint(constraint);
            children.add(child);
            layout.childAdded(this, child);
            doLayout(); // Re-layout after adding child
        }
    }

    /**
     * Sets the layout constraint for an existing child canvas.
     *
     * @param child the child canvas
     * @param constraint the new layout constraint
     */
    public void setChildConstraint(Canvas child, LayoutConstraint constraint) {
        if (child != null && children.contains(child)) {
            child.setLayoutConstraint(constraint);
            doLayout(); // Re-layout with new constraint
        }
    }

    /**
     * Gets the layout constraint for a child canvas.
     *
     * @param child the child canvas
     * @return the layout constraint or null if not set
     */
    public LayoutConstraint getChildConstraint(Canvas child) {
        return child != null ? child.getLayoutConstraint() : null;
    }

    /**
     * Recalculates the minimum size requirements based on child canvases.
     * This method calls pack() on all children first, then calculates the
     * minimum size needed to contain all visible children.
     */
    @Override
    public void pack() {
        // First, pack all children
        for (Canvas child : children) {
            if (child.isVisible()) {
                child.pack();
            }
        }

        // Calculate minimum size based on children
        int requiredWidth = 0;
        int requiredHeight = 0;

        for (Canvas child : children) {
            if (!child.isVisible()) continue;

            // Calculate the space needed for this child
            int childRight = child.getX() + Math.max(child.getWidth(), child.getMinWidth());
            int childBottom = child.getY() + Math.max(child.getHeight(), child.getMinHeight());

            requiredWidth = Math.max(requiredWidth, childRight);
            requiredHeight = Math.max(requiredHeight, childBottom);
        }

        // Update minimum size constraints
        setMinWidth(requiredWidth);
        setMinHeight(requiredHeight);
    }
}
