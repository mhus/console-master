package com.consolemaster;

import lombok.Getter;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

/**
 * Enhanced Graphics implementation using JLine's AttributedString for better ANSI support.
 * Extends the base Graphics class with JLine-specific functionality.
 */
@Getter
public class JLineGraphics extends Graphics {

    private final AttributedString[][] buffer;

    // Current drawing style using JLine's AttributedStyle
    private AttributedStyle currentStyle = AttributedStyle.DEFAULT;

    /**
     * Creates a JLine Graphics context.
     */
    public JLineGraphics(int width, int height) {
        super(width, height);
        this.buffer = new AttributedString[height][width];
        clear();
    }

    /**
     * Sets the current style for subsequent drawing operations.
     */
    public void setStyle(AttributedStyle style) {
        this.currentStyle = style != null ? style : AttributedStyle.DEFAULT;
    }

    /**
     * Sets foreground color using JLine's color constants.
     */
    public void setForegroundColor(int color) {
        this.currentStyle = currentStyle.foreground(color);
    }

    /**
     * Sets background color using JLine's color constants.
     */
    public void setBackgroundColor(int color) {
        this.currentStyle = currentStyle.background(color);
    }

    @Override
    public void setForegroundColor(AnsiColor color) {
        if (color != null) {
            this.currentStyle = currentStyle.foreground(mapAnsiColorToJLine(color));
        }
    }

    @Override
    public void setBackgroundColor(AnsiColor color) {
        if (color != null) {
            this.currentStyle = currentStyle.background(mapAnsiColorToJLine(color));
        }
    }

    @Override
    public void setFormats(AnsiFormat... formats) {
        AttributedStyle style = AttributedStyle.DEFAULT;
        if (formats != null) {
            for (AnsiFormat format : formats) {
                style = switch (format) {
                    case BOLD -> style.bold();
                    case ITALIC -> style.italic();
                    case UNDERLINE -> style.underline();
                    case STRIKETHROUGH -> style.crossedOut();
                    case DIM -> style.faint();
                    case REVERSE -> style.inverse();
                    case BLINK -> style.blink();
                    default -> style; // Für unbekannte oder nicht unterstützte Formate
                };
            }
        }
        this.currentStyle = style;
    }

    /**
     * Sets text to bold.
     */
    public void setBold(boolean bold) {
        this.currentStyle = bold ? currentStyle.bold() : currentStyle.boldOff();
    }

    /**
     * Sets text to italic.
     */
    public void setItalic(boolean italic) {
        this.currentStyle = italic ? currentStyle.italic() : currentStyle.italicOff();
    }

    /**
     * Sets text to underlined.
     */
    public void setUnderline(boolean underline) {
        this.currentStyle = underline ? currentStyle.underline() : currentStyle.underlineOff();
    }

    @Override
    public void resetStyle() {
        this.currentStyle = AttributedStyle.DEFAULT;
    }

