package com.consolemaster;

import lombok.Getter;
import lombok.Setter;
import org.jline.utils.AttributedStyle;

/**
 * A Canvas implementation for displaying formatted text with various alignment options.
 * Supports multi-line text, different text alignments, and styling options.
 */
@Getter
@Setter
public class Text extends Canvas {

    /**
     * Text alignment options.
     */
    public enum Alignment {
        LEFT,
        CENTER,
        RIGHT
    }

    private String text;
    private Alignment alignment = Alignment.LEFT;
    private boolean wordWrap = true;
    private String lineBreak = "\n";

    // JLine styling properties
    private AttributedStyle textStyle;
    private AnsiColor foregroundColor;
    private AnsiColor backgroundColor;
    private AnsiFormat[] formats = new AnsiFormat[0];

    /**
     * Creates a Text canvas with specified position, size and text content.
     *
     * @param x      the x-coordinate
     * @param y      the y-coordinate
     * @param width  the width of the text area
     * @param height the height of the text area
     * @param text   the text content to display
     */
    public Text(int x, int y, int width, int height, String text) {
        super(x, y, width, height);
        this.text = text != null ? text : "";
    }

    /**
     * Creates a Text canvas with alignment.
     *
     * @param x         the x-coordinate
     * @param y         the y-coordinate
     * @param width     the width of the text area
     * @param height    the height of the text area
     * @param text      the text content to display
     * @param alignment the text alignment
     */
    public Text(int x, int y, int width, int height, String text, Alignment alignment) {
        this(x, y, width, height, text);
        this.alignment = alignment;
    }

    /**
     * Sets the foreground color for the text.
     *
     * @param color the foreground color
     */
    public void setForegroundColor(AnsiColor color) {
        this.foregroundColor = color;
        updateTextStyle();
    }

    /**
     * Sets the background color for the text.
     *
     * @param color the background color
     */
    public void setBackgroundColor(AnsiColor color) {
        this.backgroundColor = color;
        updateTextStyle();
    }

    /**
     * Sets the text formatting.
     *
     * @param formats the formatting options
     */
    public void setFormats(AnsiFormat... formats) {
        this.formats = formats != null ? formats : new AnsiFormat[0];
        updateTextStyle();
    }

    /**
     * Sets bold formatting.
     *
     * @param bold whether text should be bold
     */
    public void setBold(boolean bold) {
        if (bold) {
            setFormats(AnsiFormat.BOLD);
        } else {
            setFormats(); // Clear formats
        }
    }

    /**
     * Sets italic formatting.
     *
     * @param italic whether text should be italic
     */
    public void setItalic(boolean italic) {
        if (italic) {
            setFormats(AnsiFormat.ITALIC);
        } else {
            setFormats(); // Clear formats
        }
    }

    /**
     * Sets underline formatting.
     *
     * @param underline whether text should be underlined
     */
    public void setUnderline(boolean underline) {
        if (underline) {
            setFormats(AnsiFormat.UNDERLINE);
        } else {
            setFormats(); // Clear formats
        }
    }

    /**
     * Updates the JLine AttributedStyle based on current color and format settings.
     */
    private void updateTextStyle() {
        AttributedStyle style = AttributedStyle.DEFAULT;

        if (foregroundColor != null) {
            // Convert AnsiColor to JLine color constants
            style = style.foreground(getJLineColor(foregroundColor, true));
        }

        if (backgroundColor != null) {
            style = style.background(getJLineColor(backgroundColor, false));
        }

        for (AnsiFormat format : formats) {
            style = switch (format) {
                case BOLD -> style.bold();
                case ITALIC -> style.italic();
                case UNDERLINE -> style.underline();
                case DIM -> style.faint();
                case REVERSE -> style.inverse();
                default -> style;
            };
        }

        this.textStyle = style;
    }

