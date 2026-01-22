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
     * @return The resolved Resource, or {@code null} if the path is invalid or
     * a {@link RepositoryException} occurs.
     */
    public @Nullable Resource getResource(@Nullable final Hit hit) {
        if (hit == null) {
            return null;
        }

        try {
            return resourceResolver.getResource(hit.getPath());
        } catch (RepositoryException e) {
            LOG.error("Could not retrieve resource from AEM query result hit for path: {}", hit, e);
        }
        return null;
    }

    /**
     * Converts a query {@link com.day.cq.search.result.Hit} directly into an AEM {@link com.day.cq.wcm.api.Page}.
     * <p>
     * This is a convenience method that first resolves the {@link org.apache.sling.api.resource.Resource} via
     * {@link #getResource(Hit)} and then adapts it to the {@code Page} API.
     *
     * @param hit The search result hit representing a page.
     * @return The Page object, or {@code null} if the hit does not point to a
     * valid page or cannot be adapted.
     */
    public @Nullable Page adaptHitToPage(@Nullable final Hit hit) {
        final Resource resource = getResource(hit);
        if (resource == null) {
            return null;
        }

        return resource.adaptTo(Page.class);
    }

}
