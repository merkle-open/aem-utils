package com.merkle.oss.aem.utils.sling;

import com.merkle.oss.aem.utils.java.FunctionalUtil;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.spi.resource.provider.ResourceProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link ResourceUtil} class.
 */
@ExtendWith(MockitoExtension.class)
class ResourceUtilTest {

    private static final String RESOURCE_TYPE_1 = "resourceType1";
    private static final String RESOURCE_TYPE_2 = "resourceType2";
    private static final String RESOURCE_TYPE_3 = "resourceType3";

    @Mock
    private Resource resource;
    @Mock
    private Resource childResource1;
    @Mock
    private Resource childResource2;
    @Mock
    private Resource childResource1Sub1;
    @Mock
    private Resource childResource1Sub2;
    @Mock
    private Resource childResource2Sub1;
    @Mock
    private ResourceResolver resourceResolver;

    /**
     * Method under test: {@link ResourceUtil#isValid(Resource)}
     */
    @Test
    void isValid_false() {
        assertFalse(ResourceUtil.isValid(null));

        assertFalse(ResourceUtil.isValid(new NonExistingResource(resourceResolver, "")));

        when(resource.getResourceType()).thenReturn("");
        assertFalse(ResourceUtil.isValid(resource));

        when(resource.getResourceType()).thenReturn(Resource.RESOURCE_TYPE_NON_EXISTING);
        assertFalse(ResourceUtil.isValid(resource));

        when(resource.getResourceType()).thenReturn(ResourceProvider.RESOURCE_TYPE_SYNTHETIC);
        assertFalse(ResourceUtil.isValid(resource));
    }

    /**
     * Method under test: {@link ResourceUtil#isValid(Resource)}
     */
    @Test
    void isValid_true() {
        when(resource.getResourceType()).thenReturn("Valid_Resource_Type");
        assertTrue(ResourceUtil.isValid(resource));
    }

