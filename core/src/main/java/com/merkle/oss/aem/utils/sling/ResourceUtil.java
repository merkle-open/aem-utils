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

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
     * Traverses the resource hierarchy downward from the immediate children of the {@code parent},
     * returning a stream of resources that match the specified resource types.
     * <p>
     * This traversal is exclusive of the {@code parent} resource and performs a lazy,
     * pre-order search. The {@code typeSet} is evaluated against each resource's
     * {@link org.apache.sling.api.resource.Resource#getResourceType()}.
     *
     * @param parent        the starting resource whose descendants will be searched; may be {@code null}
     * @param resourceTypes the resource types to include in the resulting stream;
     * @return a {@link Stream} of matching descendant {@code Resource}
     */
    public static @NonNull Stream<Resource> streamDescendantsByTypes(@Nullable final Resource parent, @Nullable final String... resourceTypes) {
        return streamDescendantsByTypes(parent, 0, resourceTypes);
    }

    /**
     * Traverses the resource hierarchy downward from the immediate children of the {@code parent},
     * returning a stream of resources that match the specified resource types.
     * <p>
     * This traversal is exclusive of the {@code parent} resource and performs a lazy,
     * pre-order search. The {@code typeSet} is evaluated against each resource's
     * {@link org.apache.sling.api.resource.Resource#getResourceType()}.
     *
     * @param parent        the starting resource whose descendants will be searched; may be {@code null}
     * @param maxDepth      the maximum depth of the traversal. {@code 1} limits the search to
     *                      immediate children; {@code 0} or less allows for infinite depth.
     * @param resourceTypes the resource types to include in the resulting stream;
     * @return a {@link Stream} of matching descendant {@code Resource}
     */
    public static @NonNull Stream<Resource> streamDescendantsByTypes(@Nullable final Resource parent, final int maxDepth, @Nullable final String... resourceTypes) {
        if (Objects.isNull(parent) || ArrayUtils.isEmpty(resourceTypes)) {
            return Stream.empty();
        }

        final Set<String> typeSet = Set.of(resourceTypes);

        return FunctionalUtil.streamDescendants(parent, Resource::listChildren, maxDepth)
                .filter(resource -> typeSet.contains(resource.getResourceType()));
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
    public static @NonNull Optional<Resource> findClosestAncestorByTypes(@Nullable final Resource currentResource, @Nullable final String... resourceTypes) {
        if (currentResource == null || ArrayUtils.isEmpty(resourceTypes)) {
            return Optional.empty();
        }

        final Set<String> typeSet = Set.of(resourceTypes);

        return FunctionalUtil.findClosestAncestorByPredicate(currentResource.getParent(), Resource::getParent, resource -> typeSet.contains(resource.getResourceType()));
    }

}
