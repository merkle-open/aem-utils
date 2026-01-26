package com.merkle.oss.aem.utils.services.cache.provider;

import com.merkle.oss.aem.utils.services.cache.memory.InMemoryCacheService;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * An abstract base class that facilitates the implementation of {@link CacheServiceProvider}.
 * <p>
 * This class acts as a bridge to the central {@link InMemoryCacheService}. By extending this
 * class, specific implementations can provide a simplified, type-safe API for a named cache.
 * </p>
 *
 * @param <K> The type of keys maintained by the provider.
 * @param <V> The type of mapped values.
 * @implSpec extending services must implement cache config
 * {@snippet :
 * @Component(service = ExampleCacheServiceImpl.class, immediate = true)
 * @Designate(ocd = ExampleCacheServiceImpl.ExampleCacheConfig.class)
 * public class ExampleCacheServiceImpl extends AbstractCacheProviderService<String, String> {
 *     //...
 *     @ObjectClassDefinition(name = "ExampleCacheService")
 *     public @interface ExampleCacheConfig {
 *         @AttributeDefinition(name = "Cache time to live", description = "Time to live in seconds for cached items.")
 *         int cache_ttl_seconds() default 180;
 *         @AttributeDefinition(name = "Cache size", description = "The max number of entries in the cache.")
 *         int cache_size() default 500;
 *     }
 *     //...
 * }
 *}
 * @implNote extending services should register and unregister the new cache on activate/deactivate lifecycle methods.
 * It is advised to clean up caches on deactivation.
 * {@snippet :
 * @Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
 * @Designate(ocd = ExampleScheduler.ExampleSchedulerConfig.class)
 * public class ExampleScheduler extends AbstractSlingJobScheduler {
 *     //...
 *     @Activate
 *     @Modified
 *     protected void activate(final ExampleCacheConfig config) {
 *         //build cache via inMemoryCacheService#buildCache
 *     }
 *     @Deactivate
 *     protected void deactivate() {
 *         inMemoryCacheService.cleanUpCache(getServiceName());
 *     }
 *     //...
 * }
 *}
 */
public abstract class AbstractCacheProviderService<K, V> implements CacheServiceProvider<K, V> {

    /**
     * @return A descriptive name for the service for unique identification.
     */
    protected abstract @NonNull String getServiceName();

    /**
     * @return The central {@link InMemoryCacheService} implementation.
     */
    protected abstract @NonNull InMemoryCacheService getInMemoryCacheService();

    @Override
    public void putToCache(@NonNull final K key, @NonNull final V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);

        getInMemoryCacheService().putToCache(getServiceName(), key, value);
    }

    @Override
    public boolean cacheContainsKey(@NonNull final K key) {
        Objects.requireNonNull(key);

        return getInMemoryCacheService().cacheContainsKey(getServiceName(), key);
    }

    @Override
    public @Nullable V getFromCache(@NonNull final K key) {
        Objects.requireNonNull(key);

        return getInMemoryCacheService().getFromCache(getServiceName(), key);
    }

    @Override
    public void removeFromCache(@NonNull final K key) {
        Objects.requireNonNull(key);

        getInMemoryCacheService().removeFromCache(getServiceName(), key);
    }

    @Override
    public void removeAllFromCache() {
        getInMemoryCacheService().removeAllFromCache(getServiceName());
    }

}
