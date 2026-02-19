package com.merkle.oss.aem.utils.query;

import com.day.cq.search.Predicate;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.tagging.Tag;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link QuerySearch} class.
 */
@ExtendWith(MockitoExtension.class)
class QuerySearchTest {

    @Mock
    private Tag tag;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Session session;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private Query query;

    /**
     * Method under test: {@link QuerySearch#toQuery(QueryBuilder, ResourceResolver)}
     */
    @Test
    void testToQuery() {
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(queryBuilder.createQuery(any(), any())).thenReturn(query);
        when(query.getHitsPerPage()).thenReturn(2L);
        when(tag.getTagID()).thenReturn("testId");

        final QuerySearch querySearch = new QuerySearch(NT_UNSTRUCTURED);
        querySearch.setPaths(Collections.singletonList("/test/path"));
        querySearch.setAddOnOffTimePredicate(false);
        querySearch.setHitsPerPage(2);
        querySearch.setAllRequiredTags(List.of(tag));
        querySearch.addAdditionalPredicates(new PredicateGroup("Type"));
        final Query queryTest = querySearch.toQuery(queryBuilder, resourceResolver);

        assertEquals(2L, queryTest.getHitsPerPage());
    }

    /**
     * Method under test: {@link QuerySearch#toQuery(QueryBuilder, ResourceResolver)}
     */
    @Test
    void testToQuery_1() {
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(queryBuilder.createQuery(any(), any())).thenReturn(query);
        when(query.getHitsPerPage()).thenReturn(2L);
        when(tag.getTagID()).thenReturn("testId");

        final QuerySearch querySearch = new QuerySearch(NT_UNSTRUCTURED);
        querySearch.setHitsPerPage(2);
        querySearch.setAddOnOffTimePredicate(true);
        querySearch.setOneRequiredTags(List.of(tag));
        querySearch.setAllRequiredTags(Collections.emptyList());
        final Query queryTest = querySearch.toQuery(queryBuilder, resourceResolver);

        assertEquals(2L, queryTest.getHitsPerPage());
    }

    /**
     * Method under test: {@link QuerySearch#setPaths(List)}
     */
    @Test
    void testSetPaths() {
        final QuerySearch querySearch = new QuerySearch("Primary Type");
        querySearch.setPaths(new ArrayList<>());
        final PredicateGroup createPropertyExistsPredicateResult = QuerySearchUtil.createPropertyExistsPredicate("Property Name");

        assertEquals(1, createPropertyExistsPredicateResult.size());

        final Predicate getResult = createPropertyExistsPredicateResult.getFirst();
        final Map<String, String> parameters = getResult.getParameters();

        assertEquals(2, parameters.size());
        assertEquals("Property Name", parameters.get("property"));

        final PredicateGroup createPropertyNotExistsPredicateResult = QuerySearchUtil
                .createPropertyNotExistsPredicate("Property Name");

        assertEquals(1, createPropertyNotExistsPredicateResult.size());

        final Predicate getResult2 = createPropertyNotExistsPredicateResult.getFirst();
        final Map<String, String> parameters2 = getResult2.getParameters();

        assertEquals(2, parameters2.size());
        assertEquals("Property Name", parameters2.get("property"));
        assertEquals("exists", parameters.get("operation"));
        assertEquals("not", parameters2.get("operation"));
        assertEquals("property", getResult.getName());
        assertEquals("property", getResult2.getName());
        assertEquals("property", getResult.getPath());
        assertEquals("property", getResult2.getPath());
        assertEquals("property", getResult.getType());
        assertEquals("property", getResult2.getType());
    }

