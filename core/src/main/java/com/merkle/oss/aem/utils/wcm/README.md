## Example usage

* [PageManagerUtil](#pagemanagerutil)
    * [containingPage()](#containingpage)
* [PageUtil](#pageutil)
    * [Fetch properties](#fetch-properties)
    * [equals() & isValid()](#equals--isvalid)
    * [findClosestAncestorByTemplates()](#findclosestancestorbytemplates)
    * [childrenByTemplates()](#childrenbytemplates)
    * [streamDescendants()](#streamdescendants)
    * [streamTree()](#streamtree)

### PageManagerUtil

#### containingPage()

```java

import com.day.cq.wcm.api.Page;
import com.merkle.oss.aem.utils.wcm.PageManagerUtil;
import org.apache.sling.api.SlingHttpServletRequest;
//other imports...

@Override
protected void doGet(@NonNull final SlingHttpServletRequest request,
                     @NonNull final SlingHttpServletResponse response) throws ServletException {

    /* <--- EXAMPLE ---> */
    final Page currentPage = PageManagerUtil.containingPage(request);
    if (currentPage == null) {
        throw new ServletException("Unable to retrieve current page from request.");
    }

    //handle servlet logic...
}


```

```java

import com.day.cq.wcm.api.Page;
import com.merkle.oss.aem.utils.wcm.PageManagerUtil;
import org.apache.sling.api.resource.Resource;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @Self
    private Resource resource;

    @PostConstruct
    void init() {
        /* <--- EXAMPLE ---> */
        final Page currentPage = PageManagerUtil.containingPage(resource);
        //do init logic...
    }

}


```

```java

import com.day.cq.wcm.api.Page;
import com.merkle.oss.aem.utils.wcm.PageManagerUtil;
import org.apache.sling.api.SlingHttpServletRequest;
//other imports...

@Override
protected void doGet(@NonNull final SlingHttpServletRequest request,
                     @NonNull final SlingHttpServletResponse response) throws ServletException {

    final String pagePath = request.getParameter("pagePath");
    /* <--- EXAMPLE ---> */
    final Page targetPage = PageManagerUtil.containingPage(pagePath, request.getResourceResolver());
    if (targetPage == null) {
        throw new ServletException("Unable to retrieve target page.");
    }

    //handle servlet logic...
}


```

### PageUtil

#### Fetch properties

```java

import com.day.cq.wcm.api.Page;
import com.merkle.oss.aem.utils.wcm.PageUtil;
//other imports...

public void propertiesForPage(final Page page) {
    //title retrieval fallback logic
    //-> jcr:title
    final String title = PageUtil.getTitle(page);
    //-> pageTitle -> jcr:title
    final String pageTitle = PageUtil.getPageTitle(page);
    //-> navTitle -> pageTitle -> jcr:title
    final String navigationTitle = PageUtil.getNavigationTitle(page);

    //-> jcr:description
    final String description = PageUtil.getDescription(page);
    //-> cq:template ("/apps/mySite/templates/campaign")
    final String templatePath = PageUtil.getTemplatePath(page);
    //-> "campaign"
    final String templateName = PageUtil.getTemplateName(page);
    //-> safe retrieval of any custom string property
    final String propertyValue = PageUtil.getProperty(page, "propertyName");
}


```

#### equals() & isValid()

```java

import com.day.cq.wcm.api.Page;
import com.merkle.oss.aem.utils.wcm.PageManagerUtil;
import com.merkle.oss.aem.utils.wcm.PageUtil;
import org.apache.sling.api.SlingHttpServletRequest;
//other imports...

@Override
protected void doGet(@NonNull final SlingHttpServletRequest request,
                     @NonNull final SlingHttpServletResponse response) throws ServletException {

    final String pagePathA = request.getParameter("pagePathA");
    final String pagePathB = request.getParameter("pagePathB");

    final Page targetPageA = PageManagerUtil.containingPage(pagePathA, request.getResourceResolver());
    final Page targetPageB = PageManagerUtil.containingPage(pagePathB, request.getResourceResolver());
    /* <--- EXAMPLE ---> */
    if (!PageUtil.isValid(targetPageA) || !PageUtil.isValid(targetPageB)) {
        throw new ServletException("Unable to retrieve valid target pages.");
    }
    /* <--- EXAMPLE ---> */
    if (PageUtil.equals(targetPageA, targetPageB)) {
        //handle case
    }

    //handle servlet logic...
}


```

#### findClosestAncestorByTemplates()

```java

import com.day.cq.wcm.api.Page;
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
        final Page targetParentPage = PageUtil.findClosestAncestorByTemplates(currentPage, "/apps/mySite/templates/home")
                .orElse(null);
        //do init logic...
    }

}


```

#### childrenByTemplates()

```java

import com.day.cq.wcm.api.Page;
import com.merkle.oss.aem.utils.wcm.PageManagerUtil;
import com.merkle.oss.aem.utils.wcm.PageUtil;
import org.apache.sling.api.resource.Resource;

import static com.merkle.oss.aem.utils.sling.SlingUtil.to;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @Self
    private Resource resource;

    public List<TeaserItem> getTeasers() {
        final Page currentPage = PageManagerUtil.containingPage(resource);
        /* <--- EXAMPLE ---> */
        return PageUtil.childrenByTemplates(currentPage, "/apps/mySite/templates/article").stream()
                .map(to(TeaserItem.class))
                .filter(Objects::nonNull)
                .toList();
    }

}


```

#### streamDescendants()

```java

import com.day.cq.wcm.api.Page;
import com.merkle.oss.aem.utils.wcm.PageManagerUtil;
import com.merkle.oss.aem.utils.wcm.PageUtil;
import org.apache.sling.api.resource.Resource;

import static com.merkle.oss.aem.utils.sling.SlingUtil.to;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @Self
    private Resource resource;

    @ValueMapValue
    private int maxTeaserDepth;

    public List<TeaserItem> getTeasers() {
        final Page currentPage = PageManagerUtil.containingPage(resource);
        /* <--- EXAMPLE ---> */
        return PageUtil.streamDescendants(currentPage, maxTeaserDepth)
                .map(to(TeaserItem.class))
                .filter(Objects::nonNull)
                .toList();
    }

}


```

#### streamTree()

```java

import com.day.cq.wcm.api.Page;
import com.merkle.oss.aem.utils.wcm.PageManagerUtil;
import com.merkle.oss.aem.utils.wcm.PageUtil;
import org.apache.sling.api.resource.Resource;

import static com.merkle.oss.aem.utils.sling.SlingUtil.to;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @Self
    private Resource resource;

    @ValueMapValue
    private int maxNavigationLevel;

    public List<NavigationItem> getNavigationItems() {
        final Page currentPage = PageManagerUtil.containingPage(resource);
        /* <--- EXAMPLE ---> */
        return PageUtil.streamTree(currentPage, maxNavigationLevel)
                .map(to(NavigationItem.class))
                .filter(Objects::nonNull)
                .toList();
    }

}


```
