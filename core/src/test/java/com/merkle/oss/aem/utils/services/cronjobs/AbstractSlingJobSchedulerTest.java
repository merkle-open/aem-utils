package com.merkle.oss.aem.utils.services.cronjobs;

import com.merkle.oss.aem.utils.services.runmode.RunModeService;
import org.apache.sling.event.jobs.JobBuilder;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.ScheduledJobInfo;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.annotations.Reference;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link AbstractSlingJobScheduler} class.
 */
@ExtendWith(MockitoExtension.class)
public class AbstractSlingJobSchedulerTest {

    private static final String CRON_EXPRESSION = "0 0 12 * * ?";

    private static final String JOB_TOPIC = "example/job/topic";

    @Mock
    private RunModeService runModeService;

    @Mock
    private JobManager jobManager;

    @Mock
    private JobBuilder jobBuilder;

    @Mock
    private JobBuilder.ScheduleBuilder scheduleBuilder;

    @Mock
    private ScheduledJobInfo scheduledJobInfo;

    @InjectMocks
    private ExampleSchedulerImpl exampleSchedulerImpl = new ExampleSchedulerImpl();

    /**
     * Method under test: {@link AbstractSlingJobScheduler#scheduleJobOnAuthor(boolean, String, String, Map)}
     */
    @Test
    void scheduleJobOnAuthor() {
        when(runModeService.isPublish()).thenReturn(true);
        exampleSchedulerImpl.scheduleJobOnAuthor(false, CRON_EXPRESSION, JOB_TOPIC, null);
        verify(jobManager, never()).getScheduledJobs(JOB_TOPIC, 0, (Map<String, Object>[]) null);

        when(runModeService.isPublish()).thenReturn(false);
        exampleSchedulerImpl.scheduleJobOnAuthor(false, CRON_EXPRESSION, JOB_TOPIC, null);
        verify(jobManager, times(1)).getScheduledJobs(JOB_TOPIC, 0, (Map<String, Object>[]) null);
    }

    /**
     * Method under test: {@link AbstractSlingJobScheduler#scheduleJobOnPublish(boolean, String, String, Map)}
     */
    @Test
    void scheduleJobOnPublish() {
        when(runModeService.isAuthor()).thenReturn(true);
        exampleSchedulerImpl.scheduleJobOnPublish(false, CRON_EXPRESSION, JOB_TOPIC, null);
        verify(jobManager, never()).getScheduledJobs(JOB_TOPIC, 0, (Map<String, Object>[]) null);

        when(runModeService.isAuthor()).thenReturn(false);
        exampleSchedulerImpl.scheduleJobOnPublish(false, CRON_EXPRESSION, JOB_TOPIC, null);
        verify(jobManager, times(1)).getScheduledJobs(JOB_TOPIC, 0, (Map<String, Object>[]) null);
    }

    /**
     * Method under test: {@link AbstractSlingJobScheduler#scheduleJob(boolean, String, String, Map)}
     */
    @Test
    void scheduleJob() {
        when(jobManager.getScheduledJobs(JOB_TOPIC, 0, (Map<String, Object>[]) null)).thenReturn(Collections.singletonList(scheduledJobInfo));
        exampleSchedulerImpl.scheduleJob(false, CRON_EXPRESSION, JOB_TOPIC, null);
        verify(jobManager, never()).createJob(JOB_TOPIC);

        when(jobManager.getScheduledJobs(JOB_TOPIC, 1, (Map<String, Object>[]) null)).thenReturn(Collections.emptyList());
        when(jobManager.createJob(JOB_TOPIC)).thenReturn(jobBuilder);
        when(jobBuilder.schedule()).thenReturn(scheduleBuilder);
        when(scheduleBuilder.add()).thenReturn(null);
        exampleSchedulerImpl.scheduleJob(true, CRON_EXPRESSION, JOB_TOPIC, null);
        verify(scheduleBuilder, atLeastOnce()).cron(CRON_EXPRESSION);

        when(jobManager.getScheduledJobs(JOB_TOPIC, 1, (Map<String, Object>[]) null)).thenReturn(Collections.singletonList(scheduledJobInfo));
        when(scheduleBuilder.add()).thenReturn(scheduledJobInfo);
        final Map<String, Object> payload = new HashMap<>();
        payload.put("key", "value");
        exampleSchedulerImpl.scheduleJob(true, CRON_EXPRESSION, JOB_TOPIC, payload);
        verify(scheduleBuilder, atLeastOnce()).cron(CRON_EXPRESSION);

        exampleSchedulerImpl.scheduleJob(true, CRON_EXPRESSION, JOB_TOPIC, Collections.emptyMap());
        verify(scheduleBuilder, atLeastOnce()).cron(CRON_EXPRESSION);
    }

    /**
     * Method under test: {@link AbstractSlingJobScheduler#doesScheduledJobExist(String)}
     */
    @Test
    void doesScheduledJobExist() {
        when(jobManager.getScheduledJobs(JOB_TOPIC, 1, (Map<String, Object>[]) null)).thenReturn(Collections.emptyList());
        assertFalse(exampleSchedulerImpl.doesScheduledJobExist(JOB_TOPIC));

        when(jobManager.getScheduledJobs(JOB_TOPIC, 1, (Map<String, Object>[]) null)).thenReturn(Collections.singletonList(scheduledJobInfo));
        assertTrue(exampleSchedulerImpl.doesScheduledJobExist(JOB_TOPIC));
    }

    /**
     * Method under test: {@link AbstractSlingJobScheduler#unscheduleJob(String)}
     */
    @Test
    void unscheduleJob() {
        when(jobManager.getScheduledJobs(JOB_TOPIC, 0, (Map<String, Object>[]) null)).thenReturn(Collections.singletonList(scheduledJobInfo));
        exampleSchedulerImpl.unscheduleJob(JOB_TOPIC);
        verify(scheduledJobInfo, times(1)).unschedule();
    }

    private static class ExampleSchedulerImpl extends AbstractSlingJobScheduler {

        @Reference
        private RunModeService runModeService;

        @Reference
        private JobManager jobManager;

        @Override
        protected @NonNull String getServiceName() {
            return "Example Scheduled Service";
        }

        @Override
        protected @NonNull RunModeService getRunModeService() {
            return runModeService;
        }

        @Override
        protected @NonNull JobManager getJobManager() {
            return jobManager;
        }

    }

}
