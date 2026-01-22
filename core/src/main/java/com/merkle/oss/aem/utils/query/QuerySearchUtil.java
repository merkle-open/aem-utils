package com.merkle.oss.aem.utils.query;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.tagging.TagManager;
import com.merkle.oss.aem.utils.java.ClassUtil;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Utility class providing static helper methods for AEM {@link Query} construction.
 */
public final class QuerySearchUtil {

    private static final Logger LOG = LoggerFactory.getLogger(QuerySearchUtil.class);

    private QuerySearchUtil() {
        ClassUtil.assertNoInstance(this.getClass());
    }

    /**
     * Creates a {@link com.day.cq.search.PredicateGroup} for a collection of AEM Tags.
     * <p>
     * Each Tag ID in the list is resolved via the {@link com.day.cq.tagging.TagManager}. If a tag cannot be resolved,
     * it is ignored. The resulting group can be configured as a logical <b>AND</b> or logical <b>OR</b>.
     * </p>
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
                .map(tag -> QuerySearch.createTagPredicate(tag, propertyPath))
                .toList());

        return tagListPredicateGroup;
    }

    /**
     * Escapes special characters for use in JCR {@code jcr:like} XPath functions.
     * <p>
     * In JCR queries, the characters {@code _} (underscore) and {@code %} (percent) are wildcards.
     * This method escapes them, along with backslashes, to ensure they are treated as literal strings.
     * </p>
     *
     * @param searchValue The raw string to be used in a "like" search.
     * @return An escaped version of the search term.
     * @see <a href="http://docs.adobe.com/docs/en/spec/jcr/1.0/6.6.5.1_jcr_like_Function.html">JCR 1.0: jcr:like Function</a>
     */
    public static @NonNull String escapeSearchValue(@NonNull final String searchValue) {
        // please don't try to understand this escaping madness. It took me quite some time...
        return RegExUtils.replaceAll(searchValue, "\\\\", "\\\\\\\\")
                .replace("_", "\\_")
                .replace("%", "\\%");
    }

    /**
     * Creates a {@link com.day.cq.search.PredicateGroup} that filters results by a set of AEM templates.
     * <p>
     * The generated group uses logical <b>OR</b> (non-required) logic, meaning a result item
     * matches if it uses <i>any</i> of the provided templates.
     * </p>
     *
     * @param querySearch The builder instance used to generate the property predicates.
     * @param templates   One or more paths to {@code cq:template} nodes.
     * @return A PredicateGroup containing template path comparisons.
     */
    public static @NonNull PredicateGroup createTemplatePredicate(@NonNull final QuerySearch querySearch, @NonNull final String... templates) {
        final PredicateGroup templatesPredicate = new PredicateGroup();
        templatesPredicate.setAllRequired(false);

        templatesPredicate.addAll(Arrays.stream(templates)
                .map(template -> querySearch.createPropertyPredicate(PredicateProperties.CQ_TEMPLATE, template))
                .toList());

        return templatesPredicate;
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
            Class<?> clazz = any.getClass();
            Field resourceResolverField = clazz.getDeclaredField("resourceResolver");
            resourceResolverField.setAccessible(true);
            resourceResolverField.set(any, resolver);
        }

    }

}
