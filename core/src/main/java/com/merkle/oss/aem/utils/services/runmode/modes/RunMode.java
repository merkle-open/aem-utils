package com.merkle.oss.aem.utils.services.runmode.modes;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Common definitions and types for AEM Run Modes.
 * <p>
 * This interface serves as a namespace for run mode classifications. It defines the
 * contract for specific run mode types, such as environment levels or instance roles.
 */
public interface RunMode {

    /**
     * Represents a specific classification of a run mode.
     * <p>
     * Implementations of this interface allow for comparing the current system state
     * against expected run mode values in a type-safe manner.
     */
    interface Type {

        /**
         * Retrieves the primary string identifier associated with this run mode type.
         *
         * @return The technical name of the run mode (e.g., "author", "prod").
         */
        @NonNull String getMode();

        /**
         * Compares a given string value against this run mode type.
         * <p>
         * This is useful for validating if a raw configuration string matches
         * a specific environment or service type.
         *
         * @param modeValue The string value to compare.
         * @return {@code true} if the provided value matches this run mode type.
         */
        boolean is(@Nullable final String modeValue);
    }

}
