package com.merkle.oss.aem.utils.java;

import com.merkle.oss.aem.utils.annotations.tooling.Generated;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Provides functional programming utilities to bridge legacy Java/AEM APIs with Stream capabilities.
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
     * final List<Resource> assets = FunctionalUtil.asStream(DamUtil.getAssets(rootFolder))
     *         .filter(DamUtil::isImage)
     *         .map(to(Resource.class))
     *         .filter(Objects::nonNull)
     *         .toList();
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
     * Traverses the hierarchy starting with the {@code current} element and moving downwards
     * through its descendants, including the {@code current} element as the first item in the stream.
     *
     * @param <T>              the type of the hierarchical element
     * @param current          the starting element; may be {@code null}
     * @param childrenProvider a function that retrieves an {@link Iterator} of children for a given element
     * @return a {@link Stream} starting with the {@code current} element followed by its descendants
     * {@snippet :
     *  final List<Resource> validResources = FunctionalUtil.streamTree(resource, Resource::listChildren)
     *                 .filter(ResourceUtil::isValid)
     *                 .toList();
     *}
     */
    public static <T> @NonNull Stream<T> streamTree(@Nullable final T current, @NonNull final Function<T, Iterator<T>> childrenProvider) {
        return streamTree(current, childrenProvider, 0);
    }

    /**
     * Traverses the hierarchy starting with the {@code current} element and moving downwards
     * through its descendants, including the {@code current} element as the first item in the stream.
     *
     * @param <T>              the type of the hierarchical element
     * @param current          the starting element; may be {@code null}
     * @param childrenProvider a function that retrieves an {@link Iterator} of children for a given element
     * @param maxDepth         the maximum depth of the traversal. {@code 1} limits the search to
     *                         immediate children; {@code 0} or less allows for infinite depth.
     * @return a {@link Stream} starting with the {@code current} element followed by its descendants
     * {@snippet :
     *  final List<Resource> validResources = FunctionalUtil.streamTree(resource, Resource::listChildren, 3)
     *                 .filter(ResourceUtil::isValid)
     *                 .toList();
     *}
     */
    public static <T> @NonNull Stream<T> streamTree(@Nullable final T current, @NonNull final Function<T, Iterator<T>> childrenProvider, final int maxDepth) {
        if (current == null) {
            return Stream.empty();
        }

        return Stream.concat(
                Stream.of(current),
                streamDescendants(current, childrenProvider, maxDepth)
        );
    }

    /**
     * Traverses the hierarchy starting from the immediate children of the {@code current} element,
     * excluding the {@code current} element itself.
     *
     * @param <T>              the type of the hierarchical element
     * @param current          the element whose descendants should be streamed; may be {@code null}
     * @param childrenProvider a function that retrieves an {@link Iterator} of children for a given element
     * @return a {@link Stream} of descendants discovered through pre-order traversal
     * @apiNote Example usage:
     * {@snippet :
     *  final List<Resource> validResources = FunctionalUtil.streamDescendants(resource, Resource::listChildren)
     *                 .filter(ResourceUtil::isValid)
     *                 .toList();
     *}
     */
    public static <T> @NonNull Stream<T> streamDescendants(@Nullable final T current, @NonNull final Function<T, Iterator<T>> childrenProvider) {
        return streamDescendants(current, childrenProvider, 0);
    }

    /**
     * Traverses the hierarchy starting from the immediate children of the {@code current} element,
     * excluding the {@code current} element itself.
     *
     * @param <T>              the type of the hierarchical element
     * @param current          the element whose descendants should be streamed; may be {@code null}
     * @param childrenProvider a function that retrieves an {@link Iterator} of children for a given element
     * @param maxDepth         the maximum depth of the traversal. {@code 1} limits the search to
     *                         immediate children; {@code 0} or less allows for infinite depth.
     * @return a {@link Stream} of descendants discovered through pre-order traversal
     * @apiNote Example usage:
     * {@snippet :
     *  final List<Resource> validResources = FunctionalUtil.streamDescendants(resource, Resource::listChildren, 3)
     *                 .filter(ResourceUtil::isValid)
     *                 .toList();
     *}
     */
    public static <T> @NonNull Stream<T> streamDescendants(@Nullable final T current, @NonNull final Function<T, Iterator<T>> childrenProvider, final int maxDepth) {
        final int effectiveDepth = (maxDepth <= 0) ? Integer.MAX_VALUE : maxDepth;
        return collectStream(current, childrenProvider, effectiveDepth);
    }

    /**
     * Internal recursive engine that lazily generates a concatenated stream of children
     * and their subtrees.
     *
     * @param current          the element whose children are being processed
     * @param childrenProvider the child accessor function
     * @param depth            the current remaining depth in the recursion
     * @return a {@link Stream} of elements at the current level and below
     */
    private static <T> @NonNull Stream<T> collectStream(@Nullable final T current, @NonNull final Function<T, Iterator<T>> childrenProvider, final int depth) {
        Objects.requireNonNull(childrenProvider);

        if (current == null) {
            return Stream.empty();
        }

        final Stream<T> children = streamChildren(current, childrenProvider);

        if (depth == 1) {
            return children;
        }

        return children.flatMap(child -> Stream.concat(
                Stream.of(child),
                collectStream(child, childrenProvider, depth - 1)
        ));
    }

    /**
     * Creates a {@link Stream} from the direct children of a given parent element.
     *
     * @param <T>              the type of the parent element
     * @param <U>              the type of the child elements
     * @param parent           the parent element to stream children from; may be {@code null}
     * @param childrenProvider a function that retrieves an {@link Iterator} of children from the parent
     * @return a {@link Stream} of child elements, or {@link Stream#empty()} if the parent is null
     * or has no children
     * @apiNote Example usage:
     * {@snippet :
     * final List<Page> siblingPages = FunctionalUtil.streamChildren(currentPage.getParent(), page -> page.listChildren(new PageFilter()))
     *                 .toList();
     *}
     */
    public static <T, U> @NonNull Stream<U> streamChildren(@Nullable final T parent, @NonNull final Function<T, Iterator<U>> childrenProvider) {
        Objects.requireNonNull(childrenProvider);

        return Optional.ofNullable(parent)
                .map(childrenProvider)
                .map(FunctionalUtil::asStream)
                .orElseGet(Stream::empty);
    }

    /**
     * Traverses upward through a hierarchical structure to find the first element
     * that satisfies the provided condition.
     * <p>
     * The traversal starts with the {@code start} element itself and proceeds to
     * successive parents as defined by the {@code parentFunc}. The search is
     * short-circuiting; it stops as soon as a match is found or the root
     * (where {@code parentFunc} returns {@code null}) is reached.
     *
     * @param <T>            the type of the hierarchical element (e.g., Page, Resource)
     * @param start          the starting element for the search; may be {@code null}
     * @param parentProvider a function that maps an element to its immediate parent
     * @param cond           the predicate used to evaluate each element in the hierarchy
     * @return an {@link Optional} containing the first matching element, or
     * {@link Optional#empty()} if no match is found or the input is null
     * @apiNote Example usage:
     * {@snippet :
     * final Page viewableParentPage = FunctionalUtil.findClosestAncestorByPredicate(currentPage.getParent(), Page::getParent, page -> !page.isHideInNav());
     *}
     * @see Stream#iterate(Object, Predicate, UnaryOperator)
     */
    public static <T> Optional<T> findClosestAncestorByPredicate(@Nullable final T start, @NonNull final UnaryOperator<T> parentProvider, @NonNull final Predicate<T> cond) {
        Objects.requireNonNull(parentProvider);
        Objects.requireNonNull(cond);

        if (start == null) {
            return Optional.empty();
        }

        return Stream.iterate(start, Objects::nonNull, parentProvider)
                .filter(cond)
                .findFirst();
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
     *         .collect(FunctionalUtil.toDistinctList(User::getEmail));
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
