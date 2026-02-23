package com.merkle.oss.aem.utils.query;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.eval.JcrPropertyPredicateEvaluator;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.merkle.oss.aem.utils.annotations.tooling.Generated;
import com.merkle.oss.aem.utils.java.ClassUtil;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.spi.commons.query.QueryConstants;
import org.apache.sling.api.resource.ResourceResolver;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Utility class providing static helper methods for AEM {@link com.day.cq.search.Query} construction.
 * <p>
 * Additionally serves as a predicate builder factory for any possible predicate definition.
 *
 * @see <a href="https://experienceleague.adobe.com/en/docs/experience-manager-cloud-service/content/implementing/developing/full-stack/search/query-builder-predicates">Query Builder Predicate Reference</a>
 */
public final class QuerySearchUtil {

    private static final Logger LOG = LoggerFactory.getLogger(QuerySearchUtil.class);

    @Generated("Bypass coverage for static utility constructor")
    private QuerySearchUtil() {
        ClassUtil.assertNoInstance(this.getClass());
    }

    /**
     * Creates a simple equality predicate for a property.
     *
     * @param propertyName  The property name.
     * @param propertyValue The expected value.
     * @return A PredicateGroup representing {@code [property] = [value]}.
     */
    public static @NonNull PredicateGroup createPropertyPredicate(@NonNull final String propertyName, @NonNull final String propertyValue) {
        final Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(JcrPropertyPredicateEvaluator.PROPERTY, propertyName);
        propertyMap.put("property.value", propertyValue);

        return PredicateGroup.create(propertyMap);
    }

    /**
     * Creates a PredicateGroup based on the specified property, operation, and value.
     * This method prepares a predicate configuration for evaluating properties.
     * Operation of a type:
     * <ul>
     *     <li>{@code equals} for exact match (default)</li>
     *     <li>{@code unequals} for unequal comparison (property must exist)</li>
     *     <li>{@code like} for using the jcr:like xpath function (optional)</li>
     *     <li>{@code not} for no match (e.g.: "not(@prop)" in xpath, value param will be ignored)</li>
     *     <li>{@code exists} for existence check (value can be true - property must exist, the default - or false - same as {@code not})</li>
     * </ul>
     *
     * @param propertyName  Name of the property.
     * @param operation     The operation (see {@link com.day.cq.search.eval.JcrPropertyPredicateEvaluator}).
     * @param propertyValue The comparison value.
     * @return A configured PredicateGroup.
     */
    public static @NonNull PredicateGroup createPropertyPredicate(@NonNull final String propertyName, @NonNull final String operation, @NonNull final String propertyValue) {
        final Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(JcrPropertyPredicateEvaluator.PROPERTY, propertyName);
        propertyMap.put("property.operation", operation);
        propertyMap.put("property.value", propertyValue);

        return PredicateGroup.create(propertyMap);
    }

    /**
     * Creates a full-text search predicate.
     *
     * @param propertyValue The search term.
     * @param propertyName  The relative path to search within.
     * @return A full-text PredicateGroup, or {@code null} if the term is blank.
     */
    public static @NonNull PredicateGroup createFullTextPredicate(@NonNull final String propertyValue, @NonNull final String propertyName) {
        final Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put("fulltext", propertyValue);
        propertyMap.put("fulltext.relPath", propertyName);

        return PredicateGroup.create(propertyMap);
    }

    /**
     * Creates a predicate group that verifies the existence of a specified property.
     *
     * @param propertyName The name of the property to check for existence.
     * @return A PredicateGroup configured to evaluate whether the specified property exists.
     */
    public static @NonNull PredicateGroup createPropertyExistsPredicate(@NonNull final String propertyName) {
        final Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(JcrPropertyPredicateEvaluator.PROPERTY, propertyName);
        propertyMap.put("property.operation", JcrPropertyPredicateEvaluator.OP_EXISTS);

        return PredicateGroup.create(propertyMap);
    }

    /**
     * Creates a predicate group to evaluate if a specified property does not exist.
     *
     * @param propertyName The name of the property to check for non-existence.
     * @return A PredicateGroup configured to check that the specified property does not exist.
     */
    public static @NonNull PredicateGroup createPropertyNotExistsPredicate(@NonNull final String propertyName) {
        final Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(JcrPropertyPredicateEvaluator.PROPERTY, propertyName);
        propertyMap.put("property.operation", JcrPropertyPredicateEvaluator.OP_NOT);

        return PredicateGroup.create(propertyMap);
    }

    /**
     * Creates a simple tag-based predicate.
     *
     * @param tag          The tag to search for.
     * @param propertyName The property containing tag IDs.
     * @return A PredicateGroup for the specific tag.
     */
    public static @NonNull PredicateGroup createTagPredicate(@NonNull final Tag tag, @NonNull final String propertyName) {
        final Map<String, String> tagMap = new HashMap<>();
        tagMap.put("tagid.property", propertyName);
        tagMap.put("tagid", tag.getTagID());

        return PredicateGroup.create(tagMap);
    }

