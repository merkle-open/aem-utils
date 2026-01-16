package com.merkle.oss.aem.utils.link;

import com.merkle.oss.aem.utils.java.ClassUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Utility for applying Sling Resource Mapping to paths.
 * <p>
 * This utility wraps the {@link ResourceResolver#map} functionality to provide a consistent
 * way of shortening internal paths, ensuring they are relative, and appending necessary
 * extensions. It is designed to safely ignore external links.
 */
public final class LinkMappingUtil {

    private LinkMappingUtil() {
        ClassUtil.assertNoInstance(this.getClass());
    }

    /**
     * Applies resource mapping to an internal path using the provided resolver.
     * <p>
     * Returns a path mapped from the provided path applying the reverse mapping used by the {@link ResourceResolver#resolve(String)}
     * such that when the path is given to the {@link ResourceResolver#resolve(String)} method, the same resource is returned.
     *
     * @param path             The path to map.
     * @param resourceResolver The resolver used for mapping.
     * @return The mapped and processed path.
     * @see #applyResourceMapping(String, SlingHttpServletRequest, ResourceResolver)
     */
    public static @NonNull String applyResourceMapping(@NonNull final String path, @NonNull final ResourceResolver resourceResolver) {
        return applyResourceMapping(path, null, resourceResolver);
    }

    /**
     * Applies resource mapping to an internal path using the current request.
     * <p>
     * Returns a URL mapped from the provided path applying the reverse mapping used by the {@link ResourceResolver#resolve(HttpServletRequest, String)}
     * such that when the path is given to the {@link ResourceResolver#resolve(HttpServletRequest, String)} method, the same resource is returned.
     *
     * @param path    The path to map.
     * @param request The current request used for context-aware mapping.
     * @return The mapped and processed path.
     * @see #applyResourceMapping(String, SlingHttpServletRequest, ResourceResolver)
     */
    public static @NonNull String applyResourceMapping(@NonNull final String path, @NonNull final SlingHttpServletRequest request) {
        return applyResourceMapping(path, request, request.getResourceResolver());
    }

    /**
     * Applies resource mapping and post-processes the result.
     * <p>
     * The following logic is applied:
     * <ol>
     * <li>Checks if the path is internal via {@link LinkUtil#isInternalLink(String)}. If not, returns the path as-is.</li>
     * <li>Applies {@link ResourceResolver#map(String)} (or request-aware map if provided).</li>
     * </ol>
     *
     * @param path             The path to map.
     * @param request          The current request (optional context).
     * @param resourceResolver The resource resolver (required).
     * @return The mapped, relative, and extended path.
     * @throws NullPointerException if the path or resourceResolver is null.
     */
    public static @NonNull String applyResourceMapping(@NonNull final String path, @Nullable final SlingHttpServletRequest request, @NonNull final ResourceResolver resourceResolver) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(resourceResolver);

        if (!LinkUtil.isInternalLink(path)) {
            return path;
        }

        return Optional.of(path)
                .map(resolverMap(request, resourceResolver))
                .map(LinkUtil::getRelativeFromAbsolutePath)
                .orElse(path);
    }

    /**
     * Determines the appropriate mapping function based on whether a request is available.
     *
     * @param request          The current request.
     * @param resourceResolver The resource resolver.
     * @return A function that maps a string path.
     */
    private static @NonNull Function<String, String> resolverMap(@Nullable final SlingHttpServletRequest request, @NonNull final ResourceResolver resourceResolver) {
        return path -> request == null ? resourceResolver.map(path) : resourceResolver.map(request, path);
    }

}
