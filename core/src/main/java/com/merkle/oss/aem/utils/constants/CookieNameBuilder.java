package com.merkle.oss.aem.utils.constants;

import com.day.cq.wcm.api.Page;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * A builder for constructing standardized, namespaced cookie names.
 * <p>
 * This utility enforces a consistent dot-separated naming convention (e.g., {@code namespace.component.context})
 * to prevent collisions in client-side storage.
 */
public class CookieNameBuilder {

    private static final String COOKIE_NAME_SEGMENT = ".";

    private final StringBuilder stringBuilder;

    /**
     * Initializes a new builder with a strict namespace and optional initial segments.
     * <p>
     * The resulting structure follows the pattern: {@code namespace.segment1.segment2}.
     *
     * @param namespace          The root namespace for the cookie (e.g., projectName). Must not be null.
     * @param cookieSegmentNames Additional initial segments to append immediately.
     * @throws NullPointerException if {@code namespace} or {@code cookieSegmentNames} is null.
     */
    public CookieNameBuilder(@NonNull final String namespace, @NonNull final String... cookieSegmentNames) {
        Objects.requireNonNull(namespace);
        Objects.requireNonNull(cookieSegmentNames);

        this.stringBuilder = new StringBuilder(namespace);
        Arrays.asList(cookieSegmentNames).forEach(this::appendSegment);
    }

    /**
     * Appends names of pages from the hierarchy at specific depths to the cookie name.
     * <p>
     * This method recursively traverses the page hierarchy <b>upwards</b> (from the given page to its parents).
     * If a page's absolute depth matches one of the provided {@code depths}, its name is appended.
     * </p>
     * <p>
     * This method traverses the page hierarchy upwards to collect segments but appends them
     * in <b>top-down order</b> (Shallowest &rarr; Deepest).
     *
     * @param page   The starting page for traversal (can be null, in which case nothing happens).
     * @param depths The absolute JCR depths (e.g., 3, 4) of the pages whose names should be included.
     * @return This {@link CookieNameBuilder} instance for method chaining.
     * @apiNote Example usage:
     * {@snippet :
     * // Given path: /content/mysite/us/en (us=depth 3, en=depth 4)
     * stringBuilder.appendPathSegmentNames(currentPage, 3, 4).toString();
     * // Result: "{cookieName}.us.en"
     *}
     */
    public @NonNull CookieNameBuilder appendPathSegmentNames(@Nullable final Page page, final int... depths) {
        if (ObjectUtils.anyNull(page)) {
            return this;
        }

        if (ArrayUtils.isEmpty(depths)) {
            return this;
        }

        final List<Integer> depthsList = Arrays.stream(depths).boxed().toList();
        final List<String> collectedSegments = new ArrayList<>();
        collectSegments(page, depthsList, Collections.min(depthsList), collectedSegments);
        Collections.reverse(collectedSegments);
        collectedSegments.forEach(this::appendSegment);

        return this;
    }

    private void collectSegments(@NonNull final Page page, @NonNull final List<Integer> depthsList, int minDepth, @NonNull final List<String> collector) {
        if (depthsList.contains(page.getDepth())) {
            collector.add(page.getName());
        }
        if (page.getDepth() > minDepth && page.getParent() != null) {
            collectSegments(page.getParent(), depthsList, minDepth, collector);
        }
    }

    /**
     * Appends a single arbitrary string segment to the cookie name.
     * <p>
     * If the segment is null or blank, it is ignored.
     *
     * @param segment The string to append (e.g., a unique ID or flag).
     * @return This {@link CookieNameBuilder} instance for method chaining.
     */
    public CookieNameBuilder appendSegmentName(@Nullable final String segment) {
        appendSegment(segment);
        return this;
    }

    /**
     * Returns the fully constructed cookie name string.
     *
     * @return The built string (e.g., {@code "project.component.us.en"}).
     */
    @Override
    public @NonNull String toString() {
        return stringBuilder.toString();
    }

    private void appendSegment(@Nullable final String segment) {
        if (StringUtils.isNotBlank(segment)) {
            stringBuilder.append(COOKIE_NAME_SEGMENT).append(segment);
        }
    }

}
