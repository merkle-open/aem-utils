package com.merkle.oss.aem.utils.java;

import com.merkle.oss.aem.utils.annotations.Generated;
import org.jspecify.annotations.NonNull;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Provides functional programming utilities to bridge legacy Java/AEM APIs with modern Stream capabilities.
 */
public final class FunctionalUtil {

    @Generated("Bypass coverage for static utility constructor")
    private FunctionalUtil() {
        ClassUtil.assertNoInstance(this.getClass());
    }

    /**
     * Converts an {@link Iterator} into a sequential {@link Stream}.
     * <p>
     * This is particularly useful in AEM when dealing with legacy APIs like {@code ResourceResolver.findResources}
     * or {@code Page.listChildren} that return iterators.
     *
     * @param iterator The iterator to convert.
     * @param <T>      The type of elements returned by the iterator.
     * @return A sequential Stream backed by the provided iterator.
     * @apiNote Example usage:
     * {@snippet :
     * final Iterator<Resource> children = page.getContentResource().listChildren();
     * final List<String> names = FunctionalUtil.asStream(children)
     *    .map(Resource::getName)
     *    .collect(Collectors.toList());
     *}
     */
    public static @NonNull <T> Stream<T> asStream(@NonNull final Iterator<T> iterator) {
        Objects.requireNonNull(iterator);

        return asStream(asIterable(iterator));
    }

    /**
     * Internal helper to convert an {@link Iterable} into a sequential {@link Stream}.
     *
     * @param iterable The iterable source.
     * @param <T>      The type of elements.
     * @return A sequential Stream.
     */
    private static @NonNull <T> Stream<T> asStream(@NonNull final Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * Adapts an {@link Iterator} to the {@link Iterable} interface.
     *
     * @param iterator The iterator to wrap.
     * @param <T>      The type of elements.
     * @return An iterable wrapper around the iterator.
     */
    private static @NonNull <T> Iterable<T> asIterable(@NonNull final Iterator<T> iterator) {
        return () -> iterator;
    }

    /**
     * Returns a {@link Collector} that accumulates the input elements into a new unmodifiable {@link List},
     * deduplicating them based on a key extracted by the provided {@link Function}.
     * <p>
     * If multiple elements generate the same key, the first element encountered in the stream
     * is preserved, and subsequent duplicates are discarded (First-In-Wins).
     * <p>
     * This collector preserves the encounter order of the stream elements.
     *
     * @param <T>          the type of the input elements
     * @param <K>          the type of the key used for deduplication
     * @param keyExtractor a non-interfering, stateless function to extract the comparison key
     * @return a {@code Collector} which collects elements into a distinct, unmodifiable {@code List} in encounter order
     * @apiNote This implementation uses a {@link LinkedHashMap} internally to ensure order preservation and
     * {@link List#copyOf(java.util.Collection)} to ensure the resulting list is immutable.
     * Unlike stateful predicates, this collector is safe for use with parallel streams.
     * <p>
     * Example usage:
     * {@snippet :
     * final List<User> distinctUsers = userList.stream()
     * .collect(FunctionalUtil.toDistinctList(User::getEmail));
     *}
     */
    public static <T, K> Collector<T, ?, List<T>> toDistinctList(Function<? super T, K> keyExtractor) {
        return Collectors.collectingAndThen(
                Collectors.toMap(
                        keyExtractor,
                        t -> t,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ),
                map -> List.copyOf(map.values())
        );
    }

}
