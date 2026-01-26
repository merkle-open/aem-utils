## Example usage

```java

import com.merkle.oss.aem.utils.services.runmode.RunModeService;
//... other imports

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
        return runModeService.isAuthor();
    }

    @Override
    protected void doGet(@NonNull final SlingHttpServletRequest request, @NonNull final SlingHttpServletResponse response) {
        //Servlet logic will only be executed on author instances
    }

}

```
