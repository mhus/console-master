package com.consolemaster;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Main process loop class that handles continuous rendering and non-blocking input processing.
 * Provides a complete processing loop with configurable frame rate and automatic event handling.
 * Includes output capture functionality to redirect stdout and stderr.
 * Now uses native terminal implementation instead of JLine.
 */
@Slf4j
@Getter
@Setter
public class ProcessLoop {

    private final ScreenCanvas screenCanvas;
    private final NativeInputHandler inputHandler;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean needsRedraw = new AtomicBoolean(true);

    // Output capture
    private final OutputCapture outputCapture = new OutputCapture();
    private boolean captureOutput = false;

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
        this.inputHandler = new NativeInputHandler(screenCanvas.getTerminal());
        updateFrameTime();

        // Set up default event handling
        inputHandler.setEventHandler(this::handleEvent);

        // Register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                stop();
            } catch (IOException e) {
                log.error("Error during shutdown: {}", e.getMessage(), e);
            }
        }));
    }

    /**
     * Starts the process loop.
     */
    public void start() throws IOException {
        if (running.getAndSet(true)) {
            return; // Already running
        }

        // Setup terminal for raw mode
        screenCanvas.getTerminal().start();

        // Enable mouse reporting if needed
        if (screenCanvas.isMouseReportingEnabled()) {
            screenCanvas.getTerminal().enableMouseTracking();
        }

        // Start output capture if enabled
        if (captureOutput) {
            outputCapture.startCapture();
        }

        // Start input handler
        inputHandler.start();

        // Initial render
        requestRedraw();

        // Start main loop
        run();
    }

    /**
     * Stops the process loop.
     */
    public void stop() throws IOException {
        if (!running.getAndSet(false)) {
            return; // Already stopped
        }

        // Stop input handler
        inputHandler.stop();

        // Stop output capture
        if (captureOutput) {
            outputCapture.stopCapture();
        }

        // Disable mouse reporting
        if (screenCanvas.isMouseReportingEnabled()) {
            screenCanvas.getTerminal().disableMouseTracking();
        }

        // Restore terminal
        screenCanvas.getTerminal().stop();
    }

    /**
     * Main processing loop.
     */
    private void run() {
        long lastFrameTime = System.nanoTime();
        long fpsUpdateTime = System.currentTimeMillis();

        while (running.get()) {
            long currentTime = System.nanoTime();
            long deltaTime = currentTime - lastFrameTime;

            try {
                // Process input events
                processEvents();

                // Update callback
                if (updateCallback != null) {
                    updateCallback.run();
                }

                // Check for terminal resize
                screenCanvas.updateSize();

                // Render if needed
                if (needsRedraw.get()) {
                    render();
                    needsRedraw.set(false);
                    frameCount++;
                }

                // Calculate FPS
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - fpsUpdateTime >= 1000) {
                    currentFPS = (int) ((frameCount * 1000) / (currentTimeMillis - fpsUpdateTime));
                    frameCount = 0;
                    fpsUpdateTime = currentTimeMillis;
                }

                // Frame rate limiting
                if (limitFrameRate) {
                    long frameTime = System.nanoTime() - currentTime;
                    long sleepTime = frameTimeNanos - frameTime;
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime / 1_000_000, (int) (sleepTime % 1_000_000));
                    }
                }

                lastFrameTime = currentTime;

            } catch (InterruptedException e) {
                // Thread was interrupted, exit gracefully
                break;
            } catch (Exception e) {
                log.error("Error in process loop: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * Processes input events from the input handler.
     */
    private void processEvents() {
        while (inputHandler.hasEvents()) {
            Event event = inputHandler.pollEvent();
            if (event != null) {
                handleEvent(event);
            }
        }
    }

    /**
     * Handles individual events.
     */
    private void handleEvent(Event event) {
        if (event instanceof KeyEvent keyEvent) {
            screenCanvas.processKeyEvent(keyEvent);
        } else if (event instanceof MouseEvent mouseEvent) {
            screenCanvas.processMouseEvent(mouseEvent);
        }

        // Request redraw for any input
        requestRedraw();
    }

    /**
     * Renders the screen.
     */
    private void render() {
        try {
            // Render callback
            if (renderCallback != null) {
                renderCallback.run();
            }

            // Render the screen canvas
            screenCanvas.render();

        } catch (Exception e) {
            log.error("Error during rendering: {}", e.getMessage(), e);
        }
    }

    /**
     * Requests a redraw on the next frame.
     */
    public void requestRedraw() {
        needsRedraw.set(true);
    }

    /**
     * Sets the target frames per second.
     */
    public void setTargetFPS(int fps) {
        this.targetFPS = Math.max(1, fps);
        updateFrameTime();
    }

    /**
     * Updates the frame time based on target FPS.
     */
    private void updateFrameTime() {
        this.frameTimeNanos = 1_000_000_000L / targetFPS;
    }

    /**
     * Enables or disables output capture.
     *
     * @param capture true to enable output capture, false to disable
     */
    public void setCaptureOutput(boolean capture) {
        this.captureOutput = capture;
        if (capture) {
            outputCapture.startCapture();
        } else {
            outputCapture.stopCapture();
        }
    }

    /**
     * Checks if output capture is enabled.
     */
    public boolean isOutputCaptureEnabled() {
        return captureOutput;
    }

    /**
     * Gets the output capture instance.
     */
    public OutputCapture getOutputCapture() {
        return outputCapture;
    }

    /**
     * Clears all captured output.
     */
    public void clearCapturedOutput() {
        outputCapture.clear();
    }

    /**
     * Creates a ConsoleOutput canvas that displays the captured output.
     *
     * @param x the x position
     * @param y the y position
     * @param width the width
     * @param height the height
     * @return a new ConsoleOutput canvas
     */
    public ConsoleOutput createConsoleOutputCanvas(int x, int y, int width, int height) {
        return new ConsoleOutput("ConsoleOutput", x, y, width, height, outputCapture);
    }

    /**
     * Enables mouse reporting for this process loop.
     */
    public void enableMouseReporting() {
        screenCanvas.enableMouseReporting();
        inputHandler.enableMouseReporting();
    }

    /**
     * Disables mouse reporting for this process loop.
     */
    public void disableMouseReporting() {
        inputHandler.disableMouseReporting();
        screenCanvas.disableMouseReporting();
    }

    /**
     * Checks if mouse reporting is enabled.
     */
    public boolean isMouseReportingEnabled() {
        return screenCanvas.isMouseReportingEnabled();
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

    /**
     * Starts the process loop asynchronously in a separate thread.
     */
    public void startAsync() throws IOException {
        if (running.getAndSet(true)) {
            return; // Already running
        }

        // Setup terminal for raw mode
        screenCanvas.getTerminal().start();

        // Enable mouse reporting if needed
        if (screenCanvas.isMouseReportingEnabled()) {
            screenCanvas.getTerminal().enableMouseTracking();
        }

        // Start output capture if enabled
        if (captureOutput) {
            outputCapture.startCapture();
        }

        // Start input handler
        inputHandler.start();

        // Initial render
        requestRedraw();

        // Start main loop in separate thread
        Thread loopThread = new Thread(this::run, "ProcessLoop");
        loopThread.setDaemon(true);
        loopThread.start();
    }
}
