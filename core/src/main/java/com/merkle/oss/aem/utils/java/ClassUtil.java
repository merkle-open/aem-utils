package com.merkle.oss.aem.utils.java;

import org.jspecify.annotations.NonNull;

/**
 * Static utility for common Java reflection and class-handling tasks.
 * <p>
 * This class provides helper methods to enforce coding standards, such as
 * preventing the instantiation of utility or constant-only classes.
 */
public final class ClassUtil {

    /**
     * private constructor to prevent instantiation.
     * <p>
     * Throws an {@link AssertionError} immediately upon invocation to ensure
     * this class remains a static-only utility.
     *
     * @see #assertNoInstance(Class)
     */
    private ClassUtil() {
        assertNoInstance(this.getClass());
    }

    /**
     * Throws an {@link AssertionError} indicating that the provided class is a
     * static utility and is not meant to be instantiated.
     * <p>
     * This is intended to be used within the private or package-private
     * constructors of utility/constant classes to provide a clear, fail-fast
     * mechanism against accidental reflection or internal instantiation.
     *
     * @param clazz The class type to include in the error message.
     * @param <T>   The type of the class.
     * @throws AssertionError always, containing the simple name of the class.
     * @apiNote Example usage:
     * {@snippet :
     * public final class MyUtils {
     *     private MyUtils() {
     *         ClassUtil.assertNoInstance(getClass());
     *     }
     * }
     *}
     */
    public static <T> void assertNoInstance(@NonNull final Class<T> clazz) {
        throw new AssertionError(clazz.getSimpleName() + " is not meant to be instantiated.");
    }

}
