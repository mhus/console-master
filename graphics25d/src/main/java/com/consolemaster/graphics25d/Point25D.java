package com.consolemaster.graphics25d;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a 3D point with x, y, and z coordinates for 2.5D graphics.
 * Used for positioning objects and camera in 2.5D space.
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
     *
     * @return a new Point25D with the same coordinates
     */
    public Point25D copy() {
        return new Point25D(x, y, z);
    }

    /**
     * Adds another point to this point.
     *
     * @param other the point to add
     * @return a new Point25D with the sum of coordinates
     */
    public Point25D add(Point25D other) {
        return new Point25D(x + other.x, y + other.y, z + other.z);
    }

    /**
     * Subtracts another point from this point.
     *
     * @param other the point to subtract
     * @return a new Point25D with the difference of coordinates
     */
    public Point25D subtract(Point25D other) {
        return new Point25D(x - other.x, y - other.y, z - other.z);
    }

    /**
     * Calculates the distance to another point.
     *
     * @param other the other point
     * @return the Euclidean distance
     */
    public double distanceTo(Point25D other) {
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
