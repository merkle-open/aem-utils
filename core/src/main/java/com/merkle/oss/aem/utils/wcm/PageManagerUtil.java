package com.merkle.oss.aem.utils.wcm;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.merkle.oss.aem.utils.annotations.Generated;
import com.merkle.oss.aem.utils.java.ClassUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Utility class for interacting with the AEM {@link com.day.cq.wcm.api.PageManager}.
 * <p>
 * Provides convenience methods to adapt objects (Requests, Resources, Resolvers) into a {@code PageManager}
 * and to resolve the containing {@link com.day.cq.wcm.api.Page} for various inputs.
 * </p>
 */
public final class PageManagerUtil {

    @Generated("Bypass coverage for static utility constructor")
    private PageManagerUtil() {
        ClassUtil.assertNoInstance(this.getClass());
    }

    /**
     * Retrieves the {@link com.day.cq.wcm.api.PageManager} associated with the given Sling Request.
     * <p>
     * This is a convenience wrapper that obtains the {@link org.apache.sling.api.resource.ResourceResolver} from the request
     * and adapts it.
     *
     * @param request The current Sling request (must not be null).
     * @return The PageManager, or {@code null} if the resolver cannot be adapted (e.g., due to permission issues).
     * @throws NullPointerException if {@code request} is null.
     */
    public static @Nullable PageManager pageManagerOf(@NonNull final SlingHttpServletRequest request) {
        return pageManagerOf(request.getResourceResolver());
    }

    /**
     * Retrieves the {@link com.day.cq.wcm.api.PageManager} associated with the given Resource's resolver.
     *
     * @param resource The resource (must not be null).
     * @return The PageManager, or {@code null} if the resolver cannot be adapted.
     * @throws NullPointerException if {@code resource} is null.
     */
    public static @Nullable PageManager pageManagerOf(@NonNull final Resource resource) {
        return pageManagerOf(resource.getResourceResolver());
    }

    /**
     * Adapts the given {@link org.apache.sling.api.resource.ResourceResolver} to a {@link com.day.cq.wcm.api.PageManager}.
     *
     * @param resourceResolver The resource resolver (must not be null).
     * @return The PageManager, or {@code null} if adaptation fails.
     * @throws NullPointerException if {@code resourceResolver} is null.
     */
    public static @Nullable PageManager pageManagerOf(@NonNull final ResourceResolver resourceResolver) {
        Objects.requireNonNull(resourceResolver);

        return resourceResolver.adaptTo(PageManager.class);
    }

    /**
     * Resolves the AEM {@link com.day.cq.wcm.api.Page} that contains the resource associated with the current request.
     * <p>
     *
     * @param request The current Sling request (must not be null).
     * @return The containing {@link com.day.cq.wcm.api.Page}, or {@code null} if the resource is not retriveable.
     * @throws NullPointerException if {@code request} is null.
     */
    public static @Nullable Page containingPage(@NonNull final SlingHttpServletRequest request) {
        return Optional.ofNullable(pageManagerOf(request))
                .map(pageManager -> pageManager.getContainingPage(request.getResource()))
                .orElse(null);
    }

    /**
     * Resolves the AEM {@link com.day.cq.wcm.api.Page} that contains the given resource.
     * <p>
     *
     * @param resource The resource to check (must not be null).
     * @return The containing {@link com.day.cq.wcm.api.Page}, or {@code null} if the resource is not retrievable.
     * @throws NullPointerException if {@code resource} is null.
     */
    public static @Nullable Page containingPage(@NonNull final Resource resource) {
        return Optional.ofNullable(pageManagerOf(resource))
                .map(pageManager -> pageManager.getContainingPage(resource))
                .orElse(null);
    }

    /**
     * Resolves the AEM {@link com.day.cq.wcm.api.Page} that contains the resource at the specified absolute path.
     *
     * @param path             The absolute path to the resource (e.g., {@code "/content/site/en/home/jcr:content/par"}).
     * @param resourceResolver The resolver used to access the content.
     * @return The containing {@link com.day.cq.wcm.api.Page}, or {@code null} if the path is invalid or no page is found.
     * @throws NullPointerException if {@code path} or {@code resourceResolver} is null.
     */
    public static @Nullable Page containingPage(@NonNull final String path, @NonNull final ResourceResolver resourceResolver) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(resourceResolver);

        final PageManager pageManager = pageManagerOf(resourceResolver);
        if (pageManager == null) {
            return null;
        }

        return pageManager.getContainingPage(path);
    }

}
