package com.merkle.oss.aem.utils.services.cronjobs;

import com.merkle.oss.aem.utils.services.runmode.RunModeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.sling.event.jobs.JobBuilder;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.ScheduledJobInfo;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

/**
 * Base implementation for the {@link SlingJobScheduler}.
 * <p>
 * This abstract class facilitates the registration and management of Scheduled Sling Jobs using
 * the {@link org.apache.sling.event.jobs.JobManager}. It enforces best practices such as:
 * <ul>
 * <li>Idempotency: Automatically unschedules existing jobs before creating a new one to
 * ensure configuration updates (like changed cron expressions) are applied correctly.</li>
 * <li>Runmode Safety: Prevents scheduling on incorrect instance types (Author vs. Publish)
 * via the {@link RunModeService}.</li>
 * <li>Payload Support: Transparently passes metadata into the Sling Job properties.</li>
 * </ul>
 *
 * @implSpec extending services must implement scheduler config
 * {@snippet :
 * @Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
 * @Designate(ocd = ExampleScheduler.ExampleSchedulerConfig.class)
 * public class ExampleScheduler extends AbstractSlingJobScheduler {
 *     //...
 *     @ObjectClassDefinition(name = "ExampleScheduler Config")
 *     public @interface ExampleSchedulerConfig {
 *
 *         @AttributeDefinition(name = "Enabled")
 *         boolean enabled() default false;
 *
 *         @AttributeDefinition(name = "Scheduler Expression")
 *         String scheduler_expression() default "0 0 0 1 * ? *";
 *
 *     }
 * }
 *}
 * @implNote extending services should register and unregister scheduled jobs on activate/deactivate lifecycle methods.
 * It is adviced to unschedule jobs on {@code publish} instances on deactivation.
 * {@snippet :
 * @Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
 * @Designate(ocd = ExampleScheduler.ExampleSchedulerConfig.class)
 * public class ExampleScheduler extends AbstractSlingJobScheduler {
 *     //...
 *     @Activate
 *     @Modified protected void activate(final ExampleSchedulerConfig config) {
 *         //schedule a job here
 *     }
 *     @Deactivate protected void deactivate() {
 *         if (runModeService.isPublish()) {
 *             unscheduleJob(MailJob.JOB_TOPIC_VALUE);
 *         }
 *     }
 *     //...
 * }
 *}
 */
public abstract class AbstractSlingJobScheduler implements SlingJobScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSlingJobScheduler.class);

    /**
     * @return A descriptive name for the service used in logging (e.g., "Daily Report Service").
     */
    protected abstract String getServiceName();

    /**
     * @return An implementation of the {@link RunModeService} to determine environment state.
     */
    protected abstract RunModeService getRunModeService();

    /**
     * @return The {@link org.apache.sling.event.jobs.JobManager} service required to interact with the Sling Eventing system.
     */
    protected abstract JobManager getJobManager();

    /**
     * {@inheritDoc}
     */
    @Override
    public void scheduleJobOnAuthor(final boolean enabled, @NonNull final String schedulerExpression, @NonNull final String jobTopic, @Nullable final Map<String, Object> payload) {
        if (getRunModeService().isPublish()) {
            LOG.error("{} mustn't be configured on publish. Scheduling of job aborted.", getServiceName());
            return;
        }

        scheduleJob(enabled, schedulerExpression, jobTopic, payload);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scheduleJobOnPublish(final boolean enabled, @NonNull final String schedulerExpression, @NonNull final String jobTopic, @Nullable final Map<String, Object> payload) {
        if (getRunModeService().isAuthor()) {
            LOG.error("{} mustn't be configured on author. Scheduling of job aborted.", getServiceName());
            return;
        }

        scheduleJob(enabled, schedulerExpression, jobTopic, payload);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scheduleJob(final boolean enabled, @NonNull final String schedulerExpression, @NonNull final String jobTopic, @Nullable final Map<String, Object> payload) {
        if (!enabled) {
            unscheduleJob(jobTopic);
            return;
        }

        if (doesScheduledJobExist(jobTopic)) {
            LOG.info("{} job already scheduled. Removing existing schedules job to register new instance of job with potential new schedule cron value.", getServiceName());
            unscheduleJob(jobTopic);
        }

        final JobBuilder jobBuilder = getJobManager().createJob(jobTopic);
        if (MapUtils.isNotEmpty(payload)) {
            jobBuilder.properties(payload);
        }

        final JobBuilder.ScheduleBuilder scheduleBuilder = jobBuilder.schedule();
        scheduleBuilder.cron(schedulerExpression);
        if (scheduleBuilder.add() == null) {
            LOG.error("Unable to register scheduled {} with schedule '{}'", getServiceName(), schedulerExpression);
        } else {
            LOG.info("Registered {} job with schedule '{}'.", getServiceName(), schedulerExpression);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean doesScheduledJobExist(@NonNull final String jopTopic) {
        return CollectionUtils.isNotEmpty(getJobManager().getScheduledJobs(jopTopic, 1, (Map<String, Object>[]) null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unscheduleJob(@NonNull final String jobTopic) {
        final Collection<ScheduledJobInfo> myJobs = getJobManager().getScheduledJobs(jobTopic, 0, (Map<String, Object>[]) null);
        myJobs.forEach(ScheduledJobInfo::unschedule);
        LOG.info("{} jobs have been unscheduled.", getServiceName());
    }

}
