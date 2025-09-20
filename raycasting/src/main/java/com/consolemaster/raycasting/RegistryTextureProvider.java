package com.consolemaster.raycasting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A texture provider registry that manages multiple texture providers and caches lookups.
 * The registry searches through providers in order and caches which provider can provide each texture.
 * Acts as a transformator that delegates to registered providers.
 */
public class RegistryTextureProvider implements TextureProvider {

    private final List<TextureProvider> providers = new ArrayList<>();
    private final Map<String, TextureProvider> cache = new HashMap<>();

    /**
     * Adds a texture provider to this registry.
     * Providers are searched in the order they were added.
     *
     * @param provider the texture provider to add
     */
    public void addProvider(TextureProvider provider) {
        if (provider != null && !providers.contains(provider)) {
            providers.add(provider);
        }
    }

    /**
     * Removes a texture provider from this registry.
     * Also clears any cached entries for this provider.
     *
     * @param provider the provider to remove
     * @return true if the provider was removed
     */
    public boolean removeProvider(TextureProvider provider) {
        boolean removed = providers.remove(provider);
        if (removed) {
            // Clear cache entries for this provider
            cache.entrySet().removeIf(entry -> entry.getValue() == provider);
        }
        return removed;
    }

    @Override
    public Texture getTexture(String path, int width, int height, EntryInfo entry, boolean light) {
        if (path == null) {
            return null;
        }

        // Check cache first
        TextureProvider cachedProvider = cache.get(path);
        if (cachedProvider != null) {
            Texture texture = cachedProvider.getTexture(path, width, height, entry, light);
            if (texture != null) {
                return texture;
            } else {
                // Cached provider no longer has this texture, remove from cache
                cache.remove(path);
            }
        }

        // Search through providers
        for (TextureProvider provider : providers) {
            Texture texture = provider.getTexture(path, width, height, entry, light);
            if (texture != null) {
                // Cache this provider for future lookups
                cache.put(path, provider);
                return texture;
            }
        }

        return null;
    }

    /**
     * Clears the texture cache.
     * This forces the registry to re-search providers on the next lookup.
     */
    public void clearCache() {
        cache.clear();
    }

    /**
     * Gets the number of registered providers.
     *
     * @return the number of providers
     */
    public int getProviderCount() {
        return providers.size();
    }

    /**
     * Gets all registered providers.
     *
     * @return a copy of the provider list
     */
    public List<TextureProvider> getProviders() {
        return new ArrayList<>(providers);
    }

    /**
     * Checks if a texture is available from any provider.
     * This method uses a simplified check and does not cache the result.
     *
     * @param path the texture path to check
     * @return true if the texture is available
     */
    public boolean hasTexture(String path) {
        if (path == null) {
            return false;
        }

        // Check cache first
        if (cache.containsKey(path)) {
            return true;
        }

        // Check providers (simplified check)
        for (TextureProvider provider : providers) {
            if (provider instanceof PictureTextureProvider pictureProvider) {
                if (pictureProvider.hasTexture(path)) {
                    return true;
                }
            }
            // For other provider types, we would need to try getTexture with dummy parameters
            // This is a limitation of the new interface design
        }

        return false;
    }
}
