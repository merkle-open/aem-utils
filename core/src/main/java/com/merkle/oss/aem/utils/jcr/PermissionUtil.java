package com.merkle.oss.aem.utils.jcr;

import com.merkle.oss.aem.utils.annotations.tooling.Generated;
import com.merkle.oss.aem.utils.java.ClassUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.jackrabbit.api.security.authorization.PrincipalSetPolicy;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.oak.spi.security.authorization.cug.CugPolicy;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.base.util.AccessControlUtil;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.AccessControlPolicyIterator;
import java.security.Principal;
import java.util.*;
import java.util.stream.Stream;

/**
 * Static utility methods for JCR Access Control, User Management, and Permission checking.
 * <p>
 * This class abstracts the complexity of working with Jackrabbit's {@link javax.jcr.security.AccessControlManager},
 * {@link org.apache.jackrabbit.api.security.user.UserManager}, and CUG (Closed User Group) Policies.
 */
public final class PermissionUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PermissionUtil.class);

    /**
     * The User ID for the anonymous user.
     */
    public static final String USER_ID_ANONYMOUS = "anonymous";

    /**
     * The User ID for the system administrator.
     */
    public static final String USER_ID_ADMIN = "admin";

    /**
     * The Group ID representing "everyone".
     */
    public static final String USER_GROUP_ID_EVERYONE = "everyone";

    @Generated("Bypass coverage for static utility constructor")
    private PermissionUtil() {
        ClassUtil.assertNoInstance(this.getClass());
    }

    /**
     * Retrieves the current User ID from the JCR Session, filtering out "anonymous".
     *
     * @param session The JCR session to inspect.
     * @return The User ID if the session is valid and not anonymous; otherwise {@code ""}.
     */
    public static @NonNull String getUserId(@Nullable final Session session) {
        if (session == null) {
            return StringUtils.EMPTY;
        }
        if (isAnonymous(session.getUserID())) {
            return StringUtils.EMPTY;
        }
        return session.getUserID();
    }

    /**
     * Checks if the provided User ID belongs to the system administrator.
     *
     * @param userId The user ID string to check.
     * @return {@code true} if the ID matches {@value #USER_ID_ADMIN}; {@code false} otherwise.
     */
    public static boolean isAdmin(@Nullable final String userId) {
        return Strings.CS.equals(userId, USER_ID_ADMIN);
    }

    private static boolean isAnonymous(@Nullable final String userId) {
        return Strings.CS.equals(userId, USER_ID_ANONYMOUS);
    }

    /**
     * Retrieves the {@link org.apache.jackrabbit.api.security.user.Authorizable} corresponding to the user associated with the current request.
     * <p>
     * This is a convenience wrapper that resolves the session from the request's resource resolver.
     *
     * @param request The current Sling Request.
     * @return The Authorizable for the current user session, or {@code null} if an error occurs.
     */
    public static @Nullable Authorizable getAuthorizable(@Nullable final SlingHttpServletRequest request) {
        if (request == null) {
            return null;
        }

        final Session session = request.getResourceResolver().adaptTo(Session.class);
        final String userId = getUserId(session);
        return getAuthorizable(userId, request.getResourceResolver());
    }

    /**
     * Retrieves a specific {@link org.apache.jackrabbit.api.security.user.Authorizable} by its ID using the provided ResourceResolver.
     *
     * @param authorizableId   The ID of the user or group to find.
     * @param resourceResolver The resolver used to access the {@link org.apache.jackrabbit.api.security.user.UserManager}.
     * @return The Authorizable object, or {@code null} if the ID is blank, the resolver is null, or the user is not found.
     * @apiNote This resolver must have sufficient permissions to read user data.
     * If the {@link org.apache.sling.api.resource.ResourceResolver} was retrieved from the current {@link org.apache.sling.api.SlingHttpServletRequest},
     * the current user does not have permission corresponding to given authorizable ID.
     */
    public static @Nullable Authorizable getAuthorizable(@Nullable final String authorizableId, @Nullable final ResourceResolver resourceResolver) {
        if (StringUtils.isBlank(authorizableId) || resourceResolver == null) {
            return null;
        }
        try {
            final UserManager userManager = resourceResolver.adaptTo(UserManager.class);
            if (userManager == null) {
                return null;
            }
            return userManager.getAuthorizable(authorizableId);
        } catch (RepositoryException e) {
            LOG.error("Unable to get authorizable for user id {}", authorizableId, e);
            return null;
        }
    }

    /**
     * Retrieves an existing {@link org.apache.jackrabbit.api.security.authorization.PrincipalSetPolicy} currently set on the given resource path.
     *
     * @param resourcePath         The absolute path of the resource.
     * @param accessControlManager The AccessControlManager instance.
     * @return The found PrincipalSetPolicy, or {@code null} if none exists or an error occurs.
     */
    public static @Nullable PrincipalSetPolicy getPolicy(@NonNull final String resourcePath, @NonNull final AccessControlManager accessControlManager) {
        Objects.requireNonNull(resourcePath);
        Objects.requireNonNull(accessControlManager);

        try {
            final AccessControlPolicy[] policies = accessControlManager.getPolicies(resourcePath);
            for (final AccessControlPolicy policy : policies) {
                if (policy instanceof PrincipalSetPolicy principalSetPolicy) {
                    return principalSetPolicy;
                }
            }
            return null;
        } catch (RepositoryException e) {
            LOG.error("Unable to get existing principal policy for resource path {}", resourcePath, e);
            return null;
        }
    }

    /**
     * Retrieves an applicable (but not necessarily set) {@link  org.apache.jackrabbit.api.security.authorization.PrincipalSetPolicy} for a resource path.
     *
     * @param resourcePath         The absolute path where the policy would be applied.
     * @param accessControlManager The AccessControlManager instance.
     * @return A valid, applicable PrincipalSetPolicy, or {@code null} if none are available.
     */
    public static @Nullable PrincipalSetPolicy getApplicablePolicy(@NonNull final String resourcePath, @NonNull final AccessControlManager accessControlManager) {
        Objects.requireNonNull(resourcePath);
        Objects.requireNonNull(accessControlManager);

        try {
            final AccessControlPolicyIterator applicablePolicyIterator = accessControlManager.getApplicablePolicies(resourcePath);
            while (applicablePolicyIterator.hasNext()) {
                final AccessControlPolicy accessControlPolicy = applicablePolicyIterator.nextAccessControlPolicy();
                if (accessControlPolicy instanceof PrincipalSetPolicy principalSetPolicy) {
                    return principalSetPolicy;
                }
            }
            return null;
        } catch (RepositoryException e) {
            LOG.error("Unable to get applicable principal policy for resource path {}", resourcePath, e);
            return null;
        }
    }

    /**
     * Retrieves the list of User Group names that are effectively authorized (via CUG) for the given resource.
     * <p>
     * This includes groups assigned directly to the resource or inherited from parent CUG policies.
     * It recursively flattens the group membership.
     *
     * @param resource         The resource to inspect.
     * @param resourceResolver The resolver used to look up group details.
     * @return A list of group names (Strings) that have access. Returns an empty list if no CUG is present or on error.
     * @apiNote <b>Important Environment Difference:</b> CUG Policies are typically evaluated differently on
     * Author vs. Publish tiers. On Author, CUGs often do not restrict read access for authors, whereas on Publish
     * they strictly enforce access. This method relies on {@link #getEffectivePolicy}, which may return
     * different results depending on the active run mode configuration.
     */
    public static @NonNull List<String> getAuthorizedUserGroups(@Nullable final Resource resource, @Nullable final ResourceResolver resourceResolver) {
        if (resource == null || resourceResolver == null) {
            return Collections.emptyList();
        }

        try {
            final Session session = resourceResolver.adaptTo(Session.class);
            if (session == null) {
                return Collections.emptyList();
            }

            final AccessControlManager accessControlManager = AccessControlUtil.getAccessControlManager(session);
            //This will only fetch correct policies on a PUBLISH tier. Not on AUTHOR
            final CugPolicy cugPolicy = PermissionUtil.getEffectivePolicy(resource.getPath(), accessControlManager);
            if (cugPolicy == null) {
                return Collections.emptyList();
            }

            return cugPolicy.getPrincipals().stream()
                    .map(Principal::getName)
                    .flatMap(name -> getUserGroupMembers(name, resourceResolver).stream())
                    .toList();

        } catch (RepositoryException e) {
            LOG.error("Unable to retrieve access control manager", e);
        }

        return Collections.emptyList();
    }

    /**
     * Retrieves the effective {@link org.apache.jackrabbit.oak.spi.security.authorization.cug.CugPolicy} (Closed User Group) for the given resource path.
     * <p>
     * This method traverses the policy hierarchy to find the CUG policy that actually controls access
     * to the specified path, handling inheritance logic.
     *
     * @param resourcePath         The absolute path to check.
     * @param accessControlManager The AccessControlManager instance.
     * @return The effective {@code CugPolicy}, or {@code null} if no CUG is active for this path.
     */
    public static @Nullable CugPolicy getEffectivePolicy(@NonNull final String resourcePath, @NonNull final AccessControlManager accessControlManager) {
        Objects.requireNonNull(resourcePath);
        Objects.requireNonNull(accessControlManager);

        try {
            final List<CugPolicy> cugPolicies = Stream.of(accessControlManager.getEffectivePolicies(resourcePath))
                    .filter(CugPolicy.class::isInstance)
                    .map(CugPolicy.class::cast)
                    .toList();

            CugPolicy closestPolicy = null;
            for (final CugPolicy cugPolicy : cugPolicies) {
                if (Strings.CS.equals(resourcePath, cugPolicy.getPath())) {
                    return cugPolicy;
                }

                if (closestPolicy == null) {
                    closestPolicy = cugPolicy;
                }

                if (Strings.CS.contains(cugPolicy.getPath(), closestPolicy.getPath())) {
                    closestPolicy = cugPolicy;
                }
            }

            return closestPolicy;
        } catch (RepositoryException e) {
            LOG.error("Unable to get existing cug policy for resource path {}", resourcePath, e);
            return null;
        }
    }

    private static @NonNull List<String> getUserGroupMembers(@Nullable final String principalName, @Nullable final ResourceResolver resourceResolver) {
        final List<String> groupNames = new ArrayList<>();
        try {
            final Authorizable authorizable = PermissionUtil.getAuthorizable(principalName, resourceResolver);
            if (authorizable == null || !authorizable.isGroup()) {
                return groupNames;
            }
            groupNames.add(principalName);
            final Group group = (Group) authorizable;
            final Iterator<Authorizable> iterator = group.getDeclaredMembers();
            while (iterator.hasNext()) {
                final Authorizable groupMemberAuthorizable = iterator.next();
                if (groupMemberAuthorizable.isGroup() && !Strings.CS.equals(groupMemberAuthorizable.getPrincipal().getName(), PermissionUtil.USER_GROUP_ID_EVERYONE)) {
                    groupNames.add(groupMemberAuthorizable.getPrincipal().getName());
                }

            }
        } catch (RepositoryException e) {
            LOG.error("Unable to retrieve group member of for {}", principalName, e);
        }
        return groupNames;
    }


    /**
     * Checks if the current session has permission to perform specific JCR actions on the request's resource.
     *
     * <p>Supported actions include (comma-separated):</p>
     * <ul>
     * <li>{@code read}: Retrieve the item and its properties.</li>
     * <li>{@code add_node}: Add a child node at the path.</li>
     * <li>{@code set_property}: Add or modify a property.</li>
     * <li>{@code remove}: Delete the item.</li>
     * </ul>
     *
     * @param request The current request (used to derive the resource and session).
     * @param actions A comma-separated list of actions (e.g., {@code "read,set_property"}).
     * @return {@code true} if the user has ALL requested permissions; {@code false} if denied or if an error occurs.
     * @apiNote Example usage:
     * {@snippet :
     * if (PermissionUtil.userHasPermissionForActions(request, "add_node,set_property")) {
     *     //handle write action
     * }
     *}
     * @see javax.jcr.Session#checkPermission(String, String)
     */
    public static boolean userHasPermissionForActions(@NonNull final SlingHttpServletRequest request, @NonNull final String actions) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(actions);

        final Node formStartNode = request.getResource().adaptTo(Node.class);
        if (formStartNode == null) {
            return false;
        }
        try {
            final Session userSession = formStartNode.getSession();
            if (userSession == null) {
                return false;
            }

            userSession.checkPermission(request.getResource().getPath(), actions);
            return true;
        } catch (RepositoryException e) {
            LOG.error("Couldn't read from repository for permission check", e);
        }
        return false;
    }

}
