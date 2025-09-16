package com.consolemaster.graphics25d;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Camera for 2.5D graphics with position and viewing direction.
 * The camera supports 6 different viewing directions: front, right, back, left, top, bottom.
 * The camera is positioned away from the projection plane to allow objects around it to be visible.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Camera25D {

    /**
     * Viewing directions for the 2.5D camera.
     */
    public enum Direction {
        FRONT(0),   // Looking towards positive Z
        RIGHT(1),   // Looking towards positive X
        BACK(2),    // Looking towards negative Z
        LEFT(3),    // Looking towards negative X
        TOP(4),     // Looking down (negative Y)
        BOTTOM(5);  // Looking up (positive Y)

        private final int value;

        Direction(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Direction fromValue(int value) {
            for (Direction dir : values()) {
                if (dir.value == value) {
                    return dir;
                }
            }
            return FRONT;
        }
    }

    private Point25D position = new Point25D(0, 0, 0);
    private Direction direction = Direction.FRONT;
    private double distance = 10.0; // Distance from camera to projection plane

    /**
     * Projects a 3D point to 2D screen coordinates based on camera position and direction.
     * The projection uses isometric-style transformation for 2.5D effect.
     */
    public Point2D project(Point25D worldPoint) {
        // Translate point relative to camera position
        Point25D relative = worldPoint.subtract(position);

        // Apply rotation based on camera direction
        Point25D rotated = applyDirectionRotation(relative);

        // Apply isometric projection (2.5D style)
        // X coordinate: combine x and z with 45-degree angle effect
        // Y coordinate: combine y with z depth effect
        double screenX = rotated.getX() + (rotated.getZ() * 0.5);
        double screenY = rotated.getY() - (rotated.getZ() * 0.5);

        return new Point2D(screenX, screenY);
    }

    /**
     * Applies rotation transformation based on camera viewing direction.
     */
    private Point25D applyDirectionRotation(Point25D point) {
        double x = point.getX();
        double y = point.getY();
        double z = point.getZ();

        switch (direction) {
            case FRONT:
                return new Point25D(x, y, z);
            case RIGHT:
                return new Point25D(-z, y, x);
            case BACK:
                return new Point25D(-x, y, -z);
            case LEFT:
                return new Point25D(z, y, -x);
            case TOP:
                return new Point25D(x, -z, y);
            case BOTTOM:
                return new Point25D(x, z, -y);
            default:
                return point;
        }
    }

    /**
     * Moves the camera in the specified direction relative to its current orientation.
     */
    public void move(double deltaX, double deltaY, double deltaZ) {
        Point25D delta = new Point25D(deltaX, deltaY, deltaZ);
        // Apply inverse rotation to move in camera-relative coordinates
        Point25D worldDelta = applyInverseDirectionRotation(delta);
        position = position.add(worldDelta);
    }

    /**
     * Applies inverse rotation for camera movement.
     */
    private Point25D applyInverseDirectionRotation(Point25D point) {
        double x = point.getX();
        double y = point.getY();
        double z = point.getZ();

        switch (direction) {
            case FRONT:
                return new Point25D(x, y, z);
            case RIGHT:
                return new Point25D(z, y, -x);
            case BACK:
                return new Point25D(-x, y, -z);
            case LEFT:
                return new Point25D(-z, y, x);
            case TOP:
                return new Point25D(x, -z, y);
            case BOTTOM:
                return new Point25D(x, z, -y);
            default:
                return point;
        }
    }

    /**
     * Rotates the camera to the next direction clockwise.
     */
    public void rotateClockwise() {
        int nextValue = (direction.getValue() + 1) % 6;
        direction = Direction.fromValue(nextValue);
    }

    /**
     * Rotates the camera to the previous direction counter-clockwise.
     */
    public void rotateCounterClockwise() {
        int prevValue = (direction.getValue() - 1 + 6) % 6;
        direction = Direction.fromValue(prevValue);
    }

    @Override
    public String toString() {
        return String.format("Camera25D[pos=%s, dir=%s, dist=%.1f]",
                           position, direction, distance);
    }
}
