package com.merkle.oss.aem.utils.java;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ClassUtil} class.
 */
class ClassUtilTest {

    /**
     * Method under test: {@link ClassUtil}
     */
    @Test
    void testAssertNoInstance() {
        final AssertionError error = assertThrows(AssertionError.class, () -> ClassUtil.assertNoInstance(String.class));
        assertTrue(error.getMessage().contains("String is not meant to be instantiated"));
    }

    @Test
    void testConstructorIsPrivate() throws Exception {
        final Constructor<ClassUtil> constructor = ClassUtil.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()), "Constructor should be private");

        constructor.setAccessible(true);
        final InvocationTargetException ite = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(AssertionError.class, ite.getCause());
    }

}
