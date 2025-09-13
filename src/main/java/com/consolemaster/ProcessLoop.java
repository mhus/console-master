package com.consolemaster;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Main process loop class that handles continuous rendering and non-blocking input processing.
 * Provides a complete processing loop with configurable frame rate and automatic event handling.
 */
@Getter
@Setter
public class ProcessLoop {

    private final ScreenCanvas screenCanvas;
    private final InputHandler inputHandler;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean needsRedraw = new AtomicBoolean(true);

    // Performance settings
    private int targetFPS = 30;
    private long frameTimeNanos;
    private boolean limitFrameRate = true;

    // Statistics
    private long frameCount = 0;
    private long lastFPSTime = 0;
    private int currentFPS = 0;

    // Callbacks
    private Runnable updateCallback;
    private Runnable renderCallback;

    /**
     * Creates a new ProcessLoop with the specified ScreenCanvas.
     *
     * @param screenCanvas the screen canvas to render
     * @throws IOException if there's an error setting up the terminal
     */
    public ProcessLoop(ScreenCanvas screenCanvas) throws IOException {
        this.screenCanvas = screenCanvas;
        this.inputHandler = new InputHandler(screenCanvas.getTerminal());
        updateFrameTime();

        // Register built-in shortcuts
        setupBuiltInShortcuts();
    }

    /**
     * Sets the target frames per second.
     *
     * @param fps the target FPS (must be > 0)
     */
    public void setTargetFPS(int fps) {
        if (fps > 0) {
            this.targetFPS = fps;
            updateFrameTime();
        }
    }

    /**
     * Sets a callback that will be called before each frame render.
     * Use this for game logic updates.
     *
     * @param updateCallback the update callback
     */
    public void setUpdateCallback(Runnable updateCallback) {
        this.updateCallback = updateCallback;
    }

    /**
     * Sets a callback that will be called after each frame render.
     * Use this for custom rendering logic.
     *
     * @param renderCallback the render callback
     */
    public void setRenderCallback(Runnable renderCallback) {
        this.renderCallback = renderCallback;
    }

    /**
     * Starts the process loop in the current thread.
     * This method will block until stop() is called.
     */
    public void start() {
        if (running.compareAndSet(false, true)) {
            inputHandler.start();
            runProcessLoop();
        }
    }

    /**
     * Starts the process loop in a separate thread.
     *
     * @return the thread running the process loop
     */
    public Thread startAsync() {
        Thread processThread = new Thread(this::start, "ProcessLoop");
        processThread.setDaemon(false);
        processThread.start();
        return processThread;
    }

    /**
     * Stops the process loop and cleans up resources.
     */
    public void stop() {
        if (running.compareAndSet(true, false)) {
            inputHandler.stop();
            try {
                screenCanvas.close();
            } catch (IOException e) {
                System.err.println("Error closing screen canvas: " + e.getMessage());
            }
        }
    }

    /**
     * Requests a redraw on the next frame.
     * Use this when the display needs to be updated.
     */
    public void requestRedraw() {
        needsRedraw.set(true);
    }

    /**
     * Checks if the process loop is currently running.
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * Gets the current frames per second.
     */
    public int getCurrentFPS() {
        return currentFPS;
    }

    /**
     * Gets the total number of frames rendered.
     */
    public long getFrameCount() {
        return frameCount;
    }

