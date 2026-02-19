## Example usage

### AbstractInMemoryCacheProviderService

```java

import com.merkle.oss.aem.utils.services.inmemorycache.memory.InMemoryCacheService;
//other imports...

@Component(service = ExampleInMemoryCacheProviderServiceImpl.class, immediate = true)
@Designate(ocd = ExampleInMemoryCacheProviderServiceImpl.ExampleCacheConfig.class)
public class ExampleInMemoryCacheProviderServiceImpl extends AbstractInMemoryCacheProviderService<String, String> {

    @Reference
    private InMemoryCacheService inMemoryCacheService;

    @Activate
    @Modified
    protected void activate(final ExampleCacheConfig config) {
        buildCache(config.cache_ttl_seconds(), config.cache_size());
    }

    @Deactivate
    protected void deactivate() {
        cleanUpCache();
    }

    @Override
    public @NonNull String getServiceName() {
        return this.getClass().getCanonicalName();
    }

    @Override
    public @NonNull InMemoryCacheService getInMemoryCacheService() {
        return inMemoryCacheService;
    }

    @ObjectClassDefinition(name = "ExampleInMemoryCacheProviderService - Config")
    public @interface ExampleCacheConfig {

        @AttributeDefinition(name = "Cache time to live", description = "Time to live in seconds for cached items.")
        int cache_ttl_seconds() default 180;

        @AttributeDefinition(name = "Cache size", description = "The max number of entries in the cache.")
        int cache_size() default 500;

    }

}


```