    /**
     * Method under test: {@link QuerySearch#setPaths(List)}
     */
    @Test
    void testSetPaths2() {
        final QuerySearch querySearch = new QuerySearch("Primary Type");
        final ArrayList<String> paths = new ArrayList<>();
        paths.add("foo");
        querySearch.setPaths(paths);
        final PredicateGroup createPropertyExistsPredicateResult = QuerySearchUtil.createPropertyExistsPredicate("Property Name");

        assertEquals(1, createPropertyExistsPredicateResult.size());

        final Predicate getResult = createPropertyExistsPredicateResult.getFirst();
        final Map<String, String> parameters = getResult.getParameters();

        assertEquals(2, parameters.size());
        assertEquals("Property Name", parameters.get("property"));

        final PredicateGroup createPropertyNotExistsPredicateResult = QuerySearchUtil
                .createPropertyNotExistsPredicate("Property Name");

        assertEquals(1, createPropertyNotExistsPredicateResult.size());

        final Predicate getResult2 = createPropertyNotExistsPredicateResult.getFirst();
        final Map<String, String> parameters2 = getResult2.getParameters();

        assertEquals(2, parameters2.size());
        assertEquals("Property Name", parameters2.get("property"));
        assertEquals("exists", parameters.get("operation"));
        assertEquals("not", parameters2.get("operation"));
        assertEquals("property", getResult.getName());
        assertEquals("property", getResult2.getName());
        assertEquals("property", getResult.getPath());
        assertEquals("property", getResult2.getPath());
        assertEquals("property", getResult.getType());
        assertEquals("property", getResult2.getType());
    }

    /**
     * Method under test: {@link QuerySearch#setPaths(List)}
     */
    @Test
    void testSetPaths3() {
        final QuerySearch querySearch = new QuerySearch("Primary Type");
        final ArrayList<String> paths = new ArrayList<>();
        paths.add("42");
        paths.add("foo");
        querySearch.setPaths(paths);
        final PredicateGroup createPropertyExistsPredicateResult = QuerySearchUtil.createPropertyExistsPredicate("Property Name");

        assertEquals(1, createPropertyExistsPredicateResult.size());

        final Predicate getResult = createPropertyExistsPredicateResult.getFirst();
        final Map<String, String> parameters = getResult.getParameters();

        assertEquals(2, parameters.size());
        assertEquals("Property Name", parameters.get("property"));

        final PredicateGroup createPropertyNotExistsPredicateResult = QuerySearchUtil
                .createPropertyNotExistsPredicate("Property Name");

        assertEquals(1, createPropertyNotExistsPredicateResult.size());

        final Predicate getResult2 = createPropertyNotExistsPredicateResult.getFirst();
        final Map<String, String> parameters2 = getResult2.getParameters();

        assertEquals(2, parameters2.size());
        assertEquals("Property Name", parameters2.get("property"));
        assertEquals("exists", parameters.get("operation"));
        assertEquals("not", parameters2.get("operation"));
        assertEquals("property", getResult.getName());
        assertEquals("property", getResult2.getName());
        assertEquals("property", getResult.getPath());
        assertEquals("property", getResult2.getPath());
        assertEquals("property", getResult.getType());
        assertEquals("property", getResult2.getType());
    }

    /**
     * Method under test: {@link QuerySearch#addPath(String)}
     */
    @Test
    void testAddPath() {
        final QuerySearch querySearch = new QuerySearch("Primary Type");
        querySearch.addPath("Path");
        final PredicateGroup createPropertyExistsPredicateResult = QuerySearchUtil.createPropertyExistsPredicate("Property Name");

        assertEquals(1, createPropertyExistsPredicateResult.size());

        final Predicate getResult = createPropertyExistsPredicateResult.getFirst();
        final Map<String, String> parameters = getResult.getParameters();

        assertEquals(2, parameters.size());
        assertEquals("Property Name", parameters.get("property"));

        final PredicateGroup createPropertyNotExistsPredicateResult = QuerySearchUtil
                .createPropertyNotExistsPredicate("Property Name");

        assertEquals(1, createPropertyNotExistsPredicateResult.size());

        final Predicate getResult2 = createPropertyNotExistsPredicateResult.getFirst();
        final Map<String, String> parameters2 = getResult2.getParameters();

        assertEquals(2, parameters2.size());
        assertEquals("Property Name", parameters2.get("property"));
        assertEquals("exists", parameters.get("operation"));
        assertEquals("not", parameters2.get("operation"));
        assertEquals("property", getResult.getName());
        assertEquals("property", getResult2.getName());
        assertEquals("property", getResult.getPath());
        assertEquals("property", getResult2.getPath());
        assertEquals("property", getResult.getType());
        assertEquals("property", getResult2.getType());
    }

