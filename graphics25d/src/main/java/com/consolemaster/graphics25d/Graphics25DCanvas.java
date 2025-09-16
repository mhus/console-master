package com.consolemaster.graphics25d;

import com.consolemaster.AnsiColor;
import com.consolemaster.Canvas;
import com.consolemaster.Graphics;
import com.consolemaster.StyledChar;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Canvas for rendering 2.5D graphics using isometric projection.
 * Objects are rendered as if viewed on graph paper with Z-axis tiles inclined at 45 degrees.
 * Supports both wireframe and solid rendering modes.
 */
@Slf4j
public class Graphics25DCanvas extends Canvas {

    /**
     * Rendering modes for 2.5D graphics.
     */
    public enum RenderMode {
        WIREFRAME,  // Only draw edges
        SOLID,      // Fill faces
        BOTH        // Draw both edges and fill
    }

    @Getter @Setter
    private Camera25D camera = new Camera25D();

    @Getter @Setter
    private RenderMode renderMode = RenderMode.WIREFRAME;

    @Getter @Setter
    private char wireframeChar = '+';

    @Getter @Setter
    private AnsiColor wireframeColor = AnsiColor.WHITE;

    @Getter @Setter
    private char defaultFillChar = '#';

    @Getter @Setter
    private AnsiColor defaultFillColor = AnsiColor.BRIGHT_WHITE;

    @Getter
    private final List<Object25D> objects = new ArrayList<>();

    // Synchronization object for thread-safe access to objects list
    private final Object objectsLock = new Object();

    // Z-buffer for depth testing
    private double[][] zBuffer;

    public Graphics25DCanvas(String name, int width, int height) {
        super(name, width, height);
        initializeZBuffer();
    }

    /**
     * Initializes the Z-buffer for depth testing.
     */
    private void initializeZBuffer() {
        zBuffer = new double[getHeight()][getWidth()];
        clearZBuffer();
    }

