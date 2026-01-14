package com.merkle.oss.aem.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FunctionalUtilTest {

    @Test
    public void testAsStream() {
        final String test = "test";
        final Iterator<String> iterator1 = Arrays.asList(test, null).iterator();
        final List<String> nullCollection = FunctionalUtil.asStream(iterator1).toList();

        assertEquals(2, nullCollection.size());
        assertTrue(nullCollection.contains(test));

        final Iterator<String> iterator2 = Arrays.asList(test, null).iterator();
        final List<String> nonNullCollection = FunctionalUtil.asStream(iterator2).filter(Objects::nonNull).toList();

        assertEquals(1, nonNullCollection.size());
        assertTrue(nonNullCollection.contains(test));

    }

}
