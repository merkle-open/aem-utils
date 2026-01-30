package com.merkle.oss.aem.utils.link;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link LinkExternalizerUtil} class.
 */
@ExtendWith(MockitoExtension.class)
class LinkExternalizerUtilTest {

    private static final String HTTPS_SCHEME = "https";

    private static final String SERVER_NAME = "www.domain.ch";

    private static final String BASE_URL = HTTPS_SCHEME + "://" + SERVER_NAME;

    private static final String FULL_PATH = "/content/tenant/ch/de/home";

    private static final String MAPPED_PATH = "/de/home";

    private static final String HTML_EXTENSION = ".html";

    private static final String ANCHOR_EXTENSION = "#someAnchor";

    private static final String QUERY_EXTENSION = "?param=value";

    private static final String EXTERNAL_LINK_HTTPS = "https://www.google.ch";

    private static final String EXTERNAL_LINK_HTTP = "http://www.google.ch";

    private static final String RICH_TEXT = "<p>follow <a href=\"/content/tenant/ch/de/home" + HTML_EXTENSION + "\">here</a>. External <a href=\"" + EXTERNAL_LINK_HTTPS + "\">link</a></p>";

    private static final String RICH_TEXT_EXTERNALIZED = "<p>follow <a href=\"" + BASE_URL + MAPPED_PATH + HTML_EXTENSION + "\">here</a>. External <a href=\"" + EXTERNAL_LINK_HTTPS + "\">link</a></p>";

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private Externalizer externalizer;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Page page;

    /**
     * Method under test: {@link LinkExternalizerUtil#externalize(Page, SlingHttpServletRequest)}
     */
    @Test
    void externalize_page_null() {
        final Page nullPage = null;
        assertThrows(NullPointerException.class, () -> LinkExternalizerUtil.externalize(nullPage, request));
        assertThrows(NullPointerException.class, () -> LinkExternalizerUtil.externalize(page, null));
    }

    /**
     * Method under test: {@link LinkExternalizerUtil#externalize(Page, SlingHttpServletRequest)}
     */
    @Test
    void externalize_page_invalid() {
        when(page.isValid()).thenReturn(false);
        assertEquals((StringUtils.EMPTY), LinkExternalizerUtil.externalize(page, request));
    }

    /**
     * Method under test: {@link LinkExternalizerUtil#externalize(Page, SlingHttpServletRequest)}
     */
    @Test
    void externalize_page() {
        when(page.isValid()).thenReturn(true);
        when(page.getPath()).thenReturn(FULL_PATH);
        when(request.getScheme()).thenReturn(HTTPS_SCHEME);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Externalizer.class)).thenReturn(externalizer);
        when(externalizer.absoluteLink(request, request.getScheme(), FULL_PATH)).thenReturn(BASE_URL + MAPPED_PATH);
        assertEquals((BASE_URL + MAPPED_PATH), LinkExternalizerUtil.externalize(page, request));

        when(resourceResolver.adaptTo(Externalizer.class)).thenReturn(null);
        assertThrows(NullPointerException.class, () -> LinkExternalizerUtil.externalize(page, request));
    }

    /**
     * Method under test: {@link LinkExternalizerUtil#externalize(String, SlingHttpServletRequest)}
     */
    @Test
    void externalize_path_null() {
        final String nullPath = null;
        assertThrows(NullPointerException.class, () -> LinkExternalizerUtil.externalize(nullPath, request));
        assertThrows(NullPointerException.class, () -> LinkExternalizerUtil.externalize(FULL_PATH, null));
    }

    /**
     * Method under test: {@link LinkExternalizerUtil#externalize(String, SlingHttpServletRequest)}
     */
    @Test
    void externalize_path_external() {
        assertEquals((EXTERNAL_LINK_HTTPS), LinkExternalizerUtil.externalize(EXTERNAL_LINK_HTTPS, request));
        assertEquals((EXTERNAL_LINK_HTTP), LinkExternalizerUtil.externalize(EXTERNAL_LINK_HTTP, request));
    }

    /**
     * Method under test: {@link LinkExternalizerUtil#externalize(String, SlingHttpServletRequest)}
     */
    @Test
    void externalize_path() {
        when(request.getScheme()).thenReturn(HTTPS_SCHEME);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Externalizer.class)).thenReturn(externalizer);
        when(externalizer.absoluteLink(request, request.getScheme(), FULL_PATH)).thenReturn(BASE_URL + MAPPED_PATH);
        assertEquals((BASE_URL + MAPPED_PATH), LinkExternalizerUtil.externalize(FULL_PATH, request));
        when(externalizer.absoluteLink(request, request.getScheme(), FULL_PATH + HTML_EXTENSION + ANCHOR_EXTENSION)).thenReturn(BASE_URL + MAPPED_PATH + HTML_EXTENSION + ANCHOR_EXTENSION);
        assertEquals((BASE_URL + MAPPED_PATH + HTML_EXTENSION + ANCHOR_EXTENSION), LinkExternalizerUtil.externalize(FULL_PATH + HTML_EXTENSION + ANCHOR_EXTENSION, request));
        when(externalizer.absoluteLink(request, request.getScheme(), FULL_PATH + HTML_EXTENSION + QUERY_EXTENSION)).thenReturn(BASE_URL + MAPPED_PATH + HTML_EXTENSION + QUERY_EXTENSION);
        assertEquals((BASE_URL + MAPPED_PATH + HTML_EXTENSION + QUERY_EXTENSION), LinkExternalizerUtil.externalize(FULL_PATH + HTML_EXTENSION + QUERY_EXTENSION, request));

        when(resourceResolver.adaptTo(Externalizer.class)).thenReturn(null);
        assertThrows(NullPointerException.class, () -> LinkExternalizerUtil.externalize(FULL_PATH, request));
    }

    /**
     * Method under test: {@link LinkExternalizerUtil#externalizeRichTextLinks(String, SlingHttpServletRequest)}
     */
    @Test
    void externalizeRichTextLinks_null() {
        assertThrows(NullPointerException.class, () -> LinkExternalizerUtil.externalizeRichTextLinks("", null));
        assertThrows(NullPointerException.class, () -> LinkExternalizerUtil.externalizeRichTextLinks(null, request));
    }

    /**
     * Method under test: {@link LinkExternalizerUtil#externalizeRichTextLinks(String, SlingHttpServletRequest)}
     */
    @Test
    void externalizeRichTextLinks() {
        assertEquals((""), LinkExternalizerUtil.externalizeRichTextLinks("", request));

        when(request.getScheme()).thenReturn(HTTPS_SCHEME);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Externalizer.class)).thenReturn(externalizer);
        when(externalizer.absoluteLink(request, request.getScheme(), FULL_PATH + HTML_EXTENSION)).thenReturn(BASE_URL + MAPPED_PATH + HTML_EXTENSION);
        when(externalizer.absoluteLink(request, request.getScheme(), EXTERNAL_LINK_HTTPS)).thenReturn(EXTERNAL_LINK_HTTPS);
        assertEquals((RICH_TEXT_EXTERNALIZED), LinkExternalizerUtil.externalizeRichTextLinks(RICH_TEXT, request));
    }

}
