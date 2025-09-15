package com.consolemaster;

/**
 * Interface for layout constraints that provide positioning and sizing hints
 * to layout managers. Layout constraints are associated with Canvas objects
 * to influence how they are positioned and sized within their parent container.
 */
public interface LayoutConstraint {

    /**
     * Gets a string representation of this constraint for debugging purposes.
     *
     * @return a human-readable description of the constraint
     */
    @Override
    String toString();

    /**
     * Checks if this constraint is compatible with the given layout.
     * This allows layouts to validate that they can handle specific constraint types.
     *
     * @param layout the layout to check compatibility with
     * @return true if the constraint is compatible with the layout
     */
    default boolean isCompatibleWith(Layout layout) {
        return true; // Default: all constraints are compatible with all layouts
    }
}
