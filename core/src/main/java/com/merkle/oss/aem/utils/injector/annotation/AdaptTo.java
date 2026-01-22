package com.merkle.oss.aem.utils.injector.annotation;

import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.merkle.oss.aem.utils.injector.AdaptToInjector.INJECTOR_NAME_ADAPT_TO;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Custom Sling Models injection annotation that triggers an adaptation
 * via the "adapt-to" injector.
 * <p>
 * This is typically used to adapt a specific object (like a Resource or Request)
 * into a different type during the Model instantiation process.
 *
 * @apiNote Example usage:
 * {@snippet :
 * @Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
 * public class ClassName {
 *
 * @AdaptTo
 * private ModelName modelViaRequest;
 * //If OtherModelName is implemented via adaptables = Resource.class
 * @AdaptTo(via = "resource")
 * private OtherModelName modelViaResource;
 *
 * }
 *}
 * @see com.merkle.oss.aem.utils.injector.AdaptToInjector
 */
@Target({METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
@InjectAnnotation
@Source(INJECTOR_NAME_ADAPT_TO)
public @interface AdaptTo {

    /**
     * Defines the source object to be used for the adaptation.
     * <p>
     * Common values include "resource", "resourceResolver", or "request".
     * Default is "resourceResolver".
     *
     * @return the source identifier for adaptation.
     */
    String via() default "resourceResolver";

}
