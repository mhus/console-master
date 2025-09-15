package com.consolemaster;

/**
 * Defines different border styles using UTF-8 Box-Drawing characters.
 * Each style provides a complete set of characters for drawing borders.
 */
public enum BorderStyle {

    /**
     * Single-line border style using thin UTF-8 Box-Drawing characters.
     * This is the default style.
     */
    SINGLE(
        '─',  // horizontal
        '│',  // vertical
        '┌',  // top-left
        '┐',  // top-right
        '└',  // bottom-left
        '┘'   // bottom-right
    ),

    /**
     * Double-line border style using thick UTF-8 Box-Drawing characters.
     * Creates a more prominent, professional appearance.
     */
    DOUBLE(
        '═',  // horizontal
        '║',  // vertical
        '╔',  // top-left
        '╗',  // top-right
        '╚',  // bottom-left
        '╝'   // bottom-right
    ),

    /**
     * Thick single-line border style.
     * Uses heavier UTF-8 Box-Drawing characters for emphasis.
     */
    THICK(
        '━',  // horizontal (heavy)
        '┃',  // vertical (heavy)
        '┏',  // top-left (heavy)
        '┓',  // top-right (heavy)
        '┗',  // bottom-left (heavy)
        '┛'   // bottom-right (heavy)
    ),

    /**
     * Rounded corner border style.
     * Uses curved corners for a softer appearance.
     */
    ROUNDED(
        '─',  // horizontal
        '│',  // vertical
        '╭',  // top-left (rounded)
        '╮',  // top-right (rounded)
        '╰',  // bottom-left (rounded)
        '╯'   // bottom-right (rounded)
    ),

    /**
     * ASCII-compatible border style using basic characters.
     * For terminals that don't support UTF-8 Box-Drawing characters.
     */
    ASCII(
        '-',  // horizontal
        '|',  // vertical
        '+',  // top-left
        '+',  // top-right
        '+',  // bottom-left
        '+'   // bottom-right
    ),

    /**
     * Dotted border style using UTF-8 characters.
     * Creates a subtle, light appearance.
     */
    DOTTED(
        '┈',  // horizontal (dotted)
        '┊',  // vertical (dotted)
        '┌',  // top-left
        '┐',  // top-right
        '└',  // bottom-left
        '┘'   // bottom-right
    );

    private final char horizontal;
    private final char vertical;
    private final char topLeft;
    private final char topRight;
    private final char bottomLeft;
    private final char bottomRight;

    /**
     * Creates a BorderStyle with the specified characters.
     *
     * @param horizontal   character for horizontal lines
     * @param vertical     character for vertical lines
     * @param topLeft      character for top-left corner
     * @param topRight     character for top-right corner
     * @param bottomLeft   character for bottom-left corner
     * @param bottomRight  character for bottom-right corner
     */
    BorderStyle(char horizontal, char vertical, char topLeft, char topRight, char bottomLeft, char bottomRight) {
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }

    /**
     * Gets the character for horizontal lines (top and bottom borders).
     *
     * @return the horizontal line character
     */
    public char getHorizontal() {
        return horizontal;
    }

    /**
     * Gets the character for vertical lines (left and right borders).
     *
     * @return the vertical line character
     */
    public char getVertical() {
        return vertical;
    }

    /**
     * Gets the character for the top-left corner.
     *
     * @return the top-left corner character
     */
    public char getTopLeft() {
        return topLeft;
    }

    /**
     * Gets the character for the top-right corner.
     *
     * @return the top-right corner character
     */
    public char getTopRight() {
        return topRight;
    }

    /**
     * Gets the character for the bottom-left corner.
     *
     * @return the bottom-left corner character
     */
    public char getBottomLeft() {
        return bottomLeft;
    }

    /**
     * Gets the character for the bottom-right corner.
     *
     * @return the bottom-right corner character
     */
    public char getBottomRight() {
        return bottomRight;
    }

    /**
     * Returns a string representation of this border style showing all characters.
     *
     * @return string representation of the border style
     */
    @Override
    public String toString() {
        return String.format("BorderStyle.%s: %c%c%c / %c %c / %c%c%c",
            name(), topLeft, horizontal, topRight, vertical, vertical, bottomLeft, horizontal, bottomRight);
    }
}
