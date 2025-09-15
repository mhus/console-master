package com.consolemaster.graphic3d;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a 3D point with x, y, and z coordinates.
 */
@Data
@AllArgsConstructor
public class Point3D {
    private double x;
    private double y;
    private double z;

    public Point3D() {
        this(0.0, 0.0, 0.0);
    }

    /**
     * Adds another point to this point.
     */
    public Point3D add(Point3D other) {
        return new Point3D(x + other.x, y + other.y, z + other.z);
    }

    /**
     * Subtracts another point from this point.
     */
    public Point3D subtract(Point3D other) {
        return new Point3D(x - other.x, y - other.y, z - other.z);
    }

    /**
     * Multiplies this point by a scalar.
     */
    public Point3D multiply(double scalar) {
        return new Point3D(x * scalar, y * scalar, z * scalar);
    }

    /**
     * Calculates the distance to another point.
     */
    public double distanceTo(Point3D other) {
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Normalizes this point (treating it as a vector).
     */
    public Point3D normalize() {
        double length = Math.sqrt(x * x + y * y + z * z);
        if (length == 0) return new Point3D(0, 0, 0);
        return new Point3D(x / length, y / length, z / length);
    }
}
