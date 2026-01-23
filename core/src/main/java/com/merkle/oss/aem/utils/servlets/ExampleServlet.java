package com.merkle.oss.aem.utils.servlets;

import com.merkle.oss.aem.utils.services.runmode.RunModeService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.jspecify.annotations.NonNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.Serial;

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
    protected void doGet(@NonNull final SlingHttpServletRequest request, @NonNull final SlingHttpServletResponse response) throws IOException {
        //Servlet logic will only be executed on author instances
    }

}
