package com.consolemaster.raycasting;

import com.consolemaster.StyledChar;

/**
 * Interface for texture rendering in raycasting.
 * Textures provide visual representation for map entries and can be scaled to any size.
 */
public interface Texture {

    /**
     * Generates a picture representation of this texture scaled to the specified dimensions.
     *
     * @param width the desired width of the texture
     * @param height the desired height of the texture
     * @param entry the entry info for color and lighting information
     * @param light true for light rendering (vertical walls), false for dark (horizontal walls)
     * @return a 2D array of styled characters representing the texture
     */
    StyledChar[][] picture(int width, int height, EntryInfo entry, boolean light);
}
