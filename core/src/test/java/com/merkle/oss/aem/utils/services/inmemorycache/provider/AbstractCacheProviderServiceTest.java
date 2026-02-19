package com.merkle.oss.aem.utils.services.inmemorycache.provider;

import com.merkle.oss.aem.utils.services.inmemorycache.memory.InMemoryCacheService;
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
 * Unit tests for the {@link AbstractInMemoryCacheProviderService} class.
 */
@ExtendWith(MockitoExtension.class)
class AbstractCacheProviderServiceTest {

    @Mock
    private InMemoryCacheService inMemoryCacheService;

    @InjectMocks
    private ExampleInMemoryCacheProviderServiceImpl exampleInMemoryCacheProviderService = new ExampleInMemoryCacheProviderServiceImpl();

    /**
     * Method under test: {@link AbstractInMemoryCacheProviderService#buildCache(int, int)}
     */
    @Test
    void buildCache() {
        assertDoesNotThrow(() -> exampleInMemoryCacheProviderService.buildCache(200, 300));
    }

    /**
     * Method under test: {@link AbstractInMemoryCacheProviderService#cleanUpCache()}
     */
    @Test
    void cleanUpCache() {
        assertDoesNotThrow(() -> exampleInMemoryCacheProviderService.cleanUpCache());
    }

    /**
     * Method under test: {@link AbstractInMemoryCacheProviderService#putToCache(Object, Object)}
     */
    @Test
    void putToCache() {
        assertThrows(NullPointerException.class, () -> exampleInMemoryCacheProviderService.putToCache("key", null));
        assertThrows(NullPointerException.class, () -> exampleInMemoryCacheProviderService.putToCache(null, null));
        assertDoesNotThrow(() -> exampleInMemoryCacheProviderService.putToCache("key", "value"));
    }

    /**
     * Method under test: {@link AbstractInMemoryCacheProviderService#cacheContainsKey(Object)}
     */
    @Test
    void cacheContainsKey() {
        assertThrows(NullPointerException.class, () -> exampleInMemoryCacheProviderService.cacheContainsKey(null));
        assertDoesNotThrow(() -> exampleInMemoryCacheProviderService.cacheContainsKey("key"));
    }

    /**
     * Method under test: {@link AbstractInMemoryCacheProviderService#getFromCache(Object)}
     */
    @Test
    void getFromCache() {
        assertThrows(NullPointerException.class, () -> exampleInMemoryCacheProviderService.getFromCache(null));
        assertDoesNotThrow(() -> exampleInMemoryCacheProviderService.getFromCache("key"));
    }

    /**
     * Method under test: {@link AbstractInMemoryCacheProviderService#removeFromCache(Object)}
     */
    @Test
    void removeFromCache() {
        assertThrows(NullPointerException.class, () -> exampleInMemoryCacheProviderService.removeFromCache(null));
        assertDoesNotThrow(() -> exampleInMemoryCacheProviderService.removeFromCache("key"));
    }

    /**
     * Method under test: {@link AbstractInMemoryCacheProviderService#removeAllFromCache()}
     */
    @Test
    void removeAllFromCache() {
        assertDoesNotThrow(() -> exampleInMemoryCacheProviderService.removeAllFromCache());
    }

    private static class ExampleInMemoryCacheProviderServiceImpl extends AbstractInMemoryCacheProviderService<String, String> {

        @Reference
        private InMemoryCacheService inMemoryCacheService;

        @Override
        protected @NonNull String getServiceName() {
            return ExampleInMemoryCacheProviderServiceImpl.class.getCanonicalName();
        }

        @Override
        protected @NonNull InMemoryCacheService getInMemoryCacheService() {
            return inMemoryCacheService;
        }

    }

}
