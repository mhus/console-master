package com.consolemaster.raycasting;

import com.consolemaster.StyledChar;

/**
 * Interface for texture rendering with coordinate-based access.
 * Textures provide direct access to styled characters at specific coordinates.
 */
public interface Texture {

    /**
     * Gets the styled character at the specified coordinates.
     * This method allows direct coordinate-based access to the texture data.
     *
     * @param x the x coordinate (0-based)
     * @param y the y coordinate (0-based)
     * @return the styled character at the given position, or null if coordinates are out of bounds
     */
    StyledChar getCharAt(int x, int y);

    int getWidth();

    int getHeight();
}
