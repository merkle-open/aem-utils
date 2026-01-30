package com.merkle.oss.aem.utils.wcm;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link PageManagerUtil} class.
 */
@ExtendWith(MockitoExtension.class)
class PageManagerUtilTest {

    private static final String PATH = "/content/resource/path/value";

    @Mock
    private Page page;
    @Mock
    private Resource resource;
    @Mock
    private ResourceResolver resourceResolver;
    @Mock
    private PageManager pageManager;
    @Mock
    private SlingHttpServletRequest request;

    /**
     * Method under test: {@link PageManagerUtil#pageManagerOf(SlingHttpServletRequest)}
     */
    @Test
    void pageManagerOf_nullRequest() {
        final SlingHttpServletRequest nullRequest = null;
        assertThrows(NullPointerException.class, () -> PageManagerUtil.pageManagerOf(nullRequest));
    }

    /**
     * Method under test: {@link PageManagerUtil#pageManagerOf(SlingHttpServletRequest)}
     */
    @Test
    void pageManagerOf_request() {
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        assertEquals(pageManager, PageManagerUtil.pageManagerOf(request));
    }

    /**
     * Method under test: {@link PageManagerUtil#pageManagerOf(Resource)}
     */
    @Test
    void pageManagerOf_nullResource() {
        final Resource nullResource = null;
        assertThrows(NullPointerException.class, () -> PageManagerUtil.pageManagerOf(nullResource));
    }

    /**
     * Method under test: {@link PageManagerUtil#pageManagerOf(Resource)}
     */
    @Test
    void pageManagerOf_resource() {
        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        assertEquals(pageManager, PageManagerUtil.pageManagerOf(resource));
    }

    /**
     * Method under test: {@link PageManagerUtil#containingPage(SlingHttpServletRequest)}
     */
    @Test
    void containingPage_request() {
        final SlingHttpServletRequest nullRequest = null;
        assertThrows(NullPointerException.class, () -> PageManagerUtil.containingPage(nullRequest));

        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(null);
        assertNull(PageManagerUtil.containingPage(request));

        when(request.getResource()).thenReturn(resource);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getContainingPage(resource)).thenReturn(null);
        assertNull(PageManagerUtil.containingPage(request));

        when(request.getResource()).thenReturn(resource);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getContainingPage(resource)).thenReturn(page);
        assertEquals(page, PageManagerUtil.containingPage(request));
    }

    /**
     * Method under test: {@link PageManagerUtil#containingPage(Resource)}
     */
    @Test
    void containingPage_resource() {
        final Resource nullResource = null;
        assertThrows(NullPointerException.class, () -> PageManagerUtil.containingPage(nullResource));

        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(null);
        assertNull(PageManagerUtil.containingPage(resource));

        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getContainingPage(resource)).thenReturn(null);
        assertNull(PageManagerUtil.containingPage(resource));

        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getContainingPage(resource)).thenReturn(page);
        assertEquals(page, PageManagerUtil.containingPage(resource));
    }

    /**
     * Method under test: {@link PageManagerUtil#containingPage(String, ResourceResolver)}
     */
    @Test
    void containingPage_path() {
        assertThrows(NullPointerException.class, () -> PageManagerUtil.containingPage(null, null));
        assertThrows(NullPointerException.class, () -> PageManagerUtil.containingPage(PATH, null));
        assertThrows(NullPointerException.class, () -> PageManagerUtil.containingPage(null, resourceResolver));
        assertNull(PageManagerUtil.containingPage(StringUtils.EMPTY, resourceResolver));

        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getContainingPage(PATH)).thenReturn(null);
        assertNull(PageManagerUtil.containingPage(PATH, resourceResolver));

        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getContainingPage(PATH)).thenReturn(page);
        assertEquals(page, PageManagerUtil.containingPage(PATH, resourceResolver));

        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(null);
        assertNull(PageManagerUtil.containingPage(PATH, resourceResolver));
    }

}
