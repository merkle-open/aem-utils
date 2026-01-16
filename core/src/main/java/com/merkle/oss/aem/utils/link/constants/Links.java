package com.merkle.oss.aem.utils.link.constants;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;

/**
 * Common constants and definitions for URL handling and URI manipulation within AEM.
 * <p>
 * This interface provides a centralized location for protocol prefixes, ports,
 * path delimiters, and standard HTML link targets.
 * </p>
 */
public interface Links {
    String APP_LINK_SESSION = "sip:";
    String APP_LINK_TEL = "tel:";
    String APP_LINK_MAILTO = "mailto:";
    String APP_LINK_FILE = "file:";
    String APP_LINK_FTP = "ftp:";
    String APP_LINK_IMAP = "imap:";
    String APP_LINK_IRC = "irc:";
    String APP_LINK_NNTP = "nntp:";

    int HTTP_PORT = 80;
    int HTTPS_PORT = 443;

    String HTTP = "http";
    String HTTPS = "https";
    String HTTP_PREFIX = "http://";
    String HTTPS_PREFIX = "https://";
    String REQUEST_SCHEME_EXTENSION = "://";
    String GENERIC_PROTOCOL_PREFIX = "//";

    String SLASH = "/";
    String FRAGMENT_SEPARATOR = "#";
    String QUERY_STRING_SEPARATOR = "?";
    String QUERY_PARAM_DELIMITER = "&";

    /**
     * Represents standard HTML link target attributes and their associated security relationships.
     * <p>
     */
    enum Target {

        NONE(StringUtils.EMPTY, StringUtils.EMPTY),
        BLANK("_blank", "noopener"),
        SELF("_self", StringUtils.EMPTY),
        PARENT("_parent", StringUtils.EMPTY),
        TOP("_top", StringUtils.EMPTY),
        DOWNLOAD("download", StringUtils.EMPTY);

        private final String target;
        private final String rel;

        /**
         * @param target The HTML {@code target} attribute value.
         * @param rel    The HTML {@code rel} attribute value.
         */
        Target(@NonNull final String target, @NonNull final String rel) {
            this.target = target;
            this.rel = rel;
        }

        /**
         * Resolves a string value to a {@link Target} constant.
         *
         * @param target The raw string target attribute.
         * @return The matching Target constant, or {@link #NONE} if no match is found.
         */
        public static @NonNull Target of(@Nullable final String target) {
            return Arrays.stream(Target.values())
                    .filter(linkTarget -> Strings.CS.equals(linkTarget.getTarget(), target))
                    .findFirst()
                    .orElse(NONE);
        }

        /**
         * Checks if the provided target string represents a "New Window" action.
         *
         * @param target The target attribute to check.
         * @return {@code true} if the target is {@code _blank}.
         */
        public static boolean isOpenInNewWindow(@Nullable final String target) {
            return BLANK == of(target);
        }

        /**
         * @return The value for the {@code target} HTML attribute.
         */
        public @NonNull String getTarget() {
            return target;
        }

        /**
         * @return The value for the {@code rel} HTML attribute (e.g., "noopener").
         */
        public @NonNull String getRel() {
            return rel;
        }

        @Override
        public @NonNull String toString() {
            return getTarget();
        }

    }

}
