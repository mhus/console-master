package com.consolemaster;

import lombok.Getter;

import java.io.*;

/**
 * Native terminal implementation that replaces JLine dependency.
 * Handles ANSI escape sequences and terminal control directly.
 */
@Getter
public abstract class Terminal {

    private final PrintStream writer;
    private final InputStream inputStream;
    private int width;
    private int height;
    private boolean rawMode = false;

    // ANSI escape sequences
    private static final String ESC = "\u001B[";
    private static final String CLEAR_SCREEN = ESC + "2J";
    private static final String CURSOR_HOME = ESC + "H";
    private static final String CURSOR_HIDE = ESC + "?25l";
    private static final String CURSOR_SHOW = ESC + "?25h";
    private static final String ALTERNATE_SCREEN_ON = ESC + "?1049h";
    private static final String ALTERNATE_SCREEN_OFF = ESC + "?1049l";
    private static final String MOUSE_TRACKING_ON = ESC + "?1000h" + ESC + "?1002h" + ESC + "?1015h" + ESC + "?1006h";
    private static final String MOUSE_TRACKING_OFF = ESC + "?1006l" + ESC + "?1015l" + ESC + "?1002l" + ESC + "?1000l";
    private static final String REQUEST_CURSOR_POSITION = ESC + "6n";

    public Terminal(PrintStream writer, InputStream inputStream) {
        this.writer = writer;
        this.inputStream = inputStream;
        detectTerminalSize();
    }

    /**
     * Detects terminal size using ANSI escape sequences.
     */
    private void detectTerminalSize() {
        try {
            // Try to get terminal size from environment variables first
            String columns = System.getenv("COLUMNS");
            String lines = System.getenv("LINES");

            if (columns != null && lines != null) {
                this.width = Integer.parseInt(columns);
                this.height = Integer.parseInt(lines);
                return;
            }

            // Fallback to default size if detection fails
            this.width = 200;
            this.height = 100;

            // Try to detect using stty if available (Unix systems)
            if (System.getProperty("os.name").toLowerCase().contains("nix") ||
                System.getProperty("os.name").toLowerCase().contains("nux") ||
                System.getProperty("os.name").toLowerCase().contains("mac")) {
                detectSizeWithStty();
            }

        } catch (Exception e) {
            // Use default size if detection fails
            this.width = 80;
            this.height = 24;
        }
    }

    /**
     * Detects terminal size using stty command on Unix systems.
     */
    private void detectSizeWithStty() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"stty", "size"});
            process.waitFor();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length == 2) {
                        this.height = Integer.parseInt(parts[0]);
                        this.width = Integer.parseInt(parts[1]);
                    }
                }
            }
        } catch (Exception e) {
            // Ignore errors and keep default size
        }
    }

    /**
     * Enters raw mode for character-by-character input.
     */
    public void enterRawMode() throws IOException {
        if (rawMode) return;

        try {
            // Unix/Linux/macOS
            if (System.getProperty("os.name").toLowerCase().contains("nix") ||
                System.getProperty("os.name").toLowerCase().contains("nux") ||
                System.getProperty("os.name").toLowerCase().contains("mac")) {

                Runtime.getRuntime().exec(new String[]{"stty", "-echo", "raw"}).waitFor();
            }
            // Windows would need different handling

            rawMode = true;

            // Enable alternate screen and hide cursor
            writer.print(ALTERNATE_SCREEN_ON);
            writer.print(CURSOR_HIDE);
            writer.flush();

        } catch (Exception e) {
            throw new IOException("Failed to enter raw mode", e);
        }
    }

    /**
     * Exits raw mode and restores normal terminal settings.
     */
    public void exitRawMode() throws IOException {
        if (!rawMode) return;

        try {
            // Restore cursor and exit alternate screen
            writer.print(CURSOR_SHOW);
            writer.print(ALTERNATE_SCREEN_OFF);
            writer.flush();

            // Unix/Linux/macOS
            if (System.getProperty("os.name").toLowerCase().contains("nix") ||
                System.getProperty("os.name").toLowerCase().contains("nux") ||
                System.getProperty("os.name").toLowerCase().contains("mac")) {

                Runtime.getRuntime().exec(new String[]{"stty", "echo", "cooked"}).waitFor();
            }

            rawMode = false;

        } catch (Exception e) {
            throw new IOException("Failed to exit raw mode", e);
        }
    }

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
     */
    public void updateSize() {
        detectTerminalSize();
    }

    /**
     * Closes the terminal and restores normal mode.
     */
    public void close() throws IOException {
        if (rawMode) {
            exitRawMode();
        }
        writer.close();
    }
}
