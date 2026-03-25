package com.merkle.oss.aem.utils.wcm;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.constants.NameConstants;
import com.merkle.oss.aem.utils.annotations.tooling.Generated;
import com.merkle.oss.aem.utils.java.ClassUtil;
import com.merkle.oss.aem.utils.java.FunctionalUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Utility class for generic AEM {@link com.day.cq.wcm.api.Page} operations.
 * <p>
 * Provides standardized null safe methods for retrieving page handling.
 */
public final class PageUtil {

    private static final String SLASH = "/";

    @Generated("Bypass coverage for static utility constructor")
    private PageUtil() {
        ClassUtil.assertNoInstance(this.getClass());
    }

    /**
     * Retrieves the basic title of the page {@code jcr:title}.
     *
     * @param page The page to inspect.
     * @return The page title, or an empty string if not available.
     */
    public static @NonNull String getTitle(@Nullable final Page page) {
        return forPage(page,
                p -> StringUtils.defaultString(p.getTitle()));
    }

    /**
     * Retrieves the "Page Title" with a fallback to the standard title.
     * <p>
     * Logic: Returns {@code pageTitle}. If blank, falls back to {@link #getTitle(com.day.cq.wcm.api.Page)}.
     *
     * @param page The page to inspect.
     * @return The page title with a fallback value, or an empty string if not available.
     */
    public static @NonNull String getPageTitle(@Nullable final Page page) {
        return forPage(page,
                p -> StringUtils.defaultIfEmpty(p.getPageTitle(), getTitle(p)));
    }

    /**
     * Retrieves the "Navigation Title" with a full fallback chain.
     * <p>
     * Logic: Returns {@code navTitle}. If blank, falls back to {@link #getPageTitle(com.day.cq.wcm.api.Page)}.
     *
     * @param page The page to inspect.
     * @return TThe navigation title with a fallback value, or an empty string if not available.
     */
    public static @NonNull String getNavigationTitle(@Nullable final Page page) {
        return forPage(page,
                p -> StringUtils.defaultIfEmpty(p.getNavigationTitle(), getPageTitle(p)));
    }

    /**
     * Retrieves the description of the page.
     *
     * @param page The page to inspect.
     * @return The description, or an empty string if not available.
     */
    public static @NonNull String getDescription(@Nullable final Page page) {
        return StringUtils.defaultString(forPage(page, Page::getDescription));
    }

    /**
     * Retrieves the absolute path of the template used by the page.
     *
     * @param page The page to inspect.
     * @return The template path (e.g., {@code /conf/my-site/settings/wcm/templates/page-content}), or empty string if not found.
     */
    public static @NonNull String getTemplatePath(@Nullable final Page page) {
        return getProperty(page, NameConstants.NN_TEMPLATE);
    }

    /**
     * Retrieves the node name of the template used by the page.
     *
     * @param page The page to inspect.
     * @return The template name (e.g., {@code page-content}), extracted from the full path.
     */
    public static @NonNull String getTemplateName(@Nullable final Page page) {
        final String templatePath = getTemplatePath(page);
        if (StringUtils.containsNone(templatePath, SLASH)) {
            return templatePath;
        }
        return StringUtils.substringAfterLast(templatePath, SLASH);
    }

    /**
     * Retrieves a generic string property from the page's content resource (jcr:content).
     *
     * @param page         The page to inspect.
     * @param propertyName The property name (e.g., "jcr:title").
     * @return The property value, or an empty string if the page/property does not exist.
     */
    public static @NonNull String getProperty(@Nullable final Page page, @NonNull final String propertyName) {
        Objects.requireNonNull(propertyName);

        return forPage(page, p -> Objects.requireNonNull(p.getProperties()).get(propertyName, StringUtils.EMPTY));
    }

    /**
     * Internal helper to apply a function to a page with null-safety.
     *
     * @param page     The page input.
     * @param function The function to execute if the page is not null.
     * @return The result of the function, or an empty string if the page is null.
     */
    private static @NonNull String forPage(@Nullable final Page page, @NonNull final Function<Page, String> function) {
        Objects.requireNonNull(function);

        if (page == null) {
            return StringUtils.EMPTY;
        }

        return function.apply(page);
    }

