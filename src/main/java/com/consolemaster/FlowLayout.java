package com.consolemaster;

/**
 * A simple flow layout that arranges children in rows, flowing to the next row
 * when the current row is full. Similar to a word-wrap layout.
 */
public class FlowLayout implements Layout {

    private final int hgap; // Horizontal gap between components
    private final int vgap; // Vertical gap between rows

    /**
     * Creates a FlowLayout with default gaps of 1.
     */
    public FlowLayout() {
        this(1, 1);
    }

    /**
     * Creates a FlowLayout with specified gaps.
     *
     * @param hgap horizontal gap between components
     * @param vgap vertical gap between rows
     */
    public FlowLayout(int hgap, int vgap) {
        this.hgap = Math.max(0, hgap);
        this.vgap = Math.max(0, vgap);
    }

    @Override
    public void layoutChildren(CompositeCanvas container) {
        if (container.getChildren().isEmpty()) {
            return;
        }

        int containerWidth = container.getWidth();
        int currentX = 0;
        int currentY = 0;
        int rowHeight = 0;

        for (Canvas child : container.getChildren()) {
            if (!child.isVisible()) {
                continue;
            }

            int childWidth = child.getWidth();
            int childHeight = child.getHeight();

            // Check if we need to wrap to next row
            if (currentX > 0 && currentX + childWidth > containerWidth) {
                // Move to next row
                currentX = 0;
                currentY += rowHeight + vgap;
                rowHeight = 0;
            }

            // Position the child
            child.setX(container.getX() + currentX);
            child.setY(container.getY() + currentY);

            // Update position for next child
            currentX += childWidth + hgap;
            rowHeight = Math.max(rowHeight, childHeight);
        }
    }

    @Override
    public Dimension getPreferredSize(CompositeCanvas container) {
        if (container.getChildren().isEmpty()) {
            return new Dimension(0, 0);
        }

        int maxWidth = 0;
        int totalHeight = 0;
        int currentRowWidth = 0;
        int currentRowHeight = 0;
        boolean firstInRow = true;

        for (Canvas child : container.getChildren()) {
            if (!child.isVisible()) {
                continue;
            }

            int childWidth = child.getWidth();
            int childHeight = child.getHeight();

            if (!firstInRow) {
                currentRowWidth += hgap;
            }

            currentRowWidth += childWidth;
            currentRowHeight = Math.max(currentRowHeight, childHeight);
            firstInRow = false;

            // If this would overflow, start a new row
            if (currentRowWidth > container.getWidth() && !firstInRow) {
                maxWidth = Math.max(maxWidth, currentRowWidth - childWidth - hgap);
                totalHeight += currentRowHeight + vgap;
                currentRowWidth = childWidth;
                currentRowHeight = childHeight;
                firstInRow = true;
            }
        }

        // Add the last row
        maxWidth = Math.max(maxWidth, currentRowWidth);
        totalHeight += currentRowHeight;

        return new Dimension(maxWidth, totalHeight);
    }

    @Override
    public String toString() {
        return String.format("FlowLayout[hgap=%d, vgap=%d]", hgap, vgap);
    }
}