    /**
     * Method under test: {@link QuerySearch#addPath(String)}
     */
    @Test
    void testAddPath2() {
        final QuerySearch querySearch = new QuerySearch("Primary Type");
        querySearch.addPath(null);
        final PredicateGroup createPropertyExistsPredicateResult = QuerySearchUtil.createPropertyExistsPredicate("Property Name");

        assertEquals(1, createPropertyExistsPredicateResult.size());

        final Predicate getResult = createPropertyExistsPredicateResult.getFirst();
        final Map<String, String> parameters = getResult.getParameters();

        assertEquals(2, parameters.size());
        assertEquals("Property Name", parameters.get("property"));

        final PredicateGroup createPropertyNotExistsPredicateResult = QuerySearchUtil
                .createPropertyNotExistsPredicate("Property Name");

        assertEquals(1, createPropertyNotExistsPredicateResult.size());

        final Predicate getResult2 = createPropertyNotExistsPredicateResult.getFirst();
        final Map<String, String> parameters2 = getResult2.getParameters();

        assertEquals(2, parameters2.size());
        assertEquals("Property Name", parameters2.get("property"));
        assertEquals("exists", parameters.get("operation"));
        assertEquals("not", parameters2.get("operation"));
        assertEquals("property", getResult.getName());
        assertEquals("property", getResult2.getName());
        assertEquals("property", getResult.getPath());
        assertEquals("property", getResult2.getPath());
        assertEquals("property", getResult.getType());
        assertEquals("property", getResult2.getType());
    }

    /**
     * Method under test: {@link QuerySearch#setAllRequiredTags(List)}
     */
    @Test
    void testSetAllRequiredTags() {
        final QuerySearch querySearch = new QuerySearch("Primary Type");
        querySearch.setAllRequiredTags(new ArrayList<>());
        final PredicateGroup createPropertyExistsPredicateResult = QuerySearchUtil.createPropertyExistsPredicate("Property Name");

        assertEquals(1, createPropertyExistsPredicateResult.size());

        final Predicate getResult = createPropertyExistsPredicateResult.getFirst();
        final Map<String, String> parameters = getResult.getParameters();

        assertEquals(2, parameters.size());
        assertEquals("Property Name", parameters.get("property"));

        final PredicateGroup createPropertyNotExistsPredicateResult = QuerySearchUtil
                .createPropertyNotExistsPredicate("Property Name");

        assertEquals(1, createPropertyNotExistsPredicateResult.size());

        final Predicate getResult2 = createPropertyNotExistsPredicateResult.getFirst();
        final Map<String, String> parameters2 = getResult2.getParameters();

        assertEquals(2, parameters2.size());
        assertEquals("Property Name", parameters2.get("property"));
        assertEquals("exists", parameters.get("operation"));
        assertEquals("not", parameters2.get("operation"));
        assertEquals("property", getResult.getName());
        assertEquals("property", getResult2.getName());
        assertEquals("property", getResult.getPath());
        assertEquals("property", getResult2.getPath());
        assertEquals("property", getResult.getType());
        assertEquals("property", getResult2.getType());
    }

