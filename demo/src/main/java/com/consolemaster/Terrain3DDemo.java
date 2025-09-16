package com.consolemaster;

import com.consolemaster.graphic3d.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Random;

/**
 * Demo showcasing 3D terrain generation and rendering using the BigDecimal-based 3D graphics system.
 * This demo generates a procedural terrain using Perlin-like noise and renders it as a 3D mesh
 * with wireframe visualization in the console.
 */
@Slf4j
public class Terrain3DDemo {

    private static final int TERRAIN_SIZE = 32;
    private static final BigDecimal HEIGHT_SCALE = BigDecimal.valueOf(5.0);
    private static final MathContext MATH_CONTEXT = new MathContext(34, RoundingMode.HALF_UP);

    private static BigDecimal[][] heightMap;
    private static Graphic3DCanvas canvas3D;
    private static Camera3D camera;
    private static Mesh3D terrainMesh;
    private static boolean wireframeMode = true;
    private static BigDecimal cameraAngle = BigDecimal.ZERO;

    public static void main(String[] args) {
        log.info("Starting Terrain 3D Demo with BigDecimal precision");

        try (Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build()) {

            terminal.enterRawMode();

            // Get terminal size
            int width = Math.max(80, terminal.getWidth());
            int height = Math.max(24, terminal.getHeight());

            // Create 3D canvas
            canvas3D = new Graphic3DCanvas("Terrain3D", width, height - 4);
            canvas3D.setRenderMode(Graphic3DCanvas.RenderMode.WIREFRAME);
            canvas3D.setWireframeChar('*');
            canvas3D.setWireframeColor(AnsiColor.GREEN);
            canvas3D.setBackfaceCulling(true);

            // Setup camera
            camera = canvas3D.getCamera();
            camera.setPosition(new Point3D(
                BigDecimal.valueOf(TERRAIN_SIZE / 2.0),
                BigDecimal.valueOf(15.0),
                BigDecimal.valueOf(TERRAIN_SIZE + 10.0)
            ));
            camera.lookAt(new Point3D(
                BigDecimal.valueOf(TERRAIN_SIZE / 2.0),
                BigDecimal.ZERO,
                BigDecimal.valueOf(TERRAIN_SIZE / 2.0)
            ));

            // Generate terrain
            generateTerrain();
            createTerrainMesh();

            // Create screen canvas
            ScreenCanvas screen = new ScreenCanvas(terminal, 80, 24);

            // Add info text
            Text infoText = new Text("infoText", 100, 1, "Terrain 3D Demo - BigDecimal Precision Engine");
            infoText.setPosition(2, 1);
            infoText.setForegroundColor(AnsiColor.CYAN);
            screen.addChild(infoText);

            Text controlsText = new Text("controlsText" ,100, 1, "Controls: SPACE=Toggle mode, R=Rotate camera, Q=Quit");
            controlsText.setPosition(2, 2);
            controlsText.setForegroundColor(AnsiColor.YELLOW);
            screen.addChild(controlsText);

            // Add 3D canvas
            canvas3D.setPosition(0, 4);
            screen.addChild(canvas3D);

            // Start interactive loop
            runInteractiveLoop(terminal, screen);

        } catch (IOException e) {
            log.error("Error running Terrain 3D demo", e);
        }
    }

