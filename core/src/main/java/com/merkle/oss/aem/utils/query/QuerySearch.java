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
import org.apache.sling.api.resource.ResourceResolver;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static com.merkle.oss.aem.utils.sling.SlingUtil.sessionOf;

/**
 * A builder-style utility to simplify the creation of AEM {@link com.day.cq.search.Query} objects.
 * <p>
 * This class streamlines the assembly of common search criteria such as paths, tags, primary types,
 * and pagination. An automatic inclusion of On/Off Time predicates
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

    private boolean addOnOffTimePredicate;

    private long hitsPerPage = DEFAULT_HITS_PER_PAGE;

    private long offset = DEFAULT_SEARCH_RESULT_OFFSET;

    private List<String> paths = new ArrayList<>();

    private List<Tag> allRequiredTags;

    private List<Tag> oneRequiredTags;

    private final List<Predicate> orderByPredicates = new ArrayList<>();

    private final List<PredicateGroup> additionalPredicates = new LinkedList<>();

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
     * Adds a sorting rule to the query. Multiple calls define a composite sort order.
     *
     * @param orderByProperty The JCR property to sort by.
     * @param ascending       {@code true} for A-Z, {@code false} for Z-A.
     */
    public void addOrderByPredicate(@NonNull final String orderByProperty, final boolean ascending) {
        orderByPredicates.add(new OrderByPredicate(orderByProperty, ascending));
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
     * Logic for On-Time: (OnTime <= Now) OR (OnTime does not exist). <br>
     * Logic for Off-Time: (OffTime > Now) OR (OffTime does not exist).
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
        return QuerySearchUtil.createTagPredicate(tag, PredicateProperties.CQ_TAGS);
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
