package com.merkle.oss.aem.utils.sling;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link RequestUtil} class.
 */
@ExtendWith(AemContextExtension.class)
class RequestUtilTest {

    private static final String SELECTOR_1 = "selector1";

    private static final String SELECTOR_2 = "selector2";

    private static final String SELECTOR_3 = "selector3";

    private static final String SUFFIX_SEGMENT_1 = "suffixSegment1";

    private static final String SUFFIX_SEGMENT_2 = "suffixSegment2";

    private static final String SUFFIX = "/" + SUFFIX_SEGMENT_1 + "/" + SUFFIX_SEGMENT_2;

    private static final String QUERY_PARAM_NAME_1 = "queryParamName1";

    private static final String QUERY_PARAM_NAME_2 = "queryParamName2";

    private static final String QUERY_PARAM_NAME_3 = "queryParamName3";

    private static final String QUERY_PARAM_VALUE_1 = "queryParamValue1";

    private static final String QUERY_PARAM_VALUE_2 = "queryParamValue2";

    private static final String QUERY_PARAM_VALUE_3 = "queryParamValue3";

    private static final String QUERY_PARAM_VALUE_INT = "3";

    private static final String QUERY_PARAM_VALUE_LONG = "4";

    private static final String QUERY_PARAM_VALUE_FLOAT = "5.5";

    private static final String QUERY_PARAM_VALUE_BOOLEAN = "true";

    private static final int VALUE_INT = 3;

    private static final long VALUE_LONG = 4L;

    private static final float VALUE_FLOAT = 5.5f;

    private static final String DEFAULT_VALUE = "default";


    /**
     * Method under test: {@link RequestUtil#hasSelector(SlingHttpServletRequest, String)}
     */
    @Test
    void hasSelector(final AemContext context) {
        final MockSlingHttpServletRequest request = context.request();
        assertThrows(NullPointerException.class, () -> RequestUtil.hasSelector(null, SELECTOR_1));
        assertThrows(NullPointerException.class, () -> RequestUtil.hasSelector(request, null));
        assertFalse(RequestUtil.hasSelector(request, SELECTOR_1));

        final MockRequestPathInfo mockRequestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        mockRequestPathInfo.setSelectorString(SELECTOR_2);
        assertFalse(RequestUtil.hasSelector(request, SELECTOR_1));

        mockRequestPathInfo.setSelectorString(SELECTOR_1);
        assertTrue(RequestUtil.hasSelector(request, SELECTOR_1));
    }

    /**
     * Method under test: {@link RequestUtil#getSelectors(SlingHttpServletRequest)}
     */
    @Test
    void getSelectors(final AemContext context) {
        final MockSlingHttpServletRequest request = context.request();
        assertTrue(RequestUtil.getSelectors(request).isEmpty());

        final MockRequestPathInfo mockRequestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        mockRequestPathInfo.setSelectorString(SELECTOR_1);
        assertEquals(List.of(SELECTOR_1), RequestUtil.getSelectors(request));

        mockRequestPathInfo.setSelectorString(SELECTOR_1 + "." + SELECTOR_2);
        assertEquals(List.of(SELECTOR_1, SELECTOR_2), RequestUtil.getSelectors(request));

        mockRequestPathInfo.setSelectorString(SELECTOR_1 + "." + SELECTOR_2 + "." + SELECTOR_3);
        assertEquals(List.of(SELECTOR_1, SELECTOR_2, SELECTOR_3), RequestUtil.getSelectors(request));
        assertNotEquals(List.of(SELECTOR_1, SELECTOR_3, SELECTOR_2), RequestUtil.getSelectors(request));
    }

    /**
     * Methods under test:
     * <ul>
     *     <li>{@link RequestUtil#getSelector(SlingHttpServletRequest, int)}</li>
     *     <li>{@link RequestUtil#getSelector(SlingHttpServletRequest, int, String)}</li>
     * </ul>
     */
    @Test
    void getSelector(final AemContext context) {
        final MockSlingHttpServletRequest request = context.request();
        assertEquals(StringUtils.EMPTY, RequestUtil.getSelector(request, 0));

        final MockRequestPathInfo mockRequestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        mockRequestPathInfo.setSelectorString(SELECTOR_1 + "." + SELECTOR_2 + "." + SELECTOR_3);
        assertEquals(StringUtils.EMPTY, RequestUtil.getSelector(request, 3));
        assertEquals(DEFAULT_VALUE, RequestUtil.getSelector(request, 3, DEFAULT_VALUE));
        assertEquals(SELECTOR_1, RequestUtil.getSelector(request, 0));
        assertEquals(SELECTOR_2, RequestUtil.getSelector(request, 1));
        assertEquals(SELECTOR_3, RequestUtil.getSelector(request, 2));
    }

