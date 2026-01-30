package com.merkle.oss.aem.utils.jcr;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.authorization.PrincipalSetPolicy;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.iterator.AccessControlPolicyIteratorAdapter;
import org.apache.jackrabbit.oak.spi.security.authorization.cug.CugPolicy;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.base.util.AccessControlUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.AccessControlPolicyIterator;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link PermissionUtil} class.
 */
@ExtendWith(MockitoExtension.class)
public class PermissionUtilTest {

    private static final String USER_ID = "userID";

    private static final String RESOURCE_PATH = "/content/resourcePath";

    private static final String CUG_PRINCIPLE_NAME = "cugPrincipalName";

    private static final String PERMISSION_ADD_NODE = "add_node";

    @Mock
    private Session session;
    @Mock
    private SlingHttpServletRequest request;
    @Mock
    private Resource resource;
    @Mock
    private ResourceResolver resourceResolver;
    @Mock
    private UserManager userManager;
    @Mock
    private Authorizable authorizable;
    @Mock
    private Group group;
    @Mock
    private AccessControlManager accessControlManager;
    @Mock
    private MockPolicy mockPolicy;
    @Mock
    private PrincipalSetPolicy principalSetPolicy;
    @Mock
    private CugPolicy cugPolicy;
    @Mock
    private CugPolicy cugPolicy2;
    @Mock
    private Principal principal;
    @Mock
    private Principal principal2;
    @Mock
    private Node node;

    public static class MockPolicy implements AccessControlPolicy {
        // Implementation details of the policy class
    }

    /**
     * Method under test: {@link PermissionUtil#getUserId(Session)}
     */
    @Test
    void getUserId() {
        assertEquals(StringUtils.EMPTY, PermissionUtil.getUserId(null));

        when(session.getUserID()).thenReturn(PermissionUtil.USER_ID_ANONYMOUS);
        assertEquals(StringUtils.EMPTY, PermissionUtil.getUserId(session));

        when(session.getUserID()).thenReturn(USER_ID);
        assertEquals(USER_ID, PermissionUtil.getUserId(session));
    }

    /**
     * Method under test: {@link PermissionUtil#isAdmin(String)}
     */
    @Test
    void isAdmin() {
        assertFalse(PermissionUtil.isAdmin(null));
        assertFalse(PermissionUtil.isAdmin(StringUtils.EMPTY));
        assertFalse(PermissionUtil.isAdmin(USER_ID));

        assertTrue(PermissionUtil.isAdmin(PermissionUtil.USER_ID_ADMIN));
    }

