package com.merkle.oss.aem.utils.services.inmemorycache.memory.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.merkle.oss.aem.utils.services.inmemorycache.memory.InMemoryCacheService;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * OSGi implementation of {@link InMemoryCacheService} that manages a registry of
 * Caffeine {@link com.github.benmanes.caffeine.cache.Cache} instances.
 * <p>
 * This implementation uses a {@link HashMap} to store cache instances indexed by name.
 * Note that while the individual Caffeine caches are thread-safe, the internal map
 * is not protected by explicit synchronization, assuming cache creation occurs
 * during component activation or controlled initialization phases.
 */
@Component(service = InMemoryCacheService.class)
public class InMemoryCacheServiceImpl implements InMemoryCacheService {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryCacheServiceImpl.class);

    /**
     * Registry of active cache instances.
     */
    private final Map<String, Cache<?, ?>> caches = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public <K, V> void buildCache(@NonNull final String serviceName, final int timeToLiveSeconds, final int cacheSize, @NonNull final Class<K> keyType, @NonNull final Class<V> valueType) {
        Objects.requireNonNull(serviceName);
        Objects.requireNonNull(keyType);
        Objects.requireNonNull(valueType);

        final Cache<K, V> cache = Caffeine.newBuilder()
                .expireAfterWrite(timeToLiveSeconds, TimeUnit.SECONDS)
                .maximumSize(cacheSize)
                .build();

        caches.put(serviceName, cache);
        LOG.debug("Created cache {} with size {} and TTL {} seconds", serviceName, cacheSize, timeToLiveSeconds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanUpCache(@NonNull final String serviceName) {
        Objects.requireNonNull(serviceName);

        final Cache<?, ?> cache = caches.get(serviceName);
        if (Objects.nonNull(cache)) {
            cache.invalidateAll();
            cache.cleanUp();
        }

        caches.remove(serviceName);
        LOG.debug("Cleaned up cache {}", serviceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <K, V> void putToCache(@NonNull final String serviceName, @NonNull final K key, @NonNull final V value) {
        Objects.requireNonNull(serviceName);
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);

        final Cache<K, V> cache = getCache(serviceName);
        if (Objects.nonNull(cache)) {
            cache.put(key, value);
            LOG.debug("Added entry to cache {} for key {}", serviceName, key);
        }

        LOG.debug("Unable to add entry to cache {} for key {} - cache not found", serviceName, key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <K> boolean cacheContainsKey(@NonNull final String serviceName, @NonNull final K key) {
        Objects.requireNonNull(serviceName);
        Objects.requireNonNull(key);

        final Cache<K, ?> cache = getCache(serviceName);
        if (Objects.nonNull(cache)) {
            return cache.getIfPresent(key) != null;
        }
        LOG.debug("Unable to check entry from cache {} for key {} - cache not found", serviceName, key);
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable <K, V> V getFromCache(@NonNull final String serviceName, @NonNull final K key) {
        Objects.requireNonNull(serviceName);
        Objects.requireNonNull(key);

        final Cache<K, V> cache = getCache(serviceName);
        if (Objects.nonNull(cache)) {
            final V value = cache.getIfPresent(key);
            if (Objects.isNull(value)) {
                LOG.debug("Empty value result found in cache {} for key {}", serviceName, key);
            }
            return value;
        }

        LOG.debug("Unable to get entry from cache {} for key {} - cache not found", serviceName, key);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <K> void removeFromCache(@NonNull final String serviceName, @NonNull final K key) {
        Objects.requireNonNull(serviceName);
        Objects.requireNonNull(key);

        final Cache<K, ?> cache = getCache(serviceName);
        if (Objects.nonNull(cache)) {
            cache.invalidate(key);
        }

        LOG.debug("Unable to remove entry from cache {} for key {} - cache not found", serviceName, key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllFromCache(@NonNull final String serviceName) {
        Objects.requireNonNull(serviceName);

        final Cache<?, ?> cache = getCache(serviceName);
        if (Objects.nonNull(cache)) {
            cache.invalidateAll();
            LOG.debug("Removed all entries from cache {}", serviceName);
        }

        LOG.debug("Unable to remove all entries from cache {} - cache not found", serviceName);
    }

    /**
     * Internal helper to retrieve a typed cache from the registry.
     *
     * @param <K>         The key type.
     * @param <V>         The value type.
     * @param serviceName The name of the cache.
     * @return The requested cache instance, or {@code null} if not found.
     * @throws ClassCastException if the cache exists but was initialized with different types than requested.
     */
    private <K, V> @Nullable Cache<K, V> getCache(@NonNull final String serviceName) {
        // Unchecked cast is necessary because the map stores heterogeneous caches.
        @SuppressWarnings("unchecked") final Cache<K, V> cache = (Cache<K, V>) caches.get(serviceName);
        return cache;
    }

}
