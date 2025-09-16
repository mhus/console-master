package com.consolemaster;

/**
 * A layout manager that arranges children in a single row or column,
 * stretching them to fill the available space while respecting their
 * minimum and maximum size constraints.
 *
 * Similar to FlowLayout but with stretching behavior and single-line arrangement.
 */
public class BoxLayout implements Layout {

    /**
     * Direction for the BoxLayout arrangement.
     */
    public enum Direction {
        HORIZONTAL,  // Arrange children in a horizontal row
        VERTICAL     // Arrange children in a vertical column
    }

    private final Direction direction;
    private final int gap; // Gap between components

    /**
     * Creates a BoxLayout with horizontal direction and default gap of 1.
     */
    public BoxLayout() {
        this(Direction.HORIZONTAL, 1);
    }

    /**
     * Creates a BoxLayout with the specified direction and default gap of 1.
     *
     * @param direction the direction to arrange children
     */
    public BoxLayout(Direction direction) {
        this(direction, 1);
    }

    /**
     * Creates a BoxLayout with horizontal direction and specified gap.
     *
     * @param gap the gap between components
     */
    public BoxLayout(int gap) {
        this(Direction.HORIZONTAL, gap);
    }

    /**
     * Creates a BoxLayout with specified direction and gap.
     *
     * @param direction the direction to arrange children
     * @param gap the gap between components
     */
    public BoxLayout(Direction direction, int gap) {
        this.direction = direction;
        this.gap = Math.max(0, gap);
    }

    @Override
    public void layoutChildren(Composite container) {
        var visibleChildren = container.getChildren().stream()
                .filter(Canvas::isVisible)
                .toList();

        if (visibleChildren.isEmpty()) {
            return;
        }

        if (direction == Direction.HORIZONTAL) {
            layoutHorizontal(container, visibleChildren);
        } else {
            layoutVertical(container, visibleChildren);
        }
    }

    private void layoutHorizontal(Composite container, java.util.List<Canvas> children) {
        int containerWidth = container.getWidth();
        int containerHeight = container.getHeight();
        int totalGaps = (children.size() - 1) * gap;
        int availableWidth = containerWidth - totalGaps;

        // Calculate minimum and preferred widths
        int totalMinWidth = 0;
        int totalPreferredWidth = 0;

        for (Canvas child : children) {
            totalMinWidth += child.getMinWidth();
            totalPreferredWidth += child.getWidth();
        }

        // Distribute width
        int[] childWidths = new int[children.size()];
        if (totalMinWidth > availableWidth) {
            // Not enough space, use minimum widths proportionally
            distributeProportionally(children, childWidths, availableWidth, true);
        } else if (totalPreferredWidth <= availableWidth) {
            // Enough space, stretch to fill
            distributeWithStretching(children, childWidths, availableWidth, true);
        } else {
            // Use preferred widths but scale down proportionally
            distributeProportionally(children, childWidths, availableWidth, false);
        }

        // Position children
        int currentX = container.getX();
        for (int i = 0; i < children.size(); i++) {
            Canvas child = children.get(i);
            child.setX(currentX);
            child.setY(container.getY());
            child.setWidth(childWidths[i]);
            child.setHeight(containerHeight);
            currentX += childWidths[i] + gap;
        }
    }

    private void layoutVertical(Composite container, java.util.List<Canvas> children) {
        int containerWidth = container.getWidth();
        int containerHeight = container.getHeight();
        int totalGaps = (children.size() - 1) * gap;
        int availableHeight = containerHeight - totalGaps;

        // Calculate minimum and preferred heights
        int totalMinHeight = 0;
        int totalPreferredHeight = 0;

        for (Canvas child : children) {
            totalMinHeight += child.getMinHeight();
            totalPreferredHeight += child.getHeight();
        }

        // Distribute height
        int[] childHeights = new int[children.size()];
        if (totalMinHeight > availableHeight) {
            // Not enough space, use minimum heights proportionally
            distributeProportionally(children, childHeights, availableHeight, true);
        } else if (totalPreferredHeight <= availableHeight) {
            // Enough space, stretch to fill
            distributeWithStretching(children, childHeights, availableHeight, false);
        } else {
            // Use preferred heights but scale down proportionally
            distributeProportionally(children, childHeights, availableHeight, false);
        }

        // Position children
        int currentY = container.getY();
        for (int i = 0; i < children.size(); i++) {
            Canvas child = children.get(i);
            child.setX(container.getX());
            child.setY(currentY);
            child.setWidth(containerWidth);
            child.setHeight(childHeights[i]);
            currentY += childHeights[i] + gap;
        }
    }

