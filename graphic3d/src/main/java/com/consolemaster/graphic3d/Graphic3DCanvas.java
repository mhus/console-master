package com.consolemaster.graphic3d;

import com.consolemaster.Canvas;
import com.consolemaster.Graphics;
import com.consolemaster.AnsiColor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A Canvas that can render 3D content using ASCII characters.
 * Supports wireframe and filled rendering modes.
 */
@Getter
@Setter
public class Graphic3DCanvas extends Canvas {

    private Camera3D camera;
    private List<Mesh3D> meshes;
    private RenderMode renderMode;
    private char wireframeChar;
    private char fillChar;
    private AnsiColor wireframeColor;
    private AnsiColor fillColor;
    private boolean backfaceCulling;
    private double[][] depthBuffer;

    /**
     * Rendering modes for 3D objects.
     */
    public enum RenderMode {
        WIREFRAME,  // Only draw edges
        FILLED,     // Fill triangles with characters
        BOTH        // Draw both wireframe and fill
    }

    public Graphic3DCanvas(String name, int width, int height) {
        super(name, width, height);
        this.camera = new Camera3D();
        this.meshes = new ArrayList<>();
        this.renderMode = RenderMode.WIREFRAME;
        this.wireframeChar = '*';
        this.fillChar = '#';
        this.wireframeColor = AnsiColor.WHITE;
        this.fillColor = AnsiColor.CYAN;
        this.backfaceCulling = true;
        this.depthBuffer = new double[height][width];
    }

    /**
     * Adds a mesh to the 3D scene.
     */
    public void addMesh(Mesh3D mesh) {
        meshes.add(mesh);
        requestRedraw();
    }

    /**
     * Removes a mesh from the 3D scene.
     */
    public void removeMesh(Mesh3D mesh) {
        meshes.remove(mesh);
        requestRedraw();
    }

    /**
     * Clears all meshes from the scene.
     */
    public void clearMeshes() {
        meshes.clear();
        requestRedraw();
    }

    @Override
    public void paint(Graphics graphics) {
        // Clear the canvas
        graphics.clear();

        // Initialize depth buffer
        clearDepthBuffer();

        // Calculate aspect ratio
        double aspectRatio = (double) getWidth() / getHeight();

        // Get view-projection matrix
        Matrix4x4 viewProjection = camera.getViewProjectionMatrix(aspectRatio);

        // Render each mesh
        for (Mesh3D mesh : meshes) {
            renderMesh(graphics, mesh, viewProjection);
        }
    }

    /**
     * Renders a single mesh to the graphics context.
     */
    private void renderMesh(Graphics graphics, Mesh3D mesh, Matrix4x4 viewProjection) {
        List<Point3D> projectedVertices = new ArrayList<>();

        // Project all vertices to screen space
        for (Point3D vertex : mesh.getVertices()) {
            Point3D projected = viewProjection.transform(vertex);

            // Convert from normalized device coordinates to screen coordinates
            // Center the projection in the canvas
            double centerX = getWidth() / 2.0;
            double centerY = getHeight() / 2.0;

            // Scale to fit canvas while maintaining aspect ratio
            // Use a smaller scale factor to ensure objects fit well
            double scale = Math.min(getWidth(), getHeight()) / 4.0;

            double screenX = centerX + projected.getX() * scale;
            double screenY = centerY - projected.getY() * scale; // Flip Y for screen coordinates

            projectedVertices.add(new Point3D(screenX, screenY, projected.getZ()));
        }

        // Render faces
        for (Mesh3D.Face3D face : mesh.getFaces()) {
            Point3D v1 = projectedVertices.get(face.getV1());
            Point3D v2 = projectedVertices.get(face.getV2());
            Point3D v3 = projectedVertices.get(face.getV3());

            // Backface culling
            if (backfaceCulling && isBackface(v1, v2, v3)) {
                continue;
            }

            // Check if triangle is within screen bounds
            if (!isTriangleVisible(v1, v2, v3)) {
                continue;
            }

            // Render based on mode
            switch (renderMode) {
                case WIREFRAME:
                    drawWireframeTriangle(graphics, v1, v2, v3);
                    break;
                case FILLED:
                    fillTriangle(graphics, v1, v2, v3, face);
                    break;
                case BOTH:
                    fillTriangle(graphics, v1, v2, v3, face);
                    drawWireframeTriangle(graphics, v1, v2, v3);
                    break;
            }
        }
    }

    /**
     * Checks if a triangle is facing away from the camera (backface).
     */
    private boolean isBackface(Point3D v1, Point3D v2, Point3D v3) {
        // Calculate cross product of two edges
        double edge1X = v2.getX() - v1.getX();
        double edge1Y = v2.getY() - v1.getY();
        double edge2X = v3.getX() - v1.getX();
        double edge2Y = v3.getY() - v1.getY();

        double crossProduct = edge1X * edge2Y - edge1Y * edge2X;
        return crossProduct < 0; // Clockwise = backface
    }

