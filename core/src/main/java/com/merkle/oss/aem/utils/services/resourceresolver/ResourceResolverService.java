package com.merkle.oss.aem.utils.services.resourceresolver;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.jspecify.annotations.NonNull;

/**
 * Provides specialized {@link org.apache.sling.api.resource.ResourceResolver} instances for system-level operations.
 * <p>
 * This service centralizes the creation of service-user-based resolvers, ensuring that
 * repository access is scoped to specific subservices (e.g., reading truststore or users).
 * </p>
 * <p>
 * <b>Security Note:</b> Resolvers returned by this service should be used within a
 * try-with-resources block or explicitly closed to prevent memory leaks and unclosed
 * sessions.
 */
public interface ResourceResolverService {

    /**
     * Provides a resolver with read-only access to the AEM Truststore.
     * <p>
     * Primarily used for retrieving global certificate configurations and cryptographic
     * materials required for secure outgoing connections.
     *
     * @return A {@link ResourceResolver} bound to the "truststore-reader" subservice.
     * @throws LoginException If the service user mapping is missing or the user is invalid.
     */
    @NonNull ResourceResolver createTruststoreReader() throws LoginException;

    /**
     * Provides a resolver with access to users and their associated Keystores.
     * <p>
     * This resolver is capable of traversing user nodes and reading keystore data
     * required for user-specific encryption or authentication tasks.
     *
     * @return A {@link ResourceResolver} bound to the "users-reader" subservice.
     * @throws LoginException If the service user mapping is missing or the user is invalid.
     */
    @NonNull ResourceResolver createUsersReader() throws LoginException;

}
