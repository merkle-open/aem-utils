package com.merkle.oss.aem.utils.query;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PredicateProperties} class.
 */
public class PredicatePropertiesTest {

    /**
     * Method under test: {@link PredicateProperties}
     */
    @Test
    void instantiationException() throws NoSuchMethodException {
        final Constructor<PredicateProperties> constructor = PredicateProperties.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertEquals(AssertionError.class, exception.getCause().getClass());
        assertTrue(exception.getCause().getMessage().contains("not meant to be instantiated"));
    }

}
