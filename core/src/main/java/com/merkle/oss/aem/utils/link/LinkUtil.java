package com.merkle.oss.aem.utils.link;

import com.day.cq.dam.api.DamConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.merkle.oss.aem.utils.constants.FileType;
import com.merkle.oss.aem.utils.java.ClassUtil;
import com.merkle.oss.aem.utils.link.constants.Links;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Central utility for validating, formatting, and manipulating URLs and JCR paths.
 * <p>
 * This class provides methods to handle AEM-specific link logic, such as appending {@code .html}
 * extensions to internal pages while ignoring DAM assets and external application links.
 *
 * @apiNote These methods do not apply Sling Resource Mapping. For shortening or
 * mapping paths, use {@link LinkMappingUtil}.
 *
 */
public class LinkUtil {

    private LinkUtil() {
        ClassUtil.assertNoInstance(this.getClass());
    }

    /**
     * Creates a link from a {@link Page} object.
     * <p>
     * The link is only returned if the page is valid (not hidden/expired).
     * Resource mapping is not applied.
     * </p>
     * <b>Note:</b> This method does not apply Sling Resource Mapping. For shortening or
     * mapping paths, use {@link LinkMappingUtil}.
     *
     * @param page The page to transform into a link.
     * @return The page path with an extension, or {@code null} if the page is null or invalid.
     */
    public static @Nullable String createLink(@Nullable final Page page) {
        return Optional.ofNullable(page)
                .filter(Page::isValid)
                .map(validPage -> validPage.adaptTo(Resource.class))
                .map(Resource::getResourceResolver)
                .map(resourceResolver -> createLink(page.getPath(), resourceResolver, false))
                .orElse(null);
    }

    /**
     * Creates a link from a path string.
     * <p>
     * Performs page validation if the path points to an internal resource.
     * </p>
     * <b>Note:</b> This method does not apply Sling Resource Mapping. For shortening or
     * mapping paths, use {@link LinkMappingUtil}.
     * <p>
     *
     * @param path             The path string.
     * @param resourceResolver The resolver to check page existence and validity.
     * @return The processed path, or {@code null} if an internal page is invalid.
     * @throws NullPointerException if the path or resourceResolver is null.
     */
    public static @Nullable String createLink(@NonNull final String path, @NonNull final ResourceResolver resourceResolver) {
        return createLink(path, resourceResolver, true);
    }

    /**
     * Internal implementation for link creation.
     * <p>
     * Normalizes internal paths by ensuring a leading slash and appends {@code .html}
     * extensions where appropriate.
     * </p>
     *
     * @param path                The source path.
     * @param resourceResolver    The resolver for JCR lookups.
     * @param needsPageValidation Whether to perform {@link Page#isValid()} checks.
     * @return The formatted path string or {@code null} if validation fails.
     */
    private static @Nullable String createLink(@NonNull String path, @NonNull final ResourceResolver resourceResolver, final boolean needsPageValidation) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(resourceResolver);

        // If an internal link starts without a slash, add it. This can happen during copy and paste
        if (!path.contains(Links.REQUEST_SCHEME_EXTENSION) && !path.startsWith(Links.SLASH) && !isApplicationLink(path)) {
            path = Links.SLASH + path;
        }

        if (needsPageValidation) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            if (pageManager == null) {
                return null;
            }

