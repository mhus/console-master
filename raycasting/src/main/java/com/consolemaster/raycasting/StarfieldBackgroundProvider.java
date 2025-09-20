package com.consolemaster.raycasting;

import com.consolemaster.StyledChar;
import com.consolemaster.AnsiColor;
import com.consolemaster.AnimationTicker;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

/**
 * A background provider that simulates a starfield with slowly rotating stars.
 * Stars are positioned on a sphere around the viewer and the entire sphere rotates,
 * maintaining relative positions of stars.
 * Note: This provider must be manually registered with the AnimationManager for animation.
 */
@Getter
@Setter
public class StarfieldBackgroundProvider implements BackgroundProvider, AnimationTicker {

    private double rotationSpeed = 0.01; // Speed of sphere rotation per tick
    private double starDensity = 0.02; // Density of stars (0.0 - 1.0)
    private int numStars = 300; // Number of stars in the field
    private double starSphereRadius = 100.0; // Radius of the star sphere

    private AnsiColor skyColor = AnsiColor.BLACK;
    private AnsiColor[] starColors = {
        AnsiColor.WHITE,
        AnsiColor.BRIGHT_WHITE,
        AnsiColor.YELLOW,
        AnsiColor.BRIGHT_YELLOW,
        AnsiColor.BLUE,
        AnsiColor.CYAN
    };

    private char[] starChars = {'.', '*', '✦', '✧', '◦', '●'};

    private List<Star> stars = new ArrayList<>();
    private Random random = new Random();
    private int canvasWidth = 80; // Default canvas width
    private int canvasHeight = 25; // Default canvas height
    private double playerAngle = 0.0; // Current player viewing angle

    // Rotation state for the star sphere
    private double sphereRotationX = 0.0;
    private double sphereRotationY = 0.0;
    private double sphereRotationZ = 0.0;

    public StarfieldBackgroundProvider() {
        initializeStars();
    }

    public StarfieldBackgroundProvider(int canvasWidth, int canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        initializeStars();
    }

    public StarfieldBackgroundProvider(double rotationSpeed, double starDensity, int numStars) {
        this.rotationSpeed = rotationSpeed;
        this.starDensity = starDensity;
        this.numStars = numStars;
        initializeStars();
    }

    private void initializeStars() {
        stars.clear();
        for (int i = 0; i < numStars; i++) {
            stars.add(createRandomStar());
        }
    }

    private Star createRandomStar() {
        // Create stars uniformly distributed on a sphere surface
        // Using spherical coordinates and converting to Cartesian
        double theta = random.nextDouble() * Math.PI * 2; // Azimuthal angle (0 to 2π)
        double phi = Math.acos(2.0 * random.nextDouble() - 1.0); // Polar angle (0 to π) for uniform distribution

        // Convert to Cartesian coordinates on unit sphere
        double x = Math.sin(phi) * Math.cos(theta);
        double y = Math.sin(phi) * Math.sin(theta);
        double z = Math.cos(phi);

        // Scale to our sphere radius
        x *= starSphereRadius;
        y *= starSphereRadius;
        z *= starSphereRadius;

        double brightness = 0.3 + random.nextDouble() * 0.7; // Brightness between 0.3 and 1.0
        double twinklePhase = random.nextDouble() * Math.PI * 2;
        double twinkleSpeed = 0.05 + random.nextDouble() * 0.15;

        return new Star(x, y, z, brightness, twinklePhase, twinkleSpeed);
    }

    @Override
    public void setDimensionAndAngle(int width, int height, double playerAngle) {
        this.playerAngle = playerAngle;
        if (stars.isEmpty() || width != this.canvasWidth || height != this.canvasHeight) {
            this.canvasWidth = width;
            this.canvasHeight = height;
            // Don't reinitialize stars - keep them in their positions
        }
    }

