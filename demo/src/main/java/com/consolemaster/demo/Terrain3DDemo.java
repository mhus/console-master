package com.consolemaster.demo;

import com.consolemaster.AnsiColor;
import com.consolemaster.AnsiFormat;
import com.consolemaster.BorderLayout;
import com.consolemaster.Canvas;
import com.consolemaster.Composite;
import com.consolemaster.Graphics;
import com.consolemaster.PositionConstraint;
import com.consolemaster.ScreenCanvas;
import com.consolemaster.graphic3d.*;
import java.math.BigDecimal;
import java.io.IOException;

/**
 * Demo application showcasing 3D terrain rendering using the console framework.
 * Generates a procedural terrain and displays it in wireframe mode.
 */
public class Terrain3DDemo {
    private static final int TERRAIN_SIZE = 20;
    private static final double TERRAIN_HEIGHT = 5.0;

    public static void main(String[] args) {
        try {
            // Create the main screen canvas with minimum size
            ScreenCanvas screen = new ScreenCanvas(80, 30);

            // Create main container with BorderLayout
            Composite mainContainer = new Composite("mainContainer",0,0,
                    new BorderLayout(1));

            // Create header
            Canvas header = new Canvas("header", 0, 3) {
                @Override
                public void paint(Graphics graphics) {
                    graphics.setForegroundColor(AnsiColor.BRIGHT_CYAN);
                    graphics.setFormats(AnsiFormat.BOLD);
                    String title = "=== 3D Terrain Demo ===";
                    int centerX = getWidth() / 2 - title.length() / 2;
                    graphics.drawString(centerX, 0, title);

                    graphics.resetStyle();
                    graphics.setForegroundColor(AnsiColor.GREEN);
                    String subtitle = "Procedural terrain rendering in wireframe";
                    centerX = getWidth() / 2 - subtitle.length() / 2;
                    graphics.drawString(centerX, 1, subtitle);

                    graphics.setForegroundColor(AnsiColor.WHITE);
                    graphics.drawHorizontalLine(0, getWidth() - 1, 2, '=');
                }
            };

            // Create 3D canvas for terrain
            Graphic3DCanvas canvas3D = new Graphic3DCanvas("Terrain3D",
                                                         screen.getWidth(),
                                                         screen.getHeight() - 6);

            // Configure 3D rendering
            canvas3D.setRenderMode(Graphic3DCanvas.RenderMode.WIREFRAME);
            canvas3D.setWireframeChar('*');
            canvas3D.setWireframeColor(AnsiColor.GREEN);
            canvas3D.setBackfaceCulling(true);

            // Setup camera position
            Camera3D camera = canvas3D.getCamera();
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

            // Generate and add terrain mesh
            Mesh3D terrainMesh = generateTerrain();
            canvas3D.addMesh(terrainMesh);

            // Create controls info
            Canvas controlsInfo = new Canvas("controls", screen.getWidth(), 3) {
                @Override
                public void paint(Graphics graphics) {
                    graphics.setForegroundColor(AnsiColor.YELLOW);
                    graphics.drawString(0, 0, "Terrain Size: " + TERRAIN_SIZE + "x" + TERRAIN_SIZE);
                    graphics.drawString(0, 1, "Height Variation: " + TERRAIN_HEIGHT);
                    graphics.setForegroundColor(AnsiColor.CYAN);
                    graphics.drawString(0, 2, "Press any key to exit...");
                }
            };

            // Set positions and add to screen (ScreenCanvas extends Composite)
            header.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.TOP_CENTER));
            canvas3D.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER));
            controlsInfo.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.BOTTOM_CENTER));

            mainContainer.addChild(header);
            mainContainer.addChild(canvas3D);
            mainContainer.addChild(controlsInfo);

            screen.setContent(mainContainer);

            // Render and display
            screen.render();

            // Wait for user input (suppress unused result warning)
            //noinspection ResultOfMethodCallIgnored
            System.in.read();

        } catch (IOException e) {
            System.err.println("Error running Terrain3D Demo: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Generates a procedural terrain mesh using a simple noise function.
     */
    private static Mesh3D generateTerrain() {
        Mesh3D terrain = new Mesh3D("TerrainMesh");

        // Generate vertices with height variation
        Point3D[][] vertices = new Point3D[TERRAIN_SIZE + 1][TERRAIN_SIZE + 1];

        for (int x = 0; x <= TERRAIN_SIZE; x++) {
            for (int z = 0; z <= TERRAIN_SIZE; z++) {
                // Simple procedural height generation
                double height = generateHeight(x, z);
                vertices[x][z] = new Point3D(
                    BigDecimal.valueOf(x),
                    BigDecimal.valueOf(height),
                    BigDecimal.valueOf(z)
                );
                terrain.addVertex(vertices[x][z]);
            }
        }

        // Generate triangular faces for the terrain
        for (int x = 0; x < TERRAIN_SIZE; x++) {
            for (int z = 0; z < TERRAIN_SIZE; z++) {
                // Each grid square becomes two triangles
                int topLeft = x * (TERRAIN_SIZE + 1) + z;
                int topRight = topLeft + 1;
                int bottomLeft = (x + 1) * (TERRAIN_SIZE + 1) + z;
                int bottomRight = bottomLeft + 1;

                // First triangle (top-left, bottom-left, top-right)
                terrain.addTriangle(topLeft, bottomLeft, topRight);

                // Second triangle (top-right, bottom-left, bottom-right)
                terrain.addTriangle(topRight, bottomLeft, bottomRight);
            }
        }

        return terrain;
    }

    /**
     * Simple height generation function using multiple sine waves for terrain variation.
     */
    private static double generateHeight(int x, int z) {
        double normalizedX = x / (double) TERRAIN_SIZE;
        double normalizedZ = z / (double) TERRAIN_SIZE;

        // Combine multiple sine waves for more interesting terrain
        double height = 0.0;

        // Large scale variation
        height += Math.sin(normalizedX * Math.PI * 2) * TERRAIN_HEIGHT * 0.5;
        height += Math.cos(normalizedZ * Math.PI * 2) * TERRAIN_HEIGHT * 0.5;

        // Medium scale variation
        height += Math.sin(normalizedX * Math.PI * 6) * TERRAIN_HEIGHT * 0.3;
        height += Math.cos(normalizedZ * Math.PI * 6) * TERRAIN_HEIGHT * 0.3;

        // Small scale noise
        height += Math.sin(normalizedX * Math.PI * 12 + normalizedZ * Math.PI * 8) * TERRAIN_HEIGHT * 0.2;

        return height;
    }
}
