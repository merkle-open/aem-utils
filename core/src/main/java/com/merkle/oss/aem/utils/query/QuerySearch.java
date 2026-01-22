package com.merkle.oss.aem.utils.query;

import com.day.cq.search.Predicate;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.eval.JcrPropertyPredicateEvaluator;
import com.day.cq.search.eval.PathPredicateEvaluator;
import com.day.cq.search.eval.TypePredicateEvaluator;
import com.day.cq.tagging.Tag;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.jackrabbit.spi.commons.query.QueryConstants;
import org.apache.sling.api.resource.ResourceResolver;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static com.merkle.oss.aem.utils.sling.SlingUtil.sessionOf;

/**
 * A builder-style utility to simplify the creation of AEM {@link com.day.cq.search.Query} objects.
 * <p>
 * This class streamlines the assembly of common search criteria such as paths, tags, primary types,
 * and pagination. An automatic inclusion of <b>On/Off Time</b> predicates
 * ensures that search results only include resources that are currently active based on their
 * {@code cq:onTime} and {@code cq:offTime} properties.
 *
 */
public class QuerySearch {

    /**
     * Default limit for results; value '0' implies no limit.
     */
    private static final long DEFAULT_HITS_PER_PAGE = 0;

    /**
     * Default starting offset for results.
     */
    private static final long DEFAULT_SEARCH_RESULT_OFFSET = 0;

