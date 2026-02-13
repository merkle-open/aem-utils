package com.merkle.oss.aem.utils.annotations.tooling;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Marker annotation used to identify code elements that should be excluded from
 * automated code coverage reports and static analysis.
 * <p>
 * While the name suggests machine-generated code, this specific implementation
 * is primarily intended to bypass coverage requirements for "unreachable"
 * boilerplate, such as:
 * <ul>
 *      <li>Private constructors in utility classes.</li>
 *      <li>Synthetic methods.</li>
 *      <li>Complex {@code equals}, {@code hashCode}, or {@code toString} overrides.</li>
 * </ul>
 * </p>
 * <p>
 * This annotation is designed to be recognized by <b>JaCoCo (version 0.8.2+)</b>
 * and other bytecode-based analysis tools. For these tools to ignore the annotated
 * code, the retention policy must be {@link RetentionPolicy#CLASS}.
 *
 * @apiNote Example usage:
 * {@snippet :
 * public final class UtilityClass {
 *     @Generated("Bypass coverage for static utility constructor")
 *     private UtilityClass() {
 *         //private
 *     }
 * }
 *}
 */
@Retention(RetentionPolicy.CLASS)
@Target({TYPE, METHOD, CONSTRUCTOR})
public @interface Generated {
    String value() default "";
}
