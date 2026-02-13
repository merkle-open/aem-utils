package com.merkle.oss.aem.utils.sling;

import com.merkle.oss.aem.utils.annotations.tooling.Generated;
import com.merkle.oss.aem.utils.java.ClassUtil;
import com.merkle.oss.aem.utils.java.FunctionalUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.spi.resource.provider.ResourceProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

/**
 * Utility class for common {@link org.apache.sling.api.resource.Resource} operations in Apache Sling.
 */
public final class ResourceUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceUtil.class);

    @Generated("Bypass coverage for static utility constructor")
    private ResourceUtil() {
        ClassUtil.assertNoInstance(this.getClass());
    }

    /**
     * Validates if the resource is a "real" existing resource that is safe for adaptation.
     * <p>
     * In Sling, certain resources are placeholders. This method returns {@code false} if the resource:
     * <ul>
     *     <li>Is {@code null}</li>
     *     <li>Is an instance of {@link org.apache.sling.api.resource.NonExistingResource}</li>
     *     <li>Has a blank resource type</li>
     *     <li>Has a type matching {@link org.apache.sling.api.resource.Resource#RESOURCE_TYPE_NON_EXISTING}</li>
     *     <li>Has a type matching {@link org.apache.sling.spi.resource.provider.ResourceProvider#RESOURCE_TYPE_SYNTHETIC} (e.g., virtual folders like /apps)</li>
     * </ul>
     *
     * @param resource The resource to validate.
     * @return {@code true} if the resource exists and is not synthetic; {@code false} otherwise.
     * @see org.apache.sling.spi.resource.provider.ResourceProvider#RESOURCE_TYPE_SYNTHETIC
     * @see org.apache.sling.api.resource.NonExistingResource
     */
    public static boolean isValid(@Nullable final Resource resource) {
        if (Objects.isNull(resource)) {
            LOG.debug("Resource was null");
            return false;
        }

        if (resource instanceof NonExistingResource) {
            LOG.debug("Resource was instance of NonExistingResource");
            return false;
        }

        if (StringUtils.isBlank(resource.getResourceType())) {
            LOG.debug("Resource has not resource type");
            return false;
        }

        if (Strings.CS.equals(resource.getResourceType(), Resource.RESOURCE_TYPE_NON_EXISTING)) {
            LOG.debug("ResourceType of resource {} was {}", resource, Resource.RESOURCE_TYPE_NON_EXISTING);
            return false;
        }

        if (Strings.CS.equals(resource.getResourceType(), ResourceProvider.RESOURCE_TYPE_SYNTHETIC)) {
            LOG.debug("ResourceType of resource {} was {}", resource, ResourceProvider.RESOURCE_TYPE_SYNTHETIC);
            return false;
        }

        LOG.debug("Resource {} validation successful", resource);
        return true;
    }

    /**
     * Returns a sequential {@link Stream} of the direct children of the given resource.
     *
     * @param resource The parent resource.
     * @return A non-null stream of child resources. Returns {@link Stream#empty()} if the resource is null.
     */
    public static @NonNull Stream<Resource> childrenAsStream(@Nullable final Resource resource) {
        return Optional.ofNullable(resource)
                .map(Resource::listChildren)
                .map(FunctionalUtil::asStream)
                .orElse(Stream.empty());
    }

    /**
     * Collects all direct children that match any of the specified resource types
     * retrievable via {org.apache.sling.api.resource.Resource#getResourceType()}
     *
     * @param parent        The parent resource to inspect.
     * @param resourceTypes One or more resource types to filter by.
     * @return A non-null list of matching child resources. Returns an empty list if no matches are found or inputs are null/empty.
     */
    public static @NonNull List<Resource> childrenOfTypes(@Nullable final Resource parent, @Nullable final String... resourceTypes) {
        if (Objects.isNull(parent)) {
            return Collections.emptyList();
        }

        if (ArrayUtils.isEmpty(resourceTypes)) {
            return Collections.emptyList();
        }

        final List<String> resourceTypeList = Stream.of(resourceTypes).toList();

        return childrenAsStream(parent)
                .filter(resource -> resourceTypeList.contains(resource.getResourceType()))
                .toList();
    }

    /**
     * Performs a deep recursive search below the given parent to find all resources of specific types.
     * <p>
     * This traverses the entire subtree (all levels deep).
     *
     * @param parent        The root resource from which to start the recursive search.
     * @param resourceTypes The resource types to look for.
     * @return A list of all matching descendant resources found.
     */
    public static @NonNull List<Resource> descendantsOfTypes(@Nullable final Resource parent, @Nullable final String... resourceTypes) {
        if (Objects.isNull(parent)) {
            return Collections.emptyList();
        }

        if (ArrayUtils.isEmpty(resourceTypes)) {
            return Collections.emptyList();
        }

        final List<String> resourceTypeList = Stream.of(resourceTypes).toList();
        final List<Resource> resources = new ArrayList<>();
        collectDescendants(parent, resourceTypeList, resources);
        return resources;
    }

    private static void collectDescendants(final Resource resource, final List<String> resourceTypeList, final List<Resource> resources) {
        resource.listChildren().forEachRemaining(child -> {
            if (resourceTypeList.contains(child.getResourceType())) {
                resources.add(child);
            }
            collectDescendants(child, resourceTypeList, resources);
        });
    }

    /**
     * Searches upwards through the hierarchy to find the nearest ancestor matching any of the specified resource types.
     * <p>
     * The search begins with the immediate parent of the {@code currentResource}.
     *
     * @param currentResource The starting resource.
     * @param resourceTypes   One or more resource types to match.
     * @return An {@link Optional} containing the nearest matching ancestor, or {@link Optional#empty()} if none found.
     */
    public static @NonNull Optional<Resource> findClosestAncestorOfResourceTypes(@Nullable final Resource currentResource, @Nullable final String... resourceTypes) {
        if (ArrayUtils.isEmpty(resourceTypes)) {
            return Optional.empty();
        }

        final List<String> resourceTypeList = Stream.of(resourceTypes).toList();
        return java.util.Optional.ofNullable(currentResource)
                .map(Resource::getParent)
                .flatMap(parent -> resourceTypeList.contains(parent.getResourceType()) ?
                        Optional.of(parent) :
                        findClosestAncestorOfResourceTypes(parent, resourceTypes)
                );
    }

}
