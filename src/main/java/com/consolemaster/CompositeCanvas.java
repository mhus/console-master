package com.consolemaster;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * A composite canvas that can contain multiple child canvases.
 * Child canvases are rendered in the order they were added.
 */
@Getter
public class CompositeCanvas extends Canvas {

    private final List<Canvas> children = new ArrayList<>();

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
     * Adds a child canvas to this composite.
     *
     * @param child the canvas to add
     */
    public void addChild(Canvas child) {
        if (child != null) {
            children.add(child);
        }
    }

    /**
     * Removes a child canvas from this composite.
     *
     * @param child the canvas to remove
     * @return true if the child was removed, false otherwise
     */
    public boolean removeChild(Canvas child) {
        return children.remove(child);
    }

    /**
     * Removes all child canvases from this composite.
     */
    public void removeAllChildren() {
        children.clear();
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
}
