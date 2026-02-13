package com.merkle.oss.aem.utils.sling;

import com.day.cq.wcm.api.Page;
import com.merkle.oss.aem.utils.annotations.tooling.Generated;
import com.merkle.oss.aem.utils.java.ClassUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.jcr.Session;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Static utility methods for common Apache Sling and AEM operations.
 * <p>
 * This class provides type-safe shortcuts for object adaptation.
 */
public final class SlingUtil {

    @Generated("Bypass coverage for static utility constructor")
    private SlingUtil() {
        ClassUtil.assertNoInstance(this.getClass());
    }

    /**
     * Creates a {@link Function} that adapts an {@link org.apache.sling.api.adapter.Adaptable} input to the specified target class.
     * <p>
     * This utility is designed for use in {@link Optional#map(Function)} or {@link java.util.stream.Stream#map(Function)}
     * pipelines to make code more readable.
     *
     * @param adapt The class to adapt to (must not be null).
     * @param <T>   The type of the adapter.
     * @return A non-null Function that returns the adapted object or {@code null} if adaptation fails.
     * @apiNote Example usage:
     * {@snippet :
     * Optional.ofNullable(resource)
     *     .map(SlingUtil.to(Page.class))
     *     .map(page -> ...);
     *}
     */
    public static <T> @NonNull Function<Adaptable, @Nullable T> to(@NonNull final Class<T> adapt) {
        Objects.requireNonNull(adapt);

        return adaptable -> adaptable.adaptTo(adapt);
    }

    /**
     * Retrieves the underlying JCR {@link javax.jcr.Session} from a {@link com.day.cq.wcm.api.Page}.
     * <p>
     * This method resolves the session via: {@code Page -> Content Resource -> ResourceResolver -> Session}.
     *
     * @param page The AEM Page (must have a valid content resource).
     * @return The JCR Session, or {@code null} if the underlying resolver cannot adapt to a session.
     * @throws NullPointerException if {@code page.getContentResource()} is null.
     */
    public static @Nullable Session sessionOf(@NonNull final Page page) {
        Objects.requireNonNull(page.getContentResource());

        return sessionOf(page.getContentResource());
    }

    /**
     * Retrieves the underlying JCR {@link javax.jcr.Session} from a {@link org.apache.sling.api.resource.Resource}.
     * <p>
     * This method resolves the session via: {@code Resource -> ResourceResolver -> Session}.
     *
     * @param resource The Sling Resource.
     * @return The JCR Session, or {@code null} if the resource's resolver cannot adapt to a session.
     */
    public static @Nullable Session sessionOf(@NonNull final Resource resource) {
        return sessionOf(resource.getResourceResolver());
    }

    /**
     * Retrieves the underlying JCR {@link javax.jcr.Session} from a {@link org.apache.sling.api.SlingHttpServletRequest}.
     * <p>
     * This method resolves the session via: {@code Request -> ResourceResolver -> Session}.
     *
     * @param request The current Sling Request.
     * @return The JCR Session, or {@code null} if the request's resolver cannot adapt to a session.
     */
    public static @Nullable Session sessionOf(@NonNull final SlingHttpServletRequest request) {
        return sessionOf(request.getResourceResolver());
    }

    /**
     * Retrieves the underlying JCR {@link Session} from a {@link ResourceResolver}.
     *
     * @param resourceResolver The ResourceResolver.
     * @return The JCR Session, or {@code null} if the resolver is not backed by a JCR provider (e.g., Resource Providers).
     */
    public static @Nullable Session sessionOf(@NonNull final ResourceResolver resourceResolver) {
        Objects.requireNonNull(resourceResolver);

        return resourceResolver.adaptTo(Session.class);
    }

    /**
     * Resolves a Context-Aware Configuration (CAConfig) for a given Page.
     * <p>
     * This is a convenience wrapper around {@link ConfigurationBuilder}.
     *
     * @param page        The context page (can be null, in which case the result is null).
     * @param configClass The annotation class defining the CAConfig.
     * @param <C>         The type of the configuration class.
     * @return The resolved configuration object, or {@code null} if the page is null or adaptation fails.
     * @apiNote Example usage:
     * {@snippet :
     * SiteConfig config = SlingUtil.caConfigOf(currentPage, SiteConfig.class);
     * if (config != null) {
     *     // use config...
     * }
     *}
     * @see org.apache.sling.caconfig.ConfigurationBuilder
     */
    public static <C> @Nullable C caConfigOf(@Nullable final Page page, @NonNull final Class<C> configClass) {
        Objects.requireNonNull(configClass);

        return Optional.ofNullable(page)
                .map(to(ConfigurationBuilder.class))
                .map(configurationBuilder -> configurationBuilder.as(configClass))
                .orElse(null);
    }

}
