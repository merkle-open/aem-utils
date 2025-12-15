package com.merkle.oss.aem.utils;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LinkMappingUtilTest {

    private static final String TEST_URL = "https://www.domain.com";
    private static final String TEST_PATH = "/content/domain/de/home";
    private static final String HTML_EXTENSION = ".html";

    @Mock
    private ResourceResolver resourceResolver;

    @Test
    public void applyResourceMapping() {
        assertThat(LinkMappingUtil.applyResourceMapping(Optional.empty(), resourceResolver)).isEmpty();
        assertThat(LinkMappingUtil.applyResourceMapping(TEST_URL, resourceResolver).get()).isEqualTo(TEST_URL);
        when(resourceResolver.map(TEST_PATH)).thenReturn(TEST_PATH);
        assertThat(LinkMappingUtil.applyResourceMapping(TEST_PATH, resourceResolver)).get().isEqualTo(TEST_PATH + HTML_EXTENSION);
    }

}