    /**
     * Runs the interactive loop for the terrain demo.
     */
    private static void runInteractiveLoop(Terminal terminal, ScreenCanvas screen) throws IOException {
        boolean running = true;

        while (running) {
            // Clear and render screen

            Graphics graphics = new GeneralGraphics(screen.getWidth(), screen.getHeight());
            screen.paint(graphics);

            // Check for input (non-blocking)
            if (terminal.reader().ready()) {
                int ch = terminal.reader().read();

                switch (ch) {
                    case 'q':
                    case 'Q':
                    case 27: // ESC
                        running = false;
                        break;

                    case ' ': // SPACE - Toggle render mode
                        toggleRenderMode();
                        break;

                    case 'r':
                    case 'R': // Rotate camera
                        rotateCamera();
                        break;

                    case 'w':
                    case 'W': // Move camera forward
                        camera.moveForward(BigDecimal.valueOf(2.0));
                        break;

                    case 's':
                    case 'S': // Move camera backward
                        camera.moveForward(BigDecimal.valueOf(-2.0));
                        break;

                    case 'a':
                    case 'A': // Move camera left
                        camera.moveRight(BigDecimal.valueOf(-2.0));
                        break;

                    case 'd':
                    case 'D': // Move camera right
                        camera.moveRight(BigDecimal.valueOf(2.0));
                        break;
                }
            }

            // Small delay to prevent excessive CPU usage
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Toggles between wireframe and filled rendering modes.
     */
    private static void toggleRenderMode() {
        wireframeMode = !wireframeMode;
        if (wireframeMode) {
            canvas3D.setRenderMode(Graphic3DCanvas.RenderMode.WIREFRAME);
            canvas3D.setWireframeColor(AnsiColor.GREEN);
        } else {
            canvas3D.setRenderMode(Graphic3DCanvas.RenderMode.FILLED);
            canvas3D.setFillColor(AnsiColor.CYAN);
        }
        log.info("Switched to {} mode", wireframeMode ? "wireframe" : "filled");
    }

    /**
     * Rotates the camera around the terrain center.
     */
    private static void rotateCamera() {
        cameraAngle = cameraAngle.add(BigDecimal.valueOf(Math.PI / 8), MATH_CONTEXT);

        BigDecimal centerX = BigDecimal.valueOf(TERRAIN_SIZE / 2.0);
        BigDecimal centerZ = BigDecimal.valueOf(TERRAIN_SIZE / 2.0);
        BigDecimal radius = BigDecimal.valueOf(TERRAIN_SIZE + 10.0);

        // Calculate new camera position using trigonometry
        BigDecimal cosAngle = BigDecimal.valueOf(Math.cos(cameraAngle.doubleValue()));
        BigDecimal sinAngle = BigDecimal.valueOf(Math.sin(cameraAngle.doubleValue()));

        BigDecimal newX = centerX.add(radius.multiply(cosAngle, MATH_CONTEXT), MATH_CONTEXT);
        BigDecimal newZ = centerZ.add(radius.multiply(sinAngle, MATH_CONTEXT), MATH_CONTEXT);

        camera.setPosition(new Point3D(newX, BigDecimal.valueOf(15.0), newZ));
        camera.lookAt(new Point3D(centerX, BigDecimal.ZERO, centerZ));

        log.info("Camera rotated to angle: {}", cameraAngle.doubleValue());
    }

    /**
     * Generates a height map for the terrain using Perlin-like noise with BigDecimal precision.
     */
    private static void generateTerrain() {
        heightMap = new BigDecimal[TERRAIN_SIZE][TERRAIN_SIZE];
        Random random = new Random(12345); // Fixed seed for reproducible terrain

        log.info("Generating terrain with size {}x{}", TERRAIN_SIZE, TERRAIN_SIZE);

        // Generate simple noise-based terrain
        for (int x = 0; x < TERRAIN_SIZE; x++) {
            for (int z = 0; z < TERRAIN_SIZE; z++) {
                BigDecimal height = BigDecimal.ZERO;

                // Multiple octaves for more interesting terrain using BigDecimal arithmetic
                height = height.add(noise(x * 0.01, z * 0.01, random).multiply(BigDecimal.valueOf(20), MATH_CONTEXT), MATH_CONTEXT);
                height = height.add(noise(x * 0.02, z * 0.02, random).multiply(BigDecimal.valueOf(10), MATH_CONTEXT), MATH_CONTEXT);
                height = height.add(noise(x * 0.05, z * 0.05, random).multiply(BigDecimal.valueOf(5), MATH_CONTEXT), MATH_CONTEXT);
                height = height.add(noise(x * 0.1, z * 0.1, random).multiply(BigDecimal.valueOf(2), MATH_CONTEXT), MATH_CONTEXT);

                heightMap[x][z] = height.multiply(HEIGHT_SCALE, MATH_CONTEXT);
            }
        }

        log.info("Terrain generation completed");
    }

    /**
     * Simple noise function for terrain generation returning BigDecimal.
     */
    private static BigDecimal noise(double x, double z, Random random) {
        // Simple deterministic noise based on coordinates
        int seed = (int) ((x * 1000 + z * 1000000) % Integer.MAX_VALUE);
        random.setSeed(seed);
        return BigDecimal.valueOf((random.nextDouble() - 0.5) * 2.0);
    }

    /**
     * Creates a 3D mesh from the generated height map using BigDecimal coordinates.
     */
    private static void createTerrainMesh() {
        terrainMesh = new Mesh3D("terrain");

        log.info("Creating terrain mesh from height map");

        // Add vertices for each point in the height map
        for (int x = 0; x < TERRAIN_SIZE; x++) {
            for (int z = 0; z < TERRAIN_SIZE; z++) {
                Point3D vertex = new Point3D(
                    BigDecimal.valueOf(x),
                    heightMap[x][z],
                    BigDecimal.valueOf(z)
                );
                terrainMesh.addVertex(vertex);
            }
        }

        // Add triangular faces to create the terrain surface
        for (int x = 0; x < TERRAIN_SIZE - 1; x++) {
            for (int z = 0; z < TERRAIN_SIZE - 1; z++) {
                int topLeft = x * TERRAIN_SIZE + z;
                int topRight = x * TERRAIN_SIZE + (z + 1);
                int bottomLeft = (x + 1) * TERRAIN_SIZE + z;
                int bottomRight = (x + 1) * TERRAIN_SIZE + (z + 1);

                // Create two triangles for each quad
                // Triangle 1: top-left, bottom-left, top-right
                terrainMesh.addTriangle(topLeft, bottomLeft, topRight);

                // Triangle 2: top-right, bottom-left, bottom-right
                terrainMesh.addTriangle(topRight, bottomLeft, bottomRight);
            }
        }

        // Add the terrain mesh to the canvas
        canvas3D.clearMeshes();
        canvas3D.addMesh(terrainMesh);

        log.info("Terrain mesh created with {} vertices and {} faces",
                terrainMesh.getVertices().size(), terrainMesh.getFaces().size());
    }

    /**
     * Creates a colorful terrain mesh with height-based coloring.
     */
    private static void createColorfulTerrainMesh() {
        terrainMesh = new Mesh3D("colorful_terrain");

        // Add vertices
        for (int x = 0; x < TERRAIN_SIZE; x++) {
            for (int z = 0; z < TERRAIN_SIZE; z++) {
                Point3D vertex = new Point3D(
                    BigDecimal.valueOf(x),
                    heightMap[x][z],
                    BigDecimal.valueOf(z)
                );
                terrainMesh.addVertex(vertex);
            }
        }

        // Add colored triangular faces based on height
        for (int x = 0; x < TERRAIN_SIZE - 1; x++) {
            for (int z = 0; z < TERRAIN_SIZE - 1; z++) {
                int topLeft = x * TERRAIN_SIZE + z;
                int topRight = x * TERRAIN_SIZE + (z + 1);
                int bottomLeft = (x + 1) * TERRAIN_SIZE + z;
                int bottomRight = (x + 1) * TERRAIN_SIZE + (z + 1);

                // Determine color based on average height
                BigDecimal avgHeight = heightMap[x][z]
                    .add(heightMap[x][z + 1], MATH_CONTEXT)
                    .add(heightMap[x + 1][z], MATH_CONTEXT)
                    .add(heightMap[x + 1][z + 1], MATH_CONTEXT)
                    .divide(BigDecimal.valueOf(4), MATH_CONTEXT);

                AnsiColor color = getHeightBasedColor(avgHeight);

                // Create two triangles with color
                terrainMesh.addTriangle(topLeft, bottomLeft, topRight, color);
                terrainMesh.addTriangle(topRight, bottomLeft, bottomRight, color);
            }
        }

        canvas3D.clearMeshes();
        canvas3D.addMesh(terrainMesh);
    }

    /**
     * Returns a color based on terrain height for visualization.
     */
    private static AnsiColor getHeightBasedColor(BigDecimal height) {
        double h = height.doubleValue();

        if (h < -2.0) return AnsiColor.BLUE;      // Water/valleys
        if (h < 0.0) return AnsiColor.CYAN;       // Low areas
        if (h < 2.0) return AnsiColor.GREEN;      // Plains
        if (h < 4.0) return AnsiColor.YELLOW;     // Hills
        if (h < 6.0) return AnsiColor.RED;        // Mountains
        return AnsiColor.WHITE;                   // Peaks
    }
}
