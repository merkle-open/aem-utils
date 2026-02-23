package com.merkle.oss.aem.utils.wcm;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.constants.NameConstants;
import com.merkle.oss.aem.utils.java.FunctionalUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link PageUtil} class.
 */
@ExtendWith(MockitoExtension.class)
class PageUtilTest {

    private static final String TITLE = "Title";
    private static final String PAGE_TITLE = "PageTitle";
    private static final String NAVIGATION_TITLE = "NavigationTitle";
    private static final String PAGE_DESCRIPTION = "PageDescription";
    private static final String TEMPLATE_NAME_1 = "templateName1";
    private static final String TEMPLATE_PATH_1 = "template/path/" + TEMPLATE_NAME_1;
    private static final String TEMPLATE_NAME_2 = "templateName2";
    private static final String TEMPLATE_PATH_2 = "template/path/" + TEMPLATE_NAME_2;
    private static final String TEMPLATE_NAME_3 = "templateName3";
    private static final String TEMPLATE_PATH_3 = "template/path/" + TEMPLATE_NAME_3;
    private static final String PATH = "/some/path/value";
    private static final String PATH_2 = "/some/alternative/path/value";

    @Mock
    private Page single;
    @Mock
    private Page parent;
    @Mock
    private Page child1;
    @Mock
    private Page child2;
    @Mock
    private Page child3;

    /**
     * Method under test: {@link PageUtil#getTitle(Page)}
     */
    @Test
    void getTitle() {
        assertEquals(StringUtils.EMPTY, PageUtil.getTitle(null));

        when(single.getTitle()).thenReturn(TITLE);
        assertEquals(TITLE, PageUtil.getTitle(single));
    }

    /**
     * Method under test: {@link PageUtil#getPageTitle(Page)}
     */
    @Test
    void getPageTitle() {
        assertEquals(StringUtils.EMPTY, PageUtil.getPageTitle(null));

        when(single.getPageTitle()).thenReturn(PAGE_TITLE);
        assertEquals(PAGE_TITLE, PageUtil.getPageTitle(single));

        when(single.getPageTitle()).thenReturn(StringUtils.EMPTY);
        assertEquals(StringUtils.EMPTY, PageUtil.getPageTitle(single));

        when(single.getTitle()).thenReturn(TITLE);
        when(single.getPageTitle()).thenReturn(StringUtils.EMPTY);
        assertEquals(TITLE, PageUtil.getPageTitle(single));
    }

    /**
     * Method under test: {@link PageUtil#getNavigationTitle(Page)}
     */
    @Test
    void getNavigationTitle() {
        assertEquals(StringUtils.EMPTY, PageUtil.getNavigationTitle(null));

        when(single.getNavigationTitle()).thenReturn(NAVIGATION_TITLE);
        assertEquals(NAVIGATION_TITLE, PageUtil.getNavigationTitle(single));

        when(single.getPageTitle()).thenReturn(PAGE_TITLE);
        when(single.getNavigationTitle()).thenReturn(StringUtils.EMPTY);
        assertEquals(PAGE_TITLE, PageUtil.getNavigationTitle(single));
    }

    /**
     * Method under test: {@link PageUtil#getDescription(Page)}
     */
    @Test
    void getDescription() {
        assertEquals(StringUtils.EMPTY, PageUtil.getDescription(null));

        when(single.getDescription()).thenReturn(PAGE_DESCRIPTION);
        assertEquals(PAGE_DESCRIPTION, PageUtil.getDescription(single));
    }

    /**
     * Method under test: {@link PageUtil#getTemplatePath(Page)}
     */
    @Test
    void getTemplatePath() {
        assertEquals(StringUtils.EMPTY, PageUtil.getTemplatePath(null));

        when(single.getProperties()).thenReturn(new ValueMapDecorator(Collections.singletonMap(NameConstants.NN_TEMPLATE, TEMPLATE_PATH_1)));
        assertEquals(TEMPLATE_PATH_1, PageUtil.getTemplatePath(single));
    }

