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
 * A background provider that simulates an alien planet environment.
 * Features multiple moons, alien aurora effects, floating particles,
 * and strange celestial phenomena typical of an extraterrestrial world.
 * Note: This provider must be manually registered with the AnimationManager for animation.
 */
@Getter
@Setter
public class AlienPlanetBackgroundProvider implements BackgroundProvider, AnimationTicker {

    // Animation settings
    private double animationSpeed = 0.02;
    private double particleSpeed = 0.05;
    private double auroraSpeed = 0.03;

    // Visual effects settings
    private boolean showMoons = true;
    private boolean showAurora = true;
    private boolean showParticles = true;
    private boolean showPulsar = true;
    private double particleDensity = 0.8;
    private double auroraBrightness = 0.7;

    // Sky colors for alien atmosphere
    private AnsiColor[] alienSkyColors = {
        AnsiColor.MAGENTA,      // Purple alien sky
        AnsiColor.CYAN,         // Turquoise atmosphere
        AnsiColor.YELLOW,       // Golden nebula
        AnsiColor.GREEN         // Toxic green atmosphere
    };

    private AnsiColor currentSkyColor = AnsiColor.MAGENTA;

    private Random random = new Random();
    private int canvasWidth = 80;
    private int canvasHeight = 25;
    private double playerAngle = 0.0;

    // Animation timers
    private double animationTimer = 0.0;
    private double particleTimer = 0.0;
    private double auroraTimer = 0.0;
    private double pulsarTimer = 0.0;
    private double skyTransitionTimer = 0.0;

    // Celestial objects
    private List<AlienMoon> moons = new ArrayList<>();
    private List<FloatingParticle> particles = new ArrayList<>();
    private List<AuroraStrip> auroraStrips = new ArrayList<>();

    public AlienPlanetBackgroundProvider() {
        initializeAlienEnvironment();
    }

    public AlienPlanetBackgroundProvider(int canvasWidth, int canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        initializeAlienEnvironment();
    }

    private void initializeAlienEnvironment() {
        initializeMoons();
        initializeParticles();
        initializeAuroraStrips();
    }

    private void initializeMoons() {
        moons.clear();

        // Large primary moon with rings
        moons.add(new AlienMoon(
            0.3, 0.2,           // Position (relative to screen)
            3.5,                // Size
            AnsiColor.BRIGHT_WHITE,
            AnsiColor.CYAN,
            '◯', '○', '·',
            true,               // Has rings
            0.01                // Orbit speed
        ));

        // Smaller secondary moon
        moons.add(new AlienMoon(
            0.7, 0.15,
            2.0,
            AnsiColor.YELLOW,
            AnsiColor.BRIGHT_YELLOW,
            '◉', '●', '·',
            false,
            0.02
        ));

        // Tiny distant moon
        moons.add(new AlienMoon(
            0.8, 0.4,
            1.2,
            AnsiColor.RED,
            AnsiColor.BRIGHT_RED,
            '●', '○', '·',
            false,
            0.005
        ));
    }

    private void initializeParticles() {
        particles.clear();
        int numParticles = (int) (canvasWidth * canvasHeight * particleDensity * 0.01);

        for (int i = 0; i < numParticles; i++) {
            particles.add(new FloatingParticle(
                random.nextDouble() * canvasWidth,
                random.nextDouble() * canvasHeight,
                random.nextDouble() * 0.02 + 0.005,  // Speed
                random.nextDouble() * Math.PI * 2,    // Direction
                0.3 + random.nextDouble() * 0.7,      // Brightness
                random.nextDouble() * Math.PI * 2,    // Phase
                0.02 + random.nextDouble() * 0.08     // Pulse speed
            ));
        }
    }

    private void initializeAuroraStrips() {
        auroraStrips.clear();

        // Create flowing aurora strips
        for (int i = 0; i < 5; i++) {
            auroraStrips.add(new AuroraStrip(
                random.nextDouble() * canvasWidth,
                random.nextDouble() * canvasHeight * 0.6, // Upper part of sky
                random.nextDouble() * 0.03 + 0.01,        // Flow speed
                0.5 + random.nextDouble() * 0.5,          // Intensity
                random.nextDouble() * Math.PI * 2         // Phase offset
            ));
        }
    }

    @Override
    public void setDimensionAndAngle(int width, int height, double playerAngle) {
        this.playerAngle = playerAngle;
        if (width != this.canvasWidth || height != this.canvasHeight) {
            this.canvasWidth = width;
            this.canvasHeight = height;
            initializeAlienEnvironment();
        }
    }

