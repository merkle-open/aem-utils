package com.merkle.oss.aem.utils.wcm;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.constants.NameConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link PageUtil} class.
 */
@ExtendWith(MockitoExtension.class)
class PageUtilTest {

    private static final int MAX_DEPTH_ROOT = 0;
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
     * Method under test: {@link PageUtil#findClosestAncestorByTemplate(Page, String...)}
     */
    @Test
    void findClosestAncestorByTemplate() {
        assertEquals(Optional.empty(), PageUtil.findClosestAncestorByTemplate(null, TEMPLATE_PATH_1, TEMPLATE_PATH_2));
        assertEquals(Optional.empty(), PageUtil.findClosestAncestorByTemplate(child2));

        when(child1.getParent()).thenReturn(null);
        assertEquals(Optional.empty(), PageUtil.findClosestAncestorByTemplate(child1, TEMPLATE_PATH_1, TEMPLATE_PATH_2));

        when(child1.getParent()).thenReturn(parent);
        when(parent.getProperties()).thenReturn(new ValueMapDecorator(Collections.singletonMap(NameConstants.NN_TEMPLATE, TEMPLATE_PATH_3)));
        assertEquals(Optional.empty(), PageUtil.findClosestAncestorByTemplate(child1, TEMPLATE_PATH_1, TEMPLATE_PATH_2));

        when(child1.getParent()).thenReturn(parent);
        when(parent.getProperties()).thenReturn(new ValueMapDecorator(Collections.singletonMap(NameConstants.NN_TEMPLATE, TEMPLATE_PATH_1)));
        assertEquals(Optional.of(parent), PageUtil.findClosestAncestorByTemplate(child1, TEMPLATE_PATH_1, TEMPLATE_PATH_2));

        when(child2.getParent()).thenReturn(child1);
        when(child1.getParent()).thenReturn(parent);
        when(child1.getProperties()).thenReturn(new ValueMapDecorator(Collections.singletonMap(NameConstants.NN_TEMPLATE, TEMPLATE_PATH_3)));
        when(parent.getProperties()).thenReturn(new ValueMapDecorator(Collections.singletonMap(NameConstants.NN_TEMPLATE, TEMPLATE_PATH_2)));
        assertEquals(Optional.of(parent), PageUtil.findClosestAncestorByTemplate(child2, TEMPLATE_PATH_1, TEMPLATE_PATH_2));
    }

    /**
     * Method under test: {@link PageUtil#firstChildByTemplate(Page, String...)}
     */
    @Test
    void firstChildByTemplate() {
        assertEquals(Optional.empty(), PageUtil.firstChildByTemplate(null, TEMPLATE_PATH_1, TEMPLATE_PATH_2));
        assertEquals(Optional.empty(), PageUtil.firstChildByTemplate(child1));

        when(parent.listChildren(ArgumentMatchers.any())).thenReturn(Collections.singletonList(child1).iterator());
        when(child1.isValid()).thenReturn(true);
        assertEquals(Optional.of(child1), PageUtil.firstChildByTemplate(parent, TEMPLATE_PATH_1, TEMPLATE_PATH_2));

        when(parent.listChildren(ArgumentMatchers.any())).thenReturn(Arrays.asList(child1, child2).iterator());
        when(child1.isValid()).thenReturn(false);
        when(child2.isValid()).thenReturn(true);
        assertEquals(Optional.of(child2), PageUtil.firstChildByTemplate(parent, TEMPLATE_PATH_1, TEMPLATE_PATH_2));

        when(parent.listChildren(ArgumentMatchers.any())).thenReturn(Arrays.asList(child1, child2).iterator());
        when(child1.isValid()).thenReturn(true);
        assertEquals(Optional.of(child1), PageUtil.firstChildByTemplate(parent, TEMPLATE_PATH_1, TEMPLATE_PATH_2));
    }

    /**
     * Method under test: {@link PageUtil#childrenByTemplate(Page, String...)}
     */
    @Test
    void childrenByTemplate() {
        assertEquals(Collections.emptyList(), PageUtil.childrenByTemplate(null, TEMPLATE_PATH_3));
        assertEquals(Collections.emptyList(), PageUtil.childrenByTemplate(child1));

        when(parent.listChildren(ArgumentMatchers.any())).thenReturn(Arrays.asList(child1, child2, child3).iterator());
        when(child1.isValid()).thenReturn(true);
        when(child2.isValid()).thenReturn(true);
        when(child3.isValid()).thenReturn(true);
        assertEquals(Arrays.asList(child1, child2, child3), PageUtil.childrenByTemplate(parent, TEMPLATE_PATH_3));

        when(parent.listChildren(ArgumentMatchers.any())).thenReturn(Arrays.asList(child1, child2).iterator());
        when(child1.isValid()).thenReturn(false);
        assertEquals(Collections.singletonList(child2), PageUtil.childrenByTemplate(parent, TEMPLATE_PATH_3));
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
     * Method under test: {@link PageUtil#streamDescendants(Page, int)}
     */
    @Test
    void streamDescendants_preconditionNotMet() {
        assertEquals(0, PageUtil.streamDescendants((null), 1).count());

        when(parent.listChildren(ArgumentMatchers.any())).thenReturn(Collections.singletonList(child1).iterator());
        when(child1.getDepth()).thenReturn(2);
        assertEquals(0, PageUtil.streamDescendants((parent), 1).count());
    }

    /**
     * Method under test: {@link PageUtil#streamDescendants(Page, int)}
     */
    @Test
    void streamDescendants_emptyStreamIfNoChildren() {
        when(single.listChildren(ArgumentMatchers.any(PageFilter.class))).thenReturn(Collections.emptyIterator());
        assertEquals(0, PageUtil.streamDescendants(single, MAX_DEPTH_ROOT).count());

        doAnswer(invocationOnMock -> Collections.singletonList(child2).iterator()).when(child1).listChildren(ArgumentMatchers.any());
        doAnswer(invocationOnMock -> Collections.singletonList(child3).iterator()).when(child2).listChildren(ArgumentMatchers.any());
        doAnswer(invocationOnMock -> Collections.emptyIterator()).when(child3).listChildren(ArgumentMatchers.any());
        doAnswer(invocationOnMock -> Collections.singletonList(child1).iterator()).when(parent).listChildren(ArgumentMatchers.any());
        when(child1.getDepth()).thenReturn(2);
        when(child2.getDepth()).thenReturn(3);
        when(child3.getDepth()).thenReturn(4);

        assertIterableEquals(Stream.of(child1, child2, child3).collect(Collectors.toList()), PageUtil.streamDescendants(parent, MAX_DEPTH_ROOT).collect(Collectors.toList()));
        assertIterableEquals(Stream.of(child1, child2).collect(Collectors.toList()), PageUtil.streamDescendants(parent, 3).collect(Collectors.toList()));
    }

    /**
     * Method under test: {@link PageUtil#streamTree(Page, int)}
     */
    @Test
    void streamTree() {
        assertEquals(0, PageUtil.streamTree((null), 1).count());

        doAnswer(invocationOnMock -> Collections.singletonList(child2).iterator()).when(child1).listChildren(ArgumentMatchers.any());
        doAnswer(invocationOnMock -> Collections.singletonList(child3).iterator()).when(child2).listChildren(ArgumentMatchers.any());
        doAnswer(invocationOnMock -> Collections.emptyIterator()).when(child3).listChildren(ArgumentMatchers.any());
        doAnswer(invocationOnMock -> Collections.singletonList(child1).iterator()).when(parent).listChildren(ArgumentMatchers.any());
        when(child1.getDepth()).thenReturn(2);
        when(child2.getDepth()).thenReturn(3);
        when(child3.getDepth()).thenReturn(4);

        assertIterableEquals(Stream.of(parent, child1, child2, child3).collect(Collectors.toList()), PageUtil.streamTree(parent, MAX_DEPTH_ROOT).collect(Collectors.toList()));
        assertIterableEquals(Stream.of(parent, child1, child2).collect(Collectors.toList()), PageUtil.streamTree(parent, 3).collect(Collectors.toList()));
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

}
