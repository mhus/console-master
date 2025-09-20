package com.consolemaster.demo;

import com.consolemaster.AnsiColor;
import com.consolemaster.Box;
import com.consolemaster.Composite;
import com.consolemaster.DefaultBorder;
import com.consolemaster.KeyEvent;
import com.consolemaster.PositionConstraint;
import com.consolemaster.ProcessLoop;
import com.consolemaster.ScreenCanvas;
import com.consolemaster.Text;
import com.consolemaster.raycasting.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Advanced demo showcasing animated enemies with proper 8-directional sprites.
 * This demo creates enemies that patrol and show different sprites based on
 * their movement direction and the player's viewing angle.
 */
@Slf4j
public class Advanced8DirectionDemo {

    private static long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 200; // Update every 200ms
    private static String lastAction = "Demo Started";
    private static RaycastingCanvas raycastingCanvas;

    public static void main(String[] args) {
        try {
            // Create main screen
            ScreenCanvas screen = new ScreenCanvas(100, 35);

            // Create raycasting canvas
            raycastingCanvas = new RaycastingCanvas("3D World", 80, 30);

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

            // Create main container
            Composite mainContainer = new Composite("mainContainer",
                screen.getWidth() - 4,
                screen.getHeight() - 4,
                new com.consolemaster.BorderLayout(1));

            // Create detailed info panel
            Text infoText = new Text("infoText", 0, 0,
                "Advanced 8-Direction Demo\n" +
                "WASD: Move/Strafe | Q/E: Rotate | R: Reset | ESC: Exit\n" +
                "Watch how enemies show different sprites based on:\n" +
                "• Their movement direction (8 directions)\n" +
                "• Your viewing angle relative to their orientation\n" +
                "• Distance-based shading effects\n" +
                "Objects: Patrolling Guards, Static Guards, Treasures",
                Text.Alignment.LEFT);
            infoText.setForegroundColor(AnsiColor.CYAN);

            Box infoBox = new Box("Info", 20, 8, new DefaultBorder());
            infoBox.setContent(infoText);
            infoBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.TOP_RIGHT));

            // Create status display
            Text statusText = new Text("statusText", 0, 0, "Last action: " + lastAction, Text.Alignment.LEFT);
            statusText.setForegroundColor(AnsiColor.YELLOW);

            Box statusBox = new Box("Status", 20, 3, new DefaultBorder());
            statusBox.setContent(statusText);
            statusBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.BOTTOM_RIGHT));

            // Set canvas position
            raycastingCanvas.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER));

            // Add components to main container
            mainContainer.addChild(raycastingCanvas);
            mainContainer.addChild(infoBox);
            mainContainer.addChild(statusBox);

            // Set main container
            screen.setContent(mainContainer);

            // Register keyboard shortcuts using the correct API
            registerKeyboardControls(screen, statusText);

            // Create process loop for animations
            ProcessLoop processLoop = new ProcessLoop(screen);
            processLoop.setUpdateCallback(() -> {
                // Update status display
                statusText.setText("Last action: " + lastAction);
            });

            // Create and add game update animation ticker
            GameUpdateTicker gameUpdateTicker = new GameUpdateTicker(raycastingCanvas, processLoop);
            processLoop.addAnimationTicker(gameUpdateTicker);

            // Set animation tick rate for smooth gameplay (5 updates per second)
            processLoop.setAnimationTicksPerSecond(5);

            log.info("Starting Advanced 8-Direction Sprite Demo...");
            processLoop.start();

        } catch (IOException e) {
            log.error("Error initializing Advanced 8-Direction Demo", e);
        }
    }

    /**
     * Registers keyboard controls using the correct framework API.
     */
    private static void registerKeyboardControls(ScreenCanvas screen, Text statusText) {
        double moveSpeed = 0.1;
        double rotateSpeed = 0.1;

        // Movement controls
        screen.registerShortcut(KeyEvent.SpecialKey.ARROW_UP.name(), () -> {
            raycastingCanvas.movePlayer(moveSpeed);
            lastAction = "Moved forward";
        });

        screen.registerShortcut(KeyEvent.SpecialKey.ARROW_DOWN.name(), () -> {
            raycastingCanvas.movePlayer(-moveSpeed);
            lastAction = "Moved backward";
        });

        screen.registerShortcut("A", () -> {
            raycastingCanvas.strafePlayer(-moveSpeed);
            lastAction = "Strafed left";
        });

        screen.registerShortcut("D", () -> {
            raycastingCanvas.strafePlayer(moveSpeed);
            lastAction = "Strafed right";
        });

        // Rotation controls
        screen.registerShortcut(KeyEvent.SpecialKey.ARROW_LEFT.name(), () -> {
            raycastingCanvas.rotatePlayer(-rotateSpeed);
            lastAction = "Rotated left";
        });

        screen.registerShortcut(KeyEvent.SpecialKey.ARROW_RIGHT.name(), () -> {
            raycastingCanvas.rotatePlayer(rotateSpeed);
            lastAction = "Rotated right";
        });

        // Demo control
        screen.registerShortcut("R", () -> {
            resetDemo(raycastingCanvas);
            lastAction = "Demo reset";
        });

        screen.registerShortcut("F", () -> {
            handleInteraction(raycastingCanvas);
            lastAction = "Interaction attempt";
        });

        // Exit controls
        screen.registerShortcut(KeyEvent.SpecialKey.ESC.name(), () -> {
            log.info("Exiting Advanced 8-Direction Demo...");
            System.exit(0);
        });
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
     * AnimationTicker implementation for game object updates.
     * Replaces the manual thread with proper AnimationManager integration.
     */
    private static class GameUpdateTicker implements com.consolemaster.AnimationTicker {
        private final RaycastingCanvas canvas;
        private final ProcessLoop processLoop;
        private long lastUpdateTime = 0;

        public GameUpdateTicker(RaycastingCanvas canvas, ProcessLoop processLoop) {
            this.canvas = canvas;
            this.processLoop = processLoop;
        }

        @Override
        public boolean tick() {
            try {
                updateGameObjects(canvas);
                lastUpdateTime = System.currentTimeMillis();
                return true; // Request redraw after each update
            } catch (Exception e) {
                log.error("Error in game update loop", e);
                return false; // Don't request redraw on error
            }
        }
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
