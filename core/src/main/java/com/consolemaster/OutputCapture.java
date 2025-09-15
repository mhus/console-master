package com.consolemaster;

import lombok.Getter;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Captures stdout and stderr output streams and makes them available for display.
 * This class redirects System.out and System.err to internal buffers while preserving
 * the ability to write to the original streams when needed.
 */
public class OutputCapture {

    private final List<String> stdoutLines = new CopyOnWriteArrayList<>();
    private final List<String> stderrLines = new CopyOnWriteArrayList<>();

    private final PrintStream originalOut;
    private final PrintStream originalErr;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private boolean capturing = false;
    private int maxLines = 1000; // Maximum number of lines to keep in memory

    /**
     * Creates a new OutputCapture instance.
     */
    public OutputCapture() {
        this.originalOut = System.out;
        this.originalErr = System.err;
    }

    /**
     * Sets the maximum number of lines to keep in memory for each stream.
     * When exceeded, oldest lines are removed.
     *
     * @param maxLines the maximum number of lines (must be > 0)
     */
    public void setMaxLines(int maxLines) {
        if (maxLines > 0) {
            this.maxLines = maxLines;
            trimLines();
        }
    }

    /**
     * Starts capturing stdout and stderr.
     */
    public void startCapture() {
        lock.writeLock().lock();
        try {
            if (!capturing) {
                System.setOut(new PrintStream(new CaptureOutputStream(stdoutLines, false)));
                System.setErr(new PrintStream(new CaptureOutputStream(stderrLines, true)));
                capturing = true;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Stops capturing and restores original stdout and stderr.
     */
    public void stopCapture() {
        lock.writeLock().lock();
        try {
            if (capturing) {
                System.setOut(originalOut);
                System.setErr(originalErr);
                capturing = false;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Clears all captured output.
     */
    public void clear() {
        lock.writeLock().lock();
        try {
            stdoutLines.clear();
            stderrLines.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Gets a copy of all stdout lines.
     *
     * @return list of stdout lines
     */
    public List<String> getStdoutLines() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(stdoutLines);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Gets a copy of all stderr lines.
     *
     * @return list of stderr lines
     */
    public List<String> getStderrLines() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(stderrLines);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Gets the last N stdout lines.
     *
     * @param count number of lines to get
     * @return list of the last N stdout lines
     */
    public List<String> getLastStdoutLines(int count) {
        lock.readLock().lock();
        try {
            int size = stdoutLines.size();
            if (count >= size) {
                return new ArrayList<>(stdoutLines);
            }
            return new ArrayList<>(stdoutLines.subList(size - count, size));
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Gets the last N stderr lines.
     *
     * @param count number of lines to get
     * @return list of the last N stderr lines
     */
    public List<String> getLastStderrLines(int count) {
        lock.readLock().lock();
        try {
            int size = stderrLines.size();
            if (count >= size) {
                return new ArrayList<>(stderrLines);
            }
            return new ArrayList<>(stderrLines.subList(size - count, size));
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Writes a message directly to the original stdout (bypassing capture).
     *
     * @param message the message to write
     */
    public void writeToOriginalOut(String message) {
        originalOut.print(message);
    }

    /**
     * Writes a message directly to the original stderr (bypassing capture).
     *
     * @param message the message to write
     */
    public void writeToOriginalErr(String message) {
        originalErr.print(message);
    }

    /**
     * Checks if output capture is currently active.
     *
     * @return true if capturing, false otherwise
     */
    public boolean isCapturing() {
        lock.readLock().lock();
        try {
            return capturing;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Trims the line lists to the maximum allowed size.
     */
    private void trimLines() {
        trimLineList(stdoutLines);
        trimLineList(stderrLines);
    }

    /**
     * Trims a line list to the maximum allowed size.
     *
     * @param lines the list to trim
     */
    private void trimLineList(List<String> lines) {
        while (lines.size() > maxLines) {
            lines.remove(0);
        }
    }

    /**
     * Custom OutputStream that captures output and adds it to a line buffer.
     */
    private class CaptureOutputStream extends OutputStream {
        private final List<String> lines;
        private final boolean isError;
        private final StringBuilder currentLine = new StringBuilder();

        CaptureOutputStream(List<String> lines, boolean isError) {
            this.lines = lines;
            this.isError = isError;
        }

        @Override
        public void write(int b) throws IOException {
            synchronized (currentLine) {
                if (b == '\n') {
                    // End of line - add to buffer
                    lock.writeLock().lock();
                    try {
                        lines.add(currentLine.toString());
                        trimLineList(lines);
                    } finally {
                        lock.writeLock().unlock();
                    }
                    currentLine.setLength(0);
                } else if (b != '\r') {
                    // Add character (ignore carriage return)
                    currentLine.append((char) b);
                }
            }

            // Also write to original stream if desired for debugging
            // Uncomment the next line to also output to original streams:
            // (isError ? originalErr : originalOut).write(b);
        }

        @Override
        public void flush() throws IOException {
            // Flush current line if it has content
            synchronized (currentLine) {
                if (currentLine.length() > 0) {
                    lock.writeLock().lock();
                    try {
                        lines.add(currentLine.toString());
                        trimLineList(lines);
                    } finally {
                        lock.writeLock().unlock();
                    }
                    currentLine.setLength(0);
                }
            }
        }
    }
}
