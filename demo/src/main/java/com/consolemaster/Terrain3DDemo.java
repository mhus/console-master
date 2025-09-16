package com.consolemaster;

import com.consolemaster.graphic3d.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Random;

/**
 * Interactive 3D terrain demo showcasing a large terrain with camera navigation.
 * Features a 1000x1000 terrain grid with height variations and smooth camera controls.
 */
@Slf4j
public class Terrain3DDemo {

    private static final int TERRAIN_SIZE = 1000;
    private static final double TERRAIN_SCALE = 0.1; // Scale down for better visibility
    private static final double HEIGHT_SCALE = 10.0; // Height variation
    private static final double CAMERA_MOVE_SPEED = 2.0;
    private static final double CAMERA_ROTATE_SPEED = 0.05; // Radians per key press

    private static Camera3D camera;
    private static Graphic3DCanvas canvas3D;
    private static Graphic3DCanvas.RenderMode currentRenderMode = Graphic3DCanvas.RenderMode.WIREFRAME;
    private static String lastAction = "Demo Started";
    private static double cameraYaw = 0.0; // Horizontal rotation
    private static double cameraPitch = 0.0; // Vertical rotation (limited)

    // Terrain data
    private static double[][] heightMap;
    private static Mesh3D terrainMesh;

    public static void main(String[] args) {
        try {
            // Create the main screen canvas
            ScreenCanvas screen = new ScreenCanvas(120, 40);

            // Create main container with BorderLayout
            Composite mainContainer = new Composite("mainContainer",
                    screen.getWidth() - 2,
                    screen.getHeight() - 2,
                    new BorderLayout(1));

            // Create header
            Box headerBox = new Box("headerBox", 0, 3, new DefaultBorder());
            Text headerText = new Text("headerText", 0, 0, "3D Terrain Demo - Navigate with Arrow Keys", Text.Alignment.CENTER);
            headerText.setForegroundColor(AnsiColor.BRIGHT_GREEN);
            headerText.setBold(true);
            headerBox.setChild(headerText);
            headerBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.TOP_CENTER));

            // Create 3D canvas
            canvas3D = new Graphic3DCanvas("3D Terrain", 100, 30);
            canvas3D.setRenderMode(currentRenderMode);
            canvas3D.setWireframeChar('.');
            canvas3D.setWireframeColor(AnsiColor.GREEN);
            canvas3D.setFillChar('▓');
            canvas3D.setFillColor(AnsiColor.BRIGHT_GREEN);
            canvas3D.setBackfaceCulling(true);

            // Setup camera
            camera = canvas3D.getCamera();
            // Start camera high above the terrain center
            camera.setPosition(new Point3D(0, 50, 0));
            updateCameraDirection();

            // Generate and create terrain
            System.out.println("Generating terrain...");
            generateTerrain();
            createTerrainMesh();
            canvas3D.addMesh(terrainMesh);
            System.out.println("Terrain generated with " + terrainMesh.getVertices().size() + " vertices and " + terrainMesh.getFaces().size() + " faces");

