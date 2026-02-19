package com.merkle.oss.aem.utils.query;

import com.day.cq.search.Predicate;
import com.day.cq.search.PredicateGroup;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link QuerySearchUtil} class.
 */

@ExtendWith(MockitoExtension.class)
class QuerySearchUtilTest {

    private static final String TAG_PROPERTY_PATH = "Tag";
    private static final List<String> TAG_LIST = List.of("tag1", "tag2");

    @Mock
    private Tag tag;

    @Mock
    private TagManager tagManager;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private QuerySearch querySearch;

    /**
     * Method under test:
     * {@link QuerySearchUtil#createPropertyPredicate(String, String)}
     */
    @Test
    void testCreatePropertyPredicate() {
        final PredicateGroup actualCreatePropertyPredicateResult = QuerySearchUtil
                .createPropertyPredicate("Property Name", "42");

        assertEquals(1, actualCreatePropertyPredicateResult.size());

        final Predicate getResult = actualCreatePropertyPredicateResult.getFirst();

        assertEquals("property", getResult.getName());
        assertEquals("property", getResult.getPath());
        assertEquals("property", getResult.getType());
        assertEquals(2, getResult.getParameters().size());
    }

    /**
     * Method under test:
     * {@link QuerySearchUtil#createPropertyPredicate(String, String, String)}
     */
    @Test
    void testCreatePropertyPredicate2() {
        final PredicateGroup actualCreatePropertyPredicateResult = QuerySearchUtil
                .createPropertyPredicate("Property Name", "Operation", "42");

        assertEquals(1, actualCreatePropertyPredicateResult.size());

        final Predicate getResult = actualCreatePropertyPredicateResult.getFirst();

        assertEquals("property", getResult.getName());
        assertEquals("property", getResult.getPath());
        assertEquals("property", getResult.getType());
        assertEquals(3, getResult.getParameters().size());
    }

    /**
     * Method under test:
     * {@link QuerySearchUtil#createFullTextPredicate(String, String)}
     */
    @Test
    void testCreateFullTextPredicate() {
        final PredicateGroup actualCreateFullTextPredicateResult = QuerySearchUtil
                .createFullTextPredicate("42", "Property Name");

        assertEquals(1, actualCreateFullTextPredicateResult.size());

        final Predicate getResult = actualCreateFullTextPredicateResult.getFirst();

        assertEquals("fulltext", getResult.getName());
        assertEquals("fulltext", getResult.getPath());
        assertEquals("fulltext", getResult.getType());
        assertEquals(2, getResult.getParameters().size());
    }

    /**
     * Method under test: {@link QuerySearchUtil#createPropertyExistsPredicate(String)}
     */
    @Test
    void testCreatePropertyExistsPredicate() {
        final PredicateGroup actualCreatePropertyExistsPredicateResult = QuerySearchUtil
                .createPropertyExistsPredicate("Property Name");

        assertEquals(1, actualCreatePropertyExistsPredicateResult.size());

        final Predicate getResult = actualCreatePropertyExistsPredicateResult.getFirst();

        assertEquals("property", getResult.getName());
        assertEquals("property", getResult.getPath());
        assertEquals("property", getResult.getType());
        assertEquals(2, getResult.getParameters().size());
    }

    /**
     * Method under test:
     * {@link QuerySearchUtil#createPropertyNotExistsPredicate(String)}
     */
    @Test
    void testCreatePropertyNotExistsPredicate() {
        final PredicateGroup actualCreatePropertyNotExistsPredicateResult = QuerySearchUtil
                .createPropertyNotExistsPredicate("Property Name");

        assertEquals(1, actualCreatePropertyNotExistsPredicateResult.size());

        final Predicate getResult = actualCreatePropertyNotExistsPredicateResult.getFirst();

        assertEquals("property", getResult.getName());
        assertEquals("property", getResult.getPath());
        assertEquals("property", getResult.getType());
        assertEquals(2, getResult.getParameters().size());
    }

