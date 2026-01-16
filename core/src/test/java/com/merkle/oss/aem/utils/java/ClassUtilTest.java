package com.merkle.oss.aem.utils.java;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ClassUtil} class.
 */
class ClassUtilTest {

    /**
     * <p>Method under test: {@link ClassUtil}
     */
    @Test
    void instantiationException() throws NoSuchMethodException {
        final Constructor<ClassUtil> constructor = ClassUtil.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertEquals(AssertionError.class, exception.getCause().getClass());
        assertTrue(exception.getCause().getMessage().contains("not meant to be instantiated"));
    }

}
