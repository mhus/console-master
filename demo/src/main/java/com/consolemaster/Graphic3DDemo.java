package com.consolemaster;

import com.consolemaster.graphic3d.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Interactive 3D graphics demo showcasing the Graphic3DCanvas capabilities.
 * Features animated 3D objects, camera controls, and different rendering modes.
 */
@Slf4j
public class Graphic3DDemo {

    private static double animationTime = 0.0;
    private static boolean isAnimating = true;
    private static Graphic3DCanvas.RenderMode currentRenderMode = Graphic3DCanvas.RenderMode.WIREFRAME;
    private static String lastAction = "Demo Started";
    private static Camera3D camera;
    private static Graphic3DCanvas canvas3D;

    public static void main(String[] args) {
        try {
            // Create the main screen canvas
            ScreenCanvas screen = new ScreenCanvas(100, 35);

            // Create main container with BorderLayout
            Composite mainContainer = new Composite("mainContainer",
                    screen.getWidth() - 2,
                    screen.getHeight() - 2,
                    new BorderLayout(1));

            // Create header
            Box headerBox = new Box("headerBox", 0, 3, new DefaultBorder());
            Text headerText = new Text("headerText", 0, 0, "3D Graphics Demo - Interactive 3D Scene", Text.Alignment.CENTER);
            headerText.setForegroundColor(AnsiColor.BRIGHT_CYAN);
            headerText.setBold(true);
            headerBox.setChild(headerText);
            headerBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.TOP_CENTER));

            // Create 3D canvas
            canvas3D = new Graphic3DCanvas("3D Scene", 80, 25);
            canvas3D.setRenderMode(currentRenderMode);
            canvas3D.setWireframeChar('*');
            canvas3D.setWireframeColor(AnsiColor.CYAN);
            canvas3D.setFillChar('#');
            canvas3D.setFillColor(AnsiColor.GREEN);
            canvas3D.setBackfaceCulling(true);

            // Setup camera
            camera = canvas3D.getCamera();
            camera.setPosition(new Point3D(0, 0, 8)); // Y=0 statt Y=2 für bessere Zentrierung
            camera.lookAt(new Point3D(0, 0, 0));

            // Create initial 3D scene
            createInitialScene();

