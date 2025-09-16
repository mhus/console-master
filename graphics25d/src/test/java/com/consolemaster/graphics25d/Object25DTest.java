package com.consolemaster.graphics25d;

import com.consolemaster.AnsiColor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Object25D class.
 */
class Object25DTest {

    @Test
    void testDefaultConstructor() {
        Object25D object = new Object25D();
        assertNull(object.getPosition());
        assertNull(object.getTexture());
        assertNull(object.getColor());
    }

    @Test
    void testConstructorWithPointAndProperties() {
        Point25D position = new Point25D(1.0, 2.0, 3.0);
        Object25D object = new Object25D(position, "X", AnsiColor.RED);

        assertEquals(position, object.getPosition());
        assertEquals("X", object.getTexture());
        assertEquals(AnsiColor.RED, object.getColor());
    }

    @Test
    void testConstructorWithCoordinatesAndProperties() {
        Object25D object = new Object25D(1.0, 2.0, 3.0, "O", AnsiColor.BLUE);

        assertEquals(1.0, object.getPosition().getX());
        assertEquals(2.0, object.getPosition().getY());
        assertEquals(3.0, object.getPosition().getZ());
        assertEquals("O", object.getTexture());
        assertEquals(AnsiColor.BLUE, object.getColor());
    }

    @Test
    void testDistanceTo() {
        Object25D object = new Object25D(3.0, 4.0, 0.0, "X", AnsiColor.RED);
        Point25D point = new Point25D(0.0, 0.0, 0.0);

        double distance = object.distanceTo(point);
        assertEquals(5.0, distance, 0.001); // 3-4-5 triangle
    }

    @Test
    void testCopy() {
        Object25D original = new Object25D(1.0, 2.0, 3.0, "X", AnsiColor.GREEN);
        Object25D copy = original.copy();

        // Check that values are the same
        assertEquals(original.getPosition().getX(), copy.getPosition().getX());
        assertEquals(original.getPosition().getY(), copy.getPosition().getY());
        assertEquals(original.getPosition().getZ(), copy.getPosition().getZ());
        assertEquals(original.getTexture(), copy.getTexture());
        assertEquals(original.getColor(), copy.getColor());

        // Ensure it's a different object
        assertNotSame(original, copy);
        // Ensure position is also copied, not referenced
        assertNotSame(original.getPosition(), copy.getPosition());
    }

    @Test
    void testSettersAndGetters() {
        Object25D object = new Object25D();
        Point25D position = new Point25D(5.0, 6.0, 7.0);

        object.setPosition(position);
        object.setTexture("@");
        object.setColor(AnsiColor.YELLOW);

        assertEquals(position, object.getPosition());
        assertEquals("@", object.getTexture());
        assertEquals(AnsiColor.YELLOW, object.getColor());
    }
}
