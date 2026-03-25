package com.merkle.oss.aem.utils.sling;

import com.merkle.oss.aem.utils.annotations.tooling.Generated;
import com.merkle.oss.aem.utils.java.ClassUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.sling.api.SlingHttpServletRequest;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Utility class for common operations related to {@link org.apache.sling.api.SlingHttpServletRequest}.
 * <p>
 * Provides convenient access to request selectors, suffixes, and parameters with
 * built-in null safety and default value handling.
 */
public final class RequestUtil {

    @Generated("Bypass coverage for static utility constructor")
    private RequestUtil() {
        ClassUtil.assertNoInstance(this.getClass());
    }

    /**
     * Checks if the request contains a specific selector (case-insensitive).
     *
     * @param request       the current Sling request
     * @param selectorValue the selector to search for
     * @return {@code true} if the selector is present, {@code false} otherwise
     */
    public static boolean hasSelector(@NonNull final SlingHttpServletRequest request, @NonNull final String selectorValue) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(selectorValue);

        final List<String> selectors = getSelectors(request);
        for (final String givenSelector : selectors) {
            if (Strings.CI.equals(givenSelector, selectorValue)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a list of all selectors from the request.
     *
     * @param request the current Sling request
     * @return a non-null list of selectors (empty list if no selectors are present)
     */
    public static @NonNull List<String> getSelectors(@NonNull final SlingHttpServletRequest request) {
        Objects.requireNonNull(request);

        return new ArrayList<>(Arrays.asList(request.getRequestPathInfo().getSelectors()));
    }

    /**
     * Gets the selector at the specified index. Returns an empty string {@code ""} if the index is out of bounds.
     *
     * @param request the current Sling request
     * @param index   the zero-based index of the selector
     * @return the selector value or an empty string
     */
    public static @NonNull String getSelector(@NonNull final SlingHttpServletRequest request, final int index) {
        return getSelector(request, index, StringUtils.EMPTY);
    }

    /**
     * Gets the selector at the specified index. Returns the provided default value if the index is out of bounds.
     *
     * @param request      the current Sling request
     * @param index        the zero-based index of the selector
     * @param defaultValue the value to return if the index is invalid
     * @return the selector value or the default value
     */
    public static @NonNull String getSelector(@NonNull final SlingHttpServletRequest request, final int index, @NonNull final String defaultValue) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(defaultValue);

        final List<String> selectors = getSelectors(request);
        if (selectors.isEmpty() || index >= selectors.size()) {
            return defaultValue;
        }

        return selectors.get(index);
    }

    /**
     * Gets the first selector of the request. Defaults to empty string {@code ""} if missing or invalid.
     *
     * @param request the current Sling request
     * @return the first selector or an empty string
     */
    public static @NonNull String getFirstSelector(@NonNull final SlingHttpServletRequest request) {
        return getSelector(request, 0, StringUtils.EMPTY);
    }

    /**
     * Gets the first selector of the request or a default value.
     *
     * @param request      the current Sling request
     * @param defaultValue the value to return if no selectors are present
     * @return the first selector or the default value
     */
    public static @NonNull String getFirstSelector(@NonNull final SlingHttpServletRequest request, @NonNull final String defaultValue) {
        return getSelector(request, 0, defaultValue);
    }

    /**
     * Checks if the request contains a suffix.
     *
     * @param request the current Sling request
     * @return {@code true} if a suffix exists, {@code false} otherwise
     */
    public static boolean hasSuffix(@NonNull final SlingHttpServletRequest request) {
        Objects.requireNonNull(request);

        return request.getRequestPathInfo().getSuffix() != null;
    }

    /**
     * Gets the suffix of the request. Defaults to empty string {@code ""} if missing or invalid.
     *
     * @param request the current Sling request
     * @return the suffix or an empty string if no suffix exists
     */
    public static @NonNull String getSuffix(@NonNull final SlingHttpServletRequest request) {
        return getSuffix(request, StringUtils.EMPTY);
    }

    /**
     * Gets the suffix of the request.
     *
     * @param request      the current Sling request
     * @param defaultValue the value to return if no suffix is available
     * @return the suffix or a default value if no suffix exists
     */
    public static @NonNull String getSuffix(@NonNull final SlingHttpServletRequest request, @NonNull final String defaultValue) {
        if (hasSuffix(request)) {
            return Objects.requireNonNull(request.getRequestPathInfo().getSuffix());
        }

        return defaultValue;
    }

    /**
     * Splits the suffix into segments using '/' as a delimiter.
     *
     * @param request the current Sling request
     * @return a non-null list of suffix segments
     */
    public static @NonNull List<String> getSuffixSegments(@NonNull final SlingHttpServletRequest request) {
        final String suffix = getSuffix(request);
        if (StringUtils.isEmpty(suffix)) {
            return Collections.emptyList();
        }

        return Arrays.asList(StringUtils.split(suffix, '/'));
    }

    /**
     * Gets a specific segment of the suffix by index. Defaults to empty string {@code ""} if missing or invalid.
     *
     * @param request the current Sling request
     * @param index   the zero-based index of the segment
     * @return the segment value or an empty string
     */
    public static @NonNull String getSuffixSegment(@NonNull final SlingHttpServletRequest request, int index) {
        return getSuffixSegment(request, index, StringUtils.EMPTY);
    }

    /**
     * Gets a specific segment of the suffix by index or a default value.
     *
     * @param request      the current Sling request
     * @param index        the zero-based index of the segment
     * @param defaultValue the value to return if the index is out of bounds
     * @return the segment value or the default value
     */
    public static @NonNull String getSuffixSegment(@NonNull final SlingHttpServletRequest request, int index, @NonNull final String defaultValue) {
        Objects.requireNonNull(defaultValue);

        final List<String> suffixSegments = getSuffixSegments(request);
        if (suffixSegments.isEmpty() || index >= suffixSegments.size()) {
            return defaultValue;
        }

        return suffixSegments.get(index);
    }

    /**
     * Gets the first segment of the suffix. Defaults to empty string {@code ""} if missing or invalid.
     *
     * @param request the current Sling request
     * @return the first segment or an empty string
     */
    public static @NonNull String getFirstSuffixSegment(@NonNull final SlingHttpServletRequest request) {
        return getSuffixSegment(request, 0, StringUtils.EMPTY);
    }

    /**
     * Gets the first segment of the suffix or a default value.
     *
     * @param request      the current Sling request
     * @param defaultValue the value to return if no suffix exists
     * @return the first segment or the default value
     */
    public static @NonNull String getFirstSuffixSegment(@NonNull final SlingHttpServletRequest request, @NonNull final String defaultValue) {
        return getSuffixSegment(request, 0, defaultValue);
    }

    /**
     * Checks if a request parameter exists.
     *
     * @param request the current Sling request
     * @param name    the parameter name
     * @return {@code true} if the parameter is present, {@code false} otherwise
     */
    public static boolean hasParameter(@NonNull final SlingHttpServletRequest request, @NonNull final String name) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(name);

        return request.getRequestParameter(name) != null;
    }

