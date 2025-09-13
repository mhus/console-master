package com.consolemaster;

import lombok.Getter;
import lombok.Setter;

/**
 * Native implementation of styled text that replaces JLine's AttributedString.
 * Handles ANSI escape sequences directly without external dependencies.
 */
@Getter
@Setter
public class StyledString {

    private final String text;
    private final TextStyle style;

    public StyledString(String text) {
        this(text, TextStyle.DEFAULT);
    }

    public StyledString(String text, TextStyle style) {
        this.text = text != null ? text : "";
        this.style = style != null ? style : TextStyle.DEFAULT;
    }

    /**
     * Creates a styled string with specified colors and formats.
     */
    public StyledString(String text, AnsiColor foreground, AnsiColor background, AnsiFormat... formats) {
        this.text = text != null ? text : "";
        this.style = new TextStyle(foreground, background, formats);
    }

    /**
     * Returns the length of the text (without ANSI codes).
     */
    public int length() {
        return text.length();
    }

    /**
     * Returns a substring with the same styling.
     */
    public StyledString substring(int start) {
        return new StyledString(text.substring(start), style);
    }

    /**
     * Returns a substring with the same styling.
     */
    public StyledString substring(int start, int end) {
        return new StyledString(text.substring(start, end), style);
    }

    /**
     * Returns the character at the specified index.
     */
    public char charAt(int index) {
        return text.charAt(index);
    }

    /**
     * Returns the styled character at the specified index.
     */
    public StyledChar getStyledChar(int index) {
        if (index < 0 || index >= text.length()) {
            return new StyledChar(' ', style);
        }
        return new StyledChar(text.charAt(index), style);
    }

    /**
     * Converts this styled string to an ANSI escape sequence string.
     */
    public void toAnsiString(NativeTerminal terminal) {
        if (text.isEmpty()) {
            return;
        }

        // Apply styling
        style.toAnsiPrefix(terminal);
        terminal.write(text);
        style.toAnsiSuffix(terminal);
    }

    /**
     * Returns just the plain text without any styling.
     */
    @Override
    public String toString() {
        return text;
    }

    /**
     * Creates a new StyledString with updated foreground color.
     */
    public StyledString withForeground(AnsiColor color) {
        return new StyledString(text, style.withForeground(color));
    }

    /**
     * Creates a new StyledString with updated background color.
     */
    public StyledString withBackground(AnsiColor color) {
        return new StyledString(text, style.withBackground(color));
    }

    /**
     * Creates a new StyledString with additional formats.
     */
    public StyledString withFormats(AnsiFormat... formats) {
        return new StyledString(text, style.withFormats(formats));
    }

    /**
     * Creates a new StyledString with updated style.
     */
    public StyledString withStyle(TextStyle newStyle) {
        return new StyledString(text, newStyle);
    }

    /**
     * Concatenates this styled string with another.
     */
    public StyledString concat(StyledString other) {
        if (other == null || other.text.isEmpty()) {
            return this;
        }

        // If styles are the same, we can concatenate efficiently
        if (this.style.equals(other.style)) {
            return new StyledString(this.text + other.text, this.style);
        }

        // Different styles - need to handle separately
        // Return a composite representation (for now, return first part)
        return this;
    }

    /**
     * Checks if this styled string is empty.
     */
    public boolean isEmpty() {
        return text.isEmpty();
    }

    /**
     * Creates a StyledString from plain text.
     */
    public static StyledString of(String text) {
        return new StyledString(text);
    }

    /**
     * Creates a StyledString with specified styling.
     */
    public static StyledString of(String text, AnsiColor foreground, AnsiColor background, AnsiFormat... formats) {
        return new StyledString(text, foreground, background, formats);
    }

    /**
     * Creates an empty StyledString.
     */
    public static StyledString empty() {
        return new StyledString("");
    }
}
