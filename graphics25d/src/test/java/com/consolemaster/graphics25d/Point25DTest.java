package com.consolemaster.graphics25d;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Point25D class.
 */
class Point25DTest {

    @Test
    void testConstructorAndGetters() {
        Point25D point = new Point25D(1.0, 2.0, 3.0);
        assertEquals(1.0, point.getX());
        assertEquals(2.0, point.getY());
        assertEquals(3.0, point.getZ());
    }

    @Test
    void testDefaultConstructor() {
        Point25D point = new Point25D();
        assertEquals(0.0, point.getX());
        assertEquals(0.0, point.getY());
        assertEquals(0.0, point.getZ());
    }

    @Test
    void testCopy() {
        Point25D original = new Point25D(1.0, 2.0, 3.0);
        Point25D copy = original.copy();

        assertEquals(original.getX(), copy.getX());
        assertEquals(original.getY(), copy.getY());
        assertEquals(original.getZ(), copy.getZ());

        // Ensure it's a different object
        assertNotSame(original, copy);
    }

    @Test
    void testAdd() {
        Point25D point1 = new Point25D(1.0, 2.0, 3.0);
        Point25D point2 = new Point25D(4.0, 5.0, 6.0);
        Point25D result = point1.add(point2);

        assertEquals(5.0, result.getX());
        assertEquals(7.0, result.getY());
        assertEquals(9.0, result.getZ());
    }

    @Test
    void testSubtract() {
        Point25D point1 = new Point25D(4.0, 5.0, 6.0);
        Point25D point2 = new Point25D(1.0, 2.0, 3.0);
        Point25D result = point1.subtract(point2);

        assertEquals(3.0, result.getX());
        assertEquals(3.0, result.getY());
        assertEquals(3.0, result.getZ());
    }

    @Test
    void testDistanceTo() {
        Point25D point1 = new Point25D(0.0, 0.0, 0.0);
        Point25D point2 = new Point25D(3.0, 4.0, 0.0);

        double distance = point1.distanceTo(point2);
        assertEquals(5.0, distance, 0.001); // 3-4-5 triangle
    }
}
