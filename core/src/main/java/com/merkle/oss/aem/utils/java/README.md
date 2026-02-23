## Example usage

* [ClassUtil](#classutil)
* [FunctionalUtil](#functionalutil)
    * [asStream()](#asstream)
    * [streamTree()](#streamtree)
    * [streamDescendants()](#streamdescendants)
    * [streamChildren()](#streamchildren)
    * [findClosestAncestorByPredicate()](#findclosestancestorbypredicate)
    * [toDistinctList()](#todistinctlist)

### ClassUtil

```java

import com.merkle.oss.aem.utils.annotations.tooling.Generated;
import com.merkle.oss.aem.utils.java.ClassUtil;

public class ExamplePrivateClass {

    @Generated("Bypass coverage for static utility constructor")
    private ExamplePrivateClass() {
        ClassUtil.assertNoInstance(this.getClass());
    }

}


```

### FunctionalUtil

#### asStream()

```java

import com.day.cq.dam.commons.util.DamUtil;
import com.merkle.oss.aem.utils.java.FunctionalUtil;
import org.apache.sling.api.resource.Resource;

import static com.merkle.oss.aem.utils.sling.SlingUtil.to;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @Self
    private Resource resource;

    @ValueMapValue
    private String rootPagePath;

    @ValueMapValue
    private String damFolderPath;

    final List<Resource> getImagesAsResource() {
        if (StringUtils.isBlank(damFolderPath)) {
            return Collections.emptyList();
        }
        final Resource rootFolder = resource.getResourceResolver().getResource(damFolderPath);
        if (rootFolder == null) {
            return Collections.emptyList();
        }

        /* <--- EXAMPLE ---> */
        return FunctionalUtil.asStream(DamUtil.getAssets(rootFolder))
                .filter(DamUtil::isImage)
                .map(to(Resource.class))
                .filter(Objects::nonNull)
                .toList();
    }

}


```

#### streamTree()

```java

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.merkle.oss.aem.utils.java.FunctionalUtil;
import com.merkle.oss.aem.utils.wcm.PageManagerUtil;
import org.apache.sling.api.resource.Resource;

import static com.merkle.oss.aem.utils.sling.SlingUtil.to;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @Self
    private Resource resource;

    @ValueMapValue
    private int maxNavigationDepth;

    public List<NavItem> getNavigationItems() {
        final Page currentPage = PageManagerUtil.containingPage(resource);
        /* <--- EXAMPLE ---> */
        return FunctionalUtil.streamTree(currentPage, page -> page.listChildren(new PageFilter()), maxNavigationDepth)
                .map(to(NavItem.class))
                .filter(Objects::nonNull)
                .toList();
    }

}


```

#### streamDescendants()

```java

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.merkle.oss.aem.utils.java.FunctionalUtil;
import com.merkle.oss.aem.utils.wcm.PageManagerUtil;
import org.apache.sling.api.resource.Resource;

import static com.merkle.oss.aem.utils.sling.SlingUtil.to;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @Self
    private Resource resource;

    @ValueMapValue
    private int maxNavigationDepth;

    public List<NavItem> getSubNavigationItems() {
        final Page currentPage = PageManagerUtil.containingPage(resource);
        /* <--- EXAMPLE ---> */
        return FunctionalUtil.streamDescendants(currentPage, page -> page.listChildren(new PageFilter()), maxNavigationDepth)
                .map(to(NavItem.class))
                .filter(Objects::nonNull)
                .toList();
    }

}


```

#### streamChildren()

```java

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.merkle.oss.aem.utils.java.FunctionalUtil;
import com.merkle.oss.aem.utils.wcm.PageManagerUtil;
import org.apache.sling.api.resource.Resource;

import static com.merkle.oss.aem.utils.sling.SlingUtil.to;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @Self
    private Resource resource;

    @ValueMapValue
    private int maxNavigationDepth;

    public List<NavItem> getChildNavigationItems() {
        final Page currentPage = PageManagerUtil.containingPage(resource);
        /* <--- EXAMPLE ---> */
        return FunctionalUtil.streamChildren(currentPage, page -> page.listChildren(new PageFilter()))
                .map(to(NavItem.class))
                .filter(Objects::nonNull)
                .toList();
    }

}


```

#### findClosestAncestorByPredicate()

```java

import com.day.cq.wcm.api.Page;
import com.merkle.oss.aem.utils.java.FunctionalUtil;
import com.merkle.oss.aem.utils.wcm.PageManagerUtil;
import org.apache.sling.api.resource.Resource;

import static com.merkle.oss.aem.utils.sling.SlingUtil.to;
//other imports...

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExampleComponent {

    @Self
    private Resource resource;

    public NavItem getParentBreadcrumbItem() {
        final Page currentPage = PageManagerUtil.containingPage(resource);
        /* <--- EXAMPLE ---> */
        return FunctionalUtil.findClosestAncestorByPredicate(currentPage.getParent(), Page::getParent, isBreadCrumbItem())
                .map(to(Object.class))
                .filter(NavItem::nonNull)
                .orElse(null);
    }

    private Predicate<Page> isBreadCrumbItem() {
        return page -> page.getProperties().get("isBreadCrumbItem", Boolean.class);
    }

}


```

#### toDistinctList()

```java

import com.merkle.oss.aem.utils.java.FunctionalUtil;
//other imports...

public List<User> getDistinctUser(final List<User> userList) {
    return userList.stream()
            /* <--- EXAMPLE ---> */
            .toList(FunctionalUtil.toDistinctList(User::getEmail));
}


```
