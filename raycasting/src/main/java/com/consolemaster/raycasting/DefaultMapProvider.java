package com.consolemaster.raycasting;

/**
 * Default implementation of MapProvider that provides a static map.
 * This provider holds a fixed map and allows for map updates.
 */
public class DefaultMapProvider implements MapProvider {

    private final EntryInfo[][] map;
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
     * Create a DefaultMapProvider with a custom map from string array.
     *
     * @param name the name of the map
     * @param mapData the map data as string array
     */
    public DefaultMapProvider(String name, String[] mapData) {
        this.name = name != null ? name : "Unnamed Map";
        String[] sourceMap = mapData != null ? mapData : DEFAULT_MAP;
        this.height = sourceMap.length;
        this.width = this.height > 0 ? sourceMap[0].length() : 0;

        // Validate that all rows have the same width
        for (String row : sourceMap) {
            if (row.length() != this.width) {
                throw new IllegalArgumentException("All map rows must have the same width");
            }
        }

        // Convert string map to EntryInfo map
        this.map = new EntryInfo[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                char c = sourceMap[y].charAt(x);
                this.map[y][x] = EntryInfo.fromCharacter(c);
            }
        }
    }

    /**
     * Create a DefaultMapProvider with EntryInfo map.
     *
     * @param name the name of the map
     * @param entryMap the map data as EntryInfo array
     */
    public DefaultMapProvider(String name, EntryInfo[][] entryMap) {
        this.name = name != null ? name : "Unnamed Map";
        if (entryMap == null || entryMap.length == 0) {
            // Fall back to default map
            this.height = DEFAULT_MAP.length;
            this.width = this.height > 0 ? DEFAULT_MAP[0].length() : 0;
            this.map = new EntryInfo[height][width];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    char c = DEFAULT_MAP[y].charAt(x);
                    this.map[y][x] = EntryInfo.fromCharacter(c);
                }
            }
        } else {
            this.height = entryMap.length;
            this.width = entryMap[0].length;

            // Validate dimensions and copy map
            this.map = new EntryInfo[height][width];
            for (int y = 0; y < height; y++) {
                if (entryMap[y].length != width) {
                    throw new IllegalArgumentException("All map rows must have the same width");
                }
                for (int x = 0; x < width; x++) {
                    this.map[y][x] = entryMap[y][x] != null ? entryMap[y][x] : EntryInfo.createEmpty();
                }
            }
        }
    }

    @Override
    public EntryInfo getEntry(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return EntryInfo.createWall(); // Return wall for out-of-bounds access
        }
        return map[y][x];
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

    /**
     * Convert the EntryInfo map back to a string array for compatibility.
     *
     * @return string array representation of the map
     */
    public String[] toStringArray() {
        String[] result = new String[height];
        for (int y = 0; y < height; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < width; x++) {
                row.append(map[y][x].toCharacter());
            }
            result[y] = row.toString();
        }
        return result;
    }
}
