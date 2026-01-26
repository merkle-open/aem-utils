package com.merkle.oss.aem.utils.services.cache.provider;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Defines the public contract for a dedicated cache provider instance.
 * <p>
 * This interface abstracts the complexity of cache management, providing a clean API
 * for interacting with a specific named cache instance. It is intended to be implemented
 * by services that require dedicated caching logic for specific domain objects.
 *
 * @param <K> The type of keys maintained by this cache provider.
 * @param <V> The type of mapped values.
 */
public interface CacheServiceProvider<K, V> {

    /**
     * Associates the specified value with the specified key in the provider's cache.
     *
     * @param key   The key with which the specified value is to be associated.
     * @param value The value to be associated with the specified key.
     */
    void putToCache(@NonNull final K key, @NonNull final V value);

    /**
     * Determines if an entry exists in the cache for the specified key.
     *
     * @param key The key whose presence in the cache is to be tested.
     * @return {@code true} if a mapping exists; {@code false} otherwise.
     */
    boolean cacheContainsKey(@NonNull final K key);

    /**
     * Retrieves the cached value associated with the specified key.
     *
     * @param key The key whose associated value is to be returned.
     * @return The cached value, or {@code null} if no mapping exists or the entry has expired.
     */
    @Nullable V getFromCache(@NonNull final K key);

    /**
     * Removes the mapping for the specified key from the cache if present.
     *
     * @param key The key whose mapping is to be removed from the cache.
     */
    void removeFromCache(@NonNull final K key);

    /**
     * Invalidates all entries within this specific provider's cache.
     * <p>
     * Use this with caution as it affects all consumers of this specific cache instance.
     */
    void removeAllFromCache();

}
