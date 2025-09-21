package com.consolemaster.demo;

import com.consolemaster.AnimationThrottle;
import com.consolemaster.ScreenCanvas;
import com.consolemaster.ProcessLoop;
import com.consolemaster.KeyEvent;
import com.consolemaster.AnsiColor;
import com.consolemaster.AnimationManager;
import com.consolemaster.EventHandler;
import com.consolemaster.raycasting.RaycastingCanvas;
import com.consolemaster.raycasting.BackgroundProvider;
import com.consolemaster.raycasting.SolidColorBackgroundProvider;
import com.consolemaster.raycasting.CloudsBackgroundProvider;
import com.consolemaster.raycasting.StarfieldBackgroundProvider;
import com.consolemaster.raycasting.ConstellationBackgroundProvider;
import com.consolemaster.raycasting.DayNightCycleBackgroundProvider;
import com.consolemaster.raycasting.AlienPlanetBackgroundProvider;

import java.io.IOException;

/**
 * Demo application showcasing the different BackgroundProvider implementations
 * for RaycastingCanvas. Users can switch between solid color, clouds, and starfield
 * backgrounds using keyboard shortcuts.
 */
public class BackgroundDemo {

    private ScreenCanvas screen;
    private RaycastingCanvas raycastingCanvas;
    private ProcessLoop processLoop;

    // Background providers
    private SolidColorBackgroundProvider solidBackground;
    private CloudsBackgroundProvider cloudsBackground;
    private StarfieldBackgroundProvider starfieldBackground;
    private ConstellationBackgroundProvider constellationBackground;
    private DayNightCycleBackgroundProvider dayNightBackground;
    private AlienPlanetBackgroundProvider alienPlanetBackground;
    private AnimationThrottle startfieldThrottle;

    // Current background type
    private BackgroundType currentBackground = BackgroundType.SOLID;

    private enum BackgroundType {
        SOLID("Solid Color Background"),
        CLOUDS("Animated Clouds Background"),
        STARFIELD("Animated Starfield Background"),
        CONSTELLATION("Constellation Background"),
        DAY_NIGHT_CYCLE("Day-Night Cycle with Weather"),
        ALIEN_PLANET("Alien Planet Environment");

        private final String description;

        BackgroundType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public static void main(String[] args) throws IOException {
        new BackgroundDemo().run();
    }

    public void run() throws IOException {
        setupComponents();
        setupBackgrounds();
        setupMap();
        setupScreen();

        // Start with solid background
        setBackground(BackgroundType.SOLID);

        // Start the process loop
        processLoop = new ProcessLoop(screen);
        processLoop.start();
    }

    private void setupComponents() throws IOException {
        screen = new ScreenCanvas(80, 25);

        // Create raycasting canvas
        raycastingCanvas = new RaycastingCanvas("Background Demo", 80, 25);
        raycastingCanvas.setPosition(0, 0);
        raycastingCanvas.setVisible(true);

        // Configure raycasting appearance
        raycastingCanvas.setWallColor(AnsiColor.WHITE);
        raycastingCanvas.setFloorColor(AnsiColor.YELLOW);
        raycastingCanvas.setCeilingColor(AnsiColor.BLUE);
        raycastingCanvas.setDrawWallEdges(true);
        raycastingCanvas.setWallEdgeThreshold(0.3);
        raycastingCanvas.setRenderCeilings(false); // Disable ceiling to show background better

        // Set player position
        raycastingCanvas.setPlayerPosition(4.5, 4.5);
        raycastingCanvas.setPlayerAngle(0.0);
    }

