package com.namics.oss.aem.utils;

import org.apache.sling.api.resource.ResourceResolver;

import java.util.Optional;

/**
 * Methods to create mapped links based on sling resource mapping.
 */
public final class LinkMappingUtil {

    /**
     * Applies the resource mapping on a given linkPath.
     *
     * @param linkPath         to apply mapping to
     * @param resourceResolver to map link path with
     * @return optional of mapped linkPath
     */
    public static Optional<String> applyResourceMapping(String linkPath, ResourceResolver resourceResolver) {
        return applyResourceMapping(Optional.ofNullable(linkPath), resourceResolver);
    }

    /**
     * Applies the resource mapping on a given linkPath.
     *
     * @param linkPath         optional, apply mapping if available
     * @param resourceResolver to map linkPath path with
     * @return optional of mapped linkPath
     */
    public static Optional<String> applyResourceMapping(Optional<String> linkPath, ResourceResolver resourceResolver) {
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
                .map(l -> resourceResolver.map(l))
                .map(LinkUtil::getRelativePathFromAbsoluteInternalLink)
                .map(LinkUtil::appendHtmlExtensionIfMissing);
    }

}