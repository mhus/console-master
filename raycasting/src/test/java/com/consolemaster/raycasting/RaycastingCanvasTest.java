package com.consolemaster.raycasting;

import com.consolemaster.AnsiColor;
import com.consolemaster.StyledChar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for RaycastingCanvas with EntryInfo system.
 */
class RaycastingCanvasTest {

    private RaycastingCanvas canvas;
    private MapProvider testMapProvider;

    @BeforeEach
    void setUp() {
        // Create a simple test map
        String[] testMap = {
            "####",
            "#  #",
            "#  #",
            "####"
        };
        testMapProvider = new DefaultMapProvider("Test Map", testMap);
        canvas = new RaycastingCanvas("test", 80, 25, testMapProvider);
    }

    @Test
    void testMapProviderIntegration() {
        assertNotNull(canvas.getMapProvider());
        assertEquals("Test Map", canvas.getMapProvider().getName());
        assertEquals(4, canvas.getMapProvider().getWidth());
        assertEquals(4, canvas.getMapProvider().getHeight());
    }

    @Test
    void testEntryInfoWallDetection() {
        // Test wall detection using EntryInfo
        EntryInfo wallEntry = canvas.getMapProvider().getEntry(0, 0);
        assertTrue(wallEntry.isWall());
        assertFalse(wallEntry.isWalkThrough());

        EntryInfo floorEntry = canvas.getMapProvider().getEntry(1, 1);
        assertFalse(floorEntry.isWall());
        assertTrue(floorEntry.isWalkThrough());
    }

    @Test
    void testPlayerMovementWithEntryInfo() {
        // Set player in valid position
        canvas.setPlayerPosition(1.5, 1.5);
        assertEquals(1.5, canvas.getPlayerX(), 0.01);
        assertEquals(1.5, canvas.getPlayerY(), 0.01);

        // Test valid position check using EntryInfo.isFallthrough()
        assertTrue(canvas.isValidPosition(1.5, 1.5)); // Floor
        assertFalse(canvas.isValidPosition(0.5, 0.5)); // Wall
    }

    @Test
    void testEntryInfoFromCharacterConversion() {
        // Test conversion from legacy characters to EntryInfo
        EntryInfo wallInfo = EntryInfo.fromCharacter('#');
        assertTrue(wallInfo.isWall());
        assertFalse(wallInfo.isWalkThrough());
        assertEquals("Wall", wallInfo.getName());

        EntryInfo floorInfo = EntryInfo.fromCharacter(' ');
        assertFalse(floorInfo.isWall());
        assertTrue(floorInfo.isWalkThrough());
        assertEquals("Empty", floorInfo.getName());
    }

