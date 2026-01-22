package com.merkle.oss.aem.utils.injector.annotation;

import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.merkle.oss.aem.utils.injector.PagePropertyInjector.INJECTOR_NAME_PAGE_PROPERTY;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Custom Sling Models injection annotation for retrieving properties from an AEM Page.
 * <p>
 * This annotation allows for the injection of properties from the current page's {@code jcr:content} node,
 * with optional support for hierarchical inheritance (searching up the page tree).
 *
 * @apiNote Example usage:
 * {@snippet :
 *   // Injects 'pageTitle' from the current page
 *   @PageProperty private String pageTitle;
 *   // Injects 'brandColor' from the current page or its ancestors as required
 *   @PageProperty(name = "brandColor", inherited = true, injectionStrategy = InjectionStrategy.REQUIRED)
 *   private String color;
 *}
 * @see com.merkle.oss.aem.utils.injector.PagePropertyInjector
 */
@Target({METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
@InjectAnnotation
@Source(INJECTOR_NAME_PAGE_PROPERTY)
public @interface PageProperty {

    /**
     * The name of the page property to inject.
     * <p>
     * If not specified, the name of the annotated field or method is used as the property name.
     *
     * @return the property name, defaults to empty string
     */
    String name() default "";

    /**
     * Defines whether the injector should look for the property in the page hierarchy.
     * <p>
     * If set to {@code true}, the injector will traverse up the AEM page tree until the
     * property is found or the root is reached.
     *
     * @return {@code true} if the property should be inherited; {@code false} otherwise
     */
    boolean inherited() default false;

    /**
     * Defines the injection strategy (Required vs. Optional).
     * <p>
     * If set to {@link org.apache.sling.models.annotations.injectorspecific.InjectionStrategy#REQUIRED}, model instantiation will fail if
     * the property cannot be resolved.
     *
     * @return the injection strategy, defaults to {@link org.apache.sling.models.annotations.injectorspecific.InjectionStrategy#OPTIONAL}
     */
    InjectionStrategy injectionStrategy() default InjectionStrategy.OPTIONAL;

}
