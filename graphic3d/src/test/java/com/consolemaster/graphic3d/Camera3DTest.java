package com.consolemaster.graphic3d;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(Math.PI / 4, camera.getFov(), 0.001);
        assertEquals(0.1, camera.getNear());
        assertEquals(100.0, camera.getFar());
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
    void testMoveForward() {
        Point3D initialPosition = camera.getPosition();
        camera.moveForward(2.0);

        // Camera should move forward along its local Z axis
        assertNotEquals(initialPosition, camera.getPosition());
    }

    @Test
    void testMoveRight() {
        Point3D initialPosition = camera.getPosition();
        camera.moveRight(1.0);

        // Camera should move right along its local X axis
        assertNotEquals(initialPosition, camera.getPosition());
    }

    @Test
    void testMoveUp() {
        Point3D initialPosition = camera.getPosition();
        camera.moveUp(0.5);

        // Camera should move up along its local Y axis
        assertNotEquals(initialPosition, camera.getPosition());
    }

    @Test
    void testRotate() {
        Point3D initialRotation = camera.getRotation();
        camera.rotate(0.1, 0.2, 0.3);

        Point3D expectedRotation = new Point3D(0.1, 0.2, 0.3);
        assertEquals(expectedRotation, camera.getRotation());
    }

    @Test
    void testLookAt() {
        Point3D target = new Point3D(1, 0, 0);
        camera.lookAt(target);

        // Rotation should be adjusted to look at the target
        assertNotEquals(new Point3D(0, 0, 0), camera.getRotation());
    }

    @Test
    void testGetViewMatrix() {
        Matrix4x4 viewMatrix = camera.getViewMatrix();
        assertNotNull(viewMatrix);

        // View matrix should be a valid 4x4 matrix
        assertEquals(4, viewMatrix.getMatrix().length);
        assertEquals(4, viewMatrix.getMatrix()[0].length);
    }

    @Test
    void testGetProjectionMatrix() {
        double aspectRatio = 16.0 / 9.0;
        Matrix4x4 projectionMatrix = camera.getProjectionMatrix(aspectRatio);
        assertNotNull(projectionMatrix);

        // Projection matrix should be a valid 4x4 matrix
        assertEquals(4, projectionMatrix.getMatrix().length);
        assertEquals(4, projectionMatrix.getMatrix()[0].length);
    }

    @Test
    void testGetViewProjectionMatrix() {
        double aspectRatio = 1.0;
        Matrix4x4 viewProjectionMatrix = camera.getViewProjectionMatrix(aspectRatio);
        assertNotNull(viewProjectionMatrix);

        // Should be the combination of projection and view matrices
        Matrix4x4 expectedVP = camera.getProjectionMatrix(aspectRatio).multiply(camera.getViewMatrix());

        // Compare a few elements to verify the matrices are equivalent
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(expectedVP.getMatrix()[i][j], viewProjectionMatrix.getMatrix()[i][j], 0.001);
            }
        }
    }

    @Test
    void testCameraConfiguration() {
        camera.setFov(Math.PI / 3);
        assertEquals(Math.PI / 3, camera.getFov());

        camera.setNear(0.5);
        assertEquals(0.5, camera.getNear());

        camera.setFar(200.0);
        assertEquals(200.0, camera.getFar());
    }
}
