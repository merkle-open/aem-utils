package com.merkle.oss.aem.utils.link;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.merkle.oss.aem.utils.link.constants.Links;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
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
 * Unit tests for the {@link LinkUtil} class.
 */
@ExtendWith(MockitoExtension.class)
public class LinkUtilTest {

    private static final String INTERNAL_LINK_NO_EXTENSION = "/content/domain/ch/de/home";
    private static final String INTERNAL_LINK = "/content/domain/ch/de/home.html";
    private static final String INTERNAL_LINK_NO_SLASH = "content/domain/ch/de/home.html";
    private static final String EXTERNAL_LINK_UNSECURE = "https://www.domain.com/content/domain/ch/de/home.html";
    private static final String EXTERNAL_LINK_GENERIC = "//www.domain.com/content/domain/ch/de/home.html";
    private static final String EXTERNAL_LINK = "https://www.domain.com";
    private static final String ABSOLUTE_LINK = "https://www.domain.com/content/domain/ch/de/home.html";
    private static final String DAM_LINK = "/content/dam/images/image.jpg";
    private static final String APPLICATION_LINK = "mailto:max.mustermann@mustermail.com";

    @Mock
    private ResourceResolver resourceResolver;
    @Mock
    private Page page;
    @Mock
    private Resource resource;
    @Mock
    private PageManager pageManager;

    /**
     * <p>Method under test: {@link LinkUtil}
     */
    @Test
    void instantiationException() throws NoSuchMethodException {
        final Constructor<LinkUtil> constructor = LinkUtil.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertEquals(AssertionError.class, exception.getCause().getClass());
        assertTrue(exception.getCause().getMessage().contains("not meant to be instantiated"));
    }

    /**
     * <p>Method under test: {@link LinkUtil#createLink(Page)}
     */
    @Test
    void createLink_page() {
        when(page.isValid()).thenReturn(false);
        assertNull(LinkUtil.createLink(page));

        when(page.isValid()).thenReturn(true);
        when(page.adaptTo(Resource.class)).thenReturn(resource);
        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        when(page.getPath()).thenReturn(INTERNAL_LINK);
        assertEquals(INTERNAL_LINK, LinkUtil.createLink(page));
    }

    /**
     * Method under test: {@link LinkUtil#createLink(String, ResourceResolver)}
     */
    @Test
    void createLink_path() {
        assertNull(LinkUtil.createLink(StringUtils.EMPTY, resourceResolver));
        assertThrows(NullPointerException.class, () -> LinkUtil.createLink(INTERNAL_LINK, null));
        assertThrows(NullPointerException.class, () -> LinkUtil.createLink(null, resourceResolver));

        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(null);
        assertNull(LinkUtil.createLink(INTERNAL_LINK, resourceResolver));

        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getPage(INTERNAL_LINK)).thenReturn(page);
        when(page.isValid()).thenReturn(false);
        assertNull(LinkUtil.createLink(INTERNAL_LINK, resourceResolver));

        when(pageManager.getPage(INTERNAL_LINK_NO_EXTENSION)).thenReturn(null);
        when(page.isValid()).thenReturn(false);
        assertEquals(INTERNAL_LINK, LinkUtil.createLink(INTERNAL_LINK_NO_EXTENSION, resourceResolver));

