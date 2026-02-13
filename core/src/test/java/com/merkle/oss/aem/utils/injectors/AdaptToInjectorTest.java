package com.merkle.oss.aem.utils.injectors;

import com.day.cq.wcm.api.PageManager;
import com.merkle.oss.aem.utils.annotations.injectors.AdaptTo;
import com.merkle.oss.aem.utils.injectors.impl.AdaptToInjector;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link AdaptToInjector} class.
 */
@ExtendWith(MockitoExtension.class)
class AdaptToInjectorTest {

    @Mock
    private AnnotatedElement annotatedElement;

    @Mock
    private PageManager pageManager;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private DisposalCallbackRegistry registry;

    @Mock
    private AdaptTo adaptToAnnotation;

    @InjectMocks
    private AdaptToInjector adaptToInjector;

    /**
     * Method under test: {@link AdaptToInjector#getName()}
     */
    @Test
    void getName() {
        assertNotNull(new AdaptToInjector().getName());
    }

    /**
     * Method under test: {@link AdaptToInjector#getValue(Object, String, Type, AnnotatedElement, DisposalCallbackRegistry)}
     */
    @Test
    void getValue() {
        assertThrows(NullPointerException.class, () -> adaptToInjector.getValue(null, "", null, null, null));
        final Object object = new Object();
        assertThrows(NullPointerException.class, () -> adaptToInjector.getValue(object, "", null, null, null));

        when(annotatedElement.isAnnotationPresent(AdaptTo.class)).thenReturn(false);
        assertNull(adaptToInjector.getValue(new Object(), "myTitle", String.class, annotatedElement, registry));

        when(annotatedElement.isAnnotationPresent(AdaptTo.class)).thenReturn(true);
        assertNull(adaptToInjector.getValue(new Object(), "myTitle", String.class, annotatedElement, registry));
        assertNull(adaptToInjector.getValue(resourceResolver, "myTitle", String.class, annotatedElement, registry));

        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        assertEquals(pageManager, adaptToInjector.getValue(resourceResolver, "myTitle", PageManager.class, annotatedElement, registry));

    }

    /**
     * Method under test: {@link AdaptToInjector#createAnnotationProcessor(AnnotatedElement)}
     */
    @Test
    void createAnnotationProcessor() {
        assertNull(adaptToInjector.createAnnotationProcessor(annotatedElement));

        when(annotatedElement.getAnnotation(AdaptTo.class)).thenReturn(adaptToAnnotation);
        assertNotNull(adaptToInjector.createAnnotationProcessor(annotatedElement));
        assertNull(Objects.requireNonNull(adaptToInjector.createAnnotationProcessor(annotatedElement)).getName());

        when(adaptToAnnotation.via()).thenReturn(null);
        assertNull(Objects.requireNonNull(adaptToInjector.createAnnotationProcessor(annotatedElement)).getVia());

        when(adaptToAnnotation.via()).thenReturn("resource");
        assertEquals("resource", Objects.requireNonNull(adaptToInjector.createAnnotationProcessor(annotatedElement)).getVia());
        assertEquals(InjectionStrategy.DEFAULT, Objects.requireNonNull(adaptToInjector.createAnnotationProcessor(annotatedElement)).getInjectionStrategy());
    }

}