    @Override
    public StyledChar getBackground(int x, int y) {
        // Convert screen coordinates to viewing direction
        double screenCenterX = canvasWidth / 2.0;
        double screenCenterY = canvasHeight / 2.0;

        // Normalized screen coordinates (-1 to 1)
        double normalizedX = (x - screenCenterX) / screenCenterX;
        double normalizedY = (y - screenCenterY) / screenCenterY;

        // Calculate the viewing direction for this pixel
        double fov = Math.PI / 3.0; // 60 degrees FOV to match raycasting
        double horizontalAngle = playerAngle + normalizedX * (fov / 2.0);
        double verticalAngle = normalizedY * (Math.PI / 6.0); // Vertical FOV of 30 degrees

        // Convert viewing direction to 3D direction vector
        double dirX = Math.cos(verticalAngle) * Math.cos(horizontalAngle);
        double dirY = Math.cos(verticalAngle) * Math.sin(horizontalAngle);
        double dirZ = Math.sin(verticalAngle);

        // Find the closest star in this viewing direction
        Star closestStar = null;
        double minDistance = Double.MAX_VALUE;

        for (Star star : stars) {
            // Apply sphere rotation to get current star position
            Point3D rotatedPos = rotatePoint(star.originalX, star.originalY, star.originalZ);

            // Calculate the direction from origin to star
            double starDirX = rotatedPos.x / starSphereRadius;
            double starDirY = rotatedPos.y / starSphereRadius;
            double starDirZ = rotatedPos.z / starSphereRadius;

            // Calculate angular distance between viewing direction and star direction
            double dotProduct = dirX * starDirX + dirY * starDirY + dirZ * starDirZ;
            double angularDistance = Math.acos(Math.max(-1.0, Math.min(1.0, dotProduct)));

            // Check if star is visible (within angular tolerance)
            double angularTolerance = 0.08; // Adjust this to control star size
            if (angularDistance < angularTolerance && angularDistance < minDistance) {
                minDistance = angularDistance;
                closestStar = star;
            }
        }

        if (closestStar != null) {
            // Calculate twinkling effect
            double twinkle = Math.sin(closestStar.twinklePhase) * 0.3 + 0.7; // 0.4 to 1.0
            double effectiveBrightness = closestStar.brightness * twinkle;

            // Select character based on brightness and distance
            double brightnessWithDistance = effectiveBrightness * (1.0 - minDistance / 0.08);
            int charIndex = (int) (brightnessWithDistance * (starChars.length - 1));
            charIndex = Math.max(0, Math.min(charIndex, starChars.length - 1));

            // Select color based on brightness
            int colorIndex = (int) (effectiveBrightness * (starColors.length - 1));
            colorIndex = Math.max(0, Math.min(colorIndex, starColors.length - 1));

            return new StyledChar(starChars[charIndex], starColors[colorIndex], skyColor, null);
        }

        // No star at this position - return sky
        return new StyledChar(' ', null, skyColor, null);
    }

    /**
     * Rotates a point around the origin using current sphere rotation angles
     */
    private Point3D rotatePoint(double x, double y, double z) {
        // Rotation around X axis
        double cosX = Math.cos(sphereRotationX);
        double sinX = Math.sin(sphereRotationX);
        double y1 = y * cosX - z * sinX;
        double z1 = y * sinX + z * cosX;

        // Rotation around Y axis
        double cosY = Math.cos(sphereRotationY);
        double sinY = Math.sin(sphereRotationY);
        double x2 = x * cosY + z1 * sinY;
        double z2 = -x * sinY + z1 * cosY;

        // Rotation around Z axis
        double cosZ = Math.cos(sphereRotationZ);
        double sinZ = Math.sin(sphereRotationZ);
        double x3 = x2 * cosZ - y1 * sinZ;
        double y3 = x2 * sinZ + y1 * cosZ;

        return new Point3D(x3, y3, z2);
    }

    @Override
    public boolean tick() {
        // Update sphere rotation
        sphereRotationY += rotationSpeed; // Main rotation around Y axis
        sphereRotationX += rotationSpeed * 0.3; // Slower rotation around X axis
        sphereRotationZ += rotationSpeed * 0.1; // Even slower rotation around Z axis

        // Keep angles in reasonable range
        sphereRotationX %= (2 * Math.PI);
        sphereRotationY %= (2 * Math.PI);
        sphereRotationZ %= (2 * Math.PI);

        // Update twinkling for all stars
        for (Star star : stars) {
            star.twinklePhase += star.twinkleSpeed;
        }

        return true; // Continue animation
    }

    /**
     * Simple 3D point class for rotated positions
     */
    private static class Point3D {
        final double x, y, z;

        Point3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    /**
     * Internal class representing a single star in the starfield.
     * Stars are positioned on a sphere and maintain their relative positions.
     */
    private static class Star {
        final double originalX, originalY, originalZ; // Original position on sphere
        final double brightness;
        double twinklePhase;
        final double twinkleSpeed;

        Star(double x, double y, double z, double brightness, double twinklePhase, double twinkleSpeed) {
            this.originalX = x;
            this.originalY = y;
            this.originalZ = z;
            this.brightness = brightness;
            this.twinklePhase = twinklePhase;
            this.twinkleSpeed = twinkleSpeed;
        }
    }
}
