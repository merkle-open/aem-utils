package com.merkle.oss.aem.utils.injector;

import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.databind.type.PlaceholderForType;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class InjectorUtilTest {

    /**
     * Method under test: {@link InjectorUtil#getPageFromAdaptable(Object)}
     */
    @Test
    void testGetPageFromAdaptable() {
        final Optional<Page> actualPageFromAdaptable = InjectorUtil.getPageFromAdaptable("Adaptable");

        assertFalse(actualPageFromAdaptable.isPresent());
    }

    /**
     * Method under test:{@link InjectorUtil#getValueFromValueMap(ValueMap, String, Type)}
     */
    @Test
    void testGetValueFromValueMap() {
        final ValueMap map = new ValueMapDecorator(Collections.emptyMap());

        assertNull(InjectorUtil.getValueFromValueMap(map, "Name", new PlaceholderForType(1)));
    }

    /**
     * Method under test: {@link InjectorUtil#getValueFromValueMap(ValueMap, String, Type)}
     */
    @Test
    void testGetValueFromValueMap2() {
        Class<Object> type = Object.class;

        assertNull(InjectorUtil.getValueFromValueMap(null, "foo", type));
    }

    /**
     * Method under test: {@link InjectorUtil#getValueFromValueMap(ValueMap, String, Type)}
     */
    @Test
    void testGetValueFromValueMap3() {
        final ValueMap map = new ValueMapDecorator(Collections.emptyMap());
        Class<Object> type = Object.class;

        assertNull(InjectorUtil.getValueFromValueMap(map, "foo", type));
    }

}
