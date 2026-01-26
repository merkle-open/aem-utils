## Example usage

```java

import com.merkle.oss.aem.utils.services.cronjobs.AbstractSlingJobScheduler;
import com.merkle.oss.aem.utils.services.runmode.RunModeService;
import org.apache.sling.event.jobs.JobManager;
//... other imports

@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = ExampleScheduler.ExampleSchedulerConfig.class)
public class ExampleScheduler extends AbstractSlingJobScheduler {

    private static final String SERVICE_NAME_IDENTIFIER = "Example Scheduled Service";

    @Reference
    private RunModeService runModeService;

    @Reference
    private JobManager jobManager;

    @Activate
    @Modified
    protected void activate(final ExampleSchedulerConfig config) {
        scheduleJob(config.enabled(), config.scheduler_expression(),"example/job/topic", null);
    }

    @Deactivate
    protected void deactivate() {
        if (runModeService.isPublish()) {
            unscheduleJob("example/job/topic");
        }
    }

    @Override
    protected RunModeService getRunModeService() {
        return runModeService;
    }

    @Override
    protected JobManager getJobManager() {
        return jobManager;
    }

    @Override
    protected String getServiceName() {
        return SERVICE_NAME_IDENTIFIER;
    }

    @ObjectClassDefinition(name = "ExampleScheduler Config")
    public @interface ExampleSchedulerConfig {

        @AttributeDefinition(name = "Enabled")
        boolean enabled() default false;

        @AttributeDefinition(name = "Scheduler Expression")
        String scheduler_expression() default "0 0 0 1 * ? *";
    }

}

```
