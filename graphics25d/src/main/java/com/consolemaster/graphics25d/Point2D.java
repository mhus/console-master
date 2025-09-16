package com.consolemaster.graphics25d;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a 2D point for screen coordinates in the 2.5D graphics system.
 * Used as the result of projecting 3D world coordinates to 2D screen space.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Point2D {
    private double x;
    private double y;

    /**
     * Creates a copy of this point.
     */
    public Point2D copy() {
        return new Point2D(x, y);
    }

    /**
     * Adds another point to this point and returns a new point.
     */
    public Point2D add(Point2D other) {
        return new Point2D(x + other.x, y + other.y);
    }

    /**
     * Subtracts another point from this point and returns a new point.
     */
    public Point2D subtract(Point2D other) {
        return new Point2D(x - other.x, y - other.y);
    }

    /**
     * Multiplies this point by a scalar and returns a new point.
     */
    public Point2D multiply(double scalar) {
        return new Point2D(x * scalar, y * scalar);
    }

    /**
     * Calculates the distance to another point.
     */
    public double distanceTo(Point2D other) {
        double dx = x - other.x;
        double dy = y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Converts to integer coordinates for screen rendering.
     */
    public int getIntX() {
        return (int) Math.round(x);
    }

    /**
     * Converts to integer coordinates for screen rendering.
     */
    public int getIntY() {
        return (int) Math.round(y);
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }
}