        when(pageManager.getPage(INTERNAL_LINK)).thenReturn(page);
        when(pageManager.getPage(ABSOLUTE_LINK)).thenReturn(null);
        when(pageManager.getPage(APPLICATION_LINK)).thenReturn(null);
        when(page.isValid()).thenReturn(true);
        assertEquals(INTERNAL_LINK, LinkUtil.createLink(INTERNAL_LINK, resourceResolver));
        assertEquals(INTERNAL_LINK, LinkUtil.createLink(INTERNAL_LINK_NO_SLASH, resourceResolver));
        assertEquals(ABSOLUTE_LINK, LinkUtil.createLink(ABSOLUTE_LINK, resourceResolver));
        assertEquals(APPLICATION_LINK, LinkUtil.createLink(APPLICATION_LINK, resourceResolver));
    }

    /**
     * Method under test: {@link LinkUtil#isValidInternalLink(String, ResourceResolver)}
     */
    @Test
    void isValidInternalLink() {
        assertFalse(LinkUtil.isValidInternalLink(StringUtils.EMPTY, resourceResolver));
        assertThrows(NullPointerException.class, () -> LinkUtil.isValidInternalLink(INTERNAL_LINK, null));

        when(this.resourceResolver.adaptTo(PageManager.class)).thenReturn(null);
        assertFalse(LinkUtil.isValidInternalLink(INTERNAL_LINK, resourceResolver));

        when(this.resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(this.pageManager.getPage(INTERNAL_LINK)).thenReturn(null);
        assertFalse(LinkUtil.isValidInternalLink(INTERNAL_LINK, resourceResolver));
        when(this.pageManager.getPage(INTERNAL_LINK)).thenReturn(page);
        when(this.page.isValid()).thenReturn(false);
        assertFalse(LinkUtil.isValidInternalLink(INTERNAL_LINK, resourceResolver));

        when(this.page.isValid()).thenReturn(true);
        assertTrue(LinkUtil.isValidInternalLink(INTERNAL_LINK, resourceResolver));
    }

    /**
     * Method under test: {@link LinkUtil#getRelativeFromAbsolutePath(String)}
     */
    @Test
    void getRelativeFromAbsolutePath() {
        assertThrows(NullPointerException.class, () -> LinkUtil.getRelativeFromAbsolutePath(null));
        assertEquals(INTERNAL_LINK, LinkUtil.getRelativeFromAbsolutePath(ABSOLUTE_LINK));
        assertEquals(INTERNAL_LINK, LinkUtil.getRelativeFromAbsolutePath(EXTERNAL_LINK_UNSECURE));
        assertEquals(INTERNAL_LINK, LinkUtil.getRelativeFromAbsolutePath(EXTERNAL_LINK_GENERIC));
        assertEquals(INTERNAL_LINK, LinkUtil.getRelativeFromAbsolutePath(INTERNAL_LINK));
    }

    /**
     * Method under test: {@link LinkUtil#appendHtmlExtensionIfMissing(String)}
     */
    @Test
    public void appendHtmlExtensionIfMissing() {
        assertThrows(NullPointerException.class, () -> LinkUtil.appendHtmlExtensionIfMissing(null));
        assertEquals(INTERNAL_LINK, LinkUtil.appendHtmlExtensionIfMissing(INTERNAL_LINK));
        assertEquals(INTERNAL_LINK, LinkUtil.appendHtmlExtensionIfMissing(INTERNAL_LINK));
    }

    /**
     * Method under test: {@link LinkUtil#isMissingHtmlExtension(String)}
     */
    @Test
    public void isMissingHtmlExtension() {
        assertThrows(NullPointerException.class, () -> LinkUtil.isMissingHtmlExtension(null));

        assertFalse(LinkUtil.isMissingHtmlExtension(""));
        assertFalse(LinkUtil.isMissingHtmlExtension("something/invalid"));
        assertFalse(LinkUtil.isMissingHtmlExtension("/content/dam/some/asset"));
        assertFalse(LinkUtil.isMissingHtmlExtension("/content/dam/some/asset.json"));
        assertFalse(LinkUtil.isMissingHtmlExtension("https://www.domain.com/is/absolute"));
        assertFalse(LinkUtil.isMissingHtmlExtension("https://www.domain.com/is/absolute.html"));
        assertFalse(LinkUtil.isMissingHtmlExtension("/internal/not/to/be/extended.html"));
        assertFalse(LinkUtil.isMissingHtmlExtension("/internal/with/selector.test.test2.html"));
        assertFalse(LinkUtil.isMissingHtmlExtension("/internal/not/to/be/extended.html?test=testValue"));
        assertFalse(LinkUtil.isMissingHtmlExtension("/internal/not/to/be/extended.html?test=testValue&b=b"));
        assertFalse(LinkUtil.isMissingHtmlExtension("/internal/not/to/be/extended.html#test"));

        assertTrue(LinkUtil.isMissingHtmlExtension("/internal/not/to/be/extended"));
        assertTrue(LinkUtil.isMissingHtmlExtension("/internal/not/to/be/extended.test"));
    }

    /**
     * Method under test: {@link LinkUtil#getTarget(boolean)}
     */
    @Test
    void getTarget() {
        assertEquals(Links.Target.SELF.getTarget(), LinkUtil.getTarget(false));
        assertEquals(Links.Target.BLANK.getTarget(), LinkUtil.getTarget(true));
    }

    /**
     * Method under test: {@link LinkUtil#isInternalLink(String)} 
     */
    @Test
    public void isInternalLink() {
        assertTrue(LinkUtil.isInternalLink(INTERNAL_LINK));
        assertFalse(LinkUtil.isInternalLink(EXTERNAL_LINK));
    }

    /**
     * Method under test: {@link LinkUtil#isLinkToDAM(String)} 
     */
    @Test
    public void isLinkToDAM() {
        assertFalse(LinkUtil.isLinkToDAM(null));
        assertFalse(LinkUtil.isLinkToDAM(StringUtils.EMPTY));
        assertFalse(LinkUtil.isLinkToDAM(INTERNAL_LINK));
        assertTrue(LinkUtil.isLinkToDAM(DAM_LINK));
    }

    /**
     * Method under test: {@link LinkUtil#isApplicationLink(String)} 
     */
    @Test
    void isApplicationLink_isTrue() {
        assertTrue(LinkUtil.isApplicationLink("sip:test@namics.com"));
        assertTrue(LinkUtil.isApplicationLink("mailto:test@namics.com"));
        assertTrue(LinkUtil.isApplicationLink("tel:0041123456789"));
    }

}