            final Page page = pageManager.getPage(path);
            if (page != null && !page.isValid()) {
                return null;
            }
        }

        return appendHtmlExtensionIfMissing(path);
    }

    /**
     * Checks if a specific JCR path represents a valid AEM Page.
     *
     * @param pagePath         The path to check.
     * @param resourceResolver The resolver for JCR access.
     * @return {@code true} if the path exists as a page and is currently valid.
     */
    public static boolean isValidInternalLink(@Nullable final String pagePath, @NonNull final ResourceResolver resourceResolver) {
        Objects.requireNonNull(resourceResolver);

        if (StringUtils.isEmpty(pagePath)) {
            return false;
        }

        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            return false;
        }

        final Page page = pageManager.getPage(pagePath);
        return page != null && page.isValid();
    }

    /**
     * Extracts the relative path from an absolute URL.
     * <p>
     * For example, transforms {@code https://www.example.com/content/home.html}
     * into {@code /content/home.html}.
     * </p>
     *
     * @param absolutePath The absolute URL.
     * @return The relative path, or the original string if it was not an absolute URL.
     */
    public static @NonNull String getRelativeFromAbsolutePath(@NonNull final String absolutePath) {
        Objects.requireNonNull(absolutePath);

        return Optional.of(absolutePath)
                .filter(path -> Strings.CS.startsWithAny(path, Links.HTTP, Links.GENERIC_PROTOCOL_PREFIX))
                .map(LinkUtil::removeProtocolFromPath)
                .map(LinkUtil::removeHostFromPath)
                .orElse(absolutePath);
    }

    private static @Nullable String removeProtocolFromPath(@Nullable final String path) {
        return StringUtils.substringAfter(path, Links.GENERIC_PROTOCOL_PREFIX);
    }

    private static @NonNull String removeHostFromPath(@Nullable final String path) {
        return Links.SLASH + StringUtils.substringAfter(path, Links.SLASH);
    }

    /**
     * Appends the {@code .html} extension to the path if it is considered missing.
     *
     * @param path The path to process.
     * @return The path with an extension appended if necessary.
     * @see #isMissingHtmlExtension(String)
     */
    public static @NonNull String appendHtmlExtensionIfMissing(@NonNull final String path) {
        Objects.requireNonNull(path);

        if (isMissingHtmlExtension(path)) {
            return path + FileType.HTML.toDotExtension();
        }

        return path;
    }

    /**
     * Determines if a path requires an {@code .html} extension.
     * <p>
     * Returns {@code false} for DAM assets (e.g. {@code /content/dam/...}), external
     * links, or paths that already contain an extension, query string, or fragment.
     * </p>
     *
     * @param path The path to check.
     * @return {@code true} if the path is an internal JCR path without an extension.
     */
    public static boolean isMissingHtmlExtension(@NonNull final String path) {
        Objects.requireNonNull(path);

        if (isLinkToDAM(path) || !isInternalLink(path)) {
            return false;
        }

        return !path.matches(".+\\.html(|\\?.*|#.*)$")
                && !path.matches(".+/(([#][^#/?]*)?|([?][^#/?]*))$");
    }

    /**
     * Returns _blank if openNewWindow is true. Otherwise, _self
     *
     * @param openNewWindow open in a new window
     * @return Returns _blank if openNewWindow is true. Otherwise, _self
     */
    public static String getTarget(final boolean openNewWindow) {
        return openNewWindow ? Links.Target.BLANK.getTarget() : Links.Target.SELF.getTarget();
    }

    /**
     * Checks if a link is internal (starts with a forward slash).
     *
     * @param link The link/path to check.
     * @return {@code true} if the link starts with {@code /}.
     */
    public static boolean isInternalLink(@Nullable final String link) {
        return StringUtils.isNotBlank(link) && link.trim().startsWith(Links.SLASH);
    }

    /**
     * Checks if a path points to a DAM asset.
     *
     * @param link The path to check.
     * @return {@code true} if the path starts with the DAM mountpoint ({@code /content/dam}).
     */
    public static boolean isLinkToDAM(@Nullable final String link) {
        return StringUtils.isNotBlank(link) && Strings.CS.startsWith(link, DamConstants.MOUNTPOINT_ASSETS);
    }

    /**
     * Checks if a path uses a specific application protocol (e.g., mailto, tel, or sip).
     *
     * @param path The path to check.
     * @return {@code true} if the path starts with an application-specific scheme.
     */
    public static boolean isApplicationLink(@NonNull final String path) {
        Objects.requireNonNull(path);

        return Stream.of(Links.APP_LINK_MAILTO, Links.APP_LINK_TEL, Links.APP_LINK_SESSION, Links.APP_LINK_FILE,
                        Links.APP_LINK_FTP, Links.APP_LINK_IMAP, Links.APP_LINK_IRC, Links.APP_LINK_NNTP)
                .parallel()
                .anyMatch(path::startsWith);
    }

}
