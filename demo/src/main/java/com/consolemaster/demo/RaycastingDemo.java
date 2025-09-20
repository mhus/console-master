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
import com.consolemaster.raycasting.RaycastingCanvas;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Interactive raycasting demo showcasing first-person 3D perspective in a 2D world.
 * Features player movement, rotation, and different map environments.
 */
@Slf4j
public class RaycastingDemo {

    private static String lastAction = "Demo Started";
    private static RaycastingCanvas raycastingCanvas;
    private static int currentMapIndex = 0;
    private static char[] wallEdgeStyles = {'│', '#', '*', '+'};
    private static int currentWallEdgeStyleIndex = 0;

    // Different maps to showcase
    private static final String[][] MAPS = {
        // Default simple map
        {
            "########",
            "#      #",
            "#  ##  #",
            "#      #",
            "########"
        },
        // Maze map
        {
            "################",
            "#              #",
            "# #### ## #### #",
            "#    #    #    #",
            "#### # ## # ####",
            "#              #",
            "# ## #### ## # #",
            "#  #      #  # #",
            "## # #### # ##",
            "#              #",
            "################"
        },
        // Complex map
        {
            "####################",
            "#                  #",
            "#  ###  ####  ###  #",
            "#  #              # #",
            "#  #  ##########  # #",
            "#     #        #    #",
            "##### # ###### # ####",
            "#       #    #      #",
            "#  ####  ####  #### #",
            "#                  #",
            "####################"
        },
        // Large complex castle/dungeon map
        {
            "################################################################################",
            "#                                                                              #",
            "#  ########    #############    ########    #############    ########        #",
            "#  #      #    #           #    #      #    #           #    #      #        #",
            "#  #  ##  #    #  #######  #    #  ##  #    #  #######  #    #  ##  #        #",
            "#  #  ##  #    #  #     #  #    #  ##  #    #  #     #  #    #  ##  #        #",
            "#  #      #    #  #  #  #  #    #      #    #  #  #  #  #    #      #        #",
            "#  ########    #  #  #  #  #    ########    #  #  #  #  #    ########        #",
            "#              #  #  #  #  #                #  #  #  #  #                    #",
            "#  ############   #  #  #  #################   #  #  #  #################    #",
            "#  #          #   #     #                  #   #     #                  #    #",
            "#  #  ######  #   #######  ##############  #   #######  ##############  #    #",
            "#  #  #    #  #            #            #  #            #            #  #    #",
            "#  #  #    #  ##############  ########  ################  ########  ####    #",
            "#  #  #    #                  #      #                    #      #          #",
            "#  #  ######  ################  ####  ################    ######  ##########",
            "#  #          #              #  #  #  #              #          #          #",
            "#  ############  ##########  #  #  #  #  ##########  ############  ######  #",
            "#               #        #   #     #  #   #        #               #    #  #",
            "#  #############  ######  #  #######  #  #  ######  #############  #    #  #",
            "#  #           #  #    #  #           #  #  #    #  #           #  #    #  #",
            "#  #  #######  #  #    #  #############  #  #    #  #  #######  #  ######  #",
            "#  #  #     #  #  #    #                 #  #    #  #  #     #  #          #",
            "#  #  #  #  #  #  ######  ###############  ######  #  #  #  #  ############",
            "#  #  #  #  #  #          #             #          #  #  #  #              #",
            "#  #  #  #  #  ############  #########  ############  #  #  #  ############",
            "#  #     #  #               #         #               #  #     #          #",
            "#  #######  #################  #####  #################  #######  ######  #",
            "#                            #       #                            #    #  #",
            "#  ##########################  #####  ##########################  #    #  #",
            "#  #                        #       #                        #  #    #  #",
            "#  #  ####################  #  ###  #  ####################  #  ######  #",
            "#  #  #                  #  #   #   #  #                  #  #          #",
            "#  #  #  ##############  #  #####   #  #  ##############  #  ############",
            "#  #  #  #            #  #           #  #  #            #  #              #",
            "#  #  #  #  ########  #  #############  #  #  ########  #  ##############",
            "#  #  #  #  #      #  #                 #  #  #      #  #              #",
            "#  #  #  #  #  ##  #  ###################  #  #  ##  #  ##############  #",
            "#  #  #  #  #  ##  #                       #  #  ##  #              #  #",
            "#  #  #  #  #      #########################  #      ##############  #  #",
            "#  #  #  #  ########                          ########            #  #  #",
            "#  #  #  #                                                        #  #  #",
            "#  #  #  ##########################################################  #  #",
            "#  #  #                                                              #  #",
            "#  #  ################################################################  #",
            "#  #                                                                    #",
            "#  ######################################################################",
            "#                                                                        #",
            "##########################################################################"
        }
    };