    /**
     * Converts AnsiColor to JLine color constants.
     */
    private int getJLineColor(AnsiColor color, boolean foreground) {
        return switch (color) {
            case BLACK -> foreground ? AttributedStyle.BLACK : AttributedStyle.BLACK;
            case RED -> foreground ? AttributedStyle.RED : AttributedStyle.RED;
            case GREEN -> foreground ? AttributedStyle.GREEN : AttributedStyle.GREEN;
            case YELLOW -> foreground ? AttributedStyle.YELLOW : AttributedStyle.YELLOW;
            case BLUE -> foreground ? AttributedStyle.BLUE : AttributedStyle.BLUE;
            case MAGENTA -> foreground ? AttributedStyle.MAGENTA : AttributedStyle.MAGENTA;
            case CYAN -> foreground ? AttributedStyle.CYAN : AttributedStyle.CYAN;
            case WHITE -> foreground ? AttributedStyle.WHITE : AttributedStyle.WHITE;
            case BRIGHT_BLACK -> foreground ? (AttributedStyle.BRIGHT | AttributedStyle.BLACK) : (AttributedStyle.BRIGHT | AttributedStyle.BLACK);
            case BRIGHT_RED -> foreground ? (AttributedStyle.BRIGHT | AttributedStyle.RED) : (AttributedStyle.BRIGHT | AttributedStyle.RED);
            case BRIGHT_GREEN -> foreground ? (AttributedStyle.BRIGHT | AttributedStyle.GREEN) : (AttributedStyle.BRIGHT | AttributedStyle.GREEN);
            case BRIGHT_YELLOW -> foreground ? (AttributedStyle.BRIGHT | AttributedStyle.YELLOW) : (AttributedStyle.BRIGHT | AttributedStyle.YELLOW);
            case BRIGHT_BLUE -> foreground ? (AttributedStyle.BRIGHT | AttributedStyle.BLUE) : (AttributedStyle.BRIGHT | AttributedStyle.BLUE);
            case BRIGHT_MAGENTA -> foreground ? (AttributedStyle.BRIGHT | AttributedStyle.MAGENTA) : (AttributedStyle.BRIGHT | AttributedStyle.MAGENTA);
            case BRIGHT_CYAN -> foreground ? (AttributedStyle.BRIGHT | AttributedStyle.CYAN) : (AttributedStyle.BRIGHT | AttributedStyle.CYAN);
            case BRIGHT_WHITE -> foreground ? (AttributedStyle.BRIGHT | AttributedStyle.WHITE) : (AttributedStyle.BRIGHT | AttributedStyle.WHITE);
            default -> foreground ? AttributedStyle.WHITE : AttributedStyle.BLACK;
        };
    }

    /**
     * Splits the text into lines based on the canvas width and word wrap settings.
     *
     * @return array of text lines
     */
    private String[] prepareTextLines() {
        if (text == null || text.isEmpty()) {
            return new String[0];
        }

        // First split by explicit line breaks
        String[] initialLines = text.split(lineBreak, -1);

        if (!wordWrap) {
            return initialLines;
        }

        // Apply word wrapping
        java.util.List<String> wrappedLines = new java.util.ArrayList<>();

        for (String line : initialLines) {
            if (line.length() <= getWidth()) {
                wrappedLines.add(line);
            } else {
                // Word wrap this line
                String[] words = line.split(" ");
                StringBuilder currentLine = new StringBuilder();

                for (String word : words) {
                    if (currentLine.length() + word.length() + 1 <= getWidth()) {
                        if (currentLine.length() > 0) {
                            currentLine.append(" ");
                        }
                        currentLine.append(word);
                    } else {
                        if (currentLine.length() > 0) {
                            wrappedLines.add(currentLine.toString());
                            currentLine = new StringBuilder(word);
                        } else {
                            // Word is longer than width, truncate it
                            wrappedLines.add(word.substring(0, Math.min(word.length(), getWidth())));
                        }
                    }
                }

                if (currentLine.length() > 0) {
                    wrappedLines.add(currentLine.toString());
                }
            }
        }

        return wrappedLines.toArray(new String[0]);
    }

    /**
     * Applies alignment to a text line.
     *
     * @param line the text line
     * @return the aligned text line with proper spacing
     */
    private String alignLine(String line) {
        if (line.length() >= getWidth()) {
            return line.substring(0, getWidth());
        }

        return switch (alignment) {
            case LEFT -> line;
            case CENTER -> {
                int padding = (getWidth() - line.length()) / 2;
                yield " ".repeat(padding) + line;
            }
            case RIGHT -> {
                int padding = getWidth() - line.length();
                yield " ".repeat(padding) + line;
            }
        };
    }

    @Override
    public void paint(Graphics graphics) {
        String[] lines = prepareTextLines();

        // Apply legacy graphics styling if available
        if (foregroundColor != null) {
            graphics.setForegroundColor(foregroundColor);
        }
        if (backgroundColor != null) {
            graphics.setBackgroundColor(backgroundColor);
        }
        if (formats.length > 0) {
            graphics.setFormats(formats);
        }

        // Draw each line with proper alignment
        for (int i = 0; i < lines.length && i < getHeight(); i++) {
            String alignedLine = alignLine(lines[i]);
            graphics.drawString(getX(), getY() + i, alignedLine);
        }
    }

    @Override
    public void paint(JLineGraphics graphics) {
        String[] lines = prepareTextLines();

        // Apply JLine styling if available
        if (textStyle != null) {
            graphics.setStyle(textStyle);
        }

        // Draw each line with proper alignment
        for (int i = 0; i < lines.length && i < getHeight(); i++) {
            String alignedLine = alignLine(lines[i]);
            graphics.drawString(getX(), getY() + i, alignedLine);
        }
    }
}
