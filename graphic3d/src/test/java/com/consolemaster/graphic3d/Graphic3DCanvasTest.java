package com.consolemaster.graphic3d;

import com.consolemaster.AnsiColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Graphic3DCanvas class.
 */
class Graphic3DCanvasTest {

    private Graphic3DCanvas canvas;

    @BeforeEach
    void setUp() {
        canvas = new Graphic3DCanvas("test_canvas", 80, 40);
    }

    @Test
    void testConstructor() {
        assertEquals("test_canvas", canvas.getName());
        assertEquals(80, canvas.getWidth());
        assertEquals(40, canvas.getHeight());
        assertNotNull(canvas.getCamera());
        assertNotNull(canvas.getMeshes());
        assertTrue(canvas.getMeshes().isEmpty());
        assertEquals(Graphic3DCanvas.RenderMode.WIREFRAME, canvas.getRenderMode());
        assertTrue(canvas.isBackfaceCulling());
    }

    @Test
    void testRenderModeConfiguration() {
        canvas.setRenderMode(Graphic3DCanvas.RenderMode.FILLED);
        assertEquals(Graphic3DCanvas.RenderMode.FILLED, canvas.getRenderMode());

        canvas.setRenderMode(Graphic3DCanvas.RenderMode.BOTH);
        assertEquals(Graphic3DCanvas.RenderMode.BOTH, canvas.getRenderMode());
    }

    @Test
    void testCharacterConfiguration() {
        canvas.setWireframeChar('+');
        assertEquals('+', canvas.getWireframeChar());

        canvas.setFillChar('#');
        assertEquals('#', canvas.getFillChar());
    }

    @Test
    void testColorConfiguration() {
        canvas.setWireframeColor(AnsiColor.RED);
        assertEquals(AnsiColor.RED, canvas.getWireframeColor());

        canvas.setFillColor(AnsiColor.BLUE);
        assertEquals(AnsiColor.BLUE, canvas.getFillColor());
    }

    @Test
    void testAddMesh() {
        Mesh3D cube = Mesh3D.createCube(1.0);
        canvas.addMesh(cube);

        assertEquals(1, canvas.getMeshes().size());
        assertTrue(canvas.getMeshes().contains(cube));
    }

    @Test
    void testRemoveMesh() {
        Mesh3D cube = Mesh3D.createCube(1.0);
        canvas.addMesh(cube);
        canvas.removeMesh(cube);

        assertTrue(canvas.getMeshes().isEmpty());
    }

    @Test
    void testClearMeshes() {
        canvas.addMesh(Mesh3D.createCube(1.0));
        canvas.addMesh(Mesh3D.createPyramid(1.0));

        assertEquals(2, canvas.getMeshes().size());

        canvas.clearMeshes();
        assertTrue(canvas.getMeshes().isEmpty());
    }

    @Test
    void testCameraAccess() {
        Camera3D camera = canvas.getCamera();
        assertNotNull(camera);

        // Modify camera and verify changes
        camera.setPosition(new Point3D(1, 2, 3));
        assertEquals(new Point3D(1, 2, 3), canvas.getCamera().getPosition());
    }

    @Test
    void testBackfaceCulling() {
        canvas.setBackfaceCulling(false);
        assertFalse(canvas.isBackfaceCulling());

        canvas.setBackfaceCulling(true);
        assertTrue(canvas.isBackfaceCulling());
    }

    @Test
    void testDepthBuffer() {
        // Depth buffer should be initialized with canvas dimensions
        double[][] depthBuffer = canvas.getDepthBuffer();
        assertEquals(40, depthBuffer.length);    // height
        assertEquals(80, depthBuffer[0].length); // width
    }
}
