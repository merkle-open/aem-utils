package com.merkle.oss.aem.utils.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Custom com.merkle.oss.aem.utils.annotations.Generated annotation with CLASS retention so JaCoCo can see it in bytecode.
 */
@Retention(RetentionPolicy.CLASS)
@Target({TYPE, METHOD, CONSTRUCTOR})
public @interface Generated {
    String value() default "";
}
