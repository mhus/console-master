package com.consolemaster.raycasting;

/**
 * Interface for providing sprites based on viewing angle.
 * Implements 8-directional sprites where the appropriate sprite is selected
 * based on the relative angle between the player's view direction and the object.
 */
public interface SpriteProvider {

    /**
     * Gets the appropriate sprite for the given relative viewing angle.
     * The angle represents the direction from the player to the object,
     * relative to the object's orientation.
     *
     * @param relativeAngle the angle from the player to the object relative to object's orientation
     *                     (in radians, 0 = facing front, PI = facing back)
     * @return the sprite to render for this viewing angle
     */
    Sprite getSpriteForAngle(double relativeAngle);

    /**
     * Gets the number of directional sprites supported by this provider.
     * Standard implementation should return 8 for 8-directional sprites.
     *
     * @return number of directions (typically 8)
     */
    default int getDirectionCount() {
        return 8;
    }

    /**
     * Gets the base scale factor for all sprites provided by this provider.
     * This can be used to make objects appear larger or smaller.
     *
     * @return base scale factor (1.0 = normal size)
     */
    default double getBaseScale() {
        return 1.0;
    }

    /**
     * Checks if this sprite provider has valid sprites.
     *
     * @return true if sprites are available, false otherwise
     */
    default boolean hasSprites() {
        return getSpriteForAngle(0.0) != null;
    }
}
