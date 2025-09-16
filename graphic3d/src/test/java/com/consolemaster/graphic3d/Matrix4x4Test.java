package com.consolemaster.graphic3d;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Unit tests for Matrix4x4 class.
 */
class Matrix4x4Test {

    private Matrix4x4 matrix;
    private static final double DELTA = 0.001;

    @BeforeEach
    void setUp() {
        matrix = new Matrix4x4();
    }

    @Test
    void testIdentityMatrix() {
        Matrix4x4 identity = new Matrix4x4();

        // Check diagonal elements are 1
        for (int i = 0; i < 4; i++) {
            assertEquals(BigDecimal.ONE, identity.get(i, i));
        }

        // Check non-diagonal elements are 0
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i != j) {
                    assertEquals(BigDecimal.ZERO, identity.get(i, j));
                }
            }
        }
    }

    @Test
    void testSetAndGet() {
        BigDecimal testValue = BigDecimal.valueOf(3.14159);
        matrix.set(1, 2, testValue);
        assertEquals(testValue, matrix.get(1, 2));

        // Test double convenience method
        matrix.set(2, 3, 2.71828);
        assertEquals(BigDecimal.valueOf(2.71828), matrix.get(2, 3));
    }

    @Test
    void testTranslationMatrixWithDouble() {
        Matrix4x4 translation = Matrix4x4.translation(2.0, 3.0, 4.0);

        // Translation values should be in the last column
        assertEquals(BigDecimal.valueOf(2.0), translation.get(0, 3));
        assertEquals(BigDecimal.valueOf(3.0), translation.get(1, 3));
        assertEquals(BigDecimal.valueOf(4.0), translation.get(2, 3));

        // Diagonal should still be identity
        assertEquals(BigDecimal.ONE, translation.get(0, 0));
        assertEquals(BigDecimal.ONE, translation.get(1, 1));
        assertEquals(BigDecimal.ONE, translation.get(2, 2));
        assertEquals(BigDecimal.ONE, translation.get(3, 3));
    }

    @Test
    void testTranslationMatrixWithBigDecimal() {
        BigDecimal x = BigDecimal.valueOf(1.123456789);
        BigDecimal y = BigDecimal.valueOf(2.234567890);
        BigDecimal z = BigDecimal.valueOf(3.345678901);

        Matrix4x4 translation = Matrix4x4.translation(x, y, z);

        assertEquals(x, translation.get(0, 3));
        assertEquals(y, translation.get(1, 3));
        assertEquals(z, translation.get(2, 3));
    }

    @Test
    void testRotationXWithDouble() {
        double angle = Math.PI / 2; // 90 degrees
        Matrix4x4 rotX = Matrix4x4.rotationX(angle);

        // Check key elements for 90-degree rotation around X-axis
        assertEquals(BigDecimal.ONE, rotX.get(0, 0));
        assertEquals(BigDecimal.ZERO.doubleValue(), rotX.get(1, 1).doubleValue(), DELTA);
        assertEquals(BigDecimal.valueOf(-1).doubleValue(), rotX.get(1, 2).doubleValue(), DELTA);
        assertEquals(BigDecimal.ONE.doubleValue(), rotX.get(2, 1).doubleValue(), DELTA);
        assertEquals(BigDecimal.ZERO.doubleValue(), rotX.get(2, 2).doubleValue(), DELTA);
    }

    @Test
    void testRotationXWithBigDecimal() {
        BigDecimal angle = BigDecimal.valueOf(Math.PI / 4); // 45 degrees
        Matrix4x4 rotX = Matrix4x4.rotationX(angle);

        // Verify matrix is not identity
        assertNotEquals(BigDecimal.ONE, rotX.get(1, 1));
        assertNotEquals(BigDecimal.ZERO, rotX.get(1, 2));
    }

    @Test
    void testRotationYWithDouble() {
        double angle = Math.PI / 2; // 90 degrees
        Matrix4x4 rotY = Matrix4x4.rotationY(angle);

        // Check key elements for 90-degree rotation around Y-axis
        assertEquals(BigDecimal.ZERO.doubleValue(), rotY.get(0, 0).doubleValue(), DELTA);
        assertEquals(BigDecimal.ONE.doubleValue(), rotY.get(0, 2).doubleValue(), DELTA);
        assertEquals(BigDecimal.ONE, rotY.get(1, 1));
        assertEquals(BigDecimal.valueOf(-1).doubleValue(), rotY.get(2, 0).doubleValue(), DELTA);
        assertEquals(BigDecimal.ZERO.doubleValue(), rotY.get(2, 2).doubleValue(), DELTA);
    }

    @Test
    void testRotationZWithDouble() {
        double angle = Math.PI / 2; // 90 degrees
        Matrix4x4 rotZ = Matrix4x4.rotationZ(angle);

        // Check key elements for 90-degree rotation around Z-axis
        assertEquals(BigDecimal.ZERO.doubleValue(), rotZ.get(0, 0).doubleValue(), DELTA);
        assertEquals(BigDecimal.valueOf(-1).doubleValue(), rotZ.get(0, 1).doubleValue(), DELTA);
        assertEquals(BigDecimal.ONE.doubleValue(), rotZ.get(1, 0).doubleValue(), DELTA);
        assertEquals(BigDecimal.ZERO.doubleValue(), rotZ.get(1, 1).doubleValue(), DELTA);
        assertEquals(BigDecimal.ONE, rotZ.get(2, 2));
    }

    @Test
    void testScalingMatrixWithDouble() {
        Matrix4x4 scaling = Matrix4x4.scaling(2.0, 3.0, 4.0);

        assertEquals(BigDecimal.valueOf(2.0), scaling.get(0, 0));
        assertEquals(BigDecimal.valueOf(3.0), scaling.get(1, 1));
        assertEquals(BigDecimal.valueOf(4.0), scaling.get(2, 2));
        assertEquals(BigDecimal.ONE, scaling.get(3, 3));

        // Non-diagonal elements should be zero
        assertEquals(BigDecimal.ZERO, scaling.get(0, 1));
        assertEquals(BigDecimal.ZERO, scaling.get(1, 0));
    }

    @Test
    void testScalingMatrixWithBigDecimal() {
        BigDecimal sx = BigDecimal.valueOf(1.5);
        BigDecimal sy = BigDecimal.valueOf(2.5);
        BigDecimal sz = BigDecimal.valueOf(3.5);

        Matrix4x4 scaling = Matrix4x4.scaling(sx, sy, sz);

        assertEquals(sx, scaling.get(0, 0));
        assertEquals(sy, scaling.get(1, 1));
        assertEquals(sz, scaling.get(2, 2));
    }

    @Test
    void testPerspectiveMatrixWithDouble() {
        double fov = Math.PI / 4;
        double aspect = 16.0 / 9.0;
        double near = 0.1;
        double far = 100.0;

        Matrix4x4 perspective = Matrix4x4.perspective(fov, aspect, near, far);

        // Check that perspective matrix is not identity
        assertNotEquals(BigDecimal.ZERO, perspective.get(0, 0));
        assertNotEquals(BigDecimal.ZERO, perspective.get(1, 1));
        assertNotEquals(BigDecimal.ZERO, perspective.get(2, 2));
        assertEquals(BigDecimal.valueOf(-1), perspective.get(3, 2));
        assertEquals(BigDecimal.ZERO, perspective.get(3, 3));
    }

    @Test
    void testPerspectiveMatrixWithBigDecimal() {
        BigDecimal fov = BigDecimal.valueOf(Math.PI / 3);
        BigDecimal aspect = BigDecimal.valueOf(4.0 / 3.0);
        BigDecimal near = BigDecimal.valueOf(0.5);
        BigDecimal far = BigDecimal.valueOf(50.0);

        Matrix4x4 perspective = Matrix4x4.perspective(fov, aspect, near, far);

        // Verify perspective-specific properties
        assertNotEquals(BigDecimal.ZERO, perspective.get(0, 0));
        assertNotEquals(BigDecimal.ZERO, perspective.get(1, 1));
        assertEquals(BigDecimal.valueOf(-1), perspective.get(3, 2));
    }

    @Test
    void testMatrixMultiplication() {
        Matrix4x4 translation = Matrix4x4.translation(1.0, 2.0, 3.0);
        Matrix4x4 scaling = Matrix4x4.scaling(2.0, 2.0, 2.0);

        Matrix4x4 combined = translation.multiply(scaling);

        // Result should have both translation and scaling effects
        assertEquals(BigDecimal.valueOf(2.0), combined.get(0, 0)); // scaling
        assertEquals(BigDecimal.valueOf(2.0), combined.get(1, 1)); // scaling
        assertEquals(BigDecimal.valueOf(1.0), combined.get(0, 3)); // translation
        assertEquals(BigDecimal.valueOf(2.0), combined.get(1, 3)); // translation
    }

    @Test
    void testPointTransformation() {
        // Test transforming a point with translation matrix
        Matrix4x4 translation = Matrix4x4.translation(1.0, 2.0, 3.0);
        Point3D originalPoint = new Point3D(1.0, 1.0, 1.0);
        Point3D transformedPoint = translation.transform(originalPoint);

        assertEquals(2.0, transformedPoint.getXAsDouble(), DELTA); // 1 + 1
        assertEquals(3.0, transformedPoint.getYAsDouble(), DELTA); // 1 + 2
        assertEquals(4.0, transformedPoint.getZAsDouble(), DELTA); // 1 + 3
    }

    @Test
    void testPointTransformationWithBigDecimal() {
        // Test with BigDecimal precision
        BigDecimal tx = BigDecimal.valueOf(0.123456789);
        BigDecimal ty = BigDecimal.valueOf(0.234567890);
        BigDecimal tz = BigDecimal.valueOf(0.345678901);

        Matrix4x4 translation = Matrix4x4.translation(tx, ty, tz);
        Point3D originalPoint = new Point3D(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
        Point3D transformedPoint = translation.transform(originalPoint);

        assertEquals(BigDecimal.ONE.add(tx), transformedPoint.getX());
        assertEquals(BigDecimal.ONE.add(ty), transformedPoint.getY());
        assertEquals(BigDecimal.ONE.add(tz), transformedPoint.getZ());
    }

    @Test
    @Disabled
    void testIdentityTransformation() {
        Matrix4x4 identity = new Matrix4x4();
        Point3D originalPoint = new Point3D(5.0, 10.0, 15.0);
        Point3D transformedPoint = identity.transform(originalPoint);

        // Identity transformation should not change the point
        assertEquals(originalPoint.getX(), transformedPoint.getX());
        assertEquals(originalPoint.getY(), transformedPoint.getY());
        assertEquals(originalPoint.getZ(), transformedPoint.getZ());
    }

    @Test
    void testChainedTransformations() {
        // Create multiple transformations
        Matrix4x4 translation = Matrix4x4.translation(1.0, 0.0, 0.0);
        Matrix4x4 scaling = Matrix4x4.scaling(2.0, 2.0, 2.0);
        Matrix4x4 rotation = Matrix4x4.rotationZ(Math.PI / 2);

        // Chain them together
        Matrix4x4 combined = translation.multiply(scaling).multiply(rotation);

        Point3D originalPoint = new Point3D(1.0, 0.0, 0.0);
        Point3D transformedPoint = combined.transform(originalPoint);

        // Verify the transformation worked (exact values depend on order of operations)
        assertNotEquals(originalPoint.getX(), transformedPoint.getX());
        assertNotEquals(originalPoint.getY(), transformedPoint.getY());
    }

    @Test
    void testMatrixSettersAndGetters() {
        // Test setting individual matrix elements
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                BigDecimal value = BigDecimal.valueOf(i * 4 + j);
                matrix.set(i, j, value);
                assertEquals(value, matrix.get(i, j));
            }
        }
    }

    @Test
    void testMatrixBounds() {
        // Test accessing matrix bounds
        assertDoesNotThrow(() -> matrix.get(0, 0));
        assertDoesNotThrow(() -> matrix.get(3, 3));
        assertDoesNotThrow(() -> matrix.set(3, 3, BigDecimal.valueOf(42)));
    }

    @Test
    void testRotationMatrixProperties() {
        // Rotation matrices should preserve vector lengths (orthogonal property)
        Matrix4x4 rotY = Matrix4x4.rotationY(Math.PI / 4);
        Point3D unitVector = new Point3D(1.0, 0.0, 0.0);
        Point3D rotatedVector = rotY.transform(unitVector);

        // Length should be preserved (approximately 1.0)
        BigDecimal length = rotatedVector.distanceTo(new Point3D(0, 0, 0));
        assertEquals(1.0, length.doubleValue(), DELTA);
    }

    @Test
    void testScalingMatrixProperties() {
        // Scaling should multiply distances by scale factor
        Matrix4x4 scaling = Matrix4x4.scaling(3.0, 3.0, 3.0);
        Point3D point = new Point3D(1.0, 1.0, 1.0);
        Point3D scaledPoint = scaling.transform(point);

        assertEquals(3.0, scaledPoint.getXAsDouble(), DELTA);
        assertEquals(3.0, scaledPoint.getYAsDouble(), DELTA);
        assertEquals(3.0, scaledPoint.getZAsDouble(), DELTA);
    }
}
