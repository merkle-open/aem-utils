package com.merkle.oss.aem.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class LinkUtilTest {

    private static final String INTERNAL_LINK = "/content/domain/de/home";
    private static final String EXTERNAL_LINK = "https://www.domain.com";
    private static final String ABSOLUTE_LINK = "https://www.domain.com/content/domain/de/home.html";
    private static final String RELATIVE_LINK = "/content/domain/de/home.html";
    private static final String DAM_LINK = "/content/dam/images/image.jpg";

    @Test
    public void isInternalLink() {
        assertTrue(LinkUtil.isInternalLink(INTERNAL_LINK));
        assertFalse(LinkUtil.isInternalLink(EXTERNAL_LINK));
    }

    @Test
    public void getRelativePathFromAbsoluteInternalLink() {
        assertEquals(RELATIVE_LINK, LinkUtil.getRelativeFromAbsolutePath(ABSOLUTE_LINK));
        assertEquals(RELATIVE_LINK, LinkUtil.getRelativeFromAbsolutePath(RELATIVE_LINK));
    }

    @Test
    public void appendHtmlExtensionIfMissing() {
        assertEquals(RELATIVE_LINK, LinkUtil.appendHtmlExtensionIfMissing(INTERNAL_LINK));
        assertEquals(RELATIVE_LINK, LinkUtil.appendHtmlExtensionIfMissing(RELATIVE_LINK));
    }

    @Test
    public void isMappedPathMissingHtmlExtension() {
        assertFalse(LinkUtil.isMissingHtmlExtension(null));
        assertFalse(LinkUtil.isMissingHtmlExtension(StringUtils.EMPTY));
        assertFalse(LinkUtil.isMissingHtmlExtension(DAM_LINK));
        assertTrue(LinkUtil.isMissingHtmlExtension(INTERNAL_LINK));
    }

    @Test
    public void isLinkToDAM() {
        assertFalse(LinkUtil.isLinkToDAM(null));
        assertFalse(LinkUtil.isLinkToDAM(StringUtils.EMPTY));
        assertFalse(LinkUtil.isLinkToDAM(INTERNAL_LINK));
        assertTrue(LinkUtil.isLinkToDAM(DAM_LINK));
    }

}
