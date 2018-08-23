package com.namics.oss.aem.utils;

import com.day.cq.commons.Externalizer;
import com.namics.oss.aem.constant.RunMode;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.settings.SlingSettingsService;

import java.util.Optional;

/**
 * Methods to create externalized links.
 */
public class LinkExternalizerUtil {

    /**
     * Creates a run mode respected absolute URL
     *
     * @param link    optional, externalize if available
     * @param request to evaluate run mode and resource mapping
     * @return optional of a externalized link
     */
    public static Optional<String> externalize(Optional<String> link, SlingHttpServletRequest request) {
        if (request == null) {
            return Optional.empty();
        }

        String runMode = evaluateRunMode(request);
        return link.map(l -> externalizeLink(request, l, runMode));
    }

    private static String evaluateRunMode(SlingHttpServletRequest request) {
        String runMode;
        final SlingBindings bindings = (SlingBindings) request.getAttribute(SlingBindings.class.getCanonicalName());
        if (bindings != null) {
            final SlingSettingsService slingSettingsService = bindings.getSling().getService(SlingSettingsService.class);
            runMode = slingSettingsService.getRunModes().contains(RunMode.Type.AUTHOR.getMode()) ? RunMode.Type.AUTHOR.getMode() : RunMode.Type.PUBLISH.getMode();
        } else {
            runMode = RunMode.Type.PUBLISH.getMode();
        }
        return runMode;
    }

    private static String externalizeLink(SlingHttpServletRequest request, String path, String runMode) {
        final ResourceResolver resolver = request.getResourceResolver();
        final String mappedUrl = resolver.map(request, path);

        final Externalizer externalizer = resolver.adaptTo(Externalizer.class);
        return externalizer.externalLink(resolver, runMode, mappedUrl);
    }
}
