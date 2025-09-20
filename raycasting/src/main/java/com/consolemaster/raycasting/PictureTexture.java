package com.consolemaster.raycasting;

import com.consolemaster.AnsiColor;
import com.consolemaster.StyledChar;

/**
 * A texture implementation that uses string arrays as source data.
 * The texture is scaled to fit the requested dimensions and uses EntryInfo for coloring.
 */
public class PictureTexture implements Texture {

    private final String[] textureData;
    private final String name;

    /**
     * Creates a new picture texture from string array data.
     *
     * @param name the name of this texture
     * @param textureData array of strings representing the texture pattern
     */
    public PictureTexture(String name, String[] textureData) {
        this.name = name;
        this.textureData = textureData != null ? textureData.clone() : new String[]{"#"};
    }

    @Override
    public StyledChar[][] picture(int width, int height, EntryInfo entry, boolean light) {
        if (width <= 0 || height <= 0) {
            return new StyledChar[0][0];
        }

        StyledChar[][] result = new StyledChar[height][width];

        // Get source dimensions
        int sourceHeight = textureData.length;
        int sourceWidth = sourceHeight > 0 ? textureData[0].length() : 1;

        // Ensure we have valid source dimensions
        if (sourceHeight == 0 || sourceWidth == 0) {
            // Fill with default character
            char defaultChar = entry != null ? entry.getCharacter() : '#';
            AnsiColor defaultColor = entry != null ? entry.getColor(light) : AnsiColor.WHITE;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    result[y][x] = new StyledChar(defaultChar, defaultColor);
                }
            }
            return result;
        }

        // Get colors from entry
        AnsiColor textureColor = entry != null ? entry.getColor(!light) : AnsiColor.WHITE;
        if (textureColor == null) {
            textureColor = AnsiColor.WHITE;
        }

        // Scale texture to fit dimensions
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Map target coordinates to source coordinates
                int sourceY = (y * sourceHeight) / height;
                int sourceX = (x * sourceWidth) / width;

                // Ensure we don't go out of bounds
                sourceY = Math.min(sourceY, sourceHeight - 1);
                sourceX = Math.min(sourceX, Math.min(sourceWidth - 1, textureData[sourceY].length() - 1));

                // Get character from source
                char textureChar;
                if (sourceY >= 0 && sourceY < textureData.length &&
                    sourceX >= 0 && sourceX < textureData[sourceY].length()) {
                    textureChar = textureData[sourceY].charAt(sourceX);
                } else {
                    textureChar = entry != null ? entry.getCharacter() : '#';
                }

                // Create styled character
                result[y][x] = new StyledChar(textureChar, textureColor);
            }
        }

        return result;
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
}
