package com.consolemaster.graphic3d;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Represents a ray in 3D space for raytracing operations.
 * A ray is defined by an origin point and a direction vector.
 */
@Getter
@RequiredArgsConstructor
public class Ray3D {

    private static final MathContext MATH_CONTEXT = new MathContext(34, RoundingMode.HALF_UP);

    private final Point3D origin;
    private final Point3D direction; // Should be normalized

    /**
     * Creates a ray with normalized direction.
     */
    public static Ray3D create(Point3D origin, Point3D direction) {
        Point3D normalizedDirection = direction.normalize();
        return new Ray3D(origin, normalizedDirection);
    }

    /**
     * Gets a point along the ray at the given distance t.
     * Point = origin + t * direction
     */
    public Point3D getPointAt(BigDecimal t) {
        Point3D scaledDirection = direction.multiply(t);
        return origin.add(scaledDirection);
    }

    /**
     * Gets a point along the ray at the given distance t (double version for convenience).
     */
    public Point3D getPointAt(double t) {
        return getPointAt(BigDecimal.valueOf(t));
    }

    /**
     * Tests intersection with a triangle defined by three vertices.
     * Uses the Möller-Trumbore intersection algorithm.
     *
     * @param v0 First vertex of triangle
     * @param v1 Second vertex of triangle
     * @param v2 Third vertex of triangle
     * @return RayHit object if intersection exists, null otherwise
     */
    public RayHit intersectTriangle(Point3D v0, Point3D v1, Point3D v2) {
        // Möller-Trumbore intersection algorithm
        Point3D edge1 = v1.subtract(v0);
        Point3D edge2 = v2.subtract(v0);

        Point3D h = direction.cross(edge2);
        BigDecimal a = edge1.dot(h);

        // Check if ray is parallel to triangle
        BigDecimal epsilon = BigDecimal.valueOf(0.0000001);
        if (a.abs().compareTo(epsilon) < 0) {
            return null; // Ray is parallel to triangle
        }

        BigDecimal f = BigDecimal.ONE.divide(a, MATH_CONTEXT);
        Point3D s = origin.subtract(v0);
        BigDecimal u = f.multiply(s.dot(h), MATH_CONTEXT);

        if (u.compareTo(BigDecimal.ZERO) < 0 || u.compareTo(BigDecimal.ONE) > 0) {
            return null;
        }

        Point3D q = s.cross(edge1);
        BigDecimal v = f.multiply(direction.dot(q), MATH_CONTEXT);

        if (v.compareTo(BigDecimal.ZERO) < 0 || u.add(v).compareTo(BigDecimal.ONE) > 0) {
            return null;
        }

        BigDecimal t = f.multiply(edge2.dot(q), MATH_CONTEXT);

        if (t.compareTo(epsilon) > 0) { // Ray intersection
            Point3D hitPoint = getPointAt(t);

            // Calculate normal (cross product of edges)
            Point3D normal = edge1.cross(edge2).normalize();

            return new RayHit(hitPoint, normal, t, u, v);
        }

        return null; // Line intersection but not ray intersection
    }

    @Override
    public String toString() {
        return String.format("Ray3D{origin=%s, direction=%s}", origin, direction);
    }
}
