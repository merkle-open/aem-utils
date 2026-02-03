package com.merkle.oss.aem.utils.query;

import com.day.cq.search.PredicateGroup;
import com.day.cq.tagging.TagManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link QuerySearchUtil} class.
 */

@ExtendWith(MockitoExtension.class)
class QuerySearchUtilTest {

    private static final String TAG_PROPERTY_PATH = "Tag";
    private static final List<String> TAG_LIST = List.of("tag1", "tag2");

    @Mock
    private TagManager tagManager;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private QuerySearch querySearch;

    @Mock
    private PredicateGroup predicateGroup;

    /**
     * Method under test: {@link QuerySearchUtil#createTagListPredicateGroup(List, String, boolean, TagManager)}
     */
    @Test
    void createTagListPredicateGroup() {
        assertNull(QuerySearchUtil.createTagListPredicateGroup(null, null, false, null));
        assertNull(QuerySearchUtil.createTagListPredicateGroup(TAG_LIST, null, false, null));
        assertNull(QuerySearchUtil.createTagListPredicateGroup(null, TAG_PROPERTY_PATH, false, null));
        assertNull(QuerySearchUtil.createTagListPredicateGroup(null, null, false, tagManager));
        assertNull(QuerySearchUtil.createTagListPredicateGroup(TAG_LIST, TAG_PROPERTY_PATH, false, null));
        assertNull(QuerySearchUtil.createTagListPredicateGroup(TAG_LIST, null, false, tagManager));
        assertNull(QuerySearchUtil.createTagListPredicateGroup(null, TAG_PROPERTY_PATH, false, tagManager));
        assertNull(QuerySearchUtil.createTagListPredicateGroup(Collections.emptyList(), TAG_PROPERTY_PATH, false, tagManager));

        when(tagManager.resolve("tag1")).thenReturn(null);
        when(tagManager.resolve("tag2")).thenReturn(null);
        assertNotNull(QuerySearchUtil.createTagListPredicateGroup(TAG_LIST, TAG_PROPERTY_PATH, false, tagManager));
    }

    /**
     * Method under test: {@link QuerySearchUtil#escapeSearchValue(String)}
     */
    @Test
    void escapeSearchValue() {
        assertEquals("foo", QuerySearchUtil.escapeSearchValue("foo"));
        assertEquals("\\\\", QuerySearchUtil.escapeSearchValue("\\"));
        assertEquals("\\_", QuerySearchUtil.escapeSearchValue("_"));
        assertEquals("\\%", QuerySearchUtil.escapeSearchValue("%"));
    }

    /**
     * Method under test: {@link QuerySearchUtil#createTemplatePredicate(QuerySearch, String...)}
     */
    @Test
    void createTemplatePredicate() {
        when(querySearch.createPropertyPredicate(any(), any())).thenReturn(predicateGroup);

        assertNotNull(QuerySearchUtil.createTemplatePredicate(querySearch, "template"));
        assertNotNull(QuerySearchUtil.createTemplatePredicate(querySearch, "template1, template2, template3"));
    }

    /**
     * Method under test: {@link QuerySearchUtil@internalSetResourceResolverOn(ResourceResolver, Query)}
     */
    @Test
    void internalSetResourceResolverOn() throws NoSuchFieldException, IllegalAccessException {
        final QueryStub queryStub = new QueryStub();
        assertNull(queryStub.getResourceResolver());

        QuerySearchUtil.internalSetResourceResolverOn(resourceResolver, null);
        assertNull(queryStub.getResourceResolver());

        QuerySearchUtil.internalSetResourceResolverOn(resourceResolver, queryStub);
        assertNotNull(queryStub.getResourceResolver());
    }

    public static class QueryStub {

        private ResourceResolver resourceResolver;

        public ResourceResolver getResourceResolver() {
            return resourceResolver;
        }

    }

}
