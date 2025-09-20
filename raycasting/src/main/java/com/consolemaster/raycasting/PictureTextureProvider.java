package com.consolemaster.raycasting;

import java.util.HashMap;
import java.util.Map;

/**
 * A texture provider that manages picture textures from string arrays.
 * Textures are stored in a map and can be retrieved by path.
 */
public class PictureTextureProvider implements TextureProvider {

    private final Map<String, PictureTexture> textures = new HashMap<>();

    /**
     * Adds a texture to this provider.
     *
     * @param path the path/name for the texture
     * @param textureData the string array representing the texture
     */
    public void addTexture(String path, String[] textureData) {
        if (path != null && textureData != null) {
            textures.put(path, new PictureTexture(path, textureData));
        }
    }

    /**
     * Adds a picture texture to this provider.
     *
     * @param path the path/name for the texture
     * @param texture the picture texture
     */
    public void addTexture(String path, PictureTexture texture) {
        if (path != null && texture != null) {
            textures.put(path, texture);
        }
    }

    @Override
    public Texture getTexture(String path) {
        return textures.get(path);
    }

    /**
     * Removes a texture from this provider.
     *
     * @param path the path of the texture to remove
     * @return the removed texture, or null if not found
     */
    public PictureTexture removeTexture(String path) {
        return textures.remove(path);
    }

    /**
     * Checks if this provider has a texture with the given path.
     *
     * @param path the path to check
     * @return true if the texture exists
     */
    public boolean hasTexture(String path) {
        return textures.containsKey(path);
    }

    /**
     * Gets all texture paths managed by this provider.
     *
     * @return array of texture paths
     */
    public String[] getTexturePaths() {
        return textures.keySet().toArray(new String[0]);
    }

    /**
     * Clears all textures from this provider.
     */
    public void clear() {
        textures.clear();
    }
}
