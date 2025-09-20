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
 * A background provider that simulates a starfield with slowly moving stars.
 * Creates a realistic night sky effect with stars of different brightness levels.
 * Note: This provider must be manually registered with the AnimationManager for animation.
 */
@Getter
@Setter
public class StarfieldBackgroundProvider implements BackgroundProvider, AnimationTicker {

    private double starSpeed = 0.5; // Speed of star movement per tick
    private double starDensity = 0.02; // Density of stars (0.0 - 1.0)
    private int numStars = 200; // Number of stars in the field

    private AnsiColor skyColor = AnsiColor.BLACK;
    private AnsiColor[] starColors = {
        AnsiColor.WHITE,
        AnsiColor.BRIGHT_WHITE,
        AnsiColor.YELLOW,
        AnsiColor.BRIGHT_YELLOW,
        AnsiColor.BLUE,
        AnsiColor.CYAN
    };

    private char[] starChars = {'.', '*', '✦', '✧', '◦'};

    private List<Star> stars = new ArrayList<>();
    private Random random = new Random();
    private int canvasWidth = 80; // Default canvas width
    private int canvasHeight = 25; // Default canvas height
    private double playerAngle = 0.0; // Current player viewing angle

    public StarfieldBackgroundProvider() {
        initializeStars();
        // No automatic registration - let application handle it
    }

    public StarfieldBackgroundProvider(int canvasWidth, int canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        initializeStars();
        // No automatic registration - let application handle it
    }

    public StarfieldBackgroundProvider(double starSpeed, double starDensity, int numStars) {
        this.starSpeed = starSpeed;
        this.starDensity = starDensity;
        this.numStars = numStars;
        initializeStars();
        // No automatic registration - let application handle it
    }

    private void initializeStars() {
        stars.clear();
        for (int i = 0; i < numStars; i++) {
            stars.add(createRandomStar());
        }
    }

    private Star createRandomStar() {
        // Create stars in spherical coordinates around the player
        // Random angle (0 to 2π) and elevation (-π/2 to π/2)
        double angle = random.nextDouble() * Math.PI * 2;
        double elevation = (random.nextDouble() - 0.5) * Math.PI; // -π/2 to π/2
        double brightness = random.nextDouble();
        double twinklePhase = random.nextDouble() * Math.PI * 2;
        double twinkleSpeed = 0.1 + random.nextDouble() * 0.2;

        return new Star(angle, elevation, brightness, twinklePhase, twinkleSpeed);
    }

    @Override
    public void setDimensionAndAngle(int width, int height, double playerAngle) {
        this.playerAngle = playerAngle;
        if (stars.isEmpty() || width != this.canvasWidth || height != this.canvasHeight) {
            this.canvasWidth = width;
            this.canvasHeight = height;
            initializeStars(); // Reinitialize stars for new canvas size
        }
    }

    @Override
    public StyledChar getBackground(int x, int y) {
        // Convert screen coordinates to viewing angles
        double screenCenterX = canvasWidth / 2.0;
        double screenCenterY = canvasHeight / 2.0;

        // Normalized screen coordinates (-1 to 1)
        double normalizedX = (x - screenCenterX) / screenCenterX;
        double normalizedY = (y - screenCenterY) / screenCenterY;

        // Calculate the world angle for this pixel (considering FOV)
        double fov = Math.PI / 3.0; // 60 degrees FOV to match raycasting
        double pixelAngle = playerAngle + normalizedX * (fov / 2.0);
        double pixelElevation = normalizedY * (Math.PI / 6.0); // Vertical FOV of 30 degrees

        // Find stars visible at this viewing direction
        for (Star star : stars) {
            // Calculate angular distance between pixel direction and star position
            double angleDiff = Math.abs(star.angle - pixelAngle);
            // Handle angle wrapping (shortest path around circle)
            if (angleDiff > Math.PI) {
                angleDiff = 2 * Math.PI - angleDiff;
            }

            double elevationDiff = Math.abs(star.elevation - pixelElevation);

            // Check if star is visible at this pixel (within angular tolerance)
            double angularTolerance = 0.1; // Adjust this to control star size
            if (angleDiff < angularTolerance && elevationDiff < angularTolerance) {
                // Calculate twinkling effect
                double twinkle = Math.sin(star.twinklePhase) * 0.3 + 0.7; // 0.4 to 1.0
                double effectiveBrightness = star.brightness * twinkle;

                // Select character based on brightness
                int charIndex = (int) (effectiveBrightness * (starChars.length - 1));
                charIndex = Math.max(0, Math.min(charIndex, starChars.length - 1));

                // Select color based on brightness
                int colorIndex = (int) (effectiveBrightness * (starColors.length - 1));
                colorIndex = Math.max(0, Math.min(colorIndex, starColors.length - 1));

                return new StyledChar(starChars[charIndex], starColors[colorIndex], skyColor, null);
            }
        }

        // No star at this position - return sky
        return new StyledChar(' ', null, skyColor, null);
    }

    @Override
    public boolean tick() {
        // Update twinkling for all stars
        for (Star star : stars) {
            star.twinklePhase += star.twinkleSpeed;

            // Slowly rotate the starfield (optional effect)
            // star.angle += starSpeed * 0.001; // Very slow rotation
        }

        return true; // Continue animation
    }

    /**
     * Internal class representing a single star in the starfield.
     */
    private static class Star {
        double angle; // Angle around the vertical axis (azimuth)
        double elevation; // Elevation angle from the horizontal plane
        double brightness;
        double twinklePhase;
        double twinkleSpeed;

        Star(double angle, double elevation, double brightness, double twinklePhase, double twinkleSpeed) {
            this.angle = angle;
            this.elevation = elevation;
            this.brightness = brightness;
            this.twinklePhase = twinklePhase;
            this.twinkleSpeed = twinkleSpeed;
        }
    }
}
