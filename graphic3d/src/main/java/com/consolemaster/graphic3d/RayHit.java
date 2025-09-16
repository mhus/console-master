package com.consolemaster.graphic3d;

import com.consolemaster.AnsiColor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents the result of a ray-object intersection.
 * Contains all information needed for shading and rendering at the hit point.
 */
@Getter
@RequiredArgsConstructor
public class RayHit {

    private final Point3D hitPoint;     // World space position where ray hit the surface
    private final Point3D normal;       // Surface normal at hit point (normalized)
    private final BigDecimal distance;  // Distance from ray origin to hit point
    private final BigDecimal u;         // Barycentric coordinate u for texture mapping
    private final BigDecimal v;         // Barycentric coordinate v for texture mapping

    // Additional properties for rendering
    private Mesh3D.Face3D face;         // The face that was hit
    private Mesh3D mesh;                // The mesh that was hit
    private AnsiColor color;            // Computed color at hit point
    private char character;             // Computed character at hit point

    /**
     * Sets the face and mesh that were hit.
     */
    public RayHit withFaceAndMesh(Mesh3D.Face3D face, Mesh3D mesh) {
        RayHit hit = new RayHit(hitPoint, normal, distance, u, v);
        hit.face = face;
        hit.mesh = mesh;
        return hit;
    }

    /**
     * Computes the final color and character for this hit point based on lighting and material properties.
     */
    public void computeShading(Point3D lightDirection, double lightIntensity) {
        if (face == null) {
            this.color = AnsiColor.WHITE;
            this.character = '*';
            return;
        }

        // Calculate lighting using dot product of normal and light direction
        double dotProduct = Math.max(0.0, normal.dot(lightDirection.normalize()).doubleValue());
        double finalIntensity = lightIntensity * dotProduct;

        // Apply ambient lighting (minimum brightness)
        double ambientLight = 0.2;
        finalIntensity = Math.max(ambientLight, finalIntensity);

        // Get effective color and character from face
        this.color = face.getEffectiveColor(u.doubleValue(), v.doubleValue(), finalIntensity);
        this.character = face.getEffectiveCharacter(u.doubleValue(), v.doubleValue(), finalIntensity);

        // Apply intensity-based character selection for better depth perception
        if (finalIntensity > 0.8) {
            this.character = '#';
        } else if (finalIntensity > 0.6) {
            this.character = '+';
        } else if (finalIntensity > 0.4) {
            this.character = '-';
        } else if (finalIntensity > 0.2) {
            this.character = '.';
        } else {
            this.character = ' ';
        }
    }

    /**
     * Gets the computed color for rendering.
     */
    public AnsiColor getColor() {
        return color != null ? color : AnsiColor.WHITE;
    }

    /**
     * Gets the computed character for rendering.
     */
    public char getCharacter() {
        return character != '\0' ? character : '*';
    }

    @Override
    public String toString() {
        return String.format("RayHit{point=%s, normal=%s, distance=%.3f, u=%.3f, v=%.3f}",
                           hitPoint, normal, distance.doubleValue(), u.doubleValue(), v.doubleValue());
    }
}