    /**
     * Method under test: {@link QuerySearch#setOneRequiredTags(List)}
     */
    @Test
    void testSetOneRequiredTags() {
        final QuerySearch querySearch = new QuerySearch("Primary Type");
        querySearch.setOneRequiredTags(new ArrayList<>());
        final PredicateGroup createPropertyExistsPredicateResult = QuerySearchUtil.createPropertyExistsPredicate("Property Name");

        assertEquals(1, createPropertyExistsPredicateResult.size());

        final Predicate getResult = createPropertyExistsPredicateResult.getFirst();
        final Map<String, String> parameters = getResult.getParameters();

        assertEquals(2, parameters.size());
        assertEquals("Property Name", parameters.get("property"));

        final PredicateGroup createPropertyNotExistsPredicateResult = QuerySearchUtil
                .createPropertyNotExistsPredicate("Property Name");

        assertEquals(1, createPropertyNotExistsPredicateResult.size());

        final Predicate getResult2 = createPropertyNotExistsPredicateResult.getFirst();
        final Map<String, String> parameters2 = getResult2.getParameters();

        assertEquals(2, parameters2.size());
        assertEquals("Property Name", parameters2.get("property"));
        assertEquals("exists", parameters.get("operation"));
        assertEquals("not", parameters2.get("operation"));
        assertEquals("property", getResult.getName());
        assertEquals("property", getResult2.getName());
        assertEquals("property", getResult.getPath());
        assertEquals("property", getResult2.getPath());
        assertEquals("property", getResult.getType());
        assertEquals("property", getResult2.getType());
    }

    /**
     * Method under test:
     * {@link QuerySearch#addAdditionalPredicates(PredicateGroup)}
     */
    @Test
    void testAddAdditionalPredicates() {
        final QuerySearch querySearch = new QuerySearch("Primary Type");
        querySearch.addAdditionalPredicates(new PredicateGroup());
        final PredicateGroup createPropertyExistsPredicateResult = QuerySearchUtil.createPropertyExistsPredicate("Property Name");

        assertEquals(1, createPropertyExistsPredicateResult.size());

        final Predicate getResult = createPropertyExistsPredicateResult.getFirst();
        final Map<String, String> parameters = getResult.getParameters();

        assertEquals(2, parameters.size());
        assertEquals("Property Name", parameters.get("property"));

        final PredicateGroup createPropertyNotExistsPredicateResult = QuerySearchUtil
                .createPropertyNotExistsPredicate("Property Name");

        assertEquals(1, createPropertyNotExistsPredicateResult.size());

        final Predicate getResult2 = createPropertyNotExistsPredicateResult.getFirst();
        final Map<String, String> parameters2 = getResult2.getParameters();

        assertEquals(2, parameters2.size());
        assertEquals("Property Name", parameters2.get("property"));
        assertEquals("exists", parameters.get("operation"));
        assertEquals("not", parameters2.get("operation"));
        assertEquals("property", getResult.getName());
        assertEquals("property", getResult2.getName());
        assertEquals("property", getResult.getPath());
        assertEquals("property", getResult2.getPath());
        assertEquals("property", getResult.getType());
        assertEquals("property", getResult2.getType());
    }

    /**
     * Method under test:
     * {@link QuerySearch#addAdditionalPredicates(PredicateGroup)}
     */
    @Test
    void testAddAdditionalPredicates2() {
        final QuerySearch querySearch = new QuerySearch("Primary Type");
        querySearch.addAdditionalPredicates(null);
        final PredicateGroup createPropertyExistsPredicateResult = QuerySearchUtil.createPropertyExistsPredicate("Property Name");

        assertEquals(1, createPropertyExistsPredicateResult.size());

        final Predicate getResult = createPropertyExistsPredicateResult.getFirst();
        final Map<String, String> parameters = getResult.getParameters();

        assertEquals(2, parameters.size());
        assertEquals("Property Name", parameters.get("property"));

        final PredicateGroup createPropertyNotExistsPredicateResult = QuerySearchUtil
                .createPropertyNotExistsPredicate("Property Name");

        assertEquals(1, createPropertyNotExistsPredicateResult.size());

        final Predicate getResult2 = createPropertyNotExistsPredicateResult.getFirst();
        final Map<String, String> parameters2 = getResult2.getParameters();

        assertEquals(2, parameters2.size());
        assertEquals("Property Name", parameters2.get("property"));
        assertEquals("exists", parameters.get("operation"));
        assertEquals("not", parameters2.get("operation"));
        assertEquals("property", getResult.getName());
        assertEquals("property", getResult2.getName());
        assertEquals("property", getResult.getPath());
        assertEquals("property", getResult2.getPath());
        assertEquals("property", getResult.getType());
        assertEquals("property", getResult2.getType());
    }