    /**
     * Creates a {@link com.day.cq.search.PredicateGroup} for a collection of AEM Tags.
     * <p>
     * Each Tag ID in the list is resolved via the {@link com.day.cq.tagging.TagManager}. If a tag cannot be resolved,
     * it is ignored. The resulting group can be configured as a logical <b>AND</b> or logical <b>OR</b>.
     *
     * @param tags         A list of tag IDs (e.g., "namespace:tag-id").
     * @param propertyPath The JCR property where tags are stored (usually {@code cq:tags}).
     * @param allRequired  If {@code true}, applies AND logic (all tags must match).
     *                     If {@code false}, applies OR logic (at least one tag must match).
     * @param tagManager   The manager used to validate and resolve the tag IDs.
     * @return A configured PredicateGroup, or {@code null} if inputs are invalid or empty.
     */
    public static @Nullable PredicateGroup createTagListPredicateGroup(@Nullable final List<String> tags, @Nullable final String propertyPath, final boolean allRequired, @Nullable final TagManager tagManager) {
        if (tagManager == null || StringUtils.isBlank(propertyPath) || tags == null || tags.isEmpty()) {
            return null;
        }

        final PredicateGroup tagListPredicateGroup = new PredicateGroup();
        tagListPredicateGroup.setAllRequired(allRequired);
        tagListPredicateGroup.addAll(tags.stream()
                .map(tagManager::resolve)
                .filter(Objects::nonNull)
                .map(tag -> createTagPredicate(tag, propertyPath))
                .toList());

        return tagListPredicateGroup;
    }

    /**
     * lower operation defaults to greater than (alternative greater or equals than).
     * <p>
     * upper operation defaults to less than (alternative less or equals than).
     *
     * @param propertyName The property to evaluate.
     * @param lowerBound   The minimum value.
     * @param upperBound   The maximum value.
     * @param decimal      Whether to treat values as decimals.
     * @return A range PredicateGroup.
     */
    public static @NonNull PredicateGroup createRangePropertyPredicate(@NonNull final String propertyName, @Nullable final String lowerBound, @Nullable final String upperBound, final boolean decimal) {
        return createRangePropertyPredicate(propertyName, lowerBound, QueryConstants.OP_NAME_GT_GENERAL, upperBound, QueryConstants.OP_NAME_LT_GENERAL, decimal);
    }

    /**
     * Creates a range property predicate based on the specified input parameters.
     *
     * @param propertyName   the name of the property to apply the range condition to
     * @param lowerBound     the lower bound value of the range; can be null or blank if not required
     * @param lowerOperation the operation for the lower bound (e.g., {@code >}, {@code >=}); ignored if lowerBound is null or blank
     * @param upperBound     the upper bound value of the range; can be null or blank if not required
     * @param upperOperation the operation for the upper bound (e.g., {@code <}, {@code <=}); ignored if upperBound is null or blank
     * @param decimal        a flag indicating whether the property is decimal-based
     * @return a PredicateGroup representing the range property predicate
     */
    public static @NonNull PredicateGroup createRangePropertyPredicate(@NonNull final String propertyName, @Nullable final String lowerBound, @Nullable final String lowerOperation,
                                                                       @Nullable final String upperBound, @Nullable final String upperOperation, final boolean decimal) {
        final Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put("rangeproperty.property", propertyName);
        if (StringUtils.isNoneBlank(lowerBound, lowerOperation)) {
            propertyMap.put("rangeproperty.lowerBound", lowerBound);
            propertyMap.put("rangeproperty.lowerOperation", lowerOperation);
        }
        if (StringUtils.isNoneBlank(upperBound, upperOperation)) {
            propertyMap.put("rangeproperty.upperBound", upperBound);
            propertyMap.put("rangeproperty.upperOperation", upperOperation);
        }
        propertyMap.put("rangeproperty.decimal", String.valueOf(decimal));

        return PredicateGroup.create(propertyMap);
    }

    /**
     * Creates a predicate group representing a date range property with optional lower and upper bounds.
     *
     * @param propertyName   the name of the property to be used in the date range comparison
     * @param lowerBound     the lower bound value of the range; can be null or blank if not required
     * @param lowerOperation the operation for the lower bound (e.g., {@code >}, {@code >=}); ignored if lowerBound is null or blank
     * @param upperBound     the upper bound value of the range; can be null or blank if not required
     * @param upperOperation the operation for the upper bound (e.g., {@code <}, {@code <=}); ignored if upperBound is null or blank
     * @return a PredicateGroup containing the configured date range property and bounds, or an empty group if no bounds are provided
     */
    public static @NonNull PredicateGroup createDateRangePropertyPredicate(@NonNull final String propertyName, @Nullable final String lowerBound, @Nullable final String lowerOperation,
                                                                           @Nullable final String upperBound, @Nullable final String upperOperation) {
        final Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put("daterange.property", propertyName);
        if (StringUtils.isNoneBlank(lowerBound, lowerOperation)) {
            propertyMap.put("daterange.lowerBound", lowerBound);
            propertyMap.put("daterange.lowerOperation", lowerOperation);
        }
        if (StringUtils.isNoneBlank(upperBound, upperOperation)) {
            propertyMap.put("daterange.upperBound", upperBound);
            propertyMap.put("daterange.upperOperation", upperOperation);
        }

        return PredicateGroup.create(propertyMap);
    }

