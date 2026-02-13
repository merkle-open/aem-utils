package com.merkle.oss.aem.utils.injectors.impl;

import com.merkle.oss.aem.utils.annotations.injectors.AdaptTo;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Injector implementation for the {@link AdaptTo} annotation.
 * <p>
 * This injector facilitates the adaptation of a source object (defined by the {@code via} parameter)
 * into the declared field or parameter type.
 * </p>
 * <p>
 * If the {@code via} attribute is provided (e.g., "resource"), Sling first resolves
 * that object and then this injector calls {@code .adaptTo(DeclaredType.class)} on it.
 */
@Component(service = {Injector.class, StaticInjectAnnotationProcessorFactory.class})
@ServiceRanking(8000)
public class AdaptToInjector implements Injector, StaticInjectAnnotationProcessorFactory {

    private static final Logger LOG = LoggerFactory.getLogger(AdaptToInjector.class);

    public static final String INJECTOR_NAME_ADAPT_TO = "adapt-to";

    /**
     * {@inheritDoc}
     *
     * @see org.apache.sling.models.spi.Injector#getName()
     */
    @Override
    public @NonNull String getName() {
        return INJECTOR_NAME_ADAPT_TO;
    }

    /**
     * Performs the actual adaptation logic during Sling Model instantiation.
     * <p>
     * Only processes elements explicitly annotated with {@link AdaptTo}.
     * The method verifies if the provided adaptable source is an instance of
     * {@link org.apache.sling.api.adapter.Adaptable} before attempting the conversion.
     * </p>
     *
     * @param adaptable                The object currently being adapted (e.g., Request or Resource).
     * @param name                     The name of the field or parameter.
     * @param declaredType             The target type to adapt into.
     * @param annotatedElement         The reflection element (field/method/parameter).
     * @param disposalCallbackRegistry Registry for cleanup tasks.
     * @return The adapted object, or {@code null} if adaptation is not possible.
     */
    @Override
    public @Nullable Object getValue(@NonNull final Object adaptable, final String name, @NonNull final Type declaredType,
                                     @NonNull final AnnotatedElement annotatedElement, @NonNull final DisposalCallbackRegistry disposalCallbackRegistry) {
        if (annotatedElement.isAnnotationPresent(AdaptTo.class) && adaptable instanceof Adaptable a) {
            return a.adaptTo((Class<?>) declaredType);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory#createAnnotationProcessor(AnnotatedElement)
     */
    @Override
    public @Nullable InjectAnnotationProcessor2 createAnnotationProcessor(@NonNull final AnnotatedElement annotatedElement) {
        final AdaptTo annotation = annotatedElement.getAnnotation(AdaptTo.class);
        return Optional.ofNullable(annotation)
                .map(AdaptToInjectAnnotationProcessor::new)
                .orElse(null);
    }

    /**
     * Processor for handling {@link AdaptTo} annotation metadata during injection.
     */
    private static class AdaptToInjectAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

        private final AdaptTo annotation;

        AdaptToInjectAnnotationProcessor(@NonNull final AdaptTo annotation) {
            this.annotation = annotation;
        }

        /**
         * The via is set to resourceResolver by default. Therefore, only if via
         * is set to empty explicitly, the via is null and then the adaptTo
         * is executed directly on the given adaptable of the model.
         * <p>
         * This case should not be used because the Self-Annotation
         * is the proper one to adapt to itself.
         *
         * @return the configured via, null if the via is set to empty or resourceResolver if via is not configured.
         */
        @Override
        public @Nullable String getVia() {
            if (StringUtils.isBlank(annotation.via())) {
                LOG.warn("AdaptTo annotation should not be used with an empty via. Use the @Self annotation instead.");
                return null;
            }

            return annotation.via();
        }

    }

}
