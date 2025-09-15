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
        canvas = new TestCanvas("canvas", 10, 20, 50, 30);
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

    @Test
    void shouldSetAndEnforceSizeConstraints() {
        // Set constraints
        canvas.setMinWidth(10);
        canvas.setMinHeight(15);
        canvas.setMaxWidth(100);
        canvas.setMaxHeight(80);

        assertEquals(10, canvas.getMinWidth());
        assertEquals(15, canvas.getMinHeight());
        assertEquals(100, canvas.getMaxWidth());
        assertEquals(80, canvas.getMaxHeight());

        // Current size should be adjusted to meet minimum requirements
        assertTrue(canvas.getWidth() >= 10);
        assertTrue(canvas.getHeight() >= 15);
    }

    @Test
    void shouldEnforceMinimumSizeWhenSettingDimensions() {
        canvas.setMinWidth(20);
        canvas.setMinHeight(25);

        // Try to set size below minimum
        canvas.setWidth(10);
        canvas.setHeight(15);

        // Should be clamped to minimum
        assertEquals(20, canvas.getWidth());
        assertEquals(25, canvas.getHeight());
    }

    @Test
    void shouldEnforceMaximumSizeWhenSettingDimensions() {
        canvas.setMaxWidth(80);
        canvas.setMaxHeight(60);

        // Try to set size above maximum
        canvas.setWidth(120);
        canvas.setHeight(100);

        // Should be clamped to maximum
        assertEquals(80, canvas.getWidth());
        assertEquals(60, canvas.getHeight());
    }

    @Test
    void shouldCreateCanvasWithConstraints() {
        TestCanvas constrainedCanvas = new TestCanvas("constrainedCanvas", 5, 10, 30, 40, 20, 25, 100, 80);

        assertEquals(5, constrainedCanvas.getX());
        assertEquals(10, constrainedCanvas.getY());
        assertEquals(30, constrainedCanvas.getWidth()); // Within constraints
        assertEquals(40, constrainedCanvas.getHeight()); // Within constraints
        assertEquals(20, constrainedCanvas.getMinWidth());
        assertEquals(25, constrainedCanvas.getMinHeight());
        assertEquals(100, constrainedCanvas.getMaxWidth());
        assertEquals(80, constrainedCanvas.getMaxHeight());
    }

    @Test
    void shouldEnforceConstraintsInConstructor() {
        // Create canvas with size below minimum
        TestCanvas smallCanvas = new TestCanvas("smallCanvas", 0, 0, 5, 8, 15, 12, 100, 80);

        // Size should be adjusted to minimum
        assertEquals(15, smallCanvas.getWidth());
        assertEquals(12, smallCanvas.getHeight());
    }

    @Test
    void shouldCheckSizeValidation() {
        canvas.setMinSize(10, 15);
        canvas.setMaxSize(80, 60);

        // Set valid size
        canvas.setWidth(40);
        canvas.setHeight(30);

        assertTrue(canvas.meetsMinimumSize());
        assertTrue(canvas.withinMaximumSize());
        assertTrue(canvas.isValidSize());

        // Test with size at minimum boundary
        canvas.setWidth(10);
        canvas.setHeight(15);

        assertTrue(canvas.meetsMinimumSize());
        assertTrue(canvas.isValidSize());

        // Test with size at maximum boundary
        canvas.setWidth(80);
        canvas.setHeight(60);

        assertTrue(canvas.withinMaximumSize());
        assertTrue(canvas.isValidSize());
    }

    @Test
    void shouldSetMinAndMaxSizeTogether() {
        canvas.setMinSize(20, 25);
        assertEquals(20, canvas.getMinWidth());
        assertEquals(25, canvas.getMinHeight());

        canvas.setMaxSize(100, 80);
        assertEquals(100, canvas.getMaxWidth());
        assertEquals(80, canvas.getMaxHeight());
    }

    @Test
    void shouldHandleInvalidConstraints() {
        // Negative minimum should be clamped to 0
        canvas.setMinWidth(-5);
        canvas.setMinHeight(-10);
        assertEquals(0, canvas.getMinWidth());
        assertEquals(0, canvas.getMinHeight());

        // Maximum below 1 should be clamped to 1
        canvas.setMaxWidth(0);
        canvas.setMaxHeight(-5);
        assertEquals(1, canvas.getMaxWidth());
        assertEquals(1, canvas.getMaxHeight());
    }

    @Test
    void shouldSetAndGetLayoutConstraint() {
        assertNull(canvas.getLayoutConstraint());

        PositionConstraint constraint = new PositionConstraint(PositionConstraint.Position.CENTER);
        canvas.setLayoutConstraint(constraint);

        assertEquals(constraint, canvas.getLayoutConstraint());
    }

    @Test
    void shouldAllowNullLayoutConstraint() {
        PositionConstraint constraint = new PositionConstraint(PositionConstraint.Position.TOP_LEFT);
        canvas.setLayoutConstraint(constraint);
        assertNotNull(canvas.getLayoutConstraint());

        canvas.setLayoutConstraint(null);
        assertNull(canvas.getLayoutConstraint());
    }

    /**
     * Test implementation of Canvas for testing purposes.
     */
    private static class TestCanvas extends Canvas {
        public TestCanvas(String name, int x, int y, int width, int height) {
            super(name, x, y, width, height);
        }

        public TestCanvas(String name, int x, int y, int width, int height, int minWidth, int minHeight, int maxWidth, int maxHeight) {
            super(name, x, y, width, height);
            setMinWidth(minWidth);
            setMinHeight(minHeight);
            setMaxWidth(maxWidth);
            setMaxHeight(maxHeight);
        }

        @Override
        public void paint(Graphics graphics) {
            // Test implementation - just fill with 'T'
            graphics.fillRect(getX(), getY(), getWidth(), getHeight(), 'T');
        }
    }
}
