package com.merkle.oss.aem.utils;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Methods for functional programming in AEM.
 */
public final class FunctionalUtil {

    /**
     * Converts an iterator object into a stream to
     * use it with functional features.
     *
     * @param iterator to convert to stream
     * @param <T>      type of iterator
     * @return stream form iterator
     * @apiNote Example:
     * <pre>{@code final Iterator<String> iterator = Arrays.asList(test, null).iterator();
     * final List<String> nullCollection = FunctionalUtil.asStream(iterator).collect(Collectors.toList());}</pre>
     */
    public static <T> Stream<T> asStream(Iterator<T> iterator) {
        return asStream(asIterable(iterator));
    }

    private static <T> Stream<T> asStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private static <T> Iterable<T> asIterable(Iterator<T> iterator) {
        return (() -> iterator);
    }

    /**
     * Negates a given predicate using predicate negate.
     * <p>
     *
     * @param predicate without negation, mostly as lambda or method reference
     * @param <T>       Type of consumed object
     * @return Negated predicted
     * @apiNote This method makes it easier to negate a predicate and removes boilerplate code.
     * <pre>{@code return Optional.ofNullable(page)
     *  .filter(not(Page::isHideInNav))
     *  .map(Page::getTitle)
     *  .orElse("default value");}</pre>
     */
    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return predicate.negate();
    }

    /**
     * Converts an {@link Optional} to a sequential singleton {@link Stream}.
     * <p>
     * Allows for a more fluent functional programming style,
     * in case of following consuming methods can not handle objects of optional.
     *
     * @return Function to map an optional to stream which can be used in Stream#flatMap
     * @apiNote Example:
     * <pre>{@code return object.stream()
     *  .map(objectId -> service.getPotentialOptionalObjectById(objectId))
     *  .flatMap(fromOptional())
     *  .map(object -> methodNotAbleToConsumeOptionalObject(object, type))
     *  .collect(Collectors.toList());}
     * </pre>
     */
    public static Function<Optional<?>, Stream<?>> fromOptional() {
        return optional -> optional.map(Stream::of)
                .orElseGet(Stream::empty);
    }

}
