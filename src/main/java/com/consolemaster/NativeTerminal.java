package com.consolemaster;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
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


}
