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
    private int width;
    private int height;

    // ANSI escape sequences
    public static final String ESC = "\u001B[";
    private static final String RESET = ESC + "0m";

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
     */
    public void updateSize() {
        detectTerminalSize();
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

    public void renderGraphics(NativeGraphics graphics) {
    }

    /**
     * Renders the graphics buffer to the terminal using ANSI escape sequences.
     */
    public void toAnsiString(NativeGraphics graphics) {
        for (int y = 0; y < graphics.getHeight(); y++) {
            for (int x = 0; x < graphics.getWidth(); x++) {
                StyledChar styledChar = graphics.getStyledChar(x,y);
                toAnsiString(styledChar);
            }
            if (y < getHeight() - 1) {
                write("\n");
            }
        }
    }

    /**
     * Converts this styled string to an ANSI escape sequence string.
     */
    public void toAnsiString(StyledChar styledChar) {

        // Apply styling
        toAnsiPrefix(styledChar);
        write(styledChar.getCharacter());
        toAnsiSuffix(styledChar);
    }

    /**
     * Generates the ANSI escape sequence prefix for this style.
     */
    public void toAnsiPrefix(StyledChar styledChar) {
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
    public void toAnsiSuffix(StyledChar styledChar) {
        if (!styledChar.getStyle().hasFormatting()) {
            return;
        }
        write(RESET);
    }

}