    /**
     * Method under test:
     * {@link QuerySearch#addAdditionalPredicates(PredicateGroup)}
     */
    @Test
    void testAddAdditionalPredicates3() {
        final QuerySearch querySearch = new QuerySearch("Primary Type");
        ArrayList<Predicate> c = new ArrayList<>();
        c.add(new Predicate("Type"));
        final PredicateGroup predicates = new PredicateGroup();
        predicates.addAll(c);
        querySearch.addAdditionalPredicates(predicates);
        final PredicateGroup createPropertyExistsPredicateResult = QuerySearchUtil.createPropertyExistsPredicate("Property Name");

        assertEquals(1, createPropertyExistsPredicateResult.size());

        final Predicate getResult = createPropertyExistsPredicateResult.getFirst();
        final Map<String, String> parameters = getResult.getParameters();

        assertEquals(2, parameters.size());
        assertEquals("Property Name", parameters.get("property"));

        final PredicateGroup createPropertyNotExistsPredicateResult = QuerySearchUtil
                .createPropertyNotExistsPredicate("Property Name");

        assertEquals(1, createPropertyNotExistsPredicateResult.size());

        final Predicate getResult2 = createPropertyNotExistsPredicateResult.getFirst();
        final Map<String, String> parameters2 = getResult2.getParameters();

        assertEquals(2, parameters2.size());
        assertEquals("Property Name", parameters2.get("property"));
        assertEquals("exists", parameters.get("operation"));
        assertEquals("not", parameters2.get("operation"));
        assertEquals("property", getResult.getName());
        assertEquals("property", getResult2.getName());
        assertEquals("property", getResult.getPath());
        assertEquals("property", getResult2.getPath());
        assertEquals("property", getResult.getType());
        assertEquals("property", getResult2.getType());
    }

    /**
     * Method under test:
     * {@link QuerySearch#addAdditionalPredicates(PredicateGroup)}
     */
    @Test
    void testAddAdditionalPredicates4() {
        final QuerySearch querySearch = new QuerySearch("Primary Type");
        ArrayList<Predicate> c = new ArrayList<>();
        c.add(new Predicate("Type"));
        c.add(new Predicate("Type"));
        final PredicateGroup predicates = new PredicateGroup();
        predicates.addAll(c);
        querySearch.addAdditionalPredicates(predicates);
        final PredicateGroup createPropertyExistsPredicateResult = QuerySearchUtil.createPropertyExistsPredicate("Property Name");

        assertEquals(1, createPropertyExistsPredicateResult.size());

        final Predicate getResult = createPropertyExistsPredicateResult.getFirst();
        final Map<String, String> parameters = getResult.getParameters();

        assertEquals(2, parameters.size());
        assertEquals("Property Name", parameters.get("property"));

        final PredicateGroup createPropertyNotExistsPredicateResult = QuerySearchUtil
                .createPropertyNotExistsPredicate("Property Name");

        assertEquals(1, createPropertyNotExistsPredicateResult.size());

        final Predicate getResult2 = createPropertyNotExistsPredicateResult.getFirst();
        final Map<String, String> parameters2 = getResult2.getParameters();

        assertEquals(2, parameters2.size());
        assertEquals("Property Name", parameters2.get("property"));
        assertEquals("exists", parameters.get("operation"));
        assertEquals("not", parameters2.get("operation"));
        assertEquals("property", getResult.getName());
        assertEquals("property", getResult2.getName());
        assertEquals("property", getResult.getPath());
        assertEquals("property", getResult2.getPath());
        assertEquals("property", getResult.getType());
        assertEquals("property", getResult2.getType());
    }

