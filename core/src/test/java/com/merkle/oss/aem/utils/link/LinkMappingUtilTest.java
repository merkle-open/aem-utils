package com.merkle.oss.aem.utils.link;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link LinkMappingUtil} class.
 */
@ExtendWith(MockitoExtension.class)
public class LinkMappingUtilTest {

    private static final String TEST_URL = "https://www.domain.com";

    private static final String FULL_PATH = "/content/tenant/ch/de/home";

    private static final String MAPPED_PATH = "/de/home";

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private ResourceResolver resourceResolver;

    /**
     * <p>Method under test: {@link LinkMappingUtil}
     */
    @Test
    void instantiationException() throws NoSuchMethodException {
        final Constructor<LinkMappingUtil> constructor = LinkMappingUtil.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertEquals(AssertionError.class, exception.getCause().getClass());
        assertTrue(exception.getCause().getMessage().contains("not meant to be instantiated"));
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link LinkMappingUtil#applyResourceMapping(String, ResourceResolver)}
     *   <li>{@link LinkMappingUtil#applyResourceMapping(String, SlingHttpServletRequest)}
     * </ul>
     */
    @Test
    public void applyResourceMapping_null() {
        final SlingHttpServletRequest nullRequest = null;
        final ResourceResolver nullResolver = null;

        assertThrows(NullPointerException.class, () -> LinkMappingUtil.applyResourceMapping(null, request));
        assertThrows(NullPointerException.class, () -> LinkMappingUtil.applyResourceMapping(null, resourceResolver));
        assertThrows(NullPointerException.class, () -> LinkMappingUtil.applyResourceMapping(FULL_PATH, nullRequest));
        assertThrows(NullPointerException.class, () -> LinkMappingUtil.applyResourceMapping(FULL_PATH, nullResolver));
    }

    /**
     * <p>Method under test: {@link LinkMappingUtil#applyResourceMapping(String, ResourceResolver)}
     */
    @Test
    public void applyResourceMapping_resolver() {
        assertTrue(LinkMappingUtil.applyResourceMapping("", resourceResolver).isEmpty());
        assertEquals(TEST_URL, LinkMappingUtil.applyResourceMapping(TEST_URL, resourceResolver));

        when(resourceResolver.map(FULL_PATH)).thenReturn(MAPPED_PATH);
        assertEquals(MAPPED_PATH, LinkMappingUtil.applyResourceMapping(FULL_PATH, resourceResolver));
    }

    /**
     * <p>Method under test: {@link LinkMappingUtil#applyResourceMapping(String, SlingHttpServletRequest)}
     */
    @Test
    public void applyResourceMapping_request() {
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        assertTrue(LinkMappingUtil.applyResourceMapping("", request).isEmpty());
        assertEquals(TEST_URL, LinkMappingUtil.applyResourceMapping(TEST_URL, request));

        when(resourceResolver.map(request, FULL_PATH)).thenReturn(MAPPED_PATH);
        assertEquals(MAPPED_PATH, LinkMappingUtil.applyResourceMapping(FULL_PATH, request));
    }

}
