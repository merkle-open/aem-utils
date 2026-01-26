## Example usage

```java

package com.merkle.oss.aem.utils.services.cache.provider;

import com.merkle.oss.aem.utils.services.cache.memory.InMemoryCacheService;
//... other imports

@Component(service = ExampleCacheServiceImpl.class, immediate = true)
@Designate(ocd = ExampleCacheServiceImpl.ExampleCacheConfig.class)
public class ExampleCacheServiceImpl extends AbstractCacheProviderService<String, String> {

    @Reference
    private InMemoryCacheService inMemoryCacheService;

    @Activate
    @Modified
    protected void activate(final ExampleCacheConfig config) {
        inMemoryCacheService.buildCache(getServiceName(), config.cache_ttl_seconds(), config.cache_size(), String.class, String.class);
    }

    @Deactivate
    protected void deactivate() {
        inMemoryCacheService.cleanUpCache(getServiceName());
    }

    @Override
    public @NonNull String getServiceName() {
        return this.getClass().getName();
    }

    @Override
    public @NonNull InMemoryCacheService getInMemoryCacheService() {
        return inMemoryCacheService;
    }

    @ObjectClassDefinition(name = "ExampleCacheService")
    public @interface ExampleCacheConfig {

        @AttributeDefinition(name = "Cache time to live", description = "Time to live in seconds for cached items.")
        int cache_ttl_seconds() default 180;

        @AttributeDefinition(name = "Cache size", description = "The max number of entries in the cache.")
        int cache_size() default 500;

    }

}


```
