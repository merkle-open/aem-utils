package com.merkle.oss.aem.utils.services.runmode.modes.impl;

import com.merkle.oss.aem.utils.annotations.Generated;
import com.merkle.oss.aem.utils.java.ClassUtil;
import com.merkle.oss.aem.utils.services.runmode.modes.RunMode;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Implementation of {@link RunMode} specifically for AEM Service Types.
 * <p>
 * This class distinguishes between the two primary roles an AEM instance can play:
 * {@code author} and {@code publish}.
 */
public final class ServiceTypeRunMode implements RunMode {

    @Generated("Bypass coverage for static utility constructor")
    private ServiceTypeRunMode() {
        ClassUtil.assertNoInstance(this.getClass());
    }

    public static final String MODE_TYPE_AUTHOR_VALUE = "author";

    public static final String MODE_TYPE_PUBLISH_VALUE = "publish";

    /**
     * Enumeration of supported AEM Service Types.
     * <p>
     * Provides type-safe access to the Author and Publish run mode identifiers.
     */
    public enum Type implements RunMode.Type {

        AUTHOR(MODE_TYPE_AUTHOR_VALUE),
        PUBLISH(MODE_TYPE_PUBLISH_VALUE);

        private final String mode;

        /**
         * @param mode The technical mode string.
         */
        Type(@NonNull final String mode) {
            this.mode = mode;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NonNull String getMode() {
            return mode;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean is(@Nullable final String value) {
            return Strings.CS.equals(mode, value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NonNull String toString() {
            return mode;
        }

    }

    /**
     * @return The fully qualified name of the {@link Type} class.
     */
    public static @NonNull String getKey() {
        return Type.class.getName();
    }

}
