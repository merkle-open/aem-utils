package com.merkle.oss.aem.utils.java;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

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
     * Method under test: {@link FunctionalUtil#toDistinctList(Function)}
     */
    @Test
    void toDistinctList() {
        final List<Object> emptyResult = Stream.empty().collect(FunctionalUtil.toDistinctList(Object::hashCode));

        assertTrue(emptyResult.isEmpty());

        final TestItem item1 = new TestItem(1, "Original");
        final TestItem item2 = new TestItem(2, "Second");
        final TestItem item3 = new TestItem(1, "Duplicate of 1");
        final TestItem item4 = new TestItem(3, "Third");
        final Stream<TestItem> itemStream = Stream.of(item1, item2, item3, item4);
        final List<TestItem> result = itemStream.collect(FunctionalUtil.toDistinctList(TestItem::id));

        assertEquals(3, result.size(), "Result should have 3 unique items");
        assertIterableEquals(List.of(item1, item2, item4), result);
        assertEquals("Original", result.getFirst().name());
    }

    /**
     * Method under test: {@link FunctionalUtil#toDistinctList(Function)}
     */
    @Test
    void toDistinctList_immutable() {
        final List<TestItem> result = Stream.of(new TestItem(1, "A"))
                .collect(FunctionalUtil.toDistinctList(TestItem::id));
        final TestItem testItem = new TestItem(2, "B");

        assertThrows(UnsupportedOperationException.class, () -> result.add(testItem));
    }

    record TestItem(int id, String name) {
    }

}
