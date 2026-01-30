package com.merkle.oss.aem.utils.link;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.merkle.oss.aem.utils.annotations.Generated;
import com.merkle.oss.aem.utils.java.ClassUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Utility class for transforming resource paths into absolute, externalized URLs.
 * <p>
 * This utility provides support for Page objects, raw path strings, and Rich Text content.
 * It leverages the AEM {@link com.day.cq.commons.Externalizer} service to prepend the appropriate
 * scheme and domain based on the current request.
 * </p>
 * By utilizing {@link com.day.cq.commons.Externalizer#absoluteLink(SlingHttpServletRequest, String, String)} as the transformation mechanism,
 * this utility provides safe externalizer functionality for multi tenancy projects based on valid resource resoler mapping configurations.
 *
 */
public class LinkExternalizerUtil {

    @Generated("Bypass coverage for static utility constructor")
    private LinkExternalizerUtil() {
        ClassUtil.assertNoInstance(this.getClass());
    }

    /**
     * Externalizes a {@link com.day.cq.wcm.api.Page} path to an absolute URL.
     * <p>
     * If the page is invalid (e.g., hidden or expired), an empty string is returned.
     * </p>
     * Externalizes the given resource path as an absolute URL based on the request.
     * The hostname (and port) are taken from the resource resolver mapping configuration,
     * if present, or dynamically from the current request using
     * ServletRequest.getServerName() and ServletRequest.getServerPort(), while the scheme is given as an argument.
     *
     * @param page    The AEM Page to externalize.
     * @param request a sling http request object (required for host, port, context path, and sling resource resolver mapping)
     * @return An absolute URL string, or an empty string if the page is invalid.
     * @throws NullPointerException if the request or page is null.
     */
    public static @NonNull String externalize(@NonNull final Page page, @NonNull final SlingHttpServletRequest request) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(page);

        if (!page.isValid()) {
            return StringUtils.EMPTY;
        }

        return absoluteLink(page.getPath(), request);
    }

    /**
     * Externalizes a string path to an absolute URL.
     * <p>
     * This method first checks if the path is considered "external" via {@link LinkUtil}.
     * If the path is external already, the path itself is returned.
     * </p>
     * Externalizes the given resource path as an absolute URL based on the request.
     * The hostname (and port) are taken from the resource resolver mapping configuration,
     * if present, or dynamically from the current request using
     * ServletRequest.getServerName() and ServletRequest.getServerPort(), while the scheme is given as an argument.
     *
     * @param path    a resource path; might contain extension, query or fragment, but plain paths are recommended; has to be without a context path
     * @param request a sling http request object (required for host, port, context path, and sling resource resolver mapping)
     * @return An absolute URL string, or an empty string if the path is internal.
     * @throws NullPointerException if the request or path is null.
     */
    public static @NonNull String externalize(@NonNull final String path, @NonNull final SlingHttpServletRequest request) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(path);

        if (!LinkUtil.isRelativ(path)) {
            return path;
        }

        return absoluteLink(path, request);
    }

    /**
     * Parses a Rich Text string and externalizes all {@code <a>} tag {@code href} attributes.
     * <p>
     * This method uses JSoup to parse the HTML, appends {@code .html} extensions where
     * missing via {@link LinkUtil}, and then externalizes the resulting paths.
     * </p>
     * Externalizes the given resource paths within the text as an absolute URL based on the request.
     * The hostname (and port) are taken from the resource resolver mapping configuration,
     * if present, or dynamically from the current request using
     * ServletRequest.getServerName() and ServletRequest.getServerPort(), while the scheme is given as an argument.
     *
     * @param richText The HTML string containing links to be processed.
     * @param request  a sling http request object (required for host, port, context path, and sling resource resolver mapping)
     * @return The processed HTML string with externalized links.
     * @throws NullPointerException if the request or richText is null.
     */
    public static @NonNull String externalizeRichTextLinks(@NonNull final String richText, @NonNull final SlingHttpServletRequest request) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(richText);

        final Document document = Jsoup.parse(richText);
        document.select("a").forEach(data -> data.attr("href", absoluteLink(LinkUtil.appendHtml(data.attr("href")), request)));

        return document.body().html();
    }

    /**
     * Internal helper to perform the actual externalization via the AEM Externalizer service.
     *
     * @param path    The path to externalize.
     * @param request The current Sling request.
     * @return The absolute link produced by the Externalizer.
     * @throws NullPointerException if the Externalizer service cannot be adapted from the request resolver.
     */
    private static @NonNull String absoluteLink(@NonNull final String path, @NonNull final SlingHttpServletRequest request) {
        final ResourceResolver resolver = request.getResourceResolver();
        final Externalizer externalizer = resolver.adaptTo(Externalizer.class);
        Objects.requireNonNull(externalizer);

        return externalizer.absoluteLink(request, request.getScheme(), path);
    }

}
