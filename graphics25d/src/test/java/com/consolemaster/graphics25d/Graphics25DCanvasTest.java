package com.consolemaster.graphics25d;

import com.consolemaster.AnsiColor;
import com.consolemaster.Graphics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Graphics25DCanvas class.
 */
class Graphics25DCanvasTest {

    @Mock
    private Graphics mockGraphics;

    private Graphics25DCanvas canvas;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        canvas = new Graphics25DCanvas("Test Canvas", 80, 40);
    }

    @Test
    void testConstructor() {
        assertEquals("Test Canvas", canvas.getName());
        assertEquals(80, canvas.getWidth());
        assertEquals(40, canvas.getHeight());
        assertEquals(0, canvas.getObjectCount());
        assertNotNull(canvas.getCamera());
        assertEquals(10.0, canvas.getViewDistance());
        assertEquals(1.0, canvas.getPerspectiveFactor());
        assertTrue(canvas.isDepthSorting());
    }

    @Test
    void testAddObject() {
        Object25D object = new Object25D(1.0, 2.0, 3.0, "X", AnsiColor.RED);
        canvas.addObject(object);

        assertEquals(1, canvas.getObjectCount());
        assertTrue(canvas.getObjects().contains(object));
    }

    @Test
    void testRemoveObject() {
        Object25D object = new Object25D(1.0, 2.0, 3.0, "X", AnsiColor.RED);
        canvas.addObject(object);
        assertEquals(1, canvas.getObjectCount());

        boolean removed = canvas.removeObject(object);
        assertTrue(removed);
        assertEquals(0, canvas.getObjectCount());
        assertFalse(canvas.getObjects().contains(object));
    }

    @Test
    void testRemoveNonExistentObject() {
        Object25D object = new Object25D(1.0, 2.0, 3.0, "X", AnsiColor.RED);
        boolean removed = canvas.removeObject(object);
        assertFalse(removed);
        assertEquals(0, canvas.getObjectCount());
    }

    @Test
    void testClearObjects() {
        canvas.addObject(new Object25D(1.0, 1.0, 1.0, "A", AnsiColor.RED));
        canvas.addObject(new Object25D(2.0, 2.0, 2.0, "B", AnsiColor.BLUE));
        assertEquals(2, canvas.getObjectCount());

        canvas.clearObjects();
        assertEquals(0, canvas.getObjectCount());
    }

    @Test
    void testCreateObject() {
        Object25D object = canvas.createObject(1.0, 2.0, 3.0, "X", AnsiColor.GREEN);

        assertNotNull(object);
        assertEquals(1.0, object.getPosition().getX());
        assertEquals(2.0, object.getPosition().getY());
        assertEquals(3.0, object.getPosition().getZ());
        assertEquals("X", object.getTexture());
        assertEquals(AnsiColor.GREEN, object.getColor());
        assertEquals(1, canvas.getObjectCount());
        assertTrue(canvas.getObjects().contains(object));
    }

    @Test
    void testPaintEmptyScene() {
        canvas.paint(mockGraphics);

        verify(mockGraphics).setBackgroundColor(AnsiColor.BLACK);
        verify(mockGraphics).clear();
        verifyNoMoreInteractions(mockGraphics);
    }

    @Test
    void testPaintWithObjects() {
        // Add an object close to the camera
        canvas.createObject(1.0, 1.0, 0.0, "X", AnsiColor.RED);

        canvas.paint(mockGraphics);

        verify(mockGraphics).setBackgroundColor(AnsiColor.BLACK);
        verify(mockGraphics).clear();
        // The object should be rendered, so we expect at least one drawChar call
        verify(mockGraphics, atLeastOnce()).setForegroundColor(AnsiColor.RED);
    }

    @Test
    void testViewDistanceFiltering() {
        // Add an object far away (beyond view distance)
        canvas.createObject(20.0, 20.0, 0.0, "X", AnsiColor.RED);
        canvas.setViewDistance(5.0);

        canvas.paint(mockGraphics);

        verify(mockGraphics).setBackgroundColor(AnsiColor.BLACK);
        verify(mockGraphics).clear();
        // The far object should not be rendered
        verify(mockGraphics, never()).setForegroundColor(AnsiColor.RED);
        verify(mockGraphics, never()).drawChar(anyInt(), anyInt(), anyChar());
    }

    @Test
    void testCameraSettings() {
        Camera25D camera = canvas.getCamera();
        assertNotNull(camera);

        // Test setting a new camera
        Camera25D newCamera = new Camera25D(5.0, 5.0, 5.0, 90);
        canvas.setCamera(newCamera);
        assertEquals(newCamera, canvas.getCamera());
    }

    @Test
    void testRenderingSettings() {
        canvas.setViewDistance(15.0);
        assertEquals(15.0, canvas.getViewDistance());

        canvas.setPerspectiveFactor(2.0);
        assertEquals(2.0, canvas.getPerspectiveFactor());

        canvas.setDepthSorting(false);
        assertFalse(canvas.isDepthSorting());

        canvas.setDefaultChar('O');
        assertEquals('O', canvas.getDefaultChar());

        canvas.setBackgroundColor(AnsiColor.BLUE);
        assertEquals(AnsiColor.BLUE, canvas.getBackgroundColor());
    }

    @Test
    void testMultipleObjectsDepthSorting() {
        // Add objects at different distances
        canvas.createObject(1.0, 0.0, 0.0, "A", AnsiColor.RED);     // Close
        canvas.createObject(5.0, 0.0, 0.0, "B", AnsiColor.BLUE);    // Far
        canvas.createObject(3.0, 0.0, 0.0, "C", AnsiColor.GREEN);   // Middle

        canvas.setDepthSorting(true);
        canvas.paint(mockGraphics);

        // All objects should be rendered (they're within default view distance)
        verify(mockGraphics).setForegroundColor(AnsiColor.RED);
        verify(mockGraphics).setForegroundColor(AnsiColor.BLUE);
        verify(mockGraphics).setForegroundColor(AnsiColor.GREEN);
    }
}
