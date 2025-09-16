package com.consolemaster;

import lombok.Getter;

import java.io.*;

/**
 * Native terminal implementation that replaces JLine dependency.
 * Handles ANSI escape sequences and terminal control directly.
 */
@Getter
public abstract class Terminal {

    protected final PrintStream writer;
    protected final InputStream inputStream;
    protected int width;
    protected int height;

    // Terminal size detection caching
    private long lastSizeDetectionTime = 0;
    private long sizeDetectionInterval = 60 * 1000; // 60 seconds

    // Screen buffer for diff-based rendering
    private StyledChar[][] screenBuffer;
    private int renderCount = 0;
    private int fullRenderInterval = 50; // Full render every 50 times
    private boolean bufferInitialized = false;

    // ANSI escape sequences
    public static final String ESC = "\u001B[";
    public static final String RESET = ESC + "0m";

    public static final String CLEAR_SCREEN = ESC + "2J";
    public static final String CURSOR_HOME = ESC + "H";
    public static final String CURSOR_HIDE = ESC + "?25l";
    public static final String CURSOR_SHOW = ESC + "?25h";
    public static final String ALTERNATE_SCREEN_ON = ESC + "?1049h";
    public static final String ALTERNATE_SCREEN_OFF = ESC + "?1049l";
    public static final String MOUSE_TRACKING_ON = ESC + "?1000h" + ESC + "?1002h" + ESC + "?1015h" + ESC + "?1006h";
    public static final String MOUSE_TRACKING_OFF = ESC + "?1006l" + ESC + "?1015l" + ESC + "?1002l" + ESC + "?1000l";
    public static final String REQUEST_CURSOR_POSITION = ESC + "6n";

    public Terminal(PrintStream writer, InputStream inputStream) {
        this.writer = writer;
        this.inputStream = inputStream;
        detectTerminalSize();
    }

    /**
     * Detects terminal size using ANSI escape sequences.
     */
    protected abstract void detectTerminalSize();

    /**
     * Clears the entire screen.
     */
    public void clearScreen() {
        writer.print(CLEAR_SCREEN);
        writer.print(CURSOR_HOME);
        writer.flush();
    }

    /**
     * Moves cursor to specified position (1-based coordinates).
     */
    public void setCursorPosition(int x, int y) {
        writer.print(ESC + (y + 1) + ";" + (x + 1) + "H");
        writer.flush();
    }

    /**
     * Hides the cursor.
     */
    public void hideCursor() {
        writer.print(CURSOR_HIDE);
        writer.flush();
    }

    /**
     * Shows the cursor.
     */
    public void showCursor() {
        writer.print(CURSOR_SHOW);
        writer.flush();
    }

    /**
     * Enables mouse tracking.
     */
    public void enableMouseTracking() {
        writer.print(MOUSE_TRACKING_ON);
        writer.flush();
    }

    /**
     * Disables mouse tracking.
     */
    public void disableMouseTracking() {
        writer.print(MOUSE_TRACKING_OFF);
        writer.flush();
    }

    /**
     * Writes text at current cursor position.
     */
    public void write(String text) {
        writer.print(text);
        writer.flush();
    }

    /**
     * Writes text at current cursor position.
     */
    public void write(char text) {
        writer.print(text);
        writer.flush();
    }

    /**
     * Writes text with ANSI styling.
     */
    public void writeStyled(String text, AnsiColor foreground, AnsiColor background, AnsiFormat... formats) {
        // Reset previous formatting
        writer.print(ESC);
        writer.print("0m");

        // Apply foreground color
        if (foreground != null) {
            writer.print(foreground.getForegroundCode());
        }

        // Apply background color
        if (background != null) {
            writer.print(background.getBackgroundCode());
        }

        // Apply formats
        if (formats != null) {
            for (AnsiFormat format : formats) {
                writer.print(format.getCode());
            }
        }

        // Write styled text
        writer.print(text);

        // Reset formatting
        writer.print(ESC);
        writer.print("0m");

        writer.flush();
    }

    /**
     * Reads a single character from input (blocking).
     */
    public int read() throws IOException {
//        return System.console().reader().read();
        return inputStream.read();
    }

    /**
     * Checks if input is available without blocking.
     */
    public boolean isInputAvailable() throws IOException {
        return inputStream.available() > 0;
    }

    /**
     * Updates terminal size (call when terminal is resized).
     * Only performs actual detection once every 60 seconds to avoid unnecessary overhead.
     */
    public void updateSize() {
        long currentTime = System.currentTimeMillis();

        // Only detect terminal size if 60 seconds have passed since last detection
        if (currentTime - lastSizeDetectionTime >= sizeDetectionInterval) {
            detectTerminalSize();
            lastSizeDetectionTime = currentTime;
        }
    }

