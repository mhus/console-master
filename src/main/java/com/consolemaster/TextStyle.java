package com.consolemaster;

import lombok.Getter;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Native text styling implementation that replaces JLine's AttributedStyle.
 * Handles ANSI escape sequences for colors and formatting.
 */
@Getter
@EqualsAndHashCode
public class TextStyle {

    public static final TextStyle DEFAULT = new TextStyle();

    private final AnsiColor foregroundColor;
    private final AnsiColor backgroundColor;
    private final Set<AnsiFormat> formats;

    /**
     * Creates a default text style (no colors, no formats).
     */
    public TextStyle() {
        this.foregroundColor = null;
        this.backgroundColor = null;
        this.formats = new HashSet<>();
    }

    /**
     * Creates a text style with specified colors and formats.
     */
    public TextStyle(AnsiColor foregroundColor, AnsiColor backgroundColor, AnsiFormat... formats) {
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.formats = new HashSet<>();
        if (formats != null) {
            this.formats.addAll(Arrays.asList(formats));
        }
    }

    /**
     * Creates a copy of this style with updated foreground color.
     */
    public TextStyle withForeground(AnsiColor color) {
        return new TextStyle(color, backgroundColor, formats.toArray(new AnsiFormat[0]));
    }

    /**
     * Creates a copy of this style with updated background color.
     */
    public TextStyle withBackground(AnsiColor color) {
        return new TextStyle(foregroundColor, color, formats.toArray(new AnsiFormat[0]));
    }

    /**
     * Creates a copy of this style with additional formats.
     */
    public TextStyle withFormats(AnsiFormat... newFormats) {
        Set<AnsiFormat> combinedFormats = new HashSet<>(formats);
        if (newFormats != null) {
            combinedFormats.addAll(Arrays.asList(newFormats));
        }
        return new TextStyle(foregroundColor, backgroundColor, combinedFormats.toArray(new AnsiFormat[0]));
    }

    /**
     * Creates a copy of this style with bold formatting.
     */
    public TextStyle bold() {
        return withFormats(AnsiFormat.BOLD);
    }

    /**
     * Creates a copy of this style without bold formatting.
     */
    public TextStyle boldOff() {
        Set<AnsiFormat> newFormats = new HashSet<>(formats);
        newFormats.remove(AnsiFormat.BOLD);
        return new TextStyle(foregroundColor, backgroundColor, newFormats.toArray(new AnsiFormat[0]));
    }

    /**
     * Creates a copy of this style with italic formatting.
     */
    public TextStyle italic() {
        return withFormats(AnsiFormat.ITALIC);
    }

    /**
     * Creates a copy of this style without italic formatting.
     */
    public TextStyle italicOff() {
        Set<AnsiFormat> newFormats = new HashSet<>(formats);
        newFormats.remove(AnsiFormat.ITALIC);
        return new TextStyle(foregroundColor, backgroundColor, newFormats.toArray(new AnsiFormat[0]));
    }

    /**
     * Creates a copy of this style with underline formatting.
     */
    public TextStyle underline() {
        return withFormats(AnsiFormat.UNDERLINE);
    }

    /**
     * Creates a copy of this style without underline formatting.
     */
    public TextStyle underlineOff() {
        Set<AnsiFormat> newFormats = new HashSet<>(formats);
        newFormats.remove(AnsiFormat.UNDERLINE);
        return new TextStyle(foregroundColor, backgroundColor, newFormats.toArray(new AnsiFormat[0]));
    }

    /**
     * Creates a copy of this style with strikethrough formatting.
     */
    public TextStyle crossedOut() {
        return withFormats(AnsiFormat.STRIKETHROUGH);
    }

    /**
     * Creates a copy of this style with dim formatting.
     */
    public TextStyle faint() {
        return withFormats(AnsiFormat.DIM);
    }

    /**
     * Creates a copy of this style with inverse formatting.
     */
    public TextStyle inverse() {
        return withFormats(AnsiFormat.REVERSE);
    }

    /**
     * Creates a copy of this style with blink formatting.
     */
    public TextStyle blink() {
        return withFormats(AnsiFormat.BLINK);
    }

    /**
     * Checks if this style has any formatting applied.
     */
    public boolean hasFormatting() {
        return foregroundColor != null || backgroundColor != null || !formats.isEmpty();
    }

    /**
     * Creates a styled string with this style.
     */
    public StyledString style(String text) {
        return new StyledString(text, this);
    }

    /**
     * Factory method for creating a style with foreground color.
     */
    public static TextStyle foreground(AnsiColor color) {
        return new TextStyle(color, null);
    }

    /**
     * Factory method for creating a style with background color.
     */
    public static TextStyle background(AnsiColor color) {
        return new TextStyle(null, color);
    }

    /**
     * Factory method for creating a style with formats only.
     */
    public static TextStyle formats(AnsiFormat... formats) {
        return new TextStyle(null, null, formats);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TextStyle{");
        if (foregroundColor != null) {
            sb.append("fg=").append(foregroundColor);
        }
        if (backgroundColor != null) {
            if (foregroundColor != null) sb.append(", ");
            sb.append("bg=").append(backgroundColor);
        }
        if (!formats.isEmpty()) {
            if (foregroundColor != null || backgroundColor != null) sb.append(", ");
            sb.append("formats=").append(formats);
        }
        sb.append("}");
        return sb.toString();
    }
}