    /**
     * Gets multiple values of a parameter as a list.
     *
     * @param request the current Sling request
     * @param name    the parameter name
     * @return a non-null list of parameter values
     */
    public static @NonNull List<String> getParameterList(@NonNull final SlingHttpServletRequest request, @NonNull final String name) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(name);

        final String[] paramValues = request.getParameterValues(name);
        if (paramValues != null) {
            return Arrays.stream(paramValues)
                    .filter(Objects::nonNull)
                    .toList();
        }

        return Collections.emptyList();
    }

    /**
     * Gets a request parameter value. Defaults to empty string {@code ""} if missing or invalid.
     *
     * @param request the current Sling request
     * @param name    the parameter name
     * @return the parameter value or an empty string
     */
    public static @NonNull String getParameter(@NonNull final SlingHttpServletRequest request, @NonNull final String name) {
        return getParameter(request, name, StringUtils.EMPTY);
    }

    /**
     * Gets a request parameter value or a default value.
     *
     * @param request      the current Sling request
     * @param name         the parameter name
     * @param defaultValue the value to return if the parameter is missing
     * @return the parameter value or the default value
     */
    public static @NonNull String getParameter(@NonNull final SlingHttpServletRequest request, @NonNull final String name, @NonNull final String defaultValue) {
        if (hasParameter(request, name)) {
            return request.getParameter(name);
        }

        return defaultValue;
    }

    /**
     * Gets a request parameter as an integer. Defaults to {@code 0} if missing or invalid.
     *
     * @param request the current Sling request
     * @param name    the parameter name
     * @return the integer value or 0
     */
    public static int getParameterAsInt(@NonNull final SlingHttpServletRequest request, @NonNull final String name) {
        return getParameterAsInt(request, name, 0);
    }

    /**
     * Gets a request parameter as an integer or a default value.
     *
     * @param request      the current Sling request
     * @param name         the parameter name
     * @param defaultValue the value to return if the parameter is missing or not a valid integer
     * @return the integer value or the default value
     */
    public static int getParameterAsInt(@NonNull final SlingHttpServletRequest request, @NonNull final String name, final int defaultValue) {
        if (hasParameter(request, name)) {
            try {
                return Integer.parseInt(getParameter(request, name));
            } catch (final NumberFormatException e) {
                return defaultValue;
            }
        }

        return defaultValue;
    }

    /**
     * Gets a request parameter as a long. Defaults to {@code 0L} if missing or invalid.
     *
     * @param request the current Sling request
     * @param name    the parameter name
     * @return the long value or 0L
     */
    public static long getParameterAsLong(@NonNull final SlingHttpServletRequest request, @NonNull final String name) {
        return getParameterAsLong(request, name, 0L);
    }

    /**
     * Gets a request parameter as a long or a default value.
     *
     * @param request      the current Sling request
     * @param name         the parameter name
     * @param defaultValue the value to return if the parameter is missing or not a valid long
     * @return the long value or the default value
     */
    public static long getParameterAsLong(@NonNull final SlingHttpServletRequest request, @NonNull final String name, final long defaultValue) {
        if (hasParameter(request, name)) {
            try {
                return Long.parseLong(getParameter(request, name));
            } catch (final NumberFormatException e) {
                return defaultValue;
            }
        }

        return defaultValue;
    }

    /**
     * Gets a request parameter as a float. Defaults to {@code 0.0f} if missing or invalid.
     *
     * @param request the current Sling request
     * @param name    the parameter name
     * @return the float value or 0.0f
     */
    public static float getParameterAsFloat(@NonNull final SlingHttpServletRequest request, @NonNull final String name) {
        return getParameterAsFloat(request, name, 0.0f);
    }

    /**
     * Gets a request parameter as a float or a default value.
     *
     * @param request      the current Sling request
     * @param name         the parameter name
     * @param defaultValue the value to return if the parameter is missing or not a valid float
     * @return the float value or the default value
     */
    public static float getParameterAsFloat(@NonNull final SlingHttpServletRequest request, @NonNull final String name, final float defaultValue) {
        if (hasParameter(request, name)) {
            try {
                return Float.parseFloat(getParameter(request, name));
            } catch (final NumberFormatException e) {
                return defaultValue;
            }
        }

        return defaultValue;
    }

    /**
     * Gets a request parameter as a boolean. Defaults to {@code false} if missing.
     *
     * @param request the current Sling request
     * @param name    the parameter name
     * @return the boolean value or {@code false}
     */
    public static boolean getParameterAsBoolean(@NonNull final SlingHttpServletRequest request, @NonNull final String name) {
        return getParameterAsBoolean(request, name, false);
    }

    /**
     * Gets a request parameter as a boolean or a default value.
     *
     * @param request      the current Sling request
     * @param name         the parameter name
     * @param defaultValue the value to return if the parameter is missing
     * @return the boolean value or the default value
     */
    public static boolean getParameterAsBoolean(@NonNull final SlingHttpServletRequest request, @NonNull final String name, final boolean defaultValue) {
        if (hasParameter(request, name)) {
            return Boolean.parseBoolean(getParameter(request, name));
        }

        return defaultValue;
    }

}
