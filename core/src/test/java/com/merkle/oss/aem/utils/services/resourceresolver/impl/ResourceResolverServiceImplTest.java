package com.merkle.oss.aem.utils.services.resourceresolver.impl;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static com.merkle.oss.aem.utils.services.resourceresolver.impl.ResourceResolverServiceImpl.TRUSTSTORE_READER_SERVICE;
import static com.merkle.oss.aem.utils.services.resourceresolver.impl.ResourceResolverServiceImpl.USERS_READER_SERVICE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link ResourceResolverServiceImpl} class.
 */
@ExtendWith(MockitoExtension.class)
class ResourceResolverServiceImplTest {

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private ResourceResolverFactory resourceResolverFactory;

    @InjectMocks
    private ResourceResolverServiceImpl resourceResolverService;

    /**
     * Method under test: {@link ResourceResolverServiceImpl#createTruststoreReader()}
     */
    @Test
    void createTruststoreReader() throws LoginException {
        when(resourceResolverFactory.getServiceResourceResolver(Map.of(ResourceResolverFactory.SUBSERVICE, TRUSTSTORE_READER_SERVICE))).thenReturn(resourceResolver);
        assertNotNull(resourceResolverService.createTruststoreReader());
    }

    /**
     * Method under test: {@link ResourceResolverServiceImpl#createUsersReader()}
     */
    @Test
    void createUsersReader() throws LoginException {
        when(resourceResolverFactory.getServiceResourceResolver(Map.of(ResourceResolverFactory.SUBSERVICE, USERS_READER_SERVICE))).thenReturn(resourceResolver);
        assertNotNull(resourceResolverService.createUsersReader());
    }

}
