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
 * A background provider that displays realistic constellation patterns.
 * Shows recognizable star patterns that slowly drift across the night sky.
 * Features different star types, constellation lines, and atmospheric twinkling.
 * Note: This provider must be manually registered with the AnimationManager for animation.
 */
@Getter
@Setter
public class ConstellationBackgroundProvider implements BackgroundProvider, AnimationTicker {

    private double driftSpeed = 0.005; // Speed of constellation drift per tick
    private double twinkleIntensity = 0.4; // Intensity of star twinkling
    private boolean showConstellationLines = true; // Show lines connecting stars

    private AnsiColor skyColor = AnsiColor.BLACK;
    private AnsiColor[] starColors = {
        AnsiColor.WHITE,
        AnsiColor.BRIGHT_WHITE,
        AnsiColor.YELLOW,
        AnsiColor.BRIGHT_YELLOW,
        AnsiColor.CYAN,
        AnsiColor.BLUE
    };

    // Different star types
    private char[] mainStars = {'★', '✦', '●', '*'};
    private char[] smallStars = {'.', '·', '◦', '°'};
    private char constellationLine = '─';

    private List<Constellation> constellations = new ArrayList<>();
    private List<Star> backgroundStars = new ArrayList<>();
    private Random random = new Random();
    private int canvasWidth = 80;
    private int canvasHeight = 25;
    private double playerAngle = 0.0;
    private double skyOffset = 0.0; // Current drift offset

    public ConstellationBackgroundProvider() {
        initializeConstellations();
        initializeBackgroundStars();
    }

    public ConstellationBackgroundProvider(int canvasWidth, int canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        initializeConstellations();
        initializeBackgroundStars();
    }

    private void initializeConstellations() {
        constellations.clear();

        // Create Big Dipper (Großer Wagen)
        Constellation bigDipper = new Constellation("Big Dipper");
        bigDipper.addStar(10, 8, 0.9, 0);
        bigDipper.addStar(15, 6, 0.8, 1);
        bigDipper.addStar(20, 5, 0.9, 2);
        bigDipper.addStar(25, 4, 0.7, 3);
        bigDipper.addStar(30, 6, 0.8, 4);
        bigDipper.addStar(35, 8, 0.9, 5);
        bigDipper.addStar(40, 10, 0.8, 6);
        // Connect the stars
        bigDipper.connectStars(0, 1, 2, 3, 4, 5, 6);
        constellations.add(bigDipper);

        // Create Orion's Belt (Orion Gürtel)
        Constellation orion = new Constellation("Orion");
        orion.addStar(50, 15, 1.0, 0); // Alnitak
        orion.addStar(55, 14, 0.9, 1); // Alnilam
        orion.addStar(60, 13, 0.8, 2); // Mintaka
        // Orion's shoulders and feet
        orion.addStar(45, 10, 0.9, 3); // Betelgeuse
        orion.addStar(65, 10, 0.8, 4); // Bellatrix
        orion.addStar(48, 20, 0.7, 5); // Rigel
        orion.addStar(62, 18, 0.6, 6); // Saiph
        // Connect constellation
        orion.connectStars(3, 0, 1, 2, 4); // Top line
        orion.connectStars(0, 5); // Left side
        orion.connectStars(2, 6); // Right side
        constellations.add(orion);

        // Create Cassiopeia (W-shaped)
        Constellation cassiopeia = new Constellation("Cassiopeia");
        cassiopeia.addStar(70, 3, 0.8, 0);
        cassiopeia.addStar(75, 1, 0.9, 1);
        cassiopeia.addStar(80, 3, 0.7, 2);
        cassiopeia.addStar(85, 1, 0.8, 3);
        cassiopeia.addStar(90, 3, 0.9, 4);
        cassiopeia.connectStars(0, 1, 2, 3, 4);
        constellations.add(cassiopeia);

        // Create Southern Cross (Kreuz des Südens)
        Constellation southernCross = new Constellation("Southern Cross");
        southernCross.addStar(20, 18, 0.9, 0); // Acrux
        southernCross.addStar(25, 15, 0.8, 1); // Gacrux
        southernCross.addStar(30, 22, 0.7, 2); // Imai
        southernCross.addStar(18, 20, 0.8, 3); // Mimosa
        // Cross pattern
        southernCross.connectStars(1, 2); // Vertical
        southernCross.connectStars(0, 3); // Horizontal
        constellations.add(southernCross);
    }

    private void initializeBackgroundStars() {
        backgroundStars.clear();
        // Add random background stars for atmosphere
        for (int i = 0; i < 80; i++) {
            double x = random.nextDouble() * (canvasWidth * 2); // Wider range for scrolling
            double y = random.nextDouble() * canvasHeight;
            double brightness = 0.2 + random.nextDouble() * 0.5; // Dimmer background stars
            double twinklePhase = random.nextDouble() * Math.PI * 2;
            double twinkleSpeed = 0.02 + random.nextDouble() * 0.08;

            backgroundStars.add(new Star(x, y, brightness, twinklePhase, twinkleSpeed, false));
        }
    }

    @Override
    public void setDimensionAndAngle(int width, int height, double playerAngle) {
        this.playerAngle = playerAngle;
        if (width != this.canvasWidth || height != this.canvasHeight) {
            this.canvasWidth = width;
            this.canvasHeight = height;
            // Reinitialize with new dimensions
            initializeConstellations();
            initializeBackgroundStars();
        }
    }