            // Wrap 3D canvas in a box for better presentation
            Box canvas3DBox = new Box("canvas3DBox", canvas3D.getWidth() + 2, canvas3D.getHeight() + 2, new DefaultBorder());
            canvas3DBox.setChild(canvas3D);
            canvas3DBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER));

            // Create control panel
            Composite controlPanel = new Composite("controlPanel", 0, 0, new FlowLayout(2, 1));

            // Render mode buttons
            Box wireframeBtn = createControlButton("Wireframe\n(W)", AnsiColor.CYAN, () -> {
                currentRenderMode = Graphic3DCanvas.RenderMode.WIREFRAME;
                canvas3D.setRenderMode(currentRenderMode);
                lastAction = "Switched to Wireframe";
            });
            controlPanel.addChild(wireframeBtn);

            Box filledBtn = createControlButton("Filled\n(F)", AnsiColor.GREEN, () -> {
                currentRenderMode = Graphic3DCanvas.RenderMode.FILLED;
                canvas3D.setRenderMode(currentRenderMode);
                lastAction = "Switched to Filled";
            });
            controlPanel.addChild(filledBtn);

            Box bothBtn = createControlButton("Both\n(B)", AnsiColor.YELLOW, () -> {
                currentRenderMode = Graphic3DCanvas.RenderMode.BOTH;
                canvas3D.setRenderMode(currentRenderMode);
                lastAction = "Switched to Both";
            });
            controlPanel.addChild(bothBtn);

            Box animateBtn = createControlButton("Animation\n(SPACE)", AnsiColor.MAGENTA, () -> {
                isAnimating = !isAnimating;
                lastAction = isAnimating ? "Animation Started" : "Animation Paused";
            });
            controlPanel.addChild(animateBtn);

            controlPanel.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.BOTTOM_CENTER));

            // Create status footer
            Box statusBox = new Box("statusBox", 0, 4, new DefaultBorder());
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
            processLoop.setTargetFPS(30); // 30 FPS for smooth 3D animation

            // Register keyboard controls
            screen.registerShortcut("Ctrl+Q", () -> {
                try {
                    processLoop.stop();
                } catch (IOException e) {
                    System.err.println("Error stopping process loop: " + e.getMessage());
                }
            });

            // 3D Controls
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

            screen.registerShortcut(" ", () -> {
                isAnimating = !isAnimating;
                lastAction = isAnimating ? "Animation Started" : "Animation Paused";
            });

            // Camera controls
            screen.registerShortcut("Up", () -> {
                camera.moveForward(0.5);
                lastAction = "Camera Forward";
            });

            screen.registerShortcut("Down", () -> {
                camera.moveForward(-0.5);
                lastAction = "Camera Backward";
            });

            screen.registerShortcut("Left", () -> {
                camera.moveRight(-0.5);
                lastAction = "Camera Left";
            });

            screen.registerShortcut("Right", () -> {
                camera.moveRight(0.5);
                lastAction = "Camera Right";
            });

            screen.registerShortcut("PageUp", () -> {
                camera.moveUp(0.5);
                lastAction = "Camera Up";
            });

            screen.registerShortcut("PageDown", () -> {
                camera.moveUp(-0.5);
                lastAction = "Camera Down";
            });

            screen.registerShortcut("R", () -> {
                // Reset camera position
                camera.setPosition(new Point3D(0, 0, 8));
                camera.lookAt(new Point3D(0, 0, 0));
                lastAction = "Camera Reset";
            });

            // Animation update callback
            processLoop.setUpdateCallback(() -> {
                if (isAnimating) {
                    animationTime += 0.03; // Animation speed
                    updateAnimatedScene();
                }
                updateStatusText((Text) statusBox.getChild(), processLoop);
            });

            System.out.println("Starting 3D Graphics Demo...");
            System.out.println("3D Controls:");
            System.out.println("- W/F/B: Switch render modes (Wireframe/Filled/Both)");
            System.out.println("- SPACE: Toggle animation");
            System.out.println("- Arrow Keys: Move camera (Forward/Back/Left/Right)");
            System.out.println("- Page Up/Down: Move camera (Up/Down)");
            System.out.println("- R: Reset camera position");
            System.out.println("- ESC or Ctrl+Q: Quit");

            // Start the process loop (this will block until stopped)
            processLoop.start();

            System.out.println("3D Graphics Demo ended.");

        } catch (IOException e) {
            log.error("Error running 3D Graphics demo", e);
        }
    }

    /**
     * Creates the initial 3D scene with various objects.
     */
    private static void createInitialScene() {
        canvas3D.clearMeshes();

        // Create a cube at the origin
        Mesh3D cube = Mesh3D.createCube(2.0);
        canvas3D.addMesh(cube);

        // Create a pyramid to the right
        Mesh3D pyramid = Mesh3D.createPyramid(1.5);
        Matrix4x4 pyramidTransform = Matrix4x4.translation(4, 0, 0);
        Mesh3D transformedPyramid = pyramid.transform(pyramidTransform);
        canvas3D.addMesh(transformedPyramid);

        // Create a rotated cube to the left
        Mesh3D leftCube = Mesh3D.createCube(1.0);
        Matrix4x4 rotation = Matrix4x4.rotationY(Math.PI / 4).multiply(Matrix4x4.rotationX(Math.PI / 6));
        Matrix4x4 translation = Matrix4x4.translation(-4, 0, 0);
        Matrix4x4 leftCubeTransform = translation.multiply(rotation);
        Mesh3D transformedLeftCube = leftCube.transform(leftCubeTransform);
        canvas3D.addMesh(transformedLeftCube);
    }

    /**
     * Updates the animated 3D scene.
     */
    private static void updateAnimatedScene() {
        canvas3D.clearMeshes();

        // Rotating cube at center
        Mesh3D cube = Mesh3D.createCube(2.0);
        Matrix4x4 cubeRotation = Matrix4x4.rotationY(animationTime)
                .multiply(Matrix4x4.rotationX(animationTime * 0.7));
        Mesh3D animatedCube = cube.transform(cubeRotation);
        canvas3D.addMesh(animatedCube);

        // Orbiting pyramid
        double orbitRadius = 4.0;
        double orbitX = Math.cos(animationTime * 2) * orbitRadius;
        double orbitZ = Math.sin(animationTime * 2) * orbitRadius;
        double orbitY = Math.sin(animationTime * 3) * 1.5;

        Mesh3D pyramid = Mesh3D.createPyramid(1.5);
        Matrix4x4 pyramidRotation = Matrix4x4.rotationY(-animationTime * 2);
        Matrix4x4 pyramidTranslation = Matrix4x4.translation(orbitX, orbitY, orbitZ);
        Matrix4x4 pyramidTransform = pyramidTranslation.multiply(pyramidRotation);
        Mesh3D orbitingPyramid = pyramid.transform(pyramidTransform);
        canvas3D.addMesh(orbitingPyramid);

        // Oscillating cube on the left
        double leftX = -4 + Math.sin(animationTime * 1.5) * 1.5;
        double leftY = Math.cos(animationTime * 2) * 2;

        Mesh3D leftCube = Mesh3D.createCube(1.0);
        Matrix4x4 leftRotation = Matrix4x4.rotationZ(animationTime * 1.2)
                .multiply(Matrix4x4.rotationX(animationTime * 0.8));
        Matrix4x4 leftTranslation = Matrix4x4.translation(leftX, leftY, 0);
        Matrix4x4 leftTransform = leftTranslation.multiply(leftRotation);
        Mesh3D animatedLeftCube = leftCube.transform(leftTransform);
        canvas3D.addMesh(animatedLeftCube);
    }

    /**
     * Creates a control button for the demo.
     */
    private static Box createControlButton(String text, AnsiColor color, Runnable action) {
        Box button = new Box("btn:" + text, 12, 3, new DefaultBorder()) {
            @Override
            protected void onFocusChanged(boolean focused) {
                super.onFocusChanged(focused);
                updateButtonStyle(this, focused, color);
            }
        };

        Text buttonText = new Text("text:" + text, 0, 0, text, Text.Alignment.CENTER);
        buttonText.setForegroundColor(color);
        button.setChild(buttonText);
        button.setCanFocus(true);

        return button;
    }

    /**
     * Updates button visual style based on focus state.
     */
    private static void updateButtonStyle(Box box, boolean focused, AnsiColor baseColor) {
        Text text = (Text) box.getChild();
        if (text != null) {
            if (focused) {
                text.setBackgroundColor(baseColor);
                text.setForegroundColor(AnsiColor.BRIGHT_WHITE);
            } else {
                text.setBackgroundColor(null);
                text.setForegroundColor(baseColor);
            }
        }
    }

    /**
     * Updates the status text display.
     */
    private static void updateStatusText(Text statusText) {
        updateStatusText(statusText, null);
    }

    /**
     * Updates the status text with current demo information.
     */
    private static void updateStatusText(Text statusText, ProcessLoop processLoop) {
        String status = "3D Graphics Demo - Interactive Scene\n";
        if (processLoop != null) {
            status += String.format("FPS: %d | Mode: %s | Animation: %s\n",
                    processLoop.getCurrentFPS(),
                    currentRenderMode.toString(),
                    isAnimating ? "ON" : "OFF");
        } else {
            status += "Initializing...\n";
        }
        status += String.format("Camera: (%.1f,%.1f,%.1f) | %s\n",
                camera.getPosition().getX(),
                camera.getPosition().getY(),
                camera.getPosition().getZ(),
                lastAction);
        status += "Controls: W/F/B=Mode, SPACE=Animate, Arrows=Move, R=Reset, ESC=Quit";

        statusText.setText(status);
        statusText.setForegroundColor(AnsiColor.BRIGHT_WHITE);
    }
}