    /**
     * Method under test: {@link QuerySearch#addOrderByPredicate(String, boolean)}
     */
    @Test
    void testAddOrderByPredicate() {
        final QuerySearch querySearch = new QuerySearch("Primary Type");
        querySearch.addOrderByPredicate("Order By Property", true);
        final PredicateGroup createPropertyExistsPredicateResult = QuerySearchUtil.createPropertyExistsPredicate("Property Name");

        assertEquals(1, createPropertyExistsPredicateResult.size());

        final Predicate getResult = createPropertyExistsPredicateResult.getFirst();
        final Map<String, String> parameters = getResult.getParameters();

        assertEquals(2, parameters.size());
        assertEquals("Property Name", parameters.get("property"));

        final PredicateGroup createPropertyNotExistsPredicateResult = QuerySearchUtil
                .createPropertyNotExistsPredicate("Property Name");

        assertEquals(1, createPropertyNotExistsPredicateResult.size());

        final Predicate getResult2 = createPropertyNotExistsPredicateResult.getFirst();
        final Map<String, String> parameters2 = getResult2.getParameters();

        assertEquals(2, parameters2.size());
        assertEquals("Property Name", parameters2.get("property"));
        assertEquals("exists", parameters.get("operation"));
        assertEquals("not", parameters2.get("operation"));
        assertEquals("property", getResult.getName());
        assertEquals("property", getResult2.getName());
        assertEquals("property", getResult.getPath());
        assertEquals("property", getResult2.getPath());
        assertEquals("property", getResult.getType());
        assertEquals("property", getResult2.getType());
    }

    /**
     * Method under test: {@link QuerySearch#addOrderByPredicate(String, boolean)}
     */
    @Test
    void testAddOrderByPredicate2() {
        final QuerySearch querySearch = new QuerySearch("Primary Type");
        querySearch.addOrderByPredicate("Order By Property", false);
        final PredicateGroup createPropertyExistsPredicateResult = QuerySearchUtil.createPropertyExistsPredicate("Property Name");

        assertEquals(1, createPropertyExistsPredicateResult.size());

        final Predicate getResult = createPropertyExistsPredicateResult.getFirst();
        final Map<String, String> parameters = getResult.getParameters();

        assertEquals(2, parameters.size());
        assertEquals("Property Name", parameters.get("property"));

        final PredicateGroup createPropertyNotExistsPredicateResult = QuerySearchUtil
                .createPropertyNotExistsPredicate("Property Name");

        assertEquals(1, createPropertyNotExistsPredicateResult.size());

        final Predicate getResult2 = createPropertyNotExistsPredicateResult.getFirst();
        final Map<String, String> parameters2 = getResult2.getParameters();

        assertEquals(2, parameters2.size());
        assertEquals("Property Name", parameters2.get("property"));
        assertEquals("exists", parameters.get("operation"));
        assertEquals("not", parameters2.get("operation"));
        assertEquals("property", getResult.getName());
        assertEquals("property", getResult2.getName());
        assertEquals("property", getResult.getPath());
        assertEquals("property", getResult2.getPath());
        assertEquals("property", getResult.getType());
        assertEquals("property", getResult2.getType());
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link QuerySearch#QuerySearch(String)}
     *   <li>{@link QuerySearch#setAddOnOffTimePredicate(boolean)}
     *   <li>{@link QuerySearch#setHitsPerPage(long)}
     *   <li>{@link QuerySearch#setOffset(long)}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        final QuerySearch actualQuerySearch = new QuerySearch("Primary Type");

        actualQuerySearch.setAddOnOffTimePredicate(true);
        actualQuerySearch.setHitsPerPage(1L);
        actualQuerySearch.setOffset(1L);

        assertTrue(actualQuerySearch.isAddOnOffTimePredicate());
        assertEquals(1L, actualQuerySearch.getHitsPerPage());
        assertEquals(1L, actualQuerySearch.getOffset());
    }

}
