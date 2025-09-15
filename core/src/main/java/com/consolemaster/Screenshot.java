package com.consolemaster;

import java.io.PrintStream;

/**
 * Utility class for capturing and outputting screenshots of graphics buffers.
 * Supports both plain text and styled output with ANSI colors.
 */
public class Screenshot {

    private final StyledChar[][] buffer;
    private final int width;
    private final int height;
    private final PrintStream output;

    /**
     * Creates a Screenshot instance with a StyledChar buffer.
     *
     * @param buffer the StyledChar buffer to capture
     * @param width the width of the buffer
     * @param height the height of the buffer
     * @param output the PrintStream to output to
     */
    public Screenshot(StyledChar[][] buffer, int width, int height, PrintStream output) {
        this.buffer = buffer;
        this.width = width;
        this.height = height;
        this.output = output;
    }

    /**
     * Creates a Screenshot instance with a LegacyGraphics context.
     *
     * @param graphics the LegacyGraphics context
     * @param output the PrintStream to output to
     */
    public Screenshot(LegacyGraphics graphics, PrintStream output) {
        this.buffer = graphics.getBuffer();
        this.width = graphics.getWidth();
        this.height = graphics.getHeight();
        this.output = output;
    }

    /**
     * Creates a Screenshot instance with a char buffer (converts to StyledChar).
     *
     * @param charBuffer the char buffer to capture
     * @param width the width of the buffer
     * @param height the height of the buffer
     * @param output the PrintStream to output to
     */
    public Screenshot(char[][] charBuffer, int width, int height, PrintStream output) {
        this.width = width;
        this.height = height;
        this.output = output;

        // Convert char buffer to StyledChar buffer
        this.buffer = new StyledChar[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.buffer[y][x] = new StyledChar(charBuffer[y][x]);
            }
        }
    }

    /**
     * Prints the buffer content with a border frame.
     *
     * @param styled if true, includes ANSI color codes and formatting
     */
    public void print(boolean styled) {
        // Print top border
        output.println("+" + "-".repeat(width) + "+");

        // Print content rows
        for (int y = 0; y < height; y++) {
            output.print("|");
            for (int x = 0; x < width; x++) {
                StyledChar styledChar = buffer[y][x];
                char c = styledChar.getCharacter();

                // Handle null or zero characters
                if (c == '\0') {
                    c = ' ';
                }

                if (styled && hasStyle(styledChar)) {
                    // Output with ANSI codes
                    output.print(buildStyledString(c, styledChar));
                } else {
                    // Plain character output
                    output.print(c);
                }
            }
            output.println("|");
        }

        // Print bottom border
        output.println("+" + "-".repeat(width) + "+");
    }

    /**
     * Prints the buffer content without styling (plain text only).
     */
    public void print() {
        print(false);
    }

    /**
     * Checks if a StyledChar has any styling applied.
     */
    private boolean hasStyle(StyledChar styledChar) {
        return styledChar.getForegroundColor() != null ||
               styledChar.getBackgroundColor() != null ||
               (styledChar.getFormats() != null && styledChar.getFormats().length > 0);
    }

    /**
     * Builds a styled string with ANSI codes for the given character and style.
     */
    private String buildStyledString(char c, StyledChar styledChar) {
        StringBuilder sb = new StringBuilder();

        // Start ANSI sequence
        sb.append("\u001B[");
        boolean hasCode = false;

        // Add foreground color
        if (styledChar.getForegroundColor() != null) {
            sb.append(styledChar.getForegroundColor().getForegroundCode());
            hasCode = true;
        }

        // Add background color
        if (styledChar.getBackgroundColor() != null) {
            if (hasCode) sb.append(";");
            sb.append(styledChar.getBackgroundColor().getBackgroundCode());
            hasCode = true;
        }

        // Add formats
        if (styledChar.getFormats() != null) {
            for (AnsiFormat format : styledChar.getFormats()) {
                if (hasCode) sb.append(";");
                sb.append(format.getCode());
                hasCode = true;
            }
        }

        // Close ANSI sequence and add character
        if (hasCode) {
            sb.append(c);
            sb.append(AnsiFormat.RESET.getCode()); // Reset
        } else {
            // No styling, just return the character
            return String.valueOf(c);
        }

        return sb.toString();
    }

    /**
     * Returns the buffer content as a plain string (for testing or further processing).
     *
     * @param styled if true, includes ANSI color codes and formatting
     * @return the buffer content as string
     */
    public String toString(boolean styled) {
        StringBuilder sb = new StringBuilder();

        // Top border
        sb.append("+").append("-".repeat(width)).append("+\n");

        // Content rows
        for (int y = 0; y < height; y++) {
            sb.append("|");
            for (int x = 0; x < width; x++) {
                StyledChar styledChar = buffer[y][x];
                char c = styledChar.getCharacter();

                if (c == '\0') {
                    c = ' ';
                }

                if (styled && hasStyle(styledChar)) {
                    sb.append(buildStyledString(c, styledChar));
                } else {
                    sb.append(c);
                }
            }
            sb.append("|\n");
        }

        // Bottom border
        sb.append("+").append("-".repeat(width)).append("+");

        return sb.toString();
    }

    /**
     * Returns the buffer content as a plain string without styling.
     */
    @Override
    public String toString() {
        return toString(false);
    }

    /**
     * Saves the screenshot to a file.
     *
     * @param filename the filename to save to
     * @param styled if true, includes ANSI color codes and formatting
     * @throws java.io.IOException if file writing fails
     */
    public void saveToFile(String filename, boolean styled) throws java.io.IOException {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(filename)) {
            writer.print(toString(styled));
        }
    }
}