    /**
     * Forces immediate terminal size detection, bypassing the 60-second cache interval.
     * Use this method when you know the terminal has been resized and need immediate updates.
     */
    public void forceUpdateSize() {
        detectTerminalSize();
        lastSizeDetectionTime = System.currentTimeMillis();
    }

    /**
     * Gets the full render interval.
     * @return the number of renders after which a full screen render is performed
     */
    public int getFullRenderInterval() {
        return fullRenderInterval;
    }

    /**
     * Sets the full render interval.
     * @param fullRenderInterval the number of renders after which a full screen render is performed (must be > 0)
     */
    public void setFullRenderInterval(int fullRenderInterval) {
        if (fullRenderInterval <= 0) {
            throw new IllegalArgumentException("Full render interval must be greater than 0");
        }
        this.fullRenderInterval = fullRenderInterval;
    }

    /**
     * Gets the size detection interval in milliseconds.
     * @return the interval in milliseconds between terminal size detections
     */
    public long getSizeDetectionInterval() {
        return sizeDetectionInterval;
    }

    /**
     * Sets the size detection interval in milliseconds.
     * @param sizeDetectionInterval the interval in milliseconds between terminal size detections (must be > 0)
     */
    public void setSizeDetectionInterval(long sizeDetectionInterval) {
        if (sizeDetectionInterval <= 0) {
            throw new IllegalArgumentException("Size detection interval must be greater than 0");
        }
        this.sizeDetectionInterval = sizeDetectionInterval;
    }

    /**
     * Closes the terminal and restores normal mode.
     */
    public void close() {
        writer.close();
    }

    public void start() {
    }

    public void stop() {
    }