    /**
     * ISO-8601 compliant date format for JCR query compatibility.
     */
    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("UTC"));

    private final Date now;

    private final String primaryType;

    private List<String> paths = new ArrayList<>();

    private List<Tag> allRequiredTags;

    private List<Tag> oneRequiredTags;

    private final List<Predicate> orderByPredicates = new ArrayList<>();

    private final List<PredicateGroup> additionalPredicates = new LinkedList<>();

    private boolean addOnOffTimePredicate = true;

    private long hitsPerPage = DEFAULT_HITS_PER_PAGE;

    private long offset = DEFAULT_SEARCH_RESULT_OFFSET;

    /**
     * Initializes a new search context for a specific node type.
     *
     * @param primaryType The {@code jcr:primaryType} of the nodes to be retrieved (e.g., "cq:Page").
     */
    public QuerySearch(@NonNull final String primaryType) {
        this.now = new Date();
        this.primaryType = primaryType;
    }

    /**
     * Sets the maximum number of results (hits) per page.
     *
     * @param hitsPerPage The limit, use 0 for unlimited.
     */
    public void setHitsPerPage(final long hitsPerPage) {
        this.hitsPerPage = hitsPerPage;
    }

    public long getHitsPerPage() {
        return hitsPerPage;
    }

    /**
     * Sets the starting offset for pagination.
     *
     * @param offset The zero-based index of the first result.
     */
    public void setOffset(final long offset) {
        this.offset = offset;
    }

    public long getOffset() {
        return offset;
    }

    /**
     * Replaces the current search paths with a new list.
     * Multiple paths are evaluated using a logical <b>OR</b>.
     *
     * @param paths List of absolute repository paths.
     */
    public void setPaths(@NonNull final List<String> paths) {
        this.paths = new ArrayList<>(paths);
    }

    /**
     * Adds a single repository path to the search scope.
     * Multiple paths are evaluated using a logical <b>OR</b>.
     *
     * @param path The absolute path to include.
     */
    public void addPath(@Nullable final String path) {
        if (StringUtils.isNotBlank(path)) {
            paths.add(path);
        }
    }

    /**
     * Configures a mandatory list of tags. A resource matches only if <b>all</b> tags are present.
     *
     * @param allRequiredTags List of tags (logical AND).
     */
    public void setAllRequiredTags(@NonNull final List<Tag> allRequiredTags) {
        this.allRequiredTags = new ArrayList<>(allRequiredTags);
    }

    /**
     * Configures an optional list of tags. A resource matches if <b>at least one</b> tag is present.
     *
     * @param oneRequiredTags List of tags (logical OR).
     */
    public void setOneRequiredTags(@NonNull final List<Tag> oneRequiredTags) {
        this.oneRequiredTags = new ArrayList<>(oneRequiredTags);
    }

    /**
     * Appends a pre-configured group of predicates to the query.
     *
     * @param predicates The predicate group to add.
     */
    public void addAdditionalPredicates(@Nullable final PredicateGroup predicates) {
        if (predicates == null) {
            return;
        }
        additionalPredicates.add(predicates);
    }

    /**
     * Determines whether to filter results based on activation (on/off) times.
     *
     * @param setPredicate {@code true} to enable filtering (default), {@code false} to disable.
     */
    public void setAddOnOffTimePredicate(final boolean setPredicate) {
        addOnOffTimePredicate = setPredicate;
    }

    public boolean isAddOnOffTimePredicate() {
        return addOnOffTimePredicate;
    }

    /**
     * Internal factory for creating a tag-based predicate.
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
     * Adds a sorting rule to the query. Multiple calls define a composite sort order.
     *
     * @param orderByProperty The JCR property to sort by.
     * @param ascending       {@code true} for A-Z, {@code false} for Z-A.
     */
    public void addOrderByPredicate(@NonNull final String orderByProperty, final boolean ascending) {
        orderByPredicates.add(new OrderByPredicate(orderByProperty, ascending));
    }

    /**
     * Creates a simple equality predicate for a property.
     *
     * @param propertyName  The property name.
     * @param propertyValue The expected value.
     * @return A PredicateGroup representing {@code [property] = [value]}.
     */
    public @NonNull PredicateGroup createPropertyPredicate(@NonNull final String propertyName, @NonNull final String propertyValue) {
        final Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(JcrPropertyPredicateEvaluator.PROPERTY, propertyName);
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
    public @NonNull PredicateGroup createFullTextPredicate(@NonNull final String propertyValue, @NonNull final String propertyName) {
        final Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put("fulltext", propertyValue);
        propertyMap.put("fulltext.relPath", propertyName);

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
    public @NonNull PredicateGroup createPropertyPredicate(@NonNull final String propertyName, @NonNull final String operation, @NonNull final String propertyValue) {
        final Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(JcrPropertyPredicateEvaluator.PROPERTY, propertyName);
        propertyMap.put("property.operation", operation);
        propertyMap.put("property.value", propertyValue);

        return PredicateGroup.create(propertyMap);
    }

    /**
     * Creates a predicate group that verifies the existence of a specified property.
     *
     * @param propertyName The name of the property to check for existence.
     * @return A PredicateGroup configured to evaluate whether the specified property exists.
     */
    public @NonNull PredicateGroup createPropertyExistsPredicate(@NonNull final String propertyName) {
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
    public @NonNull PredicateGroup createPropertyNotExistsPredicate(@NonNull final String propertyName) {
        final Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put(JcrPropertyPredicateEvaluator.PROPERTY, propertyName);
        propertyMap.put("property.operation", JcrPropertyPredicateEvaluator.OP_NOT);

        return PredicateGroup.create(propertyMap);
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
    public @NonNull PredicateGroup createRangePropertyPredicate(@NonNull final String propertyName, @Nullable final String lowerBound, @Nullable final String upperBound, final boolean decimal) {
        return createRangePropertyPredicate(propertyName, lowerBound, QueryConstants.OP_NAME_GT_GENERAL, upperBound, QueryConstants.OP_NAME_LT_GENERAL, decimal);
    }

    /**
     * Creates a range property predicate based on the specified input parameters.
     *
     * @param propertyName   the name of the property to apply the range condition to
     * @param lowerBound     the lower bound value of the range; can be null or blank if not required
     * @param lowerOperation the operation for the lower bound (e.g., ">", ">="); ignored if lowerBound is null or blank
     * @param upperBound     the upper bound value of the range; can be null or blank if not required
     * @param upperOperation the operation for the upper bound (e.g., "<", "<="); ignored if upperBound is null or blank
     * @param decimal        a flag indicating whether the property is decimal-based
     * @return a PredicateGroup representing the range property predicate
     */
    public @NonNull PredicateGroup createRangePropertyPredicate(@NonNull final String propertyName, @Nullable final String lowerBound, @Nullable final String lowerOperation,
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
     * @param lowerBound     the lower bound of the date range can be null or empty if not applicable
     * @param lowerOperation the comparison operation to use for the lower bound (e.g., ">=", ">", etc.)
     * @param upperBound     the upper bound of the date range can be null or empty if not applicable
     * @param upperOperation the comparison operation to use for the upper bound (e.g., "<=", "<", etc.)
     * @return a PredicateGroup containing the configured date range property and bounds, or an empty group if no bounds are provided
     */
    public @NonNull PredicateGroup createDateRangePropertyPredicate(@NonNull final String propertyName, @Nullable final String lowerBound, @Nullable final String lowerOperation,
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
    public @NonNull PredicateGroup createRelativeDateRangePropertyPredicate(@NonNull final String propertyName, @Nullable final String lowerBound, @Nullable final String upperBound) {
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
     * Compiles the current configuration and builds an executable AEM Query.
     *
     * @param queryBuilder     The QueryBuilder service.
     * @param resourceResolver The resolver used to execute the search.
     * @return A fully configured Query object.
     */
    public @NonNull Query toQuery(@NonNull final QueryBuilder queryBuilder, @NonNull final ResourceResolver resourceResolver) {
        final Query query = queryBuilder.createQuery(getAllPredicateGroups(), sessionOf(resourceResolver));
        // prevent Unclosed ResourceResolver error
        QuerySearchUtil.setResourceResolverOn(resourceResolver, query);

        query.setHitsPerPage(hitsPerPage);
        query.setStart(offset);

        return query;
    }

    /**
     * Combines multiple predicate groups into a single comprehensive predicate group by
     * aggregating various predicates and additional predicate groups.
     *
     * @return A unified PredicateGroup containing all the collected predicates.
     */
    private @NonNull PredicateGroup getAllPredicateGroups() {
        final PredicateGroup allPredicates = new PredicateGroup();
        addPredicateIfNotNull(allPredicates, createPathPredicate());
        addPredicateIfNotNull(allPredicates, createTypePredicate());
        addPredicateIfNotNull(allPredicates, createOnOffPredicate(addOnOffTimePredicate));
        addPredicateIfNotNull(allPredicates, createAllRequiredTagsPredicate());
        addPredicateIfNotNull(allPredicates, createOneRequiredTagsPredicate());

        for (final PredicateGroup additional : getAdditionalPredicates()) {
            addPredicateIfNotNull(allPredicates, additional);
        }

        allPredicates.addAll(orderByPredicates);

        return allPredicates;
    }

    private void addPredicateIfNotNull(@NonNull final PredicateGroup predicateGroup, @Nullable final Predicate predicate) {
        if (predicate != null) {
            predicateGroup.add(predicate);
        }
    }

    private @NonNull Predicate createTypePredicate() {
        final Predicate typePredicate = new Predicate(TypePredicateEvaluator.TYPE);
        typePredicate.set(TypePredicateEvaluator.TYPE, primaryType);

        return typePredicate;
    }

    /**
     * Creates and returns a predicate representing the logical combination of paths from the list of search paths.
     * If the list of paths is empty, this method returns null.
     * <p>
     * The predicate generated ensures that not all path conditions are required to match simultaneously.
     * Each path is converted into a predicate and added to a predicate group.
     *
     * @return a Predicate representing the logical combination of path conditions, or null if no paths are specified
     */
    private @Nullable Predicate createPathPredicate() {
        if (paths.isEmpty()) {
            return null;
        }

        final PredicateGroup pathsPredicateGroup = new PredicateGroup();
        pathsPredicateGroup.setAllRequired(false);
        pathsPredicateGroup.addAll(paths.stream()
                .map(searchPathItem -> new Predicate(PathPredicateEvaluator.PATH).set(PathPredicateEvaluator.PATH, searchPathItem))
                .toList()
        );

        return pathsPredicateGroup;
    }

    /**
     * Constructs the logic for activation-based visibility.
     * <p>
     * <b>Logic for On-Time:</b> (OnTime <= Now) OR (OnTime does not exist). <br>
     * <b>Logic for Off-Time:</b> (OffTime > Now) OR (OffTime does not exist).
     * </p>
     *
     * @param addOnOffTimePredicate Flag to toggle this logic.
     * @return The activation PredicateGroup.
     */
    private @Nullable PredicateGroup createOnOffPredicate(final boolean addOnOffTimePredicate) {
        if (!addOnOffTimePredicate) {
            return null;
        }

        final String formattedDate = DATE_FORMAT.format(this.now);
        final Map<String, String> onTime = new HashMap<>();
        onTime.put("group.p.or", "true");
        onTime.put("group.daterange.property", PredicateProperties.ON_TIME);
        onTime.put("group.daterange.upperOperation", "<=");
        onTime.put("group.daterange.upperBound", formattedDate);

        onTime.put("group.property", PredicateProperties.ON_TIME);
        onTime.put("group.property.operation", JcrPropertyPredicateEvaluator.OP_EXISTS);
        onTime.put("group.property.value", "false");

        final Map<String, String> offTime = new HashMap<>();
        offTime.put("group.p.or", "true");
        offTime.put("group.daterange.property", PredicateProperties.OFF_TIME);
        offTime.put("group.daterange.lowerOperation", ">");
        offTime.put("group.daterange.lowerBound", formattedDate);

        offTime.put("group.property", PredicateProperties.OFF_TIME);
        offTime.put("group.property.operation", JcrPropertyPredicateEvaluator.OP_EXISTS);
        offTime.put("group.property.value", "false");

        final PredicateGroup combinedGroup = new PredicateGroup();
        combinedGroup.add(PredicateGroup.create(onTime));
        combinedGroup.add(PredicateGroup.create(offTime));

        return combinedGroup;
    }

    private @NonNull PredicateGroup createSingleTagPredicate(@NonNull final Tag tag) {
        return createTagPredicate(tag, PredicateProperties.CQ_TAGS);
    }

    private @Nullable PredicateGroup createAllRequiredTagsPredicate() {
        return createTagPredicateGroup(allRequiredTags, true);
    }

    private @Nullable PredicateGroup createOneRequiredTagsPredicate() {
        return createTagPredicateGroup(oneRequiredTags, false);
    }

    private @Nullable PredicateGroup createTagPredicateGroup(@Nullable final List<Tag> tags, final boolean allRequired) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }

        final PredicateGroup tagsPredicateGroup = new PredicateGroup();
        tagsPredicateGroup.setAllRequired(allRequired);
        tagsPredicateGroup.addAll(tags.stream()
                .map(this::createSingleTagPredicate)
                .toList());

        return tagsPredicateGroup;
    }

    private @NonNull List<PredicateGroup> getAdditionalPredicates() {
        return new ArrayList<>(additionalPredicates);
    }

    private static class OrderByPredicate extends Predicate {

        OrderByPredicate(@NonNull final String orderByProperty, final boolean ascending) {
            super(Predicate.ORDER_BY);
            set(Predicate.ORDER_BY, orderByProperty);
            set(Predicate.PARAM_SORT, ascending ? Predicate.SORT_ASCENDING : Predicate.SORT_DESCENDING);
        }

    }

}