    @Override
    public void clear() {
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                buffer[y][x] = new AttributedString(" ");
            }
        }
    }

    @Override
    public void drawChar(int x, int y, char c) {
        if (isValid(x, y)) {
            buffer[y][x] = new AttributedString(String.valueOf(c), currentStyle);
        }
    }

    @Override
    public void drawString(int x, int y, String text) {
        if (text == null || y < 0 || y >= getHeight()) {
            return;
        }

        for (int i = 0; i < text.length() && x + i < getWidth(); i++) {
            if (x + i >= 0) {
                buffer[y][x + i] = new AttributedString(String.valueOf(text.charAt(i)), currentStyle);
            }
        }
    }

    @Override
    public void drawStyledString(int x, int y, String text, AnsiColor foregroundColor, AnsiColor backgroundColor, AnsiFormat... formats) {
        if (text == null || y < 0 || y >= getHeight()) {
            return;
        }

        AttributedStyle style = createStyle(foregroundColor, backgroundColor, formats);
        for (int i = 0; i < text.length() && x + i < getWidth(); i++) {
            if (x + i >= 0) {
                buffer[y][x + i] = new AttributedString(String.valueOf(text.charAt(i)), style);
            }
        }
    }

    /**
     * Draws a string with explicit JLine style.
     */
    public void drawStyledString(int x, int y, String text, AttributedStyle style) {
        if (text == null || y < 0 || y >= getHeight()) {
            return;
        }

        for (int i = 0; i < text.length() && x + i < getWidth(); i++) {
            if (x + i >= 0) {
                buffer[y][x + i] = new AttributedString(String.valueOf(text.charAt(i)), style);
            }
        }
    }

    /**
     * Gets an AttributedString at the specified position.
     */
    public AttributedString getAttributedString(int x, int y) {
        if (isValid(x, y)) {
            return buffer[y][x];
        }
        return null;
    }

    /**
     * Sets an AttributedString at the specified position.
     */
    public void setAttributedString(int x, int y, AttributedString attributedString) {
        if (isValid(x, y)) {
            buffer[y][x] = attributedString;
        }
    }

    /**
     * Converts the graphics buffer to a single AttributedString for terminal output.
     */
    public AttributedString toAttributedString() {
        AttributedStringBuilder builder = new AttributedStringBuilder();

        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                AttributedString str = buffer[y][x];
                if (str != null) {
                    builder.append(str);
                } else {
                    builder.append(" ");
                }
            }
            if (y < getHeight() - 1) {
                builder.append("\n");
            }
        }

        return builder.toAttributedString();
    }

    /**
     * Creates an AttributedStyle from AnsiColor and AnsiFormat parameters.
     */
    private AttributedStyle createStyle(AnsiColor foregroundColor, AnsiColor backgroundColor, AnsiFormat... formats) {
        AttributedStyle style = AttributedStyle.DEFAULT;

        if (foregroundColor != null) {
            style = style.foreground(mapAnsiColorToJLine(foregroundColor));
        }

        if (backgroundColor != null) {
            style = style.background(mapAnsiColorToJLine(backgroundColor));
        }

        if (formats != null) {
            for (AnsiFormat format : formats) {
                style = switch (format) {
                    case BOLD -> style.bold();
                    case ITALIC -> style.italic();
                    case UNDERLINE -> style.underline();
                    case STRIKETHROUGH -> style.crossedOut();
                    case DIM -> style.faint();
                    case REVERSE -> style.inverse();
                    case BLINK -> style.blink();
                    default -> style; // Für unbekannte oder nicht unterstützte Formate
                };
            }
        }

        return style;
    }

    /**
     * Maps AnsiColor to JLine color constants.
     */
    private int mapAnsiColorToJLine(AnsiColor color) {
        return switch (color) {
            case BLACK -> AttributedStyle.BLACK;
            case RED -> AttributedStyle.RED;
            case GREEN -> AttributedStyle.GREEN;
            case YELLOW -> AttributedStyle.YELLOW;
            case BLUE -> AttributedStyle.BLUE;
            case MAGENTA -> AttributedStyle.MAGENTA;
            case CYAN -> AttributedStyle.CYAN;
            case WHITE -> AttributedStyle.WHITE;
            case BRIGHT_BLACK -> AttributedStyle.BRIGHT + AttributedStyle.BLACK;
            case BRIGHT_RED -> AttributedStyle.BRIGHT + AttributedStyle.RED;
            case BRIGHT_GREEN -> AttributedStyle.BRIGHT + AttributedStyle.GREEN;
            case BRIGHT_YELLOW -> AttributedStyle.BRIGHT + AttributedStyle.YELLOW;
            case BRIGHT_BLUE -> AttributedStyle.BRIGHT + AttributedStyle.BLUE;
            case BRIGHT_MAGENTA -> AttributedStyle.BRIGHT + AttributedStyle.MAGENTA;
            case BRIGHT_CYAN -> AttributedStyle.BRIGHT + AttributedStyle.CYAN;
            case BRIGHT_WHITE -> AttributedStyle.BRIGHT + AttributedStyle.WHITE;
            default -> AttributedStyle.WHITE;
        };
    }
}
