package com.consolemaster.raycasting;

/**
 * Interface for providing textures by transforming coordinates directly to texture data.
 * TextureProviders act as pure transformators that map incoming coordinates to the original image.
 */
public interface TextureProvider {

    /**
     * Gets a texture by its path with specified dimensions and rendering parameters.
     * The texture is configured for the given dimensions and EntryInfo properties.
     *
     * @param path the path to the texture
     * @param width the desired width of the texture
     * @param height the desired height of the texture
     * @param entry the entry info for color and lighting information
     * @param light true for light rendering (vertical walls), false for dark (horizontal walls)
     * @return the texture if found, null otherwise
     */
    Texture getTexture(String path, int width, int height, EntryInfo entry, boolean light);
}
