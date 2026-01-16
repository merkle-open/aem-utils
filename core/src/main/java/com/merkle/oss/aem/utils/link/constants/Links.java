package com.merkle.oss.aem.utils.link.constants;

import com.merkle.oss.aem.utils.java.ClassUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;

/**
 * Common constants and definitions for URL handling and URI manipulation within AEM.
 */
public class Links {

    private Links() {
        ClassUtil.assertNoInstance(this.getClass());
    }

    public static final String APP_LINK_SESSION = "sip:";
    public static final String APP_LINK_TEL = "tel:";
    public static final String APP_LINK_MAILTO = "mailto:";
    public static final String APP_LINK_FILE = "file:";
    public static final String APP_LINK_FTP = "ftp:";
    public static final String APP_LINK_IMAP = "imap:";
    public static final String APP_LINK_IRC = "irc:";
    public static final String APP_LINK_NNTP = "nntp:";

    public static final int HTTP_PORT = 80;
    public static final int HTTPS_PORT = 443;

    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    public static final String HTTP_PREFIX = "http://";
    public static final String HTTPS_PREFIX = "https://";
    public static final String REQUEST_SCHEME_EXTENSION = "://";
    public static final String GENERIC_PROTOCOL_PREFIX = "//";

    public static final String SLASH = "/";
    public static final String FRAGMENT_SEPARATOR = "#";
    public static final String QUERY_STRING_SEPARATOR = "?";
    public static final String QUERY_PARAM_DELIMITER = "&";

    /**
     * Represents standard HTML link target attributes and their associated security relationships.
     */
    public enum Target {

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
