package com.merkle.oss.aem.utils.services.inmemorycache.memory.impl;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link InMemoryCacheServiceImpl} class.
 */
@ExtendWith(MockitoExtension.class)
public class InMemoryCacheServiceImplTest {

    private static Map<String, Cache<?, ?>> getCache(final InMemoryCacheServiceImpl service) {
        try {
            Field f = service.getClass().getDeclaredField("caches");
            f.setAccessible(true);
            return (Map<String, Cache<?, ?>>) f.get(service);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method under test: {@link InMemoryCacheServiceImpl#buildCache(String, int, int, Class, Class)}
     */
    @Test
    void buildCache() {
        final InMemoryCacheServiceImpl inMemoryCacheService = new InMemoryCacheServiceImpl();

        assertThrows(NullPointerException.class, () -> inMemoryCacheService.buildCache(null, 0, 0, null, null));
        assertThrows(NullPointerException.class, () -> inMemoryCacheService.buildCache(null, 0, 0, String.class, String.class));
        assertThrows(NullPointerException.class, () -> inMemoryCacheService.buildCache("serviceName", 0, 0, String.class, null));
        assertThrows(NullPointerException.class, () -> inMemoryCacheService.buildCache("serviceName", 0, 0, null, String.class));
        assertDoesNotThrow(() -> inMemoryCacheService.buildCache("serviceName", 300, 100, String.class, String.class));

        final Map<String, Cache<?, ?>> caches = getCache(inMemoryCacheService);
        assertNotNull(caches.get("serviceName"));
    }

    /**
     * Method under test: {@link InMemoryCacheServiceImpl#cleanUpCache(String)}
     */
    @Test
    void cleanUpCache() {
        final InMemoryCacheServiceImpl inMemoryCacheService = new InMemoryCacheServiceImpl();

        assertThrows(NullPointerException.class, () -> inMemoryCacheService.cleanUpCache(null));
        assertDoesNotThrow(() -> inMemoryCacheService.cleanUpCache("serviceName"));

        inMemoryCacheService.buildCache("serviceName", 300, 100, String.class, String.class);
        final Map<String, Cache<?, ?>> caches = getCache(inMemoryCacheService);
        assertNotNull(caches.get("serviceName"));

        inMemoryCacheService.cleanUpCache("serviceName");
        assertNull(caches.get("serviceName"));
    }

    /**
     * Method under test: {@link InMemoryCacheServiceImpl#putToCache(String, Object, Object)}
     */
    @Test
    void putToCache() {
        final InMemoryCacheServiceImpl inMemoryCacheService = new InMemoryCacheServiceImpl();

        assertThrows(NullPointerException.class, () -> inMemoryCacheService.putToCache(null, null, null));
        assertThrows(NullPointerException.class, () -> inMemoryCacheService.putToCache("serviceName", null, null));
        assertThrows(NullPointerException.class, () -> inMemoryCacheService.putToCache("serviceName", "key", null));
        assertDoesNotThrow(() -> inMemoryCacheService.putToCache("serviceName", "key", "value"));

        inMemoryCacheService.buildCache("serviceName", 300, 100, String.class, String.class);
        inMemoryCacheService.putToCache("serviceName", "key", "value");
        final Map<String, Cache<?, ?>> caches = getCache(inMemoryCacheService);
        assertNotNull(caches.get("serviceName"));
        final Cache<String, String> cache = (Cache<String, String>) caches.get("serviceName");
        assertEquals("value", cache.getIfPresent("key"));
    }

    /**
     * Method under test: {@link InMemoryCacheServiceImpl#cacheContainsKey(String, Object)}
     */
    @Test
    void cacheContainsKey() {
        final InMemoryCacheServiceImpl inMemoryCacheService = new InMemoryCacheServiceImpl();

        assertThrows(NullPointerException.class, () -> inMemoryCacheService.cacheContainsKey(null, null));
        assertThrows(NullPointerException.class, () -> inMemoryCacheService.cacheContainsKey("serviceName", null));
        assertFalse(inMemoryCacheService.cacheContainsKey("serviceName", "key"));

        inMemoryCacheService.buildCache("serviceName", 300, 100, String.class, String.class);
        assertFalse(inMemoryCacheService.cacheContainsKey("serviceName", "key"));

        inMemoryCacheService.putToCache("serviceName", "key", "value");
        assertTrue(inMemoryCacheService.cacheContainsKey("serviceName", "key"));
    }

    /**
     * Method under test: {@link InMemoryCacheServiceImpl#getFromCache(String, Object)}
     */
    @Test
    void getFromCache() {
        final InMemoryCacheServiceImpl inMemoryCacheService = new InMemoryCacheServiceImpl();

        assertThrows(NullPointerException.class, () -> inMemoryCacheService.getFromCache(null, null));
        assertThrows(NullPointerException.class, () -> inMemoryCacheService.getFromCache("serviceName", null));
        assertNull(inMemoryCacheService.getFromCache("serviceName", "key"));

        inMemoryCacheService.buildCache("serviceName", 300, 100, String.class, String.class);
        assertNull(inMemoryCacheService.getFromCache("serviceName", "key"));

        inMemoryCacheService.putToCache("serviceName", "key", "value");
        assertEquals("value", inMemoryCacheService.getFromCache("serviceName", "key"));
    }

    /**
     * Method under test: {@link InMemoryCacheServiceImpl#removeFromCache(String, Object)}
     */
    @Test
    void removeFromCache() {
        final InMemoryCacheServiceImpl inMemoryCacheService = new InMemoryCacheServiceImpl();

        assertThrows(NullPointerException.class, () -> inMemoryCacheService.removeFromCache(null, null));
        assertThrows(NullPointerException.class, () -> inMemoryCacheService.removeFromCache("serviceName", null));
        assertDoesNotThrow(() -> inMemoryCacheService.removeFromCache("serviceName", "key"));

        inMemoryCacheService.buildCache("serviceName", 300, 100, String.class, String.class);
        assertDoesNotThrow(() -> inMemoryCacheService.removeFromCache("serviceName", "key"));

        inMemoryCacheService.putToCache("serviceName", "key1", "value1");
        inMemoryCacheService.putToCache("serviceName", "key2", "value2");
        inMemoryCacheService.putToCache("serviceName", "key3", "value3");
        assertEquals("value1", inMemoryCacheService.getFromCache("serviceName", "key1"));
        assertEquals("value2", inMemoryCacheService.getFromCache("serviceName", "key2"));
        assertEquals("value3", inMemoryCacheService.getFromCache("serviceName", "key3"));

        inMemoryCacheService.removeFromCache("serviceName", "key2");
        assertEquals("value1", inMemoryCacheService.getFromCache("serviceName", "key1"));
        assertNull(inMemoryCacheService.getFromCache("serviceName", "key2"));
        assertEquals("value3", inMemoryCacheService.getFromCache("serviceName", "key3"));

    }

    /**
     * Method under test: {@link InMemoryCacheServiceImpl#removeAllFromCache(String)}
     */
    @Test
    void removeAllFromCache() {
        final InMemoryCacheServiceImpl inMemoryCacheService = new InMemoryCacheServiceImpl();

        assertThrows(NullPointerException.class, () -> inMemoryCacheService.removeAllFromCache(null));
        assertDoesNotThrow(() -> inMemoryCacheService.removeAllFromCache("serviceName"));

        inMemoryCacheService.buildCache("serviceName1", 300, 100, String.class, String.class);
        inMemoryCacheService.buildCache("serviceName2", 300, 100, String.class, String.class);

        inMemoryCacheService.putToCache("serviceName1", "key1", "value1");
        inMemoryCacheService.putToCache("serviceName1", "key2", "value2");
        inMemoryCacheService.putToCache("serviceName1", "key3", "value3");
        inMemoryCacheService.putToCache("serviceName2", "key1", "value1");
        inMemoryCacheService.putToCache("serviceName2", "key2", "value2");
        inMemoryCacheService.putToCache("serviceName2", "key3", "value3");
        assertEquals("value1", inMemoryCacheService.getFromCache("serviceName1", "key1"));
        assertEquals("value2", inMemoryCacheService.getFromCache("serviceName1", "key2"));
        assertEquals("value3", inMemoryCacheService.getFromCache("serviceName1", "key3"));
        assertEquals("value1", inMemoryCacheService.getFromCache("serviceName2", "key1"));
        assertEquals("value2", inMemoryCacheService.getFromCache("serviceName2", "key2"));
        assertEquals("value3", inMemoryCacheService.getFromCache("serviceName2", "key3"));

        inMemoryCacheService.removeAllFromCache("serviceName1");
        assertNull(inMemoryCacheService.getFromCache("serviceName1", "key1"));
        assertNull(inMemoryCacheService.getFromCache("serviceName1", "key2"));
        assertNull(inMemoryCacheService.getFromCache("serviceName1", "key3"));
        assertEquals("value1", inMemoryCacheService.getFromCache("serviceName2", "key1"));
        assertEquals("value2", inMemoryCacheService.getFromCache("serviceName2", "key2"));
        assertEquals("value3", inMemoryCacheService.getFromCache("serviceName2", "key3"));

        inMemoryCacheService.removeAllFromCache("serviceName2");
        assertNull(inMemoryCacheService.getFromCache("serviceName2", "key1"));
        assertNull(inMemoryCacheService.getFromCache("serviceName2", "key2"));
        assertNull(inMemoryCacheService.getFromCache("serviceName2", "key3"));

    }

}
