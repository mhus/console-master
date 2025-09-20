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
    private double playerAngle;

    public StarfieldBackgroundProvider() {
    }

    public StarfieldBackgroundProvider(int canvasWidth, int canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        // No automatic registration - let application handle it
    }

    public StarfieldBackgroundProvider(double starSpeed, double starDensity, int numStars) {
        this.starSpeed = starSpeed;
        this.starDensity = starDensity;
        this.numStars = numStars;
        // No automatic registration - let application handle it
    }

    private void initializeStars() {
        stars.clear();
        for (int i = 0; i < numStars; i++) {
            stars.add(createRandomStar());
        }
    }

    private Star createRandomStar() {
        double x = random.nextDouble() * canvasWidth * 2; // Extended width for smooth scrolling
        double y = random.nextDouble() * canvasHeight;
        double brightness = random.nextDouble();
        double twinklePhase = random.nextDouble() * Math.PI * 2;
        double twinkleSpeed = 0.1 + random.nextDouble() * 0.2;

        return new Star(x, y, brightness, twinklePhase, twinkleSpeed);
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
        // Find the closest star to this position
        Star closestStar = null;
        double closestDistance = Double.MAX_VALUE;

        for (Star star : stars) {
            double distance = Math.sqrt(Math.pow(star.x - x, 2) + Math.pow(star.y - y, 2));
            if (distance < closestDistance && distance < 1.0) { // Star must be very close to position
                closestDistance = distance;
                closestStar = star;
            }
        }

        if (closestStar != null) {
            // Calculate twinkling effect
            double twinkle = Math.sin(closestStar.twinklePhase) * 0.3 + 0.7; // 0.4 to 1.0
            double effectiveBrightness = closestStar.brightness * twinkle;

            // Select character based on brightness
            int charIndex = (int) (effectiveBrightness * (starChars.length - 1));
            charIndex = Math.max(0, Math.min(charIndex, starChars.length - 1));

            // Select color based on brightness
            int colorIndex = (int) (effectiveBrightness * (starColors.length - 1));
            colorIndex = Math.max(0, Math.min(colorIndex, starColors.length - 1));

            return new StyledChar(starChars[charIndex], starColors[colorIndex], skyColor, null);
        }

        // No star at this position - return sky
        return new StyledChar(' ', null, skyColor, null);
    }

    @Override
    public boolean tick() {
        // Move stars and update twinkling
        for (Star star : stars) {
            star.x -= starSpeed;
            star.twinklePhase += star.twinkleSpeed;

            // Reset star position when it moves off screen
            if (star.x < -10) {
                star.x = canvasWidth + 10 + random.nextDouble() * 20;
                star.y = random.nextDouble() * canvasHeight;
                star.brightness = random.nextDouble();
            }
        }

        return true; // Continue animation
    }

    /**
     * Internal class representing a single star in the starfield.
     */
    private static class Star {
        double x, y;
        double brightness;
        double twinklePhase;
        double twinkleSpeed;

        Star(double x, double y, double brightness, double twinklePhase, double twinkleSpeed) {
            this.x = x;
            this.y = y;
            this.brightness = brightness;
            this.twinklePhase = twinklePhase;
            this.twinkleSpeed = twinkleSpeed;
        }
    }
}
