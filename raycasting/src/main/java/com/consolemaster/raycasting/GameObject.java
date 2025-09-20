package com.consolemaster.raycasting;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a game object in the 3D world that can be rendered with 8-directional sprites.
 * Game objects have position, orientation, and visual representation through a SpriteProvider.
 */
@Getter
@Setter
public class GameObject {

    /** X position in world coordinates */
    private double x;

    /** Y position in world coordinates */
    private double y;

    /** Object orientation in radians (0 = facing east, PI/2 = facing south) */
    private double orientation;

    /** Provider for 8-directional sprites */
    private SpriteProvider spriteProvider;

    /** Object name/identifier */
    private String name;

    /** Whether this object is visible and should be rendered */
    private boolean visible = true;

    /** Whether this object blocks player movement */
    private boolean solid = false;

    /** Whether this object can be interacted with */
    private boolean interactable = false;

    /** Render distance - objects beyond this distance won't be rendered */
    private double maxRenderDistance = 20.0;

    /** Z-offset for rendering (positive = higher) */
    private double zOffset = 0.0;

    /**
     * Creates a new GameObject.
     *
     * @param name object identifier
     * @param x x position in world coordinates
     * @param y y position in world coordinates
     * @param orientation orientation in radians
     * @param spriteProvider provider for directional sprites
     */
    public GameObject(String name, double x, double y, double orientation, SpriteProvider spriteProvider) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.orientation = orientation;
        this.spriteProvider = spriteProvider;
    }

    /**
     * Creates a new GameObject with default orientation (facing east).
     *
     * @param name object identifier
     * @param x x position in world coordinates
     * @param y y position in world coordinates
     * @param spriteProvider provider for directional sprites
     */
    public GameObject(String name, double x, double y, SpriteProvider spriteProvider) {
        this(name, x, y, 0.0, spriteProvider);
    }

    /**
     * Gets the distance from this object to the specified position.
     *
     * @param fromX x coordinate to measure from
     * @param fromY y coordinate to measure from
     * @return distance to the position
     */
    public double getDistanceTo(double fromX, double fromY) {
        double dx = x - fromX;
        double dy = y - fromY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Gets the angle from the specified position to this object.
     *
     * @param fromX x coordinate to measure from
     * @param fromY y coordinate to measure from
     * @return angle in radians from the position to this object
     */
    public double getAngleFrom(double fromX, double fromY) {
        return Math.atan2(y - fromY, x - fromX);
    }

    /**
     * Gets the appropriate sprite for viewing this object from the specified angle.
     * Calculates the relative viewing angle and delegates to the SpriteProvider.
     *
     * @param viewerAngle the angle from which this object is being viewed
     * @return the sprite to render, or null if no sprite available
     */
    public Sprite getSpriteForViewAngle(double viewerAngle) {
        if (spriteProvider == null || !visible) {
            return null;
        }

        // Calculate relative angle: angle from viewer to object minus object's orientation
        double relativeAngle = viewerAngle - orientation;

        // Normalize to [0, 2π]
        while (relativeAngle < 0) relativeAngle += 2 * Math.PI;
        while (relativeAngle >= 2 * Math.PI) relativeAngle -= 2 * Math.PI;

        return spriteProvider.getSpriteForAngle(relativeAngle);
    }

    /**
     * Rotates the object by the specified angle.
     *
     * @param angle angle to rotate in radians
     */
    public void rotate(double angle) {
        this.orientation += angle;
        // Normalize to [0, 2π]
        while (this.orientation < 0) this.orientation += 2 * Math.PI;
        while (this.orientation >= 2 * Math.PI) this.orientation -= 2 * Math.PI;
    }

    /**
     * Sets the object's orientation to face the specified position.
     *
     * @param targetX x coordinate to face
     * @param targetY y coordinate to face
     */
    public void faceTowards(double targetX, double targetY) {
        this.orientation = Math.atan2(targetY - y, targetX - x);
    }

    /**
     * Checks if this object should be rendered based on distance and visibility.
     *
     * @param viewerX viewer x position
     * @param viewerY viewer y position
     * @return true if object should be rendered
     */
    public boolean shouldRender(double viewerX, double viewerY) {
        return visible &&
               spriteProvider != null &&
               spriteProvider.hasSprites() &&
               getDistanceTo(viewerX, viewerY) <= maxRenderDistance;
    }
}
