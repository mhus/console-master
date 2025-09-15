package com.consolemaster;

import lombok.Getter;
import lombok.Setter;

/**
 * A Canvas implementation for displaying formatted text with various alignment options.
 * Supports multi-line text, different text alignments, and styling options.
 * Now uses native TextStyle instead of JLine's AttributedStyle.
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

    // Native styling properties
    private TextStyle textStyle = TextStyle.DEFAULT;
    private AnsiColor foregroundColor;
    private AnsiColor backgroundColor;
    private AnsiFormat[] formats = new AnsiFormat[0];

    /**
     * Creates a Text canvas with specified position, size and text content.
     *
     * @param name   the name of the canvas
     * @param x      the x-coordinate
     * @param y      the y-coordinate
     * @param width  the width of the text area
     * @param height the height of the text area
     * @param text   the text content
     */
    public Text(String name, int x, int y, int width, int height, String text) {
        super(name, x, y, width, height);
        this.text = text != null ? text : "";
    }

    /**
     * Creates a Text canvas with specified position, size, text content and alignment.
     *
     * @param name      the name of the canvas
     * @param x         the x-coordinate
     * @param y         the y-coordinate
     * @param width     the width of the text area
     * @param height    the height of the text area
     * @param text      the text content
     * @param alignment the text alignment
     */
    public Text(String name, int x, int y, int width, int height, String text, Alignment alignment) {
        this(name, x, y, width, height, text);
        this.alignment = alignment != null ? alignment : Alignment.LEFT;
    }

    /**
     * Sets the foreground color.
     */
    public void setForegroundColor(AnsiColor color) {
        this.foregroundColor = color;
        updateTextStyle();
    }

    /**
     * Sets the background color.
     */
    public void setBackgroundColor(AnsiColor color) {
        this.backgroundColor = color;
        updateTextStyle();
    }

    /**
     * Sets the text formats.
     */
    public void setFormats(AnsiFormat... formats) {
        this.formats = formats != null ? formats : new AnsiFormat[0];
        updateTextStyle();
    }

    /**
     * Updates the internal text style based on current color and format settings.
     */
    private void updateTextStyle() {
        this.textStyle = new TextStyle(foregroundColor, backgroundColor, formats);
    }

    /**
     * Sets the text style directly.
     */
    public void setTextStyle(TextStyle style) {
        this.textStyle = style != null ? style : TextStyle.DEFAULT;
    }

    /**
     * Sets the text to bold (convenience method).
     */
    public void setBold(boolean bold) {
        if (bold) {
            setFormats(AnsiFormat.BOLD);
        } else {
            setFormats(); // Clear formats
        }
    }

    /**
     * Sets the text to italic (convenience method).
     */
    public void setItalic(boolean italic) {
        if (italic) {
            setFormats(AnsiFormat.ITALIC);
        } else {
            setFormats(); // Clear formats
        }
    }

    /**
     * Sets the text to underlined (convenience method).
     */
    public void setUnderlined(boolean underlined) {
        if (underlined) {
            setFormats(AnsiFormat.UNDERLINE);
        } else {
            setFormats(); // Clear formats
        }
    }

    /**
     * TODO remove these alias methods in future versions
     * Sets the text to underlined (alias for setUnderlined).
     */
    public void setUnderline(boolean underline) {
        setUnderlined(underline);
    }

    @Override
    public void paint(Graphics graphics) {
        graphics.clear();

        if (text == null || text.isEmpty()) {
            return;
        }

        // Split text into lines
        String[] lines = text.split(lineBreak);

        int startY = 0;
        int availableHeight = getHeight();

        // Process each line
        for (int lineIndex = 0; lineIndex < lines.length && startY < availableHeight; lineIndex++) {
            String line = lines[lineIndex];

            if (wordWrap && line.length() > getWidth()) {
                // Handle word wrapping
                String[] wrappedLines = wrapLine(line, getWidth());
                for (String wrappedLine : wrappedLines) {
                    if (startY >= availableHeight) break;
                    renderLine(graphics, wrappedLine, startY);
                    startY++;
                }
            } else {
                // Render line as-is (may be truncated if too long)
                renderLine(graphics, line, startY);
                startY++;
            }
        }
    }

    /**
     * Renders a single line of text with proper alignment.
     */
    private void renderLine(Graphics graphics, String line, int y) {
        if (y >= getHeight() || line.isEmpty()) {
            return;
        }

        int x = calculateXPosition(line);

        // Truncate line if it's too long for the available width
        String displayLine = line.length() > getWidth() ? line.substring(0, getWidth()) : line;

        // Apply styling and draw the text
        if (textStyle.hasFormatting()) {
            graphics.drawStyledString(x, y, displayLine, foregroundColor, backgroundColor, formats);
        } else {
            graphics.drawString(x, y, displayLine);
        }
    }

    /**
     * Calculates the x position based on alignment.
     */
    private int calculateXPosition(String line) {
        return switch (alignment) {
            case LEFT -> 0;
            case CENTER -> Math.max(0, (getWidth() - line.length()) / 2);
            case RIGHT -> Math.max(0, getWidth() - line.length());
        };
    }

    /**
     * Wraps a line into multiple lines based on available width.
     */
    private String[] wrapLine(String line, int maxWidth) {
        if (maxWidth <= 0) {
            return new String[]{""};
        }

        java.util.List<String> wrapped = new java.util.ArrayList<>();
        String remaining = line;

        while (remaining.length() > maxWidth) {
            // Find the best break point (prefer word boundaries)
            int breakPoint = findBreakPoint(remaining, maxWidth);
            wrapped.add(remaining.substring(0, breakPoint));
            remaining = remaining.substring(breakPoint).trim();
        }

        if (!remaining.isEmpty()) {
            wrapped.add(remaining);
        }

        return wrapped.toArray(new String[0]);
    }

    /**
     * Finds the best break point for line wrapping.
     */
    private int findBreakPoint(String text, int maxWidth) {
        if (text.length() <= maxWidth) {
            return text.length();
        }

        // Look for a space to break at
        for (int i = maxWidth - 1; i > 0; i--) {
            if (Character.isWhitespace(text.charAt(i))) {
                return i;
            }
        }

        // No good break point found, break at maxWidth
        return maxWidth;
    }

    /**
     * Calculates the preferred size based on text content.
     */
    @Override
    public void pack() {
        if (text == null || text.isEmpty()) {
            setWidth(0);
            setHeight(1);
            return;
        }

        String[] lines = text.split(lineBreak);
        int maxLineWidth = 0;
        int totalHeight = 0;

        for (String line : lines) {
            if (wordWrap && getWidth() > 0 && line.length() > getWidth()) {
                String[] wrappedLines = wrapLine(line, getWidth());
                totalHeight += wrappedLines.length;
                for (String wrappedLine : wrappedLines) {
                    maxLineWidth = Math.max(maxLineWidth, wrappedLine.length());
                }
            } else {
                totalHeight++;
                maxLineWidth = Math.max(maxLineWidth, line.length());
            }
        }

        if (getWidth() == 0) {
            setWidth(maxLineWidth);
        }
        if (getHeight() == 0) {
            setHeight(totalHeight);
        }

        // Update minimum size requirements
        setMinWidth(Math.max(getMinWidth(), maxLineWidth));
        setMinHeight(Math.max(getMinHeight(), totalHeight));
    }

    public boolean contains(int x, int y) {
        return super.contains(x, y);
    }
}