    private void distributeProportionally(java.util.List<Canvas> children, int[] sizes,
                                        int availableSpace, boolean useMinimum) {
        int totalOriginalSize = 0;
        for (Canvas child : children) {
            totalOriginalSize += useMinimum ? child.getMinWidth() :
                                (direction == Direction.HORIZONTAL ? child.getWidth() : child.getHeight());
        }

        int remainingSpace = availableSpace;
        for (int i = 0; i < children.size(); i++) {
            Canvas child = children.get(i);
            int originalSize = useMinimum ?
                (direction == Direction.HORIZONTAL ? child.getMinWidth() : child.getMinHeight()) :
                (direction == Direction.HORIZONTAL ? child.getWidth() : child.getHeight());

            if (i == children.size() - 1) {
                // Last child gets remaining space
                sizes[i] = remainingSpace;
            } else {
                sizes[i] = (int) Math.round((double) originalSize / totalOriginalSize * availableSpace);
                remainingSpace -= sizes[i];
            }

            // Respect constraints
            if (direction == Direction.HORIZONTAL) {
                sizes[i] = Math.max(child.getMinWidth(), Math.min(child.getMaxWidth(), sizes[i]));
            } else {
                sizes[i] = Math.max(child.getMinHeight(), Math.min(child.getMaxHeight(), sizes[i]));
            }
        }
    }

    private void distributeWithStretching(java.util.List<Canvas> children, int[] sizes,
                                        int availableSpace, boolean isHorizontal) {
        // First, assign minimum sizes
        int usedSpace = 0;
        for (int i = 0; i < children.size(); i++) {
            Canvas child = children.get(i);
            sizes[i] = isHorizontal ? child.getMinWidth() : child.getMinHeight();
            usedSpace += sizes[i];
        }

        // Distribute remaining space
        int remainingSpace = availableSpace - usedSpace;
        int flexibleChildren = children.size();

        while (remainingSpace > 0 && flexibleChildren > 0) {
            int spacePerChild = remainingSpace / flexibleChildren;
            int actuallyDistributed = 0;
            int newFlexibleChildren = flexibleChildren;

            for (int i = 0; i < children.size(); i++) {
                Canvas child = children.get(i);
                int maxSize = isHorizontal ? child.getMaxWidth() : child.getMaxHeight();

                if (sizes[i] < maxSize) {
                    int addition = Math.min(spacePerChild, maxSize - sizes[i]);
                    sizes[i] += addition;
                    actuallyDistributed += addition;

                    if (sizes[i] >= maxSize) {
                        newFlexibleChildren--;
                    }
                }
            }

            remainingSpace -= actuallyDistributed;
            flexibleChildren = newFlexibleChildren;

            // Prevent infinite loop
            if (actuallyDistributed == 0) {
                break;
            }
        }
    }

    @Override
    public Layout.Dimension getPreferredSize(Composite container) {
        var visibleChildren = container.getChildren().stream()
                .filter(Canvas::isVisible)
                .toList();

        if (visibleChildren.isEmpty()) {
            return new Layout.Dimension(0, 0);
        }

        if (direction == Direction.HORIZONTAL) {
            int totalWidth = 0;
            int maxHeight = 0;

            for (int i = 0; i < visibleChildren.size(); i++) {
                Canvas child = visibleChildren.get(i);
                totalWidth += child.getWidth();
                maxHeight = Math.max(maxHeight, child.getHeight());

                if (i > 0) {
                    totalWidth += gap;
                }
            }

            return new Layout.Dimension(totalWidth, maxHeight);
        } else {
            int maxWidth = 0;
            int totalHeight = 0;

            for (int i = 0; i < visibleChildren.size(); i++) {
                Canvas child = visibleChildren.get(i);
                maxWidth = Math.max(maxWidth, child.getWidth());
                totalHeight += child.getHeight();

                if (i > 0) {
                    totalHeight += gap;
                }
            }

            return new Layout.Dimension(maxWidth, totalHeight);
        }
    }

    /**
     * Gets the direction of this BoxLayout.
     *
     * @return the direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Gets the gap between components.
     *
     * @return the gap in characters
     */
    public int getGap() {
        return gap;
    }

    @Override
    public String toString() {
        return String.format("BoxLayout[direction=%s, gap=%d]", direction, gap);
    }
}