    /**
     * Checks if any part of the triangle is visible on screen.
     */
    private boolean isTriangleVisible(Point3D v1, Point3D v2, Point3D v3) {
        // Simple bounds check
        double minX = Math.min(Math.min(v1.getX(), v2.getX()), v3.getX());
        double maxX = Math.max(Math.max(v1.getX(), v2.getX()), v3.getX());
        double minY = Math.min(Math.min(v1.getY(), v2.getY()), v3.getY());
        double maxY = Math.max(Math.max(v1.getY(), v2.getY()), v3.getY());

        return maxX >= 0 && minX < getWidth() && maxY >= 0 && minY < getHeight();
    }

    /**
     * Draws the wireframe of a triangle.
     */
    private void drawWireframeTriangle(Graphics graphics, Point3D v1, Point3D v2, Point3D v3) {
        graphics.setForegroundColor(wireframeColor);
        drawLine(graphics, v1, v2, wireframeChar);
        drawLine(graphics, v2, v3, wireframeChar);
        drawLine(graphics, v3, v1, wireframeChar);
    }

    /**
     * Fills a triangle with the fill character, considering face color and texture.
     */
    private void fillTriangle(Graphics graphics, Point3D v1, Point3D v2, Point3D v3, Mesh3D.Face3D face) {
        // Simple triangle filling using scanline algorithm
        int minY = (int) Math.max(0, Math.min(Math.min(v1.getY(), v2.getY()), v3.getY()));
        int maxY = (int) Math.min(getHeight() - 1, Math.max(Math.max(v1.getY(), v2.getY()), v3.getY()));

        for (int y = minY; y <= maxY; y++) {
            List<Double> intersections = new ArrayList<>();

            // Find intersections with triangle edges
            addLineIntersection(intersections, v1, v2, y);
            addLineIntersection(intersections, v2, v3, y);
            addLineIntersection(intersections, v3, v1, y);

            if (intersections.size() >= 2) {
                intersections.sort(Double::compareTo);
                int startX = (int) Math.max(0, intersections.get(0));
                int endX = (int) Math.min(getWidth() - 1, intersections.get(intersections.size() - 1));

                for (int x = startX; x <= endX; x++) {
                    // Calculate depth for this pixel
                    double depth = interpolateDepth(v1, v2, v3, x, y);

                    // Depth test
                    if (depth < depthBuffer[y][x]) {
                        depthBuffer[y][x] = depth;

                        // Calculate texture coordinates (simplified)
                        double u = (double) (x - startX) / Math.max(1, endX - startX);
                        double v = (double) (y - minY) / Math.max(1, maxY - minY);

                        // Simple lighting calculation (basic directional light)
                        double lightIntensity = 0.7; // Default lighting

                        // Get effective color and character from face
                        AnsiColor effectiveColor = face.getEffectiveColor(u, v, lightIntensity);
                        char effectiveChar = face.getEffectiveCharacter(u, v, lightIntensity);

                        graphics.setForegroundColor(effectiveColor);
                        graphics.drawChar(x, y, effectiveChar);
                    }
                }
            }
        }
    }

    /**
     * Adds line intersection with horizontal scanline if it exists.
     */
    private void addLineIntersection(List<Double> intersections, Point3D p1, Point3D p2, int y) {
        if ((p1.getY() <= y && p2.getY() > y) || (p2.getY() <= y && p1.getY() > y)) {
            double t = (y - p1.getY()) / (p2.getY() - p1.getY());
            double x = p1.getX() + t * (p2.getX() - p1.getX());
            intersections.add(x);
        }
    }

    /**
     * Interpolates depth value at given pixel coordinates within triangle.
     */
    private double interpolateDepth(Point3D v1, Point3D v2, Point3D v3, int x, int y) {
        // Barycentric coordinates for depth interpolation
        double denom = (v2.getY() - v3.getY()) * (v1.getX() - v3.getX()) + (v3.getX() - v2.getX()) * (v1.getY() - v3.getY());
        if (Math.abs(denom) < 1e-10) return v1.getZ();

        double a = ((v2.getY() - v3.getY()) * (x - v3.getX()) + (v3.getX() - v2.getX()) * (y - v3.getY())) / denom;
        double b = ((v3.getY() - v1.getY()) * (x - v3.getX()) + (v1.getX() - v3.getX()) * (y - v3.getY())) / denom;
        double c = 1 - a - b;

        return a * v1.getZ() + b * v2.getZ() + c * v3.getZ();
    }

    /**
     * Draws a line between two points using Bresenham's algorithm.
     */
    private void drawLine(Graphics graphics, Point3D p1, Point3D p2, char character) {
        int x1 = (int) p1.getX();
        int y1 = (int) p1.getY();
        int x2 = (int) p2.getX();
        int y2 = (int) p2.getY();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        int x = x1;
        int y = y1;

        while (true) {
            if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()) {
                graphics.drawChar(x, y, character);
            }

            if (x == x2 && y == y2) break;

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
     * Clears the depth buffer.
     */
    private void clearDepthBuffer() {
        if (depthBuffer == null || depthBuffer.length != getHeight() || depthBuffer[0].length != getWidth()) {
            depthBuffer = new double[getHeight()][getWidth()];
        }
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                depthBuffer[y][x] = Double.MAX_VALUE;
            }
        }
    }

    /**
     * Requests a redraw of the canvas.
     */
    private void requestRedraw() {
        // This would typically trigger a repaint in the framework
        // Implementation depends on the framework's redraw mechanism
    }
}