    private void setupBackgrounds() {
        // Create solid color background
        solidBackground = new SolidColorBackgroundProvider(AnsiColor.BLUE);

        // Create animated clouds background
        cloudsBackground = new CloudsBackgroundProvider(0.03, 0.04, 0.4);
        cloudsBackground.setSkyColor(AnsiColor.CYAN);
        cloudsBackground.setCloudColorLight(AnsiColor.WHITE);
        cloudsBackground.setCloudColorDark(AnsiColor.BRIGHT_BLACK);

        // Create animated starfield background
        starfieldBackground = new StarfieldBackgroundProvider(80, 25);
        starfieldBackground.setRotationSpeed(0.3);
        starfieldBackground.setNumStars(150);
        starfieldBackground.setSkyColor(AnsiColor.BLACK);
        startfieldThrottle = AnimationThrottle.withDelaySeconds(starfieldBackground, 1);

        // Create animated constellation background
        constellationBackground = new ConstellationBackgroundProvider(80, 25);
        constellationBackground.setDriftSpeed(0.01);
        constellationBackground.setTwinkleIntensity(0.3);
        constellationBackground.setShowConstellationLines(true);
        constellationBackground.setSkyColor(AnsiColor.BLACK);

        // Create day-night cycle background
        dayNightBackground = new DayNightCycleBackgroundProvider(80, 25);
        dayNightBackground.setTimeSpeed(0.0001); // Slightly faster for demo
        dayNightBackground.setAutomaticWeather(true);
        dayNightBackground.setShowCelestialBodies(true);
        dayNightBackground.setCurrentTime(0.5); // Start at noon

        // Create alien planet background (newly added)
        alienPlanetBackground = new AlienPlanetBackgroundProvider(80, 25);
        alienPlanetBackground.setAnimationSpeed(0.02);
        alienPlanetBackground.setParticleDensity(0.8);
        alienPlanetBackground.setAuroraBrightness(0.7);
        alienPlanetBackground.setShowMoons(true);
        alienPlanetBackground.setShowAurora(true);
        alienPlanetBackground.setShowParticles(true);
        alienPlanetBackground.setShowPulsar(true);
    }

    private void setupMap() {
        // Create a simple map with open areas to show the background
        String[] map = {
            "################",
            "#              #",
            "#  ####    ##  #",
            "#    ##        #",
            "#              #",
            "#     ##       #",
            "#              #",
            "#  ##    ####  #",
            "#              #",
            "#      ##      #",
            "#              #",
            "################"
        };
        raycastingCanvas.setMap(map);
    }

