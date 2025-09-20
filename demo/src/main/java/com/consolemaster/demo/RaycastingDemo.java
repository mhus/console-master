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
import com.consolemaster.raycasting.DefaultMapProvider;
import com.consolemaster.raycasting.MapProvider;
import com.consolemaster.raycasting.EntryInfo;
import com.consolemaster.raycasting.PictureTextureProvider;
import com.consolemaster.raycasting.RegistryTextureProvider;
import com.consolemaster.raycasting.TilingTextureProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Interactive raycasting demo showcasing first-person 3D perspective in a 2D world.
 * Features player movement, rotation, and different map environments using MapProvider.
 */
@Slf4j
public class RaycastingDemo {

    private static String lastAction = "Demo Started";
    private static RaycastingCanvas raycastingCanvas;
    private static int currentMapIndex = 0;
    private static final char[] wallEdgeStyles = {'│', '#', '*', '+'};
    private static int currentWallEdgeStyleIndex = 0;

    // Different map providers to showcase
    private static final MapProvider[] MAP_PROVIDERS = {
        // Simple map with basic walls and floors
        new DefaultMapProvider("Simple Maze", new String[]{
            "########",
            "#      #",
            "#  ##  #",
            "#      #",
            "########"
        }),

        // Advanced map with different EntryInfo types
        createAdvancedMapProvider(),

        // Textured map with various textures
        createTexturedMapProvider(),

        // Tiling texture demonstration map
        createTilingTextureMapProvider(),

        // Complex maze map
        new DefaultMapProvider("Complex Maze", new String[]{
            "################",
            "#              #",
            "# #### ## #### #",
            "#    #    #    #",
            "#### # ## # ####",
            "#              #",
            "# ## #### ## # #",
            "#  #      #  # #",
            "## # #### # ####",
            "#              #",
            "################"
        }),

        // Natural landscape with water and grass
        createNaturalLandscapeProvider(),

        // Castle dungeon with mixed entry types
        createCastleMapProvider(),

        // Floor texture demonstration map
        createFloorTextureMapProvider(),

        // Checkerboard floor pattern map
        createCheckerboardFloorMapProvider(),

        // Background color demonstration map
        createBackgroundColorDemoMapProvider()
    };