    /**
     * Method under test: {@link PermissionUtil#getAuthorizable(SlingHttpServletRequest)}
     */
    @Test
    void getAuthorizable_request() throws RepositoryException {
        assertNull(PermissionUtil.getAuthorizable(null));

        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(session.getUserID()).thenReturn(USER_ID);
        when(resourceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(userManager.getAuthorizable(USER_ID)).thenReturn(authorizable);
        assertEquals(authorizable, PermissionUtil.getAuthorizable(request));
    }

    /**
     * Method under test: {@link PermissionUtil#getAuthorizable(String, ResourceResolver)}
     */
    @Test
    void getAuthorizable_exception() throws RepositoryException {
        when(resourceResolver.adaptTo(UserManager.class)).thenReturn(null);
        when(resourceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(userManager.getAuthorizable(USER_ID)).thenThrow(RepositoryException.class);
        assertNull(PermissionUtil.getAuthorizable(USER_ID, resourceResolver));
    }

    /**
     * Method under test: {@link PermissionUtil#getAuthorizable(String, ResourceResolver)}
     */
    @Test
    void getAuthorizable_resolver() throws RepositoryException {
        assertNull(PermissionUtil.getAuthorizable(StringUtils.EMPTY, resourceResolver));
        assertNull(PermissionUtil.getAuthorizable(USER_ID, null));

        when(resourceResolver.adaptTo(UserManager.class)).thenReturn(null);
        assertNull(PermissionUtil.getAuthorizable(USER_ID, resourceResolver));

        when(resourceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(userManager.getAuthorizable(USER_ID)).thenReturn(authorizable);
        assertEquals(authorizable, PermissionUtil.getAuthorizable(USER_ID, resourceResolver));
    }

    /**
     * Method under test: {@link PermissionUtil#getPolicy(String, AccessControlManager)}
     */
    @Test
    void getPolicy() throws RepositoryException {
        when(accessControlManager.getPolicies(RESOURCE_PATH)).thenReturn(new PrincipalSetPolicy[]{});
        assertNull(PermissionUtil.getPolicy(RESOURCE_PATH, accessControlManager));

        AccessControlPolicy[] policies = {mockPolicy, principalSetPolicy};
        when(accessControlManager.getPolicies(RESOURCE_PATH)).thenReturn(policies);
        assertNotNull(PermissionUtil.getPolicy(RESOURCE_PATH, accessControlManager));

        when(accessControlManager.getPolicies(RESOURCE_PATH)).thenThrow(RepositoryException.class);
        assertNull(PermissionUtil.getPolicy(RESOURCE_PATH, accessControlManager));
    }

    /**
     * Method under test: {@link PermissionUtil#getEffectivePolicy(String, AccessControlManager)}
     */
    @Test
    void getEffectivePolicy() throws RepositoryException {
        when(accessControlManager.getEffectivePolicies(RESOURCE_PATH)).thenReturn(new PrincipalSetPolicy[]{});
        assertNull(PermissionUtil.getEffectivePolicy(RESOURCE_PATH, accessControlManager));

        AccessControlPolicy[] policies = {mockPolicy, principalSetPolicy, cugPolicy, cugPolicy2};
        when(accessControlManager.getEffectivePolicies(RESOURCE_PATH)).thenReturn(policies);
        assertNotNull(PermissionUtil.getEffectivePolicy(RESOURCE_PATH, accessControlManager));

        when(cugPolicy.getPath()).thenReturn(RESOURCE_PATH);
        assertNotNull(PermissionUtil.getEffectivePolicy(RESOURCE_PATH, accessControlManager));
        assertEquals(cugPolicy, PermissionUtil.getEffectivePolicy(RESOURCE_PATH, accessControlManager));

        when(cugPolicy.getPath()).thenReturn(RESOURCE_PATH + "/path1");
        when(cugPolicy2.getPath()).thenReturn(RESOURCE_PATH + "/path1/path2");
        assertNotNull(PermissionUtil.getEffectivePolicy(RESOURCE_PATH, accessControlManager));
        assertEquals(cugPolicy2, PermissionUtil.getEffectivePolicy(RESOURCE_PATH, accessControlManager));

        when(accessControlManager.getEffectivePolicies(RESOURCE_PATH)).thenThrow(RepositoryException.class);
        assertNull(PermissionUtil.getEffectivePolicy(RESOURCE_PATH, accessControlManager));
    }

    /**
     * Method under test: {@link PermissionUtil#getApplicablePolicy(String, AccessControlManager)}
     */
    @Test
    void getApplicablePolicy() throws RepositoryException {
        AccessControlPolicyIterator accessControlPolicyIterator = new AccessControlPolicyIteratorAdapter(Collections.emptyList());
        when(accessControlManager.getApplicablePolicies(RESOURCE_PATH)).thenReturn(accessControlPolicyIterator);
        assertNull(PermissionUtil.getApplicablePolicy(RESOURCE_PATH, accessControlManager));

        accessControlPolicyIterator = new AccessControlPolicyIteratorAdapter(List.of(mockPolicy, principalSetPolicy));
        when(accessControlManager.getApplicablePolicies(RESOURCE_PATH)).thenReturn(accessControlPolicyIterator);
        assertNotNull(PermissionUtil.getApplicablePolicy(RESOURCE_PATH, accessControlManager));

        when(accessControlManager.getApplicablePolicies(RESOURCE_PATH)).thenThrow(RepositoryException.class);
        assertNull(PermissionUtil.getApplicablePolicy(RESOURCE_PATH, accessControlManager));
    }

    /**
     * Method under test: {@link PermissionUtil#getUserGroupNames(Resource, ResourceResolver)}
     */
    @Test
    void getUserGroupNames() throws RepositoryException {
        assertEquals(Collections.emptyList(), PermissionUtil.getUserGroupNames(null, null));
        assertEquals(Collections.emptyList(), PermissionUtil.getUserGroupNames(resource, null));
        assertEquals(Collections.emptyList(), PermissionUtil.getUserGroupNames(null, resourceResolver));

        try (MockedStatic<AccessControlUtil> accessControlUtilMockedStatic = mockStatic(AccessControlUtil.class)) {
            accessControlUtilMockedStatic.when(() -> AccessControlUtil.getAccessControlManager(any())).thenReturn(accessControlManager);
            when(resource.getPath()).thenReturn(RESOURCE_PATH);
            when(accessControlManager.getEffectivePolicies(any())).thenReturn(new PrincipalSetPolicy[]{});
            assertEquals(Collections.emptyList(), PermissionUtil.getUserGroupNames(resource, resourceResolver));

            AccessControlPolicy[] policies = {cugPolicy};
            when(cugPolicy.getPrincipals()).thenReturn(Set.of(principal));
            when(principal.getName()).thenReturn(CUG_PRINCIPLE_NAME);
            when(accessControlManager.getEffectivePolicies(any())).thenReturn(policies);
            when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
            when(resourceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
            when(userManager.getAuthorizable(CUG_PRINCIPLE_NAME)).thenReturn(authorizable);
            when(authorizable.isGroup()).thenReturn(false);
            assertEquals(Collections.emptyList(), PermissionUtil.getUserGroupNames(resource, resourceResolver));

            when(userManager.getAuthorizable(CUG_PRINCIPLE_NAME)).thenReturn(null);
            assertEquals(Collections.emptyList(), PermissionUtil.getUserGroupNames(resource, resourceResolver));

            when(authorizable.isGroup()).thenReturn(true);
            assertEquals(Collections.emptyList(), PermissionUtil.getUserGroupNames(resource, resourceResolver));

            when(userManager.getAuthorizable(CUG_PRINCIPLE_NAME)).thenReturn(group);
            when(group.isGroup()).thenReturn(true);
            when(group.getDeclaredMembers()).thenReturn(Collections.emptyIterator());
            assertEquals(List.of(CUG_PRINCIPLE_NAME), PermissionUtil.getUserGroupNames(resource, resourceResolver));

            when(userManager.getAuthorizable(CUG_PRINCIPLE_NAME)).thenReturn(group);
            when(group.isGroup()).thenReturn(true);
            when(authorizable.isGroup()).thenReturn(false);
            assertEquals(List.of(CUG_PRINCIPLE_NAME), PermissionUtil.getUserGroupNames(resource, resourceResolver));

            when(group.getDeclaredMembers()).thenReturn(List.of(authorizable).iterator());
            when(authorizable.getPrincipal()).thenReturn(principal2);
            when(principal2.getName()).thenReturn("everyone");
            assertEquals(List.of(CUG_PRINCIPLE_NAME), PermissionUtil.getUserGroupNames(resource, resourceResolver));

            when(authorizable.isGroup()).thenReturn(true);
            when(group.getDeclaredMembers()).thenReturn(List.of(authorizable).iterator());
            when(authorizable.getPrincipal()).thenReturn(principal2);
            when(principal2.getName()).thenReturn("everyone");
            assertEquals(List.of(CUG_PRINCIPLE_NAME), PermissionUtil.getUserGroupNames(resource, resourceResolver));

            when(principal2.getName()).thenReturn("anyone");
            when(group.getDeclaredMembers()).thenReturn(List.of(authorizable).iterator());
            assertEquals(List.of(CUG_PRINCIPLE_NAME, "anyone"), PermissionUtil.getUserGroupNames(resource, resourceResolver));

            when(group.getDeclaredMembers()).thenThrow(RepositoryException.class);
            assertEquals(List.of(CUG_PRINCIPLE_NAME), PermissionUtil.getUserGroupNames(resource, resourceResolver));

            accessControlUtilMockedStatic.when(() -> AccessControlUtil.getAccessControlManager(any())).thenThrow(RepositoryException.class);
            assertEquals(Collections.emptyList(), PermissionUtil.getUserGroupNames(resource, resourceResolver));
        }

    }

    /**
     * Method under test: {@link PermissionUtil#getUserGroupNames(Resource, ResourceResolver)}
     */
    @Test
    void getUserGroupNames_nullPolicy() throws RepositoryException {
        try (MockedStatic<AccessControlUtil> accessControlUtilMockedStatic = mockStatic(AccessControlUtil.class)) {
            accessControlUtilMockedStatic.when(() -> AccessControlUtil.getAccessControlManager(any())).thenReturn(accessControlManager);
            when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
            when(resource.getPath()).thenReturn(RESOURCE_PATH);
            when(accessControlManager.getEffectivePolicies(any())).thenThrow(RepositoryException.class);
            assertEquals(Collections.emptyList(), PermissionUtil.getUserGroupNames(resource, resourceResolver));
        }

    }

    /**
     * Method under test: {@link PermissionUtil#userHasPermissionForActions(SlingHttpServletRequest, String)}
     */
    @Test
    void userHasPermissionForActions_noSuchNode() {
        when(request.getResource()).thenReturn(resource);
        when(resource.adaptTo(Node.class)).thenReturn(null);

        assertFalse(PermissionUtil.userHasPermissionForActions(request, PERMISSION_ADD_NODE));
    }

    /**
     * Method under test: {@link PermissionUtil#userHasPermissionForActions(SlingHttpServletRequest, String)}
     */
    @Test
    void userHasPermissionForActions_noSuchSession() throws RepositoryException {
        when(request.getResource()).thenReturn(resource);
        when(resource.adaptTo(Node.class)).thenReturn(node);
        when(node.getSession()).thenReturn(null);

        assertFalse(PermissionUtil.userHasPermissionForActions(request, PERMISSION_ADD_NODE));
    }

    /**
     * Method under test: {@link PermissionUtil#userHasPermissionForActions(SlingHttpServletRequest, String)}
     */
    @Test
    void userHasPermissionForActions_true() throws RepositoryException {
        when(request.getResource()).thenReturn(resource);
        when(resource.adaptTo(Node.class)).thenReturn(node);
        when(node.getSession()).thenReturn(session);
        when(resource.getPath()).thenReturn(RESOURCE_PATH);

        assertTrue(PermissionUtil.userHasPermissionForActions(request, PERMISSION_ADD_NODE));
    }

    /**
     * Method under test: {@link PermissionUtil#userHasPermissionForActions(SlingHttpServletRequest, String)}
     */
    @Test
    void userHasPermissionForActions_noRepoAccess() throws RepositoryException {
        when(request.getResource()).thenReturn(resource);
        when(resource.adaptTo(Node.class)).thenReturn(node);
        when(node.getSession()).thenReturn(session);
        when(resource.getPath()).thenReturn(RESOURCE_PATH);
        doThrow(RepositoryException.class).when(session).checkPermission(RESOURCE_PATH, PERMISSION_ADD_NODE);

        assertFalse(PermissionUtil.userHasPermissionForActions(request, PERMISSION_ADD_NODE));
    }

}
