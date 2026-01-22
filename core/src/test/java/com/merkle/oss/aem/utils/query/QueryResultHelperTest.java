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
     * Method under test: {@link QueryResultHelper#getResource(Hit)}
     */
    @Test
    void getResource() throws RepositoryException {
        final QueryResultHelper queryResultHelper = QueryResultHelper.create(resourceResolver);
        assertNull(queryResultHelper.getResource(null));

        when(hit.getPath()).thenReturn(PAGE_PATH);
        when(resourceResolver.getResource(PAGE_PATH)).thenReturn(null);
        assertNull(queryResultHelper.getResource(hit));

        when(resourceResolver.getResource(PAGE_PATH)).thenReturn(resource);
        assertEquals(resource, queryResultHelper.getResource(hit));

        when(hit.getPath()).thenThrow(RepositoryException.class);
        assertNull(queryResultHelper.getResource(hit));
    }

    /**
     * Method under test: {@link QueryResultHelper#adaptHitToPage(Hit)}
     */
    @Test
    void adaptHitToPage() throws RepositoryException {
        final QueryResultHelper queryResultHelper = QueryResultHelper.create(resourceResolver);

        when(hit.getPath()).thenReturn(PAGE_PATH);
        when(resourceResolver.getResource(PAGE_PATH)).thenReturn(null);
        assertNull(queryResultHelper.adaptHitToPage(hit));

        when(resourceResolver.getResource(PAGE_PATH)).thenReturn(resource);
        when(resource.adaptTo(Page.class)).thenReturn(page);
        assertEquals(page, queryResultHelper.adaptHitToPage(hit));

        when(hit.getPath()).thenThrow(RepositoryException.class);
        assertNull(queryResultHelper.adaptHitToPage(hit));
    }

}
