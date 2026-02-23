package com.merkle.oss.aem.utils.java;

import com.day.cq.wcm.api.Page;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link FunctionalUtil} class.
 */
@ExtendWith(MockitoExtension.class)
class FunctionalUtilTest {

    @Mock
    private Page parent;
    @Mock
    private Page child1;
    @Mock
    private Page child2;
    @Mock
    private Page child3;
    @Mock
    private Page child1Sub1;
    @Mock
    private Page child1Sub2;
    @Mock
    private Page child2Sub1;
    @Mock
    private Page child1Sub1Sub1;

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
     * Method under test:
     * <ul>
     *   <li>{@link FunctionalUtil#streamTree(Object, Function)}</li>
     *   <li>{@link FunctionalUtil#streamTree(Object, Function, int)}</li>
     * </ul>
     */
    @Test
    void streamTree() {
        final Page pageNull = null;
        assertEquals(0, FunctionalUtil.streamTree(pageNull, Page::listChildren).count());

        when(parent.listChildren()).thenReturn(Collections.emptyIterator());
        assertEquals(1, FunctionalUtil.streamTree(parent, Page::listChildren).count());


        when(parent.listChildren()).thenReturn(Arrays.asList(child1, child2, child3).iterator());
        when(child1.listChildren()).thenReturn(Collections.emptyIterator());
        when(child2.listChildren()).thenReturn(Collections.emptyIterator());
        when(child3.listChildren()).thenReturn(Collections.emptyIterator());
        assertEquals(4, FunctionalUtil.streamTree(parent, Page::listChildren).count());

        when(parent.listChildren()).thenReturn(Arrays.asList(child1, child2, child3).iterator());
        when(child1.listChildren()).thenReturn(Arrays.asList(child1Sub1, child1Sub2).iterator());
        when(child1Sub1.listChildren()).thenReturn(Collections.emptyIterator());
        when(child1Sub2.listChildren()).thenReturn(Collections.emptyIterator());
        when(child2.listChildren()).thenReturn(Collections.emptyIterator());
        when(child3.listChildren()).thenReturn(Collections.emptyIterator());
        assertEquals(6, FunctionalUtil.streamTree(parent, Page::listChildren).count());

        when(parent.listChildren()).thenReturn(Arrays.asList(child1, child2, child3).iterator());
        when(child1.listChildren()).thenReturn(Arrays.asList(child1Sub1, child1Sub2).iterator());
        when(child1Sub1.listChildren()).thenReturn(List.of(child1Sub1Sub1).iterator());
        when(child1Sub1Sub1.listChildren()).thenReturn(Collections.emptyIterator());
        when(child1Sub2.listChildren()).thenReturn(Collections.emptyIterator());
        when(child2.listChildren()).thenReturn(Collections.emptyIterator());
        when(child3.listChildren()).thenReturn(Collections.emptyIterator());
        assertEquals(7, FunctionalUtil.streamTree(parent, Page::listChildren).count());

        when(parent.listChildren()).thenReturn(Arrays.asList(child1, child2, child3).iterator());
        when(child1.listChildren()).thenReturn(Arrays.asList(child1Sub1, child1Sub2).iterator());
        when(child1Sub1.listChildren()).thenReturn(Collections.emptyIterator());
        when(child1Sub2.listChildren()).thenReturn(Collections.emptyIterator());
        when(child2.listChildren()).thenReturn(Collections.emptyIterator());
        when(child3.listChildren()).thenReturn(Collections.emptyIterator());
        assertEquals(4, FunctionalUtil.streamTree(parent, Page::listChildren, 1).count());

        when(parent.listChildren()).thenReturn(Arrays.asList(child1, child2, child3).iterator());
        when(child1.listChildren()).thenReturn(Arrays.asList(child1Sub1, child1Sub2).iterator());
        when(child1Sub1.listChildren()).thenReturn(List.of(child1Sub1Sub1).iterator());
        when(child1Sub1Sub1.listChildren()).thenReturn(Collections.emptyIterator());
        when(child1Sub2.listChildren()).thenReturn(Collections.emptyIterator());
        when(child2.listChildren()).thenReturn(Collections.emptyIterator());
        when(child3.listChildren()).thenReturn(Collections.emptyIterator());
        assertEquals(6, FunctionalUtil.streamTree(parent, Page::listChildren, 2).count());

        when(parent.listChildren()).thenReturn(Arrays.asList(child1, child2, child3).iterator());
        when(child1.listChildren()).thenReturn(Arrays.asList(child1Sub1, child1Sub2).iterator());
        when(child1Sub1.listChildren()).thenReturn(List.of(child1Sub1Sub1).iterator());
        when(child1Sub1Sub1.listChildren()).thenReturn(Collections.emptyIterator());
        when(child1Sub2.listChildren()).thenReturn(Collections.emptyIterator());
        when(child2.listChildren()).thenReturn(List.of(child2Sub1).iterator());
        when(child2Sub1.listChildren()).thenReturn(Collections.emptyIterator());
        when(child3.listChildren()).thenReturn(Collections.emptyIterator());
        assertEquals(8, FunctionalUtil.streamTree(parent, Page::listChildren).count());
    }

    /**
     * Method under test:
     * <ul>
     *   <li>{@link FunctionalUtil#streamDescendants(Object, Function)}</li>
     *   <li>{@link FunctionalUtil#streamDescendants(Object, Function, int)}</li>
     * </ul>
     */
    @Test
    void streamDescendants() {
        final Page pageNull = null;
        assertEquals(0, FunctionalUtil.streamDescendants(pageNull, Page::listChildren).count());
        assertEquals(0, FunctionalUtil.streamDescendants(pageNull, Page::listChildren,3).count());

        when(parent.listChildren()).thenReturn(Arrays.asList(child1, child2, child3).iterator());
        when(child1.listChildren()).thenReturn(Collections.emptyIterator());
        when(child2.listChildren()).thenReturn(Collections.emptyIterator());
        when(child3.listChildren()).thenReturn(Collections.emptyIterator());
        assertEquals(3, FunctionalUtil.streamDescendants(parent, Page::listChildren).count());

        when(parent.listChildren()).thenReturn(Arrays.asList(child1, child2, child3).iterator());
        when(child1.listChildren()).thenReturn(Arrays.asList(child1Sub1, child1Sub2).iterator());
        when(child1Sub1.listChildren()).thenReturn(Collections.emptyIterator());
        when(child1Sub2.listChildren()).thenReturn(Collections.emptyIterator());
        when(child2.listChildren()).thenReturn(Collections.emptyIterator());
        when(child3.listChildren()).thenReturn(Collections.emptyIterator());
        assertEquals(5, FunctionalUtil.streamDescendants(parent, Page::listChildren).count());

        when(parent.listChildren()).thenReturn(Arrays.asList(child1, child2, child3).iterator());
        when(child1.listChildren()).thenReturn(Arrays.asList(child1Sub1, child1Sub2).iterator());
        when(child1Sub1.listChildren()).thenReturn(List.of(child1Sub1Sub1).iterator());
        when(child1Sub1Sub1.listChildren()).thenReturn(Collections.emptyIterator());
        when(child1Sub2.listChildren()).thenReturn(Collections.emptyIterator());
        when(child2.listChildren()).thenReturn(Collections.emptyIterator());
        when(child3.listChildren()).thenReturn(Collections.emptyIterator());
        assertEquals(6, FunctionalUtil.streamDescendants(parent, Page::listChildren).count());

        when(parent.listChildren()).thenReturn(Arrays.asList(child1, child2, child3).iterator());
        when(child1.listChildren()).thenReturn(Arrays.asList(child1Sub1, child1Sub2).iterator());
        when(child1Sub1.listChildren()).thenReturn(Collections.emptyIterator());
        when(child1Sub2.listChildren()).thenReturn(Collections.emptyIterator());
        when(child2.listChildren()).thenReturn(Collections.emptyIterator());
        when(child3.listChildren()).thenReturn(Collections.emptyIterator());
        assertEquals(3, FunctionalUtil.streamDescendants(parent, Page::listChildren, 1).count());

        when(parent.listChildren()).thenReturn(Arrays.asList(child1, child2, child3).iterator());
        when(child1.listChildren()).thenReturn(Arrays.asList(child1Sub1, child1Sub2).iterator());
        when(child1Sub1.listChildren()).thenReturn(List.of(child1Sub1Sub1).iterator());
        when(child1Sub1Sub1.listChildren()).thenReturn(Collections.emptyIterator());
        when(child1Sub2.listChildren()).thenReturn(Collections.emptyIterator());
        when(child2.listChildren()).thenReturn(Collections.emptyIterator());
        when(child3.listChildren()).thenReturn(Collections.emptyIterator());
        assertEquals(5, FunctionalUtil.streamDescendants(parent, Page::listChildren, 2).count());

        when(parent.listChildren()).thenReturn(Arrays.asList(child1, child2, child3).iterator());
        when(child1.listChildren()).thenReturn(Arrays.asList(child1Sub1, child1Sub2).iterator());
        when(child1Sub1.listChildren()).thenReturn(List.of(child1Sub1Sub1).iterator());
        when(child1Sub1Sub1.listChildren()).thenReturn(Collections.emptyIterator());
        when(child1Sub2.listChildren()).thenReturn(Collections.emptyIterator());
        when(child2.listChildren()).thenReturn(List.of(child2Sub1).iterator());
        when(child2Sub1.listChildren()).thenReturn(Collections.emptyIterator());
        when(child3.listChildren()).thenReturn(Collections.emptyIterator());
        assertEquals(7, FunctionalUtil.streamDescendants(parent, Page::listChildren).count());
    }

    /**
     * Methods under test: {@link FunctionalUtil#streamChildren(Object, Function)}
     */
    @Test
    void streamChildren() {
        when(parent.listChildren()).thenReturn(Arrays.asList(child1, child2, child3).iterator());
        assertEquals(3, FunctionalUtil.streamChildren(parent, Page::listChildren).count());

        when(parent.listChildren()).thenReturn(List.of(child1).iterator());
        assertEquals(1, FunctionalUtil.streamChildren(parent, Page::listChildren).count());

        when(parent.listChildren()).thenReturn(Arrays.asList(child2, child3).iterator());
        assertEquals(2, FunctionalUtil.streamChildren(parent, Page::listChildren).count());
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
