package com.merkle.oss.aem.utils.servlets;

import com.merkle.oss.aem.utils.link.LinkExternalizerUtil;
import com.merkle.oss.aem.utils.link.LinkMappingUtil;
import com.merkle.oss.aem.utils.services.runmode.RunModeService;
import com.merkle.oss.aem.utils.services.runmode.impl.RunModeServiceImpl;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

/**
 * Unit tests for the {@link UtilsTestServlet} class.
 */
@ExtendWith(AemContextExtension.class)
public class UtilsTestServletTest {

    private final UtilsTestServlet fixture = new UtilsTestServlet();

    /**
     * Method under test: helper to inject a value into a private field via reflection.
     *
     * <p>This is used to wire OSGi {@code @Reference}-style dependencies into the servlet instance
     * in a plain unit test (without starting a real OSGi container).
     *
     * @param target    Object whose field should be set.
     * @param fieldName Name of the field to set.
     * @param value     Value to assign to the field.
     * @throws IllegalStateException if the field does not exist or cannot be accessed.
     */
    private static void injectField(final Object target, final String fieldName, final Object value) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Field '" + fieldName + "' not found on " + target.getClass().getName()
                    + ". Consider registering the servlet as an OSGi component in the test instead.", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot inject field '" + fieldName + "' on " + target.getClass().getName(), e);
        }
    }

    /**
     * Method under test: {@link UtilsTestServlet@doGet(SlingHttpServletRequest, SlingHttpServletResponse)}.
     */
    @Test
    void doGet_null(final AemContext context) {
        final MockSlingHttpServletRequest request = context.request();
        final MockSlingHttpServletResponse response = context.response();
        assertThrows(NullPointerException.class, () -> fixture.doGet(null, response));
        assertThrows(NullPointerException.class, () -> fixture.doGet(request, null));
    }

    /**
     * Method under test: {@link UtilsTestServlet@doGet(SlingHttpServletRequest, SlingHttpServletResponse)}.
     */
    @Test
    void doGet_runMode(final AemContext context) {
        final MockSlingHttpServletRequest request = context.request();
        final MockSlingHttpServletResponse response = context.response();

        final Map<String, Object> config = Map.of(
                "serviceType", "author",
                "environmentType", "prod"
        );

        final RunModeService service = context.registerInjectActivateService(new RunModeServiceImpl(), config);
        injectField(fixture, "runModeService", service);

        assertDoesNotThrow(() -> fixture.doGet(request, response));
    }

    /**
     * Method under test: {@link UtilsTestServlet@doGet(SlingHttpServletRequest, SlingHttpServletResponse)}.
     */
    @Test
    void doGet_pathParameter(final AemContext context) {
        final MockSlingHttpServletRequest request = context.request();
        request.addRequestParameter("path", "/content/tenant/ch/de/home");
        final MockSlingHttpServletResponse response = context.response();

        final Map<String, Object> config = Map.of(
                "serviceType", "author",
                "environmentType", "prod"
        );

        final RunModeService service = context.registerInjectActivateService(new RunModeServiceImpl(), config);
        injectField(fixture, "runModeService", service);
        try (MockedStatic<LinkExternalizerUtil> linkExternalizerUtilMockedStatic = mockStatic(LinkExternalizerUtil.class)) {
            try (MockedStatic<LinkMappingUtil> linkMappingUtilMockedStatic = mockStatic(LinkMappingUtil.class)) {
                linkExternalizerUtilMockedStatic.when(() -> LinkExternalizerUtil.externalize("/content/tenant/ch/de/home", request)).thenReturn("https://www.domain.ch/de/home");
                linkMappingUtilMockedStatic.when(() -> LinkMappingUtil.map("/content/tenant/ch/de/home", request)).thenReturn("/de/home");
                linkMappingUtilMockedStatic.when(() -> LinkMappingUtil.map("/content/tenant/ch/de/home", request.getResourceResolver())).thenReturn("/de/home");
                assertDoesNotThrow(() -> fixture.doGet(request, response));
            }
        }
    }

}
