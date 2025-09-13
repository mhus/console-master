package com.consolemaster;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a styled character with color and formatting information.
 */
@Data
@AllArgsConstructor
public class StyledChar {
    private char character;
    private AnsiColor foregroundColor;
    private AnsiColor backgroundColor;
    private AnsiFormat[] formats;

    /**
     * Creates a styled character with only a foreground color.
     */
    public StyledChar(char character, AnsiColor foregroundColor) {
        this(character, foregroundColor, null, new AnsiFormat[0]);
    }

    /**
     * Creates a plain character without styling.
     */
    public StyledChar(char character) {
        this(character, null, null, new AnsiFormat[0]);
    }

    /**
     * Converts this styled character to its ANSI representation.
     */
    public String toAnsiString() {
        StringBuilder sb = new StringBuilder();

        // Add formatting codes
        if (formats != null) {
            for (AnsiFormat format : formats) {
                sb.append(format.getCode());
            }
        }

        // Add color codes
        if (foregroundColor != null) {
            sb.append(foregroundColor.getForeground());
        }
        if (backgroundColor != null) {
            sb.append(backgroundColor.getBackground());
        }

        // Add the character
        sb.append(character);

        // Reset if any styling was applied
        if (hasAnyStyle()) {
            sb.append(AnsiColor.RESET.getForeground());
        }

        return sb.toString();
    }

    /**
     * Checks if this character has any styling applied.
     */
    public boolean hasAnyStyle() {
        return foregroundColor != null || backgroundColor != null ||
               (formats != null && formats.length > 0);
    }
}