    @Override
    public StyledChar getBackground(int x, int y) {
        // Check for aurora effects first (they're atmospheric)
        if (showAurora) {
            StyledChar aurora = getAuroraEffect(x, y);
            if (aurora != null) return aurora;
        }

        // Check for floating particles
        if (showParticles) {
            StyledChar particle = getParticleEffect(x, y);
            if (particle != null) return particle;
        }

        // Check for moons
        if (showMoons) {
            StyledChar moon = getMoonEffect(x, y);
            if (moon != null) return moon;
        }

        // Check for pulsar effect
        if (showPulsar) {
            StyledChar pulsar = getPulsarEffect(x, y);
            if (pulsar != null) return pulsar;
        }

        // Return alien sky
        return new StyledChar(' ', null, currentSkyColor, null);
    }

    private StyledChar getAuroraEffect(int x, int y) {
        for (AuroraStrip strip : auroraStrips) {
            double distance = getAuroraDistance(x, y, strip);
            if (distance < 3.0) {
                double intensity = strip.intensity * auroraBrightness * (1.0 - distance / 3.0);
                double wave = Math.sin(auroraTimer * strip.flowSpeed + strip.phaseOffset + x * 0.1) * 0.5 + 0.5;
                intensity *= wave;

                if (intensity > 0.3) {
                    char auroraChar = intensity > 0.7 ? '▓' : intensity > 0.5 ? '▒' : '░';
                    AnsiColor auroraColor = getAuroraColor(intensity);
                    return new StyledChar(auroraChar, auroraColor, currentSkyColor, null);
                }
            }
        }
        return null;
    }

    private double getAuroraDistance(int x, int y, AuroraStrip strip) {
        // Create flowing ribbon-like aurora
        double waveY = strip.y + Math.sin(auroraTimer * strip.flowSpeed + x * 0.2) * 3.0;
        return Math.abs(y - waveY);
    }

    private AnsiColor getAuroraColor(double intensity) {
        if (intensity > 0.8) return AnsiColor.BRIGHT_GREEN;
        if (intensity > 0.6) return AnsiColor.GREEN;
        if (intensity > 0.4) return AnsiColor.CYAN;
        return AnsiColor.BLUE;
    }

    private StyledChar getParticleEffect(int x, int y) {
        for (FloatingParticle particle : particles) {
            double adjustedX = (particle.x + (playerAngle / (Math.PI * 2)) * canvasWidth) % canvasWidth;
            if (adjustedX < 0) adjustedX += canvasWidth;

            if (Math.abs(adjustedX - x) < 1.0 && Math.abs(particle.y - y) < 1.0) {
                double pulse = Math.sin(particle.phase) * 0.3 + 0.7;
                double effectiveBrightness = particle.brightness * pulse;

                if (effectiveBrightness > 0.5) {
                    char particleChar = effectiveBrightness > 0.8 ? '◆' : effectiveBrightness > 0.6 ? '◇' : '·';
                    AnsiColor particleColor = getParticleColor(effectiveBrightness);
                    return new StyledChar(particleChar, particleColor, currentSkyColor, null);
                }
            }
        }
        return null;
    }

    private AnsiColor getParticleColor(double brightness) {
        if (brightness > 0.9) return AnsiColor.BRIGHT_YELLOW;
        if (brightness > 0.7) return AnsiColor.YELLOW;
        if (brightness > 0.5) return AnsiColor.WHITE;
        return AnsiColor.BRIGHT_BLACK;
    }

    private StyledChar getMoonEffect(int x, int y) {
        for (AlienMoon moon : moons) {
            double moonX = moon.x * canvasWidth + Math.sin(animationTimer * moon.orbitSpeed) * 5.0;
            double moonY = moon.y * canvasHeight + Math.cos(animationTimer * moon.orbitSpeed) * 2.0;

            // Apply player angle effect
            moonX = (moonX + (playerAngle / (Math.PI * 2)) * canvasWidth * 0.1) % canvasWidth;

            double distance = Math.sqrt(Math.pow(x - moonX, 2) + Math.pow(y - moonY, 2));

            // Moon surface
            if (distance < moon.size) {
                if (distance < moon.size * 0.3) {
                    return new StyledChar(moon.coreChar, moon.coreColor, currentSkyColor, null);
                } else if (distance < moon.size * 0.7) {
                    return new StyledChar(moon.surfaceChar, moon.surfaceColor, currentSkyColor, null);
                } else {
                    return new StyledChar(moon.edgeChar, moon.surfaceColor, currentSkyColor, null);
                }
            }

            // Moon rings (for moons that have them)
            if (moon.hasRings && distance > moon.size && distance < moon.size + 2.0) {
                double ringPhase = Math.sin(x * 0.5 + animationTimer * 2.0);
                if (ringPhase > 0.3) {
                    return new StyledChar('─', AnsiColor.BRIGHT_BLACK, currentSkyColor, null);
                }
            }
        }
        return null;
    }

