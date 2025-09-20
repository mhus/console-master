package com.consolemaster.raycasting;

/**
 * Interface for providing textures by path.
 * TextureProviders can load and manage texture resources.
 */
public interface TextureProvider {

    /**
     * Gets a texture by its path.
     *
     * @param path the path to the texture
     * @return the texture if found, null otherwise
     */
    Texture getTexture(String path);
}