    /**
     * Method under test: {@link ResourceUtil#streamDescendantsByTypes(Resource, int, String...)}
     */
    @Test
    void streamDescendantsByTypes() {
        assertEquals(0, ResourceUtil.streamDescendantsByTypes(null, 0, RESOURCE_TYPE_1).count());
        assertEquals(0, ResourceUtil.streamDescendantsByTypes(null, 0, (String[]) null).count());
        assertEquals(0, ResourceUtil.streamDescendantsByTypes(resource, 0, (String[]) null).count());

        when(childResource1.getResourceType()).thenReturn(RESOURCE_TYPE_1);
        when(childResource2.getResourceType()).thenReturn(RESOURCE_TYPE_2);
        when(childResource1Sub1.getResourceType()).thenReturn(RESOURCE_TYPE_3);
        when(childResource1Sub2.getResourceType()).thenReturn(RESOURCE_TYPE_1);
        when(childResource2Sub1.getResourceType()).thenReturn(RESOURCE_TYPE_2);

        final Stream<Resource> resourceStream1 = Stream.of(childResource1, childResource2, childResource1Sub1, childResource1Sub2, childResource2Sub1);
        final Stream<Resource> resourceStream2 = Stream.of(childResource1, childResource2, childResource1Sub1, childResource1Sub2, childResource2Sub1);
        final Stream<Resource> resourceStream3 = Stream.of(childResource1, childResource2, childResource1Sub1, childResource1Sub2, childResource2Sub1);
        final Stream<Resource> resourceStream4 = Stream.of(childResource1, childResource2, childResource1Sub1, childResource1Sub2, childResource2Sub1);
        final Stream<Resource> resourceStream5 = Stream.of(childResource1, childResource2);
        final Stream<Resource> resourceStream6 = Stream.of(childResource1, childResource2, childResource1Sub1, childResource1Sub2, childResource2Sub1);
        final Stream<Resource> resourceStream7 = Stream.of(childResource1, childResource2, childResource1Sub1, childResource1Sub2, childResource2Sub1);

        try (MockedStatic<FunctionalUtil> functionalUtilMockedStatic = mockStatic(FunctionalUtil.class)) {
            functionalUtilMockedStatic.when(() -> FunctionalUtil.streamDescendants(any(Resource.class), any(), anyInt())).thenReturn(resourceStream1);
            assertEquals(2, ResourceUtil.streamDescendantsByTypes(resource, RESOURCE_TYPE_1).count());
            functionalUtilMockedStatic.when(() -> FunctionalUtil.streamDescendants(any(Resource.class), any(), anyInt())).thenReturn(resourceStream2);
            assertEquals(2, ResourceUtil.streamDescendantsByTypes(resource, 0, RESOURCE_TYPE_2).count());
            functionalUtilMockedStatic.when(() -> FunctionalUtil.streamDescendants(any(Resource.class), any(), anyInt())).thenReturn(resourceStream3);
            assertEquals(1, ResourceUtil.streamDescendantsByTypes(resource, 0, RESOURCE_TYPE_3).count());
            functionalUtilMockedStatic.when(() -> FunctionalUtil.streamDescendants(any(Resource.class), any(), anyInt())).thenReturn(resourceStream4);
            assertEquals(5, ResourceUtil.streamDescendantsByTypes(resource, 0, RESOURCE_TYPE_1, RESOURCE_TYPE_2, RESOURCE_TYPE_3).count());
            functionalUtilMockedStatic.when(() -> FunctionalUtil.streamDescendants(any(Resource.class), any(), anyInt())).thenReturn(resourceStream5);
            assertEquals(2, ResourceUtil.streamDescendantsByTypes(resource, 1, RESOURCE_TYPE_1, RESOURCE_TYPE_2).count());
            functionalUtilMockedStatic.when(() -> FunctionalUtil.streamDescendants(any(Resource.class), any(), anyInt())).thenReturn(resourceStream6);
            assertEquals(3, ResourceUtil.streamDescendantsByTypes(resource, 0, RESOURCE_TYPE_1, RESOURCE_TYPE_3).count());
            functionalUtilMockedStatic.when(() -> FunctionalUtil.streamDescendants(any(Resource.class), any(), anyInt())).thenReturn(resourceStream7);
            assertEquals(3, ResourceUtil.streamDescendantsByTypes(resource, 0, RESOURCE_TYPE_2, RESOURCE_TYPE_3).count());
        }
    }

    /**
     * Method under test: {@link ResourceUtil#findClosestAncestorByTypes(Resource, String...)}
     */
    @Test
    void findClosestAncestorByTypes() {
        assertNull(ResourceUtil.findClosestAncestorByTypes(null, "").orElse(null));
        assertNull(ResourceUtil.findClosestAncestorByTypes(null).orElse(null));
        assertNull(ResourceUtil.findClosestAncestorByTypes(childResource1, (String[]) null).orElse(null));

        when(childResource1Sub1.getParent()).thenReturn(childResource1);
        when(childResource1.getParent()).thenReturn(resource);
        when(childResource1.getResourceType()).thenReturn(RESOURCE_TYPE_3);
        when(resource.getResourceType()).thenReturn(RESOURCE_TYPE_2);
        assertEquals(childResource1, ResourceUtil.findClosestAncestorByTypes(childResource1Sub1, RESOURCE_TYPE_3).orElse(null));
        assertEquals(resource, ResourceUtil.findClosestAncestorByTypes(childResource1Sub1, RESOURCE_TYPE_2).orElse(null));
        assertNull(ResourceUtil.findClosestAncestorByTypes(childResource1Sub1, RESOURCE_TYPE_1).orElse(null));
        assertEquals(resource, ResourceUtil.findClosestAncestorByTypes(childResource1, RESOURCE_TYPE_2).orElse(null));
        assertNull(ResourceUtil.findClosestAncestorByTypes(childResource1, RESOURCE_TYPE_3).orElse(null));
    }

}
