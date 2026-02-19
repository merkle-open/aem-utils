## Example usage

* [RequestUtil](#requestutil)
    * [Selector handling](#selector-handling)
    * [Suffix handling](#suffix-handling)
    * [Parameter handling](#parameter-handling)
* [ResourceUtil](#resourceutil)
    * [isValid()](#isvalid)
    * [childrenAsStream()](#childrenasstream)
    * [childrenOfTypes()](#childrenoftypes)
    * [descendantsOfTypes()](#descendantsoftypes)
    * [findClosestAncestorOfResourceTypes()](#findclosestancestorofresourcetypes)
* [SlingUtil](#slingutil)
    * [to()](#to)
    * [caConfigOf()](#caconfigof)

### RequestUtil

#### Selector handling

```java

import com.merkle.oss.aem.utils.sling.RequestUtil;
import org.apache.sling.api.SlingHttpServletRequest;
//other imports...

@Override
protected void doGet(@NonNull final SlingHttpServletRequest request,
                     @NonNull final SlingHttpServletResponse response) {

    /* <--- EXAMPLE ---> */
    final boolean hasSelector = RequestUtil.hasSelector(request, "selectorValue");
    /* <--- EXAMPLE ---> */
    final List<String> selectors = RequestUtil.getSelectors(request);
    /* <--- EXAMPLE ---> */
    final String selector = RequestUtil.getSelector(request, 1, "defaultValue");
    /* <--- EXAMPLE ---> */
    final String firstSelector = RequestUtil.getFirstSelector(request, "defaultValue");

    //handle servlet logic...
}


```

#### Suffix handling

```java

import com.merkle.oss.aem.utils.sling.RequestUtil;
import org.apache.sling.api.SlingHttpServletRequest;
//other imports...

@Override
protected void doGet(@NonNull final SlingHttpServletRequest request,
                     @NonNull final SlingHttpServletResponse response) {

    /* <--- EXAMPLE ---> */
    final boolean hasSuffix = RequestUtil.hasSuffix(request);
    /* <--- EXAMPLE ---> */
    final String suffix = RequestUtil.getSuffix(request, "defaultValue");
    /* <--- EXAMPLE ---> */
    final List<String> suffixSegments = RequestUtil.getSuffixSegments(request);
    /* <--- EXAMPLE ---> */
    final String suffixSegment = RequestUtil.getSuffixSegment(request, 1, "defaultValue");
    /* <--- EXAMPLE ---> */
    final String firstSuffixSegment = RequestUtil.getFirstSuffixSegment(request, "defaultValue");

    //handle servlet logic...
}


```

#### Parameter handling

```java

import com.merkle.oss.aem.utils.sling.RequestUtil;
import org.apache.sling.api.SlingHttpServletRequest;
//other imports...

@Override
protected void doGet(@NonNull final SlingHttpServletRequest request,
                     @NonNull final SlingHttpServletResponse response) {
    
    /* <--- EXAMPLE ---> */
    final boolean hasParameter = RequestUtil.hasParameter(request, "parameterName");
    /* <--- EXAMPLE ---> */
    final List<String> parameterList = RequestUtil.getParameterList(request, "arrayParameterName");
    /* <--- EXAMPLE ---> */
    final String parameterValue = RequestUtil.getParameter(request, "parameterName", "defaultValue");
    /* <--- EXAMPLE ---> */
    final int parameterIntValue = RequestUtil.getParameterAsInt(request, "parameterName", 0);
    /* <--- EXAMPLE ---> */
    final long parameterLongValue = RequestUtil.getParameterAsLong(request, "parameterName", 0L);
    /* <--- EXAMPLE ---> */
    final float parameterFloatValue = RequestUtil.getParameterAsFloat(request, "parameterName", 0.0f);
    /* <--- EXAMPLE ---> */
    final boolean parameterBooleanValue = RequestUtil.getParameterAsBoolean(request, "parameterName", false);

    //handle servlet logic...
}


```

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
