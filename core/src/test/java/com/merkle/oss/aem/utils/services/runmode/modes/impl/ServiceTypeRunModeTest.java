package com.merkle.oss.aem.utils.services.runmode.modes.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ServiceTypeRunMode} class.
 */
class ServiceTypeRunModeTest {

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link ServiceTypeRunMode.Type#getMode()}
     *   <li>{@link ServiceTypeRunMode.Type#is(String)}
     *   <li>{@link ServiceTypeRunMode.Type#toString()}
     * </ul>
     */
    @Test
    void type_gettersAndSetters() {
        final ServiceTypeRunMode.Type publishValue = ServiceTypeRunMode.Type.PUBLISH;

        assertTrue(publishValue.is("publish"));
        assertEquals("publish", publishValue.getMode());
        assertEquals("publish", publishValue.toString());
    }

    /**
     * Method under test: {@link ServiceTypeRunMode#getKey()}
     */
    @Test
    void getKey() {
        assertEquals(ServiceTypeRunMode.Type.class.getName(), ServiceTypeRunMode.getKey());
    }

}
