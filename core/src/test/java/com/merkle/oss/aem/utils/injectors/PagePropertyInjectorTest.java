package com.merkle.oss.aem.utils.injectors;

import com.day.cq.wcm.api.Page;
import com.merkle.oss.aem.utils.annotations.injectors.PageProperty;
import com.merkle.oss.aem.utils.injectors.impl.PagePropertyInjector;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link PagePropertyInjector} class.
 */
@ExtendWith(MockitoExtension.class)
class PagePropertyInjectorTest {

    @Mock
    private AnnotatedElement annotatedElement;

    @Mock
    private DisposalCallbackRegistry registry;

    @Mock
    private Page page;

    @Mock
    private ValueMap valueMap;

    @Mock
    private Resource contentResource;

    @Mock
    private PageProperty pagePropertyAnnotation;

    @InjectMocks
    private PagePropertyInjector pagePropertyInjector;

    /**
     * Method under test: {@link PagePropertyInjector#getName()}
     */
    @Test
    void getName() {
        assertNotNull(new PagePropertyInjector().getName());
    }

    /**
     * Method under test: {@link PagePropertyInjector#getValue(Object, String, Type, AnnotatedElement, DisposalCallbackRegistry)}
     */
    @Test
    void getValue() {
        assertThrows(NullPointerException.class, () -> pagePropertyInjector.getValue(null, "", null, null, null));
        final Object object = new Object();
        assertThrows(NullPointerException.class, () -> pagePropertyInjector.getValue(object, "", null, null, null));

        when(annotatedElement.getAnnotation(PageProperty.class)).thenReturn(null);
        assertNull(pagePropertyInjector.getValue(new Object(), "myTitle", String.class, annotatedElement, registry));

        when(pagePropertyAnnotation.inherited()).thenReturn(false);
        when(annotatedElement.getAnnotation(PageProperty.class)).thenReturn(pagePropertyAnnotation);

        try (MockedStatic<InjectorUtil> injectorUtilInfo = mockStatic(InjectorUtil.class)) {
            final Object adaptable = new Object();
            injectorUtilInfo.when(() -> InjectorUtil.getPageFromAdaptable(adaptable)).thenReturn(Optional.of(page));
            injectorUtilInfo.when(() -> InjectorUtil.getValueFromValueMap(any(), eq("myTitle"), eq(String.class))).thenReturn(null);
            when(page.getProperties()).thenReturn(null);
            assertNull(pagePropertyInjector.getValue(adaptable, "myTitle", String.class, annotatedElement, registry));

            injectorUtilInfo.when(() -> InjectorUtil.getValueFromValueMap(any(), eq("myTitle"), eq(String.class))).thenReturn("SuccessValue");
            when(page.getProperties()).thenReturn(valueMap);
            assertEquals("SuccessValue", pagePropertyInjector.getValue(adaptable, "myTitle", String.class, annotatedElement, registry));

            when(pagePropertyAnnotation.inherited()).thenReturn(true);
            when(page.getContentResource()).thenReturn(contentResource);
            assertEquals("SuccessValue", pagePropertyInjector.getValue(adaptable, "myTitle", String.class, annotatedElement, registry));
        }
    }

    /**
     * Method under test: {@link PagePropertyInjector#createAnnotationProcessor(AnnotatedElement)}
     */
    @Test
    void createAnnotationProcessor() {
        assertNull(pagePropertyInjector.createAnnotationProcessor(annotatedElement));

        when(annotatedElement.getAnnotation(PageProperty.class)).thenReturn(pagePropertyAnnotation);
        assertNotNull(pagePropertyInjector.createAnnotationProcessor(annotatedElement));
        assertNull(Objects.requireNonNull(pagePropertyInjector.createAnnotationProcessor(annotatedElement)).getName());

        when(pagePropertyAnnotation.name()).thenReturn("name");
        when(pagePropertyAnnotation.injectionStrategy()).thenReturn(InjectionStrategy.DEFAULT);
        assertEquals("name", Objects.requireNonNull(pagePropertyInjector.createAnnotationProcessor(annotatedElement)).getName());
        assertEquals(InjectionStrategy.DEFAULT, Objects.requireNonNull(pagePropertyInjector.createAnnotationProcessor(annotatedElement)).getInjectionStrategy());
    }

}
