package com.consolemaster.graphics25d;

import com.consolemaster.AnsiColor;
import com.consolemaster.Canvas;
import com.consolemaster.Graphics;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A Canvas implementation for rendering 2.5D graphics.
 * Objects are positioned in 3D space but rendered using a 2.5D perspective
 * where z-coordinate affects the apparent size and depth sorting.
 */
@Getter
@Setter
public class Graphics25DCanvas extends Canvas {

    private final List<Object25D> objects;
    private Camera25D camera;

    // Rendering settings
    private double viewDistance = 10.0; // Maximum viewing distance
    private double perspectiveFactor = 1.0; // How much z affects size
    private boolean depthSorting = true; // Whether to sort objects by distance
    private char defaultChar = '█'; // Default character if texture is empty
    private AnsiColor backgroundColor = AnsiColor.BLACK;

    /**
     * Creates a new Graphics25DCanvas with the specified dimensions.
     *
     * @param name the name of the canvas
     * @param width the width of the canvas
     * @param height the height of the canvas
     */
    public Graphics25DCanvas(String name, int width, int height) {
        super(name, width, height);
        this.objects = new ArrayList<>();
        this.camera = new Camera25D();
    }

    /**
     * Adds an object to the 2.5D scene.
     *
     * @param object the object to add
     */
    public void addObject(Object25D object) {
        objects.add(object);
    }

    /**
     * Removes an object from the 2.5D scene.
     *
     * @param object the object to remove
     * @return true if the object was removed
     */
    public boolean removeObject(Object25D object) {
        return objects.remove(object);
    }

    /**
     * Clears all objects from the scene.
     */
    public void clearObjects() {
        objects.clear();
    }

    /**
     * Gets the number of objects in the scene.
     *
     * @return the object count
     */
    public int getObjectCount() {
        return objects.size();
    }

    /**
     * Creates a simple object at the specified position.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @param texture the texture string
     * @param color the ANSI color
     * @return the created object
     */
    public Object25D createObject(double x, double y, double z, String texture, AnsiColor color) {
        Object25D object = new Object25D(x, y, z, texture, color);
        addObject(object);
        return object;
    }

    @Override
    public void paint(Graphics graphics) {
        // Clear the background
        graphics.setBackgroundColor(backgroundColor);
        graphics.clear();

        if (objects.isEmpty()) {
            return;
        }

        // Get the list of objects to render
        List<Object25D> renderObjects = new ArrayList<>(objects);

        // Filter objects by viewing distance
        renderObjects.removeIf(obj -> obj.distanceTo(camera.getPosition()) > viewDistance);

        // Sort objects by distance from camera if depth sorting is enabled
        if (depthSorting) {
            renderObjects.sort(Comparator.comparingDouble(obj -> -obj.distanceTo(camera.getPosition())));
        }

        // Render each object
        for (Object25D object : renderObjects) {
            renderObject(graphics, object);
        }
    }

    /**
     * Renders a single object to the graphics context.
     *
     * @param graphics the graphics context
     * @param object the object to render
     */
    private void renderObject(Graphics graphics, Object25D object) {
        Point25D objPos = object.getPosition();
        Point25D camPos = camera.getPosition();

        // Calculate relative position to camera
        double relativeX = objPos.getX() - camPos.getX();
        double relativeY = objPos.getY() - camPos.getY();
        double relativeZ = objPos.getZ() - camPos.getZ();

        // Apply camera rotation
        Point25D rotatedPos = applyCameraRotation(relativeX, relativeY, relativeZ);

        // Convert 3D position to 2D screen coordinates
        Point2D screenPos = projectTo2D(rotatedPos);

        // Check if the object is within screen bounds
        if (screenPos.getX() >= 0 && screenPos.getX() < getWidth() &&
            screenPos.getY() >= 0 && screenPos.getY() < getHeight()) {

            // Get the character to render
            char renderChar = getCharacterFromTexture(object.getTexture());

            // Set color and draw the character
            graphics.setForegroundColor(object.getColor());
            graphics.drawChar((int) screenPos.getX(), (int) screenPos.getY(), renderChar);
        }
    }

    /**
     * Applies camera rotation to transform coordinates.
     *
     * @param x the x-coordinate relative to camera
     * @param y the y-coordinate relative to camera
     * @param z the z-coordinate relative to camera
     * @return the rotated coordinates
     */
    private Point25D applyCameraRotation(double x, double y, double z) {
        double rotatedX, rotatedY;

        // Apply 2D rotation based on camera direction
        double angleRad = Math.toRadians(camera.getDirection());
        double cos = Math.cos(angleRad);
        double sin = Math.sin(angleRad);

        rotatedX = x * cos - y * sin;
        rotatedY = x * sin + y * cos;

        return new Point25D(rotatedX, rotatedY, z);
    }

    /**
     * Projects a 3D point to 2D screen coordinates.
     *
     * @param point the 3D point to project
     * @return the 2D screen coordinates
     */
    private Point2D projectTo2D(Point25D point) {
        // Simple orthographic projection with perspective scaling
        double distance = Math.sqrt(point.getX() * point.getX() + point.getY() * point.getY());
        double scale = 1.0;

        // Apply perspective based on z-coordinate
        if (perspectiveFactor > 0) {
            scale = 1.0 / (1.0 + point.getZ() * perspectiveFactor * 0.1);
        }

        // Convert to screen coordinates
        double screenX = (getWidth() / 2.0) + (point.getX() * scale);
        double screenY = (getHeight() / 2.0) + (point.getY() * scale);

        return new Point2D(screenX, screenY);
    }

    /**
     * Extracts a character from the texture string.
     * If texture is empty or null, returns the default character.
     *
     * @param texture the texture string
     * @return the character to render
     */
    private char getCharacterFromTexture(String texture) {
        if (texture == null || texture.isEmpty()) {
            return defaultChar;
        }

        // For now, just use the first character of the texture
        // Could be extended to support multi-character textures or animations
        return texture.charAt(0);
    }

    /**
     * Simple 2D point class for screen coordinates.
     */
    private static class Point2D {
        private final double x;
        private final double y;

        public Point2D(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() { return x; }
        public double getY() { return y; }
    }
}
