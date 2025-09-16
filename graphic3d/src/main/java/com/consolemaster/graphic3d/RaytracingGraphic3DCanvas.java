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
 * A Canvas that renders 3D content using raytracing.
 * For each character position, a ray is cast into the 3D world to determine
 * what character and color should be displayed.
 */
@Getter
@Setter
public class RaytracingGraphic3DCanvas extends Canvas {

    private static final MathContext MATH_CONTEXT = new MathContext(34, RoundingMode.HALF_UP);

    private Camera3D camera;
    private List<Mesh3D> meshes;
    private Point3D lightDirection;
    private double lightIntensity;
    private AnsiColor backgroundColor;
    private char backgroundChar;
    private double fieldOfView; // In radians
    private double nearPlane;
    private double farPlane;

    // Performance settings
    private boolean enableAntiAliasing;
    private int antiAliasingRays; // Number of rays per pixel for anti-aliasing

    public RaytracingGraphic3DCanvas(String name, int width, int height) {
        super(name, width, height);
        this.camera = new Camera3D();
        this.meshes = new ArrayList<>();
        this.lightDirection = new Point3D(0, -1, -1).normalize(); // Light from above-left
        this.lightIntensity = 1.0;
        this.backgroundColor = AnsiColor.BLACK;
        this.backgroundChar = ' ';
        this.fieldOfView = Math.PI / 3.0; // 60 degrees
        this.nearPlane = 0.1;
        this.farPlane = 100.0;
        this.enableAntiAliasing = false;
        this.antiAliasingRays = 4;
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

        // Calculate aspect ratio
        double aspectRatio = (double) getWidth() / getHeight();

        // Render each pixel using raytracing
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                if (enableAntiAliasing) {
                    renderPixelWithAntiAliasing(graphics, x, y, aspectRatio);
                } else {
                    renderPixel(graphics, x, y, aspectRatio);
                }
            }
        }
    }

    /**
     * Renders a single pixel by casting a ray into the scene.
     */
    private void renderPixel(Graphics graphics, int x, int y, double aspectRatio) {
        Ray3D ray = createCameraRay(x, y, aspectRatio);
        RayHit hit = castRay(ray);

        if (hit != null) {
            hit.computeShading(lightDirection, lightIntensity);
            graphics.setForegroundColor(hit.getColor());
            graphics.drawChar(x, y, hit.getCharacter());
        } else {
            // No hit - draw background
            graphics.setForegroundColor(backgroundColor);
            graphics.drawChar(x, y, backgroundChar);
        }
    }

    /**
     * Renders a pixel with anti-aliasing by casting multiple rays.
     */
    private void renderPixelWithAntiAliasing(Graphics graphics, int x, int y, double aspectRatio) {
        List<RayHit> hits = new ArrayList<>();
        int hitCount = 0;

        // Cast multiple rays with slight offsets for anti-aliasing
        for (int i = 0; i < antiAliasingRays; i++) {
            double offsetX = (Math.random() - 0.5);
            double offsetY = (Math.random() - 0.5);

            Ray3D ray = createCameraRay(x + offsetX, y + offsetY, aspectRatio);
            RayHit hit = castRay(ray);

            if (hit != null) {
                hit.computeShading(lightDirection, lightIntensity);
                hits.add(hit);
                hitCount++;
            }
        }

        if (hitCount > 0) {
            // Average the results
            RayHit averageHit = averageHits(hits);
            graphics.setForegroundColor(averageHit.getColor());
            graphics.drawChar(x, y, averageHit.getCharacter());
        } else {
            // No hits - draw background
            graphics.setForegroundColor(backgroundColor);
            graphics.drawChar(x, y, backgroundChar);
        }
    }

    /**
     * Creates a camera ray for the given pixel coordinates.
     */
    private Ray3D createCameraRay(double pixelX, double pixelY, double aspectRatio) {
        // Convert pixel coordinates to normalized device coordinates (-1 to 1)
        double ndcX = (2.0 * pixelX / getWidth()) - 1.0;
        double ndcY = 1.0 - (2.0 * pixelY / getHeight()); // Flip Y for proper orientation

        // Apply aspect ratio and field of view
        double fovScale = Math.tan(fieldOfView / 2.0);
        double rayX = ndcX * aspectRatio * fovScale;
        double rayY = ndcY * fovScale;
        double rayZ = -1.0; // Forward direction in camera space

        // Ray direction in camera space
        Point3D rayDirection = new Point3D(rayX, rayY, rayZ).normalize();

        // Transform ray from camera space to world space
        Matrix4x4 cameraToWorld = camera.getViewMatrix().inverse();
        Point3D worldRayOrigin = cameraToWorld.transform(new Point3D(0, 0, 0));
        Point3D worldRayDirection = cameraToWorld.transformDirection(rayDirection);

        return Ray3D.create(worldRayOrigin, worldRayDirection);
    }

    /**
     * Casts a ray into the scene and returns the closest hit.
     */
    private RayHit castRay(Ray3D ray) {
        RayHit closestHit = null;
        BigDecimal closestDistance = null;

        // Test intersection with all meshes
        for (Mesh3D mesh : meshes) {
            for (Mesh3D.Face3D face : mesh.getFaces()) {
                Point3D v0 = mesh.getVertices().get(face.getV1());
                Point3D v1 = mesh.getVertices().get(face.getV2());
                Point3D v2 = mesh.getVertices().get(face.getV3());

                RayHit hit = ray.intersectTriangle(v0, v1, v2);

                if (hit != null) {
                    // Check if this hit is within our depth range
                    if (hit.getDistance().compareTo(BigDecimal.valueOf(nearPlane)) >= 0 &&
                        hit.getDistance().compareTo(BigDecimal.valueOf(farPlane)) <= 0) {

                        // Check if this is the closest hit so far
                        if (closestDistance == null || hit.getDistance().compareTo(closestDistance) < 0) {
                            closestDistance = hit.getDistance();
                            closestHit = hit.withFaceAndMesh(face, mesh);
                        }
                    }
                }
            }
        }

        return closestHit;
    }

    /**
     * Averages multiple ray hits for anti-aliasing.
     */
    private RayHit averageHits(List<RayHit> hits) {
        if (hits.isEmpty()) {
            return null;
        }

        // For simplicity, return the hit with the shortest distance
        // In a more sophisticated implementation, we could average colors and characters
        RayHit closest = hits.get(0);
        for (RayHit hit : hits) {
            if (hit.getDistance().compareTo(closest.getDistance()) < 0) {
                closest = hit;
            }
        }

        return closest;
    }

    /**
     * Requests a redraw of the canvas.
     */
    private void requestRedraw() {
        // This would typically trigger a repaint in the framework
        // Implementation depends on the framework's redraw mechanism
    }
}
