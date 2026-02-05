## Example usage

* [ClassUtil](#classutil)
* [FunctionalUtil](#functionalutil)
    * [asStream()](#asstream)
    * [toDistinctList()](#todistinctlist)

### ClassUtil

```java

import com.merkle.oss.aem.utils.annotations.Generated;
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

    final List<ChildPageTeaserModel> childPageTeaserModelList() {
        if (StringUtils.isBlank(rootPagePath)) {
            return Collections.emptyList();
        }
        final Page rootPage = PageManagerUtil.containingPage(rootPagePath, resource.getResourceResolver());
        if (rootPage == null) {
            return Collections.emptyList();
        }

        /* <--- EXAMPLE ---> */
        return FunctionalUtil.asStream(rootPage.listChildren(new PageFilter()))
                .map(to(ChildPageTeaserModel.class))
                .filter(Objects::nonNull)
                .toList();
    }

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
