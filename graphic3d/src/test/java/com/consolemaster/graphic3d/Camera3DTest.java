package com.consolemaster.graphic3d;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Unit tests for Camera3D class.
 */
class Camera3DTest {

    private Camera3D camera;

    @BeforeEach
    void setUp() {
        camera = new Camera3D();
    }

    @Test
    void testDefaultConstructor() {
        assertEquals(new Point3D(0, 0, 5), camera.getPosition());
        assertEquals(new Point3D(0, 0, 0), camera.getRotation());
        assertEquals(Math.PI / 4, camera.getFovAsDouble(), 0.001);
        assertEquals(0.1, camera.getNearAsDouble(), 0.001);
        assertEquals(100.0, camera.getFarAsDouble(), 0.001);
    }

    @Test
    void testParameterizedConstructor() {
        Point3D position = new Point3D(1, 2, 3);
        Point3D rotation = new Point3D(0.1, 0.2, 0.3);
        Camera3D customCamera = new Camera3D(position, rotation);

        assertEquals(position, customCamera.getPosition());
        assertEquals(rotation, customCamera.getRotation());
    }

    @Test
    void testParameterizedConstructorWithBigDecimal() {
        Point3D position = new Point3D(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3));
        Point3D rotation = new Point3D(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.3));
        BigDecimal fov = BigDecimal.valueOf(Math.PI / 3);
        BigDecimal near = BigDecimal.valueOf(0.5);
        BigDecimal far = BigDecimal.valueOf(200.0);

        Camera3D customCamera = new Camera3D(position, rotation, fov, near, far);

        assertEquals(position, customCamera.getPosition());
        assertEquals(rotation, customCamera.getRotation());
        assertEquals(fov, customCamera.getFov());
        assertEquals(near, customCamera.getNear());
        assertEquals(far, customCamera.getFar());
    }

    @Test
    void testParameterizedConstructorWithDouble() {
        Point3D position = new Point3D(1, 2, 3);
        Point3D rotation = new Point3D(0.1, 0.2, 0.3);
        double fov = Math.PI / 3;
        double near = 0.5;
        double far = 200.0;

        Camera3D customCamera = new Camera3D(position, rotation, fov, near, far);

        assertEquals(position, customCamera.getPosition());
        assertEquals(rotation, customCamera.getRotation());
        assertEquals(fov, customCamera.getFovAsDouble(), 0.001);
        assertEquals(near, customCamera.getNearAsDouble(), 0.001);
        assertEquals(far, customCamera.getFarAsDouble(), 0.001);
    }

    @Test
    void testMovementMethods() {
        Point3D initialPosition = camera.getPosition();

        // Test forward movement
        camera.moveForward(1.0);
        assertNotEquals(initialPosition, camera.getPosition());

        // Reset position
        camera.setPosition(new Point3D(0, 0, 5));

        // Test right movement
        camera.moveRight(1.0);
        assertNotEquals(new Point3D(0, 0, 5), camera.getPosition());

        // Reset position
        camera.setPosition(new Point3D(0, 0, 5));

        // Test up movement
        camera.moveUp(1.0);
        assertNotEquals(new Point3D(0, 0, 5), camera.getPosition());
    }

    @Test
    void testMovementMethodsWithBigDecimal() {
        Point3D initialPosition = camera.getPosition();

        // Test forward movement with BigDecimal
        camera.moveForward(BigDecimal.valueOf(1.0));
        assertNotEquals(initialPosition, camera.getPosition());

        // Reset position
        camera.setPosition(new Point3D(0, 0, 5));

        // Test right movement with BigDecimal
        camera.moveRight(BigDecimal.valueOf(1.0));
        assertNotEquals(new Point3D(0, 0, 5), camera.getPosition());

        // Reset position
        camera.setPosition(new Point3D(0, 0, 5));

        // Test up movement with BigDecimal
        camera.moveUp(BigDecimal.valueOf(1.0));
        assertNotEquals(new Point3D(0, 0, 5), camera.getPosition());
    }

    @Test
    void testRotation() {
        Point3D initialRotation = camera.getRotation();

        // Test rotation with double values
        camera.rotate(0.1, 0.2, 0.3);
        assertNotEquals(initialRotation, camera.getRotation());
    }

    @Test
    void testRotationWithBigDecimal() {
        Point3D initialRotation = camera.getRotation();

        // Test rotation with BigDecimal values
        camera.rotate(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.3));
        assertNotEquals(initialRotation, camera.getRotation());
    }

    @Test
    void testLookAt() {
        Point3D target = new Point3D(1, 1, 0);
        Point3D initialRotation = camera.getRotation();

        camera.lookAt(target);
        assertNotEquals(initialRotation, camera.getRotation());
    }

    @Test
    void testViewMatrix() {
        Matrix4x4 viewMatrix = camera.getViewMatrix();
        assertNotNull(viewMatrix);
    }

    @Test
    void testProjectionMatrix() {
        double aspectRatio = 16.0 / 9.0;
        Matrix4x4 projectionMatrix = camera.getProjectionMatrix(aspectRatio);
        assertNotNull(projectionMatrix);
    }

    @Test
    void testProjectionMatrixWithBigDecimal() {
        BigDecimal aspectRatio = BigDecimal.valueOf(16.0).divide(BigDecimal.valueOf(9.0), MathContext.DECIMAL128);
        Matrix4x4 projectionMatrix = camera.getProjectionMatrix(aspectRatio);
        assertNotNull(projectionMatrix);
    }

    @Test
    void testViewProjectionMatrix() {
        double aspectRatio = 16.0 / 9.0;
        Matrix4x4 viewProjectionMatrix = camera.getViewProjectionMatrix(aspectRatio);
        assertNotNull(viewProjectionMatrix);
    }

    @Test
    void testViewProjectionMatrixWithBigDecimal() {
        BigDecimal aspectRatio = BigDecimal.valueOf(16.0).divide(BigDecimal.valueOf(9.0), MathContext.DECIMAL128);
        Matrix4x4 viewProjectionMatrix = camera.getViewProjectionMatrix(aspectRatio);
        assertNotNull(viewProjectionMatrix);
    }

    @Test
    void testFovGetterSetter() {
        BigDecimal newFov = BigDecimal.valueOf(Math.PI / 2);
        camera.setFov(newFov);
        assertEquals(newFov, camera.getFov());
    }

    @Test
    void testNearFarGetterSetter() {
        BigDecimal newNear = BigDecimal.valueOf(0.5);
        BigDecimal newFar = BigDecimal.valueOf(500.0);

        camera.setNear(newNear);
        camera.setFar(newFar);

        assertEquals(newNear, camera.getNear());
        assertEquals(newFar, camera.getFar());
    }
}