    /**
     * Initializes or reinitializes the screen buffer when terminal size changes.
     */
    private void initializeScreenBuffer() {
        screenBuffer = new StyledChar[height][width];
        // Initialize with empty styled characters
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                screenBuffer[y][x] = new StyledChar(' ');
            }
        }
        bufferInitialized = true;
        renderCount = 0;
    }

    /**
     * Checks if terminal size has changed and reinitializes buffer if needed.
     */
    private void checkAndUpdateBufferSize() {
        if (!bufferInitialized || screenBuffer == null ||
                screenBuffer.length != height ||
                (screenBuffer.length > 0 && screenBuffer[0].length != width)) {
            initializeScreenBuffer();
        }
    }

    public void renderGraphics(GeneralGraphics graphics) {
        checkAndUpdateBufferSize();

        var graphicsWidth = Math.min(graphics.getWidth(), this.width);
        var graphicsHeight = Math.min(graphics.getHeight(), this.height);

        renderCount++;
        boolean forceFullRender = (renderCount % fullRenderInterval == 0);

        if (forceFullRender) {
            // Full screen render every 10th time to correct any display errors
            renderFullScreen(graphics, graphicsWidth, graphicsHeight);
        } else {
            // Diff-based render - only update changed characters
            renderDifferences(graphics, graphicsWidth, graphicsHeight);
        }
    }

    /**
     * Renders the entire screen and updates the buffer.
     */
    private void renderFullScreen(GeneralGraphics graphics, int graphicsWidth, int graphicsHeight) {
        // clearScreen();

        for (int y = 0; y < graphicsHeight; y++) {
            setCursorPosition(0, y);
            for (int x = 0; x < graphicsWidth; x++) {
                StyledChar styledChar = graphics.getStyledChar(x, y);
                screenBuffer[y][x] = styledChar;
                toAnsiString(styledChar);
            }
        }

        // Clear any remaining buffer areas that are not covered by graphics
        clearRemainingBuffer(graphicsWidth, graphicsHeight);
    }

    /**
     * Renders only the differences between current graphics and screen buffer.
     */
    private void renderDifferences(GeneralGraphics graphics, int graphicsWidth, int graphicsHeight) {
        for (int y = 0; y < graphicsHeight; y++) {
            boolean lineHasChanges = false;
            int firstChange = -1;

            // Find changes in this line
            for (int x = 0; x < graphicsWidth; x++) {
                StyledChar newChar = graphics.getStyledChar(x, y);
                StyledChar oldChar = screenBuffer[y][x];

                if (!areCharsEqual(newChar, oldChar)) {
                    if (firstChange == -1) {
                        firstChange = x;
                        lineHasChanges = true;
                    }
                }
            }

            // Render changes in this line
            if (lineHasChanges) {
                setCursorPosition(firstChange, y);
                for (int x = firstChange; x < graphicsWidth; x++) {
                    StyledChar newChar = graphics.getStyledChar(x, y);
                    StyledChar oldChar = screenBuffer[y][x];

                    if (!areCharsEqual(newChar, oldChar)) {
                        screenBuffer[y][x] = newChar;
                        toAnsiString(newChar);
                    } else {
                        // Skip unchanged characters by moving cursor
                        setCursorPosition(x + 1, y);
                    }
                }
            }
        }

        // Handle areas outside graphics bounds
        updateRemainingBufferDiff(graphicsWidth, graphicsHeight);
    }

    /**
     * Compares two StyledChar objects for equality.
     */
    private boolean areCharsEqual(StyledChar char1, StyledChar char2) {
        if (char1 == null && char2 == null) return true;
        if (char1 == null || char2 == null) return false;

        return char1.getCharacter() == char2.getCharacter() &&
                areColorsEqual(char1.getForegroundColor(), char2.getForegroundColor()) &&
                areColorsEqual(char1.getBackgroundColor(), char2.getBackgroundColor()) &&
                java.util.Arrays.equals(char1.getFormats(), char2.getFormats());
    }

    /**
     * Compares two AnsiColor objects for equality.
     */
    private boolean areColorsEqual(AnsiColor color1, AnsiColor color2) {
        if (color1 == null && color2 == null) return true;
        if (color1 == null || color2 == null) return false;
        return color1.equals(color2);
    }

    /**
     * Clears buffer areas outside the graphics bounds.
     */
    private void clearRemainingBuffer(int graphicsWidth, int graphicsHeight) {
        StyledChar emptyChar = new StyledChar(' ');

        // Clear remaining columns in covered rows
        for (int y = 0; y < graphicsHeight && y < height; y++) {
            for (int x = graphicsWidth; x < width; x++) {
                if (!areCharsEqual(screenBuffer[y][x], emptyChar)) {
                    setCursorPosition(x, y);
                    screenBuffer[y][x] = emptyChar;
                    toAnsiString(emptyChar);
                }
            }
        }

        // Clear remaining rows
        for (int y = graphicsHeight; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!areCharsEqual(screenBuffer[y][x], emptyChar)) {
                    setCursorPosition(x, y);
                    screenBuffer[y][x] = emptyChar;
                    toAnsiString(emptyChar);
                }
            }
        }
    }

    /**
     * Updates buffer areas outside graphics bounds using diff-based approach.
     */
    private void updateRemainingBufferDiff(int graphicsWidth, int graphicsHeight) {
        StyledChar emptyChar = new StyledChar(' ');

        // Check remaining columns in covered rows
        for (int y = 0; y < graphicsHeight && y < height; y++) {
            for (int x = graphicsWidth; x < width; x++) {
                if (!areCharsEqual(screenBuffer[y][x], emptyChar)) {
                    setCursorPosition(x, y);
                    screenBuffer[y][x] = emptyChar;
                    toAnsiString(emptyChar);
                }
            }
        }

        // Check remaining rows
        for (int y = graphicsHeight; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!areCharsEqual(screenBuffer[y][x], emptyChar)) {
                    setCursorPosition(x, y);
                    screenBuffer[y][x] = emptyChar;
                    toAnsiString(emptyChar);
                }
            }
        }
    }

    /**
     * Converts this styled string to an ANSI escape sequence string.
     */
    protected void toAnsiString(StyledChar styledChar) {

        // Apply styling
        toAnsiPrefix(styledChar);
        write(styledChar.getCharacter());
        toAnsiSuffix(styledChar);
    }

    /**
     * Generates the ANSI escape sequence prefix for this style.
     */
    protected void toAnsiPrefix(StyledChar styledChar) {
        if (!styledChar.getStyle().hasFormatting()) {
            return;
        }

        // Apply foreground color
        if (styledChar.getForegroundColor() != null) {
            write(styledChar.getForegroundColor().getForegroundCode());
        }

        // Apply background color
        if (styledChar.getBackgroundColor() != null) {
            write(styledChar.getBackgroundColor().getBackgroundCode());
        }

        // Apply formats
        for (AnsiFormat format : styledChar.getFormats()) {
            write(format.getCode());
        }
    }

    /**
     * Generates the ANSI escape sequence suffix (reset) for this style.
     */
    protected void toAnsiSuffix(StyledChar styledChar) {
        if (!styledChar.getStyle().hasFormatting()) {
            return;
        }
        write(RESET);
    }

}
