package com.consolemaster.raycasting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A texture provider registry that manages multiple texture providers and caches lookups.
 * The registry searches through providers in order and caches which provider can provide each texture.
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
    public Texture getTexture(String path) {
        if (path == null) {
            return null;
        }

        // Check cache first
        TextureProvider cachedProvider = cache.get(path);
        if (cachedProvider != null) {
            Texture texture = cachedProvider.getTexture(path);
            if (texture != null) {
                return texture;
            } else {
                // Cached provider no longer has this texture, remove from cache
                cache.remove(path);
            }
        }

        // Search through providers
        for (TextureProvider provider : providers) {
            Texture texture = provider.getTexture(path);
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
     * This method uses the cache and will update it if needed.
     *
     * @param path the texture path to check
     * @return true if the texture is available
     */
    public boolean hasTexture(String path) {
        return getTexture(path) != null;
    }
}