    private StyledChar getPulsarEffect(int x, int y) {
        // Create a pulsing distant star/pulsar effect
        double pulsarX = canvasWidth * 0.9;
        double pulsarY = canvasHeight * 0.1;
        double distance = Math.sqrt(Math.pow(x - pulsarX, 2) + Math.pow(y - pulsarY, 2));

        double pulseIntensity = Math.sin(pulsarTimer * 5.0) * 0.5 + 0.5;
        double maxDistance = 2.0 + pulseIntensity * 3.0;

        if (distance < maxDistance) {
            double intensity = (1.0 - distance / maxDistance) * pulseIntensity;
            if (intensity > 0.3) {
                char pulsarChar = intensity > 0.8 ? '✦' : intensity > 0.6 ? '*' : '·';
                AnsiColor pulsarColor = intensity > 0.7 ? AnsiColor.BRIGHT_MAGENTA : AnsiColor.MAGENTA;
                return new StyledChar(pulsarChar, pulsarColor, currentSkyColor, null);
            }
        }

        return null;
    }

    @Override
    public boolean tick() {
        // Update animation timers
        animationTimer += animationSpeed;
        particleTimer += particleSpeed;
        auroraTimer += auroraSpeed;
        pulsarTimer += 0.05;
        skyTransitionTimer += 0.001;

        // Slowly transition sky color
        if (skyTransitionTimer > Math.PI * 2) {
            skyTransitionTimer = 0.0;
            currentSkyColor = alienSkyColors[random.nextInt(alienSkyColors.length)];
        }

        // Update floating particles
        for (FloatingParticle particle : particles) {
            particle.x += Math.cos(particle.direction) * particle.speed;
            particle.y += Math.sin(particle.direction) * particle.speed;
            particle.phase += particle.pulseSpeed;

            // Wrap particles around screen
            if (particle.x < 0) particle.x = canvasWidth;
            if (particle.x > canvasWidth) particle.x = 0;
            if (particle.y < 0) particle.y = canvasHeight;
            if (particle.y > canvasHeight) particle.y = 0;

            // Occasionally change direction
            if (random.nextDouble() < 0.001) {
                particle.direction += (random.nextDouble() - 0.5) * 0.5;
            }
        }

        // Update aurora strips
        for (AuroraStrip strip : auroraStrips) {
            strip.y += strip.flowSpeed;
            if (strip.y > canvasHeight) {
                strip.y = -5.0;
                strip.x = random.nextDouble() * canvasWidth;
            }
        }

        return true;
    }

    // Setter methods for dynamic control
    public void setAlienSkyColor(AnsiColor color) {
        this.currentSkyColor = color;
    }

    public void triggerSkyTransition() {
        this.currentSkyColor = alienSkyColors[random.nextInt(alienSkyColors.length)];
    }

    public void addRandomParticleBurst() {
        // Add temporary particle burst effect
        for (int i = 0; i < 20; i++) {
            particles.add(new FloatingParticle(
                canvasWidth * 0.5,
                canvasHeight * 0.5,
                random.nextDouble() * 0.1 + 0.05,
                random.nextDouble() * Math.PI * 2,
                0.8 + random.nextDouble() * 0.2,
                0.0,
                0.1
            ));
        }
    }

    /**
     * Alien moon class representing celestial bodies in the alien sky
     */
    private static class AlienMoon {
        double x, y;           // Relative position (0.0 to 1.0)
        double size;           // Radius in pixels
        AnsiColor coreColor;
        AnsiColor surfaceColor;
        char coreChar;
        char surfaceChar;
        char edgeChar;
        boolean hasRings;
        double orbitSpeed;

        AlienMoon(double x, double y, double size, AnsiColor coreColor, AnsiColor surfaceColor,
                 char coreChar, char surfaceChar, char edgeChar, boolean hasRings, double orbitSpeed) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.coreColor = coreColor;
            this.surfaceColor = surfaceColor;
            this.coreChar = coreChar;
            this.surfaceChar = surfaceChar;
            this.edgeChar = edgeChar;
            this.hasRings = hasRings;
            this.orbitSpeed = orbitSpeed;
        }
    }

    /**
     * Floating particle class for atmospheric effects
     */
    private static class FloatingParticle {
        double x, y;
        double speed;
        double direction;
        double brightness;
        double phase;
        double pulseSpeed;

        FloatingParticle(double x, double y, double speed, double direction,
                        double brightness, double phase, double pulseSpeed) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.direction = direction;
            this.brightness = brightness;
            this.phase = phase;
            this.pulseSpeed = pulseSpeed;
        }
    }

    /**
     * Aurora strip class for alien atmospheric light shows
     */
    private static class AuroraStrip {
        double x, y;
        double flowSpeed;
        double intensity;
        double phaseOffset;

        AuroraStrip(double x, double y, double flowSpeed, double intensity, double phaseOffset) {
            this.x = x;
            this.y = y;
            this.flowSpeed = flowSpeed;
            this.intensity = intensity;
            this.phaseOffset = phaseOffset;
        }
    }
}
