package com.merkle.oss.aem.utils.servlets;

import com.google.gson.GsonBuilder;
import com.merkle.oss.aem.utils.constants.FileType;
import com.merkle.oss.aem.utils.link.LinkExternalizerUtil;
import com.merkle.oss.aem.utils.link.LinkMappingUtil;
import com.merkle.oss.aem.utils.services.runmode.RunModeService;
import com.merkle.oss.aem.utils.services.runmode.modes.impl.EnvironmentTypeRunMode;
import com.merkle.oss.aem.utils.services.runmode.modes.impl.ServiceTypeRunMode;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPathsStrict;
import org.jspecify.annotations.NonNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.Serial;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Diagnostic utility servlet used to verify RunMode configurations and Link transformations.
 * <p>
 * This servlet provides a JSON representation of current system environment states and
 * how specific paths are resolved through the utility pack's externalizers and mappers.
 *
 * @apiNote This servlet is registered via a fixed path. In production environments,
 * access should be restricted via Dispatcher filters to prevent sensitive system info leakage.
 * <p>
 * To pass a path value to test the link manipulation against, make use of the {@code path} query parameter:
 * https://your.domain/bin/com/merkle/oss/aem/utils/test.json?path=/content/your/test/path
 *
 */
@Component(service = Servlet.class)
@SlingServletPathsStrict(
        extensions = "json",
        methods = HttpConstants.METHOD_GET,
        paths = {
                "/bin/com/merkle/oss/aem/utils/test"
        }
)
public class UtilsTestServlet extends SlingSafeMethodsServlet {

    @Serial
    private static final long serialVersionUID = -4128453348589897586L;

    private static final String PATH_PARAMETER_NAME = "path";
    private static final String EXTERNALIZE_PATH_KEY = "LinkExternalizerUtil.externalize(path, request)";
    private static final String RESOURCE_MAP_WITH_REQUEST_KEY = "LinkMappingUtil.map(path, request)";
    private static final String RESOURCE_MAP_WITH_RESOLVER_KEY = "LinkMappingUtil.map(path, resourceResolver)";
    private static final String RUN_MODE_IS_AUTHOR_KEY = "RunModeService.isAuthor()";
    private static final String RUN_MODE_SERVICE_KEY = "RunModeService.getRunModes().get(ServiceTypeRunMode.getKey())";
    private static final String RUN_MODE_ENVIRONMENT_KEY = "RunModeService.getRunModes().get(EnvironmentTypeRunMode.getKey())";

    @Reference
    private transient RunModeService runModeService;

    /**
     * {@inheritDoc}
     *
     * @see org.apache.sling.api.servlets.SlingSafeMethodsServlet#doGet(SlingHttpServletRequest, SlingHttpServletResponse)
     */
    @Override
    protected void doGet(@NonNull final SlingHttpServletRequest request, @NonNull final SlingHttpServletResponse response) throws IOException {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(FileType.JSON.getMimeType());

        final SortedMap<String, String> informationDto = new TreeMap<>();

        final String path = request.getParameter(PATH_PARAMETER_NAME);

        if (StringUtils.isNotBlank(path)) {
            informationDto.put(EXTERNALIZE_PATH_KEY, LinkExternalizerUtil.externalize(path, request));
            informationDto.put(RESOURCE_MAP_WITH_REQUEST_KEY, LinkMappingUtil.map(path, request));
            informationDto.put(RESOURCE_MAP_WITH_RESOLVER_KEY, LinkMappingUtil.map(path, request.getResourceResolver()));
        }
        informationDto.put(RUN_MODE_ENVIRONMENT_KEY, runModeService.getRunModes().get(EnvironmentTypeRunMode.getKey()));
        informationDto.put(RUN_MODE_SERVICE_KEY, runModeService.getRunModes().get(ServiceTypeRunMode.getKey()));
        informationDto.put(RUN_MODE_IS_AUTHOR_KEY, String.valueOf(runModeService.isAuthor()));

        response.setStatus(HttpStatus.SC_OK);
        // Disable HTML escaping is needed to prevent gson from escaping chars like '=' to '\u003D'
        new GsonBuilder().disableHtmlEscaping().create().toJson(informationDto, response.getWriter());
    }

}
