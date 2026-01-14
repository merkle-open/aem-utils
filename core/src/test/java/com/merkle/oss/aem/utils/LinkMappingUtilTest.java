package com.merkle.oss.aem.utils;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LinkMappingUtilTest {

    private static final String TEST_URL = "https://www.domain.com";
    private static final String TEST_PATH = "/content/domain/de/home";
    private static final String HTML_EXTENSION = ".html";

    @Mock
    private ResourceResolver resourceResolver;

    @Test
    public void applyResourceMapping() {
        assertTrue(LinkMappingUtil.applyResourceMapping(Optional.empty(), resourceResolver).isEmpty());
        assertEquals(TEST_URL, LinkMappingUtil.applyResourceMapping(TEST_URL, resourceResolver).get());

        when(resourceResolver.map(TEST_PATH)).thenReturn(TEST_PATH);
        assertEquals(TEST_PATH + HTML_EXTENSION, LinkMappingUtil.applyResourceMapping(TEST_PATH, resourceResolver).get());
    }

}
