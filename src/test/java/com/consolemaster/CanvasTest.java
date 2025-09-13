package com.consolemaster;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Canvas base class.
 */
class CanvasTest {

    private TestCanvas canvas;

    @BeforeEach
    void setUp() {
        canvas = new TestCanvas(10, 20, 50, 30);
    }

    @Test
    void shouldCreateCanvasWithCorrectDimensions() {
        assertEquals(10, canvas.getX());
        assertEquals(20, canvas.getY());
        assertEquals(50, canvas.getWidth());
        assertEquals(30, canvas.getHeight());
        assertTrue(canvas.isVisible());
    }

    @Test
    void shouldCalculateRightAndBottomBoundaries() {
        assertEquals(60, canvas.getRight()); // x + width = 10 + 50
        assertEquals(50, canvas.getBottom()); // y + height = 20 + 30
    }

    @Test
    void shouldDetectPointsWithinBounds() {
        assertTrue(canvas.contains(10, 20)); // top-left corner
        assertTrue(canvas.contains(59, 49)); // bottom-right corner (exclusive)
        assertTrue(canvas.contains(30, 35)); // somewhere in the middle

        assertFalse(canvas.contains(9, 20));   // left of canvas
        assertFalse(canvas.contains(60, 20));  // right of canvas
        assertFalse(canvas.contains(10, 19));  // above canvas
        assertFalse(canvas.contains(10, 50));  // below canvas
    }

    @Test
    void shouldUpdatePosition() {
        canvas.setX(15);
        canvas.setY(25);

        assertEquals(15, canvas.getX());
        assertEquals(25, canvas.getY());
        assertEquals(65, canvas.getRight());
        assertEquals(55, canvas.getBottom());
    }

    @Test
    void shouldUpdateDimensions() {
        canvas.setWidth(100);
        canvas.setHeight(60);

        assertEquals(100, canvas.getWidth());
        assertEquals(60, canvas.getHeight());
        assertEquals(110, canvas.getRight());
        assertEquals(80, canvas.getBottom());
    }

    @Test
    void shouldToggleVisibility() {
        assertTrue(canvas.isVisible());

        canvas.setVisible(false);
        assertFalse(canvas.isVisible());

        canvas.setVisible(true);
        assertTrue(canvas.isVisible());
    }

    /**
     * Test implementation of Canvas for testing purposes.
     */
    private static class TestCanvas extends Canvas {
        public TestCanvas(int x, int y, int width, int height) {
            super(x, y, width, height);
        }

        @Override
        public void paint(Graphics graphics) {
            // Test implementation - just fill with 'T'
            graphics.fillRect(getX(), getY(), getWidth(), getHeight(), 'T');
        }
    }
}
