package com.consolemaster.raycasting;

/**
 * Default implementation of MapProvider that provides a static map.
 * This provider holds a fixed map and allows for map updates.
 */
public class DefaultMapProvider implements MapProvider {

    private final String[] map;
    private final String name;
    private final int width;
    private final int height;

    // Default map if none is provided
    private static final String[] DEFAULT_MAP = {
        "########",
        "#      #",
        "#  ##  #",
        "#      #",
        "########"
    };

    /**
     * Create a DefaultMapProvider with the default map.
     */
    public DefaultMapProvider() {
        this("Default Map", DEFAULT_MAP);
    }

    /**
     * Create a DefaultMapProvider with a custom map.
     *
     * @param name the name of the map
     * @param map the map data as string array
     */
    public DefaultMapProvider(String name, String[] map) {
        this.name = name != null ? name : "Unnamed Map";
        this.map = map != null ? map.clone() : DEFAULT_MAP.clone();
        this.height = this.map.length;
        this.width = this.height > 0 ? this.map[0].length() : 0;

        // Validate that all rows have the same width
        for (String row : this.map) {
            if (row.length() != this.width) {
                throw new IllegalArgumentException("All map rows must have the same width");
            }
        }
    }

    @Override
    public char getEntry(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return '#'; // Return wall for out-of-bounds access
        }
        return map[y].charAt(x);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Get the default map.
     *
     * @return a copy of the default map
     */
    public static String[] getDefaultMap() {
        return DEFAULT_MAP.clone();
    }
}
