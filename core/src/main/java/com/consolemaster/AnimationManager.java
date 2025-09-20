package com.consolemaster;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages animation tickers and executes them in a separate thread.
 * Provides thread-safe registration and unregistration of animation components.
 */
@Slf4j
@Getter
@Setter
public class AnimationManager {

    private final CopyOnWriteArrayList<AnimationTicker> tickers = new CopyOnWriteArrayList<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread animationThread;

    // Animation settings
    private int ticksPerSecond = 60;
    private long tickIntervalNanos;

    // Callback for when screen update is needed
    private Runnable redrawCallback;

    /**
     * Creates a new AnimationManager with default settings.
     */
    public AnimationManager() {
        updateTickInterval();
    }

    /**
     * Starts the animation manager thread.
     */
    public synchronized void start() {
        if (running.getAndSet(true)) {
            return; // Already running
        }

        animationThread = new Thread(this::run, "AnimationManager");
        animationThread.setDaemon(true);
        animationThread.start();

        log.debug("AnimationManager started with {} ticks per second", ticksPerSecond);
    }

    /**
     * Stops the animation manager thread.
     */
    public synchronized void stop() {
        if (!running.getAndSet(false)) {
            return; // Already stopped
        }

        if (animationThread != null) {
            animationThread.interrupt();
            try {
                animationThread.join(1000); // Wait up to 1 second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            animationThread = null;
        }

        log.debug("AnimationManager stopped");
    }

    /**
     * Main animation loop that runs in separate thread.
     */
    private void run() {
        long lastTickTime = System.nanoTime();

        while (running.get() && !Thread.currentThread().isInterrupted()) {
            long currentTime = System.nanoTime();

            try {
                // Process all animation tickers
                boolean needsRedraw = false;
                for (AnimationTicker ticker : tickers) {
                    try {
                        if (ticker.tick()) {
                            needsRedraw = true;
                        }
                    } catch (Exception e) {
                        log.error("Error in animation ticker: {}", e.getMessage(), e);
                    }
                }

                // Request redraw if any animation needs it
                if (needsRedraw && redrawCallback != null) {
                    redrawCallback.run();
                }

                // Sleep for the remaining time to maintain tick rate
                long tickTime = System.nanoTime() - currentTime;
                long sleepTime = tickIntervalNanos - tickTime;
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime / 1_000_000, (int) (sleepTime % 1_000_000));
                }

                lastTickTime = currentTime;

            } catch (InterruptedException e) {
                // Thread was interrupted, exit gracefully
                break;
            } catch (Exception e) {
                log.error("Error in animation manager loop: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * Adds an animation ticker to be managed.
     *
     * @param ticker the animation ticker to add
     */
    public void addTicker(AnimationTicker ticker) {
        if (ticker != null && !tickers.contains(ticker)) {
            tickers.add(ticker);
            log.debug("Added animation ticker: {}", ticker.getClass().getSimpleName());
        }
    }

    /**
     * Removes an animation ticker from management.
     *
     * @param ticker the animation ticker to remove
     */
    public void removeTicker(AnimationTicker ticker) {
        if (ticker != null && tickers.remove(ticker)) {
            log.debug("Removed animation ticker: {}", ticker.getClass().getSimpleName());
        }
    }

    /**
     * Removes all animation tickers.
     */
    public void clearTickers() {
        int count = tickers.size();
        tickers.clear();
        log.debug("Cleared {} animation tickers", count);
    }

    /**
     * Returns the number of registered animation tickers.
     */
    public int getTickerCount() {
        return tickers.size();
    }

    /**
     * Sets the animation tick rate (ticks per second).
     *
     * @param ticksPerSecond the desired tick rate
     */
    public void setTicksPerSecond(int ticksPerSecond) {
        this.ticksPerSecond = Math.max(1, ticksPerSecond);
        updateTickInterval();
        log.debug("Animation tick rate set to {} ticks per second", this.ticksPerSecond);
    }

    /**
     * Updates the tick interval based on ticks per second.
     */
    private void updateTickInterval() {
        this.tickIntervalNanos = 1_000_000_000L / ticksPerSecond;
    }

    /**
     * Checks if the animation manager is running.
     */
    public boolean isRunning() {
        return running.get();
    }
}