            // Wrap 3D canvas in a box for better presentation
            Box canvas3DBox = new Box("canvas3DBox", canvas3D.getWidth() + 2, canvas3D.getHeight() + 2, new DefaultBorder());
            canvas3DBox.setChild(canvas3D);
            canvas3DBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER));

            // Create status footer
            Box statusBox = new Box("statusBox", 0, 8, new DefaultBorder());
            Text statusText = new Text("statusText", 0, 0, "", Text.Alignment.CENTER);
            updateStatusText(statusText);
            statusBox.setChild(statusText);
            statusBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.BOTTOM_CENTER));

            // Add components to main container
            mainContainer.addChild(headerBox);
            mainContainer.addChild(canvas3DBox);
            mainContainer.addChild(statusBox);

            // Set content
            screen.setContent(mainContainer);

            // Create process loop
            ProcessLoop processLoop = new ProcessLoop(screen);
            processLoop.setTargetFPS(30); // 30 FPS for smooth navigation

            // Register keyboard controls
            screen.registerShortcut("Q", () -> {
                try {
                    processLoop.stop();
                } catch (IOException e) {
                    System.err.println("Error stopping process loop: " + e.getMessage());
                }
            });

            // Render mode controls
            screen.registerShortcut("W", () -> {
                currentRenderMode = Graphic3DCanvas.RenderMode.WIREFRAME;
                canvas3D.setRenderMode(currentRenderMode);
                lastAction = "Wireframe Mode";
            });

            screen.registerShortcut("F", () -> {
                currentRenderMode = Graphic3DCanvas.RenderMode.FILLED;
                canvas3D.setRenderMode(currentRenderMode);
                lastAction = "Filled Mode";
            });

            screen.registerShortcut("B", () -> {
                currentRenderMode = Graphic3DCanvas.RenderMode.BOTH;
                canvas3D.setRenderMode(currentRenderMode);
                lastAction = "Both Mode";
            });

            // Camera movement controls - Arrow keys for rotation, WASD for movement
            screen.registerShortcut(KeyEvent.SpecialKey.ARROW_LEFT.name(), () -> {
                cameraYaw -= CAMERA_ROTATE_SPEED;
                updateCameraDirection();
                lastAction = "Rotate Left";
            });

            screen.registerShortcut(KeyEvent.SpecialKey.ARROW_RIGHT.name(), () -> {
                cameraYaw += CAMERA_ROTATE_SPEED;
                updateCameraDirection();
                lastAction = "Rotate Right";
            });

            screen.registerShortcut(KeyEvent.SpecialKey.ARROW_UP.name(), () -> {
                Point3D pos = camera.getPosition();
                Point3D forward = getForwardVector();
                camera.setPosition(new Point3D(
                    pos.getX() + forward.getX() * CAMERA_MOVE_SPEED,
                    pos.getY() + forward.getY() * CAMERA_MOVE_SPEED,
                    pos.getZ() + forward.getZ() * CAMERA_MOVE_SPEED
                ));
                updateCameraDirection();
                lastAction = "Move Forward";
            });

            screen.registerShortcut(KeyEvent.SpecialKey.ARROW_DOWN.name(), () -> {
                Point3D pos = camera.getPosition();
                Point3D forward = getForwardVector();
                camera.setPosition(new Point3D(
                    pos.getX() - forward.getX() * CAMERA_MOVE_SPEED,
                    pos.getY() - forward.getY() * CAMERA_MOVE_SPEED,
                    pos.getZ() - forward.getZ() * CAMERA_MOVE_SPEED
                ));
                updateCameraDirection();
                lastAction = "Move Backward";
            });

            // Additional controls for vertical movement
            screen.registerShortcut(KeyEvent.SpecialKey.PAGE_UP.name(), () -> {
                Point3D pos = camera.getPosition();
                camera.setPosition(new Point3D(pos.getX(), pos.getY() + CAMERA_MOVE_SPEED, pos.getZ()));
                updateCameraDirection();
                lastAction = "Move Up";
            });

            screen.registerShortcut(KeyEvent.SpecialKey.PAGE_DOWN.name(), () -> {
                Point3D pos = camera.getPosition();
                camera.setPosition(new Point3D(pos.getX(), pos.getY() - CAMERA_MOVE_SPEED, pos.getZ()));
                updateCameraDirection();
                lastAction = "Move Down";
            });

            // Reset camera
            screen.registerShortcut("R", () -> {
                camera.setPosition(new Point3D(0, 50, 0));
                cameraYaw = 0.0;
                cameraPitch = 0.0;
                updateCameraDirection();
                lastAction = "Camera Reset";
            });

            // Regenerate terrain
            screen.registerShortcut("G", () -> {
                canvas3D.clearMeshes();
                generateTerrain();
                createTerrainMesh();
                canvas3D.addMesh(terrainMesh);
                lastAction = "Terrain Regenerated";
            });

            // Update callback for status display
            processLoop.setUpdateCallback(() -> {
                updateStatusText((Text) statusBox.getChild());
            });

            System.out.println("Starting Terrain 3D Demo...");
            System.out.println("Controls:");
            System.out.println("- Arrow Keys: Left/Right = Rotate camera, Up/Down = Move forward/backward");
            System.out.println("- Page Up/Down: Move camera up/down");
            System.out.println("- W/F/B: Switch render modes (Wireframe/Filled/Both)");
            System.out.println("- R: Reset camera position");
            System.out.println("- G: Generate new terrain");
            System.out.println("- Q: Quit");

            // Start the process loop (this will block until stopped)
            processLoop.start();

            System.out.println("Terrain 3D Demo ended.");

        } catch (IOException e) {
            log.error("Error running Terrain 3D demo", e);
        }
    }

    /**
     * Generates a height map for the terrain using Perlin-like noise.
     */
    private static void generateTerrain() {
        heightMap = new double[TERRAIN_SIZE][TERRAIN_SIZE];
        Random random = new Random(12345); // Fixed seed for reproducible terrain

        // Generate simple noise-based terrain
        for (int x = 0; x < TERRAIN_SIZE; x++) {
            for (int z = 0; z < TERRAIN_SIZE; z++) {
                double height = 0;

                // Multiple octaves for more interesting terrain
                height += noise(x * 0.01, z * 0.01, random) * 20;
                height += noise(x * 0.02, z * 0.02, random) * 10;
                height += noise(x * 0.05, z * 0.05, random) * 5;
                height += noise(x * 0.1, z * 0.1, random) * 2;

                heightMap[x][z] = height * HEIGHT_SCALE;
            }
        }
    }

    /**
     * Simple noise function for terrain generation.
     */
    private static double noise(double x, double z, Random random) {
        // Simple pseudo-random noise based on coordinates
        int n = (int)(x * 1000 + z * 1000000);
        random.setSeed(n);
        return (random.nextDouble() - 0.5) * 2;
    }

    /**
     * Creates the terrain mesh from the height map.
     */
    private static void createTerrainMesh() {
        terrainMesh = new Mesh3D("Terrain");

        // Create vertices
        int[][] vertexIndices = new int[TERRAIN_SIZE][TERRAIN_SIZE];
        for (int x = 0; x < TERRAIN_SIZE; x++) {
            for (int z = 0; z < TERRAIN_SIZE; z++) {
                double worldX = (x - TERRAIN_SIZE / 2.0) * TERRAIN_SCALE;
                double worldZ = (z - TERRAIN_SIZE / 2.0) * TERRAIN_SCALE;
                double worldY = heightMap[x][z];

                Point3D vertex = new Point3D(worldX, worldY, worldZ);
                vertexIndices[x][z] = terrainMesh.addVertex(vertex);
            }
        }

        // Create triangular faces - use every 10th point to reduce complexity
        int step = 10; // Only create faces for every 10th point to reduce triangle count
        for (int x = 0; x < TERRAIN_SIZE - step; x += step) {
            for (int z = 0; z < TERRAIN_SIZE - step; z += step) {
                // Create two triangles for each quad
                int v1 = vertexIndices[x][z];
                int v2 = vertexIndices[x + step][z];
                int v3 = vertexIndices[x][z + step];
                int v4 = vertexIndices[x + step][z + step];

                // Determine color based on height
                double avgHeight = (heightMap[x][z] + heightMap[x + step][z] +
                                  heightMap[x][z + step] + heightMap[x + step][z + step]) / 4.0;
                AnsiColor color = getHeightColor(avgHeight);

                // First triangle
                terrainMesh.addTriangle(v1, v2, v3, color);
                // Second triangle
                terrainMesh.addTriangle(v2, v4, v3, color);
            }
        }
    }

    /**
     * Returns a color based on the height value.
     */
    private static AnsiColor getHeightColor(double height) {
        if (height < HEIGHT_SCALE * 5) {
            return AnsiColor.BLUE; // Water/low areas
        } else if (height < HEIGHT_SCALE * 15) {
            return AnsiColor.GREEN; // Grass/medium areas
        } else if (height < HEIGHT_SCALE * 25) {
            return AnsiColor.YELLOW; // Hills
        } else {
            return AnsiColor.WHITE; // Mountains/high areas
        }
    }

    /**
     * Updates the camera direction based on yaw and pitch angles.
     */
    private static void updateCameraDirection() {
        Point3D pos = camera.getPosition();
        Point3D forward = getForwardVector();
        Point3D target = new Point3D(
            pos.getX() + forward.getX(),
            pos.getY() + forward.getY(),
            pos.getZ() + forward.getZ()
        );
        camera.lookAt(target);
    }

    /**
     * Calculates the forward vector based on camera yaw and pitch.
     */
    private static Point3D getForwardVector() {
        double x = Math.sin(cameraYaw) * Math.cos(cameraPitch);
        double y = -Math.sin(cameraPitch);
        double z = Math.cos(cameraYaw) * Math.cos(cameraPitch);
        return new Point3D(x, y, z);
    }

    /**
     * Updates the status text with current information.
     */
    private static void updateStatusText(Text statusText) {
        Point3D pos = camera.getPosition();
        String status = String.format(
            "Camera: (%.1f, %.1f, %.1f) | Yaw: %.1f° | Mode: %s | Action: %s\n" +
            "Controls: ←→ Rotate | ↑↓ Move | PgUp/PgDn Height | W/F/B Modes | R Reset | G Regenerate | Q Quit\n" +
            "Terrain: %dx%d grid with %d vertices, %d faces",
            pos.getX(), pos.getY(), pos.getZ(),
            Math.toDegrees(cameraYaw),
            currentRenderMode.name(),
            lastAction,
            TERRAIN_SIZE, TERRAIN_SIZE,
            terrainMesh != null ? terrainMesh.getVertices().size() : 0,
            terrainMesh != null ? terrainMesh.getFaces().size() : 0
        );
        statusText.setText(status);
    }
}
