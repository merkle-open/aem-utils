package com.merkle.oss.aem.utils.services.runmode.modes.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link EnvironmentTypeRunMode} class.
 */
class EnvironmentTypeRunModeTest {

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link EnvironmentTypeRunMode.Type#getMode()}
     *   <li>{@link EnvironmentTypeRunMode.Type#is(String)}
     *   <li>{@link EnvironmentTypeRunMode.Type#toString()}
     *   <li>{@link EnvironmentTypeRunMode.Type#of(String)}
     * </ul>
     */
    @Test
    void type_gettersAndSetters() {
        final EnvironmentTypeRunMode.Type localValue = EnvironmentTypeRunMode.Type.of("local");

        assertTrue(localValue.is("local"));
        assertEquals("local", localValue.getMode());
        assertEquals("local", localValue.toString());
        assertNull(EnvironmentTypeRunMode.Type.of(null));
    }

    /**
     * Method under test: {@link EnvironmentTypeRunMode#getKey()}
     */
    @Test
    void getKey() {
        assertEquals(EnvironmentTypeRunMode.Type.class.getName(), EnvironmentTypeRunMode.getKey());
    }

}
