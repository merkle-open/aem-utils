package com.merkle.oss.aem.utils.services.inmemorycache.memory;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A centralized service providing a simplified abstraction over the Caffeine caching library.
 * <p>
 * This service manages a collection of named caches, allowing for the dynamic creation,
 * retrieval, and invalidation of independent cache instances.
 */
public interface InMemoryCacheService {

    /**
     * Initializes and registers a new Caffeine cache instance.
     *
     * @param <K>                 The type of keys maintained by the cache.
     * @param <V>                 The type of mapped values.
     * @param serviceName         A unique identifier for the cache instance.
     * @param timeToLiveInSeconds Duration after which an entry should be automatically removed after the last write.
     * @param cacheSize           The maximum number of entries the cache can hold before eviction occurs.
     * @param keyType             The class representing the key type (used for runtime type safety).
     * @param valueType           The class representing the value type (used for runtime type safety).
     */
    <K, V> void buildCache(@NonNull final String serviceName, int timeToLiveInSeconds, int cacheSize, @NonNull final Class<K> keyType, @NonNull final Class<V> valueType);

    /**
     * Invalidates all entries and removes the named cache instance from the service.
     *
     * @param serviceName The unique identifier of the cache to destroy.
     */
    void cleanUpCache(@NonNull final String serviceName);

    /**
     * Associates the specified value with the specified key in the named cache.
     * If the cache previously contained a mapping for the key, the old value is replaced.
     *
     * @param <K>         The key type.
     * @param <V>         The value type.
     * @param serviceName The name of the target cache.
     * @param key         Key with which the specified value is to be associated.
     * @param value       Value to be associated with the specified key.
     */
    <K, V> void putToCache(@NonNull final String serviceName, @NonNull final K key, @NonNull final V value);

    /**
     * Checks for the existence of a mapping for the specified key within the named cache.
     *
     * @param <K>         The key type.
     * @param serviceName The name of the target cache.
     * @param key         The key whose presence is to be tested.
     * @return {@code true} if a mapping exists; {@code false} otherwise.
     */
    <K> boolean cacheContainsKey(@NonNull final String serviceName, @NonNull final K key);

    /**
     * Returns the value to which the specified key is mapped, or {@code null} if the
     * cache contains no mapping for the key.
     *
     * @param <K>         The key type.
     * @param <V>         The value type.
     * @param serviceName The name of the target cache.
     * @param key         The key whose associated value is to be returned.
     * @return The cached value, or {@code null} if not present.
     */
    @Nullable <K, V> V getFromCache(@NonNull final String serviceName, @NonNull final K key);

    /**
     * Discards any cached value for the specified key in the named cache.
     *
     * @param <K>         The key type.
     * @param serviceName The name of the target cache.
     * @param key         The key whose mapping is to be removed from the cache.
     */
    <K> void removeFromCache(@NonNull final String serviceName, @NonNull final K key);

    /**
     * Discards all entries in the named cache but does not remove the cache instance itself.
     *
     * @param serviceName The name of the target cache.
     */
    void removeAllFromCache(@NonNull final String serviceName);

}
