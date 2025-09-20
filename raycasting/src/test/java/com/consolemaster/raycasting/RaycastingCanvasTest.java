package com.consolemaster.raycasting;

import com.consolemaster.AnsiColor;
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
        assertEquals("Stone Wall", stoneWall.getName());
        assertEquals(AnsiColor.WHITE, stoneWall.getColor(false)); // Light
        assertEquals(AnsiColor.BRIGHT_BLACK, stoneWall.getColor(true)); // Dark

        // Test brick wall
        EntryInfo brickWall = EntryInfo.createBrickWall();
        assertTrue(brickWall.isWall());
        assertEquals("Brick Wall", brickWall.getName());
        assertEquals(AnsiColor.RED, brickWall.getColor(false)); // Light
        assertEquals(AnsiColor.BRIGHT_RED, brickWall.getColor(true)); // Dark
        assertEquals('▓', brickWall.getCharacter());

        // Test metal wall
        EntryInfo metalWall = EntryInfo.createMetalWall();
        assertTrue(metalWall.isWall());
        assertEquals("Metal Wall", metalWall.getName());
        assertEquals(AnsiColor.BRIGHT_WHITE, metalWall.getColor(false)); // Light
        assertEquals(AnsiColor.WHITE, metalWall.getColor(true)); // Dark
        assertEquals('▒', metalWall.getCharacter());

        // Test glass wall with dual colors
        EntryInfo glassWall = EntryInfo.createGlass();
        assertTrue(glassWall.isWall());
        assertTrue(glassWall.isTransparent());
        assertEquals(AnsiColor.CYAN, glassWall.getColor(false)); // Light
        assertEquals(AnsiColor.BLUE, glassWall.getColor(true)); // Dark
    }

    @Test
    void testEntryInfoHeight() {
        EntryInfo lowWall = EntryInfo.createLowWall();
        assertEquals(0.5, lowWall.getHeight(), 0.01);

        EntryInfo normalWall = EntryInfo.createWall();
        assertEquals(1.0, normalWall.getHeight(), 0.01);

        EntryInfo customWall = EntryInfo.builder()
                .height(1.5)
                .build();
        assertEquals(1.5, customWall.getHeight(), 0.01);
    }

    @Test
    void testBackwardCompatibilityWithStringArrays() {
        // Test that string arrays still work
        String[] legacyMap = {
            "###",
            "# #",
            "###"
        };

        canvas.setMap(legacyMap);

        // Verify conversion worked
        assertEquals(3, canvas.getMapProvider().getWidth());
        assertEquals(3, canvas.getMapProvider().getHeight());

        // Test that walls are properly converted
        EntryInfo cornerWall = canvas.getMapProvider().getEntry(0, 0);
        assertTrue(cornerWall.isWall());

        EntryInfo centerSpace = canvas.getMapProvider().getEntry(1, 1);
        assertFalse(centerSpace.isWall());
        assertTrue(centerSpace.isFallthrough());
    }
}
