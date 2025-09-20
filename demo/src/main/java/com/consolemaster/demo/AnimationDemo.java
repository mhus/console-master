package com.consolemaster.demo;

import com.consolemaster.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Demo application that demonstrates the AnimationTicker and AnimationManager functionality.
 * Shows various animated components that update at different rates and request screen redraws.
 */
@Slf4j
public class AnimationDemo {

    public static void main(String[] args) throws IOException {
        // Create screen canvas
        ScreenCanvas screen = new ScreenCanvas("Animation Demo", 80, 25);

        // Create animated components
        BlinkingText blinkingText = new BlinkingText("Blinking Text", 5, 5, "*** BLINKING ***");
        RotatingSpinner spinner = new RotatingSpinner("Spinner", 5, 8, "|/-\\");
        ProgressBar progressBar = new ProgressBar("Progress", 5, 11, 30);
        BouncingBall ball = new BouncingBall("Ball", 0, 0, screen.getWidth() - 1, screen.getHeight() - 3);

        // Create proxies to control animation speed
        AnimationThrottle slowBlinkingText = AnimationThrottle.withDelaySeconds(blinkingText, 2.0); // Very slow blinking
        AnimationThrottle normalSpinner = new AnimationThrottle(spinner, 200); // Slower spinner
        AnimationThrottle fastProgressBar = new AnimationThrottle(progressBar, 25); // Faster progress
        AnimationThrottle slowBall = AnimationThrottle.withDelaySeconds(ball, 0.5); // Slower ball

        Composite composite = new Composite("Composite", 20, 10, new BoxLayout(BoxLayout.Direction.VERTICAL));

        // Add components to screen
        composite.addChild(blinkingText);
        composite.addChild(spinner);
        composite.addChild(progressBar);
        composite.addChild(ball);

        // Create instructions
        Text instructions = new Text("Instructions", 5, screen.getHeight() - 2,
                                   "Press 'q' to quit, 'p' to pause/resume animations");
        composite.addChild(instructions);
        screen.setContent(composite);

        // Create process loop
        ProcessLoop processLoop = new ProcessLoop(screen);

        // Add animation tickers (using proxies for controlled speed)
        processLoop.addAnimationTicker(slowBlinkingText);
        processLoop.addAnimationTicker(normalSpinner);
        processLoop.addAnimationTicker(fastProgressBar);
        processLoop.addAnimationTicker(slowBall);

        // Set animation tick rate
        processLoop.setAnimationTicksPerSecond(30);

        // Register keyboard shortcuts for quitting and pausing
        screen.registerShortcut(KeyEvent.SpecialKey.END.name(), () -> {
            try {
                processLoop.stop();
            } catch (IOException e) {
                log.error("Error stopping process loop", e);
            }
        });

        screen.registerShortcut("Q", () -> {
            try {
                processLoop.stop();
            } catch (IOException e) {
                log.error("Error stopping process loop", e);
            }
        });

        screen.registerShortcut("P", () -> {
            // Toggle animation state
            if (processLoop.getAnimationTickerCount() > 0) {
                processLoop.clearAnimationTickers();
                log.info("Animations paused");
            } else {
                processLoop.addAnimationTicker(slowBlinkingText);
                processLoop.addAnimationTicker(normalSpinner);
                processLoop.addAnimationTicker(fastProgressBar);
                processLoop.addAnimationTicker(slowBall);
                log.info("Animations resumed");
            }
        });

        screen.registerShortcut("Z", () -> {
            // Toggle animation state
            if (processLoop.getAnimationTickerCount() > 0) {
                processLoop.clearAnimationTickers();
                log.info("Animations paused");
            } else {
                processLoop.addAnimationTicker(slowBlinkingText);
                processLoop.addAnimationTicker(normalSpinner);
                processLoop.addAnimationTicker(fastProgressBar);
                processLoop.addAnimationTicker(slowBall);
                log.info("Animations resumed");
            }
        });

        log.info("Starting Animation Demo - Press 'q' to quit, 'z' to pause/resume");

        // Start the process loop
        processLoop.start();

        log.info("Animation Demo stopped");
    }

