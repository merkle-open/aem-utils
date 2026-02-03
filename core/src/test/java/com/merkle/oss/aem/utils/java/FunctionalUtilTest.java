package com.merkle.oss.aem.utils.java;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link FunctionalUtil} class.
 */
class FunctionalUtilTest {

    /**
     * Method under test: {@link FunctionalUtil#asStream(Iterator)}
     */
    @Test
    void asStream() {
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
     * Method under test: {@link FunctionalUtil#distinctByKey(Function)}
     */
    @Test
    void distinctByKey() {
        final ArrayList<String> containsCopies = new ArrayList<>(Arrays.asList("A", "B", "C", "A", "B", "C", "D"));
        final ArrayList<String> noCopies = new ArrayList<>(Arrays.asList("A", "C"));
        final ArrayList<String> cleanedNoCopies = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));

        assertEquals(cleanedNoCopies, containsCopies.stream().filter(FunctionalUtil.distinctByKey(s -> s)).toList());
        assertEquals(noCopies, noCopies.stream().filter(FunctionalUtil.distinctByKey(s -> s)).toList());
        assertEquals(cleanedNoCopies, cleanedNoCopies.stream().filter(FunctionalUtil.distinctByKey(s -> s)).toList());
        assertEquals(4, (int) containsCopies.stream().filter(FunctionalUtil.distinctByKey(s -> s)).count());
        assertEquals(2, (int) noCopies.stream().filter(FunctionalUtil.distinctByKey(s -> s)).count());

        assertThrows(NullPointerException.class, () -> FunctionalUtil.distinctByKey(null));
    }

}
