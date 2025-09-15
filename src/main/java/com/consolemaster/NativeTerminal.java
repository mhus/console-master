package com.consolemaster;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

@Slf4j
public class NativeTerminal extends Terminal {

    private boolean rawMode = false;

    public NativeTerminal() {
        this(System.out, System.in);
    }

    public NativeTerminal(PrintStream writer, InputStream inputStream) {
        super(writer, inputStream);
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

    public void start() {
        try {
            enterRawMode();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        try {
            exitRawMode();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes the terminal and restores normal mode.
     */
    public void close() {
        if (rawMode) {
            try {
                exitRawMode();
            } catch (IOException e) {
                log.warn("Failed to exit raw mode on close", e);
            }
        }
        super.close();
    }

    /**
     * Detects terminal size using ANSI escape sequences.
     */
    protected void detectTerminalSize() {
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

}