    /**
     * Method under test: {@link PageUtil#getTemplateName(Page)}
     */
    @Test
    void getTemplateName() {
        assertEquals(StringUtils.EMPTY, PageUtil.getTemplateName(null));

        final String simpleValue = "simpleValue";
        when(single.getProperties()).thenReturn(new ValueMapDecorator(Collections.singletonMap(NameConstants.NN_TEMPLATE, simpleValue)));
        assertEquals(simpleValue, PageUtil.getTemplateName(single));

        when(single.getProperties()).thenReturn(new ValueMapDecorator(Collections.singletonMap(NameConstants.NN_TEMPLATE, TEMPLATE_PATH_1)));
        assertEquals(TEMPLATE_NAME_1, PageUtil.getTemplateName(single));
    }

    /**
     * Method under test: {@link PageUtil#getProperty(Page, String)}
     */
    @Test
    void getProperty() {
        assertThrows(NullPointerException.class, () -> PageUtil.getProperty(single, null));

        when(single.getProperties()).thenReturn(new ValueMapDecorator(Collections.emptyMap()));
        assertEquals(StringUtils.EMPTY, PageUtil.getProperty(single, NameConstants.NN_TEMPLATE));

        when(single.getProperties()).thenReturn(new ValueMapDecorator(Collections.singletonMap(NameConstants.NN_TEMPLATE, TEMPLATE_PATH_1)));
        assertEquals(TEMPLATE_PATH_1, PageUtil.getProperty(single, NameConstants.NN_TEMPLATE));
    }

    /**
     * Method under test: {@link PageUtil#equals(Page, Page)}
     */
    @Test
    void equals() {
        assertFalse(PageUtil.equals(null, null));
        assertFalse(PageUtil.equals(single, null));
        assertFalse(PageUtil.equals(null, parent));

        when(single.getPath()).thenReturn(PATH);
        when(parent.getPath()).thenReturn(PATH_2);
        assertFalse(PageUtil.equals(single, parent));

        when(single.getPath()).thenReturn(PATH);
        when(parent.getPath()).thenReturn(PATH);
        assertTrue(PageUtil.equals(single, parent));
    }

    /**
     * Method under test: {@link PageUtil#isValid(Page)}
     */
    @Test
    void isValid() {
        assertFalse(PageUtil.isValid(null));

        when(single.isValid()).thenReturn(false);
        assertFalse(PageUtil.isValid(single));

        when(single.isValid()).thenReturn(true);
        assertTrue(PageUtil.isValid(single));
    }

