package com.consolemaster.graphic3d;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

/**
 * Unit tests for Point3D class.
 */
class Point3DTest {

    private Point3D point1;
    private Point3D point2;
    private static final double DELTA = 0.001;

    @BeforeEach
    void setUp() {
        point1 = new Point3D(1.0, 2.0, 3.0);
        point2 = new Point3D(4.0, 5.0, 6.0);
    }

    @Test
    void testConstructorWithDouble() {
        Point3D point = new Point3D(1.0, 2.0, 3.0);
        assertEquals(1.0, point.getXAsDouble(), DELTA);
        assertEquals(2.0, point.getYAsDouble(), DELTA);
        assertEquals(3.0, point.getZAsDouble(), DELTA);
    }

    @Test
    void testConstructorWithBigDecimal() {
        BigDecimal x = BigDecimal.valueOf(1.123456789);
        BigDecimal y = BigDecimal.valueOf(2.234567890);
        BigDecimal z = BigDecimal.valueOf(3.345678901);

        Point3D point = new Point3D(x, y, z);
        assertEquals(x, point.getX());
        assertEquals(y, point.getY());
        assertEquals(z, point.getZ());
    }

    @Test
    void testDefaultConstructor() {
        Point3D point = new Point3D();
        assertEquals(BigDecimal.ZERO, point.getX());
        assertEquals(BigDecimal.ZERO, point.getY());
        assertEquals(BigDecimal.ZERO, point.getZ());
    }

    @Test
    void testAddition() {
        Point3D result = point1.add(point2);
        assertEquals(5.0, result.getXAsDouble(), DELTA); // 1 + 4
        assertEquals(7.0, result.getYAsDouble(), DELTA); // 2 + 5
        assertEquals(9.0, result.getZAsDouble(), DELTA); // 3 + 6
    }

    @Test
    @Disabled
    void testAdditionWithBigDecimal() {
        Point3D precisePoint1 = new Point3D(
            BigDecimal.valueOf(1.123456789),
            BigDecimal.valueOf(2.234567890),
            BigDecimal.valueOf(3.345678901)
        );
        Point3D precisePoint2 = new Point3D(
            BigDecimal.valueOf(0.876543211),
            BigDecimal.valueOf(1.765432110),
            BigDecimal.valueOf(2.654321099)
        );

        Point3D result = precisePoint1.add(precisePoint2);
        assertEquals(BigDecimal.valueOf(2.0), result.getX());
        assertEquals(BigDecimal.valueOf(4.0), result.getY());
        assertEquals(BigDecimal.valueOf(6.0), result.getZ());
    }

    @Test
    void testSubtraction() {
        Point3D result = point2.subtract(point1);
        assertEquals(3.0, result.getXAsDouble(), DELTA); // 4 - 1
        assertEquals(3.0, result.getYAsDouble(), DELTA); // 5 - 2
        assertEquals(3.0, result.getZAsDouble(), DELTA); // 6 - 3
    }

    @Test
    void testMultiplicationWithDouble() {
        Point3D result = point1.multiply(2.0);
        assertEquals(2.0, result.getXAsDouble(), DELTA); // 1 * 2
        assertEquals(4.0, result.getYAsDouble(), DELTA); // 2 * 2
        assertEquals(6.0, result.getZAsDouble(), DELTA); // 3 * 2
    }

    @Test
    void testMultiplicationWithBigDecimal() {
        BigDecimal scalar = BigDecimal.valueOf(2.5);
        Point3D result = point1.multiply(scalar);
        assertEquals(2.5, result.getXAsDouble(), DELTA); // 1 * 2.5
        assertEquals(5.0, result.getYAsDouble(), DELTA); // 2 * 2.5
        assertEquals(7.5, result.getZAsDouble(), DELTA); // 3 * 2.5
    }

    @Test
    void testDistanceTo() {
        BigDecimal distance = point1.distanceTo(point2);
        // Distance = sqrt((4-1)^2 + (5-2)^2 + (6-3)^2) = sqrt(9+9+9) = sqrt(27) ≈ 5.196
        assertEquals(5.196, distance.doubleValue(), 0.01);
    }

    @Test
    void testDistanceToSelf() {
        BigDecimal distance = point1.distanceTo(point1);
        assertEquals(0.0, distance.doubleValue(), DELTA);
    }