    /**
     * Methods under test:
     * <ul>
     *     <li>{@link RequestUtil#getFirstSelector(SlingHttpServletRequest)}</li>
     *     <li>{@link RequestUtil#getFirstSelector(SlingHttpServletRequest, String)}</li>
     * </ul>
     */
    @Test
    void getFirstSelector(final AemContext context) {
        final MockSlingHttpServletRequest request = context.request();
        assertEquals(StringUtils.EMPTY, RequestUtil.getFirstSelector(request));
        assertEquals(DEFAULT_VALUE, RequestUtil.getFirstSelector(request, DEFAULT_VALUE));

        final MockRequestPathInfo mockRequestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        mockRequestPathInfo.setSelectorString(SELECTOR_1 + "." + SELECTOR_2 + "." + SELECTOR_3);
        assertEquals(SELECTOR_1, RequestUtil.getFirstSelector(request));
        assertEquals(SELECTOR_1, RequestUtil.getFirstSelector(request, DEFAULT_VALUE));
    }

    /**
     * Method under test: {@link RequestUtil#hasSuffix(SlingHttpServletRequest)}
     */
    @Test
    void hasSuffix(final AemContext context) {
        final MockSlingHttpServletRequest request = context.request();
        assertThrows(NullPointerException.class, () -> RequestUtil.hasSuffix(null));
        assertFalse(RequestUtil.hasSuffix(request));

        final MockRequestPathInfo mockRequestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        mockRequestPathInfo.setSuffix(SUFFIX);
        assertTrue(RequestUtil.hasSuffix(request));
    }

    /**
     * Methods under test:
     * <ul>
     *     <li>{@link RequestUtil#getSuffix(SlingHttpServletRequest)}</li>
     *     <li>{@link RequestUtil#getSuffix(SlingHttpServletRequest, String)}</li>
     * </ul>
     */
    @Test
    void getSuffix(final AemContext context) {
        final MockSlingHttpServletRequest request = context.request();
        assertEquals(StringUtils.EMPTY, RequestUtil.getSuffix(request));
        assertEquals(DEFAULT_VALUE, RequestUtil.getSuffix(request, DEFAULT_VALUE));

        final MockRequestPathInfo mockRequestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        mockRequestPathInfo.setSuffix(SUFFIX);
        assertEquals(SUFFIX, RequestUtil.getSuffix(request));
        assertEquals(SUFFIX, RequestUtil.getSuffix(request, DEFAULT_VALUE));
    }

    /**
     * Method under test: {@link RequestUtil#getSuffixSegments(SlingHttpServletRequest)}
     */
    @Test
    void getSuffixSegments(final AemContext context) {
        final MockSlingHttpServletRequest request = context.request();
        assertTrue(RequestUtil.getSuffixSegments(request).isEmpty());

        final MockRequestPathInfo mockRequestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        mockRequestPathInfo.setSuffix(SUFFIX);
        assertEquals(List.of(SUFFIX_SEGMENT_1, SUFFIX_SEGMENT_2), RequestUtil.getSuffixSegments(request));
    }

    /**
     * Methods under test:
     * <ul>
     *     <li>{@link RequestUtil#getSuffixSegment(SlingHttpServletRequest, int)}</li>
     *     <li>{@link RequestUtil#getSuffixSegment(SlingHttpServletRequest, int, String)}</li>
     * </ul>
     */
    @Test
    void getSuffixSegment(final AemContext context) {
        final MockSlingHttpServletRequest request = context.request();
        assertEquals(StringUtils.EMPTY, RequestUtil.getSuffixSegment(request, 1));
        assertEquals(DEFAULT_VALUE, RequestUtil.getSuffixSegment(request, 1, DEFAULT_VALUE));

        final MockRequestPathInfo mockRequestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        mockRequestPathInfo.setSuffix(SUFFIX);
        assertEquals(StringUtils.EMPTY, RequestUtil.getSuffixSegment(request, 2));
        assertEquals(DEFAULT_VALUE, RequestUtil.getSuffixSegment(request, 2, DEFAULT_VALUE));
        assertEquals(SUFFIX_SEGMENT_2, RequestUtil.getSuffixSegment(request, 1));
        assertEquals(SUFFIX_SEGMENT_2, RequestUtil.getSuffixSegment(request, 1, DEFAULT_VALUE));
    }

