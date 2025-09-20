package com.consolemaster.raycasting;

import com.consolemaster.AnsiColor;

/**
 * Interface representing a single sprite image that can be rendered on the canvas.
 * A sprite contains image data (characters and colors) that represents an object or entity.
 */
public interface Sprite {

    /**
     * Gets the width of the sprite in characters.
     * @return sprite width
     */
    int getWidth();

    /**
     * Gets the height of the sprite in characters.
     * @return sprite height
     */
    int getHeight();

    /**
     * Gets the character at the specified position in the sprite.
     * @param x x-coordinate (0 to width-1)
     * @param y y-coordinate (0 to height-1)
     * @return character at position, or space if out of bounds
     */
    char getCharAt(int x, int y);

    /**
     * Gets the foreground color at the specified position in the sprite.
     * @param x x-coordinate (0 to width-1)
     * @param y y-coordinate (0 to height-1)
     * @return foreground color at position, or null if no specific color
     */
    AnsiColor getForegroundColorAt(int x, int y);

    /**
     * Gets the background color at the specified position in the sprite.
     * @param x x-coordinate (0 to width-1)
     * @param y y-coordinate (0 to height-1)
     * @return background color at position, or null if no specific color
     */
    AnsiColor getBackgroundColorAt(int x, int y);

    /**
     * Checks if the sprite has transparency at the specified position.
     * Transparent positions should not be rendered.
     * @param x x-coordinate (0 to width-1)
     * @param y y-coordinate (0 to height-1)
     * @return true if position is transparent, false otherwise
     */
    boolean isTransparentAt(int x, int y);

    /**
     * Gets the scale factor for this sprite.
     * This can be used to resize the sprite during rendering.
     * @return scale factor (1.0 = normal size, 0.5 = half size, 2.0 = double size)
     */
    default double getScale() {
        return 1.0;
    }
}
