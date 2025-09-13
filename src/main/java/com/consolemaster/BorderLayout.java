package com.consolemaster;

/**
 * A border layout that arranges components in five regions: NORTH, SOUTH, EAST, WEST, and CENTER.
 * Uses PositionConstraint to determine component placement.
 */
public class BorderLayout implements Layout {

    private final int gap;

    /**
     * Creates a BorderLayout with no gaps.
     */
    public BorderLayout() {
        this(0);
    }

    /**
     * Creates a BorderLayout with the specified gap between components.
     *
     * @param gap the gap between components
     */
    public BorderLayout(int gap) {
        this.gap = Math.max(0, gap);
    }

    @Override
    public void layoutChildren(CompositeCanvas container) {
        Canvas north = null, south = null, east = null, west = null, center = null;

        // Categorize children by their position constraints
        for (Canvas child : container.getChildren()) {
            if (!child.isVisible()) continue;

            LayoutConstraint constraint = child.getLayoutConstraint();
            if (constraint instanceof PositionConstraint posConstraint) {
                switch (posConstraint.getPosition()) {
                    case TOP_CENTER -> north = child;
                    case BOTTOM_CENTER -> south = child;
                    case CENTER_LEFT -> west = child;
                    case CENTER_RIGHT -> east = child;
                    case CENTER -> center = child;
                }
            } else if (center == null) {
                // Default unconstrained children to center
                center = child;
            }
        }

        // Calculate available space
        int containerX = container.getX();
        int containerY = container.getY();
        int containerWidth = container.getWidth();
        int containerHeight = container.getHeight();

        int currentY = containerY;
        int currentHeight = containerHeight;

        // Layout NORTH
        if (north != null) {
            north.setX(containerX);
            north.setY(currentY);
            north.setWidth(containerWidth);
            currentY += north.getHeight() + gap;
            currentHeight -= north.getHeight() + gap;
        }

        // Layout SOUTH
        if (south != null) {
            south.setX(containerX);
            south.setY(containerY + containerHeight - south.getHeight());
            south.setWidth(containerWidth);
            currentHeight -= south.getHeight() + gap;
        }

        // Calculate remaining horizontal space
        int currentX = containerX;
        int currentWidth = containerWidth;

        // Layout WEST
        if (west != null) {
            west.setX(currentX);
            west.setY(currentY);
            west.setHeight(currentHeight);
            currentX += west.getWidth() + gap;
            currentWidth -= west.getWidth() + gap;
        }

        // Layout EAST
        if (east != null) {
            east.setX(containerX + containerWidth - east.getWidth());
            east.setY(currentY);
            east.setHeight(currentHeight);
            currentWidth -= east.getWidth() + gap;
        }

        // Layout CENTER
        if (center != null) {
            center.setX(currentX);
            center.setY(currentY);
            center.setWidth(Math.max(1, currentWidth));
            center.setHeight(Math.max(1, currentHeight));
        }
    }

    @Override
    public Dimension getPreferredSize(CompositeCanvas container) {
        int totalWidth = 0;
        int totalHeight = 0;
        int northHeight = 0, southHeight = 0;
        int eastWidth = 0, westWidth = 0;
        int centerWidth = 0, centerHeight = 0;

        for (Canvas child : container.getChildren()) {
            if (!child.isVisible()) continue;

            LayoutConstraint constraint = child.getLayoutConstraint();
            if (constraint instanceof PositionConstraint posConstraint) {
                switch (posConstraint.getPosition()) {
                    case TOP_CENTER -> northHeight = child.getHeight();
                    case BOTTOM_CENTER -> southHeight = child.getHeight();
                    case CENTER_LEFT -> westWidth = child.getWidth();
                    case CENTER_RIGHT -> eastWidth = child.getWidth();
                    case CENTER -> {
                        centerWidth = child.getWidth();
                        centerHeight = child.getHeight();
                    }
                }
            }
        }

        totalWidth = Math.max(centerWidth + westWidth + eastWidth + (gap * 2), 0);
        totalHeight = Math.max(centerHeight + northHeight + southHeight + (gap * 2), 0);

        return new Dimension(totalWidth, totalHeight);
    }

    @Override
    public String toString() {
        return String.format("BorderLayout[gap=%d]", gap);
    }
}
