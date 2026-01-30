package com.merkle.oss.aem.utils.java;

import com.merkle.oss.aem.utils.annotations.Generated;
import org.jspecify.annotations.NonNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Provides functional programming utilities to bridge legacy Java/AEM APIs with modern Stream capabilities.
 */
public final class FunctionalUtil {

    @Generated
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
     * Returns a stateful {@link Predicate} that maintains a set of seen keys to allow
     * filtering a stream by a specific property of its elements.
     * <p>
     * Unlike {@link Stream#distinct()}, which uses the {@code equals()} method of the object itself,
     * this method allows for distinction based on a mapped key (e.g., a specific field or ID).
     * </p>
     * <p>
     * <b>Thread Safety:</b> This uses a {@link ConcurrentHashMap} and is safe for use with parallel streams.
     * However, for parallel streams, the specific element preserved among duplicates is non-deterministic.
     *
     * @param keyExtractor A function to extract the comparison key from the element.
     * @param <T>          The type of the stream elements.
     * @return A predicate that returns {@code true} the first time it encounters a specific key.
     * @apiNote Example usage:
     * {@snippet :
     * final List<User> distinctUsers = stream.filter(FunctionalUtil.distinctByKey(User::getEmail))
     *     .collect(Collectors.toList());
     *}
     */
    public static @NonNull <T> Predicate<T> distinctByKey(@NonNull final Function<? super T, ?> keyExtractor) {
        Objects.requireNonNull(keyExtractor);

        final Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

}
