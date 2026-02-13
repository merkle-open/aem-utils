package com.merkle.oss.aem.utils.injectors.impl;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.merkle.oss.aem.utils.injectors.InjectorUtil;
import com.merkle.oss.aem.utils.annotations.injectors.PageProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
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
import java.util.function.Supplier;

/**
 * Injector implementation for the {@link PageProperty} annotation.
 * <p>
 * This injector resolves the target page by:
 * <ol>
 * <li>Attempting to adapt the current {@code adaptable} (Request or Resource) directly to a {@link com.day.cq.wcm.api.Page}.</li>
 * <li>Using the {@code PageManager} to find the page containing the current resource.</li>
 * </ol>
 * It supports both local properties (from the page's {@code jcr:content}) and hierarchical
 * inherited properties via {@link com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap}.
 */
@Component(service = {Injector.class, StaticInjectAnnotationProcessorFactory.class})
@ServiceRanking(2500)
public class PagePropertyInjector implements Injector, StaticInjectAnnotationProcessorFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PagePropertyInjector.class);

    public static final String INJECTOR_NAME_PAGE_PROPERTY = "page-property";

    /**
     * {@inheritDoc}
     *
     * @see org.apache.sling.models.spi.Injector#getName()
     */
    @Override
    public @NonNull String getName() {
        return INJECTOR_NAME_PAGE_PROPERTY;
    }

    /**
     * Injects a value from a Page context into the annotated Sling Model field or parameter.
     * <p>
     * The logic follows these steps:
     * <ul>
     * <li>Verifies the presence of the {@link PageProperty} annotation.</li>
     * <li>Resolves the relevant {@link com.day.cq.wcm.api.Page} from the adaptable.</li>
     * <li>Obtains either a standard {@link org.apache.sling.api.resource.ValueMap} or an inheritance-aware map.</li>
     * <li>Extracts and returns the value matching the requested name and type.</li>
     * </ul>
     * </p>
     *
     * @param adaptable                The object being adapted (e.g., {@code SlingHttpServletRequest} or {@code Resource}).
     * @param name                     The default name of the property to look for.
     * @param declaredType             The target type for the injected value.
     * @param annotatedElement         The element (field/parameter) being injected.
     * @param disposalCallbackRegistry Registry for any necessary cleanup.
     * @return The resolved property value, or {@code null} if resolution fails.
     */
    @Override
    public @Nullable Object getValue(@NonNull final Object adaptable, final String name, @NonNull final Type declaredType,
                                     @NonNull final AnnotatedElement annotatedElement, @NonNull final DisposalCallbackRegistry disposalCallbackRegistry) {
        if (annotatedElement.getAnnotation(PageProperty.class) != null) {
            final boolean inherited = annotatedElement.getAnnotation(PageProperty.class).inherited();
            return InjectorUtil.getPageFromAdaptable(adaptable)
                    .map(page -> InjectorUtil.getValueFromValueMap(getProperties(page, inherited), name, declaredType))
                    .orElseGet(logAndReturnNull(adaptable, name));
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
        final PageProperty annotation = annotatedElement.getAnnotation(PageProperty.class);
        return Optional.ofNullable(annotation)
                .map(PagePropertyInjectAnnotationProcessor::new)
                .orElse(null);
    }

    /**
     * Resolves the {@link org.apache.sling.api.resource.ValueMap} for the page content.
     * <p>
     * If {@code inherited} is true, a {@link com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap} is returned,
     * which allows looking up properties in parent pages if they are missing locally.
     * </p>
     * *
     *
     * @param page      The page to get properties from.
     * @param inherited Whether to use hierarchical inheritance.
     * @return A ValueMap containing the page's properties.
     */
    private @Nullable ValueMap getProperties(@NonNull final Page page, final boolean inherited) {
        if (inherited) {
            return new HierarchyNodeInheritanceValueMap(page.getContentResource());
        }

        return page.getProperties();
    }

    private @NonNull Supplier<Object> logAndReturnNull(@NonNull final Object adaptable, @NonNull final String name) {
        return () -> {
            LOG.debug("Could not inject page property {} because injector was not able to get a page from {}", name, adaptable);
            return null;
        };
    }

    /**
     * Processor for handling {@link PageProperty} annotation metadata during injection.
     */
    private static class PagePropertyInjectAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

        private final PageProperty annotation;

        PagePropertyInjectAnnotationProcessor(@NonNull final PageProperty annotation) {
            this.annotation = annotation;
        }

        @Override
        public @Nullable String getName() {
            return StringUtils.isNotEmpty(this.annotation.name()) ? this.annotation.name() : null;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2#getInjectionStrategy()
         */
        @Override
        public @NonNull InjectionStrategy getInjectionStrategy() {
            return this.annotation.injectionStrategy();
        }

    }

}
