package com.merkle.oss.aem.utils.services.runmode.impl;

import com.merkle.oss.aem.utils.services.runmode.modes.impl.EnvironmentTypeRunMode;
import com.merkle.oss.aem.utils.services.runmode.modes.impl.ServiceTypeRunMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link RunModeServiceImpl} class.
 */
@ExtendWith(MockitoExtension.class)
class RunModeServiceImplTest {

    @Mock
    private RunModeServiceImpl.RunModeServiceConfig config;

    @InjectMocks
    private RunModeServiceImpl runModeService = new RunModeServiceImpl();

    /**
     * Method under test: {@link RunModeServiceImpl#activate(RunModeServiceImpl.RunModeServiceConfig)}
     */
    @Test
    void testActivate() {
        assertThrows(NullPointerException.class, () -> runModeService.activate(null));
        assertDoesNotThrow(() -> runModeService.activate(config));
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link RunModeServiceImpl#getRunModes()}
     * </ul>
     */
    @Test
    void testGetters_invalid() {
        when(config.serviceType()).thenReturn("publish");
        when(config.environmentType()).thenReturn(null);
        runModeService.activate(config);

        assertEquals(new HashMap<>(),runModeService.getRunModes());
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link RunModeServiceImpl#isAuthor()}
     *   <li>{@link RunModeServiceImpl#isPublish()}
     *   <li>{@link RunModeServiceImpl#isLocal()}
     *   <li>{@link RunModeServiceImpl#isRde()}
     *   <li>{@link RunModeServiceImpl#isDev()}
     *   <li>{@link RunModeServiceImpl#isStage()}
     *   <li>{@link RunModeServiceImpl#isProd()}
     *   <li>{@link RunModeServiceImpl#getRunModes()}
     * </ul>
     */
    @Test
    void testGetters() {
        when(config.serviceType()).thenReturn("author");
        when(config.environmentType()).thenReturn("local");
        runModeService.activate(config);

        assertTrue(runModeService.isAuthor());
        assertFalse(runModeService.isPublish());
        assertTrue(runModeService.isLocal());
        assertFalse(runModeService.isRde());
        assertFalse(runModeService.isDev());
        assertFalse(runModeService.isStage());
        assertFalse(runModeService.isProd());
        assertEquals(2, runModeService.getRunModes().size());
        assertEquals(ServiceTypeRunMode.Type.AUTHOR.getMode(), runModeService.getRunModes().get(ServiceTypeRunMode.getKey()));
        assertEquals(EnvironmentTypeRunMode.Type.LOCAL.getMode(), runModeService.getRunModes().get(EnvironmentTypeRunMode.getKey()));
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link RunModeServiceImpl#isAuthor()}
     *   <li>{@link RunModeServiceImpl#isPublish()}
     *   <li>{@link RunModeServiceImpl#isLocal()}
     *   <li>{@link RunModeServiceImpl#isRde()}
     *   <li>{@link RunModeServiceImpl#isDev()}
     *   <li>{@link RunModeServiceImpl#isStage()}
     *   <li>{@link RunModeServiceImpl#isProd()}
     *   <li>{@link RunModeServiceImpl#getRunModes()}
     * </ul>
     */
    @Test
    void testGetters_others() {
        when(config.serviceType()).thenReturn("publish");
        when(config.environmentType()).thenReturn("prod");
        runModeService.activate(config);

        assertFalse(runModeService.isAuthor());
        assertTrue(runModeService.isPublish());
        assertFalse(runModeService.isLocal());
        assertFalse(runModeService.isRde());
        assertFalse(runModeService.isDev());
        assertFalse(runModeService.isStage());
        assertTrue(runModeService.isProd());
        assertEquals(2, runModeService.getRunModes().size());
        assertEquals(ServiceTypeRunMode.Type.PUBLISH.getMode(), runModeService.getRunModes().get(ServiceTypeRunMode.getKey()));
        assertEquals(EnvironmentTypeRunMode.Type.PROD.getMode(), runModeService.getRunModes().get(EnvironmentTypeRunMode.getKey()));

        when(config.environmentType()).thenReturn("rde");
        runModeService.activate(config);

        assertTrue(runModeService.isRde());

        when(config.environmentType()).thenReturn("dev");
        runModeService.activate(config);

        assertTrue(runModeService.isDev());

        when(config.environmentType()).thenReturn("stage");
        runModeService.activate(config);

        assertTrue(runModeService.isStage());
    }

}