    /**
     * Methods under test:
     * <ul>
     *     <li>{@link RequestUtil#getFirstSuffixSegment(SlingHttpServletRequest)}</li>
     *     <li>{@link RequestUtil#getFirstSuffixSegment(SlingHttpServletRequest, String)}</li>
     * </ul>
     */
    @Test
    void getFirstSuffixSegment(final AemContext context) {
        final MockSlingHttpServletRequest request = context.request();
        assertEquals(StringUtils.EMPTY, RequestUtil.getFirstSuffixSegment(request));
        assertEquals(DEFAULT_VALUE, RequestUtil.getFirstSuffixSegment(request, DEFAULT_VALUE));

        final MockRequestPathInfo mockRequestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        mockRequestPathInfo.setSuffix(SUFFIX);
        assertEquals(SUFFIX_SEGMENT_1, RequestUtil.getFirstSuffixSegment(request));
        assertEquals(SUFFIX_SEGMENT_1, RequestUtil.getFirstSuffixSegment(request, DEFAULT_VALUE));
    }

    /**
     * Method under test: {@link RequestUtil#hasParameter(SlingHttpServletRequest, String)}
     */
    @Test
    void hasParameter(final AemContext context) {
        final MockSlingHttpServletRequest request = context.request();
        assertFalse(RequestUtil.hasParameter(request, QUERY_PARAM_NAME_1));

        request.setQueryString(QUERY_PARAM_NAME_1 + "=" + QUERY_PARAM_VALUE_1);
        assertTrue(RequestUtil.hasParameter(request, QUERY_PARAM_NAME_1));
    }

    /**
     * Method under test: {@link RequestUtil#getParameterList(SlingHttpServletRequest, String)}
     */
    @Test
    void getParameterList(final AemContext context) {
        final MockSlingHttpServletRequest request = context.request();
        assertTrue(RequestUtil.getParameterList(request, QUERY_PARAM_NAME_1).isEmpty());

        request.setQueryString(QUERY_PARAM_NAME_1 + "&" + QUERY_PARAM_NAME_3 + "=" + QUERY_PARAM_VALUE_3);
        assertTrue(RequestUtil.getParameterList(request, QUERY_PARAM_NAME_1).isEmpty());

        request.setQueryString(QUERY_PARAM_NAME_1 + "=" + QUERY_PARAM_VALUE_1 + "&" + QUERY_PARAM_NAME_1 + "=" + QUERY_PARAM_VALUE_2 + "&" + QUERY_PARAM_NAME_3 + "=" + QUERY_PARAM_VALUE_3);
        assertEquals(List.of(QUERY_PARAM_VALUE_1, QUERY_PARAM_VALUE_2), RequestUtil.getParameterList(request, QUERY_PARAM_NAME_1));
    }

    /**
     * Methods under test:
     * <ul>
     *     <li>{@link RequestUtil#getParameter(SlingHttpServletRequest, String)}</li>
     *     <li>{@link RequestUtil#getParameter(SlingHttpServletRequest, String, String)}</li>
     * </ul>
     */
    @Test
    void getParameter(final AemContext context) {
        final MockSlingHttpServletRequest request = context.request();
        assertEquals(StringUtils.EMPTY, RequestUtil.getParameter(request, QUERY_PARAM_NAME_1));
        assertEquals(DEFAULT_VALUE, RequestUtil.getParameter(request, QUERY_PARAM_NAME_1, DEFAULT_VALUE));

        request.setQueryString(QUERY_PARAM_NAME_1 + "=" + QUERY_PARAM_VALUE_1 + "&" + QUERY_PARAM_NAME_2 + "=" + QUERY_PARAM_VALUE_2 + "&" + QUERY_PARAM_NAME_3 + "=" + QUERY_PARAM_VALUE_3);
        assertEquals(QUERY_PARAM_VALUE_1, RequestUtil.getParameter(request, QUERY_PARAM_NAME_1));
        assertEquals(QUERY_PARAM_VALUE_1, RequestUtil.getParameter(request, QUERY_PARAM_NAME_1, DEFAULT_VALUE));
    }

    /**
     * Methods under test:
     * <ul>
     *     <li>{@link RequestUtil#getParameterAsInt(SlingHttpServletRequest, String)}</li>
     *     <li>{@link RequestUtil#getParameterAsInt(SlingHttpServletRequest, String, int)}</li>
     * </ul>
     */
    @Test
    void getParameterAsInt(final AemContext context) {
        final MockSlingHttpServletRequest request = context.request();
        assertEquals(0, RequestUtil.getParameterAsInt(request, QUERY_PARAM_NAME_1));
        assertEquals(6, RequestUtil.getParameterAsInt(request, QUERY_PARAM_NAME_1, 6));

        request.setQueryString(QUERY_PARAM_NAME_1 + "=" + QUERY_PARAM_VALUE_1);
        assertEquals(0, RequestUtil.getParameterAsInt(request, QUERY_PARAM_NAME_1));

        request.setQueryString(QUERY_PARAM_NAME_1 + "=" + QUERY_PARAM_VALUE_INT + "&" + QUERY_PARAM_NAME_2 + "=" + QUERY_PARAM_VALUE_2 + "&" + QUERY_PARAM_NAME_3 + "=" + QUERY_PARAM_VALUE_3);
        assertEquals(VALUE_INT, RequestUtil.getParameterAsInt(request, QUERY_PARAM_NAME_1));
        assertEquals(VALUE_INT, RequestUtil.getParameterAsInt(request, QUERY_PARAM_NAME_1, 6));
    }

