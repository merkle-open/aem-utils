package com.merkle.oss.aem.utils.services.runmode;

import org.jspecify.annotations.NonNull;

import java.util.Map;

/**
 * Service for detecting the current AEM execution environment (run modes).
 * <p>
 * This service provides a modern alternative to the deprecated {@code SlingSettingsService}.
 * It identifies whether the instance is an {@code author} or {@code publish} environment,
 * as well as specific stage identifiers like {@code local}, {@code rde}, {@code dev}, {@code stage}, or {@code prod}.
 *
 * @apiNote This service relies on OSGi configurations that must be
 * deployed specifically to the target run mode folders (e.g., {@code config.author.prod}).
 *
 */
public interface RunModeService {

    /**
     * Determines if the current instance is an Author environment.
     *
     * @return {@code true} if the instance is configured as {@code author}.
     */
    boolean isAuthor();

    /**
     * Determines if the current instance is a Publish environment.
     *
     * @return {@code true} if the instance is configured as {@code publish}.
     */
    boolean isPublish();

    /**
     * Determines if the code is running on a developer's local machine.
     *
     * @return {@code true} if the environment matches the {@code local} run mode.
     */
    boolean isLocal();

    /**
     * Determines if the code is running on a RDE environment.
     *
     * @return {@code true} if the environment matches the {@code rde} run mode.
     */
    boolean isRde();

    /**
     * Determines if the code is running on a Development environment.
     *
     * @return {@code true} if the environment matches the {@code dev} run mode.
     */
    boolean isDev();

    /**
     * Determines if the code is running on a Staging or UAT environment.
     *
     * @return {@code true} if the environment matches the {@code stage} run mode.
     */
    boolean isStage();

    /**
     * Determines if the code is running on a Production environment.
     *
     * @return {@code true} if the environment matches the {@code prod} run mode.
     */
    boolean isProd();

    /**
     * Provides a map containing all active run mode keys and their configured values.
     *
     * @return A {@link Map} of active run mode identifiers.
     */
    @NonNull Map<String, String> getRunModes();

}
