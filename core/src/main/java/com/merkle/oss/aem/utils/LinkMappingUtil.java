package com.merkle.oss.aem.utils;

import org.apache.sling.api.resource.ResourceResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.function.Function;

/**
 * Methods to create mapped links based on sling resource mapping.
 */
public final class LinkMappingUtil {

    /**
     * Applies the resource mapping on a given link path.
     *
     * @param linkPath         to apply mapping to
     * @param resourceResolver to map link path with
     * @return optional of mapped linkPath
     */
    public static Optional<String> applyResourceMapping(String linkPath, ResourceResolver resourceResolver) {
        return applyResourceMapping(Optional.ofNullable(linkPath), null, resourceResolver);
    }

    /**
     * Applies the resource mapping on a given link path.
     *
     * @param linkPath         optional, apply mapping if available
     * @param resourceResolver to map link path with
     * @return optional of mapped linkPath
     */
    public static Optional<String> applyResourceMapping(Optional<String> linkPath, ResourceResolver resourceResolver) {
        return applyResourceMapping(linkPath, null, resourceResolver);
    }

    /**
     * Applies the resource mapping on a given link path.
     * <p>
     * Allows to resolve a link to a an absolute mapped path
     * in regards to the given request scheme.
     *
     * @param linkPath         to apply mapping to
     * @param request          The http servlet request object which may be used to apply more mapping functionality
     * @param resourceResolver to map link path with
     * @return
     */
    public static Optional<String> applyResourceMapping(String linkPath, HttpServletRequest request, ResourceResolver resourceResolver) {
        return applyResourceMapping(Optional.ofNullable(linkPath), request, resourceResolver);
    }

    /**
     * Applies the resource mapping on a given link path.
     * <p>
     * Allows to resolve a link to a an absolute mapped path
     * in regards to the given request scheme.
     *
     * @param linkPath         optional, apply mapping if available
     * @param request          The http servlet request object which may be used to apply more mapping functionality
     * @param resourceResolver to map link path with
     * @return optional of mapped linkPath
     */
    public static Optional<String> applyResourceMapping(Optional<String> linkPath, HttpServletRequest request, ResourceResolver resourceResolver) {
        if (linkPath == null || !linkPath.isPresent()) {
            return Optional.empty();
        }

        final String path = linkPath.get();
        if (!LinkUtil.isInternalLink(path)) {
            return linkPath;
        }

        if (resourceResolver == null) {
            return Optional.empty();
        }
        return linkPath
                .map(resolverMap(request, resourceResolver))
                .map(LinkUtil::getRelativeFromAbsolutePath)
                .map(LinkUtil::appendHtmlExtensionIfMissing);
    }

    private static Function<String, String> resolverMap(HttpServletRequest request, ResourceResolver resourceResolver) {
        return linkPath -> request == null ? resourceResolver.map(linkPath) : resourceResolver.map(request, linkPath);
    }

}