    @Test
    void testAdvancedMapProviderWithCustomEntries() {
        // Create map with custom EntryInfo objects
        EntryInfo[][] customMap = new EntryInfo[3][3];

        // Fill with walls
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                customMap[y][x] = EntryInfo.createWall();
            }
        }

        // Add glass wall in center
        customMap[1][1] = EntryInfo.createGlass();

        MapProvider customProvider = new DefaultMapProvider("Custom", customMap);
        canvas.setMapProvider(customProvider);

        // Test glass properties
        EntryInfo glassEntry = canvas.getMapProvider().getEntry(1, 1);
        assertTrue(glassEntry.isWall()); // Blocks movement
        assertFalse(glassEntry.isWalkThrough()); // Can't walk through
        assertTrue(glassEntry.isTransparent()); // But is transparent
        assertEquals("Glass", glassEntry.getName());
    }

    @Test
    void testEntryInfoColors() {
        // Test single color (backward compatibility)
        EntryInfo redWall = EntryInfo.builder()
                .isWall(true)
                .colorLight(AnsiColor.RED)
                .character('█')
                .name("Red Wall")
                .build();

        assertEquals(AnsiColor.RED, redWall.getColorLight());
        assertEquals(AnsiColor.RED, redWall.getColor(false)); // Light color
        assertEquals(AnsiColor.RED, redWall.getColor(true)); // Falls back to light color when dark is not set
        assertNull(redWall.getColorDark()); // Dark color not explicitly set
        assertEquals('█', redWall.getCharacter());
        assertTrue(redWall.isWall());

        // Test light and dark colors
        EntryInfo dualColorWall = EntryInfo.builder()
                .isWall(true)
                .colorLight(AnsiColor.BRIGHT_GREEN)
                .colorDark(AnsiColor.GREEN)
                .character('█')
                .name("Dual Color Wall")
                .build();

        assertEquals(AnsiColor.BRIGHT_GREEN, dualColorWall.getColor(false)); // Light
        assertEquals(AnsiColor.GREEN, dualColorWall.getColor(true)); // Dark
        assertEquals(AnsiColor.BRIGHT_GREEN, dualColorWall.getColor()); // Default to light

        // Test backward compatibility setter
        EntryInfo compatWall = EntryInfo.builder().build();
        compatWall.setColor(AnsiColor.BLUE);
        assertEquals(AnsiColor.BLUE, compatWall.getColorLight());
        assertEquals(AnsiColor.BLUE, compatWall.getColorDark());
        assertEquals(AnsiColor.BLUE, compatWall.getColor(false));
        assertEquals(AnsiColor.BLUE, compatWall.getColor(true));

        // Test only dark color set (edge case)
        EntryInfo darkOnlyWall = EntryInfo.builder()
                .colorDark(AnsiColor.BLACK)
                .build();
        assertEquals(AnsiColor.BLACK, darkOnlyWall.getColor(true)); // Dark color
        assertEquals(AnsiColor.BLACK, darkOnlyWall.getColor(false)); // Falls back to dark when light not set
        assertNull(darkOnlyWall.getColorLight());
        assertEquals(AnsiColor.BLACK, darkOnlyWall.getColorDark());
    }

    @Test
    void testPredefinedWallTypes() {
        // Test stone wall
        EntryInfo stoneWall = EntryInfo.createStoneWall();
        assertTrue(stoneWall.isWall());
        assertFalse(stoneWall.isWalkThrough());
        assertEquals("Stone Wall", stoneWall.getName());
        assertEquals(AnsiColor.WHITE, stoneWall.getColorLight());
        assertEquals(AnsiColor.BRIGHT_BLACK, stoneWall.getColorDark());

        // Test brick wall
        EntryInfo brickWall = EntryInfo.createBrickWall();
        assertTrue(brickWall.isWall());
        assertEquals("Brick Wall", brickWall.getName());
        assertEquals(AnsiColor.RED, brickWall.getColorLight());

        // Test metal wall
        EntryInfo metalWall = EntryInfo.createMetalWall();
        assertTrue(metalWall.isWall());
        assertEquals("Metal Wall", metalWall.getName());
        assertEquals(AnsiColor.BRIGHT_WHITE, metalWall.getColorLight());

        // Test tree
        EntryInfo tree = EntryInfo.createTree();
        assertTrue(tree.isWall());
        assertEquals("Tree", tree.getName());
        assertEquals('♠', tree.getCharacter());
        assertEquals(1.3, tree.getHeight(), 0.01);

        // Test wooden wall (with texture reference)
        EntryInfo woodenWall = EntryInfo.createWoodenWall();
        assertTrue(woodenWall.isWall());
        assertEquals("Wooden Wall", woodenWall.getName());
        assertEquals("wood", woodenWall.getTexture());
        assertEquals(AnsiColor.YELLOW, woodenWall.getColorLight());
    }

    @Test
    void testTextureProvider() {
        // Test setting texture provider
        PictureTextureProvider textureProvider = new PictureTextureProvider();

        // Add a test texture
        String[] testTexture = {
            "###",
            "   ",
            "###"
        };
        textureProvider.addTexture("test", testTexture);

        canvas.setTextureProvider(textureProvider);
        assertNotNull(canvas.getTextureProvider());

        // Test texture retrieval (requires accessing texture provider functionality)
        Texture texture = textureProvider.getTexture("test", 1, 10, null, false);
        assertNotNull(texture);
        assertEquals(1, texture.getWidth());
        assertEquals(10, texture.getHeight());
    }

    @Test
    void testBackgroundColors() {
        // Test EntryInfo with background colors
        EntryInfo bgColorWall = EntryInfo.builder()
                .isWall(true)
                .character('█')
                .name("Background Color Wall")
                .colorLight(AnsiColor.WHITE)
                .colorDark(AnsiColor.BRIGHT_BLACK)
                .backgroundColorLight(AnsiColor.RED)
                .backgroundColorDark(AnsiColor.BLUE)
                .build();

        // Test background color getters
        assertEquals(AnsiColor.RED, bgColorWall.getBackgroundColor(false)); // Light background
        assertEquals(AnsiColor.BLUE, bgColorWall.getBackgroundColor(true)); // Dark background
        assertEquals(AnsiColor.RED, bgColorWall.getBackgroundColor()); // Default to light

        // Test only light background color set
        EntryInfo lightBgOnly = EntryInfo.builder()
                .backgroundColorLight(AnsiColor.GREEN)
                .build();
        assertEquals(AnsiColor.GREEN, lightBgOnly.getBackgroundColor(false)); // Light
        assertEquals(AnsiColor.GREEN, lightBgOnly.getBackgroundColor(true)); // Falls back to light
        assertNull(lightBgOnly.getBackgroundColorDark());

        // Test only dark background color set
        EntryInfo darkBgOnly = EntryInfo.builder()
                .backgroundColorDark(AnsiColor.YELLOW)
                .build();
        assertEquals(AnsiColor.YELLOW, darkBgOnly.getBackgroundColor(true)); // Dark
        assertEquals(AnsiColor.YELLOW, darkBgOnly.getBackgroundColor(false)); // Falls back to dark
        assertNull(darkBgOnly.getBackgroundColorLight());

        // Test no background colors set
        EntryInfo noBgColor = EntryInfo.builder().build();
        assertNull(noBgColor.getBackgroundColor(false));
        assertNull(noBgColor.getBackgroundColor(true));
        assertNull(noBgColor.getBackgroundColor());

        // Test backward compatibility setter
        EntryInfo compatBgWall = EntryInfo.builder().build();
        compatBgWall.setBackgroundColor(AnsiColor.MAGENTA);
        assertEquals(AnsiColor.MAGENTA, compatBgWall.getBackgroundColorLight());
        assertEquals(AnsiColor.MAGENTA, compatBgWall.getBackgroundColorDark());
        assertEquals(AnsiColor.MAGENTA, compatBgWall.getBackgroundColor(false));
        assertEquals(AnsiColor.MAGENTA, compatBgWall.getBackgroundColor(true));
    }

    @Test
    void testBackgroundColorMapProvider() {
        // Create a map with background colors
        EntryInfo[][] bgColorMap = new EntryInfo[3][3];

        // Fill with floors having different background colors
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                AnsiColor bgColor = switch ((x + y) % 3) {
                    case 0 -> AnsiColor.RED;
                    case 1 -> AnsiColor.GREEN;
                    default -> AnsiColor.BLUE;
                };

                bgColorMap[y][x] = EntryInfo.builder()
                        .isWall(false)
                        .isWalkThrough(true)
                        .character('.')
                        .name("Colored Floor")
                        .colorLight(AnsiColor.WHITE)
                        .backgroundColorLight(bgColor)
                        .backgroundColorDark(bgColor)
                        .build();
            }
        }

        // Add walls with background colors on borders
        for (int x = 0; x < 3; x++) {
            bgColorMap[0][x] = EntryInfo.builder()
                    .isWall(true)
                    .character('█')
                    .name("Wall with Background")
                    .colorLight(AnsiColor.WHITE)
                    .backgroundColorLight(AnsiColor.CYAN)
                    .backgroundColorDark(AnsiColor.BLUE)
                    .build();
            bgColorMap[2][x] = EntryInfo.builder()
                    .isWall(true)
                    .character('█')
                    .name("Wall with Background")
                    .colorLight(AnsiColor.WHITE)
                    .backgroundColorLight(AnsiColor.CYAN)
                    .backgroundColorDark(AnsiColor.BLUE)
                    .build();
        }
        for (int y = 0; y < 3; y++) {
            bgColorMap[y][0] = EntryInfo.builder()
                    .isWall(true)
                    .character('█')
                    .name("Wall with Background")
                    .colorLight(AnsiColor.WHITE)
                    .backgroundColorLight(AnsiColor.CYAN)
                    .backgroundColorDark(AnsiColor.BLUE)
                    .build();
            bgColorMap[y][2] = EntryInfo.builder()
                    .isWall(true)
                    .character('█')
                    .name("Wall with Background")
                    .colorLight(AnsiColor.WHITE)
                    .backgroundColorLight(AnsiColor.CYAN)
                    .backgroundColorDark(AnsiColor.BLUE)
                    .build();
        }

        MapProvider bgColorProvider = new DefaultMapProvider("Background Color Map", bgColorMap);
        canvas.setMapProvider(bgColorProvider);

        // Test that the map provider has entries with background colors
        EntryInfo floorEntry = canvas.getMapProvider().getEntry(1, 1);
        assertNotNull(floorEntry.getBackgroundColor());
        assertFalse(floorEntry.isWall());

        EntryInfo wallEntry = canvas.getMapProvider().getEntry(0, 0);
        assertNotNull(wallEntry.getBackgroundColor());
        assertTrue(wallEntry.isWall());
        assertEquals(AnsiColor.CYAN, wallEntry.getBackgroundColor(false));
        assertEquals(AnsiColor.BLUE, wallEntry.getBackgroundColor(true));
    }

    @Test
    void testPredefinedWallTypesWithBackgroundColors() {
        // Test that existing predefined wall types work with background color system
        EntryInfo stoneWall = EntryInfo.createStoneWall();
        assertNull(stoneWall.getBackgroundColor()); // Should be null by default
        assertNull(stoneWall.getBackgroundColorLight());
        assertNull(stoneWall.getBackgroundColorDark());

        // Test creating custom wall with background colors
        EntryInfo customBgWall = EntryInfo.builder()
                .isWall(true)
                .isWalkThrough(false)
                .isTransparent(false)
                .character('▓')
                .name("Custom Background Wall")
                .colorLight(AnsiColor.BRIGHT_YELLOW)
                .colorDark(AnsiColor.YELLOW)
                .backgroundColorLight(AnsiColor.RED)
                .backgroundColorDark(AnsiColor.BRIGHT_RED)
                .height(1.0)
                .build();

        assertTrue(customBgWall.isWall());
        assertEquals(AnsiColor.BRIGHT_YELLOW, customBgWall.getColor(false));
        assertEquals(AnsiColor.YELLOW, customBgWall.getColor(true));
        assertEquals(AnsiColor.RED, customBgWall.getBackgroundColor(false));
        assertEquals(AnsiColor.BRIGHT_RED, customBgWall.getBackgroundColor(true));
    }

    @Test
    void testGlassWallWithBackgroundColors() {
        // Test glass wall with background colors for special effects
        EntryInfo coloredGlass = EntryInfo.builder()
                .isWall(true)
                .isWalkThrough(false)
                .isTransparent(true)
                .character('|')
                .name("Colored Glass")
                .colorLight(AnsiColor.BRIGHT_CYAN)
                .colorDark(AnsiColor.CYAN)
                .backgroundColorLight(AnsiColor.BLUE)
                .backgroundColorDark(AnsiColor.BRIGHT_BLUE)
                .height(1.0)
                .build();

        assertTrue(coloredGlass.isWall());
        assertFalse(coloredGlass.isWalkThrough());
        assertTrue(coloredGlass.isTransparent()); // Transparent but has background color
        assertEquals(AnsiColor.BLUE, coloredGlass.getBackgroundColor(false));
        assertEquals(AnsiColor.BRIGHT_BLUE, coloredGlass.getBackgroundColor(true));
    }

    @Test
    void testWallHeightFeature() {
        // Test minimum height enforcement
        EntryInfo belowMinWall = EntryInfo.builder()
                .height(0.2) // Below minimum
                .build();

        assertEquals(0.2, belowMinWall.getHeight(), 0.01); // Raw value stored
        assertEquals(EntryInfo.MIN_HEIGHT, belowMinWall.getEffectiveHeight(), 0.01); // Effective height clamped

        // Test normal height
        EntryInfo normalWall = EntryInfo.builder()
                .height(1.5)
                .build();

        assertEquals(1.5, normalWall.getHeight(), 0.01);
        assertEquals(1.5, normalWall.getEffectiveHeight(), 0.01);

        // Test minimum height exactly
        EntryInfo minWall = EntryInfo.builder()
                .height(EntryInfo.MIN_HEIGHT)
                .build();

        assertEquals(EntryInfo.MIN_HEIGHT, minWall.getHeight(), 0.01);
        assertEquals(EntryInfo.MIN_HEIGHT, minWall.getEffectiveHeight(), 0.01);

        // Test height setter with validation
        EntryInfo testWall = EntryInfo.builder().build();
        testWall.setHeight(0.1); // Below minimum
        assertEquals(EntryInfo.MIN_HEIGHT, testWall.getHeight(), 0.01); // Should be clamped

        testWall.setHeight(2.0); // Above minimum
        assertEquals(2.0, testWall.getHeight(), 0.01); // Should be preserved
    }

    @Test
    void testPredefinedWallHeights() {
        // Test that predefined walls have correct heights
        EntryInfo wall = EntryInfo.createWall();
        assertEquals(1.0, wall.getHeight(), 0.01);
        assertEquals(1.0, wall.getEffectiveHeight(), 0.01);

        EntryInfo lowWall = EntryInfo.createLowWall();
        assertEquals(0.5, lowWall.getHeight(), 0.01);
        assertEquals(EntryInfo.MIN_HEIGHT, lowWall.getEffectiveHeight(), 0.01);

        EntryInfo tree = EntryInfo.createTree();
        assertEquals(1.3, tree.getHeight(), 0.01);
        assertEquals(1.3, tree.getEffectiveHeight(), 0.01);

        EntryInfo floor = EntryInfo.createFloor();
        assertEquals(0.0, floor.getHeight(), 0.01);
        assertEquals(EntryInfo.MIN_HEIGHT, floor.getEffectiveHeight(), 0.01); // Even floors get minimum height for effective height
    }

    @Test
    void testMinHeightConstant() {
        // Test that the minimum height constant is correctly defined
        assertEquals(0.5, EntryInfo.MIN_HEIGHT, 0.01);

        // Test that it's used correctly
        EntryInfo testEntry = EntryInfo.builder()
                .height(0.1)
                .build();

        assertTrue(testEntry.getEffectiveHeight() >= EntryInfo.MIN_HEIGHT);
    }

    @Test
    void testRegistryTextureProvider() {
        // Create multiple texture providers
        PictureTextureProvider provider1 = new PictureTextureProvider();
        PictureTextureProvider provider2 = new PictureTextureProvider();

        // Add textures to different providers
        String[] texture1 = {"###", "   ", "###"};
        String[] texture2 = {"***", "   ", "***"};

        provider1.addTexture("wood", texture1);
        provider2.addTexture("metal", texture2);

        // Create registry and add providers
        RegistryTextureProvider registry = new RegistryTextureProvider();
        registry.addProvider(provider1);
        registry.addProvider(provider2);

        canvas.setTextureProvider(registry);

        // Test texture retrieval from different providers with transformator interface
        EntryInfo testEntry = EntryInfo.builder()
                .colorLight(AnsiColor.WHITE)
                .colorDark(AnsiColor.BRIGHT_BLACK)
                .character('█')
                .build();

        assertNotNull(canvas.getTextureProvider().getTexture("wood", 3, 3, testEntry, true));
        assertNotNull(canvas.getTextureProvider().getTexture("metal", 3, 3, testEntry, true));
        assertNull(canvas.getTextureProvider().getTexture("nonexistent", 3, 3, testEntry, true));

        // Test caching
        assertTrue(registry.hasTexture("wood"));
        assertTrue(registry.hasTexture("metal"));
        assertFalse(registry.hasTexture("nonexistent"));
    }

    @Test
    void testPictureTexture() {
        String[] textureData = {
            "ABC",
            "DEF",
            "GHI"
        };

        // Test with the new coordinate-based interface
        EntryInfo entry = EntryInfo.builder()
                .colorLight(AnsiColor.WHITE)
                .colorDark(AnsiColor.BRIGHT_BLACK)
                .character('█')
                .build();

        // Create texture with new transformator-based constructor
        PictureTexture texture = new PictureTexture("TestTexture", textureData, 3, 3, entry, true);
        assertEquals("TestTexture", texture.getName());
        assertArrayEquals(textureData, texture.getTextureData());
        assertEquals(3, texture.getWidth());
        assertEquals(3, texture.getHeight());

        // Test coordinate-based access
        StyledChar charAtOrigin = texture.getCharAt(0, 0);
        assertNotNull(charAtOrigin);
        assertEquals('A', charAtOrigin.getCharacter());

        StyledChar charAtMiddle = texture.getCharAt(1, 1);
        assertNotNull(charAtMiddle);
        assertEquals('E', charAtMiddle.getCharacter());

        StyledChar charAtBottomRight = texture.getCharAt(2, 2);
        assertNotNull(charAtBottomRight);
        assertEquals('I', charAtBottomRight.getCharacter());

        // Test bounds checking
        StyledChar charOutOfBounds = texture.getCharAt(10, 10);
        assertNull(charOutOfBounds);

        StyledChar charNegative = texture.getCharAt(-1, -1);
        assertNull(charNegative);

        // Test with null texture data
        PictureTexture nullTexture = new PictureTexture("Null", null, 3, 3, entry, true);
        StyledChar nullResult = nullTexture.getCharAt(1, 1);
        assertNotNull(nullResult);
        assertEquals('#', nullResult.getCharacter()); // Should use default fallback when texture data is null
    }

    @Test
    void testEntryInfoWithTexture() {
        // Test EntryInfo with texture reference
        EntryInfo texturedWall = EntryInfo.builder()
                .isWall(true)
                .isWalkThrough(false)
                .isTransparent(false)
                .character('█')
                .name("Textured Wall")
                .colorLight(AnsiColor.WHITE)
                .colorDark(AnsiColor.BRIGHT_BLACK)
                .texture("stone")
                .textureInstructions("scale=2")
                .build();

        assertEquals("stone", texturedWall.getTexture());
        assertEquals("scale=2", texturedWall.getTextureInstructions());
        assertTrue(texturedWall.isWall());
        assertEquals("Textured Wall", texturedWall.getName());
    }

    @Test
    void testMapProviderToStringArray() {
        // Test conversion back to string array
        String[] originalMap = canvas.getMap();
        assertNotNull(originalMap);
        assertEquals(4, originalMap.length);
        assertEquals("####", originalMap[0]);
        assertEquals("#  #", originalMap[1]);
        assertEquals("#  #", originalMap[2]);
        assertEquals("####", originalMap[3]);
    }

    @Test
    void testWallEdgeDetection() {
        // Test wall edge functionality
        assertTrue(canvas.isDrawWallEdges());

        canvas.setDrawWallEdges(false);
        assertFalse(canvas.isDrawWallEdges());

        canvas.setWallEdgeThreshold(0.5);
        assertEquals(0.5, canvas.getWallEdgeThreshold(), 0.01);

        canvas.setWallEdgeChar('|');
        assertEquals('|', canvas.getWallEdgeChar());
    }
}
