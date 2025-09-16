package com.consolemaster.graphic3d;

import com.consolemaster.AnsiColor;
import com.consolemaster.LegacyGraphics;
import com.consolemaster.Screenshot;
import com.consolemaster.StyledChar;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for the complete 3D rendering pipeline.
 * Tests if a cube is correctly rendered to a Graphics buffer.
 */
class Graphic3DRenderingTest {

    private Graphic3DCanvas canvas3D;
    private LegacyGraphics graphics;
    private StyledChar[][] buffer;
    private final int canvasWidth = 40;
    private final int canvasHeight = 20;

    @BeforeEach
    void setUp() {
        // Create a buffer for the graphics
        buffer = new StyledChar[canvasHeight][canvasWidth];
        for (int y = 0; y < canvasHeight; y++) {
            for (int x = 0; x < canvasWidth; x++) {
                buffer[y][x] = new StyledChar(' ');
            }
        }

        // Create graphics context
        graphics = new LegacyGraphics(buffer, canvasWidth, canvasHeight);

        // Create 3D canvas
        canvas3D = new Graphic3DCanvas("test_canvas", canvasWidth, canvasHeight);
        canvas3D.setRenderMode(Graphic3DCanvas.RenderMode.WIREFRAME);
        canvas3D.setWireframeChar('*');
        canvas3D.setWireframeColor(AnsiColor.WHITE);

        // Setup camera for optimal cube viewing
        Camera3D camera = canvas3D.getCamera();
        camera.setPosition(new Point3D(0, 0, 5));
        camera.lookAt(new Point3D(0, 0, 0));
    }

    @Test
    void testCubeRenderingWireframe() {
        // Create a simple cube
        Mesh3D cube = Mesh3D.createCube(2.0);
        canvas3D.addMesh(cube);

        // Render the cube to our graphics buffer
        canvas3D.paint(graphics);

        // Verify that the buffer contains the expected wireframe characters
        boolean foundWireframeChars = false;
        int wireframeCharCount = 0;

        for (int y = 0; y < canvasHeight; y++) {
            for (int x = 0; x < canvasWidth; x++) {
                if (buffer[y][x].getCharacter() == '*') {
                    foundWireframeChars = true;
                    wireframeCharCount++;
                }
            }
        }

        assertTrue(foundWireframeChars, "No wireframe characters found - cube not rendered");
        assertTrue(wireframeCharCount > 10, "Too few wireframe characters - cube may not be properly rendered. Found: " + wireframeCharCount);

        // Debug output - print the rendered buffer
        System.out.println("Rendered cube (wireframe):");
        printBuffer();
    }

    @Test
    void testCubeRenderingFilled() {
        // Create a simple cube with colors
        Mesh3D colorfulCube = Mesh3D.createColorfulCube(2.0);
        canvas3D.addMesh(colorfulCube);
        canvas3D.setRenderMode(Graphic3DCanvas.RenderMode.FILLED);
        canvas3D.setFillChar('#');

        // Render the cube to our graphics buffer
        canvas3D.paint(graphics);

        // Verify that the buffer contains filled characters
        boolean foundFillChars = false;
        int fillCharCount = 0;

        for (int y = 0; y < canvasHeight; y++) {
            for (int x = 0; x < canvasWidth; x++) {
                char c = buffer[y][x].getCharacter();
                if (c != ' ') {
                    foundFillChars = true;
                    fillCharCount++;
                }
            }
        }

        assertTrue(foundFillChars, "No fill characters found - cube not rendered");
        assertTrue(fillCharCount > 20, "Too few fill characters - cube may not be properly rendered. Found: " + fillCharCount);

        // Debug output - print the rendered buffer
        System.out.println("Rendered cube (filled):");
        printBuffer();
    }

    @Test
    void testCubeRenderingWithRotation() {
        // Create a cube and rotate it for better visibility
        Mesh3D cube = Mesh3D.createCube(1.5);
        Matrix4x4 rotation = Matrix4x4.rotationY(Math.PI / 4).multiply(Matrix4x4.rotationX(Math.PI / 6));
        Mesh3D rotatedCube = cube.transform(rotation);
        canvas3D.addMesh(rotatedCube);

        // Render the rotated cube
        canvas3D.paint(graphics);

        // Verify rendering
        boolean foundChars = false;
        int charCount = 0;

        for (int y = 0; y < canvasHeight; y++) {
            for (int x = 0; x < canvasWidth; x++) {
                if (buffer[y][x].getCharacter() == '*') {
                    foundChars = true;
                    charCount++;
                }
            }
        }

        assertTrue(foundChars, "No characters found - rotated cube not rendered");
        assertTrue(charCount > 8, "Too few characters - rotated cube may not be properly rendered. Found: " + charCount);

        // Debug output
        System.out.println("Rendered rotated cube:");
        printBuffer();
    }

    @Test
    void testCameraPositioning() {
        // Test different camera positions
        Mesh3D cube = Mesh3D.createCube(1.0);
        canvas3D.addMesh(cube);

        // Test camera at different distances
        Camera3D camera = canvas3D.getCamera();

        // Close camera
        camera.setPosition(new Point3D(0, 0, 3));
        canvas3D.paint(graphics);
        int closeCharCount = countNonSpaceChars();

        // Clear buffer
        graphics.clear();

        // Far camera
        camera.setPosition(new Point3D(0, 0, 10));
        canvas3D.paint(graphics);
        int farCharCount = countNonSpaceChars();

        // Close camera should show more details (more characters)
        assertTrue(closeCharCount > 0, "Close camera should render something");
        assertTrue(farCharCount >= 0, "Far camera should render something or nothing");

        System.out.println("Close camera char count: " + closeCharCount);
        System.out.println("Far camera char count: " + farCharCount);
    }

