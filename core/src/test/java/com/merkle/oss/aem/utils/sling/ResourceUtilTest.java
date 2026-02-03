package com.merkle.oss.aem.utils.sling;

import com.merkle.oss.aem.utils.java.FunctionalUtil;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.spi.resource.provider.ResourceProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
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
     * Method under test: {@link ResourceUtil#childrenOfTypes(Resource, String...)}
     */
    @Test
    void childrenOfTypes() {
        assertEquals(0, ResourceUtil.childrenAsStream(null).toList().size());

        final Iterator<Resource> iterator1 = Arrays.asList(childResource1, childResource2).iterator();
        final Iterator<Resource> iterator2 = Arrays.asList(childResource1, childResource2).iterator();
        when(resource.listChildren()).thenReturn(iterator1);
        final List<Resource> resourceList = ResourceUtil.childrenAsStream(resource).toList();
        assertEquals(FunctionalUtil.asStream(iterator2).toList(), resourceList);
    }

    /**
     * Method under test: {@link ResourceUtil#childrenOfTypes(Resource, String...)}
     */
    @Test
    void childrenOfTypes_none() {
        assertEquals(Collections.emptyList(), ResourceUtil.childrenOfTypes(resource));
        assertEquals(Collections.emptyList(), ResourceUtil.childrenOfTypes(null));

        final Iterator<Resource> iterator = Arrays.asList(childResource1, childResource2).iterator();
        when(resource.listChildren()).thenReturn(iterator);
        when(childResource1.getResourceType()).thenReturn(RESOURCE_TYPE_1);
        when(childResource2.getResourceType()).thenReturn(RESOURCE_TYPE_2);
        assertEquals(0, ResourceUtil.childrenOfTypes(resource, RESOURCE_TYPE_3).size());
    }

    /**
     * Method under test: {@link ResourceUtil#childrenOfTypes(Resource, String...)}
     */
    @Test
    void childrenOfTypes_one() {
        assertEquals(Collections.emptyList(), ResourceUtil.childrenOfTypes(resource));

        final Iterator<Resource> iterator = Arrays.asList(childResource1, childResource2).iterator();
        when(resource.listChildren()).thenReturn(iterator);
        when(childResource1.getResourceType()).thenReturn(RESOURCE_TYPE_1);
        when(childResource2.getResourceType()).thenReturn(RESOURCE_TYPE_2);
        assertEquals(1, ResourceUtil.childrenOfTypes(resource, RESOURCE_TYPE_1).size());
    }

    /**
     * Method under test: {@link ResourceUtil#childrenOfTypes(Resource, String...)}
     */
    @Test
    void childrenOfTypes_two() {
        assertEquals(Collections.emptyList(), ResourceUtil.childrenOfTypes(resource));

        final Iterator<Resource> iterator1 = Arrays.asList(childResource1, childResource2).iterator();
        when(resource.listChildren()).thenReturn(iterator1);
        when(childResource1.getResourceType()).thenReturn(RESOURCE_TYPE_1);
        when(childResource2.getResourceType()).thenReturn(RESOURCE_TYPE_2);
        assertEquals(2, ResourceUtil.childrenOfTypes(resource, RESOURCE_TYPE_1, RESOURCE_TYPE_2).size());
    }

    /**
     * Method under test: {@link ResourceUtil#descendantsOfTypes(Resource, String...)}
     */
    @Test
    void descendantsOfTypes_none() {
        assertEquals(Collections.emptyList(), ResourceUtil.descendantsOfTypes(resource));
        assertEquals(Collections.emptyList(), ResourceUtil.descendantsOfTypes(null));

        final Iterator<Resource> iterator1 = Arrays.asList(childResource1, childResource2).iterator();
        final Iterator<Resource> iterator1Sub1 = Arrays.asList(childResource1Sub1, childResource1Sub2).iterator();
        final Iterator<Resource> iterator2Sub1 = List.of(childResource2Sub1).iterator();
        final List<Resource> emptyList = new ArrayList<>();
        final Iterator<Resource> empty1Sub1 = emptyList.iterator();
        final Iterator<Resource> empty1Sub2 = emptyList.iterator();
        final Iterator<Resource> empty2Sub1 = emptyList.iterator();
        when(resource.listChildren()).thenReturn(iterator1);
        when(childResource1.listChildren()).thenReturn(iterator1Sub1);
        when(childResource2.listChildren()).thenReturn(iterator2Sub1);
        when(childResource1Sub1.listChildren()).thenReturn(empty1Sub1);
        when(childResource1Sub2.listChildren()).thenReturn(empty1Sub2);
        when(childResource2Sub1.listChildren()).thenReturn(empty2Sub1);
        when(childResource1.getResourceType()).thenReturn(RESOURCE_TYPE_1);
        when(childResource2.getResourceType()).thenReturn(RESOURCE_TYPE_2);
        when(childResource1Sub1.getResourceType()).thenReturn(RESOURCE_TYPE_1);
        when(childResource1Sub2.getResourceType()).thenReturn(RESOURCE_TYPE_2);
        when(childResource2Sub1.getResourceType()).thenReturn(RESOURCE_TYPE_3);
        assertEquals(0, ResourceUtil.descendantsOfTypes(resource, "resourceType4").size());
    }

    /**
     * Method under test: {@link ResourceUtil#descendantsOfTypes(Resource, String...)}
     */
    @Test
    void descendantsOfTypes_one() {
        final Iterator<Resource> iterator1 = Arrays.asList(childResource1, childResource2).iterator();
        final Iterator<Resource> iterator1Sub1 = Arrays.asList(childResource1Sub1, childResource1Sub2).iterator();
        final Iterator<Resource> iterator2Sub1 = List.of(childResource2Sub1).iterator();
        final List<Resource> emptyList = new ArrayList<>();
        final Iterator<Resource> empty1Sub1 = emptyList.iterator();
        final Iterator<Resource> empty1Sub2 = emptyList.iterator();
        final Iterator<Resource> empty2Sub1 = emptyList.iterator();
        when(resource.listChildren()).thenReturn(iterator1);
        when(childResource1.listChildren()).thenReturn(iterator1Sub1);
        when(childResource2.listChildren()).thenReturn(iterator2Sub1);
        when(childResource1Sub1.listChildren()).thenReturn(empty1Sub1);
        when(childResource1Sub2.listChildren()).thenReturn(empty1Sub2);
        when(childResource2Sub1.listChildren()).thenReturn(empty2Sub1);
        when(childResource1.getResourceType()).thenReturn(RESOURCE_TYPE_1);
        when(childResource2.getResourceType()).thenReturn(RESOURCE_TYPE_2);
        when(childResource1Sub1.getResourceType()).thenReturn(RESOURCE_TYPE_1);
        when(childResource1Sub2.getResourceType()).thenReturn(RESOURCE_TYPE_2);
        when(childResource2Sub1.getResourceType()).thenReturn(RESOURCE_TYPE_3);
        assertEquals(1, ResourceUtil.descendantsOfTypes(resource, RESOURCE_TYPE_3).size());
    }

    /**
     * Method under test: {@link ResourceUtil#descendantsOfTypes(Resource, String...)}
     */
    @Test
    void descendantsOfTypes_two() {
        final Iterator<Resource> iterator1 = Arrays.asList(childResource1, childResource2).iterator();
        final Iterator<Resource> iterator1Sub1 = Arrays.asList(childResource1Sub1, childResource1Sub2).iterator();
        final Iterator<Resource> iterator2Sub1 = List.of(childResource2Sub1).iterator();
        final List<Resource> emptyList = new ArrayList<>();
        final Iterator<Resource> empty1Sub1 = emptyList.iterator();
        final Iterator<Resource> empty1Sub2 = emptyList.iterator();
        final Iterator<Resource> empty2Sub1 = emptyList.iterator();
        when(resource.listChildren()).thenReturn(iterator1);
        when(childResource1.listChildren()).thenReturn(iterator1Sub1);
        when(childResource2.listChildren()).thenReturn(iterator2Sub1);
        when(childResource1Sub1.listChildren()).thenReturn(empty1Sub1);
        when(childResource1Sub2.listChildren()).thenReturn(empty1Sub2);
        when(childResource2Sub1.listChildren()).thenReturn(empty2Sub1);
        when(childResource1.getResourceType()).thenReturn(RESOURCE_TYPE_1);
        when(childResource2.getResourceType()).thenReturn(RESOURCE_TYPE_2);
        when(childResource1Sub1.getResourceType()).thenReturn(RESOURCE_TYPE_1);
        when(childResource1Sub2.getResourceType()).thenReturn(RESOURCE_TYPE_2);
        when(childResource2Sub1.getResourceType()).thenReturn(RESOURCE_TYPE_3);
        assertEquals(2, ResourceUtil.descendantsOfTypes(resource, RESOURCE_TYPE_1).size());
    }

    /**
     * Method under test: {@link ResourceUtil#descendantsOfTypes(Resource, String...)}
     */
    @Test
    void descendantsOfTypes_three() {
        final Iterator<Resource> iterator1 = Arrays.asList(childResource1, childResource2).iterator();
        final Iterator<Resource> iterator1Sub1 = Arrays.asList(childResource1Sub1, childResource1Sub2).iterator();
        final Iterator<Resource> iterator2Sub1 = List.of(childResource2Sub1).iterator();
        final List<Resource> emptyList = new ArrayList<>();
        final Iterator<Resource> empty1Sub1 = emptyList.iterator();
        final Iterator<Resource> empty1Sub2 = emptyList.iterator();
        final Iterator<Resource> empty2Sub1 = emptyList.iterator();
        when(resource.listChildren()).thenReturn(iterator1);
        when(childResource1.listChildren()).thenReturn(iterator1Sub1);
        when(childResource2.listChildren()).thenReturn(iterator2Sub1);
        when(childResource1Sub1.listChildren()).thenReturn(empty1Sub1);
        when(childResource1Sub2.listChildren()).thenReturn(empty1Sub2);
        when(childResource2Sub1.listChildren()).thenReturn(empty2Sub1);
        when(childResource1.getResourceType()).thenReturn(RESOURCE_TYPE_1);
        when(childResource2.getResourceType()).thenReturn(RESOURCE_TYPE_2);
        when(childResource1Sub1.getResourceType()).thenReturn(RESOURCE_TYPE_1);
        when(childResource1Sub2.getResourceType()).thenReturn(RESOURCE_TYPE_2);
        when(childResource2Sub1.getResourceType()).thenReturn(RESOURCE_TYPE_3);
        assertEquals(4, ResourceUtil.descendantsOfTypes(resource, RESOURCE_TYPE_1, RESOURCE_TYPE_2).size());
    }

    /**
     * Method under test: {@link ResourceUtil#findClosestAncestorOfResourceTypes(Resource, String...)}
     */
    @Test
    void findClosestAncestorOfResourceTypes() {
        assertNull(ResourceUtil.findClosestAncestorOfResourceTypes(null, "").orElse(null));
        assertNull(ResourceUtil.findClosestAncestorOfResourceTypes(null).orElse(null));

        when(childResource1Sub1.getParent()).thenReturn(childResource1);
        when(childResource1.getParent()).thenReturn(resource);
        when(childResource1.getResourceType()).thenReturn(RESOURCE_TYPE_3);
        when(resource.getResourceType()).thenReturn(RESOURCE_TYPE_2);
        assertEquals(childResource1, ResourceUtil.findClosestAncestorOfResourceTypes(childResource1Sub1, RESOURCE_TYPE_3).orElse(null));
        assertEquals(resource, ResourceUtil.findClosestAncestorOfResourceTypes(childResource1Sub1, RESOURCE_TYPE_2).orElse(null));
        assertNull(ResourceUtil.findClosestAncestorOfResourceTypes(childResource1Sub1, RESOURCE_TYPE_1).orElse(null));
        assertEquals(resource, ResourceUtil.findClosestAncestorOfResourceTypes(childResource1, RESOURCE_TYPE_2).orElse(null));
        assertNull(ResourceUtil.findClosestAncestorOfResourceTypes(childResource1, RESOURCE_TYPE_3).orElse(null));
    }

}
