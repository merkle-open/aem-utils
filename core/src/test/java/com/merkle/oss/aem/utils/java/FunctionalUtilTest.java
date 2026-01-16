package com.merkle.oss.aem.utils.java;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link FunctionalUtil} class.
 */
public class FunctionalUtilTest {

    /**
     * <p>Method under test: {@link FunctionalUtil}
     */
    @Test
    void instantiationException() throws NoSuchMethodException {
        final Constructor<FunctionalUtil> constructor = FunctionalUtil.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertEquals(AssertionError.class, exception.getCause().getClass());
        assertTrue(exception.getCause().getMessage().contains("not meant to be instantiated"));
    }

    /**
     * <p>Method under test: {@link FunctionalUtil#asStream(Iterator)}
     */
    @Test
    public void asStream() {
        final String test = "test";
        final Iterator<String> iterator = Arrays.asList(test, null).iterator();

        final List<String> collect = FunctionalUtil.asStream(iterator)
                .filter(Objects::nonNull)
                .toList();

        assertEquals(1, collect.size());
        assertTrue(collect.contains(test));

        final Iterator<String> iterator2 = Arrays.asList(test, null).iterator();
        final List<String> nonNullCollection = FunctionalUtil.asStream(iterator2)
                .filter(Objects::nonNull)
                .toList();

        assertEquals(1, nonNullCollection.size());
        assertTrue(nonNullCollection.contains(test));

        assertThrows(NullPointerException.class, () -> FunctionalUtil.asStream(null));
    }

    /**
     * <p>Method under test: {@link FunctionalUtil#distinctByKey(Function)}
     */
    @Test
    void distinctByKey() {
        final ArrayList<String> containsCopies = new ArrayList<>(Arrays.asList("A", "B", "C", "A", "B", "C", "D"));
        final ArrayList<String> noCopies = new ArrayList<>(Arrays.asList("A", "C"));
        final ArrayList<String> cleanedNoCopies = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));

        assertEquals(cleanedNoCopies, containsCopies.stream().filter(FunctionalUtil.distinctByKey(s -> s)).collect(Collectors.toList()));
        assertEquals(noCopies, noCopies.stream().filter(FunctionalUtil.distinctByKey(s -> s)).collect(Collectors.toList()));
        assertEquals(cleanedNoCopies, cleanedNoCopies.stream().filter(FunctionalUtil.distinctByKey(s -> s)).collect(Collectors.toList()));
        assertEquals(4, (int) containsCopies.stream().filter(FunctionalUtil.distinctByKey(s -> s)).count());
        assertEquals(2, (int) noCopies.stream().filter(FunctionalUtil.distinctByKey(s -> s)).count());

        assertThrows(NullPointerException.class, () -> FunctionalUtil.distinctByKey(null));
    }

}
