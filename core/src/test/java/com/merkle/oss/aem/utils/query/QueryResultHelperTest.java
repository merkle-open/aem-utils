package com.merkle.oss.aem.utils.query;

import com.day.cq.search.result.Hit;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.RepositoryException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link QueryResultHelper} class.
 */
@ExtendWith(MockitoExtension.class)
class QueryResultHelperTest {

    private static final String PAGE_PATH = "/content/mysite/en";

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Hit hit;

    @Mock
    private Resource resource;

    @Mock
    private Page page;

    /**
     * Method under test: {@link QueryResultHelper#create(ResourceResolver)}
     */
    @Test
    void create() {
        final QueryResultHelper queryResultHelper = QueryResultHelper.create(resourceResolver);
        assertNotNull(queryResultHelper);
    }

    /**
     * Method under test: {@link QueryResultHelper#toResource(Hit)}
     */
    @Test
    void toResource() throws RepositoryException {
        final QueryResultHelper queryResultHelper = QueryResultHelper.create(resourceResolver);
        assertTrue(queryResultHelper.toResource(null).isEmpty());

        when(hit.getPath()).thenReturn(PAGE_PATH);
        when(resourceResolver.getResource(PAGE_PATH)).thenReturn(null);
        assertTrue(queryResultHelper.toResource(hit).isEmpty());

        when(resourceResolver.getResource(PAGE_PATH)).thenReturn(resource);
        assertEquals(Optional.of(resource), queryResultHelper.toResource(hit));

        when(hit.getPath()).thenThrow(RepositoryException.class);
        assertTrue(queryResultHelper.toResource(hit).isEmpty());
    }

    /**
     * Method under test: {@link QueryResultHelper#toPage(Hit)}
     */
    @Test
    void toPage() throws RepositoryException {
        final QueryResultHelper queryResultHelper = QueryResultHelper.create(resourceResolver);

        when(hit.getPath()).thenReturn(PAGE_PATH);
        when(resourceResolver.getResource(PAGE_PATH)).thenReturn(null);
        assertTrue(queryResultHelper.toPage(hit).isEmpty());

        when(resourceResolver.getResource(PAGE_PATH)).thenReturn(resource);
        when(resource.adaptTo(Page.class)).thenReturn(page);
        assertEquals(Optional.of(page), queryResultHelper.toPage(hit));

        when(hit.getPath()).thenThrow(RepositoryException.class);
        assertTrue(queryResultHelper.toPage(hit).isEmpty());
    }

}