    /**
     * Clears the Z-buffer with maximum depth values.
     */
    private void clearZBuffer() {
        if (zBuffer == null || zBuffer.length != getHeight() || zBuffer[0].length != getWidth()) {
            zBuffer = new double[getHeight()][getWidth()];
        }
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                zBuffer[y][x] = Double.MAX_VALUE;
            }
        }
    }

    /**
     * Adds an object to the scene.
     */
    public void addObject(Object25D object) {
        synchronized (objectsLock) {
            objects.add(object);
        }
    }

    /**
     * Removes an object from the scene.
     */
    public void removeObject(Object25D object) {
        synchronized (objectsLock) {
            objects.remove(object);
        }
    }

    /**
     * Clears all objects from the scene.
     */
    public void clearObjects() {
        synchronized (objectsLock) {
            objects.clear();
        }
    }

    @Override
    public void paint(Graphics graphics) {
        // Clear the canvas
        graphics.clear();
        clearZBuffer();

        // Create a copy of objects list to avoid ConcurrentModificationException
        List<Object25D> objectsCopy;
        synchronized (objectsLock) {
            objectsCopy = new ArrayList<>(objects);
        }

        // Sort objects by distance from camera for proper rendering order
        objectsCopy.sort(Comparator.comparingDouble(obj ->
            -camera.getPosition().distanceTo(obj.getPosition()))); // Negative for far-to-near sorting

        // Render each object
        for (Object25D object : objectsCopy) {
            renderObject(graphics, object);
        }
    }

    /**
     * Renders a single 2.5D object.
     */
    private void renderObject(Graphics graphics, Object25D object) {
        for (Object25D.Face25D face : object.getFaces()) {
            if (isBackface(object, face)) {
                continue; // Skip backfaces for performance
            }

            List<Point2D> projectedVertices = new ArrayList<>();
            List<Point25D> worldVertices = new ArrayList<>();

            // Project vertices to screen coordinates
            for (Point25D vertex : face.getVertices()) {
                Point25D worldVertex = object.getWorldVertex(vertex);
                worldVertices.add(worldVertex);
                Point2D projected = camera.project(worldVertex);
                projectedVertices.add(projected);
            }

            // Render based on mode
            switch (renderMode) {
                case WIREFRAME:
                    renderWireframe(graphics, projectedVertices, worldVertices);
                    break;
                case SOLID:
                    renderSolid(graphics, projectedVertices, worldVertices, face);
                    break;
                case BOTH:
                    renderSolid(graphics, projectedVertices, worldVertices, face);
                    renderWireframe(graphics, projectedVertices, worldVertices);
                    break;
            }
        }
    }

    /**
     * Simple backface culling based on face normal and camera direction.
     */
    private boolean isBackface(Object25D object, Object25D.Face25D face) {
        if (face.getVertices().size() < 3) {
            return false;
        }

        // Calculate face normal using first three vertices
        Point25D v0 = object.getWorldVertex(face.getVertices().get(0));
        Point25D v1 = object.getWorldVertex(face.getVertices().get(1));
        Point25D v2 = object.getWorldVertex(face.getVertices().get(2));

        Point25D edge1 = v1.subtract(v0);
        Point25D edge2 = v2.subtract(v0);

        // Cross product for normal (simplified)
        double normalX = edge1.getY() * edge2.getZ() - edge1.getZ() * edge2.getY();
        double normalY = edge1.getZ() * edge2.getX() - edge1.getX() * edge2.getZ();
        double normalZ = edge1.getX() * edge2.getY() - edge1.getY() * edge2.getX();

        // Vector from face to camera
        Point25D faceCenter = v0.add(v1).add(v2).multiply(1.0/3.0);
        Point25D toCamera = camera.getPosition().subtract(faceCenter);

        // Dot product to determine if face is pointing away
        double dot = normalX * toCamera.getX() + normalY * toCamera.getY() + normalZ * toCamera.getZ();
        return dot < 0; // Face is pointing away from camera
    }

    /**
     * Renders the wireframe of a face.
     */
    private void renderWireframe(Graphics graphics, List<Point2D> vertices, List<Point25D> worldVertices) {
        for (int i = 0; i < vertices.size(); i++) {
            Point2D start = vertices.get(i);
            Point2D end = vertices.get((i + 1) % vertices.size());
            Point25D worldStart = worldVertices.get(i);
            Point25D worldEnd = worldVertices.get((i + 1) % worldVertices.size());

            drawLine(graphics, start, end, worldStart, worldEnd, wireframeChar, wireframeColor);
        }
    }

    /**
     * Renders a filled face.
     */
    private void renderSolid(Graphics graphics, List<Point2D> vertices, List<Point25D> worldVertices, Object25D.Face25D face) {
        if (vertices.size() < 3) {
            return; // Can't fill a face with less than 3 vertices
        }

        // Simple triangle fan filling for convex polygons
        Point2D center = vertices.get(0);
        Point25D worldCenter = worldVertices.get(0);

        char fillChar = face.getFillChar() != '\0' ? face.getFillChar() : defaultFillChar;
        AnsiColor fillColor = face.getColor() != null ? face.getColor() : defaultFillColor;

        for (int i = 1; i < vertices.size() - 1; i++) {
            fillTriangle(graphics,
                        center, vertices.get(i), vertices.get(i + 1),
                        worldCenter, worldVertices.get(i), worldVertices.get(i + 1),
                        fillChar, fillColor);
        }
    }

    /**
     * Draws a line between two points using Bresenham's algorithm with depth testing.
     */
    private void drawLine(Graphics graphics, Point2D start, Point2D end,
                         Point25D worldStart, Point25D worldEnd, char ch, AnsiColor color) {
        int x0 = start.getIntX() + getWidth() / 2;
        int y0 = start.getIntY() + getHeight() / 2;
        int x1 = end.getIntX() + getWidth() / 2;
        int y1 = end.getIntY() + getHeight() / 2;

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        int x = x0, y = y0;
        double totalDistance = Math.sqrt(dx * dx + dy * dy);

        while (true) {
            if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()) {
                // Calculate depth for this pixel
                double pixelDistance = Math.sqrt((x - x0) * (x - x0) + (y - y0) * (y - y0));
                double t = totalDistance > 0 ? pixelDistance / totalDistance : 0;
                double depth = worldStart.getZ() + t * (worldEnd.getZ() - worldStart.getZ());

                // Depth test
                if (depth < zBuffer[y][x]) {
                    zBuffer[y][x] = depth;
                    graphics.drawStyledChar(x, y, ch, color, null);
                }
            }

            if (x == x1 && y == y1) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }

    /**
     * Fills a triangle using scanline algorithm with depth testing.
     */
    private void fillTriangle(Graphics graphics, Point2D p0, Point2D p1, Point2D p2,
                             Point25D world0, Point25D world1, Point25D world2,
                             char fillChar, AnsiColor fillColor) {
        // Convert to screen coordinates
        int x0 = p0.getIntX() + getWidth() / 2;
        int y0 = p0.getIntY() + getHeight() / 2;
        int x1 = p1.getIntX() + getWidth() / 2;
        int y1 = p1.getIntY() + getHeight() / 2;
        int x2 = p2.getIntX() + getWidth() / 2;
        int y2 = p2.getIntY() + getHeight() / 2;

        // Simple fill: just fill the center point for now (can be improved)
        int centerX = (x0 + x1 + x2) / 3;
        int centerY = (y0 + y1 + y2) / 3;
        double centerDepth = (world0.getZ() + world1.getZ() + world2.getZ()) / 3.0;

        if (centerX >= 0 && centerX < getWidth() && centerY >= 0 && centerY < getHeight()) {
            if (centerDepth < zBuffer[centerY][centerX]) {
                zBuffer[centerY][centerX] = centerDepth;
                graphics.drawStyledChar(centerX, centerY, fillChar, fillColor, null);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Graphics25DCanvas[%s, objects=%d, mode=%s, camera=%s]",
                           getName(), objects.size(), renderMode, camera);
    }
}
