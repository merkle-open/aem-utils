## Example usage

### PermissionUtil

#### getUserId()

```java

import com.merkle.oss.aem.utils.jcr.PermissionUtil;
import com.merkle.oss.aem.utils.sling.SlingUtil;
import org.apache.sling.api.SlingHttpServletRequest;
//other imports...

@Override
protected void doGet(@NonNull final SlingHttpServletRequest request,
                     @NonNull final SlingHttpServletResponse response) throws ServletException {

    /* <--- EXAMPLE ---> */
    final String userId = PermissionUtil.getUserId(SlingUtil.sessionOf(request));
    if (StringUtils.isBlank(userId)) {
        throw new ServletException("Unable to fulfill action because current user id is not available.");
    }

    //handle servlet logic...
}


```

#### getAuthorizable()

```java

import com.merkle.oss.aem.utils.jcr.PermissionUtil;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.SlingHttpServletRequest;
//other imports...

@Override
protected void doGet(@NonNull final SlingHttpServletRequest request,
                     @NonNull final SlingHttpServletResponse response) throws ServletException {

    /* <--- EXAMPLE ---> */
    final Authorizable authorizable = PermissionUtil.getAuthorizable(request);
    if (authorizable == null) {
        throw new ServletException("Unable to load user group information because current user is not available.");
    }

    //fetch users userGroup information via authorizable.declaredMemberOf()...
    //handle servlet logic...
}


```

```java

import com.merkle.oss.aem.utils.jcr.PermissionUtil;
import org.apache.sling.api.resource.Resource;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @Self
    private Resource resource;

    @ValueMapValue
    private List<String> userGroups = Collections.emptyList();
    
    public boolean currentUserIsMemberOfAnyAssignedUserGroup() {
        return userGroups.stream()
                /* <--- EXAMPLE ---> */
                //will only resolve to authorizable if current user has access
                .map(groupId -> PermissionUtil.getAuthorizable(groupId, resource.getResourceResolver()))
                .anyMatch(Objects::nonNull);
    }

}


```

#### getPolicy()

```java

import com.merkle.oss.aem.utils.jcr.PermissionUtil;
import com.merkle.oss.aem.utils.sling.SlingUtil;
import org.apache.jackrabbit.api.security.authorization.PrincipalSetPolicy;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.jcr.base.util.AccessControlUtil;

import javax.jcr.security.AccessControlManager;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @Self
    private Resource resource;

    public List<String> getAccessGroups() throws RepositoryException {
        final AccessControlManager accessControlManager = AccessControlUtil.getAccessControlManager(SlingUtil.sessionOf(resource));
        /* <--- EXAMPLE ---> */
        final PrincipalSetPolicy principalSetPolicy = PermissionUtil.getPolicy(resource.getPath(), accessControlManager);
        if (principalSetPolicy == null) {
            return Collections.emptyList();
        }

        return principalSetPolicy.getPrincipals().stream()
                .map(Principal::getName)
                .toList();
    }

}


```

#### getAuthorizedUserGroups()

```java

import com.merkle.oss.aem.utils.jcr.PermissionUtil;
import org.apache.sling.api.resource.Resource;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @Self
    private Resource resource;

    public List<String> getGroups() {
        /* <--- EXAMPLE ---> */
        //does not only include direct assigned user groups. Will retrieve all authorized user groups.
        return PermissionUtil.getAuthorizedUserGroups(resource, resource.getResourceResolver());
    }

}


```

#### userHasPermissionForActions()

```java

import com.merkle.oss.aem.utils.jcr.PermissionUtil;
import org.apache.sling.api.SlingHttpServletRequest;
//other imports...

@Override
protected void doGet(@NonNull final SlingHttpServletRequest request,
                     @NonNull final SlingHttpServletResponse response) throws ServletException {

    /* <--- EXAMPLE ---> */
    if (!PermissionUtil.userHasPermissionForActions(request, "add_node,set_property")) {
        response.setStatus(HttpStatus.SC_UNAUTHORIZED);
        return;
    }
    
    //handle servlet logic where user may commence content manipulation not caught by jcr
}


```