    /**
     * Main process loop implementation.
     */
    private void runProcessLoop() {
        long lastFrameTime = System.nanoTime();
        lastFPSTime = System.currentTimeMillis();

        try {
            // Initial render
            screenCanvas.render();

            while (running.get()) {
                long currentTime = System.nanoTime();
                long deltaTime = currentTime - lastFrameTime;

                // Process input events
                processInputEvents();

                // Check for terminal size changes
                checkTerminalSizeChange();

                // Call update callback
                if (updateCallback != null) {
                    updateCallback.run();
                }

                // Render frame if needed or if frame rate limiting is disabled
                boolean shouldRender = needsRedraw.get() || !limitFrameRate;
                if (shouldRender && (!limitFrameRate || deltaTime >= frameTimeNanos)) {
                    render();
                    lastFrameTime = currentTime;
                    frameCount++;
                    needsRedraw.set(false);

                    // Update FPS counter
                    updateFPSCounter();
                }

                // Sleep to maintain target frame rate
                if (limitFrameRate && deltaTime < frameTimeNanos) {
                    long sleepTime = (frameTimeNanos - deltaTime) / 1_000_000; // Convert to milliseconds
                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                } else if (limitFrameRate) {
                    // Yield to other threads if we're running at target speed
                    Thread.yield();
                }
            }
        } catch (Exception e) {
            System.err.println("Error in process loop: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Processes all pending input events.
     */
    private void processInputEvents() {
        while (inputHandler.hasEvents()) {
            KeyEvent keyEvent = inputHandler.pollEvent();
            if (keyEvent != null) {
                screenCanvas.processKeyEvent(keyEvent);

                // Request redraw if event might have changed display
                if (!keyEvent.isConsumed()) {
                    requestRedraw();
                }
            }
        }
    }

    /**
     * Checks for terminal size changes and updates the screen accordingly.
     */
    private void checkTerminalSizeChange() {
        int currentWidth = screenCanvas.getTerminal().getWidth();
        int currentHeight = screenCanvas.getTerminal().getHeight();

        if (currentWidth != screenCanvas.getWidth() || currentHeight != screenCanvas.getHeight()) {
            screenCanvas.updateSize();
            requestRedraw();
        }
    }

    /**
     * Renders the current frame.
     */
    private void render() {
        screenCanvas.render();

        // Call custom render callback
        if (renderCallback != null) {
            renderCallback.run();
        }
    }

    /**
     * Updates the frame time based on target FPS.
     */
    private void updateFrameTime() {
        frameTimeNanos = 1_000_000_000L / targetFPS;
    }

    /**
     * Updates the FPS counter.
     */
    private void updateFPSCounter() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFPSTime >= 1000) {
            currentFPS = (int) ((frameCount * 1000) / (currentTime - lastFPSTime));
            lastFPSTime = currentTime;
            frameCount = 0;
        }
    }

    /**
     * Sets up built-in keyboard shortcuts.
     */
    private void setupBuiltInShortcuts() {
        // Register ESC key to stop the process loop
        screenCanvas.registerShortcut("ESCAPE", this::stop);

        // Register Ctrl+C to stop the process loop
        screenCanvas.registerShortcut("Ctrl+C", this::stop);

        // Register F5 to force redraw
        screenCanvas.registerShortcut("F5", this::requestRedraw);
    }

    /**
     * Convenience method to create and start a process loop.
     *
     * @param screenCanvas the screen canvas to render
     * @param updateCallback optional update callback for application logic
     * @return the created ProcessLoop instance
     * @throws IOException if there's an error setting up the terminal
     */
    public static ProcessLoop createAndStart(ScreenCanvas screenCanvas, Runnable updateCallback) throws IOException {
        ProcessLoop processLoop = new ProcessLoop(screenCanvas);
        if (updateCallback != null) {
            processLoop.setUpdateCallback(updateCallback);
        }
        processLoop.start();
        return processLoop;
    }

    /**
     * Convenience method to create and start a process loop asynchronously.
     *
     * @param screenCanvas the screen canvas to render
     * @param updateCallback optional update callback for application logic
     * @return the created ProcessLoop instance
     * @throws IOException if there's an error setting up the terminal
     */
    public static ProcessLoop createAndStartAsync(ScreenCanvas screenCanvas, Runnable updateCallback) throws IOException {
        ProcessLoop processLoop = new ProcessLoop(screenCanvas);
        if (updateCallback != null) {
            processLoop.setUpdateCallback(updateCallback);
        }
        processLoop.startAsync();
        return processLoop;
    }
}