    /**
     * Blinking text component that shows/hides text periodically.
     */
    static class BlinkingText extends Text implements AnimationTicker {
        private long lastBlinkTime = 0;
        private final long blinkInterval = 500; // 500ms
        private boolean visible = true;
        private final String originalText;

        public BlinkingText(String name, int x, int y, String text) {
            super(name, x, y, text);
            this.originalText = text;
        }

        @Override
        public boolean tick() {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastBlinkTime >= blinkInterval) {
                visible = !visible;
                setText(visible ? originalText : "");
                lastBlinkTime = currentTime;
                return true; // Request redraw
            }
            return false;
        }
    }

    /**
     * Rotating spinner component that cycles through different characters.
     */
    static class RotatingSpinner extends Text implements AnimationTicker {
        private long lastRotateTime = 0;
        private final long rotateInterval = 100; // 100ms
        private final String spinChars;
        private int currentIndex = 0;

        public RotatingSpinner(String name, int x, int y, String spinChars) {
            super(name, x, y, String.valueOf(spinChars.charAt(0)));
            this.spinChars = spinChars;
        }

        @Override
        public boolean tick() {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastRotateTime >= rotateInterval) {
                currentIndex = (currentIndex + 1) % spinChars.length();
                setText("Loading " + spinChars.charAt(currentIndex));
                lastRotateTime = currentTime;
                return true; // Request redraw
            }
            return false;
        }
    }

    /**
     * Progress bar component that fills up over time.
     */
    static class ProgressBar extends Text implements AnimationTicker {
        private long lastUpdateTime = 0;
        private final long updateInterval = 50; // 50ms
        private final int maxWidth;
        private int currentProgress = 0;
        private boolean increasing = true;

        public ProgressBar(String name, int x, int y, int width) {
            super(name, x, y, "");
            this.maxWidth = width;
            updateDisplay();
        }

        @Override
        public boolean tick() {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime >= updateInterval) {
                if (increasing) {
                    currentProgress++;
                    if (currentProgress >= maxWidth) {
                        increasing = false;
                    }
                } else {
                    currentProgress--;
                    if (currentProgress <= 0) {
                        increasing = true;
                    }
                }
                updateDisplay();
                lastUpdateTime = currentTime;
                return true; // Request redraw
            }
            return false;
        }

        private void updateDisplay() {
            StringBuilder bar = new StringBuilder("[");
            for (int i = 0; i < maxWidth; i++) {
                bar.append(i < currentProgress ? "=" : " ");
            }
            bar.append("] ").append(currentProgress * 100 / maxWidth).append("%");
            setText(bar.toString());
        }
    }

    /**
     * Bouncing ball component that moves around the screen.
     */
    static class BouncingBall extends Text implements AnimationTicker {
        private long lastMoveTime = 0;
        private final long moveInterval = 100; // 100ms
        private final int maxX, maxY;
        private int dx = 1, dy = 1;

        public BouncingBall(String name, int startX, int startY, int maxX, int maxY) {
            super(name, startX, startY, "O");
            this.maxX = maxX;
            this.maxY = maxY;
        }

        @Override
        public boolean tick() {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastMoveTime >= moveInterval) {
                // Update position
                int newX = getX() + dx;
                int newY = getY() + dy;

                // Bounce off walls
                if (newX <= 0 || newX >= maxX) {
                    dx = -dx;
                    newX = Math.max(0, Math.min(maxX, newX));
                }
                if (newY <= 0 || newY >= maxY) {
                    dy = -dy;
                    newY = Math.max(0, Math.min(maxY, newY));
                }

                setPosition(newX, newY);
                lastMoveTime = currentTime;
                return true; // Request redraw
            }
            return false;
        }
    }
}
