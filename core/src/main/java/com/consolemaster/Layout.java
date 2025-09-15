package com.consolemaster;

/**
 * Interface for layout managers that can arrange child canvases within a composite canvas.
 * Layout managers are responsible for positioning and sizing child components based on
 * specific layout strategies.
 */
public interface Layout {

    /**
     * Arranges the child canvases within the given composite canvas.
     * This method is called whenever the layout needs to be recalculated,
     * such as when children are added/removed or the container is resized.
     *
     * @param container the composite canvas containing the children to layout
     */
    void layoutChildren(Composite container);

    /**
     * Calculates the preferred size for the container based on its children.
     * This is used for auto-sizing containers.
     *
     * @param container the composite canvas to calculate size for
     * @return the preferred size as a Dimension (width, height)
     */
    default Dimension getPreferredSize(Composite container) {
        // Default implementation: use current size
        return new Dimension(container.getWidth(), container.getHeight());
    }

    /**
     * Called when a child is added to the container.
     * Allows the layout to perform any necessary setup or immediate positioning.
     *
     * @param container the composite canvas
     * @param child the child that was added
     */
    default void childAdded(Composite container, Canvas child) {
        // Default: do nothing, rely on layoutChildren() being called
    }

    /**
     * Called when a child is removed from the container.
     * Allows the layout to perform any necessary cleanup.
     *
     * @param container the composite canvas
     * @param child the child that was removed
     */
    default void childRemoved(Composite container, Canvas child) {
        // Default: do nothing, rely on layoutChildren() being called
    }

    /**
     * Simple data class to represent dimensions.
     */
    record Dimension(int width, int height) {
        public Dimension {
            if (width < 0 || height < 0) {
                throw new IllegalArgumentException("Dimensions cannot be negative");
            }
        }
    }
}
