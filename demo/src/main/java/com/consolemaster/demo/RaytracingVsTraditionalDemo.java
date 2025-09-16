package com.consolemaster.demo;

import com.consolemaster.AnsiColor;
import com.consolemaster.BorderLayout;
import com.consolemaster.Box;
import com.consolemaster.BoxLayout;
import com.consolemaster.Composite;
import com.consolemaster.DefaultBorder;
import com.consolemaster.PositionConstraint;
import com.consolemaster.ProcessLoop;
import com.consolemaster.Ruler;
import com.consolemaster.ScreenCanvas;
import com.consolemaster.Text;
import com.consolemaster.graphic3d.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Demo application that showcases the new raytracing-based 3D rendering system.
 * This demo compares the traditional rasterization approach with the new raytracing approach.
 */
@Slf4j
public class RaytracingVsTraditionalDemo {

    private static boolean antiAliasingEnabled = false;

    public static void main(String[] args) {
        try {
            // Create the main screen canvas
            ScreenCanvas screen = new ScreenCanvas(120, 40);

            // Create main container
            Composite mainContainer = new Composite("mainContainer",
                    screen.getWidth() - 2,
                    screen.getHeight() - 2,
                    new BorderLayout(1));

            // Create header
            Box headerBox = new Box("headerBox", 0, 3, new DefaultBorder());
            Text headerText = new Text("headerText", 0, 0, "Raytracing vs Traditional 3D Rendering", Text.Alignment.CENTER);
            headerText.setForegroundColor(AnsiColor.BRIGHT_CYAN);
            headerText.setBold(true);
            headerBox.setContent(headerText);
            headerBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.TOP_CENTER));

            // Create side-by-side container for canvases
            Composite canvasContainer = new Composite("canvasContainer",
                    30,
                    30,
                    new BoxLayout(BoxLayout.Direction.HORIZONTAL));

            canvasContainer.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER));

            // Create raytracing canvas
            RaytracingGraphic3DCanvas raytracingCanvas = new RaytracingGraphic3DCanvas("Raytracing Scene", 55, 28);
            setupRaytracingCanvas(raytracingCanvas);

            // Create traditional canvas for comparison
            Graphic3DCanvas traditionalCanvas = new Graphic3DCanvas("Traditional Scene", 55, 28);
            setupTraditionalCanvas(traditionalCanvas);

            // Set up identical scenes in both canvases
            setupScene(raytracingCanvas);
            setupScene(traditionalCanvas);

            // Position cameras for interesting view
            setupCamera(raytracingCanvas.getCamera());
            setupCamera(traditionalCanvas.getCamera());

            // Add canvases to container
            canvasContainer.addChild(raytracingCanvas);
            canvasContainer.addChild(new Ruler("ruler",Ruler.Orientation.VERTICAL));
            canvasContainer.addChild(traditionalCanvas);

            // Create footer with instructions
            Text footerText = new Text("footerText", 0, 1,
                "Left: Raytracing | Right: Traditional | A: Toggle Anti-aliasing | Ctrl+Q: Exit",
                Text.Alignment.CENTER);
            footerText.setForegroundColor(AnsiColor.BRIGHT_YELLOW);

            // Add components to main container
            mainContainer.addChild(headerBox);
            mainContainer.addChild(canvasContainer);
            mainContainer.addChild(footerText);

            // Set content and initialize
            screen.setContent(mainContainer);

            // Create process loop for interactive display
            ProcessLoop processLoop = new ProcessLoop(screen);
            processLoop.setTargetFPS(30); // Lower FPS for raytracing performance

            log.info("Starting Raytracing Demo...");

            // Register shortcuts
            screen.registerShortcut("Q", () -> {
                try {
                    processLoop.stop();
                } catch (IOException e) {
                    log.error("Error stopping process loop", e);
                }
            });

            screen.registerShortcut("A", () -> {
                antiAliasingEnabled = !antiAliasingEnabled;
                raytracingCanvas.setEnableAntiAliasing(antiAliasingEnabled);
                log.info("Anti-aliasing toggled: {}", antiAliasingEnabled);

                // Update footer text to show current state
                String status = antiAliasingEnabled ? "ON" : "OFF";
                footerText.setText("Left: Raytracing | Right: Traditional | Anti-aliasing: " + status + " | Ctrl+Q: Exit");
            });

            System.out.println("Starting Raytracing Demo...");
            System.out.println("Press 'A' to toggle anti-aliasing, Ctrl+Q to quit");

            // Start the process loop (this will block until stopped)
            processLoop.start();

            System.out.println("Raytracing Demo ended.");

        } catch (IOException e) {
            log.error("Error creating screen canvas", e);
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error", e);
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Configures the raytracing canvas.
     */
    private static void setupRaytracingCanvas(RaytracingGraphic3DCanvas canvas) {
        canvas.setLightDirection(new Point3D(-1, -1, -1).normalize());
        canvas.setLightIntensity(0.8);
        canvas.setBackgroundColor(AnsiColor.BLACK);
        canvas.setBackgroundChar('.');
        canvas.setFieldOfView(Math.PI / 3.0); // 60 degrees
        canvas.setEnableAntiAliasing(false);
        canvas.setAntiAliasingRays(4);
    }

    /**
     * Configures the traditional canvas.
     */
    private static void setupTraditionalCanvas(Graphic3DCanvas canvas) {
        canvas.setRenderMode(Graphic3DCanvas.RenderMode.FILLED);
        canvas.setFillColor(AnsiColor.CYAN);
        canvas.setWireframeColor(AnsiColor.WHITE);
        canvas.setBackfaceCulling(true);
    }

    /**
     * Sets up the 3D scene with various objects.
     */
    private static void setupScene(RaytracingGraphic3DCanvas canvas) {
        setupSceneObjects(canvas::addMesh);
    }

    private static void setupScene(Graphic3DCanvas canvas) {
        setupSceneObjects(canvas::addMesh);
    }

    /**
     * Creates and adds 3D objects to the scene.
     */
    private static void setupSceneObjects(java.util.function.Consumer<Mesh3D> addMesh) {
        // Create a cube
        Mesh3D cube = Mesh3D.createCube(2.0);
        cube = cube.transform(Matrix4x4.translation(-2, 0, 0));
        addMesh.accept(cube);

        // Create a pyramid
        Mesh3D pyramid = Mesh3D.createPyramid(2.0);
        pyramid = pyramid.transform(Matrix4x4.translation(2, 0, 0));
        addMesh.accept(pyramid);

        // Create a rotated cube
        Matrix4x4 rotation = Matrix4x4.rotationY(Math.PI / 4.0)
            .multiply(Matrix4x4.rotationX(Math.PI / 6.0));
        Mesh3D rotatedCube = Mesh3D.createCube(1.5);
        rotatedCube = rotatedCube.transform(rotation);
        rotatedCube = rotatedCube.transform(Matrix4x4.translation(0, 2, -1));
        addMesh.accept(rotatedCube);
    }

    /**
     * Sets up the camera position and orientation.
     */
    private static void setupCamera(Camera3D camera) {
        // Position camera to get a good view of the scene
        camera.setPosition(new Point3D(0, 1, 5));
        camera.setRotation(new Point3D(-0.1, 0, 0)); // Slight downward tilt
    }
}
