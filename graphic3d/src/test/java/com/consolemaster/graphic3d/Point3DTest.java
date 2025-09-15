package com.consolemaster.graphic3d;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Point3D class.
 */
class Point3DTest {

    private Point3D point1;
    private Point3D point2;

    @BeforeEach
    void setUp() {
        point1 = new Point3D(1.0, 2.0, 3.0);
        point2 = new Point3D(4.0, 5.0, 6.0);
    }

    @Test
    void testConstructor() {
        Point3D point = new Point3D(1.0, 2.0, 3.0);
        assertEquals(1.0, point.getX());
        assertEquals(2.0, point.getY());
        assertEquals(3.0, point.getZ());
    }

    @Test
    void testDefaultConstructor() {
        Point3D point = new Point3D();
        assertEquals(0.0, point.getX());
        assertEquals(0.0, point.getY());
        assertEquals(0.0, point.getZ());
    }

    @Test
    void testAdd() {
        Point3D result = point1.add(point2);
        assertEquals(5.0, result.getX());
        assertEquals(7.0, result.getY());
        assertEquals(9.0, result.getZ());
    }

    @Test
    void testSubtract() {
        Point3D result = point2.subtract(point1);
        assertEquals(3.0, result.getX());
        assertEquals(3.0, result.getY());
        assertEquals(3.0, result.getZ());
    }

    @Test
    void testMultiply() {
        Point3D result = point1.multiply(2.0);
        assertEquals(2.0, result.getX());
        assertEquals(4.0, result.getY());
        assertEquals(6.0, result.getZ());
    }

    @Test
    void testDistanceTo() {
        double distance = point1.distanceTo(point2);
        assertEquals(Math.sqrt(27), distance, 0.001);
    }

    @Test
    void testNormalize() {
        Point3D vector = new Point3D(3.0, 4.0, 0.0);
        Point3D normalized = vector.normalize();
        assertEquals(0.6, normalized.getX(), 0.001);
        assertEquals(0.8, normalized.getY(), 0.001);
        assertEquals(0.0, normalized.getZ(), 0.001);
    }

    @Test
    void testNormalizeZeroVector() {
        Point3D zero = new Point3D(0.0, 0.0, 0.0);
        Point3D normalized = zero.normalize();
        assertEquals(0.0, normalized.getX());
        assertEquals(0.0, normalized.getY());
        assertEquals(0.0, normalized.getZ());
    }
}
