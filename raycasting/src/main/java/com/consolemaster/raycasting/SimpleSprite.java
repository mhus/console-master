package com.consolemaster.raycasting;

import com.consolemaster.AnsiColor;

/**
 * Simple implementation of the Sprite interface using character and color arrays.
 * This implementation stores sprite data as a 2D array of characters and colors.
 */
public class SimpleSprite implements Sprite {

    private final char[][] characters;
    private final AnsiColor[][] foregroundColors;
    private final AnsiColor[][] backgroundColors;
    private final boolean[][] transparency;
    private final double scale;

    /**
     * Creates a new SimpleSprite with the specified dimensions.
     *
     * @param width sprite width in characters
     * @param height sprite height in characters
     */
    public SimpleSprite(int width, int height) {
        this(width, height, 1.0);
    }

    /**
     * Creates a new SimpleSprite with the specified dimensions and scale.
     *
     * @param width sprite width in characters
     * @param height sprite height in characters
     * @param scale scale factor for rendering
     */
    public SimpleSprite(int width, int height, double scale) {
        this.characters = new char[height][width];
        this.foregroundColors = new AnsiColor[height][width];
        this.backgroundColors = new AnsiColor[height][width];
        this.transparency = new boolean[height][width];
        this.scale = scale;

        // Initialize with spaces and transparency
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                characters[y][x] = ' ';
                transparency[y][x] = true;
            }
        }
    }

    /**
     * Creates a sprite from a string array representation.
     *
     * @param spriteData array of strings representing the sprite
     * @param foregroundColor default foreground color
     * @param backgroundColor default background color
     */
    public SimpleSprite(String[] spriteData, AnsiColor foregroundColor, AnsiColor backgroundColor) {
        this(spriteData, foregroundColor, backgroundColor, 1.0);
    }

    /**
     * Creates a sprite from a string array representation with scale.
     *
     * @param spriteData array of strings representing the sprite
     * @param foregroundColor default foreground color
     * @param backgroundColor default background color
     * @param scale scale factor for rendering
     */
    public SimpleSprite(String[] spriteData, AnsiColor foregroundColor, AnsiColor backgroundColor, double scale) {
        int height = spriteData.length;
        int width = spriteData.length > 0 ? spriteData[0].length() : 0;

        this.characters = new char[height][width];
        this.foregroundColors = new AnsiColor[height][width];
        this.backgroundColors = new AnsiColor[height][width];
        this.transparency = new boolean[height][width];
        this.scale = scale;

        // Fill from string data
        for (int y = 0; y < height; y++) {
            String row = y < spriteData.length ? spriteData[y] : "";
            for (int x = 0; x < width; x++) {
                char ch = x < row.length() ? row.charAt(x) : ' ';
                characters[y][x] = ch;
                foregroundColors[y][x] = foregroundColor;
                backgroundColors[y][x] = backgroundColor;
                transparency[y][x] = (ch == ' ');
            }
        }
    }

    @Override
    public int getWidth() {
        return characters.length > 0 ? characters[0].length : 0;
    }

    @Override
    public int getHeight() {
        return characters.length;
    }

    @Override
    public char getCharAt(int x, int y) {
        if (y >= 0 && y < characters.length && x >= 0 && x < characters[y].length) {
            return characters[y][x];
        }
        return ' ';
    }

    @Override
    public AnsiColor getForegroundColorAt(int x, int y) {
        if (y >= 0 && y < foregroundColors.length && x >= 0 && x < foregroundColors[y].length) {
            return foregroundColors[y][x];
        }
        return null;
    }

    @Override
    public AnsiColor getBackgroundColorAt(int x, int y) {
        if (y >= 0 && y < backgroundColors.length && x >= 0 && x < backgroundColors[y].length) {
            return backgroundColors[y][x];
        }
        return null;
    }

    @Override
    public boolean isTransparentAt(int x, int y) {
        if (y >= 0 && y < transparency.length && x >= 0 && x < transparency[y].length) {
            return transparency[y][x];
        }
        return true;
    }

    @Override
    public double getScale() {
        return scale;
    }

    /**
     * Sets the character at the specified position.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param character character to set
     */
    public void setCharAt(int x, int y, char character) {
        if (y >= 0 && y < characters.length && x >= 0 && x < characters[y].length) {
            characters[y][x] = character;
            transparency[y][x] = (character == ' ');
        }
    }

    /**
     * Sets the foreground color at the specified position.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param color foreground color to set
     */
    public void setForegroundColorAt(int x, int y, AnsiColor color) {
        if (y >= 0 && y < foregroundColors.length && x >= 0 && x < foregroundColors[y].length) {
            foregroundColors[y][x] = color;
        }
    }

    /**
     * Sets the background color at the specified position.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param color background color to set
     */
    public void setBackgroundColorAt(int x, int y, AnsiColor color) {
        if (y >= 0 && y < backgroundColors.length && x >= 0 && x < backgroundColors[y].length) {
            backgroundColors[y][x] = color;
        }
    }

    /**
     * Sets the transparency at the specified position.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param transparent true if position should be transparent
     */
    public void setTransparencyAt(int x, int y, boolean transparent) {
        if (y >= 0 && y < transparency.length && x >= 0 && x < transparency[y].length) {
            transparency[y][x] = transparent;
        }
    }
}
