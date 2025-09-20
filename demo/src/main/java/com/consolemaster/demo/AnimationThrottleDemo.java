package com.consolemaster.demo;

import com.consolemaster.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Extended demo application that demonstrates advanced AnimationThrottle functionality.
 * Shows how to dynamically control animation speed, enable/disable animations, and
 * use different throttle configurations.
 */
@Slf4j
public class AnimationThrottleDemo {

    private static AnimationThrottle slowText, fastText, controlledSpinner;
    private static ProcessLoop processLoop;

    public static void main(String[] args) throws IOException {
        // Create screen canvas
        ScreenCanvas screen = new ScreenCanvas("Animation Throttle Demo", 80, 30);

        // Create animated components
        SimpleCounter counter1 = new SimpleCounter("Counter1", 5, 5, "Slow: ");
        SimpleCounter counter2 = new SimpleCounter("Counter2", 5, 7, "Fast: ");
        SimpleCounter counter3 = new SimpleCounter("Counter3", 5, 9, "Controlled: ");

        // Create throttles with different configurations
        slowText = AnimationThrottle.withDelaySeconds(counter1, 2.0); // Very slow
        fastText = new AnimationThrottle(counter2, 100); // Fast
        controlledSpinner = AnimationThrottle.withDelaySeconds(counter3, 1.0); // Medium speed

        // Create layout
        Composite composite = new Composite("MainComposite", 0, 0, new BoxLayout(BoxLayout.Direction.VERTICAL));

        // Add components
        composite.addChild(counter1);
        composite.addChild(counter2);
        composite.addChild(counter3);

        // Create instruction texts
        Text instructions1 = new Text("Instructions1", 5, 12, "Controls:");
        Text instructions2 = new Text("Instructions2", 5, 13, "1/2 - Slow down/Speed up 'Slow' counter");
        Text instructions3 = new Text("Instructions3", 5, 14, "3/4 - Slow down/Speed up 'Fast' counter");
        Text instructions4 = new Text("Instructions4", 5, 15, "5 - Toggle 'Controlled' counter on/off");
        Text instructions5 = new Text("Instructions5", 5, 16, "6 - Reset all timing");
        Text instructions6 = new Text("Instructions6", 5, 17, "I - Show throttle information");
        Text instructions7 = new Text("Instructions7", 5, 18, "Q - Quit");

        composite.addChild(instructions1);
        composite.addChild(instructions2);
        composite.addChild(instructions3);
        composite.addChild(instructions4);
        composite.addChild(instructions5);
        composite.addChild(instructions6);
        composite.addChild(instructions7);

        // Information display area
        Text infoArea = new Text("InfoArea", 5, 20, "Throttle Status: Ready");
        composite.addChild(infoArea);

        screen.setContent(composite);

        // Create process loop
        processLoop = new ProcessLoop(screen);

        // Add animation tickers
        processLoop.addAnimationTicker(slowText);
        processLoop.addAnimationTicker(fastText);
        processLoop.addAnimationTicker(controlledSpinner);

        // Set high tick rate for responsive controls
        processLoop.setAnimationTicksPerSecond(60);

        // Register keyboard shortcuts
        registerShortcuts(screen, infoArea);

        log.info("Starting Animation Throttle Demo");

        // Start the process loop
        processLoop.start();

        log.info("Animation Throttle Demo stopped");
    }

    private static void registerShortcuts(ScreenCanvas screen, Text infoArea) {
        // Quit
        screen.registerShortcut("Q", () -> {
            try {
                processLoop.stop();
            } catch (IOException e) {
                log.error("Error stopping process loop", e);
            }
        });

        // Control slow counter speed
        screen.registerShortcut("1", () -> {
            double currentDelay = slowText.getDelaySeconds();
            slowText.setDelaySeconds(Math.min(currentDelay + 0.5, 10.0));
            updateInfoArea(infoArea);
            log.info("Slow counter delay increased to {} seconds", slowText.getDelaySeconds());
        });

        screen.registerShortcut("2", () -> {
            double currentDelay = slowText.getDelaySeconds();
            slowText.setDelaySeconds(Math.max(currentDelay - 0.5, 0.1));
            updateInfoArea(infoArea);
            log.info("Slow counter delay decreased to {} seconds", slowText.getDelaySeconds());
        });

        // Control fast counter speed
        screen.registerShortcut("3", () -> {
            long currentDelay = fastText.getDelayMillis();
            fastText.setDelayMillis(Math.min(currentDelay + 50, 2000));
            updateInfoArea(infoArea);
            log.info("Fast counter delay increased to {} ms", fastText.getDelayMillis());
        });

        screen.registerShortcut("4", () -> {
            long currentDelay = fastText.getDelayMillis();
            fastText.setDelayMillis(Math.max(currentDelay - 50, 10));
            updateInfoArea(infoArea);
            log.info("Fast counter delay decreased to {} ms", fastText.getDelayMillis());
        });

        // Toggle controlled counter
        screen.registerShortcut("5", () -> {
            controlledSpinner.setEnabled(!controlledSpinner.isEnabled());
            updateInfoArea(infoArea);
            log.info("Controlled counter {}", controlledSpinner.isEnabled() ? "enabled" : "disabled");
        });

        // Reset all timing
        screen.registerShortcut("6", () -> {
            slowText.resetTiming();
            fastText.resetTiming();
            controlledSpinner.resetTiming();
            updateInfoArea(infoArea);
            log.info("All animation timing reset");
        });

        // Show throttle information
        screen.registerShortcut("I", () -> {
            log.info("=== Throttle Information ===");
            log.info("Slow Text: {}", slowText);
            log.info("Fast Text: {}", fastText);
            log.info("Controlled Spinner: {}", controlledSpinner);
            log.info("=========================");
            updateInfoArea(infoArea);
        });
    }

    private static void updateInfoArea(Text infoArea) {
        String status = String.format("Slow: %.1fs | Fast: %dms | Controlled: %s (%.1fs)",
                slowText.getDelaySeconds(),
                fastText.getDelayMillis(),
                controlledSpinner.isEnabled() ? "ON" : "OFF",
                controlledSpinner.getDelaySeconds());
        infoArea.setText("Status: " + status);
    }

    /**
     * Simple counter component for demonstration.
     */
    static class SimpleCounter extends Text implements AnimationTicker {
        private int count = 0;
        private final String prefix;

        public SimpleCounter(String name, int x, int y, String prefix) {
            super(name, x, y, prefix + "0");
            this.prefix = prefix;
        }

        @Override
        public boolean tick() {
            count++;
            setText(prefix + count);
            return true; // Always request redraw
        }

        public void reset() {
            count = 0;
            setText(prefix + "0");
        }
    }
}