    private void setupScreen() {
        screen.setContent(raycastingCanvas);

        // Register keyboard shortcuts
        screen.registerShortcut(KeyEvent.SpecialKey.ESC.name(), () -> {
            try {
                processLoop.stop();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        screen.registerShortcut("1", () -> {
            setBackground(BackgroundType.SOLID);
        });

        screen.registerShortcut("2", () -> {
            setBackground(BackgroundType.CLOUDS);
        });

        screen.registerShortcut("3", () -> {
            setBackground(BackgroundType.STARFIELD);
        });

        screen.registerShortcut("4", () -> {
            setBackground(BackgroundType.CONSTELLATION);
        });

        screen.registerShortcut("5", () -> {
            setBackground(BackgroundType.DAY_NIGHT_CYCLE);
        });

        screen.registerShortcut("6", () -> {
            setBackground(BackgroundType.ALIEN_PLANET);
        });

        // Day-Night cycle specific controls
        screen.registerShortcut("T", () -> {
            toggleTimeSpeed();
        });

        screen.registerShortcut("W", () -> {
            cycleWeather();
        });

        screen.registerShortcut("B", () -> {
            adjustSunnyBias();
        });

        screen.registerShortcut("N", () -> {
            setTimeToNight();
        });

        screen.registerShortcut("M", () -> {
            setTimeToMidday();
        });

        screen.registerShortcut(KeyEvent.SpecialKey.ARROW_UP.name(), () -> {
            raycastingCanvas.movePlayer(0.1);
        });

        screen.registerShortcut( KeyEvent.SpecialKey.ARROW_DOWN.name(), () -> {
            raycastingCanvas.movePlayer(-0.1);
        });

        screen.registerShortcut("A", () -> {
            raycastingCanvas.strafePlayer(-0.1);
        });

        screen.registerShortcut("D", () -> {
            raycastingCanvas.strafePlayer(0.1);
        });

        screen.registerShortcut(KeyEvent.SpecialKey.ARROW_LEFT.name(), () -> {
            raycastingCanvas.rotatePlayer(-0.1);
        });

        screen.registerShortcut(KeyEvent.SpecialKey.ARROW_RIGHT.name(), () -> {
            raycastingCanvas.rotatePlayer(0.1);
        });

        screen.registerShortcut("C", () -> {
            toggleCeilings();
        });

        screen.registerShortcut("H", () -> {
            showHelp();
        });

        screen.registerShortcut("B", () -> {
            adjustSunnyBias();
        });
    }

    private void setBackground(BackgroundType backgroundType) {
        // Remove current background from animation manager
        if (currentBackground == BackgroundType.CLOUDS) {
            processLoop.removeAnimationTicker(cloudsBackground);
        } else if (currentBackground == BackgroundType.STARFIELD) {
            processLoop.removeAnimationTicker(startfieldThrottle);
        } else if (currentBackground == BackgroundType.CONSTELLATION) {
            processLoop.removeAnimationTicker(constellationBackground);
        } else if (currentBackground == BackgroundType.DAY_NIGHT_CYCLE) {
            processLoop.removeAnimationTicker(dayNightBackground);
        } else if (currentBackground == BackgroundType.ALIEN_PLANET) {
            processLoop.removeAnimationTicker(alienPlanetBackground);
        }

        // Set new background
        currentBackground = backgroundType;
        BackgroundProvider provider;

        switch (backgroundType) {
            case SOLID:
                provider = solidBackground;
                break;
            case CLOUDS:
                provider = cloudsBackground;
                processLoop.addAnimationTicker(cloudsBackground);
                break;
            case STARFIELD:
                provider = starfieldBackground;
                processLoop.addAnimationTicker(startfieldThrottle);
                break;
            case CONSTELLATION:
                provider = constellationBackground;
                processLoop.addAnimationTicker(constellationBackground);
                break;
            case DAY_NIGHT_CYCLE:
                provider = dayNightBackground;
                processLoop.addAnimationTicker(dayNightBackground);
                break;
            case ALIEN_PLANET:
                provider = alienPlanetBackground;
                processLoop.addAnimationTicker(alienPlanetBackground);
                break;
            default:
                provider = solidBackground;
        }

        raycastingCanvas.setBackgroundProvider(provider);
        System.out.println("Switched to: " + backgroundType.getDescription());
    }

    private void toggleCeilings() {
        boolean currentState = raycastingCanvas.isRenderCeilings();
        raycastingCanvas.setRenderCeilings(!currentState);
        System.out.println("Ceiling rendering: " + (currentState ? "OFF" : "ON"));
    }

    private void showHelp() {
        System.out.println("\n=== Background Demo Controls ===");
        System.out.println("Movement:");
        System.out.println("  ↑/↓      - Move forward/backward");
        System.out.println("  A/D      - Strafe left/right");
        System.out.println("  ←/→      - Turn left/right");
        System.out.println();
        System.out.println("Backgrounds:");
        System.out.println("  1        - Solid color background");
        System.out.println("  2        - Animated clouds background");
        System.out.println("  3        - Animated starfield background");
        System.out.println("  4        - Constellation background");
        System.out.println("  5        - Day-Night cycle background");
        System.out.println("  6        - Alien planet background");
        System.out.println();
        System.out.println("Day-Night Cycle Controls (when active):");
        System.out.println("  T        - Toggle time speed (slow/fast)");
        System.out.println("  W        - Cycle weather (sunny→cloudy→rainy→stormy)");
        System.out.println("  B        - Adjust sunny weather bias (low→med→high→very high)");
        System.out.println("  N        - Set time to night (midnight)");
        System.out.println("  M        - Set time to midday (noon)");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  C        - Toggle ceiling rendering");
        System.out.println("  H        - Show this help");
        System.out.println("  ESC      - Exit demo");
        System.out.println();
        System.out.println("Current background: " + currentBackground.getDescription());
        System.out.println("Ceiling rendering: " + (raycastingCanvas.isRenderCeilings() ? "ON" : "OFF"));

        // Show additional info for Day-Night cycle
        if (currentBackground == BackgroundType.DAY_NIGHT_CYCLE) {
            System.out.println();
            System.out.println("Day-Night Cycle Status:");
            System.out.println("  Time: " + dayNightBackground.getTimeString());
            System.out.println("  Weather: " + dayNightBackground.getCurrentWeatherString());
            System.out.println("  Auto Weather: " + (dayNightBackground.isAutomaticWeather() ? "ON" : "OFF"));
        }

        System.out.println("===============================\n");
    }

    private void toggleTimeSpeed() {
        if (currentBackground == BackgroundType.DAY_NIGHT_CYCLE) {
            double currentSpeed = dayNightBackground.getTimeSpeed();
            double newSpeed = currentSpeed <= 0.0001 ? 0.001 : 0.0001; // Toggle between slow and fast
            dayNightBackground.setTimeSpeed(newSpeed);
            System.out.println("Time speed: " + (newSpeed > 0.0001 ? "FAST" : "SLOW"));
        }
    }

    private void cycleWeather() {
        if (currentBackground == BackgroundType.DAY_NIGHT_CYCLE) {
            DayNightCycleBackgroundProvider.WeatherType[] weathers =
                DayNightCycleBackgroundProvider.WeatherType.values();
            DayNightCycleBackgroundProvider.WeatherType current = dayNightBackground.getCurrentWeather();

            // Find next weather type
            int currentIndex = 0;
            for (int i = 0; i < weathers.length; i++) {
                if (weathers[i] == current) {
                    currentIndex = i;
                    break;
                }
            }

            DayNightCycleBackgroundProvider.WeatherType nextWeather =
                weathers[(currentIndex + 1) % weathers.length];
            dayNightBackground.setWeather(nextWeather);
            System.out.println("Weather: " + nextWeather.getName());
        }
    }

    private void setTimeToNight() {
        if (currentBackground == BackgroundType.DAY_NIGHT_CYCLE) {
            dayNightBackground.setTimeOfDay(0.0); // Midnight
            System.out.println("Time set to: " + dayNightBackground.getTimeString() + " (Night)");
        }
    }

    private void setTimeToMidday() {
        if (currentBackground == BackgroundType.DAY_NIGHT_CYCLE) {
            dayNightBackground.setTimeOfDay(0.5); // Noon
            System.out.println("Time set to: " + dayNightBackground.getTimeString() + " (Midday)");
        }
    }

    private void adjustSunnyBias() {
        if (currentBackground == BackgroundType.DAY_NIGHT_CYCLE) {
            double currentBias = dayNightBackground.getSunnyWeatherBias();
            // Cycle through different bias levels: 0.3 -> 0.5 -> 0.7 -> 0.9 -> 0.3
            double newBias;
            if (currentBias < 0.4) {
                newBias = 0.5; // Low -> Medium
            } else if (currentBias < 0.6) {
                newBias = 0.7; // Medium -> High
            } else if (currentBias < 0.8) {
                newBias = 0.9; // High -> Very High
            } else {
                newBias = 0.3; // Very High -> Low
            }

            dayNightBackground.setSunnyWeatherBias(newBias);
            String biasLevel = getBiasLevelName(newBias);
            System.out.println("Sunny weather bias: " + biasLevel + " (" + Math.round(newBias * 100) + "%)");
        }
    }

    private String getBiasLevelName(double bias) {
        if (bias < 0.4) return "LOW";
        if (bias < 0.6) return "MEDIUM";
        if (bias < 0.8) return "HIGH";
        return "VERY HIGH";
    }
}