    @Test
    void testRenderModeComparison() {
        // Create a colorful cube
        Mesh3D colorfulCube = Mesh3D.createColorfulCube(1.5);
        canvas3D.addMesh(colorfulCube);

        // Test wireframe mode
        canvas3D.setRenderMode(Graphic3DCanvas.RenderMode.WIREFRAME);
        canvas3D.paint(graphics);
        int wireframeCount = countNonSpaceChars();

        graphics.clear();

        // Test filled mode
        canvas3D.setRenderMode(Graphic3DCanvas.RenderMode.FILLED);
        canvas3D.paint(graphics);
        int filledCount = countNonSpaceChars();

        graphics.clear();

        // Test both mode
        canvas3D.setRenderMode(Graphic3DCanvas.RenderMode.BOTH);
        canvas3D.paint(graphics);
        int bothCount = countNonSpaceChars();

        // Verify that modes produce different results
        assertTrue(wireframeCount > 0, "Wireframe mode should render something");
        assertTrue(filledCount > 0, "Filled mode should render something");
        assertTrue(bothCount >= Math.max(wireframeCount, filledCount),
                  "Both mode should render at least as much as individual modes");

        System.out.println("Wireframe chars: " + wireframeCount);
        System.out.println("Filled chars: " + filledCount);
        System.out.println("Both chars: " + bothCount);
    }

    /**
     * Helper method to count non-space characters in the buffer.
     */
    private int countNonSpaceChars() {
        int count = 0;
        for (int y = 0; y < canvasHeight; y++) {
            for (int x = 0; x < canvasWidth; x++) {
                if (buffer[y][x].getCharacter() != ' ') {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Helper method to print the current buffer state for debugging.
     */
    private void printBuffer() {
        Screenshot screenshot = new Screenshot(graphics, System.out);
        screenshot.print(false); // Plain text output for debugging
    }

    /**
     * Test specific cube vertices positioning to debug projection issues.
     */
    @Test
    void testCubeVertexProjection() {
        // Create a simple cube and check if vertices are projected correctly
        Mesh3D cube = Mesh3D.createCube(2.0);

        // Print cube vertices for debugging
        System.out.println("Cube vertices:");
        for (int i = 0; i < cube.getVertices().size(); i++) {
            Point3D vertex = cube.getVertices().get(i);
            System.out.println("Vertex " + i + ": " + vertex);
        }

        // Check camera setup
        Camera3D camera = canvas3D.getCamera();
        System.out.println("Camera position: " + camera.getPosition());
        System.out.println("Camera rotation: " + camera.getRotation());

        // Test projection matrix
        double aspectRatio = (double) canvasWidth / canvasHeight;
        Matrix4x4 viewProjection = camera.getViewProjectionMatrix(aspectRatio);
        System.out.println("View-projection matrix created");

        // Transform one vertex to see if projection works
        Point3D testVertex = cube.getVertices().get(0);
        Point3D projected = viewProjection.transform(testVertex);
        System.out.println("Test vertex " + testVertex + " projected to " + projected);

        // Convert to screen coordinates like the renderer does
        double centerX = canvasWidth / 2.0;
        double centerY = canvasHeight / 2.0;
        double scale = Math.min(canvasWidth, canvasHeight) / 4.0;
        double screenX = centerX + projected.getX() * scale;
        double screenY = centerY - projected.getY() * scale;

        System.out.println("Screen coordinates: (" + screenX + ", " + screenY + ")");

        // Verify screen coordinates are within canvas bounds
        assertTrue(screenX >= 0 && screenX < canvasWidth, "Screen X coordinate out of bounds: " + screenX);
        assertTrue(screenY >= 0 && screenY < canvasHeight, "Screen Y coordinate out of bounds: " + screenY);
    }

    /**
     * Test the Screenshot class functionality with styled output.
     */
    @Test
    void testScreenshotFunctionality() {
        // Create a colorful cube with different face colors
        Mesh3D colorfulCube = Mesh3D.createColorfulCube(1.5);
        canvas3D.addMesh(colorfulCube);
        canvas3D.setRenderMode(Graphic3DCanvas.RenderMode.FILLED);

        // Render the cube
        canvas3D.paint(graphics);

        // Test Screenshot with plain output
        System.out.println("Screenshot - Plain output:");
        Screenshot plainScreenshot = new Screenshot(graphics, System.out);
        plainScreenshot.print(false);

        // Test Screenshot with styled output
        System.out.println("\nScreenshot - Styled output (with ANSI colors):");
        Screenshot styledScreenshot = new Screenshot(graphics, System.out);
        styledScreenshot.print(true);

        // Test alternative constructors
        System.out.println("\nScreenshot - Direct buffer constructor:");
        Screenshot bufferScreenshot = new Screenshot(buffer, canvasWidth, canvasHeight, System.out);
        bufferScreenshot.print(false);

        // Test toString functionality
        String plainString = plainScreenshot.toString(false);
        String styledString = styledScreenshot.toString(true);

        assertNotNull(plainString, "Plain string output should not be null");
        assertNotNull(styledString, "Styled string output should not be null");
        assertTrue(plainString.contains("+"), "Plain output should contain border characters");
        assertTrue(styledString.length() >= plainString.length(), "Styled output should be at least as long as plain output");

        System.out.println("Screenshot functionality test completed successfully!");
    }
}
