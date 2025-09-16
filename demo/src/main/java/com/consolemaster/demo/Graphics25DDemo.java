package com.consolemaster.demo;

import com.consolemaster.*;
import com.consolemaster.graphics25d.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Interactive 2.5D graphics demo showcasing the Graphics25DCanvas capabilities.
 * Features 2.5D scenes, camera controls, and different rendering options.
 */
@Slf4j
public class Graphics25DDemo {

    private static String lastAction = "Demo Started";
    private static Camera25D camera;
    private static Graphics25DCanvas canvas25D;
    private static int currentScene = 1;
    private static final int maxScenes = 4;

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
            Text headerText = new Text("headerText", 0, 0, "Graphics 2.5D Demo - Interactive 2.5D Scene", Text.Alignment.CENTER);
            headerText.setForegroundColor(AnsiColor.BRIGHT_CYAN);
            headerText.setBold(true);
            headerBox.setContent(headerText);
            headerBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.TOP_CENTER));

            // Create 2.5D canvas
            canvas25D = new Graphics25DCanvas("2.5D Scene", 100, 28);
            canvas25D.setBackgroundColor(AnsiColor.BLACK);
            canvas25D.setViewDistance(20.0);
            canvas25D.setPerspectiveFactor(0.5);
            canvas25D.setDepthSorting(true);

            // Setup camera
            camera = canvas25D.getCamera();
            camera.setPosition(new Point25D(0, 0, 2));
            camera.setDirection(0); // Looking north

            // Create initial scene
            createBasicScene();

            // Wrap 2.5D canvas in a box for better presentation
            Box canvas25DBox = new Box("canvas25DBox", canvas25D.getWidth() + 2, canvas25D.getHeight() + 2, new DefaultBorder());
            canvas25DBox.setContent(canvas25D);
            canvas25DBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER));

            // Create status footer
            Box statusBox = new Box("statusBox", 0, 5, new DefaultBorder());
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
            processLoop.setTargetFPS(15); // 15 FPS for smooth updates

            // Register keyboard controls
            screen.registerShortcut("Q", () -> {
                try {
                    processLoop.stop();
                } catch (IOException e) {
                    System.err.println("Error stopping process loop: " + e.getMessage());
                }
            });

            // Movement controls
            screen.registerShortcut("W", () -> {
                camera.moveForward(1.0);
                lastAction = "Camera Forward";
            });

            screen.registerShortcut("S", () -> {
                camera.moveBackward(1.0);
                lastAction = "Camera Backward";
            });

            screen.registerShortcut("A", () -> {
                camera.moveLeft(1.0);
                lastAction = "Camera Left";
            });

            screen.registerShortcut("D", () -> {
                camera.moveRight(1.0);
                lastAction = "Camera Right";
            });

            // Rotation controls
            screen.registerShortcut("E", () -> {
                camera.rotateClockwise();
                lastAction = "Rotate Right (" + camera.getDirectionName() + ")";
            });

            screen.registerShortcut("Z", () -> {
                camera.rotateCounterClockwise();
                lastAction = "Rotate Left (" + camera.getDirectionName() + ")";
            });

            // Scene switching
            screen.registerShortcut(" ", () -> {
                currentScene = (currentScene % maxScenes) + 1;
                canvas25D.clearObjects();

                switch (currentScene) {
                    case 1 -> {
                        createBasicScene();
                        lastAction = "Scene 1: Basic Objects";
                    }
                    case 2 -> {
                        createMazeScene();
                        lastAction = "Scene 2: Maze";
                    }
                    case 3 -> {
                        createGardenScene();
                        lastAction = "Scene 3: Garden";
                    }
                    case 4 -> {
                        createCityScene();
                        lastAction = "Scene 4: City";
                    }
                }
            });

            // Camera reset
            screen.registerShortcut("R", () -> {
                camera.setPosition(new Point25D(0, 0, 2));
                camera.setDirection(0);
                lastAction = "Camera Reset";
            });

            // Rendering options
            screen.registerShortcut("V", () -> {
                double currentDistance = canvas25D.getViewDistance();
                double newDistance = currentDistance == 20.0 ? 10.0 : 20.0;
                canvas25D.setViewDistance(newDistance);
                lastAction = "View Distance: " + newDistance;
            });

            screen.registerShortcut("P", () -> {
                double currentFactor = canvas25D.getPerspectiveFactor();
                double newFactor = currentFactor == 0.5 ? 1.0 : 0.5;
                canvas25D.setPerspectiveFactor(newFactor);
                lastAction = "Perspective: " + newFactor;
            });

            screen.registerShortcut("T", () -> {
                boolean currentSorting = canvas25D.isDepthSorting();
                canvas25D.setDepthSorting(!currentSorting);
                lastAction = "Depth Sorting: " + (!currentSorting ? "ON" : "OFF");
            });

            // Update callback for status display
            processLoop.setUpdateCallback(() -> {
                updateStatusText((Text) statusBox.getChild());
            });

            System.out.println("Starting Graphics 2.5D Demo...");
            System.out.println("Controls:");
            System.out.println("- WASD: Move camera (Forward/Left/Back/Right)");
            System.out.println("- E/Z: Rotate camera (Right/Left)");
            System.out.println("- SPACE: Switch scenes");
            System.out.println("- R: Reset camera position");
            System.out.println("- V: Toggle view distance");
            System.out.println("- P: Toggle perspective factor");
            System.out.println("- T: Toggle depth sorting");
            System.out.println("- ESC or Q: Quit");

            // Start the process loop (this will block until stopped)
            processLoop.start();

            System.out.println("Graphics 2.5D Demo ended.");

        } catch (IOException e) {
            log.error("Error running Graphics 2.5D demo", e);
        }
    }

    /**
     * Updates the status text with current information.
     */
    private static void updateStatusText(Text statusText) {
        String sceneName = switch (currentScene) {
            case 1 -> "Basic Objects";
            case 2 -> "Maze";
            case 3 -> "Garden";
            case 4 -> "City";
            default -> "Unknown";
        };

        String status = String.format(
            "Scene %d/%d: %s | Camera: Pos(%.1f,%.1f,%.1f) Dir:%s | Objects: %d | %s",
            currentScene, maxScenes, sceneName,
            camera.getPosition().getX(), camera.getPosition().getY(), camera.getPosition().getZ(),
            camera.getDirectionName(),
            canvas25D.getObjectCount(),
            lastAction
        );
        statusText.setText(status);
    }

    /**
     * Creates a basic scene with simple objects demonstrating different textures and colors.
     */
    private static void createBasicScene() {
        // Create a simple cross pattern
        canvas25D.createObject(0, 0, 0, "X", AnsiColor.RED);
        canvas25D.createObject(2, 0, 0, "O", AnsiColor.BLUE);
        canvas25D.createObject(-2, 0, 0, "O", AnsiColor.BLUE);
        canvas25D.createObject(0, 2, 0, "O", AnsiColor.GREEN);
        canvas25D.createObject(0, -2, 0, "O", AnsiColor.GREEN);

        // Add some objects at different heights
        canvas25D.createObject(4, 4, 1, "^", AnsiColor.YELLOW);
        canvas25D.createObject(-4, -4, 1, "^", AnsiColor.YELLOW);
        canvas25D.createObject(4, -4, -1, "v", AnsiColor.MAGENTA);
        canvas25D.createObject(-4, 4, -1, "v", AnsiColor.MAGENTA);

        // Create a circle of objects
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            double x = 6 * Math.cos(angle);
            double y = 6 * Math.sin(angle);
            canvas25D.createObject(x, y, 0, "*", AnsiColor.CYAN);
        }
    }

    /**
     * Creates a maze-like scene with walls and passages.
     */
    private static void createMazeScene() {
        // Create maze walls
        char wallChar = '█';
        AnsiColor wallColor = AnsiColor.WHITE;

        // Outer walls
        for (int x = -8; x <= 8; x++) {
            canvas25D.createObject(x, -8, 0, String.valueOf(wallChar), wallColor);
            canvas25D.createObject(x, 8, 0, String.valueOf(wallChar), wallColor);
        }
        for (int y = -8; y <= 8; y++) {
            canvas25D.createObject(-8, y, 0, String.valueOf(wallChar), wallColor);
            canvas25D.createObject(8, y, 0, String.valueOf(wallChar), wallColor);
        }

        // Internal walls creating a simple maze
        for (int x = -6; x <= 6; x += 2) {
            for (int y = -6; y <= 2; y++) {
                if (y != 0) canvas25D.createObject(x, y, 0, String.valueOf(wallChar), wallColor);
            }
        }

        // Add some items in the maze
        canvas25D.createObject(0, 0, 0, "!", AnsiColor.YELLOW);
        canvas25D.createObject(4, 4, 0, "$", AnsiColor.GREEN);
        canvas25D.createObject(-4, -4, 0, "?", AnsiColor.BLUE);
        canvas25D.createObject(6, -2, 0, "@", AnsiColor.RED);
    }

    /**
     * Creates a garden scene with trees, flowers, and paths.
     */
    private static void createGardenScene() {
        // Create trees (tall objects)
        canvas25D.createObject(-5, -5, 2, "T", AnsiColor.GREEN);
        canvas25D.createObject(5, -5, 2, "T", AnsiColor.GREEN);
        canvas25D.createObject(-5, 5, 2, "T", AnsiColor.GREEN);
        canvas25D.createObject(5, 5, 2, "T", AnsiColor.GREEN);

        // Tree trunks
        canvas25D.createObject(-5, -5, 0, "|", AnsiColor.BLACK);
        canvas25D.createObject(5, -5, 0, "|", AnsiColor.BLACK);
        canvas25D.createObject(-5, 5, 0, "|", AnsiColor.BLACK);
        canvas25D.createObject(5, 5, 0, "|", AnsiColor.BLACK);

        // Create flower beds
        String[] flowers = {"*", "o", "+", "~"};
        AnsiColor[] flowerColors = {AnsiColor.RED, AnsiColor.YELLOW, AnsiColor.MAGENTA, AnsiColor.BLUE};

        for (int i = 0; i < 20; i++) {
            double angle = i * Math.PI / 10;
            double radius = 2 + Math.random() * 2;
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);
            int flowerType = (int) (Math.random() * flowers.length);
            canvas25D.createObject(x, y, 0, flowers[flowerType], flowerColors[flowerType]);
        }

        // Create a path with stones
        for (int i = -8; i <= 8; i++) {
            if (i % 2 == 0) {
                canvas25D.createObject(i, 0, -0.5, ".", AnsiColor.BLACK);
                canvas25D.createObject(0, i, -0.5, ".", AnsiColor.BLACK);
            }
        }
    }

    /**
     * Creates a city scene with buildings of different heights.
     */
    private static void createCityScene() {
        // Create buildings with different heights
        int[][] buildings = {
            {-6, -6, 3}, {-6, -2, 1}, {-6, 2, 4}, {-6, 6, 2},
            {-2, -6, 2}, {-2, -2, 5}, {-2, 2, 1}, {-2, 6, 3},
            {2, -6, 4}, {2, -2, 2}, {2, 2, 6}, {2, 6, 1},
            {6, -6, 1}, {6, -2, 3}, {6, 2, 2}, {6, 6, 4}
        };

        for (int[] building : buildings) {
            int x = building[0];
            int y = building[1];
            int height = building[2];

            // Building base
            canvas25D.createObject(x, y, 0, "█", AnsiColor.BLACK);

            // Building top based on height
            char topChar = switch (height) {
                case 1 -> '▀';
                case 2 -> '▄';
                case 3 -> '█';
                case 4 -> '▬';
                case 5 -> '≡';
                case 6 -> '▣';
                default -> '■';
            };

            AnsiColor buildingColor = switch (height % 4) {
                case 0 -> AnsiColor.BLUE;
                case 1 -> AnsiColor.GREEN;
                case 2 -> AnsiColor.YELLOW;
                default -> AnsiColor.CYAN;
            };

            canvas25D.createObject(x, y, height, String.valueOf(topChar), buildingColor);
        }

        // Add some cars on the streets
        canvas25D.createObject(-7, 0, 0, "C", AnsiColor.RED);
        canvas25D.createObject(7, 0, 0, "C", AnsiColor.BLUE);
        canvas25D.createObject(0, -7, 0, "C", AnsiColor.YELLOW);
        canvas25D.createObject(0, 7, 0, "C", AnsiColor.GREEN);

        // Add street lights
        for (int i = -4; i <= 4; i += 4) {
            canvas25D.createObject(i, 0, 1, "l", AnsiColor.WHITE);
            canvas25D.createObject(0, i, 1, "l", AnsiColor.WHITE);
        }
    }
}