    /**
     * Method under test: {@link QuerySearchUtil#createTagPredicate(Tag, String)}
     */
    @Test
    void testCreateTagPredicate() {
        when(tag.getTagID()).thenReturn("tagId");
        final PredicateGroup predicateGroupTag = QuerySearchUtil.createTagPredicate(tag, "name");

        assertEquals("tagId", predicateGroupTag.getFirst().getParameters().get("tagid"));
        assertEquals("name", predicateGroupTag.getFirst().getParameters().get("property"));
    }

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
     * Method under test:
     * {@link QuerySearchUtil#createRangePropertyPredicate(String, String, String, String, String, boolean)}
     */
    @Test
    void testCreateRangePropertyPredicate() {
        final PredicateGroup actualCreateRangePropertyPredicateResult = QuerySearchUtil
                .createRangePropertyPredicate("Property Name", "Lower Bound", "Lower Operation",
                        "Upper Bound", "Upper Operation", true);

        assertEquals(1, actualCreateRangePropertyPredicateResult.size());

        final Predicate getResult = actualCreateRangePropertyPredicateResult.getFirst();

        assertEquals("rangeproperty", getResult.getName());
        assertEquals("rangeproperty", getResult.getPath());
        assertEquals("rangeproperty", getResult.getType());
        assertEquals(6, getResult.getParameters().size());
    }

    /**
     * Method under test:
     * {@link QuerySearchUtil#createRangePropertyPredicate(String, String, String, String, String, boolean)}
     */
    @Test
    void testCreateRangePropertyPredicate2() {
        final PredicateGroup actualCreateRangePropertyPredicateResult = QuerySearchUtil
                .createRangePropertyPredicate("Property Name", null, "Lower Operation",
                        null, "Upper Operation", true);

        assertEquals(1, actualCreateRangePropertyPredicateResult.size());

        final Predicate getResult = actualCreateRangePropertyPredicateResult.getFirst();

        assertEquals("rangeproperty", getResult.getName());
        assertEquals("rangeproperty", getResult.getPath());
        assertEquals("rangeproperty", getResult.getType());
        assertEquals(2, getResult.getParameters().size());
    }

    /**
     * Method under test:
     * {@link QuerySearchUtil#createRangePropertyPredicate(String, String, String, boolean)}
     */
    @Test
    void testCreateRangePropertyPredicate3() {
        final PredicateGroup actualCreateRangePropertyPredicateResult = QuerySearchUtil
                .createRangePropertyPredicate("Property Name", "Lower Bound", "Upper Bound", true);
        assertEquals(1, actualCreateRangePropertyPredicateResult.size());

        final Predicate getResult = actualCreateRangePropertyPredicateResult.getFirst();

        assertEquals("rangeproperty", getResult.getName());
        assertEquals("rangeproperty", getResult.getPath());
        assertEquals("rangeproperty", getResult.getType());
        assertEquals(6, getResult.getParameters().size());
    }

    /**
     * Method under test:
     * {@link QuerySearchUtil#createRangePropertyPredicate(String, String, String, boolean)}
     */
    @Test
    void testCreateRangePropertyPredicate4() {
        final PredicateGroup actualCreateRangePropertyPredicateResult = QuerySearchUtil
                .createRangePropertyPredicate("Property Name", null, null, true);

        assertEquals(1, actualCreateRangePropertyPredicateResult.size());

        final Predicate getResult = actualCreateRangePropertyPredicateResult.getFirst();

        assertEquals("rangeproperty", getResult.getName());
        assertEquals("rangeproperty", getResult.getPath());
        assertEquals("rangeproperty", getResult.getType());
        assertEquals(2, getResult.getParameters().size());
    }

    /**
     * Method under test:
     * {@link QuerySearchUtil#createDateRangePropertyPredicate(String, String, String, String, String)}
     */
    @Test
    void testCreateDateRangePropertyPredicate() {
        final PredicateGroup actualCreateDateRangePropertyPredicateResult = QuerySearchUtil
                .createDateRangePropertyPredicate("Property Name", "Lower Bound", "Lower Operation",
                        "Upper Bound", "Upper Operation");
        assertEquals(1, actualCreateDateRangePropertyPredicateResult.size());

        final Predicate getResult = actualCreateDateRangePropertyPredicateResult.getFirst();

        assertEquals("daterange", getResult.getName());
        assertEquals("daterange", getResult.getPath());
        assertEquals("daterange", getResult.getType());
        assertEquals(5, getResult.getParameters().size());
    }

    /**
     * Method under test:
     * {@link QuerySearchUtil#createDateRangePropertyPredicate(String, String, String, String, String)}
     */
    @Test
    void testCreateDateRangePropertyPredicate2() {
        final PredicateGroup actualCreateDateRangePropertyPredicateResult = QuerySearchUtil
                .createDateRangePropertyPredicate("Property Name", null, "Lower Operation", null, "Upper Operation");
        assertEquals(1, actualCreateDateRangePropertyPredicateResult.size());

        final Predicate getResult = actualCreateDateRangePropertyPredicateResult.getFirst();

        assertEquals("daterange", getResult.getName());
        assertEquals("daterange", getResult.getPath());
        assertEquals("daterange", getResult.getType());
        assertEquals(1, getResult.getParameters().size());
    }

    /**
     * Method under test:
     * {@link QuerySearchUtil#createRelativeDateRangePropertyPredicate(String, String, String)}
     */
    @Test
    void testCreateRelativeDateRangePropertyPredicate() {
        final PredicateGroup actualCreateRelativeDateRangePropertyPredicateResult = QuerySearchUtil
                .createRelativeDateRangePropertyPredicate("Property Name", "Lower Bound", "Upper Bound");

        assertEquals(1, actualCreateRelativeDateRangePropertyPredicateResult.size());

        final Predicate getResult = actualCreateRelativeDateRangePropertyPredicateResult.getFirst();

        assertEquals("relativedaterange", getResult.getName());
        assertEquals("relativedaterange", getResult.getPath());
        assertEquals("relativedaterange", getResult.getType());
        assertEquals(3, getResult.getParameters().size());
    }

    /**
     * Method under test:
     * {@link QuerySearchUtil#createRelativeDateRangePropertyPredicate(String, String, String)}
     */
    @Test
    void testCreateRelativeDateRangePropertyPredicate2() {
        final PredicateGroup actualCreateRelativeDateRangePropertyPredicateResult = QuerySearchUtil
                .createRelativeDateRangePropertyPredicate("Property Name", null, null);

        assertEquals(1, actualCreateRelativeDateRangePropertyPredicateResult.size());

        final Predicate getResult = actualCreateRelativeDateRangePropertyPredicateResult.getFirst();

        assertEquals("relativedaterange", getResult.getName());
        assertEquals("relativedaterange", getResult.getPath());
        assertEquals("relativedaterange", getResult.getType());
        assertEquals(1, getResult.getParameters().size());
    }


    /**
     * Method under test: {@link QuerySearchUtil#createTemplatePredicate(QuerySearch, String...)}
     */
    @Test
    void createTemplatePredicate() {
        assertNotNull(QuerySearchUtil.createTemplatePredicate(querySearch, "template"));
        assertNotNull(QuerySearchUtil.createTemplatePredicate(querySearch, "template1, template2, template3"));
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