    /**
     * Method under test: {@link PageUtil#streamDescendantsByTemplates(Page, int, String...)}
     */
    @Test
    void streamDescendantsByTemplates() {
        assertEquals(0, PageUtil.streamDescendantsByTemplates(parent, 0, (String[]) null).count());
        assertEquals(0, PageUtil.streamDescendantsByTemplates(null, 0, (String[]) null).count());
        assertEquals(0, PageUtil.streamDescendantsByTemplates(null, 0, TEMPLATE_PATH_1).count());

        when(child1.getProperties()).thenReturn(new ValueMapDecorator(Collections.singletonMap(NameConstants.NN_TEMPLATE, TEMPLATE_PATH_1)));
        when(child2.getProperties()).thenReturn(new ValueMapDecorator(Collections.singletonMap(NameConstants.NN_TEMPLATE, TEMPLATE_PATH_2)));
        when(child3.getProperties()).thenReturn(new ValueMapDecorator(Collections.singletonMap(NameConstants.NN_TEMPLATE, TEMPLATE_PATH_3)));

        final Stream<Page> pageStream1 = Stream.of(child1, child2, child3);
        final Stream<Page> pageStream2 = Stream.of(child1, child2, child3);
        final Stream<Page> pageStream3 = Stream.of(child1, child2, child3);
        final Stream<Page> pageStream4 = Stream.of(child1, child2, child3);
        final Stream<Page> pageStream5 = Stream.of(child1, child2, child3);
        final Stream<Page> pageStream6 = Stream.of(child1, child2, child3);
        final Stream<Page> pageStream7 = Stream.of(child1, child2, child3);

        try (MockedStatic<FunctionalUtil> functionalUtilMockedStatic = mockStatic(FunctionalUtil.class)) {
            functionalUtilMockedStatic.when(() -> FunctionalUtil.streamDescendants(any(Page.class), any(), anyInt())).thenReturn(pageStream1);
            assertEquals(1, PageUtil.streamDescendantsByTemplates(parent, TEMPLATE_PATH_1).count());
            functionalUtilMockedStatic.when(() -> FunctionalUtil.streamDescendants(any(Page.class), any(), anyInt())).thenReturn(pageStream2);
            assertEquals(1, PageUtil.streamDescendantsByTemplates(parent, 1, TEMPLATE_PATH_2).count());
            functionalUtilMockedStatic.when(() -> FunctionalUtil.streamDescendants(any(Page.class), any(), anyInt())).thenReturn(pageStream3);
            assertEquals(1, PageUtil.streamDescendantsByTemplates(parent, 0, TEMPLATE_PATH_3).count());
            functionalUtilMockedStatic.when(() -> FunctionalUtil.streamDescendants(any(Page.class), any(), anyInt())).thenReturn(pageStream4);
            assertEquals(2, PageUtil.streamDescendantsByTemplates(parent, 0, TEMPLATE_PATH_1, TEMPLATE_PATH_2).count());
            functionalUtilMockedStatic.when(() -> FunctionalUtil.streamDescendants(any(Page.class), any(), anyInt())).thenReturn(pageStream5);
            assertEquals(2, PageUtil.streamDescendantsByTemplates(parent, 0, TEMPLATE_PATH_2, TEMPLATE_PATH_3).count());
            functionalUtilMockedStatic.when(() -> FunctionalUtil.streamDescendants(any(Page.class), any(), anyInt())).thenReturn(pageStream6);
            assertEquals(3, PageUtil.streamDescendantsByTemplates(parent, 0, TEMPLATE_PATH_1, TEMPLATE_PATH_2, TEMPLATE_PATH_3).count());

            when(child2.getProperties()).thenReturn(new ValueMapDecorator(Collections.singletonMap(NameConstants.NN_TEMPLATE, TEMPLATE_PATH_1)));
            when(child3.getProperties()).thenReturn(new ValueMapDecorator(Collections.singletonMap(NameConstants.NN_TEMPLATE, TEMPLATE_PATH_1)));
            functionalUtilMockedStatic.when(() -> FunctionalUtil.streamDescendants(any(Page.class), any(), anyInt())).thenReturn(pageStream7);
            assertEquals(3, PageUtil.streamDescendantsByTemplates(parent, 0, TEMPLATE_PATH_1).count());
        }
    }

    /**
     * Method under test: {@link PageUtil#findClosestAncestorByTemplates(Page, String...)}
     */
    @Test
    void findClosestAncestorByTemplates() {
        assertEquals(Optional.empty(), PageUtil.findClosestAncestorByTemplates(null, TEMPLATE_PATH_1, TEMPLATE_PATH_2));
        assertEquals(Optional.empty(), PageUtil.findClosestAncestorByTemplates(child2));

        when(child1.getParent()).thenReturn(null);
        assertEquals(Optional.empty(), PageUtil.findClosestAncestorByTemplates(child1, TEMPLATE_PATH_1, TEMPLATE_PATH_2));

        when(child1.getParent()).thenReturn(parent);
        when(parent.getProperties()).thenReturn(new ValueMapDecorator(Collections.singletonMap(NameConstants.NN_TEMPLATE, TEMPLATE_PATH_3)));
        assertEquals(Optional.empty(), PageUtil.findClosestAncestorByTemplates(child1, TEMPLATE_PATH_1, TEMPLATE_PATH_2));

        when(child1.getParent()).thenReturn(parent);
        when(parent.getProperties()).thenReturn(new ValueMapDecorator(Collections.singletonMap(NameConstants.NN_TEMPLATE, TEMPLATE_PATH_1)));
        assertEquals(Optional.of(parent), PageUtil.findClosestAncestorByTemplates(child1, TEMPLATE_PATH_1, TEMPLATE_PATH_2));

        when(child2.getParent()).thenReturn(child1);
        when(child1.getParent()).thenReturn(parent);
        when(child1.getProperties()).thenReturn(new ValueMapDecorator(Collections.singletonMap(NameConstants.NN_TEMPLATE, TEMPLATE_PATH_3)));
        when(parent.getProperties()).thenReturn(new ValueMapDecorator(Collections.singletonMap(NameConstants.NN_TEMPLATE, TEMPLATE_PATH_2)));
        assertEquals(Optional.of(parent), PageUtil.findClosestAncestorByTemplates(child2, TEMPLATE_PATH_1, TEMPLATE_PATH_2));
    }

}
