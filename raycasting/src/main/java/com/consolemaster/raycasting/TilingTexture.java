package com.consolemaster.raycasting;

import com.consolemaster.AnsiColor;
import com.consolemaster.StyledChar;

/**
 * A texture implementation that uses string arrays as source data with tiling (repeating) behavior.
 * Instead of scaling the texture to fit the target dimensions, this texture tiles/repeats the pattern.
 */
public class TilingTexture implements Texture {

    private final String[] textureData;
    private final String name;
    private final int width;
    private final int height;
    private final EntryInfo entry;
    private final boolean light;

    /**
     * Creates a new tiling texture from string array data with specified dimensions and rendering parameters.
     *
     * @param name the name of this texture
     * @param textureData array of strings representing the texture pattern
     * @param width the target width for rendering
     * @param height the target height for rendering
     * @param entry the entry info for color and lighting information
     * @param light true for light rendering, false for dark rendering
     */
    public TilingTexture(String name, String[] textureData, int width, int height, EntryInfo entry, boolean light) {
        this.name = name;
        this.textureData = textureData != null ? textureData.clone() : new String[]{"#"};
        this.width = width;
        this.height = height;
        this.entry = entry;
        this.light = light;
    }

    @Override
    public StyledChar getCharAt(int x, int y) {
        // Check bounds
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return null;
        }

        // Get source dimensions
        int sourceHeight = textureData.length;
        int sourceWidth = sourceHeight > 0 ? textureData[0].length() : 1;

        // Handle empty texture data
        if (sourceHeight == 0 || sourceWidth == 0) {
            char defaultChar = entry != null ? entry.getCharacter() : '#';
            AnsiColor defaultColor = entry != null ? entry.getColor(!light) : AnsiColor.WHITE;
            return new StyledChar(defaultChar, defaultColor);
        }

        // Use modulo to tile/repeat the texture pattern
        int sourceY = y % sourceHeight;
        int sourceX = x % sourceWidth;

        // Ensure we don't go out of bounds after modulo operation
        sourceY = Math.max(0, Math.min(sourceY, sourceHeight - 1));
        sourceX = Math.max(0, Math.min(sourceX, Math.min(sourceWidth - 1, textureData[sourceY].length() - 1)));

        // Get character from source
        char textureChar;
        if (sourceY >= 0 && sourceY < textureData.length &&
            sourceX >= 0 && sourceX < textureData[sourceY].length()) {
            textureChar = textureData[sourceY].charAt(sourceX);
        } else {
            textureChar = entry != null ? entry.getCharacter() : '#';
        }

        // Get color from entry
        AnsiColor textureColor = entry != null ? entry.getColor(!light) : AnsiColor.WHITE;
        if (textureColor == null) {
            textureColor = AnsiColor.WHITE;
        }

        return new StyledChar(textureChar, textureColor);
    }

    /**
     * Gets the name of this texture.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the source texture data.
     */
    public String[] getTextureData() {
        return textureData.clone();
    }

    /**
     * Gets the configured width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the configured height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the source texture width (original pattern width).
     */
    public int getSourceWidth() {
        return textureData.length > 0 ? textureData[0].length() : 1;
    }

    /**
     * Gets the source texture height (original pattern height).
     */
    public int getSourceHeight() {
        return textureData.length;
    }
}
