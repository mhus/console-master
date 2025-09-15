package com.consolemaster;

/**
 * Layout constraints for positioning canvases at specific positions within a container.
 * This constraint specifies absolute or relative positioning hints.
 */
public class PositionConstraint implements LayoutConstraint {

    public enum Position {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT,
        CENTER_LEFT, CENTER, CENTER_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT,
        ABSOLUTE,  // Use absolute x, y coordinates
        // BorderLayout compatibility aliases
        NORTH, SOUTH, EAST, WEST
    }

    private final Position position;
    private final int offsetX;
    private final int offsetY;

    /**
     * Creates a position constraint with a predefined position.
     *
     * @param position the position within the container
     */
    public PositionConstraint(Position position) {
        this(position, 0, 0);
    }

    /**
     * Creates a position constraint with a position and offset.
     *
     * @param position the position within the container
     * @param offsetX  horizontal offset from the position
     * @param offsetY  vertical offset from the position
     */
    public PositionConstraint(Position position, int offsetX, int offsetY) {
        this.position = position;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /**
     * Creates an absolute position constraint.
     *
     * @param x the absolute x coordinate
     * @param y the absolute y coordinate
     * @return a new PositionConstraint for absolute positioning
     */
    public static PositionConstraint absolute(int x, int y) {
        return new PositionConstraint(Position.ABSOLUTE, x, y);
    }

    public Position getPosition() {
        return position;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    @Override
    public String toString() {
        if (position == Position.ABSOLUTE) {
            return String.format("PositionConstraint[ABSOLUTE(%d, %d)]", offsetX, offsetY);
        } else if (offsetX == 0 && offsetY == 0) {
            return String.format("PositionConstraint[%s]", position);
        } else {
            return String.format("PositionConstraint[%s + (%d, %d)]", position, offsetX, offsetY);
        }
    }
}
