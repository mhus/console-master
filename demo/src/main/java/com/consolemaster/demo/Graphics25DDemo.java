package com.consolemaster.demo;

import com.consolemaster.AnsiColor;
import com.consolemaster.BorderLayout;
import com.consolemaster.Box;
import com.consolemaster.Composite;
import com.consolemaster.DefaultBorder;
import com.consolemaster.FlowLayout;
import com.consolemaster.KeyEvent;
import com.consolemaster.PositionConstraint;
import com.consolemaster.ProcessLoop;
import com.consolemaster.ScreenCanvas;
import com.consolemaster.Text;
import com.consolemaster.graphics25d.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Interactive 2.5D graphics demo showcasing the Graphics25DCanvas capabilities.
 * Features animated 2.5D objects, camera controls, and different rendering modes.
 */
@Slf4j
public class Graphics25DDemo {

    private static double animationTime = 0.0;
    private static boolean isAnimating = true;
    private static Graphics25DCanvas.RenderMode currentRenderMode = Graphics25DCanvas.RenderMode.WIREFRAME;
    private static String lastAction = "Demo Started";
    private static Camera25D camera;
    private static Graphics25DCanvas canvas25D;

    // Animation thread
    private static Thread animationThread;
    private static final Object animationLock = new Object();
    private static Object25D rotatingCube;
    private static Object25D staticPyramid;
    private static Point25D cubePosition = new Point25D(0, 0, 0);
    private static Object25D.Rotation90 cubeRotation = Object25D.Rotation90.NONE;

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
            Text headerText = new Text("headerText", 0, 0, "2.5D Graphics Demo - Isometric Scene", Text.Alignment.CENTER);
            headerText.setForegroundColor(AnsiColor.BRIGHT_CYAN);
            headerText.setBold(true);
            headerBox.setContent(headerText);
            headerBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.TOP_CENTER));

            // Create 2.5D canvas
            canvas25D = new Graphics25DCanvas("2.5D Scene", 80, 25);
            canvas25D.setRenderMode(currentRenderMode);
            canvas25D.setWireframeChar('*');
            canvas25D.setWireframeColor(AnsiColor.CYAN);
            canvas25D.setDefaultFillChar('█');
            canvas25D.setDefaultFillColor(AnsiColor.GREEN);

            // Setup camera
            camera = canvas25D.getCamera();
            camera.setPosition(new Point25D(0, 0, 0));
            camera.setDirection(Camera25D.Direction.FRONT);
            camera.setDistance(15.0);

            // Create initial 2.5D scene
            createInitialScene();

            // Wrap 2.5D canvas in a box for better presentation
            Box canvas25DBox = new Box("canvas25DBox", canvas25D.getWidth() + 2, canvas25D.getHeight() + 2, new DefaultBorder());
            canvas25DBox.setContent(canvas25D);
            canvas25DBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER));

            // Create control panel
            Composite controlPanel = new Composite("controlPanel", 0, 0, new FlowLayout(2, 1));

            // Render mode buttons
            Box wireframeBtn = createControlButton("Wireframe\n(W)", AnsiColor.CYAN, () -> {
                currentRenderMode = Graphics25DCanvas.RenderMode.WIREFRAME;
                canvas25D.setRenderMode(currentRenderMode);
                lastAction = "Switched to Wireframe";
            });
            controlPanel.addChild(wireframeBtn);

            Box solidBtn = createControlButton("Solid\n(S)", AnsiColor.GREEN, () -> {
                currentRenderMode = Graphics25DCanvas.RenderMode.SOLID;
                canvas25D.setRenderMode(currentRenderMode);
                lastAction = "Switched to Solid";
            });
            controlPanel.addChild(solidBtn);

            Box bothBtn = createControlButton("Both\n(B)", AnsiColor.YELLOW, () -> {
                currentRenderMode = Graphics25DCanvas.RenderMode.BOTH;
                canvas25D.setRenderMode(currentRenderMode);
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
            Box statusBox = new Box("statusBox", 0, 6, new DefaultBorder());
            Text statusText = new Text("statusText", 0, 0, "", Text.Alignment.CENTER);
            updateStatusText(statusText);
            statusBox.setContent(statusText);
            statusBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.BOTTOM_CENTER));

            // Add components to main container
            mainContainer.addChild(headerBox);
            mainContainer.addChild(canvas25DBox);
            mainContainer.addChild(statusBox);

            // Set content
            screen.setContent(mainContainer);

            // Create process loop
            ProcessLoop processLoop = new ProcessLoop(screen);
            processLoop.setTargetFPS(30); // 30 FPS for smooth 2.5D animation

            // Register keyboard controls
            screen.registerShortcut("Q", () -> {
                try {
                    stopAnimationThread();
                    processLoop.stop();
                } catch (IOException e) {
                    System.err.println("Error stopping process loop: " + e.getMessage());
                }
            });

            // 2.5D Controls
            screen.registerShortcut("W", () -> {
                currentRenderMode = Graphics25DCanvas.RenderMode.WIREFRAME;
                canvas25D.setRenderMode(currentRenderMode);
                lastAction = "Wireframe Mode";
            });

            screen.registerShortcut("S", () -> {
                currentRenderMode = Graphics25DCanvas.RenderMode.SOLID;
                canvas25D.setRenderMode(currentRenderMode);
                lastAction = "Solid Mode";
            });

            screen.registerShortcut("B", () -> {
                currentRenderMode = Graphics25DCanvas.RenderMode.BOTH;
                canvas25D.setRenderMode(currentRenderMode);
                lastAction = "Both Mode";
            });

            screen.registerShortcut(" ", () -> {
                isAnimating = !isAnimating;
                lastAction = isAnimating ? "Animation Started" : "Animation Paused";
            });

            // Camera controls
            screen.registerShortcut(KeyEvent.SpecialKey.ARROW_UP.name(), () -> {
                camera.move(0, 0, 1);
                lastAction = "Camera Forward";
            });

            screen.registerShortcut(KeyEvent.SpecialKey.ARROW_DOWN.name(), () -> {
                camera.move(0, 0, -1);
                lastAction = "Camera Backward";
            });

            screen.registerShortcut(KeyEvent.SpecialKey.ARROW_LEFT.name(), () -> {
                camera.move(-1, 0, 0);
                lastAction = "Camera Left";
            });

            screen.registerShortcut(KeyEvent.SpecialKey.ARROW_RIGHT.name(), () -> {
                camera.move(1, 0, 0);
                lastAction = "Camera Right";
            });

            screen.registerShortcut(KeyEvent.SpecialKey.PAGE_UP.name(), () -> {
                camera.move(0, 1, 0);
                lastAction = "Camera Up";
            });

            screen.registerShortcut(KeyEvent.SpecialKey.PAGE_DOWN.name(), () -> {
                camera.move(0, -1, 0);
                lastAction = "Camera Down";
            });

            // Camera direction controls
            screen.registerShortcut("1", () -> {
                camera.setDirection(Camera25D.Direction.FRONT);
                lastAction = "View: Front";
            });
            screen.registerShortcut("2", () -> {
                camera.setDirection(Camera25D.Direction.RIGHT);
                lastAction = "View: Right";
            });
            screen.registerShortcut("3", () -> {
                camera.setDirection(Camera25D.Direction.BACK);
                lastAction = "View: Back";
            });
            screen.registerShortcut("4", () -> {
                camera.setDirection(Camera25D.Direction.LEFT);
                lastAction = "View: Left";
            });
            screen.registerShortcut("5", () -> {
                camera.setDirection(Camera25D.Direction.TOP);
                lastAction = "View: Top";
            });
            screen.registerShortcut("6", () -> {
                camera.setDirection(Camera25D.Direction.BOTTOM);
                lastAction = "View: Bottom";
            });

            screen.registerShortcut("R", () -> {
                // Reset camera position
                camera.setPosition(new Point25D(0, 0, 0));
                camera.setDirection(Camera25D.Direction.FRONT);
                lastAction = "Camera Reset";
            });

            screen.registerShortcut("C", () -> {
                // Create new scene
                createInitialScene();
                lastAction = "Scene Reset";
            });

            screen.registerShortcut("T", () -> {
                // Toggle textured objects
                createTexturedScene();
                lastAction = "Textured Objects";
            });

            // Start animation thread
            startAnimationThread(processLoop);

            // Update callback for status display only
            processLoop.setUpdateCallback(() -> {
                updateStatusText((Text) statusBox.getChild(), processLoop);
            });

            System.out.println("Starting 2.5D Graphics Demo...");
            System.out.println("2.5D Controls:");
            System.out.println("- W/S/B: Switch render modes (Wireframe/Solid/Both)");
            System.out.println("- SPACE: Toggle animation");
            System.out.println("- Arrow Keys: Move camera (Forward/Back/Left/Right)");
            System.out.println("- Page Up/Down: Move camera (Up/Down)");
            System.out.println("- 1-6: Change view direction (Front/Right/Back/Left/Top/Bottom)");
            System.out.println("- R: Reset camera position");
            System.out.println("- C: Reset scene");
            System.out.println("- T: Textured objects");
            System.out.println("- ESC or Ctrl+Q: Quit");

            // Start the process loop (this will block until stopped)
            processLoop.start();

            System.out.println("2.5D Graphics Demo ended.");

        } catch (IOException e) {
            log.error("Error running 2.5D Graphics demo", e);
        }
    }

    private static void createInitialScene() {
        canvas25D.clearObjects();

        // Create a rotating cube at the center
        rotatingCube = Object25D.createCube(new Point25D(0, 0, 5), 30.0, AnsiColor.BRIGHT_BLUE);
        canvas25D.addObject(rotatingCube);

        // Create a static pyramid on the left
        staticPyramid = Object25D.createPyramid(new Point25D(-5, 0, 8), 25, AnsiColor.BRIGHT_RED);
        canvas25D.addObject(staticPyramid);

        // Create a small cube on the right
        Object25D smallCube = Object25D.createCube(new Point25D(5, 2, 6), 15, AnsiColor.BRIGHT_GREEN);
        canvas25D.addObject(smallCube);

        // Create cubes rotated at different 90-degree angles
        Object25D cube90 = Object25D.createCube(new Point25D(-3, -2, 10), 18, AnsiColor.CYAN)
                .rotateTo(Object25D.Rotation90.CW_90);
        canvas25D.addObject(cube90);

        Object25D cube180 = Object25D.createCube(new Point25D(3, -2, 10), 18, AnsiColor.YELLOW)
                .rotateTo(Object25D.Rotation90.CW_180);
        canvas25D.addObject(cube180);

        // Create a ground plane (flat cube)
        Object25D ground = Object25D.createCube(new Point25D(0, -4, 5), 120, AnsiColor.YELLOW);
        // Flatten it to make it look like a ground plane
        for (Object25D.Face25D face : ground.getFaces()) {
            for (Point25D vertex : face.getVertices()) {
                if (vertex.getY() > -0.5) {
                    vertex.setY(-0.5);
                }
            }
        }
        canvas25D.addObject(ground);
    }

    private static void createTexturedScene() {
        canvas25D.clearObjects();

        // Create textured cubes with different 90-degree rotations
        Object25D texturedCube1 = Object25D.createTexturedCube(new Point25D(-3, 0, 8), 2.0);
        canvas25D.addObject(texturedCube1);

        Object25D texturedCube2 = Object25D.createTexturedCube(new Point25D(3, 0, 6), 2.5)
                .rotateTo(Object25D.Rotation90.CW_90);
        canvas25D.addObject(texturedCube2);

        Object25D texturedCube3 = Object25D.createTexturedCube(new Point25D(0, 3, 10), 1.8)
                .rotateTo(Object25D.Rotation90.CW_180);
        canvas25D.addObject(texturedCube3);

        // Rotating cube remains
        rotatingCube = Object25D.createTexturedCube(new Point25D(0, 0, 5), 3.0);
        canvas25D.addObject(rotatingCube);
    }

    /**
     * Updates the animated 2.5D scene.
     */
    private static void updateAnimatedScene() {
        canvas25D.clearObjects();

        // Rotate the cube by 90-degree steps
        animationTime += 0.03;
        if (animationTime > 2.0) { // Every 2 seconds, rotate 90 degrees
            animationTime = 0.0;
            cubeRotation = cubeRotation.combine(Object25D.Rotation90.CW_90);
        }

        // Create animated cube with current rotation and oscillating position
        double oscillatingY = Math.sin(animationTime * Math.PI) * 1;
        Point25D newCubePos = new Point25D(0, oscillatingY, 5);

        rotatingCube = Object25D.createCube(newCubePos, 3.0, AnsiColor.BRIGHT_BLUE)
                .rotateTo(cubeRotation);
        canvas25D.addObject(rotatingCube);

        // Add static objects back
        staticPyramid = Object25D.createPyramid(new Point25D(-5, 0, 8), 2.5, AnsiColor.BRIGHT_RED);
        canvas25D.addObject(staticPyramid);

        Object25D smallCube = Object25D.createCube(new Point25D(5, 2, 6), 1.5, AnsiColor.BRIGHT_GREEN);
        canvas25D.addObject(smallCube);

        // Oscillating cube with different rotation
        double oscillatingX = Math.cos(animationTime * Math.PI * 1.5) * 2;
        Object25D.Rotation90 oscillatingRotation = Object25D.Rotation90.fromSteps((int)(animationTime * 2) % 4);
        Object25D oscillatingCube = Object25D.createCube(new Point25D(oscillatingX, 1, 10), 1.0, AnsiColor.MAGENTA)
                .rotateTo(oscillatingRotation);
        canvas25D.addObject(oscillatingCube);
    }

    /**
     * Creates a control button for the demo.
     */
    private static Box createControlButton(String text, AnsiColor color, Runnable action) {
        Box button = new Box("btn:" + text, 12, 3, new DefaultBorder()) {
            @Override
            public void onFocusChanged(boolean focused) {
                super.onFocusChanged(focused);
                updateButtonStyle(this, focused, color);
            }
        };

        Text buttonText = new Text("text:" + text, 0, 0, text, Text.Alignment.CENTER);
        buttonText.setForegroundColor(color);
        button.setContent(buttonText);
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
        String status = "2.5D Graphics Demo - Isometric Projection\n";
        if (processLoop != null) {
            status += String.format("FPS: %d | Mode: %s | Animation: %s\n",
                    processLoop.getCurrentFPS(),
                    currentRenderMode.toString(),
                    isAnimating ? "ON" : "OFF");
        } else {
            status += "Initializing...\n";
        }
        status += String.format("Camera: %s at %s | %s\n",
                camera.getDirection(),
                camera.getPosition(),
                lastAction);
        status += "Controls: W/S/B=Mode, SPACE=Animate, Arrows=Move, 1-6=View, R=Reset, ESC=Quit";

        statusText.setText(status);
        statusText.setForegroundColor(AnsiColor.BRIGHT_WHITE);
    }

    /**
     * Starts the animation thread for updating the 2.5D scene.
     */
    private static void startAnimationThread(ProcessLoop processLoop) {
        animationThread = new Thread(() -> {
            try {
                final long targetFrameTime = 1000 / 30; // 30 FPS = ~33ms per frame

                while (!Thread.currentThread().isInterrupted()) {
                    long frameStart = System.currentTimeMillis();

                    synchronized (animationLock) {
                        if (isAnimating) {
                            animationTime += 0.03; // Animation speed
                            updateAnimatedScene();

                            // Signal ProcessLoop that a refresh is needed
                            processLoop.requestRedraw();
                        }
                    }

                    // Frame rate limiting
                    long frameTime = System.currentTimeMillis() - frameStart;
                    long sleepTime = targetFrameTime - frameTime;
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                }
            } catch (InterruptedException e) {
                // Thread was interrupted, exit gracefully
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("Error in animation thread", e);
            }
        }, "2.5D-Animation-Thread");

        animationThread.setDaemon(true);
        animationThread.start();
    }

    /**
     * Stops the animation thread.
     */
    private static void stopAnimationThread() {
        if (animationThread != null && animationThread.isAlive()) {
            animationThread.interrupt();
            try {
                animationThread.join(1000); // Wait max 1 second for thread to finish
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