    /**
     * Creates an advanced map provider showcasing different EntryInfo features.
     */
    private static MapProvider createAdvancedMapProvider() {
        EntryInfo[][] map = new EntryInfo[8][12];

        // Initialize with different floor types
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if ((x + y) % 3 == 0) {
                    // Stone floor
                    map[y][x] = EntryInfo.builder()
                            .isWall(false)
                            .isFallthrough(true)
                            .isTransparent(true)
                            .character('.')
                            .name("Stone Floor")
                            .colorLight(AnsiColor.WHITE)
                            .colorDark(AnsiColor.BRIGHT_BLACK)
                            .height(0.0)
                            .build();
                } else if ((x + y) % 3 == 1) {
                    // Grass floor
                    map[y][x] = EntryInfo.builder()
                            .isWall(false)
                            .isFallthrough(true)
                            .isTransparent(true)
                            .character(',')
                            .name("Grass Floor")
                            .colorLight(AnsiColor.BRIGHT_GREEN)
                            .colorDark(AnsiColor.GREEN)
                            .height(0.0)
                            .build();
                } else {
                    // Sand floor
                    map[y][x] = EntryInfo.builder()
                            .isWall(false)
                            .isFallthrough(true)
                            .isTransparent(true)
                            .character('°')
                            .name("Sand Floor")
                            .colorLight(AnsiColor.BRIGHT_YELLOW)
                            .colorDark(AnsiColor.YELLOW)
                            .height(0.0)
                            .build();
                }
            }
        }

        // Create walls around the border using stone walls
        for (int x = 0; x < map[0].length; x++) {
            map[0][x] = EntryInfo.createStoneWall();
            map[map.length - 1][x] = EntryInfo.createStoneWall();
        }
        for (int y = 0; y < map.length; y++) {
            map[y][0] = EntryInfo.createStoneWall();
            map[y][map[0].length - 1] = EntryInfo.createStoneWall();
        }

        // Add some glass walls (transparent but blocking)
        map[2][3] = EntryInfo.createGlass();
        map[2][4] = EntryInfo.createGlass();
        map[2][5] = EntryInfo.createGlass();

        // Add low walls (half height)
        map[4][2] = EntryInfo.createLowWall();
        map[4][3] = EntryInfo.createLowWall();
        map[5][2] = EntryInfo.createLowWall();
        map[5][3] = EntryInfo.createLowWall();

        // Add brick walls with proper light/dark colors
        map[3][7] = EntryInfo.createBrickWall();
        map[3][8] = EntryInfo.createBrickWall();
        map[4][7] = EntryInfo.createBrickWall();
        map[4][8] = EntryInfo.createBrickWall();

        // Add metal walls
        map[6][7] = EntryInfo.createMetalWall();
        map[6][8] = EntryInfo.createMetalWall();

        // Add custom colored walls with specific light/dark combinations
        EntryInfo greenWall = EntryInfo.builder()
                .isWall(true)
                .isFallthrough(false)
                .isTransparent(false)
                .character('█')
                .name("Green Wall")
                .colorLight(AnsiColor.BRIGHT_GREEN)
                .colorDark(AnsiColor.GREEN)
                .height(1.0)
                .build();

        EntryInfo purpleWall = EntryInfo.builder()
                .isWall(true)
                .isFallthrough(false)
                .isTransparent(false)
                .character('▓')
                .name("Purple Wall")
                .colorLight(AnsiColor.MAGENTA)
                .colorDark(AnsiColor.BRIGHT_MAGENTA)
                .height(1.2)
                .build();

        map[6][4] = greenWall;
        map[6][5] = greenWall;
        map[5][9] = purpleWall;
        map[6][9] = purpleWall;

        // Add water area (blue floor)
        EntryInfo water = EntryInfo.builder()
                .isWall(false)
                .isFallthrough(true)
                .isTransparent(true)
                .character('~')
                .name("Water")
                .colorLight(AnsiColor.BRIGHT_CYAN)
                .colorDark(AnsiColor.CYAN)
                .height(0.0)
                .build();

        map[3][9] = water;
        map[3][10] = water;
        map[4][9] = water;
        map[4][10] = water;

        return new DefaultMapProvider("Advanced Features", map);
    }

    /**
     * Creates a castle map with various EntryInfo types.
     */
    private static MapProvider createCastleMapProvider() {
        EntryInfo[][] map = new EntryInfo[12][16];

        // Initialize with floors
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                map[y][x] = EntryInfo.createFloor();
            }
        }

        // Create outer walls with stone
        for (int x = 0; x < map[0].length; x++) {
            map[0][x] = EntryInfo.createStoneWall();
            map[map.length - 1][x] = EntryInfo.createStoneWall();
        }
        for (int y = 0; y < map.length; y++) {
            map[y][0] = EntryInfo.createStoneWall();
            map[y][map[0].length - 1] = EntryInfo.createStoneWall();
        }

        // Add castle features with enhanced colors
        EntryInfo tower = EntryInfo.builder()
                .isWall(true)
                .isFallthrough(false)
                .isTransparent(false)
                .character('♦')
                .name("Tower")
                .colorLight(AnsiColor.BRIGHT_WHITE)
                .colorDark(AnsiColor.WHITE)
                .height(1.5)
                .build();

        EntryInfo gate = EntryInfo.builder()
                .isWall(false)
                .isFallthrough(true)
                .isTransparent(true)
                .character('|')
                .name("Gate")
                .colorLight(AnsiColor.YELLOW)
                .colorDark(AnsiColor.BRIGHT_YELLOW)
                .height(0.8)
                .build();

        EntryInfo courtyard = EntryInfo.builder()
                .isWall(true)
                .isFallthrough(false)
                .isTransparent(false)
                .character('▒')
                .name("Courtyard Wall")
                .colorLight(AnsiColor.CYAN)
                .colorDark(AnsiColor.BLUE)
                .height(0.7)
                .build();

        // Place towers
        map[2][2] = tower;
        map[2][13] = tower;
        map[9][2] = tower;
        map[9][13] = tower;

        // Create gates
        map[5][0] = gate;
        map[6][0] = gate;

        // Add interior walls with different materials
        for (int x = 4; x < 12; x++) {
            if (x != 7 && x != 8) { // Leave doorway
                if (x < 8) {
                    map[5][x] = EntryInfo.createBrickWall();
                } else {
                    map[5][x] = EntryInfo.createMetalWall();
                }
            }
        }

        // Add courtyard walls
        map[7][3] = courtyard;
        map[8][3] = courtyard;
        map[7][12] = courtyard;
        map[8][12] = courtyard;

        return new DefaultMapProvider("Castle", map);
    }

    /**
     * Creates a natural landscape map with water and grass.
     */
    private static MapProvider createNaturalLandscapeProvider() {
        EntryInfo[][] map = new EntryInfo[10][14];

        // Initialize with grass floor
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                map[y][x] = EntryInfo.builder()
                        .isWall(false)
                        .isFallthrough(true)
                        .isTransparent(true)
                        .character(',')
                        .name("Grass Floor")
                        .colorLight(AnsiColor.BRIGHT_GREEN)
                        .colorDark(AnsiColor.GREEN)
                        .height(0.0)
                        .build();
            }
        }

        // Create water areas
        for (int y = 2; y <= 3; y++) {
            for (int x = 4; x <= 9; x++) {
                map[y][x] = EntryInfo.builder()
                        .isWall(false)
                        .isFallthrough(true)
                        .isTransparent(true)
                        .character('~')
                        .name("Water")
                        .colorLight(AnsiColor.BRIGHT_CYAN)
                        .colorDark(AnsiColor.CYAN)
                        .height(0.0)
                        .build();
            }
        }

        // Create island with trees
        for (int y = 1; y <= 8; y++) {
            for (int x = 1; x <= 12; x++) {
                if (x == 1 || x == 12 || y == 1 || y == 8) {
                    map[y][x] = EntryInfo.createStoneWall();
                } else if ((x + y) % 4 == 0) {
                    map[y][x] = EntryInfo.createTree();
                }
            }
        }

        return new DefaultMapProvider("Natural Landscape", map);
    }

    /**
     * Creates a textured map provider showcasing various textures.
     */
    private static MapProvider createTexturedMapProvider() {
        EntryInfo[][] map = new EntryInfo[10][10];

        // Initialize with grass texture
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                map[y][x] = EntryInfo.builder()
                        .isWall(false)
                        .isFallthrough(true)
                        .isTransparent(true)
                        .character(',')
                        .name("Grass")
                        .colorLight(AnsiColor.BRIGHT_GREEN)
                        .colorDark(AnsiColor.GREEN)
                        .height(0.0)
                        .build();
            }
        }

        // Add stone walls around the border
        for (int x = 0; x < map[0].length; x++) {
            map[0][x] = EntryInfo.createStoneWall();
            map[map.length - 1][x] = EntryInfo.createStoneWall();
        }
        for (int y = 0; y < map.length; y++) {
            map[y][0] = EntryInfo.createStoneWall();
            map[y][map[0].length - 1] = EntryInfo.createStoneWall();
        }

        // Add some wooden walls
        for (int i = 2; i < 8; i++) {
            map[i][3] = EntryInfo.createWoodenWall();
            map[i][6] = EntryInfo.createWoodenWall();
        }

        // Add a water area
        for (int y = 4; y <= 5; y++) {
            for (int x = 1; x <= 8; x++) {
                map[y][x] = EntryInfo.builder()
                        .isWall(false)
                        .isFallthrough(true)
                        .isTransparent(true)
                        .character('~')
                        .name("Water")
                        .colorLight(AnsiColor.BRIGHT_CYAN)
                        .colorDark(AnsiColor.CYAN)
                        .height(0.0)
                        .build();
            }
        }

        return new DefaultMapProvider("Textured Map", map);
    }

    /**
     * Creates a tiling texture map provider showcasing various tiling textures.
     */
    private static MapProvider createTilingTextureMapProvider() {
        EntryInfo[][] map = new EntryInfo[12][12];

        // Initialize with grass texture
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                map[y][x] = EntryInfo.builder()
                        .isWall(false)
                        .isFallthrough(true)
                        .isTransparent(true)
                        .character(',')
                        .name("Grass")
                        .colorLight(AnsiColor.BRIGHT_GREEN)
                        .colorDark(AnsiColor.GREEN)
                        .height(0.0)
                        .build();
            }
        }

        // Add stone walls around the border
        for (int x = 0; x < map[0].length; x++) {
            map[0][x] = EntryInfo.createStoneWall();
            map[map.length - 1][x] = EntryInfo.createStoneWall();
        }
        for (int y = 0; y < map.length; y++) {
            map[y][0] = EntryInfo.createStoneWall();
            map[y][map[0].length - 1] = EntryInfo.createStoneWall();
        }

        // Create different sections to showcase tiling textures

        // Left section: Tiling brick walls
        for (int y = 2; y <= 5; y++) {
            map[y][3] = EntryInfo.createTilingBrickWall();
        }

        // Right section: Dotted walls
        for (int y = 2; y <= 5; y++) {
            map[y][8] = EntryInfo.createDottedWall();
        }

        // Center section: Hash pattern walls
        for (int x = 4; x <= 7; x++) {
            map[3][x] = EntryInfo.createHashWall();
        }

        // Bottom section: Wave pattern walls
        for (int x = 4; x <= 7; x++) {
            map[8][x] = EntryInfo.createWaveWall();
        }

        // Add some traditional scaling textured walls for comparison
        map[6][3] = EntryInfo.createWoodenWall(); // Uses scaling texture
        map[6][8] = EntryInfo.createBrickWall();  // Uses scaling texture

        return new DefaultMapProvider("Tiling Texture Demo", map);
    }

    /**
     * Sets up the texture provider for the raycasting canvas.
     */
    private static void setupTextureProvider(RaycastingCanvas canvas) {
        // Create a picture texture provider with various textures (scaling)
        PictureTextureProvider pictureProvider = new PictureTextureProvider();

        // Create a tiling texture provider with repeating patterns
        TilingTextureProvider tilingProvider = new TilingTextureProvider();

        // Add wood texture (scaling)
        String[] woodTexture = {
            "|||###|||",
            "###|||###",
            "|||###|||",
            "###|||###",
            "|||###|||"
        };
        pictureProvider.addTexture("wood", woodTexture);

        // Add brick texture (scaling)
        String[] brickTexture = {
            "##  ##  ##",
            "  ##  ##  ",
            "##  ##  ##",
            "  ##  ##  ",
            "##  ##  ##"
        };
        pictureProvider.addTexture("brick", brickTexture);

        // Add stone texture (scaling)
        String[] stoneTexture = {
            "█▓▒░░▒▓█",
            "▓▒░  ░▒▓",
            "▒░    ░▒",
            "░      ░",
            "▒░    ░▒",
            "▓▒░  ░▒▓",
            "█▓▒░░▒▓█"
        };
        pictureProvider.addTexture("stone", stoneTexture);

        // Add metal texture (scaling)
        String[] metalTexture = {
            "========",
            "||||||||",
            "--------",
            "||||||||",
            "========"
        };
        pictureProvider.addTexture("metal", metalTexture);

        // ===== FLOOR TEXTURES =====

        // Add simple floor texture pattern
        String[] floorTexture = {
            "░░▒▒░░",
            "░▒▓▓▒░",
            "▒▓██▓▒",
            "▒▓██▓▒",
            "░▒▓▓▒░",
            "░░▒▒░░"
        };
        pictureProvider.addTexture("floor", floorTexture);

        // Add stone floor texture
        String[] stoneFloorTexture = {
            "▓▓░░▓▓",
            "▓░  ░▓",
            "░    ░",
            "░    ░",
            "▓░  ░▓",
            "▓▓░░▓▓"
        };
        pictureProvider.addTexture("stone_floor", stoneFloorTexture);

        // Add wooden floor texture
        String[] woodFloorTexture = {
            "|||||||",
            "-------",
            "|||||||",
            "-------",
            "|||||||"
        };
        pictureProvider.addTexture("wood_floor", woodFloorTexture);

        // Add tiling patterns that look better when repeated
        String[] tilingBrickPattern = {
            "██  ██",
            "  ██  ",
            "██  ██"
        };
        tilingProvider.addTexture("tiling_brick", tilingBrickPattern);

        String[] dotPattern = {
            " ● ",
            "   ",
            " ● "
        };
        tilingProvider.addTexture("dots", dotPattern);

        String[] hashPattern = {
            "###",
            "# #",
            "###"
        };
        tilingProvider.addTexture("hash", hashPattern);

        String[] wavePattern = {
            "~~~",
            "   ",
            "~~~"
        };
        tilingProvider.addTexture("wave", wavePattern);

        // Floor tiling patterns
        String[] tilingFloorPattern = {
            "▓▓▓",
            "▓░▓",
            "▓▓▓"
        };
        tilingProvider.addTexture("tiling_floor", tilingFloorPattern);

        String[] checkerFloorPattern = {
            "██",
            "  "
        };
        tilingProvider.addTexture("checker_floor", checkerFloorPattern);

        String[] grassFloorPattern = {
            ",,'",
            "',,"
        };
        tilingProvider.addTexture("grass_floor", grassFloorPattern);

        // Create a combined registry texture provider
        RegistryTextureProvider registryProvider = new RegistryTextureProvider();

        // Register picture textures (scaling)
        registryProvider.addProvider(pictureProvider);

        // Register tiling textures (repeating)
        registryProvider.addProvider(tilingProvider);

        // Set the combined provider
        canvas.setTextureProvider(registryProvider);
    }

    /**
     * Creates a floor texture demonstration map.
     */
    private static MapProvider createFloorTextureMapProvider() {
        EntryInfo[][] map = new EntryInfo[12][16];

        // Initialize with different floor texture areas
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (x < 4) {
                    // Stone floor area
                    map[y][x] = EntryInfo.builder()
                            .isWall(false)
                            .isFallthrough(true)
                            .isTransparent(true)
                            .character('.')
                            .name("Stone Floor")
                            .colorLight(AnsiColor.WHITE)
                            .colorDark(AnsiColor.BRIGHT_BLACK)
                            .height(0.0)
                            .texture("stone_floor")
                            .build();
                } else if (x < 8) {
                    // Wooden floor area
                    map[y][x] = EntryInfo.builder()
                            .isWall(false)
                            .isFallthrough(true)
                            .isTransparent(true)
                            .character('=')
                            .name("Wood Floor")
                            .colorLight(AnsiColor.YELLOW)
                            .colorDark(AnsiColor.BRIGHT_YELLOW)
                            .height(0.0)
                            .texture("wood_floor")
                            .build();
                } else if (x < 12) {
                    // Tiled floor area
                    map[y][x] = EntryInfo.builder()
                            .isWall(false)
                            .isFallthrough(true)
                            .isTransparent(true)
                            .character('▓')
                            .name("Tiled Floor")
                            .colorLight(AnsiColor.CYAN)
                            .colorDark(AnsiColor.BLUE)
                            .height(0.0)
                            .texture("tiling_floor")
                            .build();
                } else {
                    // Grass floor area
                    map[y][x] = EntryInfo.builder()
                            .isWall(false)
                            .isFallthrough(true)
                            .isTransparent(true)
                            .character(',')
                            .name("Grass Floor")
                            .colorLight(AnsiColor.BRIGHT_GREEN)
                            .colorDark(AnsiColor.GREEN)
                            .height(0.0)
                            .texture("grass_floor")
                            .build();
                }
            }
        }

        // Add walls around the border
        for (int x = 0; x < map[0].length; x++) {
            map[0][x] = EntryInfo.createStoneWall();
            map[map.length - 1][x] = EntryInfo.createStoneWall();
        }
        for (int y = 0; y < map.length; y++) {
            map[y][0] = EntryInfo.createStoneWall();
            map[y][map[0].length - 1] = EntryInfo.createStoneWall();
        }

        // Add some interior walls to showcase floor textures better
        for (int y = 3; y <= 8; y++) {
            map[y][4] = EntryInfo.createWoodenWall();
            map[y][8] = EntryInfo.createBrickWall();
            map[y][12] = EntryInfo.createMetalWall();
        }

        // Add doorways
        map[5][4] = EntryInfo.createFloor();
        map[6][4] = EntryInfo.createFloor();
        map[5][8] = EntryInfo.createFloor();
        map[6][8] = EntryInfo.createFloor();
        map[5][12] = EntryInfo.createFloor();
        map[6][12] = EntryInfo.createFloor();

        return new DefaultMapProvider("Floor Textures Demo", map);
    }

    /**
     * Creates a checkerboard floor pattern map.
     */
    private static MapProvider createCheckerboardFloorMapProvider() {
        EntryInfo[][] map = new EntryInfo[10][10];

        // Create checkerboard pattern
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if ((x + y) % 2 == 0) {
                    // White squares
                    map[y][x] = EntryInfo.builder()
                            .isWall(false)
                            .isFallthrough(true)
                            .isTransparent(true)
                            .character('█')
                            .name("White Tile")
                            .colorLight(AnsiColor.BRIGHT_WHITE)
                            .colorDark(AnsiColor.WHITE)
                            .height(0.0)
                            .texture("checker_floor")
                            .build();
                } else {
                    // Black squares
                    map[y][x] = EntryInfo.builder()
                            .isWall(false)
                            .isFallthrough(true)
                            .isTransparent(true)
                            .character('▓')
                            .name("Black Tile")
                            .colorLight(AnsiColor.BRIGHT_BLACK)
                            .colorDark(AnsiColor.BLACK)
                            .height(0.0)
                            .texture("checker_floor")
                            .build();
                }
            }
        }

        // Add walls around the border
        for (int x = 0; x < map[0].length; x++) {
            map[0][x] = EntryInfo.createStoneWall();
            map[map.length - 1][x] = EntryInfo.createStoneWall();
        }
        for (int y = 0; y < map.length; y++) {
            map[y][0] = EntryInfo.createStoneWall();
            map[y][map[0].length - 1] = EntryInfo.createStoneWall();
        }

        // Add a few interior walls
        map[3][3] = EntryInfo.createBrickWall();
        map[3][4] = EntryInfo.createBrickWall();
        map[3][5] = EntryInfo.createBrickWall();
        map[6][3] = EntryInfo.createWoodenWall();
        map[6][4] = EntryInfo.createWoodenWall();
        map[6][5] = EntryInfo.createWoodenWall();

        return new DefaultMapProvider("Checkerboard Floor", map);
    }

    /**
     * Creates a map provider to demonstrate background colors.
     */
    private static MapProvider createBackgroundColorDemoMapProvider() {
        EntryInfo[][] map = new EntryInfo[10][12];

        // Initialize with floors having different background colors
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (x == 0 || x == map[y].length - 1 || y == 0 || y == map.length - 1) {
                    // Border walls with background colors
                    map[y][x] = EntryInfo.builder()
                            .isWall(true)
                            .isFallthrough(false)
                            .isTransparent(false)
                            .character('█')
                            .name("Background Wall")
                            .colorLight(AnsiColor.WHITE)
                            .colorDark(AnsiColor.BRIGHT_BLACK)
                            .backgroundColorLight(AnsiColor.BLUE)
                            .backgroundColorDark(AnsiColor.CYAN)
                            .height(1.0)
                            .build();
                } else {
                    // Floor with different background color patterns
                    AnsiColor bgColor = switch ((x + y) % 6) {
                        case 0 -> AnsiColor.RED;
                        case 1 -> AnsiColor.GREEN;
                        case 2 -> AnsiColor.BLUE;
                        case 3 -> AnsiColor.YELLOW;
                        case 4 -> AnsiColor.MAGENTA;
                        default -> AnsiColor.CYAN;
                    };

                    map[y][x] = EntryInfo.builder()
                            .isWall(false)
                            .isFallthrough(true)
                            .isTransparent(true)
                            .character('·')
                            .name("Colored Floor")
                            .colorLight(AnsiColor.WHITE)
                            .colorDark(AnsiColor.BRIGHT_WHITE)
                            .backgroundColorLight(bgColor)
                            .backgroundColorDark(bgColor)
                            .height(0.0)
                            .build();
                }
            }
        }

        // Add some special walls with different background colors
        EntryInfo redBgWall = EntryInfo.builder()
                .isWall(true)
                .isFallthrough(false)
                .isTransparent(false)
                .character('▓')
                .name("Red Background Wall")
                .colorLight(AnsiColor.BRIGHT_YELLOW)
                .colorDark(AnsiColor.YELLOW)
                .backgroundColorLight(AnsiColor.RED)
                .backgroundColorDark(AnsiColor.BRIGHT_RED)
                .height(1.0)
                .build();

        EntryInfo greenBgWall = EntryInfo.builder()
                .isWall(true)
                .isFallthrough(false)
                .isTransparent(false)
                .character('▒')
                .name("Green Background Wall")
                .colorLight(AnsiColor.BRIGHT_WHITE)
                .colorDark(AnsiColor.WHITE)
                .backgroundColorLight(AnsiColor.GREEN)
                .backgroundColorDark(AnsiColor.BRIGHT_GREEN)
                .height(1.2)
                .build();

        EntryInfo purpleBgWall = EntryInfo.builder()
                .isWall(true)
                .isFallthrough(false)
                .isTransparent(false)
                .character('░')
                .name("Purple Background Wall")
                .colorLight(AnsiColor.BLACK)
                .colorDark(AnsiColor.BRIGHT_BLACK)
                .backgroundColorLight(AnsiColor.MAGENTA)
                .backgroundColorDark(AnsiColor.BRIGHT_MAGENTA)
                .height(0.8)
                .build();

        // Place special background walls
        map[3][3] = redBgWall;
        map[3][4] = redBgWall;
        map[6][8] = greenBgWall;
        map[7][8] = greenBgWall;
        map[5][5] = purpleBgWall;
        map[5][6] = purpleBgWall;

        // Add glass walls with background colors
        EntryInfo glassBg = EntryInfo.builder()
                .isWall(true)
                .isFallthrough(false)
                .isTransparent(true)
                .character('|')
                .name("Colored Glass")
                .colorLight(AnsiColor.BRIGHT_CYAN)
                .colorDark(AnsiColor.CYAN)
                .backgroundColorLight(AnsiColor.BLUE)
                .backgroundColorDark(AnsiColor.BRIGHT_BLUE)
                .height(1.0)
                .build();

        map[2][7] = glassBg;
        map[2][8] = glassBg;
        map[8][3] = glassBg;
        map[8][4] = glassBg;

        return new DefaultMapProvider("Background Colors Demo", map);
    }

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
                "Raycasting Demo - First Person 3D World (MapProvider)\n" +
                "WASD: Move | Arrows: Rotate/Fine Move | M: Change Map | R: Reset\n" +
                "E: Toggle Wall Edges | T: Edge Threshold | C: Edge Style | Q/ESC: Exit",
                Text.Alignment.CENTER);
            headerText.setForegroundColor(AnsiColor.BRIGHT_CYAN);
            headerText.setBold(true);
            headerBox.setContent(headerText);
            headerBox.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.TOP_CENTER));

            // Create raycasting canvas with MapProvider
            raycastingCanvas = new RaycastingCanvas("Raycasting World", 0, 0, MAP_PROVIDERS[currentMapIndex]);
            raycastingCanvas.setPlayerPosition(2.5, 2.5);
            raycastingCanvas.setWallColor(AnsiColor.WHITE);
            raycastingCanvas.setFloorColor(AnsiColor.YELLOW);
            raycastingCanvas.setCeilingColor(AnsiColor.BLUE);
            raycastingCanvas.setLayoutConstraint(new PositionConstraint(PositionConstraint.Position.CENTER));

            // Setup texture provider
            setupTextureProvider(raycastingCanvas);

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
                // Update status text with MapProvider information
                statusText.setText(String.format(
                    "Position: (%.1f, %.1f) | Angle: %.0f° | %s | Map: %s (%d/%d)",
                    raycastingCanvas.getPlayerX(),
                    raycastingCanvas.getPlayerY(),
                    Math.toDegrees(raycastingCanvas.getPlayerAngle()),
                    lastAction,
                    raycastingCanvas.getMapProvider().getName(),
                    currentMapIndex + 1,
                    MAP_PROVIDERS.length
                ));
            });

            log.info("Starting Raycasting Demo with MapProvider...");
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

        // Wall edge controls
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

        // Map change - now uses MapProvider
        screen.registerShortcut("M", () -> {
            currentMapIndex = (currentMapIndex + 1) % MAP_PROVIDERS.length;
            raycastingCanvas.setMapProvider(MAP_PROVIDERS[currentMapIndex]);
            raycastingCanvas.setPlayerPosition(2.5, 2.5);
            lastAction = "Changed to " + MAP_PROVIDERS[currentMapIndex].getName();
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
