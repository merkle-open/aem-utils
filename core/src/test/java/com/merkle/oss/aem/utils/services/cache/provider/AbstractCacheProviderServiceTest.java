package com.merkle.oss.aem.utils.services.cache.provider;

import com.merkle.oss.aem.utils.services.cache.memory.InMemoryCacheService;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.annotations.Reference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the {@link AbstractCacheProviderService} class.
 */
@ExtendWith(MockitoExtension.class)
public class AbstractCacheProviderServiceTest {

    @Mock
    private InMemoryCacheService inMemoryCacheService;

    @InjectMocks
    private ExampleCacheProviderImpl exampleCacheProviderService = new ExampleCacheProviderImpl();

    /**
     * Method under test: {@link AbstractCacheProviderService#putToCache(Object, Object)}
     */
    @Test
    void putToCache() {
        assertThrows(NullPointerException.class, () -> exampleCacheProviderService.putToCache("key", null));
        assertThrows(NullPointerException.class, () -> exampleCacheProviderService.putToCache(null, null));
        assertDoesNotThrow(() -> exampleCacheProviderService.putToCache("key", "value"));
    }

    /**
     * Method under test: {@link AbstractCacheProviderService#cacheContainsKey(Object)}
     */
    @Test
    void cacheContainsKey() {
        assertThrows(NullPointerException.class, () -> exampleCacheProviderService.cacheContainsKey(null));
        assertDoesNotThrow(() -> exampleCacheProviderService.cacheContainsKey("key"));
    }

    /**
     * Method under test: {@link AbstractCacheProviderService#getFromCache(Object)}
     */
    @Test
    void getFromCache() {
        assertThrows(NullPointerException.class, () -> exampleCacheProviderService.getFromCache(null));
        assertDoesNotThrow(() -> exampleCacheProviderService.getFromCache("key"));
    }

    /**
     * Method under test: {@link AbstractCacheProviderService#removeFromCache(Object)}
     */
    @Test
    void removeFromCache() {
        assertThrows(NullPointerException.class, () -> exampleCacheProviderService.removeFromCache(null));
        assertDoesNotThrow(() -> exampleCacheProviderService.removeFromCache("key"));
    }

    /**
     * Method under test: {@link AbstractCacheProviderService#removeAllFromCache()}
     */
    @Test
    void removeAllFromCache() {
        assertDoesNotThrow(() -> exampleCacheProviderService.removeAllFromCache());
    }

    private static class ExampleCacheProviderImpl extends AbstractCacheProviderService<String, String> {

        @Reference
        private InMemoryCacheService inMemoryCacheService;

        @Override
        protected @NonNull String getServiceName() {
            return "Example Cache Provider Service";
        }

        @Override
        protected @NonNull InMemoryCacheService getInMemoryCacheService() {
            return inMemoryCacheService;
        }


    }

}