    /**
     * Checks if two {@link com.day.cq.wcm.api.Page} objects represent the exact same content path.
     *
     * @param targetPage  The first page to compare.
     * @param currentPage The second page to compare.
     * @return {@code true} if both are non-null and have identical paths; {@code false} otherwise.
     */
    public static boolean equals(@Nullable final Page targetPage, @Nullable final Page currentPage) {
        if (targetPage != null && currentPage != null) {
            return Strings.CS.equals(targetPage.getPath(), currentPage.getPath());
        }

        return false;
    }

    /**
     * Validates if the page object is non-null and represents a valid AEM page.
     *
     * @param page The page to check.
     * @return {@code true} if the page is not null and {@link com.day.cq.wcm.api.Page#isValid()} returns true.
     * @see com.day.cq.wcm.api.Page#isValid()
     */
    public static boolean isValid(@Nullable final Page page) {
        return page != null && page.isValid();
    }

    /**
     * Traverses the page tree hierarchy downward from the immediate children of the {@code parent},
     * returning a stream of page that match the specified templates.
     * <p>
     * This traversal is exclusive of the {@code parent} resource and performs a lazy,
     * pre-order search. The {@code typeSet} is evaluated against each page's
     * {@link com.day.cq.wcm.api.constants.NameConstants#NN_TEMPLATE}.
     *
     * @param parent    the starting page whose descendants will be searched; may be {@code null}
     * @param templates the templates to include in the resulting stream;
     * @return a {@link Stream} of matching descendant {@code Page}
     */
    public static @NonNull Stream<Page> streamDescendantsByTemplates(@Nullable final Page parent, @Nullable final String... templates) {
        return streamDescendantsByTemplates(parent, 0, templates);
    }

    /**
     * Traverses the page tree hierarchy downward from the immediate children of the {@code parent},
     * returning a stream of page that match the specified templates.
     * <p>
     * This traversal is exclusive of the {@code parent} resource and performs a lazy,
     * pre-order search. The {@code typeSet} is evaluated against each page's
     * {@link com.day.cq.wcm.api.constants.NameConstants#NN_TEMPLATE}.
     *
     * @param parent    the starting page whose descendants will be searched; may be {@code null}
     * @param maxDepth  the maximum depth of the traversal. {@code 1} limits the search to
     *                  immediate children; {@code 0} or less allows for infinite depth.
     * @param templates the templates to include in the resulting stream;
     * @return a {@link Stream} of matching descendant {@code Page}
     */
    public static @NonNull Stream<Page> streamDescendantsByTemplates(@Nullable final Page parent, final int maxDepth, @Nullable final String... templates) {
        if (Objects.isNull(parent) || ArrayUtils.isEmpty(templates)) {
            return Stream.empty();
        }

        final Set<String> templateSet = Set.of(templates);

        return FunctionalUtil.streamDescendants(parent, page -> page.listChildren(new PageFilter()), maxDepth)
                .filter(page -> templateSet.contains(getTemplatePath(page)));
    }


    /**
     * Searches upwards through the page hierarchy to find the nearest ancestor matching one of the given templates.
     * <p>
     * The search starts from the <b>parent</b> of the {@code currentPage}.
     *
     * @param currentPage The starting page (search begins at its parent).
     * @param templates   One or more template paths to match.
     * @return An {@link Optional} containing the matching ancestor page, or empty if none found.
     */
    public static @NonNull Optional<Page> findClosestAncestorByTemplates(@Nullable final Page currentPage, @Nullable final String... templates) {
        if (currentPage == null || ArrayUtils.isEmpty(templates)) {
            return Optional.empty();
        }

        final Set<String> templateSet = Set.of(templates);

        return FunctionalUtil.findClosestAncestorByPredicate(currentPage.getParent(), Page::getParent, page -> templateSet.contains(getTemplatePath(page)));
    }

}