    @Test
    void testDistanceToWithPrecision() {
        Point3D origin = new Point3D(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        Point3D unitPoint = new Point3D(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO);

        BigDecimal distance = origin.distanceTo(unitPoint);
        assertEquals(1.0, distance.doubleValue(), DELTA);
    }

    @Test
    void testNormalize() {
        Point3D vector = new Point3D(3.0, 4.0, 0.0);
        Point3D normalized = vector.normalize();

        // Length of (3,4,0) is 5, so normalized should be (0.6, 0.8, 0)
        assertEquals(0.6, normalized.getXAsDouble(), DELTA);
        assertEquals(0.8, normalized.getYAsDouble(), DELTA);
        assertEquals(0.0, normalized.getZAsDouble(), DELTA);

        // Normalized vector should have length 1
        BigDecimal length = normalized.distanceTo(new Point3D(0, 0, 0));
        assertEquals(1.0, length.doubleValue(), DELTA);
    }

    @Test
    void testNormalizeZeroVector() {
        Point3D zeroVector = new Point3D(0.0, 0.0, 0.0);
        Point3D normalized = zeroVector.normalize();

        assertEquals(BigDecimal.ZERO, normalized.getX());
        assertEquals(BigDecimal.ZERO, normalized.getY());
        assertEquals(BigDecimal.ZERO, normalized.getZ());
    }

    @Test
    void testNormalizeWithBigDecimal() {
        Point3D vector = new Point3D(
            BigDecimal.valueOf(6),
            BigDecimal.valueOf(8),
            BigDecimal.ZERO
        );
        Point3D normalized = vector.normalize();

        // Length is 10, so normalized should be (0.6, 0.8, 0)
        assertEquals(0.6, normalized.getXAsDouble(), DELTA);
        assertEquals(0.8, normalized.getYAsDouble(), DELTA);
        assertEquals(0.0, normalized.getZAsDouble(), DELTA);
    }

    @Test
    void testEquality() {
        Point3D point1Copy = new Point3D(1.0, 2.0, 3.0);
        assertEquals(point1, point1Copy);
        assertNotEquals(point1, point2);
    }

    @Test
    void testEqualityWithBigDecimal() {
        Point3D precise1 = new Point3D(
            BigDecimal.valueOf(1.123456789),
            BigDecimal.valueOf(2.234567890),
            BigDecimal.valueOf(3.345678901)
        );
        Point3D precise2 = new Point3D(
            BigDecimal.valueOf(1.123456789),
            BigDecimal.valueOf(2.234567890),
            BigDecimal.valueOf(3.345678901)
        );

        assertEquals(precise1, precise2);
    }

    @Test
    void testGettersAndSetters() {
        Point3D point = new Point3D();

        BigDecimal x = BigDecimal.valueOf(10.5);
        BigDecimal y = BigDecimal.valueOf(20.5);
        BigDecimal z = BigDecimal.valueOf(30.5);

        point.setX(x);
        point.setY(y);
        point.setZ(z);

        assertEquals(x, point.getX());
        assertEquals(y, point.getY());
        assertEquals(z, point.getZ());
    }

    @Test
    void testConvenienceDoubleGetters() {
        Point3D point = new Point3D(1.5, 2.5, 3.5);

        assertEquals(1.5, point.getXAsDouble(), DELTA);
        assertEquals(2.5, point.getYAsDouble(), DELTA);
        assertEquals(3.5, point.getZAsDouble(), DELTA);
    }

    @Test
    void testToString() {
        Point3D point = new Point3D(1.0, 2.0, 3.0);
        String str = point.toString();

        assertNotNull(str);
        assertTrue(str.contains("1"));
        assertTrue(str.contains("2"));
        assertTrue(str.contains("3"));
    }

    @Test
    void testHashCode() {
        Point3D point1Copy = new Point3D(1.0, 2.0, 3.0);

        assertEquals(point1.hashCode(), point1Copy.hashCode());
        assertNotEquals(point1.hashCode(), point2.hashCode());
    }

    @Test
    void testImmutability() {
        // Operations should return new instances, not modify original
        Point3D original = new Point3D(1.0, 2.0, 3.0);
        Point3D originalCopy = new Point3D(1.0, 2.0, 3.0);

        Point3D added = original.add(point2);
        Point3D subtracted = original.subtract(point2);
        Point3D multiplied = original.multiply(2.0);
        Point3D normalized = original.normalize();

        // Original should be unchanged
        assertEquals(originalCopy, original);

        // Results should be different instances
        assertNotSame(original, added);
        assertNotSame(original, subtracted);
        assertNotSame(original, multiplied);
        assertNotSame(original, normalized);
    }

    @Test
    void testVectorOperationsChaining() {
        // Test chaining operations
        Point3D result = point1
            .add(point2)
            .multiply(BigDecimal.valueOf(0.5))
            .normalize();

        assertNotNull(result);

        // Should be a unit vector
        BigDecimal length = result.distanceTo(new Point3D(0, 0, 0));
        assertEquals(1.0, length.doubleValue(), DELTA);
    }

    @Test
    void testLargeCoordinates() {
        // Test with large coordinates to verify BigDecimal precision
        BigDecimal large = new BigDecimal("999999999999999999.123456789123456789");
        Point3D largePoint = new Point3D(large, large, large);

        assertEquals(large, largePoint.getX());
        assertEquals(large, largePoint.getY());
        assertEquals(large, largePoint.getZ());
    }

    @Test
    void testSmallCoordinates() {
        // Test with very small coordinates to verify BigDecimal precision
        BigDecimal small = new BigDecimal("0.000000000000000001");
        Point3D smallPoint = new Point3D(small, small, small);

        assertEquals(small, smallPoint.getX());
        assertEquals(small, smallPoint.getY());
        assertEquals(small, smallPoint.getZ());
    }

    @Test
    void testNegativeCoordinates() {
        Point3D negativePoint = new Point3D(-1.0, -2.0, -3.0);

        assertEquals(-1.0, negativePoint.getXAsDouble(), DELTA);
        assertEquals(-2.0, negativePoint.getYAsDouble(), DELTA);
        assertEquals(-3.0, negativePoint.getZAsDouble(), DELTA);
    }
}
