package com.merkle.oss.aem.utils.services.cronjobs;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * Service interface for managing scheduled Sling Jobs via cron expressions.
 * <p>
 * This service provides a high-level abstraction over the Sling Job Manager and Scheduler,
 * specifically handling the runmode-dependent nuances of AEM architectures (Clusters vs. Farms).
 * </p>
 * <p>
 * Using Sling Jobs for scheduling is preferred over standard Sling Commons Scheduler
 * when guaranteed execution and auditability are required.
 */
public interface SlingJobScheduler {

    /**
     * Registers a scheduled sling job based on a provided cron regular expression.
     * <p>
     * Scheduling of a sling job is reduced to an instance of service type {@code author}.
     * Author instances are running within a cluster, and therefore sling jobs are executed only once.
     * One repository - one job execution - guaranteed by sling implementation
     *
     * @param enabled             If {@code true}, registers/updates the schedule; if {@code false}, removes all schedules for this topic.
     * @param schedulerExpression A valid Cron expression (e.g., "0 0 12 * * ?").
     * @param jobTopic            The unique Sling Job topic to trigger.
     * @param payload             Optional metadata to pass into the Job properties.
     */
    void scheduleJobOnAuthor(final boolean enabled, @NonNull final String schedulerExpression, @NonNull final String jobTopic, @Nullable final Map<String, Object> payload);

    /**
     * Registers a scheduled sling job based on a provided cron regular expression.
     * <p>
     * Scheduling of a sling job is reduced to an instance of service type {@code publish}.
     * Publish instances are running within a farm, and therefore sling jobs are executed once for every node within the farm.
     * Each repository - job execution on each node - guaranteed by sling implementation
     *
     * @param enabled             If {@code true}, registers/updates the schedule; if {@code false}, removes all schedules for this topic.
     * @param schedulerExpression A valid Cron expression.
     * @param jobTopic            The unique Sling Job topic to trigger.
     * @param payload             Optional metadata to pass into the Job properties.
     */
    void scheduleJobOnPublish(final boolean enabled, @NonNull final String schedulerExpression, @NonNull final String jobTopic, @Nullable final Map<String, Object> payload);


    /**
     * Registers a scheduled sling job based on a provided cron regular expression.
     * <ul>
     *     <li>For {@code author} instances (running within a cluster), sling jobs are executed only once.</li>
     *     <li>For {@code publish} instances (running within a farm), sling jobs are executed once for every node within the farm.</li>
     * </ul>
     *
     * @param enabled             If {@code true}, registers/updates the schedule.
     * @param schedulerExpression A valid Cron expression.
     * @param jobTopic            The unique Sling Job topic to trigger.
     * @param payload             Optional metadata to pass into the Job properties.
     */
    void scheduleJob(final boolean enabled, @NonNull final String schedulerExpression, @NonNull final String jobTopic, @Nullable final Map<String, Object> payload);

    /**
     * Checks if a scheduled job is currently registered for the specified topic.
     *
     * @param jobTopic The topic to verify.
     * @return {@code true} if a schedule exists, {@code false} otherwise.
     */
    boolean doesScheduledJobExist(@NonNull final String jobTopic);

    /**
     * Removes all scheduled instances for a given job topic.
     * <p>
     * This stops future triggers but does not cancel jobs that are currently in a "processing" state.
     *
     * @param jobTopic The topic to unschedule.
     */
    void unscheduleJob(@NonNull final String jobTopic);

}
