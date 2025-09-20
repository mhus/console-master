package com.consolemaster.raycasting;

import java.util.HashMap;
import java.util.Map;

/**
 * A texture provider that manages picture textures from string arrays.
 * Acts as a transformator that creates textures with specified dimensions and parameters.
 */
public class PictureTextureProvider implements TextureProvider {

    private final Map<String, String[]> textureDataMap = new HashMap<>();

    /**
     * Adds a texture to this provider.
     *
     * @param path the path/name for the texture
     * @param textureData the string array representing the texture
     */
    public void addTexture(String path, String[] textureData) {
        if (path != null && textureData != null) {
            textureDataMap.put(path, textureData.clone());
        }
    }

    @Override
    public Texture getTexture(String path, int width, int height, EntryInfo entry, boolean light) {
        String[] textureData = textureDataMap.get(path);
        if (textureData == null) {
            return null;
        }

        return new PictureTexture(path, textureData, width, height, entry, light);
    }

    /**
     * Removes a texture from this provider.
     *
     * @param path the path of the texture to remove
     * @return true if the texture was removed, false if not found
     */
    public boolean removeTexture(String path) {
        return textureDataMap.remove(path) != null;
    }

    /**
     * Checks if this provider has a texture with the given path.
     *
     * @param path the path to check
     * @return true if the texture exists
     */
    public boolean hasTexture(String path) {
        return textureDataMap.containsKey(path);
    }

    /**
     * Gets all texture paths managed by this provider.
     *
     * @return array of texture paths
     */
    public String[] getTexturePaths() {
        return textureDataMap.keySet().toArray(new String[0]);
    }

    /**
     * Clears all textures from this provider.
     */
    public void clear() {
        textureDataMap.clear();
    }
}
