package com.consolemaster.graphics25d;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Camera25D class.
 */
class Camera25DTest {

    @Test
    void testDefaultConstructor() {
        Camera25D camera = new Camera25D();
        assertEquals(0.0, camera.getPosition().getX());
        assertEquals(0.0, camera.getPosition().getY());
        assertEquals(0.0, camera.getPosition().getZ());
        assertEquals(0, camera.getDirection());
        assertEquals("North", camera.getDirectionName());
    }

    @Test
    void testConstructorWithPositionAndDirection() {
        Point25D position = new Point25D(1.0, 2.0, 3.0);
        Camera25D camera = new Camera25D(position, 90);

        assertEquals(1.0, camera.getPosition().getX());
        assertEquals(2.0, camera.getPosition().getY());
        assertEquals(3.0, camera.getPosition().getZ());
        assertEquals(90, camera.getDirection());
        assertEquals("East", camera.getDirectionName());
    }

    @Test
    void testConstructorWithCoordinatesAndDirection() {
        Camera25D camera = new Camera25D(1.0, 2.0, 3.0, 180);

        assertEquals(1.0, camera.getPosition().getX());
        assertEquals(2.0, camera.getPosition().getY());
        assertEquals(3.0, camera.getPosition().getZ());
        assertEquals(180, camera.getDirection());
        assertEquals("South", camera.getDirectionName());
    }

    @Test
    void testSetDirectionValid() {
        Camera25D camera = new Camera25D();

        camera.setDirection(0);
        assertEquals(0, camera.getDirection());

        camera.setDirection(90);
        assertEquals(90, camera.getDirection());

        camera.setDirection(180);
        assertEquals(180, camera.getDirection());

        camera.setDirection(270);
        assertEquals(270, camera.getDirection());
    }

    @Test
    void testSetDirectionInvalid() {
        Camera25D camera = new Camera25D();

        assertThrows(IllegalArgumentException.class, () -> camera.setDirection(45));
        assertThrows(IllegalArgumentException.class, () -> camera.setDirection(360));
        assertThrows(IllegalArgumentException.class, () -> camera.setDirection(-90));
    }

    @Test
    void testRotateClockwise() {
        Camera25D camera = new Camera25D();

        // Start at North (0)
        assertEquals(0, camera.getDirection());

        camera.rotateClockwise();
        assertEquals(90, camera.getDirection()); // East

        camera.rotateClockwise();
        assertEquals(180, camera.getDirection()); // South

        camera.rotateClockwise();
        assertEquals(270, camera.getDirection()); // West

        camera.rotateClockwise();
        assertEquals(0, camera.getDirection()); // Back to North
    }

    @Test
    void testRotateCounterClockwise() {
        Camera25D camera = new Camera25D();

        // Start at North (0)
        assertEquals(0, camera.getDirection());

        camera.rotateCounterClockwise();
        assertEquals(270, camera.getDirection()); // West

        camera.rotateCounterClockwise();
        assertEquals(180, camera.getDirection()); // South

        camera.rotateCounterClockwise();
        assertEquals(90, camera.getDirection()); // East

        camera.rotateCounterClockwise();
        assertEquals(0, camera.getDirection()); // Back to North
    }

    @Test
    void testMoveForward() {
        Camera25D camera = new Camera25D(5.0, 5.0, 0.0, 0);

        // North (0°) - forward decreases Y
        camera.moveForward(2.0);
        assertEquals(5.0, camera.getPosition().getX());
        assertEquals(3.0, camera.getPosition().getY());

        // East (90°) - forward increases X
        camera.setDirection(90);
        camera.moveForward(2.0);
        assertEquals(7.0, camera.getPosition().getX());
        assertEquals(3.0, camera.getPosition().getY());

        // South (180°) - forward increases Y
        camera.setDirection(180);
        camera.moveForward(2.0);
        assertEquals(7.0, camera.getPosition().getX());
        assertEquals(5.0, camera.getPosition().getY());

        // West (270°) - forward decreases X
        camera.setDirection(270);
        camera.moveForward(2.0);
        assertEquals(5.0, camera.getPosition().getX());
        assertEquals(5.0, camera.getPosition().getY());
    }

    @Test
    void testMoveBackward() {
        Camera25D camera = new Camera25D(5.0, 5.0, 0.0, 0);

        // North (0°) - backward increases Y
        camera.moveBackward(2.0);
        assertEquals(5.0, camera.getPosition().getX());
        assertEquals(7.0, camera.getPosition().getY());
    }

    @Test
    void testMoveLeft() {
        Camera25D camera = new Camera25D(5.0, 5.0, 0.0, 0);

        // North (0°) - left decreases X (west)
        camera.moveLeft(2.0);
        assertEquals(3.0, camera.getPosition().getX());
        assertEquals(5.0, camera.getPosition().getY());
    }

    @Test
    void testMoveRight() {
        Camera25D camera = new Camera25D(5.0, 5.0, 0.0, 0);

        // North (0°) - right increases X (east)
        camera.moveRight(2.0);
        assertEquals(7.0, camera.getPosition().getX());
        assertEquals(5.0, camera.getPosition().getY());
    }

    @Test
    void testMoveUpDown() {
        Camera25D camera = new Camera25D(5.0, 5.0, 5.0, 0);

        camera.moveUp(2.0);
        assertEquals(7.0, camera.getPosition().getZ());

        camera.moveDown(4.0);
        assertEquals(3.0, camera.getPosition().getZ());
    }

    @Test
    void testGetDirectionName() {
        Camera25D camera = new Camera25D();

        camera.setDirection(0);
        assertEquals("North", camera.getDirectionName());

        camera.setDirection(90);
        assertEquals("East", camera.getDirectionName());

        camera.setDirection(180);
        assertEquals("South", camera.getDirectionName());

        camera.setDirection(270);
        assertEquals("West", camera.getDirectionName());
    }
}
