package com.consolemaster.graphics25d;

import lombok.Data;

/**
 * Represents a 2.5D camera with position and viewing direction.
 * The camera is used to render the 2.5D scene from a specific viewpoint.
 */
@Data
public class Camera25D {
    private Point25D position;
    private int direction; // 0, 90, 180, 270 degrees

    /**
     * Creates a new camera at the origin looking north (0 degrees).
     */
    public Camera25D() {
        this.position = new Point25D(0, 0, 0);
        this.direction = 0;
    }

    /**
     * Creates a new camera at the specified position and direction.
     *
     * @param position the camera position
     * @param direction the viewing direction (0, 90, 180, 270 degrees)
     */
    public Camera25D(Point25D position, int direction) {
        this.position = position;
        setDirection(direction);
    }

    /**
     * Creates a new camera at the specified coordinates and direction.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @param direction the viewing direction (0, 90, 180, 270 degrees)
     */
    public Camera25D(double x, double y, double z, int direction) {
        this.position = new Point25D(x, y, z);
        setDirection(direction);
    }

    /**
     * Sets the viewing direction, ensuring it's one of the valid values.
     *
     * @param direction the direction in degrees (0, 90, 180, 270)
     * @throws IllegalArgumentException if direction is not valid
     */
    public void setDirection(int direction) {
        if (direction != 0 && direction != 90 && direction != 180 && direction != 270) {
            throw new IllegalArgumentException("Direction must be 0, 90, 180, or 270 degrees");
        }
        this.direction = direction;
    }

    /**
     * Rotates the camera by 90 degrees clockwise.
     */
    public void rotateClockwise() {
        direction = (direction + 90) % 360;
    }

    /**
     * Rotates the camera by 90 degrees counter-clockwise.
     */
    public void rotateCounterClockwise() {
        direction = (direction - 90 + 360) % 360;
    }

    /**
     * Moves the camera forward in the current viewing direction.
     *
     * @param distance the distance to move
     */
    public void moveForward(double distance) {
        switch (direction) {
            case 0:   // North
                position.setY(position.getY() - distance);
                break;
            case 90:  // East
                position.setX(position.getX() + distance);
                break;
            case 180: // South
                position.setY(position.getY() + distance);
                break;
            case 270: // West
                position.setX(position.getX() - distance);
                break;
        }
    }

    /**
     * Moves the camera backward in the current viewing direction.
     *
     * @param distance the distance to move
     */
    public void moveBackward(double distance) {
        moveForward(-distance);
    }

    /**
     * Moves the camera left relative to the current viewing direction.
     *
     * @param distance the distance to move
     */
    public void moveLeft(double distance) {
        switch (direction) {
            case 0:   // North - left is west
                position.setX(position.getX() - distance);
                break;
            case 90:  // East - left is north
                position.setY(position.getY() - distance);
                break;
            case 180: // South - left is east
                position.setX(position.getX() + distance);
                break;
            case 270: // West - left is south
                position.setY(position.getY() + distance);
                break;
        }
    }

    /**
     * Moves the camera right relative to the current viewing direction.
     *
     * @param distance the distance to move
     */
    public void moveRight(double distance) {
        moveLeft(-distance);
    }

    /**
     * Moves the camera up (increases z coordinate).
     *
     * @param distance the distance to move up
     */
    public void moveUp(double distance) {
        position.setZ(position.getZ() + distance);
    }

    /**
     * Moves the camera down (decreases z coordinate).
     *
     * @param distance the distance to move down
     */
    public void moveDown(double distance) {
        position.setZ(position.getZ() - distance);
    }

    /**
     * Gets the direction as a string.
     *
     * @return the direction name (North, East, South, West)
     */
    public String getDirectionName() {
        return switch (direction) {
            case 0 -> "North";
            case 90 -> "East";
            case 180 -> "South";
            case 270 -> "West";
            default -> "Unknown";
        };
    }
}
