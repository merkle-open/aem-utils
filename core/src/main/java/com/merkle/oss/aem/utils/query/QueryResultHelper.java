package com.merkle.oss.aem.utils.query;

import com.day.cq.search.result.Hit;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.Optional;

/**
 * Utility for safe and efficient retrieval of AEM Query results.
 * <p>
 * When working with the AEM {@link com.day.cq.search.QueryBuilder}, the {@link com.day.cq.search.result.Hit} objects returned
 * are tied to the session of the ResourceResolver used to execute the query. If that resolver
 * is closed, later calls to {@code hit.getResource()} will fail.
 * </p>
 * <p>
 * This helper solves that problem by "reloading" the resource path using a provided,
 * active {@link org.apache.sling.api.resource.ResourceResolver}.
 */
public final class QueryResultHelper {

    private static final Logger LOG = LoggerFactory.getLogger(QueryResultHelper.class);

    private final ResourceResolver resourceResolver;

    private QueryResultHelper(@NonNull final ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
    }

    /**
     * Factory method to create an instance of the {@code QueryResultHelper}.
     * <p>
     * The provided {@code resourceResolver} will be used to fetch the actual Resource
     * or Page objects from the JCR paths provided by the search hits. This allows the
     * original query-execution resolver to be closed safely.
     *
     * @param resourceResolver The active ResourceResolver to be used for content retrieval.
     * @return A new instance of {@code QueryResultHelper}.
     */
    public static @NonNull QueryResultHelper create(@NonNull final ResourceResolver resourceResolver) {
        return new QueryResultHelper(resourceResolver);
    }

    /**
     * Retrieves the {@link org.apache.sling.api.resource.Resource} corresponding to a query {@link com.day.cq.search.result.Hit}.
     * <p>
     * Instead of calling {@link com.day.cq.search.result.Hit#getResource()}, which depends on the internal query
     * session, this method extracts the path from the hit and resolves it via
     * the helper's internal {@code resourceResolver}.
     *
     * @param hit The search result hit to retrieve the resource for.
     * @return An {@link Optional} containing the resource, or {@link Optional#empty()}
     * if the hit is null or the resource does not exist or
     * a {@link RepositoryException} occurs.
     */
    public @NonNull Optional<Resource> toResource(@Nullable final Hit hit) {
        if (hit == null) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(resourceResolver.getResource(hit.getPath()));
        } catch (RepositoryException e) {
            LOG.error("Could not retrieve resource from AEM query result hit for path: {}", hit, e);
        }

        return Optional.empty();
    }

    /**
     * Converts a query {@link com.day.cq.search.result.Hit} directly into an AEM {@link com.day.cq.wcm.api.Page}.
     * <p>
     * This is a convenience method that first resolves the {@link org.apache.sling.api.resource.Resource} via
     * {@link #toResource(Hit)} and then adapts it to the {@code Page} API.
     *
     * @param hit The search result hit representing a page.
     * @return An {@link Optional} The Page object, or {@link Optional#empty()} if the hit does not point to a
     * valid page or cannot be adapted.
     */
    public @NonNull Optional<Page> toPage(@Nullable final Hit hit) {
        return toResource(hit).map(value -> value.adaptTo(Page.class));
    }

}