    /**
     * Methods under test:
     * <ul>
     *     <li>{@link RequestUtil#getParameterAsLong(SlingHttpServletRequest, String)}</li>
     *     <li>{@link RequestUtil#getParameterAsLong(SlingHttpServletRequest, String, long)}</li>
     * </ul>
     */
    @Test
    void getParameterAsLong(final AemContext context) {
        final MockSlingHttpServletRequest request = context.request();
        assertEquals(0L, RequestUtil.getParameterAsLong(request, QUERY_PARAM_NAME_1));
        assertEquals(6L, RequestUtil.getParameterAsLong(request, QUERY_PARAM_NAME_1, 6L));

        request.setQueryString(QUERY_PARAM_NAME_1 + "=" + QUERY_PARAM_VALUE_1);
        assertEquals(0L, RequestUtil.getParameterAsLong(request, QUERY_PARAM_NAME_1));

        request.setQueryString(QUERY_PARAM_NAME_1 + "=" + QUERY_PARAM_VALUE_LONG + "&" + QUERY_PARAM_NAME_2 + "=" + QUERY_PARAM_VALUE_2 + "&" + QUERY_PARAM_NAME_3 + "=" + QUERY_PARAM_VALUE_3);
        assertEquals(VALUE_LONG, RequestUtil.getParameterAsLong(request, QUERY_PARAM_NAME_1));
        assertEquals(VALUE_LONG, RequestUtil.getParameterAsLong(request, QUERY_PARAM_NAME_1, 6L));
    }

    /**
     * Methods under test:
     * <ul>
     *     <li>{@link RequestUtil#getParameterAsFloat(SlingHttpServletRequest, String)}</li>
     *     <li>{@link RequestUtil#getParameterAsFloat(SlingHttpServletRequest, String, float)}</li>
     * </ul>
     */
    @Test
    void getParameterAsFloat(final AemContext context) {
        final MockSlingHttpServletRequest request = context.request();
        assertEquals(0.0f, RequestUtil.getParameterAsFloat(request, QUERY_PARAM_NAME_1));
        assertEquals(6.6f, RequestUtil.getParameterAsFloat(request, QUERY_PARAM_NAME_1, 6.6f));

        request.setQueryString(QUERY_PARAM_NAME_1 + "=" + QUERY_PARAM_VALUE_1);
        assertEquals(0.0f, RequestUtil.getParameterAsFloat(request, QUERY_PARAM_NAME_1));

        request.setQueryString(QUERY_PARAM_NAME_1 + "=" + QUERY_PARAM_VALUE_FLOAT + "&" + QUERY_PARAM_NAME_2 + "=" + QUERY_PARAM_VALUE_2 + "&" + QUERY_PARAM_NAME_3 + "=" + QUERY_PARAM_VALUE_3);
        assertEquals(VALUE_FLOAT, RequestUtil.getParameterAsFloat(request, QUERY_PARAM_NAME_1));
        assertEquals(VALUE_FLOAT, RequestUtil.getParameterAsFloat(request, QUERY_PARAM_NAME_1, 6.6f));
    }

    /**
     * Methods under test:
     * <ul>
     *     <li>{@link RequestUtil#getParameterAsBoolean(SlingHttpServletRequest, String)}</li>
     *     <li>{@link RequestUtil#getParameterAsBoolean(SlingHttpServletRequest, String, boolean)}</li>
     * </ul>
     */
    @Test
    void getParameterAsBoolean(final AemContext context) {
        final MockSlingHttpServletRequest request = context.request();
        assertFalse(RequestUtil.getParameterAsBoolean(request, QUERY_PARAM_NAME_1));
        assertTrue(RequestUtil.getParameterAsBoolean(request, QUERY_PARAM_NAME_1, true));

        request.setQueryString(QUERY_PARAM_NAME_1 + "=" + QUERY_PARAM_VALUE_1);
        assertFalse(RequestUtil.getParameterAsBoolean(request, QUERY_PARAM_NAME_1));

        request.setQueryString(QUERY_PARAM_NAME_1 + "=" + QUERY_PARAM_VALUE_BOOLEAN + "&" + QUERY_PARAM_NAME_2 + "=" + QUERY_PARAM_VALUE_2 + "&" + QUERY_PARAM_NAME_3 + "=" + QUERY_PARAM_VALUE_3);
        assertTrue(RequestUtil.getParameterAsBoolean(request, QUERY_PARAM_NAME_1));
        assertTrue(RequestUtil.getParameterAsBoolean(request, QUERY_PARAM_NAME_1, false));
    }

}
