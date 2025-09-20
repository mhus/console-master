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
    private AnimationThrottle startfieldThrottle;

    // Current background type
    private BackgroundType currentBackground = BackgroundType.SOLID;

    private enum BackgroundType {
        SOLID("Solid Color Background"),
        CLOUDS("Animated Clouds Background"),
        STARFIELD("Animated Starfield Background");

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
    }

    private void setBackground(BackgroundType backgroundType) {
        // Remove current background from animation manager
        if (currentBackground == BackgroundType.CLOUDS) {
            processLoop.removeAnimationTicker(cloudsBackground);
        } else if (currentBackground == BackgroundType.STARFIELD) {
            processLoop.removeAnimationTicker(startfieldThrottle);
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
        System.out.println("  W/S      - Move forward/backward");
        System.out.println("  A/D      - Strafe left/right");
        System.out.println("  ←/→      - Turn left/right");
        System.out.println();
        System.out.println("Backgrounds:");
        System.out.println("  1        - Solid color background");
        System.out.println("  2        - Animated clouds background");
        System.out.println("  3        - Animated starfield background");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  C        - Toggle ceiling rendering");
        System.out.println("  H        - Show this help");
        System.out.println("  ESC      - Exit demo");
        System.out.println();
        System.out.println("Current background: " + currentBackground.getDescription());
        System.out.println("Ceiling rendering: " + (raycastingCanvas.isRenderCeilings() ? "ON" : "OFF"));
        System.out.println("===============================\n");
    }

}
