package com.consolemaster.raycasting;

/**
 * Interface for providing map data to the raycasting engine.
 * Implementations can provide static maps, dynamically generated maps,
 * or maps loaded from external sources.
 */
public interface MapProvider {

    /**
     * Get the entry at the specified coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the character at the specified position
     */
    char getEntry(int x, int y);

    /**
     * Get the width of the map.
     *
     * @return the map width
     */
    int getWidth();

    /**
     * Get the height of the map.
     *
     * @return the map height
     */
    int getHeight();

    /**
     * Get the name of the map.
     *
     * @return the map name
     */
    String getName();
}
