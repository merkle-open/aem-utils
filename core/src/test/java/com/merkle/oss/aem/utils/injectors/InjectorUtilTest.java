package com.merkle.oss.aem.utils.injectors;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.reflect.TypeToken;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link InjectorUtil} class.
 */
@ExtendWith(MockitoExtension.class)
class InjectorUtilTest {

    private static final List<String> TITLE_LIST = List.of("title1", "title2");

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Resource resource;

    @Mock
    private Page page;

    @Mock
    private PageManager pageManager;

    @Mock
    private ValueMap valueMap;

    @Mock
    private Resource contentResource;

    @Mock
    private InheritanceValueMap inheritanceValueMap = new HierarchyNodeInheritanceValueMap(contentResource);

    /**
     * Method under test: {@link InjectorUtil#getPageFromAdaptable(Object)}
     */
    @Test
    void getPageFromAdaptable() {
        assertFalse(InjectorUtil.getPageFromAdaptable(null).isPresent());

        when(Adaptable.class.cast(page).adaptTo(Page.class)).thenReturn(page);
        assertTrue(InjectorUtil.getPageFromAdaptable(page).isPresent());

        when(Adaptable.class.cast(resource).adaptTo(Page.class)).thenReturn(null);
        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getContainingPage(resource)).thenReturn(page);
        assertTrue(InjectorUtil.getPageFromAdaptable(resource).isPresent());
    }

    /**
     * Method under test: {@link InjectorUtil#getValueFromValueMap(ValueMap, String, Type)}
     */
    @Test
    void getValueFromValueMap() throws NoSuchFieldException {
        assertNull(InjectorUtil.getValueFromValueMap(null, "", null));
        assertNull(InjectorUtil.getValueFromValueMap(valueMap, "", null));
        assertNull(InjectorUtil.getValueFromValueMap(valueMap, "", String.class));
        assertNull(InjectorUtil.getValueFromValueMap(valueMap, "title", String.class));

        when(valueMap.get("title", String.class)).thenReturn("title");
        assertEquals("title", InjectorUtil.getValueFromValueMap(valueMap, "title", String.class));
        when((inheritanceValueMap.getInherited("title", String.class))).thenReturn("title");
        assertEquals("title", InjectorUtil.getValueFromValueMap(inheritanceValueMap, "title", String.class));

        final String[] titles = new String[]{"title1", "title2"};
        final Type type = InjectorUtilTest.class.getDeclaredField("TITLE_LIST").getGenericType();
        assertNull(InjectorUtil.getValueFromValueMap(valueMap, "titles", type));
        when(valueMap.get("titles", String[].class)).thenReturn(titles);
        assertEquals(TITLE_LIST, InjectorUtil.getValueFromValueMap(valueMap, "titles", type));

        assertNull(InjectorUtil.getValueFromValueMap(valueMap, "titles", new TypeToken<Map<String, String>>() {
        }.getType()));
        assertNull(InjectorUtil.getValueFromValueMap(valueMap, "titles", new TypeToken<Optional<String>>() {
        }.getType()));
        assertNull(InjectorUtil.getValueFromValueMap(valueMap, "titles", new TypeToken<Set<String>>() {
        }.getType()));
    }

    /**
     * Method under test: {@link InjectorUtil#getValueFromValueMap(ValueMap, String, Type)}
     */
    @Test
    void getValueFromValueMap_error() {
        when((inheritanceValueMap.getInherited("title", String.class))).thenThrow(ClassCastException.class);
        assertNull(InjectorUtil.getValueFromValueMap(inheritanceValueMap, "title", String.class));

        when((inheritanceValueMap.getInherited("ranks", int[].class))).thenThrow(ClassCastException.class);
        assertNull(InjectorUtil.getValueFromValueMap(inheritanceValueMap, "ranks", int[].class));

        when((inheritanceValueMap.getInherited("ranks", Integer[].class))).thenReturn(new Integer[]{1, 2, 3});
        assertNotNull(InjectorUtil.getValueFromValueMap(inheritanceValueMap, "ranks", int[].class));
    }

    /**
     * Method under test: {@link InjectorUtil#getValueFromValueMap(ValueMap, String, Type)}
     */
    @Test
    void getValueFromValueMap_error2() {
        when((inheritanceValueMap.getInherited("ranks", Integer[].class))).thenThrow(ClassCastException.class);
        assertNull(InjectorUtil.getValueFromValueMap(inheritanceValueMap, "ranks", Integer[].class));

        when(inheritanceValueMap.getInherited("ranks", int[].class)).thenReturn(new int[]{1, 2, 3});
        assertNotNull(InjectorUtil.getValueFromValueMap(inheritanceValueMap, "ranks", Integer[].class));

        when(inheritanceValueMap.getInherited("titles", String[].class)).thenReturn(new String[]{"title1", "title2"});
        assertNotNull(InjectorUtil.getValueFromValueMap(inheritanceValueMap, "titles", String[].class));
    }

}
