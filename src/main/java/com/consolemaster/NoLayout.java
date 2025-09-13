package com.consolemaster;

/**
 * A layout implementation that performs no automatic layout.
 * Children maintain their manually set positions and sizes.
 * This is the default layout for CompositeCanvas.
 */
public class NoLayout implements Layout {

    /**
     * Singleton instance of NoLayout.
     */
    public static final NoLayout INSTANCE = new NoLayout();

    /**
     * Private constructor to enforce singleton pattern.
     */
    private NoLayout() {
    }

    /**
     * Does nothing - children keep their current positions and sizes.
     *
     * @param container the composite canvas (not modified)
     */
    @Override
    public void layoutChildren(CompositeCanvas container) {
        // No layout - children maintain their current positions and sizes
    }

    /**
     * Returns the current size of the container.
     *
     * @param container the composite canvas
     * @return the current dimensions of the container
     */
    @Override
    public Dimension getPreferredSize(CompositeCanvas container) {
        return new Dimension(container.getWidth(), container.getHeight());
    }

    @Override
    public String toString() {
        return "NoLayout";
    }
}
