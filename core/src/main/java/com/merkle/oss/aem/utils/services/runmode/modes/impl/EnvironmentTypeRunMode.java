package com.merkle.oss.aem.utils.services.runmode.modes.impl;

import com.merkle.oss.aem.utils.annotations.Generated;
import com.merkle.oss.aem.utils.java.ClassUtil;
import com.merkle.oss.aem.utils.services.runmode.modes.RunMode;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;

/**
 * Implementation of {@link RunMode} specifically for AEM Environment Tiers.
 * <p>
 * This class categorizes the deployment stages of an AEM instance, ranging from
 * local development machines to production environments.
 */
public class EnvironmentTypeRunMode implements RunMode {

    @Generated("Bypass coverage for static utility constructor")
    private EnvironmentTypeRunMode() {
        ClassUtil.assertNoInstance(this.getClass());
    }

    public static final String MODE_ENV_LOCAL_VALUE = "local";

    public static final String MODE_ENV_DEV_VALUE = "dev";

    public static final String MODE_ENV_RDE_VALUE = "rde";

    public static final String MODE_ENV_STAGE_VALUE = "stage";

    public static final String MODE_ENV_PROD_VALUE = "prod";

    /**
     * Enumeration of supported AEM Environment Tiers.
     * <p>
     * Provides a type-safe representation of the environment the code is currently executing in.
     */
    public enum Type implements RunMode.Type {

        LOCAL(MODE_ENV_LOCAL_VALUE),
        DEV(MODE_ENV_DEV_VALUE),
        RDE(MODE_ENV_RDE_VALUE),
        STAGE(MODE_ENV_STAGE_VALUE),
        PROD(MODE_ENV_PROD_VALUE);

        private final String mode;

        /**
         * @param mode The technical run mode string.
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

        /**
         * Resolves a technical run mode string into its corresponding {@link Type}.
         *
         * @param mode The raw string value to resolve (e.g., "prod").
         * @return The matching Type, or {@code null} if no match is found.
         */
        public static @Nullable Type of(@Nullable final String mode) {
            return Arrays.stream(Type.values())
                    .filter(envType -> Strings.CS.equals(envType.getMode(), mode))
                    .findFirst()
                    .orElse(null);
        }

    }

    /**
     * @return The fully qualified name of the {@link Type} class.
     */
    public static @NonNull String getKey() {
        return Type.class.getName();
    }

}