    @Override
    public StyledChar getBackground(int x, int y) {
        // Calculate effective x position considering drift and player angle
        double angleOffset = (playerAngle / (Math.PI * 2)) * canvasWidth;
        double effectiveX = (x + skyOffset + angleOffset) % (canvasWidth * 2);

        // Check constellation stars first (they have priority)
        for (Constellation constellation : constellations) {
            for (Star star : constellation.stars) {
                if (isStarVisible(star, effectiveX, y)) {
                    return renderStar(star, true);
                }
            }
        }

        // Check constellation lines
        if (showConstellationLines) {
            for (Constellation constellation : constellations) {
                StyledChar lineChar = checkConstellationLines(constellation, effectiveX, y);
                if (lineChar != null) {
                    return lineChar;
                }
            }
        }

        // Check background stars
        for (Star star : backgroundStars) {
            if (isStarVisible(star, effectiveX, y)) {
                return renderStar(star, false);
            }
        }

        // Empty sky
        return new StyledChar(' ', null, skyColor, null);
    }

    private boolean isStarVisible(Star star, double effectiveX, double y) {
        double dx = Math.abs(star.x - effectiveX);
        double dy = Math.abs(star.y - y);
        return dx < 1.0 && dy < 1.0;
    }

    private StyledChar renderStar(Star star, boolean isConstellationStar) {
        // Calculate twinkling effect
        double twinkle = Math.sin(star.twinklePhase) * twinkleIntensity + (1.0 - twinkleIntensity);
        double effectiveBrightness = star.brightness * twinkle;

        // Select character based on star type and brightness
        char[] charSet = isConstellationStar ? mainStars : smallStars;
        int charIndex = (int) (effectiveBrightness * (charSet.length - 1));
        charIndex = Math.max(0, Math.min(charIndex, charSet.length - 1));

        // Select color based on brightness
        int colorIndex = (int) (effectiveBrightness * (starColors.length - 1));
        colorIndex = Math.max(0, Math.min(colorIndex, starColors.length - 1));

        return new StyledChar(charSet[charIndex], starColors[colorIndex], skyColor, null);
    }

    private StyledChar checkConstellationLines(Constellation constellation, double effectiveX, double y) {
        for (int i = 0; i < constellation.connections.size() - 1; i += 2) {
            int starIndex1 = constellation.connections.get(i);
            int starIndex2 = constellation.connections.get(i + 1);

            if (starIndex1 < constellation.stars.size() && starIndex2 < constellation.stars.size()) {
                Star star1 = constellation.stars.get(starIndex1);
                Star star2 = constellation.stars.get(starIndex2);

                if (isPointOnLine(star1.x, star1.y, star2.x, star2.y, effectiveX, y)) {
                    return new StyledChar(constellationLine, AnsiColor.BRIGHT_BLACK, skyColor, null);
                }
            }
        }
        return null;
    }

    private boolean isPointOnLine(double x1, double y1, double x2, double y2, double px, double py) {
        // Check if point (px, py) is approximately on line from (x1, y1) to (x2, y2)
        double tolerance = 0.8;

        // Calculate distance from point to line
        double A = y2 - y1;
        double B = x1 - x2;
        double C = x2 * y1 - x1 * y2;

        double distance = Math.abs(A * px + B * py + C) / Math.sqrt(A * A + B * B);

        if (distance > tolerance) return false;

        // Check if point is within line segment bounds
        double minX = Math.min(x1, x2) - tolerance;
        double maxX = Math.max(x1, x2) + tolerance;
        double minY = Math.min(y1, y2) - tolerance;
        double maxY = Math.max(y1, y2) + tolerance;

        return px >= minX && px <= maxX && py >= minY && py <= maxY;
    }

    @Override
    public boolean tick() {
        // Slowly drift the sky
        skyOffset += driftSpeed;
        if (skyOffset >= canvasWidth * 2) {
            skyOffset = 0;
        }

        // Update twinkling for all stars
        for (Constellation constellation : constellations) {
            for (Star star : constellation.stars) {
                star.twinklePhase += star.twinkleSpeed;
            }
        }

        for (Star star : backgroundStars) {
            star.twinklePhase += star.twinkleSpeed;
        }

        return true;
    }

    /**
     * Star class representing individual stars in constellations and background
     */
    private static class Star {
        double x, y;
        double brightness;
        double twinklePhase;
        double twinkleSpeed;
        boolean isConstellationStar;

        Star(double x, double y, double brightness, double twinklePhase, double twinkleSpeed, boolean isConstellationStar) {
            this.x = x;
            this.y = y;
            this.brightness = brightness;
            this.twinklePhase = twinklePhase;
            this.twinkleSpeed = twinkleSpeed;
            this.isConstellationStar = isConstellationStar;
        }
    }

    /**
     * Constellation class representing a group of connected stars
     */
    private static class Constellation {
        String name;
        List<Star> stars = new ArrayList<>();
        List<Integer> connections = new ArrayList<>(); // Pairs of star indices to connect

        Constellation(String name) {
            this.name = name;
        }

        void addStar(double x, double y, double brightness, int index) {
            double twinklePhase = Math.random() * Math.PI * 2;
            double twinkleSpeed = 0.03 + Math.random() * 0.07;
            stars.add(new Star(x, y, brightness, twinklePhase, twinkleSpeed, true));
        }

        void connectStars(int... starIndices) {
            for (int i = 0; i < starIndices.length - 1; i++) {
                connections.add(starIndices[i]);
                connections.add(starIndices[i + 1]);
            }
        }
    }
}
