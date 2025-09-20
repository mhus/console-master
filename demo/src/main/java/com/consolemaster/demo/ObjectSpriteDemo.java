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

/**
 * Demo application showcasing the 8-directional sprite system for game objects
 * in the raycasting engine. Demonstrates different types of objects with various
 * sprite configurations.
 */
public class ObjectSpriteDemo {

    private static String lastAction = "Demo Started";

    public static void main(String[] args) {
        // Create main screen
        ScreenCanvas screen = new ScreenCanvas("Object Sprite Demo", 100, 30);

        // Create raycasting canvas
        RaycastingCanvas raycastingCanvas = new RaycastingCanvas("3D World", 80, 25);

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

        // Create info panel using Box component
        Text infoText = new Text("Controls", 20, 6,
            "WASD: Move/Strafe | Q/E: Rotate | ESC: Exit\n" +
            "Objects showcase 8-directional sprites:\n" +
            "- Red enemies with directional sprites\n" +
            "- Yellow crates (symmetric)\n" +
            "- Brown barrels (round)\n" +
            "- Interactive items (press F near them)"
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
        Composite composite = new Composite("Main", 100, 30);
        composite.addCanvas(raycastingCanvas, PositionConstraint.of(0, 0));
        composite.addCanvas(infoBox, PositionConstraint.of(80, 0));
        composite.addCanvas(statusBox, PositionConstraint.of(80, 25));

        screen.addCanvas(composite, PositionConstraint.CENTER);

        // Create process loop for interactive controls
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

        // Start the demo
        processLoop.start();
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
