## Example usage

### RunModeService

```java

import com.merkle.oss.aem.utils.services.runmode.RunModeService;
//other imports...

@Component(service = Servlet.class)
@SlingServletResourceTypes(
        extensions = "json",
        methods = HttpConstants.METHOD_GET,
        resourceTypes = {"sling/servlet/default"}
)
public class ExampleServlet extends SlingSafeMethodsServlet implements OptingServlet {

    @Serial
    private static final long serialVersionUID = -4128453348589897586L;

    @Reference
    private transient RunModeService runModeService;

    @Override
    public boolean accepts(@NonNull final SlingHttpServletRequest request) {
        /* <--- EXAMPLE ---> */
        return runModeService.isAuthor();
    }

    @Override
    protected void doGet(@NonNull final SlingHttpServletRequest request,
                         @NonNull final SlingHttpServletResponse response) {

        //servlet logic will only be executed on author instances
    }
}


```

```java

import com.merkle.oss.aem.utils.services.runmode.RunModeService;
//other imports...

@Component(service = Servlet.class)
@SlingServletResourceTypes(
        extensions = "json",
        methods = HttpConstants.METHOD_GET,
        resourceTypes = {"sling/servlet/default"}
)
public class ExampleServlet extends SlingSafeMethodsServlet {

    @Serial
    private static final long serialVersionUID = -4128453348589897556L;

    @Reference
    private transient RunModeService runModeService;

    @Override
    protected void doGet(@NonNull final SlingHttpServletRequest request,
                         @NonNull final SlingHttpServletResponse response) {

        //intend to expose current run modes
        /* <--- EXAMPLE ---> */
        final String environmentType = runModeService.getRunModes().get(EnvironmentTypeRunMode.getKey());
        /* <--- EXAMPLE ---> */
        final String serviceType = runModeService.getRunModes().get(ServiceTypeRunMode.getKey());

        //handle servlet logic...
    }
}


```
