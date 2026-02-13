package com.merkle.oss.aem.utils.servlets;

import com.merkle.oss.aem.utils.link.LinkExternalizerUtil;
import com.merkle.oss.aem.utils.link.LinkMappingUtil;
import com.merkle.oss.aem.utils.services.runmode.RunModeService;
import com.merkle.oss.aem.utils.services.runmode.impl.RunModeServiceImpl;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

/**
 * Unit tests for the {@link UtilsTestServlet} class.
 */
@ExtendWith(AemContextExtension.class)
class UtilsTestServletTest {

    private final UtilsTestServlet fixture = new UtilsTestServlet();

    /**
     * Method under test: helper to inject a value into a private field via reflection.
     * <p>
     * This is used to wire OSGi {@code @Reference}-style dependencies into the servlet instance
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
    void doGet_runMode(final AemContext context) throws InvocationTargetException, IllegalAccessException {
        final MockSlingHttpServletRequest request = context.request();
        final MockSlingHttpServletResponse response = context.response();

        final RunModeServiceImpl service = new RunModeServiceImpl();
        final Map<String, Object> config = Map.of(
                "serviceType", "author",
                "environmentType", "prod"
        );

        final Class<?> configClass = RunModeServiceImpl.RunModeServiceConfig.class;
        Object configProxy = Proxy.newProxyInstance(
                configClass.getClassLoader(),
                new Class<?>[]{configClass},
                (proxy, method, args) -> config.get(method.getName())
        );

        try {
            Method activateMethod = RunModeServiceImpl.class.getDeclaredMethod("activate", configClass);
            activateMethod.setAccessible(true);
            activateMethod.invoke(service, configProxy);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not find a protected void activate(Map) method in RunModeServiceImpl. " +
                    "Check the method signature in your service implementation.", e);
        }

        context.registerService(RunModeService.class, service);
        injectField(fixture, "runModeService", service);

        assertDoesNotThrow(() -> fixture.doGet(request, response));
    }

    /**
     * Method under test: {@link UtilsTestServlet@doGet(SlingHttpServletRequest, SlingHttpServletResponse)}.
     */
    @Test
    void doGet_pathParameter(final AemContext context) throws InvocationTargetException, IllegalAccessException {
        final MockSlingHttpServletRequest request = context.request();
        request.addRequestParameter("path", "/content/tenant/ch/de/home");
        final MockSlingHttpServletResponse response = context.response();

        final RunModeServiceImpl service = new RunModeServiceImpl();
        final Map<String, Object> config = Map.of(
                "serviceType", "author",
                "environmentType", "prod"
        );

        final Class<?> configClass = RunModeServiceImpl.RunModeServiceConfig.class;
        Object configProxy = Proxy.newProxyInstance(
                configClass.getClassLoader(),
                new Class<?>[]{configClass},
                (proxy, method, args) -> config.get(method.getName())
        );

        try {
            Method activateMethod = RunModeServiceImpl.class.getDeclaredMethod("activate", configClass);
            activateMethod.setAccessible(true);
            activateMethod.invoke(service, configProxy);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not find a protected void activate(Map) method in RunModeServiceImpl. " +
                    "Check the method signature in your service implementation.", e);
        }

        context.registerService(RunModeService.class, service);
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
