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
 * Demo application showcasing the 8-directional sprite system for game objects
 * in the raycasting engine. Demonstrates different types of objects with various
 * sprite configurations.
 */
@Slf4j
public class ObjectSpriteDemo {

    private static String lastAction = "Demo Started";
    private static RaycastingCanvas raycastingCanvas;

    public static void main(String[] args) {
        try {
            // Create main screen
            ScreenCanvas screen = new ScreenCanvas(100, 30);

            // Create raycasting canvas
            raycastingCanvas = new RaycastingCanvas("3D World", 80, 25);

            // Setup a simple map
            String[] map = {
                "####################",
                "#                  #",
                "#  ####    ####    #",
                "#  #  #    #  #    #",
                "#  #  #    #  #    #",
                "#  ####    ####    #",
                "#                  #",
                "#                  #",
                "#     ######       #",
                "#                  #",
                "#                  #",
                "####################"
            };
            raycastingCanvas.setMap(map);
            raycastingCanvas.setPlayerPosition(2.5, 2.5);
            raycastingCanvas.setPlayerAngle(0.0);

            // Create different types of game objects with sprites
            setupGameObjects(raycastingCanvas);

            // Create main container
            Composite mainContainer = new Composite("mainContainer",
                screen.getWidth() - 4,
                screen.getHeight() - 4,
                new com.consolemaster.BorderLayout(1));

            // Create info panel using Box component
            Text infoText = new Text("infoText", 0, 0,
                "Controls\n" +
                "WASD: Move/Strafe | Q/E: Rotate | ESC: Exit\n" +
                "Objects showcase 8-directional sprites:\n" +
                "- Red enemies with directional sprites\n" +
                "- Yellow crates (symmetric)\n" +
                "- Brown barrels (round)\n" +
                "- Interactive items (press F near them)",
                Text.Alignment.LEFT
            );
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

            // Create process loop for interactive controls
            ProcessLoop processLoop = new ProcessLoop(screen);
            processLoop.setUpdateCallback(() -> {
                // Update status display
                statusText.setText("Last action: " + lastAction);
            });

            log.info("Starting Object Sprite Demo...");
            processLoop.start();

        } catch (IOException e) {
            log.error("Error initializing Object Sprite Demo", e);
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

        screen.registerShortcut("F", () -> {
            handleInteraction(raycastingCanvas);
            lastAction = "Interaction attempt";
        });

        // Exit controls
        screen.registerShortcut(KeyEvent.SpecialKey.ESC.name(), () -> {
            log.info("Exiting Object Sprite Demo...");
            System.exit(0);
        });
    }

    /**
     * Sets up various game objects with different sprite providers to demonstrate
     * the 8-directional sprite system.
     */
    private static void setupGameObjects(RaycastingCanvas canvas) {
        // Add some crates (symmetric objects - same sprite for all directions)
        GameObject crate1 = new GameObject("Crate 1", 5.5, 3.5, SimpleSpriteProvider.createCrate());
        crate1.setSolid(true);
        canvas.addGameObject(crate1);

        GameObject crate2 = new GameObject("Crate 2", 8.5, 7.5, SimpleSpriteProvider.createCrate());
        crate2.setSolid(true);
        canvas.addGameObject(crate2);

        // Add some barrels (round objects)
        GameObject barrel1 = new GameObject("Barrel 1", 12.5, 4.5, SimpleSpriteProvider.createBarrel());
        barrel1.setSolid(true);
        canvas.addGameObject(barrel1);

        GameObject barrel2 = new GameObject("Barrel 2", 15.5, 8.5, SimpleSpriteProvider.createBarrel());
        barrel2.setSolid(true);
        canvas.addGameObject(barrel2);

        // Add enemies with 8-directional sprites
        GameObject enemy1 = new GameObject("Enemy 1", 7.5, 9.5, SimpleSpriteProvider.createEnemy());
        enemy1.setOrientation(Math.PI / 4); // Face northeast
        canvas.addGameObject(enemy1);

        GameObject enemy2 = new GameObject("Enemy 2", 13.5, 6.5, SimpleSpriteProvider.createEnemy());
        enemy2.setOrientation(Math.PI); // Face west
        canvas.addGameObject(enemy2);

        GameObject enemy3 = new GameObject("Enemy 3", 16.5, 2.5, SimpleSpriteProvider.createEnemy());
        enemy3.setOrientation(3 * Math.PI / 2); // Face north
        canvas.addGameObject(enemy3);

        // Add interactive objects
        GameObject treasure = new GameObject("Treasure", 10.5, 9.5, createTreasureSprite());
        treasure.setInteractable(true);
        canvas.addGameObject(treasure);

        GameObject key = new GameObject("Key", 4.5, 8.5, createKeySprite());
        key.setInteractable(true);
        canvas.addGameObject(key);

        // Add a tall object to demonstrate Z-offset
        GameObject pillar = new GameObject("Pillar", 6.5, 5.5, createPillarSprite());
        pillar.setZOffset(-0.2); // Slightly lower
        pillar.setSolid(true);
        canvas.addGameObject(pillar);
    }

    /**
     * Creates a treasure sprite provider (golden chest).
     */
    private static SimpleSpriteProvider createTreasureSprite() {
        String[] treasureData = {
            "╔═══╗",
            "║$$$║",
            "║$$$║",
            "╚═══╝"
        };

        Sprite treasureSprite = new SimpleSprite(treasureData,
            AnsiColor.BRIGHT_YELLOW,
            AnsiColor.BLACK);

        return new SimpleSpriteProvider(treasureSprite);
    }

    /**
     * Creates a key sprite provider.
     */
    private static SimpleSpriteProvider createKeySprite() {
        String[] keyData = {
            "┌─○",
            "│  ",
            "├┐ ",
            "└┘ "
        };

        Sprite keySprite = new SimpleSprite(keyData,
            AnsiColor.BRIGHT_CYAN,
            AnsiColor.BLACK);

        return new SimpleSpriteProvider(keySprite);
    }

    /**
     * Creates a pillar sprite provider (tall decorative object).
     */
    private static SimpleSpriteProvider createPillarSprite() {
        String[] pillarData = {
            "███",
            "█▓█",
            "█▓█",
            "█▓█",
            "█▓█",
            "███"
        };

        Sprite pillarSprite = new SimpleSprite(pillarData,
            AnsiColor.WHITE,
            AnsiColor.BLACK,
            1.2); // Slightly larger scale

        return new SimpleSpriteProvider(pillarSprite);
    }

    /**
     * Handles player interaction with nearby objects.
     */
    private static void handleInteraction(RaycastingCanvas canvas) {
        GameObject closestObject = canvas.getClosestInteractableObject(1.0);

        if (closestObject != null) {
            // Simple interaction: remove the object and print message
            canvas.removeGameObject(closestObject);
            lastAction = "Collected: " + closestObject.getName();
        } else {
            lastAction = "Nothing to interact with";
        }
    }
}
