package com.consolemaster;

/**
 * ANSI formatting codes for console text styling.
 */
public enum AnsiFormat {
    RESET("\u001B[0m"),
    BOLD("\u001B[1m"),
    DIM("\u001B[2m"),
    ITALIC("\u001B[3m"),
    UNDERLINE("\u001B[4m"),
    BLINK("\u001B[5m"),
    REVERSE("\u001B[7m"),
    STRIKETHROUGH("\u001B[9m"),

    // Reset specific formatting
    RESET_BOLD("\u001B[22m"),
    RESET_DIM("\u001B[22m"),
    RESET_ITALIC("\u001B[23m"),
    RESET_UNDERLINE("\u001B[24m"),
    RESET_BLINK("\u001B[25m"),
    RESET_REVERSE("\u001B[27m"),
    RESET_STRIKETHROUGH("\u001B[29m");

    private final String code;

    AnsiFormat(String code) {
        this.code = code;
    }

    /**
     * Gets the ANSI code for this formatting.
     */
    public String getCode() {
        return code;
    }
}
