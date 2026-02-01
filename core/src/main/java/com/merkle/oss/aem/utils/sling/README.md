## Example usage

### ResourceUtil

#### isValid()

```java

import com.merkle.oss.aem.utils.sling.ResourceUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
//other imports...

@Override
protected void doGet(@NonNull final SlingHttpServletRequest request,
                     @NonNull final SlingHttpServletResponse response) throws ServletException {

    final String resourcePath = request.getParameter("resourcePath");
    final Resource resource = request.getResourceResolver().resolve(request, resourcePath);
    /* <--- EXAMPLE ---> */
    if (!ResourceUtil.isValid(resource)) {
        throw new ServletException("Unable to load valid resource for requested resourcePath.");
    }

    //handle servlet logic...
}


```

#### childrenAsStream()

```java

import com.merkle.oss.aem.utils.sling.ResourceUtil;
import org.apache.sling.api.resource.Resource;

import static com.merkle.oss.aem.utils.sling.SlingUtil.to;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @ChildResource
    private Resource containerParsys;

    public List<ModelClass> getItemsAsModel() {
        /* <--- EXAMPLE ---> */
        return ResourceUtil.childrenAsStream(containerParsys)
                .map(to(ModelClass.class))
                .filter(Objects::nonNull)
                .toList();
    }

}


```

#### childrenOfTypes()

```java

import com.merkle.oss.aem.utils.sling.ResourceUtil;
import org.apache.sling.api.resource.Resource;

import static com.merkle.oss.aem.utils.sling.SlingUtil.to;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @ChildResource
    private Resource containerParsys;

    public List<ModelClass> getItemsOfTypeAsModel() {
        /* <--- EXAMPLE ---> */
        return ResourceUtil.childrenOfTypes(containerParsys, "mySite/components/componenA", "mySite/components/componenB")
                .map(to(ModelClass.class))
                .filter(Objects::nonNull)
                .toList();
    }

}


```

#### descendantsOfTypes()

```java

import com.merkle.oss.aem.utils.sling.ResourceUtil;
import org.apache.sling.api.resource.Resource;

import static com.merkle.oss.aem.utils.sling.SlingUtil.to;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @ChildResource
    private Resource containerParsys;

    public List<ModelClass> getItemsOfTypeAsModelForCompleteTree() {
        /* <--- EXAMPLE ---> */
        return ResourceUtil.descendantsOfTypes(containerParsys, "mySite/components/componenA", "mySite/components/componenB")
                .map(to(ModelClass.class))
                .filter(Objects::nonNull)
                .toList();
    }

}


```

#### findClosestAncestorOfResourceTypes()

```java

import com.merkle.oss.aem.utils.sling.ResourceUtil;
import org.apache.sling.api.resource.Resource;

import static com.merkle.oss.aem.utils.sling.SlingUtil.to;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @Self
    private Resource resource;

    public ModelClass getParentContainerOfType() {
        /* <--- EXAMPLE ---> */
        return ResourceUtil.findClosestAncestorOfResourceTypes(resource, "mySite/components/containerA")
                .map(to(ModelClass.class))
                .orElse(null);
    }

}


```

### SlingUtil

#### to()

```java

import com.merkle.oss.aem.utils.sling.ResourceUtil;
import org.apache.sling.api.resource.Resource;

import static com.merkle.oss.aem.utils.sling.SlingUtil.to;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @ChildResource
    private Resource containerParsys;

    public List<ModelClass> getItemsAsModel() {
        return ResourceUtil.childrenAsStream(containerParsys)
                /* <--- EXAMPLE ---> */
                //reads fluent with static import
                .map(to(ModelClass.class))
                .filter(Objects::nonNull)
                .toList();
    }

}


```

#### caConfigOf()

```java

import com.day.cq.wcm.api.Page;
import com.merkle.oss.aem.utils.sling.SlingUtil;
import com.merkle.oss.aem.utils.wcm.PageManagerUtil;
import org.apache.sling.api.resource.Resource;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @Self
    private Resource resource;

    @PostConstruct
    void init() {
        final Page currentPage = PageManagerUtil.containingPage(resource);
        /* <--- EXAMPLE ---> */
        final ContextAwareConfig contextAwareConfig = SlingUtil.caConfigOf(currentPage, ContextAwareConfig.class);
        if (contextAwareConfig == null) {
            return;
        }
        //do init logic...
    }

}


```
