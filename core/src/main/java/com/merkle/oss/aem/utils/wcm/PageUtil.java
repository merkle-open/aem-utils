package com.merkle.oss.aem.utils.wcm;

import com.day.cq.commons.Filter;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.constants.NameConstants;
import com.merkle.oss.aem.utils.annotations.Generated;
import com.merkle.oss.aem.utils.java.ClassUtil;
import com.merkle.oss.aem.utils.java.FunctionalUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
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
     * Retrieves the basic title of the page (jcr:title).
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
     * Searches upwards through the page hierarchy to find the nearest ancestor matching one of the given templates.
     * <p>
     * The search starts from the <b>parent</b> of the {@code currentPage}.
     *
     * @param currentPage The starting page (search begins at its parent).
     * @param templates   One or more template paths to match.
     * @return An {@link Optional} containing the matching ancestor page, or empty if none found.
     */
    public static @NonNull Optional<Page> findClosestAncestorByTemplate(@Nullable final Page currentPage, @Nullable final String... templates) {
        if (currentPage == null) {
            return Optional.empty();
        }

        if (ArrayUtils.isEmpty(templates)) {
            return Optional.empty();
        }

        Page parent = currentPage.getParent();
        while (parent != null) {
            if (matchesAnyTemplate(parent, templates)) {
                return Optional.of(parent);
            }
            parent = parent.getParent();
        }

        return Optional.empty();
    }

    private static boolean matchesAnyTemplate(@Nullable final Page page, @Nullable final String... templates) {
        return Stream.of(templates)
                .anyMatch(template -> Strings.CS.equals(template, getTemplatePath(page)));
    }

    /**
     * Finds the first <b>direct child</b> page that matches one of the given templates.
     *
     * @param currentPage The parent page to search beneath.
     * @param templates   The template paths to filter by.
     * @return An {@link Optional} containing the first matching child page.
     */
    public static @NonNull Optional<Page> firstChildByTemplate(@Nullable final Page currentPage, @Nullable final String... templates) {
        if (currentPage == null) {
            return Optional.empty();
        }

        if (ArrayUtils.isEmpty(templates)) {
            return Optional.empty();
        }

        return FunctionalUtil.asStream(currentPage.listChildren(filterFor(templates)))
                .filter(PageUtil::isValid)
                .findFirst();
    }

    /**
     * Finds all <b>direct child</b> pages that match one of the given templates.
     *
     * @param currentPage The parent page to search beneath.
     * @param templates   The template paths to filter by.
     * @return A list of matching child pages.
     */
    public static @NonNull List<Page> childrenByTemplate(@Nullable final Page currentPage, @Nullable final String... templates) {
        if (currentPage == null) {
            return Collections.emptyList();
        }

        if (ArrayUtils.isEmpty(templates)) {
            return Collections.emptyList();
        }

        return FunctionalUtil.asStream(currentPage.listChildren(filterFor(templates)))
                .filter(PageUtil::isValid)
                .collect(Collectors.toList());
    }

    private static @NonNull Filter<Page> filterFor(@Nullable final String... templates) {
        return page -> matchesAnyTemplate(page, templates);
    }

    /**
     * Checks if two {@link com.day.cq.wcm.api.Page} objects represent the exact same content path.
     *
     * @param targetPage  The first page to compare.
     * @param currentPage The second page to compare.
     * @return {@code true} if both are non-null and have identical paths; {@code false} otherwise.
     */
    public static boolean equals(@Nullable final Page targetPage, @Nullable final Page currentPage) {
        if (ObjectUtils.allNotNull(targetPage, currentPage)) {
            return Strings.CS.equals(targetPage.getPath(), currentPage.getPath());
        }

        return false;
    }

    /**
     * Streams all descendant pages beneath the parent, traversing deeply up to the specified depth.
     * <p>
     * The {@code parent} itself is <b>not</b> included in the stream.
     *
     * @param parent   The root page of the subtree (excluded in the result).
     * @param maxDepth The maximum absolute depth to traverse (JCR depth). If 0, depth checking is ignored.
     * @return A stream of descendant pages in tree-traversal order.
     */
    public static @NonNull Stream<Page> streamDescendants(@Nullable final Page parent, final int maxDepth) {
        if (parent == null) {
            return Stream.empty();
        }

        return FunctionalUtil.asStream(parent.listChildren(new PageFilter()))
                .flatMap(page -> streamTree(page, maxDepth));
    }

    /**
     * Streams all descendant pages beneath the parent, traversing deeply up to the specified depth.
     * <p>
     * The {@code parent} itself <b>is</b> included in the stream.
     *
     * @param parent   The root of the subtree (included in the result).
     * @param maxDepth The maximum absolute depth to traverse (JCR depth). If 0, depth checking is ignored.
     * @return A stream containing the parent and its descendants in tree-traversal order.
     */
    public static @NonNull Stream<Page> streamTree(@Nullable final Page parent, final int maxDepth) {
        if (parent == null) {
            return Stream.empty();
        }

        if (maxDepth != 0 && parent.getDepth() > maxDepth) {
            return Stream.empty();
        }

        return FunctionalUtil.asStream(parent.listChildren(new PageFilter()))
                .map(page -> streamTree(page, maxDepth))
                .reduce(Stream.of(parent), Stream::concat);
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
     * Retrieves a generic string property from the page's content resource (jcr:content).
     *
     * @param page         The page to inspect.
     * @param propertyName The property name (e.g., "jcr:title").
     * @return The property value, or an empty string if the page/property does not exist.
     * @throws NullPointerException if {@code propertyName} is null.
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

}
