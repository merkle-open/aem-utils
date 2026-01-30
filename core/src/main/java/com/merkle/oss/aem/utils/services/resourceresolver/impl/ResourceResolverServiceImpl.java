package com.merkle.oss.aem.utils.services.resourceresolver.impl;

import com.merkle.oss.aem.utils.services.resourceresolver.ResourceResolverService;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.jspecify.annotations.NonNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Map;

/**
 * Implementation of {@link ResourceResolverService} utilizing the Sling {@link org.apache.sling.api.resource.ResourceResolverFactory}.
 * <p>
 * This implementation relies on Service User Mappings configured via:
 * {@code org.apache.sling.serviceusermapping.impl.ServiceUserMapperImpl.amended}.
 */
@Component(service = ResourceResolverService.class)
public class ResourceResolverServiceImpl implements ResourceResolverService {

    /**
     * Subservice identifier for Truststore access.
     */
    public static final String TRUSTSTORE_READER_SERVICE = "truststore-reader";

    /**
     * Subservice identifier for Users access.
     */
    public static final String USERS_READER_SERVICE = "users-reader";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull ResourceResolver createTruststoreReader() throws LoginException {
        return create(TRUSTSTORE_READER_SERVICE, resourceResolverFactory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull ResourceResolver createUsersReader() throws LoginException {
        return create(USERS_READER_SERVICE, resourceResolverFactory);
    }

    /**
     * Helper method to generate a service resource resolver for a specific subservice.
     *
     * @param name The subservice name.
     * @param resourceResolverFactory The factory injected via OSGi.
     * @return An authenticated ResourceResolver.
     * @throws LoginException If the subservice mapping is incorrectly configured.
     */
    private @NonNull ResourceResolver create(@NonNull final String name, @NonNull final ResourceResolverFactory resourceResolverFactory) throws LoginException {
        return resourceResolverFactory.getServiceResourceResolver(Map.of(ResourceResolverFactory.SUBSERVICE, name));
    }

}
