package com.consolemaster.demo;

import com.consolemaster.AnsiColor;
import com.consolemaster.BorderLayout;
import com.consolemaster.Box;
import com.consolemaster.Composite;
import com.consolemaster.DefaultBorder;
import com.consolemaster.KeyEvent;
import com.consolemaster.PositionConstraint;
import com.consolemaster.ProcessLoop;
import com.consolemaster.ScreenCanvas;
import com.consolemaster.Text;
import com.consolemaster.raycasting.*;

import java.io.IOException;

/**
 * Advanced demo showcasing animated enemies with proper 8-directional sprites.
 * This demo creates enemies that patrol and show different sprites based on
 * their movement direction and the player's viewing angle.
 */
public class Advanced8DirectionDemo {

    private static long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 200; // Update every 200ms
    private static String lastAction = "Demo Started";

    public static void main(String[] args) throws IOException {
        // Create main screen
        ScreenCanvas screen = new ScreenCanvas("Advanced 8-Direction Sprite Demo", 100, 35);

        // Create raycasting canvas
        RaycastingCanvas raycastingCanvas = new RaycastingCanvas("3D World", 80, 30);

        // Setup a more complex map
        String[] map = {
            "########################",
            "#                      #",
            "#  ###            ###  #",
            "#  # #            # #  #",
            "#  # #            # #  #",
            "#  ###            ###  #",
            "#                      #",
            "#                      #",
            "#      ##########      #",
            "#      #        #      #",
            "#      #        #      #",
            "#      #        #      #",
            "#      ##########      #",
            "#                      #",
            "#                      #",
            "########################"
        };
        raycastingCanvas.setMap(map);
        raycastingCanvas.setPlayerPosition(2.5, 2.5);
        raycastingCanvas.setPlayerAngle(0.0);

        // Create patrolling enemies with proper 8-directional sprites
        setupAdvancedGameObjects(raycastingCanvas);

        // Create detailed info panel
        Text infoText = new Text("Advanced 8-Direction Demo", 20, 6,
            "WASD: Move/Strafe | Q/E: Rotate | R: Reset | ESC: Exit\n" +
            "Watch how enemies show different sprites based on:\n" +
            "• Their movement direction (8 directions)\n" +
            "• Your viewing angle relative to their orientation\n" +
            "• Distance-based shading effects\n" +
            "Objects: Patrolling Guards, Static Guards, Treasures"
        );
        infoText.setForegroundColor(AnsiColor.CYAN);

        Box infoBox = new Box("Info", 20, 6, new DefaultBorder());
        infoBox.setChild(infoText);

        // Create status display
        Text statusText = new Text("Status", 20, 3, "Last action: " + lastAction);
        statusText.setForegroundColor(AnsiColor.YELLOW);

        Box statusBox = new Box("Status", 20, 3, new DefaultBorder());
        statusBox.setChild(statusText);

        // Add components to composite
        Composite composite = new Composite("Main", 100, 35, new BorderLayout());
        composite.addChild(raycastingCanvas, PositionConstraint.CENTER);
        composite.addChild(infoBox, PositionConstraint.NORTH);
        composite.addChild(statusBox, PositionConstraint.SOUTH);

        screen.setContent(composite);

        // Create process loop for interactive controls and animations
        ProcessLoop processLoop = new ProcessLoop(screen);

        // Add keyboard controls
        processLoop.addKeyboardEventHandler(event -> {
            switch (event.getKey()) {
                case 'w', 'W' -> {
                    raycastingCanvas.movePlayer(0.1);
                    lastAction = "Moved forward";
                }
                case 's', 'S' -> {
                    raycastingCanvas.movePlayer(-0.1);
                    lastAction = "Moved backward";
                }
                case 'a', 'A' -> {
                    raycastingCanvas.strafePlayer(-0.1);
                    lastAction = "Strafed left";
                }
                case 'd', 'D' -> {
                    raycastingCanvas.strafePlayer(0.1);
                    lastAction = "Strafed right";
                }
                case 'q', 'Q' -> {
                    raycastingCanvas.rotatePlayer(-0.1);
                    lastAction = "Rotated left";
                }
                case 'e', 'E' -> {
                    raycastingCanvas.rotatePlayer(0.1);
                    lastAction = "Rotated right";
                }
                case 'r', 'R' -> {
                    resetDemo(raycastingCanvas);
                    lastAction = "Demo reset";
                }
                case 'f', 'F' -> {
                    handleInteraction(raycastingCanvas);
                    lastAction = "Interaction attempt";
                }
                case 27 -> { // ESC
                    processLoop.stop();
                    return true;
                }
            }

            // Update status display
            statusText.setText("Last action: " + lastAction);
            return true;
        });

        // Add animation update using pre-render callback
        processLoop.setPreRenderAction(() -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime > UPDATE_INTERVAL) {
                updateGameObjects(raycastingCanvas);
                lastUpdateTime = currentTime;
            }
        });

        // Start the demo
        processLoop.start();
    }

    /**
     * Sets up advanced game objects with detailed 8-directional sprites.
     */
    private static void setupAdvancedGameObjects(RaycastingCanvas canvas) {
        // Patrolling guard in the central area
        PatrollingEnemy guard1 = new PatrollingEnemy("Patrol Guard 1", 12.0, 8.0, createDetailedGuardSprites());
        guard1.addPatrolPoint(12.0, 8.0);
        guard1.addPatrolPoint(12.0, 12.0);
        guard1.addPatrolPoint(8.0, 12.0);
        guard1.addPatrolPoint(8.0, 8.0);
        canvas.addGameObject(guard1);

        // Patrolling guard in the upper area
        PatrollingEnemy guard2 = new PatrollingEnemy("Patrol Guard 2", 5.0, 3.0, createDetailedGuardSprites());
        guard2.addPatrolPoint(5.0, 3.0);
        guard2.addPatrolPoint(18.0, 3.0);
        guard2.addPatrolPoint(18.0, 6.0);
        guard2.addPatrolPoint(5.0, 6.0);
        canvas.addGameObject(guard2);

        // Static guards facing different directions
        GameObject staticGuard1 = new GameObject("Static Guard E", 15.0, 10.0, createDetailedGuardSprites());
        staticGuard1.setOrientation(0.0); // Facing East
        canvas.addGameObject(staticGuard1);

        GameObject staticGuard2 = new GameObject("Static Guard S", 6.0, 4.0, createDetailedGuardSprites());
        staticGuard2.setOrientation(Math.PI / 2); // Facing South
        canvas.addGameObject(staticGuard2);

        GameObject staticGuard3 = new GameObject("Static Guard W", 17.0, 4.0, createDetailedGuardSprites());
        staticGuard3.setOrientation(Math.PI); // Facing West
        canvas.addGameObject(staticGuard3);

        GameObject staticGuard4 = new GameObject("Static Guard N", 10.0, 14.0, createDetailedGuardSprites());
        staticGuard4.setOrientation(3 * Math.PI / 2); // Facing North
        canvas.addGameObject(staticGuard4);

        // Add some interactive treasures
        GameObject treasure1 = new GameObject("Golden Treasure", 20.0, 13.0, createAdvancedTreasureSprite());
        treasure1.setInteractable(true);
        canvas.addGameObject(treasure1);

        GameObject treasure2 = new GameObject("Silver Treasure", 3.0, 13.0, createAdvancedTreasureSprite());
        treasure2.setInteractable(true);
        canvas.addGameObject(treasure2);

        // Add decorative objects
        GameObject pillar1 = new GameObject("Pillar 1", 11.5, 4.5, createDecorativePillar());
        pillar1.setSolid(true);
        canvas.addGameObject(pillar1);

        GameObject pillar2 = new GameObject("Pillar 2", 11.5, 12.5, createDecorativePillar());
        pillar2.setSolid(true);
        canvas.addGameObject(pillar2);
    }

    /**
     * Creates detailed 8-directional guard sprites with proper animations.
     */
    private static SimpleSpriteProvider createDetailedGuardSprites() {
        Sprite[] guardSprites = new Sprite[8];

        // East (0°) - facing right
        guardSprites[0] = new SimpleSprite(new String[]{
            " ☻ ",
            "/│\\",
            " ╱╲"
        }, AnsiColor.RED, null);

        // Southeast (45°) - facing down-right
        guardSprites[1] = new SimpleSprite(new String[]{
            " ☻ ",
            " ╲│",
            "  ╲"
        }, AnsiColor.RED, null);

        // South (90°) - facing down
        guardSprites[2] = new SimpleSprite(new String[]{
            " ☻ ",
            " │ ",
            "╱ ╲"
        }, AnsiColor.RED, null);

        // Southwest (135°) - facing down-left
        guardSprites[3] = new SimpleSprite(new String[]{
            " ☻ ",
            "│╱ ",
            "╱  "
        }, AnsiColor.RED, null);

        // West (180°) - facing left
        guardSprites[4] = new SimpleSprite(new String[]{
            " ☻ ",
            "/│\\",
            "╱ ╲"
        }, AnsiColor.RED, null);

        // Northwest (225°) - facing up-left
        guardSprites[5] = new SimpleSprite(new String[]{
            "╲  ",
            "│╲ ",
            " ☻ "
        }, AnsiColor.RED, null);

        // North (270°) - facing up
        guardSprites[6] = new SimpleSprite(new String[]{
            "╱ ╲",
            " │ ",
            " ☻ "
        }, AnsiColor.RED, null);

        // Northeast (315°) - facing up-right
        guardSprites[7] = new SimpleSprite(new String[]{
            "  ╱",
            " ╱│",
            " ☻ "
        }, AnsiColor.RED, null);

        return new SimpleSpriteProvider(guardSprites);
    }

    /**
     * Creates an advanced treasure sprite.
     */
    private static SimpleSpriteProvider createAdvancedTreasureSprite() {
        String[] treasureData = {
            "╔═══╗",
            "║♦♦♦║",
            "║♦$♦║",
            "║♦♦♦║",
            "╚═══╝"
        };

        Sprite treasureSprite = new SimpleSprite(treasureData,
            AnsiColor.BRIGHT_YELLOW,
            AnsiColor.BLACK);

        return new SimpleSpriteProvider(treasureSprite);
    }

    /**
     * Creates a decorative pillar sprite.
     */
    private static SimpleSpriteProvider createDecorativePillar() {
        String[] pillarData = {
            "▓▓▓",
            "███",
            "▓▓▓",
            "███",
            "▓▓▓",
            "███"
        };

        Sprite pillarSprite = new SimpleSprite(pillarData,
            AnsiColor.WHITE,
            AnsiColor.BRIGHT_BLACK,
            1.3);

        return new SimpleSpriteProvider(pillarSprite);
    }

    /**
     * Updates game objects (animations, AI, etc.).
     */
    private static void updateGameObjects(RaycastingCanvas canvas) {
        for (GameObject obj : canvas.getGameObjects()) {
            if (obj instanceof PatrollingEnemy patrollingEnemy) {
                patrollingEnemy.update();
            }
        }
    }

    /**
     * Handles player interaction with nearby objects.
     */
    private static void handleInteraction(RaycastingCanvas canvas) {
        GameObject closestObject = canvas.getClosestInteractableObject(1.5);

        if (closestObject != null && closestObject.isInteractable()) {
            canvas.removeGameObject(closestObject);
            lastAction = "Collected: " + closestObject.getName();
        } else {
            lastAction = "Nothing to interact with";
        }
    }

    /**
     * Resets the demo to initial state.
     */
    private static void resetDemo(RaycastingCanvas canvas) {
        canvas.clearGameObjects();
        canvas.setPlayerPosition(2.5, 2.5);
        canvas.setPlayerAngle(0.0);
        setupAdvancedGameObjects(canvas);
        lastAction = "Demo reset!";
    }

    /**
     * Inner class representing a patrolling enemy with AI movement.
     */
    private static class PatrollingEnemy extends GameObject {
        private final java.util.List<Point> patrolPoints = new java.util.ArrayList<>();
        private int currentTargetIndex = 0;
        private static final double MOVE_SPEED = 0.05;
        private static final double ARRIVAL_THRESHOLD = 0.2;

        public PatrollingEnemy(String name, double x, double y, SpriteProvider spriteProvider) {
            super(name, x, y, spriteProvider);
        }

        public void addPatrolPoint(double x, double y) {
            patrolPoints.add(new Point(x, y));
        }

        public void update() {
            if (patrolPoints.isEmpty()) return;

            Point target = patrolPoints.get(currentTargetIndex);
            double dx = target.x - getX();
            double dy = target.y - getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < ARRIVAL_THRESHOLD) {
                // Reached target, move to next patrol point
                currentTargetIndex = (currentTargetIndex + 1) % patrolPoints.size();
                target = patrolPoints.get(currentTargetIndex);
                dx = target.x - getX();
                dy = target.y - getY();
                distance = Math.sqrt(dx * dx + dy * dy);
            }

            if (distance > 0) {
                // Move towards target
                double moveX = (dx / distance) * MOVE_SPEED;
                double moveY = (dy / distance) * MOVE_SPEED;

                setX(getX() + moveX);
                setY(getY() + moveY);

                // Update orientation to face movement direction
                setOrientation(Math.atan2(dy, dx));
            }
        }

        private static class Point {
            final double x, y;
            Point(double x, double y) { this.x = x; this.y = y; }
        }
    }
}