    /**
     * Creates a predicate group for a relative date range based on the provided property name and bounds.
     *
     * @param propertyName the name of the property for which the relative date range predicate is being created
     * @param lowerBound   the lower bound of the relative date range can be null or empty to exclude it
     * @param upperBound   the upper bound of the relative date range can be null or empty to exclude it
     * @return a PredicateGroup configured with the relative date range properties
     */
    public static @NonNull PredicateGroup createRelativeDateRangePropertyPredicate(@NonNull final String propertyName, @Nullable final String lowerBound, @Nullable final String upperBound) {
        final Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put("relativedaterange.property", propertyName);
        if (StringUtils.isNotBlank(lowerBound)) {
            propertyMap.put("relativedaterange.lowerBound", lowerBound);
        }
        if (StringUtils.isNotBlank(upperBound)) {
            propertyMap.put("relativedaterange.upperBound", upperBound);
        }

        return PredicateGroup.create(propertyMap);
    }

    /**
     * Creates a {@link com.day.cq.search.PredicateGroup} that filters results by a set of AEM templates.
     * <p>
     * The generated group uses logical <b>OR</b> (non-required) logic, meaning a result item
     * matches if it uses <i>any</i> of the provided templates.
     *
     * @param querySearch The builder instance used to generate the property predicates.
     * @param templates   One or more paths to {@code cq:template} nodes.
     * @return A PredicateGroup containing template path comparisons.
     */
    public static @NonNull PredicateGroup createTemplatesPredicate(@NonNull final QuerySearch querySearch, @NonNull final String... templates) {
        final PredicateGroup templatesPredicate = new PredicateGroup();
        templatesPredicate.setAllRequired(false);

        templatesPredicate.addAll(Arrays.stream(templates)
                .map(template -> createPropertyPredicate(PredicateProperties.CQ_TEMPLATE, template))
                .toList());

        return templatesPredicate;
    }

    /**
     * Escapes special characters for use in JCR {@code jcr:like} XPath functions.
     * <p>
     * In JCR queries, the characters {@code _} (underscore) and {@code %} (percent) are wildcards.
     * This method escapes them, along with backslashes, to ensure they are treated as literal strings.
     *
     * @param searchValue The raw string to be used in a "like" search.
     * @return An escaped version of the search term.
     * @see <a href="http://docs.adobe.com/docs/en/spec/jcr/1.0/6.6.5.1_jcr_like_Function.html">JCR 1.0: jcr:like Function</a>
     */
    public static @NonNull String escapeSearchValue(@NonNull final String searchValue) {
        Objects.requireNonNull(searchValue);

        // please don't try to understand this escaping madness. It took me quite some time...
        return RegExUtils.replaceAll(searchValue, "\\\\", "\\\\\\\\")
                .replace("_", "\\_")
                .replace("%", "\\%");
    }

    /**
     * Forcibly attaches a {@link org.apache.sling.api.resource.ResourceResolver} to an existing {@link com.day.cq.search.Query} object.
     * <p>
     * <b>Warning:</b> This method uses reflection to access private fields within the
     * QueryBuilder implementation. It is used to ensure that the Query instance remains
     * valid and associated with an active resolver, preventing {@code IllegalStateException}
     * or unclosed resolver warnings in specific AEM environments.
     *
     * @param resolver The active ResourceResolver to attach.
     * @param query    The Query object to modify.
     */
    public static void setResourceResolverOn(@NonNull final ResourceResolver resolver, @Nullable final Query query) {
        try {
            internalSetResourceResolverOn(resolver, query);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            LOG.error("Could not set ResourceResolver on provided Query using reflection: {} => {}", e.getClass().getName(), e.getMessage());
        }

    }

    /**
     * Internal reflection logic to set the {@code resourceResolver} field on an object.
     */
    static void internalSetResourceResolverOn(@NonNull final ResourceResolver resolver, @Nullable final Object any) throws NoSuchFieldException, IllegalAccessException {
        if (any != null) {
            final Class<?> clazz = any.getClass();
            final Field resourceResolverField = clazz.getDeclaredField("resourceResolver");
            resourceResolverField.setAccessible(true);
            resourceResolverField.set(any, resolver);
        }

    }

}
