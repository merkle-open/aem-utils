package com.namics.oss.aem.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LinkUtilTest {

    private static final String INTERNAL_LINK = "/content/namics/de/home";
    private static final String EXTERNAL_LINK = "https://www.namics.com";
    private static final String ABSOLUTE_LINK = "https://www.namics.com/content/namics/de/home.html";
    private static final String RELATIVE_LINK = "/content/namics/de/home.html";
    private static final String DAM_LINK = "/content/dam/images/image.jpg";

    @Test
    public void isInternalLink() {
        Assert.assertTrue(LinkUtil.isInternalLink(INTERNAL_LINK));
        Assert.assertFalse(LinkUtil.isInternalLink(EXTERNAL_LINK));
    }

    @Test
    public void getRelativePathFromAbsoluteInternalLink() {
        assertThat(LinkUtil.getRelativePathFromAbsoluteInternalLink(ABSOLUTE_LINK)).isEqualTo(RELATIVE_LINK);
        assertThat(LinkUtil.getRelativePathFromAbsoluteInternalLink(RELATIVE_LINK)).isEqualTo(RELATIVE_LINK);
    }

    @Test
    public void appendHtmlExtensionIfMissing() {
        assertThat(LinkUtil.appendHtmlExtensionIfMissing(INTERNAL_LINK)).isEqualTo(RELATIVE_LINK);
        assertThat(LinkUtil.appendHtmlExtensionIfMissing(RELATIVE_LINK)).isEqualTo(RELATIVE_LINK);
    }

    @Test
    public void isMappedPathMissingHtmlExtension() {
        Assert.assertFalse(LinkUtil.isMappedPathMissingHtmlExtension(null));
        Assert.assertFalse(LinkUtil.isMappedPathMissingHtmlExtension(StringUtils.EMPTY));
        Assert.assertFalse(LinkUtil.isMappedPathMissingHtmlExtension(DAM_LINK));
        Assert.assertTrue(LinkUtil.isMappedPathMissingHtmlExtension(INTERNAL_LINK));
    }

    @Test
    public void isLinkToDAM() {
        Assert.assertFalse(LinkUtil.isLinkToDAM(null));
        Assert.assertFalse(LinkUtil.isLinkToDAM(StringUtils.EMPTY));
        Assert.assertFalse(LinkUtil.isLinkToDAM(INTERNAL_LINK));
        Assert.assertTrue(LinkUtil.isLinkToDAM(DAM_LINK));
    }

}