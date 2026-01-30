package com.merkle.oss.aem.utils.sling;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.Session;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link SlingUtil} class.
 */
@ExtendWith(MockitoExtension.class)
public class SlingUtilTest {

    private static class MyConfigClass {
        // Implementation details of the config class
    }

    @Mock
    private Page page;
    @Mock
    private Resource resource;
    @Mock
    private ResourceResolver resourceResolver;
    @Mock
    private SlingHttpServletRequest request;
    @Mock
    private Session session;
    @Mock
    private ConfigurationBuilder configurationBuilder;
    @Mock
    private MyConfigClass myConfigClass;

    /**
     * Method under test: {@link SlingUtil#to(Class)}
     */
    @Test
    void to() {
        when(this.resourceResolver.adaptTo(Session.class)).thenReturn(session);
        final Session session = Optional.of(resourceResolver)
                .map(SlingUtil.to(Session.class))
                .orElse(null);

        assertEquals(session, this.session);
    }

    /**
     * Method under test: {@link SlingUtil#sessionOf(Page)}
     */
    @Test
    void sessionOf_page() {
        when(this.page.getContentResource()).thenReturn(resource);
        when(this.resource.getResourceResolver()).thenReturn(resourceResolver);
        when(this.resourceResolver.adaptTo(Session.class)).thenReturn(session);
        assertEquals(SlingUtil.sessionOf(page), session);
    }

    /**
     * Method under test: {@link SlingUtil#sessionOf(Resource)}
     */
    @Test
    void sessionOf_resource() {
        when(this.resource.getResourceResolver()).thenReturn(resourceResolver);
        when(this.resourceResolver.adaptTo(Session.class)).thenReturn(session);
        assertEquals(SlingUtil.sessionOf(resource), session);
    }

    /**
     * Method under test: {@link SlingUtil#sessionOf(SlingHttpServletRequest)}
     */
    @Test
    void sessionOf_request() {
        when(this.request.getResourceResolver()).thenReturn(resourceResolver);
        when(this.resourceResolver.adaptTo(Session.class)).thenReturn(session);
        assertEquals(SlingUtil.sessionOf(request), session);
    }

    /**
     * Method under test: {@link SlingUtil#sessionOf(ResourceResolver)}
     */
    @Test
    void sessionOf_resourceResolver() {
        when(this.resourceResolver.adaptTo(Session.class)).thenReturn(session);
        assertEquals(SlingUtil.sessionOf(resourceResolver), session);
    }

    /**
     * Method under test: {@link SlingUtil#caConfigOf(Page, Class)}
     */
    @Test
    void caConfigOf_page() {
        assertNull(SlingUtil.caConfigOf(null, MyConfigClass.class));

        when(this.page.adaptTo(ConfigurationBuilder.class)).thenReturn(null);
        assertNull(SlingUtil.caConfigOf(page, MyConfigClass.class));

        when(this.page.adaptTo(ConfigurationBuilder.class)).thenReturn(configurationBuilder);
        when(this.configurationBuilder.as(MyConfigClass.class)).thenReturn(null);
        assertNull(SlingUtil.caConfigOf(page, MyConfigClass.class));

        when(this.page.adaptTo(ConfigurationBuilder.class)).thenReturn(configurationBuilder);
        when(this.configurationBuilder.as(MyConfigClass.class)).thenReturn(myConfigClass);

        assertEquals(SlingUtil.caConfigOf(page, MyConfigClass.class), myConfigClass);
    }

}