    public static void main(String[] args) {
        try {
            // Create the main screen canvas
            ScreenCanvas screen = new ScreenCanvas(80, 25);

            // Create main container with BorderLayout
            Composite mainContainer = new Composite("mainContainer",
                    screen.getWidth() - 4,
                    screen.getHeight() - 4,
                    new BorderLayout(1));

            // Create header
            Box headerBox = new Box("headerBox", 0, 5, new DefaultBorder());
            Text headerText = new Text("headerText", 0, 0,
                "Raycasting Demo - First Person 3D World\n" +
                "WASD: Move | Arrows: Rotate/Fine Move | M: Change Map | R: Reset\n" +
                "E: Toggle Wall Edges | T: Edge Threshold | C: Edge Style | Q/ESC: Exit",
                Text.Alignment.CENTER);
            headerText.setForegroundColor(AnsiColor.BRIGHT_CYAN);
            headerText.setBold(true);
            headerBox.setContent(headerText);
            headerBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.TOP_CENTER));

            // Create raycasting canvas
            raycastingCanvas = new RaycastingCanvas("Raycasting World", 0, 0);
            raycastingCanvas.setMap(MAPS[currentMapIndex]);
            raycastingCanvas.setPlayerPosition(2.5, 2.5);
            raycastingCanvas.setWallColor(AnsiColor.WHITE);
            raycastingCanvas.setFloorColor(AnsiColor.YELLOW);
            raycastingCanvas.setCeilingColor(AnsiColor.BLUE);
            raycastingCanvas.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER));

            // Create status panel
            Text statusText = new Text("statusText", 0, 0, "", Text.Alignment.LEFT);
            statusText.setForegroundColor(AnsiColor.WHITE);
            Box statusBox = new Box("statusBox", 0, 3, new DefaultBorder());
            statusBox.setContent(statusText);
            statusBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.BOTTOM_CENTER));

            // Add all components to main container
            mainContainer.addChild(headerBox);
            mainContainer.addChild(raycastingCanvas);
            mainContainer.addChild(statusBox);

            // Position the main container
            screen.setContent(mainContainer);

            // Register keyboard shortcuts
            registerKeyboardControls(screen);

            // Create and start the process loop
            ProcessLoop processLoop = new ProcessLoop(screen);
            processLoop.setUpdateCallback(() -> {
                // Update status text
                statusText.setText(String.format(
                    "Position: (%.1f, %.1f) | Angle: %.0f° | %s | Map: %d/%d",
                    raycastingCanvas.getPlayerX(),
                    raycastingCanvas.getPlayerY(),
                    Math.toDegrees(raycastingCanvas.getPlayerAngle()),
                    lastAction,
                    currentMapIndex + 1,
                    MAPS.length
                ));
            });

            log.info("Starting Raycasting Demo...");
            processLoop.start();

        } catch (IOException e) {
            log.error("Error initializing Raycasting Demo", e);
        }
    }

    private static void registerKeyboardControls(ScreenCanvas screen) {
        double moveSpeed = 0.1;
        double rotateSpeed = 0.1;

        // Movement controls
        screen.registerShortcut("W", () -> {
            raycastingCanvas.movePlayer(moveSpeed);
            lastAction = "Move Forward";
        });

        screen.registerShortcut("S", () -> {
            raycastingCanvas.movePlayer(-moveSpeed);
            lastAction = "Move Backward";
        });

        screen.registerShortcut("A", () -> {
            raycastingCanvas.strafePlayer(-moveSpeed);
            lastAction = "Strafe Left";
        });

        screen.registerShortcut("D", () -> {
            raycastingCanvas.strafePlayer(moveSpeed);
            lastAction = "Strafe Right";
        });

        // Rotation controls
        screen.registerShortcut(KeyEvent.SpecialKey.ARROW_LEFT.name(), () -> {
            raycastingCanvas.rotatePlayer(-rotateSpeed);
            lastAction = "Rotate Left";
        });

        screen.registerShortcut(KeyEvent.SpecialKey.ARROW_RIGHT.name(), () -> {
            raycastingCanvas.rotatePlayer(rotateSpeed);
            lastAction = "Rotate Right";
        });

        // Wall edge controls - basic version without getter methods
        screen.registerShortcut("E", () -> {
            raycastingCanvas.setDrawWallEdges(!raycastingCanvas.isDrawWallEdges());
            lastAction = "Wall Edges toggled";
        });

        screen.registerShortcut("T", () -> {
            raycastingCanvas.setWallEdgeThreshold(
                raycastingCanvas.getWallEdgeThreshold() + 0.1 > 1.0 ? 0.0 : raycastingCanvas.getWallEdgeThreshold() + 0.1
            );
            lastAction = "Edge Threshold changed";
        });

        screen.registerShortcut("C", () -> {
            currentWallEdgeStyleIndex = (currentWallEdgeStyleIndex + 1) % wallEdgeStyles.length;
            raycastingCanvas.setWallEdgeChar(wallEdgeStyles[currentWallEdgeStyleIndex]);
            lastAction = "Edge Character changed";
        });

        // Map change
        screen.registerShortcut("M", () -> {
            currentMapIndex = (currentMapIndex + 1) % MAPS.length;
            raycastingCanvas.setMap(MAPS[currentMapIndex]);
            raycastingCanvas.setPlayerPosition(2.5, 2.5);
            lastAction = "Changed to Map " + (currentMapIndex + 1);
        });

        // Reset player
        screen.registerShortcut("R", () -> {
            raycastingCanvas.setPlayerPosition(2.5, 2.5);
            raycastingCanvas.setPlayerAngle(0.0);
            lastAction = "Player Reset";
        });

        // Fine movement controls
        screen.registerShortcut(KeyEvent.SpecialKey.ARROW_UP.name(), () -> {
            raycastingCanvas.movePlayer(moveSpeed * 0.5);
            lastAction = "Move Forward (Slow)";
        });

        screen.registerShortcut(KeyEvent.SpecialKey.ARROW_DOWN.name(), () -> {
            raycastingCanvas.movePlayer(-moveSpeed * 0.5);
            lastAction = "Move Backward (Slow)";
        });

        // Exit
        screen.registerShortcut("Q", () -> {
            log.info("Exiting Raycasting Demo...");
            System.exit(0);
        });

        screen.registerShortcut(KeyEvent.SpecialKey.ESCAPE.name(), () -> {
            log.info("Exiting Raycasting Demo...");
            System.exit(0);
        });
    }

    private static Box createControlButton(String text, AnsiColor color, Runnable action) {
        Text buttonText = new Text("buttonText", 0, 0, text, Text.Alignment.CENTER);
        buttonText.setForegroundColor(color);
        buttonText.setBold(true);

        Box button = new Box("button", 12, 3, new DefaultBorder());
        button.setContent(buttonText);

        return button;
    }
}
