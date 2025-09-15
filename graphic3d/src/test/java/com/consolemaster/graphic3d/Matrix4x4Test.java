package com.consolemaster.graphic3d;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Matrix4x4 class.
 */
class Matrix4x4Test {

    private Matrix4x4 matrix;

    @BeforeEach
    void setUp() {
        matrix = new Matrix4x4();
    }

    @Test
    void testIdentityMatrix() {
        Matrix4x4 identity = new Matrix4x4();

        // Check diagonal elements are 1
        for (int i = 0; i < 4; i++) {
            assertEquals(1.0, identity.getMatrix()[i][i]);
        }

        // Check non-diagonal elements are 0
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i != j) {
                    assertEquals(0.0, identity.getMatrix()[i][j]);
                }
            }
        }
    }

    @Test
    void testTranslation() {
        Matrix4x4 translation = Matrix4x4.translation(1.0, 2.0, 3.0);
        Point3D point = new Point3D(0.0, 0.0, 0.0);
        Point3D result = translation.transform(point);

        assertEquals(1.0, result.getX());
        assertEquals(2.0, result.getY());
        assertEquals(3.0, result.getZ());
    }

    @Test
    void testRotationX() {
        Matrix4x4 rotation = Matrix4x4.rotationX(Math.PI / 2);
        Point3D point = new Point3D(0.0, 1.0, 0.0);
        Point3D result = rotation.transform(point);

        assertEquals(0.0, result.getX(), 0.001);
        assertEquals(0.0, result.getY(), 0.001);
        assertEquals(1.0, result.getZ(), 0.001);
    }

    @Test
    void testRotationY() {
        Matrix4x4 rotation = Matrix4x4.rotationY(Math.PI / 2);
        Point3D point = new Point3D(1.0, 0.0, 0.0);
        Point3D result = rotation.transform(point);

        assertEquals(0.0, result.getX(), 0.001);
        assertEquals(0.0, result.getY(), 0.001);
        assertEquals(-1.0, result.getZ(), 0.001);
    }

    @Test
    void testRotationZ() {
        Matrix4x4 rotation = Matrix4x4.rotationZ(Math.PI / 2);
        Point3D point = new Point3D(1.0, 0.0, 0.0);
        Point3D result = rotation.transform(point);

        assertEquals(0.0, result.getX(), 0.001);
        assertEquals(1.0, result.getY(), 0.001);
        assertEquals(0.0, result.getZ(), 0.001);
    }

    @Test
    void testScaling() {
        Matrix4x4 scaling = Matrix4x4.scaling(2.0, 3.0, 4.0);
        Point3D point = new Point3D(1.0, 1.0, 1.0);
        Point3D result = scaling.transform(point);

        assertEquals(2.0, result.getX());
        assertEquals(3.0, result.getY());
        assertEquals(4.0, result.getZ());
    }

    @Test
    void testMatrixMultiplication() {
        Matrix4x4 translation = Matrix4x4.translation(1.0, 0.0, 0.0);
        Matrix4x4 scaling = Matrix4x4.scaling(2.0, 2.0, 2.0);
        Matrix4x4 combined = scaling.multiply(translation);

        Point3D point = new Point3D(1.0, 1.0, 1.0);
        Point3D result = combined.transform(point);

        assertEquals(4.0, result.getX()); // (1+1)*2
        assertEquals(2.0, result.getY()); // 1*2
        assertEquals(2.0, result.getZ()); // 1*2
    }

    @Test
    void testPerspectiveMatrixCreation() {
        // Test that perspective matrix creation works
        Matrix4x4 perspective = Matrix4x4.perspective(Math.PI / 4, 1.0, 0.1, 100.0);
        assertNotNull(perspective);
        // Basic validation that matrix was modified from identity
        assertTrue(perspective.getMatrix()[0][0] != 1.0 || perspective.getMatrix()[1][1] != 1.0);
    }
}
