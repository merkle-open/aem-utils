package com.merkle.oss.aem.utils.constants;

import org.jspecify.annotations.NonNull;

/**
 * Represents common file types and their associated metadata used within the AEM ecosystem.
 * <p>
 * This enum provides a centralized mapping between logical file identifiers,
 * official IANA MIME types, and standard file extensions.
 * </p>
 */
public enum FileType {

    HTML("text/html", "html"),
    TXT("text/plain", "txt"),
    JSON("application/json", "json"),
    TEXT_XML("text/xml", "xml"),
    VCARD("text/x-vcard", "vcf");

    private static final String DOT = ".";

    private final String mimeType;
    private final String extension;

    /**
     *
     * @param mimeType  The official IANA media type.
     * @param extension The standard file extension (without the leading dot).
     */
    FileType(@NonNull final String mimeType, @NonNull final String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    /**
     * Returns the official MIME type (Media Type) associated with this file type.
     *
     * @return The MIME type string (e.g., "application/json").
     */
    public @NonNull String getMimeType() {
        return mimeType;
    }

    /**
     * Returns the standard file extension without a leading dot.
     *
     * @return The extension string (e.g., "json").
     */
    public @NonNull String getExtension() {
        return extension;
    }

    /**
     * Returns the file extension prefixed with a leading dot.
     * <p>
     *
     * @return The extension in the lower case with a leading dot (e.g., ".html").
     */
    public @NonNull String toDotExtension() {
        return DOT + getExtension();
    }

    /**
     * Returns the lowercase name of the enum constant.
     *
     * @return Lowercase representation of the constant name.
     */
    @Override
    public @NonNull String toString() {
        return name().toLowerCase();
    }

}
