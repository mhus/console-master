package com.consolemaster.graphic3d;

import com.consolemaster.AnsiColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

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
    void testCanvasCreation() {
        assertEquals("test_canvas", canvas.getName());
        assertEquals(80, canvas.getWidth());
        assertEquals(40, canvas.getHeight());
    }

    @Test
    void testDefaultRenderMode() {
        assertEquals(Graphic3DCanvas.RenderMode.WIREFRAME, canvas.getRenderMode());
    }

    @Test
    void testRenderModeChange() {
        canvas.setRenderMode(Graphic3DCanvas.RenderMode.FILLED);
        assertEquals(Graphic3DCanvas.RenderMode.FILLED, canvas.getRenderMode());

        canvas.setRenderMode(Graphic3DCanvas.RenderMode.BOTH);
        assertEquals(Graphic3DCanvas.RenderMode.BOTH, canvas.getRenderMode());
    }

    @Test
    void testWireframeCharacterSettings() {
        canvas.setWireframeChar('*');
        assertEquals('*', canvas.getWireframeChar());

        canvas.setWireframeChar('#');
        assertEquals('#', canvas.getWireframeChar());
    }

    @Test
    void testFillCharacterSettings() {
        canvas.setFillChar('#');
        assertEquals('#', canvas.getFillChar());

        canvas.setFillChar('█');
        assertEquals('█', canvas.getFillChar());
    }

    @Test
    void testWireframeColorSettings() {
        canvas.setWireframeColor(AnsiColor.RED);
        assertEquals(AnsiColor.RED, canvas.getWireframeColor());

        canvas.setWireframeColor(AnsiColor.BLUE);
        assertEquals(AnsiColor.BLUE, canvas.getWireframeColor());
    }

    @Test
    void testFillColorSettings() {
        canvas.setFillColor(AnsiColor.GREEN);
        assertEquals(AnsiColor.GREEN, canvas.getFillColor());

        canvas.setFillColor(AnsiColor.YELLOW);
        assertEquals(AnsiColor.YELLOW, canvas.getFillColor());
    }

    @Test
    void testMeshManagement() {
        assertEquals(0, canvas.getMeshes().size());

        Mesh3D cube = Mesh3D.createCube(2.0);
        canvas.addMesh(cube);
        assertEquals(1, canvas.getMeshes().size());
        assertTrue(canvas.getMeshes().contains(cube));

        Mesh3D pyramid = Mesh3D.createPyramid(1.5);
        canvas.addMesh(pyramid);
        assertEquals(2, canvas.getMeshes().size());

        canvas.removeMesh(cube);
        assertEquals(1, canvas.getMeshes().size());
        assertFalse(canvas.getMeshes().contains(cube));
        assertTrue(canvas.getMeshes().contains(pyramid));

        canvas.clearMeshes();
        assertEquals(0, canvas.getMeshes().size());
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
        // Depth buffer should be initialized with canvas dimensions and use BigDecimal
        BigDecimal[][] depthBuffer = canvas.getDepthBuffer();
        assertEquals(40, depthBuffer.length);    // height
        assertEquals(80, depthBuffer[0].length); // width

        // Initially, depth buffer should be null (representing maximum depth)
        assertNull(depthBuffer[0][0]);
        assertNull(depthBuffer[39][79]);
    }

    @Test
    void testDefaultCameraSettings() {
        Camera3D camera = canvas.getCamera();
        assertNotNull(camera);

        // Check default camera position and rotation
        assertEquals(new Point3D(0, 0, 5), camera.getPosition());
        assertEquals(new Point3D(0, 0, 0), camera.getRotation());
    }

    @Test
    void testMeshWithBigDecimalPrecision() {
        // Test adding mesh created with BigDecimal precision
        Mesh3D preciseCube = Mesh3D.createCube(BigDecimal.valueOf(1.123456789));
        canvas.addMesh(preciseCube);

        assertEquals(1, canvas.getMeshes().size());
        assertTrue(canvas.getMeshes().contains(preciseCube));
    }

    @Test
    void testCameraWithBigDecimalPrecision() {
        Camera3D camera = canvas.getCamera();

        // Test setting camera properties with BigDecimal precision
        Point3D precisePosition = new Point3D(
            BigDecimal.valueOf(1.123456789),
            BigDecimal.valueOf(2.234567890),
            BigDecimal.valueOf(3.345678901)
        );

        camera.setPosition(precisePosition);
        assertEquals(precisePosition, camera.getPosition());

        // Test BigDecimal camera methods
        camera.moveForward(BigDecimal.valueOf(0.123456789));
        assertNotEquals(precisePosition, camera.getPosition());
    }

    @Test
    void testRenderModeEnum() {
        // Test all render mode values
        Graphic3DCanvas.RenderMode[] modes = Graphic3DCanvas.RenderMode.values();
        assertEquals(3, modes.length);

        assertTrue(java.util.Arrays.asList(modes).contains(Graphic3DCanvas.RenderMode.WIREFRAME));
        assertTrue(java.util.Arrays.asList(modes).contains(Graphic3DCanvas.RenderMode.FILLED));
        assertTrue(java.util.Arrays.asList(modes).contains(Graphic3DCanvas.RenderMode.BOTH));
    }

    @Test
    void testCanvasInheritance() {
        // Verify that Graphic3DCanvas extends Canvas
        assertTrue(canvas instanceof com.consolemaster.Canvas);
    }

    @Test
    void testMultipleMeshTypes() {
        // Test adding different types of meshes
        Mesh3D cube = Mesh3D.createCube(1.0);
        Mesh3D pyramid = Mesh3D.createPyramid(1.5);
        Mesh3D colorfulCube = Mesh3D.createColorfulCube(2.0);
        Mesh3D texturedCube = Mesh3D.createTexturedCube(1.2);

        canvas.addMesh(cube);
        canvas.addMesh(pyramid);
        canvas.addMesh(colorfulCube);
        canvas.addMesh(texturedCube);

        assertEquals(4, canvas.getMeshes().size());
        assertTrue(canvas.getMeshes().contains(cube));
        assertTrue(canvas.getMeshes().contains(pyramid));
        assertTrue(canvas.getMeshes().contains(colorfulCube));
        assertTrue(canvas.getMeshes().contains(texturedCube));
    }
}
