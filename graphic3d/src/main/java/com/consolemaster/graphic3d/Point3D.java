package com.consolemaster.graphic3d;

import lombok.Data;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Represents a 3D point with x, y, and z coordinates using BigDecimal for precision.
 */
@Data
public class Point3D {
    private static final MathContext MATH_CONTEXT = new MathContext(34, RoundingMode.HALF_UP);

    private BigDecimal x;
    private BigDecimal y;
    private BigDecimal z;

    // BigDecimal constructor
    public Point3D(BigDecimal x, BigDecimal y, BigDecimal z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Double constructor for compatibility
    public Point3D(double x, double y, double z) {
        this.x = BigDecimal.valueOf(x);
        this.y = BigDecimal.valueOf(y);
        this.z = BigDecimal.valueOf(z);
    }

    public Point3D() {
        this(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    /**
     * Adds another point to this point.
     */
    public Point3D add(Point3D other) {
        return new Point3D(
            x.add(other.x, MATH_CONTEXT),
            y.add(other.y, MATH_CONTEXT),
            z.add(other.z, MATH_CONTEXT)
        );
    }

    /**
     * Subtracts another point from this point.
     */
    public Point3D subtract(Point3D other) {
        return new Point3D(
            x.subtract(other.x, MATH_CONTEXT),
            y.subtract(other.y, MATH_CONTEXT),
            z.subtract(other.z, MATH_CONTEXT)
        );
    }

    /**
     * Multiplies this point by a scalar.
     */
    public Point3D multiply(BigDecimal scalar) {
        return new Point3D(
            x.multiply(scalar, MATH_CONTEXT),
            y.multiply(scalar, MATH_CONTEXT),
            z.multiply(scalar, MATH_CONTEXT)
        );
    }

    /**
     * Multiplies this point by a scalar (double for compatibility).
     */
    public Point3D multiply(double scalar) {
        return multiply(BigDecimal.valueOf(scalar));
    }

    /**
     * Calculates the distance to another point.
     */
    public BigDecimal distanceTo(Point3D other) {
        BigDecimal dx = x.subtract(other.x, MATH_CONTEXT);
        BigDecimal dy = y.subtract(other.y, MATH_CONTEXT);
        BigDecimal dz = z.subtract(other.z, MATH_CONTEXT);

        BigDecimal distanceSquared = dx.multiply(dx, MATH_CONTEXT)
            .add(dy.multiply(dy, MATH_CONTEXT), MATH_CONTEXT)
            .add(dz.multiply(dz, MATH_CONTEXT), MATH_CONTEXT);

        return sqrt(distanceSquared);
    }

    /**
     * Normalizes this point (treating it as a vector).
     */
    public Point3D normalize() {
        BigDecimal lengthSquared = x.multiply(x, MATH_CONTEXT)
            .add(y.multiply(y, MATH_CONTEXT), MATH_CONTEXT)
            .add(z.multiply(z, MATH_CONTEXT), MATH_CONTEXT);

        if (lengthSquared.compareTo(BigDecimal.ZERO) == 0) {
            return new Point3D(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        BigDecimal length = sqrt(lengthSquared);
        return new Point3D(
            x.divide(length, MATH_CONTEXT),
            y.divide(length, MATH_CONTEXT),
            z.divide(length, MATH_CONTEXT)
        );
    }

    /**
     * Helper method to calculate square root of BigDecimal using Newton's method.
     */
    private static BigDecimal sqrt(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal x = value;
        BigDecimal two = BigDecimal.valueOf(2);
        BigDecimal previousX;

        do {
            previousX = x;
            x = x.add(value.divide(x, MATH_CONTEXT), MATH_CONTEXT).divide(two, MATH_CONTEXT);
        } while (x.subtract(previousX, MATH_CONTEXT).abs().compareTo(BigDecimal.valueOf(1e-30)) > 0);

        return x;
    }

    // Convenience methods for double compatibility
    public double getXAsDouble() {
        return x.doubleValue();
    }

    public double getYAsDouble() {
        return y.doubleValue();
    }

    public double getZAsDouble() {
        return z.doubleValue();
    }
}
