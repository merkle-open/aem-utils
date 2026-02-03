package com.merkle.oss.aem.utils.constants;

import com.day.cq.wcm.api.Page;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link CookieNameBuilder} class.
 */
@ExtendWith(MockitoExtension.class)
class CookieNameBuilderTest {

    private static final String NAMESPACE = "projectName";

    private static final String COMPONENT = "componentName";

    private static final String SEGMENT_NAME_A = "segmentA";

    private static final String SEGMENT_NAME_B = "segmentB";

    @Mock
    private Page pageDepthOne;

    @Mock
    private Page pageDepthTwo;

    @Mock
    private Page pageDepthThree;

    @Mock
    private Page pageDepthFour;

    @Mock
    private Page page;

    @Test
    void buildCookieNames() {
        assertEquals("projectName.segmentA", new CookieNameBuilder(NAMESPACE, SEGMENT_NAME_A).toString());
        assertEquals("projectName.segmentA.segmentB", new CookieNameBuilder(NAMESPACE, SEGMENT_NAME_A, SEGMENT_NAME_B).toString());
        assertEquals("projectName.segmentA.segmentB", new CookieNameBuilder(NAMESPACE, SEGMENT_NAME_A).appendSegmentName(SEGMENT_NAME_B).toString());
        assertEquals("projectName.segmentA", new CookieNameBuilder(NAMESPACE, SEGMENT_NAME_A).appendSegmentName(null).toString());
        final String[] nullArray = null;
        assertThrows(NullPointerException.class, () -> new CookieNameBuilder(NAMESPACE, nullArray));
    }

    @Test
    void appendPathSegmentNames() {
        when(page.getParent()).thenReturn(pageDepthFour);
        when(pageDepthFour.getParent()).thenReturn(pageDepthThree);
        when(pageDepthThree.getParent()).thenReturn(pageDepthTwo);
        when(pageDepthTwo.getParent()).thenReturn(pageDepthOne);
        when(page.getDepth()).thenReturn(5);
        when(pageDepthFour.getDepth()).thenReturn(4);
        when(pageDepthThree.getDepth()).thenReturn(3);
        when(pageDepthTwo.getDepth()).thenReturn(2);
        when(pageDepthFour.getName()).thenReturn("pageDepth4");
        when(pageDepthThree.getName()).thenReturn("pageDepth3");
        when(pageDepthTwo.getName()).thenReturn("pageDepth2");

        assertEquals("projectName.componentName.pageDepth2.pageDepth3", new CookieNameBuilder(NAMESPACE, COMPONENT).appendPathSegmentNames(page, 2, 3).toString());
        assertEquals("projectName.componentName.pageDepth2.pageDepth3", new CookieNameBuilder(NAMESPACE, COMPONENT).appendPathSegmentNames(page, 3, 2).toString());
        assertEquals("projectName.componentName.pageDepth3", new CookieNameBuilder(NAMESPACE, COMPONENT).appendPathSegmentNames(page, 1, 3).toString());
        assertEquals("projectName.componentName.pageDepth3", new CookieNameBuilder(NAMESPACE, COMPONENT).appendPathSegmentNames(page, 3, 1).toString());
        assertEquals("projectName.componentName.pageDepth2.pageDepth3.pageDepth4", new CookieNameBuilder(NAMESPACE, COMPONENT).appendPathSegmentNames(page, 4, 3, 2).toString());
        assertEquals("projectName.componentName.pageDepth2.pageDepth3.pageDepth4", new CookieNameBuilder(NAMESPACE, COMPONENT).appendPathSegmentNames(page, 3, 2, 4).toString());
        assertEquals("projectName.componentName.pageDepth2.pageDepth3.pageDepth4", new CookieNameBuilder(NAMESPACE, COMPONENT).appendPathSegmentNames(page, 2, 3, 4).toString());
        assertEquals("projectName.componentName.pageDepth2.pageDepth3.pageDepth4", new CookieNameBuilder(NAMESPACE, COMPONENT).appendPathSegmentNames(page, 2, 4, 3).toString());
        assertEquals("projectName.componentName.pageDepth2.pageDepth4", new CookieNameBuilder(NAMESPACE, COMPONENT).appendPathSegmentNames(page, 2, 4).toString());
        assertEquals("projectName.componentName.pageDepth2.pageDepth4", new CookieNameBuilder(NAMESPACE, COMPONENT).appendPathSegmentNames(page, 4, 2).toString());
        assertEquals("projectName.componentName.pageDepth3", new CookieNameBuilder(NAMESPACE, COMPONENT).appendPathSegmentNames(page, 3).toString());
        assertEquals("projectName.componentName.pageDepth2", new CookieNameBuilder(NAMESPACE, COMPONENT).appendPathSegmentNames(page, 2).toString());
        assertEquals("projectName.componentName", new CookieNameBuilder(NAMESPACE, COMPONENT).appendPathSegmentNames(page).toString());

        when(pageDepthTwo.getParent()).thenReturn(null);

        assertEquals("projectName.componentName", new CookieNameBuilder(NAMESPACE, COMPONENT).appendPathSegmentNames(page, 1).toString());
        assertEquals("projectName.componentName", new CookieNameBuilder(NAMESPACE, COMPONENT).appendPathSegmentNames(null, 1).toString());

    }

}
