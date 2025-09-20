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
        assertFalse(wallEntry.isFallthrough());

        EntryInfo floorEntry = canvas.getMapProvider().getEntry(1, 1);
        assertFalse(floorEntry.isWall());
        assertTrue(floorEntry.isFallthrough());
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
        assertFalse(wallInfo.isFallthrough());
        assertEquals("Wall", wallInfo.getName());

        EntryInfo floorInfo = EntryInfo.fromCharacter(' ');
        assertFalse(floorInfo.isWall());
        assertTrue(floorInfo.isFallthrough());
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
        assertFalse(glassEntry.isFallthrough()); // Can't walk through
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
        assertFalse(stoneWall.isFallthrough());
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

        // Test texture retrieval with new transformator-based interface
        EntryInfo testEntry = EntryInfo.builder()
                .colorLight(AnsiColor.WHITE)
                .colorDark(AnsiColor.BRIGHT_BLACK)
                .character('█')
                .build();

        Texture retrievedTexture = canvas.getTextureProvider().getTexture("test", 3, 3, testEntry, true);
        assertNotNull(retrievedTexture);

        // Test coordinate-based access
        StyledChar charAtOrigin = retrievedTexture.getCharAt(0, 0);
        assertNotNull(charAtOrigin);
        assertEquals('#', charAtOrigin.getCharacter());

        StyledChar charAtMiddle = retrievedTexture.getCharAt(1, 1);
        assertNotNull(charAtMiddle);
        assertEquals(' ', charAtMiddle.getCharacter());

        // Test bounds checking
        StyledChar charOutOfBounds = retrievedTexture.getCharAt(10, 10);
        assertNull(charOutOfBounds);
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
                .isFallthrough(false)
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
