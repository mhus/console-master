package com.consolemaster.graphics25d;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a point in 2.5D space with x, y, z coordinates.
 * Used for positioning objects in the 2.5D graphics system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Point25D {
    private double x;
    private double y;
    private double z;

    /**
     * Creates a copy of this point.
     */
    public Point25D copy() {
        return new Point25D(x, y, z);
    }

    /**
     * Adds another point to this point and returns a new point.
     */
    public Point25D add(Point25D other) {
        return new Point25D(x + other.x, y + other.y, z + other.z);
    }

    /**
     * Subtracts another point from this point and returns a new point.
     */
    public Point25D subtract(Point25D other) {
        return new Point25D(x - other.x, y - other.y, z - other.z);
    }

    /**
     * Multiplies this point by a scalar and returns a new point.
     */
    public Point25D multiply(double scalar) {
        return new Point25D(x * scalar, y * scalar, z * scalar);
    }

    /**
     * Calculates the distance to another point.
     */
    public double distanceTo(Point25D other) {
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", x, y, z);
    }
}
