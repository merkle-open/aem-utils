package com.namics.oss.aem.utils;

import com.day.cq.dam.api.DamConstants;
import com.namics.oss.aem.constant.Links;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static com.namics.oss.aem.constant.Links.HTML_EXTENSION;

/**
 * Methods for handling links
 */
public class LinkUtil {

    /**
     * Checks if a link is an internal link
     *
     * @param link Link to check
     * @return true, if link starts with "/", otherwise false
     */
    public static boolean isInternalLink(final String link) {
        return StringUtils.isNotBlank(link) && link.trim().startsWith("/");
    }

    /**
     * Returns the relative path of an absolute path. If the path is already relative, the relative path is returned without any changes.
     *
     * @param absolutePath absolute path
     * @return relative path
     */
    public static String getRelativePathFromAbsoluteInternalLink(String absolutePath) {
        return Optional.of(absolutePath)
                .filter(path -> StringUtils.startsWithAny(path, Links.HTTP, Links.GENERIC_PROTOCOL_PREFIX ))
                .map(LinkUtil::removeProtocolFromPath)
                .map(LinkUtil::removeHostFromPath)
                .orElse(absolutePath);
    }

    private static String removeProtocolFromPath(String path) {
        return StringUtils.substringAfter(path, "//");
    }

    private static String removeHostFromPath(String path) {
        return "/" + StringUtils.substringAfter(path, "/");
    }

    /**
     * Add .html extension to path if necessary
     *
     * @param path to append extension
     * @return path with .html extension
     */
    public static String appendHtmlExtensionIfMissing(String path) {
        if (isMappedPathMissingHtmlExtension(path)) {
            return path + HTML_EXTENSION;
        }
        return path;
    }

    /**
     * Checks if .html extension is missing
     *
     * @param path to check
     * @return true, if the link is not a link to DAM and if .html extension is missing, otherwise false
     */
    public static boolean isMappedPathMissingHtmlExtension(String path) {
        if (StringUtils.isEmpty(path) || isLinkToDAM(path)) {
            return false;
        }
        return path.startsWith("/") && !path.matches(".+\\.html(|\\?.*|#.*)$") && !path.matches(".+/(([#][^#/?]*)?|([?][^#/?]*))$");
    }

    /**
     * Checks if the link is a link to the DAM
     *
     * @param link to check
     * @return true, if the link starts with "/content/dam"
     */
    public static boolean isLinkToDAM(String link) {
        return StringUtils.startsWith(link, DamConstants.MOUNTPOINT_ASSETS);
    }
}
