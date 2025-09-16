package com.consolemaster.graphic3d;

import com.consolemaster.Canvas;
import com.consolemaster.Graphics;
import com.consolemaster.AnsiColor;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import java.util.ArrayList;
import java.util.List;

/**
 * A Canvas that can render 3D content using ASCII characters with BigDecimal precision.
 * Supports wireframe and filled rendering modes.
 */
@Getter
@Setter
public class Graphic3DCanvas extends Canvas {

    private static final MathContext MATH_CONTEXT = new MathContext(34, RoundingMode.HALF_UP);
    private static final BigDecimal TWO = BigDecimal.valueOf(2);
    private static final BigDecimal FOUR = BigDecimal.valueOf(4);

    private Camera3D camera;
    private List<Mesh3D> meshes;
    private RenderMode renderMode;
    private char wireframeChar;
    private char fillChar;
    private AnsiColor wireframeColor;
    private AnsiColor fillColor;
    private boolean backfaceCulling;
    private BigDecimal[][] depthBuffer;

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
        this.depthBuffer = new BigDecimal[height][width];
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
        BigDecimal aspectRatio = BigDecimal.valueOf(getWidth()).divide(BigDecimal.valueOf(getHeight()), MATH_CONTEXT);

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
            BigDecimal centerX = BigDecimal.valueOf(getWidth()).divide(TWO, MATH_CONTEXT);
            BigDecimal centerY = BigDecimal.valueOf(getHeight()).divide(TWO, MATH_CONTEXT);

            // Scale to fit canvas while maintaining aspect ratio
            // Use a smaller scale factor to ensure objects fit well
            BigDecimal scale = BigDecimal.valueOf(Math.min(getWidth(), getHeight())).divide(FOUR, MATH_CONTEXT);

            BigDecimal screenX = centerX.add(projected.getX().multiply(scale, MATH_CONTEXT), MATH_CONTEXT);
            BigDecimal screenY = centerY.subtract(projected.getY().multiply(scale, MATH_CONTEXT), MATH_CONTEXT); // Flip Y for screen coordinates

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
        BigDecimal edge1X = v2.getX().subtract(v1.getX(), MATH_CONTEXT);
        BigDecimal edge1Y = v2.getY().subtract(v1.getY(), MATH_CONTEXT);
        BigDecimal edge2X = v3.getX().subtract(v1.getX(), MATH_CONTEXT);
        BigDecimal edge2Y = v3.getY().subtract(v1.getY(), MATH_CONTEXT);

        BigDecimal crossProduct = edge1X.multiply(edge2Y, MATH_CONTEXT).subtract(edge1Y.multiply(edge2X, MATH_CONTEXT), MATH_CONTEXT);
        return crossProduct.compareTo(BigDecimal.ZERO) < 0; // Clockwise = backface
    }

    /**
     * Checks if any part of the triangle is visible on screen.
     */
    private boolean isTriangleVisible(Point3D v1, Point3D v2, Point3D v3) {
        // Simple bounds check - convert to double for simplicity
        double minX = Math.min(Math.min(v1.getXAsDouble(), v2.getXAsDouble()), v3.getXAsDouble());
        double maxX = Math.max(Math.max(v1.getXAsDouble(), v2.getXAsDouble()), v3.getXAsDouble());
        double minY = Math.min(Math.min(v1.getYAsDouble(), v2.getYAsDouble()), v3.getYAsDouble());
        double maxY = Math.max(Math.max(v1.getYAsDouble(), v2.getYAsDouble()), v3.getYAsDouble());

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
        // Simple triangle filling using scanline algorithm - convert to double for simplicity
        int minY = Math.max(0, (int) Math.min(Math.min(v1.getYAsDouble(), v2.getYAsDouble()), v3.getYAsDouble()));
        int maxY = Math.min(getHeight() - 1, (int) Math.max(Math.max(v1.getYAsDouble(), v2.getYAsDouble()), v3.getYAsDouble()));

        for (int y = minY; y <= maxY; y++) {
            List<Double> intersections = new ArrayList<>();

            // Find intersections with triangle edges
            addLineIntersection(intersections, v1, v2, y);
            addLineIntersection(intersections, v2, v3, y);
            addLineIntersection(intersections, v3, v1, y);

            if (intersections.size() >= 2) {
                intersections.sort(Double::compareTo);
                int startX = Math.max(0, intersections.get(0).intValue());
                int endX = Math.min(getWidth() - 1, intersections.get(intersections.size() - 1).intValue());

                for (int x = startX; x <= endX; x++) {
                    // Calculate depth for this pixel
                    double depth = interpolateDepth(v1, v2, v3, x, y);

                    // Depth test
                    if (depthBuffer[y][x] == null || BigDecimal.valueOf(depth).compareTo(depthBuffer[y][x]) < 0) {
                        depthBuffer[y][x] = BigDecimal.valueOf(depth);

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
        double p1Y = p1.getYAsDouble();
        double p2Y = p2.getYAsDouble();

        if ((p1Y <= y && p2Y > y) || (p2Y <= y && p1Y > y)) {
            double t = (y - p1Y) / (p2Y - p1Y);
            double x = p1.getXAsDouble() + t * (p2.getXAsDouble() - p1.getXAsDouble());
            intersections.add(x);
        }
    }

    /**
     * Interpolates depth value at given pixel coordinates within triangle.
     */
    private double interpolateDepth(Point3D v1, Point3D v2, Point3D v3, int x, int y) {
        // Barycentric coordinates for depth interpolation - using double for simplicity
        double v1x = v1.getXAsDouble(), v1y = v1.getYAsDouble(), v1z = v1.getZAsDouble();
        double v2x = v2.getXAsDouble(), v2y = v2.getYAsDouble(), v2z = v2.getZAsDouble();
        double v3x = v3.getXAsDouble(), v3y = v3.getYAsDouble(), v3z = v3.getZAsDouble();

        double denom = (v2y - v3y) * (v1x - v3x) + (v3x - v2x) * (v1y - v3y);
        if (Math.abs(denom) < 1e-10) return v1z;

        double a = ((v2y - v3y) * (x - v3x) + (v3x - v2x) * (y - v3y)) / denom;
        double b = ((v3y - v1y) * (x - v3x) + (v1x - v3x) * (y - v3y)) / denom;
        double c = 1 - a - b;

        return a * v1z + b * v2z + c * v3z;
    }

    /**
     * Draws a line between two points using Bresenham's algorithm.
     */
    private void drawLine(Graphics graphics, Point3D p1, Point3D p2, char character) {
        int x1 = (int) p1.getXAsDouble();
        int y1 = (int) p1.getYAsDouble();
        int x2 = (int) p2.getXAsDouble();
        int y2 = (int) p2.getYAsDouble();

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
            depthBuffer = new BigDecimal[getHeight()][getWidth()];
        }
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                depthBuffer[y][x] = null; // Use null to represent maximum depth
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
